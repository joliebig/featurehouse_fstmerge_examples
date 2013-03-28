package net.sourceforge.pmd.lang.rule.stat;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.Metric;


public interface StatisticalRule extends Rule {
    void addDataPoint(DataPoint point);
    Object[] getViolationParameters(DataPoint point);
}
