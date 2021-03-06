

package com.lowagie.text.pdf;

import java.awt.Color;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;



public class PdfPRow {

    
    public static final float BOTTOM_LIMIT = -(1 << 30);

    protected PdfPCell cells[];

    protected float widths[];

    protected float maxHeight = 0;

    protected boolean calculated = false;
    
    private int[] canvasesPos;

    
    public PdfPRow(PdfPCell cells[]) {
        this.cells = cells;
        widths = new float[cells.length];
    }

    
    public PdfPRow(PdfPRow row) {
        maxHeight = row.maxHeight;
        calculated = row.calculated;
        cells = new PdfPCell[row.cells.length];
        for (int k = 0; k < cells.length; ++k) {
            if (row.cells[k] != null)
                cells[k] = new PdfPCell(row.cells[k]);
        }
        widths = new float[cells.length];
        System.arraycopy(row.widths, 0, widths, 0, cells.length);
    }

    
    public boolean setWidths(float widths[]) {
        if (widths.length != cells.length)
            return false;
        System.arraycopy(widths, 0, this.widths, 0, cells.length);
        float total = 0;
        calculated = false;
        for (int k = 0; k < widths.length; ++k) {
            PdfPCell cell = cells[k];
            cell.setLeft(total);
            int last = k + cell.getColspan();
            for (; k < last; ++k)
                total += widths[k];
            --k;
            cell.setRight(total);
            cell.setTop(0);
        }
        return true;
    }

    
    public float calculateHeights() {
        maxHeight = 0;
        for (int k = 0; k < cells.length; ++k) {
            PdfPCell cell = cells[k];
            if (cell == null)
                continue;
            Image img = cell.getImage();
            if (img != null) {
                img.scalePercent(100);
                float refWidth = img.getScaledWidth();
                if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    refWidth = img.getScaledHeight();
                }
                float scale = (cell.getRight() - cell.getEffectivePaddingRight()
                    - cell.getEffectivePaddingLeft() - cell.getLeft())
                    / refWidth;
                img.scalePercent(scale * 100);
                float refHeight = img.getScaledHeight();
                if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    refHeight = img.getScaledWidth();
                }
                cell.setBottom(cell.getTop() - cell.getEffectivePaddingTop()
                    - cell.getEffectivePaddingBottom()
                    - refHeight);
            } else {
                if (cell.getRotation() == 0 || cell.getRotation() == 180) {
                    float rightLimit = cell.isNoWrap() ? 20000 : cell.getRight()
                            - cell.getEffectivePaddingRight();
                    float bry = (cell.getFixedHeight() > 0) ? cell.getTop()
                            - cell.getEffectivePaddingTop()
                            + cell.getEffectivePaddingBottom()
                            - cell.getFixedHeight() : BOTTOM_LIMIT;
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    setColumn(ct,
                            cell.getLeft() + cell.getEffectivePaddingLeft(), bry,
                            rightLimit, cell.getTop() - cell.getEffectivePaddingTop());
                    try {
                        ct.go(true);
                    } catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                    float yLine = ct.getYLine();
                    if (cell.isUseDescender())
                        yLine += ct.getDescender();
                    cell.setBottom(yLine - cell.getEffectivePaddingBottom());
                }
                else {
                    if (cell.getFixedHeight() > 0) {
                        cell.setBottom(cell.getTop() - cell.getFixedHeight());
                    }
                    else {
                        ColumnText ct = ColumnText.duplicate(cell.getColumn());
                        setColumn(ct, 0, cell.getLeft() + cell.getEffectivePaddingLeft(),
                                20000, cell.getRight() - cell.getEffectivePaddingRight());
                        try {
                            ct.go(true);
                        } catch (DocumentException e) {
                            throw new ExceptionConverter(e);
                        }
                        cell.setBottom(cell.getTop() - cell.getEffectivePaddingTop() 
                            - cell.getEffectivePaddingBottom() - ct.getFilledWidth());
                    }
                }
            }
            float height = cell.getFixedHeight();
            if (height <= 0)
                height = cell.getHeight();
            if (height < cell.getFixedHeight())
                height = cell.getFixedHeight();
            else if (height < cell.getMinimumHeight())
                height = cell.getMinimumHeight();
            if (height > maxHeight)
                maxHeight = height;
        }
        calculated = true;
        return maxHeight;
    }

    
    public void writeBorderAndBackground(float xPos, float yPos, PdfPCell cell,
            PdfContentByte[] canvases) {
        PdfContentByte lines = canvases[PdfPTable.LINECANVAS];
        PdfContentByte backgr = canvases[PdfPTable.BACKGROUNDCANVAS];
        
        float x1 = cell.getLeft() + xPos;
        float y2 = cell.getTop() + yPos;
        float x2 = cell.getRight() + xPos;
        float y1 = y2 - maxHeight;

        
        Color background = cell.getBackgroundColor();
        if (background != null) {
            backgr.setColorFill(background);
            backgr.rectangle(x1, y1, x2 - x1, y2 - y1);
            backgr.fill();
        }
        
        if (cell.hasBorders()) {
            if (cell.isUseVariableBorders()) {
                Rectangle borderRect = new Rectangle(cell.getLeft() + xPos, cell
                        .getTop()
                        - maxHeight + yPos, cell.getRight() + xPos, cell.getTop()
                        + yPos);
                borderRect.cloneNonPositionParameters(cell);
                borderRect.setBackgroundColor(null);
                lines.rectangle(borderRect);
            } else {
                
                if (cell.getBorderWidth() != Rectangle.UNDEFINED) {
                    lines.setLineWidth(cell.getBorderWidth());
                }
                
                Color color = cell.getBorderColor();
                if (color != null) {
                    lines.setColorStroke(color);
                }

                
                if (cell.hasBorder(Rectangle.BOX)) {
                    lines.rectangle(x1, y1, x2 - x1, y2 - y1);
                }
                
                
                else {
                    if (cell.hasBorder(Rectangle.RIGHT)) {
                        lines.moveTo(x2, y1);
                        lines.lineTo(x2, y2);
                    }
                    if (cell.hasBorder(Rectangle.LEFT)) {
                        lines.moveTo(x1, y1);
                        lines.lineTo(x1, y2);
                    }
                    if (cell.hasBorder(Rectangle.BOTTOM)) {
                        lines.moveTo(x1, y1);
                        lines.lineTo(x2, y1);
                    }
                    if (cell.hasBorder(Rectangle.TOP)) {
                        lines.moveTo(x1, y2);
                        lines.lineTo(x2, y2);
                    }
                }
                lines.stroke();
                if (color != null) {
                    lines.resetRGBColorStroke();
                }
            }
        }
    }

    private void saveAndRotateCanvases(PdfContentByte[] canvases, float a, float b, float c, float d, float e, float f) {
        int last = PdfPTable.TEXTCANVAS + 1;
        if (canvasesPos == null) {
            canvasesPos = new int[last * 2];
        }
        for (int k = 0; k < last; ++k) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            canvasesPos[k * 2] = bb.size();
            canvases[k].saveState();
            canvases[k].concatCTM(a, b, c, d, e, f);
            canvasesPos[k * 2 + 1] = bb.size();
        }
    }
    
    private void restoreCanvases(PdfContentByte[] canvases) {
        int last = PdfPTable.TEXTCANVAS + 1;
        for (int k = 0; k < last; ++k) {
            ByteBuffer bb = canvases[k].getInternalBuffer();
            int p1 = bb.size();
            canvases[k].restoreState();
            if (p1 == canvasesPos[k * 2 + 1])
                bb.setSize(canvasesPos[k * 2]);
        }
    }
    
    private float setColumn(ColumnText ct, float llx, float lly, float urx, float ury) {
        if (llx > urx)
            urx = llx;
        if (lly > ury)
            ury = lly;
        ct.setSimpleColumn(llx, lly, urx, ury);
        return ury;
    }
    
    
    public void writeCells(int colStart, int colEnd, float xPos, float yPos,
            PdfContentByte[] canvases) {
        if (!calculated)
            calculateHeights();
        if (colEnd < 0)
            colEnd = cells.length;
        colEnd = Math.min(colEnd, cells.length);
        if (colStart < 0)
            colStart = 0;
        if (colStart >= colEnd)
            return;
        int newStart;
        for (newStart = colStart; newStart >= 0; --newStart) {
            if (cells[newStart] != null)
                break;
            xPos -= widths[newStart - 1];
        }
        xPos -= cells[newStart].getLeft();
        for (int k = newStart; k < colEnd; ++k) {
            PdfPCell cell = cells[k];
            if (cell == null)
                continue;
            writeBorderAndBackground(xPos, yPos, cell, canvases);
            Image img = cell.getImage();
            float tly = 0;
            switch (cell.getVerticalAlignment()) {
            case Element.ALIGN_BOTTOM:
                tly = cell.getTop() + yPos - maxHeight + cell.getHeight()
                        - cell.getEffectivePaddingTop();
                break;
            case Element.ALIGN_MIDDLE:
                tly = cell.getTop() + yPos + (cell.getHeight() - maxHeight) / 2
                        - cell.getEffectivePaddingTop();
                break;
            default:
                tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                break;
            }
            if (img != null) {
                if (cell.getRotation() != 0) {
                    img = Image.getInstance(img);
                    img.setRotation(img.getImageRotation() + (float)(cell.getRotation() * Math.PI / 180.0));
                }
                boolean vf = false;
                if (cell.getHeight() > maxHeight) {
                    img.scalePercent(100);
                    float scale = (maxHeight - cell.getEffectivePaddingTop() - cell
                            .getEffectivePaddingBottom())
                            / img.getScaledHeight();
                    img.scalePercent(scale * 100);
                    vf = true;
                }
                float left = cell.getLeft() + xPos
                        + cell.getEffectivePaddingLeft();
                if (vf) {
                    switch (cell.getHorizontalAlignment()) {
                    case Element.ALIGN_CENTER:
                        left = xPos
                                + (cell.getLeft() + cell.getEffectivePaddingLeft()
                                        + cell.getRight()
                                        - cell.getEffectivePaddingRight() - img
                                        .getScaledWidth()) / 2;
                        break;
                    case Element.ALIGN_RIGHT:
                        left = xPos + cell.getRight()
                                - cell.getEffectivePaddingRight()
                                - img.getScaledWidth();
                        break;
                    default:
                        break;
                    }
                    tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                }
                img.setAbsolutePosition(left, tly - img.getScaledHeight());
                try {
                    canvases[PdfPTable.TEXTCANVAS].addImage(img);
                } catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
            } else {
                
                if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    float netWidth = maxHeight - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom();
                    float netHeight = cell.getWidth() - cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight();
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    ct.setCanvases(canvases);
                    ct.setSimpleColumn(0, 0, netWidth + 0.001f, -netHeight);
                    try {
                        ct.go(true);
                    } catch (DocumentException e) {
                        throw new ExceptionConverter(e);
                    }
                    float calcHeight = -ct.getYLine();
                    if (netWidth <= 0 || netHeight <= 0)
                        calcHeight = 0;
                    if (calcHeight > 0) {
                        if (cell.isUseDescender())
                            calcHeight -= ct.getDescender();
                        ct = ColumnText.duplicate(cell.getColumn());
                        ct.setCanvases(canvases);
                        ct.setSimpleColumn(0, -0.001f, netWidth + 0.001f, calcHeight);
                        float pivotX;
                        float pivotY;
                        if (cell.getRotation() == 90) {
                            pivotY = cell.getTop() + yPos - maxHeight + cell.getEffectivePaddingBottom();
                            switch (cell.getVerticalAlignment()) {
                            case Element.ALIGN_BOTTOM:
                                pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight();
                                break;
                            case Element.ALIGN_MIDDLE:
                                pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() + calcHeight) / 2;
                                break;
                            default: 
                                pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft() + calcHeight;
                                break;
                            }
                            saveAndRotateCanvases(canvases, 0,1,-1,0,pivotX,pivotY);
                        }
                        else {
                            pivotY = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                            switch (cell.getVerticalAlignment()) {
                            case Element.ALIGN_BOTTOM:
                                pivotX = cell.getLeft() + xPos + cell.getEffectivePaddingLeft();
                                break;
                            case Element.ALIGN_MIDDLE:
                                pivotX = cell.getLeft() + xPos + (cell.getWidth() + cell.getEffectivePaddingLeft() - cell.getEffectivePaddingRight() - calcHeight) / 2;
                                break;
                            default: 
                                pivotX = cell.getLeft() + xPos + cell.getWidth() - cell.getEffectivePaddingRight() - calcHeight;
                                break;
                            }
                            saveAndRotateCanvases(canvases, 0,-1,1,0,pivotX,pivotY);
                        }
                        try {
                            ct.go();
                        } catch (DocumentException e) {
                            throw new ExceptionConverter(e);
                        } finally {
                            restoreCanvases(canvases);
                        }
                    }
                } 
                else {
                    float fixedHeight = cell.getFixedHeight();
                    float rightLimit = cell.getRight() + xPos
                            - cell.getEffectivePaddingRight();
                    float leftLimit = cell.getLeft() + xPos
                            + cell.getEffectivePaddingLeft();
                    if (cell.isNoWrap()) {
                        switch (cell.getHorizontalAlignment()) {
                            case Element.ALIGN_CENTER:
                                rightLimit += 10000;
                                leftLimit -= 10000;
                                break;
                            case Element.ALIGN_RIGHT:
                                leftLimit -= 20000;
                                break;
                            default:
                                rightLimit += 20000;
                                break;
                        }
                    }
                    ColumnText ct = ColumnText.duplicate(cell.getColumn());
                    ct.setCanvases(canvases);
                    float bry = tly
                            - (maxHeight 
                            - cell.getEffectivePaddingTop() - cell.getEffectivePaddingBottom());
                    if (fixedHeight > 0) {
                        if (cell.getHeight() > maxHeight) {
                            tly = cell.getTop() + yPos - cell.getEffectivePaddingTop();
                            bry = cell.getTop() + yPos - maxHeight + cell.getEffectivePaddingBottom();
                        }
                    }
                    if (tly > bry && leftLimit < rightLimit) {
                        ct.setSimpleColumn(leftLimit, bry - 0.001f,    rightLimit, tly);
                        if (cell.getRotation() == 180) {
                            float shx = leftLimit + rightLimit;
                            float shy = yPos + yPos - maxHeight + cell.getEffectivePaddingBottom() - cell.getEffectivePaddingTop();
                            saveAndRotateCanvases(canvases, -1,0,0,-1,shx,shy);
                        }
                        try {
                            ct.go();
                        } catch (DocumentException e) {
                            throw new ExceptionConverter(e);
                        } finally {
                            if (cell.getRotation() == 180) {
                                restoreCanvases(canvases);
                            }
                        }
                    }
                }
            }
            PdfPCellEvent evt = cell.getCellEvent();
            if (evt != null) {
                Rectangle rect = new Rectangle(cell.getLeft() + xPos, cell.getTop()
                        + yPos - maxHeight, cell.getRight() + xPos, cell.getTop()
                        + yPos);
                evt.cellLayout(cell, rect, canvases);
            }
        }
    }

    
    public boolean isCalculated() {
        return calculated;
    }

    
    public float getMaxHeights() {
        if (calculated)
            return maxHeight;
        else
            return calculateHeights();
    }

    
    public void setMaxHeights(float maxHeight) {
        this.maxHeight = maxHeight;
    }

    

    float[] getEventWidth(float xPos) {
        int n = 0;
        for (int k = 0; k < cells.length; ++k) {
            if (cells[k] != null)
                ++n;
        }
        float width[] = new float[n + 1];
        n = 0;
        width[n++] = xPos;
        for (int k = 0; k < cells.length; ++k) {
            if (cells[k] != null) {
                width[n] = width[n - 1] + cells[k].getWidth();
                ++n;
            }
        }
        return width;
    }

    
    public PdfPRow splitRow(float newHeight) {
        PdfPCell newCells[] = new PdfPCell[cells.length];
        float fh[] = new float[cells.length * 2];
        boolean allEmpty = true;
        for (int k = 0; k < cells.length; ++k) {
            PdfPCell cell = cells[k];
            if (cell == null)
                continue;
            fh[k * 2] = cell.getFixedHeight();
            fh[k * 2 + 1] = cell.getMinimumHeight();
            Image img = cell.getImage();
            PdfPCell c2 = new PdfPCell(cell);
            if (img != null) {
                if (newHeight > cell.getEffectivePaddingBottom()
                        + cell.getEffectivePaddingTop() + 2) {
                    c2.setPhrase(null);
                    allEmpty = false;
                }
            } else {
                int status;
                float y;
                ColumnText ct = ColumnText.duplicate(cell.getColumn());
                if (cell.getRotation() == 90 || cell.getRotation() == 270) {
                    y = setColumn(ct,
                            cell.getTop() - newHeight + cell.getEffectivePaddingBottom(),
                            cell.getLeft() + cell.getEffectivePaddingLeft(),
                            cell.getTop() - cell.getEffectivePaddingTop(),
                            cell.getRight() - cell.getEffectivePaddingRight());
                }
                else {
                    float rightLimit = cell.isNoWrap() ? 20000 : cell.getRight()
                            - cell.getEffectivePaddingRight();
                    float y1 = cell.getTop() - newHeight
                            + cell.getEffectivePaddingBottom();
                    float y2 = cell.getTop() - cell.getEffectivePaddingTop();
                    y = setColumn(ct,
                            cell.getLeft() + cell.getEffectivePaddingLeft(), y1,
                            rightLimit, y2);
                }
                try {
                    status = ct.go(true);
                } catch (DocumentException e) {
                    throw new ExceptionConverter(e);
                }
                boolean thisEmpty = (ct.getYLine() == y);
                if (thisEmpty)
                    ct = ColumnText.duplicate(cell.getColumn());
                allEmpty = (allEmpty && thisEmpty);
                if ((status & ColumnText.NO_MORE_TEXT) == 0 || thisEmpty) {
                    c2.setColumn(ct);
                    ct.setFilledWidth(0);
                } else {
                    c2.setPhrase(null);
                }
            }
            newCells[k] = c2;
            cell.setFixedHeight(newHeight);
        }
        if (allEmpty) {
            for (int k = 0; k < cells.length; ++k) {
                PdfPCell cell = cells[k];
                if (cell == null)
                    continue;
                float f = fh[k * 2];
                float m = fh[k * 2 + 1];
                if (f <= 0)
                    cell.setMinimumHeight(m);
                else
                    cell.setFixedHeight(f);
            }
            return null;
        }
        calculateHeights();
        PdfPRow split = new PdfPRow(newCells);
        split.widths = (float[]) widths.clone();
        split.calculateHeights();
        return split;
    }
    
    
    public PdfPCell[] getCells() {
        return cells;
    }
}