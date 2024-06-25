/**
 * @(#)PaletteTextFieldUI.java
 *
 * <p>Copyright (c) 2008 The authors and contributors of JHotDraw. You may not use, copy or modify
 * this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;

/** PaletteTextFieldUI. */
public class PaletteTextFieldUI extends BasicTextFieldUI {

  /**
   * Creates a UI for a JTextField.
   *
   * @param c the text field
   * @return the UI
   */
  public static ComponentUI createUI(JComponent c) {
    return new PaletteTextFieldUI();
  }

  /**
   * Initializes component properties, e.g. font, foreground, background, caret color, selection
   * color, selected text color, disabled text color, and border color. The font, foreground, and
   * background properties are only set if their current value is either null or a UIResource, other
   * properties are set if the current value is null.
   *
   * @see #uninstallDefaults
   * @see #installUI
   */
  @Override
  protected void installDefaults() {
        JTextComponent editor = getComponent();
        PaletteLookAndFeel plaf = PaletteLookAndFeel.getInstance();
        String prefix = getPropertyPrefix();

        applyStyle(editor, plaf, prefix, ".font", "Font");
        applyStyle(editor, plaf, prefix, ".background", "Color");
        applyStyle(editor, plaf, prefix, ".foreground", "Color");
        applyStyle(editor, plaf, prefix, ".caretForeground", "CaretColor");
        applyStyle(editor, plaf, prefix, ".selectionBackground", "SelectionColor");
        applyStyle(editor, plaf, prefix, ".selectionForeground", "SelectedTextColor");
        applyStyle(editor, plaf, prefix, ".inactiveForeground", "DisabledTextColor");
        applyStyle(editor, plaf, prefix, ".border", "Border");

        Insets margin = editor.getMargin();
        if (margin == null || margin instanceof UIResource) {
            editor.setMargin(plaf.getInsets(prefix + ".margin"));
        }
        editor.setOpaque(plaf.getBoolean(prefix + ".opaque"));
    }

    private void applyStyle(JTextComponent editor, PaletteLookAndFeel plaf, String prefix, String suffix, String property) {
        try {
            java.lang.reflect.Method getter = JTextComponent.class.getMethod("get" + property);
            java.lang.reflect.Method setter = JTextComponent.class.getMethod("set" + property, getter.getReturnType());
            Object value = getter.invoke(editor);
            if ((value == null) || (value instanceof UIResource)) {
                setter.invoke(editor, plaf.getClass().getMethod("get" + property, String.class).invoke(plaf, prefix + suffix));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//Refactoring end
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    super.paintSafely(g);
  }

  @Override
  public void paintBackground(Graphics g) {
    JTextComponent c = getComponent();
    if (c.getBorder() instanceof BackdropBorder) {
      BackdropBorder bb = (BackdropBorder) c.getBorder();
      bb.getBackdropBorder().paintBorder(c, g, 0, 0, c.getWidth(), c.getHeight());
    } else {
      super.paintBackground(g);
    }
  }
}
