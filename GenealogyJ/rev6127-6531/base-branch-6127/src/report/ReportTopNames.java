import java.util.ArrayList;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.time.PointInTime;
import genj.report.Report;


public class ReportTopNames extends Report {
	 
	public boolean showAllNames = true;
	
	
	private String textTitle = translate("title");
	private String textFileName = translate("filename");
	private String textDate = translate("date");
	private String textGivenNames = translate("givennames");
	private String textSurnames = translate("surnames");
	

	
	
	
	public void start(Gedcom gedcom) {
		  	
		
		displayHeader(gedcom.getName());
		outputReport(gedcom);
		 
	}
	
	  
	
	
	public void displayHeader(String strSubject) {
  
		  int loop;
		  String strULine="";
			
		  
		  println(textTitle);
		  for(loop=0 ;loop<textTitle.length(); loop++)
			  strULine += "=";
		  println(strULine);
		  println(); 
		  println(textFileName + ": " + strSubject);
		  println(textDate + ": " + PointInTime.getNow().toString());
		  println();
		  
	}	
	
	
	
	
	public void outputReport(Gedcom gedcom) {
  
		class objRec{
			
			public objRec(String str, int i) {
				strName = str;
				count = i;
			}
			
			public void inccounter() {
				count +=1;	
			}
			
			String strName;
			int count;	
		}
		
		
		ArrayList<objRec> alGiven = new ArrayList<objRec>();
		ArrayList<objRec> alSurname = new ArrayList<objRec>();
		objRec objTemp;
		Indi person;
		Entity[] individuals;
		int loop, loop2, firstSpace, position, maxNames, numIndis;
		String strGiven, strSurname, strOutput, strULine;
		boolean flagFound, flagSwapped;
		float percent;
	
		
		individuals = gedcom.getEntities(Gedcom.INDI,"");
		
		numIndis = individuals.length;
		
		for(loop=0; loop<individuals.length; loop++) {
	        
			
			person = (Indi)individuals[loop];      
			strGiven = person.getFirstName();
			strSurname = person.getLastName();
			
			
			firstSpace = strGiven.indexOf(" ");
			if(firstSpace !=-1) strGiven = strGiven.substring(0, firstSpace);


			
			
			
			if(alGiven.isEmpty()) {
				objTemp = new objRec(strGiven, 1);
				alGiven.add(objTemp);
			} else {
	 
				flagFound=false;
				
				for(loop2=0; loop2<alGiven.size(); loop2++) {
					
					if(alGiven.get(loop2).strName.equals(strGiven)) {
						alGiven.get(loop2).inccounter();
						flagFound = true;
					}
				}
				
				
				if(!flagFound) {
					
					objTemp = new objRec(strGiven, 1);
					alGiven.add(objTemp);
				}
	
			}
		        	

			
			
			
			if(alSurname.isEmpty()) {
				objTemp = new objRec(strSurname, 1);
				alSurname.add(objTemp);
			} else {
	 
				flagFound=false;
				
				for(loop2=0; loop2<alSurname.size(); loop2++) {
					
					if(alSurname.get(loop2).strName.equals(strSurname)) {
						alSurname.get(loop2).inccounter();
						flagFound = true;
					}
				}
				
				
				if(!flagFound) {
					
					objTemp = new objRec(strSurname, 1);
					alSurname.add(objTemp);
				}
		
			}
			
	    }
			
	
		
		do { 
			flagSwapped = false;
			for(loop=1; loop<alGiven.size(); loop++) {
				if((alGiven.get(loop).count > alGiven.get(loop-1).count)) {
					
					
					objTemp = alGiven.get(loop-1);
					
					alGiven.set(loop-1, alGiven.get(loop));
					alGiven.set(loop, objTemp);
					
					flagSwapped = true;
					
					
				}
			}
		}while(flagSwapped);
		
		
		do { 
			flagSwapped = false;
			for(loop=1; loop<alSurname.size(); loop++) {
				if((alSurname.get(loop).count > alSurname.get(loop-1).count)) {
					
					
					objTemp = alSurname.get(loop-1);
					
					alSurname.set(loop-1, alSurname.get(loop));
					alSurname.set(loop, objTemp);
					
					flagSwapped = true;
					
				}
			}
		}while(flagSwapped);		
		
		
		
		
		
		println(textGivenNames);
		strULine="";
		for(loop=0 ;loop<textGivenNames.length(); loop++)
		  strULine += "-";
		println(strULine);
		
		if(showAllNames) maxNames = alGiven.size();
		else {
			if(alGiven.size() < 20) maxNames = alGiven.size(); 
				else maxNames = 20;	
		}
		for(loop=0; loop<maxNames; loop++) {
			percent = (float)alGiven.get(loop).count/numIndis*100;
			
			println(align(alGiven.get(loop).strName, 20, 3) + align(Integer.toString(alGiven.get(loop).count),6,2) + "  -  " + Float.toString(percent).substring(0,4) + "%") ;					
		}
		
		
		
		println();
		println(textSurnames);
		strULine="";
		for(loop=0 ;loop<textSurnames.length(); loop++)
			  strULine += "-";
		println(strULine);		

		if(showAllNames) maxNames = alSurname.size();
		else {
			if(alSurname.size() < 20) maxNames = alSurname.size(); 
				else maxNames = 20;	
		}		
		for(loop=0; loop<maxNames; loop++) {
			percent = (float)alSurname.get(loop).count/numIndis*100;
			println(align(alSurname.get(loop).strName, 20, 3) + align(Integer.toString(alSurname.get(loop).count),6,2) + "  -  " + Float.toString(percent).substring(0,4) + "%") ;					
		}		
		
	}
}
 