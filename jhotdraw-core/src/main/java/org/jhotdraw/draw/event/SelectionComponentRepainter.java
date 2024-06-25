/**
 * @(#)SelectionComponentRepainter.java
 *
 * <p>Copyright (c) 2008-2010 The authors and contributors of JHotDraw. You may not use, copy or
 * modify this file, except in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.event;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JComponent;
import org.jhotdraw.api.app.Disposable;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;

/**
 * Calls repaint on components, which show attributes of the drawing editor and of its views based
 * on the current selection.
 */
public class SelectionComponentRepainter extends DrawingListenerAdapter
    implements PropertyChangeListener, FigureSelectionListener, Disposable {

  private DrawingEditor editor;
  private JComponent component;

  public SelectionComponentRepainter(DrawingEditor editor, JComponent component) {
    this.editor = editor;
    this.component = component;
    if (editor != null) {
      if (editor.getActiveView() != null) {
        DrawingView view = editor.getActiveView();
        view.addPropertyChangeListener(this);
        view.addFigureSelectionListener(this);
        if (view.getDrawing() != null) {
          view.getDrawing().addDrawingListener(this);
        }
      }
      editor.addPropertyChangeListener(this);
    }
  }

  @Override
  public void drawingAttributeChanged(DrawingEvent evt) {
    component.repaint();
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    handleActiveViewChange(evt);
    handleDrawingChange(evt);
    component.repaint();
}

private void handleActiveViewChange(PropertyChangeEvent evt) {
    String name = evt.getPropertyName();
    if ((name == null && DrawingEditor.ACTIVE_VIEW_PROPERTY == null)
            || (name != null && name.equals(DrawingEditor.ACTIVE_VIEW_PROPERTY))) {
        removeListenersFromView((DrawingView) evt.getOldValue());
        addListenersToView((DrawingView) evt.getNewValue());
    }
}

private void handleDrawingChange(PropertyChangeEvent evt) {
    String name = evt.getPropertyName();
    if ((name == null && DrawingView.DRAWING_PROPERTY == null)
            || (name != null && name.equals(DrawingView.DRAWING_PROPERTY))) {
        removeDrawingListener((Drawing) evt.getOldValue());
        addDrawingListener((Drawing) evt.getNewValue());
    }
}

private void removeListenersFromView(DrawingView view) {
    if (view != null) {
        view.removePropertyChangeListener(this);
        view.removeFigureSelectionListener(this);
        removeDrawingListener(view.getDrawing());
    }
}

private void addListenersToView(DrawingView view) {
    if (view != null) {
        view.addPropertyChangeListener(this);
        view.addFigureSelectionListener(this);
        addDrawingListener(view.getDrawing());
    }
}

private void removeDrawingListener(Drawing drawing) {
    if (drawing != null) {
        drawing.removeDrawingListener(this);
    }
}

private void addDrawingListener(Drawing drawing) {
    if (drawing != null) {
        drawing.addDrawingListener(this);
    }
}
//Refactoring end

  @Override
  public void selectionChanged(FigureSelectionEvent evt) {
    component.repaint();
  }

  @Override
  public void dispose() {
    if (editor != null) {
      if (editor.getActiveView() != null) {
        DrawingView view = editor.getActiveView();
        view.removePropertyChangeListener(this);
        view.removeFigureSelectionListener(this);
        if (view.getDrawing() != null) {
          view.getDrawing().removeDrawingListener(this);
        }
      }
      editor.removePropertyChangeListener(this);
      editor = null;
    }
    component = null;
  }
}
