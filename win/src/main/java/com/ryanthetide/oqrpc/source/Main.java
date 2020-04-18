package com.ryanthetide.oqrpc.source;

import com.ryanthetide.oqrpc.api.ApiReceiver;
import com.ryanthetide.oqrpc.api.ApiSender;
import com.ryanthetide.oqrpc.gui.ConfigGUI;
import com.ryanthetide.oqrpc.gui.AlreadyRunningGUI;
import mslinks.ShellLink;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.net.*;

public class Main {

    public static void main(String[] args) {

        Config.init();
        bootInit();
        checkUtil();

        try {
            new ApiReceiver();
        } catch (Exception e) {
            AlreadyRunningGUI.open();
        }

        UpdateChecker.check(false);
        SystemTrayHandler.systemTray();

        if (Config.getAddress().isEmpty()) {
            ConfigGUI.open();
        } else {
            ApiSender.ask(Main.getUrl(), new JSONObject().put("message", "startup").put("address", Main.getIp()));
        }
    }

    private static void bootInit() {
        try {
            File startupFolder = new File(System.getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\");
            try {
                new File(startupFolder.getAbsolutePath() + "\\oqrpc.bat").delete();
            } catch (Exception ignored) {}

            ShellLink sL = ShellLink.createLink(Config.getUpdater());
            sL.setCMDArgs("boot");
            sL.saveTo(startupFolder.getPath() + "\\oqrpc.lnk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void checkUtil() {
        File utilJar = new File(Config.getUpdater());
        if (utilJar.exists()) utilJar.delete();
        URL utilUrl = Main.class.getResource("/oqrpc/Util.jar");
        try {
            FileUtils.copyURLToFile(utilUrl, utilJar);
        } catch (Exception e) {e.printStackTrace();}
    }

    public static String getIp() {
        String ip = "";
        try(final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (Exception ignored) {}
        return ip;
    }

    public static String getUrl() {
        return "http://" + Config.getAddress() + ":8080";
    }
}
