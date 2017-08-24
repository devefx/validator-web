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

package org.devefx.validator.internal.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;
import org.devefx.validator.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;

public class ConstraintMetaDataImpl implements ConstraintMetaData {

    private static final Log logger = LogFactory.getLog(ConstraintMetaDataImpl.class);

    private final Class<?> constraintClass;

    private final List<Field> initParamAnnotationFields = new ArrayList<>();
    
    private boolean hasScriptAnnotation;
    
    private String scriptId;
    
    private boolean scriptRemote;

    public ConstraintMetaDataImpl(Class<?> constraintClass) {
        this.constraintClass = constraintClass;
        presentInitParamAnnotationFields();
        parserScriptAnnotation();
    }

    private void presentInitParamAnnotationFields() {
        for (Field field : constraintClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(InitParam.class)) {
                initParamAnnotationFields.add(field);
            }
        }
    }
    
    private void parserScriptAnnotation() {
        Script script = this.constraintClass.getAnnotation(Script.class);
        if (script != null) {
            this.hasScriptAnnotation = true;
            this.scriptId = script.id();
            this.scriptRemote = script.remote();
            if (!StringUtils.hasText(this.scriptId)) {
                this.scriptId = this.constraintClass.getSimpleName();
            }
        }
    }

    @Override
    public Class<?> getType() {
        return constraintClass;
    }

    @Override
    public Map<String, Object> getInitParams(ConstraintValidator constraintValidator) {
        if (initParamAnnotationFields.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Object> initParams = new LinkedHashMap<>(initParamAnnotationFields.size());
        for (Field field : initParamAnnotationFields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                initParams.put(field.getName(), field.get(constraintValidator));
            } catch (IllegalAccessException e) {
                if (logger.isInfoEnabled()) {
                    logger.info(e.getMessage(), e);
                }
            }
        }
        return initParams;
    }

    @Override
    public boolean hasScriptAnnotation() {
        return hasScriptAnnotation;
    }

    @Override
    public String getScriptID() {
        return scriptId;
    }

    @Override
    public boolean isScriptRemote() {
        return scriptRemote;
    }
}
