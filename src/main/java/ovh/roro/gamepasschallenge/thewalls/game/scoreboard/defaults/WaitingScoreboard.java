package ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults;

import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.IScoreboard;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class WaitingScoreboard implements IScoreboard {

    private int playersIndex = -1;

    @Override
    public void initScoreboard(GamePlayer player) {
        int index = 0;

        player.getScoreboard().setLine(index++, "§a");

        if (this.playersIndex == -1)
            this.playersIndex = index;

        index = this.updatePlayers(player, index);

        player.getScoreboard().setLine(index, "§a");
    }

    public void updatePlayers(GamePlayer player) {
        this.updatePlayers(player, this.playersIndex);
    }

    private int updatePlayers(GamePlayer player, int index) {
        player.getScoreboard().setLine(index++, "Joueurs : §a" + (GameManager.getInstance().getPlayers().size()));
        return index;
    }

}
