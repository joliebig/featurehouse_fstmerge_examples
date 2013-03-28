


package net.sf.freecol.client.control;

import java.awt.Color;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.GameOptions;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.NationOptions.NationState;
import net.sf.freecol.common.model.NationType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.networking.Connection;
import net.sf.freecol.common.networking.Message;
import net.sf.freecol.common.networking.ChatMessage;
import net.sf.freecol.common.networking.StreamedMessageHandler;
import net.sf.freecol.server.generator.MapGeneratorOptions;

import org.w3c.dom.Element;



public final class PreGameInputHandler extends InputHandler implements StreamedMessageHandler {
    private static final Logger logger = Logger.getLogger(PreGameInputHandler.class.getName());



    
    public PreGameInputHandler(FreeColClient freeColClient) {
        super(freeColClient);
    }





    
    public synchronized Element handle(Connection connection, Element element) {
        Element reply = null;

        if (element != null) {

            String type = element.getTagName();

            if (type.equals("addPlayer")) {
                reply = addPlayer(element);
            } else if (type.equals("removePlayer")) {
                reply = removePlayer(element);
            } else if (type.equals("updateGameOptions")) {
                reply = updateGameOptions(element);
            } else if (type.equals("updateMapGeneratorOptions")) {
                reply = updateMapGeneratorOptions(element);
            } else if (type.equals("chat")) {
                reply = chat(element);
            } else if (type.equals("playerReady")) {
                reply = playerReady(element);
            } else if (type.equals("updateNation")) {
                reply = updateNation(element);
            } else if (type.equals("updateNationType")) {
                reply = updateNationType(element);
            } else if (type.equals("updateColor")) {
                reply = updateColor(element);
            } else if (type.equals("setAvailable")) {
                reply = setAvailable(element);
            } else if (type.equals("startGame")) {
                reply = startGame(element);
            } else if (type.equals("logout")) {
                reply = logout(element);
            } else if (type.equals("disconnect")) {
                reply = disconnect(element);
            } else if (type.equals("error")) {
                reply = error(element);
            } else {
                logger.warning("Message is of unsupported type \"" + type + "\".");
            }
        }

        return reply;
    }

    
    public void handle(Connection connection, XMLStreamReader in, XMLStreamWriter out) {
        if (in.getLocalName().equals("updateGame")) {
            updateGame(connection, in, out);
        } else {
            logger.warning("Unkown (streamed) request: " + in.getLocalName());
        }
    }
    
    
    public boolean accepts(String tagName) {
        return tagName.equals("updateGame");
    }

    
    private Element addPlayer(Element element) {
        Game game = getFreeColClient().getGame();

        Element playerElement = (Element) element.getElementsByTagName(Player.getXMLElementTagName()).item(0);
        if (game.getFreeColGameObject(playerElement.getAttribute("ID")) == null) {
           Player newPlayer = new Player(game, playerElement);
           getFreeColClient().getGame().addPlayer(newPlayer);
        } else {
           game.getFreeColGameObject(playerElement.getAttribute("ID")).readFromXMLElement(playerElement);
        }
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element removePlayer(Element element) {
        Game game = getFreeColClient().getGame();

        Element playerElement = (Element) element.getElementsByTagName(Player.getXMLElementTagName()).item(0);
        Player player = new Player(game, playerElement);

        getFreeColClient().getGame().removePlayer(player);
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }

    
    private Element updateGameOptions(Element element) {
        Game game = getFreeColClient().getGame();

        Element mgoElement = (Element) element.getElementsByTagName(GameOptions.getXMLElementTagName()).item(0);
        game.getGameOptions().readFromXMLElement(mgoElement);

        getFreeColClient().getCanvas().getStartGamePanel().updateGameOptions();

        return null;
    }
    
    
    private Element updateMapGeneratorOptions(Element element) {
        Element mgoElement = (Element) element.getElementsByTagName(MapGeneratorOptions.getXMLElementTagName()).item(0);
        getFreeColClient().getPreGameController().getMapGeneratorOptions().readFromXMLElement(mgoElement);

        getFreeColClient().getCanvas().getStartGamePanel().updateMapGeneratorOptions();

        return null;
    }
    
    
    private Element chat(Element element)  {
        ChatMessage chatMessage = new ChatMessage(getGame(), element);
        Canvas canvas = getFreeColClient().getCanvas();
        canvas.getStartGamePanel().displayChat(chatMessage.getPlayer().getName(),
                                               chatMessage.getMessage(),
                                               chatMessage.isPrivate());
        return null;
    }


    
    private Element playerReady(Element element) {
        Game game = getFreeColClient().getGame();

        Player player = (Player) game.getFreeColGameObject(element.getAttribute("player"));
        boolean ready = Boolean.valueOf(element.getAttribute("value")).booleanValue();

        player.setReady(ready);
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element updateNation(Element element) {
        Game game = getFreeColClient().getGame();

        Player player = (Player) game.getFreeColGameObject(element.getAttribute("player"));
        Nation nation = FreeCol.getSpecification().getNation(element.getAttribute("value"));

        player.setNation(nation);
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element updateNationType(Element element) {
        Game game = getFreeColClient().getGame();

        Player player = (Player) game.getFreeColGameObject(element.getAttribute("player"));
        NationType nationType = FreeCol.getSpecification().getNationType(element.getAttribute("value"));

        player.setNationType(nationType);
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element updateColor(Element element) {
        Game game = getFreeColClient().getGame();

        Player player = (Player) game.getFreeColGameObject(element.getAttribute("player"));
        String color = element.getAttribute("value");

        player.setColor(new Color(Integer.decode(color)));

        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element setAvailable(Element element) {
        Nation nation = Specification.getSpecification().getNation(element.getAttribute("nation"));
        NationState state = Enum.valueOf(NationState.class, element.getAttribute("state"));
        getFreeColClient().getGame().getNationOptions().setNationState(nation, state);
        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private void updateGame(Connection connection, XMLStreamReader in, XMLStreamWriter out) {
        try {
            in.nextTag();
            getFreeColClient().getGame().readFromXML(in);
        } catch (XMLStreamException e) {
            logger.warning(e.toString());
        }
    }


    
    private Element startGame(Element element) {
        
        new Thread(FreeCol.CLIENT_THREAD+"Starting game") {
            public void run() {
                while (getFreeColClient().getGame().getMap() == null) {
                    try {
                        Thread.sleep(200);
                    } catch (Exception ex) {}
                }

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        getFreeColClient().getPreGameController().startGame();
                    }
                });
            }
        }.start();
        return null;
    }


    
    private Element logout(Element element) {
        Game game = getFreeColClient().getGame();

        String playerID = element.getAttribute("player");
        

        Player player = (Player) game.getFreeColGameObject(playerID);

        game.removePlayer(player);

        getFreeColClient().getCanvas().getStartGamePanel().refreshPlayersTable();

        return null;
    }


    
    private Element error(Element element)  {
        Canvas canvas = getFreeColClient().getCanvas();

        if (element.hasAttribute("messageID")) {
            canvas.errorMessage(element.getAttribute("messageID"), element.getAttribute("message"));
        } else {
            canvas.errorMessage(null, element.getAttribute("message"));
        }

        return null;
    }
}
