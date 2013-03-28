


package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;


public class ExportData extends FreeColObject {

    
    private int highLevel = 90;

    
    private int lowLevel = 10;

    
    private int exportLevel = 50;

    
    private boolean exported = false;

    
    public ExportData() {}
    
    
    public ExportData(GoodsType goodsType) {
        setId(goodsType.getId());
    }

    
    public ExportData(GoodsType goodsType, boolean exported, int lowLevel, int highLevel, int exportLevel) {
        setId(goodsType.getId());
        this.exported = exported;
        this.lowLevel = lowLevel;
        this.highLevel = highLevel;
        this.exportLevel = exportLevel;
    }

    
    public ExportData(GoodsType goodsType, boolean exported, int exportLevel) {
        this(goodsType, exported, 0, 100, exportLevel);
    }

    
    public ExportData(GoodsType goodsType, ExportData template) {
        setId(goodsType.getId());
        this.exported = template.exported;
        this.lowLevel = template.lowLevel;
        this.highLevel = template.highLevel;
        this.exportLevel = template.exportLevel;
    }

    
    public final int getHighLevel() {
        return highLevel;
    }

    
    public final void setHighLevel(final int newHighLevel) {
        this.highLevel = newHighLevel;
    }

    
    public final int getLowLevel() {
        return lowLevel;
    }

    
    public final void setLowLevel(final int newLowLevel) {
        this.lowLevel = newLowLevel;
    }

    
    public final int getExportLevel() {
        return exportLevel;
    }

    
    public final void setExportLevel(final int newExportLevel) {
        this.exportLevel = newExportLevel;
    }

    
    public final boolean isExported() {
        return exported;
    }

    
    public final void setExported(final boolean newExport) {
        this.exported = newExport;
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("exported", Boolean.toString(exported));
        out.writeAttribute("highLevel", Integer.toString(highLevel));
        out.writeAttribute("lowLevel", Integer.toString(lowLevel));
        out.writeAttribute("exportLevel", Integer.toString(exportLevel));

        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        exported = Boolean.parseBoolean(in.getAttributeValue(null, "exported"));
        highLevel = Integer.parseInt(in.getAttributeValue(null, "highLevel"));
        lowLevel = Integer.parseInt(in.getAttributeValue(null, "lowLevel"));
        exportLevel = Integer.parseInt(in.getAttributeValue(null, "exportLevel"));
    
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "exportData";
    }

} 
