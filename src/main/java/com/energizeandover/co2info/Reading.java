/*
 * Copyright 2020 Energize Andover
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.energizeandover.co2info;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class that represents one CO2 reading.
 */
public class Reading implements Serializable {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private LocalDateTime localDateTime;
    private double ppm;
    private State state;

    public Reading() {
    }

    public Reading(LocalDateTime localDateTime, double ppm) {
        this.localDateTime = localDateTime;
        this.ppm = ppm;

        if (ppm >= 5000) {
            state = State.CRITICAL;
        } else if (ppm >= 2000) {
            state = State.VERY_POOR;
        } else if (ppm >= 1000) {
            state = State.POOR;
        } else if (ppm == 0) {
            state = State.BROKEN;
        } else {
            state = State.NORMAL;
        }
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public double getPpm() {
        return ppm;
    }

    public void setPpm(double ppm) {
        this.ppm = ppm;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return localDateTime.format(formatter) + " - " + ppm + "ppm " + state;
    }

    public enum State {
        CRITICAL("[CRITICAL]"),
        VERY_POOR("[VERY POOR]"),
        POOR("[POOR]"),
        NORMAL(""),
        BROKEN("[BROKEN]");

        private String warning;

        State(String warning) {
            this.warning = warning;
        }

        @Override
        public String toString() {
            return warning;
        }
    }
}
