

package net.sf.freecol.client.gui.panel;

import java.util.logging.Logger;

import javax.swing.JLabel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;
import net.sf.freecol.common.model.Tension;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;


public final class IndianSettlementPanel extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(IndianSettlementPanel.class.getName());

    
    public IndianSettlementPanel(final Canvas canvas, IndianSettlement settlement) {
        
        super(canvas);

        setLayout(new MigLayout("wrap 2, gapx 20", "", ""));
        
        JLabel settlementLabel = new JLabel(canvas.getImageIcon(settlement, false));
        Player indian = settlement.getOwner();
        Player player = getMyPlayer();
        boolean visited = settlement.hasBeenVisited(player);
        String text = Messages.message(settlement.getNameFor(player)) + ", "
            + Messages.message(StringTemplate.template(settlement.isCapital()
                                                       ? "indianCapital"
                                                       : "indianSettlement")
                               .addStringTemplate("%nation%", indian.getNationName()));
        Tension tension = settlement.getAlarm(player);
        String tensionString
            = (!player.hasContacted(indian)) ? "notContacted"
            : (tension != null) ? tension.toString()
            : "indianSettlement.tensionUnknown";
        text += " (" + Messages.message(tensionString) + ")";
        settlementLabel.setText(text);
        add(settlementLabel);

        Unit missionary = settlement.getMissionary();
        if (missionary != null) {
            String missionaryName = Messages.message(StringTemplate.template("model.unit.nationUnit")
                                                     .addStringTemplate("%nation%", missionary.getOwner().getNationName())
                                                     .addStringTemplate("%unit%", missionary.getLabel()));
            add(new JLabel(missionaryName, canvas.getImageIcon(missionary, true), JLabel.CENTER));
        }

        add(localizedLabel("indianSettlement.learnableSkill"), "newline");
        UnitType skillType = settlement.getLearnableSkill();
        if (visited) {
            if (skillType == null) {
                add(localizedLabel("indianSettlement.skillNone"));
            } else {
                add(new JLabel(Messages.message(skillType.getNameKey()),
                               canvas.getImageIcon(skillType, true), JLabel.CENTER));
            }
        } else {
            add(localizedLabel("indianSettlement.skillUnknown"));
        }

        GoodsType[] wantedGoods = settlement.getWantedGoods();
        add(localizedLabel("indianSettlement.highlyWanted"), "newline");
        if (!visited || wantedGoods.length == 0 || wantedGoods[0] == null) {
            add(localizedLabel("indianSettlement.wantedGoodsUnknown"));
        } else {
            add(new JLabel(Messages.message(wantedGoods[0].getNameKey()),
                           canvas.getImageIcon(wantedGoods[0], false),
                           JLabel.CENTER));
        }

        add(localizedLabel("indianSettlement.otherWanted"), "newline");
        if (!visited || wantedGoods.length <= 1 || wantedGoods[1] == null) {
            add(localizedLabel("indianSettlement.wantedGoodsUnknown"));
        } else {
            int i, n = 1;
            for (i = 2; i < wantedGoods.length; i++) {
                if (wantedGoods[i] != null) n++;
            }
            add(new JLabel(Messages.message(wantedGoods[1].getNameKey()),
                           canvas.getImageIcon(wantedGoods[1], false),
                           JLabel.CENTER),
                "split " + Integer.toString(n));
            for (i = 2; i < wantedGoods.length; i++) {
                if (wantedGoods[i] != null) {
                    add(new JLabel(Messages.message(wantedGoods[i].getNameKey()),
                                   canvas.getImageIcon(wantedGoods[i], false),
                                   JLabel.CENTER));
                }
            }
        }

        add(okButton, "newline 20, span, tag ok");

        setSize(getPreferredSize());
    }

}
