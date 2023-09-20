package mech.mania.engine;

import mech.mania.engine.character.CharacterClassData;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.MoveAction;
import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.DoNothingPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mech.mania.engine.TestingUtils.assertListContentsEqual;

public class GetPossibleMoveActionsTest {
    private final Position center = new Position(2,2);
    private final Position left1 = new Position(center.getX() - 1, center.getY());
    private final Position right1 = new Position(center.getX() + 1, center.getY());
    private final List<List<Character>> map = List.of(
            "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "teeet".chars().mapToObj(ch -> (char) ch).toList(),
            "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "ttttt".chars().mapToObj(ch -> (char) ch).toList()
    );
    private GameState game;
    private CharacterState human;
    private CharacterState zombie;

    @BeforeEach
    public void setUp() {
        Player humanPlayer = new DoNothingPlayer(false);
        Player zombiePlayer = new DoNothingPlayer(true);

        game = new GameState(humanPlayer, zombiePlayer, map);

        // remove all characters
        game.getCharacterStates().clear();

        // Add our defined human & zombie
        human = new CharacterState("123", center, false, CharacterClassType.NORMAL);
        zombie = new CharacterState("456", center, true, CharacterClassType.ZOMBIE);

        game.getCharacterStates().put(human.getId(), human);
        game.getCharacterStates().put(zombie.getId(), zombie);
    }

    @Test
    public void range0Human() {
        List<MoveAction> moveExpected = List.of(
                new MoveAction(human.getId(), center)
        );
        human.applyClassData(new CharacterClassData(1, 0, 1, 0, List.of()));
        List<MoveAction> moveHuman = game.getPossibleMoveActions(false).get(human.getId());
        assertListContentsEqual(moveExpected, moveHuman);
    }

    @Test
    public void range0Zombie() {
        List<MoveAction> moveExpected = List.of(
                new MoveAction(zombie.getId(), center)
        );
        zombie.applyClassData(new CharacterClassData(1, 0, 1, 0, List.of()));
        List<MoveAction> move = game.getPossibleMoveActions(true).get(zombie.getId());
        assertListContentsEqual(moveExpected, move);
    }

    @Test
    public void range1PlusHuman() {
        List<MoveAction> moveExpected = List.of(
                new MoveAction(human.getId(), center),
                new MoveAction(human.getId(), left1),
                new MoveAction(human.getId(), right1)
        );
        human.applyClassData(new CharacterClassData(1, 1, 1, 0, List.of()));
        List<MoveAction> move1 = game.getPossibleMoveActions(false).get(human.getId());
        human.applyClassData(new CharacterClassData(1, 2, 1, 0, List.of()));
        List<MoveAction> move2 = game.getPossibleMoveActions(false).get(human.getId());
        human.applyClassData(new CharacterClassData(1, 3, 1, 0, List.of()));
        List<MoveAction> move3 = game.getPossibleMoveActions(false).get(human.getId());

        assertListContentsEqual(moveExpected, move1);
        assertListContentsEqual(moveExpected, move2);
        assertListContentsEqual(moveExpected, move3);
    }

    @Test
    public void range1PlusZombie() {
        List<MoveAction> moveExpected = List.of(
                new MoveAction(zombie.getId(), center),
                new MoveAction(zombie.getId(), left1),
                new MoveAction(zombie.getId(), right1)
        );
        zombie.applyClassData(new CharacterClassData(1, 1, 1, 0, List.of()));
        List<MoveAction> move1 = game.getPossibleMoveActions(true).get(zombie.getId());
        zombie.applyClassData(new CharacterClassData(1, 2, 1, 0, List.of()));
        List<MoveAction> move2 = game.getPossibleMoveActions(true).get(zombie.getId());
        zombie.applyClassData(new CharacterClassData(1, 3, 1, 0, List.of()));
        List<MoveAction> move3 = game.getPossibleMoveActions(true).get(zombie.getId());

        assertListContentsEqual(moveExpected, move1);
        assertListContentsEqual(moveExpected, move2);
        assertListContentsEqual(moveExpected, move3);
    }
}
