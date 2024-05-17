package ai.yda.llm.thread;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ai.yda.llm.assistant.AssistantEntity;
import ai.yda.llm.openai.OpenAiService;

@Component
@Transactional
@RequiredArgsConstructor
public class ThreadService {

    private final OpenAiService openAiService;

    private final ThreadDao threadDao;

    private final ThreadMapper threadMapper;

    /**
     * Retrieves the first available thread associated with the specified assistant or creates a new thread if none exist.
     * <p>
     * This method first attempts to fetch any existing threads associated with the given {@link AssistantEntity}.
     * If no threads are found, it interacts with the OpenAI service to create a new thread, then persists this new thread
     * into the database using the provided assistant's ID. This method ensures that every assistant has at least one thread
     * associated with them, either by reusing an existing thread or by initiating a new one.
     *
     * @param assistant the {@link AssistantEntity} for which the thread is to be retrieved or created.
     * @return the {@link ThreadEntity} that is either retrieved from the existing entries or created new.
     */
    public ThreadEntity createOrGetAnyThreadByAssistant(final AssistantEntity assistant) {
        var threads = threadDao.getThreadsByAssistant(assistant);
        if (threads.isEmpty()) {
            var openAiThread = openAiService.createThread();
            return threadDao.createThread(threadMapper.createEntity(openAiThread.getId(), assistant.getId()));
        }
        return threads.stream().findAny().get();
    }
}
