

package tree.output;

import java.util.HashMap;
import java.util.Map;

import tree.IndiBox;
import tree.filter.TreeFilter;
import tree.filter.TreeFilterBase;


public class HorizontalLines implements TreeFilter {

    
    private int levelMin = 0;

    
    private int levelMax = 0;

    
    private Map<Integer, Integer> levelHeight = new HashMap<Integer, Integer>();

    
    private Map<Integer, Integer> levelCoord = new HashMap<Integer, Integer>();

    private int spacing;

    public HorizontalLines(int spacing)
    {
        this.spacing = spacing;
    }

    public void filter(IndiBox indibox)
    {
        
        levelMin = 0;
        levelMax = 0;
        levelHeight.clear();
        levelCoord.clear();

        
        new DetermineLevelHeight().filter(indibox);

        
        int yCoord = 0;
        for (int i = levelMin; i <= levelMax; i++) {
            levelCoord.put(i, yCoord);
            yCoord += levelHeight.get(i);
        }
        levelCoord.put(levelMax + 1, yCoord);

        
        new AssignCoordinates().filter(indibox);
	}

    
    private int getYCoord(int level) {
        return levelCoord.get(level);
    }

    
    private class DetermineLevelHeight extends TreeFilterBase {

        
        private int level = 0;

        protected void preFilter(IndiBox indibox) {
            if (indibox.prev != null)
                level += indibox.y;

            if (level > levelMax)
                levelMax = level;
            if (level < levelMin)
                levelMin = level;

            Integer height = (Integer)levelHeight.get(level);
            if (height == null)
                height = 0;
            int newHeight = indibox.height + spacing * 2;
            if (indibox.family != null)
                newHeight += indibox.family.height;
            if (newHeight > height)
                levelHeight.put(level, newHeight);
        }

        protected void postFilter(IndiBox indibox) {
            if (indibox.prev != null)
                level -= indibox.y;
        }
    }

    
    private class AssignCoordinates extends TreeFilterBase {

        
        private int level = 0;

        protected void preFilter(IndiBox indibox) {
            if (indibox.prev != null)
                level += indibox.y;
        }

        protected void postFilter(IndiBox indibox) {
            int thisLevel = level;
            if (indibox.prev != null) {
                level -= indibox.y;
                indibox.y = getYCoord(thisLevel) - getYCoord(thisLevel - indibox.y);
            }
            indibox.hPlus = getYCoord(thisLevel + indibox.hPlus) - getYCoord(thisLevel);
            indibox.hMinus = getYCoord(thisLevel) - getYCoord(thisLevel - indibox.hMinus);
        }
    }
}
