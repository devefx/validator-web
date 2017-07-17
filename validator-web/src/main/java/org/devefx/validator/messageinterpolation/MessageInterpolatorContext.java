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

package org.devefx.validator.messageinterpolation;

import java.util.Collections;
import java.util.Map;

/**
 * Implementation of the context used during message interpolation.
 */
public class MessageInterpolatorContext implements MessageInterpolator.Context {

    private final Map<String, Object> initParams;
    private final Object validatedValue;
    private final Class<?> rootBeanType;
    private final Map<String, Object> messageParameters;

    public MessageInterpolatorContext(Map<String, Object> initParams) {
        this(initParams, null, null, Collections.<String, Object>emptyMap());
    }

    public MessageInterpolatorContext(Map<String, Object> initParams,
                                      Object validatedValue,
                                      Class<?> rootBeanType,
                                      Map<String, Object> messageParameters) {
        this.initParams = initParams;
        this.validatedValue = validatedValue;
        this.rootBeanType = rootBeanType;
        this.messageParameters = messageParameters;
    }

    @Override
    public Map<String, Object> getInitParams() {
        return initParams;
    }

    @Override
    public Object getValidatedValue() {
        return validatedValue;
    }

    @Override
    public Class<?> getRootBeanType() {
        return rootBeanType;
    }

    public Map<String, Object> getMessageParameters() {
        return messageParameters;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        //allow unwrapping into public super types
        if (type.isAssignableFrom(MessageInterpolator.Context.class)) {
            return type.cast(this);
        }
        throw new RuntimeException("Type " + type.getName() + " not supported for unwrapping.");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MessageInterpolatorContext that = (MessageInterpolatorContext) o;

        if (validatedValue != null ? !validatedValue.equals(that.validatedValue) : that.validatedValue != null)
            return false;
        return rootBeanType != null ? rootBeanType.equals(that.rootBeanType) : that.rootBeanType == null;
    }

    @Override
    public int hashCode() {
        int result = validatedValue != null ? validatedValue.hashCode() : 0;
        result = 31 * result + (rootBeanType != null ? rootBeanType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("MessageInterpolatorContext");
        sb.append("{validatedValue=").append(validatedValue);
        sb.append('}');
        return sb.toString();
    }
}
