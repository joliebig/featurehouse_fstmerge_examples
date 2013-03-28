package net.sourceforge.pmd;


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
}
