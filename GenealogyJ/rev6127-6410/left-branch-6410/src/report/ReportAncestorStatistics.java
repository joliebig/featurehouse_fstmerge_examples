

import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.report.Report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Vector;


public class ReportAncestorStatistics extends Report {
    private static final boolean DEBUG = false;
    private static final String STACK_SEPARATOR = "\n";
    private static final String LINE_SEPARATOR =
    "--------------------------------------------------------------------------------";

    private double dImplexFactor;
    Vector vecGenerationInfo = new Vector();
    private HashSet setIndi = new HashSet();
    private HashSet setCommonAncestor = new HashSet();
    private TreeMap mapImplexCommonIndi = new TreeMap();

    private double dConsanguinityFactor;
    private TreeMap mapConsanguinityCommonIndi = new TreeMap();

    private class GenerationInfo {
        int iLevel;

        int iPossibleCount;
        int iKnownCount;
        int iDiffCount;

        int iPossibleCumul;
        int iKnownCumul;
        int iDiffCumul;

        double dCoverage;
        double dCoverageCumul;
        double dImplex;

        GenerationInfo(int iLevel) {
            this.iLevel = iLevel;
        }
    }

    private class ConsanguinityInfo {
        public Indi indi;
        public int count;
        public double consanguinityFactor;
        public Stack stackIndi = new Stack();
    }

    
    public void start(Indi indi) {

        
        clearStats();

        
        computeImplexFactor(indi);

        
        computeConsanguinityFactor(indi);

        
        printHeader(indi);

        
        printImplexStats();

        
        printConsanguinityStats(indi);
        
        
        println("");
        println(translate("footer_info"));
    }

    
    private void clearStats() {
        
        dImplexFactor = 0;
        vecGenerationInfo.clear();
        setIndi.clear();
        setCommonAncestor.clear();
        mapImplexCommonIndi.clear();

        
        dConsanguinityFactor = 0;
        mapConsanguinityCommonIndi.clear();
    }

    
    private void printHeader(Indi indi) {
        
        
        println(translate("name"));
    	println();

        
        println(translate("root_individual"));
        println(LINE_SEPARATOR);
        println(indi.toString());
        println();

        
        println(translate("implex_factor", new Double(dImplexFactor)));
        println(translate("consanguinity_factor", new Double(dConsanguinityFactor)));
        println();
    }

    
    private void printImplexStats() {
        
        println(align(translate("header_implex_generation"), 10, Report.ALIGN_LEFT) +
        align(translate("header_implex_possible"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_known"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_known_percent"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_cumul"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_cumul_percent"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_diff"), 10, Report.ALIGN_RIGHT) +
        align(translate("header_implex_implex"), 10, Report.ALIGN_RIGHT));
        println(LINE_SEPARATOR);

        
        Iterator itr = vecGenerationInfo.iterator();
        while (itr.hasNext()) {
            GenerationInfo info = (GenerationInfo) itr.next();

            
            println(align(""+info.iLevel, 10, Report.ALIGN_LEFT) +
            align(""+info.iPossibleCount, 10, Report.ALIGN_RIGHT) +
            align(""+info.iKnownCount, 10, Report.ALIGN_RIGHT) +
            align(""+info.dCoverage + "%", 10, Report.ALIGN_RIGHT) +
            align(""+info.iKnownCumul, 10, Report.ALIGN_RIGHT) +
            align(""+info.dCoverageCumul + "%", 10, Report.ALIGN_RIGHT) +
            align(""+info.iDiffCount, 10, Report.ALIGN_RIGHT) +
            align(""+info.dImplex + "%", 10, Report.ALIGN_RIGHT));
        }
        println();

        
        println(translate("header_implex_common_ancestors"));
        println(LINE_SEPARATOR);

        
        Collection col = mapImplexCommonIndi.values();
        itr = col.iterator();
        while (itr.hasNext()) {
            println(itr.next());
        }
        println();
    }

    
    private void printConsanguinityStats(Indi indi) {
        
        println(translate("header_consanguinity_common_ancestors"));
        println(LINE_SEPARATOR);

        
        Collection col = mapConsanguinityCommonIndi.values();
        Iterator itr = col.iterator();
        while (itr.hasNext()) {
            ConsanguinityInfo info = (ConsanguinityInfo) itr.next();

            
            println(align(info.indi.toString(), 60, Report.ALIGN_LEFT) +
            align(info.consanguinityFactor + "", 20, Report.ALIGN_RIGHT));

            
            if (DEBUG) {
                StringBuffer strLine = new StringBuffer();
                Iterator itrStack = info.stackIndi.iterator();
                while (itrStack.hasNext()) {
                    String strToken = (String) itrStack.next();
                    if (strToken.equals(STACK_SEPARATOR)) {
                        println(strLine);
                        strLine.setLength(0);
                    }
                    else {
                        if (strLine.length() != 0)
                            strLine.append(" - ");
                        strLine.append(strToken);
                    }
                }
            }
        }
    }

    
    private void computeImplexFactor(Indi indi) {
        
        List listIndi = new ArrayList();
        listIndi.add(indi);

        
        int iLevel = 1;
        while (!listIndi.isEmpty()) {
            List listParent = new ArrayList();
            computeGeneration(iLevel, listIndi, listParent);
            listIndi = listParent;
            iLevel++;
        }

        
        int iPossibleCumul = 0;
        int iKnownCumul = 0;
        int iDiffCumul = 0;
        Iterator itr = vecGenerationInfo.iterator();
        while (itr.hasNext()) {
            GenerationInfo info = (GenerationInfo) itr.next();

            
            info.iPossibleCount = (int) Math.pow(2.0f, info.iLevel - 1);

            
            iPossibleCumul += info.iPossibleCount;
            iKnownCumul += info.iKnownCount;
            iDiffCumul += info.iDiffCount;

            
            info.iPossibleCumul = iPossibleCumul;
            info.iKnownCumul = iKnownCumul;
            info.iDiffCumul = iDiffCumul;

            
            info.dCoverage = (10000 * info.iKnownCount / info.iPossibleCount) / 100d;
            info.dCoverageCumul = (10000 * info.iKnownCumul / info.iPossibleCumul) / 100d;

            
            if (iKnownCumul != 0) {
                info.dImplex = (10000 * (info.iKnownCumul - info.iDiffCumul) / info.iKnownCumul) / 100d;
                dImplexFactor = info.dImplex;
            }
        }
    }

    
    private void addCommonAncestor(Indi indi) {
        if (indi == null)
            return;

        
        String strId = indi.getId();
        setCommonAncestor.add(strId);

        
        Fam famc = indi.getFamilyWhereBiologicalChild();
        if (famc != null) {
            addCommonAncestor(famc.getWife());
            addCommonAncestor(famc.getHusband());
        }
    }

    
    private void computeGeneration(int iLevel, List listIndi, List listParent) {
        
        GenerationInfo info = new GenerationInfo(iLevel);
        vecGenerationInfo.add(info);

        
        Iterator itr = listIndi.iterator();
        while (itr.hasNext()) {
            Indi indi = (Indi) itr.next();

            
            String strId = indi.getId();
            if (setIndi.contains(strId)) {
                
                if (!setCommonAncestor.contains(strId)) {
                    
                    mapImplexCommonIndi.put(strId, indi);

                    
                    addCommonAncestor(indi);
                }
            }
            else {
                
                setIndi.add(strId);
                info.iDiffCount++;
            }

            
            info.iKnownCount++;

            
            Fam famc = indi.getFamilyWhereBiologicalChild();
            if (famc != null) {
                
                Indi indiWife = famc.getWife();
                if (indiWife != null)
                    listParent.add(indiWife);

                
                Indi indiHusband = famc.getHusband();
                if (indiHusband != null)
                    listParent.add(indiHusband);
            }
        }
    }

    
    private void computeConsanguinityFactor(Indi indi) {
        
        dConsanguinityFactor = 0;

        
        Fam famc = indi.getFamilyWhereBiologicalChild();
        if (famc == null)
            return;

        Stack vecWife = new Stack();
        Stack vecHusband = new Stack();
        checkRightTree(famc.getWife(), 0, vecWife, famc.getHusband(), 0, vecHusband);
    }

    
    private void checkRightTree(Indi indiRight, int iLevelRight, Stack stackRight,
    Indi indiLeft, int iLevelLeft, Stack stackLeft) {
        
        if (indiRight == null || indiLeft == null)
            return;

        
        
        searchInLeftTree(indiRight, iLevelRight, stackRight, indiLeft, 0, stackLeft);

        
        String strIdRight = indiRight.getId();
        stackRight.push(strIdRight);

        
        Fam famc = indiRight.getFamilyWhereBiologicalChild();
        if (famc != null) {
            
            
            checkRightTree(famc.getWife(), iLevelRight + 1, stackRight,
            indiLeft, iLevelLeft, stackLeft);
            checkRightTree(famc.getHusband(), iLevelRight + 1, stackRight,
            indiLeft, iLevelLeft, stackLeft);
        }

        
        stackRight.pop();
    }

    
    private void searchInLeftTree(Indi indiRight, int iLevelRight, Stack stackRight,
    Indi indiLeft, int iLevelLeft, Stack stackLeft) {
        
        if (indiRight == null || indiLeft == null)
            return;

        
        
        String strIdLeft = indiLeft.getId();
        if (stackRight.contains(strIdLeft))
            return;

        
        
        String strIdRight = indiRight.getId();
        if (strIdRight == strIdLeft) {
            
            ConsanguinityInfo info = (ConsanguinityInfo) mapConsanguinityCommonIndi.get(strIdRight);
            if (info == null) {
                
                info = new ConsanguinityInfo();
                info.indi = indiRight;
            }

            
            if (DEBUG) {
                Iterator itrStack = stackRight.iterator();
                while (itrStack.hasNext())
                    info.stackIndi.push(itrStack.next());
                info.stackIndi.push(STACK_SEPARATOR);

                itrStack = stackLeft.iterator();
                while (itrStack.hasNext())
                    info.stackIndi.push(itrStack.next());
                info.stackIndi.push(STACK_SEPARATOR);
            }

            
            mapConsanguinityCommonIndi.put(strIdRight, info);

            
            double dPower = iLevelRight + iLevelLeft + 1;
            double dConsanguinityPart = Math.pow(0.5, dPower);
            dConsanguinityFactor += dConsanguinityPart;
            info.consanguinityFactor += dConsanguinityPart;
            info.count++;
            return;
        }

        
        stackLeft.push(strIdLeft);

        
        Fam famc = indiLeft.getFamilyWhereBiologicalChild();
        if (famc != null) {
            
            searchInLeftTree(indiRight, iLevelRight, stackRight,
            famc.getWife(), iLevelLeft + 1, stackLeft);
            searchInLeftTree(indiRight, iLevelRight, stackRight,
            famc.getHusband(), iLevelLeft + 1, stackLeft);
        }

        
        stackLeft.pop();
    }
}
