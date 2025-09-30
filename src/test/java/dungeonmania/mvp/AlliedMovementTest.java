package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlliedMovementTest {
    @Test
    @DisplayName("Test the ally follows")
    public void testAllyFollows() {
        // Mercenary Path
        //
        // M1       M2      M3      M4      M5      Wall    M9      P10
        //                                  M6      M7      M8
        // Player Path
        //
        //                  P1      P2/3    P4      Wall    P8      P9      P10
        //                                  P5      P6      P7

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_follows", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(1, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(new Position(5, 1), getPlayerPos(res));

        // move down around wall
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(5, 2), getPlayerPos(res));

        // move right around wall
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));

        // move right around wall
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 2), getMercPos(res));
        assertEquals(new Position(7, 2), getPlayerPos(res));

        // move up around wall
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(7, 2), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(new Position(8, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(8, 1), getMercPos(res));
        assertEquals(new Position(9, 1), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test the ally doesn't go into space")
    public void testAllyDistantSquare() {
        // Mercenary Path
        //
        // M1       M2      M3-6/P1 P2-6  Wall
        //

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_distinctSquares", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(1, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move right - no player movement
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move right - no player and merc movement
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move right - no player and merc movement
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test the ally follows and check distinct squares")
    public void testAllyFollowsAndDistinctSquares() {
        // Mercenary Path
        //
        // M1       M2      M3      M4      M5      Wall    M9/10   P11
        //                                  M6      M7      M8
        // Player Path
        //
        //                  P1      P2/3    P4      Wall    P8/9    P10     P11
        //                                  P5      P6      P7

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_follows", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(1, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getMercPos(res));
        assertEquals(new Position(5, 1), getPlayerPos(res));

        // move down around wall
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(5, 2), getPlayerPos(res));

        // move right around wall
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));

        // move right around wall
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 2), getMercPos(res));
        assertEquals(new Position(7, 2), getPlayerPos(res));

        // move up around wall
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(7, 2), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // bump into wall - no change in position for merc and player
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(7, 2), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(new Position(8, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(8, 1), getMercPos(res));
        assertEquals(new Position(9, 1), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test the ally uses Dijkstra to get to player v1")
    public void testAllyJumps() {
        //
        // P1       P2/3    P4      M3      M2      M1
        //


        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_jump", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(6, 2), getMercPos(res));
        assertEquals(new Position(1, 2), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 2), getMercPos(res));
        assertEquals(new Position(2, 2), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(4, 2), getMercPos(res));
        assertEquals(new Position(2, 2), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 2), getMercPos(res));
        assertEquals(new Position(3, 2), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test the ally uses Dijkstra to get to player v1")
    public void testAllyDijkstraV1() {
        //                  M4      M5      M6      Wall
        // M1       M2      M3      Wall    M7      Coin    P
        //                          Wall


        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_dijkstra_v1", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(1, 1), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 1), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 0), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 0), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 0), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test the ally uses Dijkstra to get to player v2")
    public void testAllyDijkstraV2() {
        // M4       M5      M6      M7
        // M3       Wall    Wall    Coin    P
        // M2       M1      Wall    Wall    Wall    Wall
        //
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_alliedMovementTest_dijkstra_v2", "c_alliedMovementTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        assertEquals(new Position(1, 2), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // pick up first treasure
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(0, 2), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(0, 1), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 0), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 0), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), getMercPos(res));
        assertEquals(new Position(4, 1), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 0), getMercPos(res));
        assertEquals(new Position(3, 1), getPlayerPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }
}
