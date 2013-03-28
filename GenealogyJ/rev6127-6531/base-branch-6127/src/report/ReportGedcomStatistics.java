

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyDate;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.gedcom.time.Delta;
import genj.report.Report;
import genj.util.ReferenceSet;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;


public class ReportGedcomStatistics extends Report {

    
    public boolean analyzeIndividuals = true;
    
    public boolean reportAgeToIndis = true;

    
    public boolean analyzeFamilies = true;
    
    public boolean reportIndisToMarriageAge = true;
    
    public int reportFamsToChildren = 1;
    public String[] reportFamsToChildrens = { translate("choice.all"), translate("choice.minmax"), translate("choice.none")};
    
    public boolean reportIndisToChildBirth = true;

    
    public boolean analyzeLastNames = true;
    
    public boolean reportAgeToLastNames = true;
    
    public boolean reportLastNamesToMarriageAge = true;
    
    public int reportLastNamesToChildren = 2;
    public String[] reportLastNamesToChildrens = { translate("choice.all"), translate("choice.minmax"), translate("choice.none")};
    
    public boolean reportLastNamesToChildBirths = true;
    
    public boolean sortLastNamesByName = true;

    
    public boolean analyzeOccupations = true;
    
    public boolean sortOccupationsByName = true;
    
    public boolean reportIndisToOccupations = true;

    
    public boolean analyzeBirthPlaces = true;
    
    public boolean reportIndisToBirthPlaces = true;
    
    public boolean sortBirthPlacesByName = true;

    
    public boolean analyzeBaptismPlaces = true;
    
    public boolean reportIndisToBaptismPlaces = true;
    
    public boolean sortBaptismPlacesByName = true;

    
    public boolean analyzeMarriagePlaces = true;
    
    public boolean reportIndisToMarriagePlaces = true;
    
    public boolean sortMarriagePlacesByName = true;

    
    public boolean analyzeEmigrationPlaces = true;
    
    public boolean reportIndisToEmigrationPlaces = true;
    
    public boolean sortEmigrationPlacesByName = true;

    
    public boolean analyzeImmigrationPlaces = true;
    
    public boolean reportIndisToImmigrationPlaces = true;
    
    public boolean sortImmigrationPlacesByName = true;

    
    public boolean analyzeNaturalizationPlaces = true;
    
    public boolean reportIndisToNaturalizationPlaces = true;
    
    public boolean sortNaturalizationPlacesByName = true;

    
    public boolean analyzeDeathPlaces = true;
    
    public boolean reportIndisToDeathPlaces = true;
    
    public boolean sortDeathPlacesByName = true;

    
    private static class StatisticsIndividuals {
        
        int which = -1;
        
        int number = 0;
        
        ReferenceSet age = new ReferenceSet();
        
        ReferenceSet childBirthAge = new ReferenceSet();
        
        int minChildBirthAge = Integer.MAX_VALUE;
        
        int maxChildBirthAge = Integer.MIN_VALUE;
        
        int childBirthNumber = 0;
        
        int sumChildBirthAge = 0;
        
        int minAge = Integer.MAX_VALUE;
        
        int maxAge = Integer.MIN_VALUE;
        
        int sumAge = 0;
    }

    
    private static class StatisticsLastNames {

        
        ReferenceSet lastNamesIndis = new ReferenceSet();
        
        ReferenceSet lastNamesStatistic = new ReferenceSet();
    }

    
    private static class StatisticsOccupations {

        
        int numberIndis = 0;
        
        ReferenceSet occupations = new ReferenceSet();
    }

    
    private static class StatisticsFamilies {
        
        StatisticsIndividuals husbands = new StatisticsIndividuals();
        
        StatisticsIndividuals wifes = new StatisticsIndividuals();
        
        int number = 0;
        
        int withChildren = 0;
        
        ReferenceSet children = new ReferenceSet();
        
        int minChildren = 999;
        
        int maxChildren = 0;
        
        int sumChildren = 0;
    }

    
    private static class StatisticsPlaces {
        
        int which = -1;
        
        int entitiesWithKnownPlaces = 0;
        
        ReferenceSet places = new ReferenceSet();
    }

    
    private static final int ALL = 1;
    private static final int MALES = 2;
    private static final int FEMALES = 3;
    private static final int UNKNOWN = 4;

    
    private static final int INDIS = 5;
    private static final int CHILDBIRTH = 6;

    
    private static final int BIRTH = 7;
    private static final int BAPTISM = 8;
    private static final int MARRIAGE = 9;
    private static final int EMIGRATION = 10;
    private static final int IMMIGRATION = 11;
    private static final int NATURALIZATION = 12;
    private static final int DEATH = 13;

    
    public void start(Gedcom gedcom) {

        
        if((analyzeIndividuals==false)&&(analyzeLastNames==false)&&
        (analyzeOccupations==false)&&(analyzeFamilies==false)&&
        (analyzeBirthPlaces==false)&&(analyzeBaptismPlaces==false)&&
        (analyzeMarriagePlaces==false)&&(analyzeEmigrationPlaces==false)&&
        (analyzeImmigrationPlaces==false)&&(analyzeNaturalizationPlaces==false)&&
        (analyzeDeathPlaces==false))
            return;

        
        Entity[] indis = gedcom.getEntities(Gedcom.INDI, "");
        Entity[] fams = gedcom.getEntities(Gedcom.FAM,"");

        
        StatisticsIndividuals all=null, males=null, females=null, unknown=null;
        StatisticsLastNames lastNames = null;
        StatisticsOccupations occupations = null;
        StatisticsFamilies families=null;
        StatisticsPlaces births=null, baptisms=null, marriages=null, emigrations=null, immigrations=null, naturalizations=null, deaths=null;

        
        if(analyzeIndividuals) {
            all = new StatisticsIndividuals();
            all.which = ALL;
            males = new StatisticsIndividuals();
            males.which= MALES;
            females = new StatisticsIndividuals();
            females.which= FEMALES;
            unknown = new StatisticsIndividuals();
            unknown.which= UNKNOWN;
            analyzeIndividuals(indis, all, males, females, unknown);
        }

        if(analyzeFamilies) {
            families = new StatisticsFamilies();
            families.number = fams.length;
            analyzeFamilies(fams, null, families);
        }

        if(analyzeLastNames) {
            lastNames = new StatisticsLastNames();
            analyzeLastNames(indis, lastNames);
        }

        if(analyzeOccupations) {
            occupations = new StatisticsOccupations();
            analyzeOccupations(indis, occupations);
        }

        if(analyzeBirthPlaces) {
            births = new StatisticsPlaces();
            births.which = BIRTH;
            analyzePlaces(indis, births);
        }

        if(analyzeBaptismPlaces) {
            baptisms = new StatisticsPlaces();
            baptisms.which = BAPTISM;
            analyzePlaces(indis, baptisms);
        }

        if(analyzeMarriagePlaces) {
            marriages = new StatisticsPlaces();
            marriages.which = MARRIAGE;
            analyzePlaces(fams, marriages);
        }

        if(analyzeEmigrationPlaces) {
            emigrations = new StatisticsPlaces();
            emigrations.which = EMIGRATION;
            analyzePlaces(indis, emigrations);
        }

        if(analyzeImmigrationPlaces) {
            immigrations = new StatisticsPlaces();
            immigrations.which = IMMIGRATION;
            analyzePlaces(indis, immigrations);
        }

        if(analyzeNaturalizationPlaces) {
            naturalizations = new StatisticsPlaces();
            naturalizations.which = NATURALIZATION;
            analyzePlaces(indis, naturalizations);
        }

        if(analyzeDeathPlaces) {
            deaths = new StatisticsPlaces();
            deaths.which = DEATH;
            analyzePlaces(indis, deaths);
        }

        
        println(translate("header",gedcom.getName()));
        println();

        if(analyzeIndividuals) {
            int i;
            if(reportAgeToIndis)
                i=1;
            else
                i=3;
            reportIndividuals(i, null, 0, all, males, females, unknown);
        }

        if(analyzeFamilies)
            reportFamilies(families, reportFamsToChildren, reportIndisToChildBirth, false);

        if(analyzeLastNames)
            reportLastNames(lastNames,  sortLastNamesByName? gedcom.getCollator() : null, indis.length);

        if(analyzeOccupations)
            reportOccupations(occupations, sortOccupationsByName? gedcom.getCollator() : null);

        if(analyzeBirthPlaces) {
            println(translate("birthPlaces")+": "+new Integer(births.places.getKeys().size()));
            reportPlaces(reportIndisToBirthPlaces, sortBirthPlacesByName ? gedcom.getCollator() : null, births);
        }

        if(analyzeBaptismPlaces) {
            println(translate("baptismPlaces")+": "+new Integer(baptisms.places.getKeys().size()));
            reportPlaces(reportIndisToBaptismPlaces, sortBaptismPlacesByName ? gedcom.getCollator() : null, baptisms);
        }

        if(analyzeMarriagePlaces) {
            println(translate("marriagePlaces")+": "+new Integer(marriages.places.getKeys().size()));
            reportPlaces(reportIndisToMarriagePlaces, sortMarriagePlacesByName ? gedcom.getCollator() : null, marriages);
        }

        if(analyzeEmigrationPlaces) {
            println(translate("emigrationPlaces")+": "+new Integer(emigrations.places.getKeys().size()));
            reportPlaces(reportIndisToEmigrationPlaces, sortEmigrationPlacesByName ? gedcom.getCollator() : null, emigrations);
        }

        if(analyzeImmigrationPlaces) {
            println(translate("immigrationPlaces")+": "+new Integer(immigrations.places.getKeys().size()));
            reportPlaces(reportIndisToImmigrationPlaces, sortImmigrationPlacesByName ? gedcom.getCollator() : null, immigrations);
        }

        if(analyzeNaturalizationPlaces) {
            println(translate("naturalizationPlaces")+": "+new Integer(naturalizations.places.getKeys().size()));
            reportPlaces(reportIndisToNaturalizationPlaces, sortNaturalizationPlacesByName ? gedcom.getCollator() : null, naturalizations);
        }

        if(analyzeDeathPlaces) {
            println(translate("deathPlaces")+": "+new Integer(deaths.places.getKeys().size()));
            reportPlaces(reportIndisToDeathPlaces, sortDeathPlacesByName ? gedcom.getCollator() : null, deaths);
        }

    }

    
    private double roundNumber(double number, int digits) {
        if((Double.isNaN(number))||(Double.isInfinite(number)))
            return 0.0;

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(digits);
        nf.setMaximumFractionDigits(digits);
        nf.setGroupingUsed(false);

        return Double.parseDouble(nf.format(number).replace(',','.'));
    }

    
    private void analyzePlaces(Entity[] e, StatisticsPlaces places) {

        Property prop;
        Property[] props;
        String place;

        for(int i=0;i<e.length;i++) {

            prop = null;
            props = null;

            switch(places.which) {

                case BIRTH:
                    props = new Property[1];
                    props[0] = e[i].getProperty(new TagPath("INDI:BIRT:PLAC"));
                    break;

                case BAPTISM:
                    ArrayList baps = new ArrayList();
                    prop = e[i].getProperty("BAPM");
                    if (prop!=null) {
                        prop = e[i].getProperty(new TagPath("INDI:BAPM:PLAC"));
                        baps.add(prop);
                    }
                    prop = e[i].getProperty("BAPL");
                    if (prop!=null) {
                        prop = e[i].getProperty(new TagPath("INDI:BAPL:PLAC"));
                        baps.add(prop);
                    }
                    prop = e[i].getProperty("CHR");
                    if (prop!=null) {
                        prop = e[i].getProperty(new TagPath("INDI:CHR:PLAC"));
                        baps.add(prop);
                    }
                    prop = e[i].getProperty("CHRA");
                    if (prop!=null) {
                        prop = e[i].getProperty(new TagPath("INDI:CHRA:PLAC"));
                        baps.add(prop);
                    }
                    props = (Property[])baps.toArray(new Property[baps.size()]);
                    break;

                case EMIGRATION:
                    prop = e[i].getProperty("EMIG");
                    if (prop!=null)
                        props = e[i].getProperties(new TagPath("INDI:EMIG:PLAC"));
                    break;

                case IMMIGRATION:
                    prop = e[i].getProperty("IMMI");
                    if (prop!=null)
                        props = e[i].getProperties(new TagPath("INDI:IMMI:PLAC"));
                    break;

                case NATURALIZATION:
                    prop = e[i].getProperty("NATU");
                    if (prop!=null)
                        props = e[i].getProperties(new TagPath("INDI:NATU:PLAC"));
                    break;

                case MARRIAGE:
                    prop = e[i].getProperty("MARR");
                    if (prop!=null)
                        props = e[i].getProperties(new TagPath("FAM:MARR:PLAC"));
                    break;

                case DEATH:
                    props = new Property[1];
                    prop = e[i].getProperty("DEAT");
                    if (prop!=null)
                        props[0] = e[i].getProperty(new TagPath("INDI:DEAT:PLAC"));
                    break;

            }

            if (props!=null && props.length>0) {
                for(int j=0;j<props.length;j++) {
                    if(props[j]!=null) {
                        place = props[j].getValue();
                        if (place.length()>0) {
                            if(places.places.add(place, e[i]))
                                places.entitiesWithKnownPlaces++;
                        }
                    }
                }
            }
        }
    }

    
    private void analyzeAge(Indi indi, Delta age, StatisticsIndividuals single, StatisticsIndividuals all, int which) {

        if(age==null)
            return;
        int a = age.getYears()*360+age.getMonths()*30+age.getDays();

        switch(which) {
            case INDIS:
            case MARRIAGE:

                if(all!=null) {
                    all.age.add(new Integer(a),indi);
                    all.sumAge=all.sumAge+a;
                    if(a>all.maxAge)
                        all.maxAge=a;

                    if(a<all.minAge)
                        all.minAge=a;
                }

                single.age.add(new Integer(a),indi);
                single.sumAge=single.sumAge+a;

                if(a>single.maxAge)
                    single.maxAge=a;

                if(a<single.minAge)
                    single.minAge=a;
                break;

            case CHILDBIRTH:

                single.childBirthNumber++;
                single.sumChildBirthAge = single.sumChildBirthAge + a;
                single.childBirthAge.add(new Integer(a), indi);

                if(a < single.minChildBirthAge)
                    single.minChildBirthAge = a;
                if(a > single.maxChildBirthAge)
                    single.maxChildBirthAge = a;
                break;
        }
    }

    
    private void analyzeIndividuals(Entity[] e,StatisticsIndividuals all,StatisticsIndividuals males,StatisticsIndividuals females,StatisticsIndividuals unknown) {

        for(int i=0;i<e.length;i++) {

            Delta age = null;
            Indi indi = (Indi)e[i];

            all.number++;

            if(indi.getDeathDate()!=null)
                age = indi.getAge(indi.getDeathDate().getStart());

            switch (indi.getSex()) {

                case PropertySex.MALE:
                    males.number++;
                    analyzeAge(indi, age, males, all, INDIS);
                    break;

                case PropertySex.FEMALE:
                    females.number++;
                    analyzeAge(indi, age, females, all, INDIS);
                    break;

                default:
                    unknown.number++;
                    analyzeAge(indi, age, unknown, all, INDIS);
                    break;
            }
        }
    }

    
    private void analyzeLastNames(Entity[] e, StatisticsLastNames lastNames) {

        
        for(int i=0;i<e.length;i++)
            lastNames.lastNamesIndis.add(((Indi)e[i]).getLastName(), (Indi)e[i]);

        
        Iterator it = lastNames.lastNamesIndis.getKeys().iterator();
        ArrayList familiesToLastName = new ArrayList();
        while(it.hasNext()) {
            familiesToLastName.clear();
            String name = (String)it.next();
            
            Iterator entities = lastNames.lastNamesIndis.getReferences(name).iterator();
            
            while(entities.hasNext()) {
                Indi indi = (Indi)entities.next();
                if(indi.getNoOfFams() > 0) {
                    Fam[] fams = indi.getFamiliesWhereSpouse();
                    for(int j=0;j<fams.length;j++)
                        familiesToLastName.add(fams[j]);
                }
            }
            
            StatisticsFamilies families = new StatisticsFamilies();
            families.number = familiesToLastName.size();
            StatisticsIndividuals all = new StatisticsIndividuals();
            all.which=ALL;
            StatisticsIndividuals males = new StatisticsIndividuals();
            males.which=MALES;
            StatisticsIndividuals females = new StatisticsIndividuals();
            females.which=FEMALES;
            StatisticsIndividuals unknown = new StatisticsIndividuals();
            unknown.which=UNKNOWN;
            
            analyzeIndividuals((Entity[])lastNames.lastNamesIndis.getReferences(name).toArray(new Entity[0]), all, males, females, unknown);
            analyzeFamilies((Entity[])familiesToLastName.toArray(new Entity[0]), name, families);
            
            lastNames.lastNamesStatistic.add(name, all);
            lastNames.lastNamesStatistic.add(name, males);
            lastNames.lastNamesStatistic.add(name, females);
            lastNames.lastNamesStatistic.add(name, unknown);
            lastNames.lastNamesStatistic.add(name, families);
        }
    }

    
    private void analyzeOccupations(Entity[] e, StatisticsOccupations occupations) {

        for(int i=0;i<e.length;i++) {
            occupations.numberIndis++;
            
            Property[] props = e[i].getProperties(new TagPath("INDI:OCCU"));
            if (props!=null) {
                for(int j=0;j<props.length;j++) {
                    String occu = props[j].getValue();
                    if(occu.length()>0)
                        occupations.occupations.add(occu, e[i]);
                }
            }
        }
    }

    
    private void analyzeFamilies(Entity[] e, String lastName, StatisticsFamilies families) {

        Delta age;

        for(int i=0;i<e.length;i++) {
            Fam fam = (Fam)e[i];

            
            Indi husband=fam.getHusband();
            Indi wife=fam.getWife();
            PropertyDate date = fam.getMarriageDate();

            if(date!=null) {
                if((husband!=null)&&((lastName==null)||husband.getLastName().equals(lastName))){
                    age = husband.getAge(date.getStart());
                    analyzeAge(husband, age, families.husbands, null, MARRIAGE);
                }
                if((wife!=null)&&((lastName==null)||wife.getLastName().equals(lastName))){
                    age= wife.getAge(date.getStart());
                    analyzeAge(wife, age, families.wifes, null, MARRIAGE);
                }
            }

            
            Indi[] children = fam.getChildren();

            for(int j=0;j<children.length;j++) {
                date = children[j].getBirthDate();
                if(date!=null) {
                    if ((husband!=null)&&((lastName==null)||(husband.getLastName().equals(lastName)))) {
                        age = husband.getAge(date.getStart());
                        analyzeAge(husband, age, families.husbands, null, CHILDBIRTH);
                    }
                    if ((wife!=null)&&((lastName==null)||(wife.getLastName().equals(lastName)))) {
                        age = wife.getAge(date.getStart());
                        analyzeAge(wife, age, families.wifes, null, CHILDBIRTH);
                    }
                }
            }

            
            families.children.add(new Integer(children.length), fam);

            if(children.length > 0)
                families.withChildren++;

            if(children.length>families.maxChildren)
                families.maxChildren=children.length;

            if(children.length<families.minChildren)
                families.minChildren=children.length;
        }
    }

    
    private int[] calculateAverageAge(double ages, double numAges) {
        int[] age = {0, 0, 0};

        
        if((numAges>0)&&(ages!=Integer.MAX_VALUE)&&(ages!=Integer.MIN_VALUE)) {
            age[0] = (int)roundNumber(Math.floor(ages/360/numAges),0);
            ages = ages%(360*numAges);
            age[1] = (int)roundNumber(Math.floor(ages/30/numAges),0);
            ages = ages%(30*numAges);
            age[2] = (int)roundNumber(ages/numAges, 0);
        }
        return age;
    }

    
    private void printAges(int printIndis, int indent, StatisticsIndividuals stats, int which) {

        int[] age;

        switch(which) {
            case INDIS:
            case MARRIAGE:

                if(stats.age.getKeys().size()>0) {
                    
                    if(stats.age.getSize()==1) {
                        
                        Indi indi = (Indi)stats.age.getReferences((Integer)stats.age.getKeys().get(0)).iterator().next();
                        age = calculateAverageAge(stats.sumAge,1);
                        println(getIndent(indent)+new Delta(age[2], age[1], age[0])+" "+translate("oneIndi"));
                        if(printIndis<3)
                            println(getIndent(indent+1)+translate("entity", indi.getId(), indi.getName() ));
                    }
                    else {
                        
                        
                        printMinMaxAge(indent, "minAge", stats.minAge, stats.age.getReferences(new Integer(stats.minAge)));
                        
                        age = calculateAverageAge(stats.sumAge,stats.age.getSize());
                        println(getIndent(indent)+translate("avgAge")+" "+new Delta(age[2], age[1], age[0]));
                        
                        printMinMaxAge(indent, "maxAge", stats.maxAge, stats.age.getReferences(new Integer(stats.maxAge)));
                    }
                }
                else
                    
                    println(getIndent(indent)+translate("noData"));
                break;
            case CHILDBIRTH:
                if(stats.childBirthAge.getKeys().size()>0) {
                    
                    if(stats.childBirthAge.getSize()==1) {
                        
                        Indi indi = (Indi)stats.childBirthAge.getReferences((Integer)stats.childBirthAge.getKeys().get(0)).iterator().next();
                        age = calculateAverageAge(stats.sumChildBirthAge,1);
                        println(getIndent(indent)+new Delta(age[2], age[1], age[0])+" "+translate("oneIndi"));
                        if(printIndis<3)
                            println(getIndent(indent+1)+translate("entity", indi.getId(), indi.getName()));
                    }
                    else{
                        
                        
                        printMinMaxAge(indent, "minAge", stats.minChildBirthAge, stats.childBirthAge.getReferences(new Integer(stats.minChildBirthAge)));
                        
                        age = calculateAverageAge(stats.sumChildBirthAge,stats.childBirthNumber);
                        println(getIndent(indent)+translate("avgAge")+" "+new Delta(age[2], age[1], age[0]));
                        
                        printMinMaxAge(indent, "maxAge", stats.maxChildBirthAge, stats.childBirthAge.getReferences(new Integer(stats.maxChildBirthAge)));
                    }
                }
                else
                    
                    println(getIndent(indent)+translate("noData"));
                break;
        }
    }

    
    private void printMinMaxAge(int indent, String prefix, int age, Collection c) {

        int[] avg = calculateAverageAge(age,1);

        println(getIndent(indent)+translate(prefix)+" "+new Delta(avg[2], avg[1], avg[0]));
        Iterator it = c.iterator();
        while(it.hasNext()) {
            Indi indi = (Indi)it.next();
            println(getIndent(indent+1)+translate("entity", indi.getId(), indi.getName() ));
        }
    }

    
    private void reportIndividuals(int printIndis, String lastName, double numberAllIndis, StatisticsIndividuals all, StatisticsIndividuals males, StatisticsIndividuals females, StatisticsIndividuals unknown) {

        int indent;

        if(lastName==null) {
            println(translate("people"));
            println(getIndent(2)+translate("number",all.number));
            indent=3;
        }
        else {
            println(getIndent(2)+"\""+lastName+"\""+": "+all.number+" ("+roundNumber((double)all.number/(double)numberAllIndis*100, OPTIONS.getPositions())+"%)");
            println(getIndent(3)+translate("ages"));
            println(getIndent(4)+translate("all"));
            indent=5;
        }

        if((lastName==null) || (all.number>0))
            printAges(printIndis, indent, all, INDIS);

        if((lastName==null) || (males.number>0)) {
            println(getIndent(indent-1)+translate("males", ""+males.number, ""+roundNumber((double)males.number/(double)all.number*100, OPTIONS.getPositions()) ));
            printAges(printIndis, indent, males, INDIS);
        }

        if((lastName==null) || (females.number>0)) {
            println(getIndent(indent-1)+translate("females", ""+females.number, ""+roundNumber((double)females.number/(double)all.number*100, OPTIONS.getPositions()) ));
            printAges(printIndis, indent, females, INDIS);
        }

        if((lastName==null) || (unknown.number>0)) {
            println(getIndent(indent-1)+translate("unknown", ""+unknown.number, ""+roundNumber((double)unknown.number/(double)all.number*100, OPTIONS.getPositions()) ));
            printAges(printIndis, indent, unknown, INDIS);
        }

        if(lastName==null)
            println();
    }

    
    private void printChildren(StatisticsFamilies families, int childs, int indent) {
        Iterator it = families.children.getReferences(new Integer(childs)).iterator();
        while(it.hasNext()) {
            Fam fam = (Fam)it.next();
            println(getIndent(indent+2)+translate("entity", fam.getId(), fam.toString() ));
        }
    }


    
    private void reportFamilies(StatisticsFamilies families, int reportFamsToChildren, boolean reportIndisToChildBirths, boolean lastName) {

        int i = -1, j = -1, indent = -1;
        if(reportIndisToMarriageAge)
            i=1;
        else
            i=3;

        if(reportIndisToChildBirth)
            j=1;
        else
            j=3;

        if(lastName==false) {
            println(translate("families")+": "+families.number);
            indent = 2;
        }
        else
            indent = 3;

        if(families.number>0) {
            
            println(getIndent(indent)+translate("ageAtMarriage"));
            
            println(getIndent(indent+1)+translate("husbands"));
            printAges(i, indent+2, families.husbands, MARRIAGE);
            
            println(getIndent(indent+1)+translate("wifes"));
            printAges(i, indent+2, families.wifes, MARRIAGE);

            
            println(getIndent(indent)+translate("withChildren", ""+families.withChildren, ""+roundNumber((double)families.withChildren/(double)families.number*100,OPTIONS.getPositions()) ));

            switch(reportFamsToChildren) {
                case 0:
                    println(getIndent(indent+1)+translate("avgChildren",""+roundNumber((double)families.withChildren/(double)families.number,OPTIONS.getPositions())));
                    Iterator f = families.children.getKeys().iterator();
                    while(f.hasNext()) {
                        int children = ((Integer)f.next()).intValue();
                        println(getIndent(indent+1)+translate("children")+": "+children);
                        printChildren(families, children, indent);
                    }
                    break;
                case 1:
                    println(getIndent(indent+1)+translate("avgChildren",""+roundNumber((double)families.withChildren/(double)families.number,OPTIONS.getPositions())));
                    println(getIndent(indent+1)+translate("minChildren",families.minChildren));
                    printChildren(families, families.minChildren, indent);
                    println(getIndent(indent+1)+translate("maxChildren",families.maxChildren));
                    printChildren(families, families.maxChildren, indent);
                    break;
                case 2:
                    println(getIndent(indent+1)+translate("minChildren",families.minChildren));
                    println(getIndent(indent+1)+translate("avgChildren",""+roundNumber((double)families.withChildren/(double)families.number,OPTIONS.getPositions())));
                    println(getIndent(indent+1)+translate("maxChildren",families.maxChildren));
                    break;
            }

            
            println(getIndent(indent)+translate("agesAtChildBirths"));
            
            println(getIndent(indent+1)+translate("husbands"));
            printAges(j, indent+2, families.husbands, CHILDBIRTH);
            
            println(getIndent(indent+1)+translate("wifes"));
            printAges(j, indent+2, families.wifes, CHILDBIRTH);
        }

        if(lastName==false)
            println();
    }

    
    private void reportPlaces(boolean reportIndisToPlaces, Comparator sort, StatisticsPlaces places) {

        Iterator p = places.places.getKeys(sort).iterator();
        while(p.hasNext()) {
            String place = (String)p.next();
            int number = places.places.getSize(place);
            println(getIndent(2)+place+": "+number+" ("+roundNumber((double)number/(double)places.entitiesWithKnownPlaces*100, OPTIONS.getPositions())+"%)");
            if(reportIndisToPlaces) {
                Iterator entities = places.places.getReferences(place).iterator();
                while(entities.hasNext()) {
                    if(places.which==MARRIAGE) {
                        Fam fam = (Fam)entities.next();
                        println(getIndent(3)+translate("entity", fam.getId(), fam.toString() ));
                    }
                    else {
                        Indi indi = (Indi)entities.next();
                        println(getIndent(3)+translate("entity", indi.getId(), indi.getName() ));
                    }
                }
            }
        }
        println();
    }
    
    private void reportLastNames(StatisticsLastNames lastNames, Comparator sort, int numberAllIndis) {

        println(translate("lastNames", ""+lastNames.lastNamesIndis.getKeys().size(), ""+numberAllIndis ));
        Iterator it = lastNames.lastNamesIndis.getKeys(sort).iterator();
        while(it.hasNext()) {
            String name = (String)it.next();
            StatisticsIndividuals all=null, males=null, females=null, unknown=null;
            StatisticsFamilies families = null;
            Iterator stats = lastNames.lastNamesStatistic.getReferences(name).iterator();
            while(stats.hasNext()) {
                Object stat = stats.next();
                if(stat instanceof StatisticsIndividuals) {
                    switch(((StatisticsIndividuals)stat).which) {
                        case ALL:
                            all=(StatisticsIndividuals)stat;
                            break;
                        case MALES:
                            males=(StatisticsIndividuals)stat;
                            break;
                        case FEMALES:
                            females=(StatisticsIndividuals)stat;
                            break;
                        case UNKNOWN:
                            unknown=(StatisticsIndividuals)stat;
                            break;
                    }
                }
                else
                    families = (StatisticsFamilies)stat;
            }
            int i;
            if(reportAgeToLastNames)
                i=1;
            else
                i=3;
            reportIndividuals(i, name, numberAllIndis, all, males, females, unknown);
            reportFamilies(families, reportFamsToChildren, reportIndisToChildBirth, true);
        }
    }

    
    private void reportOccupations(StatisticsOccupations occupations, Comparator sort) {

        println(translate("occupations"));
        println(getIndent(2)+translate("number", occupations.occupations.getKeys().size()));
        Iterator it = occupations.occupations.getKeys(sort).iterator();
        while(it.hasNext()) {
            String occupation = (String)it.next();
            println(getIndent(3)+translate("occupation", occupation, ""+occupations.occupations.getSize(occupation), ""+roundNumber((double)occupations.occupations.getSize(occupation)/(double)occupations.occupations.getSize()*100, OPTIONS.getPositions()) ));
            if(reportIndisToOccupations) {
                Iterator indis = occupations.occupations.getReferences(occupation).iterator();
                while(indis.hasNext()) {
                    Indi indi = (Indi)indis.next();
                    println(getIndent(4)+translate("entity", indi.getId(), indi.getName() ));
                }
            }
        }
        println();
    }

} 