package pl.com.labaj.ornitho.util;

import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

public class DocumentTagNamesComparator implements Comparator<Node> {
    private static final DocumentTagNamesComparator INSTANCE = new DocumentTagNamesComparator();

    public static DocumentTagNamesComparator documentComparator() {
        return INSTANCE;
    }

    private DocumentTagNamesComparator() {}

    @Override
    public int compare(Node node1, Node node2) {
        if (!node1.getNodeName().equals(node2.getNodeName())) {
            return -1;
        }

        return compareChildren(getElementChildren(node1), getElementChildren(node2));
    }

    private int compareChildren(List<Node> children1, List<Node> children2) {
        if (children1.size() != children2.size()) {
            return -1;
        }

        for (int i = 0; i < children1.size(); i++) {
            var childrenCompareResult = compare(children1.get(i), children2.get(i));
            if (childrenCompareResult != 0) {
                return childrenCompareResult;
            }
        }

        return 0;
    }

    private List<Node> getElementChildren(Node node) {
        var children = node.getChildNodes();
        return IntStream.range(0, children.getLength())
                .mapToObj(children::item)
                .filter(not(Text.class::isInstance))
                .collect(toList());
    }
}
