

package net.sf.freecol.client.gui.panel;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Goods;
import net.sf.freecol.common.model.Unit;

import net.miginfocom.swing.MigLayout;


public final class DumpCargoDialog extends FreeColPanel {

    private static final Logger logger = Logger.getLogger(DumpCargoDialog.class.getName());

    private static final String CANCEL = "CANCEL";

    private final JLabel header;

    private final JButton cancelButton;

    private List<Goods> goodsList;

    private List<JCheckBox> checkBoxes;

    
    public DumpCargoDialog(Canvas parent, Unit unit) {
        super(parent);

        header = new JLabel(Messages.message("dumpGoods"));
        header.setFont(mediumHeaderFont);

        cancelButton = new JButton("cancel");
        cancelButton.setActionCommand(String.valueOf(CANCEL));
        cancelButton.addActionListener(this);

        goodsList = unit.getGoodsList();
        checkBoxes = new ArrayList<JCheckBox>(goodsList.size());

        setLayout(new MigLayout("wrap 1", "", ""));

        for (Goods goods : goodsList) {
            
            
            JCheckBox checkBox = new JCheckBox(goods.toString(),
                                               
                                               true);
            checkBoxes.add(checkBox);
            add(checkBox);
        }

        add(okButton, "newline 20, span, split 2, tag ok");
        add(cancelButton, "tag cancel");

        setSize(getPreferredSize());
    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            for (int index = 0; index < checkBoxes.size(); index++) {
                if (checkBoxes.get(index).isSelected()) {
                    getController().unloadCargo(goodsList.get(index));
                }
            }
        }
        getCanvas().remove(this);
    }

}
