package org.vector.yda.persistance.dao;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.persistance.entity.AssistantEntity;
import org.vector.yda.persistance.entity.ThreadEntity;
import org.vector.yda.persistance.repository.ThreadRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class ThreadDao {

    private final ThreadRepository threadRepository;

    public List<ThreadEntity> getThreadsByAssistant(final AssistantEntity assistant) {
        return threadRepository.findAllByAssistantId(assistant.getId());
    }

    public ThreadEntity createThread(final ThreadEntity thread) {
        return threadRepository.save(thread);
    }
}
