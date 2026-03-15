package ru.ssau.tk.faible.coplatebackend.util;

import java.security.SecureRandom;

public class InviteCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // без O,0,1,I для удобства
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom random = new SecureRandom();


    public static String generate() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }
}