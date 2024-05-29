package ai.yda.llm.thread;

import com.theokanning.openai.messages.Message;
import com.theokanning.openai.threads.Thread;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import ai.yda.llm.openai.OpenAiService;

@Component
@RequiredArgsConstructor
public class ThreadService {

    private final OpenAiService openAiService;

    private final ThreadMapper threadMapper;

    /**
     * Creates a new thread in the OpenAI service with the given initial message.
     *
     * @param message the initial message to start the thread.
     * @return the newly created {@link Thread} object that represents the thread in the OpenAI service.
     */
    public Thread createThread(final String message) {
        return openAiService.createThread(threadMapper.toThreadRequest(message));
    }

    /**
     * Adds a message to an existing thread in the OpenAI service.
     *
     * @param threadId the ID of the thread to which the message is to be added.
     * @param message  the content of the message to be added to the thread.
     * @return the newly created {@link Message} object that represents the added message in the thread.
     */
    public Message addMessage(final String threadId, final String message) {
        return openAiService.createMessage(threadId, threadMapper.toMessageRequest(message));
    }
}
