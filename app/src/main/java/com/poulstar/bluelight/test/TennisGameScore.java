package com.poulstar.bluelight.test;

import java.util.List;

public class TennisGameScore extends GameScore {

    private List<Integer> scores;

    public TennisGameScore(List<Integer> setScores) {
        this.scores = setScores;
    }

    @Override
    public int calculateScore() {
        int finalScore = 0;
        for (int score :
                this.scores) {
            finalScore += score;
        }
        return finalScore;
    }

    @Override
    public String getName() {
        return "tennis";
    }
}
