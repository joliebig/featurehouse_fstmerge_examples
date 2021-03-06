package net.sf.jabref.imports;

import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.OutputPrinter;
import net.sf.jabref.Util;
import net.sf.jabref.journals.JournalAbbreviations;


public class IEEEXploreFetcher implements EntryFetcher {

    ImportInspector dialog = null;
    OutputPrinter status;
    HTMLConverter htmlConverter = new HTMLConverter();
    JournalAbbreviations journalAbbrev = new JournalAbbreviations("/resource/IEEEJournalList.txt");
    private String terms;
    String startUrl = "http://ieeexplore.ieee.org";
    String searchUrlPart = "/search/freesearchresult.jsp?queryText=%28";
    String endUrl = "%29+%3Cin%3E+metadata&ResultCount=25&ResultStart=";
    String risUrl = "http://ieeexplore.ieee.org/xpls/citationAct";
    private int perPage = 25, hits = 0, unparseable = 0, parsed = 0;
    private boolean shouldContinue = false;
    private JCheckBox fetchAstracts = new JCheckBox(Globals.lang("Include abstracts"), false);
    private boolean fetchingAbstracts = false;
    private boolean fetchRis = true;
    private static final int MAX_RIS_FETCH = 25;

    
    Pattern hitsPattern = Pattern.compile(".*Your search matched <strong>(\\d+)</strong>.*");
    Pattern maxHitsPattern = Pattern.compile(".*A maximum of <strong>(\\d+)</strong>.*");
    Pattern paperEntryPattern = Pattern.compile(".*<strong>(.+)</strong><br>"
                + "\\s+(.+)<br>"
                + "\\s+<A href=.+>(.+)</A><br>"
                + "\\s+(.+)\\s+(.+)\\s+(.+)\\s+(.+).*");
    Pattern stdEntryPattern = Pattern.compile(".*<strong>(.+)</strong><br>"
                + "\\s+(.+)");
    Pattern volumePattern = Pattern.compile(".*Volume (\\d+),&nbsp;(.+)");
    Pattern numberPattern = Pattern.compile(".*Issue (\\d+)</a>,&nbsp;(.+)");
    Pattern partPattern = Pattern.compile(".*Part (\\d+),&nbsp;(.+)");
    Pattern datePattern = Pattern.compile("(.*)\\s?(\\d{4}).*");
    Pattern publicationPattern = Pattern.compile("(.*), \\d*\\.*\\s?(.*)");
    Pattern proceedingPattern = Pattern.compile("(.*?)\\.?\\s?Proceedings\\s?(.*)");
    Pattern conferencePattern = Pattern.compile("(.*)\\.\\s?(.*)");

    String abbrvPattern = ".*[^,] '?\\d+\\)?";
    Pattern acceptedPatterns = Pattern.compile("(.*) : (Accepted.*)");

    Pattern abstractLinkPattern = Pattern.compile(
            "<a href=\"(.+)\" class=\"bodyCopySpaced\">Abstract</a>");

    Pattern ieeeArticleNumberPattern =
        Pattern.compile("<a href=\".*arnumber=(\\d+).*\">");

    public JPanel getOptionsPanel() {
        JPanel pan = new JPanel();
        pan.setLayout(new BorderLayout());
        pan.add(fetchAstracts, BorderLayout.CENTER);
        return pan;
    }

    public boolean processQuery(String query, ImportInspector dialog, OutputPrinter status) {
        this.dialog = dialog;
        this.status = status;
        this.terms = query;
        piv = 0;
        
        shouldContinue = true;
        parsed = 0;
        unparseable = 0;
        String address = makeUrl(0);
        try {
            URL url = new URL(address);
            
            
            
            

            
            String page = getResults(url);
            

            
            if (page.indexOf("You have entered an invalid search") >= 0) {
                status.showMessage(Globals.lang("You have entered an invalid search '%0'.",
                        terms),
                        Globals.lang("Search IEEEXplore"), JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            
            if (page.indexOf("No results") >= 0) {
                status.showMessage(Globals.lang("No entries found for the search string '%0'",
                        terms),
                        Globals.lang("Search IEEEXplore"), JOptionPane.INFORMATION_MESSAGE);
                return false;
            }
            hits = getNumberOfHits(page, "Your search matched", hitsPattern);

            int maxHits = getNumberOfHits(page, "A maximum of", maxHitsPattern);
            

            if (hits > maxHits)
                hits = maxHits;
            
            fetchingAbstracts = fetchAstracts.isSelected();
            
            if (hits > MAX_RIS_FETCH) {
                if (fetchingAbstracts == true) {
                    status.showMessage(
                        Globals.lang("%0 entries found. To reduce server load, "
                        +"only %1 will be downloaded.",
                                new String[] {String.valueOf(hits), String.valueOf(MAX_RIS_FETCH)}),
                        Globals.lang("Search IEEEXplore"), JOptionPane.INFORMATION_MESSAGE);
                    fetchRis = true;
                    hits = MAX_RIS_FETCH;
                } else {
                    fetchRis = false;
                }
            }

            parse(dialog, page, 0, 1);
            int firstEntry = perPage;
            while (shouldContinue && (firstEntry < hits)) {
                address = makeUrl(firstEntry);
                page = getResults(new URL(address));

                if (!shouldContinue)
                    break;

                parse(dialog, page, 0, 1+firstEntry);
                firstEntry += perPage;

            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ConnectException e) {
            status.showMessage(Globals.lang("Connection to IEEEXplore failed"),
                    Globals.lang("Search IEEExplore"), JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
        
    public String getTitle() {
        return Globals.menuTitle("Search IEEEXplore");
    }

    public URL getIcon() {
        return GUIGlobals.getIconUrl("www");
    }

    public String getHelpPage() {
        return "IEEEXploreHelp.html";
    }

    public String getKeyName() {
        return "Search IEEEXplore";
    }

    
    public void stopFetching() {
        shouldContinue = false;
    }

    private String makeUrl(int startIndex) {
        StringBuffer sb = new StringBuffer(startUrl).append(searchUrlPart);
        sb.append(terms.replaceAll(" ", "+"));
        sb.append(endUrl);
        sb.append(String.valueOf(startIndex));
        return sb.toString();
    }

    int piv = 0;

    private void parse(ImportInspector dialog, String text, int startIndex, int firstEntryNumber) {
        piv = startIndex;
        int entryNumber = firstEntryNumber;
        
        BibtexEntry entry;
        while (((entry = parseNextEntry(text, piv, entryNumber)) != null)
            && (shouldContinue)) {
            if (entry.getField("title") != null) {
                dialog.addEntry(entry);
                dialog.setProgress(parsed+unparseable, hits);
                parsed++;
            }
            entryNumber++;
        }
    }

    private BibtexEntry parseEntryRis(String number, boolean abs, boolean isStandard)
        throws IOException
    {
        URL url;
        URLConnection conn;
        try {
            url = new URL(risUrl);
            conn = url.openConnection();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        PrintWriter out = new PrintWriter(
                conn.getOutputStream());
        String cite = "cite";
        if (abs == true)
            cite = "cite_abs";
        out.write(
                "fileFormate=ris&dlSelect="+cite+"&arnumber="+
                URLEncoder.encode(
                    "<arnumber>"+number+"</arnumber>",
                    "UTF-8"));
        out.flush();
        out.close();
        InputStream inp = conn.getInputStream();
        List<BibtexEntry> items = new RisImporter().importEntries(inp);
        inp.close();
        if (items.size() > 0) {
            
            BibtexEntry entry = items.get(0);
            if (isStandard == true) {
                entry.setType(BibtexEntryType.getType("standard"));
                entry.setField("organization", "IEEE");
                String stdNumber = entry.getField("journal");
                String[] parts = stdNumber.split("Std ");
                if (parts.length == 2) {
                    stdNumber = parts[1];
                    parts = stdNumber.split(", ");
                    if (parts.length == 2) {
                        stdNumber = parts[0];
                        String date = parts[1];
                        parts = date.split(" ");
                        if (parts.length == 2) {
                            entry.setField("month", parts[0]);
                        }
                    }
                    entry.setField("number", stdNumber);
                }
                entry.clearField("journal");
                entry.clearField("booktitle");
                
                String title = entry.getField("title");
                entry.setField("title", title);
            }
            return entry;
        } else
            return null;
    }

    private BibtexEntry cleanup(BibtexEntry entry) {
        if (entry == null)
            return null;
        if (entry.getType().getName() == "Standard")
            return entry;
        
        String month = entry.getField("month");
        
        entry.setField("month", month);
        
        
        BibtexEntryType type = entry.getType();
        String sourceField;
        if (type.getName() == "Article") {
            sourceField = "journal";
            entry.clearField("booktitle");
        } else {
            sourceField = "booktitle";
        }
        String fullName = entry.getField(sourceField);
        if (fullName == null) {
            System.err.println("Null publication");
            return null;
        }
        
        
        
        if (type.getName() == "Article") {
            String[] parts = fullName.split("[\\[\\]]"); 
            fullName = parts[0];
            if (parts.length == 3) {
                fullName += parts[2];
            }
        } else {
            fullName = fullName.replace("Conference Proceedings", "Proceedings").replace("Proceedings of", "Proceedings").replace("  ", ". ").replace("Proceedings.", "Proceedings");
        }
        
        
        
        Matcher m1 = publicationPattern.matcher(fullName);
        if (m1.find()) {
            String prefix = m1.group(2).trim();
            String postfix = m1.group(1).trim();
            String abbrv = "";
            String[] parts = prefix.split("\\. ", 2);
            if (parts.length == 2) {
                if (parts[0].matches(abbrvPattern)) {
                    prefix = parts[1];
                    abbrv = parts[0];
                } else {
                    prefix = parts[0];
                    abbrv = parts[1];
                }
        
            }
            if (prefix.matches(abbrvPattern) == false) {
                fullName = prefix + " " + postfix + " " + abbrv;
                fullName = fullName.trim();
            } else {
                fullName = postfix + " " + prefix;
            }

    
        
            
                
            
        }
        
        if (type.getName() == "Article") {
            fullName = fullName.replace("- ", "-"); 
            Matcher m2 = acceptedPatterns.matcher(fullName);
            if (m2.find()) {
                fullName = m2.group(1);
                entry.setField("note", m2.group(2));
            }
            fullName = fullName.trim();
            String id = journalAbbrev.getAbbreviatedName(fullName, false);
            if (id != null)
                fullName = id;
        } else {
            
            Matcher m2 = proceedingPattern.matcher(fullName);
            if (m2.find()) {
                String prefix = m2.group(2); 
                String postfix = m2.group(1).replaceAll("\\.$", "");
                if (prefix.matches(abbrvPattern) == false) {
                    String abbrv = "";
                
                    String[] parts = postfix.split("\\. ", 2);
                    if (parts.length == 2) {
                        if (parts[0].matches(abbrvPattern)) {
                            postfix = parts[1];
                            abbrv = parts[0];
                        } else {
                            postfix = parts[0];
                            abbrv = parts[1];
                        }
                        
                    }
                    fullName = prefix.trim() + " " + postfix.trim() + " " + abbrv;
                    
                } else {
                    fullName = postfix.trim() + " " + prefix.trim();
                }
                
            }
            
            
            
            fullName = fullName.trim();
            
            fullName = fullName.replaceAll("^[tT]he ", "").replaceAll("^\\d{4} ", "").replaceAll("[,.]$", "");
            String year = entry.getField("year");
            fullName = fullName.replaceAll(", " + year + "\\.?", "");
            
            
            if (fullName.contains("Abstract") == false && fullName.contains("Summaries") == false && fullName.contains("Conference Record") == false)
                fullName = "Proc. " + fullName;
            
        }
        
        entry.setField(sourceField, fullName);
        return entry;
    }

    private BibtexEntry parseNextEntry(String allText, int startIndex, int entryNumber)
    {
        BibtexEntry entry = null;
        String toFind = new StringBuffer().append("<div align=\"left\"><strong>")
                .append(entryNumber).append(".</strong></div>").toString();
        int index = allText.indexOf(toFind, startIndex);
        int endIndex = allText.indexOf("</table>", index+1);
        if (endIndex < 0)
            endIndex = allText.length();

        if (index >= 0) {
            piv = index+1;
            String text = allText.substring(index, endIndex);
            
            BibtexEntryType type = BibtexEntryType.getType("article");
            String sourceField = null;
            if (text.indexOf("JNL") >= 0) {
                type = BibtexEntryType.getType("article");
                sourceField = "journal";
            } else if (text.indexOf("CNF") >= 0){
                type = BibtexEntryType.getType("inproceedings");
                sourceField = "booktitle";
            } else if (text.indexOf("STD") >= 0) {
                type = BibtexEntryType.getType("Standard");
            } else {
                System.err.println("Type detection failed.");
            }
            if (fetchRis == true) {
                Matcher number =
                    ieeeArticleNumberPattern.matcher(text);
                if (number.find()) {
                    try {
                        entry = parseEntryRis(number.group(1), fetchingAbstracts, type.getName() == "Standard");
                    } catch (IOException e) {
                        e.printStackTrace();  
                    }
                }
                if (entry != null) { 
                    
                    int pgInd = text.indexOf("Digital Object Identifier ");
                    if (pgInd >= 0) {
                        int fieldEnd = text.indexOf("<br>", pgInd);
                        if (fieldEnd >= 0) {
                            entry.setField("doi",
                                text.substring(pgInd+26, fieldEnd).trim());
                        }
                    }
                    return cleanup(entry);
                }
            }

            index = 0;
            entry = new BibtexEntry(Util.createNeutralId(), type);
            
            
            if (type.getName() == "Standard") {
                Matcher mstd = stdEntryPattern.matcher(text);
                if (mstd.find()) {
                    entry.setField("title", convertHTMLChars(mstd.group(1)));
                    entry.setField("year", convertHTMLChars(mstd.group(2)));
                    entry.setField("organization", "IEEE");
                    return entry;
                }
                System.err.println("Standard entry parsing failed.");
            }
            Matcher m = paperEntryPattern.matcher(text);
            String tmp;
            String rest = "";
            if (m.find()) {
                
                entry.setField("title", convertHTMLChars(m.group(1)));
                
                tmp = convertHTMLChars(m.group(2));
                if (tmp.charAt(tmp.length()-1) == ';')
                    tmp= tmp.substring(0, tmp.length()-1);
                entry.setField("author", tmp.replaceAll("; ", " and "));
                
                tmp = m.group(3);
                String fullName = convertHTMLChars(tmp);
                entry.setField(sourceField, fullName);
                
                String misc = m.group(4);
                for (int i = 5; i < 8; i++) {
                    tmp = m.group(i);
                    if (tmp.startsWith("Page") == false)
                        misc += tmp; 
                    else
                        break;
                }
                
                Matcher ms1 = volumePattern.matcher(misc);
                if (ms1.find()) {
                    
                    entry.setField("volume", convertHTMLChars(ms1.group(1)));
                    misc = ms1.group(2);
                }
                
                Matcher ms2 = numberPattern.matcher(misc);
                if (ms2.find()) {
                    
                    entry.setField("number", convertHTMLChars(ms2.group(1)));
                    misc = ms2.group(2);
                }
                
                Matcher ms3 = partPattern.matcher(misc);
                if (ms3.find()) {
                    entry.setField("part", ms3.group(1));
                    misc = ms3.group(2);
                }
                
                Matcher ms4 = datePattern.matcher(misc);
                if (ms4.find()) {
                    
                    entry.setField("month", convertHTMLChars(ms4.group(1)).replaceAll("-", "--"));
                    
                    entry.setField("year", ms4.group(2));
                } else {
                      Matcher ms5 = datePattern.matcher(fullName);
                    if (ms5.find()) {
                        entry.setField("year", ms5.group(2));
                    }
                }
            } else {
                System.err.println("---no structure match---");
                System.err.println(text);
                unparseable++;
                return null;
            }
            int pgInd = text.indexOf("Page(s):");
            if (pgInd >= 0) {
                
                rest = text.substring(pgInd+8);
                pgInd = rest.indexOf("<br>");
                if (pgInd >= 0) {
                    tmp = rest.substring(0, pgInd);
                    pgInd = tmp.indexOf("vol");
                    if (pgInd >= 0)
                        tmp = tmp.substring(0,pgInd);
                    pgInd = tmp.indexOf("Vol");
                    if (pgInd >= 0)
                        tmp = tmp.substring(0,pgInd);
                    entry.setField("pages", tmp.replaceAll("\\s+", "").replaceAll("-","--"));
                }
                
                pgInd = rest.indexOf("Digital Object Identifier ", pgInd);
                if (pgInd >= 0) {
                    int fieldEnd = rest.indexOf("<br>", pgInd);
                    if (fieldEnd >= 0) {
                        entry.setField("doi", rest.substring(pgInd+26, fieldEnd).trim());
                    }
                }
            }


            return cleanup(entry);
        }
        return null;
    }

    
    private String convertHTMLChars(String text) {

        return htmlConverter.format(text);
    }


    
    private int getNumberOfHits(String page, String marker, Pattern pattern) throws IOException {
        int ind = page.indexOf(marker);
        if (ind < 0)
            throw new IOException(Globals.lang("Could not parse number of hits"));
        String substring = page.substring(ind, Math.min(ind+42, page.length()));
        Matcher m = pattern.matcher(substring);
        if (!m.find())
            return 0;
        if (m.groupCount() >= 1) {
            try {
                return Integer.parseInt(m.group(1));
            } catch (NumberFormatException ex) {
                throw new IOException(Globals.lang("Could not parse number of hits"));
            }
        }
        throw new IOException(Globals.lang("Could not parse number of hits"));
    }

    
    public String getResults(URL source) throws IOException {
        
        InputStream in = source.openStream();
        StringBuffer sb = new StringBuffer();
        byte[] buffer = new byte[256];
        while(true) {
            int bytesRead = in.read(buffer);
            if(bytesRead == -1) break;
            for (int i=0; i<bytesRead; i++)
                sb.append((char)buffer[i]);
        }
        return sb.toString();
    }

    
    public String getResultsFromFile(File f) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(f));
        StringBuffer sb = new StringBuffer();
        byte[] buffer = new byte[256];
        while(true) {
            int bytesRead = in.read(buffer);
            if(bytesRead == -1) break;
            for (int i=0; i<bytesRead; i++)
                sb.append((char)buffer[i]);
        }
        return sb.toString();
    }

    
    public String fetchAbstract(String link) throws IOException {
        URL url = new URL(link);
        String page = getResults(url);

        String marker = "Abstract</span><br>";
        int index = page.indexOf(marker);
        int endIndex = page.indexOf("</td>", index + 1);
        if ((index >= 0) && (endIndex > index)) {
            return new String(page.substring(index + marker.length(), endIndex).trim());
        }

        return null;
    }

}
