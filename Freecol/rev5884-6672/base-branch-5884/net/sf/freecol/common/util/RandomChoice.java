

package net.sf.freecol.common.util;

import java.util.Collection;
import java.util.Random;
import net.sf.freecol.common.PseudoRandom;


public class RandomChoice<T> {

    private final int probability;
    private final T object;

    public RandomChoice(T object, int probability) {
        this.probability = probability;
        this.object = object;
    }

    public int getProbability() {
        return probability;
    }

    public T getObject() {
        return object;
    }


    public static <T> T getWeightedRandom(PseudoRandom pseudoRandom, Collection<RandomChoice<T>> input) {
        if (input == null || input.isEmpty()) {
            return null;
        } else if (input.size() == 1) {
            return input.iterator().next().getObject();
        } else {
            return select(input, pseudoRandom.nextInt(getTotalProbability(input)));
        }
    }

    public static <T> T getWeightedRandom(Random random, Collection<RandomChoice<T>> input) {
        if (input == null || input.isEmpty()) {
            return null;
        } else if (input.size() == 1) {
            return input.iterator().next().getObject();
        } else {
            return select(input, random.nextInt(getTotalProbability(input)));
        }
    }

    public static <T> int getTotalProbability(Collection<RandomChoice<T>> input) {
        int total = 0;
        for (RandomChoice<T> choice : input) {
            total += choice.getProbability();
        }
        return total;
    }        

    public static <T> T select(Collection<RandomChoice<T>> input, int probability) {
        if (input.isEmpty()) {
            return null;
        } else {
            int total = 0;
            for (RandomChoice<T> choice : input) {
                total += choice.getProbability();
                if (probability < total) {
                    return choice.getObject();
                }
            }
            return input.iterator().next().getObject();
        }
    }

}
