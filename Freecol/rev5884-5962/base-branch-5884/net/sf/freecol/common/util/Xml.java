


package net.sf.freecol.common.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



public final class Xml
{


    

    public static Document newDocument() {

        try {
            DocumentBuilderFactory  factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder  builder = factory.newDocumentBuilder();
            return builder.newDocument();
        }
        catch ( ParserConfigurationException e ) {
            throw new Exception( e );
        }
    }


    public static Document documentFrom( String string ) {

        return documentFrom( new InputSource(new StringReader(string)) );
    }


    public static Document documentFrom( InputStream stream ) {

        return documentFrom( new InputSource(stream) );
    }


    public static String toString( Document document ) {

        return document.getDocumentElement().toString();
    }


    public static boolean hasAttribute( Node xmlElement, String attributeName ) {

        return xmlElement.getAttributes().getNamedItem(attributeName) != null;
    }


    public static String attribute( Node xmlElement, String attributeName ) {

        return xmlElement.getAttributes().getNamedItem(attributeName).getNodeValue();
    }

    public static String attribute( Node xmlElement, String attributeName, String otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return attribute(xmlElement, attributeName);
        } else {
            return otherwise;
        }
    }

    public static String[] arrayAttribute( Node xmlElement, String attributeName, String separator ) {

        return attribute(xmlElement, attributeName).split(separator);
    }

    public static String[] arrayAttribute( Node xmlElement, String attributeName ) {

        return arrayAttribute(xmlElement, attributeName, ",");
    }

    public static String[] arrayAttribute( Node xmlElement, String attributeName, String[] otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return arrayAttribute(xmlElement, attributeName, ",");
        } else {
            return otherwise;
        }
    }

    public static char charAttribute( Node xmlElement, String attributeName ) {

        return attribute(xmlElement, attributeName).charAt(0);
    }

    public static char charAttribute( Node xmlElement, String attributeName, char otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return charAttribute(xmlElement, attributeName);
        } else {
            return otherwise;
        }
    }

    

    public static float floatAttribute( Node xmlElement, String attributeName ) {

        return Float.parseFloat( attribute(xmlElement, attributeName) );
    }

    public static float floatAttribute( Node xmlElement, String attributeName, float otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return floatAttribute(xmlElement, attributeName);
        } else {
            return otherwise;
        }
    }

    public static float[] floatArrayAttribute( Node xmlElement, String attributeName, String separator ) {
        String[] array = arrayAttribute(xmlElement, attributeName, separator);
        float[] output = new float[array.length];
        for (int i = 0; i < array.length ; i++) {
            output[i] = Float.parseFloat(array[i]);
        }
        return output;
    }
    
    public static float[] floatArrayAttribute( Node xmlElement, String attributeName ) {

        return floatArrayAttribute(xmlElement, attributeName, ",");
    }

    public static float[] floatArrayAttribute( Node xmlElement, String attributeName, float[] otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return floatArrayAttribute(xmlElement, attributeName, ",");
        } else {
            return otherwise;
        }
    }


    public static int intAttribute( Node xmlElement, String attributeName ) {

        return Integer.parseInt( attribute(xmlElement, attributeName) );
    }

    public static int intAttribute( Node xmlElement, String attributeName, int otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return intAttribute(xmlElement, attributeName);
        } else {
            return otherwise;
        }
    }

    public static int[] intArrayAttribute( Node xmlElement, String attributeName, String separator ) {
        String[] array = arrayAttribute(xmlElement, attributeName, separator);
        
        int[] output = new int[array.length];
        for (int i = 0; i < array.length ; i++) {
            output[i] = Integer.parseInt(array[i]);
        }
        return output;
    }
    
    public static int[] intArrayAttribute( Node xmlElement, String attributeName ) {

        return intArrayAttribute(xmlElement, attributeName, ",");
    }

    public static int[] intArrayAttribute( Node xmlElement, String attributeName, int[] otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return intArrayAttribute(xmlElement, attributeName, ",");
        } else {
            return otherwise;
        }
    }

    public static boolean booleanAttribute( Node xmlElement, String attributeName ) {

        return parseTruth( attribute(xmlElement, attributeName) );
    }

    public static boolean booleanAttribute( Node xmlElement, String attributeName, boolean otherwise ) {
        if (hasAttribute(xmlElement, attributeName)) {
            return booleanAttribute(xmlElement, attributeName);
        } else {
            return otherwise;
        }
    }

    public static void forEachChild( Node xml, Method method ) {

        NodeList  childList = xml.getChildNodes();

        for ( int ci = 0, nc = childList.getLength();  ci < nc;  ci ++ ) {
            Node  child = childList.item( ci );

            if ( child instanceof Element ) {

                method.invokeOn( child );
            }
        }
    }


    

    private static Document documentFrom( InputSource source ) {

        DocumentBuilderFactory  factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder  builder = factory.newDocumentBuilder();
            return builder.parse( source );
        }
        catch ( ParserConfigurationException e ) {
            throw new Exception( e );
        }
        catch ( SAXException e ) {
            throw new Exception( e );
        }
        catch ( IOException e ) {
            throw new Exception( e );
        }
    }


    private static boolean parseTruth( String truthAsString )
    {
        if ( "yes".equals(truthAsString) ||
             "true".equals(truthAsString) ) {
            return true;
        }
        else if ( "no".equals(truthAsString) ) {
            return false;
        }
        throw new RuntimeException( "mus be 'yes' or 'no': " + truthAsString );
    }


    

    private Xml() {
    }


    

    public interface Method {

        public void invokeOn( Node xml );
    }


    
    public static final class Exception extends RuntimeException {

        Exception( Throwable cause ) {

            super( cause );
        }
    }

}
