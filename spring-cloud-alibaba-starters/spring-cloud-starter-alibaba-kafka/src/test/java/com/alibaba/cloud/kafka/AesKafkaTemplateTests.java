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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link AesKafkaTemplate}.
 *
 * @author Cursor
 */
public class AesKafkaTemplateTests {

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void shouldEncryptProducerRecordBeforeSending() {
		KafkaOperations<String, String> kafkaOperations = mock(KafkaOperations.class);
		SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<SendResult<String, String>>();
		when(kafkaOperations.send(any(ProducerRecord.class))).thenReturn(future);
		AesKafkaProperties properties = properties();
		AesKafkaMessageEncryptor encryptor = new DefaultAesKafkaMessageEncryptor(
				properties);
		AesKafkaTemplate<String> template = new AesKafkaTemplate<String>(kafkaOperations,
				encryptor, properties);

		template.send("topic", "key", "hello kafka");

		ArgumentCaptor<ProducerRecord> captor = ArgumentCaptor
				.forClass(ProducerRecord.class);
		verify(kafkaOperations).send(captor.capture());
		ProducerRecord<String, String> record = captor.getValue();
		assertThat(record.value()).isNotEqualTo("hello kafka");
		assertThat(encryptor.decrypt(record.value())).isEqualTo("hello kafka");
		assertThat(record.headers().lastHeader(properties.getEncryptedHeader()))
				.isNotNull();
	}

	private AesKafkaProperties properties() {
		AesKafkaProperties properties = new AesKafkaProperties();
		properties.setSecretKey("1234567890123456");
		return properties;
	}

}
