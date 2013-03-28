package net.sourceforge.pmd.util.viewer.model;


import net.sourceforge.pmd.jaxen.Attribute;




public class AttributeToolkit {

    
    public static String formatValueForXPath(Attribute attribute) {
        return '\'' + attribute.getValue() + '\'';
    }

    
    public static String constructPredicate(Attribute attribute) {
        return "[@" + attribute.getName() + '=' +
                formatValueForXPath(attribute) + ']';
    }
}