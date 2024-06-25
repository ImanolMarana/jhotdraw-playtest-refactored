/*
 * @(#)ColorFormatter.java
 *
 * Copyright (c) 2009-2010 The authors and contributors of JHotDraw.
 *
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.text;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.text.ParseException;
import org.jhotdraw.color.HSBColorSpace;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * {@code ColorFormatter} is used to format colors into a textual representation which can be
 * displayed as a tooltip.
 *
 * <p>By default, the formatter is adaptive, meaning that the format depends on the {@code
 * ColorSpace} of the current {@code Color} value.
 *
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: ColorFormatter.java 632 2010-01-21 16:06:59Z rawcoder $
 */
public class ColorToolTipTextFormatter extends ColorFormatter {

  private static final long serialVersionUID = 1L;
  private ResourceBundleUtil labels;

  public ColorToolTipTextFormatter() {
    labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
  }

  @Override
  public String valueToString(Object value) throws ParseException {
    if (value == null) {
      return handleNullValue();
    } else if (!(value instanceof Color)) {
      throw new ParseException("Value is not a color " + value, 0);
    } else {
      return colorToString((Color) value);
    }
  }

  private String handleNullValue() throws ParseException {
    if (allowsNullValue) {
      return "";
    } else {
      throw new ParseException("Null value is not allowed.", 0);
    }
  }

  private String colorToString(Color c) {
    Format f = getAdaptiveFormat(c);

    switch (f) {
      case RGB_HEX:
        return getRgbHexFormat(c);
      case RGB_INTEGER:
        return getRgbIntegerFormat(c);
      case RGB_PERCENTAGE:
        return getRgbPercentageFormat(c);
      case HSB_PERCENTAGE:
        return getHsbPercentageFormat(c);
      case GRAY_PERCENTAGE:
        return getGrayPercentageFormat(c);
      default:
        return "";
    }
  }

  private Format getAdaptiveFormat(Color c) {
    if (isAdaptive) {
      if (c.getColorSpace().equals(HSBColorSpace.getInstance())) {
        return Format.HSB_PERCENTAGE;
      } else if (c.getColorSpace().equals(ColorSpace.getInstance(ColorSpace.CS_GRAY))) {
        return Format.GRAY_PERCENTAGE;
      } else {
        return Format.RGB_INTEGER;
      }
    } else {
      return outputFormat;
    }
  }

  private String getRgbHexFormat(Color c) {
    String str = "000000" + Integer.toHexString(c.getRGB() & 0xffffff);
    return labels.getFormatted(
        "attribute.color.rgbHexComponents.toolTipText", str.substring(str.length() - 6));
  }

  private String getRgbIntegerFormat(Color c) {
    return labels.getFormatted(
        "attribute.color.rgbComponents.toolTipText",
        numberFormat.format(c.getRed()),
        numberFormat.format(c.getGreen()),
        numberFormat.format(c.getBlue()));
  }

  private String getRgbPercentageFormat(Color c) {
    return labels.getFormatted(
        "attribute.color.rgbPercentageComponents.toolTipText",
        numberFormat.format(c.getRed() / 255f),
        numberFormat.format(c.getGreen() / 255f),
        numberFormat.format(c.getBlue() / 255f));
  }

  private String getHsbPercentageFormat(Color c) {
    float[] components =
        (c.getColorSpace().equals(HSBColorSpace.getInstance()))
            ? c.getComponents(null)
            : Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), new float[3]);
    return labels.getFormatted(
        "attribute.color.hsbComponents.toolTipText",
        numberFormat.format(components[0] * 360),
        numberFormat.format(components[1] * 100),
        numberFormat.format(components[2] * 100));
  }

  private String getGrayPercentageFormat(Color c) {
    float[] components =
        (c.getColorSpace().equals(ColorSpace.getInstance(ColorSpace.CS_GRAY)))
            ? c.getComponents(null)
            : c.getColorComponents(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
    return labels.getFormatted(
        "attribute.color.grayComponents.toolTipText",
        numberFormat.format(components[0] * 100));
  }
//Refactoring end
  }
}
