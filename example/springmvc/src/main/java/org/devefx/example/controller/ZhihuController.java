package org.devefx.example.controller;

import org.devefx.example.validation.zhihu.ZhihuLoginValidation;
import org.devefx.example.validation.zhihu.ZhihuRegisterValidation;
import org.devefx.validator.Valid;
import org.devefx.validator.internal.util.ThreadContext;
import org.devefx.validator.util.MultiValueMap;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/zhihu")
public class ZhihuController {

	@RequestMapping
	public ModelAndView index() {
		return new ModelAndView("zhihu/index");
	}
	
	/**
	 * 登录处理
	 * @return
	 */
	@Valid(value=ZhihuLoginValidation.class)
	@ResponseBody
	@RequestMapping("/login")
	public String login() {
		// 获取模型对象
		@SuppressWarnings("unchecked")
		MultiValueMap<String, String> valueMap = (MultiValueMap<String, String>) ThreadContext.getModel();
		String account = valueMap.getFirst("account");
		String password =valueMap.getFirst("password");
		System.out.println("登录名：" + account + "\t密码：" + password);
		
		return "success";
	}
	
	/**
	 * 注册处理
	 * @return
	 */
	@Valid(value=ZhihuRegisterValidation.class)
	@ResponseBody
	@RequestMapping("/register")
	public String register() {
		return "success";
	}
}
