package org.vogel.kubernetes.dashboard;

import io.kubernetes.client.custom.IntOrString;
import io.kubernetes.client.custom.Quantity;
import io.kubernetes.client.models.*;
import lombok.Getter;
import org.apache.commons.collections4.MapUtils;

import java.math.BigDecimal;
import java.util.*;

import static java.util.stream.Collectors.*;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.vogel.kubernetes.dashboard.FormatUtils.joinListWithCommas;

@Getter
public class Container {
    private String name;
    private String containerId;
    private String image;
    private String imageId;
    private String ports;
    private String hostPorts;
    private List<String> commands;
    private List<String> args;
    private ContainerState state;
    private ContainerState lastState;
    private Boolean ready;
    private Integer restartCount;
    private Map<String, String> limits = new HashMap<>();
    private Map<String, String> requests = new HashMap<>();
    private String livenessProbe;
    private String readinessProbe;
    private List<ContainerEnvironmentFrom> envFrom = new ArrayList<>();
    private List<ContainerEnvironment> env = new ArrayList<>();
    private List<String> mounts = new ArrayList<>();
    private List<Integer> containerPorts;

    public Container(V1Container kubeContainer, List<V1ContainerStatus> containerStatuses) {
        name = kubeContainer.getName();
        Optional<V1ContainerStatus> containerStatus;
        if (containerStatuses == null) {
            containerStatus = Optional.empty();
        } else {
            containerStatus = containerStatuses.stream()
                    .filter(status -> status.getName()
                            .equals(name))
                    .findFirst();
        }
        describeContainerBasicInfo(kubeContainer);
        containerStatus.ifPresent(this::describeContainerBasicStatusInfo);
        describeContainerCommand(kubeContainer);
        containerStatus.ifPresent(this::describeContainerState);
        describeContainerResource(kubeContainer.getResources());
        describeContainerProbe(kubeContainer);
        if (kubeContainer.getEnvFrom() != null && !kubeContainer.getEnvFrom()
                .isEmpty()) {
            describeContainerEnvFrom(kubeContainer.getEnvFrom());
        }
        describeContainerEnvVars(kubeContainer);
        describeContainerVolumes(kubeContainer);
    }

    private void describeContainerBasicInfo(V1Container kubeContainer) {
        image = kubeContainer.getImage();
        List<V1ContainerPort> kubeContainerPorts = kubeContainer.getPorts();
        if (kubeContainerPorts != null) {
            containerPorts = kubeContainerPorts.stream()
                    .filter(port -> port.getContainerPort() != null)
                    .map(V1ContainerPort::getContainerPort)
                    .collect(toList());
        }
        this.ports = describeContainerPorts(kubeContainerPorts);
        hostPorts = describeContainerHostPorts(kubeContainerPorts);
    }

    private void describeContainerBasicStatusInfo(V1ContainerStatus status) {
        containerId = status.getContainerID();
        imageId = status.getImageID();
    }

    private String describeContainerPorts(List<V1ContainerPort> ports) {
        String result;

        if (ports != null) {
            result = ports.stream()
                    .map(port -> String.format("%d/%s", port.getContainerPort(), port.getProtocol()))
                    .collect(joining(","));
        } else {
            result = "";
        }

        return result;
    }

    private String describeContainerHostPorts(List<V1ContainerPort> ports) {
        String result;

        if (ports != null) {
            result = ports.stream()
                    .map(port -> String.format("%d/%s", defaultIfNull(port.getHostPort(), 0),
                                               port.getProtocol()))
                    .collect(joining(","));
        } else {
            result = "";
        }

        return result;
    }

    private void describeContainerCommand(V1Container kubeContainer) {
        if (kubeContainer.getCommand() != null) {
            commands = kubeContainer.getCommand();
        }
        if (kubeContainer.getArgs() != null) {
            args = kubeContainer.getArgs();
        }
    }

    private void describeContainerState(V1ContainerStatus status) {
        state = new ContainerState(status.getState());
        lastState = new ContainerState(status.getLastState());
        ready = status.isReady();
        restartCount = status.getRestartCount();
    }

    private void describeContainerResource(V1ResourceRequirements resources) {
        Map<String, Quantity> resourcesLimits = resources.getLimits();
        if (MapUtils.isNotEmpty(resourcesLimits)) {
            limits = resourcesLimits
                    .keySet()
                    .stream()
                    .collect(toMap(key -> key, key -> resourcesLimits.get(key)
                            .toSuffixedString()));
        }
        Map<String, Quantity> resourcesRequests = resources.getRequests();
        if (MapUtils.isNotEmpty(resourcesRequests)) {
            requests = resourcesRequests
                    .keySet()
                    .stream()
                    .collect(toMap(key -> key, key -> resourcesRequests.get(key)
                            .toSuffixedString()));
        }
    }

    private void describeContainerProbe(V1Container kubeContainer) {
        if (kubeContainer.getLivenessProbe() != null) {
            livenessProbe = describeProbe(kubeContainer.getLivenessProbe());
        }
        if (kubeContainer.getReadinessProbe() != null) {
            readinessProbe = describeProbe(kubeContainer.getReadinessProbe());
        }
    }

    private String describeProbe(V1Probe probe) {
        String attrs = String.format("delay=%ds timeout=%ds period=%ds #success=%d #failure=%d",
                                     probe.getInitialDelaySeconds(), probe.getTimeoutSeconds(),
                                     probe.getPeriodSeconds(), probe.getSuccessThreshold(),
                                     probe.getFailureThreshold());
        if (probe.getExec() != null) {
            String command = joinListWithCommas(probe.getExec()
                                                        .getCommand());
            return String.format("exec %s %s", command, attrs);
        } else if (probe.getHttpGet() != null) {
            String url;
            V1HTTPGetAction httpGet = probe.getHttpGet();
            if (httpGet.getPort() != null) {
                IntOrString port = httpGet.getPort();
                if (port.isInteger() && port.getIntValue() > 0) {
                    url = String.format("%s://%s:%d/%s", httpGet.getScheme(), httpGet.getHost(), httpGet.getPort()
                                                .getIntValue(),
                                        httpGet.getPath());
                } else if (!port.isInteger() && isBlank(port.getStrValue())) {
                    url = String.format("%s://%s:%s/%s", httpGet.getScheme(), httpGet.getHost(), httpGet.getPort()
                                                .getStrValue(),
                                        httpGet.getPath());
                } else {
                    url = String.format("%s://%s/%s", httpGet.getScheme(), httpGet.getHost(), httpGet.getPath());
                }
            } else {
                url = String.format("%s://%s/%s", httpGet.getScheme(), httpGet.getHost(), httpGet.getPath());
            }

            return String.format("http-get %s %s", url, attrs);
        } else if (probe.getTcpSocket() != null) {
            return String.format("tcp-socket %s:%s %s", probe.getTcpSocket()
                    .getHost(), probe.getTcpSocket()
                                         .getPort()
                                         .getIntValue(), attrs);
        }

        return String.format("unknown %s", attrs);
    }

    private void describeContainerEnvFrom(List<V1EnvFromSource> envFromSources) {
        for (V1EnvFromSource envFromSource : envFromSources) {
            String envName = null;
            String from = null;
            Boolean optional = null;
            String prefix = envFromSource.getPrefix();
            if (envFromSource.getConfigMapRef() != null) {
                from = " ConfigMap";
                envName = envFromSource.getConfigMapRef()
                        .getName();
                optional = Boolean.TRUE.equals(envFromSource.getConfigMapRef()
                                                       .isOptional());
            } else if (envFromSource.getSecretRef() != null) {
                from = " Secret";
                envName = envFromSource.getSecretRef()
                        .getName();
                optional = Boolean.TRUE.equals(envFromSource.getSecretRef()
                                                       .isOptional());
            }
            if (isNotBlank(envName)) {
                ContainerEnvironmentFrom cef = new ContainerEnvironmentFrom(envName, from, optional, prefix);
                envFrom.add(cef);
            }
        }
    }

    // TODO start need to fix the logic for this
    private void describeContainerEnvVars(V1Container kubeContainer) {
        List<V1EnvVar> envVars = kubeContainer.getEnv();
        if (envVars != null) {
            for (V1EnvVar envVar : envVars) {
                ContainerEnvironment containerEnvironment = null;
                V1EnvVarSource envVarValueFrom = envVar.getValueFrom();
                if (envVarValueFrom == null) {
                    containerEnvironment = new ContainerEnvironment(envVar.getName(), envVar.getValue(), null);
                } else {
                    if (envVarValueFrom.getFieldRef() != null) {
                        String valueFrom = envVarResolverFunc(envVar);

                        V1ObjectFieldSelector fieldRef = envVarValueFrom.getFieldRef();
                        String value = String.format("%s (%s:%s)", valueFrom, fieldRef.getApiVersion(),
                                                     fieldRef.getFieldPath());
                        containerEnvironment = new ContainerEnvironment(envVar.getName(), value, null);
                    } else if (envVarValueFrom.getResourceFieldRef() != null) {
                        extractContainerResourceValue(envVarValueFrom.getResourceFieldRef(), kubeContainer);
                        String resource = envVarValueFrom.getResourceFieldRef()
                                .getResource();
                    } else if (envVarValueFrom.getSecretKeyRef() != null) {
                        V1SecretKeySelector secretKeyRef = envVarValueFrom.getSecretKeyRef();
                        Boolean optional = Boolean.TRUE.equals(secretKeyRef.isOptional());
                        String value = String.format("&lt;set to the key '%s' in secret '%s'&gt;",
                                                     secretKeyRef.getKey(),
                                                     secretKeyRef.getName());
                        containerEnvironment = new ContainerEnvironment(envVar.getName(), value, optional);
                    } else if (envVarValueFrom.getConfigMapKeyRef() != null) {
                        V1ConfigMapKeySelector configMapKeyRef = envVarValueFrom.getConfigMapKeyRef();
                        Boolean optional = Boolean.TRUE.equals(configMapKeyRef.isOptional());
                        String value = String.format("&lt;set to the key '%s' in config map '%s'&gt;",
                                                     configMapKeyRef.getKey(),
                                                     configMapKeyRef.getName());
                        containerEnvironment = new ContainerEnvironment(envVar.getName(), value, optional);
                    }
                }
                if (containerEnvironment != null) {
                    env.add(containerEnvironment);
                }
            }
        }
    }

    private String envVarResolverFunc(V1EnvVar env) {
        try {
            V1ObjectFieldSelector fieldRef = env.getValueFrom()
                    .getFieldRef();
            String internalFieldPath = convertFieldLabel(fieldRef.getApiVersion(), "Pod", fieldRef.getFieldPath(), "");
            return extractFieldPathAsString(/*pod*/null, internalFieldPath);
        } catch (Exception e) {
            return "";
        }
    }

    private String convertFieldLabel(String apiVersion, String pod, String fieldPath, String s) {
        return "";
    }

    private String extractFieldPathAsString(Object obj, String fieldPath) {
        return "";
    }

    private String extractContainerResourceValue(V1ResourceFieldSelector fs,
                                                 V1Container kubeContainer) {
        Quantity divisor = new Quantity(new BigDecimal(fs.getDivisor()), Quantity.Format.DECIMAL_SI);

        switch (fs.getResource()) {
            case "limits.cpu":
                return convertResourceCPUToString(kubeContainer.getResources()
                                                          .getLimits()
                                                          .get(""), divisor);
            case "limits.memory":
            case "limits.ephemeral-storage":
            case "requests.cpu":
                return convertResourceCPUToString(kubeContainer.getResources()
                                                          .getRequests()
                                                          .get(""), divisor);
            case "requests.memory":
            case "requests.ephemeral-storage":
            default:
        }

        return "";
    }

    private String convertResourceCPUToString(Quantity cpu, Quantity divisor) {
        return ""/*cpu.getNumber()
                .divide(divisor.getNumber())
                .toString()*/;
    }
    // TODO end need to fix the logic for this

    private void describeContainerVolumes(V1Container kubeContainer) {
        List<V1VolumeMount> volumeMounts = kubeContainer.getVolumeMounts();
        if (volumeMounts != null) {
            for (V1VolumeMount mount : volumeMounts) {
                List<String> flagsList = new ArrayList<>();
                if (Boolean.TRUE.equals(mount.isReadOnly())) {
                    flagsList.add("ro");
                } else {
                    flagsList.add("rw");
                }
                if (isNotBlank(mount.getSubPath())) {
                    flagsList.add(String.format("path=%s", mount.getSubPath()));
                }
                String flags = joinListWithCommas(flagsList);
                mounts.add(String.format("%s from %s (%s)", mount.getMountPath(), mount.getName(), flags));
            }
        }
    }
}
