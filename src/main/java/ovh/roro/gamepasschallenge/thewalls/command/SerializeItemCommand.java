package ovh.roro.gamepasschallenge.thewalls.command;

import com.google.gson.JsonPrimitive;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.Base64;

/**
 * This file is a part of TheWalls project.
 *
 * @author roro1506_HD
 */
public class SerializeItemCommand extends Command {

    public SerializeItemCommand() {
        super("serializeitem");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.isOp())
            return false;

        Player player = (Player) sender;
        ItemStack itemStack = player.getItemInHand();

        if (itemStack == null || itemStack.getType() == Material.AIR) {
            player.sendMessage("§cVous devez tenir un item en main pour le sérialiser !");
            return false;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        NBTTagCompound tag = new NBTTagCompound();

        CraftItemStack.asNMSCopy(itemStack).save(tag);

        try {
            NBTCompressedStreamTools.a(tag, (DataOutput) new DataOutputStream(outputStream));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String serialized = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        TextComponent component = new TextComponent("Cliquez : " + serialized);

        component.setColor(ChatColor.AQUA);
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, serialized));

        player.spigot().sendMessage(component);
        return true;
    }
}
