import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.TagPath;
import genj.gedcom.PropertyDate;
import genj.gedcom.time.PointInTime;
import genj.report.Report;


public class ReportContemporaries extends Report {
	 
	
	public int optionLifeSpan = 70;
  
	
	private String textTitle = translate("title");
	private String textDate = translate("date"); 
	private String textSelect = translate("select");
	private String textNothing = translate("nothing");
	private String textNoDates = translate("nodates");
	private String textContemps = translate("contemps");
	
  public void start(Indi indi) {
	  
	  
	  
	  
	  displayHeader();
	  
	  
	  doReport(indi);  
  }
    
  
  public void start(Gedcom gedcom) {
	  
	  Entity ent;
	  
	  
	  Indi person;
	  
	  
	  ent = getEntityFromUser(textSelect + " ", gedcom, "INDI");
	  if(ent==null) return;
	  
	  person = (Indi)ent;
	  
	  
	  displayHeader();
	  
	  
	  doReport(person);	 
  }
    
    
  public void doReport(Indi indi) {
	
	  int subjectDOBYear, subjectDODYear, loop;
	  int tempDOBYear, tempDODYear;
	  Entity[] individuals;
	  String strSpan="";
	  
	  
	  subjectDOBYear = getYear(indi.getBirthDate());
	  subjectDODYear = getYear(indi.getDeathDate());

	  
	  if((subjectDOBYear ==-1) && (subjectDODYear ==-1)) { 
		  println(textNothing);
		  println(textNoDates);
		  return;	  
	  } 
	  
	  
	  if(subjectDOBYear ==-1) {
		  subjectDOBYear = subjectDODYear - optionLifeSpan;
		  strSpan=subjectDOBYear + "(est) - ";
	  }
	  else strSpan=subjectDOBYear + " - ";

	  
	  if(subjectDODYear ==-1) {
		  subjectDODYear = subjectDOBYear + optionLifeSpan;
		  
		  if (subjectDODYear < PointInTime.getNow().getYear()) 
			  strSpan = strSpan + subjectDODYear + "(est)";
	  }
	  else strSpan= strSpan + subjectDODYear;	  
	  
	  
	  println(textContemps + " " + indi.getName() + " " + strSpan);
	  println();
	  
	  
	  individuals = indi.getGedcom().getEntities(Gedcom.INDI,"");
      
	  for(loop=0; loop<individuals.length; loop++) {	  
	  
		  
		  if((Indi)(individuals[loop]) != indi ) {

		  	  
			  tempDOBYear = getYear(((Indi)individuals[loop]).getBirthDate());
			  
			  tempDODYear = getYear(((Indi)individuals[loop]).getDeathDate());
			  
			  
			  if((tempDOBYear ==-1) && (tempDODYear ==-1)) { 
				  
			  } else {
	  
				  
				  if(tempDOBYear ==-1) {
					  tempDOBYear = tempDODYear - optionLifeSpan;
					  strSpan=tempDOBYear + "(est) - ";
				  }
				  else strSpan=tempDOBYear + " - ";
		  
				  
				  if(tempDODYear ==-1) {
					  tempDODYear = tempDOBYear + optionLifeSpan;
					  
					  if (tempDODYear < PointInTime.getNow().getYear()) 
						  strSpan = strSpan + tempDODYear + "(est)";
				  }
				  else strSpan= strSpan + tempDODYear;	  
				  		  
				  
				  if( (tempDODYear < subjectDOBYear) || (tempDOBYear > subjectDODYear) ) {
					 
				  }
				  else {
					  strSpan = ((Indi)(individuals[loop])).getName() + " " + strSpan; 
					  println(strSpan);
				  }
			  
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
  
    
  public void displayHeader() {  
	  
	  println(align(textTitle, 80, 1));
	  println(textDate + ": " + PointInTime.getNow().toString());	 
	  println();
  }
} 