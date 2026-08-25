package cn.tofocus.lejia.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import cn.tofocus.lejia.annotation.ValidStringIn;

public class ValidStringInValidator implements ConstraintValidator<ValidStringIn, String>
{
    private String[] allowedValues;
    
    private boolean nullable;
    
    @Override
    public void initialize(ValidStringIn constraintAnnotation)
    {
        this.allowedValues = constraintAnnotation.values();
        this.nullable = constraintAnnotation.nullable();
    }
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        if (value == null)
        {
            return nullable;
        }
        for (String allowedValue : allowedValues)
        {
            if (allowedValue.equals(value))
            {
                return true;
            }
        }
        return false;
    }
}
