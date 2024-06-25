/**
 * @(#)PaletteTextFieldUI.java
 *
 * <p>Copyright (c) 2009-2010 The authors and contributors of JHotDraw. You may not use, copy or
 * modify this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.gui.plaf.palette;

import java.awt.*;
import java.awt.geom.Line2D;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.text.*;

/** PaletteFormattedTextFieldUI. */
public class PaletteFormattedTextFieldUI extends BasicFormattedTextFieldUI {

  private Color errorIndicatorForeground;

  /**
   * Creates a UI for a JTextField.
   *
   * @param c the text field
   * @return the UI
   */
  public static ComponentUI createUI(JComponent c) {
    return new PaletteFormattedTextFieldUI();
  }

  /**
   * Creates a view (FieldView) based on an element.
   *
   * @param elem the element
   * @return the view
   */
  @Override
  public View create(Element elem) {
    /* We create our own view here. This view always uses the
     * text alignment that was specified by the text component. Even
     * then, when the text is longer than in the text component.
     *
     * Draws a wavy line if the value of the field is not valid.
     */
    return new FieldView(elem) {
      @Override
      public void paint(Graphics gr, Shape a) {
        Graphics2D g = (Graphics2D) gr;
        JFormattedTextField editor = (JFormattedTextField) getComponent();
        if (!editor.isEditValid()) {
          Rectangle r = (Rectangle) a;
          g.setColor(errorIndicatorForeground);
          g.setStroke(new BasicStroke(
              2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {3f, 3f}, 0.5f));
          g.draw(new Line2D.Float(
              r.x, r.y + r.height - 0.5f, r.x + r.width - 1, r.y + r.height - 0.5f));
        }
        super.paint(g, a);
      }
    };
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
    installDefaultsFromPalette(getComponent(), getPropertyPrefix());
    errorIndicatorForeground = PaletteLookAndFeel.getInstance().getColor(getPropertyPrefix() + ".errorIndicatorForeground");
  }

  private void installDefaultsFromPalette(JTextComponent editor, String prefix) {
    PaletteLookAndFeel plaf = PaletteLookAndFeel.getInstance();
    installDefaultProperty(editor::setFont, editor.getFont(), plaf.getFont(prefix + ".font"));
    installDefaultProperty(editor::setBackground, editor.getBackground(), plaf.getColor(prefix + ".background"));
    installDefaultProperty(editor::setForeground, editor.getForeground(), plaf.getColor(prefix + ".foreground"));
    installDefaultProperty(editor::setCaretColor, editor.getCaretColor(), plaf.getColor(prefix + ".caretForeground"));
    installDefaultProperty(editor::setSelectionColor, editor.getSelectionColor(), plaf.getColor(prefix + ".selectionBackground"));
    installDefaultProperty(editor::setSelectedTextColor, editor.getSelectedTextColor(), plaf.getColor(prefix + ".selectionForeground"));
    installDefaultProperty(editor::setDisabledTextColor, editor.getDisabledTextColor(), plaf.getColor(prefix + ".inactiveForeground"));
    installDefaultProperty(editor::setBorder, editor.getBorder(), plaf.getBorder(prefix + ".border"));
    installDefaultProperty(editor::setMargin, editor.getMargin(), plaf.getInsets(prefix + ".margin"));
    editor.setOpaque(plaf.getBoolean(prefix + ".opaque"));
  }

  private <T> void installDefaultProperty(Consumer<T> setter, T currentValue, T defaultValue) {
    if ((currentValue == null) || (currentValue instanceof UIResource)) {
      setter.accept(defaultValue);
    }
  }

//Refactoring end

  @Override
  protected void paintSafely(Graphics gr) {
    Graphics2D g = (Graphics2D) gr;
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(
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
