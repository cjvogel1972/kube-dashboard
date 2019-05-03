package org.vogel.kubernetes.dashboard

import io.kubernetes.client.models.V1PersistentVolumeClaimCondition
import org.joda.time.DateTime
import spock.lang.Specification

class PersistentVolumeClaimConditionSpec extends Specification {
    def "create a PersistentVolumeClaimCondition"() {
        given:
        DateTime dt = DateTime.now()
        V1PersistentVolumeClaimCondition pvcCond = new V1PersistentVolumeClaimCondition()
        pvcCond.setType("type")
        pvcCond.setStatus("status")
        pvcCond.setLastProbeTime(dt)
        pvcCond.setLastTransitionTime(dt)
        pvcCond.setReason("reason")
        pvcCond.setMessage("message")

        when:
        def condition = new PersistentVolumeClaimCondition(pvcCond)

        then:
        condition.type == "type"
        condition.status == "status"
        condition.lastProbeTime == dt
        condition.lastTransitionTime == dt
        condition.reason == "reason"
        condition.message == "message"
    }
}
