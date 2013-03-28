package net.sourceforge.pmd.util.filter;


public class AndFilter<T> extends AbstractCompoundFilter<T> {

	public AndFilter() {
		super();
	}

	public AndFilter(Filter<T>... filters) {
		super(filters);
	}

	public boolean filter(T obj) {
		boolean match = true;
		for (Filter<T> filter : filters) {
			if (!filter.filter(obj)) {
				match = false;
				break;
			}
		}
		return match;
	}

	@Override
	protected String getOperator() {
		return "and";
	}
}
