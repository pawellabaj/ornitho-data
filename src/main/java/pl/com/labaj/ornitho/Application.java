package pl.com.labaj.ornitho;

import pl.com.labaj.ornitho.builder.gpx.GPXBuilder;
import pl.com.labaj.ornitho.io.FileUtils;
import pl.com.labaj.ornitho.io.gpx.GPXWriter;
import pl.com.labaj.ornitho.io.page.PageLoader;
import pl.com.labaj.ornitho.model.Observations;
import pl.com.labaj.ornitho.parser.LocationParser;
import pl.com.labaj.ornitho.parser.ObservationsParser;
import pl.com.labaj.ornitho.parser.PageReader;
import pl.com.labaj.ornitho.parser.PositionParser;
import pl.com.labaj.ornitho.parser.SubjectParser;

public class Application {

    private final PageReader<Observations> observationsReader;
    private final GPXBuilder gpxBuilder;
    private final FileUtils fileUtils;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("ornitho search results page URL is mandatory");
        }

        var pageLoader = new PageLoader();

        var positionParser = new PositionParser();
        var locationParser = new LocationParser(positionParser);

        var subjectParser = new SubjectParser();
        var gpxWriter = new GPXWriter();
        var locationReader = new PageReader<>(pageLoader, locationParser);

        var observationsParser = new ObservationsParser(locationReader, subjectParser);
        var observationsReader = new PageReader<>(pageLoader, observationsParser);

        var fileUtils = new FileUtils();
        var gpxBuilder = new GPXBuilder(fileUtils, gpxWriter);

        var application = new Application(observationsReader, fileUtils, gpxBuilder);

        application.run(args[0]);
    }

    public Application(PageReader<Observations> observationsReader, FileUtils fileUtils, GPXBuilder gpxBuilder) {
        this.observationsReader = observationsReader;
        this.gpxBuilder = gpxBuilder;
        this.fileUtils = fileUtils;
    }

    private void run(String pageUrl) {
        var observations = observationsReader.read(pageUrl);
        var path = fileUtils.getPath(observations, "gpx");
        gpxBuilder.buildAndSave(observations, path);
    }
}
