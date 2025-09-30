package dungeonmania.entities.enemies;

import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {

    public static final double DEFAULT_FAIL_RATE = 0.0;
    private Random bribeFail;
    private double failRate;

    public Assassin(
        String id,
        Position position,
        double health,
        double attack,
        int bribeAmount,
        int bribeRadius,
        double failRate,
        boolean allied,
        int mindControlDuration
    ) {
        super(id, position, health, attack, bribeAmount, bribeRadius, allied, mindControlDuration);
        this.bribeFail = new Random(System.currentTimeMillis());
        this.failRate = failRate;
    }

    public double getBribeResult() {
        return bribeFail.nextDouble();
    }

    @Override
    public void interact(Player player, Game game) {
        if (getBribeResult() > this.failRate) {
            super.interact(player, game);
        } else {
            super.bribe(player);
        }
    }
}
