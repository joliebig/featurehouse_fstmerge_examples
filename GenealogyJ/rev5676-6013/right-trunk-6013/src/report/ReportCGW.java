

import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PropertyPlace;
import genj.report.Report;
import genj.util.swing.Action2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.ImageIcon;



public class ReportCGW extends Report {

    private final static Charset UTF8 = Charset.forName("ISO-8859-1");

    
    public int depPos = 2;
    
    public int cityPos = 1;
    
    public int depLen = 0;

    
    public void start(Gedcom gedcom) {
      start(gedcom, gedcom.getEntities(Gedcom.INDI));
    }

    
    public void start(Indi[] indis)  {
      start(indis[0].getGedcom(), Arrays.asList(indis));
    }

    
    private void start(Gedcom gedcom, Collection indis) {

	
	File dir = getDirectoryFromUser(translate("target.dir"), Action2.TXT_OK);
	if (dir==null)
	    return;

	
	if (!dir.exists()&&!dir.mkdirs()) {
	    println("***Couldn't create output directory "+dir);
	    return;
	}

	
	Map primary = new TreeMap();
	for (Iterator it = indis.iterator(); it.hasNext();)
	    analyze(  (Indi) it.next(), primary);

	
	for (Iterator ps = primary.keySet().iterator(); ps.hasNext(); ) {
	    String p = (String)ps.next();

	try{
	    export (p, primary, dir);
	}catch(IOException ioe){
	    System.err.println("IO Exception!");
	    ioe.printStackTrace();
	}

	    
	}

	
	
    }

    private void export(String dept, Map primary, File dir) throws IOException{
	File file = new File(dir, dept+".csv");
	PrintWriter out = getWriter(new FileOutputStream(file));

	println(translate("DepartmentJur")+" : "+dept);
	Map secondary = (Map)lookup(primary, dept, null);
	for (Iterator ss = secondary.keySet().iterator(); ss.hasNext(); ) {
	    String s = (String)ss.next();

	    Map namelist = (Map)lookup(secondary, s, null);
	    for (Iterator ns = namelist.keySet().iterator(); ns.hasNext(); ) {
		String t = (String)ns.next();
		println("  "+t + " ; " + s);
		out.println(t+" ; "+s);
	    }
	}
	out.close();
    }

    
    private void analyze(Indi indi, Map primary) {

	
	String name = indi.getLastName();
	if (name.length()==0)
	    return;

	
	for (Iterator places = indi.getProperties(PropertyPlace.class).iterator(); places.hasNext(); ) {

	    PropertyPlace place = (PropertyPlace)places.next();

	    String dept = place.getJurisdiction(depPos);
	    if (dept == null)  continue;
	    if (dept.length()==0) continue;
	    int l = Math.min(dept.length(),depLen);
	    if (l > 0) dept = dept.substring(0,l);
	    String jurisdiction = place.getJurisdiction(cityPos);
	    if (jurisdiction.length()==0) jurisdiction = "???";
	    
	    keep(name, jurisdiction, dept, primary);

	}
    }

    private void keep(String name, String place, String dept, Map primary) {

	
	
	Map secondary = (Map)lookup(primary, dept, TreeMap.class);
	Map namelist = (Map)lookup(secondary, place, TreeMap.class);
	lookup(namelist, name, TreeMap.class);
	
    }

    
    private Object lookup(Map index, String key, Class fallback) {
	
	Object result = index.get(key);
	if (result==null) {
	    try {
		result = fallback.newInstance();
	    } catch (Throwable t) {
		t.printStackTrace();
		throw new IllegalArgumentException("can't instantiate fallback "+fallback);
	    }
	    index.put(key, result);
	}
	
	return result;
    }

    
    private PrintWriter getWriter(OutputStream out) {
	return new PrintWriter(new OutputStreamWriter(out, UTF8));
    }


} 
