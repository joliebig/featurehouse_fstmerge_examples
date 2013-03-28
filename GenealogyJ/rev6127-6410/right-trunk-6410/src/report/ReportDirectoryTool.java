import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.TagPath;
import genj.gedcom.PropertyDate;
import genj.report.Report;


public class ReportDirectoryTool extends Report {
	 
	
	public int optionLifeSpan = 70;
  
	
	private String textTitle = translate("title");
	private String textDate = translate("date"); 
	private String textAge = translate("age"); 
	private String textSelect = translate("select");

	
  
  public void start(Gedcom gedcom) {
	  
	  int loop;
	  int tempDOBYear, tempDODYear;
	  int yearToCheck;
	  int iAge;
	  String strTemp;
	  String strOccu;
	  Entity[] individuals;
	  Boolean flagHasOccu;
	  
	  
	  strTemp = getValueFromUser("",textSelect);
   
	  if(strTemp==null) return;
	  
	  
	  try {
	  yearToCheck=Integer.parseInt(strTemp);
	  } catch (java.lang.NumberFormatException e) {  
		  println("Invalid year");
		  return;
	  }

	  
	  println(textTitle + " " + yearToCheck);
	  println();
	  
	  
	  
	  
	  
	  
	  individuals = gedcom.getEntities(Gedcom.INDI,"");
      
	  for(loop=0; loop<individuals.length; loop++) {	  
	  
	  	  
		  tempDOBYear = getYear(((Indi)individuals[loop]).getBirthDate());
		  
		  tempDODYear = getYear(((Indi)individuals[loop]).getDeathDate());
  
		  
		  
		  if(!((tempDOBYear==-1) && (tempDODYear==-1))){
			  
			  
			  if(tempDOBYear ==-1) tempDOBYear = tempDODYear - optionLifeSpan;

			  
			  if(tempDODYear ==-1) tempDODYear = tempDOBYear + optionLifeSpan;
				  
			  
			  if((yearToCheck>=tempDOBYear) && (yearToCheck<=tempDODYear)){
				  
				  iAge = yearToCheck - tempDOBYear;
				  
				  flagHasOccu = false;
				  
				  
				  if(individuals[loop].getProperty(new TagPath("INDI:OCCU"))==null)
					  strOccu="";
				  else {
					  strOccu = individuals[loop].getProperty(new TagPath("INDI:OCCU")).getDisplayValue();
					  flagHasOccu = true;
				  }
				  			  
				  if((flagHasOccu) && (individuals[loop].getProperty(new TagPath("INDI:OCCU:DATE"))!=null))
					  strOccu += " " + individuals[loop].getProperty(new TagPath("INDI:OCCU:DATE")).getDisplayValue();

				  if((flagHasOccu) && (individuals[loop].getProperty(new TagPath("INDI:OCCU:PLAC"))!=null))
					  strOccu += " " + individuals[loop].getProperty(new TagPath("INDI:OCCU:PLAC")).getDisplayValue();
				  				  
				  
				  if(strOccu.length()>0) strOccu = "[" + strOccu + "]";
			  
				  println(individuals[loop] + textAge + " " + iAge + " " + strOccu);
			  }
		  
		  }
	 
	  
	  }
	  
  } 
    
  public int getYear(PropertyDate someDate) {
	  
 	  String strYear;
	  
	  
	  if ((someDate==null) || (!someDate.isValid()) || (someDate.isRange())) 
		  return -1;
	  
	  
	  strYear = (someDate.getDisplayValue().trim());
	  strYear = strYear.substring(strYear.length()-4);
	  return Integer.parseInt(strYear);
  }

  
} 