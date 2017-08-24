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

package org.devefx.validator.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.beans.Container;
import org.devefx.validator.beans.DefaultContainer;
import org.devefx.validator.internal.engine.scanner.ClassPathScanner;
import org.devefx.validator.internal.engine.scanner.filter.ValidationScriptMappingTypeFilter;
import org.devefx.validator.util.ClassUtils;
import org.devefx.validator.util.ResourceUtils;
import org.devefx.validator.util.StringUtils;

public class StartupUtil {
    
    private static final Log log = LogFactory.getLog(StartupUtil.class);
    
    /**
     * The default set of entries into the container
     */
    public static final String SYSTEM_DEFAULT_PROPERTIES_PATH = "org/devefx/validator/defaults.properties";
    
    public static final String PROP_PREFIX = "p:";
    
    public static final String CHOOSE_TOKEN = ",";
    
    public static final String SCAN_PACKAGE_INIT_PARAMETER = "scan-package";
    
    public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";

    @SuppressWarnings("unchecked")
    public static void setupDefaultContainer(DefaultContainer container, ServletConfig servletConfig) {
        Properties defaults = new Properties();
        
        try {
            InputStream in = ResourceUtils.getResourceAsStream(SYSTEM_DEFAULT_PROPERTIES_PATH);
            defaults.load(in);
            
            Enumeration<String> en = servletConfig.getInitParameterNames();
            while (en.hasMoreElements()) {
                String name = en.nextElement();
                String value = servletConfig.getInitParameter(name);
                defaults.setProperty(name, value);
            }
            
            for (String name : defaults.stringPropertyNames()) {
                String value = defaults.getProperty(name);
                if (name.startsWith(PROP_PREFIX)) {
                    name = name.substring(PROP_PREFIX.length());
                    container.setProperty(name, value);
                } else {
                    createNewInstance(container, name, value);
                }
            }
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to load system defaults", e);
        }
        
        container.setupFinished();
    }

    private static void createNewInstance(DefaultContainer container, String askFor, String value) {
        if (container.containsBean(askFor)) {
            log.info("An instance with name '" + askFor + "' already exists.  " +
                    "Redefining this object as a new instance of type: " + value);
        }
        
        Class<?> requiredClass = null;
        try {
            requiredClass = ClassUtils.forName(askFor);
        } catch (Exception e) {
            // it's not a required class
        }
        
        if (requiredClass != null) {
            if (!container.getBeansOfType(requiredClass).isEmpty()) {
                log.info("An instance with type '" + askFor + "' already exists.  " +
                        "Redefining this object as a new instance of type: " + value);
            }
        }
        
        for (String className : value.split(CHOOSE_TOKEN)) {
            className = StringUtils.clean(className);
            if (className == null) {
                continue;
            }
            try {
                Object instance = ClassUtils.newInstance(className);
                if (requiredClass == null ||
                        requiredClass.isInstance(instance)) {
                    container.addBean(askFor, instance);
                    return;
                }
            } catch (Throwable e) {
                if (log.isDebugEnabled()) {
                    log.debug("Unable to instantiate class [" + className + "] for object named '" + askFor + "'.  ", e);
                }
            }
        }
    }
    
    public static Container createAndSetupDefaultContainer(ServletConfig servletConfig) {
        Container container;
        
        try {
            String typeName = servletConfig.getInitParameter(Container.class.getName());
            if (typeName == null) {
                container = new DefaultContainer();
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Using alternate Container implementation: " + typeName);
                }
                container = (Container) ClassUtils.newInstance(typeName);
            }
            if (container instanceof DefaultContainer) {
                DefaultContainer defaultContainer = (DefaultContainer) container;
                scanValidationComponents(defaultContainer, servletConfig);
                setupDefaultContainer(defaultContainer, servletConfig);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return container;
    }

    private static void scanValidationComponents(DefaultContainer container, ServletConfig servletConfig) {
        String scanPackage = servletConfig.getInitParameter(SCAN_PACKAGE_INIT_PARAMETER);
        if (scanPackage == null) {
            return;
        }
        
        ClassPathScanner scanner = new ClassPathScanner();
        scanner.addIncludeFilter(new ValidationScriptMappingTypeFilter());
        Set<Class<?>> classes = scanner.scan(scanPackage);
        
        for (Class<?> beanClass : classes) {
            try {
                Object bean = ClassUtils.newInstance(beanClass);
                String beanName = generateBeanName(beanClass, container);
                container.addBean(beanName, bean);
            } catch (Exception e) {
                log.error("Unable to instantiate class [" + beanClass.getName() + "].", e);
            }
        }
    }
    
    public static String generateBeanName(Class<?> beanClass, Container container) {
        String generatedBeanName = beanClass.getName();
        String id = generatedBeanName;
        int counter = -1;
        while (counter == -1 || container.containsBean(id)) {
            counter++;
            id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + counter;
        }
        return id;
    }
}
