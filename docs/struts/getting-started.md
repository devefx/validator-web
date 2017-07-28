# Struts2 Configuration

Struts2拦截器配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE struts PUBLIC  
    "-//Apache Software Foundation//DTD Struts Configuration 2.3//EN"  
    "http://struts.apache.org/dtds/struts-2.3.dtd">  
<struts>
  <include file="struts-default.xml" />

  <package name="all" extends="struts-default">
    <interceptors>
      <interceptor name="validatorInterceptor" class="org.devefx.validator.external.struts2.Struts2ValidatorInterceptor"/>
      <interceptor-stack name="validatorStack">
        <interceptor-ref name="defaultStack"/>
        <interceptor-ref name="validatorInterceptor"/>
      </interceptor-stack>
    </interceptors>
    <default-interceptor-ref name="validatorStack" />
  </package>
  
</struts>
```

其他配置同[Servlet Configuration](https://github.com/devefx/validator-web/blob/master/docs/servlet/getting-started.md)
