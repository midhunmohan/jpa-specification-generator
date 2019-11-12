package com.horcrux.components.searchandfilter.processor.generators;

import com.horcrux.components.searchandfilter.processor.util.CommonUtil;
import com.horcrux.components.searchandfilter.annotation.SearchAndFilter;
import com.horcrux.components.searchandfilter.processor.util.Constants;
import com.squareup.javapoet.*;
import org.springframework.data.jpa.domain.Specification;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by midhun on 4/3/19.
 */
public class SpecificationGenerator {
    /**
     * generate Specification Class with criteria builders for search and filter results
     * @param name
     * @param element
     * @param messager
     * @param searchAndFilter
     * @return
     */
    public static TypeSpec generate(String name, Element element, Messager messager, SearchAndFilter searchAndFilter) {
        List<FieldSpec> fieldSpecs = new ArrayList<>();
        List<MethodSpec> methodSpecs = new ArrayList<>();
        List<Element> fields = element.getEnclosedElements().stream()
                .filter(el -> el.getKind().isField())
                .filter(el -> !CommonUtil.isExcluded(el, searchAndFilter))
                .collect(Collectors.toList());

        ClassName specification = ClassName.get(Specification.class);
        ClassName entity = ClassName.get(element.getEnclosingElement().asType().toString(), element.getSimpleName().toString());
        ClassName list = ClassName.get(List.class);
        TypeName entitySpecification = ParameterizedTypeName.get(specification, entity);
        //generate for fields having support for Equals operation
        fields.stream().filter(field -> Constants.EQUALS.contains(TypeName.get(field.asType()).toString())).forEach(field -> {
            methodSpecs.add(equalsOperation(field, entitySpecification));
        });
        //generate for fields having support for In operation
        fields.stream().filter(field -> Constants.IN.contains(TypeName.get(field.asType()).toString())).forEach(field -> {
            TypeName listOfField = ParameterizedTypeName.get(list, TypeName.get(field.asType()));
            methodSpecs.add(inOperation(field, entitySpecification, listOfField));
        });
        //generate for fields having support for LIKE operation
        fields.stream().filter(field -> CommonUtil.subjectToSearch(field, searchAndFilter)).filter(field -> Constants.LIKE.contains(TypeName.get(field.asType()).toString())).forEach(field -> {
            methodSpecs.add(likeOperation(field, entitySpecification));
        });
        //generate for fields having support for Comparison operators
        fields.stream().filter(field -> Constants.GT_LT_GTE_AND_LTE.contains(TypeName.get(field.asType()).toString())).forEach(field -> {
            methodSpecs.add(gteOperation(field, entitySpecification));
            methodSpecs.add(gtOperation(field, entitySpecification));
            methodSpecs.add(lteOperation(field, entitySpecification));
            methodSpecs.add(ltOperation(field, entitySpecification));
        });
        //generate for ENUM fields: IN & Equals operation
        fields.stream().filter(field -> !CommonUtil.isPrimitive(field)).filter(field -> CommonUtil.isEnum(field)).forEach(field -> {
            methodSpecs.add(equalsOperation(field, entitySpecification));
            TypeName listOfField = ParameterizedTypeName.get(list, TypeName.get(field.asType()));
            methodSpecs.add(inOperation(field, entitySpecification, listOfField));
        });

        //generate 1=1 where condition
        methodSpecs.add(alwaysTrueOperation(entitySpecification));
        //generate class with given name
        TypeSpec specificationClass = TypeSpec.classBuilder(name + Constants.SPECIFICATION_TEXT)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs)
                .addFields(fieldSpecs)
                .addJavadoc(CodeBlock.of("generated via annotation processing"))
                .build();
        return specificationClass;
    }

    /**
     * Returns Entity Specification with equality check for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec equalsOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.EQUALS_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> $L == null ? null: cb.equal(root.get($S),$L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with in operation for a given set of values
     * @param field
     * @param entitySpecification
     * @param listOfField
     * @return
     */
    private static MethodSpec inOperation(Element field, TypeName entitySpecification, TypeName listOfField) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.IN_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(listOfField, field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null || $L.isEmpty()) ? null: root.get($S).in($L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with search over a field for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec likeOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.LIKE_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null || $L.isEmpty()) ? null: cb.like(root.get($S), $S+ $L + $S)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString(), Constants.LIKE_QUERY_SEPARATOR, field.getSimpleName().toString(), Constants.LIKE_QUERY_SEPARATOR)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with Field value Greater than for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec gtOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.GREATER_THAN_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null) ? null: cb.greaterThan(root.get($S), $L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with Field value Greater than or equal to for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec gteOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.GREATER_THAN_OR_EQUAL_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null ) ? null: cb.greaterThanOrEqualTo(root.get($S), $L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with Field value Less than for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec ltOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.LESS_THAN_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null ) ? null: cb.lessThan(root.get($S), $L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * Returns Entity Specification with Field value Less than or equal for a given value
     * @param field
     * @param entitySpecification
     * @return
     */
    private static MethodSpec lteOperation(Element field, TypeName entitySpecification) {
        return MethodSpec
                .methodBuilder(MessageFormat.format(Constants.LESS_THAN_OR_EQUAL_TEXT, CommonUtil.formatFieldNameForMethodName(field)))
                .returns(entitySpecification)
                .addParameter(ParameterSpec.builder(TypeName.get(field.asType()), field.getSimpleName().toString()).build())
                .addStatement("return (root, query, cb) -> ( $L == null ) ? null: cb.lessThanOrEqualTo(root.get($S), $L)", field.getSimpleName().toString(), field.getSimpleName().toString(), field.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }

    /**
     * return true every time
     * @param entitySpec
     * @return
     */
    private static MethodSpec alwaysTrueOperation(TypeName entitySpec) {
        return MethodSpec
                .methodBuilder(Constants.ALWAYS_TRUE)
                .returns(entitySpec)
                .addStatement("return (root, query, cb) -> cb.isTrue(cb.literal(true))")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }
}
