package fi.pohina.vinkkilista.api;

import java.util.function.Consumer;
import java.util.function.Supplier;
import static org.junit.Assert.assertEquals;

class Utils {
    /**
     * Checks that the getter gets the same value which was set with the setter
     */
    public static <T> void testSetterAndGetter(
        T valueToSet,
        Consumer<T> setter,
        Supplier<T> getter) {
        setter.accept(valueToSet);

        T received = getter.get();

        assertEquals(valueToSet, received);
    }
}
