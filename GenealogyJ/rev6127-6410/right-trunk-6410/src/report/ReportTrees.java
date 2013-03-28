
 


import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.report.Report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;



public class ReportTrees extends Report {

    
    public int minGroupSize = 2;  
    public int maxGroupSize = 20;

    
    public void start(Gedcom gedcom) {

		String title = translate("fileheader",gedcom.getName());
        
        Entity[] indis = gedcom.getEntities(Gedcom.INDI, "INDI:NAME");

        println(title);
        println("");

        
        println(translate("indicount",indis.length)+"\n");


        HashSet unvisited = new HashSet(Arrays.asList(indis));
        start(indis,unvisited);
	}

  
  public void start(Indi indi) {

      println( translate("indiheader",indi.getName()));
      println();

    HashSet unvisited = new HashSet(Arrays.asList(indi.getGedcom().getEntities(Gedcom.INDI, "INDI:NAME")));

    start( new Indi[] { indi }, unvisited);
  }

  public void start(Indi[] indis) {
      println(translate("indisheader",indis.length));

      for (int i=0; i<indis.length; i++) {
          println ("- "+indis[i].getName());
      }
      println();

      
    HashSet unvisited = new HashSet(Arrays.asList(indis[0].getGedcom().getEntities(Gedcom.INDI, "INDI:NAME")));
      start(indis,unvisited);
    }

  public void start(Entity[] indis, HashSet allIndis) {
        HashSet unvisited = new HashSet(Arrays.asList(indis));
        List trees = new ArrayList();
        while (!unvisited.isEmpty()) {
          Indi indi = (Indi)unvisited.iterator().next();

          
          Tree tree = new Tree();

          
          unvisited.remove(indi);

          
          iterate(indi, tree, allIndis);

          
          trees.add(tree);
        }

        
        if (!trees.isEmpty()) {

          
          Collections.sort(trees);

          
          println(align(translate("count"),7, Report.ALIGN_RIGHT)+"  "+translate("indi_name"));
          println("-------  ----------------------------------------------");

            int grandtotal=0;
            int loners=0;
            for (int i=0; i<trees.size(); i++) {

              Tree tree = (Tree)trees.get(i);

              
              grandtotal += tree.size();
              if (tree.size()<minGroupSize)
                loners +=tree.size();
              else if (tree.size()<maxGroupSize){
                  if (i != 0) println();
                  String prefix = ""+tree.size();
                  Iterator it = tree.iterator();
                  while (it.hasNext()){
                      Indi indi = (Indi)it.next();
                      println(align(prefix,7, Report.ALIGN_RIGHT)+"  "+indi.getId()+
                              " "+indi.getName()+
                              " "+"("+indi.getBirthAsString()+ " - "+
                              indi.getDeathAsString()+")" );
                      prefix = "";
                  }
              }
              else {
                println(align(""+tree.size(),7, Report.ALIGN_RIGHT)+"  "+tree );
              }
            }

            println("");
            println(translate("grandtotal",grandtotal));

            if (loners>0) {
                Object[] msgargs = {new Integer(loners), new Integer(minGroupSize)};
                println("\n"+translate("loners",msgargs));
            }

        }


        
        return;
    }

    
    private void iterate(Indi indi, Tree tree, Set unvisited) {

      
      Stack todos  = new Stack();
      if (unvisited.remove(indi))
          todos.add(indi);

      
      while (!todos.isEmpty()) {

        Indi todo = (Indi)todos.pop();

        
        tree.add(todo);

        
        Fam famc = todo.getFamilyWhereBiologicalChild();
        if (famc!=null)  {
          Indi mother = famc.getWife();
          if (mother!=null&&unvisited.remove(mother))
            todos.push(mother);

          Indi father = famc.getHusband();
          if (father!=null&&unvisited.remove(father))
            todos.push(father);
        }

        
        Fam[] fams = todo.getFamiliesWhereSpouse();
        for (int f=0;f<fams.length;f++) {

            
            Fam fam = fams[f];
            Indi spouse = fam.getOtherSpouse(todo);
            if (spouse!=null&&unvisited.remove(spouse))
              todos.push(spouse);

            
            Indi[] children = fam.getChildren();
            for (int c = 0; c < children.length; c++) {
              if (unvisited.remove(children[c]))
                todos.push(children[c]);
            }

            
        }

        
      }

      
    }

    
    private class Tree extends HashSet implements Comparable {

      private Indi oldestIndividual;

      public int compareTo(Object that) {
        return ((Tree)that).size()-((Tree)this).size();
      }

      public String toString() {
        return oldestIndividual.getId()+
        " "+oldestIndividual.getName()+
        "("+oldestIndividual.getBirthAsString()+ "-"+
        oldestIndividual.getDeathAsString()+")";
      }

      public boolean add(Object o) {
        
        Indi indi = (Indi)o;
        
        if (isOldest(indi))
          oldestIndividual = indi;
        
        return super.add(o);
      }

      private boolean isOldest(Indi indi) {
        long jd;
        try {
          jd = oldestIndividual.getBirthDate().getStart().getJulianDay();
        } catch (Throwable t) {
          return true;
        }
        try {
          return indi.getBirthDate().getStart().getJulianDay() < jd;
        } catch (Throwable t) {
          return false;
        }

      }

    } 

} 
