package net.miau.simpleveinminer;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Random;

public final class SimpleVeinMiner extends JavaPlugin implements Listener {

    private final ArrayList<BlockFace> blockFaces = new ArrayList<>();
    private boolean needsPermission = false;

    @Override
    public void onEnable() {
        getConfig().options().header("Permission: simpleveinminer.use");
        getConfig().options().copyDefaults(true);
        saveConfig();
        if (!getConfig().contains("needsPermission")) {
            getConfig().set("needsPermission", this.needsPermission);
            saveConfig();
        }
        this.needsPermission = getConfig().getBoolean("needsPermission");

        Bukkit.getPluginManager().registerEvents(this, this);

        blockFaces.add(BlockFace.UP);
        blockFaces.add(BlockFace.DOWN);
        blockFaces.add(BlockFace.NORTH);
        blockFaces.add(BlockFace.SOUTH);
        blockFaces.add(BlockFace.EAST);
        blockFaces.add(BlockFace.WEST);

        new Metrics(this, 17391);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()
                || !event.getBlock().getType().toString().contains("_ORE")
                || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (this.needsPermission && !event.getPlayer().hasPermission("simpleveinminer.use")) return;
        ItemStack hand = event.getPlayer().getInventory().getItemInMainHand();
        ArrayList<Block> allBlocks = new ArrayList<>(blocksAround(event.getBlock()));
        int newBlocks = allBlocks.size();
        while (newBlocks > 0) {
            ArrayList<Block> toAdd = new ArrayList<>();
            for (Block block : allBlocks) {
                for (Block around : blocksAround(block)) {
                    if (allBlocks.contains(around) || toAdd.contains(around)) continue;
                    toAdd.add(around);
                }
            }
            newBlocks = toAdd.size();
            allBlocks.addAll(toAdd);
        }
        for (Block toBreak : allBlocks) {
            if (toBreak.breakNaturally(hand)) {
                if (hand.getEnchantments().containsKey(Enchantment.DURABILITY)) {
                    int chance = 1 + hand.getEnchantments().get(Enchantment.DURABILITY);
                    if (new Random().nextInt(0, chance) == 0) {
                        hand.setDurability((short)(hand.getDurability() + 1));
                        if (hand.getType().getMaxDurability() <= hand.getDurability()) {
                            break;
                        }
                    }
                } else {
                    hand.setDurability((short)(hand.getDurability() + 1));
                }
                if (hand.getType().getMaxDurability() == hand.getDurability()) {
                    hand.setType(Material.getMaterial("AIR"));
                    break;
                }
            }
        }
    }

    private ArrayList<Block> blocksAround(Block block) {
        ArrayList<Block> newBlocks = new ArrayList<>();
        for (BlockFace face : blockFaces) {
            Block b = block.getRelative(face);
            if (b.getType() != block.getType()) continue;
            newBlocks.add(b);
        }
        return newBlocks;
    }
}
