
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.time.PointInTime;
import genj.report.Report;
import genj.report.options.ComponentReport;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jfree.util.Log;

import tree.Translator;
import tree.graphics.GraphicsOutput;
import tree.graphics.GraphicsOutputFactory;
import tree.graphics.GraphicsRenderer;
import tree.output.RendererFactory;











public class ReportCommonAncestor extends ComponentReport {

    private static final String OUTPUT_CATEGORY = "output";
    
    private static final int FAMILY_WIDTH = 300;
    private static final int FAMILY_HEIGH = 100;
    private static final int SPACE_BETWEEN_RECTANGLES = 20;
    private static final int SPACE_BETWEEN_TITLE_AND_COMMON_ANCESTOR = 30;
    private static final int SPACE_BETWEEN_LINES = 18;
    private static final int SPACE_BEFORE_DATE = 20;
    private static final int SHADOW_SIZE = 3;
    private static final int SPACE_BETWEEN_BORDER_AND_RECTANGLE = 5;
    private static final int SPACE_BETWEEN_BORDER_AND_TITLE = 25;
    
   
    
    private static final int MALE = 1;
    private static final int FEMALE = 2;
    
    private Font boldFontStyle;
    private Font plainFontStyle;
    private Font dateFontStyle;
    private Font titleFontStyle;
    private Font smallFontStyle;
    
    private final int YEAR_LIMIT_NUMBER = 75;
    private final int YEAR_LIMIT = Calendar.getInstance().get(Calendar.YEAR) - YEAR_LIMIT_NUMBER;
    
    
    
    public boolean use_colors = true;
    
    
    public boolean display_ids = true;
    
    
    public boolean displayRecentYears = true;
    
    
   public int husband_or_wife_first = 0;
   public String husband_or_wife_firsts[] = { 
		   translate("wife"),
		   translate("husband")
		     };

   
	
    public int ufont_name=0;
	public String ufont_names[] = { translate("ufont_name.0"),
			translate("ufont_name.1"), translate("ufont_name.2"),
			translate("ufont_name.3"), translate("ufont_name.4"),
			translate("ufont_name.5") };
    private String font_name = "Helvetica";
    
    
    
    
    private GraphicsOutputFactory outputs = new GraphicsOutputFactory();
    
    
    private Translator translator = new Translator(this);;

    
    private RendererFactory renderers = new RendererFactory(translator);

    
    
    private enum Position {LEFT, RIGHT, CENTER};
    
    
    
    
   
    public ReportCommonAncestor()
    {
        addOptions(outputs, OUTPUT_CATEGORY);
    }
    
    
	
	public boolean usesStandardOut() {
		return false;
	}

	 
	
	public String accepts(Object context) {
		
		if (context instanceof Indi)
			return getName();
		
		if (context instanceof Indi[]) {
			Indi[] indis = (Indi[]) context;
			if (indis.length == 2)
				return getName();
		}
		
		return null;
	}

	 
	
	public void start(Indi indi) {
		initStyles();
		
		
		Indi other = (Indi) getEntityFromUser(translate("select"), indi.getGedcom(), Gedcom.INDI);

		if (other == null)
			return;
		
		start(new Indi[] { indi, other });
	}

	 
	
	public void start(Indi[] indis) {

		
		Indi firstSelectedIndi = indis[0];
		Indi secondSelectedIndi = indis[1];
		
		
		Set<Indi> ancestorList = new LinkedHashSet<Indi>();
		
		
		Indi ancestor = null;
		if (firstSelectedIndi.isAncestorOf(secondSelectedIndi)){
			ancestor = firstSelectedIndi;
		}else if (secondSelectedIndi.isAncestorOf(firstSelectedIndi)){
			ancestor = secondSelectedIndi;
		}else {
			getCommonAncestor(firstSelectedIndi, secondSelectedIndi, ancestorList);
			getCommonAncestor(secondSelectedIndi, firstSelectedIndi, ancestorList);
			ancestorList = filterAncestors(ancestorList);

		}
		
		if (ancestorList.size()==1){
			ancestor = (Indi) ancestorList.toArray()[0];
		}
		
		else if (ancestorList.size()>1) {
			for (Indi indi : ancestorList) {
				Log.info("name : "+indi.getName()+" id : "+indi.getId());
			}
			Log.info("----------------------");

			ancestor = (Indi)getValueFromUser(translate("select_ancestor_in_list"), ancestorList.toArray(), (Indi) ancestorList.toArray()[0]);
		}  


		
		if (ancestor != null){
			List<Step> firstIndiDirectLinks = new ArrayList<Step>();
			firstIndiDirectLinks.add(new Step(getLastFamilyWhereSpouse(firstSelectedIndi),firstSelectedIndi, firstSelectedIndi.getSex()));
			getAncestorListBetween(ancestor, firstSelectedIndi, firstIndiDirectLinks);
			Collections.reverse(firstIndiDirectLinks);
			LOG.fine("indi's link number : "+firstIndiDirectLinks.size());
			
			List<Step> secondIndiDirectLinks = new ArrayList<Step>();
		
			secondIndiDirectLinks.add(new Step(getLastFamilyWhereSpouse(secondSelectedIndi),secondSelectedIndi, secondSelectedIndi.getSex()));
			getAncestorListBetween(ancestor, secondSelectedIndi, secondIndiDirectLinks);
			Collections.reverse(secondIndiDirectLinks);
			LOG.fine("other's link number : "+secondIndiDirectLinks.size());

			GraphicsOutput output = outputs.createOutput(this);
	        if (output == null) 
	            return;
	        try {
	            output.output(new Renderer(firstSelectedIndi, secondSelectedIndi, firstIndiDirectLinks, secondIndiDirectLinks));
	            output.display(this);
	        } catch (IOException e) {
	            println("error");
	        }
		}
		
		
		else if (ancestor == null) {
			getOptionFromUser(translate("nocommon"), Report.OPTION_OK);
			return;
		}












	}
	  
	  
	 
	
	private void initStyles(){
		font_name = translate("ufont_name."+ufont_name);
		
		boldFontStyle = new Font(font_name, Font.BOLD, 12);
	    plainFontStyle = new Font(font_name, Font.PLAIN, 12);
	    dateFontStyle = new Font(font_name, Font.PLAIN, 10);
	    titleFontStyle = new Font(font_name, Font.BOLD, 14);
	    smallFontStyle = new Font(font_name, Font.BOLD, 8);
	}
	
	 
	
	private List<Object> regroupCoupleMembers(List<Indi> ancestorList){
		List<Object> results = new ArrayList<Object>();
		
			for (Indi ancestor : ancestorList) {
				Fam[] families = ancestor.getFamiliesWhereSpouse();
				
				
				for (int i = 0; i < families.length; i++) {
					Indi otherSpouse = families[i].getOtherSpouse(ancestor);
					for (Indi indi : ancestorList) {
						if (otherSpouse.equals(indi) 
								&& !results.contains(families[i])){
							ancestorList.remove(otherSpouse);
							results.add(families[i]);
							break;
						}
					}
				}
			}
			return results;
	}
	
	 
	
	private Set<Indi> filterAncestors(Set<Indi> ancestorList){
		Set<Indi> filteredList = new LinkedHashSet<Indi>();
		boolean found = false;
		for (Indi ancestor : ancestorList) {
			found = false;
			Fam[] families = ancestor.getFamiliesWhereSpouse();
			
			
			for (int i = 0; i < families.length; i++) {
				Indi otherSpouse = families[i].getOtherSpouse(ancestor);
				if (filteredList.contains(otherSpouse)){
					found = true;
				}
			}
			if (found == false){
				filteredList.add(ancestor);
			}
		}
		return filteredList;
	}
	
	 
	
	private Fam getLastFamilyWhereSpouse(Indi indi){
		Fam[] fams = indi.getFamiliesWhereSpouse();
		if (fams == null || fams.length==0){
			return null;
		}
		return fams[fams.length-1];
	}
	
	 
	
	private void getCommonAncestor(Indi firstIndi, Indi secondIndi, Set<Indi> ancestorList) {

		Indi father = firstIndi.getBiologicalFather();
		if (father != null) {
			if (father.isAncestorOf(secondIndi)){
				ancestorList.add(father);
			}
			else{
				getCommonAncestor(father, secondIndi, ancestorList);
			}
		}
		
		Indi mother = firstIndi.getBiologicalMother();
		if (mother != null) {
			if (mother.isAncestorOf(secondIndi)){
				ancestorList.add(mother);
			} else {
				getCommonAncestor(mother, secondIndi, ancestorList);
			}
			
		}

	}

	 
	
	private void getAncestorListBetween(Indi ancestor, Indi descendant, List<Step> directLinks) {
		
		Indi link = getParentInDirectLine(ancestor, descendant);

		
		if(link != null){
			directLinks.add(new Step(descendant.getFamilyWhereBiologicalChild(),link, link.getSex()));
			LOG.fine("found link between indi and ancestor : "+link.getName());
			getAncestorListBetween(ancestor, link, directLinks);
		}

	}
	
	 
	
	private Indi getParentInDirectLine(Indi ancestor, Indi child) {

		
		Indi father = child.getBiologicalFather();
		if (father != null) {

			if (father.isDescendantOf(ancestor) || father.equals(ancestor))
				return father;
		}

		Indi mother = child.getBiologicalMother();
		if (mother != null) {
			if (mother.isDescendantOf(ancestor) || mother.equals(ancestor))
				return mother;
		}

		
		
		
		return null;
	}


	
	 
	
	private class Step{
		Fam famWhereSpouse;
		int linkSex;
		Indi link;
		
		 
		
		public Step(Fam famWhereChild, Indi link, int linkSex){
			this.famWhereSpouse = famWhereChild;
			this.link = link;
			this.linkSex = linkSex;
		}
		
		public Indi getLink(){










			return link;
		}
		
		 
		
		public Indi getWife(){
			if(linkSex==FEMALE){
				return link;
			}
			if(famWhereSpouse!=null){
				return famWhereSpouse.getWife();	
			}
			return null;
		}
		
		 
		
		public Indi getHusband(){
			if(linkSex==MALE){
				return link;
			}
			
			if(famWhereSpouse!=null){
				return famWhereSpouse.getHusband();	
			}
			return null;
		}
	}
	
	 
	private class Renderer implements GraphicsRenderer {

	        private Indi firstIndi;
	        private Indi secondIndi;
	        private List<Step> firstIndiDirectLinks;
	        private List<Step> secondIndiDirectLinks;

	       

	        private AffineTransform defaultTransform;

	        private int width;
	        private int height;
	        private double cx;
	        private double cy;

	        
	        
	        public Renderer(Indi firstIndi, Indi secondIndi, List<Step> firstIndiDirectLinks, List<Step> secondIndiDirectLinks) {
	            this.firstIndi = firstIndi;
	            this.secondIndi = secondIndi;
	            this.firstIndiDirectLinks = firstIndiDirectLinks;
	            this.secondIndiDirectLinks = secondIndiDirectLinks;


	            width = 3 * FAMILY_WIDTH + FAMILY_WIDTH/2;
	            height = (Math.max(firstIndiDirectLinks.size(), secondIndiDirectLinks.size())) * FAMILY_HEIGH
	            		+ (Math.max(firstIndiDirectLinks.size(), secondIndiDirectLinks.size())+3) * SPACE_BETWEEN_RECTANGLES
	            		+ SPACE_BEFORE_DATE
	            		+ SPACE_BETWEEN_TITLE_AND_COMMON_ANCESTOR
	            		+ SPACE_BETWEEN_BORDER_AND_RECTANGLE
	            		+ SPACE_BETWEEN_BORDER_AND_TITLE;

	            cx = width / 2;
	        }

	        
	        
	        public void render(Graphics2D graphics) {
	        	cy = 0;
	            graphics.setPaint(Color.BLACK);
	            graphics.setBackground(Color.WHITE);
	            graphics.clearRect(0, 0, getImageWidth(), getImageHeight());
	            graphics.drawRoundRect(SPACE_BETWEEN_BORDER_AND_RECTANGLE, 
	            						SPACE_BETWEEN_BORDER_AND_RECTANGLE, 
	            						getImageWidth()-SPACE_BETWEEN_BORDER_AND_RECTANGLE*2, 
	            						getImageHeight()-SPACE_BETWEEN_BORDER_AND_RECTANGLE*2, 50, 50);




	            graphics.setFont(plainFontStyle);
	            graphics.setStroke(new BasicStroke(2));
	            defaultTransform = new AffineTransform(graphics.getTransform());
	            cy += SPACE_BETWEEN_BORDER_AND_RECTANGLE;
	            int nbMaxGen = Math.max(firstIndiDirectLinks.size(), secondIndiDirectLinks.size());
	            
	            
	            graphics.setFont(titleFontStyle);
	            cy += SPACE_BETWEEN_BORDER_AND_TITLE;
	            centerString(graphics, getTitleLine(firstIndi, secondIndi, nbMaxGen), (int)cx, (int)cy );
	            graphics.setFont(plainFontStyle);
	            cy += SPACE_BETWEEN_TITLE_AND_COMMON_ANCESTOR;
	            
	            
	            render(graphics, firstIndiDirectLinks.get(0),Position.CENTER);
	            graphics.drawLine((int)cx, (int)cy+FAMILY_HEIGH, (int)cx, (int)cy+FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES);
	            if(firstIndiDirectLinks.size()>1){
	            	graphics.drawLine((int)cx-FAMILY_WIDTH, (int)cy+FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES, (int)cx, (int)cy+FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES);
	            }
	            if(secondIndiDirectLinks.size()>1){
	            	graphics.drawLine((int)cx, (int)cy+FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES, (int)cx+FAMILY_WIDTH, (int)cy+FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES);
	            }
	            
	            cy+=SPACE_BETWEEN_RECTANGLES;
	            
	            
	            for (int i=1;i<nbMaxGen;i++) {
					cy += FAMILY_HEIGH+SPACE_BETWEEN_RECTANGLES;
					if(firstIndiDirectLinks.size()>i){
						 graphics.drawLine((int)cx-FAMILY_WIDTH, (int)cy-SPACE_BETWEEN_RECTANGLES, (int)cx-FAMILY_WIDTH, (int)cy);
						render(graphics, firstIndiDirectLinks.get(i),Position.LEFT);
					}
					if(secondIndiDirectLinks.size()>i){
						 graphics.drawLine((int)cx+FAMILY_WIDTH, (int)cy-SPACE_BETWEEN_RECTANGLES, (int)cx+FAMILY_WIDTH, (int)cy);
						render(graphics, secondIndiDirectLinks.get(i),Position.RIGHT);
					}
				}
	            
	            
	            graphics.setFont(smallFontStyle);
	            centerString(graphics, PointInTime.getNow().toString(), (int)cx+FAMILY_WIDTH, (int)cy+FAMILY_HEIGH+SPACE_BEFORE_DATE );
	            
	        }

	        
	        
	        public int getImageWidth() {
	            return width;
	        }

	        
	        
	        public int getImageHeight() {
	            return height;
	        }

	        
	        
	        private void render(Graphics2D graphics, Step step, Position rightLeft){
	        	
	        	graphics.setPaint(Color.BLACK);
	        	int cxStep = 0;
	        	if(rightLeft==Position.LEFT){
	        		cxStep = (int)cx-FAMILY_WIDTH;
	        	} else if(rightLeft==Position.RIGHT){
	        		cxStep = (int)cx+FAMILY_WIDTH;
	        	}else{
	        		cxStep = (int)cx;
	        	}

	        	LOG.fine("step.link.getName() "+step.getLink().getName());
	        	
	        	
	
	        	graphics.setPaint(Color.LIGHT_GRAY);
	        	graphics.fillRect(cxStep-FAMILY_WIDTH/2+SHADOW_SIZE, (int)cy+SHADOW_SIZE, FAMILY_WIDTH+SHADOW_SIZE, FAMILY_HEIGH+SHADOW_SIZE);
	        	
	        	graphics.setPaint(Color.BLACK);
	        	graphics.clearRect(cxStep-FAMILY_WIDTH/2, (int)cy, FAMILY_WIDTH, FAMILY_HEIGH);
	        	graphics.drawRect(cxStep-FAMILY_WIDTH/2, (int)cy, FAMILY_WIDTH, FAMILY_HEIGH);
	        	
	        	
	        	if(husband_or_wife_first == 0){
	        		renderWife(graphics, step, cxStep, (int)cy);
	        		renderHusband(graphics, step, cxStep, (int)cy + SPACE_BETWEEN_LINES*2);
	        	} else {
	        		renderHusband(graphics, step, cxStep, (int)cy);
	        		renderWife(graphics, step, cxStep, (int)cy + SPACE_BETWEEN_LINES*2);
	        	}
	        	
	        	
	        	
	        	if(step.famWhereSpouse != null 
	        			&& step.famWhereSpouse.getMarriageDate()!=null){

	        		centerString(graphics, getMarriageLine(step),(int)cxStep, (int)cy + SPACE_BETWEEN_LINES*5);
	        	}
	        }

	        
			
			private void renderWife(Graphics2D graphics, Step step, int cxStep, int cyStep) {
				if(step.getWife() != null){
		        	 if(step.linkSex == FEMALE){
		        		 graphics.setFont(boldFontStyle);
		        		 if(use_colors){
		        			 graphics.setPaint(Color.MAGENTA);
		        		 }
		        		 centerString(graphics, getNameLine(step.getWife()), cxStep, cyStep + SPACE_BETWEEN_LINES);
			        		graphics.setFont(plainFontStyle);
			        		graphics.setPaint(Color.BLACK);
		        	 } else{
		        		 centerString(graphics, getNameLine(step.getWife()), cxStep, cyStep + SPACE_BETWEEN_LINES);
		        	 }
		        	 graphics.setFont(dateFontStyle);
		        	 centerString(graphics, getDateLine(step.getWife()), cxStep, cyStep + SPACE_BETWEEN_LINES*2);
		        	 graphics.setFont(plainFontStyle);
	        	}
			}

			
			
			private void renderHusband(Graphics2D graphics, Step step, int cxStep, int cyStep) {
				if(step.getHusband() != null){
	        		if(step.linkSex == MALE){
		        		graphics.setFont(boldFontStyle);
		        		if(use_colors){
		        			graphics.setPaint(Color.BLUE);
		        		}
		        		centerString(graphics, getNameLine(step.getHusband()), cxStep, cyStep + SPACE_BETWEEN_LINES);
		        		graphics.setFont(plainFontStyle);
		        		graphics.setPaint(Color.BLACK);
		        	}else{
		        		centerString(graphics, getNameLine(step.getHusband()), cxStep, cyStep + SPACE_BETWEEN_LINES);
		        	}
	        		graphics.setFont(dateFontStyle);
		        	 centerString(graphics, getDateLine(step.getHusband()), cxStep, cyStep + SPACE_BETWEEN_LINES*2);
		        	 graphics.setFont(plainFontStyle);
	        	}
			}
	        
	        
	        
	        private String getNameLine(Indi indi){
	        	StringBuffer sb =  new StringBuffer(indi.getFirstName())
	        				.append(" ")
	        				.append(indi.getLastName());
	        				
		        	if(display_ids){
		        		sb.append(" [")
		        		.append(indi.getId())
		        		.append("]");
		        	}
	        	return sb.toString();
	        }
	        
	        
	        
	        private String getDateLine(Indi indi){
	        	StringBuffer sb =  new StringBuffer();
	        	
	        	if(displayRecentYears 
	        			|| indi.getDeathDate(true).getStart().getYear()<YEAR_LIMIT  
	        			|| indi.getBirthDate(true).getStart().getYear()<YEAR_LIMIT){
	        		sb.append("("+indi.getBirthDate(true))
		        	.append(" - ")
		        	.append(indi.getDeathDate(true))
		        	.append(")");
	        	}

	        	return sb.toString();
	        }
	        
	        
	        
	        private String getMarriageLine(Step step){
	        	StringBuffer sb = new StringBuffer();
	        	
	        	if(displayRecentYears || step.famWhereSpouse.getMarriageDate(true).getStart().getYear()<YEAR_LIMIT){

		        	sb.append(translate("marriage.date"))
								        	.append(" ")
								        	.append(step.famWhereSpouse.getMarriageDate(true));
		        	
		        	if(display_ids){
		        		sb.append(" [")
		        		.append(step.famWhereSpouse.getId())
		        		.append("]");
		        	}
	        	}

				return sb.toString();
	        }
	        
	        
	        
	        private String getTitleLine(Indi indi, Indi other, int generationCount){
	        	
	        	String[] args = {getNameLine(indi), getNameLine(other)};
	        	return translate("title",args);
	        }
	        

	        
	        
	        private void centerString(Graphics2D graphics, String text, int x, int y) {
	            Rectangle2D rect = graphics.getFont().getStringBounds(text,
	                    graphics.getFontRenderContext());
	            int width = (int)rect.getWidth();
	            graphics.drawString(text, x - width/2, y);
	        }

	        
	        
	        private int getGenerationCount(Indi indi, int max) {
	            if (indi == null)
	                return -1;
	            if (max == 0)
	                return 0;
	            Fam family = indi.getFamilyWhereBiologicalChild();
	            if (family != null) {
	                int g1 = getGenerationCount(family.getHusband(), max - 1) + 1;
	                int g2 = getGenerationCount(family.getWife(), max - 1) + 1;
	                if (g2 > g1)
	                    return g2;
	                else
	                    return g1;
	            }
	            return 0;
	        }
	    }
}
