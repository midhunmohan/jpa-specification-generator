package com.horcrux.components.searchandfilter.processor;


import com.google.auto.service.AutoService;
import com.horcrux.components.searchandfilter.processor.generators.RepositoryGenerator;
import com.horcrux.components.searchandfilter.processor.generators.SpecificationGenerator;
import com.horcrux.components.searchandfilter.processor.util.CommonUtil;
import com.horcrux.components.searchandfilter.annotation.SearchAndFilter;
import com.horcrux.components.searchandfilter.processor.generators.DTOGenerator;
import com.squareup.javapoet.JavaFile;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Created by midhun on 27/2/19.
 * @author midhun
 */
@SupportedAnnotationTypes("SearchAndFilter")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class SearchAndFilterProcessor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations,
                           RoundEnvironment roundEnv) {
        for (TypeElement te : annotations) {
            //Get the members that are annotated with
            for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
                generateCode(CommonUtil.getPackage(e), e, messager, ((TypeElement) e).getAnnotation(SearchAndFilter.class));
                messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, e.getSimpleName());
            }
        }
        return false;
    }

    /**
     * Prints an error message
     *
     * @param e   The element which has caused the error. Can be null
     * @param msg The error message
     */
    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    /**
     * Processes the class which annotated with @SearchAndFilter annotation
     * @param parentSpace
     * @param element
     * @param messager
     * @param searchAndFilter
     */
    private void generateCode(String parentSpace, Element element, Messager messager, SearchAndFilter searchAndFilter) {
        String generatedFileName = CommonUtil.formatFieldNameForMethodName(searchAndFilter.name().isEmpty() ? element.getSimpleName().toString() : searchAndFilter.name());

        JavaFile javaFileForDTO = JavaFile.builder(parentSpace + ".dto", DTOGenerator.generate(generatedFileName, element, messager, searchAndFilter))
                .build();
        JavaFile javaFileForSpecification = JavaFile.builder(parentSpace + ".specification", SpecificationGenerator.generate(generatedFileName, element, messager, searchAndFilter))
                .build();
        JavaFile javaFileForRepository = JavaFile.builder(parentSpace + ".repository", RepositoryGenerator.generate(generatedFileName, element, messager, searchAndFilter))
                .build();

        try {
            javaFileForDTO.writeTo(filer);
            javaFileForSpecification.writeTo(filer);
            javaFileForRepository.writeTo(filer);
        } catch (Exception e) {
            e.printStackTrace();
            messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
        }
    }
}
