

package net.sf.freecol.client.gui;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;




public class FAFile {


    
    private HashMap<Object, Object> letters = new HashMap<Object, Object>();
    private int maxHeight = 0;

    
    public FAFile(InputStream is) throws IOException {
        load(new CREatingInputStream(is));
    }
    
    
    public Dimension getDimension(String text) {
        FAName fn = getFAName(text);
        if (fn != null) {
            return new Dimension(fn.width, fn.height);
        }

        int width = 0;
        for (int i=0; i<text.length(); i++) {
            FALetter fl = getLetter(text.charAt(i));
            width += fl.advance;
        }

        int firstMinX = Integer.MAX_VALUE;
        FALetter letter = getLetter(text.charAt(0));
        for (int i=0; i<letter.points.length; i++) {
            Point p = letter.points[i];
            if (p.x < firstMinX) {
                firstMinX = p.x;
            }
        }  

        width += firstMinX;
        int lastMaxX = 0;
        letter = getLetter(text.charAt(text.length()-1));

        for (int i=0; i<letter.points.length; i++) {
            Point p = letter.points[i];
            if (p.x > lastMaxX) {
                lastMaxX = p.x;
            }
        }        

        width += lastMaxX;        

        return new Dimension(width, maxHeight);
    }

    
    public Point[] getPoints(String text) {
        FAName fn = getFAName(text);
        if (fn != null) {
            return fn.points;
        }
        ArrayList<Point> points = new ArrayList<Point>();
        int x = 0;
        for (int i=0; i<text.length(); i++) {
            FALetter fl = getLetter(text.charAt(i));
            for (int j=0; j<fl.points.length; j++) {
                Point p = fl.points[j];
                points.add(new Point(p.x + x, p.y));
            }

            x += fl.advance;
        }
        return points.toArray(new Point[0]);
    }

    private void load(InputStream is) throws IOException {
        letters.clear();

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        if (!in.readLine().startsWith("FontAnimationFile")) {
            throw new IllegalStateException("Not a FAF");
        }

        StringTokenizer st = new StringTokenizer(in.readLine());
        maxHeight = Integer.parseInt(st.nextToken());

        String line = in.readLine();
        while (line != null && !line.startsWith("[Chars]")) {
            String name = line;
            st = new StringTokenizer(in.readLine());
            int width = Integer.parseInt(st.nextToken());
            int height = Integer.parseInt(st.nextToken());
            int numberOfPoints = Integer.parseInt(st.nextToken());
            int[] xs = new int[numberOfPoints];
            int[] ys = new int[numberOfPoints];

            line = in.readLine();
            st = new StringTokenizer(line);
            for (int i=0; i<numberOfPoints; i++) {
                xs[i] = Integer.parseInt(st.nextToken());               
            }

            line = in.readLine();
            st = new StringTokenizer(line);         
            for (int i=0; i<numberOfPoints; i++) {
                ys[i] = Integer.parseInt(st.nextToken());               
            }   

            FAName newLetter = new FAName();
            newLetter.name = name;
            newLetter.width = width;
            newLetter.height = height;
            newLetter.points = new Point[numberOfPoints];
            for (int i=0; i<numberOfPoints; i++) {
                newLetter.points[i] = new Point(xs[i], ys[i]);                
            }                       
            letters.put(name, newLetter);
            line = in.readLine();
        }

        line = in.readLine();
        while (line != null) {
            st = new StringTokenizer(line.substring(1));
            char letter = line.charAt(0);
            int advance = Integer.parseInt(st.nextToken());
            int numberOfPoints = Integer.parseInt(st.nextToken());
            int[] xs = new int[numberOfPoints];
            int[] ys = new int[numberOfPoints];
            line = in.readLine();
            st = new StringTokenizer(line);
            for (int i=0; i<numberOfPoints; i++) {
                xs[i] = Integer.parseInt(st.nextToken());               
            }

            line = in.readLine();
            st = new StringTokenizer(line);         
            for (int i=0; i<numberOfPoints; i++) {
                ys[i] = Integer.parseInt(st.nextToken());               
            }   

            FALetter newLetter = new FALetter();
            newLetter.letter = letter;
            newLetter.advance = advance;
            newLetter.points = new Point[numberOfPoints];
            for (int i=0; i<numberOfPoints; i++) {
                newLetter.points[i] = new Point(xs[i], ys[i]);                
            }           
            letters.put(new Character(letter), newLetter);
            line = in.readLine();
        }
    }


    private FALetter getLetter(char letter) {
        return (FALetter) letters.get(new Character(letter));
    }

    private FAName getFAName(String name) {
        return (FAName) letters.get(name);
    }

    private static class FALetter {
        public char letter;
        public Point[] points;
        public int advance;
    }

    private static class FAName {
        public String name;
        public Point[] points;
        public int width;
        public int height;
    }

    
    private static class CREatingInputStream extends InputStream {
        
        CREatingInputStream(InputStream in) {
            this.in = in;
        }
        
        
        @Override
        public int read() throws IOException {
            int c;
            do {
                c = this.in.read();
            } while(c == '\r');
            return c;
        }
        
        private final InputStream in;
    }
}

