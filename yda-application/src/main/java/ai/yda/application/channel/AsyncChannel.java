package ai.yda.application.channel;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.core.channel.AbstractChannel;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class AsyncChannel extends AbstractChannel<SseEmitter> {

    @PostMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getNumbers(@RequestBody final BaseAssistantRequest request) {
        return processRequest(request);
    }

    @GetMapping
    public SseEmitter test() {
        return processRequest(new BaseAssistantRequest() {
            {
                setQuery("hello");
            }
        });
    }
}
