package com.citytechinc.aem.bedrock.models.annotations;

import org.apache.sling.models.annotations.Source;
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
@Source(LinkInject.NAME)
public @interface LinkInject {

	String NAME = "links";

	/**
	 * If set to true, the model can be instantiated even if there is no link
	 * available. Default = true.
	 */
	boolean optional() default true;

	/**
	 * The property to use for setting the title on the link
	 */
	String titleProperty() default "";

	/**
	 * Whether to get the link via inheriting
	 */
	boolean inherit() default false;
}
