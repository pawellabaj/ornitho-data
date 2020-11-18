package pl.com.labaj.ornitho.grid.core;

import org.jsoup.nodes.Document;

import java.util.function.Function;

@FunctionalInterface
public interface Analyzer<R> extends Function<Document, R> {
    R apply(Document document);
}
