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

import com.alibaba.cloud.drools.compile.DroolsRuleBuildException;
import com.alibaba.cloud.drools.compile.DroolsRuleBuilder;
import com.alibaba.cloud.drools.model.RuleDefinition;
import com.alibaba.cloud.drools.model.RuleSetDefinition;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for {@link DroolsRuleBuilder}.
 *
 * @author Cursor
 */
public class DroolsRuleBuilderTests {

	private final DroolsRuleBuilder ruleBuilder = new DroolsRuleBuilder(
			"com.alibaba.cloud.drools.test");

	@Test
	public void shouldBuildDrlFromRuleSetDefinition() {
		RuleDefinition rule = RuleDefinition.of("high value order",
				"$order : OrderFact(amount > 100)", "$order.setHighValue(true);");
		rule.setSalience(10);
		RuleDefinition disabledRule = RuleDefinition.of("disabled",
				"$order : OrderFact(amount > 1)", "$order.setHighValue(false);");
		disabledRule.setEnabled(false);

		RuleSetDefinition ruleSet = RuleSetDefinition.of("order-risk", "v1")
				.addImport(OrderFact.class.getName())
				.addGlobal("result", "java.util.List").addRule(rule)
				.addRule(disabledRule);

		String drl = this.ruleBuilder.build(ruleSet);

		assertThat(drl).contains("package com.alibaba.cloud.drools.test")
				.contains("import " + OrderFact.class.getName() + ";")
				.contains("global java.util.List result;")
				.contains("rule \"high value order\"").contains("salience 10")
				.contains("$order : OrderFact(amount > 100)")
				.contains("$order.setHighValue(true);")
				.doesNotContain("rule \"disabled\"");
	}

	@Test
	public void shouldRejectRuleSetWithoutEnabledRules() {
		RuleDefinition rule = RuleDefinition.of("disabled",
				"$order : OrderFact(amount > 1)", "$order.setHighValue(false);");
		rule.setEnabled(false);

		assertThatThrownBy(() -> this.ruleBuilder
				.build(RuleSetDefinition.of("order-risk", "v1").addRule(rule)))
						.isInstanceOf(DroolsRuleBuildException.class)
						.hasMessageContaining("enabled rule");
	}

}
