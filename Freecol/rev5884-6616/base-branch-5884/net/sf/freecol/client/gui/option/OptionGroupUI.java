

package net.sf.freecol.client.gui.option;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
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


public final class OptionGroupUI extends JPanel implements OptionUpdater {
    private static final Logger logger = Logger.getLogger(OptionGroupUI.class.getName());




    
    public static final int H_GAP = 10;

    private final OptionUpdater[] optionUpdaters;

    
    public OptionGroupUI(OptionGroup option, boolean editable, int level, HashMap<String, JComponent> optionUIs) {
        
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        
        JPanel horizontalPanel = null;
        boolean buttonAdded = false;
        
        ArrayList<OptionUpdater> ou = new ArrayList<OptionUpdater>();
        Iterator<Option> it = option.iterator();
        while (it.hasNext()) {
            Option o = it.next();

            if (o instanceof OptionGroup) {
                if (level == 2) {
                    final OptionGroupUI groupUI = new OptionGroupUI((OptionGroup) o, editable, 1, optionUIs);
                    final OptionGroupButton ogb = new OptionGroupButton(o.getName(), groupUI);
                    ou.add(ogb);
                    if ((horizontalPanel == null) || !buttonAdded) {
                        horizontalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                        horizontalPanel.setOpaque(false);
                        add(horizontalPanel);
                    }
                    horizontalPanel.add(ogb);
                    buttonAdded = true;
                } else {
                    final OptionGroupUI groupUI = new OptionGroupUI((OptionGroup) o, editable, level+1, optionUIs);
                    add(groupUI);
                    ou.add(groupUI);
                    buttonAdded = false;
                }
            } else if (o instanceof BooleanOption) {                
                final BooleanOptionUI boi = new BooleanOptionUI((BooleanOption) o, editable);
                ou.add(boi);
                final boolean alreadyAdded = (horizontalPanel != null && !buttonAdded);
                if (!alreadyAdded || buttonAdded) {
                    horizontalPanel = new JPanel(new GridLayout(1, 2, H_GAP, 5));
                    horizontalPanel.setOpaque(false);
                    add(horizontalPanel);
                }
                horizontalPanel.add(boi);
                if (alreadyAdded) {
                    horizontalPanel = null;
                }
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), boi);
                }
            } else if (o instanceof PercentageOption) {
                final PercentageOptionUI soi = new PercentageOptionUI((PercentageOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof ListOption) {
                @SuppressWarnings("unchecked")
                final ListOptionUI soi = new ListOptionUI((ListOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof IntegerOption) {
                final IntegerOptionUI iou = new IntegerOptionUI((IntegerOption) o, editable);
                add(iou);
                ou.add(iou);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), iou);
                }
            } else if (o instanceof FileOption) {
                final FileOptionUI iou = new FileOptionUI((FileOption) o, editable);
                add(iou);
                ou.add(iou);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), iou);
                }
            } else if (o instanceof RangeOption) {
                final RangeOptionUI soi = new RangeOptionUI((RangeOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof SelectOption) {
                final SelectOptionUI soi = new SelectOptionUI((SelectOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof LanguageOption) {
                final LanguageOptionUI soi = new LanguageOptionUI((LanguageOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof AudioMixerOption) {
                final AudioMixerOptionUI soi = new AudioMixerOptionUI((AudioMixerOption) o, editable);
                add(soi);
                ou.add(soi);
                buttonAdded = false;
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), soi);
                }
            } else if (o instanceof FreeColAction) {
                final FreeColActionUI fau = new FreeColActionUI((FreeColAction) o, this);
                ou.add(fau);
                add(fau);
                
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), fau);
                }
            } else {
                logger.warning("Unknown option.");
            }
        }
        optionUpdaters = ou.toArray(new OptionUpdater[0]);

        setBorder(BorderFactory.createTitledBorder(option.getName()));
        setOpaque(false);
    }

    
    public void rollback() {
        for (int i = 0; i < optionUpdaters.length; i++) {
            optionUpdaters[i].rollback();
        }
    }
    
    
    public void unregister() {
        for (int i = 0; i < optionUpdaters.length; i++) {
            optionUpdaters[i].unregister();
        }
    }

    
    public void updateOption() {
        for (int i = 0; i < optionUpdaters.length; i++) {
            optionUpdaters[i].updateOption();
        }
    }
    
    
    public void reset() {
        for (int i = 0; i < optionUpdaters.length; i++) {
            optionUpdaters[i].reset();
        }
    }

    
    public void removeKeyStroke(KeyStroke keyStroke) {
        for (int i = 0; i < optionUpdaters.length; i++) {
            if (optionUpdaters[i] instanceof FreeColActionUI) {
                ((FreeColActionUI) optionUpdaters[i]).removeKeyStroke(keyStroke);
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
