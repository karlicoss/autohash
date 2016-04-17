package com.karlicoss.auto.value.hash;

import org.jetbrains.annotations.NotNull;

import java.util.List;

class Util {

    private Util() { }

    /*
        I wish I could use java 8 APIs...
     */
    @NotNull
    public static String join(@NotNull CharSequence delimiter, @NotNull List<? extends CharSequence> elements) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < elements.size(); i++) {
            result.append(elements.get(i));
            if (i != elements.size() - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }
}
