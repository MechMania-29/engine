package mech.mania.engine;

import mech.mania.engine.character.CharacterClassAbility;
import mech.mania.engine.character.CharacterClassData;
import mech.mania.engine.character.CharacterClassType;
import mech.mania.engine.character.CharacterState;
import mech.mania.engine.character.action.AbilityAction;
import mech.mania.engine.character.action.AbilityActionType;
import mech.mania.engine.character.action.AttackAction;
import mech.mania.engine.character.action.AttackActionType;
import mech.mania.engine.player.DoNothingPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.terrain.TerrainData;
import mech.mania.engine.terrain.TerrainState;
import mech.mania.engine.terrain.TerrainType;
import mech.mania.engine.util.Position;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static mech.mania.engine.Config.TERRAIN_DATAS;
import static mech.mania.engine.TestingUtils.assertListContentsEqual;

public class GetPossibleAbilityActionsTest {
    private final Position center = new Position(2,2);
    private final Position left1 = new Position(center.getX() - 1, center.getY());
    private final Position right1 = new Position(center.getX() + 1, center.getY());
    private final Position left2 = new Position(center.getX() - 2, center.getY());
    private final Position right2 = new Position(center.getX() + 2, center.getY());
    private final List<List<Character>> map = List.of(
            "tttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "tttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "eeeeet".chars().mapToObj(ch -> (char) ch).toList(),
            "tttttt".chars().mapToObj(ch -> (char) ch).toList(),
            "tttttt".chars().mapToObj(ch -> (char) ch).toList()
    );
    private GameState game;
    private CharacterState human;
    private CharacterState human2;
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
        human2 = new CharacterState("234", right1, false, CharacterClassType.NORMAL);
        zombie = new CharacterState("456", center, true, CharacterClassType.ZOMBIE);

        game.getCharacterStates().put(human.getId(), human);
        game.getCharacterStates().put(human2.getId(), human2);
        game.getCharacterStates().put(zombie.getId(), zombie);

        // Add broken barricade
        TerrainState brokenBarricade = new TerrainState(left1.toString(), TERRAIN_DATAS.get(TerrainType.BARRICADE), left1);

        while (!brokenBarricade.isDestroyed()) {
            brokenBarricade.attack();
        }

        game.getTerrainStates().put(brokenBarricade.getId(), brokenBarricade);
    }

    @Test
    public void zombieHasNone() {
        List<AbilityAction> abilityExpected = List.of();
        zombie.applyClassData(new CharacterClassData(5, 5, 5, 0, List.of()));
        List<AbilityAction> ability = game.getPossibleAbilityActions(true).get(zombie.getId());
        assertListContentsEqual(abilityExpected, ability);
    }

    @Test
    public void range0() {
        List<AbilityAction> abilityExpected = List.of();
        human.applyClassData(new CharacterClassData(1, 0, 0, 0, List.of()));
        List<AbilityAction> ability = game.getPossibleAbilityActions(false).get(human.getId());
        assertListContentsEqual(abilityExpected, ability);
    }

    @Test
    public void range1() {
        List<AbilityAction> abilityExpected = List.of(
                new AbilityAction(human.getId(), AbilityActionType.HEAL, null, human2.getId())
        );
        human.applyClassData(
                new CharacterClassData(1, 0, 1, 0,
                        List.of(CharacterClassAbility.HEAL, CharacterClassAbility.BUILD_BARRICADE))
        );
        List<AbilityAction> ability = game.getPossibleAbilityActions(false).get(human.getId());
        assertListContentsEqual(abilityExpected, ability);
    }

    @Test
    public void range2Plus() {
        List<AbilityAction> abilityExpected = List.of(
                new AbilityAction(human.getId(), AbilityActionType.HEAL, null, human2.getId()),
                new AbilityAction(human.getId(), AbilityActionType.BUILD_BARRICADE, left2, null),
                new AbilityAction(human.getId(), AbilityActionType.BUILD_BARRICADE, right2, null)
        );
        human.applyClassData(
                new CharacterClassData(1, 0, 2, 0,
                        List.of(CharacterClassAbility.HEAL, CharacterClassAbility.BUILD_BARRICADE))
        );
        List<AbilityAction> ability2 = game.getPossibleAbilityActions(false).get(human.getId());
        human.applyClassData(
                new CharacterClassData(1, 0, 3, 0,
                        List.of(CharacterClassAbility.HEAL, CharacterClassAbility.BUILD_BARRICADE))
        );
        List<AbilityAction> ability3 = game.getPossibleAbilityActions(false).get(human.getId());
        human.applyClassData(
                new CharacterClassData(1, 0, 4, 0,
                        List.of(CharacterClassAbility.HEAL, CharacterClassAbility.BUILD_BARRICADE))
        );
        List<AbilityAction> ability4 = game.getPossibleAbilityActions(false).get(human.getId());

        assertListContentsEqual(abilityExpected, ability2);
        assertListContentsEqual(abilityExpected, ability3);
        assertListContentsEqual(abilityExpected, ability4);
    }
}
