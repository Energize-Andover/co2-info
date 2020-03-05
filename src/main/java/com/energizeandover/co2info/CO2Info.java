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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;

/**
 * Main class for co2-info. Reads CO2 data from csv and allows the user to find information.
 */
public class CO2Info {
    private static Scanner scanner;
    private static MeterDatabase meterDatabase;
    private static Path csvPath;

    /**
     * co2-info main method.
     *
     * @param args CSV file to read from
     */
    public static void main(String[] args) {
        try {
            scanner = new Scanner(System.in);
            csvPath = findPath(args);

            addReadings();

            System.out.println("Successfully loaded " + meterDatabase.size() + " meters.\n");
            showMenu();
        } catch (FileNotFoundException e) {
            System.out.println("Failed to load meters: File not found.");
        } catch (IOException e) {
            System.out.println("Failed to load meters: An IOException occurred.");
        } catch (CsvValidationException e) {
            System.out.println("Failed to load meters: A CsvException occurred.");
        }
    }

    /**
     * Returns a file path from command-line argument or user input.
     *
     * @param args command-line argument
     * @return file path
     */
    private static Path findPath(String[] args) throws FileNotFoundException {
        Path path;

        if (args.length > 0) {
            path = Paths.get(args[0]);
        } else {
            System.out.print("\nEnter input file name: ");
            path = Paths.get(scanner.nextLine().trim());
        }

        if (!Files.exists(path)) {
            throw new FileNotFoundException();
        }
        return path;
    }

    /**
     * Iterates through the CSV file and adds Readings to the MeterData of the MeterDatabase.
     */
    private static void addReadings() throws IOException, CsvValidationException {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath.toFile()))) {
            meterDatabase = initializeMeterDatabase(csvReader);

            System.out.println("Reading CSV...");
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
    private static MeterDatabase initializeMeterDatabase(CSVReader csvReader)
            throws IOException, CsvValidationException {
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
    private static String[] formatNames(String[] meterNames) {
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
    private static LocalDateTime parseTime(String timeEntry) {
        String[] date_time = timeEntry.split(" ");
        LocalDate localDate = LocalDate.parse(date_time[0]);
        LocalTime localTime = LocalTime.parse(date_time[1]);
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * Shows the co2-info menu.
     */
    private static void showMenu() {
        while (true) {
            String choice = promptChoice();
            switch (choice) {
                case "1":
                    System.out.println(meterDatabase.toStringAverages());
                    break;
                case "2":
                    System.out.println("Unhealthy ppm readings:\n"
                            + String.join("", findUnhealthyReadings(meterDatabase)));
                    break;
                case "3":
                    System.out.println("Broken ppm readings:\n"
                            + String.join("", findBrokenReadings(meterDatabase)));
                    break;
                case "4":
                    System.out.println("Enter meter name: ");
                    findMeter(scanner.nextLine());
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Please enter 1, 2, 3, 4, or 5.");
                    break;
            }
        }
    }

    private static String promptChoice() {
        System.out.println("Search Database: ");
        System.out.println("1. Find all average readings");
        System.out.println("2. Find unhealthy readings");
        System.out.println("3. Find broken readings");
        System.out.println("4. Find individual meter's readings");
        System.out.println("5. Quit");

        return scanner.nextLine().trim();
    }

    private static List<String> findBrokenReadings(MeterDatabase meterDatabase) {
        List<String> brokenReadings = new ArrayList<>();

        for (MeterData meterData : meterDatabase) {
            brokenReadings.add(meterData.toStringBrokenReadings());
        }
        return brokenReadings;
    }

    private static List<String> findUnhealthyReadings(MeterDatabase meterDatabase) {
        List<String> unhealthyReadings = new ArrayList<>();

        for (MeterData meterData : meterDatabase) {
            unhealthyReadings.add(meterData.toStringUnhealthyReadings());
        }
        return unhealthyReadings;
    }

    /**
     * Gets a list of the MeterData in the MeterDatabase that match the input name and lets the user view one.
     *
     * @param name input meter name
     */
    private static void findMeter(String name) {
        List<MeterData> meterDataList = meterDatabase.matchMeterName(name);

        System.out.println("Found " + meterDataList.size() + " meter(s) matching \"" + name + "\".");
        if (meterDataList.size() == 0) {
            scanner.nextLine();
            return;
        }

        for (int i = 0, size = meterDataList.size(); i < size; i++) {
            System.out.println((i + 1) + ". " + meterDataList.get(i).getMeterName());
        }
        int meterIndex = selectMeter(meterDataList.size());
        System.out.println(meterDataList.get(meterIndex));
    }

    /**
     * Prompts the user to select a MeterData.
     *
     * @param listSize size of list
     * @return int representing index of selected MeterData
     */
    private static int selectMeter(int listSize) {
        OptionalInt choice;
        do {
            System.out.println("Enter a number between [1, " + listSize + "]: ");
            choice = tryParseInt(scanner.nextLine().trim());
        } while (!choice.isPresent()
                || (choice.getAsInt() < 1 || choice.getAsInt() > listSize));
        return choice.getAsInt() - 1;
    }

    /**
     * Returns an optional int that contains the parsed string, or empty if cannot be parsed.
     *
     * @param string string to be parsed
     * @return optional int that contains the parsed string or empty if cannot be parsed
     */
    private static OptionalInt tryParseInt(String string) {
        try {
            return OptionalInt.of(Integer.parseInt(string));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
