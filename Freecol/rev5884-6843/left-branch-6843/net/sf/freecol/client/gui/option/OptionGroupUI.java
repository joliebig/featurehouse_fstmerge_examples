

package net.sf.freecol.client.gui.option;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.action.FreeColAction;
import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.client.gui.panel.FreeColPanel;
import net.sf.freecol.common.option.AudioMixerOption;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.FileOption;
import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.ListOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.OptionGroup;
import net.sf.freecol.common.option.PercentageOption;
import net.sf.freecol.common.option.RangeOption;
import net.sf.freecol.common.option.SelectOption;

import net.miginfocom.swing.MigLayout;


public final class OptionGroupUI extends JPanel implements OptionUpdater {

    private static final Logger logger = Logger.getLogger(OptionGroupUI.class.getName());

    private final ArrayList<OptionUpdater> optionUpdaters = new ArrayList<OptionUpdater>();

    
    public OptionGroupUI(OptionGroup option, boolean editable, int level, Map<String, JComponent> optionUIs) {
        
        setLayout(new MigLayout("wrap 4", "[fill]related[fill]unrelated[fill]related[fill]"));

        Iterator<Option> it = option.iterator();
        while (it.hasNext()) {
            Option o = it.next();
            addOptionUI(o, editable, level, optionUIs);
        }

        setOpaque(false);
    }


    private void addOptionUI(Option o, boolean editable, int level, Map<String, JComponent> optionUIs) {
        if (o instanceof OptionGroup) {
            add(new JLabel(o.getName()), "span, split 2");
            add(new JSeparator(), "growx");
            Iterator<Option> it = ((OptionGroup) o).iterator();
            while (it.hasNext()) {
                Option option = it.next();
                addOptionUI(option, editable, level, optionUIs);
            }
        } else if (o instanceof BooleanOption) {                
            final BooleanOptionUI boi = new BooleanOptionUI((BooleanOption) o, editable);
            optionUpdaters.add(boi);
            if (boi.getText().length() > 40) {
                add(boi, "newline, span");
            } else {
                add(boi, "span 2");
            }
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), boi);
            }
        } else if (o instanceof PercentageOption) {
            final PercentageOptionUI soi = new PercentageOptionUI((PercentageOption) o, editable);
            add(soi, "newline, span");
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof ListOption) {
            @SuppressWarnings("unchecked")
            final ListOptionUI soi = new ListOptionUI((ListOption) o, editable);
            add(soi);
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof IntegerOption) {
            final IntegerOptionUI iou = new IntegerOptionUI((IntegerOption) o, editable);
            if (iou.getLabel().getText().length() > 30) {
                add(iou.getLabel(), "newline, span 3, right");
            } else {
                add(iou.getLabel(), "right");
            }
            add(iou);
            optionUpdaters.add(iou);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), iou);
            }
        } else if (o instanceof FileOption) {
            final FileOptionUI iou = new FileOptionUI((FileOption) o, editable);
            add(iou, "newline, span");
            optionUpdaters.add(iou);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), iou);
            }
        } else if (o instanceof RangeOption) {
            final RangeOptionUI soi = new RangeOptionUI((RangeOption) o, editable);
            add(soi, "newline, span");
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof SelectOption) {
            final SelectOptionUI soi = new SelectOptionUI((SelectOption) o, editable);
            if (soi.getLabel().getText().length() > 30) {
                add(soi.getLabel(), "newline, span 3, right");
            } else {
                add(soi.getLabel(), "right");
            }
            add(soi);
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof LanguageOption) {
            final LanguageOptionUI soi = new LanguageOptionUI((LanguageOption) o, editable);
            if (soi.getLabel().getText().length() > 30) {
                add(soi.getLabel(), "newline, span 3");
            } else {
                add(soi.getLabel());
            }
            add(soi);
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof AudioMixerOption) {
            final AudioMixerOptionUI soi = new AudioMixerOptionUI((AudioMixerOption) o, editable);
            if (soi.getLabel().getText().length() > 30) {
                add(soi.getLabel(), "newline, span 3");
            } else {
                add(soi.getLabel());
            }
            add(soi);
            optionUpdaters.add(soi);
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), soi);
            }
        } else if (o instanceof FreeColAction) {
            final FreeColActionUI fau = new FreeColActionUI((FreeColAction) o, this);
            optionUpdaters.add(fau);
            add(fau, "newline, span");
            if (!o.getId().equals(Option.NO_ID)) {
                optionUIs.put(o.getId(), fau);
            }
        } else {
            logger.warning("Unknown option.");
        }
    }


    
    public void rollback() {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            optionUpdater.rollback();
        }
    }
    
    
    public void unregister() {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            optionUpdater.unregister();
        }
    }

    
    public void updateOption() {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            optionUpdater.updateOption();
        }
    }
    
    
    public void reset() {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            optionUpdater.reset();
        }
    }

    
    public void removeKeyStroke(KeyStroke keyStroke) {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            if (optionUpdater instanceof FreeColActionUI) {
                ((FreeColActionUI) optionUpdater).removeKeyStroke(keyStroke);
            }
        }
    }

    
    private class OptionGroupButton extends JButton implements OptionUpdater {
        
        private final OptionGroupUI groupUI;
        private final OptionGroupButton optionGroupButton;
        private final OptionGroupPanel optionGroupPanel;
        
        
        OptionGroupButton(final String name, final OptionGroupUI groupUI) {
            super(name);
            
            this.groupUI = groupUI;
            optionGroupButton = this;
            optionGroupPanel = new OptionGroupPanel();
            
            addActionListener(new ActionListener() {
               public void actionPerformed(ActionEvent e) {
                   optionGroupButton.setEnabled(false);
                   FreeCol.getFreeColClient().getCanvas().addAsFrame(optionGroupPanel);
               }                
            });
        }
        
        
        public void rollback() {
            groupUI.rollback();
        }
        
        
        public void updateOption() {
            groupUI.updateOption();
        }
        
        
        public void reset() {
            groupUI.reset();
        }
        
        
        public void unregister() {
            groupUI.unregister();
            FreeCol.getFreeColClient().getCanvas().remove(optionGroupPanel);
        }
        
                
        private class OptionGroupPanel extends FreeColPanel {
            public OptionGroupPanel() {
                super(FreeCol.getFreeColClient().getCanvas(), new BorderLayout());
                
                JButton button = new JButton(Messages.message("ok"));
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        FreeCol.getFreeColClient().getCanvas().remove(optionGroupPanel);
                        optionGroupButton.setEnabled(true);
                    }
                });
                
                add(groupUI, BorderLayout.CENTER);
                add(button, BorderLayout.SOUTH);
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(400, 200);
            }
            
            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        }
    }
}
