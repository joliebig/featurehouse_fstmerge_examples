

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JTextField;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;

import net.miginfocom.swing.MigLayout;


public class ConfirmDeclarationDialog extends FreeColDialog<List<String>> {

    private static final Logger logger = Logger.getLogger(ConfirmDeclarationDialog.class.getName());

    
    public ConfirmDeclarationDialog(final Canvas parent) {
        super(parent);

        final JTextField nationField =
            new JTextField(Messages.message("declareIndependence.defaultNation",
                                            "%nation%",
                                            Messages.getNationAsString(getMyPlayer())), 20);
        final JTextField countryField =
            new JTextField(Messages.message("declareIndependence.defaultCountry",
                                            "%nation%",
                                            getMyPlayer().getNewLandName()), 20);

        okButton = new JButton(Messages.message("declareIndependence.areYouSure.yes"));

        setLayout(new MigLayout("wrap 1", "", ""));

        okButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    List<String> result = new ArrayList<String>();
                    
                    result.add(nationField.getText().replaceAll("[^\\s\\w]", ""));
                    result.add(countryField.getText());
                    setResponse(result);
                }
            });

        cancelButton = new JButton(Messages.message("declareIndependence.areYouSure.no"));
        cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    setResponse(null);
                }
            });

        add(getDefaultTextArea(Messages.message("declareIndependence.areYouSure.text",
                                                "%monarch%",
                                                getMyPlayer().getMonarch().getName())));
        add(getDefaultTextArea(Messages.message("declareIndependence.enterCountry")));
        add(countryField);
        add(getDefaultTextArea(Messages.message("declareIndependence.enterNation")));
        add(nationField);
        add(okButton, "newline 20, split 2, tag ok");
        add(cancelButton, "tag cancel");

    }

    public void requestFocus() {
        okButton.requestFocus();
    }

}