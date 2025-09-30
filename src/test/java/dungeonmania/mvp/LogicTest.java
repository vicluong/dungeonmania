package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogicTest {
    @Test
    @DisplayName("Test a simple lightbulb connection")
    public void testLightBulbBasic() {
        //
        // P        B       Sw      Wi      Wi      Lb
        //

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_simpleLight", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(6, 1), getLightBulbOffPos(res, 0));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(6, 1), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test activation of multiple lightbulbs with the same switch")
    public void testMultiActivation() {
        //                  Wi      Lb
        // P        B       Sw
        //                  Wi      Lb

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_multi", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 2), getPlayerPos(res));
        assertEquals(new Position(4, 1), getLightBulbOffPos(res, 1));
        assertEquals(new Position(4, 3), getLightBulbOffPos(res, 0));

        // push boulder - light bulbs turn on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 2), getPlayerPos(res));
        assertEquals(new Position(4, 1), getLightBulbOnPos(res, 1));
        assertEquals(new Position(4, 3), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test a simple switch door connection")
    public void testSwitchDoorBasic() {
        //          B
        //          Sw
        //          Wi
        // P        SwD

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_simpleSwitchDoor", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 4), getPlayerPos(res));
        assertEquals(new Position(2, 4), getSwitchDoorPos(res));
        assertEquals(new Position(2, 1), getBoulderPos(res));

        // can't pass through switch door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(1, 4), getPlayerPos(res));

        // move up
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 3), getPlayerPos(res));

        // move up
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 2), getPlayerPos(res));

        // move up
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 1), getPlayerPos(res));

        // move up
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 0), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), getPlayerPos(res));

        // push boulder - activate switch door
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(2, 2), getBoulderPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 2), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 3), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 4), getPlayerPos(res));

        // move left - through switch door
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(2, 4), getPlayerPos(res));

        // move left - beyond switch door
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 4), getPlayerPos(res));
    }

    @Test
    @DisplayName("Test that switches work as conductors")
    public void testSwitchConductors() {
        //
        // P        B       Sw      Sw      Wi      Sw      Lb
        //

                DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_switchConductor", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(7, 1), getLightBulbOffPos(res, 0));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(7, 1), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test AND light bulb passes")
    public void testANDPass() {
        // P        B       Sw      Wi
        //                          Lb
        //          B       Sw      Wi

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_ANDPass", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb stays off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 2), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 2), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getPlayerPos(res));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test AND light bulb fails")
    public void testANDFail() {
        // P
        // B                B
        // Sw               Sw
        // Wi       Wi      Wi
        //          Lb

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_ANDFail", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(2, 5), getLightBulbOffPos(res, 0));

        // push boulder - light bulb stays off
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 2), getPlayerPos(res));
        assertEquals(new Position(2, 5), getLightBulbOffPos(res, 0));

        // move up
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));

        // move right
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(3, 2), getPlayerPos(res));
        assertEquals(new Position(2, 5), getLightBulbOffPos(res, 0));
    }

    @Test
    @DisplayName("Test OR light bulb")
    public void testOR() {
        // P        B       Sw      Wi
        //                          Lb
        //          B       Sw      Wi

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_OR", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOnPos(res, 0));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 2), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 2), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getPlayerPos(res));

        // push boulder - light bulb still on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test XOR light bulb")
    public void testXOR() {
        // P        B       Sw      Wi
        //                          Lb
        //          B       Sw      Wi

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_XOR", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOnPos(res, 0));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(2, 2), getPlayerPos(res));

        // move left
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 2), getPlayerPos(res));

        // move down
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getPlayerPos(res));

        // push boulder - light bulb turns off due to XOR condition failing
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));
    }

    @Test
    @DisplayName("Test CO_AND passing with conductors activated on the same tick")
    public void testCOANDPass() {
        //                  Wi      Wi      Wi
        // P        B       Sw              Lb
        //                  Wi      Wi      Wi

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_COANDPass", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 2), getPlayerPos(res));
        assertEquals(new Position(5, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb turns on
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 2), getPlayerPos(res));
        assertEquals(new Position(5, 2), getLightBulbOnPos(res, 0));
    }

    @Test
    @DisplayName("Test CO_AND failing with conductors not activated on the same tick")
    public void testCOANDFail() {
        // P        B       Sw      Wi
        //                          Lb
        //          B       Sw      Wi
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_COANDFail", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb doesn't turn on due to CO_AND condition
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 2), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(1, 3), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));

        // push boulder - light bulb doesn't turn on due to CO_AND condition
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 3), getPlayerPos(res));
        assertEquals(new Position(4, 2), getLightBulbOffPos(res, 0));
    }

    @Test
    @DisplayName("Test wire is walkable")
    public void testWalkableWire() {
        //
        // P        Wi      Wi
        //

        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicTest_walkableWire", "c_logicTest_basic");

        // check starting position
        assertEquals(new Position(1, 1), getPlayerPos(res));

        // player walks on wire
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));

        // player walks on wire
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getPlayerPos(res));

        // player walks away from wire
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getPlayerPos(res));

    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }

    private Position getLightBulbOffPos(DungeonResponse res, int index) {
        return TestUtils.getEntities(res, "light_bulb_off").get(index).getPosition();
    }

    private Position getLightBulbOnPos(DungeonResponse res, int index) {
        return TestUtils.getEntities(res, "light_bulb_on").get(index).getPosition();
    }

    private Position getSwitchDoorPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "switch_door").get(0).getPosition();
    }

    private Position getBoulderPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "boulder").get(0).getPosition();
    }
}
