package pro.filaretov.spring.config.aws;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySource;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySourceLocator;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.ReflectionUtils;

/**
 * This class is a way to make AWS Parameter Store PropertySource have higher precedence than Config Server.
 * <p/>
 * <p><b>Additional context</b></p>
 * Config Server defines {@code ConfigServicePropertySourceLocator} class with {@code @Order(0)}. On the other hand, AWS
 * Parameter Store config defines {@link AwsParamStorePropertySourceLocator} which does not specify any {@code @Order}.
 * This means it will have {@code Ordered.LOWEST_PRECEDENCE} by default.
 * <p/>
 * This class extends {@link AwsParamStorePropertySourceLocator} and does nothing but specifies {@code @Order} higher
 * than Config Server does.
 * <p/>
 */
@Order(-100)
@Slf4j
public class OrderedAwsParamStorePropertySourceLocator extends AwsParamStorePropertySourceLocator {

    private final Set<String> contexts = new LinkedHashSet<>();
    private AWSSimpleSystemsManagement ssmClient;
    private AwsParamStoreProperties properties;

    @Value("${my.aws.paramstore.label}")
    private String paramStoreLabel;

    public OrderedAwsParamStorePropertySourceLocator(AWSSimpleSystemsManagement ssmClient,
        AwsParamStoreProperties properties) {
        super(ssmClient, properties);

        this.ssmClient = ssmClient;
        this.properties = properties;
    }

    @Override
    public List<String> getContexts() {
        return new ArrayList<>(contexts);
    }

    @Override
    public PropertySource<?> locate(Environment environment) {
        if (!(environment instanceof ConfigurableEnvironment)) {
            return null;
        }

        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

        String appName = properties.getName();

        if (appName == null) {
            appName = env.getProperty("spring.application.name");
        }

        List<String> profiles = Arrays.asList(env.getActiveProfiles());

        String prefix = this.properties.getPrefix();

        String appContext = prefix + "/" + appName;
        addProfiles(this.contexts, appContext, profiles);
        this.contexts.add(appContext + "/");

        String defaultContext = prefix + "/" + this.properties.getDefaultContext();
        addProfiles(this.contexts, defaultContext, profiles);
        this.contexts.add(defaultContext + "/");

        CompositePropertySource composite = new CompositePropertySource("aws-param-store");

        for (String propertySourceContext : this.contexts) {
            try {
                composite.addPropertySource(create(propertySourceContext));
            } catch (Exception e) {
                if (this.properties.isFailFast()) {
                    log.error(
                        "Fail fast is set and there was an error reading configuration from AWS Parameter Store:\n"
                            + e.getMessage());
                    ReflectionUtils.rethrowRuntimeException(e);
                } else {
                    log.warn("Unable to load AWS config from " + propertySourceContext, e);
                }
            }
        }

        return composite;
    }

    private AwsParamStorePropertySource create(String context) {
        LabelAwareAwsParamStorePropertySource propertySource =
            new LabelAwareAwsParamStorePropertySource(context, paramStoreLabel, this.ssmClient);
        propertySource.init();
        return propertySource;
    }

    private void addProfiles(Set<String> contexts, String baseContext,
        List<String> profiles) {
        for (String profile : profiles) {
            contexts.add(baseContext + this.properties.getProfileSeparator() + profile + "/");
        }
    }
}
