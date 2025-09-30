package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class SunstoneTest {
    @Test
    @DisplayName("Door Simple Test")
    public void testSunstoneSimpleDoor() {
        //
        //  P1  P2/Sunstone P3/Door
        //

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_doortest", "c_sunstone_doortest");

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // Walk through door (assert that player actually goes through and sunstone is not used)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Door Complex Test")
    public void testSunstoneComplexDoor() {
        //
        //  P1  P2/Sunstone P3/Key P4/Door
        //

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_doortestcomplex", "c_sunstone_doortest");

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // pick up key
        res = dmc.tick(Direction.RIGHT);
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Walk through door (assert that player actually goes through and both sunstone and key is not used)
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Crafting Test Simple")
    public void testSunstoneCraftingSimple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_build", "c_sunstone_build");

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // pick up pieces of wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        assertEquals(0, TestUtils.getInventory(res, "shield").size());
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @DisplayName("Crafting Test Complex")
    public void testSunstoneCraftingComplex() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_buildcomplex", "c_sunstone_build");

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // pick up pieces of wood
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // pick up a treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        assertEquals(0, TestUtils.getInventory(res, "shield").size());
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());

        // assert treasure has not been used in crafting since we have a sunstone
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Treasure Goal Test")
    public void testSunstoneTreasureGoal() {
        // Goal is 3 treasures
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_treasure", "c_sunstone_goal");

        // Pick up treasure and assert goal not met
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // Pick up treasure and assert goal not met
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // Pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Bribery Fail Test")
    public void testSunstoneBribery() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunstone_bribery", "c_sunstone_bribery");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // Pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
        dmc.interact(mercId)
        );
        // attempt bribe
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // walk into mercenary, a battle occurs
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());


    }
}
