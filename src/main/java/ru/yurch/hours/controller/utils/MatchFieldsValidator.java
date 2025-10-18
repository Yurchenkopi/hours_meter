package ru.yurch.hours.controller.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class MatchFieldsValidator implements ConstraintValidator<MatchFields, Object> {
    private String field;
    private String fieldMatch;
    @Override
    public void initialize(MatchFields constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        Object fieldValue = beanWrapper.getPropertyValue(field);
        Object fieldMatchValue = beanWrapper.getPropertyValue(fieldMatch);
        boolean valid = (fieldValue != null && fieldValue.equals(fieldMatchValue));
        if (!valid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addPropertyNode(fieldMatch)
                    .addConstraintViolation();
        }
        return valid;
    }
}
