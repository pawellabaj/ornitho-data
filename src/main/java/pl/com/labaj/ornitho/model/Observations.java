package pl.com.labaj.ornitho.model;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class Observations {
    @NonNull
    String subject;

    String description;

    @NonNull
    DateRange dateRange;

    @NonNull
    List<Location> locations;
}
