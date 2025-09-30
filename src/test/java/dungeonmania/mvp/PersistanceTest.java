package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class PersistanceTest {

    @Test
    @DisplayName("Test a saved game is saved locally and can be loaded successfully")
    public void testSavedGameExists() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_movementTest_testMovementRight", "c_movementTest_testMovementRight");
        assertDoesNotThrow(() -> dmc.saveGame("save"));
        assertDoesNotThrow(() -> dmc.loadGame("save"));
        assertDoesNotThrow(() -> System.out.println(dmc.allGames()));
    }

    @Test
    @DisplayName("Test loading a save game that does not exist will raise an error")
    public void testInvalidLoad() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("NA"));
    }

    @Test
    @DisplayName("Test player location persistance")
    public void testPlayerLocationPersistance() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse initDungonRes = dmc.newGame(
            "d_movementTest_testMovementRight", "c_movementTest_testMovementRight");
        EntityResponse initPlayer = TestUtils.getPlayer(initDungonRes).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(),
        initPlayer.getType(), new Position(2, 1), false);

        // move player right
        DungeonResponse actualDungonRes = dmc.tick(Direction.RIGHT);
        actualDungonRes = assertDoesNotThrow(() -> dmc.saveGame("playerMove"));
        actualDungonRes = assertDoesNotThrow(() -> dmc.loadGame("playerMove"));
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @DisplayName("test battle stats persistnace")
    public void testPlayerStatsPersistance() {
        DungeonManiaController controller = new DungeonManiaController();
        DungeonResponse postBattleResponse = TestUtils.genericSpiderSequence(
                controller, "c_battleTests_basicSpiderSpiderDies");
        postBattleResponse = assertDoesNotThrow(() -> controller.saveGame("battle"));
        postBattleResponse = assertDoesNotThrow(() -> controller.loadGame("battle"));
        BattleResponse battle = postBattleResponse.getBattles().get(0);
        BattleTest b = new BattleTest();
        b.assertBattleCalculations(battle, true, "c_battleTests_basicSpiderSpiderDies", "spider");
    }

    @Test
    @DisplayName("Test inventory persistance")
    public void testInventoryPersistance() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invincibilityPotion", "c_potionsTest_invincibilityPotion");

        assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        // pick up invincibility potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());

        res = dmc.saveGame("items");
        res = dmc.loadGame("items");

        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());
        assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
    }

    @Test
    @DisplayName("Test crafting persistance")
    public void testCraftingPersistance() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_BuildablesTest_BuildBow", "c_BuildablesTest_BuildBow");

        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());

        // Pick up Wood
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "wood").size());

        // Pick up Arrow x3
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "arrow").size());

        // Build Bow
        assertEquals(0, TestUtils.getInventory(res, "bow").size());
        res = assertDoesNotThrow(() -> dmc.build("bow"));
        assertEquals(1, TestUtils.getInventory(res, "bow").size());

        // Materials used in construction disappear from inventory
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());

        res = dmc.saveGame("crafting");
        res = dmc.loadGame("crafting");

        assertEquals(1, TestUtils.getInventory(res, "bow").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
    }

    @Test
    @DisplayName("Test mercenary bribe persistance")
    public void testMercenaryBribePersistance() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      M2      M1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyBattle", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        res = dmc.saveGame("mercenary");
        res = dmc.loadGame("mercenary");

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Test potion persistance - single")
    public void testPotionPersistanceSingle() throws InvalidActionException {
                //   S1_2   S1_3       P_1
        //   S1_1   S1_4/P_4   P_2/POT/P_3/P_5
        //   S1_6   S1_5       P_6                              S2_2       S2_3
        //                     P_7                 P_8/S2_8     S2_1       S2_4
        //                                         S2_7         S2_6       S2_5
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_potionsTest_invisibilityDuration", "c_potionsTest_invisibilityDuration");

        assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());
        assertEquals(2, TestUtils.getEntities(res, "spider").size());

        // pick up invisibility_potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // consume invisibility_potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));

        res = dmc.saveGame("potion");
        res = dmc.loadGame("potion");

        // meet first spider, battle does not occur because the player is invisible
        // we need to check that the effects exist before they are worn off,
        // otherwise teams which don't implement potions will pass
        res = dmc.tick(Direction.LEFT);
        assertEquals(2, TestUtils.getEntities(res, "spider").size());
        assertEquals(0, res.getBattles().size());

        // meet second spider and battle because the player is no longer invisible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
        assertEquals(1, res.getBattles().size());
        assertTrue(res.getBattles().get(0).getRounds().size() >= 1);
    }

    // @Test
    // @DisplayName("Test potion persistance - queue")
    // public void testPotionPersistanceQueue() throws InvalidActionException{
    //     //  Wall   P_1/2/3    P_4   P_5/6/7/S_9/P_9     S_2     S_3
    //     //                          S_8/P_8             S_1     S_4
    //     //                          S_7                 S_6     S_5
    //     DungeonManiaController dmc = new DungeonManiaController();
    //     DungeonResponse res = dmc.newGame("d_potionsTest_potionQueuing", "c_potionsTest_potionQueuing");

    //     assertEquals(1, TestUtils.getEntities(res, "invincibility_potion").size());
    //     assertEquals(1, TestUtils.getEntities(res, "invisibility_potion").size());
    //     assertEquals(1, TestUtils.getEntities(res, "spider").size());

    //     // buffer
    //     res = dmc.tick(Direction.LEFT);
    //     res = dmc.tick(Direction.LEFT);

    //     // pick up invincibility potion
    //     res = dmc.tick(Direction.RIGHT);
    //     assertEquals(0, TestUtils.getEntities(res, "invincibility_potion").size());
    //     assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

    //     // pick up invisibility potion
    //     res = dmc.tick(Direction.RIGHT);
    //     assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
    //     assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

    //     // consume invisibility potion (invisibility has duration 3)
    //     res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
    //     assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());

    //     // consume invincibility potion (invisibility has duration 2)
    //     res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
    //     assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

    //     // meet spider, but not battle occurs (invisibility has duration 1)
    //     res = dmc.tick(Direction.DOWN);
    //     assertEquals(1, TestUtils.getEntities(res, "spider").size());
    //     assertEquals(0, res.getBattles().size());

    //     res = dmc.saveGame("potionQueue");
    //     res = dmc.loadGame("potionQueue");

    //     // meet spider again, battle does occur but won immediately
    //     // (invisibility has duration 0, invincibility in effect)
    //     res = dmc.tick(Direction.UP);
    //     assertEquals(0, TestUtils.getEntities(res, "spider").size());
    //     assertEquals(1, res.getBattles().size());
    //     assertEquals(1, res.getBattles().get(0).getRounds().size());
    // }

    @Test
    @DisplayName("Test door persistance")
    public void testDoorPersistance() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_doorRemainsOpen", "c_DoorsKeysTest_doorRemainsOpen");

        // pick up key
        res = dmc.tick(Direction.RIGHT);

        // open door
        res = dmc.tick(Direction.RIGHT);

        // player no longer has a key but can move freely through door
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        res = dmc.saveGame("door");
        res = dmc.loadGame("door");

        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @DisplayName("Test goal persistance")
    public void testGoalPersistance() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_complexGoalsTest_andAll", "c_complexGoalsTest_andAll");

        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // kill spider
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        res = dmc.saveGame("goal");
        res = dmc.loadGame("goal");

        // pickup treasure
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.getGoals(res).contains(":exit"));
        assertFalse(TestUtils.getGoals(res).contains(":treasure"));
        assertFalse(TestUtils.getGoals(res).contains(":boulders"));

        // move to exit
        res = dmc.tick(Direction.DOWN);
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @DisplayName("Test mind control persistance")
    public void testMindControlPersistance() throws InvalidActionException {
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

        res = dmc.saveGame("mindcontrol");
        res = dmc.loadGame("mindcontrol");

        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Test Swamp Tile Persistance")
    public void testSwampTilePersistance() {
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

        res = dmc.saveGame("slime");
        res = dmc.loadGame("slime");

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

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

    private Position getSpiderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "spider").get(0).getPosition();
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    @Test
    @DisplayName("Test new save overwrites old save")
    public void testSaveOverwrite() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");

        res = dmc.saveGame("overwrite");
        res = dmc.loadGame("overwrite");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        res = dmc.saveGame("overwrite");
        res = dmc.loadGame("overwrite");

        // move player to exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }
}
