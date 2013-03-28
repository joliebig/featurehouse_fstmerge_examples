
import genj.chart.Chart;
import genj.chart.IndexedSeries;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomException;
import genj.gedcom.Indi;
import genj.gedcom.PropertyAge;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertySex;
import genj.gedcom.time.Delta;
import genj.report.Report;

import java.awt.BorderLayout;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;


public class ReportDemography extends Report {

  
  private static final int MAX_AGE = 100;
  
  
  private int ageGroupSize = 10;

  
  private String[] categories;

  
  public int getAgeGroupSize() {
    return ageGroupSize;
  }

  
  public void setAgeGroupSize(int set) {
    ageGroupSize = Math.max(1, Math.min(25, set));
  }

  
  public boolean usesStandardOut() {
    return false;
  }

  
  public void start(Gedcom gedcom) throws Exception {

    createCategories();
    String labelForMen = translate("men");
    String labelForWomen = translate("women");
    String labelForFathers = translate("fathers");
    String labelForMothers = translate("mothers");
    String diagramTitle = translate("title", gedcom.getName());

    IndiAnalyzer[] indiAnalyzers = {
        
        new IndiAnalyzer(labelForMen, labelForWomen, PropertyAge.getLabelForAge()){

          public void addFact(Indi indi) {
            try { addAge(indi, indi.getDeathDate()); } catch (RuntimeException e) {}
          }
        },

        
        new IndiAnalyzer(labelForMen, labelForWomen, translate("ageAtFirstMariage")){

          public void addFact(Indi indi) {
            try { addAge(indi, indi.getFamiliesWhereSpouse()[0].getMarriageDate()); } catch (RuntimeException e) {}
          }
        },

        
        new IndiAnalyzer(labelForFathers, labelForMothers, translate("ageAtParentsDeath")){
          
          public void addFact(Indi indi) {
            try { addAgeForMale  ( indi, indi.getBiologicalFather().getDeathDate() ); } catch (RuntimeException e) {}
            try { addAgeForFemale( indi, indi.getBiologicalMother().getDeathDate() ); } catch (RuntimeException e) {}
          }
        },
        
        
        new IndiAnalyzer(labelForFathers, labelForMothers, translate("ageAtChildsBirth")){

          public void addFact(Indi indi) {
            try { 
              PropertyDate birthDate = indi.getBirthDate();
              try { addAgeForMale  ( indi.getBiologicalFather(), birthDate ); } catch (RuntimeException e) {}
              addAgeForFemale( indi.getBiologicalMother(), birthDate );
            }
            catch (RuntimeException e) {}
          }},

        
        new IndiAnalyzer(labelForFathers, labelForMothers, translate("ageOfYoungestChildLeftBehind")){

          public void addFact(Indi indi) {
            try {
              
              PropertyDate deathDate = indi.getDeathDate();  
              
              
              Fam[] fams = indi.getFamiliesWhereSpouse();
              Fam fam = fams[fams.length - 1 ];
              Indi[] children = fam.getChildren();
              Indi child = children[children.length - 1]; 

              
              
              if ( indi.getSex() == PropertySex.MALE )
                  addAgeForMale(child, deathDate);
                else 
                  addAgeForFemale(child, deathDate);
            }
            catch (RuntimeException e) {}
          }
        },
        
        new IndiAnalyzer(labelForMen, labelForWomen, translate("yearsSinceDeathOfSpouse")){
          
          public void addFact(Indi indi) {
            try {
              Indi[] partners = indi.getPartners();
              for ( int i=0 ; i<partners.length ; i++ )
                try { addRemainingYears(indi, partners[i].getDeathDate()); } catch (RuntimeException e) {}
            }
            catch (RuntimeException e) {}
          }
        }
        
    };

    FamAnalyzer[] famAnalyzers = {
        
        
        new FamAnalyzer(labelForMen, labelForWomen, translate("ageAtDivorce")){
          
          public void addFact(Fam fam) {
            try {
              PropertyDate divorce = fam.getDivorceDate();
              try { addAgeForMale(fam.getHusband(), divorce); } catch (RuntimeException e) {}
              addAgeForFemale(fam.getWife(), divorce);
            }
            catch (RuntimeException e) {}
          }
        },
        
        
        new FamAnalyzer(labelForMen, labelForWomen, translate("ageOfOldestWhenYoungestWasBorn")){
          
          public void addFact(Fam fam) {
            try { 
              Indi[] children = fam.getChildren();
              if ( 1 >= children.length ) return; 
              Indi youngest = children[children.length - 1];  
              Indi oldest = children[0];  

              addAge(oldest, youngest.getBirthDate());
            }
            catch (RuntimeException e) {}
          }
        },

        
        new FamAnalyzer(labelForFathers, labelForMothers, translate("ageOfYoungestOrphan")){
          
          public void addFact(Fam fam) {
            try {
              Indi[] children = fam.getChildren();
              Indi youngest = children[children.length - 1]; 
              
              PropertyDate father = fam.getHusband().getDeathDate();
              PropertyDate mother = fam.getWife().getDeathDate();
              if ( father.getStart().getYear() > mother.getStart().getYear() ) 
                addAgeForMale(youngest, father);
              else  
                addAgeForFemale(youngest, mother);
            }
            catch (RuntimeException e) {}
          }
        }
        
    };

    gatherData( gedcom,       indiAnalyzers, famAnalyzers);
    showData  ( diagramTitle, indiAnalyzers, famAnalyzers);
  }

  
  private void showData(
      String title, 
      IndiAnalyzer[] indiAnalyzers,
      FamAnalyzer[] famAnalyzers) {
    
    JTabbedPane charts = new JTabbedPane();
    for ( int i=0 ; i<indiAnalyzers.length ; i++ ) {
      charts.addTab( indiAnalyzers[i].getAgeLabel(), indiAnalyzers[i].createChart (title) );
    }
    for ( int i=0 ; i<famAnalyzers.length ; i++ ) {
      charts.addTab( famAnalyzers[i].getAgeLabel(), famAnalyzers[i].createChart (title) );
    }
    
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(BorderLayout.CENTER, charts);
    showComponentToUser(panel);
  }

  
  private void gatherData(
      Gedcom gedcom, 
      IndiAnalyzer[] indiAnalyzers,
      FamAnalyzer[] famAnalyzers) {
    
    
    Iterator indis = gedcom.getEntities( Gedcom.INDI ).iterator();
    while ( indis.hasNext() ) {
      Indi indi = (Indi) indis.next();
      for ( int i=0 ; i<indiAnalyzers.length ; i++ ) {
        indiAnalyzers[i].addFact( indi );
      }
    }

    
    Iterator fams = gedcom.getEntities( Gedcom.FAM ).iterator();
    while ( fams.hasNext() ) {
      Fam fam = (Fam) fams.next();
      for ( int i=0 ; i<famAnalyzers.length ; i++ ) {
        famAnalyzers[i].addFact( fam );
      }
    }
  }

  private abstract class FamAnalyzer  extends Analyzer {
    
    
    public FamAnalyzer (String maleLabel, String femaleLabel, String ageLabel) {
      super(maleLabel, femaleLabel, ageLabel);
    }    

    
    public abstract void addFact (Fam fam);
  }

  private abstract class IndiAnalyzer  extends Analyzer {
    
    
    public IndiAnalyzer (String maleLabel, String femaleLabel, String ageLabel) {
      super(maleLabel, femaleLabel, ageLabel);
    }    
    
    
    public abstract void addFact (Indi indi);
  }
  
  
  private void createCategories() {
    categories = new String[MAX_AGE/ageGroupSize + 1];
    categories[0] = MAX_AGE + "+";
    for (int i=1;i<categories.length;i++) {
      if (ageGroupSize<5 && i%Math.ceil(5F/ageGroupSize)!=0)
        categories[i] = "";
      else
        categories[i] = (MAX_AGE - (i*ageGroupSize)) + "+";
    }
  }


  
  private abstract class Analyzer {

    private IndexedSeries males;
    private IndexedSeries females;
    private String ageLabel;

    
    public Analyzer (String maleLabel, String femaleLabel, String ageLabel) {

      this.ageLabel = ageLabel;
      males = new IndexedSeries(maleLabel, categories.length);
      females = new IndexedSeries(femaleLabel, categories.length);
    }

    
    public Chart createChart (String title) {
      
      
      
      
      
      
      IndexedSeries[] nestedSeries = new IndexedSeries[]{ males, females};
      DecimalFormat decimalFormat = new DecimalFormat("#; #");
      return new Chart(title, ageLabel, nestedSeries, categories, decimalFormat, true, true);
    }

    
    private int calculateGroup(PropertyDate firstEvent, PropertyDate lastEvent) {
      
      if ( ! firstEvent.isValid() || ! lastEvent.isValid() )
        throw new IllegalArgumentException();

      try {
        if ( firstEvent.getStart().getJulianDay() > lastEvent.getStart().getJulianDay() )
          throw new IllegalArgumentException();
      } catch (GedcomException e) {
        throw new IllegalArgumentException();
      }
        
      int years = Delta.get( firstEvent.getStart(), lastEvent.getStart() ).getYears();
      return years>=MAX_AGE ? 0 : (MAX_AGE-years-1)/ageGroupSize + 1;
    }

    
    protected void addAgeForMale( Indi individual, PropertyDate event ) {
       
       
      males.dec( calculateGroup( individual.getBirthDate(), event) );
    }
    
    
    protected void addAgeForFemale( Indi individual, PropertyDate event ) {
       
       
      females.inc( calculateGroup( individual.getBirthDate(), event) );
    }
    
    
    protected void addAge( Indi individual, PropertyDate event ) {
      if (individual.getSex() == PropertySex.MALE)
        addAgeForMale(individual, event);
      else
        addAgeForFemale(individual, event);
    }
    
    
    protected void addRemainingYears( Indi individual, PropertyDate event ) {
      int group = calculateGroup( event, individual.getDeathDate());
      
      
      
      if (individual.getSex() == PropertySex.MALE)
        males.dec( group);
      else
        females.inc( group);
    }
    
    public String getAgeLabel() {
      return ageLabel;
    }
  }
} 
