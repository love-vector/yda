package org.vector.yda.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import org.vector.yda.persistance.dao.ThreadDao;
import org.vector.yda.persistance.entity.AssistantEntity;
import org.vector.yda.persistance.entity.ThreadEntity;
import org.vector.yda.util.mapper.ThreadMapper;

@Component
@RequiredArgsConstructor
public class ThreadService {

    private final OpenAiService openAiService;

    private final ThreadDao threadDao;

    private final ThreadMapper threadMapper;

    public ThreadEntity createOrGetAnyThreadByAssistant(final AssistantEntity assistant) {
        var threads = threadDao.getThreadsByAssistant(assistant);
        if (threads.isEmpty()) {
            var openAiThread = openAiService.createThread();
            return threadDao.createThread(threadMapper.createEntity(openAiThread.getId(), assistant.getId()));
        }
        return threads.getFirst();
    }
}
