package io.finto.integration.fineract.annotation;

import io.finto.integration.fineract.FineractIntegrationAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FineractIntegrationAutoConfiguration.class)
public @interface EnableFineractIntegration {
}
