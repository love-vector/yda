package ai.yda.framework.core.assistant;

import java.util.List;

import reactor.core.publisher.Flux;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.rag.core.application.RagApplication;

public class RagAssistantReactive extends RagAssistant {

    public RagAssistantReactive(
            RagApplication<BaseAssistantRequest, ?, BaseAssistantResponse> ragApplication,
            List<Channel<BaseAssistantRequest, BaseAssistantResponse>> channels) {
        super(ragApplication, channels);
    }

    @Override
    public Flux<BaseAssistantResponse> processRequestReactive(BaseAssistantRequest request) {
        return Flux.fromIterable(List.of(processRequest(request)));
    }
}
