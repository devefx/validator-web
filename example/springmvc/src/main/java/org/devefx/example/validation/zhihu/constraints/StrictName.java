package org.devefx.example.validation.zhihu.constraints;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.script.annotation.Script;

@Script(remote=true)
public class StrictName implements ConstraintValidator {

	@Override
	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		String strValue = value.toString();
		for (char ch : strValue.toCharArray()) {
			if (!Character.isLetterOrDigit(ch) && !Character.isIdeographic(ch)) {
				return false;
			}
		}
		return true;
	}
}
