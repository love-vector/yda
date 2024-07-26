package ai.yda.framework.channel.rest.spring.sync;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@AutoConfiguration
@EnableConfigurationProperties({RestSpringSyncProperties.class})
public class RestSpringSyncAutoConfig {}
