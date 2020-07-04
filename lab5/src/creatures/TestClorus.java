package creatures;

import org.junit.Test;

import java.awt.*;
import java.util.HashMap;

import huglife.Action;
import huglife.Direction;
import huglife.Empty;
import huglife.Impassible;
import huglife.Occupant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/** Tests the plip class
 *  @authr Siyuan Chen
 */

public class TestClorus {
  @Test
  public void testBasics() {
    Clorus c = new Clorus(2);
    assertEquals(2, c.energy(), 0.01);
    assertEquals(new Color(34, 0, 231), c.color());
    c.move();
    assertEquals(1.97, c.energy(), 0.01);
    c.move();
    assertEquals(1.94, c.energy(), 0.01);
    c.stay();
    assertEquals(1.93, c.energy(), 0.01);
    c.stay();
    assertEquals(1.92, c.energy(), 0.01);
  }

  @Test
  public void testReplicate() {
    Clorus c0 = new Clorus(2);
    c0.move();
    c0.move();
    assertEquals(1.94, c0.energy(), 0.01);
    Clorus c1 = c0.replicate();
    assertEquals(0.97, c0.energy(), 0.01);
    assertEquals(0.97, c1.energy(), 0.01);
    assertNotEquals(c0, c1);
  }

  @Test
  public void testChoose() {

    // No empty adjacent spaces; stay.
    Clorus c = new Clorus(1.2);
    HashMap<Direction, Occupant> surrounded = new HashMap<Direction, Occupant>();
    surrounded.put(Direction.TOP, new Impassible());
    surrounded.put(Direction.BOTTOM, new Impassible());
    surrounded.put(Direction.LEFT, new Impassible());
    surrounded.put(Direction.RIGHT, new Impassible());

    Action actual = c.chooseAction(surrounded);
    Action expected = new Action(Action.ActionType.STAY);

    assertEquals(expected, actual);

    c = new Clorus(1.2);
    HashMap<Direction, Occupant> onePlip = new HashMap<Direction, Occupant>();
    onePlip.put(Direction.TOP, new Plip());
    onePlip.put(Direction.BOTTOM, new Empty());
    onePlip.put(Direction.LEFT, new Empty());
    onePlip.put(Direction.RIGHT, new Impassible());

    actual = c.chooseAction(onePlip);
    expected = new Action(Action.ActionType.ATTACK, Direction.TOP);

    assertEquals(expected, actual);

    // Energy >= 1; replicate towards an empty space.
    c = new Clorus(1.2);
    HashMap<Direction, Occupant> topEmpty = new HashMap<Direction, Occupant>();
    topEmpty.put(Direction.TOP, new Empty());
    topEmpty.put(Direction.BOTTOM, new Impassible());
    topEmpty.put(Direction.LEFT, new Impassible());
    topEmpty.put(Direction.RIGHT, new Impassible());

    actual = c.chooseAction(topEmpty);
    expected = new Action(Action.ActionType.REPLICATE, Direction.TOP);

    assertEquals(expected, actual);


    // Energy >= 1; replicate towards an empty space.
    c = new Clorus(1.2);
    HashMap<Direction, Occupant> allEmpty = new HashMap<Direction, Occupant>();
    allEmpty.put(Direction.TOP, new Empty());
    allEmpty.put(Direction.BOTTOM, new Empty());
    allEmpty.put(Direction.LEFT, new Empty());
    allEmpty.put(Direction.RIGHT, new Empty());

    actual = c.chooseAction(allEmpty);
    Action unexpected = new Action(Action.ActionType.STAY);

    assertNotEquals(unexpected, actual);


    // Energy < 1; move.
    c = new Clorus(.99);

    actual = c.chooseAction(allEmpty);
    unexpected = new Action(Action.ActionType.STAY);

    assertNotEquals(unexpected, actual);


    // Energy < 1; move.
    c = new Clorus(.99);

    actual = c.chooseAction(topEmpty);
    expected = new Action(Action.ActionType.MOVE, Direction.TOP);

    assertEquals(expected, actual);
  }
}
