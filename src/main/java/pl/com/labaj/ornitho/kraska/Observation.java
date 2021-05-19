package pl.com.labaj.ornitho.kraska;

import java.time.LocalDateTime;

class Observation {
    private final int id;
    private final double latitude;
    private final double longitude;
    private final LocalDateTime date;

    public Observation(int id, double latitude, double longitude, LocalDateTime date) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public LocalDateTime getDate() {
        return date;
    }
}
