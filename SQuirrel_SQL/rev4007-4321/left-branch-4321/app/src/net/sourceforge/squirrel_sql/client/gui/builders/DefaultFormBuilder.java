package net.sourceforge.squirrel_sql.client.gui.builders;
import java.awt.Component;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ConstantSize;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;

public final class DefaultFormBuilder extends I15dPanelBuilder {
    
    private RowSpec lineGapSpec = FormFactory.LINE_GAP_ROWSPEC;
    
    private RowSpec paragraphGapSpec = FormFactory.PARAGRAPH_GAP_ROWSPEC;
    
    private int leadingColumnOffset = 0;
    
    private boolean rowGroupingEnabled = false;
    
        
    public DefaultFormBuilder(FormLayout layout) {
        this(new JPanel(), layout);
    }
        
    public DefaultFormBuilder(JPanel panel, FormLayout layout) {
        this(panel, layout, null);
    }
        
    public DefaultFormBuilder(FormLayout layout, ResourceBundle bundle) {
        this(new JPanel(), layout, bundle);
    }
        
    public DefaultFormBuilder(JPanel panel, FormLayout layout, ResourceBundle bundle) {
        super(panel, layout, bundle);
    }
    
    
    public void setLineGapSize(ConstantSize lineGapSize) {
        RowSpec rowSpec = FormFactory.createGapRowSpec(lineGapSize);
        this.lineGapSpec = rowSpec.asUnmodifyable();
    }
    
    public RowSpec getLineGapSpec() {
        return lineGapSpec;
    }
    
    public void setParagraphGapSize(ConstantSize paragraphGapSize) {
        RowSpec rowSpec = FormFactory.createGapRowSpec(paragraphGapSize);
        this.paragraphGapSpec = rowSpec.asUnmodifyable();
    }
    
    public int getLeadingColumnOffset() {
        return leadingColumnOffset;
    }
    
    public void setLeadingColumnOffset(int columnOffset) {
        this.leadingColumnOffset = columnOffset;
    }
    
    public boolean isRowGroupingEnabled() {
        return rowGroupingEnabled;
    }
    
    public void setRowGroupingEnabled(boolean enabled) {
        rowGroupingEnabled = enabled;
    }
    
    
    public void append(Component component) {
        append(component, 1);
    }
    
    public void append(Component component, int columnSpan) {
        ensureCursorColumnInGrid();
        ensureHasGapRow(lineGapSpec);
        ensureHasComponentLine();
        setColumnSpan(columnSpan);
        add(component);
        setColumnSpan(1);
        nextColumn(columnSpan + 1);
    }
        
    public void append(Component c1, Component c2) {
        append(c1);
        append(c2);
    }
        
    public void append(Component c1, Component c2, Component c3) {
        append(c1);
        append(c2);
        append(c3);
    }
    
    
    public JLabel append(String textWithMnemonic) {
        JLabel label = getComponentFactory().createLabel(textWithMnemonic);
        append(label);
        return label;
    }
        
    public JLabel append(String textWithMnemonic, Component component) {
        return append(textWithMnemonic, component, 1);
    }
        
    public JLabel append(String textWithMnemonic, Component c, int columnSpan) {
        JLabel label = append(textWithMnemonic);
        label.setLabelFor(c);
        append(c, columnSpan);
        return label;
    }
        
    public JLabel append(String textWithMnemonic, Component c1, Component c2) {
        JLabel label = append(textWithMnemonic, c1);
        append(c2);
        return label;
    }
        
    public void append(String textWithMnemonic, Component c1, Component c2, int colSpan) {
        append(textWithMnemonic, c1);
        append(c2, colSpan);
    }
        
    public JLabel append(String textWithMnemonic, Component c1, Component c2, Component c3) {
        JLabel label = append(textWithMnemonic, c1, c2);
        append(c3);
        return label;
    }
        
    public JLabel append(String textWithMnemonic, Component c1, Component c2, Component c3, Component c4) {
        JLabel label = append(textWithMnemonic, c1, c2, c3);
        append(c4);
        return label;
    }
    
    
    public JLabel appendI15d(String resourceKey) {
        return append(getI15dString(resourceKey));
    }
        
    public JLabel appendI15d(String resourceKey, Component c, int columnSpan) {
        JLabel label = appendI15d(resourceKey);
        append(c, columnSpan);
        return label;
    }
        
    public JLabel appendI15d(String resourceKey, Component component) {
        return appendI15d(resourceKey, component, 1);
    }
        
    public JLabel appendI15d(String resourceKey, Component component, boolean nextLine) {
        JLabel label = appendI15d(resourceKey, component, 1);
        if (nextLine) {
            nextLine();
        }
        return label;
    }
        
    public JLabel appendI15d(String resourceKey, Component c1, Component c2) {
        JLabel label = appendI15d(resourceKey, c1);
        append(c2);
        return label;
    }
        
    public JLabel appendI15d(String resourceKey, Component c1, Component c2, int colSpan) {
        JLabel label = appendI15d(resourceKey, c1);
        append(c2, colSpan);
        return label;
    }
        
    public JLabel appendI15d(String resourceKey, Component c1, Component c2, Component c3) {
        JLabel label = appendI15d(resourceKey, c1, c2);
        append(c3);
        return label;
    }
        
    public JLabel appendI15d(String resourceKey, Component c1, Component c2, Component c3, Component c4) {
        JLabel label = appendI15d(resourceKey, c1, c2, c3);
        append(c4);
        return label;
    }
    
    
    public JLabel appendTitle(String textWithMnemonic) {
        JLabel titleLabel = getComponentFactory().createTitle(textWithMnemonic);
        append(titleLabel);
        return titleLabel;
    }
    
    public JLabel appendI15dTitle(String resourceKey) {
        return appendTitle(getI15dString(resourceKey));
    }
    
    
    public JComponent appendSeparator() {
        return appendSeparator("");
    }
    
    public JComponent appendSeparator(String text) {
        ensureCursorColumnInGrid();
        ensureHasGapRow(paragraphGapSpec);
        ensureHasComponentLine();
        setColumn(super.getLeadingColumn());
        int columnSpan = getColumnCount();
        setColumnSpan(getColumnCount());
        JComponent titledSeparator = addSeparator(text);
        setColumnSpan(1);
        nextColumn(columnSpan);
        return titledSeparator;
    }
    
    public void appendI15dSeparator(String resourceKey) {
        appendSeparator(getI15dString(resourceKey));
    }
    
    
    protected int getLeadingColumn() {
        int column = super.getLeadingColumn();
        return column + getLeadingColumnOffset() * getColumnIncrementSign();
    }
    
    
    private void ensureCursorColumnInGrid() {
        if (getColumn() > getColumnCount()) {
            nextLine();
        }
    }
    
    private void ensureHasGapRow(RowSpec gapRowSpec) {
        if ((getRow() == 1) || (getRow() <= getRowCount()))
            return;
        if (getRow() <= getRowCount()) {
            RowSpec rowSpec = getCursorRowSpec();
            if ((rowSpec == gapRowSpec))
                return;
        }
        appendRow(gapRowSpec);
        nextLine();
    }
    
    private void ensureHasComponentLine() {
        if (getRow() <= getRowCount()) return;
        appendRow(FormFactory.PREF_ROWSPEC);  
        if (isRowGroupingEnabled()) {
            getLayout().addGroupedRow(getRow());
        }      
    }
    
    private RowSpec getCursorRowSpec() {
        return getLayout().getRowSpec(getRow());
    }
}
