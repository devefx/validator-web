# validator-web

### 特性

- 前后端验证框架，设计精巧、使用简单
- 支持SpringMVC、Struts2、Servlet
- 自动生成JavaScript前端验证代码
- 统一前后端验证规范
- 支持自定义验证规则
- 支持对自定义数据验证，默认支持：form、json、xml
- 支持分组验证
- 支持BeanValidation扩展（提供HibernateValidation实现）
- 支持国际化消息模板，模板支持EL表达式

### 安装

```cmd
git clone https://github.com/devefx/validator-web.git
cd validator-web
mvn clean install -Dmaven.test.skip
```

如果你使用 Maven,那么在 pom.xml 中加入下面的代码即可:

```xml
<dependency>
    <groupId>org.devefx</groupId>
    <artifactId>validator-web</artifactId>
    <version>1.0.0-alphal</version>
</dependency>
```

### 配置验证器

以下配置均使用默认配置，为了确保代码在使用模型前已经被执行，请将代码放在ServletContextListener.contextInitialized中保证容器启动完成时被调用（推荐使用Spring进行配置）

```java
ValidatorConfig validatorConfig = new ValidatorConfig();
		
ValidatorFactoryImpl validatorFactory = new ValidatorFactoryImpl();
validatorFactory.setValidatorConfig(validatorConfig);

Validator validator = validatorFactory.buildValidator();

ValidatorUtils.setValidator(validator);
```

### 创建验证模型

```java
import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Email;
import org.devefx.validator.constraints.Length;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("login")
public class LoginValidation implements Validation {
    @Override
    public void initialize(ValidationContext context) {
        context.constraint("email", new NotEmpty());
        context.constraint("email", new Email());
        context.constraint("password", new NotEmpty());
        context.constraint("password", new Length(4, 20));
    }
}
```

### 使用验证模型

SpringMVC 示例（需要配置SpringValidatorInterceptor拦截器）

```java
@Controller
public class LoginController {
   @Valid(value=LoginValidation.class)
   @RequestMapping("/login")
   public void login() {
      // ...
   }
}
```

Struts 示例（需要配置Struts2ValidatorInterceptor拦截器）

```java
public class LoginAction extends ActionSupport {
    @Valid(value=LoginValidation.class)
    public void login() {
       // ...
    }
}
```

Servlet示例（Servlet需要继承AbstractValidatorHttpServlet）

```java
@Valid(value=LoginValidation.class)
@WebServlet(name="loginServlet", urlPatterns="/login")
public class LoginServlet extends AbstractValidatorHttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ...
    }
}
```

### 文档

- [Servlet Documentation](https://github.com/devefx/validator-web/blob/master/docs/servlet/getting-started.md)
- [SpringMVC Documentation](https://github.com/devefx/validator-web/blob/master/docs/springmvc/getting-started.md)
- [Struts Documentation](https://github.com/devefx/validator-web/blob/master/docs/struts/getting-started.md)

### 示例

https://github.com/devefx/validator-web/tree/master/example

### 协议

https://www.apache.org/licenses/LICENSE-2.0.txt
