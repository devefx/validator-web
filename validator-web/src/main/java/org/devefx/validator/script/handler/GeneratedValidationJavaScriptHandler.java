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

package org.devefx.validator.script.handler;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.devefx.validator.ConstraintDescriptor;
import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.ValidatorContext;
import org.devefx.validator.ValidatorFactory;
import org.devefx.validator.beans.factory.annotation.Inject;
import org.devefx.validator.beans.factory.annotation.Value;
import org.devefx.validator.internal.metadata.ConstraintMetaData;
import org.devefx.validator.internal.metadata.ConstraintMetaDataManager;
import org.devefx.validator.messageinterpolation.MessageInterpolator;
import org.devefx.validator.script.mapping.Mapping;
import org.devefx.validator.util.LocaleUtils;
import org.devefx.validator.util.StringUtils;

public class GeneratedValidationJavaScriptHandler extends BaseValidationHandler {

    private static final String CONTENT_TYPE = "application/javascript; charset=utf-8";
    
    private static final String REMOTE_MAPPING = "Remote";
    
    private static final String VALIDATOR_NAMESPACE = "va";
    
    private RemoteValidateHandler remoteHandler;
    
    public GeneratedValidationJavaScriptHandler() {
        setContentType(CONTENT_TYPE);
    }
    
    @Value("${GeneratedValidationJavaScriptHandler.path}")
    @Override
    public void setPath(String path) {
        super.setPath(path);
    }
    
    @Value("${GeneratedValidationJavaScriptHandler.suffix}")
    @Override
    public void setSuffix(String suffix) {
        super.setSuffix(suffix);
    }
    
    @Override
    protected Locale getLocale(HttpServletRequest request) {
        String locale = request.getParameter("locale");
        if (StringUtils.hasText(locale)) {
            try {
                return LocaleUtils.toLocale(locale);
            } catch (IllegalArgumentException e) { }
        }
        return request.getLocale();
    }
    
    @Inject
    public void setRemoteHandler(RemoteValidateHandler remoteHandler) {
        this.remoteHandler = remoteHandler;
    }
    
    @Override
    protected String generateValidationScript(String contextPath,
            String servletPath, String scriptName, Locale locale) {
        
        Mapping mapping = mappingManager.getMapping(scriptName);
        if (mapping == null) {
            return null;
        }
        
        ValidationContext.Accessor context = mapping.getValidationContext();
        
        Vars vars = new Vars();
        vars.validationContext = context;
        vars.validatorContext = context.getValidatorContext();
        vars.validatorFactory = vars.validatorContext.getValidatorFactory();
        vars.messageInterpolator = vars.validatorFactory.getMessageInterpolator();
        vars.cmdManager = vars.validatorContext.getConstraintMetaDataManager();
        vars.servletPath = servletPath;
        vars.scriptName = scriptName;
        
        StringBuilder buffer = new StringBuilder();
        
        buffer
            .append("(function(" + VALIDATOR_NAMESPACE + ") {\n")
            .append("  $.extend($.validator.validations, {\n")
            .append("    '" + scriptName + "': function (context) {\n")
            .append("      context.setFailFast(" + context.isFailFast() + ");\n")
            .append("      context.setThrowException(" + context.isThrowException() + ");\n")
            .append(this.generateConstraintDescriptor(vars, context, locale))
            .append("    }\n")
            .append("  });\n")
            .append("}($.validator.constraints));");
        
        return buffer.toString();
    }

    protected String generateConstraintDescriptor(Vars vars, ValidationContext.Accessor context, Locale locale) {
        StringBuilder buffer = new StringBuilder();
        
        for (ConstraintDescriptor descriptor : context.getConstraintDescriptors()) {
            
            String name = descriptor.getName();
            
            String message = vars.messageInterpolator.interpolateBundleMessage(
                    descriptor.getMessageTemplate(),
                    context.getResourceBundleLocator(),
                    locale
                );
            
            if (message != null) {
                message = message.replaceAll("\"", "\\\\\"");
            }
            
            ConstraintValidator validator = descriptor.getConstraintValidator();
            
            Class<?> type = validator.getClass();
            
            ConstraintMetaData metadata = vars.cmdManager.getConstraintMetaData(type);
            
            if (metadata.hasScriptAnnotation()) {
                buffer
                    .append("      context.constraint(\"" + name + "\", \"" + message + "\", ")
                    .append(this.generateConstraintValidator(vars, validator))
                    .append(this.generateGroups(descriptor.getGroups()))
                    .append(");\n");
            }
        }
        return buffer.toString();
    }
    
    protected String generateConstraintValidator(Vars vars, ConstraintValidator validator) {
        Class<?> type = validator.getClass();
        
        ConstraintMetaData metadata = vars.cmdManager.getConstraintMetaData(type);
        
        StringBuilder buffer = new StringBuilder();
        
        String scirptName = metadata.getScriptID();
        if (metadata.isScriptRemote()) {
            scirptName = REMOTE_MAPPING;
        } else if (!StringUtils.hasText(scirptName)) {
            scirptName = type.getSimpleName();
        }
        
        buffer
            .append("new ")
            .append(VALIDATOR_NAMESPACE)
            .append('.')
            .append(scirptName)
            .append("(")
            .append(this.generateConstraintValidatorParams(vars, validator, metadata))
            .append(")");
        
        return buffer.toString();
    }
    
    private String generateConstraintValidatorParams(Vars vars,
            ConstraintValidator validator, ConstraintMetaData metadata) {
        
        Map<String, Object> initParams = metadata.getInitParams(validator);
        
        StringBuilder buffer = new StringBuilder();
        
        if (metadata.isScriptRemote()) {
            buffer
                .append("\"" + vars.servletPath)
                .append(this.remoteHandler.getPath())
                .append(vars.scriptName)
                .append(this.remoteHandler.getSuffix())
                .append("?id=")
                .append(Integer.toHexString(validator.hashCode()))
                .append("\", ")
                .append(this.generateMap(vars, initParams));
        } else {
            boolean first = true;
            for (Entry<String, Object> entry : initParams.entrySet()) {
                if(first)
                    first = false;
                else
                    buffer.append(", ");
                
                buffer.append(this.generateValue(vars, entry.getValue()));
            }
        }
        return buffer.toString();
    }
    
    protected String generateGroups(Collection<Class<?>> groups) {
        StringBuilder buffer = new StringBuilder();
        
        if (groups != ValidationContext.DEFAULT_GROUPS) {
            for (Class<?> group : groups) {
                buffer.append(", \"" + group.getSimpleName() + "\"");
            }
        }
        return buffer.toString();
    }
    
    @SuppressWarnings("rawtypes")
    public String generateValue(Vars vars, Object value) {
        if (value == null)
            return "null";
        
        if (value instanceof String)
            return "\"" + value + "\"";
        
        if (value instanceof Character)
            return "\'" + value + "\'";
        
        if (value instanceof Double) {
            if(((Double)value).isInfinite() || ((Double)value).isNaN())
                return "NaN";
            else
                return value.toString();
        }
        
        if (value instanceof Float) {
            if(((Float)value).isInfinite() || ((Float)value).isNaN())
                return "NaN";
            else
                return value.toString();
        }
        
        if (value instanceof Number)
            return value.toString();
        
        if (value instanceof Boolean)
            return value.toString();
        
        if (value instanceof Date) {
            return String.format("new Date(%d)", new Date().getTime());
        }
        
        if (value instanceof Map) {
            return this.generateMap(vars, (Map)value);
        }
        
        if (value instanceof List) {
            return this.generateList(vars, (List)value);
        }
        
        if (value instanceof Object[]) {
            List list = Arrays.asList((Object[])value);
            return this.generateList(vars, list);
        }
        
        if (value instanceof ConstraintValidator) {
            return this.generateConstraintValidator(vars, (ConstraintValidator)value);
        }
        return "\"" + value + "\"";
    }
    
    @SuppressWarnings("rawtypes")
    private String generateMap(Vars vars, Map map) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('{');
        
        boolean first = true;
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            if(first)
                first = false;
            else
                buffer.append(", ");
            
            Map.Entry entry = (Map.Entry)iter.next();
            buffer
                .append("\"" + entry.getKey() + "\":")
                .append(this.generateValue(vars, entry.getValue()));
        }
        
        buffer.append('}');
        return buffer.toString();
    }

    @SuppressWarnings("rawtypes")
    private String generateList(Vars vars, List list) {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        
        boolean first = true;
        for (Object value : list) {
            if(first)
                first = false;
            else
                buffer.append(", ");
            
            buffer.append(generateValue(vars, value));
        }
        
        buffer.append(']');
        return buffer.toString();
    }

    @Override
    protected long getLastModifiedTime() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long startTime = runtimeMXBean.getStartTime();
        // Container start time (browsers are only accurate to the second)
        return startTime - (startTime % 1000);
    }
    
    class Vars {
        ValidationContext.Accessor validationContext;
        ValidatorContext validatorContext;
        ValidatorFactory validatorFactory;
        MessageInterpolator messageInterpolator;
        ConstraintMetaDataManager cmdManager;
        String servletPath;
        String scriptName;
    }
}
