
package org.openscience.jmol.app.webexport;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Orbitals extends JPanel implements ActionListener {

  
  
    private static final long serialVersionUID = 1L;
    static private final String newline = "\n";
    JButton OrbopenButton, OrbdeleteButton, CooropenButton, saveButton;
    JTextField Coorfile, appletPath;
    JFileChooser fc;
    JList OrbList;
    JComboBox RenderMode, FormatBox;

    
    
    
    
    
    
    

    
    private static final int SmallAtomDot = 1;
    private static final int Wireframe = 2;

    

    public JComponent getPanel() {

      
      JLabel Description = new JLabel(
          "Create a web page with one Jmol Applet to display orbitals on one molecule or atom.");

      
      JPanel pathPanel = new JPanel();
      pathPanel.setLayout(new BorderLayout());
      
      
      pathPanel.add(appletPath, BorderLayout.PAGE_END);
      pathPanel.setBorder(BorderFactory
          .createTitledBorder("Relative Path to Jmol Applet:"));

      
      Coorfile = new JTextField(20);
      Coorfile.addActionListener(this);
      Coorfile.setText("");

      
      CooropenButton = new JButton("Select Coordinate File...");
      CooropenButton.addActionListener(this);

      
      
      
      JPanel CoorPanel = new JPanel();
      CoorPanel.setLayout(new BorderLayout());
      
      CoorPanel.add(Coorfile, BorderLayout.CENTER);
      CoorPanel.add(CooropenButton, BorderLayout.PAGE_END);
      CoorPanel.setBorder(BorderFactory
          .createTitledBorder("File containing atom coordinates:"));

      
      JLabel RenderModeLabel = new JLabel("Rendering Mode:");
      String[] RenderModes = { "Small Atom Dot", "Wireframe" };
      RenderMode = new JComboBox(RenderModes);
      RenderMode.setSelectedIndex(0);
      
      
      JPanel RenderPanel = new JPanel();
      RenderPanel.add(RenderModeLabel);
      RenderPanel.add(RenderMode);

      
      JPanel PathCoorRendPanel = new JPanel();
      PathCoorRendPanel.setLayout(new BorderLayout());
      PathCoorRendPanel.add(pathPanel, BorderLayout.PAGE_START);
      PathCoorRendPanel.add(CoorPanel, BorderLayout.CENTER);
      PathCoorRendPanel.add(RenderPanel, BorderLayout.PAGE_END);

      
      JLabel PageFormatLabel = new JLabel("Page Format:");
      String[] PageFormats = { "Single orbital", "Up to 2 orbitals at once",
          "Up to 3 orbitals at once", "Up to 4 orbitals at once",
      
      
      };
      FormatBox = new JComboBox(PageFormats);
      FormatBox.setSelectedIndex(3);
      
      JPanel FormatPanel = new JPanel();
      FormatPanel.add(PageFormatLabel);
      FormatPanel.add(FormatBox);

      
      saveButton = new JButton("Save .html as...");
      saveButton.addActionListener(this);

      
      JPanel savePanel = new JPanel();
      savePanel.add(saveButton);

      
      JPanel leftpanel = new JPanel();
      leftpanel.setLayout(new BorderLayout());
      leftpanel.add(PathCoorRendPanel, BorderLayout.PAGE_START);
      leftpanel.add(FormatPanel, BorderLayout.CENTER);
      leftpanel.add(savePanel, BorderLayout.PAGE_END);

      
      fc = new JFileChooser();

      
      

  
      DefaultListModel orbfilelist = new DefaultListModel();
      OrbList = new JList(orbfilelist);
      OrbList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    
      OrbList.setDragEnabled(true);
      JScrollPane OrbListView = new JScrollPane(OrbList);
      OrbListView.setPreferredSize(new Dimension(300, 200));

      
      OrbopenButton = new JButton("Add File(s)...");
      OrbopenButton.addActionListener(this);

      
      OrbdeleteButton = new JButton("Delete Selected");
      OrbdeleteButton.addActionListener(this);

      
      JPanel OrbButtonsPanel = new JPanel();
      OrbButtonsPanel.add(OrbopenButton);
      OrbButtonsPanel.add(OrbdeleteButton);

      
      JPanel OrbPanel = new JPanel();
      OrbPanel.setLayout(new BorderLayout());
      OrbPanel.add(OrbButtonsPanel, BorderLayout.PAGE_START);
      OrbPanel.add(OrbListView, BorderLayout.PAGE_END);
      OrbPanel.setBorder(BorderFactory
          .createTitledBorder("Orbital Files (Drag to Preferred Order):"));

      
      JPanel OrbitalPanel = new JPanel();
      OrbitalPanel.setLayout(new BorderLayout());

      
      OrbitalPanel.add(Description, BorderLayout.PAGE_START);
      OrbitalPanel.add(leftpanel, BorderLayout.CENTER);
      OrbitalPanel.add(OrbPanel, BorderLayout.LINE_END);

      return (OrbitalPanel);
    }

    public void actionPerformed(ActionEvent e) {

      
      if (e.getSource() == OrbopenButton) {
        
        fc.setMultiSelectionEnabled(true);
        fc.setDialogTitle("Choose the Orbital Files:");
        int returnVal = fc.showOpenDialog(Orbitals.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File[] files = fc.getSelectedFiles();
          DefaultListModel listModel = (DefaultListModel) OrbList.getModel();
          
          
          for (int i = 0; i < files.length; i++) {
            
            String str = files[i].getName();
            listModel.addElement(str);
          }

        } else {
          
        }
        
      } else if (e.getSource() == OrbdeleteButton) {
        DefaultListModel listModel = (DefaultListModel) OrbList.getModel();
        
        int[] todelete = OrbList.getSelectedIndices();
        for (int i = 0; i < todelete.length; i++) {
          listModel.remove(todelete[i]);
        }
        
      } else if (e.getSource() == saveButton) {
        fc.setDialogTitle("Save .html file as:");
        int returnVal = fc.showSaveDialog(Orbitals.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          
          DefaultListModel listModel = (DefaultListModel) OrbList.getModel();
          LogPanel.log("Saving: " + file.getName() + "." + newline);
          
          LogPanel
              .log("  Coordinate file: " + Coorfile.getText() + "." + newline);
          for (int i = 0; i < listModel.getSize(); i++) {
            LogPanel.log("  Orbital file #" + i + " is "
                + listModel.getElementAt(i) + ".");
          }
          String str = Coorfile.getText();
          boolean retVal = true;
          try {
            retVal = orbtohtml((FormatBox.getSelectedIndex() + 1), (RenderMode
                .getSelectedIndex() + 1), file, str, OrbList, appletPath
                .getText());
          } catch (IOException IOe) {
            LogPanel.log(IOe.getMessage());
          }
          if (!retVal) {
            LogPanel.log("Call to orbtohtml unsuccessful.");
          }
        } else {
          LogPanel.log("Save command cancelled by \"user\".");
        }
        
      } else if (e.getSource() == CooropenButton) {
        
        fc.setMultiSelectionEnabled(false);
        fc.setDialogTitle("Choose a Coordinate File:");
        int returnVal = fc.showOpenDialog(Orbitals.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File file = fc.getSelectedFile();
          
          Coorfile.removeAll();
          String str = file.getName();
          Coorfile.setText(str);
          
        } else {
          LogPanel.log("Coordinate file selection cancelled by user.");
        }
      }
    }

    private boolean checkformat(int FormatChoice) throws IOException {
      
      switch (FormatChoice) {
      case 1: 
        return true;
      case 2: 
        return true;
      case 3: 
        return true;
      case 4: 
        return true;
      case 5: 
        throw new IOException("WideOneOrb format not yet implemented.");
      case 6:
        throw new IOException("WideTwoOrb format not yet implemented.");
      default: 
        throw new IOException("Unacceptable format choice for web page.");
      }
    }

    public boolean orbtohtml(int FormatChoice, int Rendering, File outfile,
                             String Coorfile, JList OrbList, String appletPath)
        throws IOException { 
      boolean formatOK = false;
      try {
        formatOK = checkformat(FormatChoice);
      } catch (IOException IOe) {
        throw IOe;
      }
      
      if (formatOK) {
        
        PrintStream out = null;
        try {
          out = new PrintStream(new FileOutputStream(outfile));
        } catch (FileNotFoundException e) {
          throw e; 
        }
        
        out
            .println("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
        out.println("<html>");
        out.println("<head>");
        out.println("  <meta content=\"text/html; charset=ISO-8859-1\"");
        out.println(" http-equiv=\"content-type\">");
        out.println("  <title>Sufaces using jmol</title>");
        out.println("  <meta content=\"???\" name=\"author\">");
        out
            .println("  <meta content=\"chemistry, orbital, orbitals, isosurface, wavefunction, Jmol, image, animation, rotatable, live display\" name=\"keywords\">");
        out
            .println("  <meta content=\"Orbital display using jmol\" name=\"description\">");
        out.println("  <meta");
        out
            .println(" content=\"Page design and layout by J. Gutow 5-2006, page written by Orbitals to Web.jar\"");
        out.println(" name=\"details\">");
        out.println("  <meta");
        out
            .println(" content=\"This page is designed to have the text and normal html edited in a standard web design\"");
        out.println(" name=\"instr1\">");
        out.println("  <meta");
        out
            .println(" content=\"program.  The javascript is to control the jmol applet (see below).  It is best to use\"");
        out.println(" name=\"instr2\">");
        out.println("  <meta");
        out
            .println(" content=\"the java program to generate the proper java script to allow orbital display.  It is\"");
        out.println(" name=\"instr3\">");
        out.println("  <meta");
        out
            .println(" content=\"important to have all the gzipped files in the same directory as the .html file.  The\"");
        out.println(" name=\"instr4\">");
        out.println("  <meta");
        out
            .println(" content=\"files needed are: 1) a gzipped file with the atom coordinates, in any of the formats Jmol\"");
        out.println(" name=\"instr5\">");
        out.println("  <meta");
        out
            .println(" content=\"can read. 2) a gzipped gaussian cube file for each of the orbitals to be displayed.  To\"");
        out.println(" name=\"instr6\">");
        out.println("  <meta");
        out
            .println(" content=\"save loading time it seems to be better to avoid including the atom coordinates in the\"");
        out.println(" name=\"instr7\">");
        out.println("  <meta");
        out
            .println(" content=\"cube files.  It is also recommended that the file names for the surfaces correspond to\"");
        out.println(" name=\"instr8\">");
        out.println("  <meta");
        out
            .println(" content=\"reasonable names for the orbitals as the file names are used to generate the menu items\"");
        out.println(" name=\"instr9\">");
        out
            .println("  <meta content=\"that appear in the popup menus for selecting orbitals.\"");
        out.println(" name=\"instr10\">");
        out.println("  <meta");
        out
            .println(" content=\"The relative path to Jmol on your server must be correct below!!\"");
        out.println(" name=\"instr11\">");
        out.println("  <script src=\"" + appletPath + "/Jmol.js\"></script>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div style=\"text-align: center;\"><big><big><span");
        out
            .println(" style=\"font-weight: bold;\">&lt;Replace this text with your title&gt;<br>");
        out.println("</span></big></big>");
        out.println("<div style=\"text-align: left;\"><big><big><span");
        out
            .println(" style=\"font-weight: bold;\"></span></big></big>&lt;Describe your");
        out
            .println("molecule and orbitals here. Don't forget to mention that there is a");
        out
            .println("live display below.&nbsp; The user's browser window may be too small to");
        out
            .println("display your text and the table containing the Jmol applet without");
        out.println("scrolling.&gt;<br>");
        out.println("<br>");
        out
            .println("<big><big><span style=\"font-weight: bold;\"></span></big></big>");
        out
            .println("<table style=\"width: 100%; text-align: left;\" border=\"1\" cellpadding=\"2\"");
        out.println(" cellspacing=\"2\">");
        out.println("  <tbody>");
        out.println("    <tr>");
        out.println("      <td style=\"vertical-align: top;\">");
        out.println("      <script>");
        out.println("jmolInitialize(\"" + appletPath + "\");");
        
        switch (Rendering) {
        case SmallAtomDot: {
          
          out.println("jmolApplet(300, \"load " + Coorfile
              + "; spacefill 2%; background white\"); ");
          break;
        }
        case Wireframe: {
          
          out
              .println("jmolApplet(300, \"load "
                  + Coorfile
                  + "; spacefill 0%; wireframe; labels %e; set labeloffset 0 0; background white\"); ");
          break;
        }
        }
        out.println("jmolBr();");
        out
            .println("jmolHtml(\"This image may be rotated and zoomed.  See below for more instructions.\");");
        out.println("        </script>");
        out.println("      <br>");
        out.println("      </td>");
        out.println("      <td style=\"vertical-align: top;\">");
        out.println("      <div style=\"text-align: right;\"> </div>");
        out.println("      <form>");
        out.println("        <div style=\"text-align: right;\"> </div>");
        out
            .println("        <table style=\"width: 100%; text-align: left;\" border=\"0\"");
        out.println(" cellpadding=\"2\" cellspacing=\"2\">");
        out.println("          <tbody>");
        
        
        String poscolor = "";
        String negcolor = "";
        String color1 = "";
        String color2 = "";
        String color3 = "";
        String color4 = "";
        for (int j = 1; j <= FormatChoice; j++) {
          
          switch (j) {
          case 1: {
            negcolor = "red";
            poscolor = "blue";
            color1 = "'true'";
            color2 = " ";
            color3 = " ";
            color4 = " ";
            
            break;
          }
          case 2: {
            negcolor = "yellow";
            poscolor = "green";
            color1 = " ";
            color2 = "'true'";
            color3 = " ";
            color4 = " ";
            
            break;
          }
          case 3: {
            negcolor = "cyan";
            poscolor = "purple";
            color1 = " ";
            color2 = " ";
            color3 = "'true'";
            color4 = " ";
            
            break;
          }
          case 4: {
            negcolor = "pink";
            poscolor = "brown";
            color1 = " ";
            color2 = " ";
            color3 = " ";
            color4 = "'true'";
            
            break;
          }
          default: {
            System.out.println(j + ": executing default");
            break;
          }
          }
          out.println("            <tr>");
          out.println("              <td colspan=\"1\" rowspan=\"1\"");
          out
              .println(" style=\"vertical-align: top; white-space: nowrap; text-align: right;\"><span");
          out.println(" style=\"font-weight: bold;\">Orbital");
          out.println(j + ":</span><br>");
          out.println("              </td>");
          out.println("              <td style=\"vertical-align: top;\">");
          out.println("              <script>");
          out.println("   jmolMenu([");
          out.println("   ['isosurface neg" + j + " delete; isosurface pos" + j
              + " delete;','none'],");
          
          for (int i = 0; i < OrbList.getModel().getSize(); i++) {
            int dotIndex = OrbList.getModel().getElementAt(i).toString().indexOf(
                ".");
            String Itemname = OrbList.getModel().getElementAt(i).toString()
                .substring(0, dotIndex);
            out.println("   ['isosurface neg" + j + " delete; isosurface pos" + j
                + " delete; isosurface neg" + j + " -0.06 \""
                + OrbList.getModel().getElementAt(i) + "\"; color isosurface "
                + negcolor + ";isosurface pos" + j + " 0.06 \""
                + OrbList.getModel().getElementAt(i) + "\";color isosurface "
                + poscolor + ";',\"" + Itemname + "\"],");
          }
          out.println("   ]);");
          out.println("   </script>");
          out.println("              <br>");
          out.println("              </td>");
          out.println("            </tr>");
          out.println("            <tr>");
          out.println("              <td");
          out
              .println(" style=\"vertical-align: top; text-align: right; white-space: nowrap;\">Surface");
          out.println("Type:<br>");
          out.println("              </td>");
          out.println("              <td");
          out
              .println(" style=\"vertical-align: top; text-align: left; white-space: nowrap;\">");
          out.println("              <script>");
          out.println("   jmolRadioGroup([['isosurface neg" + j
              + " fill nomesh nodots; isosurface pos" + j
              + " fill nomesh nodots;','Solid','true'],");
          out.println("   ['isosurface neg" + j
              + " mesh nofill nodots; isosurface pos" + j
              + " mesh nofill nodots;', 'Mesh'],");
          out.println("   ['isosurface neg" + j
              + " dots nomesh nofill; isosurface pos" + j
              + " dots nomesh nofill;', 'Dot']");
          out.println("    ]);");
          out.println("   </script><br>");
          out.println("              </td>");
          out.println("            </tr>");
          out.println("            <tr>");
          out.println("              <td");
          out
              .println(" style=\"vertical-align: top; text-align: right; white-space: nowrap;\">Surface");
          out.println("Color:<br>");
          out.println("              </td>");
          out.println("              <td");
          out
              .println(" style=\"vertical-align: top; text-align: left; white-space: nowrap;\">");
          out.println("              <script>");
          out.println("   jmolRadioGroup([['isosurface neg" + j
              + "; color isosurface red; isosurface pos" + j
              + "; color isosurface blue;',\"blue/red\"," + color1 + "],");
          out.println("   ['isosurface neg" + j
              + "; color isosurface yellow; isosurface pos" + j
              + "; color isosurface green;',\"green/yellow\"," + color2 + "],");
          out.println("   ['isosurface neg" + j
              + "; color isosurface cyan; isosurface pos" + j
              + "; color isosurface purple;',\"purple/cyan\"," + color3 + "],");
          out.println("   ['isosurface neg" + j
              + "; color isosurface pink; isosurface pos" + j
              + "; color isosurface brown;',\"brown/pink\"," + color4 + "]");
          out.println("   ]);");
          out.println("   </script>");
          out.println("              <br>");
          out.println("              </td>");
          out.println("            </tr>");

        }
        out.println("          </tbody>");
        out.println("        </table>");
        out.println("      </form>");
        out
            .println("&lt;This space can be used as a short caption for the applet at left.&gt;<br><br>");
        String Stamp = "";
        Stamp = WebExport.TimeStamp_WebLink();
        out.println(Stamp);
        out.println("Original");
        out
            .println("page composed by <a href=\"http://www.uwosh.edu/faculty_staff/gutow/\">J.");
        out.println("Gutow 4/2006</a>. </small> </td>");
        out.println("    </tr>");
        out.println("    <tr>");
        out
            .println("      <td colspan=\"2\" rowspan=\"1\" style=\"vertical-align: top;\">");
        out
            .println("      <div style=\"text-align: center;\"><span style=\"font-weight: bold;\">Instructions");
        out.println("for using Jmol to display");
        out.println("orbitals</span>: <br>");
        out.println("      </div>");
        out.println("      <ol>");
        out
            .println("        <li>Choose which orbitals to display by selecting them using the");
        out
            .println("orbital popup menus.&nbsp; You can control orbital color and fill mode");
        out
            .println("by selecting the appropriate options following each orbital menu.&nbsp;");
        out.println("        </li>");
        out.println("        <li>ROTATE the image by");
        out.println("holding");
        out
            .println("down the mouse button while moving the cursor over the image.&nbsp; </li>");
        out
            .println("        <li>ZOOM by holding down the shift key while moving the cursor");
        out.println("up");
        out
            .println("(decrease magnification) or down (increase magnification) on top of the");
        out.println("image.&nbsp; </li>");
        out
            .println("        <li>Other options are available in the control menu accessible by");
        out.println("holding");
        out
            .println("the mouse button down while the cursor is over \"Jmol\" in the lower");
        out
            .println("right corner (right click also works on a multibutton mouse).&nbsp; </li>");
        out
            .println("        <li>For more info about Jmol go to <a target=\"_blank\"");
        out.println(" href=\"http://www.jmol.org\">www.jmol.org.</a></li>");
        out.println("      </ol>");
        out.println("      </td>");
        out.println("    </tr>");
        out.println("  </tbody>");
        out.println("</table>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        out.close();
      }
      return true;
    }

  }
