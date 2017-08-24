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
import java.util.regex.PatternSyntaxException;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script
public class Regex implements ConstraintValidator {

    private java.util.regex.Pattern pattern;
    @InitParam
    private String regexp;
    @InitParam
    private int flags;

    public Regex(String regexp) {
        this(regexp, 0);
    }
    
    public Regex(String regexp, int flags) {
        try {
            this.regexp = regexp;
            this.flags = flags;
            this.pattern = Pattern.compile(this.regexp, this.flags);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regular expression.");
        }
    }
    
    @Override
    public boolean isValid(Object value) {
        if (value == null) {
            return true;
        }
        Matcher m = pattern.matcher(value.toString());
        return m.matches();
    }
    
}
