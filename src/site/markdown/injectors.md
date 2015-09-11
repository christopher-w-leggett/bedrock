## Sling Models Injectors

### Available Injectors

Title | Name | Service Ranking | Description 
:-------|:-----|:-----|:-----
Resource Resolver Adaptable Injector | adaptable | Integer.MIN_VALUE | Injects objects that can be adapted from ResourceResolver
Component Content Injector | component | Integer.MAX_VALUE | Injects objects related to the current component context
Enum Injector | enum | 4000 | Injects Enums 
Image Injector | images | 4000 |  Injects com.day.cq.wcm.foundation.Image from the current resource
Inherit Injector | inherit | 4000 | Injects a property inheriting from parent pages if it isn't found on the current page
Link Injector | links | 4000 | Injects com.citytechinc.aem.bedrock.api.link.Link from the a property
Model List Injector | model-list | 999 | Injects a list of models from adapted from the child of a child resource gotten by name
Reference Injector | references | 4000 | Injects a resource or object adapted from a resource based on the value of a property
Tag Injector | tags | 800 | Gets a property and resolves tags from it
ValueMap Injector | valuemap | 2500 |  Gets a property from a ValueMap

### Injector-specific Annotations

Annotation | Supported Optional Elements | Injector
@ImageInject | injectionStrategy, isSelf, inherit, and selectors | images
@IneritInject | injectionStrategy | inherit
@LinkInject | injectionStrategy, titleProperty, and inherit | links
@ReferenceInject | injectionStrategy and inherit | references
@TagInject | injectionStrategy, and inherit | tags