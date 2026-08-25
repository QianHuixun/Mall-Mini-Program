package cn.tofocus.lejia.util.excel;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TypeValidator implements ConstraintValidator<Type, Object> {
    private Class<?> type;

    @Override
    public void initialize(Type constraintAnnotation) {
        this.type = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return value != null && type.isInstance(value);
    }
}
