

import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertySex;
import genj.gedcom.time.PointInTime;
import genj.report.Report;
import genj.util.swing.Action2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class ReportLinesCirc extends Report {
    private final static Charset CHARSET = Charset.forName("ISO-8859-1");

    
    private int maxlevel=10;
    public int umaxlevel=2;
    public String umaxlevels[] = { translate("umaxlevel.5"),
				     translate("umaxlevel.6"),
				     translate("umaxlevel.7"),
				     translate("umaxlevel.8"),
				     translate("umaxlevel.9"),
				    translate("umaxlevel.10")};

    
    private int x_pages=1;
    private int y_pages=1;
    public int unb_pages=0;
    public String unb_pagess[] = { translate("unb_pages.0"),
				     translate("unb_pages.1"),
				     translate("unb_pages.2"),
				     translate("unb_pages.3"),
				     translate("unb_pages.4"),
				     translate("unb_pages.5"),
				    translate("unb_pages.6")};
    private int radius=0;

    
    public int colourtext =1;
    public String colourtexts[] = { translate("colourtext.0"),
				     translate("colourtext.1")};

    
    public int colouroption =0;
    public String colouroptions[] = { translate("colouroption.0"),
				     translate("colouroption.1"),
				    translate("colouroption.2")};
    private boolean alternating;
    private boolean gradient;

    
    public int dateformat=1;
    public String dateformats[] = { translate("dateformat.0"),
				     translate("dateformat.1"),
				    translate("dateformat.2")};

    
    private boolean marrest;
    private boolean printmarr;
    public boolean uprintmarr=true;
    

    
    public int uenc_choice=0;
    private int enc_choice=1;
    public String uenc_choices[] = { translate("uenc_choice.0"),
				     translate("uenc_choice.1"),
				    translate("uenc_choice.2")};

    
    public int ufont_name=0;
    public String ufont_names[] = { translate("ufont_name.0"),
				     translate("ufont_name.1"),
				     translate("ufont_name.2"),
				     translate("ufont_name.3"),
				     translate("ufont_name.4"),
				    translate("ufont_name.5")};
    private String font_name = "Helvetica";

    
    public boolean  printdate=false;

    
    private PrintWriter writer;
    private int indicentre=1;
    private int numindilines=0;
    private int nummarr=-1;
    private final String version = "genj 1.0";
    private boolean transparent;
    
    private PrintWriter getWriter(OutputStream out) {
	return new PrintWriter(new OutputStreamWriter(out, CHARSET));
    }

    
    public File start(Indi indi) {

      
      initUserOptions();

      
      File file = getFileFromUser(translate("output.file"), Action2.TXT_OK,true);
      if (file == null)
        return null;

      
      try{
        writer = getWriter(new FileOutputStream(file));
      }catch(IOException ioe){
        System.err.println("IO Exception!");
        ioe.printStackTrace();
        return null; 
      }

      
      main(indi,null);
      
      writer.flush();
      writer.close();

      
      return file;

    }

    private void initUserOptions(){
	
	marrest=false;
	printmarr=true;
	alternating=(colouroption == 0);
	gradient=(colouroption == 2);
	maxlevel=umaxlevel + 5;
	enc_choice=1+uenc_choice;
	font_name = translate("ufont_name."+ufont_name);
	transparent = (colouroption != 0);
	switch (unb_pages){
	case 1:x_pages=2;y_pages=1;break;
	case 2:x_pages=2;y_pages=2;break;
	case 3:x_pages=3;y_pages=2;break;
	case 4:x_pages=4;y_pages=3;break;
	case 5:x_pages=5;y_pages=4;break;
	case 6:x_pages=6;y_pages=4;break;
	default:x_pages=1;y_pages=1;break;
	}

    }

private String put_given_name(Indi person,int length){


    return escapePsString(truncateString(givens(person),length));
}

    private String put_full_name(Indi person,int sur_upper,int n_order,int length){
	return fullname(person,sur_upper,n_order,length);

}
    private void endline(int ahnen,int offset,int info,int max){
	writer.println(") "+ahnen+" "+offset+" "+info+" "+max+"} addind");
    }

    private void putperson(Fam family, Indi person, int level, int ahnen, int info, int dateformat) {

	int[] levellength = {25,26,23,16,15,15,21,21,21,21,21};
	PersonCell pCell = new PersonCell();
	if (eq(level,1)) {
	    numindilines += pCell.add(surname(person),numindilines);
	    numindilines += pCell.add(put_given_name(person,levellength[level]),numindilines);
	    numindilines += pCell.add(getDateString(birth(person),death(person),dateformat),numindilines);
	    writer.print(pCell.get(ahnen,info));
	}else if(and(ge(level,2),le(level,6))){
	    numindilines += pCell.add(surname(person),numindilines);
	    numindilines += pCell.add(put_given_name(person,levellength[level]),numindilines);
	    numindilines += pCell.add(getDateString(birth(person),death(person),dateformat),numindilines);
	    writer.print(pCell.get(ahnen,info));
	}else if(or(eq(level,7),eq(level,8))){
	    numindilines += pCell.add(put_full_name(person,0,1,getel(levellength,level)),numindilines);
	    numindilines += pCell.add(getDateString(birth(person),death(person),dateformat),numindilines);
	    writer.print(pCell.get(ahnen,info));
	}else if(ge(level,9)){
	    numindilines += pCell.add(put_full_name(person,0,1,getel(levellength,level))+
				      getDateString(birth(person),death(person),dateformat)
				      ,numindilines);
	    writer.print(pCell.get(ahnen,info));
	}
        if (printmarr){
	    
	    if (marrest && false){
		
	    }else{
		if (marriage(family)!=null){
		    if (eq("M",sex(person))){
			nummarr++;
			writer.println(d(nummarr)+" {(" +date(marriage(family)) +") "+ d(ahnen)+ " "+ d(info)+"} addmarr");
		    }
		}
	    }
	}
    }

    private void semicirc(Fam family, Indi person, int level, int ahnen, int info, int maxlevel, int dateformat) {
	if (person!= null && le(level,maxlevel)) {
	    putperson(family,person,level,ahnen,info,dateformat);
	    int nextlevel = add(level,1);
	    int nextahnen = mul(ahnen,2);
	    semicirc(parents(person), father(person), nextlevel, nextahnen, info,maxlevel,dateformat);
	    semicirc(parents(person), mother(person), nextlevel, add(nextahnen,1), info,maxlevel,dateformat);
	}
    }

    
    boolean le(int a, int b){ return (a<=b);}
    boolean gt(int a, int b){ return (a>b);}
    boolean ge(int a, int b){ return (a>=b);}
    boolean ne(int a, int b){ return (a!=b);}
    boolean eq(int a, int b){ return (a==b);}
    boolean eq(String a, String b){ return (a==b);}
    boolean or(boolean a, boolean b){ return (a||b);}
    boolean and(boolean a, boolean b){ return (a&&b);}
    int add(int a, int b){return a+b;}
    int mul(int a, int b){return a*b;}
    int getel(int[] intarray, int offset){
	if (offset>= intarray.length) return 0;
	else return intarray[offset];
    }
    String d(int i){ return(""+i);}
    String d(boolean b){ return(b?"1":"0");}
    PropertyDate birth(Indi entity){
		
	if (entity==null)
	    return null;
	Property prop = entity.getProperty("BIRT");
	if (prop==null)
	    return null;

	return (PropertyDate) prop.getProperty("DATE");
    }
    PropertyDate death(Indi entity){
		
	if (entity==null)
	    return null;
	Property prop = entity.getProperty("DEAT");
	if (prop==null)
	    return null;

	return (PropertyDate) prop.getProperty("DATE");
    }
    String date(PropertyDate date){
	if (date != null ) {
	    return date.getDisplayValue();
	} else {
	    return "";
	}
    }
    String year(PropertyDate date){
	if (date != null ) {
	    return date.getStart().isValid()?""+date.getStart().getYear():"????";
	} else {
	    return "";
	}
    }
    PropertyDate marriage(Fam family){
	Property prop;
	if (family == null) return null;
	prop = family.getProperty("MARR");
	if (prop==null) return null;
	return  (PropertyDate) prop.getProperty("DATE");
    }

    Indi husband(Fam fam){return (fam == null)?null:fam.getHusband();}
    Indi wife(Fam fam){return (fam == null)?null:fam.getWife();}
    Fam parents(Indi person) {return (person == null)?null:person.getFamilyWhereBiologicalChild();}
    Indi father(Indi person){return (person == null)?null:husband(parents(person));}
    Indi mother(Indi person){return (person == null)?null:wife(parents(person));}
    String sex(Indi indi){return((indi.getSex() == PropertySex.MALE )? "M":"F");}
    
    private String fullname(Indi indi,int isUpper,int type,int length){
	if (indi.getName().length()<=length){
	    return escapePsString(indi.getName());
	} else {
	    return escapePsString(truncateString(indi.getName(),length));
	}
    }
    String surname(Indi indi){
	if (indi == null) return "";
	return escapePsString(truncateString(indi.getLastName(),30));
    }
    String givens(Indi indi){
	if (indi == null) return "";
	return indi.getFirstName();
    }

    private String truncateString(String s, int length){
	if (s.length()<=length){
	    return s;
	} else {
	    return s.substring(0,length);
	}
    }

    private String escapePsString(String s){
	String result;
	result = s.replaceAll("\\\\","\\\\\\\\");
	result = result.replaceAll("\\(","\\\\(");
	result = result.replaceAll("\\)","\\\\)");
	return result;
    }

    private void putpageprintouts(int xn,int yn){
	int page_num = 0;
	int yi = yn-1;
	int yi_ord;
	int xi;

	while(yi >= 0) {
	    yi_ord = yn-1-yi;
	    xi=xn- 1;
	    while(xi>=0) {
		page_num = add(page_num, 1);
		writer.println("%%Page: "+page_num+ " " +page_num+ "\n"+
			    "cleartomark mark\n"+
			    xi+ " " +yi +" print-a-page\n"+
			    "showpage\n");
		xi= xi- 1;
	    }
	    yi= yi- 1;
	}
    }

    private void printfile(){
	writer.println("%!PS-Adobe-3.0");
	writer.println("%%Title: (PS-CIRCLE.PS - Circular Genealogical Pedigree Chart in Postscript format)");
	writer.println("%%Creator: " +version+ " - a Lifelines circle ancestry chart report generator");
	writer.println("%%CreationDate: ");
	writer.println("%%Pages: "+d(mul(x_pages,y_pages)));
	writer.println("%%PageOrder: Ascend");
	writer.println("%%Orientation: Portrait");
	writer.println("%%EndComments\n");

	writer.println("%%BeginDefaults");
	writer.println("%%ViewingOrientation: 1 0 0 1");
	writer.println("%%EndDefaults\n");

	writer.println("%%BeginProlog\n");
	writer.println("%   much of the code involved with font encoding and with multipaging");
	writer.println("%   is borrowed from Robert Simms <rsimms@ces.clemson.edu>\n");

	writer.println("%page margins");
	writer.println("/margin_top 20 def");
	writer.println("/margin_bottom 20 def");
	writer.println("/margin_left 20 def");
	writer.println("/margin_right 20 def\n");

	writer.println("%number of pages in each direction");

	writer.println("/xpages "+d(x_pages)+" def");
	writer.println("/ypages "+d(y_pages)+" def\n");

	writer.println("/fontname /"+font_name+" def\n");

	writer.println("/portrait true def\n");

	writer.println("/inch {72 mul} def\n");

	writer.println("/*SF {                 % Complete selectfont emulation");
	writer.println("  exch findfont exch");
	writer.println("  dup type /arraytype eq {makefont}{scalefont} ifelse setfont");
	writer.println("} bind def\n");

	writer.println("/BuildRectPath{");
	writer.println("	dup type dup /integertype eq exch /realtype eq or{");
	writer.println("			4 -2 roll moveto 	%Operands are: x y width height");
	writer.println("			dup 0 exch rlineto");
	writer.println("			exch 0 rlineto");
	writer.println("			neg 0 exch rlineto");
	writer.println("			closepath");
	writer.println("		}{");
	writer.println("			dup length 4 sub 0 exch 4 exch{");
	writer.println("				1 index exch 4 getinterval aload pop");
	writer.println("				BuildRectPath");
	writer.println("			}for");
	writer.println("			pop");
	writer.println("		}ifelse");
	writer.println("} bind def\n");

	writer.println("/*RC { gsave newpath BuildRectPath fill grestore } bind def\n");

	writer.println("% install Level 2 emulations, or substitute built-in Level 2 operators");
	writer.println("/languagelevel where");
	writer.println("  {pop languagelevel}{1} ifelse");
	writer.println("2 lt {");
	writer.println("  /RC /*RC load def");
	writer.println("  /SF /*SF load def");
	writer.println("}{");
	writer.println("  /RC /rectclip load def      % use RC instead of rectclip");
	writer.println("  /SF /selectfont load def    % use SF instead of selectfont");
	writer.println("} ifelse\n");

	writer.println("%Coordinate conversion utilities");
	writer.println("/polar { %(ang rad) -> (x y)");
	writer.println("	/rad exch def		/ang exch def");
	writer.println("	/x rad ang cos mul def		/y rad ang sin mul def");
	writer.println("	x y");
	writer.println("} def\n");

	writer.println("/midang {");
	writer.println("	/inf exch def");
	writer.println("	inf 1 eq {360 2 maxlevel exp div mul -90.0 add}           %for first level male, go counter clockwise from bottom");
	writer.println("				{360 2 maxlevel exp div mul 90.0 add} ifelse     %for first level female, go clockwise from bottom");
	writer.println("} def\n");

	writer.println("%Shortcut macros");
	writer.println("/m {moveto} def		/l {lineto} def\n");

	writer.println("%Constants");
	writer.println("/pi 3.14159265358979 def");
	writer.println("/ptsize 10 def");
	writer.println("/offset ptsize 1.25 mul neg def\n");

	writer.println("/radius {4.0 7.0 div exch indicentre add mul inch} def");

	writer.println("%begin font encoding   borrowed from Robert Simms");
	if(ne(enc_choice, 0)) {
	    writer.println("/encvecmod* {  % on stack should be /Encoding and an encoding array");
	    writer.println("	% make an array copy so we don't try to modify the original via pointer");
	    writer.println("	dup length array copy");
	    writer.println("	encvecmod aload length dup 2 idiv exch 2 add -1 roll exch");
	    writer.println("	{dup 4 2 roll put}");
	    writer.println("	repeat");
	    writer.println("} def");
	    writer.println("/reenc {");
	    writer.println("	findfont");
	    writer.println("	dup length dict begin");
	    writer.println("		{1 index /FID eq {pop pop} {");
	    writer.println("			1 index /Encoding eq {");
	    writer.println("					encvecmod* def");
	    writer.println("				}{def} ifelse");
	    writer.println("			} ifelse");
	    writer.println("		} forall");
	    writer.println("		currentdict");
	    writer.println("	end");
	    writer.println("	definefont pop");
	    writer.println("} def");
	}
	if(eq(enc_choice, 1)) {
	    writer.println("% Adjust the font so that it is iso-8859-1 compatible");
	    writer.println("/languagelevel where {pop languagelevel}{1} ifelse 2 ge {");
	    writer.println("	/encvecmod* {pop ISOLatin1Encoding} def	% Use built-in ISOLatin1Encoding if PS interpreter is Level 2");
	    writer.println("}{");
	    
	    writer.println("	/encvecmod [");
	    writer.println("		16#90 /dotlessi   16#91 /grave        16#92 /acute      16#93 /circumflex");
	    writer.println("		16#94 /tilde      16#95 /macron       16#96 /breve      16#97 /dotaccent");
	    writer.println("		16#98 /dieresis   16#99 /.notdef      16#9a /ring       16#9b /cedilla");
	    writer.println("		16#9c /.notdef    16#9d /hungarumlaut 16#9e /ogonek     16#9f /caron");
	    writer.println("		16#a0 /space      16#a1 /exclamdown   16#a2 /cent       16#a3 /sterling");
	    writer.println("		16#a4 /currency   16#a5 /yen         16#a6 /brokenbar   16#a7 /section");
	    writer.println("		16#a8 /dieresis   16#a9 /copyright   16#aa /ordfeminine 16#ab /guillemotleft");
	    writer.println("		16#ac /logicalnot 16#ad /hyphen      16#ae /registered  16#af /macron");
	    writer.println("		16#b0 /degree     16#b1 /plusminus   16#b2 /twosuperior 16#b3 /threesuperior");
	    writer.println("		16#b4 /acute      16#b5 /mu          16#b6 /paragraph    16#b7 /periodcentered");
	    writer.println("		16#b8 /cedilla    16#b9 /onesuperior 16#ba /ordmasculine 16#bb /guillemotright");
	    writer.println("		16#bc /onequarter 16#bd /onehalf    16#be /threequarters 16#bf /questiondown");
	    writer.println("		16#c0 /Agrave      16#c1 /Aacute    16#c2 /Acircumflex 16#c3 /Atilde");
	    writer.println("		16#c4 /Adieresis   16#c5 /Aring     16#c6 /AE          16#c7 /Ccedilla");
	    writer.println("		16#c8 /Egrave      16#c9 /Eacute    16#ca /Ecircumflex 16#cb /Edieresis");
	    writer.println("		16#cc /Igrave      16#cd /Iacute    16#ce /Icircumflex 16#cf /Idieresis");
	    writer.println("		16#d0 /Eth         16#d1 /Ntilde    16#d2 /Ograve      16#d3 /Oacute");
	    writer.println("		16#d4 /Ocircumflex 16#d5 /Otilde    16#d6 /Odieresis   16#d7 /multiply");
	    writer.println("		16#d8 /Oslash      16#d9 /Ugrave    16#da /Uacute      16#db /Ucircumflex");
	    writer.println("		16#dc /Udieresis   16#dd /Yacute    16#de /Thorn       16#df /germandbls");
	    writer.println("		16#e0 /agrave      16#e1 /aacute    16#e2 /acircumflex 16#e3 /atilde");
	    writer.println("		16#e4 /adieresis   16#e5 /aring     16#e6 /ae          16#e7 /ccedilla");
	    writer.println("		16#e8 /egrave      16#e9 /eacute    16#ea /ecircumflex 16#eb /edieresis");
	    writer.println("		16#ec /igrave      16#ed /iacute    16#ee /icircumflex 16#ef /idieresis");
	    writer.println("		16#f0 /eth         16#f1 /ntilde    16#f2 /ograve      16#f3 /oacute");
	    writer.println("		16#f4 /ocircumflex 16#f5 /otilde    16#f6 /odieresis   16#f7 /divide");
	    writer.println("		16#f8 /oslash      16#f9 /ugrave    16#fa /uacute      16#fb /ucircumflex");
	    writer.println("		16#fc /udieresis   16#fd /yacute    16#fe /thorn       16#ff /ydieresis");
	    writer.println("	] def");
	    writer.println("} ifelse\n");
	} else if(eq(enc_choice, 2)) {
	    
	    writer.println("/encvecmod [");
	    writer.println("	16#a0 /space     16#a1 /Aogonek 16#a2 /breve     16#a3 /Lslash");
	    writer.println("	16#a4 /currency  16#a5 /Lcaron  16#a6 /Sacute    16#a7 /section");
	    writer.println("	16#a8 /dieresis  16#a9 /Scaron  16#aa /Scedilla  16#ab /Tcaron");
	    writer.println("	16#ac /Zacute    16#ad /hyphen  16#ae /Zcaron    16#af /Zdotaccent");
	    writer.println("	16#b0 /degree    16#b1 /aogonek 16#b2 /ogonek    16#b3 /lslash");
	    writer.println("	16#b4 /acute     16#b5 /lcaron  16#b6 /sacute    16#b7 /caron");
	    writer.println("	16#b8 /cedilla   16#b9 /scaron  16#ba /scedilla  16#bb /tcaron");
	    writer.println("	16#bc /zacute    16#bd /hungarumlaut 16#be /zcaron 16#bf /zdotaccent");
	    writer.println("	16#c0 /Racute    16#c1 /Aacute  16#c2 /Acircumflex 16#c3 /Abreve");
	    writer.println("	16#c4 /Adieresis 16#c5 /Lacute  16#c6 /Cacute    16#c7 /Ccedilla");
	    writer.println("	16#c8 /Ccaron    16#c9 /Eacute  16#ca /Eogonek   16#cb /Edieresis");
	    writer.println("	16#cc /Ecaron    16#cd /Iacute  16#ce /Icircumflex 16#cf /Dcaron");
	    writer.println("	16#d0 /Dcroat    16#d1 /Nacute   16#d2 /Ncaron    16#d3 /Oacute");
	    writer.println("	16#d4 /Ocircumflex 16#d5 /Ohungarumlaut 16#d6 /Odieresis 16#d7 /multiply");
	    writer.println("	16#d8 /Rcaron    16#d9 /Uring   16#da /Uacute    16#db /Uhungarumlaut");
	    writer.println("	16#dc /Udieresis 16#dd /Yacute  16#de /Tcommaaccent 16#df /germandbls");
	    writer.println("	16#e0 /racute    16#e1 /aacute  16#e2 /acircumflex 16#e3 /abreve");
	    writer.println("	16#e4 /adieresis 16#e5 /lacute  16#e6 /cacute    16#e7 /ccedilla");
	    writer.println("	16#e8 /ccaron    16#e9 /eacute  16#ea /eogonek   16#eb /edieresis");
	    writer.println("	16#ec /ecaron    16#ed /iacute  16#ee /icircumflex 16#ef /dcaron");
	    writer.println("	16#f0 /dcroat    16#f1 /nacute  16#f2 /ncaron     16#f3 /oacute");
	    writer.println("	16#f4 /ocircumflex 16#f5 /ohungarumlaut 16#f6 /odieresis 16#f7 /divide");
	    writer.println("	16#f8 /rcaron    16#f9 /uring   16#fa /uacute    16#fb /uhungarumlaut");
	    writer.println("	16#fc /udieresis 16#fd /yacute  16#fe /tcommaaccent  16#ff /dotaccent");
	    writer.println("] def\n");
	} else if(eq(enc_choice, 3)) {
	    
	    writer.println("/encvecmod [");
	    writer.println("	16#80 /Ccedilla    16#81 /udieresis 16#82 /eacute      16#83 /acircumflex");
	    writer.println("	16#84 /adieresis   16#85 /agrave    16#86 /aring       16#87 /ccedilla");
	    writer.println("	16#88 /ecircumflex 16#89 /edieresis 16#8a /egrave      16#8b /idieresis");
	    writer.println("	16#8c /icircumflex 16#8d /igrave    16#8e /Adieresis   16#8f /Aring");
	    writer.println("	16#90 /Eacute      16#91 /ae        16#92 /AE          16#93 /ocircumflex");
	    writer.println("	16#94 /odieresis   16#95 /ograve    16#96 /ucircumflex 16#97 /ugrave");
	    writer.println("	16#98 /ydieresis   16#99 /Odieresis 16#9a /Udieresis   16#9b /cent");
	    writer.println("	16#9c /sterling    16#9d /yen       16#9e /.notdef     16#9f /florin");
	    writer.println("	16#a0 /aacute      16#a1 /iacute    16#a2 /oacute      16#a3 /uacute");
	    writer.println("	16#a4 /ntilde      16#a5 /Ntilde    16#a6 /ordfeminine 16#a7 /ordmasculine");
	    writer.println("	16#a8 /questiondown 16#a9 /.notdef  16#aa /.notdef     16#ab /onehalf");
	    writer.println("	16#ac /onequarter  16#ad /exclamdown 16#ae /guillemotleft  16#af /guillemotright");
	    writer.println("	16#e1 /germandbls  16#ed /oslash    16#f1 /plusminus   16#f6 /divide");
	    writer.println("	16#f8 /degree      16#f9 /bullet");
	    writer.println("] def\n");
	}
	if(ne(enc_choice, 0)) {
	    writer.println("/gedfont fontname reenc");
	    writer.println("/fontname /gedfont def\n");
	}
	writer.println("%end font encoding   end of section borrowed from Robert Simms");

	if (gradient){
	    writer.println("/gradient{   %draw and fill 256 circles with a decreasing radius and slightly diffent colour");
	    writer.println("	/blue2 exch def	/green2 exch def	/red2 exch def");
	    writer.println("	/blue1 exch def	/green1 exch def	/red1 exch def\n");
	    writer.println("	/maxrad maxlevel radius def");
	    writer.println("	/delta_r maxrad neg 256 div def                          %find radius step to use\n");
	    writer.println("	gsave");
	    writer.println("		maxrad delta_r 0.0 {                                  %step through the circles from large to small");
	    writer.println("			/r exch def");
	    writer.println("			/ratio r maxrad div def");
	    writer.println("			/red red1 red2 sub ratio mul red2 add def          % work out the new colour");
	    writer.println("			/blue blue1 blue2 sub ratio mul blue2 add def");
	    writer.println("			/green green1 green2 sub ratio mul green2 add def\n");
	    writer.println("			red green blue setrgbcolor");
	    writer.println("			newpath 0.0 0.0 r 0 360 arc fill                   %draw and fill circles");
	    writer.println("		} for");
	    writer.println("	grestore");
	    writer.println("} def\n");
	}
	writer.println("/fan{  %Fan Template");
	writer.println("	gsave");
	if(or(!printmarr,!transparent)){
	    writer.println("	%begin gender specific shading of boxes");
	    writer.println("	/c 1 def                          %flag for the alternating colours");
	    writer.println("	1 indicentre sub 1 maxlevel {%shade the boxes if necessary");
	    writer.println("		/i exch def");
	    writer.println("		/delta_ang 360.0 2 i exp div def  %set the angle stepsize");
	    writer.println("		/r1 i radius def		/r2 i 1 sub radius def        %find the inner and outer radius for the box");
	    if (ge(maxlevel,8)){
		writer.println("		i 8 ge {0}{0.7 radfactor div} ifelse");
	    }else{
		writer.println("		.7 radfactor div");
	    }
	    writer.println(" setlinewidth                %if level is beyond 7 make lines thinnest possible\n");
	    writer.println("		90.0 delta_ang 449.99 { %step through all angles from 90deg to 90deg+360deg (450deg)");
	    writer.println("			/ang1 exch def		/ang2 ang1 delta_ang add def     %find the beginning and ending angle for each box");
	    writer.println("			newpath");
	    writer.println("				i 0 gt{%draw the box");
	    writer.println("					ang1 r1 polar m 0 0 r1 ang1 ang2 arc ang2 r2 polar l 0 0 r2 ang2 ang1 arcn");
	    writer.println("				}{");
	    writer.println("					0 0 1 radius 0 0 1 radius 0 360 arc");
	    writer.println("				}ifelse");
	    writer.println("			closepath");
	    if(!transparent){
		writer.println("				i 0 gt {                              %fill in box if necessary");
		writer.println("					c 1 eq {/c1 0 def rf gf bf setrgbcolor} {/c1 1 def rm gm bm setrgbcolor} ifelse");
		writer.println("				}{");
		writer.println("					centrepersonsex 0 eq {rm gm bm setrgbcolor} {rf gf bf setrgbcolor} ifelse");
		writer.println("				}ifelse");
		writer.println("				gsave fill grestore");
		writer.println("				i 0 gt{/c c1 def}if                                    %exchange color for next box");
		writer.println("			rl gl bl setrgbcolor\n");
	    }
	    if(!printmarr){
		if(!transparent){
		    writer.println("				i 9 le {stroke} if              %draw outline of box if level is less than 10");
		}else{
		    writer.println("				stroke");
		}
	    }
	    writer.println("		}for");
	    writer.println("	}for %end gender specific shading of boxes");
	}
	if (printmarr){
	    writer.println("	%begin draw boxes around husband and wife");
	    writer.println("	rl gl bl setrgbcolor");
	    writer.println("	2 indicentre sub 1 maxlevel {                    %step through the levels");
	    writer.println("		/i exch def");
	    if (ge(maxlevel,8)){
		writer.println("		i 8 ge {0}{0.7 radfactor div} ifelse");
	    }else{
		writer.println("		.7 radfactor div");
	    }
	    writer.println(" setlinewidth\n");
	    writer.println("		/delta_ang 360.0 2 i 1 sub exp div def  %set the angle stepsize");
	    writer.println("		90.0 delta_ang 449.99 {");
	    writer.println("			/ang1 exch def		/ang2 ang1 delta_ang add def");
	    writer.println("			/r1 i radius def	/r2 i 1 sub radius def\n");

	    writer.println("			%draw tic marks around marriage date");
	    writer.println("			/delta_r r1 r2 sub 15 div def");
	    writer.println("			/angave ang1 delta_ang 2 div add def");
	    writer.println("			/r_inner r2 delta_r add def");
	    writer.println("			/r_outer r1 delta_r sub def\n");

	    writer.println("			newpath angave r_outer polar m angave r1 polar l stroke");
	    writer.println("			r2 0 gt{");
	    writer.println("				newpath angave r2 polar m angave r_inner polar l stroke");
	    writer.println("			}if\n");

	    if(!transparent){
		writer.println("			rm gm bm setrgbcolor         %erase small gap between male and female");
		writer.println("			.5 setlinewidth");
		writer.println("			newpath angave r_outer polar m angave r_inner polar l stroke");
		writer.println("			rl gl bl setrgbcolor");
		if (ge(maxlevel,8)){
		    writer.println("		i 8 ge {0}{0.7 radfactor div} ifelse");
		}else{
		    writer.println("		.7 radfactor div");
		}
		writer.println(" setlinewidth");
	    }

	    writer.println("			%finish tic marks\n");

	    writer.println("			newpath	%draw box around parents");
	    writer.println("				ang1 r1 polar m 0 0 r1 ang1 ang2 arc");
	    writer.println("				ang2 r2 polar l 0 0 r2 ang2 ang1 arcn closepath");
	    writer.println("			stroke");
	    writer.println("		}for");
	    writer.println("	}for	%end draw boxes around husband and wife\n");
	}


	if (printdate){
	    writer.println("	0 0 0 setrgbcolor");
	    writer.println("	fontname 5 SF");
	    writer.println("	/radiusprint maxlevel radius 1.01 mul def");
	    writer.println("	datetoday radiusprint 300 circtext");
	}
	writer.println("	grestore");
	writer.println("} def\n");

	writer.println("/angtext{   %Angled Line Printing Procedure for outer lines than do not curve");
	writer.println("	/inf exch def		/offst exch def		/ang exch def		/levelnum exch def		/str exch def\n");

	writer.println("	gsave");
	writer.println("	ang rotate                                               %rotate coordinate system for printing\n");

	writer.println("	/r1 levelnum 1 sub radius def		/r2 levelnum radius def");
	if(printmarr){
	    writer.println("	levelnum 1 eq indicentre 0 eq and{/r1 0 def /r2 0 def}if\n");
	}
	writer.println("	/y r1 r2 add 2 div def\n");

	writer.println("	inf 0 eq{0 offst -10 mul 15 add translate}{y 0.0 translate}ifelse\n");

	writer.println("	str stringwidth pop 2 div neg offst moveto");
	writer.println("	str show");
	writer.println("	grestore");
	writer.println("} def\n");

	writer.println("/circtext{   %Circular Line Printing Procedure for inner lines than do curve\n");

	writer.println("	/angle exch def	/textradius exch def	/str exch def\n");

	writer.println("	/xradius textradius ptsize 4 div add def");
	writer.println("	gsave");
	writer.println("		angle str findhalfangle add rotate");
	writer.println("		str {/charcode exch def ( ) dup 0 charcode put circchar} forall");
	writer.println("	grestore");
	writer.println("} def\n");

	writer.println("/findhalfangle {stringwidth pop 2 div 2 xradius mul pi mul div 360 mul} def\n");

	writer.println("/circchar{   %print each character at a different angle around the circle");
	writer.println("	/char exch def\n");

	writer.println("	/halfangle char findhalfangle def");
	writer.println("		gsave");
	writer.println("		halfangle neg  rotate");
	writer.println("		textradius 0 translate");
	writer.println("		-90 rotate");
	writer.println("		char stringwidth pop 2 div neg 0 moveto");
	writer.println("		char show");
	writer.println("	grestore");
	writer.println("	halfangle 2 mul neg rotate");
	writer.println("} def\n");

	writer.println("/setprintcolor{");
	writer.println("	/ahnen exch def		/inf exch def");
	writer.println("	ahnen 2 div dup cvi eq {redmale greenmale bluemale setrgbcolor}{redfemale greenfemale bluefemale setrgbcolor} ifelse");
	writer.println("	ahnen inf mul 1 eq {redmale greenmale bluemale setrgbcolor} if");
	writer.println("} def\n");

	writer.println("/position{  %compute position from ahnentafel number");
	writer.println("	/ahnenn exch def");
	writer.println("	ahnenn 2 maxlevel -1 add exp lt {");
	writer.println("		/a 2 ahnenn log 1.9999 log div floor exp def");
	writer.println("		/numerator 2 a mul -1 add -2 ahnenn a neg add mul add def");
	writer.println("		/fact 2 maxlevel -2 add exp def");
	writer.println("		numerator a div fact mul");
	writer.println("	}{2 maxlevel exp ahnenn neg add} ifelse");
	writer.println("} def\n");

	writer.println("/level {1 add log 2 log div ceiling cvi} def %compute generation level from ahnentafel number\n");

	writer.println("/info{");
	writer.println("	/max exch def		/inf exch def		/noffset exch def		/ahnen exch def");
	writer.println("	/fntfactor {[0 0.85 0.85 0.8 0.7 0.5 0.4 0.3 0.3 0.25 0.25 0.25 0.25] exch get} def %set different font sizes for each level\n");

	writer.println("	ahnen 2 maxlevel exp lt {");
	writer.println("		/place ahnen position def");
	writer.println("		/levelnum ahnen level def    %get the level number of the current person");
	writer.println("		/radtab levelnum radius def  %get the radius of the current level");
	writer.println("		/ftsize ptsize levelnum fntfactor mul def  %find the new fontsize depending on the current level number");
	writer.println("		/offset ftsize 1.25 mul neg def            %find the distance that the text should be printed from the ring");
	writer.println("		inf ahnen setprintcolor      %print the names and information in alternating colors as defined below in line #350");
	writer.println("		fontname ftsize SF %set the font to use\n");

	writer.println("		levelnum 5 lt {levelnum radtab place noffset inf max inner}  % the inner four rings");
	writer.println("						{levelnum place noffset inf 0 max outer} ifelse  % all outer rings");
	writer.println("	} if");
	writer.println("} def\n");

	if(eq(indicentre,1)){
	    writer.println("/indiinfo{");
	    writer.println("	/inf exch def		/noffset exch def		/ahnen exch def");
	    writer.println("	/ftsize ptsize 0.9 mul def  %find the new fontsize depending on the current level number");
	    writer.println("	/offset ftsize 1.25 mul neg def            %find the distance that the text should be printed from the ring");
	    writer.println("	inf ahnen setprintcolor      %print the names and information in alternating colors as defined below in line #350");
	    writer.println("	fontname ftsize SF %set the font to use\n");

	    writer.println("	0 0 noffset 0 angtext");
	    writer.println("} def\n");
	}

	writer.println("/nstr 7 string def");
	writer.println("/prtn {-0.5 inch 5.5 inch m nstr cvs show} def");
	writer.println("/prt {-0.5 inch 5.5 inch m	show} def\n");

	if (printmarr){
	    writer.println("/minfo{");
	    writer.println("	/inf exch def		/ahnen exch def");
	    writer.println("	/fntfactor {[0 0.7 0.7 0.6 0.6 0.5 0.4 0.3 0.3 0.25 0.25 0.25 0.25] exch get} def %set different font sizes for each level\n");

	    writer.println("	ahnen 2 maxlevel exp lt {");
	    writer.println("		/place ahnen 1 eq {0}{ahnen 2 div position}ifelse def  %get the position of the text counting on the outer ring from bottom upwards");
	    writer.println("		/levelnum ahnen level def   %get the level number of the current person");
	    writer.println("		/ftsize ptsize levelnum fntfactor mul 0.80 mul def  %find the new fontsize depending on the current level number");
	    writer.println("		/offset ftsize 0.35 mul neg def            %find the distance that the text should be printed from the ring");
	    writer.println("		rl gl bl setrgbcolor");
	    writer.println("		dup");
	    writer.println("		/namelength exch length def");
	    writer.println("		/f namelength 11 lt {1}{11 namelength div}ifelse def");
	    writer.println("		fontname ftsize f mul SF %set the font to use\n");

	    writer.println("		levelnum place 0 inf 1 1 outer");
	    writer.println("	} if");
	    writer.println("} def\n");
	}

	writer.println("/inner{");
	writer.println("	/max exch def		/inf exch def		/noffset exch def		/place exch def		/radtab exch def		/levelnum exch def");
	writer.println("	% slight modifications for each level for line spacing");
	if(eq(indicentre,0)){
	    writer.println("		max 3 eq {/factor {[0.0 0.98 0.97 0.97 0.975] exch get} def}if");
	    writer.println("		max 2 eq {/factor {[0.0 0.80 0.885 0.935 0.94] exch get} def}if");
	    writer.println("		max 1 eq {/factor {[0.0 0.70 0.835 0.905 0.91] exch get} def}if\n");
	}
	if(eq(indicentre,1)){
	    writer.println("		max 3 eq {/factor {[0.0 0.96 0.98 0.98 0.975] exch get} def}if");
	    writer.println("		max 2 eq {/factor {[0.0 0.96 0.935 0.945 0.94] exch get} def}if");
	    writer.println("		max 1 eq {/factor {[0.0 0.96 0.905 0.915 0.91] exch get} def}if\n");
	}

	writer.println("	levelnum 1 eq indicentre 0 eq and{/offset offset 0.75 mul def} if  %max the offset a bit smaller for the first level");
	writer.println("	radtab levelnum factor mul noffset offset mul add place inf midang circtext");
	writer.println("} def\n");

	writer.println("/outer{");
	writer.println("	/max exch def	/marr exch def		/inf exch def		/noffset exch def		/place exch def		/levelnum exch def\n");

	writer.println("			% in the following:");
	writer.println("			%      f1 spreads the text out apart from eachother when more positive (larger)");
	writer.println("			%      f2 shifts the set of text counter clockwise when more positive (larger)");
	if(eq(maxlevel,5)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 1.35 def} if}if");
	    writer.println("		max 2 eq {levelnum 5 eq {/f1 -2.5 def	/f2 0.25 def} if}if\n");
	}
	if(eq(maxlevel,6)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 6.50 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.7 def	/f2 1.50 def} if}if");
	    writer.println("		max 2 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.7 def	/f2 1.50 def} if}if\n");
	}
	if(eq(maxlevel,7)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 6.50 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.30 def} if}if");
	    writer.println("		max 2 eq {");
	    writer.println("			 		 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 3.30 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 0.70 def} if}if");
	    writer.println("		max 1 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.30 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -2.0 def	/f2 1.20 def} if}if\n");
	}
	if(eq(maxlevel,8)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 6.50 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.30 def} if}if");
	    writer.println("		max 2 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 3.30 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 2.20 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.7 def	/f2 0.80 def} if}if");
	    writer.println("		max 1 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 3.30 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 1.50 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.7 def	/f2 0.50 def} if}if\n");
	}
	if(eq(maxlevel,9)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 6.50 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.30 def} if}if");
	    writer.println("		max 2 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.00 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 2.00 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.6 def	/f2 1.40 def} if}if");
	    writer.println("		max 1 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.00 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 2.00 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.6 def	/f2 1.40 def} if");
	    writer.println("					 levelnum 9 eq {/f1  0.0 def	/f2 0.00 def} if}if\n");
	}
	if(eq(maxlevel,10)){
	    writer.println("		max 3 eq {levelnum 5 eq {/f1 -2.5 def	/f2 6.50 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.30 def} if}if");
	    writer.println("		max 2 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.00 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 2.00 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.6 def	/f2 1.40 def} if}if");
	    writer.println("		max 1 eq {");
	    writer.println("					 levelnum 5 eq {/f1 -2.5 def	/f2 4.85 def} if");
	    writer.println("					 levelnum 6 eq {/f1 -1.6 def	/f2 4.00 def} if");
	    writer.println("					 levelnum 7 eq {/f1 -1.0 def	/f2 1.70 def} if");
	    writer.println("					 levelnum 8 eq {/f1 -0.6 def	/f2 1.20 def} if");
	    writer.println("					 levelnum 9 eq {/f1  0.0 def	/f2 0.40 def} if");
	    writer.println("					 levelnum 10 ge{/f1  0.0 def	/f2 0.225 def}if}if\n");
	}

	writer.println("	marr 1 eq {/f1 0.0 def		/f2 0.0 def} if\n");

	writer.println("	/ang place inf midang f1 noffset mul f2 add add def");
	writer.println("	levelnum ang offset inf angtext");
	writer.println("} def\n");

	writer.println("%   borrowed from Robert Simms");
	if(eq(indicentre,1)){
	    writer.println("/addcenterindi {centerperson_array 3 1 roll put} def");
	}
	if(printmarr){
	    writer.println("/addmarr {marriage_array 3 1 roll put} def");
	}
	writer.println("/addind {person_array 3 1 roll put} def\n");
    }

    private void main(Indi person, Fam fam) {
	int psex = (person.getSex() == PropertySex.MALE )? 0:1;
	
	
	
	  
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	printfile();

	if (printdate){
	    
	    writer.println("/datetoday (Date: " +PointInTime.getNow().toString()+ ") def\n\n");
	    
	}

	writer.println("/indicentre "+d(indicentre)+" def %1=put individual in centre,0=family at centre");
	if(eq(indicentre,1)){
	    writer.println("/centrepersonsex "+psex+" def %0=male; 1=female\n\n");
	}

	writer.println("/maxlevel "+ d(maxlevel)+ " def");

	writer.println("% color  of the text in RGB format");
	if(eq(colourtext,1)){
	    writer.println("/redmale   0.0 def  /greenmale   0.0 def  /bluemale   1.0 def");
	    writer.println("/redfemale 1.0 def  /greenfemale 0.0 def  /bluefemale 0.0 def\n");
	}else{
	    writer.println("/redmale   0.0 def  /greenmale   0.0 def  /bluemale   0.0 def");
	    writer.println("/redfemale 0.0 def  /greenfemale 0.0 def  /bluefemale 0.0 def\n");
	}

	if (gradient){
	    writer.println("/transparent 1 def         % 1=transparent, 0=color shading\n");

	    writer.println("/rf 0.0 def /gf 0.0 def /bf 0.0 def %rgb female box fill");
	    writer.println("/rm 0.0 def /gm 0.0 def /bm 0.0 def %rgb male box fill\n");

	}else{
	    if (!alternating){
		writer.println("/transparent 1 def         % 1=transparent, 0=color shading\n");

		writer.println("/rf 1.0 def /gf 1.0 def /bf 1.0 def %rgb female box fill");
		writer.println("/rm 1.0 def /gm 1.0 def /bm 1.0 def %rgb male box fill\n");
	    }else{
		writer.println("/transparent 0 def         % 1=transparent, 0=color shading\n");

		writer.println("/rf 0.8 def /gf 0.8 def /bf 1.0 def %rgb female box fill");
		writer.println("/rm 1.0 def /gm 0.8 def /bm 0.8 def %rgb male box fill\n");
	    }
	}
	

	writer.println("/rl 0.0 def /gl 0.0 def /bl 0.0 def %  rgb for lines");

	writer.println("%     partially borrowed from Robert Simms");
	writer.println("% Find printable dimension for chart with a sequence of steps\n");

	writer.println("% get printable area for each page");
	writer.println("clippath pathbbox newpath");
	writer.println("/ury exch def /urx exch def");
	writer.println("/lly exch def /llx exch def\n");

	writer.println("/llx llx margin_left add def /lly lly margin_bottom add def");
	writer.println("/urx urx margin_right sub def /ury ury margin_top sub def\n");

	writer.println("% get available width and height for printing on a sheet of paper");
	writer.println("/wp urx llx sub def");
	writer.println("/hp ury lly sub def\n");

	writer.println("% get width and height of the multi-page printable area");
	writer.println("/tw0 wp xpages mul def");
	writer.println("/th0 hp ypages mul def\n");

	writer.println("tw0 th0 gt {");
	if(eq(radius,0)) {writer.println("	/mindim th0 def\n");}
	writer.println("	th0 wp div ceiling cvi xpages lt {/xpages th0 wp div ceiling cvi def /tw0 wp xpages mul def /ypages ypages def}{/xpages xpages def /ypages ypages def}ifelse");
	writer.println("}{");
	if(eq(radius,0)) {writer.println("	/mindim tw0 def\n");}
	writer.println("	tw0 hp div ceiling cvi ypages lt {/ypages tw0 hp div ceiling cvi def /th0 hp ypages mul def /xpages xpages def}{/xpages xpages def /ypages ypages def}ifelse");
	writer.println("}ifelse\n");

	if(gt(radius,0)) {
	    writer.println("/radfactor " +d(radius)+ " inch 8 inch div def");
	}else{
	    writer.println("/radfactor mindim 8 inch div def");
	}
	writer.println("/scalefactor 7.0 maxlevel indicentre add div radfactor mul def\n");

	writer.println("/print-a-page { % page printing procedure");
	writer.println("	/ypage exch ypages 2 div 1 sub sub def  %y-correction to center chart");
	writer.println("	/xpage exch xpages 2 div 1 sub sub def  %x-correction to center chart");
	writer.println("	ypage ypages lt xpage xpages lt and { %only print if page is in correct range");
	writer.println("		gsave");
	writer.println("			llx lly translate");
	writer.println("			0 0 wp hp RC		% specify (rectangular) clipping path to keep the margins clean");
	writer.println("			xpage wp mul ypage hp mul translate	% move origin so that desired portion of chart lands within clipping path");
	writer.println("			scalefactor dup scale  %enlarge scale to fit page");
	if (gradient){
	    writer.println("0.6431 0.3255 0.0228  % inside centre color in RGB format");
	    writer.println("0.9922 0.7686 0.5490  % outside rim color in RGB format    to form a radial gradient");
	    writer.println("gradient\n");
	}
	writer.println("			fan  %draw circle template");
	if(eq(indicentre,1)){
	    writer.println("			centerperson_array {exec indiinfo} forall %put in center person\n");
	}
	writer.println("			person_array {exec info} forall %put in all people with dates");
	if(printmarr) {
	    writer.println("			marriage_array {exec minfo} forall %put in marriage dates\n");
	}
	writer.println("			1 dup scale %reset scale to normal");
	writer.println("		grestore");
	writer.println("	} if");
	writer.println("} def      % print-a-page procedure\n");

	writer.println("%%EndProlog");
	writer.println("%%BeginSetUp\n");

	writer.println("/fillarray{% store vertical lines and individual records in arrays");

	if(eq(indicentre,1)){
	    PersonCell pCell = new PersonCell();
	    int index=0;
	    index+=pCell.add(surname(person),index);
	    index+=pCell.add(put_given_name(person,25),index);
	    index+=pCell.add(getDateString(birth(person),death(person),dateformat),index);
	    writer.print(pCell.getCenter(psex));
	    semicirc(parents(person),father(person),1,1,1,maxlevel,dateformat);
	    semicirc(parents(person),mother(person),1,1,2,maxlevel,dateformat);
	}else{
	    semicirc(fam,husband(fam),1,1,1,maxlevel,dateformat);
	    semicirc(fam,wife(fam),1,1,2,maxlevel,dateformat);
	}
	writer.println("} def\n");

	if(eq(indicentre,1)){
	    writer.println("/centerperson_array 3 array def\n");
	}
		if(printmarr){
	    writer.println("/marriage_array "+d(add(nummarr,1))+" array def\n");
	}
	writer.println("/person_array "+numindilines+" array def");

	writer.println("fillarray\n");

	writer.println("mark\n");
	writer.println("%%EndSetUp");
	putpageprintouts(x_pages,y_pages);
	writer.println("%%EOF");
    }

    private String getDateString(PropertyDate birth, PropertyDate death, int dateformat){
	String result = "";
	String birthstr = "";
	String deathstr = "";
	if (birth != null && !birth.isValid()) birth = null;
	if (death != null && !death.isValid()) death = null;
	if (birth == null && death == null) return "";
	switch (dateformat){
	case 2:
	    birthstr = "    ";
	    deathstr = "    ";
	    break;
	case 1:
	    if (death != null){
		deathstr = year(death);
	    }
	    if (birth != null){
		birthstr = year(birth);
	    }
	    result = "("+birthstr+"-"+deathstr+")";
	    break;
	default:
	    if (death != null){
		if (birth != null){
		    result += year(birth);
		}
		result += "-"+year(death);
	    } else {
		if (birth != null){
		    result += OPTIONS.getBirthSymbol()+birth.getDisplayValue();
		}
	    }
	    break;
	}
	return result;
    }

  class PersonCell {
    List cells;
    int max;
    PersonCell(){
        cells = new ArrayList(5);
        max = 0;
    }
    int add(String text,int index){
        if (text == null || text.length() == 0) return 0;
        cells.add(""+index+" {("+text);
        max++;
        return 1;
    }
    String get(int ahnen,int info){
        String result="";
        int offset;
        for (offset=1; offset<=max; offset++){
        result += (String)(cells.get(offset-1))+") "+ahnen+" "+offset+" "+info+" "+max+"} addind\n";
        }
        return(result);
    }
    String getCenter(int sex){
        String result="";
        int offset;
        for (offset=1; offset<=max; offset++){
        result += (String)(cells.get(offset-1))+") "+sex+" "+offset+" 0} addcenterindi\n";
        }
        return(result);
    }
  }
}    

