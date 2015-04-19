package fr.univ.lille1.main;

/**
 * This class is only used for test.
 * It create 6 instances of Main, it is very useful in oder to create 6 nodes in the same run
 * Basically just a shortcut
 * Every arguments are the id of each nodes
 */
public class Launcher extends Main {
    public static void main(String[] args) {

        for (int i = 1; i <= 6; i++) {
            Main.main(new String[]{String.valueOf(i)});
        }


    }
}
