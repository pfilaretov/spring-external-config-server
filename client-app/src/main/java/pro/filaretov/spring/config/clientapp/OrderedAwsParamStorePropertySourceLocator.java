package pro.filaretov.spring.config.clientapp;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySourceLocator;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * A dirty way to make AWS Parameter Store PropertySource has higher precedence than Config Server.
 * <p/>
 * <p><b>Additional context</b></p>
 * Config Server defines {@link ConfigServicePropertySourceLocator} class with {@code @Order(0)}. On the other hand, AWS
 * Parameter Store config defines {@link AwsParamStorePropertySourceLocator} which does not specify any {@code @Order}.
 * This means it will have {@code Ordered.LOWEST_PRECEDENCE} by default.
 * <p/>
 * This class extends {@link AwsParamStorePropertySourceLocator} and does nothing but specifies {@code @Order} higher
 * than Config Server does.
 * <p/>
 * NOTE: {@link AwsParamStorePropertySourceLocator} will still be loaded and uselessly retrieve properties from AWS
 * Parameter Store.
 */
@Order(-100)
public class OrderedAwsParamStorePropertySourceLocator extends AwsParamStorePropertySourceLocator {

    public OrderedAwsParamStorePropertySourceLocator(AWSSimpleSystemsManagement ssmClient,
        AwsParamStoreProperties properties) {
        super(ssmClient, properties);
    }
}
