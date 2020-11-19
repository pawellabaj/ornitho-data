package pl.com.labaj.ornitho.parser;

import pl.com.labaj.ornitho.io.page.PageLoader;

public class PageReader<R> {
    private final PageLoader pageLoader;
    private final DocumentParser<R> parser;

    public PageReader(PageLoader pageLoader, DocumentParser<R> parser) {
        this.pageLoader = pageLoader;
        this.parser = parser;
    }

    public final R read(String pageUrl) {
        var document = pageLoader.load(pageUrl);
        return parser.parse(document);
    }
}
