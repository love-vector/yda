package ai.yda.framework.rag.base.generator.assistant;

import lombok.RequiredArgsConstructor;

import ai.yda.framework.rag.core.generator.Generator;

@RequiredArgsConstructor
public abstract class AssistantGenerator implements Generator<AssistantRagRequest, AssistantRagResponse> {

    protected final String assistantId;
}
