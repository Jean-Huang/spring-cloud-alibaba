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
import java.util.Map;

import com.alibaba.cloud.drools.model.RuleDefinition;
import com.alibaba.cloud.drools.model.RuleSetDefinition;

/**
 * Converts user rule definitions into Drools DRL text.
 *
 * @author Cursor
 */
public class DroolsRuleBuilder {

	private final String defaultPackageName;

	public DroolsRuleBuilder(String defaultPackageName) {
		this.defaultPackageName = defaultPackageName;
	}

	public String build(RuleSetDefinition ruleSetDefinition) {
		validateRuleSet(ruleSetDefinition);
		String packageName = resolvePackageName(ruleSetDefinition);
		StringBuilder drl = new StringBuilder();
		drl.append("package ").append(packageName).append("\n\n");
		appendImports(drl, ruleSetDefinition.getImports());
		appendGlobals(drl, ruleSetDefinition.getGlobals());
		appendRules(drl, ruleSetDefinition.getRules());
		return drl.toString();
	}

	String resolvePackageName(RuleSetDefinition ruleSetDefinition) {
		if (hasText(ruleSetDefinition.getPackageName())) {
			return ruleSetDefinition.getPackageName();
		}
		return this.defaultPackageName;
	}

	private void appendImports(StringBuilder drl, List<String> imports) {
		if (imports == null || imports.isEmpty()) {
			return;
		}
		for (String importClassName : imports) {
			if (hasText(importClassName)) {
				drl.append("import ").append(importClassName).append(";\n");
			}
		}
		drl.append("\n");
	}

	private void appendGlobals(StringBuilder drl, Map<String, String> globals) {
		if (globals == null || globals.isEmpty()) {
			return;
		}
		for (Map.Entry<String, String> entry : globals.entrySet()) {
			if (hasText(entry.getKey()) && hasText(entry.getValue())) {
				drl.append("global ").append(entry.getValue()).append(" ")
						.append(entry.getKey()).append(";\n");
			}
		}
		drl.append("\n");
	}

	private void appendRules(StringBuilder drl, List<RuleDefinition> rules) {
		for (RuleDefinition rule : rules) {
			if (!rule.isEnabled()) {
				continue;
			}
			appendRule(drl, rule);
		}
	}

	private void appendRule(StringBuilder drl, RuleDefinition rule) {
		drl.append("rule \"").append(escapeRuleName(rule.getName())).append("\"\n");
		if (rule.getSalience() != null) {
			drl.append("\tsalience ").append(rule.getSalience()).append("\n");
		}
		drl.append("when\n");
		appendBlock(drl, rule.getCondition());
		drl.append("then\n");
		appendBlock(drl, rule.getAction());
		drl.append("end\n\n");
	}

	private void appendBlock(StringBuilder drl, String block) {
		String[] lines = block.split("\\r?\\n");
		for (String line : lines) {
			drl.append("\t").append(line).append("\n");
		}
	}

	private void validateRuleSet(RuleSetDefinition ruleSetDefinition) {
		if (ruleSetDefinition == null) {
			throw new DroolsRuleBuildException("Rule set definition must not be null");
		}
		if (!hasText(ruleSetDefinition.getId())) {
			throw new DroolsRuleBuildException("Rule set id must not be empty");
		}
		if (!hasText(ruleSetDefinition.getVersion())) {
			throw new DroolsRuleBuildException("Rule set version must not be empty");
		}
		if (!hasText(resolvePackageName(ruleSetDefinition))) {
			throw new DroolsRuleBuildException("Rule set package name must not be empty");
		}
		if (ruleSetDefinition.getRules() == null
				|| ruleSetDefinition.getRules().isEmpty()) {
			throw new DroolsRuleBuildException("Rule set must contain at least one rule");
		}
		validateRules(ruleSetDefinition.getRules());
	}

	private void validateRules(List<RuleDefinition> rules) {
		boolean hasEnabledRule = false;
		for (RuleDefinition rule : rules) {
			if (rule == null || !rule.isEnabled()) {
				continue;
			}
			hasEnabledRule = true;
			if (!hasText(rule.getName())) {
				throw new DroolsRuleBuildException("Rule name must not be empty");
			}
			if (!hasText(rule.getCondition())) {
				throw new DroolsRuleBuildException(
						"Rule condition must not be empty: " + rule.getName());
			}
			if (!hasText(rule.getAction())) {
				throw new DroolsRuleBuildException(
						"Rule action must not be empty: " + rule.getName());
			}
		}
		if (!hasEnabledRule) {
			throw new DroolsRuleBuildException(
					"Rule set must contain at least one enabled rule");
		}
	}

	private String escapeRuleName(String ruleName) {
		return ruleName.replace("\\", "\\\\").replace("\"", "\\\"");
	}

	private boolean hasText(String value) {
		return value != null && value.trim().length() > 0;
	}

}
