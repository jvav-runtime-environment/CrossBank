package cn.JvavRE.crossBank.utils;

public class ServerType {
    private static Boolean isFolia;

    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
        }

        return isFolia;
    }
}
