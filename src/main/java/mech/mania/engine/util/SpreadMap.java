package mech.mania.engine.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpreadMap {
    public static <T> List<T> spread(Map<T, Integer> map) {
        HashMap<T, Integer> hashMap = new HashMap<>(map);
        ArrayList<T> arrayList = new ArrayList<>();

        while (hashMap.values().stream().anyMatch(count -> count > 0)) {
            for (Map.Entry<T, Integer> entry : hashMap.entrySet()) {
                T key = entry.getKey();
                Integer value = entry.getValue();
                if (value > 0) {
                    arrayList.add(key);
                    entry.setValue(value - 1);
                }
            }
        }

        return arrayList;
    }
}
