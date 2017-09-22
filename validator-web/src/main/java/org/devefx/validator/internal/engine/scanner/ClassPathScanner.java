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

package org.devefx.validator.internal.engine.scanner;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devefx.validator.util.Assert;
import org.devefx.validator.util.ClassUtils;

public class ClassPathScanner {
    
    private static final Log logger = LogFactory.getLog(ClassPathScanner.class);
    
    private static final String CLASS_SUFFIX = ".class";
    
    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();
    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();
    private ClassLoader classLoader;
    
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    
    public void addIncludeFilter(TypeFilter includeFilter) {
        this.includeFilters.add(includeFilter);
    }
    
    public void addExcludeFilter(TypeFilter excludeFilter) {
        this.excludeFilters.add(0, excludeFilter);
    }
    
    public void resetFilters() {
        this.includeFilters.clear();
        this.excludeFilters.clear();
    }
    
    public Set<Class<?>> scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
        for (String basePackage : basePackages) {
            Set<Class<?>> candidates = findCandidateClasses(basePackage);
            for (Class<?> candidate : candidates) {
                if (isCandidate(candidate)) {
                    classes.add(candidate);
                }
            }
        }
        return classes;
    }
    
    public Set<Class<?>> findCandidateClasses(String basePackage) {
        Set<Class<?>> candidates = new LinkedHashSet<Class<?>>();
        try {
            String packageSearchPath = basePackage.replace('.', '/');
            ClassLoader classLoader = this.classLoader;
            if (classLoader == null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            }
            Enumeration<URL> resources = classLoader.getResources(packageSearchPath);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (logger.isTraceEnabled()) {
                    logger.trace("Scanning " + resource);
                }
                if ("file".equals(resource.getProtocol())) {
                    findClasses(candidates, classLoader, basePackage, resource.getFile());
                } else if ("jar".equals(resource.getProtocol())) {
                    findClasses(candidates, classLoader, packageSearchPath, (JarURLConnection) resource.openConnection());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O failure during classpath scanning", e);
        }
        return candidates;
    }
    
    protected void findClasses(Set<Class<?>> classes, ClassLoader classLoader,
            String packageName, String packagePath) {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles(new DefaultFileFilter())) {
            if (file.isDirectory()) {
                findClasses(classes, classLoader, packageName + "." + file.getName(), file.getPath());
            } else {
                String className = packageName.concat(".") + file.getName().replace(CLASS_SUFFIX, "");
                try {
                    Class<?> c = ClassUtils.forName(className, classLoader);
                    classes.add(c);
                } catch (ClassNotFoundException e) {
                    // ignore
                }
            }
        }
    }

    protected void findClasses(Set<Class<?>> classes, ClassLoader classLoader,
            String packageName, JarURLConnection connection) throws IOException {
        JarFile jarFile = connection.getJarFile();
        if (jarFile == null) {
            return;
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(CLASS_SUFFIX) && name.startsWith(packageName)) {
                String className = name.replace('/', '.').replace(CLASS_SUFFIX, "");
                try {
                    Class<?> c = ClassUtils.forName(className, classLoader);
                    classes.add(c);
                } catch (ClassNotFoundException e) {
                    // ignore
                    e.printStackTrace();
                }
            }
        }
    }
    
    protected boolean isCandidate(Class<?> candidate) throws IllegalStateException {
        for (TypeFilter filter : excludeFilters) {
            if (filter.match(candidate)) {
                return false;
            }
        }
        for (TypeFilter filter : includeFilters) {
            if (filter.match(candidate)) {
                return true;
            }
        }
        return false;
    }
    
    class DefaultFileFilter implements FileFilter {
        @Override
        public boolean accept(File file) {
            return file.isDirectory() || (file.isFile() && file.getName().endsWith(CLASS_SUFFIX));
        }
    }
}
