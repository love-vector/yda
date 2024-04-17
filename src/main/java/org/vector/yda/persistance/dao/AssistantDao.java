package org.vector.yda.persistance.dao;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.exception.not.found.AssistantNotFoundException;
import org.vector.yda.persistance.entity.AssistantEntity;
import org.vector.yda.persistance.repository.AssistantRepository;

@Component
@Transactional
@RequiredArgsConstructor
public class AssistantDao {

    private final AssistantRepository assistantRepository;

    public AssistantEntity getAssistantById(final Long assistantId) {
        return assistantRepository.findById(assistantId).orElseThrow(AssistantNotFoundException::new);
    }

    public List<AssistantEntity> getAssistantsByUserId(final UUID userId) {
        return assistantRepository.findAllByUserId(userId);
    }

    public AssistantEntity createAssistant(final AssistantEntity assistant) {
        return assistantRepository.save(assistant);
    }

    public AssistantEntity updateAssistant(final AssistantEntity assistant) {
        return assistantRepository.save(assistant);
    }

    public void deleteAssistant(final AssistantEntity assistant) {
        assistantRepository.delete(assistant);
    }
}
