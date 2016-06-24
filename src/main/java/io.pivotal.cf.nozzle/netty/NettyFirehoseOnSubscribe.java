package io.pivotal.cf.nozzle.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.CharsetUtil;
import org.cloudfoundry.dropsonde.events.Envelope;
import reactor.core.publisher.FluxEmitter;

import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManagerFactory;
import java.io.Closeable;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

/**
 * Heavily based on Mike Heath's implementation of Snotel..
 */
class NettyFirehoseOnSubscribe implements Consumer<FluxEmitter<Envelope>>, Closeable {

	public static final String HANDLER_NAME = "handler";
	private final Channel channel;

	// Set this EventLoopGroup if an EventLoopGroup was not provided. This groups needs to be shutdown when the
	// firehose client shuts down.
	private final EventLoopGroup eventLoopGroup;
	private final Bootstrap bootstrap;

	public NettyFirehoseOnSubscribe(URI uri, String token, String subscriptionId, boolean skipTlsValidation, EventLoopGroup eventLoopGroup, Class<? extends SocketChannel> channelClass) {
		try {
			final String host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();
			final String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
			final int port = getPort(scheme, uri.getPort());
			final URI fullUri = uri.resolve("/firehose/" + subscriptionId);

			final SslContext sslContext;
			if ("wss".equalsIgnoreCase(scheme)) {
				final SslContextBuilder sslContextBuilder = SslContextBuilder.forClient();
				if (skipTlsValidation) {
					sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
				} else {
					sslContextBuilder.trustManager(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()));
				}
				sslContext = sslContextBuilder.build();
			} else {
				sslContext = null;
			}

			bootstrap = new Bootstrap();
			if (eventLoopGroup == null) {
				this.eventLoopGroup = new NioEventLoopGroup();
				bootstrap.group(this.eventLoopGroup);
			} else {
				this.eventLoopGroup = null;
				bootstrap.group(eventLoopGroup);
			}
			bootstrap
					.channel(channelClass == null ? NioSocketChannel.class : channelClass)
					.remoteAddress(host, port)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel channel) throws Exception {
							final HttpHeaders headers = new DefaultHttpHeaders();
							headers.add(HttpHeaders.Names.AUTHORIZATION, token);
							final WebSocketClientHandler handler =
									new WebSocketClientHandler(
											WebSocketClientHandshakerFactory.newHandshaker(
													fullUri, WebSocketVersion.V13, null, false, headers));
							final ChannelPipeline pipeline = channel.pipeline();
							if (sslContext != null) {
								pipeline.addLast(sslContext.newHandler(channel.alloc(), host, port));
							}
							pipeline.addLast(
									new HttpClientCodec(),
									new HttpObjectAggregator(8192));
							pipeline.addLast(HANDLER_NAME, handler);
						}
					});
			channel = null;
		} catch (NoSuchAlgorithmException | SSLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void accept(FluxEmitter<Envelope> envelopeFluxEmitter) {
		bootstrap.connect().addListener((ChannelFutureListener)future -> {
			if (future.isSuccess()) {
				future.channel().pipeline().get(WebSocketClientHandler.class).setSubscriber(envelopeFluxEmitter);
			} else {
				envelopeFluxEmitter.fail(future.cause());
			}
		});
	}

	@Override
	public void close() {
		if (channel != null) {
			channel.close();
		}
		if (eventLoopGroup != null) {
			eventLoopGroup.shutdownGracefully();
		}
	}

	private int getPort(String scheme, int port) {
		if (port == -1) {
			if ("ws".equalsIgnoreCase(scheme)) {
				return 80;
			} else if ("wss".equalsIgnoreCase(scheme)) {
				return 443;
			} else {
				return -1;
			}
		} else {
			return port;
		}
	}

	public boolean isConnected() {
		return channel != null && channel.isActive();
	}



	private static class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

		private final WebSocketClientHandshaker handshaker;

		private FluxEmitter<? super Envelope> fluxEmitter;

		public WebSocketClientHandler(WebSocketClientHandshaker handshaker) {
			this.handshaker = handshaker;
		}

		@Override
		public void channelActive(ChannelHandlerContext context) throws Exception {
			handshaker.handshake(context.channel());
		}

		@Override
		public void channelInactive(ChannelHandlerContext context) throws Exception {
			fluxEmitter.complete();
		}

		@Override
		public void exceptionCaught(ChannelHandlerContext context, Throwable cause) throws Exception {
			context.close();
			fluxEmitter.fail(cause);
		}

		@Override
		protected void channelRead0(ChannelHandlerContext context, Object message) throws Exception {
			final Channel channel = context.channel();
			if (!handshaker.isHandshakeComplete()) {
				handshaker.finishHandshake(channel, (FullHttpResponse) message);
				channel.pipeline().addBefore(HANDLER_NAME, "websocket-frame-aggregator", new WebSocketFrameAggregator(64 * 1024));
//				fluxEmitter.
				return;
			}

			if (message instanceof FullHttpResponse) {
				final FullHttpResponse response = (FullHttpResponse) message;
				throw new IllegalStateException(
						"Unexpected FullHttpResponse (getStatus=" + response.getStatus() +
								", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
			}

			final WebSocketFrame frame = (WebSocketFrame) message;
			if (frame instanceof PingWebSocketFrame) {
				context.writeAndFlush(new PongWebSocketFrame(((PingWebSocketFrame)frame).retain().content()));
			} else if (frame instanceof BinaryWebSocketFrame) {
				final ByteBufInputStream input = new ByteBufInputStream(((BinaryWebSocketFrame)message).content());
				final Envelope envelope = Envelope.ADAPTER.decode(input);
				fluxEmitter.next(envelope);
			}
		}

		public void setSubscriber(FluxEmitter<? super Envelope> fluxEmitter) {
			this.fluxEmitter = fluxEmitter;
		}
	}

}
