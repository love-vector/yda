package ai.yda.test;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import ai.yda.framework.llm.LlmProvider;

@Service
@RequiredArgsConstructor
public class TestService {

    private final LlmProvider azureProvider;

    public void testTest() {}
}
