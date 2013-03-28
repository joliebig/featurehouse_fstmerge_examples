

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.util.logging.Logger;

import javax.swing.JComboBox;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.Specification;

import net.miginfocom.swing.MigLayout;


public final class SpecificationDialog extends FreeColDialog<Boolean> implements ActionListener {

    private static final Logger logger = Logger.getLogger(SpecificationDialog.class.getName());

    private final String[] files =
        new String[] {
        "data/freecol/specification.xml",
        "data/classic/specification.xml"
    };

    private final JComboBox specificationBox =
        new JComboBox(new String[] { "FreeCol", "Colonization" });

    private final JComboBox difficultyBox =
        new JComboBox(new String[] {
                Messages.message("model.difficulty.veryEasy"),
                Messages.message("model.difficulty.easy"),
                Messages.message("model.difficulty.medium"),
                Messages.message("model.difficulty.hard"),
                Messages.message("model.difficulty.veryHard")
            });


    
    public SpecificationDialog(Canvas parent) {
        super(parent);
        setLayout(new MigLayout("wrap 1", "fill"));
        add(specificationBox);
        add(difficultyBox);
        add(okButton, "tag ok");

        setSize(getPreferredSize());
    }

    
    @Override
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            try {
                Specification.createSpecification(new FileInputStream(files[specificationBox.getSelectedIndex()]));
                Specification.getSpecification().applyDifficultyLevel(difficultyBox.getSelectedIndex());
                setResponse(true);
            } catch(Exception e) {
                logger.warning(e.toString());
            }
        }
    }


}