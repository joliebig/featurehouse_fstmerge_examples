


package net.sf.freecol.common;


import net.sf.freecol.FreeCol;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class ServerInfo {


    private String name;
    private String address;
    private int port;

    private int currentlyPlaying;
    private int slotsAvailable;
    private boolean isGameStarted;
    private String version;
    private int gameState;


    
    protected ServerInfo() {

    }


    
    public ServerInfo(String name, String address, int port, int slotsAvailable, int currentlyPlaying, boolean isGameStarted, String version, int gameState) {
        update(name, address, port, slotsAvailable, currentlyPlaying, isGameStarted, version, gameState);
    }

    
    
    public ServerInfo(Element element) {
        readFromXMLElement(element);
    }


    
    public void update(String name, String address, int port, int slotsAvailable, 
                       int currentlyPlaying, boolean isGameStarted, String version, int gameState) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.slotsAvailable = slotsAvailable;
        this.currentlyPlaying = currentlyPlaying;
        this.isGameStarted = isGameStarted;
        this.version = version;
        this.gameState = gameState;
    }

    
    
    public String getName() {
        return name;
    }

    
    public String getAddress() {
        return address;
    }


    
    public int getPort() {
        return port;
    }
    
    
    
    public int getCurrentlyPlaying() {
        return currentlyPlaying;
    }
    
    
    
    public int getSlotsAvailable() {
        return slotsAvailable;
    }

    
    
    public String getVersion() {
        return version;
    }


    
    public int getGameState() {
        return gameState;
    }


    
    public Element toXMLElement(Document document) {
        Element element = document.createElement(getXMLElementTagName());

        element.setAttribute("name", name);
        element.setAttribute("address", address);
        element.setAttribute("port", Integer.toString(port));
        element.setAttribute("slotsAvailable", Integer.toString(slotsAvailable));
        element.setAttribute("currentlyPlaying", Integer.toString(currentlyPlaying));
        element.setAttribute("isGameStarted", Boolean.toString(isGameStarted));
        element.setAttribute("version", version);
        element.setAttribute("gameState", Integer.toString(gameState));
        
        return element;
    }


    
    public void readFromXMLElement(Element element) {
        update(element.getAttribute("name"), element.getAttribute("address"),
                Integer.parseInt(element.getAttribute("port")),
                Integer.parseInt(element.getAttribute("slotsAvailable")),
                Integer.parseInt(element.getAttribute("currentlyPlaying")),
                Boolean.valueOf(element.getAttribute("slotsAvailable")).booleanValue(),
                element.getAttribute("version"),
                Integer.parseInt(element.getAttribute("gameState")));
    }


    
    public static String getXMLElementTagName() {
        return "serverInfo";
    }


    
    public String toString() {
        return name + "(" + address + ":" + port + ") " + currentlyPlaying 
                + ", " + slotsAvailable + ", " + isGameStarted + ", " + version
                + ", " + gameState;
    }

}
