package ai.yda.framework.llm;

import java.util.List;
import java.util.concurrent.Flow;

public interface LlmProviderService {

    AssistantPrototype getAssistant(final String assistantId);

    List<AssistantPrototype> getAssistants();

    AssistantPrototype createAssistant(final AssistantPrototype assistantPrototype);

    void updateAssistant(final String assistantId, final AssistantPrototype assistantPrototype);

    void deleteAssistant(final String assistantId);

    ThreadPrototype createThread(final ThreadPrototype threadPrototype);

    MessagePrototype createMessage(final String threadId, final MessagePrototype messagePrototype);

    Flow.Publisher<String> streamRun(final String assistantId, final String threadId);
}
