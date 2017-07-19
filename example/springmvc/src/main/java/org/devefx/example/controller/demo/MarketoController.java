package org.devefx.example.controller.demo;

import org.devefx.example.validation.demo.MarketoValidation;
import org.devefx.example.validation.demo.MarketoValidationStep2;
import org.devefx.example.validation.demo.MarketoValidationStep2.SameCompanyAddress;
import org.devefx.validator.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/demo/marketo")
public class MarketoController {

	/**
	 * Step1
	 */
	@RequestMapping()
	public ModelAndView marketo() {
		return new ModelAndView("demo/marketo/index");
	}
	
	/**
	 * Step2
	 */
	@RequestMapping("/step2")
	public ModelAndView step2() {
		return new ModelAndView("demo/marketo/step2");
	}
	
	/**
	 * Step1 使用默认组验证
	 */
	@Valid(value=MarketoValidation.class)
	@ResponseBody
	@RequestMapping("/do/default")
	public String marketoDefault() {
		return "success";
	}
	
	/**
	 * Step2 使用默认组验证
	 */
	@Valid(value=MarketoValidationStep2.class)
	@ResponseBody
	@RequestMapping("/do/step2/default")
	public String marketoStep2Default() {
		return "success";
	}
	
	/**
	 * Step2 使用SameCompanyAddress组验证
	 */
	@Valid(value=MarketoValidationStep2.class, groups=SameCompanyAddress.class)
	@ResponseBody
	@RequestMapping("/do/step2/same")
	public String marketoStep2Same() {
		return "success";
	}
}
