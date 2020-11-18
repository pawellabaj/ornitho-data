package pl.com.labaj.ornitho.grid.model;

import io.jenetics.jpx.Point;
import io.jenetics.jpx.TrackSegment;
import lombok.Value;

@Value
public class Location {
    String name;
    Point center;
    TrackSegment segment;
}
