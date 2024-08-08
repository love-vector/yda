package ai.yda.framework.channel.rest.spring.sync;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import ai.yda.framework.channel.rest.spring.sync.security.SecurityConfiguration;
import ai.yda.framework.channel.rest.spring.sync.session.RestSyncSessionProvider;
import ai.yda.framework.channel.rest.spring.sync.web.RestSyncChannel;

@AutoConfiguration
@EnableConfigurationProperties({RestSpringSyncProperties.class})
@Import({RestSyncChannel.class, SecurityConfiguration.class, RestSyncSessionProvider.class})
public class RestSpringSyncAutoConfiguration {}
