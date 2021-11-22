package com.poulstar.bluelight;

import org.junit.Test;

import static org.junit.Assert.*;

import com.poulstar.bluelight.test.FootballGameScore;
import com.poulstar.bluelight.test.GameListEmptyException;
import com.poulstar.bluelight.test.GameScore;
import com.poulstar.bluelight.test.TennisGameScore;

import java.util.ArrayList;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void check_game() throws GameListEmptyException {
        ArrayList<GameScore> games = new ArrayList<>();
        games.add(new FootballGameScore(1, 0));

        ArrayList<Integer> tennisScores = new ArrayList<>();
        tennisScores.add(5);
        tennisScores.add(2);
        tennisScores.add(10);
        tennisScores.add(6);
        tennisScores.add(10);

        games.add(new TennisGameScore(tennisScores));

        for (GameScore score :
                games) {
            System.out.println("score: " + score.getName() + "-" + score.calculateScore());
        }
        assertEquals(games.get(0).calculateScore(), 1);
        games = null;

        try{
            games.get(0).getName();
        }catch (Exception e) {
            throw new GameListEmptyException();
        }
    }

    @Test
    public void test_name() {
        
    }
}