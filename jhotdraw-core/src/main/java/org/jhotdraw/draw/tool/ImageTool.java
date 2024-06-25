/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.ImageHolderFigure;

/**
 * A tool to create new figures that implement the ImageHolderFigure interface, such as ImageFigure.
 * The figure to be created is specified by a prototype.
 *
 * <p>Immediately, after the ImageTool has been activated, it opens a JFileChooser, letting the user
 * specify an image file. The the user then performs the following mouse gesture:
 *
 * <ol>
 *   <li>Press the mouse button and drag the mouse over the DrawingView. This defines the bounds of
 *       the created figure.
 * </ol>
 *
 * <hr> <b>Design Patterns</b>
 *
 * <p><em>Prototype</em><br>
 * The {@code ImageTool} creates new figures by cloning a prototype {@code ImageHolderFigure}
 * object.<br>
 * Prototype: {@link ImageHolderFigure}; Client: {@link ImageTool}. <hr>
 */
public class ImageTool extends CreationTool {

  private static final long serialVersionUID = 1L;
  protected FileDialog fileDialog;
  protected JFileChooser fileChooser;
  protected boolean useFileDialog;

  public ImageTool(ImageHolderFigure prototype) {
    super(prototype);
  }

  public ImageTool(ImageHolderFigure prototype, Map<AttributeKey<?>, Object> attributes) {
    super(prototype, attributes);
  }

  public void setUseFileDialog(boolean newValue) {
    useFileDialog = newValue;
    if (useFileDialog) {
      fileChooser = null;
    } else {
      fileDialog = null;
    }
  }

  public boolean isUseFileDialog() {
    return useFileDialog;
  }

  @Override
  public void activate(DrawingEditor editor) {
    super.activate(editor);
    DrawingView v = getView();
    if (v == null) {
      return;
    }

    File file = loadFile(v);
    if (file != null) {
      loadImage(file);
    } else {
      handleNoFileSelected();
    }
  }

  private File loadFile(DrawingView v) {
    if (useFileDialog) {
      getFileDialog().setVisible(true);
      return getFileDialog().getFile() != null ? 
          new File(getFileDialog().getDirectory(), getFileDialog().getFile()) : 
          null;
    } else {
      return getFileChooser().showOpenDialog(v.getComponent()) == JFileChooser.APPROVE_OPTION ?
          getFileChooser().getSelectedFile() :
          null;
    }
  }

  private void loadImage(File file) {
    final ImageHolderFigure loaderFigure = ((ImageHolderFigure) prototype.clone());
    new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
        loaderFigure.loadImage(file);
        return null;
      }

      @Override
      protected void done() {
        try {
          get();
          updateImage(loaderFigure);
        } catch (IOException ex) {
          showErrorDialog(getView().getComponent(), ex.getMessage());
        } catch (InterruptedException | ExecutionException ex) {
          showErrorDialog(getView().getComponent(), ex.getMessage());
          getDrawing().remove(createdFigure);
          fireToolDone();
        }
      }
    }.execute();
  }

  private void updateImage(ImageHolderFigure loaderFigure) {
    if (createdFigure == null) {
      ((ImageHolderFigure) prototype)
          .setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
    } else {
      ((ImageHolderFigure) createdFigure)
          .setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
    }
  }

  private void handleNoFileSelected() {
    // getDrawing().remove(createdFigure);
    if (isToolDoneAfterCreation()) {
      fireToolDone();
    }
  }
  
  private void showErrorDialog(Component component, String message) {
    JOptionPane.showMessageDialog(component, message, null, JOptionPane.ERROR_MESSAGE);
  }

//Refactoring end

  private JFileChooser getFileChooser() {
    if (fileChooser == null) {
      fileChooser = new JFileChooser();
    }
    return fileChooser;
  }

  private FileDialog getFileDialog() {
    if (fileDialog == null) {
      fileDialog = new FileDialog(new Frame());
    }
    return fileDialog;
  }
}
