

package tree.filter;

import tree.IndiBox;



public class FilterChain implements TreeFilter {

    private TreeFilter[] filters;

    public FilterChain(TreeFilter[] filters)
    {
        this.filters = filters;
    }

	public void filter(IndiBox indibox)
    {
        for (int i = 0; i < filters.length; i++)
            filters[i].filter(indibox);
    }
}
