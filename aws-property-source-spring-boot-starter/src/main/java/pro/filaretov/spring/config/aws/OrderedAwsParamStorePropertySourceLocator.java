package pro.filaretov.spring.config.aws;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import org.springframework.cloud.aws.paramstore.AwsParamStoreProperties;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySourceLocator;
import org.springframework.core.annotation.Order;

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
public class OrderedAwsParamStorePropertySourceLocator extends AwsParamStorePropertySourceLocator {

    public OrderedAwsParamStorePropertySourceLocator(AWSSimpleSystemsManagement ssmClient,
        AwsParamStoreProperties properties) {
        super(ssmClient, properties);
    }
}