package creatures;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import huglife.Action;
import huglife.Creature;
import huglife.Direction;
import huglife.Occupant;

import static huglife.HugLifeUtils.randomEntry;

public class Clorus extends Creature {
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
   * energy to lose when stay.
   */
  private static final double stayEnergylost = 0.01;

  /**
   * energy to lose when move.
   */
  private static final double moveEnergyLost = 0.03;

  /**
   * fraction of energy to retain when replicating.
   */
  private static final double repEnergyRetained = 0.5;

  public Clorus(double e) {
    super("clorus");
    r = 34;
    g = 0;
    b = 231;
    energy = e;
  }

  public Clorus() {
    this(1);
  }

  public void move() {
    this.energy -= moveEnergyLost;
  }

  public void attack(Creature c) {
    energy += c.energy();
  }

  public Clorus replicate() {
    this.energy *= repEnergyRetained;
    return new Clorus(this.energy);
  }

  public void stay() {
    this.energy -= stayEnergylost;
  }

  public Action chooseAction(Map<Direction, Occupant> neighbors) {
    Deque<Direction> emptyNeighbors = new ArrayDeque<>();
    Deque<Direction> plipNeighbors = new ArrayDeque<>();
    boolean anyPlip = false;

    for (Direction d : neighbors.keySet()) {
      if (neighbors.get(d).name().equals("empty")) emptyNeighbors.add(d);

      else if (neighbors.get(d).name().equals("plip")) {
        anyPlip = true;
        plipNeighbors.add(d);
      }
    }

    if (emptyNeighbors.size() == 0) return new Action(Action.ActionType.STAY);

    else if (anyPlip) return new Action(Action.ActionType.ATTACK, randomEntry(plipNeighbors));

    else if (energy >= 1)
      return new Action(Action.ActionType.REPLICATE, randomEntry(emptyNeighbors));

    return new Action(Action.ActionType.MOVE, randomEntry(emptyNeighbors));
  }

  public Color color() {
    return color(r, g, b);
  }
}
