package mech.mania.engine;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestingUtils {
    public static <K, V> void assertMapsEqual(Map<K, V> expected, Map<K, V> got) {
        assertEquals(expected.size(), got.size(), String.format("Maps have different sizes: expected: %s, got: %s", expected, got));

        for (K key : expected.keySet()) {
            if (!got.keySet().contains(key)) {
                assertEquals(key, null, String.format("Got has missing key: expected: %s, got: %s", expected, got));
                return;
            }
            if (!got.get(key).equals(expected.get(key))) {
                assertEquals(got.get(key), expected.get(key), String.format("Got has incorrect value: expected: %s, got: %s", expected, got));
                return;
            }
        }

        for (K key : got.keySet()) {
            if (!expected.keySet().contains(key)) {
                assertEquals(null, key, String.format("Got has extra key: expected: %s, got: %s", expected, got));
                return;
            }
        }
    }

    public static <T> void assertListContentsEqual(List<T> expected, List<T> got) {
        assertEquals(expected.size(), got.size(), String.format("Lists have different sizes: expected: %s, got: %s", expected, got));

        for (T value : expected) {
            boolean exists = false;
            for (T value2 : got) {
                if (value2.equals(value)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                assertEquals(value, null, String.format("Got has missing value: expected: %s, got: %s", expected, got));
                return;
            }
        }


        for (T value : got) {
            boolean exists = false;
            for (T value2 : expected) {
                if (value2.equals(value)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                assertEquals(null, value, String.format("Got has extra value: expected: %s, got: %s", expected, got));
                return;
            }
        }
    }
}
