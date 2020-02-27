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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents a database of multiple CO2 meters' readings.
 */
public class MeterDatabase extends ArrayList<MeterData> {
    public MeterDatabase() {
        super();
    }

    public MeterDatabase(List<MeterData> meterDataList) {
        super(meterDataList);
    }

    public MeterDatabase(String[] meterNames) {
        super(meterNames.length);
        for (String meterName : meterNames) {
            meterName = meterName.replaceAll("\\s+CO2", "");
            this.add(new MeterData(meterName));
        }
    }

    public void addAll(LocalDateTime localDateTime, String[] ppmEntries) {
        for (int i = 0, length = ppmEntries.length; i < length; i++) {
            if (!ppmEntries[i].equals("")) {
                get(i).add(new Reading(localDateTime, Double.parseDouble(ppmEntries[i])));
            }
        }
    }

    public List<MeterData> matchMeterName(String name) {
        return this.stream()
                .filter(meterData -> meterData.getMeterName().contains(name))
                .collect(Collectors.toList());
    }

    public String toStringAverages() {
        StringBuilder stringBuilder = new StringBuilder("Meter Database\n");

        for (MeterData meterData : this) {
            stringBuilder.append(meterData.toStringAverage()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Meter Database\n");

        for (MeterData meterData : this) {
            stringBuilder.append(meterData).append("\n");
        }
        return stringBuilder.toString();
    }
}
