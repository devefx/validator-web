# Spring MVC Configuration

### 基本配置 - 创建 Validator

```xml
<!-- 创建Validator配置 -->
<bean id="validatorConfig" class="org.devefx.validator.ValidatorConfig">
    <property name="invalidHandler">
        <bean class="org.devefx.validator.internal.engine.DefaultInvalidHandler">
            <!-- 输出风格 -->
            <property name="outputStyle" value="XML"/>
        </bean>
    </property>
</bean>

<!-- 验证器工厂 -->
<bean id="validatorFactory" class="org.devefx.validator.internal.engine.ValidatorFactoryImpl" init-method="init">
    <!-- Validator配置 -->
    <property name="validatorConfig" ref="validatorConfig"/>
    <!-- Validation对象Spring容器获取 -->
    <property name="validationFactory">
        <bean class="org.devefx.validator.external.spring.SpringValidationFactory"/>
    </property>
</bean>

<!-- 验证器 -->
<bean id="validator" factory-bean="validatorFactory" factory-method="buildValidator"/>

<!-- 设置为全局，相当于调用ValidatorUtils.setValidator(validator) -->
<bean class="org.springframework.beans.factory.config.MethodInvokingFactoryBean">
    <property name="staticMethod" value="org.devefx.validator.ValidatorUtils.setValidator"/>
    <property name="arguments" ref="validator"/>
</bean>
```

### 基本配置 - 拦截器

配置了拦截器才能使Controller中的`@Valid`注解生效

```xml
<mvc:interceptors>
    <bean class="org.devefx.validator.external.spring.mvc.SpringValidatorInterceptor"/>
</mvc:interceptors>
```

### 可选配置 - 前端验证器功能支持

**Spring 配置**

`base-package` 要扫描的路径（根据Validation生成脚本）

```xml
<!-- 扫描验证组件 -->
<context:component-scan base-package="org.devefx.example.validation">
    <!-- 组件过滤器，只扫描使用@ScriptMapping注解的Validation类 -->
    <context:include-filter type="custom" expression="org.devefx.validator.spring.filter.ValidationScriptMappingTypeFilter"/>
</context:component-scan>

<!-- 简化配置，自动注册ValidatorController，ValidatorHandlerMappin -->
<bean class="org.devefx.validator.spring.ConfigPostProcessor"/>
```
**web.xml配置**

```xml
<servlet>
  <servlet-name>dispatcher</servlet-name>
  <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  <init-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring-web.xml</param-value>
  </init-param>
  <load-on-startup>1</load-on-startup>
</servlet>
<servlet-mapping>
  <servlet-name>dispatcher</servlet-name>
  <url-pattern>/</url-pattern>
</servlet-mapping>
<!-- script url mapping -->
<servlet-mapping>
  <servlet-name>dispatcher</servlet-name>
  <url-pattern>/va/*</url-pattern>
</servlet-mapping>
```

### 可选配置 - 修改前端验证器默认配置

默认配文件：org.devefx.validator.defaults.properties

```xml
<bean class="org.devefx.validator.spring.InitPropertyPlaceholderConfigurer">
  <property name="location" value="classpath:userconfig.properties"/>
</bean>
```
**userconfig.properties**
```prop
# 生成器的二级路径（不推荐修改）
GeneratedValidationJavaScriptHandler.path=/validation-js/
# 远程验证的访问后缀（不推荐修改）
GeneratedValidationJavaScriptHandler.suffix=.js
# 远程验证的二级路径（不推荐修改）
RemoteValidateHandler.path=/remote-validate/
# 远程验证的访问后缀（不推荐修改）
RemoteValidateHandler.suffix=.do
# Validator.js的映射路径
ValidatorFileJavaScriptHandler.path=/validator.js
# Jquery.js的映射路径
JqueryFileJavaScriptHandler.path=/lib/jquery.js
# Jquery-Form.js的映射路径
JqueryFormFileJavaScriptHandler.path=/lib/jquery.js
# 资源缓存时间（单位秒）
periodCacheableTime=604800
# 调试模式（开启后不会对js进行压缩，方便前端调试）
debug=false
```







