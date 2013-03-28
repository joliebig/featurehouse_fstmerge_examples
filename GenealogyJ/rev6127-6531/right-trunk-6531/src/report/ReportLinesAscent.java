import genj.gedcom.Indi;
import genj.report.Report;


public class ReportLinesAscent extends Report {
		 
	
	public boolean optionMaternal = true;
	public boolean optionPaternal = true;
	
	
	private String textMaternal = translate("maternalline");
	private String textPaternal = translate("paternalline");
	private String textTitle = translate("title"); 	


  public void start(Indi indi) {

	  Indi person;
	  int iGen=1;
	  String strTemp, strULine;
	  int iLoop;
	  
	  
	  if(!optionMaternal&&!optionPaternal) return;
	  
	  
	  person = indi;

	  if(optionMaternal){
		  strULine="";
		  for(iLoop=0 ;iLoop<textMaternal.length(); iLoop++)
			  strULine += "-";		  
		  println(textMaternal);
		  println(strULine);
		  println(iGen + " " + getIndent(iGen)+indi.getName());
		  iGen++;
		  while((indi=indi.getBiologicalMother())!=null) {
			  strTemp = "";
			  strTemp += getIndent(iGen,2,"") + iGen + " " + getNonNullString(indi.getName());
			  strTemp += " (" + getNonNullString(indi.getBirthAsString()) + " - " + getNonNullString(indi.getDeathAsString()) + ")"; 
			  println(strTemp);
			  iGen++;  
		  }
	  }
	  
	  if(optionPaternal){
		  strULine="";
		  for(iLoop=0 ;iLoop<textPaternal.length(); iLoop++)
			  strULine += "-";
		  println();
		  println();
		  println(textPaternal);
		  println(strULine);
		  
		  indi=person;
		  iGen=1;
		  println(iGen + " " + getIndent(iGen)+indi.getName());
		  iGen++;
		  while((indi=indi.getBiologicalFather())!=null) {
			  strTemp = "";
			  strTemp += getIndent(iGen,2,"") + iGen + " " + getNonNullString(indi.getName());
			  strTemp += " (" + getNonNullString(indi.getBirthAsString()) + " - " + getNonNullString(indi.getDeathAsString()) + ")"; 
			  println(strTemp);  
			  iGen++;  
		  }
	  }	  
  }

  
  private String getNonNullString(String testString) {
	  if(testString==null) return ""; else return testString;  
  }
} 