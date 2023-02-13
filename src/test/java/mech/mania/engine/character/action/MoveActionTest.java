package mech.mania.engine.character.action;

import mech.mania.engine.util.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MoveActionTest {
    @Test
    public void BaseTest() {
        MoveAction action = new MoveAction("0", new Position(132, 13));

        Assertions.assertEquals(action.getExecutingCharacterId(), "0");
        Assertions.assertEquals(action.getDestination(), new Position(132, 13));

        System.out.println(action.getExecutingCharacterId() + " moving to " + action.getDestination());

    }
}
