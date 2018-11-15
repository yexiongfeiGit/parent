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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        List<TypeSpec> subTypes = new ArrayList<>();

        final Converter<String, String> enumConvert = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

        List<FieldSpec> fieldSpecs = new ArrayList<>();
        for (Column column : model.getColumnList()) {
            final FieldSpec.Builder builder = FieldSpec.builder(getType(column), FIELD_CONVERT.convert(column.getName()), Modifier.PRIVATE);
            final String remark = column.getRemark();
            if (!Strings.isNullOrEmpty(remark)) {
                String javaDoc = null;
                final Matcher matcher = ENUM_PATTERN.matcher(remark);

                TypeSpec.Builder enumBuilder = null;

                @Nullable final String typeName = CLASS_CONVERT.convert(column.getName());

                while (matcher.find()) {
                    if (javaDoc == null) {
                        javaDoc = remark.substring(0, matcher.start());
                    }

                    if (enumBuilder == null) {
                        enumBuilder = newEnumBuilder(typeName);
                    }

                    final TypeSpec.Builder localEnumBuilder = enumBuilder;

                    Optional.ofNullable(enumConvert.convert(matcher.group(2))).ifPresent(name -> {
                        final String value = matcher.group(1);
                        final String enumName = name.toUpperCase();
                        final String other = matcher.group(3);

                        localEnumBuilder.addEnumConstant(enumName, TypeSpec.anonymousClassBuilder("$L,$S", value, other)
                            .build());

                        log.info("name: {}, value: {}, comment: {}", name, value, other);
                    });

                }

                javaDoc = Optional.ofNullable(javaDoc).orElse(remark);
                builder.addJavadoc(javaDoc + "\n");

                if (enumBuilder != null) {
                    final TypeSpec enumType = enumBuilder

                        .build();
                    subTypes.add(enumType);
                }
            }
            final FieldSpec fieldSpec = builder.build();
            fieldSpecs.add(fieldSpec);
        }

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
            .build();

        final JavaFile javaFile = JavaFile.builder(model.packageName + ".vo", typeSpec).build();
        write(out, javaFile);
    }

    private TypeSpec.Builder newEnumBuilder(@Nullable String typeName) {
        TypeSpec.Builder enumBuilder;
        enumBuilder = TypeSpec.enumBuilder(typeName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Getter.class);

        final TypeVariableName type = TypeVariableName.get(typeName);
        final TypeVariableName returnType = TypeVariableName.get("Optional<" + typeName + ">", Optional.class);

        enumBuilder.addMethod(MethodSpec.methodBuilder("valueOf")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(int.class, "code")
            .addCode(CodeBlock.builder()
                .beginControlFlow("for ($T v : values())", type)
                .add(CodeBlock.builder()
                    .beginControlFlow("if (v.code == code)")
                    .addStatement("return Optional.of(v)")
                    .endControlFlow()
                    .addStatement("return Optional.empty()")
                    .build())
                .endControlFlow()
                .build())
            .returns(returnType)
            .build());

        enumBuilder.addField(TypeName.INT, "code", Modifier.PUBLIC, Modifier.FINAL)
            .addField(String.class, "remark", Modifier.PUBLIC, Modifier.FINAL);

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

    private Type getType(Column column) {
        final int type = column.getType();
        final JDBCType jdbcType = JDBCType.valueOf(type);

        return convertJDBCType(jdbcType);
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
