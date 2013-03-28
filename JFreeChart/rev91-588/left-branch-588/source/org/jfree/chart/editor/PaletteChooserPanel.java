

package org.jfree.chart.editor;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.jfree.chart.plot.ColorPalette;
import org.jfree.chart.plot.RainbowPalette;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBlockRenderer;


class PaletteChooserPanel extends JPanel {

    
    private JComboBox selector;

    
    public PaletteChooserPanel(PaletteSample current, 
                               PaletteSample[] available) {
        setLayout(new BorderLayout());
        this.selector = new JComboBox(available);
        this.selector.setSelectedItem(current);
        this.selector.setRenderer(new PaletteSample(new RainbowPalette()));
        add(this.selector);
    }

    
    public ColorPalette getSelectedPalette() {
        PaletteSample sample = (PaletteSample) this.selector.getSelectedItem();
        return sample.getPalette();
    }
}
