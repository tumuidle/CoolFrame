package me.h3xadecimal.utils;

import java.util.Random;

public class RandomUtils {
    // 问就是各路职业选手和主播
    public static final String[] names = {
            "NertZ", "Snappi", "VLDN", "SunPayrus", "sAw",
            "s1mple", "b1t", "iM", "Aleksib", "jL", "B1ad3"
    };

    public static String getRandomName() {
        Random rd = new Random();
        return names[rd.nextInt(names.length)];
    }
}
