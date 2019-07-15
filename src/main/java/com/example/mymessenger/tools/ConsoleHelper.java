package com.example.mymessenger.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConsoleHelper {

    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String message;
        while (true) {
            try {
                message = reader.readLine();
                break;
            } catch (IOException e) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
        return message;
    }

    public static int readInt() {
        int i;
        while (true) {
            try {
                i = Integer.parseInt(readString());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Произошла ошибка при попытке ввода цифр. Попробуйте еще раз.");
            }
        }
        return i;
    }
}
