package ai.yda.framework.autoconfigure.base.spring;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ConfigurationProperties(BaseSpringProviderProperties.CONFIG_PREFIX)
public class BaseSpringProviderProperties {

    public static final String CONFIG_PREFIX = "ai.yda.framework.base.spring.provider";
}
