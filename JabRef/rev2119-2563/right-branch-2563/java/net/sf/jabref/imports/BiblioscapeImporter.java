package net.sf.jabref.imports;

import java.io.InputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.BibtexFields;


public class BiblioscapeImporter extends ImportFormat {

    
    public String getFormatName() {
        return "Biblioscape";
    }

    
    public String getCLIId() {
      return "biblioscape";
    }

    
    public boolean isRecognizedFormat(InputStream in) throws IOException {
        return true;
    }

    
    public List importEntries(InputStream stream) throws IOException {

        ArrayList bibItems = new ArrayList();
        BufferedReader in = new BufferedReader(ImportFormatReader.getReaderDefaultEncoding(stream));
        String line;
        HashMap hm = new HashMap();
        HashMap lines = new HashMap();
        StringBuffer previousLine = null;
        while ((line = in.readLine()) != null){
            if (line.length() == 0) continue; 
                                          
        
        if (line.equals("------")){
          String[] type = new String[2];
          String[] pages = new String[2];
          String country = null;
          String address = null;
          String titleST = null;
          String titleTI = null;
          Vector comments = new Vector();
          
          Object[] l = lines.entrySet().toArray();
          for (int i = 0; i < l.length; ++i){
            Map.Entry entry = (Map.Entry) l[i];
            if (entry.getKey().equals("AU")) hm.put("author", entry.getValue()
                .toString());
            else if (entry.getKey().equals("TI")) titleTI = entry.getValue()
                .toString();
            else if (entry.getKey().equals("ST")) titleST = entry.getValue()
                .toString();
            else if (entry.getKey().equals("YP")) hm.put("year", entry
                .getValue().toString());
            else if (entry.getKey().equals("VL")) hm.put("volume", entry
                .getValue().toString());
            else if (entry.getKey().equals("NB")) hm.put("number", entry
                .getValue().toString());
            else if (entry.getKey().equals("PS")) pages[0] = entry.getValue()
                .toString();
            else if (entry.getKey().equals("PE")) pages[1] = entry.getValue()
                .toString();
            else if (entry.getKey().equals("KW")) hm.put("keywords", entry
                .getValue().toString());
            
            
            
            
            else if (entry.getKey().equals("RT")) type[0] = entry.getValue()
                .toString();
            else if (entry.getKey().equals("SB")) comments.add("Subject: "
                + entry.getValue().toString());
            else if (entry.getKey().equals("SA")) comments
                .add("Secondary Authors: " + entry.getValue().toString());
            else if (entry.getKey().equals("NT")) hm.put("note", entry
                .getValue().toString());
            
            
            else if (entry.getKey().equals("PB")) hm.put("publisher", entry
                .getValue().toString());
            else if (entry.getKey().equals("TA")) comments
                .add("Tertiary Authors: " + entry.getValue().toString());
            else if (entry.getKey().equals("TT")) comments
                .add("Tertiary Title: " + entry.getValue().toString());
            else if (entry.getKey().equals("ED")) hm.put("edition", entry
                .getValue().toString());
            
            
            else if (entry.getKey().equals("TW")) type[1] = entry.getValue()
                .toString();
            else if (entry.getKey().equals("QA")) comments
                .add("Quaternary Authors: " + entry.getValue().toString());
            else if (entry.getKey().equals("QT")) comments
                .add("Quaternary Title: " + entry.getValue().toString());
            else if (entry.getKey().equals("IS")) hm.put("isbn", entry
                .getValue().toString());
            
            
            else if (entry.getKey().equals("AB")) hm.put("abstract", entry
                .getValue().toString());
            
            
            
            
            
            
            
            
            
            
            else if (entry.getKey().equals("AD")) address = entry.getValue()
                .toString();
            else if (entry.getKey().equals("LG")) hm.put("language", entry
                .getValue().toString());
            else if (entry.getKey().equals("CO")) country = entry.getValue()
                .toString();
            else if (entry.getKey().equals("UR") || entry.getKey().equals("AT")){
              String s = entry.getValue().toString().trim();
              hm.put(s.startsWith("http://") || s.startsWith("ftp://") ? "url"
                  : "pdf", entry.getValue().toString());
            }else if (entry.getKey().equals("C1")) comments.add("Custom1: "
                + entry.getValue().toString());
            else if (entry.getKey().equals("C2")) comments.add("Custom2: "
                + entry.getValue().toString());
            else if (entry.getKey().equals("C3")) comments.add("Custom3: "
                + entry.getValue().toString());
            else if (entry.getKey().equals("C4")) comments.add("Custom4: "
                + entry.getValue().toString());
            
            
            
            
            else if (entry.getKey().equals("C5")) comments.add("Custom5: "
                + entry.getValue().toString());
            else if (entry.getKey().equals("C6")) comments.add("Custom6: "
                + entry.getValue().toString());
            
            
            
            
            else if (entry.getKey().equals("DE")) hm.put("annote", entry
                .getValue().toString());
            
            
            
            
            
            
            else if (entry.getKey().equals("CA")) comments.add("Categories: "
                + entry.getValue().toString());
            
            
            else if (entry.getKey().equals("TH")) comments.add("Short Title: "
                + entry.getValue().toString());
            
            
            
            
            else if (entry.getKey().equals("SE")) hm.put("chapter", entry
                .getValue().toString());
            
            
            
            
          }

          String bibtexType = "misc";
          
          for (int i = 1; i >= 0 && bibtexType.equals("misc"); --i){
            if (type[i] == null) continue;
            type[i] = type[i].toLowerCase();
            if (type[i].indexOf("article") >= 0) bibtexType = "article";
            else if (type[i].indexOf("journal") >= 0) bibtexType = "article";
            else if (type[i].indexOf("book section") >= 0) bibtexType = "inbook";
            else if (type[i].indexOf("book") >= 0) bibtexType = "book";
            else if (type[i].indexOf("conference") >= 0) bibtexType = "inproceedings";
            else if (type[i].indexOf("proceedings") >= 0) bibtexType = "inproceedings";
            else if (type[i].indexOf("report") >= 0) bibtexType = "techreport";
            else if (type[i].indexOf("thesis") >= 0
                && type[i].indexOf("master") >= 0) bibtexType = "mastersthesis";
            else if (type[i].indexOf("thesis") >= 0) bibtexType = "phdthesis";
          }

          
          
          if (bibtexType.equals("article")){
            if (titleST != null) hm.put("journal", titleST);
            if (titleTI != null) hm.put("title", titleTI);
          }else if (bibtexType.equals("inbook")){
            if (titleST != null) hm.put("booktitle", titleST);
            if (titleTI != null) hm.put("title", titleTI);
          }else{
            if (titleST != null) hm.put("booktitle", titleST); 
                                                               
                                                               
            if (titleTI != null) hm.put("title", titleTI);
          }

          
          if (pages[0] != null || pages[1] != null) hm.put("pages",
              (pages[0] != null ? pages[0] : "")
                  + (pages[1] != null ? "--" + pages[1] : ""));

          
          if (address != null) hm.put("address", address
              + (country != null ? ", " + country : ""));

          if (comments.size() > 0){ 
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < comments.size(); ++i)
                s.append(i > 0 ? "; " : "").append(comments.elementAt(i).toString());
            hm.put("comment", s.toString());
          }
          BibtexEntry b = new BibtexEntry(BibtexFields.DEFAULT_BIBTEXENTRY_ID,
              Globals.getEntryType(bibtexType));
          b.setField(hm);
          bibItems.add(b);

          hm.clear();
          lines.clear();
          previousLine = null;

          continue;
        }
        
        if (line.startsWith("--") && line.length() >= 7
            && line.substring(4, 7).equals("-- ")){
          lines.put(line.substring(2, 4), previousLine = new StringBuffer(line
              .substring(7)));
          continue;
        }
        
        if (previousLine == null) 
        return null;
        previousLine.append(line.trim());
      }

        return bibItems;
    }

}
