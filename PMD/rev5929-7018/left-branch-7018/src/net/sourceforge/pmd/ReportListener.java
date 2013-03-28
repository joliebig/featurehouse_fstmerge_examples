
package net.sourceforge.pmd;

import net.sourceforge.pmd.stat.Metric;

public interface ReportListener {
    void ruleViolationAdded(IRuleViolation ruleViolation);

    void metricAdded(Metric metric);
}
