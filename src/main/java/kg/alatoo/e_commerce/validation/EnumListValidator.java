package kg.alatoo.e_commerce.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class EnumListValidator implements ConstraintValidator<ValidEnumList, List<String>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnumList constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
    }

    @Override
    public boolean isValid(List<String> enumValues, ConstraintValidatorContext context) {
        if (enumValues == null || enumValues.isEmpty()) {
            return true; // Null or empty lists are considered valid (you can adjust this behavior)
        }

        for (String value : enumValues) {
            try {
                // Validate each value against the enum class
                Enum.valueOf(enumClass, value.toUpperCase()); // case-insensitive comparison
            } catch (IllegalArgumentException e) {
                return false; // Invalid enum value found
            }
        }

        return true; // All values are valid
    }
}

