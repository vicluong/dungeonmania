package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EnemiesGoalsTest {

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal with a spider")
    public void enemiesGoal() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_basicSpider", "c_enemiesGoalsTest_singleEnemy");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    // @Tag("11-10")
    @DisplayName("Test the player battles three enemies consecutively, defeats them and achieves goal")
    public void enemiesGoalsMultiple() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_consecutiveEnemies",
                                          "c_enemiesGoalsTest_consecutiveEnemies");

        // assert goal not met
        List<EntityResponse> entities = res.getEntities();
        int spiderCount = TestUtils.countEntityOfType(entities, "spider");
        int zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        int mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(1, spiderCount);
        assertEquals(1, zombieCount);
        assertEquals(1, mercCount);
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks zombie
        DungeonResponse postBattleResponse = dmc.tick(Direction.RIGHT);
        entities = postBattleResponse.getEntities();
        spiderCount = TestUtils.countEntityOfType(entities, "spider");
        zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(1, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(1, mercCount);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks mercenary
        postBattleResponse = dmc.tick(Direction.RIGHT);
        entities = postBattleResponse.getEntities();
        spiderCount = TestUtils.countEntityOfType(entities, "spider");
        zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(1, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(0, mercCount);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks spider
        postBattleResponse = dmc.tick(Direction.RIGHT);

        // assert goal met
        entities = postBattleResponse.getEntities();
        spiderCount = TestUtils.countEntityOfType(entities, "spider");
        zombieCount = TestUtils.countEntityOfType(entities, "zombie_toast");
        mercCount = TestUtils.countEntityOfType(entities, "mercenary");
        assertEquals(0, spiderCount);
        assertEquals(0, zombieCount);
        assertEquals(0, mercCount);
        assertTrue(TestUtils.getGoals(res).contains(""));
    }

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal with a spawner v1")
    public void enemiesGoalWithSpawnerv1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_spawner_v1", "c_enemiesGoalsTest_singleEnemy");
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertEquals(1, TestUtils.countType(res, "zombie_toast_spawner"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertEquals(1, TestUtils.countType(res, "zombie_toast_spawner"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        //player picks up sword
        res = dmc.tick(Direction.RIGHT);

        //player interacts with spawner, destroying it
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }


    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal with a spawner v2")
    public void enemiesGoalWithSpawnerv2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_spawner_v2", "c_enemiesGoalsTest_singleEnemy");
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player picks up sword
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        //player interacts with spawner, destroying it
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "mercenary"));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player battles mercenary
        res = dmc.tick(Direction.LEFT);

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "mercenary"));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal AND exit goal")
    public void enemiesGoalAnd() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_enemiesAndExit", "c_enemiesGoalsTest_singleEnemy");

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // player attacks spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        //player exits
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal AND exit goal - exit required last")
    public void enemiesGoalAndExitLast() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_andExitLast", "c_enemiesGoalsTest_singleEnemy");

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // player checks exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // player attacks spider
        res = dmc.tick(Direction.LEFT);

        // assert enemies goal met but not exit
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertFalse(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        //player exits
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal OR exit goal - exit achieved")
    public void enemiesGoalORv1() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_enemiesOrExit", "c_enemiesGoalsTest_singleEnemy");

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // player attacks spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }

    @Test
    // @Tag("13-1")
    @DisplayName("Test achieving a basic enemies goal OR exit goal - enemies achieved")
    public void enemiesGoalORv2() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_enemiesGoalsTest_enemiesOrExit", "c_enemiesGoalsTest_singleEnemy");

        // assert goal not met
        assertEquals(1, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // player attacks spider
        res = dmc.tick(Direction.LEFT);

        // assert goal met
        assertEquals(0, TestUtils.countType(res, "spider"));
        assertTrue(TestUtils.getGoals(res).contains(""));
    }
}
