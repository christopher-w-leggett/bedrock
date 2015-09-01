package com.citytechinc.aem.bedrock.models.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.apache.sling.models.annotations.Source;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.spi.injectorspecific.InjectAnnotation;

/**
 * Allows for the injection of Resources or objects of types adaptable from
 * Resource based on path references. This injector will work for single
 * instances or parameterized Collections.
 *
 * This injector will look to the value of the property whose name is that of
 * the annotated element and treating it as an absolute or relative path, will
 * get the Resource identified by the path (starting from the current resource
 * when the path is relative).
 *
 * This injector will only work if you are adapting from a Resource or
 * SlingHttpServletRequest.
 *
 * This injector will respect the value of the {@link javax.inject.Named}
 * annotation.
 */
@Target({ METHOD, FIELD, PARAMETER })
@Retention(RUNTIME)
@InjectAnnotation
@Source(TagInject.NAME)
public @interface TagInject {

	String NAME = "tags";

	/**
	 * if set to REQUIRED injection is mandatory, if set to OPTIONAL injection
	 * is optional, in case of DEFAULT the standard annotations (
	 * {@link org.apache.sling.models.annotations.Optional},
	 * {@link org.apache.sling.models.annotations.Required}) are used. If even
	 * those are not available the default injection strategy defined on the
	 * {@link org.apache.sling.models.annotations.Model} applies. Default value
	 * = DEFAULT.
	 *
	 * @return Injection strategy
	 */
	InjectionStrategy injectionStrategy() default InjectionStrategy.DEFAULT;

	boolean inherit() default false;

}
