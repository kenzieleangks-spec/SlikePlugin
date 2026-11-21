package com.example.slikesmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements Listener {

    static class ProfileHolder implements InventoryHolder {
        Inventory inv;
        @Override public Inventory getInventory() { return inv; }
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SlikeSMP aktif!");
    }

    private Component txt(String s, NamedTextColor c) {
        return Component.text(s, c).decoration(TextDecoration.ITALIC, false);
    }

    private ItemStack item(Material m, String name, NamedTextColor color, String... lore) {
        ItemStack it = new ItemStack(m);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(txt(name, color).decorate(TextDecoration.BOLD));
        List<Component> l = new ArrayList<>();
        for (String s : lore) l.add(txt(s, NamedTextColor.GRAY));
        meta.lore(l);
        it.setItemMeta(meta);
        return it;
    }

    private void openProfile(Player p) {
        ProfileHolder holder = new ProfileHolder();
        Inventory inv = getServer().createInventory(holder, 27,
                Component.text("Profile - " + p.getName()));
        holder.inv = inv;

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) head.getItemMeta();
        sm.setOwningPlayer(p);
        sm.displayName(txt(p.getName(), NamedTextColor.AQUA).decorate(TextDecoration.BOLD));
        sm.lore(List.of(
                txt("Lv." + p.getLevel(), NamedTextColor.BLUE),
                txt("Ping: " + p.getPing() + "ms", NamedTextColor.GRAY)));
        head.setItemMeta(sm);
        inv.setItem(4, head);

        inv.setItem(10, item(Material.PAPER, "PROFILE", NamedTextColor.AQUA, "Lihat data profile kamu"));
        inv.setItem(12, item(Material.LIME_DYE, "DAILY QUEST", NamedTextColor.GREEN, "Misi harian kamu"));
        inv.setItem(14, item(Material.NETHER_STAR, "LEVEL PASS", NamedTextColor.GOLD, "Hadiah tiap level"));
        inv.setItem(16, item(Material.EMERALD, "MILESTONE", NamedTextColor.DARK_GREEN, "Pencapaian server"));

        p.openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getView().getTopInventory().getHolder() instanceof ProfileHolder)) return;
        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (e.getClickedInventory() != e.getView().getTopInventory()) return;

        String msg = switch (e.getSlot()) {
            case 10 -> "Level: " + p.getLevel() + " | Nama: " + p.getName();
            case 12 -> "Daily Quest belum tersedia.";
            case 14 -> "Level Pass belum tersedia.";
            case 16 -> "Milestone belum tersedia.";
            default -> null;
        };
        if (msg != null) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
            p.sendMessage(txt(msg, NamedTextColor.YELLOW));
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (e.getView().getTopInventory().getHolder() instanceof ProfileHolder) {
            e.setCancelled(true);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("profile")) {
            if (sender instanceof Player p) {
                openProfile(p);
            } else {
                sender.sendMessage("Hanya untuk pemain.");
            }
            return true;
        }
        return false;
    }
  }
