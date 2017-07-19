<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html lang="en">
	<head>
		<meta charset="utf-8">
		<title>Login Form with Email Password Link</title>
		<link rel="stylesheet" media="screen" href="<%=basePath%>/resources/demo/login/screen.css">
		<!-- 依赖库 -->
	    <script type="text/javascript" src="<%=basePath%>va/lib/jquery.js"></script>
	    <script type="text/javascript" src="<%=basePath%>va/lib/jquery.form.js"></script>
	    <!-- 核心库 -->
	    <script type="text/javascript" src="<%=basePath%>va/validator.js"></script>
	    <!-- 验证器 -->
	    <script type="text/javascript" src="<%=basePath%>va/validation-js/demo_login.js"></script>
		<script>
			$(function() {
				// highlight
				var elements = $("input[type!='submit'], textarea, select");
				elements.focus(function() {
					$(this).parents('li').addClass('highlight');
				});
				elements.blur(function() {
					$(this).parents('li').removeClass('highlight');
				});
				var valid = $("#login").validate();
				$("#forgotpassword").click(function() {
				    // 设置验证组
				    valid.settings.groups = ["ForgotPassword"];
				    // 修改请求地址并提交
				    var form = $("#login");
				    form.attr("action", "<%=basePath%>demo/login/do/forgotpassword");
					form.submit();
					// 还原请求地址和验证组
					form.attr("action", "<%=basePath%>demo/login/do/default");
					valid.settings.groups = ["Default"];
					return false;
				});
			});
		</script>
	</head>
	<body>
		<div id="page">
			<div id="header">
				<h1>Login</h1>
			</div>
			<div id="content">
				<p id="status"></p>
				<form action="<%=basePath%>demo/login/do/default" method="post" id="login">
					<fieldset>
						<legend>User details</legend>
						<ul>
							<li>
								<label for="email">
									<span class="required">Email address</span>
								</label>
								<input id="email" name="email" class="text" type="text">
								<label for="email" class="error">This must be a valid email address</label>
							</li>
							<li>
								<label for="password">
									<span class="required">Password</span>
								</label>
								<input name="password" type="password" class="text" id="password">
							</li>
							<li>
								<label class="centered info"><a id="forgotpassword" href="#">Email my password...</a></label>
							</li>
						</ul>
					</fieldset>
					<fieldset class="submit">
						<input type="submit" class="button" value="Login...">
					</fieldset>
					<div class="clear"></div>
				</form>
			</div>
		</div>
	</body>
</html>
