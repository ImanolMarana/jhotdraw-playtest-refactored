/*
 * @(#)DrawingOpacityIcon.java
 *
 * Copyright (c) 2008 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg.gui;

import java.awt.*;
import java.net.*;
import org.jhotdraw.draw.*;

/**
 * {@code DrawingOpacityIcon} visualizes an opacity attribute of the {@code Drawing} object which is
 * in the active {@code DrawingView} of a {@code DrawingEditor}.
 */
public class DrawingOpacityIcon extends javax.swing.ImageIcon {

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
  public DrawingOpacityIcon(
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

  public DrawingOpacityIcon(
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
    if (editor != null) {
      DrawingView view = editor.getActiveView();
      if (view != null && view.getDrawing() != null) {
        return view.getDrawing().attr().get(opacityKey);
      } else {
        return opacityKey.get(editor.getDefaultAttributes());
      }
    }
    return 0d;
  }

  private Color getFillColor(Double opacity) {
    if (fillColorKey != null) {
      Color fillColor = null;
      if (editor != null) {
        DrawingView view = editor.getActiveView();
        if (view != null && view.getDrawing() != null) {
          fillColor = fillColorKey.get(view.getDrawing().attr());
        } else {
          fillColor = fillColorKey.get(editor.getDefaultAttributes());
        }
      }
      if (opacity != null && fillColor == null) {
        fillColor = Color.BLACK;
      }
      return fillColor;
    }
    return null;
  }

  private Color getStrokeColor(Double opacity) {
    if (strokeColorKey != null) {
      Color strokeColor = null;
      if (editor != null) {
        DrawingView view = editor.getActiveView();
        if (view != null && view.getDrawing() != null) {
          strokeColor = strokeColorKey.get(view.getDrawing().attr());
        } else {
          strokeColor = strokeColorKey.get(editor.getDefaultAttributes());
        }
      }
      if (opacity != null && strokeColor == null) {
        strokeColor = Color.BLACK;
      }
      return strokeColor;
    }
    return null;
  }

  private void paintFill(Graphics2D g, int x, int y, Color fillColor) {
    if (fillColorKey != null && fillShape != null && fillColor != null) {
      g.setColor(
          new Color((((int) (getOpacity() * 255)) << 24) | (fillColor.getRGB() & 0xffffff), true));
      g.translate(x, y);
      g.fill(fillShape);
      g.translate(-x, -y);
    }
  }

  private void paintStroke(Graphics2D g, int x, int y, Color strokeColor) {
    if (strokeColorKey != null && strokeShape != null && strokeColor != null) {
      g.setColor(
          new Color(
              (((int) (getOpacity() * 255)) << 24) | (strokeColor.getRGB() & 0xffffff), true));
      g.translate(x, y);
      g.draw(strokeShape);
      g.translate(-x, -y);
    }
  }
//Refactoring end
        g.setColor(
            new Color((((int) (opacity * 255)) << 24) | (fillColor.getRGB() & 0xffffff), true));
        g.translate(x, y);
        g.fill(fillShape);
        g.translate(-x, -y);
      }
    }
    if (strokeColorKey != null && strokeShape != null) {
      if (opacity != null) {
        if (strokeColor == null) {
          strokeColor = Color.BLACK;
        }
        g.setColor(
            new Color((((int) (opacity * 255)) << 24) | (strokeColor.getRGB() & 0xffffff), true));
        g.translate(x, y);
        g.draw(strokeShape);
        g.translate(-x, -y);
      }
    }
  }
}
