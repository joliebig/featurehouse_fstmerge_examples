

package org.jfree.chart.editor;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jfree.chart.axis.ColorBar;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.GreyPalette;
import org.jfree.chart.plot.RainbowPalette;
import org.jfree.chart.util.ResourceBundleWrapper;
import org.jfree.layout.LCBLayout;


class DefaultColorBarEditor extends DefaultNumberAxisEditor {

    
    private JCheckBox invertPaletteCheckBox;

    
    private boolean invertPalette = false;

    
    private JCheckBox stepPaletteCheckBox;

    
    private boolean stepPalette = false;

    
    private PaletteSample currentPalette;

    
    private PaletteSample[] availablePaletteSamples;

    
   protected  static ResourceBundle localizationResources
           = ResourceBundleWrapper.getBundle(
                   "org.jfree.chart.editor.LocalizationBundle");

    
    public DefaultColorBarEditor(ColorBar colorBar) {
        super((NumberAxis) colorBar.getAxis());
        this.invertPalette = colorBar.getColorPalette().isInverse();
        this.stepPalette = colorBar.getColorPalette().isStepped();
        this.currentPalette = new PaletteSample(colorBar.getColorPalette());
        this.availablePaletteSamples = new PaletteSample[2];
        this.availablePaletteSamples[0]
            = new PaletteSample(new RainbowPalette());
        this.availablePaletteSamples[1]
            = new PaletteSample(new GreyPalette());

        JTabbedPane other = getOtherTabs();

        JPanel palettePanel = new JPanel(new LCBLayout(4));
        palettePanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        palettePanel.add(new JPanel());
        this.invertPaletteCheckBox = new JCheckBox(
            localizationResources.getString("Invert_Palette"),
            this.invertPalette
        );
        this.invertPaletteCheckBox.setActionCommand("invertPalette");
        this.invertPaletteCheckBox.addActionListener(this);
        palettePanel.add(this.invertPaletteCheckBox);
        palettePanel.add(new JPanel());

        palettePanel.add(new JPanel());
        this.stepPaletteCheckBox = new JCheckBox(
            localizationResources.getString("Step_Palette"),
            this.stepPalette
        );
        this.stepPaletteCheckBox.setActionCommand("stepPalette");
        this.stepPaletteCheckBox.addActionListener(this);
        palettePanel.add(this.stepPaletteCheckBox);
        palettePanel.add(new JPanel());

        palettePanel.add(
            new JLabel(localizationResources.getString("Palette"))
        );
        JButton button
            = new JButton(localizationResources.getString("Set_palette..."));
        button.setActionCommand("PaletteChoice");
        button.addActionListener(this);
        palettePanel.add(this.currentPalette);
        palettePanel.add(button);

        other.add(localizationResources.getString("Palette"), palettePanel);

    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals("PaletteChoice")) {
            attemptPaletteSelection();
        }
        else if (command.equals("invertPalette")) {
            this.invertPalette = this.invertPaletteCheckBox.isSelected();
        }
        else if (command.equals("stepPalette")) {
            this.stepPalette = this.stepPaletteCheckBox.isSelected();
        }
        else {
            super.actionPerformed(event);  
        }
    }

    
    private void attemptPaletteSelection() {
        PaletteChooserPanel panel
            = new PaletteChooserPanel(null, this.availablePaletteSamples);
        int result = JOptionPane.showConfirmDialog(
            this, panel, localizationResources.getString("Palette_Selection"),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            double zmin = this.currentPalette.getPalette().getMinZ();
            double zmax = this.currentPalette.getPalette().getMaxZ();
            this.currentPalette.setPalette(panel.getSelectedPalette());
            this.currentPalette.getPalette().setMinZ(zmin);
            this.currentPalette.getPalette().setMaxZ(zmax);
        }
    }

    
    public void setAxisProperties(ColorBar colorBar) {
        super.setAxisProperties(colorBar.getAxis());
        colorBar.setColorPalette(this.currentPalette.getPalette());
        colorBar.getColorPalette().setInverse(this.invertPalette); 
        colorBar.getColorPalette().setStepped(this.stepPalette); 
    }

    
    public static DefaultColorBarEditor getInstance(ColorBar colorBar) {

        if (colorBar != null) {
            return new DefaultColorBarEditor(colorBar);
        }
        else {
            return null;
        }

    }

}
