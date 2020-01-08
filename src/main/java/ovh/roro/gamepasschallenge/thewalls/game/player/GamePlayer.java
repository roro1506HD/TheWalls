package ovh.roro.gamepasschallenge.thewalls.game.player;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IScoreboardCriteria;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardObjective;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardScore;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.Scoreboard;
import net.minecraft.server.v1_8_R3.ScoreboardObjective;
import net.minecraft.server.v1_8_R3.ScoreboardScore;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.potion.PotionEffect;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.GameState;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeamPlayer;
import ovh.roro.gamepasschallenge.thewalls.util.ScoreboardSign;
import ovh.roro.gamepasschallenge.thewalls.util.item.ItemFactory;

import java.util.UUID;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GamePlayer {

    private final CraftPlayer player;
    private final ScoreboardSign scoreboard;
    private final ScoreboardObjective healthObjective;
    private final ScoreboardScore healthScore;

    private boolean alive;
    private boolean spectator;
    private boolean scoreboardInitialized;
    private GameTeam team;
    private GameTeamPlayer teamPlayer;

    public GamePlayer(CraftPlayer player) {
        Scoreboard scoreboard = MinecraftServer.getServer().getWorld().getScoreboard();

        this.player = player;
        this.scoreboard = new ScoreboardSign(player, "§a§lGAME PASS §f§lCHALLENGE");
        this.healthObjective = new ScoreboardObjective(scoreboard, "%", IScoreboardCriteria.b);
        this.healthScore = new ScoreboardScore(scoreboard, this.healthObjective, player.getName());
        this.healthScore.setScore(100);

        this.spectator = TheWalls.getInstance().getGameConfig().getGame().getSpecs().contains(player.getName());
    }

    public void initialize(boolean lobbyStuff) {
        if (this.spectator && (GameManager.getInstance().getState() == GameState.IN_GAME || GameManager.getInstance().getState() == GameState.TELEPORTING)) {
            this.player.setGameMode(GameMode.SPECTATOR);
            this.player.getInventory().clear();
            this.player.getInventory().setArmorContents(null);
            this.player.teleport(TheWalls.getInstance().getGameConfig().getGame().getSpecSpawn());
            ScoreboardManager.getInstance().getGameScoreboard().initScoreboard(this);
            this.updateShownHealth();
            return;
        }

        this.player.getInventory().clear();
        this.player.getInventory().setArmorContents(null);
        this.player.setMaxHealth(20.0D);
        this.player.setHealthScale(20.0D);
        this.player.setHealth(20.0D);
        this.player.setFoodLevel(20);
        this.player.setSaturation(20.0F);
        this.player.setExhaustion(20.0F);
        this.player.setWalkSpeed(0.2F);
        this.player.setLevel(0);
        this.player.setExp(0.0F);
        this.player.getActivePotionEffects().stream()
                .map(PotionEffect::getType)
                .forEach(this.player::removePotionEffect);

        if (lobbyStuff) {
            this.player.setGameMode(GameMode.ADVENTURE);
            this.player.teleport(TheWalls.getInstance().getGameConfig().getGame().getLobby());
            this.player.getInventory().setItem(0, ItemFactory.TEAM_SELECTOR);
        }
    }

    public void reset() {
        this.alive = true;

        this.scoreboard.clearLines();
        ScoreboardManager.getInstance().getWaitingScoreboard().initScoreboard(this);

        this.scoreboardInitialized = false;
        this.sendPacket(new PacketPlayOutScoreboardObjective(this.healthObjective, 1));

        this.healthScore.setScore(100);

        this.initialize(true);
    }

    public void updateHealth() {
        this.healthScore.setScore((int) Math.round(100 * (this.player.getHealth() + this.player.getHandle().getAbsorptionHearts()) / 20));
    }

    public void updateShownHealth() {
        if (!this.scoreboardInitialized) {
            this.sendPacket(new PacketPlayOutScoreboardObjective(this.healthObjective, 0));
            this.sendPacket(new PacketPlayOutScoreboardDisplayObjective(2, this.healthObjective));
            this.scoreboardInitialized = true;
        }

        for (GamePlayer gamePlayer : GameManager.getInstance().getPlayers())
            this.sendPacket(new PacketPlayOutScoreboardScore(gamePlayer.healthScore));
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        PlayerConnection playerConnection = this.player.getHandle().playerConnection;

        playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut));
        playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, new ChatComponentText(subtitle)));
        playerConnection.sendPacket(new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, new ChatComponentText(title)));
    }

    public void sendActionBar(String message) {
        this.player.getHandle().playerConnection.sendPacket(new PacketPlayOutChat(new ChatComponentText(message), (byte) 2));
    }

    public void sendMessage(String message) {
        this.player.sendMessage("§a§lGamePass §r§lChallenge §8» " + message);
    }

    public void sendPacket(Packet<?> packet) {
        this.player.getHandle().playerConnection.sendPacket(packet);
    }

    public CraftPlayer getPlayer() {
        return this.player;
    }

    public UUID getUniqueId() {
        return this.player.getUniqueId();
    }

    public String getName() {
        return this.player.getName();
    }

    public ScoreboardSign getScoreboard() {
        return this.scoreboard;
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isSpectator() {
        return this.spectator;
    }

    public void setSpectator(boolean spectator) {
        this.spectator = spectator;
    }

    public void incrementKills() {
        this.teamPlayer.incrementKills();
    }

    public int getKills() {
        if (this.teamPlayer == null)
            return 0;
        return this.teamPlayer.getKills();
    }

    public GameTeam getTeam() {
        return this.team;
    }

    public void setTeam(GameTeam team) {
        this.team = team;
    }

    public GameTeamPlayer getTeamPlayer() {
        return this.teamPlayer;
    }

    public void setTeamPlayer(GameTeamPlayer teamPlayer) {
        this.teamPlayer = teamPlayer;
    }

    public void setPosition(int position) {
        this.teamPlayer.setPosition(position);
    }

    public boolean hasTeam() {
        return this.team != null;
    }
}
