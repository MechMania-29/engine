package mech.mania.engine.util;
import mech.mania.engine.character.CharacterClassType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        Map<CharacterClassType, Integer> classMap = Map.of(
                CharacterClassType.DEMOLITIONIST, 2,
                CharacterClassType.MEDIC, 3,
                CharacterClassType.MARKSMAN, 1,
                CharacterClassType.NORMAL, 4
        );

        List<CharacterClassType> classList = SpreadMap.spread(classMap);
        assertEquals("[NORMAL, MARKSMAN, DEMOLITIONIST, MEDIC, NORMAL, DEMOLITIONIST, MEDIC, NORMAL, MEDIC, NORMAL]", classList.toString());
    }
}
