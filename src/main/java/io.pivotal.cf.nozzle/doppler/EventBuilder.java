/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.cf.nozzle.doppler;

import org.cloudfoundry.doppler.Error;
import org.cloudfoundry.doppler.Event;
import org.cloudfoundry.doppler.HttpStart;

public class EventBuilder {
    @SuppressWarnings("unchecked")
    public static <T extends Event> T toEvent(org.cloudfoundry.dropsonde.events.Envelope envelope) {
        switch (envelope.eventType) {
            case HttpStart:
                return (T) HttpStart.from(envelope.httpStart);
            case HttpStop:
                return (T) org.cloudfoundry.doppler.HttpStop.from(envelope.httpStop);
            case HttpStartStop:
                return (T) org.cloudfoundry.doppler.HttpStartStop.from(envelope.httpStartStop);
            case LogMessage:
                return (T) org.cloudfoundry.doppler.LogMessage.from(envelope.logMessage);
            case ValueMetric:
                return (T) org.cloudfoundry.doppler.ValueMetric.from(envelope.valueMetric);
            case CounterEvent:
                return (T) org.cloudfoundry.doppler.CounterEvent.from(envelope.counterEvent);
            case Error:
                return (T) Error.from(envelope.error);
            case ContainerMetric:
                return (T) org.cloudfoundry.doppler.ContainerMetric.from(envelope.containerMetric);
            default:
                throw new IllegalStateException(String.format("Envelope event type %s is unsupported", envelope.eventType));
        }
    }

}
