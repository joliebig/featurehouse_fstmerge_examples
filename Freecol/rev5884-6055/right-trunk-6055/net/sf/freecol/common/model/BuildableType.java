

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.util.Utils;


public abstract class BuildableType extends FreeColGameObjectType {

    public static final int UNDEFINED = Integer.MIN_VALUE;

    public static final String NOTHING = "model.buildableType.nothing";
    
    
    private int populationRequired = 1;

    
    private List<AbstractGoods> goodsRequired = new ArrayList<AbstractGoods>();
    
    private final HashMap<String, Boolean> requiredAbilities = new HashMap<String, Boolean>();

    public String getGoodsRequiredAsString() {
        if (goodsRequired == null || goodsRequired.isEmpty()) {
            return "";
        } else {
            ArrayList<String> result = new ArrayList<String>();
            for (AbstractGoods goods : goodsRequired) {
                result.add(Messages.message("model.goods.goodsAmount",
                                            "%amount%", String.valueOf(goods.getAmount()),
                                            "%goods%", goods.getType().getName()));
            }
            return Utils.join(", ", result);
        }
    }

    
    public final List<AbstractGoods> getGoodsRequired() {
        return goodsRequired;
    }

    
    public final int getAmountRequiredOf(GoodsType type){
    	for(AbstractGoods goods : this.goodsRequired){
    		if(goods.getType() == type){
    			return goods.getAmount();
    		}
    	}
    	return 0;
    }
    
    
    public final void setGoodsRequired(final List<AbstractGoods> newGoodsRequired) {
        this.goodsRequired = newGoodsRequired;
    }

    
    public int getPopulationRequired() {
        return populationRequired;
    }

    
    public void setPopulationRequired(final int newPopulationRequired) {
        this.populationRequired = newPopulationRequired;
    }

    
    public Map<String, Boolean> getAbilitiesRequired() {
        return requiredAbilities;
    }

    protected void readAttributes(XMLStreamReader in, Specification specification) throws XMLStreamException {
        super.readFromXML(in, specification);
    }

    protected FreeColObject readChild(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        String childName = in.getLocalName();
        if ("required-ability".equals(childName)) {
            String abilityId = in.getAttributeValue(null, "id");
            boolean value = getAttribute(in, "value", true);
            getAbilitiesRequired().put(abilityId, value);
            specification.addAbility(abilityId);
            in.nextTag(); 
            return new Ability(abilityId, value);
        } else if ("required-goods".equals(childName)) {
            GoodsType type = specification.getGoodsType(in.getAttributeValue(null, "id"));
            int amount = getAttribute(in, "value", 0);
            AbstractGoods requiredGoods = new AbstractGoods(type, amount);
            if (amount > 0) {
                type.setBuildingMaterial(true);
                if (getGoodsRequired() == null) {
                    setGoodsRequired(new ArrayList<AbstractGoods>());
                }
                getGoodsRequired().add(requiredGoods);
            }
            in.nextTag(); 
            return requiredGoods;
        } else {
            return super.readChild(in, specification);
        }
    }

}
