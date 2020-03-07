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

import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.OptionalInt;
import java.util.Scanner;

public class Main {
    private static DatabaseInfo databaseInfo;

    /**
     * co2-info main method.
     *
     * @param args CSV file to read from
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Path csvPath = findPath(args, scanner);
            databaseInfo = new DatabaseInfo(csvPath);

            System.out.println("Reading CSV...");
            databaseInfo.addReadings();

            System.out.println("Successfully loaded " + databaseInfo.findSize() + " meters.\n");
            showMenu(scanner);
        } catch (IOException | CsvValidationException e) {
            System.out.println("Failed to load meters: " + e.toString());
        }
    }

    /**
     * Returns a file path from command-line argument or user input.
     *
     * @param args command-line argument
     * @return file path
     */
    private static Path findPath(String[] args, Scanner scanner) throws FileNotFoundException {
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
     * Shows the co2-info menu.
     */
    private static void showMenu(Scanner scanner) {
        while (true) {
            String choice = promptChoice(scanner);
            switch (choice) {
                case "1":
                    System.out.println(databaseInfo.findAverages());
                    break;
                case "2":
                    System.out.println("Unhealthy ppm readings:\n"
                            + String.join("", databaseInfo.findUnhealthyReadings()));
                    break;
                case "3":
                    System.out.println("Broken ppm readings:\n"
                            + String.join("", databaseInfo.findBrokenReadings()));
                    break;
                case "4":
                    System.out.println("Enter meter name: ");
                    findMeter(scanner.nextLine(), scanner);
                    break;
                case "5":
                    return;
                default:
                    System.out.println("Please enter 1, 2, 3, 4, or 5.");
                    break;
            }
        }
    }

    private static String promptChoice(Scanner scanner) {
        System.out.println("Search Database: ");
        System.out.println("1. Find all average readings");
        System.out.println("2. Find unhealthy readings");
        System.out.println("3. Find broken readings");
        System.out.println("4. Find individual meter's readings");
        System.out.println("5. Quit");

        return scanner.nextLine().trim();
    }

    /**
     * Gets a list of the MeterData in the MeterDatabase that match the input name and lets the user view one.
     *
     * @param name input meter name
     */
    private static void findMeter(String name, Scanner scanner) {
        List<MeterData> meterDataList = databaseInfo.findMeters(name);

        System.out.println("Found " + meterDataList.size() + " meter(s) matching \"" + name + "\".");
        if (meterDataList.size() == 0) {
            scanner.nextLine();
            return;
        }

        for (int i = 0, size = meterDataList.size(); i < size; i++) {
            System.out.println((i + 1) + ". " + meterDataList.get(i).getMeterName());
        }
        int meterIndex = selectMeter(meterDataList.size(), scanner);
        System.out.println(meterDataList.get(meterIndex));
    }

    /**
     * Prompts the user to select a MeterData.
     *
     * @param listSize size of list
     * @return int representing index of selected MeterData
     */
    private static int selectMeter(int listSize, Scanner scanner) {
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
