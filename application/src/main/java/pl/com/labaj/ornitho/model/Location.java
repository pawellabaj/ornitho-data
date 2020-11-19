package pl.com.labaj.ornitho.model;

import com.grum.geocalc.BoundingArea;
import com.grum.geocalc.Point;
import lombok.Value;

@Value
public class Location {
    String name;
    Point center;
    BoundingArea boundingArea;
}
