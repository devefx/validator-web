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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.script.annotation.Script;

@Script
public class Mobile implements ConstraintValidator {

    private static final Pattern mobilePattern = Pattern.compile("^(0|86|17951)?(13[0-9]|15[012356789]|17[3678]|18[0-9]|14[57])[0-9]{8}$");
    
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        String mobile = value.toString();
        Matcher matcher = mobilePattern.matcher(mobile);
        return matcher.matches();
    }
}
