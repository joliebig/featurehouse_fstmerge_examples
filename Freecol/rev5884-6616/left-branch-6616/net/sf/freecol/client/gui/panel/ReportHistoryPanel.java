

package net.sf.freecol.client.gui.panel;

import java.util.List;

import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.HistoryEvent;
import net.sf.freecol.common.model.Turn;

import net.miginfocom.swing.MigLayout;


public final class ReportHistoryPanel extends ReportPanel {

    
    public ReportHistoryPanel(Canvas parent) {

        super(parent, Messages.message("menuBar.report.history"));

        List<HistoryEvent> history = getMyPlayer().getHistory();

        
        reportPanel.removeAll();
        if (history.size() == 0) {
            return;
        }

        reportPanel.setLayout(new MigLayout("wrap 2", "[]20[fill]", ""));

        for (HistoryEvent event : history) {
            reportPanel.add(new JLabel(Turn.toString(event.getTurn())));
            reportPanel.add(getDefaultTextArea(event.toString(), 40));
        }
    }
}
