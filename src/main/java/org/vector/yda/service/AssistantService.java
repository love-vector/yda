package org.vector.yda.service;

import java.net.URI;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.vector.yda.model.dto.AssistantDto;
import org.vector.yda.persistance.dao.AssistantDao;
import org.vector.yda.security.UserDetailsService;
import org.vector.yda.util.mapper.AssistantMapper;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AssistantService {

    private final UserDetailsService userDetailsService;
    private final OpenAiService openAiService;

    private final AssistantDao assistantDao;

    private final AssistantMapper assistantMapper;

    public AssistantDto getAssistant(final Long assistantId) {
        return assistantMapper.toDto(assistantDao.getAssistantById(assistantId));
    }

    public List<AssistantDto> getUserAssistants() {
        var user = userDetailsService.getAuthorizedUser();
        return assistantDao.getAssistantsByUserId(user.getId()).parallelStream()
                .map(assistantMapper::toDto)
                .toList();
    }

    public URI createAssistant(final AssistantDto assistantDto) {
        var user = userDetailsService.getAuthorizedUser();
        var openAiAssistant = openAiService.createAssistant(assistantDto);
        var assistant = assistantDao.createAssistant(
                assistantMapper.createEntity(assistantDto, openAiAssistant.getId(), user.getId()));
        return URI.create(assistant.getId().toString());
    }

    public void updateAssistant(final Long assistantId, final AssistantDto assistantDto) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistant = assistantDao.updateAssistant(assistantMapper.updateEntity(assistant, assistantDto));
        openAiService.updateAssistant(assistant.getAssistantId(), assistantMapper.toModifyAssistantRequest(assistant));
    }

    public void deleteAssistant(final Long assistantId) {
        var assistant = assistantDao.getAssistantById(assistantId);
        assistantDao.deleteAssistant(assistant);
        openAiService.deleteAssistant(assistant.getAssistantId());
    }
}
