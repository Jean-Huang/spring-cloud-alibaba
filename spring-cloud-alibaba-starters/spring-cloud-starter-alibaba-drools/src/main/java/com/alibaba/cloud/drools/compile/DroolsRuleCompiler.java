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

package com.alibaba.cloud.drools.compile;

import java.util.List;

import com.alibaba.cloud.drools.model.CompiledRuleSet;
import com.alibaba.cloud.drools.model.RuleSetDefinition;
import org.kie.api.KieBase;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;

/**
 * Compiles generated DRL into an executable Drools {@link KieBase}.
 *
 * @author Cursor
 */
public class DroolsRuleCompiler {

	private final DroolsRuleBuilder ruleBuilder;

	public DroolsRuleCompiler(DroolsRuleBuilder ruleBuilder) {
		this.ruleBuilder = ruleBuilder;
	}

	public CompiledRuleSet compile(RuleSetDefinition ruleSetDefinition) {
		String drl = this.ruleBuilder.build(ruleSetDefinition);
		return compile(ruleSetDefinition, drl);
	}

	public CompiledRuleSet compile(RuleSetDefinition ruleSetDefinition, String drl) {
		try {
			KieHelper kieHelper = new KieHelper();
			kieHelper.addContent(drl, ResourceType.DRL);
			Results results = kieHelper.verify();
			List<Message> errors = results.getMessages(Message.Level.ERROR);
			if (!errors.isEmpty()) {
				throw new DroolsRuleBuildException(buildErrorMessage(errors));
			}
			KieBase kieBase = kieHelper.build();
			return new CompiledRuleSet(ruleSetDefinition.getId(),
					ruleSetDefinition.getVersion(),
					this.ruleBuilder.resolvePackageName(ruleSetDefinition), drl, kieBase);
		}
		catch (DroolsRuleBuildException ex) {
			throw ex;
		}
		catch (Exception ex) {
			throw new DroolsRuleBuildException("Failed to compile Drools rule set", ex);
		}
	}

	private String buildErrorMessage(List<Message> errors) {
		StringBuilder message = new StringBuilder("Drools rule compilation failed:");
		for (Message error : errors) {
			message.append("\n").append(error.getText());
		}
		return message.toString();
	}

}
