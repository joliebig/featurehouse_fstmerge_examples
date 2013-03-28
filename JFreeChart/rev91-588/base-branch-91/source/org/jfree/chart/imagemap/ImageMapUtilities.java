

package org.jfree.chart.imagemap;

import java.io.IOException;
import java.io.PrintWriter;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.util.StringUtils;


public class ImageMapUtilities {

    
    public static void writeImageMap(PrintWriter writer, String name, 
                                     ChartRenderingInfo info)
        throws IOException {

        
        ImageMapUtilities.writeImageMap(writer, name, info,
                new StandardToolTipTagFragmentGenerator(),
                new StandardURLTagFragmentGenerator());

    }

    
    public static void writeImageMap(PrintWriter writer,
                                     String name,
                                     ChartRenderingInfo info,
                                     boolean useOverLibForToolTips) 
        throws IOException {

        ToolTipTagFragmentGenerator toolTipTagFragmentGenerator = null;
        if (useOverLibForToolTips) {
            toolTipTagFragmentGenerator 
                    = new OverLIBToolTipTagFragmentGenerator();
        }
        else {
            toolTipTagFragmentGenerator 
                    = new StandardToolTipTagFragmentGenerator();
        }
        ImageMapUtilities.writeImageMap(writer, name, info, 
                toolTipTagFragmentGenerator, 
                new StandardURLTagFragmentGenerator());

    }

    
    public static void writeImageMap(PrintWriter writer, String name, 
            ChartRenderingInfo info,
            ToolTipTagFragmentGenerator toolTipTagFragmentGenerator,
            URLTagFragmentGenerator urlTagFragmentGenerator) 
        throws IOException {

        writer.println(ImageMapUtilities.getImageMap(name, info, 
                toolTipTagFragmentGenerator, urlTagFragmentGenerator));
    }

    
    public static String getImageMap(String name, ChartRenderingInfo info) {
        return ImageMapUtilities.getImageMap(name, info,
                new StandardToolTipTagFragmentGenerator(),
                new StandardURLTagFragmentGenerator());
    }

    
    public static String getImageMap(String name, ChartRenderingInfo info,
            ToolTipTagFragmentGenerator toolTipTagFragmentGenerator,
            URLTagFragmentGenerator urlTagFragmentGenerator) {

        StringBuffer sb = new StringBuffer();
        sb.append("<map id=\"" + name + "\" name=\"" + name + "\">");
        sb.append(StringUtils.getLineSeparator());
        EntityCollection entities = info.getEntityCollection();
        if (entities != null) {
            int count = entities.getEntityCount();
            for (int i = count - 1; i >= 0; i--) {
                ChartEntity entity = entities.getEntity(i);
                if (entity.getToolTipText() != null 
                        || entity.getURLText() != null) {
                    String area = entity.getImageMapAreaTag(
                            toolTipTagFragmentGenerator, 
                            urlTagFragmentGenerator);
                    if (area.length() > 0) {
                        sb.append(area);
                        sb.append(StringUtils.getLineSeparator());
                    }
                }
            }
        }
        sb.append("</map>");
        return sb.toString();
        
    }

}
