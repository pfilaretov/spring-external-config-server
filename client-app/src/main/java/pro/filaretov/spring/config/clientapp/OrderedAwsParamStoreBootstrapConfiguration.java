package pro.filaretov.spring.config.clientapp;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.autoconfigure.paramstore.AwsParamStoreBootstrapConfiguration;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AwsParamStoreProperties.class)
@ConditionalOnClass({AWSSimpleSystemsManagement.class, OrderedAwsParamStorePropertySourceLocator.class})
// add @ConditionalOnProperty, maybe?
public class OrderedAwsParamStoreBootstrapConfiguration extends AwsParamStoreBootstrapConfiguration {

    /**
     * This method's name should match the one in {@link AwsParamStoreBootstrapConfiguration} so that
     * the former shadows the latter, and we do not have a bean of type {@code AwsParamStorePropertySourceLocator}.
     */
    @Bean
    OrderedAwsParamStorePropertySourceLocator awsParamStorePropertySourceLocator(
        AWSSimpleSystemsManagement ssmClient, AwsParamStoreProperties properties) {
        return new OrderedAwsParamStorePropertySourceLocator(ssmClient, properties);
    }
}
