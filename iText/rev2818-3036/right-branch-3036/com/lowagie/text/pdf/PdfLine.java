

package com.lowagie.text.pdf;

import java.util.ArrayList;
import java.util.Iterator;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.ListItem;



public class PdfLine {
    
    
    
    
    protected ArrayList<PdfChunk> line;
    
    
    protected float left;
    
    
    protected float width;
    
    
    protected int alignment;
    
    
    protected float height;
    
    
    protected Chunk listSymbol = null;
    
    
    protected float symbolIndent;
    
    
    protected boolean newlineSplit = false;
    
    
    protected float originalWidth;
    
    protected boolean isRTL = false;
    
    
    
    
    
    PdfLine(float left, float right, int alignment, float height) {
        this.left = left;
        this.width = right - left;
        this.originalWidth = this.width;
        this.alignment = alignment;
        this.height = height;
        this.line = new ArrayList<PdfChunk>();
    }
    
    PdfLine(float left, float remainingWidth, int alignment, boolean newlineSplit, ArrayList<PdfChunk> line, boolean isRTL) {
        this.left = left;
        this.width = remainingWidth;
        this.alignment = alignment;
        this.line = line;
        this.newlineSplit = newlineSplit;
        this.isRTL = isRTL;
    }
    
    
    
    
    
    PdfChunk add(PdfChunk chunk) {
        
        if (chunk == null || chunk.toString().equals("")) {
            return null;
        }
        
        
        PdfChunk overflow = chunk.split(width);
        newlineSplit = (chunk.isNewlineSplit() || overflow == null);
        
        
        
        
        
        if (chunk.length() > 0) {
            if (overflow != null)
                chunk.trimLastSpace();
            width -= chunk.width();
            addToLine(chunk);
        }
        
        
        
        else if (line.size() < 1) {
            chunk = overflow;
            overflow = chunk.truncate(width);
            width -= chunk.width();
            if (chunk.length() > 0) {
                addToLine(chunk);
                return overflow;
            }
            
            else {
                if (overflow != null)
                    addToLine(overflow);
                return null;
            }
        }
        else {
            width += line.get(line.size() - 1).trimLastSpace();
        }
        return overflow;
    }
    
    private void addToLine(PdfChunk chunk) {
        if (chunk.changeLeading && chunk.isImage()) {
            float f = chunk.getImage().getScaledHeight() + chunk.getImageOffsetY();
            if (f > height) height = f;
        }
        line.add(chunk);
    }
    
    
    
    
    
    public int size() {
        return line.size();
    }
    
    
    
    public Iterator<PdfChunk> iterator() {
        return line.iterator();
    }
    
    
    
    float height() {
        return height;
    }
    
    
    
    float indentLeft() {
        if (isRTL) {
            switch (alignment) {
                case Element.ALIGN_LEFT:
                    return left + width;
                case Element.ALIGN_CENTER:
                    return left + (width / 2f);
                default:
                    return left;
            }
        }
        else {
            switch (alignment) {
                case Element.ALIGN_RIGHT:
                    return left + width;
                case Element.ALIGN_CENTER:
                    return left + (width / 2f);
                default:
                    return left;
            }
        }
    }
    
    
    
    public boolean hasToBeJustified() {
        return ((alignment == Element.ALIGN_JUSTIFIED || alignment == Element.ALIGN_JUSTIFIED_ALL) && width != 0);
    }
    
    
    
    public void resetAlignment() {
        if (alignment == Element.ALIGN_JUSTIFIED) {
            alignment = Element.ALIGN_LEFT;
        }
    }
    
    
    void setExtraIndent(float extra) {
        left += extra;
        width -= extra;
    }
    
    
    
    float widthLeft() {
        return width;
    }
    
    
    
    int numberOfSpaces() {
        String string = toString();
        int length = string.length();
        int numberOfSpaces = 0;
        for (int i = 0; i < length; i++) {
            if (string.charAt(i) == ' ') {
                numberOfSpaces++;
            }
        }
        return numberOfSpaces;
    }
    
    
    
    public void setListItem(ListItem listItem) {
        this.listSymbol = listItem.getListSymbol();
        this.symbolIndent = listItem.getIndentationLeft();
    }
    
    
    
    public Chunk listSymbol() {
        return listSymbol;
    }
    
    
    
    public float listIndent() {
        return symbolIndent;
    }
    
    
    
    public String toString() {
        StringBuffer tmp = new StringBuffer();
        for (Iterator<PdfChunk> i = line.iterator(); i.hasNext(); ) {
            tmp.append(i.next().toString());
        }
        return tmp.toString();
    }
    
    
    public boolean isNewlineSplit() {
        return newlineSplit && (alignment != Element.ALIGN_JUSTIFIED_ALL);
    }
    
    
    public int getLastStrokeChunk() {
        int lastIdx = line.size() - 1;
        for (; lastIdx >= 0; --lastIdx) {
            PdfChunk chunk = line.get(lastIdx);
            if (chunk.isStroked())
                break;
        }
        return lastIdx;
    }
    
    
    public PdfChunk getChunk(int idx) {
        if (idx < 0 || idx >= line.size())
            return null;
        return line.get(idx);
    }
    
    
    public float getOriginalWidth() {
        return originalWidth;
    }
    
    
    float getMaxSizeSimple() {
        float maxSize = 0;
        for (int k = 0; k < line.size(); ++k) {
            PdfChunk chunk = line.get(k);
            if (!chunk.isImage()) {
                maxSize = Math.max(chunk.font().size(), maxSize);
            }
            else {
                maxSize = Math.max(chunk.getImage().getScaledHeight() + chunk.getImageOffsetY() , maxSize);
            }
        }
        return maxSize;
    }
    
    boolean isRTL() {
        return isRTL;
    }
    
    
    public float getWidthCorrected(float charSpacing, float wordSpacing) {
        float total = 0;
        for (int k = 0; k < line.size(); ++k) {
            PdfChunk ck = line.get(k);
            total += ck.getWidthCorrected(charSpacing, wordSpacing);
        }
        return total;
    }
    

   public float getAscender() {
       float ascender = 0;
       for (int k = 0; k < line.size(); ++k) {
           PdfChunk ck = line.get(k);
           if (ck.isImage())
               ascender = Math.max(ascender, ck.getImage().getScaledHeight() + ck.getImageOffsetY());
           else {
               PdfFont font = ck.font();
               ascender = Math.max(ascender, font.getFont().getFontDescriptor(BaseFont.ASCENT, font.size()));
           }
       }
       return ascender;
   }


    public float getDescender() {
        float descender = 0;
        for (int k = 0; k < line.size(); ++k) {
            PdfChunk ck = line.get(k);
            if (ck.isImage())
                descender = Math.min(descender, ck.getImageOffsetY());
            else {
                PdfFont font = ck.font();
                descender = Math.min(descender, font.getFont().getFontDescriptor(BaseFont.DESCENT, font.size()));
            }
        }
        return descender;
    }
}