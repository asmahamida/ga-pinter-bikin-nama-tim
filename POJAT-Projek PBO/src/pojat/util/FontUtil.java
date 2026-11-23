/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package pojat.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

public class FontUtil {

    private static Font loadFont(String fileName, float size) {
        try {
            String path = "/pojat/resources/fonts/" + fileName;

            InputStream is = FontUtil.class.getResourceAsStream(path);

            if (is == null) {
                throw new IOException("Font tidak ditemukan: " + path);
            }

            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();

            return font.deriveFont(size);

        } catch (FontFormatException | IOException e) {
            System.out.println("Gagal memuat font: " + e.getMessage());
            return new Font("Serif", Font.PLAIN, (int) size);
        }
    }

    public static Font circeRegular(float size) {
        return loadFont("CirceSlabC-Regular.ttf", size);
    }

    public static Font circeBold(float size) {
        return loadFont("CirceSlabC-Bold.ttf", size);
    }

    public static Font circeMedium(float size) {
        return loadFont("CirceSlabC-Medium.ttf", size);
    }
}
