package com.nyx1024.petrified.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fluent builder for creating ItemStacks.
 */
public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(@NotNull Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(@NotNull ItemStack item) {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    /**
     * Set the display name.
     */
    @NotNull
    public ItemBuilder name(@Nullable String name) {
        if (meta != null) {
            meta.setDisplayName(name);
        }
        return this;
    }

    /**
     * Add lore lines.
     */
    @NotNull
    public ItemBuilder lore(@NotNull String... lore) {
        return lore(Arrays.asList(lore));
    }

    /**
     * Add lore lines from list.
     */
    @NotNull
    public ItemBuilder lore(@NotNull List<String> lore) {
        if (meta != null) {
            meta.setLore(new ArrayList<>(lore));
        }
        return this;
    }

    /**
     * Add an enchantment.
     */
    @NotNull
    public ItemBuilder enchant(@NotNull Enchantment enchant, int level) {
        if (meta != null) {
            meta.addEnchant(enchant, level, true);
        }
        return this;
    }

    /**
     * Add item flags.
     */
    @NotNull
    public ItemBuilder flags(@NotNull ItemFlag... flags) {
        if (meta != null) {
            meta.addItemFlags(flags);
        }
        return this;
    }

    /**
     * Set unbreakable.
     */
    @NotNull
    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta != null) {
            meta.setUnbreakable(unbreakable);
        }
        return this;
    }

    /**
     * Set the amount.
     */
    @NotNull
    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    /**
     * Build the final ItemStack.
     */
    @NotNull
    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}
