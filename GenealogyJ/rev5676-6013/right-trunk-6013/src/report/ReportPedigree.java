import java.util.ArrayList;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.report.Report;


public class ReportPedigree extends Report {
	 
		
	
	private String textTitle = translate("title");
	private String textSelect = translate("select");
	private String textUnknown = translate("unknown");
	
  public void start(Indi indi) {
	   
	  
	  doReport(indi);  
  }
    
  
  public void start(Gedcom gedcom) {

	  
	  Entity ent;
	  Indi person;
  
	  
	  ent = getEntityFromUser(textSelect + " ", gedcom, "INDI");
	  if(ent==null) return;
	  person = (Indi)ent;
	   
	  
	  doReport(person);	 
  }
    
   
  
  public void doReport(Indi indi) {
	
	  
	  ArrayList<Indi> parseList = new ArrayList<Indi>();
	  ArrayList<Indi> tempList = new ArrayList<Indi>();
	  int iGen, iLoop, iTemp;
	  Indi tempIndi;
	  Indi dummyIndi;
	  int iGenerations;	  
	  Object aGens[] = {2,3,4,5,6};
	  int iPosition, iRow, iCol;
	  int iDOBYear, iDODYear;
	  String strDates;
	  Indi iDiagIndi[] = new Indi[31];
	  int iDiag[][] = {
			  {-1,-1,-1,-2,15},
			  {-1,-1,-2,7,-1},
			  {-1,-1,-3,-2,16},
			  {-1,-2,3,-1,-1},
			  {-1,-3,-3,-2,17},
			  {-1,-3,-2,8,-1},
			  {-1,-3,-1,-2,18},
			  {-2,1,-1,-1,-1},
			  {-3,-3,-1,-2,19},
			  {-3,-3,-2,9,-1},
			  {-3,-3,-3,-2,20},
			  {-3,-2,4,-1,-1},
			  {-3,-1,-3,-2,21},
			  {-3,-1,-2,10,-1},
			  {-3,-1,-1,-2,22},
			  {0,-1,-1,-1,-1},
			  {-3,-1,-1,-2,23},
			  {-3,-1,-2,11,-1},
			  {-3,-1,-3,-2,24},
			  {-3,-2,5,-1,-1},
			  {-3,-3,-3,-2,25},
			  {-3,-3,-2,12,-1},
			  {-3,-3,-1,-2,26},
			  {-2,2,-1,-1,-1},
			  {-1,-3,-1,-2,27},
			  {-1,-3,-2,13,-1},
			  {-1,-3,-3,-2,28},
			  {-1,-2,6,-1,-1},
			  {-1,-1,-3,-2,29},
			  {-1,-1,-2,14,-1},
			  {-1,-1,-1,-2,30}	  		  
	  };
	  String strRow;
	   
	  
	  parseList.add(indi);
	
	  
	  
	  
	  dummyIndi = new Indi();	  
	  
	  
	  iGen=1;
	  
	  iGenerations = 5;

	  
	  iPosition=1;
	  iDiagIndi[0]=indi;
	  
	  do {
		
		 for(iLoop=0;iLoop<parseList.size();iLoop++){
			 			 
			
			 if((parseList.get(iLoop).getBiologicalFather()) == null) {
				 tempIndi = dummyIndi;
				 
			 } else {		 
				 tempIndi = parseList.get(iLoop).getBiologicalFather();		 		 
				 
			 }
			 
			 tempList.add(tempIndi);

			 
			 
			 if((parseList.get(iLoop).getBiologicalMother()) == null) {
				 tempIndi = dummyIndi;				 
				 
			 }
			 else {		 
				 tempIndi = parseList.get(iLoop).getBiologicalMother();
				 
			 }
			 
			 tempList.add(tempIndi);			 

			  
		 }
		 
		 parseList.clear();
		 for(iLoop=0;iLoop<tempList.size();iLoop++){
			 parseList.add(tempList.get(iLoop));
			 iDiagIndi[iPosition++] = tempList.get(iLoop);
		 }
		 tempList.clear();
		 
	  } while(++iGen < iGenerations);

	  
	  
	  
	  
	  println(align(textTitle + ": " + indi, 125, 1));
	  println();
	  
	  for(iRow=0; iRow<31; iRow++){
		  strRow = "";
		  for(iCol=0; iCol<5; iCol++){
			  
			  iTemp = iDiag[iRow][iCol];
			  if(iTemp<0){
				  if(iTemp==-1)strRow=strRow + align(" ",24, 3);				 
				  if(iTemp==-2)strRow=strRow + align("|-------------",24, 2);
				  if(iTemp==-3)strRow=strRow + align("|             ",24, 2);
			  }
			  	else {
			  		if(iDiagIndi[iTemp].getName()=="") strRow=strRow + align("("+textUnknown+ ")",32,3);
			  			else {
			  				strDates="";
			  				iDOBYear = getYear(iDiagIndi[iTemp].getBirthDate());
			  				if(iDOBYear==-1) strDates = " (-";
			  					else strDates = " ("+iDOBYear+"-";
			  			    iDODYear = getYear(iDiagIndi[iTemp].getDeathDate());
			  			    if(iDODYear==-1) strDates = strDates + ")";
		  						else strDates = strDates + iDODYear + ")";
			  				strRow=strRow + align(iDiagIndi[iTemp].getName()+strDates, 32, 3);

			  			}
			  	}
		  }	  
		  println(strRow);
	  }
  
	  
  }
  
  
public int getYear(PropertyDate someDate) {
	  
 	  String strYear;
	  
	  
	  if ((someDate==null) || (!someDate.isValid()) || (someDate.isRange())) 
		  return -1;
	  
	  
	  strYear = (someDate.toString().trim());
	  strYear = strYear.substring(strYear.length()-4);
	  return Integer.parseInt(strYear);
  }  
    
 


} 