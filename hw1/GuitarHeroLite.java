/** A client that uses the synthesizer package to replicate a plucked guitar string sound */
import java.util.ArrayList;
import java.util.List;

import es.datastructur.synthesizer.GuitarString;

public class GuitarHeroLite {

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */

        String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
        GuitarString[] strings = new GuitarString[37];
        List<GuitarString> playedStrings = new ArrayList<>();

        for (int i = 0; i < strings.length; i++) {
            strings[i] = new GuitarString(440.0 * Math.pow(2.0, (i - 24.0) / 12.0));
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
            for (GuitarString s : playedStrings)
                sample += s.sample();

        /* play the sample on standard audio */
            StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
            for (GuitarString s : playedStrings)
                s.tic();
        }
    }
}

