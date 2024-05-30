package ai.yda.framework.core;

import ai.yda.framework.context.ContextService;
import ai.yda.framework.intent.IntentService;
import ai.yda.framework.knowledge.KnowledgeService;
import lombok.RequiredArgsConstructor;

import ai.yda.framework.llm.LlmService;

@RequiredArgsConstructor
public class CoreService {

    private final IntentService intentService;
    private final KnowledgeService knowledgeService;
    private final ContextService contextService;
    private final LlmService llmService;
}
