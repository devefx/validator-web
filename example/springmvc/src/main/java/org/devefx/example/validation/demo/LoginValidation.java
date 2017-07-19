package org.devefx.example.validation.demo;

import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Email;
import org.devefx.validator.constraints.Length;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.groups.Default;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("demo_login")
public class LoginValidation implements Validation {

	public interface ForgotPassword {}
	
	@Override
	public void initialize(ValidationContext context) {
		context.constraint("email", new NotEmpty(), Default.class, ForgotPassword.class);
		context.constraint("email", new Email(), Default.class, ForgotPassword.class);
		context.constraint("password", new NotEmpty());
		context.constraint("password", new Length(4, 20));
	}
}
