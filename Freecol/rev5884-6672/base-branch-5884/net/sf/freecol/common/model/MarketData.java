


package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.Specification;


public class MarketData extends FreeColObject {

    
    private int costToBuy;

    
    private int paidForSale;

    
    private int amountInMarket;

    
    private int initialPrice;

    
    private int arrears;

    
    private int sales;

    
    private int incomeBeforeTaxes;

    
    private int incomeAfterTaxes;

    
    private int oldPrice;

    
    private boolean traded;


    
    public MarketData() {
        traded = false;
    }
    
    
    public MarketData(GoodsType goodsType) {
        setId(goodsType.getId());
        traded = false;
    }

    
    public final int getCostToBuy() {
        return costToBuy;
    }

    
    public final void setCostToBuy(final int newCostToBuy) {
        this.costToBuy = newCostToBuy;
    }

    
    public final int getPaidForSale() {
        return paidForSale;
    }

    
    public final void setPaidForSale(final int newPaidForSale) {
        this.paidForSale = newPaidForSale;
    }

    
    public final int getAmountInMarket() {
        return amountInMarket;
    }

    
    public final void setAmountInMarket(final int newAmountInMarket) {
        this.amountInMarket = newAmountInMarket;
    }

    
    public final int getInitialPrice() {
        return initialPrice;
    }

    
    public final void setInitialPrice(final int newInitialPrice) {
        this.initialPrice = newInitialPrice;
    }

    
    public final int getArrears() {
        return arrears;
    }

    
    public final void setArrears(final int newArrears) {
        this.arrears = newArrears;
    }

    
    public final int getSales() {
        return sales;
    }

    
    public final void setSales(final int newSales) {
        this.traded |= this.sales != newSales;
        this.sales = newSales;
    }

    
    public final int getIncomeBeforeTaxes() {
        return incomeBeforeTaxes;
    }

    
    public final void setIncomeBeforeTaxes(final int newIncomeBeforeTaxes) {
        this.incomeBeforeTaxes = newIncomeBeforeTaxes;
    }

    
    public final int getIncomeAfterTaxes() {
        return incomeAfterTaxes;
    }

    
    public final void setIncomeAfterTaxes(final int newIncomeAfterTaxes) {
        this.incomeAfterTaxes = newIncomeAfterTaxes;
    }

    
    public final int getOldPrice() {
        return oldPrice;
    }

    
    public void setOldPrice(int oldPrice) {
        this.oldPrice = oldPrice;
    }

    
    public final boolean getTraded() {
        return traded;
    }

    
    public void setTraded(boolean traded) {
        this.traded = traded;
    }

    
    public final GoodsType getGoodsType() {
        return Specification.getSpecification().getGoodsType(getId());
    }

    
    protected void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        
        out.writeStartElement(getXMLElementTagName());

        out.writeAttribute("ID", getId());
        out.writeAttribute("amount", Integer.toString(amountInMarket));
        out.writeAttribute("initialPrice", Integer.toString(initialPrice));
        out.writeAttribute("arrears", Integer.toString(arrears));
        out.writeAttribute("sales", Integer.toString(sales));
        out.writeAttribute("incomeBeforeTaxes", Integer.toString(incomeBeforeTaxes));
        out.writeAttribute("incomeAfterTaxes", Integer.toString(incomeAfterTaxes));
        out.writeAttribute("traded", Boolean.toString(traded));
        out.writeEndElement();
    }

    
    protected void readFromXMLImpl(XMLStreamReader in) throws XMLStreamException {
        setId(in.getAttributeValue(null, "ID"));
        amountInMarket = Integer.parseInt(in.getAttributeValue(null, "amount"));
        initialPrice = getAttribute(in, "initialPrice", -1);
        
        if (initialPrice < 0) {
            initialPrice = getGoodsType().getInitialSellPrice();
        }
        arrears = Integer.parseInt(in.getAttributeValue(null, "arrears"));
        sales = Integer.parseInt(in.getAttributeValue(null, "sales"));
        incomeBeforeTaxes = Integer.parseInt(in.getAttributeValue(null, "incomeBeforeTaxes"));
        incomeAfterTaxes = Integer.parseInt(in.getAttributeValue(null, "incomeAfterTaxes"));
        traded = getAttribute(in, "traded", sales != 0);
        in.nextTag();
    }

    
    public static String getXMLElementTagName() {
        return "marketData";
    }

} 
