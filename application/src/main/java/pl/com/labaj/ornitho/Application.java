package pl.com.labaj.ornitho;

import pl.com.labaj.ornitho.core.Analyzer;
import pl.com.labaj.ornitho.core.GPXBuilder;
import pl.com.labaj.ornitho.core.LocationAnalyzer;
import pl.com.labaj.ornitho.core.ObservationsAnalyzer;
import pl.com.labaj.ornitho.core.PositionParser;
import pl.com.labaj.ornitho.io.GPXWriter;
import pl.com.labaj.ornitho.io.PageLoader;
import pl.com.labaj.ornitho.model.Observations;

public class Application {

    private final Analyzer<Observations> observationsAnalyzer;
    private final PageLoader pageLoader;
    private final GPXBuilder gpxBuilder;
    private final GPXWriter gpxWriter;

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("ornitho search results page URL is mandatory");
        }

        var pageLoader = new PageLoader();
        var positionParser = new PositionParser();
        var locationAnalyzer = new LocationAnalyzer(positionParser);
        var resultsAnalyzer = new ObservationsAnalyzer(locationAnalyzer, pageLoader);
        var gpxBuilder = new GPXBuilder();
        var gpxGenerator = new GPXWriter();

        var grid = new Application(pageLoader, resultsAnalyzer, gpxBuilder, gpxGenerator);

        grid.run(args[0]);
    }

    public Application(PageLoader pageLoader, Analyzer<Observations> observationsAnalyzer, GPXBuilder gpxBuilder, GPXWriter gpxWriter) {
        this.pageLoader = pageLoader;
        this.observationsAnalyzer = observationsAnalyzer;
        this.gpxBuilder = gpxBuilder;
        this.gpxWriter = gpxWriter;
    }

    private void run(String pageUrl) {
        var observationsDocument = pageLoader.load(pageUrl);
        var observations = observationsAnalyzer.apply(observationsDocument);

        var gpx = gpxBuilder.build(observations);
        gpxWriter.save(observations, gpx);
    }
}
