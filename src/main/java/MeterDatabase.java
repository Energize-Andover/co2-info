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

    ArrayList<String> findBrokenReadings() {
        ArrayList<String> brokenReadings = new ArrayList<>();

        for (MeterData meterData : this) {
            brokenReadings.add(meterData.toStringBrokenReadings());
        }
        return brokenReadings;
    }

    ArrayList<String> findUnhealthyReadings() {
        ArrayList<String> unhealthyReadings = new ArrayList<>();

        for (MeterData meterData : this) {
            unhealthyReadings.add(meterData.toStringUnhealthyReadings());
        }
        return unhealthyReadings;
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
