package dungeonmania;

import java.io.IOException;
import java.util.UUID;

import org.json.JSONObject;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.goals.Goal;
import dungeonmania.goals.GoalFactory;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;
import dungeonmania.map.GraphNodeFactory;
import dungeonmania.util.FileLoader;

/**
 * GameBuilder -- A builder to build up the whole game
 * @author      Webster Zhang
 * @author      Tina Ji
 */
public class GameBuilder {
    private String configName;
    private String dungeonName;

    private JSONObject config = null;
    private JSONObject dungeon = null;

    public GameBuilder setConfigName(String configName) {
        this.configName = configName;
        return this;
    }

    public GameBuilder setDungeonName(String dungeonName) {
        this.dungeonName = dungeonName;
        return this;
    }

    public GameBuilder() { }

    public GameBuilder(String configName, String dungeonName) {
        this.configName = configName;
        this.dungeonName = dungeonName;
    }

    public GameBuilder(String dungeonName, JSONObject config, JSONObject dungeon) {
        this.dungeonName = dungeonName;
        this.config = config;
        this.dungeon = dungeon;
    }

    public GameBuilder(String configName, JSONObject dungeon) {
        this.configName = configName;
        this.dungeon = dungeon;
    }

    public Game buildGame() {
        if (config == null)
            loadConfig();
        if (dungeon == null)
            loadDungeon();
        if (dungeon == null && config == null) {
            return null; // something went wrong
        }

        Game game = new Game(dungeonName);
        EntityFactory factory = new EntityFactory(config);
        game.setEntityFactory(factory);
        buildMap(game);
        buildGoals(game);
        BattleFacade b = null;
        if (dungeon.has("battles")) {
            b = new BattleFacade(dungeon.getJSONArray("battles"));
        }
        Inventory i = null;
        if (dungeon.has("inventory")) {
            i = new Inventory(dungeon.getJSONArray("inventory"), config);
        }
        int initialTreasureCount = dungeon.optInt("initTreasureCount", -1);
        game.init(UUID.randomUUID().toString(), 0, b, i, initialTreasureCount);
        return game;
    }

    private void loadConfig() {
        String configFile = String.format("/configs/%s.json", configName);
        try {
            config = new JSONObject(FileLoader.loadResourceFile(configFile));
        } catch (IOException e) {
            e.printStackTrace();
            config = null;
        }
    }

    private void loadDungeon() {
        String dungeonFile = String.format("/dungeons/%s.json", dungeonName);
        try {
            dungeon = new JSONObject(FileLoader.loadResourceFile(dungeonFile));
        } catch (IOException e) {
            dungeon = null;
        }
    }

    private void buildMap(Game game) {
        GameMap map = new GameMap();
        map.setGame(game);
        dungeon.getJSONArray("entities").forEach(e -> {
            JSONObject jsonEntity = (JSONObject) e;
            GraphNode newNode = GraphNodeFactory.createEntity(jsonEntity, game.getEntityFactory());
            Entity entity = newNode.getEntities().get(0);

            if (newNode != null)
                map.addNode(newNode);

            if (entity instanceof Player)
                map.setPlayer((Player) entity);
        });
        game.setMap(map);
    }

    public void buildGoals(Game game) {
        if (!dungeon.isNull("goal-condition")) {
            Goal goal = GoalFactory.createGoal(dungeon.getJSONObject("goal-condition"), config);
            game.setGoals(goal);
        }
    }
}
