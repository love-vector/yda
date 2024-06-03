package ai.yda.framework.rag.channels;

import ai.yda.framework.rag.shared.model.Request;
import ai.yda.framework.rag.shared.model.Response;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.RagApplication;

@RequiredArgsConstructor
public class DefaultCommunicationChannel implements CommunicationChannel {

    private final RagApplication coreService;

    @Override
    public Response sendRequest(final Request request) {
        return new Response(request.message());
    }
}
