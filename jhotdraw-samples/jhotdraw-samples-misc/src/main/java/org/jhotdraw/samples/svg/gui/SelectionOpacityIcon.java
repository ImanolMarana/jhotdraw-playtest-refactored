/*
 * @(#)SelectionOpacityIcon.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import java.awt.*;
import java.net.*;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.figure.Figure;

/**
 * {@code SelectionOpacityIcon} visualizes an opacity attribute of the selected {@code Figure}(s) in
 * the active {@code DrawingView} of a {@code DrawingEditor}.
 */
public class SelectionOpacityIcon extends javax.swing.ImageIcon {

  private static final long serialVersionUID = 1L;
  private DrawingEditor editor;
  private AttributeKey<Double> opacityKey;
  private AttributeKey<Color> fillColorKey;
  private AttributeKey<Color> strokeColorKey;
  private Shape fillShape;
  private Shape strokeShape;

  /**
   * Creates a new instance.
   *
   * @param editor The drawing editor.
   * @param opacityKey The opacityKey of the default attribute
   * @param imageLocation the icon image
   * @param fillShape The shape to be drawn with the fillColor of the default attribute.
   */
  public SelectionOpacityIcon(
      DrawingEditor editor,
      AttributeKey<Double> opacityKey,
      AttributeKey<Color> fillColorKey,
      AttributeKey<Color> strokeColorKey,
      URL imageLocation,
      Shape fillShape,
      Shape strokeShape) {
    super(imageLocation);
    this.editor = editor;
    this.opacityKey = opacityKey;
    this.fillColorKey = fillColorKey;
    this.strokeColorKey = strokeColorKey;
    this.fillShape = fillShape;
    this.strokeShape = strokeShape;
  }

  public SelectionOpacityIcon(
      DrawingEditor editor,
      AttributeKey<Double> opacityKey,
      AttributeKey<Color> fillColorKey,
      AttributeKey<Color> strokeColorKey,
      Image image,
      Shape fillShape,
      Shape strokeShape) {
    super(image);
    this.editor = editor;
    this.opacityKey = opacityKey;
    this.fillColorKey = fillColorKey;
    this.strokeColorKey = strokeColorKey;
    this.fillShape = fillShape;
    this.strokeShape = strokeShape;
  }

  @Override
  public void paintIcon(java.awt.Component c, java.awt.Graphics gr, int x, int y) {
        Graphics2D g = (Graphics2D) gr;
        super.paintIcon(c, g, x, y);

        Double opacity = getOpacity();
        Color fillColor = getFillColor(opacity);
        Color strokeColor = getStrokeColor(opacity);

        paintFill(g, x, y, fillColor);
        paintStroke(g, x, y, strokeColor);
    }

    private Double getOpacity() {
        DrawingView view = (editor == null) ? null : editor.getActiveView();
        if (view != null && view.getSelectedFigures().size() == 1) {
            Figure f = view.getSelectedFigures().iterator().next();
            return f.attr().get(opacityKey);
        } else if (editor != null) {
            return opacityKey.get(editor.getDefaultAttributes());
        } else {
            return opacityKey.getDefaultValue();
        }
    }

    private Color getFillColor(Double opacity) {
        if (fillColorKey == null) {
            return null;
        }

        Color fillColor;
        DrawingView view = (editor == null) ? null : editor.getActiveView();
        if (view != null && view.getSelectedFigures().size() == 1) {
            Figure f = view.getSelectedFigures().iterator().next();
            fillColor = f.attr().get(fillColorKey);
        } else if (editor != null) {
            fillColor = fillColorKey.get(editor.getDefaultAttributes());
        } else {
            fillColor = fillColorKey.getDefaultValue();
        }
        return (opacity != null && fillColor != null)
                ? new Color((((int) (opacity * 255)) << 24) | (fillColor.getRGB() & 0xffffff), true)
                : null;
    }

    private Color getStrokeColor(Double opacity) {
        if (strokeColorKey == null) {
            return null;
        }

        Color strokeColor;
        DrawingView view = (editor == null) ? null : editor.getActiveView();
        if (view != null && view.getSelectedFigures().size() == 1) {
            Figure f = view.getSelectedFigures().iterator().next();
            strokeColor = f.attr().get(strokeColorKey);
        } else if (editor != null) {
            strokeColor = strokeColorKey.get(editor.getDefaultAttributes());
        } else {
            strokeColor = strokeColorKey.getDefaultValue();
        }
        return (opacity != null && strokeColor != null)
                ? new Color((((int) (opacity * 255)) << 24) | (strokeColor.getRGB() & 0xffffff), true)
                : null;
    }

    private void paintFill(Graphics2D g, int x, int y, Color fillColor) {
        if (fillColorKey != null && fillShape != null && fillColor != null) {
            g.setColor(fillColor);
            g.translate(x, y);
            g.fill(fillShape);
            g.translate(-x, -y);
        }
    }

    private void paintStroke(Graphics2D g, int x, int y, Color strokeColor) {
        if (strokeColorKey != null && strokeShape != null && strokeColor != null) {
            g.setColor(strokeColor);
            g.translate(x, y);
            g.draw(strokeShape);
            g.translate(-x, -y);
        }
    }
//Refactoring end