package com.sreemanth.rsocket.data;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor

public class Message {

	private String origin;
	private long index;
	private String interaction;
	private long creationDate = Instant.now().getEpochSecond();

	public Message(String origin, String interaction, long index) {
		this.origin = origin;
		this.interaction = interaction;
		this.index = index;
	}
}
