package org.devefx.example.validation.demo;

import java.util.regex.Pattern;

import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Email;
import org.devefx.validator.constraints.EqualTo;
import org.devefx.validator.constraints.Length;
import org.devefx.validator.constraints.Mobile;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.constraints.Regex;
import org.devefx.validator.constraints.URL;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("demo_marketo")
public class MarketoValidation implements Validation {

	@Override
	public void initialize(ValidationContext context) {
		context.constraint("co_name", new NotEmpty());
		context.constraint("co_name", new Length(1, 40));
		context.constraint("co_url", new NotEmpty());
		context.constraint("co_url", new Length(1, 40));
		context.constraint("co_url", new URL());
		context.constraint("first_name", new NotEmpty());
		context.constraint("first_name", new Length(1, 40));
		context.constraint("last_name", new NotEmpty());
		context.constraint("last_name", new Length(1, 40));
		context.constraint("address1", new NotEmpty());
		context.constraint("city", new NotEmpty());
		context.constraint("state", new NotEmpty());
		context.constraint("zip", new NotEmpty());
		context.constraint("zip", new Regex("[0-9]{6}"));
		context.constraint("phone", new NotEmpty());
		context.constraint("phone", new Mobile());
		context.constraint("email", new NotEmpty());
		context.constraint("email", new Length(1, 40));
		context.constraint("email", new Email());
		context.constraint("password1", new NotEmpty());
		context.constraint("password1", new Length(1, 40));
		context.constraint("password1", new Regex("[a-z0-9]{6,}", Pattern.CASE_INSENSITIVE));
		context.constraint("password2", new NotEmpty());
		context.constraint("password2", new Length(1, 40));
		context.constraint("password2", new EqualTo("password1"));
	}

}
