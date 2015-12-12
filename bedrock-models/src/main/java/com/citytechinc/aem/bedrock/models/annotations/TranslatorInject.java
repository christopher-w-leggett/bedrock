package com.citytechinc.aem.bedrock.models.annotations;

import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver;
import com.citytechinc.aem.bedrock.models.i18n.impl.DefaultLocaleResolver;
import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
     * The key for the translation in the AEM translator.
     */
    String key();

    /**
     * The locale resolver to use when resolving the locale.
     */
    Class<? extends LocaleResolver> localeResolver() default DefaultLocaleResolver.class;

}
