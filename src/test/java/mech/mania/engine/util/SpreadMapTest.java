package mech.mania.engine.util;
import mech.mania.engine.character.CharacterClassType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SpreadMapTest {
    @Test
    public void testSpread() {
        Map<String, Integer> stringMap = new HashMap<>();
        stringMap.put("a", 3);
        stringMap.put("b", 4);
        stringMap.put("c", 1);

        List<String> stringList = SpreadMap.spread(stringMap);
        assertEquals("[a, b, c, a, b, a, b, b]", stringList.toString());

        Map<Integer, Integer> integerMap = new HashMap<>();
        integerMap.put(1, 2);
        integerMap.put(2, 3);
        integerMap.put(3, 1);

        List<Integer> integerList = SpreadMap.spread(integerMap);
        assertEquals("[1, 2, 3, 1, 2, 2]", integerList.toString());

        Map<CharacterClassType, Integer> classMap = new HashMap<>();
        classMap.put(CharacterClassType.DEMOLITIONIST, 2);
        classMap.put(CharacterClassType.MEDIC, 3);
        classMap.put(CharacterClassType.MARKSMAN, 1);
        classMap.put(CharacterClassType.NORMAL, 4);

        List<CharacterClassType> classList = SpreadMap.spread(classMap);
        assertEquals(
                classList.stream().filter(classType -> classType == CharacterClassType.DEMOLITIONIST).count(), 2
        );
        assertEquals(
                classList.stream().filter(classType -> classType == CharacterClassType.MEDIC).count(), 3
        );
        assertEquals(
                classList.stream().filter(classType -> classType == CharacterClassType.MARKSMAN).count(), 1
        );
        assertEquals(
                classList.stream().filter(classType -> classType == CharacterClassType.NORMAL).count(), 4
        );
        assertNotEquals(classList.get(0), classList.get(1));
        assertNotEquals(classList.get(1), classList.get(2));
        assertNotEquals(classList.get(2), classList.get(3));
    }
}
