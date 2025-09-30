package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class SceptreTest {
    @Test
    @DisplayName("Craft Test Basic recipe")
    public void testSceptreCraftBasic() {
        // pick up 1 wood, 1 treasure, 1 sunstone, test that a sceptre can be crafted,
        // inventory should be empty
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptretest_basiccraft", "c_sceptretest");

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // build sceptre, remove ingredients
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @DisplayName("Craft Test two sunstones")
    public void testSceptreCraftComplex() {
        // pick up 1 wood, 2 sunstone, test that a sceptre can be crafted, inventory
        // should contain one sunstone
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptretest_complexcraft", "c_sceptretest");

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "sunstone").size());

        // build sceptre, remove ingredients
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @DisplayName("Mind Control test allied")
    public void testSceptreMindControl() throws InvalidActionException {
        //
        // P1 P2/Wood P3/Treasure P4/Sunstone,P5/Craft,P6/Mind Control P7/M7 M6 M5 M4 M3
        // M2 M1
        //
        // craft a sceptre, mind control a mercenary and test no battle occurs
        // pick up 1 wood, 1 treasure, 1 sunstone, test that a sceptre can be crafted,
        // inventory should be empty
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptretest_mindcontrol", "c_sceptretest");

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // build sceptre, remove ingredients
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());

        // mind control mercenary and test a battle does not occur
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Mind Control test duration")
    public void testSceptreMindControlDuration() throws InvalidActionException {
        //
        // P1 P2/Wd P3/Trs,P7 P4/Snst,P5/Cft,P6/MndCtrl,P8 P9/M9 M8 M7 M6 M5 M4 M3 M2 M1
        //
        // craft a sceptre, mind control a mercenary and wait for the duration, test a
        // battle does occur
        // pick up 1 wood, 1 treasure, 1 sunstone, test that a sceptre can be crafted,
        // inventory should be empty
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptretest_mindcontrolduration", "c_sceptretest");

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // build sceptre, remove ingredients
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());

        // mind control mercenary and test a battle does occur after duration of 2 has
        // elapsed
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @DisplayName("Mind Control test out of range")
    public void testSceptreMindControlOutOfRange() throws InvalidActionException {
        //
        // P1 P2/Wood P3/Treasure, P4/Sunstone,P5/Craft,P6/Mind Control P7/Treasure
        // P8/M8 M7 M6 M5 M4 M3 M2 M1
        //
        // pick up some treasure and craft a sceptre, mind control a mercenary that is
        // out of range (1 in this test)
        // pick up 1 wood, 1 treasure, 1 sunstone, test that a sceptre can be crafted,
        // inventory should be empty
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptretest_mindcontrolrange", "c_sceptretest_range");

        // pick up wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up sunstone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        // build sceptre, remove ingredients
        res = assertDoesNotThrow(() -> dmc.build("sceptre"));
        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = dmc.interact(mercId);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
    }
}
