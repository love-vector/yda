package ai.yda.framework.channel.rest.spring.sync.web;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.channel.rest.spring.sync.RestSpringSyncProperties;
import ai.yda.framework.core.channel.AbstractChannel;

@RestController
@RequestMapping(path = "${" + RestSpringSyncProperties.CONFIG_PREFIX + ".endpoint-relative-path}")
@RequiredArgsConstructor
public class RestChannel extends AbstractChannel<String> {

    @PostMapping
    public String call(@RequestBody @Validated final BaseAssistantRequest request) {
        return processRequest(request);
    }
}
