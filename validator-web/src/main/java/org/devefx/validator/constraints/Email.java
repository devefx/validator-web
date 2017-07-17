/*
 * Copyright 2016-2017, Youqian Yue (devefx@163.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.devefx.validator.constraints;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.script.annotation.Script;

@Script
public class Email implements ConstraintValidator {
	
	private static final String emailPattern = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";
	
	@Override
	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		
		// cannot split email string at @ as it can be a part of quoted local part of email.
		// so we need to split at a position of last @ present in the string:
		String stringValue = value.toString();
		if (stringValue.length() == 0) {
			return true;
		}
		int splitPosition = stringValue.lastIndexOf("@");
		
		// need to check if
		if (splitPosition < 0) {
			return false;
		}
		
		String name = stringValue.substring(0, splitPosition);
		String domain = stringValue.substring(splitPosition + 1);
		
		boolean valid = false;
		if ("163.com".equals(domain) || "126.com".equals(domain) || "yeah.net".equals(domain)) {
			valid = newRegExp("^[a-z][a-z0-9_]{5,17}$", Pattern.CASE_INSENSITIVE).test(name);
		} else if ("qq.com".equals(domain) || "foxmail.com".equals(domain)) {
			valid = ("qq.com".equals(domain) && newRegExp("^[1-9][0-9]{4,10}$").test(name));
			valid = valid || (newRegExp("^[a-z][a-z0-9._-]{2,17}$", Pattern.CASE_INSENSITIVE).test(name) && 
	    			!newRegExp("([._-]){2,}").test(name));
		} else if ("sina.com".equals(domain) || "sina.cn".equals(domain)) {
			valid = newRegExp("^[a-z0-9][a-z0-9_]{2,14}[a-z0-9]$").test(name);
		} else if ("sohu.com".equals(domain)) {
			valid = newRegExp("^[a-z][a-zA-Z0-9_]{3,15}$").test(name);
		} else if ("gmail.com".equals(domain)) {
			valid = (newRegExp("^[a-z0-9][a-z0-9.]{4,28}[a-z0-9]$", Pattern.CASE_INSENSITIVE).test(name)
					&& !newRegExp("\\.{2,}").test(name) && 
					(name.length() < 8 || newRegExp("[a-z]").test(name)));
		} else if ("outlook.com".equals(domain) || "hotmail.com".equals(domain)) {
			valid = (newRegExp("^[a-z][a-z0-9._-]{0,63}$", Pattern.CASE_INSENSITIVE).test(name)
					&& !newRegExp("\\.{2,}").test(name));
		} else if ("yahoo.com".equals(domain) || "yahoo.com.cn".equals(domain) || "yahoo.cn".equals(domain)) {
			valid = (newRegExp("^[a-z][a-z0-9._]{2,30}[a-z0-9]$", Pattern.CASE_INSENSITIVE).test(name)
					&& !newRegExp("_{2,}").test(name) && newRegExp("\\.").count(name) < 2);
		} else {
			valid = Pattern.matches(emailPattern, stringValue);
		}
		return valid;
	}
	
	private static Map<String, RegExp> regExpMap = new HashMap<>();
	
	private RegExp newRegExp(String regex) {
		return newRegExp(regex, 0);
	}
	
	private RegExp newRegExp(String regex, int flags) {
		RegExp regExp = regExpMap.get(regex + "/" + flags);
		if (regExp == null) {
			regExp = new RegExp(regex, flags);
			regExpMap.put(regex + "/" + flags, regExp);
		}
		return regExp;
	}
	
	static class RegExp {
		private Pattern pattern;
		public RegExp(String regex, int flags) {
			this.pattern = Pattern.compile(regex, flags);
		}
		public boolean test(String value) {
			return pattern.matcher(value).find();
		}
		public int count(String value) {
			int count = 0;
			Matcher matcher = pattern.matcher(value);
			while (matcher.find()) {
				count++;
			}
			return count;
		}
	}
}
