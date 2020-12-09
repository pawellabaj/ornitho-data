package pl.com.labaj.ornitho.io.gpx;

import org.w3c.dom.Document;
import pl.com.labaj.ornitho.io.DocumentLoader;

public class GPXExtensions {

    private final Document lineExtensions;
    private final Document wayPointExtensions;

    public GPXExtensions(DocumentLoader documentLoader) {
        lineExtensions = documentLoader.loadDocument("gpx/line-extensions.xml");
        wayPointExtensions = documentLoader.loadDocument("gpx/way-point-extensions.xml");
    }

    public Document getLineExtensions() {
        return lineExtensions;
    }

    public Document getWaypointExtensions() {
        return wayPointExtensions;
    }
}
