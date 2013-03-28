

package net.sf.freecol.client.gui.panel;

import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Player.Stance;

import org.w3c.dom.Element;

import net.miginfocom.swing.MigLayout;


public final class ReportForeignAffairPanel extends ReportPanel {


    
    public ReportForeignAffairPanel(Canvas parent) {

        super(parent, Messages.message("menuBar.report.foreign"));

        
        reportPanel.removeAll();
        reportPanel.setLayout(new GridLayout(0, 2));

        Element report = getController().getForeignAffairsReport();
        int number = report.getChildNodes().getLength();
        for (int i = 0; i < number; i++) {
            Element enemyElement = (Element) report.getChildNodes().item(i);
            JPanel enemyPanel = new JPanel(new MigLayout("gapy 0", "[][]20[align right]0[]", ""));
            enemyPanel.setOpaque(false);
            Player enemy = (Player) getGame().getFreeColGameObject(enemyElement.getAttribute("player"));
            JLabel coatLabel = new JLabel();
            final ImageIcon coatOfArms = getLibrary().getCoatOfArmsImageIcon(enemy.getNation());
            if (coatOfArms != null) {
                coatLabel.setIcon(coatOfArms);
            }
            enemyPanel.add(coatLabel, "spany, aligny top");
            enemyPanel.add(localizedLabel(enemy.getNationName()), "wrap 12");

            enemyPanel.add(new JLabel(Messages.message("report.stance")), "newline");
            Stance stance = Enum.valueOf(Stance.class, enemyElement.getAttribute("stance"));
            enemyPanel.add(new JLabel(Player.getStanceAsString(stance)));

            enemyPanel.add(new JLabel(Messages.message("report.numberOfColonies")), "newline");
            enemyPanel.add(new JLabel(enemyElement.getAttribute("numberOfColonies")));

            enemyPanel.add(new JLabel(Messages.message("report.numberOfUnits")), "newline");
            enemyPanel.add(new JLabel(enemyElement.getAttribute("numberOfUnits")));

            enemyPanel.add(new JLabel(Messages.message("report.militaryStrength")), "newline");
            enemyPanel.add(new JLabel(enemyElement.getAttribute("militaryStrength")));

            enemyPanel.add(new JLabel(Messages.message("report.navalStrength")), "newline");
            enemyPanel.add(new JLabel(enemyElement.getAttribute("navalStrength")));

            enemyPanel.add(new JLabel(Messages.message("goldTitle")), "newline");
            enemyPanel.add(new JLabel(enemyElement.getAttribute("gold")));

            if (enemyElement.hasAttribute("tax")) {
                enemyPanel.add(new JLabel(Messages.message("menuBar.colopedia.father")), "newline 8");
                enemyPanel.add(new JLabel(enemyElement.getAttribute("foundingFathers")));

                enemyPanel.add(new JLabel(Messages.message("tax")), "newline");
                enemyPanel.add(new JLabel(enemyElement.getAttribute("tax")));
                enemyPanel.add(new JLabel("%"));

                enemyPanel.add(new JLabel(Messages.message("report.sonsOfLiberty")), "newline");
                enemyPanel.add(new JLabel(enemyElement.getAttribute("SoL")));
                enemyPanel.add(new JLabel("%"));

            }
            reportPanel.add(enemyPanel);
        }

        reportPanel.doLayout();
    }

}
