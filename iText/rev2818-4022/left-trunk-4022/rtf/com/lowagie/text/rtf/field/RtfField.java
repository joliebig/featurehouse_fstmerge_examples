

package com.lowagie.text.rtf.field;

import java.io.IOException;
import java.io.OutputStream;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocWriter;
import com.lowagie.text.Font;
import com.lowagie.text.rtf.RtfBasicElement;
import com.lowagie.text.rtf.document.RtfDocument;
import com.lowagie.text.rtf.style.RtfFont;



public abstract class RtfField extends Chunk implements RtfBasicElement {

    
    private static final byte[] FIELD = DocWriter.getISOBytes("\\field");
    
    private static final byte[] FIELD_DIRTY = DocWriter.getISOBytes("\\flddirty");
    
    private static final byte[] FIELD_PRIVATE = DocWriter.getISOBytes("\\fldpriv");
    
    private static final byte[] FIELD_LOCKED = DocWriter.getISOBytes("\\fldlock");
    
    private static final byte[] FIELD_EDIT = DocWriter.getISOBytes("\\fldedit");
    
    private static final byte[] FIELD_ALT = DocWriter.getISOBytes("\\fldalt");
    
    private static final byte[] FIELD_INSTRUCTIONS = DocWriter.getISOBytes("\\*\\fldinst");
    
    private static final byte[] FIELD_RESULT = DocWriter.getISOBytes("\\fldrslt");

    
    private boolean fieldDirty = false;
    
    private boolean fieldEdit = false;
    
    private boolean fieldLocked = false;
    
    private boolean fieldPrivate = false;
    
    private boolean fieldAlt = false;
    
    private boolean inTable = false;
    
    private boolean inHeader = false;
    
    protected RtfDocument document = null;
    
    private RtfFont font = null;

    
    protected RtfField(RtfDocument doc) {
        this(doc, new Font());
    }
    
    
    protected RtfField(RtfDocument doc, Font font) {
        super("", font);
        this.document = doc;
        this.font = new RtfFont(this.document, font);
    }
    
    
    public void setRtfDocument(RtfDocument doc) {
        this.document = doc;
        this.font.setRtfDocument(this.document);
    }
    
    
    private void writeFieldBegin(OutputStream result) throws IOException 
    {
        result.write(OPEN_GROUP);
        result.write(FIELD);
        if(fieldDirty) result.write(FIELD_DIRTY);
        if(fieldEdit) result.write(FIELD_EDIT);
        if(fieldLocked) result.write(FIELD_LOCKED);
        if(fieldPrivate) result.write(FIELD_PRIVATE);
    }
    
    
    private void writeFieldInstBegin(OutputStream result) throws IOException 
    {
        result.write(OPEN_GROUP);        
        result.write(FIELD_INSTRUCTIONS);
        result.write(DELIMITER);
    }
    
    
    protected abstract void writeFieldInstContent(OutputStream result) throws IOException;
    
    
    private void writeFieldInstEnd(OutputStream result) throws IOException 
    {
        if(fieldAlt) {
            result.write(DELIMITER);
            result.write(FIELD_ALT);
        }
        result.write(CLOSE_GROUP);
    }
    
    
    private void writeFieldResultBegin(final OutputStream result) throws IOException 
    {
        result.write(OPEN_GROUP);
        result.write(FIELD_RESULT);
        result.write(DELIMITER);
    }
    
     
    protected abstract void writeFieldResultContent(OutputStream result) throws IOException;
    
     
    private void writeFieldResultEnd(final OutputStream result) throws IOException 
    {
        result.write(DELIMITER);
        result.write(CLOSE_GROUP);
    }
    
    
    private void writeFieldEnd(OutputStream result) throws IOException
    {
        result.write(CLOSE_GROUP);
    }
    
        
    public void writeContent(final OutputStream result) throws IOException
    {
        this.font.writeBegin(result);
        writeFieldBegin(result);
        writeFieldInstBegin(result);
        writeFieldInstContent(result);
        writeFieldInstEnd(result);
        writeFieldResultBegin(result);
        writeFieldResultContent(result);
        writeFieldResultEnd(result);
        writeFieldEnd(result);
        this.font.writeEnd(result);
    }        
        
    
    public boolean isFieldAlt() {
        return fieldAlt;
    }
    
    
    public void setFieldAlt(boolean fieldAlt) {
        this.fieldAlt = fieldAlt;
    }
    
    
    public boolean isFieldDirty() {
        return fieldDirty;
    }
    
    
    public void setFieldDirty(boolean fieldDirty) {
        this.fieldDirty = fieldDirty;
    }
    
    
    public boolean isFieldEdit() {
        return fieldEdit;
    }
    
    
    public void setFieldEdit(boolean fieldEdit) {
        this.fieldEdit = fieldEdit;
    }
    
    
    public boolean isFieldLocked() {
        return fieldLocked;
    }
    
    
    public void setFieldLocked(boolean fieldLocked) {
        this.fieldLocked = fieldLocked;
    }
    
    
    public boolean isFieldPrivate() {
        return fieldPrivate;
    }
    
    
    public void setFieldPrivate(boolean fieldPrivate) {
        this.fieldPrivate = fieldPrivate;
    }

    
    public void setInTable(boolean inTable) {
        this.inTable = inTable;
    }
    
    
    public boolean isInTable() {
        return this.inTable;
    }
    
    
    public void setInHeader(boolean inHeader) {
        this.inHeader = inHeader;
    }
    
    
    public boolean isInHeader() {
        return this.inHeader;
    }
    
    
    public boolean isEmpty() {
        return false;
    }
    
    
    public void setFont(Font font) {
        super.setFont(font);
        this.font = new RtfFont(this.document, font);
    }
}
