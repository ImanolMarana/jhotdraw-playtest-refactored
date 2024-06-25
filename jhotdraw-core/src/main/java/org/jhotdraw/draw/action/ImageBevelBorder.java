/*
 * @(#)ImageBevelBorder.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import java.awt.*;
import java.awt.image.*;
import javax.swing.border.*;

/**
 * Draws a filled bevel border using an image and insets. The image must consist of a bevel and a
 * fill area.
 *
 * <p>The insets and the size of the image are used do determine which parts of the image shall be
 * used to draw the corners and edges of the bevel as well the fill area.
 *
 * <p>For example, if you provide an image of size 10,10 and a insets of size 2, 2, 4, 4, then the
 * corners of the border are made up of top left: 2,2, top right: 2,4, bottom left: 2,4, bottom
 * right: 4,4 rectangle of the image. The inner area of the image is used to fill the inner area.
 */
public class ImageBevelBorder implements Border {

  private static final boolean VERBOSE = false;

  /** The image to be used for drawing. */
  private BufferedImage image;

  /** The border insets */
  private Insets borderInsets;

  /** The insets of the image. */
  private Insets imageInsets;

  /** This attribute is set to true, when the image is used to fill the content area too. */
  private boolean fillContentArea;

  /**
   * Creates a new instance with the given image and insets. The image has the same insets as the
   * border.
   */
  public ImageBevelBorder(Image img, Insets borderInsets) {
    this(img, borderInsets, borderInsets, true);
  }

  /**
   * Creates a new instance with the given image and insets. The image has different insets than the
   * border.
   */
  public ImageBevelBorder(Image img, Insets imageInsets, Insets borderInsets) {
    this(img, imageInsets, borderInsets, true);
  }

  /**
   * Creates a new instance with the given image and insets. The image has different insets than the
   * border.
   */
  public ImageBevelBorder(
      Image img, Insets imageInsets, Insets borderInsets, boolean fillContentArea) {
    if (img instanceof BufferedImage) {
      this.image = (BufferedImage) img;
    } else {
      Frame f = new Frame();
      f.pack();
      MediaTracker t = new MediaTracker(f);
      t.addImage(img, 0);
      try {
        t.waitForAll();
      } catch (InterruptedException e) {
        // allow empty
      }
      image = new BufferedImage(img.getWidth(f), img.getHeight(f), BufferedImage.TYPE_INT_ARGB);
      Graphics2D imgGraphics = image.createGraphics();
      imgGraphics.drawImage(img, 0, 0, f);
      imgGraphics.dispose();
      f.dispose();
    }
    this.imageInsets = imageInsets;
    this.borderInsets = borderInsets;
    this.fillContentArea = fillContentArea;
  }

  /** Returns true if the border is opaque. This implementation always returns false. */
  @Override
  public boolean isBorderOpaque() {
    return false;
  }

  /**
   * Returns the insets of the border.
   *
   * @param c the component for which this border insets value applies
   */
  @Override
  public Insets getBorderInsets(Component c) {
    return (Insets) borderInsets.clone();
  }

  /**
   * Paints the bevel image for the specified component with the specified position and size.
   *
   * @param c the component for which this border is being painted
   * @param gr the paint graphics
   * @param x the x position of the painted border
   * @param y the y position of the painted border
   * @param width the width of the painted border
   * @param height the height of the painted border
   */
  @Override
  public void paintBorder(Component c, Graphics gr, int x, int y, int width, int height) {
    if (image == null) {
      return;
    }

    Graphics2D g = (Graphics2D) gr;
    
    int top = imageInsets.top;
    int left = imageInsets.left;
    int bottom = imageInsets.bottom;
    int right = imageInsets.right;
    int imgWidth = image.getWidth();
    int imgHeight = image.getHeight();

    if (fillContentArea && width == imgWidth && height == imgHeight) {
      g.drawImage(image, x, y, c);
      return;
    }

    optimizeInsets(width, height, imgWidth, imgHeight);

    adjustInsetsForSmallComponent(width, height, left, right, top, bottom);

    drawLeads(g, x, y, width, height, top, left, bottom, right, imgWidth, imgHeight, c);

    drawEdges(g, x, y, width, height, top, left, bottom, right, imgWidth, imgHeight, c);

    fillCenter(g, x, y, width, height, top, left, bottom, right, imgWidth, imgHeight, c);
  }

  private void optimizeInsets(int width, int height, int imgWidth, int imgHeight) {
    if (width == imgWidth) {
      imageInsets.left = imgWidth;
      imageInsets.right = 0;
    }
    if (height == imgHeight) {
      imageInsets.top = imgHeight;
      imageInsets.bottom = 0;
    }
  }

  private void adjustInsetsForSmallComponent(int width, int height, int left, int right, int top, int bottom) {
    if (width < left + right) {
      imageInsets.left = Math.min(left, width / 2);
      imageInsets.right = width - imageInsets.left;
    }
    if (height < top + bottom) {
      imageInsets.top = Math.min(top, height / 2);
      imageInsets.bottom = height - imageInsets.top;
    }
  }

  private void drawLeads(
      Graphics2D g,
      int x,
      int y,
      int width,
      int height,
      int top,
      int left,
      int bottom,
      int right,
      int imgWidth,
      int imgHeight,
      Component c) {
    if (top > 0 && left > 0) {
      g.drawImage(image, x, y, x + left, y + top, 0, 0, left, top, c);
    }
    if (top > 0 && right > 0) {
      g.drawImage(
          image, x + width - right, y, x + width, y + top, imgWidth - right, 0, imgWidth, top, c);
    }
    if (bottom > 0 && left > 0) {
      g.drawImage(
          image,
          x,
          y + height - bottom,
          x + left,
          y + height,
          0,
          imgHeight - bottom,
          left,
          imgHeight,
          c);
    }
    if (bottom > 0 && right > 0) {
      g.drawImage(
          image,
          x + width - right,
          y + height - bottom,
          x + width,
          y + height,
          imgWidth - right,
          imgHeight - bottom,
          imgWidth,
          imgHeight,
          c);
    }
  }

  private void drawEdges(
      Graphics2D g,
      int x,
      int y,
      int width,
      int height,
      int top,
      int left,
      int bottom,
      int right,
      int imgWidth,
      int imgHeight,
      Component c) {
    BufferedImage subImg;
    TexturePaint paint;
    // North
    if (top > 0 && left + right < width) {
      subImg = image.getSubimage(left, 0, imgWidth - right - left, top);
      paint = new TexturePaint(subImg, new Rectangle(x + left, y, imgWidth - left - right, top));
      g.setPaint(paint);
      g.fillRect(x + left, y, width - left - right, top);
    }
    // South
    if (bottom > 0 && left + right < width) {
      subImg = image.getSubimage(left, imgHeight - bottom, imgWidth - right - left, bottom);
      paint =
          new TexturePaint(
              subImg, new Rectangle(x + left, y + height - bottom, imgWidth - left - right, bottom));
      g.setPaint(paint);
      g.fillRect(x + left, y + height - bottom, width - left - right, bottom);
    }
    // West
    if (left > 0 && top + bottom < height) {
      subImg = image.getSubimage(0, top, left, imgHeight - top - bottom);
      paint = new TexturePaint(subImg, new Rectangle(x, y + top, left, imgHeight - top - bottom));
      g.setPaint(paint);
      g.fillRect(x, y + top, left, height - top - bottom);
    }
    // East
    if (right > 0 && top + bottom < height) {
      subImg = image.getSubimage(imgWidth - right, top, right, imgHeight - top - bottom);
      paint =
          new TexturePaint(
              subImg, new Rectangle(x + width - right, y + top, right, imgHeight - top - bottom));
      g.setPaint(paint);
      g.fillRect(x + width - right, y + top, right, height - top - bottom);
    }
  }

  private void fillCenter(
      Graphics2D g,
      int x,
      int y,
      int width,
      int height,
      int top,
      int left,
      int bottom,
      int right,
      int imgWidth,
      int imgHeight,
      Component c) {
    if (fillContentArea && left + right < width && top + bottom < height) {
      BufferedImage subImg = image.getSubimage(left, top, imgWidth - right - left, imgHeight - top - bottom);
      TexturePaint paint =
          new TexturePaint(
              subImg,
              new Rectangle(x + left, y + top, imgWidth - right - left, imgHeight - top - bottom));
      g.setPaint(paint);
      g.fillRect(x + left, y + top, width - right - left, height - top - bottom);
    }
  }

//Refactoring end
}
