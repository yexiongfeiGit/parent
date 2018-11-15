package com.wokoworks.framework.code.generator.templates;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;

import java.io.*;
import java.lang.reflect.Type;
import java.sql.JDBCType;
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

    void write(OutputStream out, JavaFile javaFile) throws IOException {
        try(ByteArrayOutputStream arrOut = new ByteArrayOutputStream()) {
            try (Writer writer = new OutputStreamWriter(arrOut)) {
                javaFile.writeTo(writer);
            }
            out.write(arrOut.toByteArray());
        }
    }

    CodeBlock getDateJavaDocCodeBlock() {
        return CodeBlock.builder().add("@date $L\n", new SimpleDateFormat("yyyy-MM-dd").format(new Date())).build();
    }

    CodeBlock getAuthorJavaDocCodeBlock() {
        return CodeBlock.builder().add("@author $L\n", System.getProperty("user.name")).build();
    }
}
