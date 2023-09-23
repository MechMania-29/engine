package mech.mania.engine;

import mech.mania.engine.player.ComputerPlayer;
import mech.mania.engine.player.Player;
import mech.mania.engine.util.Position;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static mech.mania.engine.Config.BOARD_SIZE;
import static mech.mania.engine.TestingUtils.assertMapsEqual;

public class GetTilesInRangeTest {
    @Test
    public void getTilesInRangeBasicTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState empty = new GameState(human, zombie, List.of());

        Position center = new Position(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Map<String, Position> range0Expected = Map.of(center.toString(), center);
        Map<String, Position> range0 = empty.getTilesInRange(center, 0, false, false, false);
        Map<String, Position> range0Attack = empty.getTilesInRange(center, 0, false, true, false);

        assertMapsEqual(range0Expected, range0);
        assertMapsEqual(range0Expected, range0Attack);

        Map<String, Position> range1Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range1 = empty.getTilesInRange(center, 1, false, false, false);
        Map<String, Position> range1Attack = empty.getTilesInRange(center, 1, false, true, false);

        assertMapsEqual(range1Expected, range1);
        assertMapsEqual(range1Expected, range1Attack);

        Map<String, Position> range2Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX() + 1, center.getY() + 1),
                new Position(center.getX() - 1, center.getY() - 1),
                new Position(center.getX() - 1, center.getY() + 1),
                new Position(center.getX() + 1, center.getY() - 1),
                new Position(center.getX() + 2, center.getY()),
                new Position(center.getX() - 2, center.getY()),
                new Position(center.getX(), center.getY() + 2),
                new Position(center.getX(), center.getY() - 2)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range2 = empty.getTilesInRange(center, 2, false, false, false);
        Map<String, Position> range2Attack = empty.getTilesInRange(center, 2, false, true, false);

        assertMapsEqual(range2Expected, range2);
        assertMapsEqual(range2Expected, range2Attack);
    }

    @Test
    public void getTilesInRangeDiagonalTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);
        GameState empty = new GameState(human, zombie, List.of());

        Position center = new Position(BOARD_SIZE / 2, BOARD_SIZE / 2);
        Map<String, Position> range0Expected = Map.of(center.toString(), center);
        Map<String, Position> range0 = empty.getTilesInRange(center, 0, true, false, false);

        assertMapsEqual(range0Expected, range0);

        Map<String, Position> range1Expected = Stream.of(
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY()),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX() + 1, center.getY() + 1),
                new Position(center.getX() - 1, center.getY() - 1),
                new Position(center.getX() - 1, center.getY() + 1),
                new Position(center.getX() + 1, center.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> range1 = empty.getTilesInRange(center, 1, true, false, false);

        assertMapsEqual(range1Expected, range1);
    }

    @Test
    public void getTilesInRangeTerrainTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);

        List<List<Character>> map = List.of(
                "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttett".chars().mapToObj(ch -> (char) ch).toList(),
                "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttttt".chars().mapToObj(ch -> (char) ch).toList()
        );

        GameState game = new GameState(human, zombie, map);

        Position middle = new Position(2, 2);
        Map<String, Position> rangeExpected = Map.of(middle.toString(), middle);
        Map<String, Position> range1 = game.getTilesInRange(middle, 1, false, false, false);
        Map<String, Position> range5 = game.getTilesInRange(middle, 5, false, false, false);

        assertMapsEqual(rangeExpected, range1);
        assertMapsEqual(rangeExpected, range5);

        Map<String, Position> rangeAttackExpected = Stream.of(
                middle,
                new Position(middle.getX() + 1, middle.getY()),
                new Position(middle.getX() - 1, middle.getY()),
                new Position(middle.getX(), middle.getY() + 1),
                new Position(middle.getX(), middle.getY() - 1)
        ).collect(Collectors.toMap(Position::toString, pos -> pos));
        Map<String, Position> rangeAttack1 = game.getTilesInRange(middle, 1, false, true, false);
        Map<String, Position> rangeAttack5 = game.getTilesInRange(middle, 5, false, true, false);

        assertMapsEqual(rangeAttackExpected, rangeAttack1);
        assertMapsEqual(rangeAttackExpected, rangeAttack5);
    }

    @Test
    public void getTilesInRangeTerrainBarricadesTest() {
        Player human = new ComputerPlayer(false);
        Player zombie = new ComputerPlayer(true);

        List<List<Character>> map = List.of(
                "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttbtt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttett".chars().mapToObj(ch -> (char) ch).toList(),
                "ttttt".chars().mapToObj(ch -> (char) ch).toList(),
                "ttttt".chars().mapToObj(ch -> (char) ch).toList()
        );

        GameState game = new GameState(human, zombie, map);

        Position middle = new Position(2, 2);
        Position up1 = new Position(middle.getX(), middle.getY() - 1);

        Map<String, Position> rangeExpected = Map.of(
                middle.toString(), middle,
                up1.toString(), up1
        );
        Map<String, Position> range1 = game.getTilesInRange(middle, 1, false, false, true);
        Map<String, Position> range5 = game.getTilesInRange(middle, 5, false, false, true);

        assertMapsEqual(rangeExpected, range1);
        assertMapsEqual(rangeExpected, range5);
    }
}
