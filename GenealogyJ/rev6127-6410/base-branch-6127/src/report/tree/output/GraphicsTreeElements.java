

package tree.output;

import genj.gedcom.Fam;
import genj.gedcom.Indi;
import genj.gedcom.Property;
import genj.gedcom.PropertyPlace;
import genj.gedcom.PropertySex;
import genj.gedcom.TagPath;
import genj.renderer.MediaRenderer;
import genj.report.Options;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import tree.FamBox;
import tree.IndiBox;


public class GraphicsTreeElements implements TreeElements {

    
    private static final int DEFAULT_INDIBOX_WIDTH = 110;

    
    private static final int SHRINKED_INDIBOX_WIDTH = 50;

    
    private static final int DEFAULT_INDIBOX_HEIGHT = 64;

    
    private static final int DEFAULT_FAMBOX_WIDTH = 100;

    
    private static final int DEFAULT_FAMBOX_HEIGHT = 27;

    
    private static final int MAX_IMAGE_WIDTH = 50;

    private static final int DEFAULT_INDIBOX_LINES = 2;
    private static final int DEFAULT_FAMBOX_LINES = 1;
    private static final int TEXT_MARGIN = 5;
    private static final int NAME_LINE_HEIGHT = 12;
    private static final int LINE_HEIGHT = 10;
    private static final TagPath PATH_INDIBIRTPLAC = new TagPath("INDI:BIRT:PLAC");
    private static final TagPath PATH_INDIDEATPLAC = new TagPath("INDI:DEAT:PLAC");
    private static final TagPath PATH_INDIOCCU = new TagPath("INDI:OCCU");
    private static final TagPath PATH_INDITITL = new TagPath("INDI:TITL");
    private static final TagPath PATH_FAMMARRPLAC = new TagPath("FAM:MARR:PLAC");
    private static final TagPath PATH_FAMDIVPLAC = new TagPath("FAM:DIV:PLAC");

    
    private double IMAGE_SCALE_FACTOR = 4;

    
    private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, false, false);


    
    private static final Color[] BOX_COLORS = {
            new Color(0xff, 0xff, 0xff), 
            new Color(0xce, 0xb6, 0xbd), 
            new Color(0xde, 0x55, 0xff), 
            new Color(0x84, 0x82, 0xff), 
            new Color(0xad, 0xae, 0xef), 
            new Color(0xad, 0xcf, 0xff), 
            new Color(0xe7, 0xdb, 0xe7), 
            new Color(0xd6, 0x5d, 0x5a), 
            new Color(0xff, 0x82, 0xb5), 
            new Color(0xef, 0xae, 0xc6), 
            new Color(0xff, 0xdd, 0xdd), 
            new Color(0xce, 0xaa, 0x31), 
            new Color(0xff, 0xdd, 0x00), 

            new Color(0xff, 0xff, 0x33), 

            new Color(0xff, 0xff, 0xdd), 
            new Color(0xde, 0xff, 0xde), 
            new Color(0x82, 0xff, 0x82), 
            new Color(0x1a, 0xe1, 0x1a), 
            new Color(0xa9, 0xd0, 0xa9), 
            new Color(0xa9, 0xd0, 0xbf), 
            new Color(0xbb, 0xbb, 0xbb), 
            new Color(0xaa, 0x95, 0x95), 
            new Color(0x9e, 0xa3, 0xb2), 
            new Color(0xcd, 0xd3, 0xe9), 
            new Color(0xdf, 0xe2, 0xe2), 
            new Color(0xfa, 0xfa, 0xfa), 
            new Color(0xff, 0xff, 0xff) 
    };

    private static final int COLOR_GENERATIONS = (BOX_COLORS.length - 1) / 2;

    private static final float STROKE_WIDTH = 2.0f;

    
    private static final String MALE_SYMBOL = "\u";

    
    private static final String FEMALE_SYMBOL = "\u";

    
    private static final String UNKNOWN_SYMBOL = "?";

    
    private static final Stroke DASHED_STROKE = new BasicStroke(STROKE_WIDTH,
            BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
            new float[] { 3.0f, 6.0f }, 0.0f);

    
    private static final Font DETAILS_FONT = new Font("verdana", Font.PLAIN, 10);

    
    private static final Font NAME_FONT = new Font("verdana", Font.BOLD, 12);

    
    private static final Font ID_FONT = new Font("verdana", Font.ITALIC, 10);

    
    private static Font sexSymbolFont = null;
    static {
        
        String[] candidateFontNames = { "sansserif", "apple symbol", "symbol" };
        for (int i = 0; i < candidateFontNames.length; i++) {
            Font candidateFont = new Font(candidateFontNames[i], Font.PLAIN, 10);
            if (candidateFont.canDisplay(MALE_SYMBOL.charAt(0))) {
                sexSymbolFont = candidateFont;
                break;
            }
        }
        if (sexSymbolFont == null)
            sexSymbolFont = new Font("SansSerif", Font.PLAIN, 10);
    }

    
    public int max_names = 0;

    public String[] max_namess = { "nolimit", "1", "2", "3" };

    
    public int max_names_per_line = 2;

    public String[] max_names_per_lines = { "nolimit", "1", "2", "3" };

    
    public boolean draw_title = false;

    
    public boolean draw_name_suffix = false;

    
    public int font_name_suffix = Font.BOLD + Font.ITALIC;

    public String[] font_name_suffixs = { "plain", "bold", "italic", "bolditalic" };

    
    public boolean draw_places = true;

    
    public boolean draw_dates = true;

    
    public boolean draw_occupation = true;

    
    public boolean draw_images = true;

    
    public boolean high_quality_images = false;

    
    public boolean draw_sex_symbols = true;

    
    public boolean draw_indi_ids = false;

    
    public boolean draw_fam_ids = false;

    
    public boolean draw_divorce = true;

    
    public boolean shrink_boxes = false;

    
    public boolean use_colors = true;

    
    public boolean swap_names = false;


    
    private Graphics2D graphics = null;

    
    public void setGraphics(Graphics2D graphics) {
        this.graphics = graphics;
    }

    
    public void drawIndiBox(IndiBox indibox, int x, int y, int gen) {

        
        if (!graphics.hitClip(x, y, indibox.width, indibox.height))
            return;

        Indi i = indibox.individual;

        
        int imageWidth = 0;
        int imageHeight = indibox.height;
        if (draw_images) {
          Dimension d = MediaRenderer.getSize(i, graphics);
          if (d.width>0&&d.height>0)
            imageWidth = d.width * indibox.height / d.height;
            if (imageWidth > MAX_IMAGE_WIDTH) {
                imageWidth = MAX_IMAGE_WIDTH;
                imageHeight = d.height * imageWidth / d.width;
            }
        }
        int dataWidth = indibox.width - imageWidth;

        Color color = getBoxColor(gen);
        Shape box = new RoundRectangle2D.Double(x, y, indibox.width, indibox.height, 15, 15);
        graphics.setColor(color);
        graphics.fill(box);
        graphics.setColor(Color.BLACK);

        Shape oldClip = graphics.getClip();
        graphics.clip(box);

        
        String nameSuffix = null;
        if (draw_name_suffix) {
            nameSuffix = i.getNameSuffix();
            if (nameSuffix != null && nameSuffix.equals(""))
            	nameSuffix = null;
        }

        
        int currentY = y + 14;
        String[] firstNames = getFirstNames(i);
        String lastName = null;

        
        if (draw_title && i.getProperty(PATH_INDITITL) != null)
        	lastName = i.getLastName() + " " + i.getProperty(PATH_INDITITL);
        else
        	lastName = i.getLastName();

        if (swap_names) { 
            graphics.setFont(NAME_FONT);
            centerString(graphics, lastName, x + dataWidth/2, currentY);
            currentY += NAME_LINE_HEIGHT;

            if (nameSuffix != null) {
                graphics.setFont(new Font("verdana", font_name_suffix, 12));
            	centerString(graphics, nameSuffix, x + dataWidth/2, currentY);
                currentY += NAME_LINE_HEIGHT;
            }
        }

        graphics.setFont(NAME_FONT);
        for (int j = 0; j < firstNames.length; j++) { 
            centerString(graphics, firstNames[j], x + dataWidth/2, currentY);
            currentY += NAME_LINE_HEIGHT;
        }

        if (!swap_names) { 
            graphics.setFont(NAME_FONT);
            centerString(graphics, lastName, x + dataWidth/2, currentY);
            currentY += NAME_LINE_HEIGHT;

            if (nameSuffix != null) {
                graphics.setFont(new Font("verdana", font_name_suffix, 12));
            	centerString(graphics, nameSuffix, x + dataWidth/2, currentY);
                currentY += NAME_LINE_HEIGHT;
            }
        }

        graphics.setFont(DETAILS_FONT);

        Property birthDate = null;
        Property deathDate = null;
        PropertyPlace birthPlace = null;
        PropertyPlace deathPlace = null;
        Property occupation = null;

        if (draw_dates) {
            birthDate = i.getBirthDate();
            if (birthDate != null && !birthDate.isValid())
                birthDate = null;
            deathDate = i.getDeathDate();
            if (deathDate != null && !deathDate.isValid())
                deathDate = null;
        }

        if (draw_places) {
            birthPlace = (PropertyPlace)i.getProperty(PATH_INDIBIRTPLAC);
            if (birthPlace != null && birthPlace.getDisplayValue().equals(""))
                birthPlace = null;
            deathPlace = (PropertyPlace)i.getProperty(PATH_INDIDEATPLAC);
            if (deathPlace != null && deathPlace.getDisplayValue().equals(""))
                deathPlace = null;
        }

        if (draw_occupation)
            occupation = i.getProperty(PATH_INDIOCCU);

        
        if (birthDate != null || birthPlace != null) {
            centerString(graphics, Options.getInstance().getBirthSymbol(), x + 7, currentY);
            if (birthDate != null) {
                graphics.drawString(birthDate.getDisplayValue(), x + 13, currentY);
                currentY += LINE_HEIGHT;
            }
            if (birthPlace != null) {
                graphics.drawString(birthPlace.getFirstAvailableJurisdiction(), x + 13, currentY);
                currentY += LINE_HEIGHT;
            }
        }

        
        if (i.getDeathDate() != null || i.getProperty(PATH_INDIDEATPLAC) != null) {
            centerString(graphics, Options.getInstance().getDeathSymbol(), x + 7, currentY);
            if (deathDate != null) {
                graphics.drawString(deathDate.getDisplayValue(), x + 13, currentY);
                currentY += LINE_HEIGHT;
            }
            if (deathPlace != null) {
                graphics.drawString(deathPlace.getFirstAvailableJurisdiction(), x + 13, currentY);
                currentY += LINE_HEIGHT;
            }
            if (deathDate == null && deathPlace == null)
                currentY += LINE_HEIGHT;
        }

        
        if (occupation != null) {
            graphics.drawString(occupation.getDisplayValue(), x + 6, currentY);
        }



        
        if (draw_sex_symbols) {
            int symbolX = x + dataWidth  - 14;
            int symbolY = y + indibox.height - 5;
            graphics.setFont(sexSymbolFont);
            graphics.drawString(getSexSymbol(i.getSex()), symbolX, symbolY);
        }

        
        if (draw_indi_ids) {
            graphics.setFont(ID_FONT);
            graphics.drawString(i.getId(), x + 8, y + indibox.height - 4);
        }

        
        if(imageWidth > 0)
        {
            AffineTransform transform = null;
            double scale = 1;
            if (high_quality_images)
            {
                transform = graphics.getTransform();
                graphics.scale(1/IMAGE_SCALE_FACTOR, 1/IMAGE_SCALE_FACTOR);
                scale = IMAGE_SCALE_FACTOR;
            }

            MediaRenderer.render(graphics, 
                new Rectangle((int)(x + dataWidth*scale), (int)(y), (int)(imageWidth*scale), (int)(imageHeight*scale)), 
                i);

            if (high_quality_images)
                graphics.setTransform(transform);
        }

        graphics.setClip(oldClip);
        graphics.draw(box);
    }
    
    public void drawFamBox(FamBox fambox, int x, int y, int gen) {

        
        if (!graphics.hitClip(x, y, fambox.width, fambox.height))
            return;

        Fam f = fambox.family;

        Color color = getBoxColor(gen);
        Shape box = new RoundRectangle2D.Double(x, y, fambox.width, fambox.height, 5, 5);
        graphics.setColor(color);
        graphics.fill(box);
        graphics.setColor(Color.BLACK);

        Shape oldClip = graphics.getClip();
        graphics.clip(box);

        int currentY = y + 12;

        graphics.setFont(DETAILS_FONT);

        Property marriageDate = null;
        Property divorceDate = null;
        PropertyPlace marriagePlace = null;
        PropertyPlace divorcePlace = null;

        if (draw_dates) {
            marriageDate = f.getMarriageDate();
            if (marriageDate != null && !marriageDate.isValid())
                marriageDate = null;
            divorceDate = f.getDivorceDate();
            if (divorceDate != null && !divorceDate.isValid())
                divorceDate = null;
        }

        if (draw_places) {
            marriagePlace = (PropertyPlace)f.getProperty(PATH_FAMMARRPLAC);
            if (marriagePlace != null && marriagePlace.getDisplayValue().equals(""))
                marriagePlace = null;
            divorcePlace = (PropertyPlace)f.getProperty(PATH_FAMDIVPLAC);
            if (divorcePlace != null && divorcePlace.getDisplayValue().equals(""))
                divorcePlace = null;
        }

        
        if (f.getMarriageDate() != null) {
            centerString(graphics, Options.getInstance().getMarriageSymbol(), x + 13, currentY);
            if (marriageDate != null) {
                graphics.drawString(marriageDate.getDisplayValue(), x + 25, currentY);
                currentY += LINE_HEIGHT;
            }
            if (marriagePlace != null) {
                graphics.drawString(marriagePlace.getFirstAvailableJurisdiction(), x + 25, currentY);
                currentY += LINE_HEIGHT;
            }
            if (marriageDate == null && marriagePlace == null)
                currentY += LINE_HEIGHT;
        }

        
        if (draw_divorce && f.getDivorceDate() != null) {
            centerString(graphics, Options.getInstance().getDivorceSymbol(), x + 13, currentY);
            if (divorceDate != null) {
                graphics.drawString(divorceDate.getDisplayValue(), x + 25, currentY);
                currentY += LINE_HEIGHT;
            }
            if (divorcePlace != null) {
                graphics.drawString(divorcePlace.getFirstAvailableJurisdiction(), x + 25, currentY);
                currentY += LINE_HEIGHT;
            }
            if (divorceDate == null && divorcePlace == null)
                currentY += LINE_HEIGHT;
        }

        
        if (draw_fam_ids) {
            graphics.setFont(ID_FONT);
            graphics.drawString(f.getId(), x + 8, y + fambox.height - 4);
        }

        graphics.setClip(oldClip);
        graphics.draw(box);
    }

    
    public void drawLine(int x1, int y1, int x2, int y2) {
        graphics.drawLine(x1, y1, x2, y2);
    }

    
    public void drawDashedLine(int x1, int y1, int x2, int y2) {
        Stroke oldStroke = graphics.getStroke();
        graphics.setStroke(DASHED_STROKE);
        graphics.drawLine(x1, y1, x2, y2);
        graphics.setStroke(oldStroke);
    }

    
    public void header(int width, int height) {
        graphics.setStroke(new BasicStroke(STROKE_WIDTH));
        graphics.setBackground(Color.WHITE);
        graphics.clearRect(0, 0, width, height);
    }

    
    public void footer() {
    }

    
    public static void centerString(Graphics2D graphics, String text, int x, int y) {
        int width = getTextWidth(text, graphics.getFont(), graphics);
        graphics.drawString(text, x - width / 2, y);
    }

    
    public static void alignRightString(Graphics2D graphics, String text, int x, int y) {
        int width = getTextWidth(text, graphics.getFont(), graphics);
        graphics.drawString(text, x - width, y);
    }

    private static int getTextWidth(String text, Font font, Graphics2D graphics) {
        FontRenderContext fontRenderContext = FONT_RENDER_CONTEXT;
        if (graphics != null)
            fontRenderContext = graphics.getFontRenderContext();
        return (int)font.getStringBounds(text, fontRenderContext).getWidth();
    }

    private static int getTextWidth(String text, Font font) {
        return getTextWidth(text, font, null);
    }

    private static String getSexSymbol(int sex) {
        if (sex == PropertySex.MALE)
            return MALE_SYMBOL;
        if (sex == PropertySex.FEMALE)
            return FEMALE_SYMBOL;
        return UNKNOWN_SYMBOL;
    }

    
    private Color getBoxColor(int gen) {
        if (!use_colors)
            return Color.WHITE;
        if (gen == 0)
            return BOX_COLORS[COLOR_GENERATIONS];
        if (gen < 0)
            return BOX_COLORS[-((-gen - 1) % COLOR_GENERATIONS) + COLOR_GENERATIONS - 1];
        
        return BOX_COLORS[(gen - 1) % COLOR_GENERATIONS + COLOR_GENERATIONS + 1];
    }

    
    private String[] getFirstNames(Indi indi) {
        String firstName = indi.getFirstName();
        if (max_names <= 0 && max_names_per_line <= 0)
            return new String[] {firstName};
        if (firstName.trim().equals(""))
            return new String[] {""};

        String[] names = firstName.split("  *");
        int namesCount = names.length;
        if (max_names > 0 && max_names < namesCount)
            namesCount = max_names;
        int linesCount = 1;
        if (max_names_per_line > 0)
            linesCount = (namesCount - 1) / max_names_per_line + 1;
        String[] lines = new String[linesCount];
        int currentName = 0;
        for (int j = 0; j < linesCount; j++) {
            StringBuffer sb = new StringBuffer();
            for (int k = 0; k < max_names_per_line; k++) {
                int n = j * max_names_per_line + k;
                if (n >= namesCount)
                    break;
                sb.append(names[n]).append(" ");
            }
            lines[j] = sb.substring(0, sb.length() - 1);
        }

        return lines;
    }

    public void getIndiBoxSize(IndiBox indibox)
    {
        Indi i = indibox.individual;
        indibox.height = DEFAULT_INDIBOX_HEIGHT;
        if (shrink_boxes)
            indibox.width = SHRINKED_INDIBOX_WIDTH;
        else
            indibox.width = DEFAULT_INDIBOX_WIDTH;

        
        int lines = 0;
        if (draw_dates && i.getBirthDate() != null && i.getBirthDate().isValid())
            lines++;
        PropertyPlace birthPlace = (PropertyPlace)i.getProperty(PATH_INDIBIRTPLAC);
        if (draw_places && birthPlace != null && !birthPlace.getDisplayValue().equals(""))
            lines++;

        PropertyPlace deathPlace = (PropertyPlace)i.getProperty(PATH_INDIDEATPLAC);
        if (deathPlace != null && deathPlace.getDisplayValue().equals(""))
            deathPlace = null;
        if (i.getDeathDate() != null || deathPlace != null) {
            lines++;
            if (draw_dates && draw_places && i.getDeathDate() != null && i.getDeathDate().isValid() && deathPlace != null)
                lines++;
        }
        if (draw_occupation && i.getProperty(PATH_INDIOCCU) != null)
            lines++;
        if (lines - DEFAULT_INDIBOX_LINES > 0)
            indibox.height += (lines - DEFAULT_INDIBOX_LINES) * LINE_HEIGHT;

        
        int width = 0;
        String[] firstNames = getFirstNames(i);
        for (int j = 0; j < firstNames.length; j++) {
            int w2 = getTextWidth(firstNames[j], NAME_FONT);
            width = width>w2?width:w2;
        }

        
        indibox.height += (firstNames.length - 1) * NAME_LINE_HEIGHT;

        
        if (draw_name_suffix && i.getNameSuffix() != null && i.getNameSuffix().length()>0)
        	indibox.height += NAME_LINE_HEIGHT;

        
        if (width + 2*TEXT_MARGIN > indibox.width)
            indibox.width = width + 2*TEXT_MARGIN;
        if (draw_title && i.getProperty(PATH_INDITITL) != null)
        	width = getTextWidth(i.getLastName() + " " + i.getProperty(PATH_INDITITL), NAME_FONT);
        else
        	width = getTextWidth(i.getLastName(), NAME_FONT);

        if (width + 2*TEXT_MARGIN > indibox.width)
            indibox.width = width + 2*TEXT_MARGIN;
        width = getTextWidth(i.getNameSuffix(), NAME_FONT);
        if (width + 2*TEXT_MARGIN > indibox.width)
            indibox.width = width + 2*TEXT_MARGIN;

        if (i.getBirthDate() != null) {
            width = getTextWidth(i.getBirthDate().getDisplayValue(), DETAILS_FONT);
            if (width + 13+TEXT_MARGIN > indibox.width)
                indibox.width = width + 13+TEXT_MARGIN;
        }
        if (i.getDeathDate() != null) {
            width = getTextWidth(i.getDeathDate().getDisplayValue(), DETAILS_FONT);
            if (width + 13+TEXT_MARGIN > indibox.width)
                indibox.width = width + 13+TEXT_MARGIN;
        }

        if (draw_places) {
            if (birthPlace != null) {
                width = getTextWidth(birthPlace.getFirstAvailableJurisdiction(), DETAILS_FONT);
                if (width + 13+TEXT_MARGIN > indibox.width)
                    indibox.width = width + 13+TEXT_MARGIN;
            }
            if (deathPlace != null) {
                width = getTextWidth(deathPlace.getFirstAvailableJurisdiction(), DETAILS_FONT);
                if (width + 13+TEXT_MARGIN > indibox.width)
                    indibox.width = width + 13+TEXT_MARGIN;
            }
        }

        if (draw_occupation && i.getProperty(PATH_INDIOCCU) != null) {
            width = getTextWidth(i.getProperty(PATH_INDIOCCU).getDisplayValue(), DETAILS_FONT);
            if (width + 7+TEXT_MARGIN > indibox.width)
                indibox.width = width + 7+TEXT_MARGIN;
        }

        if (draw_indi_ids) {
            width = getTextWidth(i.getId(), ID_FONT);
            if (draw_sex_symbols)
                width += 14;
            if (width + 8+TEXT_MARGIN > indibox.width)
                indibox.width = width + 8+TEXT_MARGIN;
        }

        
        if(draw_images)
        {
          Dimension d = MediaRenderer.getSize(i, graphics);
          if(d.width>0&&d.height>0) {
              int newWidth = d.width * DEFAULT_INDIBOX_HEIGHT / d.height;
              if (newWidth < MAX_IMAGE_WIDTH)
                  indibox.width += newWidth;
              else
                  indibox.width += MAX_IMAGE_WIDTH;
          }
        }
    }

    public void getFamBoxSize(FamBox fambox)
    {
        Fam f = fambox.family;
        fambox.width = DEFAULT_FAMBOX_WIDTH;
        fambox.height = DEFAULT_FAMBOX_HEIGHT;

        
        int lines = 0;
        PropertyPlace marriagePlace = (PropertyPlace)f.getProperty(PATH_FAMMARRPLAC);
        if (f.getMarriageDate() != null) {
            lines++;
            if (draw_dates && draw_places && f.getMarriageDate().isValid() && marriagePlace != null && !marriagePlace.getDisplayValue().equals(""))
                lines++;
        }
        PropertyPlace divorcePlace = (PropertyPlace)f.getProperty(PATH_FAMDIVPLAC);
        if (draw_divorce && f.getDivorceDate() != null) {
            lines++;
            if (draw_dates && draw_places && f.getDivorceDate().isValid() && divorcePlace != null && !divorcePlace.getDisplayValue().equals(""))
                lines++;
        }

        if (lines - DEFAULT_FAMBOX_LINES > 0)
            fambox.height += (lines - DEFAULT_FAMBOX_LINES) * LINE_HEIGHT;

        
        if (f.getMarriageDate() != null) {
            int width = getTextWidth(f.getMarriageDate().getDisplayValue(), DETAILS_FONT);
            if (width + 25+TEXT_MARGIN > fambox.width)
                fambox.width = width + 25+TEXT_MARGIN;
        }
        if (draw_divorce && f.getDivorceDate() != null) {
            int width = getTextWidth(f.getDivorceDate().getDisplayValue(), DETAILS_FONT);
            if (width + 25+TEXT_MARGIN > fambox.width)
                fambox.width = width + 25+TEXT_MARGIN;
        }

        if (draw_places) {
            if (marriagePlace != null) {
                int width = getTextWidth(marriagePlace.getFirstAvailableJurisdiction(), DETAILS_FONT);
                if (width + 25+TEXT_MARGIN > fambox.width)
                    fambox.width = width + 25+TEXT_MARGIN;
            }
            if (draw_divorce && divorcePlace != null) {
                int width = getTextWidth(divorcePlace.getFirstAvailableJurisdiction(), DETAILS_FONT);
                if (width + 25+TEXT_MARGIN > fambox.width)
                    fambox.width = width + 25+TEXT_MARGIN;
            }
        }
    }
}
