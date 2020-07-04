package creatures;

import huglife.Creature;
import huglife.Direction;
import huglife.Action;
import huglife.Occupant;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import static huglife.HugLifeUtils.randomEntry;

/**
 * An implementation of a motile pacifist photosynthesizer.
 *
 * @author Josh Hug
 */
public class Plip extends Creature {

    /**
     * red color.
     */
    private int r;

    /**
     * green color.
     */
    private int g;

    /**
     * blue color.
     */
    private int b;

    /**
     * probability of taking a move when ample space available.
     */
    private static final double moveProbability = 0.5;

    /**
     * energy to gain when stay.
     */
    private static final double stayEnergyGained = 0.2;

    /**
     * energy to lose when move.
     */
    private static final double moveEnergyLost = 0.15;

    /**
     * fraction of energy to retain when replicating.
     */
    private static final double repEnergyRetained = 0.5;

    /**
     * creates plip with energy equal to E.
     */
    public Plip(double e) {
        super("plip");
        r = 99;
        g = 255;
        b = 76;
        energy = e;
    }

    /**
     * creates a plip with energy equal to 1.
     */
    public Plip() {
        this(1);
    }

    /**
     * Should return a color with red = 99, blue = 76, and green that varies
     * linearly based on the energy of the Plip. If the plip has zero energy,
     * it should have a green value of 63. If it has max energy, it should
     * have a green value of 255. The green value should vary with energy
     * linearly in between these two extremes. It's not absolutely vital
     * that you get this exactly correct.
     */
    public Color color() {
        g = ((int) this.energy * 96) + 63;
        return color(r, g, b);
    }

    /**
     * Do nothing with C, Plips are pacifists.
     */
    public void attack(Creature c) {
        // do nothing.
    }

    /**
     * Plips should lose 0.15 units of energy when moving. If you want to
     * to avoid the magic number warning, you'll need to make a
     * private static final variable. This is not required for this lab.
     */
    public void move() {
        this.energy -= moveEnergyLost;
        if (this.energy < 0) this.energy = 0;
    }


    /**
     * Plips gain 0.2 energy when staying due to photosynthesis.
     */
    public void stay() {
        this.energy += stayEnergyGained;
        if (this.energy > 2.0) this.energy = 2.0;
    }

    /**
     * Plips and their offspring each get 50% of the energy, with none
     * lost to the process. Now that's efficiency! Returns a baby Plip.
     */
    public Plip replicate() {
        this.energy *= repEnergyRetained;
        return new Plip(this.energy);
    }

    /**
     * Plips take exactly the following actions based on NEIGHBORS:
     * 1. If no empty adjacent spaces, STAY.
     * 2. Otherwise, if energy >= 1, REPLICATE towards an empty direction
     * chosen at random.
     * 3. Otherwise, if any Cloruses, MOVE with 50% probability,
     * towards an empty direction chosen at random.
     * 4. Otherwise, if nothing else, STAY
     * <p>
     * Returns an object of type Action. See Action.java for the
     * scoop on how Actions work. See SampleCreature.chooseAction()
     * for an example to follow.
     */
    public Action chooseAction(Map<Direction, Occupant> neighbors) {

        Deque<Direction> emptyNeighbors = new ArrayDeque<>();
        boolean anyClorus = false;

        // add all empty neighbors
        for (Direction d : neighbors.keySet()) {
            if (neighbors.get(d).name().equals("empty")) emptyNeighbors.add(d);
            else if (neighbors.get(d).name().equals("clorus")) anyClorus = true;
        }

        // Rule 1
        if (emptyNeighbors.size() == 0) return new Action(Action.ActionType.STAY);

        // Rule 2
        else if (this.energy >= 1.0)
            return new Action(Action.ActionType.REPLICATE, randomEntry(emptyNeighbors));

        // Rule 3
        else if (anyClorus) {
            if (Math.random() <= moveProbability)
                return new Action(Action.ActionType.MOVE, randomEntry(emptyNeighbors));
        }

        // Rule 4
        return new Action(Action.ActionType.STAY);
    }
}
