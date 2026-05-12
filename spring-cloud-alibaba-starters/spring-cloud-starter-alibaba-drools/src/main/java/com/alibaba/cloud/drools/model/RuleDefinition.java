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

package com.alibaba.cloud.drools.model;

/**
 * User-defined rule fragment that can be rendered into DRL.
 *
 * @author Cursor
 */
public class RuleDefinition {

	private String name;

	private String description;

	private Integer salience;

	private String condition;

	private String action;

	private boolean enabled = true;

	public RuleDefinition() {
	}

	public RuleDefinition(String name, String condition, String action) {
		this.name = name;
		this.condition = condition;
		this.action = action;
	}

	public static RuleDefinition of(String name, String condition, String action) {
		return new RuleDefinition(name, condition, action);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getSalience() {
		return salience;
	}

	public void setSalience(Integer salience) {
		this.salience = salience;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
