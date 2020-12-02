package pl.com.labaj.ornitho.parser;

import org.jsoup.nodes.Document;

public interface DocumentParser<T> {
    T parse(Document document);
}
