package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

public class MidnightArmourTest {
    @Test
    @DisplayName("Test midnight armour can be crafted")
    public void testMidnightArmourBasic() {
        //
        //  P1  P2/Sword    P3/Sunstone
        //
        // Player picks up a sword and a midnight armour can be crafted
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_basic", "c_midnightArmourTest");

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());

        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @DisplayName("Test midnight armour cannot be crafted with zombie")
    public void testMidnightArmourCraftComplex() {
        //
        //  P1  P2/Sword    P3/Sunstone _   _   Z1
        //
        // Player picks up a sword and a midnight armour can be crafted
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_zombie", "c_midnightArmourTest");

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        assertEquals(1, TestUtils.getEntities(res, "zombie_toast").size());
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
    }

    @Test
    @DisplayName("Test midnight armour applies buff")
    public void testMidnightArmourBuff() {
        // P1   P2/Sword    P3/Sunstone,P4/Build    P5/M5   M4  M3  M2  M1
        DungeonManiaController dmc = new DungeonManiaController();
        String config = "c_midnightArmourTest";

        DungeonResponse res = dmc.newGame("d_midnightArmourTest_basic", config);

        // pick up sunstone and sword
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));

        res = dmc.tick(Direction.RIGHT);
        BattleResponse battle = res.getBattles().get(0);

        RoundResponse firstRound = battle.getRounds().get(0);

        double enemyAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("mercenary_attack", config));
        double shieldEffect = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_defence", config));
        double expectedDamage = (enemyAttack - shieldEffect) / 10;
        // Check that damage reduction has occurred
        assertEquals(expectedDamage, -firstRound.getDeltaCharacterHealth(), 0.001);

        // Check that attack damage increase has occurred
        double playerBaseAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));
        double swordAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", config));
        assertEquals((playerBaseAttack + swordAttack) / 5, -firstRound.getDeltaEnemyHealth(), 0.001);
    }

    @Test
    @DisplayName("Test midnight armour does not lose durability")
    public void testMidnightArmourDurability() {
        DungeonManiaController dmc = new DungeonManiaController();
        String config = "c_midnightArmourTest";
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_basic", config);

        // pick up sunstone and sword
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sunstone").size());
        // craft midnight armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        // check that a shield would normally break after one battle
        int shieldDurability = Integer.parseInt(TestUtils.getValueFromConfigFile("shield_durability", config));
        assertEquals(1, shieldDurability);
        // initiate battle
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
        // show that midnight armour stays in inventory
        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
    }
}
