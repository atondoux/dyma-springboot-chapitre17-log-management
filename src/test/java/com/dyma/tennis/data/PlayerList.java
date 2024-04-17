package com.dyma.tennis.data;

import com.dyma.tennis.Player;
import com.dyma.tennis.Rank;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

public class PlayerList {

    public static Player RAFAEL_NADAL = new Player(
            "Rafael",
            "Nadal",
            LocalDate.of(1986, Month.JUNE, 3),
            new Rank(1, 5000)
    );

    public static Player NOVAK_DJOKOVIC = new Player(
            "Novak",
            "Djokovic",
            LocalDate.of(1987, Month.MAY, 22),
            new Rank(2, 4000)
    );

    public static Player ROGER_FEDERER = new Player(
            "Roger",
            "Federer",
            LocalDate.of(1981, Month.AUGUST, 8),
            new Rank(3, 3000)
    );

    public static Player ANDY_MURRAY = new Player(
            "Andy",
            "Murray",
            LocalDate.of(1987, Month.MAY, 15),
            new Rank(4, 2000)
    );

    public static List<Player> ALL = Arrays.asList(RAFAEL_NADAL, NOVAK_DJOKOVIC, ROGER_FEDERER, ANDY_MURRAY);
}
