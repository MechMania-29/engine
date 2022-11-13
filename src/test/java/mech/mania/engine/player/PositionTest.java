package mech.mania.engine.player;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PositionTest {
    @Test
    public void BaseTest() {
        Position pos = new Position(1, 2);

        Assertions.assertEquals(pos.getX(), 1);
        Assertions.assertEquals(pos.getY(), 2);

        pos.setX(5);
        pos.setY(3);

        Assertions.assertEquals(pos.getX(), 5);
        Assertions.assertEquals(pos.getY(), 3);
    }
}
