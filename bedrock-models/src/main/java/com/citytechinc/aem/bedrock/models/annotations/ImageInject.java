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
@Source(ImageInject.NAME)
public @interface ImageInject {

	String NAME = "images";

	/**
	 * If set to true, the model can be instantiated even if there is no image
	 * available. Default = true.
	 */
	boolean optional() default true;

	/**
	 * The path to the image from the current resource. If none is set it will
	 * use the current resource.
	 */
	String path() default "";

	/**
	 * Whether to get the link via inheriting
	 */
	boolean inherit() default false;
}
