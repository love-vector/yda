package ai.yda.application.channels;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.AbstractChannel;

@RestController
@RequestMapping("/channels")
public class AsyncChannel extends AbstractChannel<SseEmitter> {

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getNumbers(@RequestBody BaseAssistantRequest request) {

        return processRequest(request);
    }
}
