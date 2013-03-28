

package net.sf.freecol.client.gui.action;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.panel.FreeColDialog;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Map.Position;


public class ScaleMapAction extends FreeColAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ScaleMapAction.class.getName());




    public static final String id = "scaleMapAction";


    
    ScaleMapAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.tools.scaleMap", null, 0, null);
    }

    
    public String getId() {
        return id;
    }
    
    
    @Override
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled()
                && freeColClient.isMapEditor()
                && freeColClient.getGame() != null
                && freeColClient.getGame().getMap() != null; 
    }
    
    
    public void actionPerformed(ActionEvent e) {
        final Game game = freeColClient.getGame();
        final Map oldMap = game.getMap();

        final int oldWidth = oldMap.getWidth();
        final int oldHeight = oldMap.getHeight();

        MapSize ms = showMapSizeDialog();
        if (ms != null) {
            scaleMapTo(ms.width, ms.height);
        }
    }
    
    
    private MapSize showMapSizeDialog() {
        
        final int COLUMNS = 5;

        final Game game = freeColClient.getGame();
        final Map oldMap = game.getMap();
        
        final Canvas canvas = getFreeColClient().getCanvas();
        final String okText = Messages.message("ok");
        final String cancelText = Messages.message("cancel");
        final String widthText = Messages.message("width");
        final String heightText = Messages.message("height");
        
        final JTextField inputWidth = new JTextField(Integer.toString(oldMap.getWidth()), COLUMNS);
        final JTextField inputHeight = new JTextField(Integer.toString(oldMap.getHeight()), COLUMNS);

        final FreeColDialog<MapSize> inputDialog = new FreeColDialog<MapSize>(canvas) {
            public void requestFocus() {
                inputWidth.requestFocus();
            }
        };

        inputDialog.setLayout(new BoxLayout(inputDialog, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        final ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    int width = Integer.parseInt(inputWidth.getText());
                    int height = Integer.parseInt(inputHeight.getText());
                    if (width <= 0 || height <= 0) {
                        throw new NumberFormatException();
                    }
                    inputDialog.setResponse(new MapSize(width, height));
                } catch (NumberFormatException nfe) {
                    canvas.errorMessage("integerAboveZero");
                }
            }
        };
        JButton okButton = new JButton(okText);
        buttons.add(okButton);
        
        JButton cancelButton = new JButton(cancelText);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                inputDialog.setResponse(null);
            }
        });
        buttons.add(cancelButton);
        inputDialog.setCancelComponent(cancelButton);
        
        okButton.addActionListener(al);
        inputWidth.addActionListener(al);
        inputHeight.addActionListener(al);
        
        JLabel widthLabel = new JLabel(widthText);
        widthLabel.setLabelFor(inputWidth);
        JLabel heightLabel = new JLabel(heightText);
        heightLabel.setLabelFor(inputHeight);
        
        JPanel widthPanel = new JPanel(new FlowLayout());
        widthPanel.setOpaque(false);
        widthPanel.add(widthLabel);
        widthPanel.add(inputWidth);
        JPanel heightPanel = new JPanel(new FlowLayout());
        heightPanel.setOpaque(false);
        heightPanel.add(heightLabel);
        heightPanel.add(inputHeight);       
        
        inputDialog.add(widthPanel);
        inputDialog.add(heightPanel);
        inputDialog.add(buttons);

        inputDialog.setSize(inputDialog.getPreferredSize());

        return canvas.showFreeColDialog(inputDialog);
    }
    
    private class MapSize {
        int width;
        int height;
        
        MapSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
    
    
    private void scaleMapTo(final int width, final int height) {
        
        
        final Game game = freeColClient.getGame();
        final Map oldMap = game.getMap();

        final int oldWidth = oldMap.getWidth();
        final int oldHeight = oldMap.getHeight();
        
        Tile[][] tiles = new Tile[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                final int oldX = (x * oldWidth) / width;
                final int oldY = (y * oldHeight) / height;
                
                Tile importTile = oldMap.getTile(oldX, oldY);
                Tile t = new Tile(game, importTile.getType(), x, y);
                if (t.getTileItemContainer() != null) {
                    t.getTileItemContainer().copyFrom(importTile.getTileItemContainer());
                }
                tiles[x][y] = t;
            }
        }

        Map map = new Map(game, tiles);
        game.setMap(map);
        
        
        
        freeColClient.getGUI().setSelectedTile(new Position(0, 0));
        freeColClient.getCanvas().refresh();
    }
}
