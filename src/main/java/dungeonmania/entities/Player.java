package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONObject;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Sunstone;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, Overlappable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> queue = new LinkedList<>();
    private Potion inEffective = null;
    private int nextTrigger = 0;
    private int potionDuration = 0;

    private PlayerState state;

    public Player(
        String id,
        Position position,
        double health,
        double attack,
        JSONArray inventoryJSON,
        Potion inEffective,
        int nextTrigger,
        List<Potion> potionQueue
        ) {
        super(id, position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        this.inEffective = inEffective;
        this.nextTrigger = nextTrigger;
        state = new PlayerState(this);
        if (inEffective instanceof InvisibilityPotion) {
            state.transitionInvisible();
        } else if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        }
        this.queue.addAll(potionQueue);
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public List<String> getBuildables() {
        return inventory.getBuildables();
    }

    public boolean build(String entity, EntityFactory factory) {
        InventoryItem item = inventory.checkBuildCriteria(this, true, entity, factory);
        if (item == null)
            return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied())
                    return;
            }
            map.getGame().battle(this, (Enemy) entity);
        }
        if (entity instanceof InventoryItem) {
            if (entity instanceof Bomb) {
                ((Bomb) entity).onOverlap(map, this);
            } else {
                if (!(this).pickUp(entity))
                    return;
                map.destroyEntity(entity);
            }
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null)
            inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public boolean useKey() {
        Key k = inventory.getFirst(Key.class);
        Sunstone s = inventory.getFirst(Sunstone.class);
        if (s != null) {
            return true;
        } else if (k != null) {
            inventory.remove(k);
            return true;
        }
        return false;
    }

    public void triggerNext(int currentTick) {
        if (queue.isEmpty()) {
            inEffective = null;
            state.transitionBase();
            return;
        }
        inEffective = queue.remove();
        if (inEffective instanceof InvincibilityPotion) {
            state.transitionInvincible();
        } else {
            state.transitionInvisible();
        }
        nextTrigger = currentTick + inEffective.getDuration();
    }

    public void changeState(PlayerState playerState) {
        state = playerState;
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        queue.add(potion);
        potionDuration = potion.getDuration();
        if (inEffective == null) {
            triggerNext(tick);
        }
    }

    public void onTick(int tick) {
        if (inEffective == null || tick == nextTrigger)
            triggerNext(tick);
        this.potionDuration -= 1;
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    @Override
    public void setHealth(double health) {
        this.battleStatistics.setHealth(health);
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public BattleStatistics applyBuff(BattleStatistics origin) {
        if (state.isInvincible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                    0,
                    0,
                    0,
                    1,
                    1,
                    true,
                    true));
        } else if (state.isInvisible()) {
            return BattleStatistics.applyBuff(origin, new BattleStatistics(
                    0,
                    0,
                    0,
                    1,
                    1,
                    false,
                    false));
        }
        return origin;
    }

    public boolean hasSceptre() {
        return countEntityOfType(Sceptre.class) >= 1;
    }

    public int getMindControlDuration() {
        return inventory.getEntities(Sceptre.class).get(0).getMindControlDuration();
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = super.toJSON();
        if (this.inEffective != null)
            jsonObject.put("inEffect", this.inEffective.getClass().getSimpleName());
        jsonObject.put("nextTrigger", nextTrigger);
        jsonObject.put("duration", potionDuration);
        JSONArray potionQueue = new JSONArray();
        queue.stream().forEach(p -> potionQueue.put(p.getClass().getSimpleName()));
        jsonObject.put("queue", potionQueue);
        return jsonObject;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }
}
