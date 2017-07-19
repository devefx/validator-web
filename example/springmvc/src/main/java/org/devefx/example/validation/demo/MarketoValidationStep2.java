package org.devefx.example.validation.demo;

import org.devefx.validator.Validation;
import org.devefx.validator.ValidationContext;
import org.devefx.validator.constraints.Email;
import org.devefx.validator.constraints.Length;
import org.devefx.validator.constraints.Mobile;
import org.devefx.validator.constraints.NotEmpty;
import org.devefx.validator.constraints.Regex;
import org.devefx.validator.groups.Default;
import org.devefx.validator.script.annotation.ScriptMapping;

@ScriptMapping("demo_marketo_step2")
public class MarketoValidationStep2 implements Validation {

	public interface SameCompanyAddress {}
	
	@Override
	public void initialize(ValidationContext context) {
		context.constraint("bill_first_name", new NotEmpty());
		context.constraint("bill_first_name", new Length(0, 40));
		context.constraint("bill_last_name", new NotEmpty());
		context.constraint("bill_last_name", new Length(0, 40));
		context.constraint("email", new NotEmpty());
		context.constraint("email", new Length(0, 40));
		context.constraint("email", new Email());
		context.constraint("bill_address1", new NotEmpty());
		context.constraint("bill_address1", new Length(0, 40));
		context.constraint("bill_address2", new Length(0, 40));
		context.constraint("bill_city", new NotEmpty());
		context.constraint("bill_city", new Length(0, 40));
		context.constraint("bill_state", new NotEmpty());
		context.constraint("bill_zip", new NotEmpty());
		context.constraint("bill_zip", new Regex("[0-9]{6}"));
		context.constraint("bill_phone", new NotEmpty());
		context.constraint("bill_phone", new Mobile());
		
		context.constraint("cc_type", new NotEmpty(), Default.class, SameCompanyAddress.class);
		context.constraint("cc_exp_month", new NotEmpty(), Default.class, SameCompanyAddress.class);
		context.constraint("cc_exp_year", new NotEmpty(), Default.class, SameCompanyAddress.class);
		context.constraint("credit_card", new NotEmpty(), Default.class, SameCompanyAddress.class);
		context.constraint("credit_card", new Length(0, 40), Default.class, SameCompanyAddress.class);
		context.constraint("cc_cvv", new NotEmpty(), Default.class, SameCompanyAddress.class);
		context.constraint("cc_cvv", new Length(0, 4), Default.class, SameCompanyAddress.class);
	}

}
