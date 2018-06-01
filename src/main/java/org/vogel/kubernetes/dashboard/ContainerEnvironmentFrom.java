package org.vogel.kubernetes.dashboard;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContainerEnvironmentFrom {
    private String name;
    private String from;
    private Boolean optional;
    private String prefix;

}
