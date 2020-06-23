package com.sreemanth.rsocket.contoller;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class RSocketController {

	private AtomicLong counter = new AtomicLong();

	@MessageMapping("request-response")
	public Message requestResponse(Message message) {
		log.info("Request:{}", message);
		return new Message("Server", "Response", counter.incrementAndGet());
	}

	@MessageMapping("fire-and-forget")
	public void fireAndForget(Message message) {
		log.info("Request(Fire and Forget):{}", message);
	}

	@MessageMapping("stream")
	public Flux<Message> stream(Message message) {

		return Flux.interval(Duration.ofSeconds(1)).map(index -> new Message("Server", "Stream", index)).log();
	}

	@MessageMapping("channel")
	public Flux<Message> channel(Flux<Duration> settings) {
		return settings.doOnNext(setting -> log.info("\n Frequency Duration {}", setting.getSeconds()))
				.switchMap(setting -> Flux.interval(setting).map(index -> new Message("Server", "channel", index)))
				.log();

	}
}
