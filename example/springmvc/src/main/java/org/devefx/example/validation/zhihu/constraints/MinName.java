package org.devefx.example.validation.zhihu.constraints;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script(remote=true)
public class MinName implements ConstraintValidator {
	
	@InitParam
	private int chineseMin;
	@InitParam
	private int letterMin;
	
	public MinName(int chineseMin, int letterMin) {
		this.chineseMin = chineseMin;
		this.letterMin = letterMin;
	}
	
	@Override
	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		
		int chineseNum = 0;
		int letterNum = 0;
		
		String strValue = value.toString();
		for (char ch : strValue.toCharArray()) {
			if (Character.isIdeographic(ch)) {
				chineseNum++;
			} else if (Character.isLetterOrDigit(ch)) {
				letterNum++;
			}
		}
		return chineseNum >= chineseMin || letterNum >= letterMin;
	}

}
