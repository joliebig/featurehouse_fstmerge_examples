

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
import net.sf.freecol.server.generator.TerrainGenerator;


public class DetermineHighSeasAction extends FreeColAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(DetermineHighSeasAction.class.getName());




    public static final String id = "determineHighSeasAction";


    
    DetermineHighSeasAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.tools.determineHighSeas", null, 0, null);
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
        final Map map = game.getMap();

        Parameters p = showParametersDialog();
        if (p != null) {
            TerrainGenerator.determineHighSeas(map, p.distToLandFromHighSeas, p.maxDistanceToEdge);
        }
    }
    
    
    private Parameters showParametersDialog() {
        
        final int COLUMNS = 5;
        final int DEFAULT_distToLandFromHighSeas = 4;
        final int DEFAULT_maxDistanceToEdge = 12;
        
        final Canvas canvas = getFreeColClient().getCanvas();
        final String okText = Messages.message("ok");
        final String cancelText = Messages.message("cancel");
        final String dText = Messages.message("menuBar.tools.determineHighSeas.distToLandFromHighSeas");
        final String mText = Messages.message("menuBar.tools.determineHighSeas.maxDistanceToEdge");
        
        final JTextField inputD = new JTextField(Integer.toString(DEFAULT_distToLandFromHighSeas), COLUMNS);
        final JTextField inputM = new JTextField(Integer.toString(DEFAULT_maxDistanceToEdge), COLUMNS);

        final FreeColDialog<Parameters> inputDialog = new FreeColDialog<Parameters>(canvas)  {
            public void requestFocus() {
                inputD.requestFocus();
            }
        };

        inputDialog.setLayout(new BoxLayout(inputDialog, BoxLayout.Y_AXIS));

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);

        final ActionListener al = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    int d = Integer.parseInt(inputD.getText());
                    int m = Integer.parseInt(inputM.getText());
                    if (d <= 0 || m <= 0) {
                        throw new NumberFormatException();
                    }
                    inputDialog.setResponse(new Parameters(d, m));
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
        inputD.addActionListener(al);
        inputM.addActionListener(al);
        
        JLabel widthLabel = new JLabel(dText);
        widthLabel.setLabelFor(inputD);
        JLabel heightLabel = new JLabel(mText);
        heightLabel.setLabelFor(inputM);
        
        JPanel widthPanel = new JPanel(new FlowLayout());
        widthPanel.setOpaque(false);
        widthPanel.add(widthLabel);
        widthPanel.add(inputD);
        JPanel heightPanel = new JPanel(new FlowLayout());
        heightPanel.setOpaque(false);
        heightPanel.add(heightLabel);
        heightPanel.add(inputM);       
        
        inputDialog.add(widthPanel);
        inputDialog.add(heightPanel);
        inputDialog.add(buttons);

        inputDialog.setSize(inputDialog.getPreferredSize());

        return canvas.showFreeColDialog(inputDialog);
    }
    
    private class Parameters {
        int distToLandFromHighSeas;
        int maxDistanceToEdge;
        
        Parameters(int distToLandFromHighSeas, int maxDistanceToEdge) {
            this.distToLandFromHighSeas = distToLandFromHighSeas;
            this.maxDistanceToEdge = maxDistanceToEdge;
        }
    }
    
}
