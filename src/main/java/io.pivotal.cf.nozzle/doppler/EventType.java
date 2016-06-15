package io.pivotal.cf.nozzle.doppler;

public enum EventType {
    HttpStart,
    HttpStop,
    HttpStartStop,
    LogMessage,
    ValueMetric,
    CounterEvent,
    Error,
    ContainerMetric
}
