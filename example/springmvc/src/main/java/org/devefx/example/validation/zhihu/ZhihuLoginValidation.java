package org.devefx.example.validation.zhihu;

import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Email;
import org.devefx.validator.constraints.Mobile;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.constraints.Options;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("zhihu_login")
public class ZhihuLoginValidation implements Validation {

	@Override
	public void initialize(ValidationContext context) {
		context.constraint("account", "请填写手机号或邮箱", new NotEmpty());
		context.constraint("account", "请填写正确的手机号或邮箱", new Options(new Mobile(), new Email()));
		context.constraint("password", "请填写密码", new NotEmpty());
	}
}
