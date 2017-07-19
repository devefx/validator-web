<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>Example List</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
  </head>
  <body>
    <div style="padding: 20px 50px;">
	    <h1>Example List</h1>
	    <ul style="font-size: 20px;">
	        <li>
	           <a href="<%=basePath%>zhihu" target="_blank">知乎</a>
	        </li>
	        <li>
               <a href="<%=basePath%>demo/login" target="_blank">Demo Login</a>
            </li>
            <li>
               <a href="<%=basePath%>demo/marketo" target="_blank">Demo Marketo</a>
            </li>
	    </ul>
    </div>
  </body>
</html>
