/*
 * @(#)MoveConstrainedAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.action;

import static org.jhotdraw.draw.constrainer.TranslationDirection.EAST;
import static org.jhotdraw.draw.constrainer.TranslationDirection.NORTH;
import static org.jhotdraw.draw.constrainer.TranslationDirection.SOUTH;
import static org.jhotdraw.draw.constrainer.TranslationDirection.WEST;

import java.awt.geom.*;
import java.util.HashSet;
import org.jhotdraw.draw.*;
import org.jhotdraw.draw.constrainer.TranslationDirection;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.util.ResourceBundleUtil;

/** Moves the selected figures by one constrained unit. */
public abstract class MoveConstrainedAction extends AbstractSelectedAction {

  private static final long serialVersionUID = 1L;
  private TranslationDirection dir;

  public MoveConstrainedAction(DrawingEditor editor, TranslationDirection dir) {
    super(editor);
    this.dir = dir;
    updateEnabledState();
  }

  @Override
  public void actionPerformed(java.awt.event.ActionEvent e) {
    if (getView().getSelectionCount() <= 0) {
        return;
    }

    HashSet<Figure> transformedFigures = getTransformedFigures();
    if (transformedFigures.isEmpty()) {
        return;
    }

    Rectangle2D.Double r = calculateCombinedBounds(transformedFigures);
    Point2D.Double p0 = new Point2D.Double(r.x, r.y);

    translateRectangle(r);

    AffineTransform tx = new AffineTransform();
    tx.translate(r.x - p0.x, r.y - p0.y);

    transformFigures(transformedFigures, tx);

    fireUndoableEditHappened(new TransformEdit(transformedFigures, tx));
}

private HashSet<Figure> getTransformedFigures() {
    HashSet<Figure> transformedFigures = new HashSet<>();
    for (Figure f : getView().getSelectedFigures()) {
        if (f.isTransformable()) {
            transformedFigures.add(f);
        }
    }
    return transformedFigures;
}

private Rectangle2D.Double calculateCombinedBounds(HashSet<Figure> figures) {
    Rectangle2D.Double r = null;
    for (Figure f : figures) {
        if (r == null) {
            r = f.getBounds();
        } else {
            r.add(f.getBounds());
        }
    }
    return r;
}

private void translateRectangle(Rectangle2D.Double r) {
    if (getView().getConstrainer() != null) {
        getView().getConstrainer().translateRectangle(r, dir);
    } else {
        translateRectangleWithoutConstrainer(r);
    }
}

private void translateRectangleWithoutConstrainer(Rectangle2D.Double r) {
    switch (dir) {
        case NORTH:
            r.y -= 1;
            break;
        case SOUTH:
            r.y += 1;
            break;
        case WEST:
            r.x -= 1;
            break;
        case EAST:
            r.x += 1;
            break;
    }
}

private void transformFigures(HashSet<Figure> figures, AffineTransform tx) {
    for (Figure f : figures) {
        f.willChange();
        f.transform(tx);
        f.changed();
    }
}
//Refactoring end
  }

  public static class East extends MoveConstrainedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.moveConstrainedEast";

    public East(DrawingEditor editor) {
      super(editor, TranslationDirection.EAST);
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      labels.configureAction(this, ID);
    }
  }

  public static class West extends MoveConstrainedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.moveConstrainedWest";

    public West(DrawingEditor editor) {
      super(editor, TranslationDirection.WEST);
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      labels.configureAction(this, ID);
    }
  }

  public static class North extends MoveConstrainedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.moveConstrainedNorth";

    public North(DrawingEditor editor) {
      super(editor, TranslationDirection.NORTH);
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      labels.configureAction(this, ID);
    }
  }

  public static class South extends MoveConstrainedAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.moveConstrainedSouth";

    public South(DrawingEditor editor) {
      super(editor, TranslationDirection.SOUTH);
      ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
      labels.configureAction(this, ID);
    }
  }
}
