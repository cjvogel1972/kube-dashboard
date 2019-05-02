package org.vogel.kubernetes.dashboard;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.equalsAny;
import static org.vogel.kubernetes.dashboard.FormatUtils.joinListWithCommas;

@Getter
public class Requirement {
    private String key;
    private String operation;
    private List<String> values;

    public Requirement(String key, String operation, List<String> values) throws RequirementException {
        validateLabelKey(key);
        validateOperation(operation, values);
        for (String value : values) {
            validateLabelValue(value);
        }

        this.key = key;
        this.operation = operation;
        this.values = values;
        Collections.sort(this.values);
    }

    public String string() {
        StringBuilder builder = new StringBuilder();
        if (operation.equals("!")) {
            builder.append("!");
        }
        builder.append(key);

        if (operation.equals("=")) {
            builder.append("=");
        } else if (operation.equals("==")) {
            builder.append("==");
        } else if (operation.equals("!=")) {
            builder.append("!=");
        } else if (operation.equals("in")) {
            builder.append(" in ");
        } else if (operation.equals("notin")) {
            builder.append(" notin ");
        } else if (operation.equals("gt")) {
            builder.append(">");
        } else if (operation.equals("lt")) {
            builder.append("<");
        } else if (equalsAny(operation, "exists", "!")) {
            return builder.toString();
        }

        if (equalsAny(operation, "in", "notin")) {
            builder.append("(");
        }

        String joinedValues = joinListWithCommas(values);
        builder.append(joinedValues);

        if (equalsAny(operation, "in", "notin")) {
            builder.append(")");
        }

        return builder.toString();
    }

    private void validateLabelKey(String key) throws RequirementException {
        List<String> errs = isQualifiedName(key);
        if (!errs.isEmpty()) {
            String errMsg = String.format("invalid label key %s:%s", key, String.join(", ", errs));
            throw new RequirementException(errMsg);
        }
    }

    private void validateLabelValue(String value) throws RequirementException {
        List<String> errs = isValidLabelValue(value);
        if (!errs.isEmpty()) {
            String errMsg = String.format("invalid label value %s:%s", value, String.join(", ", errs));
            throw new RequirementException(errMsg);
        }
    }

    private void validateOperation(String operation, List<String> values) throws RequirementException {
        if (equalsAny(operation, "in", "notin")) {
            if (values.isEmpty()) {
                throw new RequirementException("for 'in', 'notin' operators, values set can't be empty");
            }
        } else if (equalsAny(operation, "=", "==", "!=")) {
            if (values.size() != 1) {
                throw new RequirementException("exact-match compatibility requires one single value");
            }
        } else if (equalsAny(operation, "exists", "!")) {
            if (!values.isEmpty()) {
                throw new RequirementException("values set must be empty for exists and does not exist");
            }
        } else if (equalsAny(operation, "gt", "lt")) {
            if (values.size() != 1) {
                throw new RequirementException("for 'Gt', 'Lt' operators, exactly one value is required");
            }
            for (String value : values) {
                try {
                    Integer.parseInt(value);
                } catch (NumberFormatException nfe) {
                    throw new RequirementException("for 'Gt', 'Lt' operators, the value must be an integer");
                }
            }
        } else {
            String message = String.format("operator '%s' is not recognized", operation);
            throw new RequirementException(message);
        }
    }

    private List<String> isQualifiedName(String value) {
        List<String> errs = new ArrayList<>();

        String qualifiedNameFmt = "([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]";

        String[] parts = value.split("/");

        String name = "";
        switch (parts.length) {
            case 1:
                name = parts[0];
                break;
            case 2:
                String prefix = parts[0];
                name = parts[1];
                if (prefix.length() == 0) {
                    errs.add("prefix part must be non-empty");
                } else {
                    List<String> msgs = isDNS1123Subdomain(prefix);
                    if (!msgs.isEmpty()) {
                        msgs.stream()
                                .map(msg -> String.format("prefix part %s", msg))
                                .forEach(errs::add);
                    }
                }
                break;
            default:
                errs.add(String.format(
                        "a qualified name %s with an optional DNS subdomain prefix and '/' (e.g. 'example.com/MyName')",
                        regexError(
                                "must consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character",
                                qualifiedNameFmt, "MyName", "my.name", "123-abc")));
        }

        if (name.length() == 0) {
            errs.add("name part must be non-empty");
        } else if (name.length() > 63) {
            errs.add("must be no more than 63 characters");
        }
        Pattern pattern = Pattern.compile(String.format("^%s$", qualifiedNameFmt));
        Matcher matcher = pattern.matcher(name);
        if (!matcher.matches()) {
            errs.add(String.format("name part %s", regexError(
                    "must consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character",
                    qualifiedNameFmt, "MyName", "my.name", "123-abc")));
        }

        return errs;
    }

    private List<String> isDNS1123Subdomain(String value) {
        List<String> errs = new ArrayList<>();

        if (value.length() > 253) {
            errs.add("must be no more than 253 characters");
        }

        String dns1123SubdomainFmt = "[a-z0-9]([-a-z0-9]*[a-z0-9])?(\\.[a-z0-9]([-a-z0-9]*[a-z0-9])?)*";
        Pattern pattern = Pattern.compile(String.format("^%s$", dns1123SubdomainFmt));
        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            errs.add(regexError(
                    "a DNS-1123 subdomain must consist of lower case alphanumeric characters, '-' or '.', and must start and end with an alphanumeric character",
                    dns1123SubdomainFmt, "example.com"));
        }

        return errs;
    }

    private String regexError(String msg, String fmt, String... exampleChoices) {
        String examples = Arrays.stream(exampleChoices)
                .map(ex -> String.format("'%s' ", ex))
                .collect(joining(", or "));
        return String.format("%s (e.g. %s, regex used for validation is '%s')", msg, examples, fmt);
    }

    private List<String> isValidLabelValue(String value) {
        List<String> errs = new ArrayList<>();

        if (value.length() > 63) {
            errs.add("must be no more than 63 characters");
        }

        String qualifiedNameFmt = "([A-Za-z0-9][-A-Za-z0-9_.]*)?[A-Za-z0-9]";
        Pattern pattern = Pattern.compile(String.format("^%s$", qualifiedNameFmt));
        Matcher matcher = pattern.matcher(value);
        if (!matcher.matches()) {
            errs.add(regexError(
                    "a valid label must be an empty string or consist of alphanumeric characters, '-', '_' or '.', and must start and end with an alphanumeric character",
                    qualifiedNameFmt, "MyValue", "my_value", "12345"));
        }

        return errs;
    }
}
