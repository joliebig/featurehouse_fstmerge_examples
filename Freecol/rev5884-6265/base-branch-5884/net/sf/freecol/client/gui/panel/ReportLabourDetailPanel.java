

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;



public final class ReportLabourDetailPanel extends ReportPanel implements ActionListener {
    
    
    public ReportLabourDetailPanel(Canvas parent) {
        super(parent, Messages.message("report.labour.details"));
    }

    
    public void setDetailPanel(JPanel detailPanel) {
        reportPanel.add(detailPanel);
    }

}
