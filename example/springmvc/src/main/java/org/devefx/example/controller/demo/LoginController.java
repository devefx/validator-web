package org.devefx.example.controller.demo;

import org.devefx.example.validation.demo.LoginValidation;
import org.devefx.example.validation.demo.LoginValidation.ForgotPassword;
import org.devefx.validator.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/demo/login")
public class LoginController {

	/**
	 * 登录页
	 */
	@RequestMapping()
	public ModelAndView login() {
		return new ModelAndView("demo/login/index");
	}
	
	/**
	 * 使用默认组验证登录
	 */
	@Valid(value=LoginValidation.class)
	@ResponseBody
	@RequestMapping("/do/default")
	public String loginDefault() {
		return "success";
	}
	
	/**
	 * 使用ForgotPassword组进行验证（忽略password）
	 */
	@Valid(value=LoginValidation.class, groups=ForgotPassword.class)
	@ResponseBody
	@RequestMapping("/do/forgotpassword")
	public String LoginForgotPassword() {
		return "success";
	}
}
