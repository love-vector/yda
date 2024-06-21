package ai.yda.framework.generator.theokanning;

import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.generator.theokanning.dto.RunRequest;
import ai.yda.framework.generator.theokanning.util.CustomHttpHeaders;
import ai.yda.framework.rag.core.generator.Generator;
import ai.yda.framework.rag.core.session.SessionProvider;

@RequiredArgsConstructor
public class TheoKanningGenerator implements Generator<BaseAssistantRequest, BaseAssistantResponse> {

    private SessionProvider sessionProvider;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public BaseAssistantResponse generate(BaseAssistantRequest request) {

        var xx = createRunStream(
                "thread_fIXyqifnwBEMMyrxlPfl8YqW",
                RunRequest.builder()
                        .assistantId("asst_9S7lP9N2n99EPept42iUjLeL")
                        .build());
        System.out.println(xx);

        return BaseAssistantResponse.builder().build();
    }

    public List<String> createRunStream(final String threadId, final RunRequest runRequest) {
        String key =
                "sk-proj-eTAeDDwK4dEQY09ZQktbT3BlbkFJeaKh8oDF3sZT00LtAVnf"; // Blocking to get the key synchronously

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + key);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(CustomHttpHeaders.OPEN_AI_BETA, "assistants=v2");

        HttpEntity<RunRequest> requestEntity = new HttpEntity<>(runRequest, headers);

        ResponseEntity<String[]> response = restTemplate.exchange(
                "https://api.openai.com/v1/threads/{thread_id}/runs",
                HttpMethod.POST,
                requestEntity,
                String[].class,
                threadId);

        return response.getBody() != null ? List.of(response.getBody()) : Collections.emptyList();
    }

    @Override
    public SessionProvider getSessionProvider() {
        return sessionProvider;
    }
}
