package org.devefx.example.validation.zhihu;

import org.devefx.example.validation.zhihu.constraints.MaxName;
import org.devefx.example.validation.zhihu.constraints.MinName;
import org.devefx.example.validation.zhihu.constraints.StrictName;
import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Length;
import org.devefx.validator.constraints.Mobile;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("zhihu_register")
public class ZhihuRegisterValidation implements Validation {
	
	@Override
	public void initialize(ValidationContext context) {
		context.constraint("fullname", "请填写姓名", new NotEmpty());
		context.constraint("fullname", "姓名中不能含有特殊字符", new StrictName());
		context.constraint("fullname", "姓名最短为 2 个汉字或 3 个英文字符", new MinName(2, 3));
		context.constraint("fullname", "姓名最长为 10 个汉字或 20 个英文字符", new MaxName(10, 20));
		context.constraint("phone_num", "请填写手机号", new NotEmpty());
		context.constraint("phone_num", "请输入正确的手机号", new Mobile());
		context.constraint("password", "请填写密码", new NotEmpty());
		context.constraint("password", "请输入 6-128 位的密码", new Length(6, 128));
	}
}
