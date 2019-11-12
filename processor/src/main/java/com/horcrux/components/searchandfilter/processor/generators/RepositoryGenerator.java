package com.horcrux.components.searchandfilter.processor.generators;

import com.horcrux.components.searchandfilter.annotation.SearchAndFilter;
import com.horcrux.components.searchandfilter.processor.util.Constants;
import com.squareup.javapoet.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.Arrays;

/**
 * Created by midhun on 20/3/19.
 * @author midhun
 * Create Repository interface to support specification executer to aid criteria builder
 */
public class RepositoryGenerator {
    public static TypeSpec generate(String name, Element element, Messager messager, SearchAndFilter searchAndFilter) {
        ClassName jpaRepository = ClassName.get(JpaRepository.class);
        ClassName specificationExecuter = ClassName.get(JpaSpecificationExecutor.class);
        ClassName entity = ClassName.get(element.getEnclosingElement().asType().toString(), element.getSimpleName().toString());
        ClassName serializationFieldType = ClassName.get(Long.class);
        TypeName superInterfaceForJpaRepository = ParameterizedTypeName.get(jpaRepository, entity, serializationFieldType);
        TypeName superInterfaceForSpecificationExecuter = ParameterizedTypeName.get(specificationExecuter, entity);
        TypeSpec repository = TypeSpec.interfaceBuilder(name + Constants.REPOSITORY_TEXT)
                .addSuperinterfaces(Arrays.asList(superInterfaceForJpaRepository, superInterfaceForSpecificationExecuter))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Repository.class)
                .addJavadoc(CodeBlock.of("generated via annotation processing"))
                .build();
        return repository;
    }
}
