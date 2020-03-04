package se.kyh;

import java.util.Scanner;

public class Main {
    private static String salt;
    private static String hash;

    public static void signup() {
        Scanner input = new Scanner(System.in);
        System.out.println("Sign up with password:");
        String password = input.nextLine();

        HashClass hashClass = new HashClass();
        String result = hashClass.hash(password);
        String[] saltAndHash = result.split(":");
        salt = saltAndHash[0];
        hash = saltAndHash[1];
        System.out.println(result);
    }

    public static void login() {
        Scanner input = new Scanner(System.in);
        System.out.println("Provide your password please: ");
        String password = input.nextLine();

        HashClass hashClass = new HashClass();

        if (hashClass.verify(password, salt, hash)) {
            System.out.println("Logged in");
        } else {
            System.out.println("Looser");
        }
    }

    public static void main(String[] args) {
        signup();
        login();
    }
}