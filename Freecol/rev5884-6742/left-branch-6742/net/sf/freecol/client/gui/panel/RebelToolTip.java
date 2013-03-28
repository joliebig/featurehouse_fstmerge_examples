

package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JToolTip;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.GoodsType;

import net.miginfocom.swing.MigLayout;


public class RebelToolTip extends JToolTip {

    
    public RebelToolTip(Colony colony, Canvas parent) {

        setLayout(new MigLayout("fillx, wrap 3", "[][right][right]", ""));

        int members = colony.getMembers();
        int rebels = colony.getSoL();

        add(new JLabel(Messages.message("colonyPanel.rebelLabel", "%number%", "")));
        add(new JLabel(Integer.toString(members)));
        add(new JLabel(Integer.toString(rebels) + "%"));
        add(new JLabel(Messages.message("colonyPanel.royalistLabel", "%number%", "")));
        add(new JLabel(Integer.toString(colony.getUnitCount() - members)));
        add(new JLabel(Integer.toString(colony.getTory()) + "%"));

        int libertyProduction = 0;
        for (GoodsType goodsType : Specification.getSpecification().getLibertyGoodsTypeList()) {
            add(new JLabel(Messages.getName(goodsType)));
            int netProduction = colony.getProductionNetOf(goodsType);
            libertyProduction += netProduction;
            add(new ProductionLabel(goodsType, netProduction, parent), "span 2");
        }

        float turns100 = 0;
        float turns50 = 0;
        float turnsNext = 0;

        if (libertyProduction > 0) {
            int liberty = colony.getLiberty();
            int requiredLiberty = Colony.LIBERTY_PER_REBEL * colony.getUnitCount();

            if (liberty < requiredLiberty) {
                turns100 = (requiredLiberty - liberty) / libertyProduction;
            }

            requiredLiberty = requiredLiberty / 2;
            if (liberty < requiredLiberty) {
                turns50 = (requiredLiberty - liberty) / libertyProduction;
            }

            if (members < colony.getUnitCount()) {
                requiredLiberty  = Colony.LIBERTY_PER_REBEL * (members + 1);
                if (liberty < requiredLiberty) {
                    turnsNext = (requiredLiberty - liberty) / libertyProduction;
                }
            }
        }

        String na = Messages.message("notApplicable.short");
        add(new JLabel(Messages.message("report.nextMember")));
        add(new JLabel(turnsNext == 0 ? na : Integer.toString((int) Math.ceil(turnsNext))), "skip");
        add(new JLabel(Messages.message("report.50percent")));
        add(new JLabel(turns50 == 0 ? na : Integer.toString((int) Math.ceil(turns50))), "skip");
        add(new JLabel(Messages.message("report.100percent")));
        add(new JLabel(turns100 == 0 ? na : Integer.toString((int) Math.ceil(turns100))), "skip");

    }


    public Dimension getPreferredSize() {
        return new Dimension(350, 250);
    }
}


