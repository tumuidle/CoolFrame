package me.h3xadecimal.utils;

import java.awt.*;

public class AWTUtils {
    private static final int width;
    private static final int height;

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

    static {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension size = tk.getScreenSize();
        width = size.width;
        height = size.height;
    }
}
