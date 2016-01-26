package com.citytechinc.aem.bedrock.models.annotations;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Allows for the injection of translated content.
 * <p/>
 * This injector will create a new instance on the specified {@code LocaleResolver} and provide it the current
 * resource and resource resolver for identifying the {@code Locale}.  The {@code Locale} will then be used to identify
 * a {@code ResourceBundle} for retrieving the translated content.
 */
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(TranslatorInject.NAME)
public @interface TranslatorInject {

    String NAME = "translator";

    /**
     * if set to REQUIRED injection is mandatory, if set to OPTIONAL injection is optional, in case of DEFAULT the
     * standard annotations ( {@link org.apache.sling.models.annotations.Optional}, {@link
     * org.apache.sling.models.annotations.Required}) are used. If even those are not available the default injection
     * strategy defined on the {@link org.apache.sling.models.annotations.Model} applies. Default value = DEFAULT.
     *
     * @return Injection strategy
     */
    InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;

    /**
     * The text to translate..
     */
    String text();

    /**
     * A comment for translators to specify the context in which the text is used.
     */
    String comment() default "";

    /**
     * The filter for selecting a locale resolver to use when resolving the locale.  If no value is provided,
     * standard OSGi service ranking is used to pick a locale resolver.
     */
    String localeResolverFilter() default "";

}
