

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Region;

import net.miginfocom.swing.MigLayout;


public final class ReportExplorationPanel extends ReportPanel {

    
    private static final Comparator<Region> regionComparator = new Comparator<Region>() {
        public int compare(Region region1, Region region2) {
            int number1 = region1.getDiscoveredIn().getNumber();
            int number2 = region2.getDiscoveredIn().getNumber();
            if (number1 == number2) {
                return region2.getScoreValue() - region1.getScoreValue();
            } else {
                return number2 - number1;
            }
        }
    };

    
    public ReportExplorationPanel(Canvas parent) {
        super(parent, Messages.message("menuBar.report.exploration"));

        
        reportPanel.removeAll();

        List<Region> regions = new ArrayList<Region>();
        for (Region region : getGame().getMap().getRegions()) {
            if (region.getDiscoveredIn() != null) {
                regions.add(region);
            }
        }
        Collections.sort(regions, regionComparator);

        reportPanel.setLayout(new MigLayout("wrap 5, fillx", "", ""));

        
        reportPanel.add(new JLabel(Messages.message("report.exploration.nameOfRegion")));
        reportPanel.add(new JLabel(Messages.message("report.exploration.typeOfRegion")));
        reportPanel.add(new JLabel(Messages.message("report.exploration.discoveredIn")));
        reportPanel.add(new JLabel(Messages.message("report.exploration.discoveredBy")));
        reportPanel.add(new JLabel(Messages.message("report.exploration.valueOfRegion")));

        for (Region region : regions) {
            reportPanel.add(new JLabel(region.getName()));
            reportPanel.add(new JLabel(region.getTypeName()));
            reportPanel.add(new JLabel(region.getDiscoveredIn().toString()));
            reportPanel.add(localizedLabel(region.getDiscoveredBy().getNationName()));
            reportPanel.add(new JLabel(String.valueOf(region.getScoreValue())));
        }
    }
}
