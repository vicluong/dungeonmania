package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class AssassinTest {

    @Test
    @DisplayName("Failed Bribe")
    public void testFailedBribe() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1 P2/Treasure P3/Treasure P4/Treasure  A4       A3       A2     A1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribe", "c_assassinTest_failedbribe");
        String assassinId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();
        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getAssPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getAssPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(4, 1), getAssPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(assassinId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into assassin, a battle should occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }

    @Test
    @DisplayName("Successful Bribe")
    public void testSuccessfulBribe() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1 P2/Treasure P3/Treasure P4/Treasure  A4       A3       A2     A1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribe", "c_assassinTest_successfulbribe");

        String assassinId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getAssPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getAssPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(4, 1), getAssPos(res));

        // attempt bribe
        res = assertDoesNotThrow(() -> dmc.interact(assassinId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into assassin, a battle should not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @DisplayName("Invisible Player")
    public void testInvisiblePlayer() throws IllegalArgumentException, InvalidActionException {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2      P3      P4/invis pot   A4       A3       A2     A1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_invisibility", "c_assassinTest_successfulbribe");

        // move to pick up invis potion
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getEntities(res, "invisibility_potion").size());
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // use invisibility potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        // walk into assassin, a battle should not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    private Position getAssPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }
}
