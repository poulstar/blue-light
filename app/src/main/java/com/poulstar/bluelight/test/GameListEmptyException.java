package com.poulstar.bluelight.test;

public class GameListEmptyException extends Exception {
    public GameListEmptyException() {
        super("Game list is empty");
    }
}
