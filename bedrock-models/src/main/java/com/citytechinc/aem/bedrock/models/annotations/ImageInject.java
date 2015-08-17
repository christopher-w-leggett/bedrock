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
	String SELF = ".";
    String IMG_SELECTOR = "img";

	/**
	 * If set to true, the model can be instantiated even if there is no image
	 * available. Default = true.
	 */
	boolean optional() default true;

	/**
	 * The path to the image from the current resource. If none is set it will
	 * look for a child resource with a name matching the name of the annotated
     * property or method using bean conventions.  This property can be set to
     * ImageInject.SELF in cases where the current resource is also the image
     * resource.
	 */
	String path() default "";

	/**
	 * Whether to get the link via inheriting
	 */
	boolean inherit() default false;

	/**
	 * Selector to set on the injected Image object.  This affects the calculated
	 * source of the image.  Defaults to img as this selector will trigger the OOB
	 * ImageServlet and is usually the selector you want.
	 */
	String[] selectors() default { IMG_SELECTOR };

}
