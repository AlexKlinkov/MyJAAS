package ru.inner.project.MyJAAS.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ValidateObject {

    public static boolean objectIsNotNullOrEmpty(Object param) {
        if (param != null) {
            String convertedParam = (String) param;
            return !convertedParam.isBlank();
        }
        return false;
    }
}
