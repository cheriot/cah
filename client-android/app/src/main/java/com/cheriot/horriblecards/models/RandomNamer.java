package com.cheriot.horriblecards.models;

import java.util.Random;

/**
 * Created by cheriot on 8/29/16.
 */
public class RandomNamer {

    public static final String[] names = new String[]{
            "Donut",
            "Penguin",
            "Stumpy",
            "Whicker",
            "Shadow",
            "Howard",
            "Wilshire",
            "Darling",
            "Disco",
            "Jack",
            "The Bear",
            "Sneak",
            "The Big L",
            "Whisp",
            "Wheezy",
            "Crazy",
            "Goat",
            "Pirate",
            "Saucy",
            "Hambone",
            "Butcher",
            "Walla Walla",
            "Snake",
            "Caboose",
            "Sleepy",
            "Killer",
            "Stompy",
            "Mopey",
            "Dopey",
            "Weasel",
            "Ghost",
            "Dasher",
            "Grumpy",
            "Hollywood",
            "Tooth",
            "Noodle",
            "King",
            "Cupid",
            "Prancer",
    };

    private Random generator;

    public RandomNamer() {
        generator = new Random();
    }

    public String randomName() {
        return names[generator.nextInt(names.length-1)];
    }
}
