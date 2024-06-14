package ai.yda.application.channel;

import org.springframework.web.bind.annotation.*;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.AbstractChannel;

// @RestController
// @RequestMapping("/channels")
public class RestChannel extends AbstractChannel {

    //    @PostMapping
    public BaseAssistantResponse handleRequest(@RequestBody BaseAssistantRequest request) {
        return processRequest(request);
    }
}
