package net.sourceforge.pmd.util.filter;


public abstract class AbstractDelegateFilter<T> implements Filter<T> {
	protected Filter<T> filter;

	public AbstractDelegateFilter() {
	}

	public AbstractDelegateFilter(Filter<T> filter) {
		this.filter = filter;
	}

	public Filter<T> getFilter() {
		return filter;
	}

	public void setFilter(Filter<T> filter) {
		this.filter = filter;
	}

	
	public boolean filter(T obj) {
		return filter.filter(obj);
	}

	
	public String toString() {
		return filter.toString();
	}
}
