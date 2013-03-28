

package net.sf.freecol.client.gui.option;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import net.sf.freecol.common.option.AudioMixerOption;
import net.sf.freecol.common.option.BooleanOption;
import net.sf.freecol.common.option.FileOption;
import net.sf.freecol.common.option.IntegerOption;
import net.sf.freecol.common.option.LanguageOption;
import net.sf.freecol.common.option.ListOption;
import net.sf.freecol.common.option.Option;
import net.sf.freecol.common.option.OptionGroup;
import net.sf.freecol.common.option.OptionMap;
import net.sf.freecol.common.option.PercentageOption;
import net.sf.freecol.common.option.RangeOption;
import net.sf.freecol.common.option.SelectOption;

import net.miginfocom.swing.MigLayout;


public final class OptionMapUI extends JPanel implements OptionUpdater {

    private static final Logger logger = Logger.getLogger(OptionMapUI.class.getName());

    public static final int H_GAP = 10;

    private final List<OptionUpdater> optionUpdaters = new ArrayList<OptionUpdater>();
    
    private final HashMap<String, JComponent> optionUIs;

    private final JTabbedPane tb;


    
    public OptionMapUI(OptionMap option) {
        this(option, true);
    }

    
    public OptionMapUI(OptionMap option, boolean editable) {
        super(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setLayout(new MigLayout("wrap 4", "[fill]related[fill]unrelated[fill]related[fill]"));
        northPanel.setOpaque(false);
        
        optionUIs = new HashMap<String, JComponent>();

        tb = new JTabbedPane(JTabbedPane.TOP);
        tb.setOpaque(false);

        ArrayList<JComponent> ou = new ArrayList<JComponent>();
        Iterator<Option> it = option.iterator();
        while (it.hasNext()) {
            Option o = it.next();

            if (o instanceof OptionGroup) {
                OptionGroupUI c = new OptionGroupUI((OptionGroup) o, editable, 1, optionUIs);
                c.setOpaque(true);
                optionUpdaters.add(c);
                JScrollPane scroll = new JScrollPane(c, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scroll.getVerticalScrollBar().setUnitIncrement(16);
                scroll.setBorder(BorderFactory.createEmptyBorder());
                c.setBorder(BorderFactory.createEmptyBorder(H_GAP - 5, H_GAP, 0,
                        H_GAP));
                tb.addTab(o.getName(), null, scroll, o.getShortDescription());
            } else if (o instanceof BooleanOption) {
                BooleanOptionUI c = new BooleanOptionUI((BooleanOption) o, editable);
                if (c.getText().length() > 40) {
                    northPanel.add(c, "newline, span");
                } else {
                    northPanel.add(c, "span 2");
                }
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof FileOption) {
                final FileOptionUI iou = new FileOptionUI((FileOption) o, editable);
                northPanel.add(iou, "newline, span");
                optionUpdaters.add(iou);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), iou);
                }
            } else if (o instanceof PercentageOption) {
                PercentageOptionUI c = new PercentageOptionUI((PercentageOption) o, editable);
                northPanel.add(c, "newline, span");
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof ListOption) {
                @SuppressWarnings("unchecked")
                ListOptionUI c = new ListOptionUI((ListOption) o, editable);
                northPanel.add(c);
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof IntegerOption) {
                IntegerOptionUI c = new IntegerOptionUI((IntegerOption) o, editable);
                if (c.getLabel().getText().length() > 30) {
                    northPanel.add(c.getLabel(), "newline, span 3, right");
                } else {
                    northPanel.add(c.getLabel(), "right");
                }
                northPanel.add(c);
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof RangeOption) {
                RangeOptionUI c = new RangeOptionUI((RangeOption) o, editable);
                northPanel.add(c, "newline, span");
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof SelectOption) {
                SelectOptionUI c = new SelectOptionUI((SelectOption) o, editable);
                if (c.getLabel().getText().length() > 30) {
                    northPanel.add(c.getLabel(), "newline, span 3, right");
                } else {
                    northPanel.add(c.getLabel(), "right");
                }
                northPanel.add(c);
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof LanguageOption) {
                LanguageOptionUI c = new LanguageOptionUI((LanguageOption) o, editable);
                if (c.getLabel().getText().length() > 30) {
                    northPanel.add(c.getLabel(), "newline, span 3");
                } else {
                    northPanel.add(c.getLabel());
                }
                northPanel.add(c);
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else if (o instanceof AudioMixerOption) {
                AudioMixerOptionUI c = new AudioMixerOptionUI((AudioMixerOption) o, editable);
                if (c.getLabel().getText().length() > 30) {
                    northPanel.add(c.getLabel(), "newline, span 3");
                } else {
                    northPanel.add(c.getLabel());
                }
                northPanel.add(c);
                optionUpdaters.add(c);
                if (!o.getId().equals(Option.NO_ID)) {
                    optionUIs.put(o.getId(), c);
                }
            } else {
                logger.warning("Unknown option.");
            }
        }

        add(northPanel, BorderLayout.NORTH);
        if (tb.getTabCount() > 0) {
            add(tb, BorderLayout.CENTER);
        }

        setOpaque(false);
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
    
    public JComponent getOptionUI(String key) {
        return optionUIs.get(key);
    }

    
    public void reset() {
        for (OptionUpdater optionUpdater : optionUpdaters) {
            optionUpdater.reset();
        }
    }
}
