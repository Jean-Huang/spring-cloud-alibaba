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

import org.junit.Test;

import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link KafkaAesAutoConfiguration}.
 *
 * @author Cursor
 */
public class KafkaAesAutoConfigurationTests {

	private ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KafkaAesAutoConfiguration.class));

	@Test
	public void shouldNotCreateEncryptionBeansWithoutSecretKey() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(AesKafkaProperties.class);
			assertThat(context).doesNotHaveBean(AesKafkaMessageEncryptor.class);
			assertThat(context).doesNotHaveBean(AesKafkaMessageProcessor.class);
		});
	}

	@Test
	public void shouldCreateEncryptionBeansWhenSecretKeyConfigured() {
		this.contextRunner
				.withPropertyValues(
						"spring.cloud.alibaba.kafka.aes.secret-key=1234567890123456")
				.run(context -> {
					assertThat(context).hasSingleBean(AesKafkaMessageEncryptor.class);
					assertThat(context).hasSingleBean(AesKafkaMessageProcessor.class);
				});
	}

	@Test
	public void shouldCreateAesKafkaTemplateWhenKafkaOperationsExists() {
		this.contextRunner.withUserConfiguration(KafkaOperationsConfiguration.class)
				.withPropertyValues(
						"spring.cloud.alibaba.kafka.aes.secret-key=1234567890123456")
				.run(context -> assertThat(context)
						.hasSingleBean(AesKafkaTemplate.class));
	}

	@Test
	public void shouldBackOffWhenDisabled() {
		this.contextRunner.withPropertyValues(
				"spring.cloud.alibaba.kafka.aes.secret-key=1234567890123456",
				"spring.cloud.alibaba.kafka.aes.enabled=false").run(context -> {
					assertThat(context).doesNotHaveBean(AesKafkaMessageEncryptor.class);
					assertThat(context).doesNotHaveBean(AesKafkaMessageProcessor.class);
				});
	}

	@Configuration
	protected static class KafkaOperationsConfiguration {

		@Bean
		@SuppressWarnings("unchecked")
		public KafkaOperations<String, String> kafkaOperations() {
			return mock(KafkaOperations.class);
		}

	}

}
