

package net.sf.freecol.client.gui.panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.ColonyTile;
import net.sf.freecol.common.model.FeatureContainer;
import net.sf.freecol.common.model.FreeColGameObjectType;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Modifier;
import net.sf.freecol.common.model.Scope;
import net.sf.freecol.common.model.TileType;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.resources.ResourceManager;

import net.miginfocom.swing.MigLayout;

public class WorkProductionPanel extends FreeColPanel {

    private static final Border border = BorderFactory
        .createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK),
                              BorderFactory.createEmptyBorder(2, 2, 2, 2));

    public WorkProductionPanel(Canvas canvas, Unit unit) {
        super(canvas);

        setLayout(new MigLayout("wrap 3, insets 10 10 10 10", "[]30:push[right][]", ""));

        Colony colony = unit.getColony();
        UnitType unitType = unit.getType();

        JLabel headline = new JLabel();
        Set<Modifier> modifiers;
        Set<Modifier> basicModifiers;
        Set<Modifier> colonyModifiers = new LinkedHashSet<Modifier>();
        if (unit.getLocation() instanceof ColonyTile) {
            ColonyTile colonyTile = (ColonyTile) unit.getLocation();
            GoodsType goodsType = unit.getWorkType();
            basicModifiers = colonyTile.getProductionModifiers(goodsType, unitType);
            modifiers = new LinkedHashSet<Modifier>(basicModifiers);
            basicModifiers.addAll(colony.getModifierSet(goodsType.getId()));
            if (colony.getProductionBonus() != 0) {
                modifiers.add(colony.getProductionModifier(goodsType));
            }

            add(new JLabel(Messages.getLabel(colonyTile)), "span, align center, wrap 30");

            TileType tileType = colonyTile.getWorkTile().getType();
            int width = canvas.getClient().getImageLibrary().getTerrainImageWidth(tileType);
            int height = canvas.getClient().getImageLibrary().getTerrainImageHeight(tileType);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            canvas.getGUI().displayColonyTile((Graphics2D) image.getGraphics(), colonyTile.getWorkTile().getMap(),
                                              colonyTile.getWorkTile(), 0, 0, colony);
            add(new JLabel(new ImageIcon(image)));

        } else {
            Building building = (Building) unit.getLocation();
            GoodsType goodsType = building.getGoodsOutputType();
            modifiers = new LinkedHashSet<Modifier>();
            if (building.getType().getProductionModifier() != null) {
                modifiers.add(building.getType().getProductionModifier());
            }
            if (goodsType != null) {
                modifiers.addAll(sortModifiers(unit.getModifierSet(goodsType.getId())));
            }
            if (colony.getProductionBonus() != 0) {
                modifiers.add(colony.getProductionModifier(goodsType));
            }
            for (Modifier modifier : colony.getModifierSet(goodsType.getId())) {
                if (modifier.getSource() != building.getType()) {
                    colonyModifiers.add(modifier);
                }
            }
            modifiers.addAll(sortModifiers(colonyModifiers));
            add(new JLabel(Messages.getName(building)), "span, align center, wrap 30");

            add(new JLabel(ResourceManager.getImageIcon(building.getType().getId() + ".image")));
        }

        add(new UnitLabel(unit, canvas, false, false), "wrap");

        int percentageCount = 0;
        float totalPercentage = 0;
        float result = 0;
        for (Modifier modifier : modifiers) {
            FreeColGameObjectType source = modifier.getSource();
            String sourceName;
            if (source == null) {
                sourceName = "???";
            } else {
                sourceName = Messages.getName(source);
                if (unitType != null && modifier.hasScope()) {
                    for (Scope scope : modifier.getScopes()) {
                        if (scope.appliesTo(unitType)) {
                            sourceName += " (" + Messages.getName(unitType) + ")";
                        }
                    }
                }
            }
            String bonus = getModifierFormat().format(modifier.getValue());
            boolean percentage = false;
            if (modifier.getType() == Modifier.Type.PERCENTAGE) {
                totalPercentage += modifier.getValue();
                percentageCount++;
            } else if (totalPercentage != 0) {
                if (percentageCount > 1) {
                    String resultString = getModifierFormat().format(totalPercentage);
                    if (totalPercentage > 0) {
                        resultString = "+" + resultString;
                    }
                    JLabel resultLabel = new JLabel(resultString);
                    resultLabel.setBorder(border);
                    add(resultLabel, "skip");
                    add(new JLabel("%"));
                }
                result += (result * totalPercentage) / 100;
                totalPercentage = 0;
                percentageCount = 0;
            }
            switch(modifier.getType()) {
            case ADDITIVE:
                if (modifier.getValue() > 0) {
                    bonus = "+" + bonus;
                }
                result += modifier.getValue();
                break;
            case PERCENTAGE:
                if (modifier.getValue() > 0) {
                    bonus = "+" + bonus;
                }
                percentage = true;
                break;
            case MULTIPLICATIVE:
                bonus = "\u" + bonus;
                result *= modifier.getValue();
                break;
            default:
            }
            add(new JLabel(sourceName), "newline");
            add(new JLabel(bonus));
            if (percentage) {
                add(new JLabel("%"));
            }
        }

        if (totalPercentage != 0) {
            result += (result * totalPercentage) / 100;
        }

        Font bigFont = getFont().deriveFont(Font.BOLD, 16);

        

        JLabel finalLabel = new JLabel(Messages.message("model.source.finalResult.name"));
        finalLabel.setFont(bigFont);
        add(finalLabel, "newline");

        JLabel finalResult = new JLabel(getModifierFormat().format(result));
        finalResult.setFont(bigFont);
        finalResult.setBorder(border);
        add(finalResult, "wrap 30");

        add(okButton, "span, tag ok");

        setSize(getPreferredSize());

    }
}


