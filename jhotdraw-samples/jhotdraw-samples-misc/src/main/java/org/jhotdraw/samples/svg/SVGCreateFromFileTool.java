/*
 * @(#)ImageTool.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.samples.svg;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.CompositeFigure;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.ImageHolderFigure;
import org.jhotdraw.draw.io.InputFormat;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.samples.svg.io.SVGInputFormat;
import org.jhotdraw.samples.svg.io.SVGZInputFormat;

/**
 * A tool to create new figures from an input file. If the file holds a bitmap image, this tool
 * creates a SVGImageFigure. If the file holds a SVG or a SVGZ image, ths tool creates a
 * SVGGroupFigure.
 *
 * <p>Immediately, after the ImageTool has been activated, it opens a JFileChooser, letting the user
 * specify a file. The the user then performs the following mouse gesture:
 *
 * <ol>
 *   <li>Press the mouse button and drag the mouse over the DrawingView. This defines the bounds of
 *       the created Figure.
 * </ol>
 */
public class SVGCreateFromFileTool extends CreationTool {

  private static final long serialVersionUID = 1L;
  protected FileDialog fileDialog;
  protected JFileChooser fileChooser;
  protected CompositeFigure groupPrototype;
  protected ImageHolderFigure imagePrototype;
  protected boolean useFileDialog;

  public SVGCreateFromFileTool(ImageHolderFigure imagePrototype, CompositeFigure groupPrototype) {
    super(imagePrototype);
    this.groupPrototype = groupPrototype;
    this.imagePrototype = imagePrototype;
  }

  public SVGCreateFromFileTool(
      ImageHolderFigure imagePrototype,
      CompositeFigure groupPrototype,
      Map<AttributeKey<?>, Object> attributes) {
    super(imagePrototype, attributes);
    this.groupPrototype = groupPrototype;
    this.imagePrototype = imagePrototype;
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

        File file = chooseFile(v);
        if (file != null) {
            if (isSVG(file)) {
                loadSVG(file);
            } else {
                loadImage(file);
            }
        } else {
            handleFileNotChosen();
        }
    }

    private boolean isSVG(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".svg") || fileName.endsWith(".svgz");
    }

    private void loadSVG(File file) {
        prototype = groupPrototype.clone();
        new SwingWorker<Drawing, Drawing>() {
            @Override
            protected Drawing doInBackground() throws Exception {
                Drawing drawing = new DefaultDrawing();
                InputFormat in = file.getName().toLowerCase().endsWith(".svg")
                        ? new SVGInputFormat()
                        : new SVGZInputFormat();
                in.read(file.toURI(), drawing);
                return drawing;
            }

            @Override
            protected void done() {
                try {
                    Drawing drawing = get();
                    addFiguresToPrototype(drawing);
                } catch (InterruptedException | ExecutionException ex) {
                    logErrorAndShowMessage(ex);
                    failed(ex);
                }
            }

            private void addFiguresToPrototype(Drawing drawing) {
                CompositeFigure parent = (createdFigure == null)
                        ? (CompositeFigure) prototype
                        : (CompositeFigure) createdFigure;
                
                if (createdFigure != null) {
                    parent.willChange();
                }
                
                for (Figure f : drawing.getChildren()) {
                    parent.add(f);
                }
                
                if (createdFigure != null) {
                    parent.changed();
                }
            }

            private void logErrorAndShowMessage(Exception ex) {
                Logger.getLogger(SVGCreateFromFileTool.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(
                        getView().getComponent(), ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);
            }

        }.execute();
    }

    private void loadImage(File file) {
        prototype = imagePrototype;
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
                    setImageForPrototype(loaderFigure);
                } catch (InterruptedException | ExecutionException ex) {
                    Logger.getLogger(SVGCreateFromFileTool.class.getName()).log(Level.SEVERE, null, ex);
                    failed(ex);
                }
            }

            private void setImageForPrototype(ImageHolderFigure loaderFigure) {
                try {
                    if (createdFigure == null) {
                        ((ImageHolderFigure) prototype)
                                .setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
                    } else {
                        ((ImageHolderFigure) createdFigure)
                                .setImage(loaderFigure.getImageData(), loaderFigure.getBufferedImage());
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            getView().getComponent(), ex.getMessage(), null, JOptionPane.ERROR_MESSAGE);
                }
            }

        }.execute();
    }

    private File chooseFile(DrawingView v) {
        if (useFileDialog) {
            getFileDialog().setVisible(true);
            return (getFileDialog().getFile() != null)
                    ? new File(getFileDialog().getDirectory(), getFileDialog().getFile())
                    : null;
        } else {
            return (getFileChooser().showOpenDialog(v.getComponent()) == JFileChooser.APPROVE_OPTION)
                    ? getFileChooser().getSelectedFile()
                    : null;
        }
    }
    
    private void handleFileNotChosen() {
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }

//Refactoring end
  }

  @Override
  protected Figure createFigure() {
    if (prototype instanceof CompositeFigure) {
      // we must not apply default attributs to the composite figure,
      // because this would change the look of the figures that we
      // read from the SVG file.
      return prototype.clone();
    } else {
      return super.createFigure();
    }
  }

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
