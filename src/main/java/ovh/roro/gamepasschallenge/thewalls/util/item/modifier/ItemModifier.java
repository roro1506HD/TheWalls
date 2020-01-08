package ovh.roro.gamepasschallenge.thewalls.util.item.modifier;

import net.minecraft.server.v1_8_R3.ChatComponentText;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagInt;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import ovh.roro.gamepasschallenge.thewalls.util.item.IItemModifier;
import ovh.roro.gamepasschallenge.thewalls.util.item.ItemBuilder;

/**
 * @author roro1506_HD
 */
public class ItemModifier implements IItemModifier {
    private final ItemBuilder builder;

    private NBTTagList enchantments = new NBTTagList();
    private NBTTagList lore = new NBTTagList();
    private NBTTagInt hideFlags = new NBTTagInt(0);

    private NBTTagString name;
    private NBTTagByte unbreakable;

    public ItemModifier(ItemBuilder builder) {
        this.builder = builder;
    }

    public ItemModifier addLore(String... lore) {
        for (String s : lore)
            this.lore.add(new NBTTagString(s));
        return this;
    }

    public ItemModifier setName(String title) {
        this.name = new NBTTagString(title);
        return this;
    }

    public ItemModifier setUnbreakable(boolean unbreakable) {
        if (unbreakable)
            this.unbreakable = new NBTTagByte((byte) 1);
        else
            this.unbreakable = null;
        return this;
    }

    public ItemModifier addEnchantment(Enchantment enchantment, int level) {
        NBTTagCompound enchantmentTag = new NBTTagCompound();

        enchantmentTag.setShort("id", (short) enchantment.getId());
        enchantmentTag.setShort("lvl", (short) level);

        this.enchantments.add(enchantmentTag);
        return this;
    }

    public ItemModifier addItemFlags(ItemFlag... itemFlags) {
        int flags = this.hideFlags.d();

        for (ItemFlag flag : itemFlags)
            flags |= 1 << flag.ordinal();

        this.hideFlags = new NBTTagInt(flags);
        return this;
    }

    @Override
    public ItemBuilder apply() {
        NBTTagCompound displayTag = new NBTTagCompound();

        if (this.name != null)
            displayTag.set("Name", this.name);

        if (!this.lore.isEmpty())
            displayTag.set("Lore", this.lore);

        if (this.unbreakable != null)
            this.builder.addNBTTag("Unbreakable", this.unbreakable);

        if (!this.enchantments.isEmpty())
            this.builder.addNBTTag("Enchantments", this.enchantments);

        if (this.hideFlags.d() != 0)
            this.builder.addNBTTag("HideFlags", this.hideFlags);

        return this.builder.addNBTTag("display", displayTag);
    }
}
