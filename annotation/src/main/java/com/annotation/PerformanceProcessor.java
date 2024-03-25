package com.annotation;

import com.annotation.PerformanceTest;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.sun.source.util.Trees;
import javax.lang.model.element.ExecutableElement;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class PerformanceProcessor extends AbstractProcessor
{
    @Override
    public Set<String> getSupportedAnnotationTypes()
    {
        Set<String> set = new HashSet<>();
        set.add(PerformanceTest.class.getName());

        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion()
    {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv)
    {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(PerformanceTest.class);
        List<FieldSpec> fieldSpecList = new ArrayList<>();
        List<MethodSpec> methodSpecList = new ArrayList<>();

        processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
            "list all : " + elements.toString());
        for (Element element : elements)
        {

            TypeElement typeElement = (TypeElement) element;

            for (Element field : typeElement.getEnclosedElements())
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "field : " + field.getKind());

                if (field.getKind() == ElementKind.METHOD) {
                    ExecutableElement executableElement = (ExecutableElement) field;
                    Trees trees = Trees.instance(processingEnv);
                    String methodBody = trees.getTree(executableElement).getBody().toString();
                    methodBody = methodBody.substring(2, methodBody.length());
                    methodBody = methodBody.substring(0, methodBody.length()-3);
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "element body : " + methodBody);

                    String methodNm = String.format("p%s", field.getSimpleName());

                    MethodSpec methodSpec = MethodSpec.methodBuilder(methodNm)
                        .addModifiers(Modifier.PUBLIC)
                        .addStatement("long start = System.currentTimeMillis()")
                        .addStatement(methodBody)
                        .addStatement("long end = System.currentTimeMillis()")
                        .addStatement("System.out.println(String.format(\"method running time : %d\", end-start))")
                        .build();

                    methodSpecList.add(methodSpec);

                } else if (field.getKind() == ElementKind.FIELD) {
                    String fieldNm = field.getSimpleName().toString();
                    TypeName fieldTypeName = TypeName.get(field.asType());

                    FieldSpec fieldSpec = FieldSpec.builder(fieldTypeName, fieldNm)
                        .build();

                    fieldSpecList.add(fieldSpec);
                }
            }
            ClassName className = ClassName.get(typeElement);
            String getterClassName = String.format("P%s", className.simpleName());

            TypeSpec getterClass = TypeSpec.classBuilder(getterClassName)
                .addModifiers(Modifier.PUBLIC)
                .addFields(fieldSpecList)
                .addMethods(methodSpecList)
                .build();

            try
            {
                JavaFile.builder(className.packageName(), getterClass)
                    .build()
                    .writeTo(processingEnv.getFiler());
            }
            catch (IOException e)
            {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "ERROR : " + e);
            }
        }

        return true;
    }
}
