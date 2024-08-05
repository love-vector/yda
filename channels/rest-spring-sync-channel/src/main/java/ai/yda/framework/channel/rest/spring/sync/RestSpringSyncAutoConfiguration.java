package ai.yda.framework.channel.rest.spring.sync;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import ai.yda.framework.channel.rest.spring.sync.security.SecurityConfiguration;
import ai.yda.framework.channel.rest.spring.sync.web.RestChannel;

@AutoConfiguration
@EnableConfigurationProperties({RestSpringSyncProperties.class})
@Import({RestChannel.class, SecurityConfiguration.class})
public class RestSpringSyncAutoConfiguration {}
