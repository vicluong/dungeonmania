package dungeonmania.entities.enemies;

import org.json.JSONObject;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount = Mercenary.DEFAULT_BRIBE_AMOUNT;
    private int bribeRadius = Mercenary.DEFAULT_BRIBE_RADIUS;
    private boolean allied = false;
    private MovementStrategy strategy = new TargetedMovement();
    private int mindControlDuration = 0;

    public Mercenary(String id, Position position, double health, double attack, int bribeAmount, int bribeRadius,
            boolean allied, int mindControlDuration) {
        super(id, position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
        this.allied = allied;
        this.mindControlDuration = mindControlDuration;
    }

    public boolean isAllied() {
        return allied;
    }

    public boolean isAttached() {
        return isAllied() && ((AlliedMovement) strategy).isAttached();
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied)
            return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     *
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        return (bribeRadius >= 0 && player.countEntityOfType(Treasure.class) >= bribeAmount) || player.hasSceptre();
    }

    /**
     * bribe the merc
     */
    protected void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }
    }

    public int getBribeAmount() {
        return this.bribeAmount;
    }

    @Override
    public void interact(Player player, Game game) {
        allied = true;
        if (!player.hasSceptre()) {
            bribe(player);
        } else {
            setMindControlDuration(player.getMindControlDuration());
        }
        strategy = new AlliedMovement();
    }

    private void setMindControlDuration(int mindControlDuration) {
        this.mindControlDuration = mindControlDuration;
    }

    @Override
    public void move(Game game) {
        strategy.doMove(this, game);
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && canBeBribed(player);
    }

    public void update() {
        if (mindControlDuration > 0) {
            mindControlDuration--;
            if (mindControlDuration == 0) {
                strategy = new TargetedMovement();
                allied = false;
            }
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject json = super.toJSON();
        json.put("allied", allied);
        json.put("mindControlDuration", mindControlDuration);
        return json;
    }
}
