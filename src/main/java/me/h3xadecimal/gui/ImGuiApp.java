package me.h3xadecimal.gui;

import static imgui.ImGui.*;
import static imgui.flag.ImGuiConfigFlags.*;

import imgui.ImGuiIO;
import imgui.ImGuiStyle;
import imgui.app.Application;
import imgui.app.Configuration;
import imgui.internal.ImGuiContext;
import me.h3xadecimal.Main;
import me.h3xadecimal.utils.AWTUtils;
import me.h3xadecimal.utils.CustomGLFW;
import me.h3xadecimal.utils.RandomUtils;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;


public class ImGuiApp extends Application {
    private final CustomGLFW glfw = new CustomGLFW();
    private ImGuiContext context;
    private int frame = Main.prop.getRefreshInterval();

    private int lastCount = 0;
    private HashMap<Integer, Point> lastPos = new HashMap<>();
    private HashMap<Integer, String> lastName = new HashMap<>();

    @Override
    protected void configure(Configuration config) {
        config.setTitle("Cool Frame");
    }

    @Override
    protected void initImGui(Configuration config) {
        context = createContext();
        setCurrentContext(context);

        ImGuiStyle style = getStyle();
        style.setWindowRounding(0f);
        style.setWindowBorderSize(0f);
        style.setWindowPadding(0f, 0f);

        ImGuiIO io = getIO();
        io.addConfigFlags(DockingEnable);
        io.addConfigFlags(ViewportsEnable);
        io.setConfigViewportsNoTaskBarIcon(true);
        io.setConfigViewportsNoDecoration(true);
    }

    @Override
    protected void preProcess() {
        glfw.setViewportWindowsHidden(true);
    }

    @Override
    public void process() {
        frame ++;

        if (frame % Main.prop.getRefreshInterval() == 0) {
            lastCount = new Random().nextInt(Main.prop.getMinCount(), Main.prop.getMaxCount());
            refreshPositions();
        }

        for (Integer key: lastPos.keySet()) {
            Point pos = lastPos.get(key);
            setNextWindowSize(40, 90);
            setNextWindowPos(pos.x, pos.y);
            setNextWindowBgAlpha(20);

            if (begin(String.valueOf(key))) {
                text(lastName.get(key));
                end();
            }
        }

        if (frame % 1000 == 0) System.gc();
    }

    private void refreshPositions() {
        System.out.println("Refreshing position for " + lastCount + " frames");
        lastPos.clear();
        lastName.clear();
        for (int i = 0; i < lastCount; i++) {
            Random rdx = new Random();
            Random rdy = new Random();

            lastPos.put(i, new Point(rdx.nextInt(AWTUtils.getWidth()-100), rdy.nextInt(AWTUtils.getHeight()-100)));
            lastName.put(i, RandomUtils.getRandomName());
        }
    }
}
