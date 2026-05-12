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

import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link AesKafkaMessageProcessor}.
 *
 * @author Cursor
 */
public class AesKafkaMessageProcessorTests {

	@Test
	public void shouldDecryptConsumerRecordPayload() {
		AesKafkaMessageEncryptor encryptor = encryptor();
		AesKafkaMessageProcessor processor = new AesKafkaMessageProcessor(encryptor,
				properties());
		ConsumerRecord<String, String> record = new ConsumerRecord<String, String>("topic",
				0, 0L, "key", encryptor.encrypt("hello kafka"));

		assertThat(processor.decrypt(record)).isEqualTo("hello kafka");
	}

	@Test
	public void shouldProcessConsumerRecordWithDecryptedPayload() {
		AesKafkaMessageEncryptor encryptor = encryptor();
		AesKafkaMessageProcessor processor = new AesKafkaMessageProcessor(encryptor,
				properties());
		ConsumerRecord<String, String> record = new ConsumerRecord<String, String>("topic",
				0, 0L, "key", encryptor.encrypt("hello kafka"));
		AtomicReference<String> payload = new AtomicReference<String>();

		processor.process(record, payload::set);

		assertThat(payload.get()).isEqualTo("hello kafka");
	}

	@Test
	public void shouldDecryptMessageAndRemoveEncryptedHeader() {
		AesKafkaProperties properties = properties();
		AesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties);
		AesKafkaMessageProcessor processor = new AesKafkaMessageProcessor(encryptor,
				properties);
		Message<String> message = MessageBuilder
				.withPayload(encryptor.encrypt("hello kafka"))
				.setHeader(properties.getEncryptedHeader(), true).build();

		Message<String> decrypted = processor.decrypt(message);

		assertThat(decrypted.getPayload()).isEqualTo("hello kafka");
		assertThat(decrypted.getHeaders()).doesNotContainKey(properties.getEncryptedHeader());
	}

	private AesKafkaMessageEncryptor encryptor() {
		return new DefaultAesKafkaMessageEncryptor(properties());
	}

	private AesKafkaProperties properties() {
		AesKafkaProperties properties = new AesKafkaProperties();
		properties.setSecretKey("1234567890123456");
		return properties;
	}

}
