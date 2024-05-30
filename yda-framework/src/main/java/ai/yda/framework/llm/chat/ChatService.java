package ai.yda.framework.llm.chat;

import ai.yda.framework.llm.thread.ThreadService;
import com.theokanning.openai.OpenAiHttpException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import retrofit2.HttpException;

import org.springframework.stereotype.Component;

import ai.yda.framework.llm.openai.OpenAiService;

@Component
@RequiredArgsConstructor
public class ChatService {

    private final ThreadService threadService;
    private final OpenAiService openAiService;

    /**
     * Sends a message and streams the responses in a chat interaction.
     * If the request does not contain a thread ID, a new thread will be created.
     *
     * @param request the {@link ChatRequest} containing the assistant ID and the message to be sent.
     * @return a {@link Flux<String>} that streams the chat responses as they become available.
     * @throws OpenAiHttpException if there is an issue with the OpenAI API call, including network errors,
     *                             or if the OpenAI service returns an error response that can be parsed
     *                             into a known error structure
     * @throws HttpException       if a lower-level HTTP error occurs that does not involve a parseable
     *                             OpenAI service error response
     */
    public Flux<String> chat(final ChatRequest request) {
        var threadMono = request.threadId() == null || request.threadId().isBlank()
                ? Mono.fromCallable(
                        () -> threadService.createThread(request.message()).getId())
                : Mono.fromCallable(() -> threadService
                        .addMessage(request.threadId(), request.message())
                        .getThreadId());
        return threadMono.flatMapMany(threadId -> openAiService.streamRun(request.assistantId(), threadId));
    }
}
