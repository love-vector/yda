/*
 * YDA - Open-Source Java AI Assistant.
 * Copyright (C) 2024 Love Vector OÜ <https://vector-inc.dev/>

 * This file is part of YDA.

 * YDA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * YDA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public License
 * along with YDA.  If not, see <https://www.gnu.org/licenses/>.
*/
package ai.yda.framework.core.rag.transformation;

import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.openai.api.ResponseFormat;

@Slf4j
@Builder
@Getter
@Setter
public class ReasoningOpenAiChatOptions implements FunctionCallingOptions {

    // @formatter:off
    /**
     * ID of the model to use.
     */
    private @JsonProperty("model") String model;
    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on their existing
     * frequency in the text so far, decreasing the model's likelihood to repeat the same line verbatim.
     */
    private @JsonProperty("frequency_penalty") Double frequencyPenalty;
    /**
     * Modify the likelihood of specified tokens appearing in the completion. Accepts a JSON object
     * that maps tokens (specified by their token ID in the tokenizer) to an associated bias value from -100 to 100.
     * Mathematically, the bias is added to the logits generated by the model prior to sampling. The exact effect will
     * vary per model, but values between -1 and 1 should decrease or increase likelihood of selection; values like -100
     * or 100 should result in a ban or exclusive selection of the relevant token.
     */
    private @JsonProperty("logit_bias") Map<String, Integer> logitBias;
    /**
     * Whether to return log probabilities of the output tokens or not. If true, returns the log probabilities
     * of each output token returned in the 'content' of 'message'.
     */
    private @JsonProperty("logprobs") Boolean logprobs;
    /**
     * An integer between 0 and 5 specifying the number of most likely tokens to return at each token position,
     * each with an associated log probability. 'logprobs' must be set to 'true' if this parameter is used.
     */
    private @JsonProperty("top_logprobs") Integer topLogprobs;
    /**
     * The maximum number of tokens to generate in the chat completion. The total length of input
     * tokens and generated tokens is limited by the model's context length.
     */
    private @JsonProperty("max_tokens") Integer maxTokens;
    /**
     * An upper bound for the number of tokens that can be generated for a completion,
     * including visible output tokens and reasoning tokens.
     */
    private @JsonProperty("max_completion_tokens") Integer maxCompletionTokens;
    /**
     * How many chat completion choices to generate for each input message. Note that you will be charged based
     * on the number of generated tokens across all of the choices. Keep n as 1 to minimize costs.
     */
    private @JsonProperty("n") Integer n;

    /**
     * Output types that you would like the model to generate for this request.
     * Most models are capable of generating text, which is the default.
     * The gpt-4o-audio-preview model can also be used to generate audio.
     * To request that this model generate both text and audio responses,
     * you can use: ["text", "audio"].
     * Note that the audio modality is only available for the gpt-4o-audio-preview model
     * and is not supported for streaming completions.
     */
    private @JsonProperty("modalities") List<String> outputModalities;

    /**
     * Audio parameters for the audio generation. Required when audio output is requested with
     * modalities: ["audio"]
     * Note: that the audio modality is only available for the gpt-4o-audio-preview model
     * and is not supported for streaming completions.
     *
     */
    private @JsonProperty("audio") OpenAiApi.ChatCompletionRequest.AudioParameters outputAudio;

    /**
     * Number between -2.0 and 2.0. Positive values penalize new tokens based on whether they
     * appear in the text so far, increasing the model's likelihood to talk about new topics.
     */
    private @JsonProperty("presence_penalty") Double presencePenalty;
    /**
     * An object specifying the format that the model must output. Setting to { "type":
     * "json_object" } enables JSON mode, which guarantees the message the model generates is valid JSON.
     */
    private @JsonProperty("response_format") ResponseFormat responseFormat;
    /**
     * Options for streaming response. Included in the API only if streaming-mode completion is requested.
     */
    private @JsonProperty("stream_options") OpenAiApi.ChatCompletionRequest.StreamOptions streamOptions;
    /**
     * This feature is in Beta. If specified, our system will make a best effort to sample
     * deterministically, such that repeated requests with the same seed and parameters should return the same result.
     * Determinism is not guaranteed, and you should refer to the system_fingerprint response parameter to monitor
     * changes in the backend.
     */
    private @JsonProperty("seed") Integer seed;
    /**
     * Up to 4 sequences where the API will stop generating further tokens.
     */
    private @JsonProperty("stop") List<String> stop;
    /**
     * What sampling temperature to use, between 0 and 1. Higher values like 0.8 will make the output
     * more random, while lower values like 0.2 will make it more focused and deterministic. We generally recommend
     * altering this or top_p but not both.
     */
    private @JsonProperty("temperature") Double temperature;
    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the
     * results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10%
     * probability mass are considered. We generally recommend altering this or temperature but not both.
     */
    private @JsonProperty("top_p") Double topP;
    /**
     * A list of tools the model may call. Currently, only functions are supported as a tool. Use this to
     * provide a list of functions the model may generate JSON inputs for.
     */
    private @JsonProperty("tools") List<OpenAiApi.FunctionTool> tools;
    /**
     * Controls which (if any) function is called by the model. none means the model will not call a
     * function and instead generates a message. auto means the model can pick between generating a message or calling a
     * function. Specifying a particular function via {"type: "function", "function": {"name": "my_function"}} forces
     * the model to call that function. none is the default when no functions are present. auto is the default if
     * functions are present. Use the {@link OpenAiApi.ChatCompletionRequest.ToolChoiceBuilder} to create a tool choice object.
     */
    private @JsonProperty("tool_choice") Object toolChoice;
    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    private @JsonProperty("user") String user;
    /**
     * Whether to enable <a href="https://platform.openai.com/docs/guides/function-calling/parallel-function-calling">parallel function calling</a> during tool use.
     * Defaults to true.
     */
    private @JsonProperty("parallel_tool_calls") Boolean parallelToolCalls;

    /**
     * OpenAI Tool Function Callbacks to register with the ChatModel.
     * For Prompt Options the functionCallbacks are automatically enabled for the duration of the prompt execution.
     * For Default Options the functionCallbacks are registered but disabled by default. Use the enableFunctions to set the functions
     * from the registry to be used by the ChatModel chat completion requests.
     */
    @JsonIgnore
    private List<FunctionCallback> functionCallbacks = new ArrayList<>();

    /**
     * List of functions, identified by their names, to configure for function calling in
     * the chat completion requests.
     * Functions with those names must exist in the functionCallbacks registry.
     * The {@link #functionCallbacks} from the PromptOptions are automatically enabled for the duration of the prompt execution.
     *
     * Note that function enabled with the default options are enabled for all chat completion requests. This could impact the token count and the billing.
     * If the functions is set in a prompt options, then the enabled functions are only active for the duration of this prompt execution.
     */
    @JsonIgnore
    private Set<String> functions = new HashSet<>();

    /**
     * If true, the Spring AI will not handle the function calls internally, but will proxy them to the client.
     * It is the client's responsibility to handle the function calls, dispatch them to the appropriate function, and return the results.
     * If false, the Spring AI will handle the function calls internally.
     */
    @JsonIgnore
    private Boolean proxyToolCalls;

    /**
     * Optional HTTP headers to be added to the chat completion request.
     */
    @JsonIgnore
    private Map<String, String> httpHeaders = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> toolContext;

    /**
     * Constrains effort on reasoning for reasoning models. Currently supported values are low, medium, and high.
     * Reducing reasoning effort can result in faster responses and fewer tokens used on reasoning in a response.
     * Optional. Defaults to medium.
     * Only for 'o1' models.
     */
    private @JsonProperty("reasoning_effort") String reasoningEffort;

    @Override
    @JsonIgnore
    public List<String> getStopSequences() {
        return getStop();
    }

    @JsonIgnore
    public void setStopSequences(List<String> stopSequences) {
        setStop(stopSequences);
    }

    @Override
    @JsonIgnore
    public Integer getTopK() {
        return null;
    }

    public static ReasoningOpenAiChatOptions fromOptions(ReasoningOpenAiChatOptions fromOptions) {
        return ReasoningOpenAiChatOptions.builder()
                .model(fromOptions.getModel())
                .frequencyPenalty(fromOptions.getFrequencyPenalty())
                .logitBias(fromOptions.getLogitBias())
                .logprobs(fromOptions.getLogprobs())
                .topLogprobs(fromOptions.getTopLogprobs())
                .maxTokens(fromOptions.getMaxTokens())
                .maxCompletionTokens(fromOptions.getMaxCompletionTokens())
                .n(fromOptions.getN())
                .outputModalities(fromOptions.getOutputModalities())
                .outputAudio(fromOptions.getOutputAudio())
                .presencePenalty(fromOptions.getPresencePenalty())
                .responseFormat(fromOptions.getResponseFormat())
                .seed(fromOptions.getSeed())
                .stop(fromOptions.getStop())
                .temperature(fromOptions.getTemperature())
                .topP(fromOptions.getTopP())
                .tools(fromOptions.getTools())
                .toolChoice(fromOptions.getToolChoice())
                .user(fromOptions.getUser())
                .parallelToolCalls(fromOptions.getParallelToolCalls())
                .functionCallbacks(fromOptions.getFunctionCallbacks())
                .functions(fromOptions.getFunctions())
                .httpHeaders(fromOptions.getHttpHeaders())
                .proxyToolCalls(fromOptions.getProxyToolCalls())
                .toolContext(fromOptions.getToolContext())
                .reasoningEffort(fromOptions.getReasoningEffort())
                .build();
    }

    @Override
    public ReasoningOpenAiChatOptions copy() {
        return ReasoningOpenAiChatOptions.fromOptions(this);
    }

    @Override
    public String toString() {
        return "ReasoningOpenAiChatOptions: " + ModelOptionsUtils.toJsonString(this);
    }
}
