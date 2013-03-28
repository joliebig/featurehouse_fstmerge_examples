


package net.sf.freecol.client.gui.panel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.option.OptionMapUI;
import net.sf.freecol.common.model.DifficultyLevel;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.option.AbstractOption;
import net.sf.freecol.common.option.OptionMap;

import net.miginfocom.swing.MigLayout;


public final class DifficultyDialog extends FreeColDialog<DifficultyLevel> implements ItemListener {

    private static final Logger logger = Logger.getLogger(DifficultyDialog.class.getName());

    private static final String RESET = "RESET";

    private OptionMapUI ui;
    private DifficultyLevel level;
    private JPanel optionPanel;

    private int DEFAULT_INDEX = 0;
    private int CUSTOM_INDEX = -1;

    private final JComboBox difficultyBox = new JComboBox();

    
    public DifficultyDialog(Canvas parent) {
        super(parent);
        setLayout(new MigLayout("wrap 1, fill"));

        
        JLabel header = localizedLabel("gameOptions.difficultySettings.name");
        header.setFont(((Font) UIManager.get("HeaderFont")).deriveFont(0, 48));
        add(header, "center, wrap 20");

        for (DifficultyLevel level : Specification.getSpecification().getDifficultyLevels()) {
            String id = level.getId();
            if ("model.difficulty.medium".equals(id)) {
                DEFAULT_INDEX = difficultyBox.getItemCount();
            } else if ("model.difficulty.custom".equals(id)) {
                CUSTOM_INDEX = difficultyBox.getItemCount();
            }
            difficultyBox.addItem(Messages.message(id));
        }
        difficultyBox.setSelectedIndex(DEFAULT_INDEX);
        difficultyBox.addItemListener(this);
        add(difficultyBox);

        
        level = Specification.getSpecification().getDifficultyLevel(DEFAULT_INDEX);
        ui = new OptionMapUI(new DifficultyOptionMap(level), false);
        ui.setOpaque(false);
        optionPanel = new JPanel();
        optionPanel.setOpaque(true);
        optionPanel.add(ui);
        JScrollPane scrollPane = new JScrollPane(optionPanel,
                                                 JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
        add(scrollPane, "height 100%, width 100%");

        
        add(okButton, "newline 20, split 3, tag ok");

        JButton reset = new JButton(Messages.message("reset"));
        reset.setActionCommand(RESET);
        reset.addActionListener(this);
        reset.setMnemonic('R');
        add(reset);
        
        add(cancelButton, "tag cancel");

        setSize(780, 540);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(780, 540);
    }
    
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    public void initialize() {
        removeAll();

    }

    
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (OK.equals(command)) {
            ui.unregister();
            ui.updateOption();
            getCanvas().remove(this);
            setResponse(level);
        } else if (CANCEL.equals(command)) {
            ui.rollback();
            ui.unregister();
            getCanvas().remove(this);
            setResponse(Specification.getSpecification().getDifficultyLevel(DEFAULT_INDEX));
        } else if (RESET.equals(command)) {
            ui.reset();
        } else {
            logger.warning("Invalid ActionCommand: " + command);
        }
    }

    public void itemStateChanged(ItemEvent event) {
        int index = difficultyBox.getSelectedIndex();
        level = Specification.getSpecification().getDifficultyLevel(index);
        ui = new OptionMapUI(new DifficultyOptionMap(level), (index == CUSTOM_INDEX));
        optionPanel.removeAll();
        optionPanel.add(ui);
        revalidate();
        repaint();
    }


    private class DifficultyOptionMap extends OptionMap {

        private DifficultyLevel level;

        public DifficultyOptionMap(DifficultyLevel level) {
            super("difficultySettings");
            this.level = level;
            for (AbstractOption option: level.getOptions().values()) {
                option.setGroup("difficultySettings");
                add(option);
            }
        }

        public DifficultyLevel getLevel() {
            return level;
        }

        protected void addDefaultOptions() {
            
        }

        protected boolean isCorrectTagName(String tagName) {
            return true;
        }
    }

}
