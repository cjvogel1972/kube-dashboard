package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1Event
import io.kubernetes.client.models.V1EventSource
import io.kubernetes.client.models.V1ObjectMeta
import org.joda.time.DateTime
import spock.lang.Specification

class EventSpec extends Specification {
    def "test creating an Event"() {
        given:
        def kubeEvent = Mock(V1Event)
        def metadata = Mock(V1ObjectMeta)
        def source = Mock(V1EventSource)
        source.component >> 'component'
        source.host >> 'localhost'
        kubeEvent.metadata >> metadata
        kubeEvent.type >> 'type'
        kubeEvent.reason >> 'reason'
        kubeEvent.count >> 1
        kubeEvent.firstTimestamp >> DateTime.now().minusDays(1)
        kubeEvent.source >> source
        kubeEvent.message >> 'message'

        when:
        def event = new Event(kubeEvent)

        then:
        event.type == 'type'
        event.reason == 'reason'
        event.interval == '1d'
        event.message == 'message'
        event.source == 'component,localhost'
    }

    def "test creating an Event multiple events"() {
        given:
        def kubeEvent = Mock(V1Event)
        def metadata = Mock(V1ObjectMeta)
        def source = Mock(V1EventSource)
        source.component >> 'component'
        source.host >> 'localhost'
        kubeEvent.metadata >> metadata
        kubeEvent.type >> 'type'
        kubeEvent.reason >> 'reason'
        kubeEvent.count >> 2
        kubeEvent.firstTimestamp >> DateTime.now().minusDays(2)
        kubeEvent.lastTimestamp >> DateTime.now().minusDays(1)
        kubeEvent.source >> source
        kubeEvent.message >> 'message'

        when:
        def event = new Event(kubeEvent)

        then:
        event.type == 'type'
        event.reason == 'reason'
        event.interval == '1d (x2 over 2d)'
        event.message == 'message'
        event.source == 'component,localhost'
    }

    def "test creating an Event no source host"() {
        given:
        def kubeEvent = Mock(V1Event)
        def metadata = Mock(V1ObjectMeta)
        def source = Mock(V1EventSource)
        source.component >> 'component'
        kubeEvent.metadata >> metadata
        kubeEvent.type >> 'type'
        kubeEvent.reason >> 'reason'
        kubeEvent.count >> 1
        kubeEvent.firstTimestamp >> DateTime.now().minusDays(1)
        kubeEvent.source >> source
        kubeEvent.message >> 'message'

        when:
        def event = new Event(kubeEvent)

        then:
        event.type == 'type'
        event.reason == 'reason'
        event.interval == '1d'
        event.message == 'message'
        event.source == 'component'
    }
}
