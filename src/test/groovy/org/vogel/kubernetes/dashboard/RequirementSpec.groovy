package org.vogel.kubernetes.dashboard

import spock.lang.Specification

class RequirementSpec extends Specification {
    def "create Requirement equals"() {
        when:
        Requirement req = new Requirement("foo", "=", ["bar"])

        then:
        req.key == "foo"
        req.operation == "="
        req.values.size() == 1
        req.values[0] == "bar"
        req.string() == "foo=bar"
    }

    def "create Requirement equals equals"() {
        when:
        Requirement req = new Requirement("foo", "==", ["bar"])

        then:
        req.key == "foo"
        req.operation == "=="
        req.values.size() == 1
        req.values[0] == "bar"
        req.string() == "foo==bar"
    }

    def "create Requirement not equals"() {
        when:
        Requirement req = new Requirement("foo", "!=", ["bar"])

        then:
        req.key == "foo"
        req.operation == "!="
        req.values.size() == 1
        req.values[0] == "bar"
        req.string() == "foo!=bar"
    }

    def "create Requirement not"() {
        when:
        Requirement req = new Requirement("foo", "!", [])

        then:
        req.key == "foo"
        req.operation == "!"
        req.values.size() == 0
        req.string() == "!foo"
    }

    def "create Requirement exists"() {
        when:
        Requirement req = new Requirement("foo", "exists", [])

        then:
        req.key == "foo"
        req.operation == "exists"
        req.values.size() == 0
        req.string() == "foo"
    }

    def "create Requirement in"() {
        when:
        Requirement req = new Requirement("foo", "in", ["blah", "bar"])

        then:
        req.key == "foo"
        req.operation == "in"
        req.values.size() == 2
        req.values[0] == "bar"
        req.values[1] == "blah"
        req.string() == "foo in (bar,blah)"
    }

    def "create Requirement not in"() {
        when:
        Requirement req = new Requirement("foo", "notin", ["blah", "bar"])

        then:
        req.key == "foo"
        req.operation == "notin"
        req.values.size() == 2
        req.values[0] == "bar"
        req.values[1] == "blah"
        req.string() == "foo notin (bar,blah)"
    }

    def "create Requirement greater than"() {
        when:
        Requirement req = new Requirement("foo", "gt", ["1"])

        then:
        req.key == "foo"
        req.operation == "gt"
        req.values.size() == 1
        req.values[0] == "1"
        req.string() == "foo>1"
    }

    def "create Requirement less than"() {
        when:
        Requirement req = new Requirement("foo", "lt", ["1"])

        then:
        req.key == "foo"
        req.operation == "lt"
        req.values.size() == 1
        req.values[0] == "1"
        req.string() == "foo<1"
    }

    def "create Requirement equals with domain"() {
        when:
        Requirement req = new Requirement("example.com/foo", "=", ["bar"])

        then:
        req.key == "example.com/foo"
        req.operation == "="
        req.values.size() == 1
        req.values[0] == "bar"
        req.string() == "example.com/foo=bar"
    }

    def "create Requirement equals empty values"() {
        when:
        new Requirement("foo", "=", [])

        then:
        def e = thrown(RequirementException)
        e.message == "exact-match compatibility requires one single value"
    }

    def "create Requirement in empty values"() {
        when:
        new Requirement("foo", "in", [])

        then:
        def e = thrown(RequirementException)
        e.message == "for 'in', 'notin' operators, values set can't be empty"
    }

    def "create Requirement exists with values"() {
        when:
        new Requirement("foo", "exists", ["bar"])

        then:
        def e = thrown(RequirementException)
        e.message == "values set must be empty for exists and does not exist"
    }

    def "create Requirement gt empty values"() {
        when:
        new Requirement("foo", "gt", [])

        then:
        def e = thrown(RequirementException)
        e.message == "for 'Gt', 'Lt' operators, exactly one value is required"
    }

    def "create Requirement gt not integer"() {
        when:
        new Requirement("foo", "gt", ["bar"])

        then:
        def e = thrown(RequirementException)
        e.message == "for 'Gt', 'Lt' operators, the value must be an integer"
    }

    def "create Requirement bad operator"() {
        when:
        new Requirement("foo", "eq", ["bar"])

        then:
        def e = thrown(RequirementException)
        e.message == "operator 'eq' is not recognized"
    }

    def "create Requirement key empty"() {
        when:
        new Requirement("", "eq", ["bar"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key :name part must be non-empty, name part must consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character (e.g. 'MyName' , or 'my.name' , or '123-abc' , regex used for validation is '([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]')"
    }

    def "create Requirement key too long"() {
        when:
        new Requirement("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.01234567890", "=", ["bar"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.01234567890:must be no more than 63 characters"
    }

    def "create Requirement value too long"() {
        when:
        new Requirement("foo", "=", ["abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.01234567890"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label value abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ-_.01234567890:must be no more than 63 characters"
    }

    def "create Requirement value bad regex"() {
        when:
        new Requirement("foo", "=", ["bar!"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label value bar!:a valid label must be an empty string or consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character (e.g. 'MyValue' , or 'my_value' , or '12345' , regex used for validation is '([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]')"
    }

    def "create Requirement key domain empty"() {
        when:
        new Requirement("/foo", "=", ["bat"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key /foo:prefix part must be non-empty"
    }

    def "create Requirement key domain too long"() {
        when:
        new Requirement("abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz/foo", "=", ["bat"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz.abcdefghijklmnopqrstuvwxyz/foo:prefix part must be no more than 253 characters"
    }

    def "create Requirement key domain too many slashes"() {
        when:
        new Requirement("example.com/foo/bar", "=", ["bat"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key example.com/foo/bar:a qualified name must consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character (e.g. 'MyName' , or 'my.name' , or '123-abc' , regex used for validation is '([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]') with an optional DNS subdomain prefix and '/' (e.g. 'example.com/MyName'), name part must be non-empty, name part must consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character (e.g. 'MyName' , or 'my.name' , or '123-abc' , regex used for validation is '([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]')"
    }

    def "create Requirement key domain bad regex"() {
        when:
        new Requirement(".example.com/foo", "=", ["bat"])

        then:
        def e = thrown(RequirementException)
        e.message == "invalid label key .example.com/foo:prefix part a DNS-1123 subdomain must consist of lower case alphanumeric characters, '-' or '.', and must start and end with an alphanumeric character (e.g. 'example.com' , regex used for validation is '[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*')"
    }
}
