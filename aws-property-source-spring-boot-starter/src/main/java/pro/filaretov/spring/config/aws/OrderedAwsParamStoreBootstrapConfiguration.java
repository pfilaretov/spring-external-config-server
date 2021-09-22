package pro.filaretov.spring.config.aws;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.util.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.autoconfigure.paramstore.AwsParamStoreBootstrapConfiguration;
import org.springframework.cloud.aws.core.SpringCloudClientConfiguration;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySourceLocator;
import org.springframework.cloud.bootstrap.BootstrapConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This is the {@link BootstrapConfiguration} that replaces {@link AwsParamStoreBootstrapConfiguration} to load
 * {@link OrderedAwsParamStorePropertySourceLocator} instead of {@link AwsParamStorePropertySourceLocator}.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AwsParamStoreProperties.class)
@ConditionalOnClass({AWSSimpleSystemsManagement.class, OrderedAwsParamStorePropertySourceLocator.class})
// add @ConditionalOnProperty, maybe?
public class OrderedAwsParamStoreBootstrapConfiguration {

    @Bean
    OrderedAwsParamStorePropertySourceLocator orderedAwsParamStorePropertySourceLocator(
        AWSSimpleSystemsManagement ssmClient, AwsParamStoreProperties properties) {
        return new OrderedAwsParamStorePropertySourceLocator(ssmClient, properties);
    }

    @Bean
    @ConditionalOnMissingBean
    AWSSimpleSystemsManagement ssmClient(AwsParamStoreProperties awsParamStoreProperties) {
        AWSSimpleSystemsManagementClientBuilder builder = AWSSimpleSystemsManagementClientBuilder
            .standard().withClientConfiguration(SpringCloudClientConfiguration.getClientConfiguration());
        return StringUtils.isNullOrEmpty(awsParamStoreProperties.getRegion())
            ? builder.build()
            : builder.withRegion(awsParamStoreProperties.getRegion()).build();
    }
}
