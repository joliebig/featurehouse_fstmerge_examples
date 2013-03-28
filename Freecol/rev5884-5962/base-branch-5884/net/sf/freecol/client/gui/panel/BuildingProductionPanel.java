

package net.sf.freecol.client.gui.panel;

import java.awt.Font;

import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.FeatureContainer;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Unit;

import net.miginfocom.swing.MigLayout;

public class BuildingProductionPanel extends FreeColPanel {

    public BuildingProductionPanel(Canvas canvas, Unit unit) {

        super(canvas);

        Colony colony = unit.getColony();
        Building building = unit.getWorkLocation();
        GoodsType goodsType = building.getGoodsOutputType();
        Set<Modifier> basicModifiers = building.getProductivityModifiers(unit);
        basicModifiers.addAll(unit.getColony().getModifierSet(goodsType.getId()));
        Set<Modifier> modifiers = sortModifiers(basicModifiers);
        if (colony.getProductionBonus() != 0) {
            modifiers.add(colony.getProductionModifier(goodsType));
        }

        setLayout(new MigLayout("", "[]20[align right][]", ""));

        add(new JLabel(building.getName()), "span, align center");

        
        add(new UnitLabel(unit, canvas, false, true), "newline");
        add(new JLabel(getLibrary().getGoodsImageIcon(goodsType)));

        for (Modifier modifier : modifiers) {
            FreeColGameObjectType source = modifier.getSource();
            String sourceName;
            if (source == null) {
                sourceName = "???";
            } else {
                sourceName = source.getName();
            }
            add(new JLabel(sourceName), "newline");
            boolean percent = false;
            String bonus = getModifierFormat().format(modifier.getValue());
            switch(modifier.getType()) {
            case ADDITIVE:
                if (modifier.getValue() > 0) {
                    bonus = "+" + bonus;
                }
                break;
            case PERCENTAGE:
                if (modifier.getValue() > 0) {
                    bonus = "+" + bonus;
                }
                percent = true;
                break;
            case MULTIPLICATIVE:
                bonus = "\u" + bonus;
                break;
            default:
            }
            add(new JLabel(bonus));
            if (percent) {
                add(new JLabel("%"));
            }
        }

        Font bigFont = getFont().deriveFont(Font.BOLD, 20f);

        int result = (int) FeatureContainer.applyModifierSet(0, building.getGame().getTurn(), basicModifiers) +
            colony.getProductionBonus();
        JLabel finalLabel = new JLabel(Messages.message("modifiers.finalResult.name"));
        finalLabel.setFont(bigFont);
        add(new JSeparator(JSeparator.HORIZONTAL), "newline, span, growx");
        add(finalLabel);
        JLabel finalResult = new JLabel(getModifierFormat().format(result));
        finalResult.setFont(bigFont);
        add(finalResult);

        add(okButton, "newline 20, span, tag ok");
        setSize(getPreferredSize());

    }
}