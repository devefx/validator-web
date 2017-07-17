package org.devefx.validator.constraints;

import javax.servlet.http.HttpServletRequest;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.WebContext;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.internal.util.WebContextThreadStack;

public class EqualTo implements ConstraintValidator {
	
	@InitParam
	private String name;
	@InitParam
	private boolean ignoreCase;
	
	public EqualTo(String name) {
		this(name, false);
	}
	
	public EqualTo(String name, boolean ignoreCase) {
		this.name = name;
	}

	@Override
	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		
		WebContext context = WebContextThreadStack.get();
		HttpServletRequest request = context.getHttpServletRequest();
		
		String diffValue = request.getParameter(name);
		if (diffValue == null) {
			return false;
		}
		return ignoreCase ? diffValue.equalsIgnoreCase(value.toString())
				: diffValue.equals(value);
	}

}
