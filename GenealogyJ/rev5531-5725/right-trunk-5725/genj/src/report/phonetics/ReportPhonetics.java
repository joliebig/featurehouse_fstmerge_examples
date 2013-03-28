package phonetics;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.report.Report;
import genj.util.ReferenceSet;

import java.util.Iterator;
import java.util.TreeSet;


public class ReportPhonetics extends Report {

    public int outputFormat = 0;
    public boolean reportFirstNames = true;

    public static Phonetics[] outputFormats = {
      new Soundex(),
      new Metaphone(),
      new DoubleMetaphone(),
      new Nysiis(),
      new Phonex()
    };

    
    public boolean usesStandardOut() {
        return true;
    }

    
    public boolean isReadOnly() {
        return true;
    }

    
    public String accepts(Object context) {
        
        return context instanceof Indi || context instanceof Gedcom ? getName() : null;
    }

    
    public void start(Gedcom gedcom) {
      Entity[] indis = gedcom.getEntities(Gedcom.INDI, "");
      printPhonetic(gedcom, indis, outputFormats[outputFormat]);
    }

    
    public void start(Indi indi) {

          Phonetics phonetic = (Phonetics) getValueFromUser(translate("select"), outputFormats, outputFormats[outputFormat]);
          if (phonetic== null)
              return;
          printPhonetic(indi, phonetic);
    }

    private void printPhonetic(Gedcom gedcom, Entity[] indis, Phonetics phonetics) {
        Indi indi = null;
        String str = "";

        println(translate("outputFormat")+": "+outputFormats[outputFormat]);
        println();

        if(reportFirstNames) {
            ReferenceSet names = new ReferenceSet();
            for (int i = 0; i < indis.length; i++) {
                indi = (Indi) indis[i];
                names.add(indi.getLastName(), indi);
            }
            Iterator last = names.getKeys(gedcom.getCollator()).iterator();
            while(last.hasNext()) {
                str = (String)last.next();
                println(str+": "+encode(str, phonetics));
                Iterator first = names.getReferences(str).iterator();
                while(first.hasNext()) {
                    indi  = (Indi)first.next();
                    println(getIndent(2)+indi.getFirstName()+" ("+indi.getId()+")"+": "+encode(str, phonetics));
                }
            }
        }
        else {
            TreeSet names = new TreeSet();
            for (int i = 0; i < indis.length; i++) {
                indi = (Indi) indis[i];
                names.add(indi.getLastName());
            }
            Iterator it = names.iterator();
            while(it.hasNext()) {
                str = (String)it.next();
                println(str+": "+encode(str, phonetics));
            }
        }
    }

    private void printPhonetic(Indi indi, Phonetics phonetics) {

        
        String firstName = indi.getFirstName();
        String lastName = indi.getLastName();

        println(translate("outputFormat")+": "+phonetics);
        println();

        if(reportFirstNames) {
            println(firstName+": "+encode(firstName, phonetics));
            println(lastName+": "+encode(lastName, phonetics));
        }
        else {
            println(lastName+": "+encode(lastName, phonetics));
        }
    }

    private String encode(String input, Phonetics phonetics) {
      String result = phonetics.encode(input);
      return result==null ? "" : result;
    }

} 
