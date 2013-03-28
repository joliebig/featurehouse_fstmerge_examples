


package net.sf.freecol.server.ai;

import java.util.logging.Logger;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.TileImprovement;
import net.sf.freecol.common.model.TileImprovementType;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.WorkLocation;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class WorkLocationPlan extends ValuedAIObject {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(WorkLocationPlan.class.getName());

    
    private WorkLocation workLocation;
    private int priority;
    private GoodsType goodsType;


    
    public WorkLocationPlan(AIMain aiMain, WorkLocation workLocation, GoodsType goodsType) {
        super(aiMain);
        this.workLocation = workLocation;
        this.goodsType = goodsType;
        setValue(getProductionOf(goodsType));
    }


    
    public WorkLocationPlan(AIMain aiMain, Element element) {
        super(aiMain);
        readFromXMLElement(element);
        setValue(getProductionOf(goodsType));
    }


    
    public TileImprovementPlan createTileImprovementPlan() {   
        return updateTileImprovementPlan(null);
    }
        
    
    public TileImprovementPlan updateTileImprovementPlan(TileImprovementPlan tip) {
        if (workLocation instanceof ColonyTile) {
            Tile tile = ((ColonyTile) workLocation).getWorkTile();
            
            if (tip != null && tip.getTarget() != tile) {
                throw new IllegalArgumentException("The given TileImprovementPlan was not created for this Tile.");
            }
            
            
            TileImprovementType impType = TileImprovement.findBestTileImprovementType(tile, goodsType);
            if (impType != null) {
                int value = impType.getValue(tile.getType(), goodsType);
                if (tip == null) {
                    return new TileImprovementPlan(getAIMain(), tile, impType, value);
                } else {
                    tip.setType(impType);
                    tip.setValue(value);
                    return tip;
                }
            }
        }
        return null;
    }

    
    public WorkLocation getWorkLocation() {
        return workLocation;
    }

    
    
    public int getProductionOf(GoodsType goodsType) {
        if (goodsType == null || goodsType != this.goodsType) {
            return 0;
        }
        
        if (workLocation instanceof ColonyTile) {
            if (!goodsType.isFarmed()) {
                return 0;
            }

            ColonyTile ct = (ColonyTile) workLocation;
            Tile t = ct.getWorkTile();
            UnitType expertUnitType = FreeCol.getSpecification().getExpertForProducing(goodsType);

            int base = t.getMaximumPotential(goodsType, expertUnitType);

            if (t.isLand() && base != 0) {
                base++;
            }
            
            return expertUnitType.getProductionFor(goodsType, base);
        } else {
            if (goodsType.isFarmed()) {
                return 0;
            } else {
                

                if (goodsType == Specification.getSpecification().getGoodsType("model.goods.hammers")) {
                    return 16;
                } else if (goodsType == Specification.getSpecification().getGoodsType("model.goods.bells")) {
                    return 12;
                } else if (goodsType == Specification.getSpecification().getGoodsType("model.goods.crosses")) {
                    return 10;
                } else {
                    return workLocation.getColony().getOwner().getMarket().getSalePrice(goodsType, 1);
                }
            }
        }
    }

    
    public GoodsType getGoodsType() {
        return goodsType;
    }
    
    
    
    public void setGoodsType(GoodsType goodsType) {
        this.goodsType = goodsType;
        setValue(getProductionOf(goodsType));
    }

    
        
    public Element toXMLElement(Document document) {
        Element element = document.createElement(getXMLElementTagName());

        element.setAttribute("ID", workLocation.getId());
        element.setAttribute("priority", Integer.toString(priority));
        element.setAttribute("goodsType", goodsType.getId());

        return element;
    }


        
    public void readFromXMLElement(Element element) {
        workLocation = (WorkLocation) getAIMain().getFreeColGameObject(element.getAttribute("ID"));
        priority = Integer.parseInt(element.getAttribute("priority"));
        goodsType = FreeCol.getSpecification().getGoodsType(element.getAttribute("goodsType"));
    }


    
    public static String getXMLElementTagName() {
        return "workLocationPlan";
    }
}
