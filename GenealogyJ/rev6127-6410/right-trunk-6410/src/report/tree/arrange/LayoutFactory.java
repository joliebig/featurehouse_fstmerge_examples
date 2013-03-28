

package tree.arrange;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tree.filter.FilterChain;
import tree.filter.SameHeightSpouses;
import tree.filter.TreeFilter;
import tree.output.HorizontalLines;


public class LayoutFactory {
    
    public static final int SPACING = 10;

    
    public int arrangement = 0;

    public String[] arrangements;

    private Map<String, TreeFilter> layouts = new LinkedHashMap<String, TreeFilter>();
    private List<TreeFilter> layoutList = new ArrayList<TreeFilter>();

    
    public LayoutFactory()
    {
        add("center", getLayout(new CenteredArranger(SPACING)));
        add("left", getLayout(new AlignLeftArranger(SPACING)));
    }

    public TreeFilter createLayout()
    {
        return layoutList.get(arrangement);
    }

    private void add(String name, TreeFilter layout)
    {
        layouts.put(name, layout);
        layoutList.add(layout);
        arrangements = layouts.keySet().toArray(new String[0]);
    }

    private TreeFilter getLayout(TreeFilter layout)
    {
        return new FilterChain(new TreeFilter[] {
                layout,
                new SameHeightSpouses(),
                new HorizontalLines(SPACING)
            });
    }

}
