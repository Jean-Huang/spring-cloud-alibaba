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

package com.alibaba.cloud.drools;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import com.alibaba.cloud.drools.compile.DroolsRuleBuilder;
import com.alibaba.cloud.drools.compile.DroolsRuleCompiler;
import com.alibaba.cloud.drools.model.CompiledRuleSet;
import com.alibaba.cloud.drools.model.DroolsRuleDeploymentResult;
import com.alibaba.cloud.drools.model.RuleDefinition;
import com.alibaba.cloud.drools.model.RuleSetDefinition;
import com.alibaba.cloud.drools.publish.DroolsRulePublisher;
import com.alibaba.cloud.drools.publish.RulePublishResult;
import com.alibaba.cloud.drools.publish.RuleRuntimePlatformClient;
import com.alibaba.cloud.drools.test.DroolsRuleTester;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the end-to-end Drools rule service.
 *
 * @author Cursor
 */
public class DroolsRuleServiceTests {

	@Test
	public void shouldCompileTestAndPublishRuleSet() {
		AtomicReference<CompiledRuleSet> publishedRuleSet = new AtomicReference<>();
		DroolsRuleService ruleService = new DroolsRuleService(
				new DroolsRuleCompiler(
						new DroolsRuleBuilder("com.alibaba.cloud.drools.test")),
				new DroolsRuleTester(),
				new DroolsRulePublisher(new CapturingRuntimePlatformClient(
						publishedRuleSet)));
		OrderFact order = new OrderFact(120);

		DroolsRuleDeploymentResult result = ruleService.compileTestAndPublish(
				createRuleSet(), Collections.singletonList(order));

		assertThat(order.isHighValue()).isTrue();
		assertThat(result.getTestResult().getFiredRules()).isEqualTo(1);
		assertThat(result.getTestResult().getFiredRuleNames())
				.containsExactly("mark high value order");
		assertThat(result.getPublishResult().isSuccess()).isTrue();
		assertThat(publishedRuleSet.get()).isSameAs(result.getCompiledRuleSet());
	}

	private RuleSetDefinition createRuleSet() {
		return RuleSetDefinition.of("order-risk", "v1")
				.addImport(OrderFact.class.getName())
				.addRule(RuleDefinition.of("mark high value order",
						"$order : OrderFact(amount > 100)",
						"$order.setHighValue(true);"));
	}

	private static final class CapturingRuntimePlatformClient
			implements RuleRuntimePlatformClient {

		private final AtomicReference<CompiledRuleSet> publishedRuleSet;

		private CapturingRuntimePlatformClient(
				AtomicReference<CompiledRuleSet> publishedRuleSet) {
			this.publishedRuleSet = publishedRuleSet;
		}

		@Override
		public RulePublishResult publish(CompiledRuleSet compiledRuleSet) {
			this.publishedRuleSet.set(compiledRuleSet);
			return RulePublishResult.success(compiledRuleSet.getId(),
					compiledRuleSet.getVersion(), "published");
		}

	}

}
