package edu.ufl.cise.plpfa22;

import java.util.Objects;

public class stringRuntime {

    static int a=0;
    static int b=0;
    static int c=0;
    int temp=0;

    public static boolean ge(String arguments1, String arguments2){
        return arguments1.endsWith(arguments2);}
    public static boolean le(String arguments1, String arguments2){return arguments2.startsWith(arguments1);}

    public static boolean not(boolean arguments) {
        return !arguments;
    }

    public static boolean lt(int arguments1 , int arguments2) {
        return arguments1 < arguments2;
    }

    public static boolean gt(int arguments1 , int arguments2) {return arguments1 > arguments2;}

    public static boolean eq(int arguments1 , int arguments2) {
        return arguments1 == arguments2;
    }
    
//    public static boolean ntEq(int arguments1 , int arguments2) {
//        return arguments1 != arguments2;
//    }

    public static boolean eq(boolean arguments1 , boolean arguments2) {
        return arguments1 == arguments2;
    }

    public static boolean lt(boolean arguments1 , boolean arguments2) {
        return !arguments1 && arguments2;
    }

    public static boolean gt(boolean arguments1 , boolean arguments2) {
        return arguments1 && !arguments2;
    }

    //suffix
    public static boolean gt(String arguments1, String arguments2){
        if(Objects.equals(arguments1, arguments2)){
            return false;
        }
        return arguments1.endsWith(arguments2);}

    //prefix
    public static boolean lt(String arguments1, String arguments2){
        if(Objects.equals(arguments1, arguments2)){
            return false;
        }
        return arguments2.startsWith(arguments1);
    }


//    public static int negate(int arguments1){
//        return -1 * arguments1;
//    }
}