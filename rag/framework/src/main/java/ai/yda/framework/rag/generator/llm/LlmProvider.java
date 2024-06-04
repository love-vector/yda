package ai.yda.framework.rag.generator.llm;

import java.util.List;
import java.util.concurrent.Flow;

public interface LlmProvider {

    Assistant getAssistant(final String assistantId);

    List<Assistant> getAssistants();

    Assistant createAssistant(final Assistant assistant);

    void updateAssistant(final String assistantId, final Assistant assistant);

    void deleteAssistant(final String assistantId);

    Thread createThread(final Thread thread);

    Message createMessage(final String threadId, final Message message);

    Flow.Publisher<String> streamRun(final String assistantId, final String threadId);
}
