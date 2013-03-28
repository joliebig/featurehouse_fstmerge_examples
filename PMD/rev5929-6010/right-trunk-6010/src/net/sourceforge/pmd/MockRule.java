package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;


public class MockRule extends AbstractRule {

	public MockRule() {
		super();
	}

	public MockRule(String name, String description, String message, String ruleSetName, int priority) {
		this(name, description, message, ruleSetName);
		setPriority(priority);
	}

	public MockRule(String name, String description, String message, String ruleSetName) {
		super();
		setName(name);
		setDescription(description);
		setMessage(message);
		setRuleSetName(ruleSetName);
	}

	public void apply(List<Node> nodes, RuleContext ctx) {
	}
}
