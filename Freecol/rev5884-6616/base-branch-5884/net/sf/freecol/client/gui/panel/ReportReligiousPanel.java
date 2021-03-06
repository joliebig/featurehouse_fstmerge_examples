

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionListener;

import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Player;

import net.miginfocom.swing.MigLayout;


public final class ReportReligiousPanel extends ReportPanel implements ActionListener {


    
    public ReportReligiousPanel(Canvas parent) {
        super(parent, Messages.message("menuBar.report.religion"));

        reportPanel.setLayout(new MigLayout("wrap 5, gap 20 20", "", ""));
        Player player = getMyPlayer();

        reportPanel.add(new JLabel(Messages.message("crosses")));
        FreeColProgressBar progressBar = new FreeColProgressBar(getCanvas(), Goods.CROSSES);
        reportPanel.add(progressBar, "span");

        List<Colony> colonies = player.getColonies();
        Collections.sort(colonies, getClient().getClientOptions().getColonyComparator());

        int production = 0;
        for (Colony colony : colonies) {
            reportPanel.add(createColonyButton(colony), "split 2, flowy, align center");
            reportPanel.add(new BuildingPanel(colony.getBuildingForProducing(Goods.CROSSES), getCanvas()));
            production += colony.getProductionOf(Goods.CROSSES);
        }

        progressBar.update(0, player.getImmigrationRequired(), player.getImmigration(), production);

    }


    private JButton createColonyButton(Colony colony) {
        JButton button = FreeColPanel.getLinkButton(colony.getName(), null, colony.getId());
        button.addActionListener(this);
        return button;
    }


}

