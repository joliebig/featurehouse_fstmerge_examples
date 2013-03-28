

package net.sf.freecol.common.networking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.IndianNationType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.model.Map.CircleIterator;
import net.sf.freecol.common.model.Map.Position;
import net.sf.freecol.common.util.RandomChoice;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;



public class BuildColonyMessage extends Message {

    
    String colonyName;

    
    String builderId;


    
    public BuildColonyMessage(String colonyName, Unit builder) {
        this.colonyName = colonyName;
        this.builderId = builder.getId();
    }

    
    public BuildColonyMessage(Game game, Element element) {
        this.colonyName = element.getAttribute("name");
        this.builderId = element.getAttribute("unit");
    }

    
    public Element handle(FreeColServer server, Player player, Connection connection) {
        Game game = player.getGame();
        ServerPlayer serverPlayer = server.getPlayer(connection);

        Unit unit;
        try {
            unit = server.getUnitSafely(builderId, serverPlayer);
        } catch (Exception e) {
            return Message.clientError(e.getMessage());
        }
        if (colonyName == null || colonyName.length() == 0) {
            return Message.createError("server.buildColony.badName",
                                       "Empty colony name");
        } else if (player.getColony(colonyName) != null) {
            return Message.createError("server.buildColony.badName",
                                       "Non-unique colony name " + colonyName);
        } else if (!unit.canBuildColony()) {
            return Message.createError("server.buildColony.badUnit",
                                       "Unit " + builderId
                                       + " can not build colony " + colonyName);
        }

        Tile tile = unit.getTile();
        if(tile.getOwner() != null && tile.getOwner() != player){
        	return Message.createError("server.buildColony.tileHasOwner",
        			"Tile " + tile + " belongs to someone else");
        }

        Settlement settlement = null;
        
        if (player.isEuropean()) {
            settlement = new Colony(game, serverPlayer, colonyName, tile);
            unit.buildColony((Colony)settlement);
        } else {
            settlement = new IndianSettlement(game, serverPlayer, tile, 
                                              colonyName, false,
                                              generateSkillForLocation(game.getMap(), tile, player.getNationType()),
                                              new HashSet<Player>(), null);
            unit.buildIndianSettlement((IndianSettlement)settlement);
        }
        HistoryEvent h = new HistoryEvent(game.getTurn().getNumber(),
                                          HistoryEvent.Type.FOUND_COLONY,
                                          "%colony%", settlement.getName());
        player.getHistory().add(h);
        server.getInGameController().sendUpdatedTileToAll(tile, serverPlayer);

        
        Element reply = Message.createNewRootElement("multiple");
        Document doc = reply.getOwnerDocument();
        Element update = doc.createElement("update");
        reply.appendChild(update);
        update.appendChild(tile.toXMLElement(player, doc));
        Map map = game.getMap();
        for (Tile t : map.getSurroundingTiles(tile, settlement.getRadius())) {
            if (t.getOwningSettlement() == settlement) {
                update.appendChild(t.toXMLElement(player, doc));
            }
        }
        
        
        
        
        
        for (int range = unit.getLineOfSight() + 1; 
             range <= settlement.getLineOfSight(); range++) {
            CircleIterator circle = map.getCircleIterator(tile.getPosition(),
                                                          false, range);
            while (circle.hasNext()) {
                update.appendChild(map.getTile(circle.next()).toXMLElement(player, doc));
            }
        }
        Element history = doc.createElement("addHistory");
        reply.appendChild(history);
        history.appendChild(h.toXMLElement(player, doc));
        return reply;
    }

    
    public Element toXMLElement() {
        Element result = createNewRootElement(getXMLElementTagName());
        result.setAttribute("name", colonyName);
        result.setAttribute("unit", builderId);
        return result;
    }

    
    public static String getXMLElementTagName() {
        return "buildColony";
    }
    
    
    private UnitType generateSkillForLocation(Map map, Tile tile, NationType nationType) {
        List<RandomChoice<UnitType>> skills = ((IndianNationType) nationType).getSkills();
        java.util.Map<GoodsType, Integer> scale = new HashMap<GoodsType, Integer>();
        Random random = new Random();
        
        for (RandomChoice<UnitType> skill : skills) {
            scale.put(skill.getObject().getExpertProduction(), 1);
        }

        Iterator<Position> iter = map.getAdjacentIterator(tile.getPosition());
        while (iter.hasNext()) {
            Map.Position p = iter.next();
            Tile t = map.getTile(p);
            for (GoodsType goodsType : scale.keySet()) {
                scale.put(goodsType, scale.get(goodsType).intValue() + t.potential(goodsType, null));
            }
        }

        List<RandomChoice<UnitType>> scaledSkills = new ArrayList<RandomChoice<UnitType>>();
        for (RandomChoice<UnitType> skill : skills) {
            UnitType unitType = skill.getObject();
            int scaleValue = scale.get(unitType.getExpertProduction()).intValue();
            scaledSkills.add(new RandomChoice<UnitType>(unitType, skill.getProbability() * scaleValue));
        }
        
        UnitType skill = RandomChoice.getWeightedRandom(random, scaledSkills);
        if (skill == null) {
            
            List<UnitType> unitList = FreeCol.getSpecification().getUnitTypesWithAbility("model.ability.expertScout");
            return unitList.get(random.nextInt(unitList.size()));
        } else {
            return skill;
        }
    }
}
