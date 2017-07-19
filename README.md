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

### 文档

- [Servlet Documentation](https://github.com/devefx/validator-web/blob/master/docs/servlet/getting-started.md)
- [SpringMVC Documentation](https://github.com/devefx/validator-web/blob/master/docs/springmvc/getting-started.md)
- [Struts Documentation](https://github.com/devefx/validator-web/blob/master/docs/struts/getting-started.md)

### 示例

https://github.com/devefx/validator-web/tree/master/example

### 协议

https://www.apache.org/licenses/LICENSE-2.0.txt
