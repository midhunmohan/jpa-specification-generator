package com.horcrux.components.searchandfilter.processor.util;

import com.horcrux.components.searchandfilter.annotation.SearchAndFilter;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods
 * Created by midhun on 17/3/19.
 * @author midhun
 * @version 1.0
 */
public final class CommonUtil {

    /**
     * return string that to be used for name collection of a field
     * @param name
     * @return
     */
    public static String buildPluralForName(String name) {
        return name.concat("List");
    }

    /**
     * type to an Element for getting details like package, class, enclosing class etc
     * @param type
     * @return
     */
    private static TypeElement toElement(TypeMirror type) {
        return (TypeElement) ((DeclaredType) type).asElement();
    }

    /***
     * check whether the element is Enum or not
     * @param element
     * @return
     */
    public static Boolean isEnum(Element element) {
        return toElement(element.asType()).getKind() == ElementKind.ENUM;
    }

    /**
     * filter out primitive types
     * @param element
     * @return
     */
    public static Boolean isPrimitive(Element element) {
        return TypeName.get(element.asType()).isPrimitive();
    }

    /**
     * generate a pattern for naming methods
     * @param field
     * @return
     */
    public static String formatFieldNameForMethodName(Element field) {
        return field.getSimpleName().toString().substring(0, 1).toUpperCase() + field.getSimpleName().toString().substring(1, field.getSimpleName().length());
    }

    /**
     * generate a pattern for naming methods
     * @param field
     * @return
     */
    public static String formatFieldNameForMethodName(String field) {
        return field.substring(0, 1).toUpperCase() + field.toString().substring(1, field.length());
    }

    /**
     * Annotation provided option to exclude fields from processing
     * @param field
     * @param searchAndFilter
     * @return
     */
    public static Boolean isExcluded(Element field, SearchAndFilter searchAndFilter){
        return Stream.of(searchAndFilter.exclude()).collect(Collectors.toList()).contains(field.getSimpleName().toString());
    }

    /**
     * check whether the field is subject to be search by a given input
     * @param field
     * @param searchAndFilter
     * @return
     */
    public static Boolean subjectToSearch(Element field, SearchAndFilter searchAndFilter){
        return Stream.of(searchAndFilter.searchOver()).collect(Collectors.toList()).contains(field.getSimpleName().toString());
    }

    /**
     * get package- qualified name from element that annotated with SearchFilter annotation
     * @param e
     * @return
     */
    public static String getPackage(Element e){
        String qualifiedPath = e.getEnclosingElement().asType().toString();
        return qualifiedPath.substring(0, qualifiedPath.lastIndexOf("."));
    }

    /**
     * get name for Getter Methods
     * @param fieldName
     * @return
     */
    public static String getterName(String fieldName){
        return "get" + formatFieldNameForMethodName(fieldName);
    }

    /**
     * get name for setter methods
     * @param fieldName
     * @return
     */
    public static String setterName(String fieldName){
        return "set" + formatFieldNameForMethodName(fieldName);
    }
}
