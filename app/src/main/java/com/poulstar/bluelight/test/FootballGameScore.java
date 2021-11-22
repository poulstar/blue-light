package com.poulstar.bluelight.test;

public class FootballGameScore extends GameScore {

    private int firstHalfScore;
    private int secondHalfScore;

    public FootballGameScore(int firstHalfScore, int secondHalfScore) {
        this.firstHalfScore = firstHalfScore;
        this.secondHalfScore = secondHalfScore;
    }

    @Override
    public int calculateScore() {
        return this.firstHalfScore + this.secondHalfScore;
    }

    @Override
    public String getName() {
        return "football";
    }
}
