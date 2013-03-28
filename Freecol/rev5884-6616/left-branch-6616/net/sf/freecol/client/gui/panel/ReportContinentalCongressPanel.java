

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.FoundingFather;
import net.sf.freecol.common.model.BuildingType;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.TypeCountMap;

import net.miginfocom.swing.MigLayout;



public final class ReportContinentalCongressPanel extends ReportPanel {

    static final String title = Messages.message("report.continentalCongress.title");

    static final String none = Messages.message("report.continentalCongress.none");

    private final static GoodsType goodsType = Goods.BELLS;

    
    public ReportContinentalCongressPanel(Canvas parent) {
        super(parent, title);

        reportPanel.setLayout(new MigLayout("fill, wrap 3", "", ""));

        Player player = getMyPlayer();

        JLabel recruiting = new JLabel(Messages.message("report.continentalCongress.recruiting"));
        recruiting.setFont(smallHeaderFont);
        reportPanel.add(recruiting, "align center, growy");
        if (player.getCurrentFather() == null) {
            reportPanel.add(new JLabel(none), "wrap 20");
        } else {
            FoundingFather father = player.getCurrentFather();
            JLabel currentFatherLabel = new JLabel(Messages.getName(father),
                                                   new ImageIcon(getLibrary().getFoundingFatherImage(father)),
                                                   JLabel.CENTER);
            currentFatherLabel.setToolTipText(Messages.getDescription(father));
            currentFatherLabel.setVerticalTextPosition(JLabel.TOP);
            currentFatherLabel.setHorizontalTextPosition(JLabel.CENTER);
            reportPanel.add(currentFatherLabel);
            FreeColProgressBar progressBar = new FreeColProgressBar(getCanvas(), goodsType);
            int total = 0;
            for (Colony colony : player.getColonies()) {
                total += colony.getProductionNetOf(goodsType);
            }
            int bells = player.getLiberty();
            int required = player.getTotalFoundingFatherCost();
            progressBar.update(0, required, bells, total);
            reportPanel.add(progressBar, "wrap 20");
        }

        
        if (player.getFatherCount() > 0) {
            for (FoundingFather father : FreeCol.getSpecification().getFoundingFathers()) {
                if (player.hasFather(father)) {
                    JLabel fatherLabel = new JLabel(Messages.getName(father),
                                                   new ImageIcon(getLibrary().getFoundingFatherImage(father)),
                                                   JLabel.CENTER);
                    fatherLabel.setVerticalTextPosition(JLabel.TOP);
                    fatherLabel.setHorizontalTextPosition(JLabel.CENTER);
                    fatherLabel.setToolTipText(Messages.getDescription(father));
                    reportPanel.add(fatherLabel);
                }
            }
        }
    }
}
