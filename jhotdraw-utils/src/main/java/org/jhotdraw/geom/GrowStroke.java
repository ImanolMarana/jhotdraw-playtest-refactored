/*
 * @(#)GrowStroke.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import org.jhotdraw.geom.path.BezierPath;

/**
 * GrowStroke can be used to grow/shrink a figure by a specified line width. This only works with
 * closed convex paths having edges in clockwise direction.
 *
 * <p>Note: Although this is a Stroke object, it does not actually create a stroked shape, but one
 * that can be used for filling.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class GrowStroke extends DoubleStroke {

  private double grow;

  public GrowStroke(double grow, double miterLimit) {
    super(grow * 2d, 1d, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_BEVEL, miterLimit, null, 0f);
    this.grow = grow;
  }

  @Override
  public Shape createStrokedShape(Shape s) {
          BezierPath bp = new BezierPath();
          Path2D.Double left = new Path2D.Double();
          Path2D.Double right = new Path2D.Double();
          initializePaths(s, left, right);

          double[] coords = new double[6];
          // FIXME - We only do a flattened path
          for (PathIterator i = s.getPathIterator(null, 0.1d); !i.isDone(); i.next()) {
              processSegment(i, coords, bp);
          }

          if (bp.size() > 1) {
              traceStroke(bp, left, right);
          }
          return getFinalShape(left, right);
      }

      private void initializePaths(Shape s, Path2D.Double left, Path2D.Double right) {
          if (s instanceof Path2D.Double) {
              left.setWindingRule(((Path2D.Double) s).getWindingRule());
              right.setWindingRule(((Path2D.Double) s).getWindingRule());
          } else if (s instanceof BezierPath) {
              left.setWindingRule(((BezierPath) s).getWindingRule());
              right.setWindingRule(((BezierPath) s).getWindingRule());
          }
      }
    
      private void processSegment(PathIterator i, double[] coords, BezierPath bp) {
          int type = i.currentSegment(coords);
          switch (type) {
              case PathIterator.SEG_MOVETO:
                  handleMoveTo(bp, coords);
                  break;
              case PathIterator.SEG_LINETO:
                  handleLineTo(bp, coords);
                  break;
              case PathIterator.SEG_QUADTO:
                  bp.quadTo(coords[0], coords[1], coords[2], coords[3]);
                  break;
              case PathIterator.SEG_CUBICTO:
                  bp.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
                  break;
              case PathIterator.SEG_CLOSE:
                  bp.setClosed(true);
                  break;
          }
      }
    
      private void handleMoveTo(BezierPath bp, double[] coords) {
          if (bp.size() != 0) {
              traceStroke(bp, new Path2D.Double(), new Path2D.Double());
          }
          bp.clear();
          bp.moveTo(coords[0], coords[1]);
      }
    
      private void handleLineTo(BezierPath bp, double[] coords) {
          if (coords[0] != bp.nodes().get(bp.size() - 1).x[0]
                  || coords[1] != bp.nodes().get(bp.size() - 1).y[0]) {
              bp.lineTo(coords[0], coords[1]);
          }
      }
    
      private Shape getFinalShape(Path2D.Double left, Path2D.Double right) {
          if (Geom.contains(left.getBounds2D(), right.getBounds2D())) {
              return (grow > 0) ? left : right;
          } else {
              return (grow > 0) ? right : left;
          }
      }
//Refactoring end
}
