import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Main class for part2. Reads CO2 data from csv and allows the user to find information.
 */
public class CO2Info {
    private static Scanner scanner;
    private static MeterDatabase meterDatabase;
    private static Path csvPath;

    /**
     * CO2Reader main method.
     *
     * @param args CSV file to read from
     */
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        csvPath = findValidPath(args);

        addReadings();
        System.out.println("Successfully loaded " + meterDatabase.size() + " meters.");

//        System.out.println(meterDatabase);
        showMenu();
    }

    /**
     * Returns a valid file path from command-line argument or user input.
     *
     * @param args command-line argument
     * @return valid file path
     */
    private static Path findValidPath(String[] args) {
        boolean validArg = true;
        Path path;

        while (true) {
            if (validArg && args.length > 0) {
                path = Paths.get(args[0]);
            } else {
                System.out.print("\nEnter input file name: ");
                path = Paths.get(scanner.nextLine().trim());
            }

            if (Files.exists(path)) {
                return path;
            } else {
                System.out.print("File does not exist!");
                validArg = false;
            }
        }
    }

    /**
     * Iterates through the CSV file and adds Readings to the MeterReadings of the MeterDatabase.
     */
    private static void addReadings() {
        try (CSVReader csvReader = new CSVReader(new FileReader(csvPath.toFile()))) {
            meterDatabase = initializeMeterDatabase(csvReader);

            System.out.println("Reading CSV...");
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                LocalDateTime localDateTime = parseTime(line[0]);

                String[] ppmEntries = new String[line.length - 1];
                System.arraycopy(line, 1, ppmEntries, 0, ppmEntries.length);

                for (int i = 0, length = ppmEntries.length; i < length; i++) {
                    if (!ppmEntries[i].equals("")) {
                        meterDatabase.get(i).add(new Reading(localDateTime, Double.parseDouble(ppmEntries[i])));
                    }
                }
            }
        } catch (IOException | CsvException e) {
            System.out.println("An exception occurred when reading the csv file!");
            e.printStackTrace();
            System.exit(-2);
        }
    }

    /**
     * Gets meter names from headers and returns a MeterDatabase from them.
     *
     * @return MeterDatabase with meters from header names (skipping temperature column)
     */
    private static MeterDatabase initializeMeterDatabase(CSVReader csvReader) {
        String[] meterNames = {};
        try {
            meterNames = csvReader.readNext();
            System.arraycopy(meterNames, 1, meterNames, 0, meterNames.length - 1);
        } catch (CsvValidationException | IOException e) {
            System.out.println("An exception occurred when initializing the Meter Database!");
            e.printStackTrace();
            System.exit(-1);
        }
        return new MeterDatabase(meterNames);
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
     * Shows the CO2Reader menu.
     */
    private static void showMenu() {
        while (true) {
            System.out.println("Search Database:");
            System.out.println("1. Find all average readings");
            System.out.println("2. Find unhealthy readings");
            System.out.println("3. Find broken readings");
            System.out.println("4. Find individual meter's readings");
            System.out.println("5. Quit");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.println(meterDatabase.toStringAverages());
                    break;
                case 2:
                    System.out.println("Unhealthy ppm readings:\n"
                            + String.join("", meterDatabase.findUnhealthyReadings()));
                    break;
                case 3:
                    System.out.println("Broken ppm readings:\n"
                            + String.join("", meterDatabase.findBrokenReadings()));
                    break;
                case 4:
                    System.out.println("Which meter?");
                    String answer = scanner.nextLine();
                    List<MeterReadings> meterReadingsList = meterDatabase.matchMeterName(answer);

                    if (meterReadingsList.size() > 0) {
                        System.out.println("Found " + meterReadingsList.size() + " meters.");
                        for (int i = 0, size = meterReadingsList.size(); i < size; i++) {
                            System.out.println((i + 1) + ". " + meterReadingsList.get(i).getMeterName());
                        }
                        int meterChoice = scanner.nextInt();
                        scanner.nextLine();
                        System.out.println(meterReadingsList.get(meterChoice - 1));
                    }
                    else {
                        System.out.println("Meter not found!");
                    }
                    break;
                case 5:
                    return;
            }
        }
    }
}
