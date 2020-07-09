import java.util.ArrayList;
import java.util.List;

import es.datastructur.synthesizer.Harp;

public class HarpPlayer {

  public static void main(String[] args) {
    /* create two guitar strings, for concert A and C */
    String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    Harp[] strings = new Harp[37];
    List<Harp> playedStrings = new ArrayList<>();

    for (int i = 0; i < strings.length; i++) {
      strings[i] = new Harp(440.0 * Math.pow(2.0, (i - 24.0) / 12.0));
    }

    while (true) {

      /* check if the user has typed a key; if so, process it */
      if (StdDraw.hasNextKeyTyped()) {
        char key = StdDraw.nextKeyTyped();
        if (keyboard.indexOf(key) == -1)
          continue;

        playedStrings.add(strings[keyboard.indexOf(key)]);
        strings[keyboard.indexOf(key)].pluck();
      }

      /* compute the superposition of samples */
      double sample = 0;
      for (Harp s : playedStrings)
        sample += s.sample();

        /* play the sample on standard audio */
      StdAudio.play(sample);

      /* advance the simulation of each guitar string by one step */
      for (Harp s : playedStrings)
        s.tic();
    }
  }
}

