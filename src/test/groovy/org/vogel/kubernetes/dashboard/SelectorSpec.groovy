package org.vogel.kubernetes.dashboard

import spock.lang.Specification

class SelectorSpec extends Specification {
    def "create Selector and is empty"() {
        when:
        Selector selector = new Selector()

        then:
        selector.isEmpty()
        selector.string() == ""
        selector.requirements.size() == 0
    }

    def "add single Request"() {
        given:
        Requirement req = new Requirement("foo", "=", ["bar"])
        Selector selector = new Selector()

        when:
        selector.add(req)

        then:
        !selector.isEmpty()
        selector.string() == "foo=bar"
        selector.requirements.size() == 1
    }
}
