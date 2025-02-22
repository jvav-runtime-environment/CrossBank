package cn.JvavRE.crossBank.utils;

public class Digit {
    public static boolean isDigit(String s){
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
