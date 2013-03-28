package net.sourceforge.pmd;


public enum RulePriorityEnum {
    HIGH(1, "High"),
    MEDIUM_HIGH(2, "Medium High"),
    MEDIUM(3, "Medium"),
    MEDIUM_LOW(4, "Medium Low"),
    LOW(5, "Low");

    private final int priority;
    private final String name;

    private RulePriorityEnum(int priority, String name) {
	this.priority = priority;
	this.name = name;
    }

    
    public int getPriority() {
	return priority;
    }

    
    public String getName() {
	return name;
    }

    
    @Override
    public String toString() {
	return name;
    }

    
    public static RulePriorityEnum valueOf(int priority) {
	try {
	    return RulePriorityEnum.values()[priority - 1];
	} catch (ArrayIndexOutOfBoundsException e) {
	    return LOW;
	}
    }
}
