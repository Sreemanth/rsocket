package com.sreemanth.rsocket.data;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import lombok.extern.slf4j.Slf4j;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@ShellComponent
public class RSocketShellClient {

	private RSocketRequester rSocketRequester;

	private Disposable disposable;
	private Disposable disposable2;

	private AtomicLong counter = new AtomicLong();

	public RSocketShellClient(RSocketRequester.Builder builder) {
		rSocketRequester = builder.connectTcp("localhost", 7000).block();
		log.info("rSocketRequester", rSocketRequester);
	}

	@ShellMethod("Send one request, response will come")
	public void requestResponse() {

		Message message = rSocketRequester.route("request-response")
				.data(new Message("Client", "request-response", counter.incrementAndGet())).retrieveMono(Message.class)
				.block();
		log.info("Response:" + message);

	}

	@ShellMethod("Fire and forget, send data and ignore")
	public void fireAndForget() {
		rSocketRequester.route("fire-and-forget")
				.data(new Message("Client", "fire-and-forget", counter.incrementAndGet())).send().block();
	}

	@ShellMethod("Stream data")
	public void stream() {
		this.disposable = rSocketRequester.route("stream")
				.data(new Message("Client", "stream", counter.incrementAndGet())).retrieveFlux(Message.class)
				.subscribe((err) -> {
					log.info("Response recieved: {}", err);
				});
	}

	@ShellMethod("Stop stream data")
	public void s() {
		if (this.disposable != null) {
			this.disposable.dispose();
		}
		if (this.disposable2 != null) {
			this.disposable2.dispose();
		}

	}

	@ShellMethod("Stream some settings to server, Stream of responses will be printed.")
	public void channel() {
		Mono<Duration> setting1 = Mono.just(Duration.ofSeconds(1));
		Mono<Duration> setting2 = Mono.just(Duration.ofSeconds(3)).delayElement(Duration.ofSeconds(5));
		Mono<Duration> settings3 = Mono.just(Duration.ofSeconds(5)).delayElement(Duration.ofSeconds(15));
		Flux<Duration> settings = Flux.concat(setting1, setting2, settings3);

		this.disposable2 = this.rSocketRequester.route("channel").data(settings).retrieveFlux(Message.class)
				.subscribe(message -> log.info("Recieved: {} (Type s to stop)", message));
	}

}
