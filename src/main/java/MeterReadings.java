import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class that represents one CO2 meter's readings.
 */
public class MeterReadings extends ArrayList<Reading> implements Serializable {
    private String meterName;

    public MeterReadings() {
    }

    public MeterReadings(String meterName) {
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
