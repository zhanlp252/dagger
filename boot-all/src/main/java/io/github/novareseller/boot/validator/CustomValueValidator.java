package io.github.novareseller.boot.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * @author: Bowen huang
 * @date: 2020/08/09
 */
public class CustomValueValidator implements ConstraintValidator<CustomValue, Object> {

    private String[] strValues;

    private int[] intValues;

    @Override
    public void initialize(CustomValue customValue) {
        this.strValues = customValue.strValues();
        this.intValues = customValue.intValues();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext){
        if(value instanceof String){
            for(String s : strValues){
                if(s.equals(value)) {
                    return true;
                }
            }
        }else if(value instanceof Integer){
            for(Integer i : intValues){
                if(Objects.equals(i ,value)) {
                    return true;
                }
            }
        }
        return false;
    }
}
