package pl.com.labaj.ornitho.parser;

public class SubjectParser {
    String parse(String text) {
        if (text.contains(":")) {
            return text.split(":")[1].trim();
        }
        if (text.contains("(")) {
            return text.split("\\(")[0].trim();
        }
        return text;
    }
}
