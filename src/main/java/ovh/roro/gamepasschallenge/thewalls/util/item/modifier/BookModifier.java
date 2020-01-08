package ovh.roro.gamepasschallenge.thewalls.util.item.modifier;

import net.minecraft.server.v1_8_R3.NBTTagByte;
import net.minecraft.server.v1_8_R3.NBTTagList;
import net.minecraft.server.v1_8_R3.NBTTagString;
import ovh.roro.gamepasschallenge.thewalls.util.item.IItemModifier;
import ovh.roro.gamepasschallenge.thewalls.util.item.ItemBuilder;

/**
 * @author roro1506_HD
 */
public class BookModifier implements IItemModifier
{
    private final ItemBuilder builder;

    private final NBTTagList pages = new NBTTagList();
    private NBTTagString author = new NBTTagString("");
    private NBTTagString title = new NBTTagString("");

    public BookModifier(ItemBuilder builder)
    {
        this.builder = builder;
    }

    public BookModifier addPage(String page)
    {
        this.pages.add(new NBTTagString(page));
        return this;
    }

    public BookModifier setTitle(String title)
    {
        this.title = new NBTTagString(title);
        return this;
    }

    public BookModifier setAuthor(String author)
    {
        this.author = new NBTTagString(author);
        return this;
    }

    @Override
    public ItemBuilder apply()
    {
        return this.builder
                .addNBTTag("pages", this.pages)
                .addNBTTag("author", this.author)
                .addNBTTag("title", this.title)
                .addNBTTag("resolved", new NBTTagByte((byte) 1));
    }
}
