package com.killing3k.gamepoker;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MetaItem extends ItemStack {

    public MetaItem(Material type, int meta, String name, List<String> lore) {
        super(type, 1, (byte) meta);
        ItemMeta nmeta = getItemMeta();
        nmeta.setDisplayName(name);
        nmeta.setLore(lore);
        setItemMeta(nmeta);
    }

    public MetaItem(Material type, int meta, String name) {
        super(type, 1, (byte) meta);
        ItemMeta nmeta = getItemMeta();
        nmeta.setDisplayName(name);
        setItemMeta(nmeta);
    }

    public MetaItem(Material type, int meta) {
        super(type, 1, (byte) meta);
    }

}
