

package net.sf.freecol.client.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JTextField;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.StringTemplate;

import net.miginfocom.swing.MigLayout;


public class ConfirmDeclarationDialog extends FreeColDialog<List<String>> {

    private static final Logger logger = Logger.getLogger(ConfirmDeclarationDialog.class.getName());

    
    public ConfirmDeclarationDialog(final Canvas parent) {
        super(parent);
        Player player = getMyPlayer();

        StringTemplate nation
            = StringTemplate.template("declareIndependence.defaultNation")
                .addStringTemplate("%nation%", player.getNationName());
        final JTextField nationField
            = new JTextField(Messages.message(nation), 20);
        StringTemplate country
            = StringTemplate.template("declareIndependence.defaultCountry")
                .add("%nation%", player.getNewLandName());
        final JTextField countryField =
            new JTextField(Messages.message(country), 20);

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


        StringTemplate sure
            = StringTemplate.template("declareIndependence.areYouSure.text")
                .add("%monarch%", player.getMonarch().getNameKey());
        add(getDefaultTextArea(Messages.message(sure)));
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