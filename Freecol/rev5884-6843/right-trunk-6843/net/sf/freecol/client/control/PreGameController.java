


package net.sf.freecol.client.control;


import java.awt.Color;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.CanvasMouseListener;
import net.sf.freecol.client.gui.CanvasMouseMotionListener;
import net.sf.freecol.client.gui.GUI;
import net.sf.freecol.client.gui.InGameMenuBar;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.sound.SoundPlayer;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.ModelMessage;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.ChatMessage;
import net.sf.freecol.common.resources.ChipResource;
import net.sf.freecol.common.resources.ColorResource;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.resources.ResourceMapping;
import net.sf.freecol.server.generator.MapGeneratorOptions;

import org.w3c.dom.Element;




public final class PreGameController {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(PreGameController.class.getName());


    private FreeColClient freeColClient;

    private MapGeneratorOptions mapGeneratorOptions = null;




    
    public PreGameController(FreeColClient freeColClient) {
        this.freeColClient = freeColClient;
    }



    
    void setMapGeneratorOptions(MapGeneratorOptions mapGeneratorOptions) {
        this.mapGeneratorOptions = mapGeneratorOptions; 
    }
    
    
    public MapGeneratorOptions getMapGeneratorOptions() {
        return mapGeneratorOptions; 
    }

    
    public void setReady(boolean ready) {
        
        freeColClient.getMyPlayer().setReady(ready);

        
        Element readyElement = Message.createNewRootElement("ready");
        readyElement.setAttribute("value", Boolean.toString(ready));

        freeColClient.getClient().send(readyElement);
    }


    
    public void setNation(Nation nation) {
        
        freeColClient.getMyPlayer().setNation(nation);

        
        Element nationElement = Message.createNewRootElement("setNation");
        nationElement.setAttribute("value", nation.getId());

        freeColClient.getClient().sendAndWait(nationElement);
    }


    
    public void setNationType(NationType nationType) {
        
        freeColClient.getMyPlayer().setNationType(nationType);

        
        Element nationTypeElement = Message.createNewRootElement("setNationType");
        nationTypeElement.setAttribute("value", nationType.getId());

        freeColClient.getClient().sendAndWait(nationTypeElement);
    }


    
    public void setColor(Color color) {
        
        freeColClient.getMyPlayer().setColor(color);

        
        Element colorElement = Message.createNewRootElement("setColor");
        
        
        
        colorElement.setAttribute("value", "#" + Integer.toHexString(color.getRGB()).substring(2));

        freeColClient.getClient().sendAndWait(colorElement);
    }

    public void setAvailable(Nation nation, NationState state) {
        freeColClient.getGame().getNationOptions().getNations().put(nation, state);
        Element availableElement = Message.createNewRootElement("setAvailable");
        availableElement.setAttribute("nation", nation.getId());
        availableElement.setAttribute("state", state.toString());
        freeColClient.getClient().sendAndWait(availableElement);
    }


    
    public void requestLaunch() {
        Canvas canvas = freeColClient.getCanvas();

        if (!freeColClient.getGame().isAllPlayersReadyToLaunch()) {
            canvas.errorMessage("server.notAllReady");
            return;
        }

        Element requestLaunchElement = Message.createNewRootElement("requestLaunch");
        freeColClient.getClient().send(requestLaunchElement);

        canvas.showStatusPanel( Messages.message("status.startingGame") );
    }


    
    public void chat(String message) {
        ChatMessage chatMessage = new ChatMessage(freeColClient.getMyPlayer(),
                                                  message,
                                                  Boolean.FALSE);
        freeColClient.getClient().send(chatMessage.toXMLElement());
    }
    

    
    public void sendGameOptions() {
        Element updateGameOptionsElement = Message.createNewRootElement("updateGameOptions");
        updateGameOptionsElement.appendChild(freeColClient.getGame().getGameOptions().toXMLElement(updateGameOptionsElement.getOwnerDocument()));

        freeColClient.getClient().send(updateGameOptionsElement);        
    }

    
     public void sendMapGeneratorOptions() {
         if (mapGeneratorOptions != null) {
             Element updateMapGeneratorOptionsElement = Message.createNewRootElement("updateMapGeneratorOptions");
             updateMapGeneratorOptionsElement
                 .appendChild(mapGeneratorOptions.toXMLElement(updateMapGeneratorOptionsElement.getOwnerDocument()));
             freeColClient.getGame().setMapGeneratorOptions(mapGeneratorOptions);
             freeColClient.getClient().send(updateMapGeneratorOptionsElement);
         }
     }    

    
    public void startGame() {
        Canvas canvas = freeColClient.getCanvas();
        GUI gui = freeColClient.getGUI();

        ResourceMapping gameMapping = new ResourceMapping();
        for (Player player : freeColClient.getGame().getPlayers()) {
            gameMapping.add(player.getNationID() + ".color",
                            new ColorResource(player.getColor()));
            gameMapping.add(player.getNationID() + ".chip",
                            ChipResource.colorChip(player.getColor()));
            gameMapping.add(player.getNationID() + ".mission.chip",
                            ChipResource.missionChip(player.getColor(), false));
            gameMapping.add(player.getNationID() + ".mission.expert.chip",
                            ChipResource.missionChip(player.getColor(), true));
            ResourceManager.setGameMapping(gameMapping);
        }

        if (!freeColClient.isHeadless()) {
            canvas.closeMainPanel();
            canvas.closeMenus();
            canvas.closeStatusPanel();
            
            
            freeColClient.playMusicOnce("england", SoundPlayer.STANDARD_DELAY);
        }

        InGameController inGameController = freeColClient.getInGameController();
        InGameInputHandler inGameInputHandler = freeColClient.getInGameInputHandler();

        freeColClient.getClient().setMessageHandler(inGameInputHandler);

        if (!freeColClient.isHeadless()) {
            gui.setInGame(true);
            freeColClient.getFrame().setJMenuBar(new InGameMenuBar(freeColClient));
        }

        Unit activeUnit = freeColClient.getMyPlayer().getNextActiveUnit();
        
        gui.setActiveUnit(activeUnit);
        if (activeUnit != null) {
            gui.setFocus(activeUnit.getTile().getPosition());
        } else {
            gui.setFocus(((Tile) freeColClient.getMyPlayer().getEntryLocation()).getPosition());
        }

        canvas.addMouseListener(new CanvasMouseListener(canvas, gui));
        canvas.addMouseMotionListener(new CanvasMouseMotionListener(canvas, gui, freeColClient.getGame().getMap()));
        
        if (freeColClient.getGame().getTurn().getNumber() == 1) {
            Player player = freeColClient.getMyPlayer();
            player.addModelMessage(new ModelMessage(ModelMessage.MessageType.TUTORIAL, 
                                                    "tutorial.startGame", player));
            
            inGameController.nextModelMessage();
        }
    }
}
