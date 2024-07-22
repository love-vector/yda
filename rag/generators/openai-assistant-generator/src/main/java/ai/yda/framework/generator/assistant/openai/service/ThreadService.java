package ai.yda.framework.generator.assistant.openai.service;

import java.io.IOException;
import java.util.List;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.AssistantsClientBuilder;
import com.azure.ai.openai.assistants.models.*;
import com.azure.core.credential.KeyCredential;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public class ThreadService {
    private final AssistantsClient assistantsClient;

    public ThreadService(String apiKey) {
        this.assistantsClient = new AssistantsClientBuilder()
                .credential(new KeyCredential(apiKey))
                .buildClient();
    }

    public AssistantThread createThread(final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        var creationOptions = new AssistantThreadCreationOptions().setMessages(List.of(threadMessageOptions));
        return assistantsClient.createThread(creationOptions);
    }

    public void addMessageToThread(final String threadId, final String content) {
        var threadMessageOptions = new ThreadMessageOptions(MessageRole.USER, content);
        assistantsClient.createMessage(threadId, threadMessageOptions);
    }

    public SseEmitter createRunStream(final String threadId, final String assistantId, final String context) {
        var emitter = new SseEmitter();
        var createRunOptions = new CreateRunOptions(assistantId).setAdditionalInstructions(context);
        assistantsClient.createRunStream(threadId, createRunOptions).stream().forEach(streamUpdate -> {
            try {
                if (streamUpdate instanceof StreamMessageUpdate deltaMessage) {
                    emitter.send(deltaMessage.getMessage());
                }
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        });
        emitter.complete();
        return emitter;
    }
}
