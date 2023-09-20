package mech.mania.engine;

import mech.mania.engine.character.CharacterClassData;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.AttackActionType;
import mech.mania.engine.player.DoNothingPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mech.mania.engine.TestingUtils.assertListContentsEqual;

public class GetPossibleAttackActionsTest {
    private final Position center = new Position(2,2);
    private final Position up1 = new Position(center.getX(), center.getY() - 1);
    private final Position down1 = new Position(center.getX(), center.getY() + 1);
    private final Position up1left1 = new Position(center.getX() - 1, center.getY() - 1);
    private final Position up1right1 = new Position(center.getX() + 1, center.getY() - 1);
    private final Position down1left1 = new Position(center.getX() - 1, center.getY() + 1);
    private final Position down1right1 = new Position(center.getX() + 1, center.getY() + 1);
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
        List<AttackAction> attackExpected = List.of(
                new AttackAction(human.getId(), zombie.getId(), AttackActionType.CHARACTER)
        );
        human.applyClassData(new CharacterClassData(1, 1, 0, 0, List.of()));
        List<AttackAction> attack = game.getPossibleAttackActions(false).get(human.getId());
        assertListContentsEqual(attackExpected, attack);
    }

    @Test
    public void range0Zombie() {
        List<AttackAction> attackExpected = List.of(
                new AttackAction(zombie.getId(), human.getId(), AttackActionType.CHARACTER)
        );
        zombie.applyClassData(new CharacterClassData(1, 1, 0, 0, List.of()));
        List<AttackAction> attack = game.getPossibleAttackActions(true).get(zombie.getId());
        assertListContentsEqual(attackExpected, attack);
    }

    @Test
    public void range1Human() {
        List<AttackAction> attackExpected = List.of(
                new AttackAction(human.getId(), zombie.getId(), AttackActionType.CHARACTER),
                new AttackAction(human.getId(), up1.toString(), AttackActionType.TERRAIN),
                new AttackAction(human.getId(), down1.toString(), AttackActionType.TERRAIN)
        );
        human.applyClassData(new CharacterClassData(1, 1, 1, 0, List.of()));
        List<AttackAction> attack = game.getPossibleAttackActions(false).get(human.getId());
        assertListContentsEqual(attackExpected, attack);
    }

    @Test
    public void range1Zombie() {
        List<AttackAction> attackExpected = List.of(
                new AttackAction(zombie.getId(), human.getId(), AttackActionType.CHARACTER),
                new AttackAction(zombie.getId(), up1.toString(), AttackActionType.TERRAIN),
                new AttackAction(zombie.getId(), down1.toString(), AttackActionType.TERRAIN),
                new AttackAction(zombie.getId(), up1left1.toString(), AttackActionType.TERRAIN),
                new AttackAction(zombie.getId(), up1right1.toString(), AttackActionType.TERRAIN),
                new AttackAction(zombie.getId(), down1left1.toString(), AttackActionType.TERRAIN),
                new AttackAction(zombie.getId(), down1right1.toString(), AttackActionType.TERRAIN)
        );
        zombie.applyClassData(new CharacterClassData(1, 1, 1, 0, List.of()));
        List<AttackAction> attack = game.getPossibleAttackActions(true).get(zombie.getId());
        assertListContentsEqual(attackExpected, attack);
    }
}
