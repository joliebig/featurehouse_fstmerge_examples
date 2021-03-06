

package com.lowagie.text.pdf;

import java.util.HashMap;

import com.lowagie.text.DocumentException;


public class Type3Font extends BaseFont {
    
    private boolean[] usedSlot;
    private IntHashtable widths3 = new IntHashtable();
    private HashMap<Integer, Type3Glyph> char2glyph = new HashMap<Integer, Type3Glyph>();
    private PdfWriter writer;
    private float llx = Float.NaN, lly, urx, ury;
    private PageResources pageResources = new PageResources();
    private boolean colorized;
    
        
    public Type3Font(PdfWriter writer, char[] chars, boolean colorized) {
        this(writer, colorized);
    }
    
        
    public Type3Font(PdfWriter writer, boolean colorized) {
        this.writer = writer;
        this.colorized = colorized;
        fontType = FONT_TYPE_T3;
        usedSlot = new boolean[256];
    }
    
        
    public PdfContentByte defineGlyph(char c, float wx, float llx, float lly, float urx, float ury) {
        if (c == 0 || c > 255)
            throw new IllegalArgumentException("The char " + (int)c + " doesn't belong in this Type3 font");
        usedSlot[c] = true;
        Integer ck = new Integer(c);
        Type3Glyph glyph = char2glyph.get(ck);
        if (glyph != null)
            return glyph;
        widths3.put(c, (int)wx);
        if (!colorized) {
            if (Float.isNaN(this.llx)) {
                this.llx = llx;
                this.lly = lly;
                this.urx = urx;
                this.ury = ury;
            }
            else {
                this.llx = Math.min(this.llx, llx);
                this.lly = Math.min(this.lly, lly);
                this.urx = Math.max(this.urx, urx);
                this.ury = Math.max(this.ury, ury);
            }
        }
        glyph = new Type3Glyph(writer, pageResources, wx, llx, lly, urx, ury, colorized);
        char2glyph.put(ck, glyph);
        return glyph;
    }
    
    public String[][] getFamilyFontName() {
        return new String[0][];
    }
    
    public float getFontDescriptor(int key, float fontSize) {
        return 0;
    }
    
    public String[][] getFullFontName() {
        return new String[0][];
    }
    
    public int getKerning(char char1, char char2) {
        return 0;
    }
    
    public String getPostscriptFontName() {
        return "";
    }
    
    protected int[] getRawCharBBox(int c, String name) {
        return null;
    }
    
    int getRawWidth(int c, String name) {
        return 0;
    }
    
    public boolean hasKernPairs() {
        return false;
    }
    
    public boolean setKerning(char char1, char char2, int kern) {
        return false;
    }
    
    public void setPostscriptFontName(String name) {
    }
    
    void writeFont(PdfWriter writer, PdfIndirectReference ref, Object[] params) throws com.lowagie.text.DocumentException, java.io.IOException {
        if (this.writer != writer)
            throw new IllegalArgumentException("Type3 font used with the wrong PdfWriter");
        
        
        int firstChar = 0;
        while( firstChar < usedSlot.length && !usedSlot[firstChar] ) firstChar++;
        
        if ( firstChar == usedSlot.length ) {
            throw new DocumentException( "No glyphs defined for Type3 font" );
        }
        int lastChar = usedSlot.length - 1;
        while( lastChar >= firstChar && !usedSlot[lastChar] ) lastChar--;
        
        int[] widths = new int[lastChar - firstChar + 1];
        int[] invOrd = new int[lastChar - firstChar + 1];
        
        int invOrdIndx = 0, w = 0;
        for( int u = firstChar; u<=lastChar; u++, w++ ) {
            if ( usedSlot[u] ) {
                invOrd[invOrdIndx++] = u;
                widths[w] = widths3.get(u);
            }
        }
        PdfArray diffs = new PdfArray();
        PdfDictionary charprocs = new PdfDictionary();
        int last = -1;
        for (int k = 0; k < invOrdIndx; ++k) {
            int c = invOrd[k];
            if (c > last) {
                last = c;
                diffs.add(new PdfNumber(last));
            }
            ++last;
            int c2 = invOrd[k];
            String s = GlyphList.unicodeToName(c2);
            if (s == null)
                s = "a" + c2;
            PdfName n = new PdfName(s);
            diffs.add(n);
            Type3Glyph glyph = char2glyph.get(new Integer(c2));
            PdfStream stream = new PdfStream(glyph.toPdf(null));
            stream.flateCompress();
            PdfIndirectReference refp = writer.addToBody(stream).getIndirectReference();
            charprocs.put(n, refp);
        }
        PdfDictionary font = new PdfDictionary(PdfName.FONT);
        font.put(PdfName.SUBTYPE, PdfName.TYPE3);
        if (colorized)
            font.put(PdfName.FONTBBOX, new PdfRectangle(0, 0, 0, 0));
        else
            font.put(PdfName.FONTBBOX, new PdfRectangle(llx, lly, urx, ury));
        font.put(PdfName.FONTMATRIX, new PdfArray(new float[]{0.001f, 0, 0, 0.001f, 0, 0}));
        font.put(PdfName.CHARPROCS, writer.addToBody(charprocs).getIndirectReference());
        PdfDictionary encoding = new PdfDictionary();
        encoding.put(PdfName.DIFFERENCES, diffs);
        font.put(PdfName.ENCODING, writer.addToBody(encoding).getIndirectReference());
        font.put(PdfName.FIRSTCHAR, new PdfNumber(firstChar));
        font.put(PdfName.LASTCHAR, new PdfNumber(lastChar));
        font.put(PdfName.WIDTHS, writer.addToBody(new PdfArray(widths)).getIndirectReference());
        if (pageResources.hasResources())
            font.put(PdfName.RESOURCES, writer.addToBody(pageResources.getResources()).getIndirectReference());
        writer.addToBody(font, ref);
    }
    
    
    byte[] convertToBytes(String text) {
        char[] cc = text.toCharArray();
        byte[] b = new byte[cc.length];
        int p = 0;
        for (int k = 0; k < cc.length; ++k) {
            char c = cc[k];
            if (charExists(c))
                b[p++] = (byte)c;
        }
        if (b.length == p)
            return b;
        byte[] b2 = new byte[p];
        System.arraycopy(b, 0, b2, 0, p);
        return b2;
    }
    
    byte[] convertToBytes(char char1) {
        if (charExists(char1))
            return new byte[]{(byte)char1};
        else return new byte[0];
    }
    
    public int getWidth(char char1) {
        if (!widths3.containsKey(char1))
            throw new IllegalArgumentException("The char " + (int)char1 + " is not defined in a Type3 font");
        return widths3.get(char1);
    }
    
    public int getWidth(String text) {
        char[] c = text.toCharArray();
        int total = 0;
        for (int k = 0; k < c.length; ++k)
            total += getWidth(c[k]);
        return total;
    }
    
    public int[] getCharBBox(char c) {
        return null;
    }
    
    public boolean charExists(char c) {
        if (c > 0 && c < 256) {
            return usedSlot[c];
        } else {
            return false;
        }
    }
    
    public boolean setCharAdvance(char c, int advance) {
        return false;
    }
    
}
