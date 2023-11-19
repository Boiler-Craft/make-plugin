package com.example.makeplugin;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ResultMapBuilder {
    public ResultMapBuilder() {}

    String createResultMapConstructor(PsiClass psiClass) {
        final StringBuilder resultMapBuilder = new StringBuilder("new " + psiClass.getName() + "(");

        Arrays.stream(psiClass.getFields())
                .forEach(field -> {
                    resultMapBuilder.append(makeInitialValue(field.getType().getDeepComponentType().getPresentableText(), field.getType().getSuperTypes()) + ", ");
                });

        resultMapBuilder.setLength(resultMapBuilder.length() - 2);
        resultMapBuilder.append(");");

        return getStringBuilderResult(resultMapBuilder);
    }

    String createResultBuilder(PsiClass psiClass) {
        final StringBuilder resultMapBuilder = new StringBuilder(psiClass.getName() + ".builder()\n");

        Arrays.stream(psiClass.getFields())
                .forEach(field -> {
                    resultMapBuilder.append("." + field.getName() + "(" + makeInitialValue(field.getType().getPresentableText(), field.getType().getSuperTypes()) + ")\n");
                });

        resultMapBuilder.append(".build();");

        return getStringBuilderResult(resultMapBuilder);
    }

    private String makeInitialValue(String type, PsiType[] psiTypes) {
        if ("Integer".equals(type) || "int".equals(type)) {
            return "1";
        } else if ("Long".equals(type) || "long".equals(type)) {
            return "1L";
        } else if ("Short".equals(type) || "short".equals(type)) {
            return "0";
        } else if ("Float".equals(type) || "float".equals(type)) {
            return "0.0f";
        } else if ("Double".equals(type) || "double".equals(type)) {
            return "0.0";
        } else if ("Character".equals(type) || "char".equals(type)) {
            return "' '";
        } else if ("Boolean".equals(type) || "boolean".equals(type)) {
            return "false";
        } else if ("String".equals(type)) {
            return "\"\"";
        } else if ("BigDecimal".equals(type)) {
            return "BigDecimal.ZERO";
        } else if (type.contains("List")) {
            return "List.of()";
        } else if (type.contains("Map")) {
            return "Map.of()";
        } else if (type.contains("Set")) {
            return "Set.of()";
        } else if (type.contains("LocalDateTime")) {
            return "LocalDateTime.now()";
        }

        boolean anEnum = Arrays.stream(psiTypes)
                .filter(psiType -> psiType.getPresentableText().startsWith("Enum"))
                .findFirst()
                .isPresent();

        if (anEnum) {
            return type;
        }

        return "null";
    }

    @NotNull
    private String getResultMapName(String fieldName) {
        return fieldName.substring(0, 1).toLowerCase() + fieldName.substring(1) + "Map";
    }

    @NotNull
    private String getStringBuilderResult(StringBuilder resultMapBuilder) {
        String result = resultMapBuilder.toString();
        resultMapBuilder.setLength(0);
        return result;
    }

    private String getResultMapNameFromPsiClass(PsiClass psiClass) {
        String className = psiClass.getName();
        if (className == null) return "";
        return className.substring(0, 1).toLowerCase() + className.substring(1) + "Map";
    }

    @NotNull
    private String getFirstCharLowerCaseClassName(PsiClass psiClass) {
        String className = psiClass.getName();
        if (className == null) return "";
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }
}
