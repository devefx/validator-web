package org.devefx.example.validation.zhihu.constraints;

import java.nio.charset.Charset;

import org.devefx.validator.ConstraintValidator;
import org.devefx.validator.constraints.annotation.InitParam;
import org.devefx.validator.script.annotation.Script;

@Script(remote=true)
public class MaxName implements ConstraintValidator {
	
	public static final Charset GBK = Charset.forName("GBK");
	
	@InitParam
	private int chineseMax;
	@InitParam
	private int letterMax;
	
	public MaxName(int chineseMax, int letterMax) {
		this.chineseMax = chineseMax;
		this.letterMax = letterMax;
	}
	
	@Override
	public boolean isValid(Object value) {
		if (value == null) {
			return true;
		}
		String strValue = value.toString();
		if (strValue.getBytes(GBK).length > letterMax) {
			return false;
		}
		
		int chineseNum = 0;
		int letterNum = 0;
		for (char ch : strValue.toCharArray()) {
			if (Character.isIdeographic(ch)) {
				chineseNum++;
			} else if (Character.isLetterOrDigit(ch)) {
				letterNum++;
			}
		}
		return chineseNum <= chineseMax && letterNum <= letterMax;
	}

}
