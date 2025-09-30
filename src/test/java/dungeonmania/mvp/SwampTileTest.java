package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SwampTileTest {
    @Test
    @DisplayName("Basic stuck test with movement factor of 2")
    public void testBasicStuck() {
        // ST
        // S
        // M        ST              P

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTest_basic", "c_swampTest_basic");

        // check starting position
        // swamp tiles - (1,1), (2,3)
        assertEquals(new Position(1, 3), getMercPos(res));
        assertEquals(new Position(1, 2), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));

        // move right - enemies move onto swamp tiles
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getMercPos(res));
        assertEquals(new Position(1, 1), getSpiderPos(res));
        assertEquals(new Position(5, 3), getPlayerPos(res));

        // move left - stuck tick 1
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 3), getMercPos(res));
        assertEquals(new Position(1, 1), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));

        // move right - stuck tick 2
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getMercPos(res));
        assertEquals(new Position(1, 1), getSpiderPos(res));
        assertEquals(new Position(5, 3), getPlayerPos(res));

        // move left - free
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 3), getMercPos(res));
        assertEquals(new Position(2, 1), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));
    }

    @Test
    @DisplayName("Basic stuck test with movement factor of 0")
    public void testNoStuck() {
        // ST
        // S
        // M        ST              P

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTest_none", "c_swampTest_basic");

        // check starting position
        // swamp tiles - (1,1), (2,3)
        assertEquals(new Position(1, 3), getMercPos(res));
        assertEquals(new Position(1, 2), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));

        // move right - enemies move onto swamp tiles
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getMercPos(res));
        assertEquals(new Position(1, 1), getSpiderPos(res));
        assertEquals(new Position(5, 3), getPlayerPos(res));

        // move left - enemies move past swamp
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 3), getMercPos(res));
        assertEquals(new Position(2, 1), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));
    }

    @Test
    @DisplayName("Basic stuck test with movement factor of 100")
    public void testLongStuck() {
        // ST
        // S
        // M        ST              P

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTest_long", "c_swampTest_basic");

        // check starting position
        // swamp tiles - (1,1), (2,3)
        assertEquals(new Position(1, 3), getMercPos(res));
        assertEquals(new Position(1, 2), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));

        // move right - enemies move onto swamp tiles
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getMercPos(res));
        assertEquals(new Position(1, 1), getSpiderPos(res));
        assertEquals(new Position(5, 3), getPlayerPos(res));

        for (int i = 0; i < 50; i++) {
            // move left - stuck tick 1
            res = dmc.tick(Direction.LEFT);
            assertEquals(new Position(2, 3), getMercPos(res));
            assertEquals(new Position(1, 1), getSpiderPos(res));
            assertEquals(new Position(4, 3), getPlayerPos(res));

            // move right - stuck tick 2
            res = dmc.tick(Direction.RIGHT);
            assertEquals(new Position(2, 3), getMercPos(res));
            assertEquals(new Position(1, 1), getSpiderPos(res));
            assertEquals(new Position(5, 3), getPlayerPos(res));
        }

        // move left - free
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(3, 3), getMercPos(res));
        assertEquals(new Position(2, 1), getSpiderPos(res));
        assertEquals(new Position(4, 3), getPlayerPos(res));
    }

    @Test
    @DisplayName("Basic player and ally unimpeded by swamp tile")
    public void testPlayerAndAlly() {
        //                                  ST      ST
        // M                T       P       ST      ST
        //

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTest_playerAndAllies", "c_swampTest_basic");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        // swamp tiles - (6,1), (7,1), (6,2), (7,3)
        assertEquals(new Position(1, 2), getMercPos(res));
        assertEquals(new Position(5, 2), getPlayerPos(res));

        // move left - into treasure tile
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 2), getMercPos(res));
        assertEquals(new Position(4, 2), getPlayerPos(res));

        // bribe mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 2), getMercPos(res));
        assertEquals(new Position(4, 2), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 2), getMercPos(res));
        assertEquals(new Position(5, 2), getPlayerPos(res));

        // player enters swamp tile unimpeded
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));

        // player and ally enter swamp tile unimpeded
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 2), getMercPos(res));
        assertEquals(new Position(7, 2), getPlayerPos(res));

        // player and ally enter swamp tile unimpeded
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(7, 2), getMercPos(res));
        assertEquals(new Position(7, 1), getPlayerPos(res));


        // player and ally enter swamp tile unimpeded
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(7, 1), getMercPos(res));
        assertEquals(new Position(6, 1), getPlayerPos(res));

        // player and ally enter swamp tile unimpeded
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(6, 1), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));
    }

    @Test
    @DisplayName("Player saves ally stuck in swamp and they follow")
    public void testPlayerSavesAlly() {
        //
        // M        ST              T       P
        //

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTest_playerSavesAlly", "c_swampTest_basic");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // check starting position
        // swamp tiles - (4,2)
        assertEquals(new Position(1, 2), getMercPos(res));
        assertEquals(new Position(9, 2), getPlayerPos(res));

        // move left - into treasure tile
        res = dmc.tick(Direction.LEFT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 2), getMercPos(res));
        assertEquals(new Position(8, 2), getPlayerPos(res));

        // bribe mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 2), getMercPos(res));
        assertEquals(new Position(8, 2), getPlayerPos(res));

        // move left -  ally moves into swamp
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 2), getMercPos(res));
        assertEquals(new Position(7, 2), getPlayerPos(res));

        // move left -  ally stuck in swamp
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(4, 2), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));

        // move left -  ally freed, player approaches
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(6, 2), getMercPos(res));
        assertEquals(new Position(5, 2), getPlayerPos(res));

        // move right -  ally free to move around
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 2), getMercPos(res));
        assertEquals(new Position(6, 2), getPlayerPos(res));
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position getSpiderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "spider").get(0).getPosition();
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }
}
