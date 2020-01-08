package ovh.roro.gamepasschallenge.thewalls.game;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.json.JsonBorder;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.runnable.TeleportRunnable;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.ScoreboardManager;
import ovh.roro.gamepasschallenge.thewalls.game.scoreboard.defaults.GameScoreboard;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;
import ovh.roro.gamepasschallenge.thewalls.game.team.TeamManager;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.File;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class GameManager {

    private static GameManager instance;

    private final Map<UUID, GamePlayer> playersByUuid;
    private final Random random;
    private final GameLoop gameLoop;
    private final TeamManager teamManager;

    private GameState state;
    private int timeElapsed;

    private ImmutableSet<GamePlayer> allPlayersCache;

    public GameManager() {
        instance = this;

        this.playersByUuid = new HashMap<>();
        this.random = new SecureRandom();
        this.gameLoop = new GameLoop();
        this.teamManager = new TeamManager(TheWalls.getInstance().getGameConfig().getTeams());
        this.state = GameState.WAITING;
    }

    public static GameManager getInstance() {
        return instance;
    }

    public void startGame() {
        if (this.getPlayers().stream().anyMatch(player -> player.getTeam() == null)) {
            this.broadcastMessage("§cTous les joueurs ne sont pas dans une équipe !");
            return;
        }

        // Create map
        World world;
        try {
            FileUtils.copyDirectory(new File(TheWalls.getInstance().getDataFolder(), "world"), new File("gameworld"));
            world = new WorldCreator("gameworld").createWorld();

            JsonBorder config = TheWalls.getInstance().getGameConfig().getBorder();

            world.getWorldBorder().setCenter(config.getCenterX(), config.getCenterZ());
            world.getWorldBorder().setSize(config.getStartSize());

            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doDaylightCycle", "true");
            world.setGameRuleValue("sendCommandFeedback", "false");
            world.setGameRuleValue("doMobSpawning", "true");
            world.setGameRuleValue("doTileDrops", "true");
        } catch (Exception ex) {
            TheWalls.getInstance().getLogger().log(Level.SEVERE, "An error occurred while creating game world", ex);
            this.broadcastMessage("§cUne erreur est survenue lors de la création du monde de jeu.");
            return;
        }

        this.state = GameState.TELEPORTING;
        this.gameLoop.reset();

        this.teamManager.getTeams().forEach(team -> {
            team.getJsonTeam().getSpawn().setWorld(world);
            team.getJsonTeam().getDoors().forEach(door -> {
                door.getMinCorner().setWorld(world);
                door.getMaxCorner().setWorld(world);
            });
            team.initialize();
        });

        TheWalls.getInstance().getGameConfig().getGame().getSpecSpawn().setWorld(world);

        Collection<GamePlayer> gamePlayers = this.getPlayers();

        this.getSpectators().forEach(player -> player.initialize(false));

        new TeleportRunnable(gamePlayers, () -> {
            ItemStack[] starter = TheWalls.getInstance().getGameConfig().getGame().getStarter().toArray(new ItemStack[0]);
            gamePlayers.forEach(player -> {
                // Reset player
                player.initialize(false);
                player.setAlive(true);

                Player bukkitPlayer = player.getPlayer();

                bukkitPlayer.setWalkSpeed(0.2F);
                bukkitPlayer.setFoodLevel(20);
                bukkitPlayer.removePotionEffect(PotionEffectType.JUMP);
                bukkitPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
                bukkitPlayer.setGameMode(GameMode.SURVIVAL);
                bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENDERDRAGON_GROWL, 10.0F, 1.0F);
                bukkitPlayer.getInventory().addItem(starter);
            });

            GameScoreboard gameScoreboard = ScoreboardManager.getInstance().getGameScoreboard();

            for (GamePlayer player : getAllPlayers())
                gameScoreboard.updatePlayers(player);

            this.state = GameState.IN_GAME;
            Bukkit.getScheduler().runTaskTimer(TheWalls.getInstance(), this.gameLoop, 20L, 20L);
        });
    }

    public void checkWin() {
        if (this.teamManager.getAliveTeams().size() == 0) {
            Bukkit.getScheduler().cancelTasks(TheWalls.getInstance());
            this.broadcastMessage("§cToutes les équipes ont été éliminées...");
            this.resetGame();
            return;
        }

        GameTeam winningTeam = null;
        for (GameTeam team : this.teamManager.getTeams()) {
            if (team.isEliminated())
                continue;

            if (winningTeam == null)
                winningTeam = team;
            else
                return;
        }

        if (winningTeam == null)
            return;

        winningTeam.setPosition(1);
        this.finishGame(winningTeam);
    }

    private void finishGame(GameTeam team) {
        this.state = GameState.FINISHED;
        Bukkit.getScheduler().cancelTasks(TheWalls.getInstance());

        this.broadcastMessage("§eVictoire de l'équipe " + team.getJsonTeam().getChatColor() + team.getJsonTeam().getNameFeminineSingular() + "§e !");

        List<GamePlayer> winners = team.getPlayers().stream()
                .map(this::getPlayer)
                .filter(player -> player.getPlayer().isOnline() && player.isAlive())
                .collect(Collectors.toList());

        for (int i = 0; i < winners.size(); i++)
            winners.get(i).setPosition(i + 1);

        new Thread(() -> {
            try {
                ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
                Invocable invocable = (Invocable) engine;

                engine.eval(TheWalls.getInstance().getGameConfig().getGame().getCsvScript());

                File file = new File("game.csv");

                if (file.exists())
                    file.delete();

                PrintWriter writer = new PrintWriter(file);

                for (GameTeam gameTeam : this.teamManager.getTeams())
                    writer.println(invocable.invokeFunction("mapToCsv", gameTeam));

                writer.flush();
                writer.close();
            } catch (Exception ex) {
                Bukkit.getScheduler().runTask(TheWalls.getInstance(), () -> this.broadcastMessage("§cUne erreur est survenue lors de la création du CSV."));
                ex.printStackTrace();
            }
        }).start();

        new BukkitRunnable() {
            private int timer = 10;

            @Override
            public void run() {
                if (this.timer-- == 0 || winners.isEmpty()) {
                    super.cancel();
                    GameManager.this.resetGame();
                    return;
                }

                for (GamePlayer winner : winners) {
                    if (!winner.getPlayer().isOnline())
                        continue;

                    Firework firework = winner.getPlayer().getWorld().spawn(winner.getPlayer().getLocation(), Firework.class);
                    FireworkEffect effect = FireworkEffect.builder()
                            .with(FireworkEffect.Type.values()[GameManager.this.random.nextInt(FireworkEffect.Type.values().length)])
                            .withColor(Color.fromRGB(GameManager.this.random.nextInt(256), GameManager.this.random.nextInt(256), GameManager.this.random.nextInt(256)))
                            .flicker(GameManager.this.random.nextBoolean())
                            .trail(GameManager.this.random.nextBoolean())
                            .build();

                    FireworkMeta meta = firework.getFireworkMeta();

                    meta.addEffect(effect);
                    meta.setPower(GameManager.this.random.nextInt(3) + 1);

                    firework.setFireworkMeta(meta);
                }

                winners.removeIf(player -> !player.getPlayer().isOnline());
            }
        }.runTaskTimer(TheWalls.getInstance(), 20L, 20L);
    }

    private void resetGame() {
        this.state = GameState.WAITING;
        this.timeElapsed = 0;

        this.playersByUuid.values().forEach(GamePlayer::reset);
        this.playersByUuid.values().forEach(player -> {
            player.getScoreboard().clearLines();
            ScoreboardManager.getInstance().getWaitingScoreboard().initScoreboard(player);
        });

        Bukkit.unloadWorld("gameworld", false);

        try {
            FileUtils.deleteDirectory(new File("gameworld"));
        } catch (Exception ex) {
            TheWalls.getInstance().getLogger().log(Level.SEVERE, "An error occurred while unloading game world", ex);
        }
    }

    public int getTimeElapsed() {
        return this.timeElapsed;
    }

    int increaseTimeElapsed() {
        return ++this.timeElapsed;
    }

    public GamePlayer addPlayer(Player player) {
        GamePlayer gamePlayer = this.playersByUuid.computeIfAbsent(player.getUniqueId(), unused -> new GamePlayer((CraftPlayer) player));

        gamePlayer.getScoreboard().create();

        if (this.state == GameState.WAITING) {
            ScoreboardManager.getInstance().getWaitingScoreboard().initScoreboard(gamePlayer);
            this.getAllPlayers().forEach(ScoreboardManager.getInstance().getWaitingScoreboard()::updatePlayers);
        } else
            ScoreboardManager.getInstance().getGameScoreboard().initScoreboard(gamePlayer);

        return gamePlayer;
    }

    public boolean removePlayer(Player player) {
        GamePlayer gamePlayer;
        if ((gamePlayer = this.playersByUuid.remove(player.getUniqueId())) == null)
            return false;

        if (this.state == GameState.WAITING)
            this.getAllPlayers().forEach(ScoreboardManager.getInstance().getWaitingScoreboard()::updatePlayers);

        if (gamePlayer.hasTeam())
            gamePlayer.getTeam().removePlayer(gamePlayer);

        return this.state == GameState.IN_GAME && !gamePlayer.isSpectator();
    }

    public GamePlayer getPlayer(UUID uuid) {
        return this.playersByUuid.get(uuid);
    }

    public void broadcastMessage(String message) {
        for (GamePlayer player : this.playersByUuid.values())
            player.sendMessage(message);
    }

    public GameState getState() {
        return this.state;
    }

    public TeamManager getTeamManager() {
        return this.teamManager;
    }

    public ImmutableSet<GamePlayer> getPlayers() {
        ImmutableSet.Builder<GamePlayer> builder = ImmutableSet.builder();

        for (GamePlayer player : this.playersByUuid.values())
            if (!player.isSpectator())
                builder.add(player);

        return builder.build();
    }

    public ImmutableSet<GamePlayer> getAlivePlayers() {
        ImmutableSet.Builder<GamePlayer> builder = ImmutableSet.builder();

        for (GamePlayer player : this.playersByUuid.values())
            if (!player.isSpectator() && player.isAlive())
                builder.add(player);

        return builder.build();
    }

    public ImmutableSet<GamePlayer> getSpectators() {
        ImmutableSet.Builder<GamePlayer> builder = ImmutableSet.builder();

        for (GamePlayer player : this.playersByUuid.values())
            if (player.isSpectator())
                builder.add(player);

        return builder.build();
    }

    public ImmutableSet<GamePlayer> getAllPlayers() {
        if (this.allPlayersCache == null || this.allPlayersCache.size() != this.playersByUuid.size())
            this.allPlayersCache = ImmutableSet.copyOf(this.playersByUuid.values());

        return this.allPlayersCache;
    }
}
