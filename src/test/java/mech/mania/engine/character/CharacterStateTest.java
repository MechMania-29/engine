package mech.mania.engine.character;

import mech.mania.engine.util.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CharacterStateTest {
    @Test
    public void BaseTest() {
        CharacterState state = new CharacterState("0", new Position(1, 2), false, CharacterClassType.NORMAL);

        Assertions.assertEquals(state.getId(), "0");
        Assertions.assertEquals(state.getPosition(), new Position(1, 2));
        Assertions.assertFalse(state.isZombie());

        state.makeZombie();

        Assertions.assertTrue(state.isZombie());

        CharacterState cloned = state.clone();
        state.setPosition(new Position(2, 4));
        Assertions.assertNotEquals(cloned.getPosition(), state.getPosition());
    }
}
