import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class that represents one CO2 reading.
 */
public class Reading implements Serializable {
    public enum Warning {
        CRITICAL {
            @Override
            public String toString() {
                return "[CRITICAL]";
            }
        },
        VERY_POOR {
            @Override
            public String toString() {
                return "[VERY POOR]";
            }
        },
        POOR() {
            @Override
            public String toString() {
                return "[POOR]";
            }
        },
        NORMAL() {
            @Override
            public String toString() {
                return "";
            }
        },
        BROKEN() {
            @Override
            public String toString() {
                return "[BROKEN]";
            }
        }
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    private LocalDateTime localDateTime;
    private double ppm;
    private Warning warning;

    public Reading() {
    }

    public Reading(LocalDateTime localDateTime, double ppm) {
        this.localDateTime = localDateTime;
        this.ppm = ppm;

        if (ppm >= 5000) {
            warning = Warning.CRITICAL;
        } else if (ppm >= 2000) {
            warning = Warning.VERY_POOR;
        } else if (ppm >= 1000) {
            warning = Warning.POOR;
        } else if (ppm == 0) {
            warning = Warning.BROKEN;
        } else {
            warning = Warning.NORMAL;
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

    public Warning getWarning() {
        return warning;
    }

    public void setWarning(Warning warning) {
        this.warning = warning;
    }

    @Override
    public String toString() {
        return localDateTime.format(formatter) + " - " + ppm + "ppm " + warning;
    }
}
