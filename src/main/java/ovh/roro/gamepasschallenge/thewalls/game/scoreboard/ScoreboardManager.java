package ovh.roro.gamepasschallenge.thewalls.game.scoreboard;

import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults.GameScoreboard;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults.WaitingScoreboard;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class ScoreboardManager {

    private static final ScoreboardManager INSTANCE = new ScoreboardManager();

    private final WaitingScoreboard waitingScoreboard;
    private final GameScoreboard gameScoreboard;

    private ScoreboardManager() {
        this.waitingScoreboard = new WaitingScoreboard();
        this.gameScoreboard = new GameScoreboard();
    }

    public static ScoreboardManager getInstance() {
        return INSTANCE;
    }

    public WaitingScoreboard getWaitingScoreboard() {
        return this.waitingScoreboard;
    }

    public GameScoreboard getGameScoreboard() {
        return this.gameScoreboard;
    }
}
