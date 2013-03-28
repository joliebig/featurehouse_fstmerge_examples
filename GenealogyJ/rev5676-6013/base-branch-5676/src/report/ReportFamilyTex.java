


import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.TagPath;
import genj.report.Report;





public class ReportFamilyTex extends Report {

    public boolean reportParents = true;
    public boolean reportOtherSpouses = true;
    public boolean reportDetailedChildrenData = true;


    public boolean reportNumberFamilies = true;
    public boolean reportPages = false;
	public boolean reportNumberIndi = true;
	
	
	public boolean reportNoteFam    = true;
	public boolean reportNoteIndi   = false;
	public boolean reportNoteDeath  = true;
	public boolean reportNoteBirth  = true;
	public boolean reportDetailOccupation  = true;
	public boolean reportFamiliyImage  = false;  
	public boolean reportTexHeader  = false;

	public boolean reportsubsection_on_newpage  = false;

    
    public void start(Gedcom gedcom) {
    	String str = "";


	  
	  if (reportTexHeader == true){
		  println("\\documentclass[10pt,a4paper]{article}");
		  println("\\ukage[T1]{fontenc}");
		  println("\\ukage[latin1]{inputenc}");
		  println("\\ukage{float}" );
		  println("%\\ukage{ngerman} % or use: \\ukage[francais]{babel}");
		  println("\\ukage[pdftex]{graphicx}");
		  println("\\DeclareGraphicsExtensions{.jpg,.pdf,.png} % for pdflatex");
		  println("\\ukage{subfig} % for subfloat");
		  println("\\ukage{fancybox}");
		  println("\\ukage[	pdftex,");
		  println("		colorlinks,");
		  println("		linkcolor=black]");
		  println("		{hyperref}");
		  println("\n\\newcommand{\\Bild}[3]{%");
		  println("\\begin{figure}[H]%");
		  println("\\includegraphics[width=120mm]{#2}%");
		  println("\\caption{#3 \\\\ {\\tiny (#2)}}%");
		  println("\\label{pic_#1}%\n\\end{figure}%\n}");
		  println("\n\\newcommand{\\Bildh}[3]{%");
		  println("\\begin{figure}[H]%");
		  println("\\includegraphics[height=160mm]{#2}%");
		  println("\\caption{#3 \\\\ {\\tiny (#2)}}%");
		  println("\\label{pic_#1}%");
		  println("\\end{figure}%");
		  println("}");
		  println("\n%Notes are from 4 sources: Birth, death, persons, families");
		  println("%with the next options you can select how the notes are printed");
		  println("%\\newcommand{ \\NoteBirth }[1]{ \\footnote{ #1 } }");
		  println("\\newcommand{ \\NoteBirth }[1]{, Notiz: #1 }");
		  println("%\\newcommand{ \\NoteBirth }[1]{ \\\\ \\leftskip=12mm Notiz: #1 }");
		  println("\n%\\newcommand{ \\NoteDeath }[1]{ \\footnote{ #1 } }");
		  println("\\newcommand{ \\NoteDeath }[1]{, Notiz: #1  }");
		  println("\n%\\newcommand{ \\NoteIndi  }[1]{ \\footnote{ #1 } }");
		  println("\\newcommand{ \\NoteIndi  }[1]{ \\\\ \\leftskip=12mm Notiz: #1 }");
		  println("\n% \\newcommand{ \\NoteFam   }[1]{ \\\\ \\leftskip=0mm Notiz zur Familie: #1 } \\par");
		  println("\\newcommand{ \\NoteFam   }[1]{ \\footnote{ #1 } }");
		  println("\n%\\newcommand{\\zeile}[2]{\\hspace*{#1}\\begin{minipage}[t]{\\textwidth} #2 \\end{minipage}\\\\}");
		  println("\n\\begin{document}");
		  println("");
		  println("\n\n\\title{Title}");
		  println("\\author{your name \\and your helper}");
		  println("% \\thanks{to all suporter}");
		  println("\\date{ \\today }\n\n\\restylefloat{figure}");
		  println("\n\\maketitle");
		  println("\n\\section{Introduction}\nsome words ...");
		  println("\n\\subsection{Used symbols}");
		  println("The following symbols are used for the events:\\\\");
		  println(TexEncode(OPTIONS.getBirthSymbol()) + " - Birth \\\\");
		  println(TexEncode(OPTIONS.getDeathSymbol()) + " - Death \\\\");
		  println(TexEncode(OPTIONS.getMarriageSymbol()) + " - Marriage \\\\");
		  println(TexEncode(OPTIONS.getOccuSymbol()) + " - Occupation \\\\");
		  println(TexEncode(OPTIONS.getChildOfSymbol()) + " - Child in Family\\\\");
		  println(TexEncode(OPTIONS.getBaptismSymbol()) + " - Baptism \\\\");
		  println("\n\\section{Families}");
		  println("\n\n\\parindent0mm");
		  
	  }
      
	  println("\n\n\\IfFileExists{head}{\\input{head}}\n\n");
	  
      Entity[] fams = gedcom.getEntities(Gedcom.FAM,"");
      for(int i=0; i<fams.length; i++) {
          analyzeFam((Fam)fams[i]);
      }
      
      
      println("\n\n\\IfFileExists{foot}{\\input{foot}}\n");
      
	  
	  if (reportTexHeader == true){
		  println("\\tableofcontents");
		  println("\\end{document}");
	  }

    }

    
    public void start(Fam fam) {
      analyzeFam(fam);
    }

    private String trim(Object o) {
        if(o == null)
            return "";
        return o.toString();
    }

	
    private String TexEncode(String str) {
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	String out;
    	
    	
    	
    	
    	out = str.replaceAll("[\\\\]", "\\\\textbackslash");		
    	out = out.replaceAll("[%]", "\\\\%");
    	out = out.replaceAll("[{]", "\\\\{");
    	out = out.replaceAll("[}]", "\\\\}"); 
    	out = out.replaceAll("\\\u", "\\\\[");				
    	out = out.replaceAll("\\\u", "\\\\]");    				
    	
    	out = out.replaceAll("[_]", "\\\\_");
        out = out.replaceAll("[#]", "\\\\#");
        out = out.replaceAll("[&]", "\\\\&");
        out = out.replaceAll("<", "\\\\textless{}");
    	out = out.replaceAll(">", "\\\\textgreater{}");
    	out = out.replaceAll("\u", "\\\\S");	
    	out = out.replaceAll("(\\$)", "\\\\\\$");  

        
        out = out.replaceAll("\u", "\\\\ss{}");
    	out = out.replaceAll("\u", "\\\\\"{A}");
    	out = out.replaceAll("\u", "\\\\\"{O}");
    	out = out.replaceAll("\u", "\\\\\"{U}");
    	out = out.replaceAll("\u", "\\\\\"{a}");
    	out = out.replaceAll("\u", "\\\\\"{o}");
    	out = out.replaceAll("\u", "\\\\\"{u}");
  	
    	
    	out = out.replaceAll("\u", "\\\\`{e}");		
    	out = out.replaceAll("\u", "\\\\'{e}");		
    	out = out.replaceAll("\u", "\\\\^{e}");	
    	out = out.replaceAll("\u", "\\\\\"{e}");	
    	out = out.replaceAll("\u", "\\\\'{E}");	
    	out = out.replaceAll("\u", "\\\\`{E}"); 
    	out = out.replaceAll("\u", "\\\\^{E}"); 
    	
       	out = out.replaceAll("\u", "\\\\^{a}");	
    	out = out.replaceAll("\u", "\\\\'{a}");	
    	out = out.replaceAll("\u", "\\\\`{a}");	
    	out = out.replaceAll("\u", "\\\\^{A}");	
    	out = out.replaceAll("\u", "\\\\ae");	
    	out = out.replaceAll("\u", "\\\\AE");	
    	
    	out = out.replaceAll("\u", "\\\\c{c}");	
    	out = out.replaceAll("\u", "\\\\c{C}");	
    	
    	out = out.replaceAll("\u", "\\\\oe");	
    	out = out.replaceAll("\u", "\\\\OE");	
    		
    	
    	
    	out = out.replaceAll("\u", "\\\\\"{i}");	
    	out = out.replaceAll("\u", "\\\\'{i}");	
    	out = out.replaceAll("\u", "\\\\`{i}");	
    	out = out.replaceAll("\u", "\\\\^{i}");	
    	out = out.replaceAll("\u", "\\\\\"{I}"); 
    	out = out.replaceAll("\u", "\\\\^{I}");	
    	
    	out = out.replaceAll("\u", "\\\\'{o}");	
    	out = out.replaceAll("\u", "\\\\`{o}");	
    	out = out.replaceAll("\u", "\\\\^{o}");	

    	out = out.replaceAll("\u", "\\\\'{u}");	
    	out = out.replaceAll("\u", "\\\\`{u}");	
    	out = out.replaceAll("\u", "\\\\^{u}");	
    	
    	out = out.replaceAll("\u", "\\\\~{n}");	
    	out = out.replaceAll("\u", "\\\\~{N}");	
    	
    	out = out.replaceAll("\u", "\\\\r{a}");	
    	out = out.replaceAll("\u", "\\\\r{A}");	
    	
    	
    	
    	out = out.replaceAll("\u", "\\\\degree");	
    	out = out.replaceAll("\\*", "\\\\textasteriskcentered");	
    	
    	
    	
    	

    	return out;
    }

	
	private String getIndentTex( int i) {

		
		String str = "\\leftskip=";
		str = str + (6*(i-1)) + "mm ";

		return str;
	}


	
	private String familyNote(Fam f) {
		if(!reportNoteFam) 
		{ 
			return "";
		}
		String str = "";

		for(int n = 0; n < f.getProperties("NOTE").length; n++) {
			str += "\\NoteFam{";
			str += TexEncode(trim(f.getProperties("NOTE")[n]));
			str += "} ";
		}
        return str;
    }

	
	private String BirthNote(Indi i) {
		if (!reportNoteBirth)
		{
			return "";
		}
		String str = "";

		
			str += trim(i.getProperty(new TagPath("INDI:BIRT:NOTE")));
			if (str.length() <1)
				return "";
			str = "\\NoteBirth{"+ TexEncode(str) +"} ";
		

        return str;
    }

	
	private String DeathNote(Indi i) {
        if( ! reportNoteDeath) {
			return "";
			}
		String str = "";

		
			str += trim(i.getProperty(new TagPath("INDI:DEAT:NOTE")));
			if (str.length() <1)
				return "";
			str = "\\NoteDeath{"+ TexEncode(str) + "} ";
		
        return str;
    }

	private String ID_of_Family(Fam f)
	{
		if (reportNumberFamilies==true)
		{
			return TexEncode((String)  f.getId())+" ";
		}
		else
		{
			return "";
		}

	}
	private String Name_of_Husband(Fam f)
	{
		String str="";
	
		str = f.getHusband().getName();
		if (reportNumberIndi==true)
		{
			str += " " + f.getHusband().getId();
		}
		
		return TexEncode(str); 
	}
	
	private String Name_of_Wife(Fam f)
	{
		String str="";
		
		str = f.getWife().getName();
		if (reportNumberIndi==true)
		{
			str += " " + f.getWife().getId();
		}		
		return TexEncode(str); 
	}
	
	private String Name_of_Indi(Indi Indi)
	{
		String str="";

		
		str = Indi.getName();
		if (reportNumberIndi==true)
		{
			str += " " + Indi.getId();
		}		
		return TexEncode(str); 
	}


	
	private String familyToString(Fam f) {
		Indi husband = f.getHusband(), wife = f.getWife();
		String str = "\\hyperlink{"+f.getId()+"}{"+ ID_of_Family(f);

		if(husband!=null)
			str = str + Name_of_Husband(f);
		if(husband!=null & wife!=null)
			str = str + " " + TexEncode(translate("and")) + " ";
		if(wife!=null)
			str = str + Name_of_Wife(f);

		if (reportPages)
			str = str + "(" + TexEncode(translate("Chap")) +  ". \\ref*{"+f.getId()+"}, S. \\pageref*{"+f.getId()+"})";
		str += "}";
        return str;
    }

	
    private String familyToStringSubsection(Fam f) {
    	Indi husband = f.getHusband(), wife = f.getWife();

        String str = "\\leftskip=0mm ";
        
        if (reportsubsection_on_newpage == true) {
        	str += "\\newpage ";
        }
        
        str += "\\subsection{"+ ID_of_Family(f);

        if(husband!=null)
            str = str + Name_of_Husband(f);
        if(husband!=null & wife!=null)
        	str = str + " " + TexEncode(translate("and")) + " ";
        if(wife!=null)
            str = str + Name_of_Wife(f);

        str += "} \n\\hypertarget{"+f.getId()+"}{}\n\\label{"+f.getId()+"}";
        return str;
    }

	

	private String familyImageCaption(Fam f) {
        
		String str = "\n";
		Indi husband = f.getHusband(), wife = f.getWife();
		str += "Stammbaum der Famile " + ID_of_Family(f);
		if(husband!=null)
			str = str + Name_of_Husband(f);
		if(husband!=null & wife!=null)
			str = str + " " + translate("and") + " ";
		if(wife!=null)
			str = str + Name_of_Wife(f);

		return str;

	}


	
	private String familyImage(Fam f) {
        
		
		Indi husband = f.getHusband(), wife = f.getWife();
		String str = "\n";

		if(husband!=null){
			str += "\\IfFileExists{"+husband.getId()+".pdf}{"; 
			str += "\\Bild{Bild_"+husband.getId()+"}{"+husband.getId()+".pdf}{" + familyImageCaption(f) + "}}\n";
		}
        return str;
	}

	
    private void analyzeFam(Fam f) {
    	
		String str = "";
		println(familyToStringSubsection(f));
		if (reportFamiliyImage == true) {
			println(familyImage(f));
		}

		
        println(str + familyNote(f));

        if( (trim(f.getMarriageDate()).length()>0) || (trim(f.getProperty(new TagPath("FAM:MARR:PLAC"))).length()>0) )
            println(getIndentTex(1)+TexEncode(OPTIONS.getMarriageSymbol()+" "+trim(f.getMarriageDate())+" "+trim(f.getProperty(new TagPath("FAM:MARR:PLAC"))))+"\\par");
        analyzeIndi(f.getHusband(), f);
        analyzeIndi(f.getWife(), f);
        analyzeChildren(f);
    }

	
    private void analyzeIndi(Indi indi, Fam f) {
    	
        if(indi==null)
            return;

		String str;
		println( getIndentTex(2) + Name_of_Indi(indi) + "\\par");

        
        

        if(reportParents) {
          Fam fam = indi.getFamilyWhereBiologicalChild();
            if(fam!=null)
                println(getIndentTex(3)+TexEncode(OPTIONS.getChildOfSymbol()) + " "+familyToString(fam) + "\\par");
        }

        if( (trim(indi.getBirthAsString()).length()>0) || (trim(indi.getProperty(new TagPath("INDI:BIRT:PLAC"))).length()>0) ) {
            str = OPTIONS.getBirthSymbol()+" "+trim(indi.getBirthAsString())+" "+trim(indi.getProperty(new TagPath("INDI:BIRT:PLAC")));
            str = TexEncode(str);
			println(getIndentTex(3) + str + BirthNote(indi) + "\\par");
			}

        if(indi.getProperty("DEAT")!=null && ( (trim(indi.getDeathAsString()).length()>0) || (trim(indi.getProperty(new TagPath("INDI:DEAT:PLAC"))).length()>0) ) ) {
            str = OPTIONS.getDeathSymbol()+" "+trim(indi.getDeathAsString())+" "+trim(indi.getProperty(new TagPath("INDI:DEAT:PLAC")));
            str = TexEncode(str);
            println(getIndentTex(3) + str + DeathNote(indi) + "\\par");
		}

        if(reportOtherSpouses) {
            Fam[] families = indi.getFamiliesWhereSpouse();
            if(families.length > 1) {
                println(getIndentTex(3)+translate("otherSpouses")+"\\par");
                for(int i=0; i<families.length; i++) {
                    if(families[i]!=f) {
                        
                        str = "";
                        if((trim(families[i].getMarriageDate()).length()>0) || (trim(families[i].getProperty(new TagPath("FAM:MARR:PLAC"))).length()>0))
                            str = OPTIONS.getMarriageSymbol()+" "+trim(families[i].getMarriageDate())+" "+trim(families[i].getProperty(new TagPath("FAM:MARR:PLAC")))+" ";
                        str = TexEncode(str);
                        println(getIndentTex(4)+str+" "+familyToString(families[i])+"\\par");
                    }
                }
            }
        }
        if (reportDetailOccupation & trim(indi.getProperty(new TagPath("INDI:OCCU"))).length()>0) {
			str = translate("occupation")+": "+ trim(indi.getProperty(new TagPath("INDI:OCCU"))) ;
			str = TexEncode(str);
			println(getIndentTex(3) + str + "\\par");
		}
    }

    
	
    private void analyzeChildren(Fam f) {

        Indi[] children = f.getChildren();
        Indi child;
        Fam[] families;
        Fam family;
		String str = "";

        if(children.length>0)
            println(getIndentTex(2) + translate("children") + "\\par");
        for(int i=0; i<children.length; i++) {
            child = children[i];
            str = Name_of_Indi(child); 
            println(getIndentTex(3) + str + "\\par");
            if(reportDetailedChildrenData) {
                if ( (trim(child.getBirthAsString()).length()>0) || (trim(child.getProperty(new TagPath("INDI:BIRT:PLAC"))).length()>0) ) {
                    str = OPTIONS.getBirthSymbol()+" ";
                    str += trim(child.getBirthAsString())+" ";
                    str += trim(child.getProperty(new TagPath("INDI:BIRT:PLAC")));
					str = TexEncode(str);
					println(getIndentTex(4) + str + BirthNote(child) + "\\par");
				}
                printBaptism(child, "BAPM");
                printBaptism(child, "BAPL");
                printBaptism(child, "CHR");
                printBaptism(child, "CHRA");
                families = child.getFamiliesWhereSpouse();
                for(int j=0; j<families.length; j++) {
                    family = (Fam)families[j];
                    

                    str = trim(family.getMarriageDate())+" ";
                    str += trim(family.getProperty(new TagPath("FAM:MARR:PLAC")));
					str = TexEncode(str);
					println(getIndentTex(4) + OPTIONS.getMarriageSymbol() + " " + familyToString(family) + " " + str + "\\par");                    
                }
                if (reportDetailOccupation & trim(child.getProperty(new TagPath("INDI:OCCU"))).length()>0) {
					str = translate("occupation")+": ";
					str += trim(child.getProperty(new TagPath("INDI:OCCU")));
					str = TexEncode(str);
					println(getIndentTex(4) + str + "\\par");
				}
                if(child.getProperty("DEAT")!=null && ( (trim(child.getDeathAsString()).length()>0) || (trim(child.getProperty(new TagPath("INDI:DEAT:PLAC"))).length()>0) ) ) {
                    str = OPTIONS.getDeathSymbol()+" ";
                    str += trim(child.getDeathAsString())+" ";
                    str += trim(child.getProperty(new TagPath("INDI:DEAT:PLAC")));
                    str = TexEncode(str);
					println(getIndentTex(4) + str + DeathNote(child) + "\\par");
				}
            }
        }
    }

	
    private void printBaptism(Indi indi, String tag) {
    	String str = "";
        if( (indi.getProperty(tag)!=null) 
        	&& (
        			(trim(indi.getProperty(new TagPath("INDI:"+tag+":DATE"))).length()>0) 
        			|| (trim(indi.getProperty(new TagPath("INDI:"+tag+":PLAC"))).length()>0) 
        		) 
        	) {
	        	
	        	str = OPTIONS.getBaptismSymbol() + " (" + tag;
	        	str += "): ";
	        	str += trim(indi.getProperty(new TagPath("INDI:"+tag+":DATE")))+" ";
	        	str += trim(indi.getProperty(new TagPath("INDI:"+tag+":PLAC")));
	        	str = TexEncode(str);
	        	println(getIndentTex(4) + str + "\\par");
	        }
    }

} 
