package tech.bzpl.ecard.client.util;

import java.util.Scanner;

public class CialloScanner {
    private static final Scanner scanner = new Scanner(System.in);

    public static int getInt(String message) {
        System.out.print(message);
        String input = scanner.nextLine();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + input);
            return getInt(message);
        }
    }


    public static int getMoney(String message) {
        // Enter a money like xxxx.xx(2 digits)
        System.out.print(message);
        String input = scanner.nextLine();
        try {
            String[] parts = input.split("\\.");
            if (parts.length > 2) {
                System.out.println("Invalid input: " + input);
                return getMoney(message);
            }
            if (parts.length == 1) {
                return Integer.parseInt(parts[0]) * 100;
            }
            if (parts[1].length() > 2) {
                System.out.println("Invalid input: " + input);
                return getMoney(message);
            }
            return Integer.parseInt(parts[0]) * 100 + Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: " + input);
            return getMoney(message);
        }

    }

    public static String getLine(String message) {
        System.out.print(message);
        return scanner.nextLine();
    }
}