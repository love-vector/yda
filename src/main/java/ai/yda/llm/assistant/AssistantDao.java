package ai.yda.llm.assistant;

import java.util.HashSet;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@RequiredArgsConstructor
public class AssistantDao {

    private final AssistantRepository assistantRepository;

    public AssistantEntity getAssistantById(final Long assistantId) {
        return assistantRepository.findById(assistantId).orElseThrow(AssistantNotFoundException::new);
    }

    public Set<AssistantEntity> getAssistants() {
        return new HashSet<>(assistantRepository.findAll());
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
