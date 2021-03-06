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
import java.util.ArrayList;

/**
 * Class that represents one CO2 meter's readings.
 */
public class MeterData extends ArrayList<Reading> implements Serializable {
    private String meterName;

    public MeterData() {
    }

    public MeterData(String meterName) {
        super();
        this.meterName = meterName;
    }

    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public double findAveragePpm() {
        double sum = 0;
        int size = 0;
        for (Reading reading : this) {
            if (reading.getPpm() != 0) {
                sum += reading.getPpm();
                size++;
            }
        }
        return sum / size;
    }

    public int findReadingCount(double min, double max) {
        int count = 0;
        for (Reading reading : this) {
            if (reading.getPpm() >= min && reading.getPpm() < max) {
                count++;
            }
        }
        return count;
    }

    public int findReadingCount(int equals) {
        int count = 0;
        for (Reading reading : this) {
            if (reading.getPpm() == equals) {
                count++;
            }
        }
        return count;
    }

    public String toStringUnhealthyReadings() {
        int poorCount = findReadingCount(1000, 2000);
        int veryPoorCount = findReadingCount(2000, 5000);
        int criticalCount = findReadingCount(5000, Double.MAX_VALUE);

        return meterName + ":\n"
                + ((poorCount > 0) ? "    " + poorCount + " poor [1000ppm, 2000ppm)\n" : "")
                + ((veryPoorCount > 0) ? "    " + veryPoorCount + " very poor [2000ppm, 5000ppm)\n" : "")
                + ((criticalCount > 0) ? "    " + criticalCount + " critical > 5000ppm\n" : "");
    }

    public String toStringBrokenReadings() {
        int brokenCount = findReadingCount(0);
        return meterName + ": "
                + ((brokenCount > 0) ? brokenCount + " broken readings\n" : "\n");
    }

    public String toStringAverage() {
        return meterName + ": " + Math.round(findAveragePpm() * 100) / 100.0 + " average ppm";
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(meterName).append("\n");
        for (Reading reading : this) {
            stringBuilder.append(reading).append("\n");
        }
        return stringBuilder.toString();
    }
}
