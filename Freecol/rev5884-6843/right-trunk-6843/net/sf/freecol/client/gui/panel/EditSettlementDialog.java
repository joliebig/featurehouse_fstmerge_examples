

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.IndianSettlement;
import net.sf.freecol.common.model.IndianNationType;
import net.sf.freecol.common.model.Nation;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.Unit.UnitState;
import net.sf.freecol.common.model.UnitType;

import net.miginfocom.swing.MigLayout;


public final class EditSettlementDialog extends FreeColDialog<IndianSettlement> 
    implements ItemListener {

    private static final Logger logger = Logger.getLogger(EditSettlementDialog.class.getName());

    private static final UnitType BRAVE = Specification.getSpecification().getUnitType("model.unit.brave");

    private static final String REMOVE = "REMOVE";
    
    private final IndianSettlement settlement;

    private final JCheckBox capital;
    private final JComboBox owner;
    private final JComboBox skill;
    private final JSpinner units;
    private final JTextField name;

    private class SkillRenderer implements ListCellRenderer {

        private JLabel label = new JLabel();

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            label.setText(Messages.message(((UnitType) value).getNameKey()));
            return label;
        }
    }

    
    public EditSettlementDialog(Canvas canvas, final IndianSettlement settlement) {
        
        super(canvas);
        this.settlement = settlement;

        okButton.addActionListener(this);

        Vector<Nation> natives = new Vector<Nation>();
        for (Player player : settlement.getGame().getPlayers()) {
            if (player.isAI() && player.isIndian()) {
                natives.add(player.getNation());
            }
        }

        setLayout(new MigLayout("wrap 2, gapx 20", "", ""));

        add(new JLabel(Messages.message("name")));
        name = new JTextField(settlement.getName(), 30);
        add(name);

        add(new JLabel(Messages.message("nation")));
        owner = new JComboBox(natives);
        owner.setSelectedItem(settlement.getOwner().getNation());
        owner.addItemListener(this);
        add(owner);

        add(new JLabel(Messages.message("capital")));
        capital = new JCheckBox();
        capital.setSelected(settlement.isCapital());
        add(capital);

        add(new JLabel(Messages.message("report.indian.skillTaught")));
        skill = new JComboBox();
        IndianNationType ownerType = (IndianNationType)
            ((Nation) owner.getSelectedItem()).getType();
        UnitType[] skills = new UnitType[ownerType.getSkills().size()];
        for (int index = 0; index < skills.length; index++) {
            skills[index] = ownerType.getSkills().get(index).getObject();
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(skills);
        skill.setModel(model);
        skill.setSelectedItem(settlement.getLearnableSkill());
        skill.setRenderer(new SkillRenderer());
        add(skill);

        add(new JLabel(Messages.message("report.units")));
        int unitCount = settlement.getUnitCount();
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(unitCount, 1, 20, 1);
        units = new JSpinner(spinnerModel);
        add(units);
        
        JButton remove = new JButton(Messages.message("editor.removeSettlement"));
        remove.setActionCommand(REMOVE);
        remove.addActionListener(this);
        add(remove,"span,align center");
        
        add(okButton, "newline 20, span, split 2, tag ok");
        add(cancelButton, "tag cancel");

        setSize(getPreferredSize());
    }

    public void itemStateChanged(ItemEvent e) {
        IndianNationType ownerType = (IndianNationType)
            ((Nation) owner.getSelectedItem()).getType();
        UnitType[] skills = new UnitType[ownerType.getSkills().size()];
        for (int index = 0; index < skills.length; index++) {
            skills[index] = ownerType.getSkills().get(index).getObject();
        }
        DefaultComboBoxModel model = new DefaultComboBoxModel(skills);
        skill.setModel(model);
        skill.setSelectedItem(settlement.getLearnableSkill());
    }

    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            settlement.setName(name.getText());
            Nation newNation = (Nation) owner.getSelectedItem();
            if (newNation != settlement.getOwner().getNation()) {
                Player newPlayer = settlement.getGame().getPlayer(newNation.getId());
                settlement.changeOwner(newPlayer);
                MapEditorTransformPanel.setNativePlayer(newPlayer);
            }
            if (capital.isSelected() && !settlement.isCapital()) {
                
                for (IndianSettlement indianSettlement : settlement.getOwner().getIndianSettlements()) {
                    indianSettlement.setCapital(false);
                }
                settlement.setCapital(true);
            } else if (!capital.isSelected() && settlement.isCapital()) {
                settlement.setCapital(false);
            }
            settlement.setLearnableSkill((UnitType) skill.getSelectedItem());
            int numberOfUnits = (Integer) units.getValue() - settlement.getUnitCount();
            if (numberOfUnits > 0) {
                for (int index = 0; index < numberOfUnits; index++) {
                    settlement.add(new Unit(settlement.getGame(), settlement, settlement.getOwner(),
                                            BRAVE, UnitState.ACTIVE));
                }
            } else if (numberOfUnits < 0) {
                List<Unit> unitList = new ArrayList<Unit>(settlement.getUnitList().subList(0, -numberOfUnits));
                for (Unit unit : unitList) {
                    unit.dispose();
                }
            } 
        }
        else if(REMOVE.equals(command)) {
        	boolean confirm = getCanvas().showConfirmDialog("editor.removeSettlement.text",
                                                          "ok", "cancel");
        	if(!confirm){
        		return;
        	}
        	
        	Tile t = settlement.getTile();
        	List<Unit> unitList = new ArrayList<Unit>(t.getUnitList());
            for (Unit unit : unitList) {
                unit.dispose();
            }
        	t.setSettlement(null);
            settlement.dispose();
        }
        getCanvas().remove(this);
    }
}
