package mech.mania.engine.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PlayerStateTest {
    @Test
    public void BaseTest() {
        PlayerState state = new PlayerState(0, new Position(1, 2), false);

        Assertions.assertEquals(state.getId(), 0);
        Assertions.assertEquals(state.getPosition(), new Position(1, 2));
        Assertions.assertFalse(state.isZombie());

        state.makeZombie();

        Assertions.assertTrue(state.isZombie());
    }
}
