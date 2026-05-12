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

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;

import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.concurrent.ListenableFuture;

/**
 * Kafka sender that encrypts message payloads with AES before publishing.
 *
 * @param <K> Kafka message key type
 * @author Cursor
 */
public class AesKafkaTemplate<K> {

	private final KafkaOperations<K, String> kafkaOperations;

	private final AesKafkaMessageEncryptor encryptor;

	private final AesKafkaProperties properties;

	public AesKafkaTemplate(KafkaOperations<K, String> kafkaOperations,
			AesKafkaMessageEncryptor encryptor, AesKafkaProperties properties) {
		Assert.notNull(kafkaOperations, "KafkaOperations must not be null");
		Assert.notNull(encryptor, "AesKafkaMessageEncryptor must not be null");
		Assert.notNull(properties, "AesKafkaProperties must not be null");
		this.kafkaOperations = kafkaOperations;
		this.encryptor = encryptor;
		this.properties = properties;
	}

	public ListenableFuture<SendResult<K, String>> send(String topic, String data) {
		return send(new ProducerRecord<K, String>(topic, data));
	}

	public ListenableFuture<SendResult<K, String>> send(String topic, K key,
			String data) {
		return send(new ProducerRecord<K, String>(topic, key, data));
	}

	public ListenableFuture<SendResult<K, String>> send(String topic, Integer partition,
			K key, String data) {
		return send(new ProducerRecord<K, String>(topic, partition, key, data));
	}

	public ListenableFuture<SendResult<K, String>> send(ProducerRecord<K, String> record) {
		Assert.notNull(record, "ProducerRecord must not be null");
		ProducerRecord<K, String> encryptedRecord = new ProducerRecord<K, String>(
				record.topic(), record.partition(), record.timestamp(), record.key(),
				encryptor.encrypt(record.value()), record.headers());
		markEncrypted(encryptedRecord);
		return kafkaOperations.send(encryptedRecord);
	}

	public ListenableFuture<SendResult<K, String>> send(Message<String> message) {
		Assert.notNull(message, "Message must not be null");
		MessageBuilder<String> messageBuilder = MessageBuilder
				.withPayload(encryptor.encrypt(message.getPayload()))
				.copyHeaders(message.getHeaders());
		if (StringUtils.hasText(properties.getEncryptedHeader())) {
			messageBuilder.setHeader(properties.getEncryptedHeader(), true);
		}
		Message<String> encryptedMessage = messageBuilder.build();
		return kafkaOperations.send(encryptedMessage);
	}

	private void markEncrypted(ProducerRecord<K, String> record) {
		if (StringUtils.hasText(properties.getEncryptedHeader())
				&& record.value() != null) {
			record.headers().add(new RecordHeader(properties.getEncryptedHeader(), "true"
					.getBytes(StandardCharsets.UTF_8)));
		}
	}

}
