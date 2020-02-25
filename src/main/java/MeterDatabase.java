import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class that represents a database of multiple CO2 meters' readings.
 */
public class MeterDatabase extends ArrayList<MeterReadings> {
    public MeterDatabase() {
        super();
    }

    public MeterDatabase(List<MeterReadings> meterReadingsList) {
        super(meterReadingsList);
    }

    public MeterDatabase(String[] meterNames) {
        super(meterNames.length);
        for (String meterName : meterNames) {
            meterName = meterName.replaceAll("\\s+CO2", "");
            this.add(new MeterReadings(meterName));
        }
    }

    ArrayList<String> findBrokenReadings() {
        ArrayList<String> brokenReadings = new ArrayList<>();

        for (MeterReadings meterReadings : this) {
            brokenReadings.add(meterReadings.toStringBrokenReadings());
        }
        return brokenReadings;
    }

    ArrayList<String> findUnhealthyReadings() {
        ArrayList<String> unhealthyReadings = new ArrayList<>();

        for (MeterReadings meterReadings : this) {
            unhealthyReadings.add(meterReadings.toStringUnhealthyReadings());
        }
        return unhealthyReadings;
    }

    public List<MeterReadings> matchMeterName(String name) {
        return this.stream()
                .filter(meterReadings -> meterReadings.getMeterName().contains(name))
                .collect(Collectors.toList());
    }

    public String toStringAverages() {
        StringBuilder stringBuilder = new StringBuilder("Meter Database\n");

        for (MeterReadings meterReadings : this) {
            stringBuilder.append(meterReadings.toStringAverage()).append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("Meter Database\n");

        for (MeterReadings meterReadings : this) {
            stringBuilder.append(meterReadings).append("\n");
        }
        return stringBuilder.toString();
    }
}
