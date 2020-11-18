package pl.com.labaj.ornitho.model;

import lombok.Value;

import java.util.List;

@Value
public class Observations {
    String specie;
    DateRange dateRange;
    List<Location> locations;
}
