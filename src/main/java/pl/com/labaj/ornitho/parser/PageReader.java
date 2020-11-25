package pl.com.labaj.ornitho.parser;

import pl.com.labaj.ornitho.io.PageLoader;

public class PageReader<R> {
    private final PageLoader pageLoader;
    private final DocumentParser<R> parser;

    public PageReader(PageLoader pageLoader, DocumentParser<R> parser) {
        this.pageLoader = pageLoader;
        this.parser = parser;
    }

    public R read(String pageUrl) {
        var document = pageLoader.load(pageUrl);
        return parser.parse(document);
    }
}
