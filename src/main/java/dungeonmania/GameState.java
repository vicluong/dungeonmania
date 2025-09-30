package dungeonmania;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.goals.Goal;

public class GameState {

    private Player p;
    private String id;
    private int currentTicks;
    private JSONObject config;
    private Goal goals;
    private BattleFacade battles;
    private Inventory inventory;
    private List<Entity> entityList;
    private int initialTreasureCount;

    public GameState(Game game) {
        p = game.getPlayer();
        id = game.getId();
        currentTicks = game.getTick();
        goals = game.getGoals();
        config = game.getEntityFactory().getConfig();
        battles = game.getBattleFacade();
        inventory = p.getInventory();
        entityList = game.getMap().getEntities();
        initialTreasureCount = game.getInitialTreasureCount();
    }

    public JSONObject toJSON() {
        JSONObject state = new JSONObject();
        JSONObject dungeon = new JSONObject();
        JSONArray entities = new JSONArray();
        entityList.stream().forEach(e -> entities.put(e.toJSON()));
        dungeon.put("entities", entities);
        dungeon.put("id", id);
        dungeon.put("tick", currentTicks);
        dungeon.put("goal-condition", goals.toJSON());
        dungeon.put("battles", battles.toJSON());
        dungeon.put("inventory", inventory.toJSON());
        dungeon.put("initTreasureCount", initialTreasureCount);
        state.put("config", config);
        state.put("dungeon", dungeon);
        return state;
    }
}
