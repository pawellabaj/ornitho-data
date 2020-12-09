package pl.com.labaj.ornitho.util;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class Assertions {
    private Assertions(){}

    public static <T> Consumer<List<? extends T>> listAssertions(BiConsumer<T, Integer> itemAtIndexConsumer) {
        return items -> IntStream.range(0, items.size())
                .forEach(atIndex -> {
                    T actual = items.get(atIndex);
                    Consumer<T> itemConsumer = item -> itemAtIndexConsumer.accept(item, atIndex);
                    assertThat(actual).satisfies(itemConsumer);
                });
    }
}
