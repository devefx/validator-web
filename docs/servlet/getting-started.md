# Servlet Configuration

### 基本配置 - 配置 Validator

创建一个ValidatorSetupListener，用来初始化Validator

```java
public class ValidatorSetupListener implements ServletContextListener {
  @Override
  public void contextInitialized(ServletContextEvent sce) {
    // 创建Validator配置
    ValidatorConfig validatorConfig = new ValidatorConfig();
    
    DefaultInvalidHandler invalidHandler = new DefaultInvalidHandler();
    invalidHandler.setOutputStyle(OutputStyle.XML);
    validatorConfig.setInvalidHandler(invalidHandler);
    
    // 验证器工厂
    ValidatorFactory validatorFactory = new ValidatorFactoryImpl(validatorConfig);
    
    // 创建Validator对象，并设置到全局
    Validator validator = validatorFactory.buildValidator();
    ValidatorUtils.setValidator(validator);
  }
  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
```
在**web.xml**中添加下面的配置

`scan-package` 要扫描的路径（根据Validation生成脚本）

```xml
<listener>
  <listener-class>ValidatorSetupListener</listener-class>
</listener>
<servlet>
  <servlet-name>scriptSupportServlet</servlet-name>
  <servlet-class>org.devefx.validator.web.servlet.ScriptSupportServlet</servlet-class>
  <init-param>
    <param-name>scan-package</param-name>
    <param-value>org.devefx.example.validation</param-value>
  </init-param>
  <!-- 其他配置参数，参考 https://github.com/devefx/validator-web/blob/master/docs/springmvc/getting-started.md 中的内容 -->
  <init-param>
    <param-name>debug</param-name>
    <param-value>true</param-value>
  </init-param>
</servlet>
<servlet-mapping>
  <servlet-name>scriptSupportServlet</servlet-name>
  <url-pattern>/va/*</url-pattern>
</servlet-mapping>
```
