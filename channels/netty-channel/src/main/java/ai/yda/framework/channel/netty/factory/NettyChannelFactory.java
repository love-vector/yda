package ai.yda.framework.channel.netty.factory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.HttpVersion;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.messages.AssistantMessage;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.framework.channel.netty.config.NettyChannelConfig;
import ai.yda.framework.core.channel.AbstractChannel;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.core.channel.factory.AbstractChannelFactory;
import ai.yda.framework.core.channel.factory.ChannelConfiguration;

@Slf4j
public class NettyChannelFactory extends AbstractChannelFactory<BaseAssistantRequest, AssistantMessage> {
    private static final int MAX_CONTENT_LENGTH = 65536;

    @Override
    public Channel<BaseAssistantRequest, AssistantMessage> createChannel(
            final ChannelConfiguration<BaseAssistantRequest, AssistantMessage> configuration) {
        return new HttpNettyChannel(configuration);
    }

    private static class HttpNettyChannel extends AbstractChannel<AssistantMessage> {
        private final ChannelConfiguration<BaseAssistantRequest, AssistantMessage> configuration;

        HttpNettyChannel(final ChannelConfiguration<BaseAssistantRequest, AssistantMessage> configuration) {
            this.configuration = configuration;
            try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
                executor.execute(this::setupHttpServer);
            }
        }

        private void setupHttpServer() {
            EventLoopGroup bossGroup = new NioEventLoopGroup(1);
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(@NonNull final SocketChannel ch) {
                                ch.pipeline().addLast(new HttpServerCodec());
                                ch.pipeline().addLast(new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                                ch.pipeline().addLast(new HttpServerExpectContinueHandler());
                                ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                    @Override
                                    protected void channelRead0(
                                            final ChannelHandlerContext ctx, final FullHttpRequest req)
                                            throws IOException {
                                        if (!configuration
                                                .getConfigs()
                                                .get(NettyChannelConfig.METHOD)
                                                .equalsIgnoreCase(req.method().name())) {
                                            sendError(ctx);
                                            return;
                                        }

                                        BaseAssistantRequest request = new ObjectMapper()
                                                .readValue(
                                                        req.content().toString(io.netty.util.CharsetUtil.UTF_8),
                                                        configuration.getRequestClass());
                                        var response = processRequest(request);

                                        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.OK,
                                                Unpooled.copiedBuffer(
                                                        new ObjectMapper().writeValueAsString(response),
                                                        io.netty.util.CharsetUtil.UTF_8));
                                        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

                                        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
                                    }

                                    private void sendError(final ChannelHandlerContext ctx) {
                                        FullHttpResponse response = new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.METHOD_NOT_ALLOWED,
                                                Unpooled.copiedBuffer(
                                                        "Failure: " + HttpResponseStatus.METHOD_NOT_ALLOWED + "\r\n",
                                                        io.netty.util.CharsetUtil.UTF_8));
                                        response.headers()
                                                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                    }
                                });
                            }
                        });

                ChannelFuture f = b.bind(
                                Integer.parseInt(configuration.getConfigs().get(NettyChannelConfig.PORT)))
                        .sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }
}
