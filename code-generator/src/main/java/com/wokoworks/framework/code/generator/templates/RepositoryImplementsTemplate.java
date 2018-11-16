package com.wokoworks.framework.code.generator.templates;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * @author 0x0001
 */
public class RepositoryImplementsTemplate extends AbstractTemplate<VoTemplate.Model> {

    @Override
    public void process(VoTemplate.Model model, OutputStream out) throws IOException {
        final String voName = Optional.ofNullable(CLASS_CONVERT.convert(model.getTableName())).orElseThrow(NullPointerException::new);
        final String className = String.format("%sRepositoryImpl", voName);

        final ClassName voType = ClassName.get(model.getPackageName() + ".vo", voName);
        final ParameterizedTypeName baseRepository = ParameterizedTypeName.get(ClassName.get("com.wokoworks.framework.data.impl", "BaseRepositoryImpl"),
            voType, ClassName.get(Integer.class));
        final TypeSpec type = TypeSpec.classBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("table name: $L\n\n", model.getTableName())
            .addJavadoc(getDateJavaDocCodeBlock())
            .addJavadoc(getAuthorJavaDocCodeBlock())
            .superclass(baseRepository)
            .addSuperinterface(ClassName.get(model.getPackageName() + ".repository", voName + "Repository"))
            .addMethod(MethodSpec.methodBuilder("getTableName")
                .returns(String.class)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder().addStatement("return $S", model.getTableName()).build())
                .build())
            .addMethod(MethodSpec.methodBuilder("getBeanClass")
                .addAnnotation(Override.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Class.class), voType))
                .addModifiers(Modifier.PUBLIC)
                .addCode(CodeBlock.builder().addStatement("return $T.class", voType).build())
                .build())
            .addMethod(MethodSpec.methodBuilder("condition")
                .addModifiers(Modifier.PRIVATE)
                .returns(ClassName.get(model.getPackageName() + ".vo", voName,"ConditionBuilder"))
                .addStatement("return $T.conditionBuilder()", voType)
                .build())
            .build();

        final JavaFile.Builder builder = JavaFile.builder(model.getPackageName() + ".repository.impl", type);
        write(out, builder.build());
    }

    public static void main(String[] args) throws IOException {
        VoTemplate.Model model = new VoTemplate.Model();
        model.setPackageName("com.wokoworks.data");
        model.setTableName("user_name");
        new RepositoryImplementsTemplate()
            .process(model, System.out);
    }
}
