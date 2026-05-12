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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;

/**
 * Auto configuration for AES encrypted Kafka message support.
 *
 * @author Cursor
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KafkaOperations.class)
@ConditionalOnProperty(prefix = AesKafkaProperties.PREFIX, name = "enabled",
		havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(AesKafkaProperties.class)
public class KafkaAesAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = AesKafkaProperties.PREFIX, name = "secret-key")
	public AesKafkaMessageEncryptor aesKafkaMessageEncryptor(
			AesKafkaProperties properties) {
		return new DefaultAesKafkaMessageEncryptor(properties);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(AesKafkaMessageEncryptor.class)
	public AesKafkaMessageProcessor aesKafkaMessageProcessor(
			AesKafkaMessageEncryptor encryptor, AesKafkaProperties properties) {
		return new AesKafkaMessageProcessor(encryptor, properties);
	}

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({ KafkaOperations.class, AesKafkaMessageEncryptor.class })
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public AesKafkaTemplate aesKafkaTemplate(KafkaOperations kafkaOperations,
			AesKafkaMessageEncryptor encryptor, AesKafkaProperties properties) {
		return new AesKafkaTemplate(kafkaOperations, encryptor, properties);
	}

}
