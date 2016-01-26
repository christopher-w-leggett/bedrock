package com.citytechinc.aem.bedrock.models.impl

import com.citytechinc.aem.bedrock.core.mocks.i18n.MockResourceBundleProvider
import com.citytechinc.aem.bedrock.models.annotations.TranslatorInject
import com.citytechinc.aem.bedrock.models.i18n.LocaleResolver
import com.citytechinc.aem.bedrock.models.specs.BedrockModelSpec
import com.google.common.base.Optional
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolver
import org.apache.sling.i18n.ResourceBundleProvider
import org.apache.sling.models.annotations.Model

import static org.osgi.framework.Constants.SERVICE_RANKING

class TranslatorInjectorSpec extends BedrockModelSpec {

    @Model(adaptables = Resource)
    static class TranslatorInjectorModel {

        @TranslatorInject(text = "some text")
        String someTextTranslation

        @TranslatorInject(text = "some text", comment = "with comment")
        String someTextWithCommentTranslation

        @TranslatorInject(text = "some text", localeResolverFilter = "(resolver.name=custom)")
        String someTextWithCustomResolverTranslation
    }

    def setupSpec() {
        nodeBuilder.apps {
            citytechinc {
                i18n {
                    en {
                        sometext(
                            "sling:key": "some text",
                            "sling:message": "some text with custom resolver"
                        )
                    }
                    es {
                        sometext(
                            "sling:key": "some text",
                            "sling:message": "un poco de texto"
                        )
                        sometextwithcomment(
                            "sling:key": "some text ((with comment))",
                            "sling:message": "un texto con comentario"
                        )
                    }
                }
            }
        }

        pageBuilder.content {
            citytechinc {
                es {
                    "jcr:content" {
                        component {
                        }
                    }
                }
            }
        }

        //NOTE: TranslatorInjector uses ServiceReference natural ordering to determine correct services to use.
        //There is a defect in the MockServiceReference of the sling testing library that does not perform correct ordering.
        //Because of this  we can't successfully test the service reference ordering through this spec.
        //For now, just setting custom resolver to max value to satisfy test.
        slingContext.with {
            registerService(LocaleResolver, new CustomLocaleResolver(), ["resolver.name": "custom", (SERVICE_RANKING): Integer.MAX_VALUE])
            registerService(ResourceBundleProvider, new MockResourceBundleProvider("/apps/citytechinc/i18n", resourceResolver), [:])
        }
    }

    def "inject translations"() {
        setup:
        def resource = resourceResolver.resolve("/content/citytechinc/es/jcr:content/component")

        def model = resource.adaptTo(TranslatorInjectorModel)

        expect:
        model.someTextTranslation == "un poco de texto"
        model.someTextWithCommentTranslation == "un texto con comentario"
        model.someTextWithCustomResolverTranslation == "some text with custom resolver"
    }

    public static class CustomLocaleResolver implements LocaleResolver {

        @Override
        Optional<Locale> resolve(final Resource resource, final ResourceResolver resourceResolver) {
            Optional.of(Locale.ENGLISH)
        }
    }
}
