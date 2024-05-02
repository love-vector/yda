package ai.yda.llm.thread;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ai.yda.llm.assistant.AssistantEntity;

@Component
@Transactional
@RequiredArgsConstructor
public class ThreadDao {

    private final ThreadRepository threadRepository;

    public Set<ThreadEntity> getThreadsByAssistant(final AssistantEntity assistant) {
        return new HashSet<>(threadRepository.findAllByAssistantId(assistant.getId()));
    }

    public ThreadEntity createThread(final ThreadEntity thread) {
        return threadRepository.save(thread);
    }
}
