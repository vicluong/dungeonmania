package dungeonmania.mvp;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public class DungeonGenerationTest {

    @Test
    @DisplayName("Test Player and Exit are Spawned at the right position")
    public void testPlayerExit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 5, 5, "c_basicGoalsTest_exit");
        assertDoesNotThrow(() -> TestUtils.getEntityAtPos(res, "player", new Position(0, 0)));
        assertDoesNotThrow(() -> TestUtils.getEntityAtPos(res, "exit", new Position(5, 5)));
    }


    @Test
    @DisplayName("Test there is a path from players initial position to the exit")
    public void testPlayerCanExit() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.generateDungeon(0, 0, 15, 15, "c_basicGoalsTest_exit");
        Position playerPos = new Position(0, 0);
        Position exitPos = new Position(15, 15);
        assertDoesNotThrow(() -> TestUtils.getEntityAtPos(res, "player", playerPos));
        assertDoesNotThrow(() -> TestUtils.getEntityAtPos(res, "exit", exitPos));
        List<EntityResponse> walls = TestUtils.getEntities(res, "wall");
        // System.out.println(walls);
        assertTrue(TestUtils.hasPath(walls, playerPos, exitPos));
    }
}
