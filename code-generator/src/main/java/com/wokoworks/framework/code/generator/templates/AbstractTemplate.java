package com.wokoworks.framework.code.generator.templates;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.JDBCType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 0x0001
 */
public abstract class AbstractTemplate<T> implements Template<T> {

    public static final Converter<String, String> CLASS_CONVERT = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL);
    public static final Converter<String, String> FIELD_CONVERT = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.LOWER_CAMEL);

    Type convertJDBCType(JDBCType jdbcType){
        return JDBCTypeConvert.getInstance().getType(jdbcType);
    }

    private String formatDate(Date date) {
        return SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(date);
    }

    void write(OutputStream out, JavaFile javaFile) throws IOException {
        final JavaFile newFile = javaFile.toBuilder()
            .skipJavaLangImports(true)
            .addFileComment("auto generator by wokoworks $L", formatDate(new Date()))
            .indent("    ").build();
        try(ByteArrayOutputStream arrOut = new ByteArrayOutputStream()) {
            try (Writer writer = new OutputStreamWriter(arrOut)) {
                newFile.writeTo(writer);
            }
            out.write(arrOut.toByteArray());
        }
    }

    CodeBlock getDateJavaDocCodeBlock() {
        return CodeBlock.builder().add("@date $L\n", formatDate(new Date())).build();
    }

    CodeBlock getAuthorJavaDocCodeBlock() {
        return CodeBlock.builder().add("@author $L\n", System.getProperty("user.name")).build();
    }
}
