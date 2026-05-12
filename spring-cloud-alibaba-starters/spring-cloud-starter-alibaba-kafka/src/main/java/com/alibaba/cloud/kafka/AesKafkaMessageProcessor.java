/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.kafka;

import java.util.function.Consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Consumer-side helper for decrypting and processing AES encrypted Kafka messages.
 *
 * @author Cursor
 */
public class AesKafkaMessageProcessor {

	private final AesKafkaMessageEncryptor encryptor;

	private final AesKafkaProperties properties;

	public AesKafkaMessageProcessor(AesKafkaMessageEncryptor encryptor,
			AesKafkaProperties properties) {
		Assert.notNull(encryptor, "AesKafkaMessageEncryptor must not be null");
		Assert.notNull(properties, "AesKafkaProperties must not be null");
		this.encryptor = encryptor;
		this.properties = properties;
	}

	public String decrypt(String payload) {
		return encryptor.decrypt(payload);
	}

	public byte[] decrypt(byte[] payload) {
		return encryptor.decrypt(payload);
	}

	public <K> String decrypt(ConsumerRecord<K, String> record) {
		Assert.notNull(record, "ConsumerRecord must not be null");
		return decrypt(record.value());
	}

	public Message<String> decrypt(Message<String> message) {
		Assert.notNull(message, "Message must not be null");
		MessageBuilder<String> messageBuilder = MessageBuilder
				.withPayload(decrypt(message.getPayload()))
				.copyHeaders(message.getHeaders());
		if (StringUtils.hasText(properties.getEncryptedHeader())) {
			messageBuilder.removeHeader(properties.getEncryptedHeader());
		}
		return messageBuilder.build();
	}

	public <K> void process(ConsumerRecord<K, String> record, Consumer<String> consumer) {
		Assert.notNull(consumer, "Consumer must not be null");
		consumer.accept(decrypt(record));
	}

	public void process(Message<String> message, Consumer<Message<String>> consumer) {
		Assert.notNull(consumer, "Consumer must not be null");
		consumer.accept(decrypt(message));
	}

}
