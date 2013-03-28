

package net.sf.freecol.server.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Market;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.NoRouteToServerException;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIMain;
import net.sf.freecol.server.generator.IMapGenerator;
import net.sf.freecol.server.model.ServerPlayer;

import org.w3c.dom.Element;



public final class PreGameController extends Controller {

    private static final Logger logger = Logger.getLogger(PreGameController.class.getName());

    
    public PreGameController(FreeColServer freeColServer) {
        super(freeColServer);
    }

    
    public void startGame() throws FreeColException{
        FreeColServer freeColServer = getFreeColServer();

        Game game = freeColServer.getGame();
        
        Specification.getSpecification().applyDifficultyLevel(game.getGameOptions().getInteger(GameOptions.DIFFICULTY));
        
        IMapGenerator mapGenerator = freeColServer.getMapGenerator();
        AIMain aiMain = new AIMain(freeColServer);
        freeColServer.setAIMain(aiMain);
        game.setFreeColGameObjectListener(aiMain);

        
        game.setUnknownEnemy(new Player(game, Player.UNKNOWN_ENEMY, false, null));

        for (Entry<Nation, NationState> entry : game.getNationOptions().getNations().entrySet()) {
            if (entry.getValue() != NationState.NOT_AVAILABLE &&
                game.getPlayer(entry.getKey().getId()) == null) {
                freeColServer.addAIPlayer(entry.getKey());
            }
        }
        Collections.sort(game.getPlayers(), Player.playerComparator);
        
        
        
        Element oldGameOptions = game.getGameOptions().toXMLElement(Message.createNewRootElement("oldGameOptions").getOwnerDocument());
        
        
        mapGenerator.createMap(game);
        Map map = game.getMap();
        
        
        game.getGameOptions().readFromXMLElement(oldGameOptions);
        
        
        sendUpdatedGame();        
        
        
        freeColServer.setGameState(FreeColServer.GameState.IN_GAME);
        try {
            freeColServer.updateMetaServer();
        } catch (NoRouteToServerException e) {}
        
        Element startGameElement = Message.createNewRootElement("startGame");
        freeColServer.getServer().sendToAll(startGameElement);
        freeColServer.getServer().setMessageHandlerToAllConnections(freeColServer.getInGameInputHandler());
    }
    
    
    public void sendUpdatedGame() {
        Game game = getFreeColServer().getGame();

        Iterator<Player> playerIterator = game.getPlayerIterator();
        while (playerIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer) playerIterator.next();
            
            if (player.isEuropean() && !player.isREF()) {
                player.modifyGold(game.getGameOptions().getInteger(GameOptions.STARTING_MONEY));

                
                
                
                Europe europe = player.getEurope();
                for (int index = 0; index < Europe.RECRUIT_COUNT; index++) {
                    String optionId = "model.option.recruitable.slot" + index;
                    if (Specification.getSpecification().hasOption(optionId)) {
                        String unitTypeId = Specification.getSpecification()
                            .getStringOption(optionId).getValue();
                        europe.setRecruitable(index, Specification.getSpecification().getUnitType(unitTypeId));
                    } else {
                        europe.setRecruitable(index, player.generateRecruitable(player.getId() + "slot." + Integer.toString(index+1)));
                    }
                }

                Market market = player.getMarket();
                for (GoodsType goodsType : FreeCol.getSpecification().getGoodsTypeList()) {
                    if (goodsType.isNewWorldGoodsType() || goodsType.isNewWorldLuxuryType()) {
                        int increase = getPseudoRandom().nextInt(3);
                        if (increase > 0) {
                            int newPrice = goodsType.getInitialSellPrice() + increase;
                            market.getMarketData(goodsType).setInitialPrice(newPrice);
                        }
                    }
                }
            }
            if (player.isAI()) {
                continue;
            }

            try {
                XMLStreamWriter out = player.getConnection().send();
                out.writeStartElement("updateGame");
                game.toXML(out, player);
                out.writeEndElement();
                player.getConnection().endTransmission(null);
            } catch (Exception e) {
                logger.log(Level.SEVERE, "EXCEPTION: ", e);
            }
        }
    }

}
