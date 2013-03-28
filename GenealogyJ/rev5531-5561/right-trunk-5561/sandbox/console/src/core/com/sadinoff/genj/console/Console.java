

package com.sadinoff.genj.console;
import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertySex;
import genj.io.GedcomReader;
import genj.io.GedcomWriter;
import genj.util.Origin;
import genj.util.Resources;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Console {
    
    private static final boolean SOUND = Boolean.getBoolean("console.sound");
    protected static final String LB = System.getProperty("line.separator"); 
    private static final boolean DEBUG = Boolean.getBoolean("console.debug");
    

    
    private Resources resources = Resources.get(this);
    protected final Gedcom gedcom;
    protected final LineSource in;
    protected final PrintWriter out;

    
    protected  final Map<Integer, String> sexMap  = new HashMap<Integer,String>();
    {
        sexMap.put(PropertySex.MALE,resources.getString("sex-indicator.male")); 
        sexMap.put(PropertySex.FEMALE,resources.getString("sex-indicator.female")); 
        sexMap.put(PropertySex.UNKNOWN,resources.getString("sex-indicator.unknown")); 
    }

    
    public Console(Gedcom gedcomArg, final BufferedReader userInput, final PrintWriter output) {
        out = output;
        in = new BufferedReaderSource(userInput, out);
        gedcom = gedcomArg;
        setPrompt();
    }

    private void setPrompt()
    {
        in.setPrompt("> ");    
    }
    
    public Console(Gedcom gedcomArg, boolean useReadLine) throws IOException
    {
        gedcom = gedcomArg;
        out = new PrintWriter(new OutputStreamWriter(System.out, "UTF-8")); 
        if( useReadLine )
        {
            ReadLineSource rlSource = new ReadLineSource();
            in = rlSource;
        }
        else
        {
            in = new BufferedReaderSource(new BufferedReader(new InputStreamReader(System.in,"UTF-8")), out);  
        }
        setPrompt();
    }
    
    public Console(Gedcom gedcomArg) throws IOException {
         this(gedcomArg,Boolean.getBoolean("console.use-readline"));
    }

    
    
    enum UIFeedbackType{ SYNTAX_ERROR, MOTION_SIDEWAYS, MOTION_UP, MOTION_DOWN, MOTION_SPOUSE,
                       MOTION_HYPERSPACE,
                       HIT_WALL,
                       SET_VALUE,
                       NOT_FOUND,
                       STALL,
                       MISSILE_LOCK,
                       INTERSECT_GRANITE_CLOUD, 
    };

    
    public void giveFeedback(UIFeedbackType event) {
        if (! SOUND)
            return;
      switch(  event)
      {
      case SYNTAX_ERROR:
          AudioUtil.play(resources.getString("soundfile.syntax-error")); 
          break;
      case MOTION_HYPERSPACE:
          AudioUtil.play(resources.getString("soundfile.jump")); 
          break;
      case HIT_WALL:
          AudioUtil.play(resources.getString("soundfile.hit-wall")); 
          break;
      case SET_VALUE:
          AudioUtil.play(resources.getString("soundfile.set-value")); 
          break;
      default:
      }
    }
    
    
    public static void main(String[] args)  
        throws Exception
    {
        Resources resources = Resources.get("com.sadinoff.genj.console"); 

        if( args.length< 1)
        {
            System.err.println(resources.getString("usage")+ Console.class.getName() +" filename "); 
            System.err.println("       java [classpath_options] "+ Console.class.getName() +" -u URL"); 
            System.exit(1);
        }

        Origin origin;
        if( args.length ==2 )
        {
            if(! args[0].equals("-u")) 
            {
                System.err.println(resources.getString("startup.unknown-option")+args[0]); 
                System.err.println("usage: java [classpath_options] "+ Console.class.getName() +" filename "); 
                System.err.println("       java [classpath_options] "+ Console.class.getName() +" -u URL"); 
                System.exit(1);
            }
            URL url = new URL(args[0]);
            origin = Origin.create(url);
        }
        else
        {
            origin = Origin.create(new File(args[0]).toURL());
        }
        

        GedcomReader reader = new GedcomReader(origin);
        Gedcom gedcom = reader.read();
        Console tt = new Console(gedcom);
        tt.go();
    }

    interface Action
    {
        enum ArgType{ARG_NO, ARG_YES,ARG_OPTIONAL};
        Indi doIt(Indi theIndi, String arg) throws Exception;
        String getDoc();
        ArgType getArgUse();
        String getArgName();
        boolean modifiesDatamodel();
    }

    abstract class ActionHelper implements Action
    {
        public ArgType getArgUse(){ return ArgType.ARG_NO; }
        public String getArgName(){ return null; }
        public boolean modifiesDatamodel() { return false; } 
    }

    
    final protected int parseInt(String arg, int nullDefault) {
        if (null == arg)
                return nullDefault;

        return Integer.parseInt(arg);
    }

    private List<String> resourceGetList(String key, String[] defaultList)
    {
        List<String> ret = new ArrayList<String>();
        for(int i=1; ;i++)
        {
            String actualKey = key +"."+i; 
            String val = resources.getString(actualKey,false);
            if( null == val)
                break;
            ret.add(val);
        }
        if( ret.size() == 0)
            return Arrays.asList(defaultList);
        return ret;
    }
    
    private List<String> resourceGetList(String key, String defaultSingleVal)
    {
        return resourceGetList(key, new String[] {defaultSingleVal});
    }
    
    
    protected Map<List<String>,Action> getActionMap()
    {
        final Map<List<String>,Action>  actionMap = new LinkedHashMap<List<String>,Action>();
        

        actionMap.put(resourceGetList("version.command", "version"), new ActionHelper(){public Indi doIt(Indi ti, String arg){ 
            out.println(getVersion());
            return ti;}
        public String getDoc() {return resources.getString("version.help");} 
            });
        
        
        actionMap.put(resourceGetList("help.command", "help"), new ActionHelper(){public Indi doIt(Indi ti, String arg){ 
            out.println(getHelpText(actionMap));
            return ti;}
        public String getDoc() {return resources.getString("help.help", "print this help message");} 
            });
        
        
        actionMap.put(resourceGetList("exit.command", "exit"), new ActionHelper(){ 
            public Indi doIt(Indi ti, String arg) throws IOException  {
                if( ! gedcom.hasUnsavedChanges())
                    System.exit(0);
                out.println();
                out.print(resources.getString("exit.unsaved-changes")); 
                out.flush();
                String line = in.readLine();
                if( null != line && line.toLowerCase().startsWith(resources.getString("yesno.yes")))  
                    System.exit(0);
                out.println(resources.getString("exit.try-save-filename")); 
                return ti;
            }
                public String getDoc(){return resources.getString("exit.help", "quit the program");} 
            });

        

        
        actionMap.put(resourceGetList("save.command","save"), new Action(){ 
            
            public Indi doIt(Indi ti, String arg){
                try{
                    File saveTo = new File( arg );
                    String  fname = saveTo.getName();
                    File canon = saveTo.getCanonicalFile();
                    File parentdir = canon.getParentFile();
                    File tempFile = File.createTempFile(fname,"",parentdir); 
                    
                    OutputStream fos = new BufferedOutputStream(new FileOutputStream(tempFile));
                    
                    GedcomWriter writer = new GedcomWriter(gedcom,arg,null,fos);                    
                    writer.write(); 
                    if(!  tempFile.renameTo(saveTo) )
                        throw new Exception(resources.getString("save.error.unable-to-rename")+tempFile+" to "+saveTo); 
                    out.println(resources.getString("save.wrote-file-successfully",arg)); 
                    out.println(resources.getString("save.remember")); 
                    gedcom.setUnchanged();
                }
                catch( Exception e)
                {
                    out.println(resources.getString("save.error.io-error")+e); 
                    giveFeedback(UIFeedbackType.INTERSECT_GRANITE_CLOUD);
                }
                return ti;
            }
            public boolean modifiesDatamodel() { return true; } 

        public String getDoc() { return resources.getString("save.help"); } 
        public ArgType getArgUse() { return ArgType.ARG_YES; }
        public String getArgName() { return resources.getString("save.arg");} 
        });
        
        actionMap.put(resourceGetList("undo.command","undo"), new Action(){ 
            
            public Indi doIt(Indi ti, String arg){
                String oldID = ti.getId();
                try
                {
                    gedcom.undo();
                }
                catch( Exception e)
                {
                    out.println(resources.getString("undo.error.couldn't-undo")+ e); 
                    giveFeedback(UIFeedbackType.INTERSECT_GRANITE_CLOUD);
                }
                
                
                ti = (Indi)gedcom.getEntity(oldID);
                
                if( null == ti )
                    out.println(resources.getString("undo.warn.returning-to-root")); 
                return (Indi)gedcom.getFirstEntity(Gedcom.INDI);
            }
            public boolean modifiesDatamodel() { return false; } 

        public String getDoc() { return resources.getString("undo.help");} 
        public ArgType getArgUse() { return ArgType.ARG_NO; }
        public String getArgName() { return "";} 
        });

        actionMap.put(resourceGetList("look.command","look"), new ActionHelper() 
                {
                    public Indi doIt(final Indi ti ,final String targetID){
                        if( targetID != null && targetID.length()>0)
                        {
                            Indi target = (Indi)gedcom.getEntity("INDI", targetID); 
                            if( null == target)
                                out.println(resources.getString("look.no-record")); 
                            else
                                out.println(dump(target));
                        }
                        else
                            out.println(dump(ti));
                        return ti;
                    }
                    public String getDoc(){return resources.getString("look.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_OPTIONAL; }
                    public String getArgName() { return resources.getString("look.arg");} 
                    });        
        
        actionMap.put(resourceGetList("gind.command","gind"), new Action() 
                {
                    public Indi doIt(final Indi ti ,final String targetID){
                        Entity  newEntity = gedcom.getEntity("INDI", targetID); 
                        if (null == newEntity )
                        {
                            out.println(resources.getString("error.can't-find-entity")+targetID); 
                            return ti;
                        }
                        Indi newInd = (Indi)newEntity;
                        giveFeedback(UIFeedbackType.MOTION_HYPERSPACE);                                                    
                        return newInd;
                    }
                    public boolean modifiesDatamodel() { return false; } 
                    public String getDoc(){return resources.getString("goto.help");} 
                    public ArgType getArgUse() {  return ArgType.ARG_YES;}
                    public String getArgName() {  return resources.getString("goto.arg"); } 
                });

        actionMap.put(resourceGetList("search.command","search"), new Action() 
                {
                    public Indi doIt(final Indi ti ,final String searchArg){
                        out.println(resources.getString("search.results.start")); 
                        for( Object entity : gedcom.getEntities("INDI")) 
                        {
                            Indi candidate = (Indi)entity;
                            if( candidate.getName().toLowerCase().contains(searchArg.toLowerCase()))
                                out.println("  "+candidate); 
                        }
                        out.println(resources.getString("search.results.end")); 
                        out.println();
                        return ti;
                    }
                    public boolean modifiesDatamodel() { return false; } 
                    public String getDoc(){return resources.getString("search.help");} 
                    public ArgType getArgUse() {  return ArgType.ARG_YES;}
                    public String getArgName() {  return resources.getString("search.arg"); } 
                });
        
        
        actionMap.put(resourceGetList("gdad.command","gdad"), new ActionHelper() 
                {
                    public Indi doIt(final Indi ti , String arg){
                        Indi dad = ti.getBiologicalFather();
                        if( null == dad)
                        {   
                            out.println(resources.getString("gdad.error.no-dad")); 
                            giveFeedback(UIFeedbackType.HIT_WALL);                            
                            return ti;
                        }
                        else
                            return dad;
                    }
                    public String getDoc(){return resources.getString("gdad.help");} 
                });        
        
        actionMap.put(resourceGetList("gmom.command","gmom"), new ActionHelper() 
                {
                    public Indi doIt(final Indi ti, String arg){
                        Indi mom = ti.getBiologicalMother();
                        if( null == mom)
                        {   
                            out.println(resources.getString("gmom.error.nomom")); 
                            giveFeedback(UIFeedbackType.HIT_WALL);                            
                            return ti;
                        }
                        else
                            return mom;
                    }
                    public String getDoc(){return resources.getString("gmom.help");} 
                });
        
        actionMap.put(resourceGetList("gspo.command","gspo"),new Action() 
                {

                    public Indi doIt(Indi theIndi, String arg) {
                        Fam[] marriages = theIndi.getFamiliesWhereSpouse();
                        if( marriages.length ==0)
                        {
                            out.println(resources.getString("gspo.error.notmarried")); 
                            giveFeedback(UIFeedbackType.HIT_WALL);                            
                            return theIndi;
                        }
                        
                        int targetMarriage;
                        try
                        {
                            int marriageArg = parseInt(arg,1);
                            targetMarriage = marriageArg -1;
                        }
                        catch(NumberFormatException nfe)
                        {
                            out.println(resources.getString("gspo.error.cantparse")+arg+" as a number"); 
                            giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                            return theIndi;
                        }
                        return  marriages[targetMarriage].getOtherSpouse(theIndi);
                    }

                    public boolean modifiesDatamodel() { return false; } 
                    
                    public String getDoc() {return resources.getString("gspo.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_OPTIONAL;}
                    public String getArgName() { return resources.getString("gspo.arg");} 
                    });

        
        actionMap.put(resourceGetList("gsib.command","gsib"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String arg) {
                        Fam bioKidFamily = theIndi.getFamilyWhereBiologicalChild();
                        if( null == bioKidFamily)
                        {
                            out.println(resources.getString("gsib.not-a-kid-in-biofamily")); 
                            giveFeedback(UIFeedbackType.HIT_WALL);                            
                            return theIndi;
                        }
                        Indi[]sibs =  bioKidFamily.getChildren();
                        if( arg == null || arg.length() == 0)
                        {
                            
                            int myIndex =-1; 
                            for( int i =0; i<sibs.length; i++)
                                if( sibs[i]==theIndi)  
                                    myIndex =i;
                            if( myIndex == -1)
                            {
                                out.println(resources.getString("gsib.error.cant-find-myself")); 
                                return theIndi;
                            }
                            return sibs[(myIndex+1)%sibs.length];
                        }
                        else
                        {
                            try 
                            {
                                int kidNumber = parseInt(arg, 0);
                                if( kidNumber <1 || kidNumber > sibs.length)
                                {
                                    out.println("bad sib number"); 
                                    giveFeedback(UIFeedbackType.NOT_FOUND);
                                    return theIndi;
                                }
                                return sibs[kidNumber-1];
                            }
                            catch(NumberFormatException nfe)
                            {
                                out.println(resources.getString("error.cant-parse-arg-as-number")+arg+" as a number"); 
                                giveFeedback(UIFeedbackType.SYNTAX_ERROR);                                
                                return theIndi;
                            }
                        }
                    }
                    public boolean modifiesDatamodel() { return false; } 
                    public String getDoc() {    return resources.getString("gsib.help");}  
                    public ArgType getArgUse() {return ArgType.ARG_OPTIONAL;}
                    public String getArgName() { return "N";} 

                });
   
        actionMap.put(resourceGetList("gchi.command","gchi"), new Action() 
                {
                    
                public String getDoc() {    return resources.getString("gkid.help");} 
                public ArgType getArgUse() {return ArgType.ARG_OPTIONAL;}
                public String getArgName() { return resources.getString("gkid.arg");} 
                public boolean modifiesDatamodel() { return false; } 
            public Indi doIt(Indi theIndi, String arg) {
                
                
                Indi[] children=  theIndi.getChildren();
                if(0==children.length)
                {
                    out.println(resources.getString("gkid.error.no-kids"));  
                    giveFeedback(UIFeedbackType.HIT_WALL);                            
                    return theIndi;
                }
                if( null == arg || arg.length()==0)
                    return children[0];
                try 
                {
                    if( Character.isDigit(arg.charAt(0)))
                    {
                        int kidNumber = Integer.parseInt(arg);
                        if( kidNumber <1 || kidNumber > children.length)
                        {
                            out.println(resources.getString("gkid.error.bad-sib-number"));  
                            giveFeedback(UIFeedbackType.NOT_FOUND);                            
                            return theIndi;
                        }
                        return children[kidNumber-1];
                    }
                    else
                    {
                        Indi foundKid =null;
                        for( int i =0; i< children.length;i++)
                        {
                            Indi kid = children[i];
                            if( ! kid.getFirstName().equals(arg))
                                continue;
                            if( null== foundKid)
                                foundKid = kid;
                            else
                            {
                                out.println(resources.getString("gkid.error.two-or-more-kids-named",arg)); 
                                giveFeedback(UIFeedbackType.NOT_FOUND);
                                return theIndi;
                            }
                        }
                        if (null == foundKid)
                        {
                            out.println(resources.getString("gkid.error.no-kid-named",arg)); 
                            giveFeedback(UIFeedbackType.NOT_FOUND);
                            return theIndi;
                        }
                        return foundKid;
                    }
                }
                catch(NumberFormatException nfe)
                {
                    out.println(resources.getString("gkid.error.cant-parse-arg")+arg+"] as a number"); 
                    giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                }
                return theIndi;
            }
                });
        

        actionMap.put(resourceGetList("cbro.command","cbro"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Indi newSib =  createBiologicalSibling(ti,PropertySex.MALE);
                        if(null != arg && arg.length() > 0)
                            setFirstName(newSib, arg);
                        return newSib;
                    }

                    public String getDoc(){return resources.getString("cbro.help");}  
                    public ArgType getArgUse() {return ArgType.ARG_OPTIONAL;}
                    public String getArgName() {return "FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });

        actionMap.put(resourceGetList("csis.command","csis"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Indi newSib =  createBiologicalSibling(ti,PropertySex.FEMALE);
                        if(null != arg && arg.length() > 0)
                            setFirstName(newSib, arg);
                        return newSib;
                    }
                        
                    public String getDoc(){return "Create a biological sister [with first name FNAME]";} 
                    public ArgType getArgUse() {return ArgType.ARG_OPTIONAL;}
                    public String getArgName() {return "FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });

        actionMap.put(resourceGetList("cson.command","cson"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        int marriageNumber =1;
                        boolean numeric = false;
                        if( null == arg || arg.length() ==0 || Character.isDigit(arg.charAt(0)))
                        {
                            marriageNumber = parseInt(arg,1);
                            numeric = true;
                        }
                        Indi kid = createChild(ti,marriageNumber-1,PropertySex.MALE);
                        if( ! numeric )
                            setFirstName(kid,arg);
                        return kid;
                    }
                        
                    public String getDoc(){return resources.getString("cson.help");} 

                    public ArgType getArgUse() {return ArgType.ARG_OPTIONAL;}

                    public String getArgName() {return "N/FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });
        
        actionMap.put(resourceGetList("cdaut.command","cdaut"), new Action(){ 
            public Indi doIt(final Indi ti, String arg) throws GedcomException{
                int marriageNumber =1 ;
                boolean numeric = false;
                if( null == arg || arg.length() ==0 || Character.isDigit(arg.charAt(0)))
                {
                    marriageNumber = parseInt(arg,1);
                    numeric = true;
                }
                Indi kid = createChild(ti,marriageNumber-1,PropertySex.FEMALE);
                if( ! numeric )
                    setFirstName(kid,arg);
                return kid;

            }
                    
            public String getDoc(){return resources.getString("cdau.help");}  
            public ArgType getArgUse() { return ArgType.ARG_OPTIONAL;}
            public String getArgName() { return "N/FNAME";} 
            public boolean modifiesDatamodel() { return true; } 
        });

        
        actionMap.put(resourceGetList("cspou.command","cspou"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Indi spouse = createFamilyAndSpouse(ti);
                        if(null != arg && arg.length() > 0 )
                            setFirstName(spouse,arg);
                        return spouse;
                    }
                    public String getDoc(){return resources.getString("cspo.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_OPTIONAL;}
                    public String getArgName() { return "FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });

        
        actionMap.put(resourceGetList("cdad.command","cdad"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Indi parent= createParent(ti,PropertySex.MALE);
                        if(null != arg && arg.length() > 0 )
                            setFirstName(parent,arg);
                        return parent;
                    }
                    public String getDoc(){return resources.getString("cdad.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_OPTIONAL;}
                    public String getArgName() { return "FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });
        actionMap.put(resourceGetList("cmom.command","cmom"), new Action() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Indi parent= createParent(ti,PropertySex.FEMALE);
                        if(null != arg && arg.length() > 0 )
                            setFirstName(parent,arg);
                        return parent;
                    }
                    public String getDoc(){return "Create and goto a mother [with first name FNAME]";} 
                    public ArgType getArgUse() { return ArgType.ARG_OPTIONAL;}
                    public String getArgName() { return "FNAME";} 
                    public boolean modifiesDatamodel() { return true; } 
                });

                
        actionMap.put(resourceGetList("rsib.command","rsib"), new Action(){ 
                    public Indi doIt(final Indi ti, final String existingSibID) throws GedcomException{
                        Fam theFam = getCreateBiologicalFamily(ti);
                        Indi existingSib = (Indi)gedcom.getEntity("INDI", existingSibID); 
                        if (null == existingSib)
                        {
                            System.out.println(resources.getString("error.can't-find-individual-named",new Object[]{existingSibID})); 
                            return ti;
                        }
                        Fam existingFam = existingSib.getFamilyWhereBiologicalChild();
                        if( null != existingFam )
                        {
                            out.println(resources.getString("rsib.error-already-in-family",new Object[] {existingSib,ti, existingFam})); 
                            return ti;
                        }
                        theFam.addChild(existingSib);
                        return existingSib;
                    }
                    public String getDoc(){return resources.getString("rsib.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_YES;}
                    public String getArgName() { return resources.getString("rsib.arg");} 
                    public boolean modifiesDatamodel() { return true; } 
                });        
        
        actionMap.put(resourceGetList("del.command","del"), new ActionHelper() 
                {
                    public Indi doIt(final Indi ti, String arg) throws GedcomException{
                        Fam[] famsc = ti.getFamiliesWhereChild();
                        Fam[] famss = ti.getFamiliesWhereSpouse();
                        gedcom.deleteEntity(ti);
                        for (Fam fam : famsc)
                            fam.getChildren();
                        for(Fam fam: famss)
                        {
                            fam.getHusband();
                            fam.getWife();
                        }
                            
                        
                      out.println(resources.getString("del.returning-to-root")); 
                      return (Indi)gedcom.getFirstEntity(Gedcom.INDI);
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc(){return resources.getString("del.help");} 
                });
        
        

        actionMap.put(resourceGetList("sname.command","sname"), new Action() 
                {
            final Pattern firstLastPat = Pattern.compile("((\\S+\\s+)+)(\\S+)"); 
                    public Indi doIt(Indi theIndi, String arg) {
                        Matcher firstLastMatcher = firstLastPat.matcher(arg);
                        if( ! firstLastMatcher.find())
                        {
                            giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                            out.println(resources.getString("snam.error.syntax"));  
                        }
                        String first = firstLastMatcher.group(1).trim();
                        String last = firstLastMatcher.group(3);
                        theIndi.setName(first, last);
                        return theIndi;
                    }
                    
                    public String getDoc() {return resources.getString("snam.help");}  
                    public ArgType getArgUse() { return ArgType.ARG_YES;}
                    public String getArgName() { return resources.getString("snam.arg"); } 
                    public boolean modifiesDatamodel() { return true; 
                    } 
                });

        actionMap.put(resourceGetList("sfnm.command","sfnm"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String arg) {
                        theIndi.setName(arg,theIndi.getLastName());
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("fnam.help");}  
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return resources.getString("fnam.arg");}  
                });
        actionMap.put(resourceGetList("slnm.command","slnm"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String arg) {
                        theIndi.setName(theIndi.getFirstName(), arg);
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("lnam.help");}  
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return resources.getString("lnam.arg");} 
                });

        actionMap.put(resourceGetList("ssex.command","ssex"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String newSex) {
                        newSex = newSex.toLowerCase();
                        if(newSex.equals(resources.getString("ssex.M")))  
                            theIndi.setSex(PropertySex.MALE);
                        else if( newSex.equals(resources.getString("ssex.F")))  
                            theIndi.setSex(PropertySex.FEMALE);
                        else if(newSex.equals(resources.getString("ssex.U")))  
                            theIndi.setSex(PropertySex.UNKNOWN);
                        else
                            out.println(resources.getString("ssex.error.input"));  
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("ssex.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return "S";} 
                });

        actionMap.put(resourceGetList("bday.command","bday"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String arg) {
                        PropertyDate date =theIndi.getBirthDate(true) ;
                        setDate(date, arg);
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("bday.help");}  
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return "BDAY";} 
                });


        actionMap.put(resourceGetList("dday.command","dday"), new Action() 
                {
                    public Indi doIt(Indi theIndi, String arg) {
                        PropertyDate date =theIndi.getDeathDate(true) ;
                        setDate(date, arg);
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("dday.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return resources.getString("dday.arg");}  
                });

        
        actionMap.put(resourceGetList("mday.command","mday"), new Action() 
                {
                    final Pattern mdayPat = Pattern.compile("(?:#(\\d+)\\s+)?(\\p{Alnum}.*)"); 

                    public Indi doIt(Indi theIndi, String arg) {
                        Matcher matcher = mdayPat.matcher(arg);
                        if( ! matcher.matches())
                        {
                            giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                            out.println("arg to mday has bad syntax! [" +arg+"]");
                            return theIndi;
                        }
                        final String marNumStr = matcher.group(1);
                        final String mdateArg  = matcher.group(2);
                        final int marNumArg = parseInt(marNumStr, 1);
                        final int familyIndex = marNumArg -1;
                        Fam[] fams = theIndi.getFamiliesWhereSpouse();
                        if( fams.length <1)
                        {
                            giveFeedback(UIFeedbackType.NOT_FOUND);
                            out.println("This Individual is not a spouse in a Family");
                            return theIndi;
                        }
                        if( marNumArg > fams.length)
                        {
                            giveFeedback(UIFeedbackType.NOT_FOUND);
                            out.println("Marriage/Family number is out of range:"+ marNumArg);
                            return theIndi;
                        }
                        Fam theFam = fams[familyIndex];
                        PropertyDate mdateProperty = theFam.getMarriageDate(true);
                        setDate(mdateProperty, mdateArg);
                        return theIndi;
                    }
                    public boolean modifiesDatamodel() { return true; } 
                    public String getDoc() { return resources.getString("mday.help");} 
                    public ArgType getArgUse() { return ArgType.ARG_YES ; }
                    public String getArgName() { return resources.getString("mday.arg");}  
                });
        
        
        return actionMap;
    }
    
    
    public void go()  throws Exception{
        Indi theIndi = (Indi)gedcom.getFirstEntity(Gedcom.INDI);

        final Map<List<String>,Action> actionMap = getActionMap();
        final Map<String, Action> commandToAction= expandActionMap(actionMap);

        final Pattern commandPat = Pattern.compile("^(\\p{Alnum}+)(\\s+(\\S.*))?"); 
        for(;;)
        {
            out.println("------"); 
            out.print(resources.getString("you-are-at")); 
            out.println(brief(theIndi));
            out.flush();
            final String line;
            {
                final String inputResult = in.readLine();
                if( null==inputResult)
                    continue;
                line = inputResult.trim();
            }
            if( line.length() ==0)
                continue;
            Matcher lineMatcher = commandPat.matcher(line);
            if( ! lineMatcher.matches())
            {
                giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                out.println(resources.getString("error.syntax-error")); 
                continue;
            }
            String command = lineMatcher.group(1);
            String args = lineMatcher.group(3);
            if( DEBUG )
                out.println("cmd=["+command+"], args=["+args+"]"); 
            if( ! commandToAction.containsKey(command) ) {
                out.println(resources.getString(resources.getString("error.unknown-command", command))); 
                giveFeedback(UIFeedbackType.SYNTAX_ERROR);
                continue;
            }
            Action action = commandToAction.get(command);
            try
            {
                if(action.modifiesDatamodel())
                {
                    gedcom.startTransaction();
                }
                theIndi = action.doIt(theIndi, args);
            }
            catch( Exception re)
            {
                out.println(resources.getString("error.exception")+re);  
                
                re.printStackTrace();
            }
            finally
            {
                if( action.modifiesDatamodel())
                {
                    gedcom.endTransaction();
                }
            }
        }
    }

    private Map<String, Action> expandActionMap(Map<List<String>, Action> actionMap) {
        Map<String,Action>  theMap = new HashMap<String, Action>();
        for(Map.Entry<List<String>,Action> entry  : actionMap.entrySet())
            for( String command : entry.getKey())
            {
                if( theMap.containsKey(command))
                {
                    throw new RuntimeException(resources.getString("error.configerr")+command);  
                }
                theMap.put(command, entry.getValue());
            }
        return theMap;
    }

    
    private String dump(Fam fam)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(fam);
        PropertyDate mdate = fam.getMarriageDate();
        if( null != mdate)
            buf.append(" {"+mdate+"}");
        buf.append(LB);
        buf.append(resources.getString("dump.husband")+ fam.getHusband()+LB);  
        buf.append(resources.getString("dump.wife")+fam.getWife()+LB);      
        for( Indi child: fam.getChildren() )
        {
            buf.append("\t"); 
            buf.append(child.toString());
            buf.append(LB);
        }
        buf.append("\t"); 
        return buf.toString();
    }
    
    private String indent(String str )
    {
        StringBuffer buf = new StringBuffer(str.length());
        for( String line : str.split("\\r?\\n")) 
        {
            buf.append("  "); 
            buf.append(line);
            buf.append(LB);
        }
        return buf.toString();
    }

    protected String brief(final Indi theInd)
    {
        StringBuffer buf = new StringBuffer(theInd.toString());
        buf.append( "["); 
        buf.append( sexMap.get(theInd.getSex()));
        buf.append("]"); 
        buf.append(LB);
        buf.append("\t"); 
        buf.append(resources.getString("dump.born"));  
        buf.append(theInd.getBirthAsString());
        buf.append(resources.getString("dump.died"));  
        buf.append(theInd.getDeathAsString());
        buf.append("}"); 
        buf.append(LB);
        return buf.toString();
    }
    
    protected String dump(final Indi theInd)
    {
        StringBuffer buf = new StringBuffer(brief(theInd));
        Fam bioKidFamily = theInd.getFamilyWhereBiologicalChild();
        if( null != bioKidFamily)
        {
            buf.append(resources.getString("dump.kid-in-family")+LB);  
            buf.append(indent(dump(bioKidFamily)));
        }
        buf.append(resources.getString("dump.marriages"));  
        buf.append(LB);
        Fam[] spouseFamilies = theInd.getFamiliesWhereSpouse();
        for(Fam fam : spouseFamilies)
        {
            buf.append(indent(dump(fam)));
        }
        return buf.toString();
    }

    protected Indi createChild(final Indi parent, int marriageIndex, int sex) throws GedcomException
    {
        Fam[] families = parent.getFamiliesWhereSpouse();
        Fam theFamily;
        if( families.length > 1)
        {
            if( marriageIndex > families.length-1 || marriageIndex<0)
                throw new IllegalArgumentException(resources.getString("error.bad-marriage-index") +marriageIndex+ " "+parent   
                       +" is only a spouse in "+families.length+" families");  
            theFamily = families[marriageIndex];
        }
        else if( families.length== 0)
        {
            createFamilyAndSpouse(parent);
            theFamily = parent.getFamiliesWhereSpouse()[0];
        }
        else
            theFamily = families[0];
        Indi child = (Indi)gedcom.createEntity(Gedcom.INDI);
        child.setSex(sex);
        theFamily.addChild(child);
        Indi father = child.getBiologicalFather();
        child.setName("",father.getLastName()); 
        return child;
    }
    
    protected Indi createFamilyAndSpouse(Indi ti) throws GedcomException
    {
        Fam theFamily =  (Fam) gedcom.createEntity(Gedcom.FAM);
        Indi spouse = (Indi)gedcom.createEntity(Gedcom.INDI);
        if(ti.getSex() == PropertySex.FEMALE)
        {
            theFamily.setWife(ti);
            spouse.setSex(PropertySex.MALE);
            theFamily.setHusband(spouse);
        }
        else
        {
            theFamily.setHusband(ti);
            spouse.setSex(PropertySex.FEMALE);
            theFamily.setWife(spouse);
        }
        return spouse;
    }
    
    protected final Fam getCreateBiologicalFamily(Indi ti ) throws GedcomException
    {
        Fam theFam =  ti.getFamilyWhereBiologicalChild();
        if( null == theFam)
        {
             createParent(ti,PropertySex.MALE);
            theFam =  ti.getFamilyWhereBiologicalChild();
        }
        return theFam;
    }
    
    protected Indi createBiologicalSibling(Indi ti, int sex) throws GedcomException {
        Fam theFam =  getCreateBiologicalFamily(ti);
        Indi child = (Indi)gedcom.createEntity(Gedcom.INDI);
        child.setSex(sex);
        theFam.addChild(child);
        Indi father = child.getBiologicalFather();
        child.setName("",father.getLastName()); 
        return child;       
    }
    
    
    
    protected Indi createParent(Indi theChild, int sex) throws GedcomException
    {
        if( null != theChild.getFamilyWhereBiologicalChild())
            throw new IllegalArgumentException(resources.getString("error.cant-have-many-biofamilies"));  
        Indi parent = (Indi)gedcom.createEntity(Gedcom.INDI);
        parent.setSex(sex);
        Indi newOtherParent = createFamilyAndSpouse(parent);
        if( PropertySex.MALE  == sex)
            parent.setName("",theChild.getLastName()); 
        else
            newOtherParent.setName("",theChild.getLastName()); 
        Fam newFamily = parent.getFamiliesWhereSpouse()[0];
        newFamily.addChild(theChild);
        return parent;
    }

    
    protected boolean setDate(PropertyDate date, String newValue)
    {
        String oldValue = date.getValue();
        date.setValue(newValue);
        if( date.isValid())
            return true;
        out.println(resources.getString("error.parse-date")); 
        giveFeedback(UIFeedbackType.SYNTAX_ERROR);
        date.setValue(oldValue);
        assert(date.isValid());
        return false;
    }
    
    private String getVersion()
    {
        return resources.getString("version.version") 
        + "$Revision: 1.26 $".replace("Revision:","").replace("$",""); 
    }
    

    private  String getHelpText(Map<List<String>, Action> actionMap) {
        
        
        StringBuffer buf = new StringBuffer(1000);
        buf.append(resources.getString("help.available-commands"));  
        buf.append(LB);
        for( List<String> actionKey: actionMap.keySet())
        {
            Action a = actionMap.get(actionKey);
            for(String cmdName : actionKey)
            {
                buf.append(cmdName);
                buf.append(" "); 
                switch (a.getArgUse())
                {
                case ARG_OPTIONAL:
                    buf.append('[');  
                    buf.append(a.getArgName());
                    buf.append(']');  
                    break;
                case ARG_YES:
                    buf.append(a.getArgName());
                    break;
                }
                buf.append(LB);
            }
            buf.append("-"); 
            buf.append(a.getDoc());
            buf.append(LB);
            buf.append(LB);
        }
        return buf.toString();
    }
    
    private void setFirstName(Indi indi, String firstName) {
        indi.setName(firstName,indi.getLastName());
    }

}


