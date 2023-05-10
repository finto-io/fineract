package io.finto.integration.fineract.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Qualifier(FintoFinerectClientQualifier.QUALIFIER_VALUE)
public @interface FintoFinerectClientQualifier {
    String QUALIFIER_VALUE = "FintoFinerectClientQualifier";
}
