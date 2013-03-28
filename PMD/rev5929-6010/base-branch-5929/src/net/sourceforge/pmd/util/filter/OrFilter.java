package net.sourceforge.pmd.util.filter;


public class OrFilter<T> extends AbstractCompoundFilter<T> {

	public OrFilter() {
		super();
	}

	public OrFilter(Filter<T>... filters) {
		super(filters);
	}

	public boolean filter(T obj) {
		boolean match = false;
		for (Filter<T> filter : filters) {
			if (filter.filter(obj)) {
				match = true;
				break;
			}
		}
		return match;
	}

	@Override
	protected String getOperator() {
		return "or";
	}
}
