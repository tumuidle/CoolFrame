package me.h3xadecimal;

import imgui.ImGui;
import imgui.app.Application;
import me.h3xadecimal.gui.ImGuiApp;
import me.h3xadecimal.gui.UiMain;
import me.h3xadecimal.utils.ConfigProperties;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Main {
    public static final File PROP_FILE = new File("cool-frame.properties");
    public static final ConfigProperties prop = new ConfigProperties();
    public static final UiMain main;

    public static final ImGuiApp app;
    public static final Thread imguiThread;

    static {
        try {
            main = new UiMain();
        } catch (Exception e) {
            throw new RuntimeException("加载出错", e);
        }

        app = new ImGuiApp();
        imguiThread = new Thread(() -> {
            Application.launch(app);
        });
        imguiThread.setName("ImGui");
    }

    public static void main(String[] args) throws Exception {
        if (!PROP_FILE.exists()) PROP_FILE.createNewFile();
        prop.load(PROP_FILE.toPath());

        main.setVisible(true);
        imguiThread.start();
    }

    public static void exit(int code) {
        try {
            Files.writeString(PROP_FILE.toPath(), prop.dump(), StandardCharsets.UTF_8);
            Files.deleteIfExists(new File("imgui.ini").toPath());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.exit(code);
    }
}