package cn.tofocus.lejia.annotation;

import java.lang.annotation.*;

import javax.validation.Constraint;
import javax.validation.Payload;

import cn.tofocus.lejia.validator.ValidStringInValidator;

@Documented
@Constraint(validatedBy = ValidStringInValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStringIn
{
    String message() default "非法参数值";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
    
    /**
     * 是否可空
     * @return
     */
    boolean nullable() default true;
    
    /**
     * 合法值列表
     * @return
     */
    String[] values();
}
