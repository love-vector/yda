package ai.yda.framework.llm;

import java.util.List;
import java.util.concurrent.Flow;

public interface LlmProviderService {

    Assistant getAssistant(final String assistantId);

    List<Assistant> getAssistants();

    Assistant createAssistant(final Assistant assistant);

    void updateAssistant(final String assistantId, final Assistant assistant);

    void deleteAssistant(final String assistantId);

    Thread createThread(final Thread thread);

    Message createMessage(final String threadId, final Thread thread);

    Flow.Publisher<String> streamRun(final String assistantId, final String threadId);
}
