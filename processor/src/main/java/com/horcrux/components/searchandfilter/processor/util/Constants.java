package com.horcrux.components.searchandfilter.processor.util;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by midhun on 19/3/19.
 */
public class Constants {
    public static final String INITIALIZE_METHOD = "init";
    public static final String SPECIFICATION_TEXT="Specification";
    public static final String DTO_TEXT="DTO";
    public static final String REPOSITORY_TEXT="SearchAndFilterRepository";
    public static final String GREATER_THAN_TEXT="with{0}GreaterThan";
    public static final String GREATER_THAN_OR_EQUAL_TEXT="with{0}GreaterThanOrEqual";
    public static final String LESS_THAN_TEXT="with{0}LessThan";
    public static final String LESS_THAN_OR_EQUAL_TEXT="with{0}LessThanOrEqual";
    public static final String EQUALS_TEXT="with{0}Equals";
    public static final String IN_TEXT="with{0}In";
    public static final String LIKE_TEXT="with{0}Like";
    public static final String ALWAYS_TRUE="returnTrue";


    public static final String LIKE_QUERY_SEPARATOR = "%";
    public static final List<String> EQUALS = Arrays.asList(String.class, BigDecimal.class, Double.class, Integer.class, ZonedDateTime.class, Long.class, Boolean.class, Enum.class).stream().map(item -> item.getTypeName()).collect(Collectors.toList());
    public static final List<String> GT_LT_GTE_AND_LTE = Arrays.asList(BigDecimal.class, Double.class, Integer.class, ZonedDateTime.class, Long.class).stream().map(item -> item.getTypeName()).collect(Collectors.toList());
    public static final List<String> LIKE = Arrays.asList(String.class).stream().map(item -> item.getTypeName()).collect(Collectors.toList());
    public static final List<String> IN = Arrays.asList(String.class, BigDecimal.class, Double.class, Integer.class, Long.class).stream().map(item -> item.getTypeName()).collect(Collectors.toList());
    public static final List<String> SUPPORTED_TYPES = Arrays.asList(String.class, BigDecimal.class, Double.class, Integer.class, ZonedDateTime.class, Long.class, Boolean.class, Enum.class).stream().map(item -> item.getTypeName()).collect(Collectors.toList());
}
