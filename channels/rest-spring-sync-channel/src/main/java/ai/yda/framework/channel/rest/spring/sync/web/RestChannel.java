package ai.yda.framework.channel.rest.spring.sync.web;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.yda.framework.channel.core.Channel;
import ai.yda.framework.channel.rest.spring.sync.RestSpringSyncProperties;
import ai.yda.framework.core.assistant.Assistant;
import ai.yda.framework.rag.core.model.RagRequest;
import ai.yda.framework.rag.core.model.RagResponse;

@RestController
@RequestMapping(
        path = "${" + RestSpringSyncProperties.CONFIG_PREFIX + ".endpoint-relative-path:"
                + RestSpringSyncProperties.DEFAULT_ENDPOINT_RELATIVE_PATH + "}")
@RequiredArgsConstructor
public class RestChannel implements Channel<RagRequest, RagResponse> {

    private final Assistant<RagRequest, RagResponse> assistant;

    @Override
    @PostMapping
    public RagResponse processRequest(@RequestBody @Validated final RagRequest ragRequest) {
        return assistant.assist(ragRequest);
    }
}
