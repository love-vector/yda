package org.vector.assistant.service;

import java.net.URI;
import java.util.Objects;

import com.theokanning.openai.assistants.ModifyAssistantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.assistant.dto.assistant.AssistantDTO;
import org.vector.assistant.dto.assistant.CreateAssistantRequest;
import org.vector.assistant.dto.assistant.UpdateAssistantRequest;
import org.vector.assistant.exception.conflict.AssistantAlreadyExistsException;
import org.vector.assistant.exception.notfound.AssistantNotFoundException;
import org.vector.assistant.persistance.dao.AssistantDao;
import org.vector.assistant.security.AuthenticationService;
import org.vector.assistant.util.mapper.AssistantMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AssistantService {

    private final AiService aiService;

    private final AssistantDao assistantDao;

    private final AuthenticationService authenticationService;

    private final AssistantMapper assistantMapper;

    public Mono<URI> createAssistant(final CreateAssistantRequest request) {
        return authenticationService.getUserId().flatMap(userId -> assistantDao
                .existsByNameAndUserId(request.name(), request.userId())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new AssistantAlreadyExistsException());
                    }
                    return aiService
                            .createAssistant(request)
                            .map(assistant -> assistantMapper.toEntity(assistant, userId))
                            .flatMap(assistantDao::createAssistant)
                            .map(assistant -> URI.create(Objects.requireNonNull(assistant.getId())));
                }));
    }

    public Mono<AssistantDTO> getAssistant(final String assistantId) {
        return authenticationService.getUserId().flatMap(userId -> assistantDao
                .getAssistant(assistantId, userId)
                .switchIfEmpty(Mono.error(AssistantNotFoundException::new))
                .flatMap(assistantEntity -> aiService
                        .getAssistant(assistantId)
                        .onErrorResume(e -> {
                            log.error("Error retrieving assistant from AI service: {}", e.getMessage());
                            return Mono.error(
                                    new AssistantNotFoundException(String.format("OpenAI : %s", e.getMessage())));
                        })
                        .map(aiServiceAssistant -> assistantMapper.toDto(assistantEntity))));
    }

    public Flux<AssistantDTO> getAssistants() {
        return authenticationService
                .getUserId()
                .flatMapMany(assistantDao::getAssistantsForCurrentUser)
                .map(assistantMapper::toDto);
    }

    public Mono<URI> updateAssistant(final String assistantId, final UpdateAssistantRequest request) {
        return authenticationService.getUserId().flatMap(userId -> assistantDao
                .existsByIdAndUserId(assistantId, userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new AssistantNotFoundException());
                    }
                    return aiService
                            .updateAssistant(
                                    assistantId,
                                    ModifyAssistantRequest.builder()
                                            .instructions(request.instructions())
                                            .build())
                            .onErrorResume(e -> {
                                log.error("Error updating assistant from AI service: {}", e.getMessage());
                                return Mono.error(
                                        new AssistantNotFoundException(String.format("OpenAI : %s", e.getMessage())));
                            })
                            .map(assistant -> assistantMapper.toEntity(assistant, userId))
                            .flatMap(assistantDao::updateAssistant)
                            .map(assistant -> URI.create(Objects.requireNonNull(assistant.getId())));
                }));
    }

    public Mono<Void> deleteAssistant(final String assistantId) {
        return authenticationService.getUserId().flatMap(userId -> assistantDao
                .existsByIdAndUserId(assistantId, userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new AssistantNotFoundException());
                    }
                    return aiService
                            .deleteAssistant(assistantId)
                            .onErrorResume(e -> {
                                log.error("Error deleting assistant from AI service: {}", e.getMessage());
                                return Mono.error(
                                        new AssistantNotFoundException(String.format("OpenAI : %s", e.getMessage())));
                            })
                            .flatMap(deleteResult -> {
                                if (!deleteResult.isDeleted()) {
                                    return Mono.error(new RuntimeException());
                                }
                                return assistantDao.deleteAssistant(deleteResult.getId());
                            })
                            .doOnSuccess(assistant -> log.debug("Assistant with id {} was deleted", assistantId));
                }));
    }
}
