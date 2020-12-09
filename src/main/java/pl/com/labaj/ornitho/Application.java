package pl.com.labaj.ornitho;

import pl.com.labaj.ornitho.builder.gpx.GPXBuilder;
import pl.com.labaj.ornitho.io.DocumentLoader;
import pl.com.labaj.ornitho.io.PageLoader;
import pl.com.labaj.ornitho.io.PathGenerator;
import pl.com.labaj.ornitho.io.gpx.GPXExtensions;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;
import pl.com.labaj.ornitho.model.Observations;
import pl.com.labaj.ornitho.parser.DateRangeParser;
import pl.com.labaj.ornitho.parser.LocationParser;
import pl.com.labaj.ornitho.parser.ObservationsParser;
import pl.com.labaj.ornitho.parser.PageReader;
import pl.com.labaj.ornitho.parser.PositionParser;
import pl.com.labaj.ornitho.parser.SubjectParser;

public class Application {

    private final PageReader<Observations> observationsReader;
    private final GPXBuilder gpxBuilder;
    private final PathGenerator pathGenerator;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("ornitho search results page URL is mandatory");
        }

        var pageLoader = new PageLoader();

        var positionParser = new PositionParser();
        var locationParser = new LocationParser(positionParser);
        var subjectParser = new SubjectParser();
        var dateRangeParser = new DateRangeParser();

        var locationReader = new PageReader<>(pageLoader, locationParser);

        var observationsParser = new ObservationsParser(locationReader, subjectParser, dateRangeParser);
        var observationsReader = new PageReader<>(pageLoader, observationsParser);

        var documentLoader = new DocumentLoader();
        var gpxFileUtils = new GPXExtensions(documentLoader);
        var gpxWriter = new GPXWriter();
        var gpxBuilder = new GPXBuilder(gpxFileUtils, gpxWriter);

        var pathGenerator = new PathGenerator();

        var application = new Application(observationsReader, pathGenerator, gpxBuilder);

        application.run(args[0]);
    }

    public Application(PageReader<Observations> observationsReader, PathGenerator pathGenerator, GPXBuilder gpxBuilder) {
        this.observationsReader = observationsReader;
        this.gpxBuilder = gpxBuilder;
        this.pathGenerator = pathGenerator;
    }

    void run(String pageUrl) {
        var observations = observationsReader.read(pageUrl);
        var path = pathGenerator.getPath(observations, "gpx");
        gpxBuilder.buildAndSave(observations, path);
    }
}
