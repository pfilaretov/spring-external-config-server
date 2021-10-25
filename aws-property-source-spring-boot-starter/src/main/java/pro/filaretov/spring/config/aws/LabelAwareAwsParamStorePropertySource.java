package pro.filaretov.spring.config.aws;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathRequest;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;
import com.amazonaws.services.simplesystemsmanagement.model.ParameterStringFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.aws.paramstore.AwsParamStorePropertySource;
import org.springframework.util.StringUtils;

/**
 * {@link AwsParamStorePropertySource} that can retrieve parameters filtered by labels.
 */
@Slf4j
public class LabelAwareAwsParamStorePropertySource extends AwsParamStorePropertySource {

    private final String context;
    private final String paramStoreLabel;
    private final Map<String, Object> properties = new LinkedHashMap<>();

    public LabelAwareAwsParamStorePropertySource(String context, String paramStoreLabel, AWSSimpleSystemsManagement ssmClient) {
        super(context, ssmClient);
        this.context = context;
        this.paramStoreLabel = paramStoreLabel;
    }

    @Override
    public void init() {
        GetParametersByPathRequest paramsRequest = new GetParametersByPathRequest()
            .withPath(context)
            .withRecursive(true)
            .withWithDecryption(true);

        if (!StringUtils.isEmpty(paramStoreLabel)) {
            paramsRequest.withParameterFilters(new ParameterStringFilter()
                .withKey("Label")
                .withOption("Equals")
                .withValues(paramStoreLabel));
        }

        getParameters(paramsRequest);
    }

    @Override
    public String[] getPropertyNames() {
        Set<String> strings = properties.keySet();
        return strings.toArray(new String[0]);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    private void getParameters(GetParametersByPathRequest paramsRequest) {
        GetParametersByPathResult paramsResult = source.getParametersByPath(paramsRequest);
        for (Parameter parameter : paramsResult.getParameters()) {
            String key = parameter.getName().replace(context, "").replace('/', '.');
            log.info("Populating property {} retrieved from AWS Parameter Store: {}", key, parameter.getName());
            properties.put(key, parameter.getValue());
        }

        if (paramsResult.getNextToken() != null) {
            getParameters(paramsRequest.withNextToken(paramsResult.getNextToken()));
        }
    }
}
