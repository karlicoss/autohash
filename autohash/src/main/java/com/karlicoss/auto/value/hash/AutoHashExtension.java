package com.karlicoss.auto.value.hash;

import com.google.auto.service.AutoService;
import com.google.auto.value.extension.AutoValueExtension;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.squareup.javapoet.TypeName.INT;
import static java.util.Collections.nCopies;
import static javax.lang.model.element.Modifier.ABSTRACT;
import static javax.lang.model.element.Modifier.FINAL;
import static javax.lang.model.element.Modifier.PRIVATE;
import static javax.lang.model.element.Modifier.PUBLIC;

@AutoService(AutoValueExtension.class)
public class AutoHashExtension extends AutoValueExtension {

    @Override
    public boolean applicable( Context context) {
        TypeElement type = context.autoValueClass();
        return type.getAnnotation(AutoHash.class) != null;
    }

    @Override
    public String generateClass( Context context,  String className,  String classToExtend, boolean isFinal) {
        TypeSpec.Builder subclass = TypeSpec.classBuilder(className)
                .superclass(TypeVariableName.get(classToExtend))
                .addField(FieldSpec.builder(INT, "hashCode", PRIVATE).build())
                .addMethod(generateSuperCallConstructor(context.properties()))
                .addMethod(generateHashCode())
                .addModifiers(isFinal ? FINAL : ABSTRACT);

        return JavaFile.builder(context.packageName(), subclass.build()).build().toString();
    }


    MethodSpec generateHashCode() {
        MethodSpec.Builder hashCodeMethod = MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override.class)
                .addModifiers(PUBLIC)
                .returns(INT);

        hashCodeMethod.addStatement("int hash = hashCode");

        hashCodeMethod.beginControlFlow("if (hash == 0) ");
        hashCodeMethod.addStatement("int zeroable = super.hashCode()");
        hashCodeMethod.addStatement("hash = zeroable != 0 ? zeroable : 0xDEADBEEF");
        hashCodeMethod.addStatement("hashCode = hash");
        hashCodeMethod.endControlFlow();

        hashCodeMethod.addStatement("return hash");

        return hashCodeMethod.build();
    }


    private static MethodSpec generateSuperCallConstructor( Map<String, ExecutableElement> properties) {
        List<ParameterSpec> params = new ArrayList<>();
        for (Map.Entry<String, ExecutableElement> entry : properties.entrySet()) {
            TypeName typeName = TypeName.get(entry.getValue().getReturnType());
            params.add(ParameterSpec.builder(typeName, entry.getKey()).build());
        }

        CodeBlock code = CodeBlock.builder()
                .addStatement("super(" + Util.join(", ", nCopies(properties.size(), "$N")) + ")", properties.keySet().toArray())
                .build();

        return MethodSpec.constructorBuilder()
                .addParameters(params)
                .addCode(code)
                .build();
    }
}
