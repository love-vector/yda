package ai.yda.framework.core.channel.factory.netty;

import java.io.IOException;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

import ai.yda.common.shared.model.impl.BaseAssistantRequest;
import ai.yda.common.shared.model.impl.BaseAssistantResponse;
import ai.yda.framework.core.channel.AbstractChannel;
import ai.yda.framework.core.channel.Channel;
import ai.yda.framework.core.channel.factory.AbstractChannelFactory;
import ai.yda.framework.core.channel.factory.ChannelConfiguration;

public class HttpNettyChannelFactory extends AbstractChannelFactory<BaseAssistantRequest, BaseAssistantResponse> {

    @Override
    public Channel<BaseAssistantRequest, BaseAssistantResponse> createChannel(
            ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration) {
        return new HttpNettyChannel(configuration);
    }

    private static class HttpNettyChannel extends AbstractChannel {
        private final ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration;

        public HttpNettyChannel(ChannelConfiguration<BaseAssistantRequest, BaseAssistantResponse> configuration) {
            this.configuration = configuration;
            Executors.newSingleThreadExecutor().execute(this::setupHttpServer);
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
                            public void initChannel(SocketChannel ch) {
                                ch.pipeline().addLast(new HttpServerCodec());
                                ch.pipeline().addLast(new HttpObjectAggregator(65536));
                                ch.pipeline().addLast(new HttpServerExpectContinueHandler());
                                ch.pipeline().addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req)
                                            throws IOException {
                                        if (!configuration
                                                .getMethod()
                                                .equalsIgnoreCase(req.method().name())) {
                                            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
                                            return;
                                        }

                                        BaseAssistantRequest request = new ObjectMapper()
                                                .readValue(
                                                        req.content().toString(io.netty.util.CharsetUtil.UTF_8),
                                                        configuration.getRequestClass());
                                        BaseAssistantResponse response = processRequest(request);

                                        FullHttpResponse httpResponse = new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_1,
                                                HttpResponseStatus.OK,
                                                Unpooled.copiedBuffer(
                                                        new ObjectMapper().writeValueAsString(response),
                                                        io.netty.util.CharsetUtil.UTF_8));
                                        httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");

                                        ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
                                    }

                                    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
                                        FullHttpResponse response = new DefaultFullHttpResponse(
                                                HttpVersion.HTTP_1_1,
                                                status,
                                                Unpooled.copiedBuffer(
                                                        "Failure: " + status.toString() + "\r\n",
                                                        io.netty.util.CharsetUtil.UTF_8));
                                        response.headers()
                                                .set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
                                        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
                                    }
                                });
                            }
                        });

                ChannelFuture f = b.bind(8081).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }
    }
}
