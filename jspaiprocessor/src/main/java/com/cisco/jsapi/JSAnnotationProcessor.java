package com.cisco.jsapi;

import com.cisco.jsapi.JSApi;
import com.cisco.jsapi.JSApiError;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public final class JSAnnotationProcessor extends AbstractProcessor {

    private Messager mMessager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
    }

    private void log(String message) {
        if(mMessager != null) {
            mMessager.printMessage(Diagnostic.Kind.NOTE, message);
        }
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(JSApi.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        StringBuilder contentsb = new StringBuilder();
        ArrayList<ClassName> packagesb = new ArrayList<ClassName>();

        //method annotation
        Set<Element> tableSet = (Set<Element>)roundEnv.getElementsAnnotatedWith(JSApi.class);
        for (Element te: tableSet) {
            if(te.getKind() != ElementKind.METHOD) {
                continue;
            }

            String importName = te.getEnclosingElement().asType().toString();
            String methodName = te.getSimpleName().toString();
            createPackageCode(packagesb, importName);
            createSwitchCaseCode(contentsb, methodName, isStaticMethod(te));
        }

        //no support's method annotation (get the first JSApiError annotation, ignore others.)
        Set<Element> errorSet = (Set<Element>)roundEnv.getElementsAnnotatedWith(JSApiError.class);
        for (Element te: errorSet) {
            if(te.getKind() != ElementKind.METHOD) {
                continue;
            }

            String importName = te.getEnclosingElement().asType().toString();
            String methodName = te.getSimpleName().toString();
            createPackageCode(packagesb, importName);
            createSwtichDefaultCode(contentsb, methodName, isStaticMethod(te));
            break;
        }

        createClassFile(contentsb, packagesb);
        return true;
    }

    /**
     * create package code
     * @param packageList
     * @param importName
     */
    private void createPackageCode(List<ClassName> packageList, String importName) {
        int lastPointIndex = importName.lastIndexOf(".");
        ClassName cn = ClassName.get(importName.substring(0, lastPointIndex), importName.substring(++lastPointIndex));
        packageList.add(cn);
    }

    /**
     * create swtich case code
     * @param csb
     * @param methodName
     */
    private void createSwitchCaseCode(StringBuilder csb, String methodName, boolean isStaticMethod) {
        csb.append("case \"").append(methodName).append("\":");
        csb.append("\n").append("  ");
        if(isStaticMethod) {
            csb.append("$T.");
        } else {
            csb.append("$T.get().");
        }
        csb.append(methodName).append("(params);").append("\n");
        csb.append("  ").append("break;").append("\n");
    }

    /**
     * create switch default code
     * @param csb
     * @param methodName
     * @param isStaticMethod
     */
    private void createSwtichDefaultCode(StringBuilder csb, String methodName, boolean isStaticMethod) {
        csb.append("default:").append("\n").append("  ");
        if(isStaticMethod) {
            csb.append("$T.");
        } else {
            csb.append("$T.get().");
        }
        csb.append(methodName).append("(params);").append("\n");
        csb.append("  ").append("break;").append("\n");
    }

    /**
     * create class file
     * @param csb
     * @param psb
     */
    private void createClassFile(StringBuilder csb, ArrayList<ClassName> psb) {
        if(psb.size() == 0) {
            return ;
        }

        MethodSpec method = MethodSpec.methodBuilder(com.cisco.jsapi.JSApiConfig.METHOD_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String.class, "method")
                .addParameter(String.class, "params")
                .addStatement("switch(method) { \n" + csb.toString() + "}", psb.toArray())
                .build();

        TypeSpec _class = TypeSpec.classBuilder(com.cisco.jsapi.JSApiConfig.CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(method)
                .build();

        JavaFile javaFile = JavaFile.builder(com.cisco.jsapi.JSApiConfig.PACKAGE_NAME, _class).build();

        try {
            javaFile.writeTo(processingEnv.getFiler());
            javaFile.writeTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * whether is static method
     * @param te
     * @return
     */
    private boolean isStaticMethod(Element te) {
        boolean flag = false;
        Set<Modifier> set = te.getModifiers();
        for (Modifier m : set) {
            if(m.name().equals("STATIC")) {
                flag = true;
                break;
            }
        }

        return flag;
    }

}
