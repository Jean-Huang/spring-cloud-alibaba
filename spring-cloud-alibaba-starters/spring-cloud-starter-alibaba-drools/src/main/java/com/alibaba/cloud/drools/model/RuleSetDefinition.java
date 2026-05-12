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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A complete user rule set before it is rendered and compiled by Drools.
 *
 * @author Cursor
 */
public class RuleSetDefinition {

	private String id;

	private String version;

	private String packageName;

	private List<String> imports = new ArrayList<>();

	private Map<String, String> globals = new LinkedHashMap<>();

	private List<RuleDefinition> rules = new ArrayList<>();

	public RuleSetDefinition() {
	}

	public RuleSetDefinition(String id, String version) {
		this.id = id;
		this.version = version;
	}

	public static RuleSetDefinition of(String id, String version) {
		return new RuleSetDefinition(id, version);
	}

	public RuleSetDefinition addImport(String importClassName) {
		this.imports.add(importClassName);
		return this;
	}

	public RuleSetDefinition addGlobal(String name, String type) {
		this.globals.put(name, type);
		return this;
	}

	public RuleSetDefinition addRule(RuleDefinition rule) {
		this.rules.add(rule);
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public List<String> getImports() {
		return imports;
	}

	public void setImports(List<String> imports) {
		this.imports = imports;
	}

	public Map<String, String> getGlobals() {
		return globals;
	}

	public void setGlobals(Map<String, String> globals) {
		this.globals = globals;
	}

	public List<RuleDefinition> getRules() {
		return rules;
	}

	public void setRules(List<RuleDefinition> rules) {
		this.rules = rules;
	}

}
