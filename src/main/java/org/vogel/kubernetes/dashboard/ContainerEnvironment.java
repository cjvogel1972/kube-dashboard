package org.vogel.kubernetes.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContainerEnvironment {
    private String name;
    private String value;
    private Boolean optional;
}
