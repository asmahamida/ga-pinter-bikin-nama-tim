package pojat.util;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

/**
 * Shared UI formatting and interaction helpers for POJAT frames.
 */
public final class UIHelper {

    private static final NumberFormat RUPIAH_FORMAT =
            NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"));

    private UIHelper() {
    }

    public static String formatRupiah(double amount) {
        return RUPIAH_FORMAT.format(amount);
    }

    public static void applyHandCursor(AbstractButton... buttons) {
        Cursor hand = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        for (AbstractButton btn : buttons) {
            if (btn != null) {
                btn.setCursor(hand);
                btn.setFocusPainted(false);
            }
        }
    }

    public static void styleTable(JTable table) {
        if (table == null) {
            return;
        }
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(224, 212, 184));
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setFont(new Font("Circe Slab C Bold", Font.PLAIN, 12));
            header.setBackground(new Color(0, 83, 38));
            header.setForeground(new Color(247, 231, 198));
            header.setReorderingAllowed(false);
        }
    }
}
