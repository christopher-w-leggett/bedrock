package com.citytechinc.aem.bedrock.core.replication

import com.citytechinc.aem.bedrock.core.specs.BedrockSpec
import com.citytechinc.aem.bedrock.core.utils.PathUtils
import com.day.cq.replication.ReplicationActionType
import com.day.cq.replication.ReplicationStatus
import com.day.cq.replication.Replicator
import org.apache.sling.api.resource.Resource
import org.apache.sling.api.resource.ResourceResolverFactory
import spock.lang.Shared

class PageReplicationListenerSpec extends BedrockSpec {

    static final def STATUS_ACTIVE = [isActivated: { true }] as ReplicationStatus

    static final def STATUS_INACTIVE = [isActivated: { false }] as ReplicationStatus

    @Shared
    PageReplicationListener listener

    def setupSpec() {
        pageBuilder.content {
            home {
                active1 {
                    active2()
                }
                inactive1 {
                    inactive2()
                }
            }
        }

        registerResourceAdapter(ReplicationStatus, { Resource resource ->
            def pagePath = PathUtils.getPagePath(resource.path)
            def status

            if (pagePath.startsWith("/content/home/active1")) {
                status = STATUS_ACTIVE
            } else {
                status = STATUS_INACTIVE
            }

            status
        })
    }

    def setup() {
        listener = new PageReplicationListener()

        listener.with {
            resourceResolverFactory = [getAdministrativeResourceResolver: {
                this.resourceResolver
            }] as ResourceResolverFactory
            replicator = Mock(Replicator)
            activate([(ENABLED): true])
        }
    }

    def "handle activate for invalid path does nothing"() {
        when:
        listener.handleActivate("/content/invalid")

        then:
        0 * listener.replicator.replicate(*_)
    }

    def "handle activate for non-page path does nothing"() {
        when:
        listener.handleActivate("/")

        then:
        0 * listener.replicator.replicate(*_)
    }

    def "handle activate for page path activates ancestor pages"() {
        when:
        listener.handleActivate("/content/home/inactive1/inactive2")

        then:
        with(listener.replicator) {
            1 * replicate(session, ReplicationActionType.ACTIVATE, "/content/home/inactive1")
            1 * replicate(session, ReplicationActionType.ACTIVATE, "/content/home")
        }
    }

    def "handle activate for page path ignores already activated ancestor pages"() {
        when:
        listener.handleActivate("/content/home/active1/active2")

        then:
        1 * listener.replicator.replicate(session, ReplicationActionType.ACTIVATE, "/content/home")
    }
}
