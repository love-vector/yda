package ai.yda.framework.channel.http.streaming.factory;

import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.channel.http.streaming.config.HttpStreamingChannelConfig;
import ai.yda.framework.core.channel.AbstractChannel;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.core.channel.factory.AbstractChannelFactory;
import ai.yda.framework.core.channel.factory.ChannelConfiguration;

public class WebFluxChannelFactory extends AbstractChannelFactory<BaseAssistantRequest, BaseAssistantResponse> {

    @Override
    public Channel<BaseAssistantRequest, BaseAssistantResponse> createChannel(
            ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration) {
        return new WebFluxChannel(configuration);
    }

    private static class WebFluxChannel extends AbstractChannel {
        private final ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration;

        public WebFluxChannel(ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration) {
            this.configuration = configuration;
            setupHttpServer();
        }

        private void setupHttpServer() {
            RouterFunction<ServerResponse> route = RouterFunctions.route(
                    RequestPredicates.POST(configuration.getConfigs().get(HttpStreamingChannelConfig.URI))
                            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
                    this::handleRequest);
            // Note: RouterFunction must be registered in the Spring application context
        }

        private Mono<ServerResponse> handleRequest(ServerRequest request) {
            return request.bodyToMono(configuration.getRequestClass())
                    //                    .flatMap(this::processRequest)
                    .flatMap(response -> ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response));
        }

        //        private Mono<BaseAssistantResponse> processRequest(BaseAssistantRequest request) {
        ////            return Mono.fromCallable(() -> assistant.processRequest(request));
        //            return null;
        //        }
    }
}
