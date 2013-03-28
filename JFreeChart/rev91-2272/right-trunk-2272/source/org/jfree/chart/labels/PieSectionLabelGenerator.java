

package org.jfree.chart.labels;

import java.awt.Font;
import java.awt.Paint;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

import org.jfree.data.pie.PieDataset;


public interface PieSectionLabelGenerator {

    
    public String generateSectionLabel(PieDataset dataset, Comparable key);

    
    public AttributedString generateAttributedSectionLabel(PieDataset dataset,
                                                           Comparable key);

}
