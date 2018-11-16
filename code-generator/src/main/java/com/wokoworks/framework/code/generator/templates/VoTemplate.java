package com.wokoworks.framework.code.generator.templates;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.squareup.javapoet.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 0x0001
 */
@Slf4j
public class VoTemplate extends AbstractTemplate<VoTemplate.Model> {

    /**
     * 通过注释内容生成枚举的正则表达式匹配规则
     */
    private static final Pattern ENUM_PATTERN = Pattern.compile("(\\d+)\\:\\s*([\\w]+)(.*?)(?=\\d+\\:|$)");


    @Override
    public void process(Model model, OutputStream out) throws IOException {
        final String className = Optional.ofNullable(CLASS_CONVERT.convert(model.getTableName())).orElseThrow(NullPointerException::new);

        final FieldProcess fieldProcess = new FieldProcess(model);
        fieldProcess.process();
        final List<FieldSpec> fieldSpecs = fieldProcess.getFieldSpecs();
        final List<TypeSpec> subTypes = fieldProcess.getSubTypes();

        final TypeSpec typeSpec = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("table name: $L\n\n", model.getTableName())
            .addJavadoc(getDateJavaDocCodeBlock())
            .addJavadoc(getAuthorJavaDocCodeBlock())
            .addAnnotation(Data.class)
            .addAnnotation(Builder.class)
            .addAnnotation(NoArgsConstructor.class)
            .addAnnotation(AllArgsConstructor.class)
            .addFields(fieldSpecs)
            .addTypes(subTypes)
            .addMethod(MethodSpec.methodBuilder("conditionBuilder")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(TypeVariableName.get("ConditionBuilder"))
                .addCode(CodeBlock.builder().addStatement("return new $T()", TypeVariableName.get("ConditionBuilder")).build())
                .build())
            .build();

        final JavaFile javaFile = JavaFile.builder(model.packageName + ".vo", typeSpec)
            .build();
        write(out, javaFile);
    }


    @Data
    public static class Model {
        private String packageName;
        private String tableName;
        private List<Column> columnList;
    }

    @Data
    public static class Column {
        private String name;
        private int type;
        private String remark;
    }

    private class FieldProcess {

        private final Model model;

        @Getter
        private final List<TypeSpec> subTypes = new ArrayList<>();
        @Getter
        private final List<FieldSpec> fieldSpecs = new ArrayList<>();

        private final TypeSpec.Builder conditionBuilder = TypeSpec
            .classBuilder("ConditionBuilder")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        private final Map<String, String> enumMap = new HashMap<>();


        FieldProcess(Model model) {
            this.model = model;

            final ClassName condition = ClassName.get("com.wokoworks.framework.data", "Condition");
            final TypeVariableName conditionBuilder = TypeVariableName.get("ConditionBuilder");
            final ClassName conditions = ClassName.get("com.wokoworks.framework.data.impl.condition", "Conditions");

            this.conditionBuilder.addField(FieldSpec.builder(condition, "condition", Modifier.PRIVATE).build())
                .addMethod(MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(condition)
                    .addCode(CodeBlock.builder().addStatement("return condition").build())
                    .build())
                .addMethod(MethodSpec.methodBuilder("and")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(conditionBuilder)
                    .addParameter(condition, "condition")
                    .addCode(CodeBlock.builder()
                        .beginControlFlow("if (this.condition == null)")
                        .addStatement("this.condition = condition")
                        .endControlFlow()
                        .beginControlFlow("else")
                        .addStatement("this.condition = $T.and(this.condition, condition)", conditions)
                        .endControlFlow()
                        .addStatement("return this")
                        .build())
                    .build())
                .addMethod(MethodSpec.methodBuilder("or")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(conditionBuilder)
                    .addParameter(condition, "condition")
                    .beginControlFlow("if (this.condition == null)")
                    .addStatement("this.condition = condition")
                    .endControlFlow()
                    .beginControlFlow("else")
                    .addStatement("this.condition = $T.or(this.condition, condition)", conditions)
                    .endControlFlow()
                    .addStatement("return this")
                    .build())
            ;

        }

        private Type getType(Column column) {

            final int type = column.getType();
            final JDBCType jdbcType = JDBCType.valueOf(type);

            return convertJDBCType(jdbcType);
        }

        private void addField(Column column) {
            // create field
            final FieldSpec.Builder builder = FieldSpec.builder(getType(column), FIELD_CONVERT.convert(column.getName()), Modifier.PRIVATE);
            final String remark = column.getRemark();
            if (!Strings.isNullOrEmpty(remark)) {
                builder.addJavadoc(remark + "\n");
            }
            final FieldSpec fieldSpec = builder.build();
            fieldSpecs.add(fieldSpec);
        }

        private void addCondition(Column column) {
            final ClassName conditions = ClassName.get("com.wokoworks.framework.data.impl.condition", "Conditions");

            final TypeVariableName returnType = TypeVariableName.get("ConditionBuilder");
            // and equal
            final String name = column.getName();

            TypeName paramType = TypeName.get(convertJDBCType(JDBCType.valueOf(column.type)));

            boolean isEnum = false;

            if (enumMap.containsKey(column.getName())) {
                paramType = TypeVariableName.get(enumMap.get(column.getName()));
                isEnum = true;
            }

            // and
            final String fieldName = FIELD_CONVERT.convert(name);
            final MethodSpec.Builder equalBuilder = MethodSpec.methodBuilder(fieldName + "EqualTo")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramType, fieldName)
                .returns(returnType);
            if (isEnum) {
                equalBuilder.addStatement("return and($T.equals($S, $L.code))", conditions, column.name, fieldName);
            } else {
                equalBuilder.addStatement("return and($T.equals($S, $L))", conditions, column.name, fieldName);
            }
            conditionBuilder.addMethod(equalBuilder.build());


            final MethodSpec.Builder andEqualBuilder = MethodSpec.methodBuilder(FIELD_CONVERT.convert("and_" + name + "_equal_to"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramType, fieldName)
                .returns(returnType);
            if (isEnum) {
                andEqualBuilder.addStatement("return and($T.equals($S, $L.code))", conditions, column.name, fieldName);
            } else {
                andEqualBuilder.addStatement("return and($T.equals($S, $L))", conditions, column.name, fieldName);
            }
            conditionBuilder.addMethod(andEqualBuilder.build());

            // or equal
            final MethodSpec.Builder orEqualBuilder = MethodSpec.methodBuilder(FIELD_CONVERT.convert("or_" + name + "_equal_to"))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(paramType, fieldName)
                .returns(returnType);
            if (isEnum){
                orEqualBuilder.addStatement("return or($T.equals($S, $L.code))", conditions, name, fieldName);
            } else {
                orEqualBuilder.addStatement("return or($T.equals($S, $L))", conditions, name, fieldName);
            }


            conditionBuilder.addMethod(orEqualBuilder.build());
        }

        private void tryMakeEnum(Column column) {
            final Converter<String, String> enumConvert = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

            // try create enum
            final String remark = column.getRemark();
            if (!Strings.isNullOrEmpty(remark)) {
                final Matcher matcher = ENUM_PATTERN.matcher(remark);

                TypeSpec.Builder enumBuilder = null;

                @Nullable final String typeName = CLASS_CONVERT.convert(column.getName());

                while (matcher.find()) {

                    if (enumBuilder == null) {
                        enumBuilder = newEnumBuilder(typeName);
                    }

                    final TypeSpec.Builder localEnumBuilder = enumBuilder;

                    Optional.ofNullable(enumConvert.convert(matcher.group(2))).ifPresent(name -> {
                        final String value = matcher.group(1);
                        final String enumName = name.toUpperCase();
                        final String other = matcher.group(3);

                        localEnumBuilder.addEnumConstant(enumName, TypeSpec.anonymousClassBuilder("$L,$S", value, other.trim())
                            .build());

                        log.info("name: {}, value: {}, comment: {}", name, value, other);
                    });
                }

                if (enumBuilder != null) {
                    enumMap.put(column.getName(), typeName);
                    subTypes.add(enumBuilder.build());
                }
            }
        }

        public void process() {
            model.getColumnList().stream()
                .peek(this::addField)
                .peek(this::tryMakeEnum)
                .forEach(this::addCondition);

            // 私有构造器
            conditionBuilder.addMethod(MethodSpec.constructorBuilder().addModifiers(Modifier.PRIVATE).build());

            subTypes.add(conditionBuilder.build());
        }

        private TypeSpec.Builder newEnumBuilder(String typeName) {
            final TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(typeName)
                .addModifiers(Modifier.PUBLIC);

            final TypeVariableName type = TypeVariableName.get(typeName);
            final ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Optional.class), type);

            enumBuilder.addMethod(MethodSpec.methodBuilder("valueOf")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(int.class, "code")
                .addCode(CodeBlock.builder()
                    .beginControlFlow("for ($T v : values())", type)
                    .add(CodeBlock.builder()
                        .beginControlFlow("if (v.code == code)")
                        .addStatement("return Optional.of(v)")
                        .endControlFlow()
                        .build())
                    .endControlFlow()
                    .addStatement("return Optional.empty()")
                    .build())
                .returns(returnType)
                .build());

            enumBuilder.addField(FieldSpec.builder(TypeName.INT, "code", Modifier.PUBLIC, Modifier.FINAL).addJavadoc("程序使用代码,会存储在数据库中\n").build())
                .addField(FieldSpec.builder(String.class, "remark", Modifier.PUBLIC, Modifier.FINAL).addJavadoc("可读性备注\n").build());

            enumBuilder.addMethod(MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(TypeName.INT, "code")
                .addParameter(String.class, "remark")
                .addCode(CodeBlock.builder()
                    .addStatement("this.code = code")
                    .addStatement("this.remark = remark")
                    .build())
                .build());
            return enumBuilder;
        }
    }

    public static void main(String[] args) {
        String str = "分类ID 1: New分类1 2: Old 分类2";
        final Pattern pattern = Pattern.compile("(\\d+)\\:\\s*([\\w]+)(.*?)(?=\\d\\:|$)");

        final Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            System.out.println(matcher.start());
            final String value = matcher.group(1);
            final String name = matcher.group(2);
            final String other = matcher.group(3);

            log.info("name: {}, value: {}, comment: {}", name, value, other);
        }

    }
}
