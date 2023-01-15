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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public final class SimpleVeinMiner extends JavaPlugin implements Listener {

    private final ArrayList<BlockFace> blockFaces = new ArrayList<>();
    private boolean needsPermission = false;
    private boolean checkForDiagonalOres = false;
    private boolean cancelIfSneaking = false;

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
        if (!getConfig().contains("checkForDiagonalOres")) {
            getConfig().set("checkForDiagonalOres", this.checkForDiagonalOres);
            saveConfig();
        }
        this.checkForDiagonalOres = getConfig().getBoolean("checkForDiagonalOres");
        if (!getConfig().contains("cancelIfSneaking")) {
            getConfig().set("cancelIfSneaking", this.cancelIfSneaking);
            saveConfig();
        }
        this.cancelIfSneaking = getConfig().getBoolean("cancelIfSneaking");

        Bukkit.getPluginManager().registerEvents(this, this);

        blockFaces.add(BlockFace.UP);
        blockFaces.add(BlockFace.DOWN);
        blockFaces.add(BlockFace.NORTH);
        blockFaces.add(BlockFace.SOUTH);
        blockFaces.add(BlockFace.EAST);
        blockFaces.add(BlockFace.WEST);

        new Metrics(this, 17391);

        try {
            URL url = new URL("https://api.github.com/repos/innocentmiau/SimpleVeinMiner/releases/latest");
            String s = stream(url);
            String version = s.substring(s.indexOf("\"tag_name\":\"") + 13, s.indexOf("\"target_commitish\"") - 2);
            if (!version.equals(this.getDescription().getVersion())) {
                getLogger().info("---[SimpleVeinMiner]---");
                getLogger().info("[>] There is a new update available.");
                getLogger().info("[>] current version: " + this.getDescription().getVersion());
                getLogger().info("[>] latest version: " + version);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
    }

    public String stream(URL url) throws IOException {
        try (InputStream input = url.openStream()) {
            InputStreamReader isr = new InputStreamReader(input);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return json.toString();
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()
                || !event.getBlock().getType().toString().contains("_ORE")
                || event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        if (this.needsPermission && !event.getPlayer().hasPermission("simpleveinminer.use")) return;
        if (this.cancelIfSneaking && event.getPlayer().isSneaking()) return;
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
        if (allBlocks.size() == 0) return;
        allBlocks.remove(event.getBlock());
        // event.getPlayer().sendMessage("Experience: " + event.getExpToDrop());
        // event.getPlayer().sendMessage("Blocks: " + allBlocks.size());
        event.setExpToDrop(event.getExpToDrop() * (allBlocks.size() + 1));
        // event.getPlayer().sendMessage("Total Experience: " + event.getExpToDrop());
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
        if (this.checkForDiagonalOres) {
            // top row
            for (int i = -1; i<2; i++) {
                checkBlock(block.getType(), block.getRelative(1, i, 1), newBlocks);
                checkBlock(block.getType(), block.getRelative(1, i, 0), newBlocks);
                checkBlock(block.getType(), block.getRelative(1, i, -1), newBlocks);

                checkBlock(block.getType(), block.getRelative(0, i, 1), newBlocks);
                checkBlock(block.getType(), block.getRelative(0, i, -1), newBlocks);

                checkBlock(block.getType(), block.getRelative(-1, i, 1), newBlocks);
                checkBlock(block.getType(), block.getRelative(-1, i, 0), newBlocks);
                checkBlock(block.getType(), block.getRelative(-1, i, -1), newBlocks);
            }
        }
        return newBlocks;
    }

    private void checkBlock(Material type, Block block, ArrayList<Block> blocks) {
        if (!blocks.contains(block) && block.getType() == type) {
            blocks.add(block);
        }
    }
}
