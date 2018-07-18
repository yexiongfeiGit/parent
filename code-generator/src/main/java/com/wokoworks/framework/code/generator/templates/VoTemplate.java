package com.wokoworks.framework.code.generator.templates;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.squareup.javapoet.*;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.lang.model.element.Modifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.sql.Ref;
import java.sql.Struct;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 0x0001
 */
@Slf4j
public class VoTemplate implements Template<VoTemplate.Model> {

    public static final Converter<String, String> CLASS_CONVERT = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);
    public static final Converter<String, String> FIELD_CONVERT = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

    private static final Map<JDBCType, Type> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put(JDBCType.BIT, boolean.class);
        TYPE_MAP.put(JDBCType.TINYINT, short.class);
        TYPE_MAP.put(JDBCType.SMALLINT, short.class);
        TYPE_MAP.put(JDBCType.INTEGER, int.class);
        TYPE_MAP.put(JDBCType.BIGINT, long.class);
        TYPE_MAP.put(JDBCType.FLOAT, float.class);
        TYPE_MAP.put(JDBCType.REAL, float.class);
        TYPE_MAP.put(JDBCType.DOUBLE, double.class);
        TYPE_MAP.put(JDBCType.NUMERIC, BigDecimal.class);
        TYPE_MAP.put(JDBCType.DECIMAL, BigDecimal.class);
        TYPE_MAP.put(JDBCType.CHAR, char.class);
        TYPE_MAP.put(JDBCType.VARCHAR, String.class);
        TYPE_MAP.put(JDBCType.LONGVARCHAR, String.class);
        TYPE_MAP.put(JDBCType.DATE, Date.class);
        TYPE_MAP.put(JDBCType.TIME, Date.class);
        TYPE_MAP.put(JDBCType.TIMESTAMP, Date.class);
        TYPE_MAP.put(JDBCType.BINARY, byte[].class);
        TYPE_MAP.put(JDBCType.VARBINARY, byte[].class);
        TYPE_MAP.put(JDBCType.LONGVARBINARY, byte[].class);
        TYPE_MAP.put(JDBCType.NULL, Object.class);
        TYPE_MAP.put(JDBCType.OTHER, Object.class);
        TYPE_MAP.put(JDBCType.JAVA_OBJECT, Object.class);
//      TYPE_MAP.put(JDBCType.DISTINCT, D)
        TYPE_MAP.put(JDBCType.STRUCT, Struct.class);
        TYPE_MAP.put(JDBCType.ARRAY, Object[].class);
        TYPE_MAP.put(JDBCType.BLOB, byte[].class);
        TYPE_MAP.put(JDBCType.CLOB, char[].class);
        TYPE_MAP.put(JDBCType.REF, Ref.class);
//      TYPE_MAP.put(JDBCType.DATALINK, datalink)
        TYPE_MAP.put(JDBCType.BOOLEAN, boolean.class);
        /* JDBC 4.0 Types */

//            /**
//             * Identifies the SQL type {@code ROWID}.
//             */
//            ROWID(Types.ROWID),
//            /**
//             * Identifies the generic SQL type {@code NCHAR}.
//             */
//            NCHAR(Types.NCHAR),
//            /**
//             * Identifies the generic SQL type {@code NVARCHAR}.
//             */
//            NVARCHAR(Types.NVARCHAR),
//            /**
//             * Identifies the generic SQL type {@code LONGNVARCHAR}.
//             */
//            LONGNVARCHAR(Types.LONGNVARCHAR),
//            /**
//             * Identifies the generic SQL type {@code NCLOB}.
//             */
//            NCLOB(Types.NCLOB),
//            /**
//             * Identifies the generic SQL type {@code SQLXML}.
//             */
//            SQLXML(Types.SQLXML),
//
//            /* JDBC 4.2 Types */
//
//            /**
//             * Identifies the generic SQL type {@code REF_CURSOR}.
//             */
//            REF_CURSOR(Types.REF_CURSOR),
//
//            /**
//             * Identifies the generic SQL type {@code TIME_WITH_TIMEZONE}.
//             */
//            TIME_WITH_TIMEZONE(Types.TIME_WITH_TIMEZONE),
//
//            /**
//             * Identifies the generic SQL type {@code TIMESTAMP_WITH_TIMEZONE}.
//             */
//            TIMESTAMP_WITH_TIMEZONE(Types.TIMESTAMP_WITH_TIMEZONE);
    }

    /**
     * 通过注释内容生成枚举的正则表达式匹配规则
     */
    private static final Pattern ENUM_PATTERN = Pattern.compile("(\\d+)\\:\\s*([\\w]+)(.*?)(?=\\d\\:|$)");


    @Override
    public void process(Model model, OutputStream out) throws IOException {
        @Nullable final String className = CLASS_CONVERT.convert(model.getTableName());

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

                while(matcher.find()) {
                    if (javaDoc == null) {
                        javaDoc = remark.substring(0, matcher.start());
                    }

                    if (enumBuilder == null) {
                        enumBuilder = newEnumBuilder(typeName);
                    }


                    final String value = matcher.group(1);
                    final String name = enumConvert.convert(matcher.group(2)).toUpperCase();
                    final String other = matcher.group(3);

                    enumBuilder.addEnumConstant(name, TypeSpec.anonymousClassBuilder("$L,$S", value, other)
                        .build());

                    log.info("name: {}, value: {}, comment: {}", name, value, other);
                }

                javaDoc = Optional.ofNullable(javaDoc).orElse(remark);
                builder.addJavadoc(javaDoc + "\n");

                if(enumBuilder != null) {
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
            .addAnnotation(Data.class)
            .addFields(fieldSpecs)
            .addTypes(subTypes)
            .build();

        final JavaFile javaFile = JavaFile.builder(model.packageName, typeSpec).build();
        final BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(out));

        javaFile.writeTo(bufferedWriter);
        bufferedWriter.flush();
    }

    private TypeSpec.Builder newEnumBuilder(@Nullable String typeName) {
        TypeSpec.Builder enumBuilder;
        enumBuilder = TypeSpec.enumBuilder(typeName)
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Getter.class);

        final TypeVariableName type = TypeVariableName.get(typeName);
        final TypeVariableName returnType = TypeVariableName.get("Optional<"+ typeName+">", Optional.class);

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

        return TYPE_MAP.get(jdbcType);
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
        while(matcher.find()) {
            System.out.println(matcher.start());
            final String value = matcher.group(1);
            final String name = matcher.group(2);
            final String other = matcher.group(3);

            log.info("name: {}, value: {}, comment: {}", name, value, other);
        }

    }
}
