package kg.alatoo.e_commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class EnumListValidator implements ConstraintValidator<ValidEnumList, List<String>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnumList constraintAnnotation) {
        this.enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(List<String> enumValues, ConstraintValidatorContext context) {
        if (enumValues == null || enumValues.isEmpty()) {
            return true;
        }

        for (String value : enumValues) {
            if (!isValidEnumValue(value)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidEnumValue(String value) {
        try {
            Enum<?>[] enumConstants = enumClass.getEnumConstants();
            for (Enum<?> enumConstant : enumConstants) {
                if (enumConstant.name().equalsIgnoreCase(value)) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
