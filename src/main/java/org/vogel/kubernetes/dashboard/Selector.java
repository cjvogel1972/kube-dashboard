package org.vogel.kubernetes.dashboard;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;

@Getter
public class Selector {
    private List<Requirement> requirements = new ArrayList<>();

    public void add(Requirement requirement) {
        requirements.add(requirement);
    }

    public boolean isEmpty() {
        return requirements.isEmpty();
    }

    public String string() {
        return requirements.stream()
                .map(Requirement::string)
                .collect(joining(","));
    }
}
