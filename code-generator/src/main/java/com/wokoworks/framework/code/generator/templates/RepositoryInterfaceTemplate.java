package com.wokoworks.framework.code.generator.templates;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * @author 0x0001
 */
public class RepositoryInterfaceTemplate extends AbstractTemplate<VoTemplate.Model> {

    @Override
    public void process(VoTemplate.Model model, OutputStream out) throws IOException {
        final String voName = Optional.ofNullable(CLASS_CONVERT.convert(model.getTableName())).orElseThrow(NullPointerException::new);
        final String className = String.format("%sRepository", voName);

        final ParameterizedTypeName baseRepository = ParameterizedTypeName.get(ClassName.get("com.wokoworks.framework.data", "BaseRepository"),
            ClassName.get(model.getPackageName() + ".vo", voName), ClassName.get(Integer.class));
        final TypeSpec type = TypeSpec.interfaceBuilder(className)
            .addModifiers(Modifier.PUBLIC)
            .addJavadoc("table name: $L\n\n", model.getTableName())
            .addJavadoc(getDateJavaDocCodeBlock())
            .addJavadoc(getAuthorJavaDocCodeBlock())
            .addSuperinterface(baseRepository)
            .build();

        final JavaFile.Builder builder = JavaFile.builder(model.getPackageName() + ".repository", type);
        write(out, builder.build());
    }

    public static void main(String[] args) throws IOException {
        VoTemplate.Model model = new VoTemplate.Model();
        model.setPackageName("com.wokoworks.data");
        model.setTableName("user_name");
        new RepositoryInterfaceTemplate()
            .process(model, System.out);
    }
}
