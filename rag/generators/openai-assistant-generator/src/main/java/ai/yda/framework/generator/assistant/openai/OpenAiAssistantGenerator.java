package ai.yda.framework.generator.assistant.openai;

import java.util.List;

import com.azure.ai.openai.assistants.AssistantsClient;
import com.azure.ai.openai.assistants.models.*;
import lombok.RequiredArgsConstructor;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.model.RagContext;
import ai.yda.framework.rag.core.session.SessionProvider;

@RequiredArgsConstructor
public class OpenAiAssistantGenerator implements Generator<BaseAssistantRequest, RagContext, BaseAssistantResponse> {

    private final String assistantId;

    private final AssistantsClient assistantsClient;

    private SessionProvider sessionProvider;

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request, RagContext context) {

        var createAndRunThreadOptions = new CreateAndRunThreadOptions(assistantId)
                .setThread(new AssistantThreadCreationOptions()
                        .setMessages(List.of(new ThreadMessageOptions(MessageRole.USER, request.getContent()))));
        var run = assistantsClient.createThreadAndRun(createAndRunThreadOptions);

        do {
            run = assistantsClient.getRun(run.getThreadId(), run.getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } while (run.getStatus() == RunStatus.QUEUED || run.getStatus() == RunStatus.IN_PROGRESS);

        PageableList<ThreadMessage> messages = assistantsClient.listMessages(run.getThreadId());
        List<ThreadMessage> data = messages.getData();
        BaseAssistantResponse response = null;

        for (int i = 0; i < data.size(); i++) {
            ThreadMessage dataMessage = data.get(i);
            MessageRole role = dataMessage.getRole();
            if (role.equals(MessageRole.ASSISTANT)) {
                for (MessageContent messageContent : dataMessage.getContent()) {
                    MessageTextContent messageTextContent = (MessageTextContent) messageContent;
                    System.out.println(i + ": Role = " + role + ", content = "
                            + messageTextContent.getText().getValue());
                    response = BaseAssistantResponse.builder()
                            .content(messageTextContent.getText().getValue())
                            .build();
                }
            }
        }
        return response;
    }

    @Override
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }
}
