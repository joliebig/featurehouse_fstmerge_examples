import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.gedcom.time.PointInTime;
import genj.report.Report;


public class ReportMissingInformation extends Report {
	 
	
	public boolean checkBirthDate = true;
	public boolean checkBirthPlace = true;
	public boolean checkBaptismDate = true;
	public boolean checkBaptismPlace = true;
	public boolean checkDeathDate = true;
	public boolean checkDeathPlace = true;
	public boolean checkSex = true;
	public boolean checkGiven = true;
	public boolean checkSurname = true;
  
	
	private String textTitle = translate("title");
	private String textSubject = translate("subject"); 
	private String textBirth = translate("birth");
	private String textBaptism = translate("baptism");
	private String textDeath = translate("death");
	private String textDate = translate("date");
	private String textPlace = translate("place");
	private String textSex = translate("sex");
	private String textGiven = translate("given");
	private String textSurname = translate("surname");
	private String textKey = translate("key");

	
	
	private int colName = 30;
	private int colData = 6;
	private int numDataCols = 9;
  
  public void start(Indi indi) {
	  
   	 
	  
	  displayHeader(indi.getName());
	  
	  checkIndi(indi);
	  
  }
   
  public void checkIndi(Indi indi) {
  
		
	
	PropertyDate tempDate;
	PropertyPlace tempPlace;
	String strDataRow;
	String strNameID;
	Boolean flagOk1, flagOk2;

	
    
	strNameID = indi.getName() + " " + indi.getId();
    strDataRow = align(strNameID, colName, 3); 

    
    
 	
	if(checkBirthDate) {		
		
		tempDate = indi.getBirthDate();
		if((tempDate == null) || (!tempDate.isValid())) {
			strDataRow = strDataRow + align("X",colData,1);
		} else { 
			strDataRow = strDataRow + align("ok",colData,1);	
		}
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	
	
	if(checkBirthPlace) {
		tempPlace = (PropertyPlace)indi.getProperty(new TagPath("INDI:BIRT:PLAC"));
		if((tempPlace == null)){
			strDataRow = strDataRow + align("X",colData,1);
		}else {
			strDataRow = strDataRow + align("ok",colData,1);	
		}
		
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	
	
	if(checkBaptismDate) {
		
		flagOk1 = true;
		flagOk2 = true;
		
		
		tempDate = (PropertyDate)indi.getProperty(new TagPath("INDI:BAPM:DATE"));		
		if((tempDate == null) || (!tempDate.isValid())) {
			flagOk1 = false;
		}
		
		tempDate = (PropertyDate)indi.getProperty(new TagPath("INDI:CHR:DATE"));
		if((tempDate == null) || (!tempDate.isValid())) {
			flagOk2 =false;
		}	
	
		
		if(flagOk1 || flagOk2) {
			strDataRow = strDataRow + align("ok",colData,1);
		} else {
		strDataRow = strDataRow + align("X",colData,1);
			
		}
		
	}
	else {
		strDataRow = strDataRow + align("-",colData,1);
	}
	
	
	
	if(checkBaptismPlace) {
		
		flagOk1 = true;
		flagOk2 = true;
		
		
		tempPlace = (PropertyPlace)indi.getProperty(new TagPath("INDI:BAPM:PLAC"));
		
		if((tempPlace == null) || (tempPlace.toString() == "")) {
			flagOk1 = false;
		}

		
		tempPlace = (PropertyPlace)indi.getProperty(new TagPath("INDI:CHR:PLAC"));
		if((tempPlace == null) || (tempPlace.toString() =="")) {
			flagOk2 = false;
		}		
			
		if(flagOk1 || flagOk2) {	
			strDataRow = strDataRow + align("ok",colData,1);
		} else { 
			strDataRow = strDataRow + align("X",colData,1);	
		}		
	}
	else {
		strDataRow = strDataRow + align("-",colData,1);
	}
	
	
	
	
	
	if(checkDeathDate) {
		
		
		
		tempDate = indi.getDeathDate();
		if((indi.getDeathDate() == null) || (!tempDate.isValid())) {
			strDataRow = strDataRow + align("X",colData,1);
		} else {
		strDataRow = strDataRow + align("ok",colData,1);
		}
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	
	
	if(checkDeathPlace) {
		tempPlace = (PropertyPlace)indi.getProperty(new TagPath("INDI:DEAT:PLAC"));
		if((tempPlace == null)){
			strDataRow = strDataRow + align("X",colData,1);
		}else {
			strDataRow = strDataRow + align("ok",colData,1);	
		}
		
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}	

	
	if(checkSex) {
		if((indi.getSex() != PropertySex.MALE) && (indi.getSex() != PropertySex.FEMALE)) { 
			strDataRow = strDataRow + align("X",colData,1);
		} else {	
		strDataRow = strDataRow + align("ok",colData,1);
		}
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	

	
	
	if(checkGiven) {
		if(indi.getFirstName() == "") {
			strDataRow = strDataRow + align("X",colData,1);
	} else {
		strDataRow = strDataRow + align("ok",colData,1);
	}
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	
	
	
	
	if(checkSurname) {
		if(indi.getLastName() == "") {
			strDataRow = strDataRow + align("X",colData,1);
	} else {
		strDataRow = strDataRow + align("ok",colData,1);
	} 
	} else {
		strDataRow = strDataRow + align("-",colData,1);	
	}
	
	
	println(strDataRow);
  }
 
  
  
  
  
  
  public void start(Gedcom gedcom) {
	  
	  
	  Entity[] individuals;
	  int loop;
	  Indi person;

	    
	  
	  displayHeader(gedcom.getName());
	
	  
	  individuals = gedcom.getEntities(Gedcom.INDI,"");
      
	  for(loop=0; loop<individuals.length; loop++) {
        
      	
		person = (Indi)individuals[loop];      
      	checkIndi(person);
        	
      }
	 
  }
  
  

  
  public void displayHeader(String strSubject) {
  
	  String strColHeader1, strColHeader2;
	  String strUnderLine;
	  int loop;	  
	  
	  
	  println(align(textTitle, (colName + numDataCols*colData), 1));
	  println();
	  
	  println(textSubject + ": " + strSubject);
	  println(textDate + ": " + PointInTime.getNow().toString());
	  println(textKey);
	  println();
	   
	  strUnderLine = "-";
	  for(loop=1; loop<(colName+numDataCols*colData)-1; loop++)
		  strUnderLine += "-";
	  
	  
	  strColHeader1 = align(" ", colName, 1)
	  				+ align(textBirth, colData, 1)
	  				+ align(textBirth, colData, 1)
	  				+ align(textBaptism, colData, 1)
	  				+ align(textBaptism, colData, 1)	  				
	  				+ align(textDeath, colData, 1)
	  				+ align(textDeath, colData, 1);
	  
	  
	  strColHeader2 = align(" ", colName, 1)
	  				+ align(textDate, colData, 1)
	  				+ align(textPlace, colData, 1)
	    			+ align(textDate, colData, 1)
	    			+ align(textPlace, colData, 1)
	    			+ align(textDate, colData, 1)
	    			+ align(textPlace, colData, 1)	    			
	    			+ align(textSex,colData, 1) 
	    			+ align(textGiven,colData,1) 
	    			+ align(textSurname,colData,1);
 
	  
	  println(strColHeader1);
	  println(strColHeader2);
	  println(strUnderLine);
  
  }
} 