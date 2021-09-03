package pro.filaretov.spring.config.clientapp;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import com.amazonaws.util.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.core.SpringCloudClientConfiguration;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
