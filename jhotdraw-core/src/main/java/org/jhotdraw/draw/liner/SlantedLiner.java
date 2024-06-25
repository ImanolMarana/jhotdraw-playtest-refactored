/*
 * @(#)SlantedLiner.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.liner;

import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.figure.ConnectionFigure;
import org.jhotdraw.draw.figure.LineConnectionFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.path.BezierPath;

/** SlantedLiner. */
public class SlantedLiner implements Liner {

  private double slantSize;

  public SlantedLiner() {
    this(20);
  }

  public SlantedLiner(double slantSize) {
    this.slantSize = slantSize;
  }

  @Override
  public Collection<Handle> createHandles(BezierPath path) {
    return Collections.emptyList();
  }

  @Override
  public void lineout(ConnectionFigure figure) {
    BezierPath path = ((LineConnectionFigure) figure).getBezierPath();
    Connector start = figure.getStartConnector();
    Connector end = figure.getEndConnector();
    if (start == null || end == null || path == null) {
        return;
    }

    if (figure.getStartFigure() == figure.getEndFigure()) {
        lineoutSameFigure(figure, path, start, end);
    } else {
        lineoutDifferentFigures(figure, path, start, end);
    }

    // Ensure all path nodes are straight
    for (BezierPath.Node node : path.nodes()) {
        node.setMask(BezierPath.C0_MASK);
    }
    path.invalidatePath();
}

private void lineoutSameFigure(ConnectionFigure figure, BezierPath path, Connector start, Connector end) {
    // Ensure path has exactly five nodes
    while (path.size() < 5) {
        path.add(1, new BezierPath.Node(0, 0));
    }
    while (path.size() > 5) {
        path.remove(1);
    }

    Point2D.Double sp = start.findStart(figure);
    Point2D.Double ep = end.findEnd(figure);
    Rectangle2D.Double sb = start.getBounds();
    Rectangle2D.Double eb = end.getBounds();

    int soutcode = getOutcode(sp, sb, eb);
    int eoutcode = getOutcode(ep, eb, sb);

    path.nodes().get(0).moveTo(sp);
    path.nodes().get(path.size() - 1).moveTo(ep);

    soutcode = adjustStartOutcode(soutcode);
    eoutcode = adjustEndOutcode(soutcode, eoutcode);

    setNodePosition(path.nodes().get(1), sp, soutcode, slantSize);
    setNodePosition(path.nodes().get(3), ep, eoutcode, slantSize);

    setMiddleNodePosition(path, soutcode);
}

private void lineoutDifferentFigures(ConnectionFigure figure, BezierPath path, Connector start, Connector end) {
    // Ensure path has exactly four nodes
    while (path.size() < 4) {
        path.add(1, new BezierPath.Node(0, 0));
    }
    while (path.size() > 4) {
        path.remove(1);
    }

    Point2D.Double sp = start.findStart(figure);
    Point2D.Double ep = end.findEnd(figure);
    Rectangle2D.Double sb = start.getBounds();
    Rectangle2D.Double eb = end.getBounds();

    int soutcode = getOutcode(sp, sb, eb);
    int eoutcode = getOutcode(ep, eb, sb);

    path.nodes().get(0).moveTo(sp);
    path.nodes().get(path.size() - 1).moveTo(ep);

    setNodePosition(path.nodes().get(1), sp, soutcode, slantSize);
    setNodePosition(path.nodes().get(2), ep, eoutcode, slantSize);
}

private int getOutcode(Point2D.Double p, Rectangle2D.Double rect, Rectangle2D.Double otherRect) {
    int outcode = rect.outcode(p);
    if (outcode == 0) {
        outcode = Geom.outcode(rect, otherRect);
    }
    return outcode;
}

private int adjustStartOutcode(int soutcode) {
    switch (soutcode) {
        case Geom.OUT_TOP:
            return Geom.OUT_RIGHT;
        case Geom.OUT_RIGHT:
            return Geom.OUT_TOP;
        case Geom.OUT_BOTTOM:
            return Geom.OUT_RIGHT;
        case Geom.OUT_LEFT:
            return Geom.OUT_BOTTOM;
        default:
            return Geom.OUT_RIGHT;
    }
}

private int adjustEndOutcode(int soutcode, int eoutcode) {
    switch (soutcode) {
        case Geom.OUT_TOP:
            return Geom.OUT_LEFT;
        case Geom.OUT_RIGHT:
            return Geom.OUT_TOP;
        case Geom.OUT_BOTTOM:
            return Geom.OUT_RIGHT;
        case Geom.OUT_LEFT:
            return Geom.OUT_BOTTOM;
        default:
            return eoutcode;
    }
}

private void setNodePosition(BezierPath.Node node, Point2D.Double p, int outcode, double slantSize) {
    if ((outcode & Geom.OUT_RIGHT) != 0) {
        node.moveTo(p.x + slantSize, p.y);
    } else if ((outcode & Geom.OUT_LEFT) != 0) {
        node.moveTo(p.x - slantSize, p.y);
    } else if ((outcode & Geom.OUT_BOTTOM) != 0) {
        node.moveTo(p.x, p.y + slantSize);
    } else {
        node.moveTo(p.x, p.y - slantSize);
    }
}

private void setMiddleNodePosition(BezierPath path, int soutcode) {
    switch (soutcode) {
        case Geom.OUT_RIGHT:
        case Geom.OUT_LEFT:
            path.nodes().get(2).moveTo(path.nodes().get(1).x[0], path.nodes().get(3).y[0]);
            break;
        case Geom.OUT_TOP:
        case Geom.OUT_BOTTOM:
        default:
            path.nodes().get(2).moveTo(path.nodes().get(1).y[0], path.nodes().get(3).x[0]);
            break;
    }
}
//Refactoring end

  //  @Override
  //  public void read(DOMInput in) {
  //    slantSize = in.getAttribute("slant", 20d);
  //  }
  //
  //  @Override
  //  public void write(DOMOutput out) {
  //    out.addAttribute("slant", slantSize);
  //  }

  @Override
  public Liner clone() {
    try {
      return (Liner) super.clone();
    } catch (CloneNotSupportedException ex) {
      InternalError error = new InternalError(ex.getMessage());
      error.initCause(ex);
      throw error;
    }
  }
}
