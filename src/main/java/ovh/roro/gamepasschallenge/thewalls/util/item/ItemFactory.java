package ovh.roro.gamepasschallenge.thewalls.util.item;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import ovh.roro.gamepasschallenge.thewalls.util.item.modifier.BannerModifier;
import ovh.roro.gamepasschallenge.thewalls.util.item.modifier.ItemModifier;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class ItemFactory {

    public static final ItemStack TEAM_SELECTOR = ItemBuilder.of(Material.BANNER)
            .getModifier(ItemModifier.class)
            .setName("§aSélecteur d'équipe §8▪ §7Clic-droit")
            .addItemFlags(ItemFlag.HIDE_POTION_EFFECTS)
            .apply()
            .getModifier(BannerModifier.class)
            .setBase(DyeColor.WHITE)
            .apply()
            .toBukkitItemStack();

}
