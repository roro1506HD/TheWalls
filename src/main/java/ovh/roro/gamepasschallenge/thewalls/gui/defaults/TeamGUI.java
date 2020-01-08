package ovh.roro.gamepasschallenge.thewalls.gui.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ovh.roro.gamepasschallenge.thewalls.game.GameManager;
import ovh.roro.gamepasschallenge.thewalls.game.player.GamePlayer;
import ovh.roro.gamepasschallenge.thewalls.game.team.GameTeam;
import ovh.roro.gamepasschallenge.thewalls.gui.AbstractGUI;
import ovh.roro.gamepasschallenge.thewalls.util.item.ItemBuilder;
import ovh.roro.gamepasschallenge.thewalls.util.item.modifier.BannerModifier;
import ovh.roro.gamepasschallenge.thewalls.util.item.modifier.ItemModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class TeamGUI extends AbstractGUI {

    @Override
    public void display(Player player) {
        this.inventory = Bukkit.createInventory(null, (int) Math.ceil(GameManager.getInstance().getTeamManager().getTeams().size() / 9.0D) * 9, "§aSélecteur d'équipe");

        this.update(player);

        player.openInventory(this.inventory);
    }

    @Override
    public void update(Player player) {
        this.clearSlotData();

        List<GameTeam> teams = new ArrayList<>(GameManager.getInstance().getTeamManager().getTeams());

        for (int i = 0; i < teams.size(); i++) {
            GameTeam team = teams.get(i);

            this.setSlotData(this.getTeamBanner(team, player), i, "team_" + team.getJsonTeam().getId());
        }
    }

    @Override
    public void onClick(Player player, ItemStack itemStack, String action, int slot, ClickType clickType) {
        int teamId = Integer.parseInt(action.replace("team_", ""));
        GameTeam team = GameManager.getInstance().getTeamManager().getById(teamId);
        GamePlayer gamePlayer = GameManager.getInstance().getPlayer(player.getUniqueId());

        if (team.hasPlayer(player.getUniqueId()) || team.getPlayers().size() == team.getJsonTeam().getSize() || gamePlayer.isSpectator()) {
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 10.0F, 1.0F);
            return;
        }

        team.addPlayer(gamePlayer);
        gamePlayer.sendMessage("§7Vous avez rejoint l'équipe " + team.getJsonTeam().getChatColor() + team.getJsonTeam().getNameFeminineSingular());

        super.update();
    }

    private ItemStack getTeamBanner(GameTeam team, Player player) {
        ChatColor color = team.getJsonTeam().getChatColor();
        List<GamePlayer> players = team.getPlayers().stream()
                .map(GameManager.getInstance()::getPlayer)
                .collect(Collectors.toList());

        ItemModifier itemModifier = ItemBuilder.of(Material.BANNER)
                .setAmount(players.size())
                .getModifier(BannerModifier.class)
                .setBase(team.getJsonTeam().getDyeColor())
                .apply()
                .getModifier(ItemModifier.class)
                .setName(color + "Equipe " + team.getJsonTeam().getNameFeminineSingular())
                .addLore("§7Membres (" + players.size() + "/" + team.getJsonTeam().getSize() + ") :")
                .addLore("");

        for (int i = 0; i < team.getJsonTeam().getSize(); i++) {
            GamePlayer tempPlayer = i < players.size() ? players.get(i) : null;

            if (tempPlayer == null)
                itemModifier.addLore("§c▪ Personne");
            else
                itemModifier.addLore("§7▪ " + color + (tempPlayer.getUniqueId().equals(player.getUniqueId()) ? "§l" : "") + tempPlayer.getName());
        }

        itemModifier.addLore("");

        if (team.hasPlayer(player.getUniqueId()))
            itemModifier.addLore("§c» Vous êtes déjà dans cette équipe");
        else
            itemModifier.addLore("§e» Cliquez pour rejoindre cette équipe");

        return itemModifier
                .apply()
                .toBukkitItemStack();
    }
}
