/*
 * The MIT License.
 *
 *  Copyright (c) 2019 Nekoneko
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package dev.nekoneko.liquidstopper;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class LiquidStopper extends JavaPlugin implements Listener {

    private static final String PREFIX = ChatColor.GRAY + "[" + ChatColor.BLUE + "Liquid" + ChatColor.RED + "Stopper" + ChatColor.GRAY + "] " + ChatColor.RESET;

    private boolean water, lava;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.getServer().getPluginManager().registerEvents(this, this);

        new Metrics(this, 4769);
    }

    @Override
    public void onDisable() {
        this.customSaveConfig();
    }

    private void loadConfig() {
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        this.water = config.getBoolean("stopper.water", false);
        this.lava = config.getBoolean("stopper.lava", false);
    }

    private void customSaveConfig() {
        this.saveDefaultConfig();

        FileConfiguration config = this.getConfig();
        config.set("stopper.water", this.water);
        config.set("stopper.lava", this.lava);
        try {
            config.save(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "Failed to save config.yml", ex);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String first, String[] args) {
        if (!sender.hasPermission("liquidstopper.command")) {
            sender.sendMessage(PREFIX + "権限を持っていません");
            return true;
        }
        if (first.equalsIgnoreCase("stopwater")) {
            this.water = !this.water;
            sender.sendMessage(PREFIX + (this.water ? "水が流れなくなったよ！" : "水が流れちゃうよ"));
        } else if (first.equalsIgnoreCase("stoplava")) {
            this.lava = !this.lava;
            sender.sendMessage(PREFIX + (this.lava ? "溶岩が流れなくなったよ！" : "溶岩が流れちゃうよ"));
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            if (first.equalsIgnoreCase("givewater")) {
                player.getInventory().addItem(new ItemStack(Material.WATER_BUCKET));
            } else if (first.equalsIgnoreCase("givelava")) {
                player.getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
            }
        } else {
            sender.sendMessage(PREFIX + "プレイヤーだけだよ");
        }
        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockFromTo(BlockFromToEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.WATER) {
            event.setCancelled(water);
        } else if (block.getType() == Material.LAVA) {
            event.setCancelled(lava);
        } else if (block.getBlockData() instanceof Waterlogged) {
            event.setCancelled(water);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void blockPhysics(BlockPhysicsEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.WATER) {
            event.setCancelled(water);
        } else if (block.getType() == Material.LAVA) {
            event.setCancelled(lava);
        }
    }
}
