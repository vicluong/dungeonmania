package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.buildables.MidnightArmour;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.buildables.Shield;
import dungeonmania.entities.collectables.*;
import dungeonmania.entities.enemies.*;
import dungeonmania.entities.logic_entities.LightBulbOff;
import dungeonmania.entities.logic_entities.LightBulbOn;
import dungeonmania.entities.logic_entities.Switch;
import dungeonmania.entities.logic_entities.SwitchDoor;
import dungeonmania.map.GameMap;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

public class EntityFactory {
    private JSONObject config;
    private Random ranGen = new Random();

    public EntityFactory(JSONObject config) {
        this.config = config;
    }

    public JSONObject getConfig() {
        return this.config;
    }

    public Entity createEntity(JSONObject jsonEntity) {
        return constructEntity(jsonEntity, config);
    }

    public void spawnSpider(Game game) {
        GameMap map = game.getMap();
        int tick = game.getTick();
        int rate = config.optInt("spider_spawn_interval", 0);
        if (rate == 0 || (tick + 1) % rate != 0)
            return;
        int radius = 20;
        Position player = map.getPlayer().getPosition();

        Spider dummySpider = buildSpider(
            UUID.randomUUID().toString(),
            new Position(0, 0),
            new Position(0, 0),
            1
        ); // for checking possible positions

        List<Position> availablePos = new ArrayList<>();
        for (int i = player.getX() - radius; i < player.getX() + radius; i++) {
            for (int j = player.getY() - radius; j < player.getY() + radius; j++) {
                if (Position.calculatePositionBetween(player, new Position(i, j)).magnitude() > radius)
                    continue;
                Position np = new Position(i, j);
                if (!map.canMoveTo(dummySpider, np))
                    continue;
                availablePos.add(np);
            }
        }
        Position initPosition = availablePos.get(ranGen.nextInt(availablePos.size()));
        Spider spider = buildSpider(UUID.randomUUID().toString(), initPosition, initPosition, 1);
        map.addEntity(spider);
        game.register(() -> spider.move(game), Game.AI_MOVEMENT, spider.getId());
    }

    public void spawnZombie(Game game, ZombieToastSpawner spawner) {
        GameMap map = game.getMap();
        int tick = game.getTick();
        Random randGen = new Random();
        int spawnInterval = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        if (spawnInterval == 0 || (tick + 1) % spawnInterval != 0)
            return;
        List<Position> pos = spawner.getPosition().getCardinallyAdjacentPositions();
        pos = pos
                .stream()
                .filter(p -> !map.getEntities(p).stream().anyMatch(e -> (e instanceof Wall)))
                .collect(Collectors.toList());
        if (pos.size() == 0)
            return;
        ZombieToast zt = buildZombieToast(UUID.randomUUID().toString(), pos.get(randGen.nextInt(pos.size())));
        map.addEntity(zt);
        game.register(() -> zt.move(game), Game.AI_MOVEMENT, zt.getId());
    }

    public Spider buildSpider(String id, Position pos, Position initPos, int nextPositionElement) {
        double spiderHealth = config.optDouble("spider_health", Spider.DEFAULT_HEALTH);
        double spiderAttack = config.optDouble("spider_attack", Spider.DEFAULT_ATTACK);
        return new Spider(id, pos, spiderHealth, spiderAttack, initPos, nextPositionElement);
    }

    public Player buildPlayer(String id, Position pos, Potion inEffective, int nextTrigger, List<Potion> potionQueue) {
        double playerHealth = config.optDouble("player_health", Player.DEFAULT_HEALTH);
        double playerAttack = config.optDouble("player_attack", Player.DEFAULT_ATTACK);
        JSONArray inventory = config.optJSONArray("inventory");
        return new Player(id, pos, playerHealth, playerAttack, inventory, inEffective, nextTrigger, potionQueue);
    }

    public ZombieToast buildZombieToast(String id, Position pos) {
        double zombieHealth = config.optDouble("zombie_health", ZombieToast.DEFAULT_HEALTH);
        double zombieAttack = config.optDouble("zombie_attack", ZombieToast.DEFAULT_ATTACK);
        return new ZombieToast(id, pos, zombieHealth, zombieAttack);
    }

    public Mercenary buildMercenary(String id, Position pos, boolean allied, int mindControlDuration) {
        double mercenaryHealth = config.optDouble("mercenary_health", Mercenary.DEFAULT_HEALTH);
        double mercenaryAttack = config.optDouble("mercenary_attack", Mercenary.DEFAULT_ATTACK);
        int mercenaryBribeAmount = config.optInt("bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT);
        int mercenaryBribeRadius = config.optInt("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS);
        return new Mercenary(id, pos, mercenaryHealth, mercenaryAttack, mercenaryBribeAmount, mercenaryBribeRadius,
                allied, mindControlDuration);
    }

    private Entity buildAssassin(String id, Position pos, boolean allied, int mindControlDuration) {
        double assassinHealth = config.optDouble("assassin_health", Assassin.DEFAULT_HEALTH);
        double assassinAttack = config.optDouble("assassin_attack", Assassin.DEFAULT_ATTACK);
        int assassinBribeAmount = config.optInt("assassin_bribe_amount", Assassin.DEFAULT_BRIBE_AMOUNT);
        int assassinBribeRadius = config.optInt("bribe_radius", Assassin.DEFAULT_BRIBE_RADIUS);
        double assassinBribeFailRate = config.optDouble("assassin_bribe_fail_rate", Assassin.DEFAULT_FAIL_RATE);
        return new Assassin(
                id,
                pos,
                assassinHealth,
                assassinAttack,
                assassinBribeAmount,
                assassinBribeRadius,
                assassinBribeFailRate,
                allied,
                mindControlDuration);
    }

    public Bow buildBow(String id, int durability) {
        int bowDurability = config.optInt("bow_durability");
        if (durability != -1)
            bowDurability = durability;
        return new Bow(id, bowDurability);
    }

    public Shield buildShield(String id, int durability) {
        int shieldDurability = config.optInt("shield_durability");
        if (durability != -1)
            shieldDurability = durability;
        double shieldDefence = config.optInt("shield_defence");
        return new Shield(id, shieldDurability, shieldDefence);
    }

    public Sceptre buildSceptre(String id) {
        int mindControlDuration = config.optInt("mind_control_duration");
        return new Sceptre(id, mindControlDuration);
    }

    public MidnightArmour buildMidnightArmour(String id) {
        int attack = config.optInt("midnight_armour_attack");
        int defence = config.optInt("midnight_armour_defence");
        return new MidnightArmour(id, attack, defence);
    }

    private Entity constructEntity(JSONObject jsonEntity, JSONObject config) {
        JSONObject positionJSON = jsonEntity.optJSONObject("position");
        Position pos = new Position(jsonEntity.optInt("x"), jsonEntity.optInt("y"));
        if (positionJSON != null)
            pos = new Position(positionJSON.optInt("x"), positionJSON.optInt("y"));
        String id = jsonEntity.optString("entityId", UUID.randomUUID().toString());
        int durability = jsonEntity.optInt("durability", -1);
        switch (jsonEntity.getString("type")) {
            case "player":
                int nextTrigger = jsonEntity.optInt("nextTrigger", 0);
                String inEffective = jsonEntity.optString("inEffect");
                Potion currentPot = null;
                if (!inEffective.equals("")) {
                    int potionDuration = jsonEntity.optInt("duration");
                    switch (inEffective) {
                        case "InvincibilityPotion":
                            currentPot = new InvincibilityPotion(UUID.randomUUID().toString(), null, potionDuration);
                            break;
                        case "InvisibilityPotion":
                            currentPot = new InvisibilityPotion(UUID.randomUUID().toString(), null, potionDuration);
                            break;
                        default:
                            currentPot = null;
                            break;

                    }
                }
                JSONArray potionQueueJSON = jsonEntity.optJSONArray("queue");
                List<Potion> potionQueue = new ArrayList<>();
                if (potionQueueJSON != null) {
                    potionQueueJSON.forEach(p -> {
                        if (p.equals("InvisibilityPotion")) {
                            potionQueue.add(new InvisibilityPotion(UUID.randomUUID().toString(), null, config.optInt(
                                    "invisibility_potion_duration",
                                    InvisibilityPotion.DEFAULT_DURATION)));
                        } else if (p.equals("InvincibilityPotion")) {
                            potionQueue.add(new InvincibilityPotion(UUID.randomUUID().toString(), null, config.optInt(
                                    "invincibility_potion_duration",
                                    InvincibilityPotion.DEFAULT_DURATION)));
                        }
                    });
                }
                return buildPlayer(id, pos, currentPot, nextTrigger, potionQueue);
            case "zombie_toast":
                return buildZombieToast(id, pos);
            case "zombie_toast_spawner":
                return new ZombieToastSpawner(id, pos);
            case "mercenary":
                boolean allied = jsonEntity.optBoolean("allied", false);
                int mindControlDuration = jsonEntity.optInt("mindControlDuration", 0);
                return buildMercenary(id, pos, allied, mindControlDuration);
            case "assassin":
                allied = jsonEntity.optBoolean("allied", false);
                mindControlDuration = jsonEntity.optInt("mindControlDuration", 0);
                return buildAssassin(id, pos, allied, mindControlDuration);
            case "wall":
                return new Wall(id, pos);
            case "boulder":
                return new Boulder(id, pos);
            case "switch":
                boolean activated = jsonEntity.optBoolean("activated", false);
                return new Switch(id, pos, activated);
            case "exit":
                return new Exit(id, pos);
            case "treasure":
                return new Treasure(id, pos);
            case "wood":
                return new Wood(id, pos);
            case "arrow":
                return new Arrow(id, pos);
            case "bomb":
                int bombRadius = config.optInt("bomb_radius", Bomb.DEFAULT_RADIUS);
                return new Bomb(id, pos, bombRadius);
            case "invisibility_potion":
                int invisibilityPotionDuration = config.optInt(
                        "invisibility_potion_duration",
                        InvisibilityPotion.DEFAULT_DURATION);
                return new InvisibilityPotion(id, pos, invisibilityPotionDuration);
            case "invincibility_potion":
                int invincibilityPotionDuration = config.optInt("invincibility_potion_duration",
                        InvincibilityPotion.DEFAULT_DURATION);
                return new InvincibilityPotion(id, pos, invincibilityPotionDuration);
            case "portal":
                return new Portal(id, pos, ColorCodedType.valueOf(jsonEntity.getString("colour")));
            case "sword":
                double swordAttack = config.optDouble("sword_attack", Sword.DEFAULT_ATTACK);
                int swordDurability = -1;
                if (durability == -1)
                    swordDurability = config.optInt("sword_durability", Sword.DEFAULT_DURABILITY);
                else
                    swordDurability = durability;
                return new Sword(id, pos, swordAttack, swordDurability);
            case "spider":
                JSONObject initPosJSON = jsonEntity.optJSONObject("initPos");
                Position initPos = pos;
                if (initPosJSON != null)
                    initPos = new Position(initPosJSON.getInt("x"), initPosJSON.getInt("y"));
                int nextPositionElement = jsonEntity.optInt("nextPositionElement", 1);
                return buildSpider(id, pos, initPos, nextPositionElement);
            case "door":
                return new Door(id, pos, jsonEntity.getInt("key"), jsonEntity.optBoolean("isOpen"));
            case "key":
                return new Key(id, pos, jsonEntity.getInt("key"));
            case "sun_stone":
                return new Sunstone(id, pos);
            case "swamp_tile":
                return new SwampTile(id, pos, jsonEntity.getInt("movement_factor"));
            case "sceptre":
                return buildSceptre(id);
            case "midnight_armour":
                return buildMidnightArmour(id);
            case "bow":
                return buildBow(id, durability);
            case "shield":
                return buildShield(id, durability);
            case "light_bulb_off":
                return new LightBulbOff(id, pos, jsonEntity.getString("logic"));
            case "light_bulb_on":
                return new LightBulbOn(id, pos, jsonEntity.getString("logic"));
            case "switch_door":
                return new SwitchDoor(id, pos, jsonEntity.getString("logic"));
            case "wire":
                return new Wire(id, pos);
            default:
                return null;
        }
    }
}
