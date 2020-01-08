package ovh.roro.gamepasschallenge.thewalls.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import ovh.roro.gamepasschallenge.thewalls.TheWalls;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.GameState;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;
import ovh.roro.gamepasschallenge.thewalls.gui.GUIManager;
import ovh.roro.gamepasschallenge.thewalls.gui.defaults.TeamGUI;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        GameManager.getInstance().addPlayer(player).initialize(true);

        event.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GameManager gameManager = GameManager.getInstance();
        GamePlayer gamePlayer = gameManager.getPlayer(player.getUniqueId());
        GameTeam gameTeam = gamePlayer.getTeam();
        boolean eliminated = gameManager.removePlayer(player);

        if (gameManager.getState() != GameState.WAITING && gameManager.getState() != GameState.FINISHED && eliminated) {
            gamePlayer.setAlive(false);
            gamePlayer.setPosition(gameManager.getAlivePlayers().size() + 1);
            gameManager.broadcastMessage("§c" + player.getName() + " s'est déconnecté et est par conséquent éliminé.");

            System.out.println("[DEBUG] " + gamePlayer.getName() + " eliminated offline (" + gamePlayer.getKills() + " kills, #" + gamePlayer.getTeamPlayer().getPosition() + ")");

            if (gameTeam.isEliminated()) {
                gameTeam.setPosition(gameManager.getTeamManager().getAliveTeams().size() + 1);
                gameManager.broadcastMessage("§cL'équipe " + gameTeam.getJsonTeam().getChatColor() + gameTeam.getJsonTeam().getNameFeminineSingular() + " §cest éliminée ! Elle termine #" + gameTeam.getPosition() + " !");
                System.out.println("[DEBUG] \"" + gameTeam.getJsonTeam().getNameMasculineSingular() + "\" eliminated (" + gameTeam.getTotalKills() + " kills, #" + gameTeam.getPosition() + ")");
            }

            gameManager.checkWin();
        }

        event.setQuitMessage(null);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        GameManager gameManager = GameManager.getInstance();
        GamePlayer gamePlayer = gameManager.getPlayer(event.getEntity().getUniqueId());

        if (gameManager.getState() != GameState.IN_GAME || !gamePlayer.isAlive()) {
            event.setCancelled(true);
            return;
        }

        Bukkit.getScheduler().runTask(TheWalls.getInstance(), () -> {
            gamePlayer.updateHealth();
            gameManager.getSpectators().forEach(GamePlayer::updateShownHealth);
        });
    }

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Bukkit.getScheduler().runTask(TheWalls.getInstance(), () -> {
            GameManager gameManager = GameManager.getInstance();

            gameManager.getPlayer(event.getEntity().getUniqueId()).updateHealth();
            gameManager.getSpectators().forEach(GamePlayer::updateShownHealth);
        });
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        GameManager gameManager = GameManager.getInstance();

        if ((gameManager.getState() != GameState.IN_GAME &&
                gameManager.getState() != GameState.FINISHED) ||
                !gameManager.getPlayer(event.getPlayer().getUniqueId()).isSpectator() ||
                !(event.getRightClicked() instanceof Player))
            return;

        Player player = (Player) event.getRightClicked();

        if (GameManager.getInstance().getPlayer(player.getUniqueId()).isSpectator())
            return;

        Inventory inventory = Bukkit.createInventory(null, 45, "Inventaire de " + player.getName());

        ItemStack[] contents = new ItemStack[45];

        System.arraycopy(player.getInventory().getContents(), 0, contents, 9, 36);
        System.arraycopy(player.getInventory().getArmorContents(), 0, contents, 0, 4);

        inventory.setContents(contents);

        event.getPlayer().openInventory(inventory);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().startsWith("RIGHT_"))
            return;

        Player player = event.getPlayer();
        ItemStack clickedItem = event.getItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR)
            return;

        if (clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName() && clickedItem.getItemMeta().getDisplayName().equals("§aSélecteur d'équipe §8▪ §7Clic-droit"))
            GUIManager.getInstance().openGUI(player, new TeamGUI());
    }

    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        if (GameManager.getInstance().getState() != GameState.IN_GAME)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        GameManager gameManager = GameManager.getInstance();

        if (gameManager.getState() != GameState.IN_GAME)
            return;

        GamePlayer player = gameManager.getPlayer(event.getPlayer().getUniqueId());

        if (player.getTeam().getDoors().stream().anyMatch(door -> door.contains(event.getBlock().getLocation()))) {
            int minutesBeforeOpening = (TheWalls.getInstance().getGameConfig().getBorder().getShrinkStart() - GameManager.getInstance().getTimeElapsed()) / 60;
            player.sendMessage("§cPatientez ! La porte s'ouvrira toute seule dans §l" + (minutesBeforeOpening == 0 ? "moins d'une minute" : minutesBeforeOpening == 1 ? "une minute" : minutesBeforeOpening + " minutes") + "§c...");
            event.setCancelled(true);
        } else if (TheWalls.getInstance().getGameConfig().getGame().getUnbreakableBlocks().contains(event.getBlock().getType())) {
            player.sendMessage("§cNous sommes désolés, mais ce bloc est incassable pour des raisons logistiques.");
            event.setCancelled(true);
        }
    }
}
