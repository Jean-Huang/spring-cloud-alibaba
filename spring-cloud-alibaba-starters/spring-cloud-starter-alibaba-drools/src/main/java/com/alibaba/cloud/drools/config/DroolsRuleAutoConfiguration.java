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

package com.alibaba.cloud.drools.config;

import com.alibaba.cloud.drools.DroolsRuleService;
import com.alibaba.cloud.drools.compile.DroolsRuleBuilder;
import com.alibaba.cloud.drools.compile.DroolsRuleCompiler;
import com.alibaba.cloud.drools.publish.DroolsRulePublisher;
import com.alibaba.cloud.drools.publish.NoopRuleRuntimePlatformClient;
import com.alibaba.cloud.drools.publish.RuleRuntimePlatformClient;
import com.alibaba.cloud.drools.test.DroolsRuleTester;
import org.kie.internal.utils.KieHelper;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-configuration for Drools rule services.
 *
 * @author Cursor
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(KieHelper.class)
@ConditionalOnProperty(prefix = "spring.cloud.alibaba.drools", name = "enabled",
		matchIfMissing = true)
@EnableConfigurationProperties(DroolsRuleProperties.class)
public class DroolsRuleAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public DroolsRuleBuilder droolsRuleBuilder(
			DroolsRuleProperties droolsRuleProperties) {
		return new DroolsRuleBuilder(droolsRuleProperties.getDefaultPackageName());
	}

	@Bean
	@ConditionalOnMissingBean
	public DroolsRuleCompiler droolsRuleCompiler(DroolsRuleBuilder droolsRuleBuilder) {
		return new DroolsRuleCompiler(droolsRuleBuilder);
	}

	@Bean
	@ConditionalOnMissingBean
	public DroolsRuleTester droolsRuleTester() {
		return new DroolsRuleTester();
	}

	@Bean
	@ConditionalOnMissingBean
	public RuleRuntimePlatformClient ruleRuntimePlatformClient() {
		return new NoopRuleRuntimePlatformClient();
	}

	@Bean
	@ConditionalOnMissingBean
	public DroolsRulePublisher droolsRulePublisher(
			RuleRuntimePlatformClient ruleRuntimePlatformClient) {
		return new DroolsRulePublisher(ruleRuntimePlatformClient);
	}

	@Bean
	@ConditionalOnMissingBean
	public DroolsRuleService droolsRuleService(DroolsRuleCompiler droolsRuleCompiler,
			DroolsRuleTester droolsRuleTester, DroolsRulePublisher droolsRulePublisher) {
		return new DroolsRuleService(droolsRuleCompiler, droolsRuleTester,
				droolsRulePublisher);
	}

}
