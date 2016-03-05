package net.mkcz.kraken.api.request;

/**
 * Created by cintezam on 05/03/16.
 */
public enum OHLCInterval {
    MINUTE(1),
    FIVE_MINUTES(5),
    HALF_HOUR(30),
    HOUR(60),
    FOUR_HOURS(240),
    DAY(1440),
    WEEK(10080),
    FIFTEEN_DAYS(21600);

    private int durationInMinutes;

    OHLCInterval(final int durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public int getDuration() {
        return durationInMinutes;
    }
}
