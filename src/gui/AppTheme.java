package gui;

import java.awt.*;

public class AppTheme {
    // Primary Green Palette
    public static final Color PRIMARY         = new Color(0x1B8A4F);   // Deep green
    public static final Color PRIMARY_DARK    = new Color(0x145C38);   // Darker green (sidebar)
    public static final Color PRIMARY_LIGHT   = new Color(0x27AE60);   // Lighter green
    public static final Color PRIMARY_HOVER   = new Color(0x1E9E5A);   // Hover state
    public static final Color ACCENT          = new Color(0x2ECC71);   // Bright green accent
    public static final Color ACCENT_LIGHT    = new Color(0xA8EFC5);   // Very light green

    // Neutrals
    public static final Color BG_MAIN         = new Color(0xF4F7F5);   // Off-white background
    public static final Color BG_CARD         = Color.WHITE;
    public static final Color BG_SIDEBAR      = new Color(0x0F3D26);   // Very dark green sidebar
    public static final Color BG_SIDEBAR_ITEM = new Color(0x1A5C38);   // Nav item hover

    // Text
    public static final Color TEXT_PRIMARY    = new Color(0x1A2E22);
    public static final Color TEXT_SECONDARY  = new Color(0x5A7A65);
    public static final Color TEXT_WHITE      = Color.WHITE;
    public static final Color TEXT_MUTED      = new Color(0x8FA89A);

    // Status colors
    public static final Color SUCCESS         = new Color(0x27AE60);
    public static final Color WARNING         = new Color(0xF39C12);
    public static final Color DANGER          = new Color(0xE74C3C);
    public static final Color INFO            = new Color(0x2980B9);

    // Borders
    public static final Color BORDER          = new Color(0xDDE8E3);
    public static final Color BORDER_FOCUS    = PRIMARY;

    // Fonts
    public static final Font  FONT_TITLE      = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font  FONT_SUBTITLE   = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font  FONT_BODY       = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font  FONT_SMALL      = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font  FONT_BUTTON     = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font  FONT_NAV        = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font  FONT_LOGO       = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font  FONT_STAT_NUM   = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font  FONT_STAT_LABEL = new Font("Segoe UI", Font.PLAIN, 12);

    public static final int SIDEBAR_WIDTH     = 230;
    public static final int CORNER_RADIUS     = 12;
}
