package ai.yda.core;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ai.yda.context.ContextModuleService;
import ai.yda.intent.IntentModuleService;
import ai.yda.knowledge.KnowledgeModuleService;
import ai.yda.llm.LlmModuleService;

@Service
@RequiredArgsConstructor
public class CoreModuleService {

    private final IntentModuleService intentService;
    private final KnowledgeModuleService knowledgeService;
    private final ContextModuleService contextService;
    private final LlmModuleService llmService;
}
