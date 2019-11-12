package com.horcrux.components.searchandfilter.processor.generators;

import com.horcrux.components.searchandfilter.processor.util.CommonUtil;
import com.horcrux.components.searchandfilter.processor.util.Constants;
import com.horcrux.components.searchandfilter.annotation.SearchAndFilter;
import com.squareup.javapoet.*;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by midhun on 4/3/19.
 * @author midhun
 */
public class DTOGenerator {
    /**
     * Generate a Data Transfer Object to handle data for the possible filter criterias
     * @param name
     * @param element
     * @param messager
     * @param searchAndFilter
     * @return
     */
    public static TypeSpec generate(String name, Element element, Messager messager, SearchAndFilter searchAndFilter) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> methodSpecs = new ArrayList<>();
        ClassName list = ClassName.get(List.class);
        //excluded fields are not considered
        List<Element> fields = element.getEnclosedElements().stream()
                .filter(el -> el.getKind().isField())
                .filter(el -> !CommonUtil.isExcluded(el, searchAndFilter))
                .collect(Collectors.toList());
        fields.forEach(field -> {
            //add property declaration for DTO
            fieldSpecs.add(field(field.getSimpleName().toString(), TypeName.get(field.asType())));
            //getter method
            methodSpecs.add(getter(field.getSimpleName().toString(), TypeName.get(field.asType())));
            //setter method
            methodSpecs.add(setter(field.getSimpleName().toString(), TypeName.get(field.asType())));
        });
        //Add Fields for Array of Field Type which are under IN query support
        fields.stream()
                .filter(field -> Constants.IN.contains(TypeName.get(field.asType()).toString()))
                .forEach(field -> {
                    TypeName listOfField = ParameterizedTypeName.get(list, TypeName.get(field.asType()));
                    String fieldName = CommonUtil.buildPluralForName(field.getSimpleName().toString());
                    fieldSpecs.add(field(fieldName, listOfField));
                    methodSpecs.add(getter(fieldName, listOfField));
                    methodSpecs.add(setter(fieldName, listOfField));
                });
        //Add Fields to hold an array of Enum Types
        fields.stream().filter(field -> !CommonUtil.isPrimitive(field))
                .filter(field -> CommonUtil.isEnum(field))
                .forEach(field -> {
                    TypeName listOfField = ParameterizedTypeName.get(list, TypeName.get(field.asType()));
                    String fieldName = CommonUtil.buildPluralForName(field.getSimpleName().toString());
                    fieldSpecs.add(field(fieldName, listOfField));
                    methodSpecs.add(getter(fieldName, listOfField));
                    methodSpecs.add(setter(fieldName, listOfField));
                });
        //Generate Class
        TypeSpec filterDTO = TypeSpec.classBuilder(name + Constants.DTO_TEXT)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs)
                .addFields(fieldSpecs)
                .addJavadoc(CodeBlock.of("generated via annotation processing"))
                .build();
        return filterDTO;
    }

    /**
     * Return getter for a given field and return type
     * @param fieldName
     * @param typeName
     * @return
     */
    private static MethodSpec getter(String fieldName, TypeName typeName) {
        return MethodSpec
                .methodBuilder(CommonUtil.getterName(fieldName))
                .returns(typeName)
                .addStatement("return this.$L", fieldName)
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    /**
     * Return setter for a given field and argument type
     * @param fieldName
     * @param typeName
     * @return
     */
    private static MethodSpec setter(String fieldName, TypeName typeName) {
        return MethodSpec
                .methodBuilder(CommonUtil.setterName(fieldName))
                .returns(void.class)
                .addParameter(ParameterSpec.builder(typeName, fieldName).build())
                .addStatement("this.$L=$L", fieldName, fieldName)
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    /**
     * return field declaration for a given name and type
     * @param fieldName
     * @param typeName
     * @return
     */
    private static FieldSpec field(String fieldName, TypeName typeName) {
        return FieldSpec.builder(typeName, fieldName, Modifier.PRIVATE).build();
    }
}
