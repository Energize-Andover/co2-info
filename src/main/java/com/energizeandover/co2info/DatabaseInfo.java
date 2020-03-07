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

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseInfo {
    private Path csvPath;
    private MeterDatabase meterDatabase;

    public DatabaseInfo(Path csvPath) {
        this.csvPath = csvPath;
    }

    /**
     * Iterates through the CSV file and adds Readings to the MeterData of the MeterDatabase.
     */
    public void addReadings() throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath.toFile()))) {
            meterDatabase = initializeMeterDatabase(csvReader);

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                LocalDateTime localDateTime = parseTime(line[0]);

                String[] ppmEntries = new String[line.length - 1];
                System.arraycopy(line, 1, ppmEntries, 0, ppmEntries.length);

                meterDatabase.addAll(localDateTime, ppmEntries);
            }
        }
    }

    /**
     * Gets meter names from headers and returns a MeterDatabase from them.
     *
     * @return MeterDatabase with meters from header names (skipping temperature column)
     */
    private MeterDatabase initializeMeterDatabase(CSVReader csvReader) throws IOException, CsvValidationException {
        String[] firstLine;
        firstLine = csvReader.readNext();

        String[] meterNames = Arrays.copyOfRange(firstLine, 1, firstLine.length);
        meterNames = formatNames(meterNames);
        return new MeterDatabase(meterNames);
    }

    /**
     * Removes "CO2" and extra spaces from meter names.
     *
     * @param meterNames array containing names of meters
     * @return array of names with "CO2" and extra spaces removed
     */
    private String[] formatNames(String[] meterNames) {
        return Arrays.stream(meterNames)
                .map(name -> name.replaceAll("\\s+CO2", ""))
                .toArray(String[]::new);
    }

    /**
     * Parses a LocalDateTime from String timeEntry.
     *
     * @param timeEntry String containing date and time separated by a space
     * @return LocalDateTime matching timeEntry
     */
    private LocalDateTime parseTime(String timeEntry) {
        String[] date_time = timeEntry.split(" ");
        LocalDate localDate = LocalDate.parse(date_time[0]);
        LocalTime localTime = LocalTime.parse(date_time[1]);
        return LocalDateTime.of(localDate, localTime);
    }

    public int findSize() {
        return meterDatabase.size();
    }

    public String findAverages() {
        return meterDatabase.toStringAverages();
    }

    public List<String> findBrokenReadings() {
        List<String> brokenReadings = new ArrayList<>();

        for (MeterData meterData : meterDatabase) {
            brokenReadings.add(meterData.toStringBrokenReadings());
        }
        return brokenReadings;
    }

    public List<String> findUnhealthyReadings() {
        List<String> unhealthyReadings = new ArrayList<>();

        for (MeterData meterData : meterDatabase) {
            unhealthyReadings.add(meterData.toStringUnhealthyReadings());
        }
        return unhealthyReadings;
    }

    public List<MeterData> findMeters(String name) {
        return meterDatabase.matchMeterName(name);
    }
}
