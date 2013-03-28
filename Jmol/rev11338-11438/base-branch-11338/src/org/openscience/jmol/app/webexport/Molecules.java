
package org.openscience.jmol.app.webexport;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

class Molecules extends JPanel implements ActionListener {

  

  private static final long serialVersionUID = 1L;
  
  JButton saveButton, MolecopenButton, MolecdeleteButton;
  JTextField appletPath;
  JFileChooser fc;
  JList MolecList;
  JComboBox RenderMode, FormatBox;

  
  private static final int FromLinks = 1;
  private static final int FromMenu = 2;

  
  private static final int Wireframe = 1;
  private static final int BallandStick = 2;
  private static final int Spacefilling = 3;

  
  JComponent getPanel() {

    
    JLabel Description = new JLabel(
        "Create a web page with one Jmol Applet to display molecules chosen by user.");

    
    appletPath = new JTextField(20);
    appletPath.addActionListener(this);
    appletPath.setText("../../Applets/Java/Jmol");

    
    JPanel pathPanel = new JPanel();
    pathPanel.setLayout(new BorderLayout());
    
    
    pathPanel.add(appletPath, BorderLayout.PAGE_END);
    pathPanel.setBorder(BorderFactory
        .createTitledBorder("Relative Path to Jmol Applet:"));

    

    
    JLabel RenderModeLabel = new JLabel("Rendering Mode:");
    String[] RenderModes = { "Wireframe", "BallandStick", "Spacefilling" };
    RenderMode = new JComboBox(RenderModes);
    RenderMode.setSelectedIndex(2);
    
    
    JPanel RenderPanel = new JPanel();
    RenderPanel.add(RenderModeLabel);
    RenderPanel.add(RenderMode);

    
    JPanel PathCoorRendPanel = new JPanel();
    PathCoorRendPanel.setLayout(new BorderLayout());
    PathCoorRendPanel.add(pathPanel, BorderLayout.PAGE_START);
    
    PathCoorRendPanel.add(RenderPanel, BorderLayout.PAGE_END);

    
    JLabel PageFormatLabel = new JLabel("Page Format:");
    String[] PageFormats = { "Molecules from links (best with 4 or less)",
        "Molecules from popup menu" };
    FormatBox = new JComboBox(PageFormats);
    FormatBox.setSelectedIndex(0);
    
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

    
    
    ArrayListTransferHandler arrayListHandler = new ArrayListTransferHandler(
        null);
    DefaultListModel Molecfilelist = new DefaultListModel();
    MolecList = new JList(Molecfilelist);
    MolecList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    MolecList.setTransferHandler(arrayListHandler);
    MolecList.setDragEnabled(true);
    JScrollPane MolecListView = new JScrollPane(MolecList);
    MolecListView.setPreferredSize(new Dimension(300, 200));

    
    

    
    MolecopenButton = new JButton("Add File(s)...");
    MolecopenButton.addActionListener(this);

    
    MolecdeleteButton = new JButton("Delete Selected");
    MolecdeleteButton.addActionListener(this);

    
    JPanel MolecButtonsPanel = new JPanel();
    MolecButtonsPanel.add(MolecopenButton);
    MolecButtonsPanel.add(MolecdeleteButton);

    
    JPanel MolecPanel = new JPanel();
    MolecPanel.setLayout(new BorderLayout());
    MolecPanel.add(MolecButtonsPanel, BorderLayout.PAGE_START);
    MolecPanel.add(MolecListView, BorderLayout.PAGE_END);
    MolecPanel.setBorder(BorderFactory
        .createTitledBorder("Molecule Files (Drag to Preferred Order):"));

    
    JPanel MoleculePanel = new JPanel();
    MoleculePanel.setLayout(new BorderLayout());

    
    MoleculePanel.add(Description, BorderLayout.PAGE_START);
    MoleculePanel.add(leftpanel, BorderLayout.CENTER);
    MoleculePanel.add(MolecPanel, BorderLayout.LINE_END);

    return (MoleculePanel);
  }

  public void actionPerformed(ActionEvent e) {

    
    if (e.getSource() == MolecopenButton) {
      
      fc.setMultiSelectionEnabled(true);
      fc.setDialogTitle("Choose the Molecule Files:");
      int returnVal = fc.showOpenDialog(Molecules.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File[] files = fc.getSelectedFiles();
        DefaultListModel listModel = (DefaultListModel) MolecList.getModel();
        
        
        for (int i = 0; i < files.length; i++) {
          
          String str = files[i].getName();
          listModel.addElement(str);
        }

      } else {
        
      }
      
    } else if (e.getSource() == MolecdeleteButton) {
      DefaultListModel listModel = (DefaultListModel) MolecList.getModel();
      
      int[] todelete = MolecList.getSelectedIndices();
      for (int i = 0; i < todelete.length; i++) {
        listModel.remove(todelete[i]);
      }
      
    } else if (e.getSource() == saveButton) {
      fc.setDialogTitle("Save .html file as:");
      int returnVal = fc.showSaveDialog(Molecules.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        
        DefaultListModel listModel = (DefaultListModel) MolecList.getModel();
        LogPanel.log("Saving: " + file.getName() + ".\n");
        for (int i = 0; i < listModel.getSize(); i++) {
          LogPanel.log("  Molecule file #" + i + " is "
              + listModel.getElementAt(i) + ".");
        }
        boolean retVal = true;
        try {
          retVal = molectohtml((FormatBox.getSelectedIndex() + 1), (RenderMode
              .getSelectedIndex() + 1), file, MolecList, appletPath.getText());
        } catch (IOException IOe) {
          LogPanel.log(IOe.getMessage());
        }
        if (!retVal) {
          LogPanel.log("Call to molectohtml unsuccessful.");
        }
      } else {
        LogPanel.log("Save command cancelled by \"user\".");
      }
      
    }
  }

  private boolean checkformat(int FormatChoice) throws IOException {
    
    switch (FormatChoice) {
    case FromLinks: 
      return true;
    case FromMenu: 
      return true;
    default: 
      throw new IOException("Unacceptable format choice for web page.");
    }
  }

  public boolean molectohtml(int FormatChoice, int Rendering, File outfile,
                             JList MolecList, String appletPath)
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
      out.println("  <title>Molecules using jmol</title>");
      out.println("  <meta content=\"???\" name=\"author\">");
      out
          .println("  <meta content=\"chemistry, Jmol, image, animation, rotatable, live display\" name=\"keywords\">");
      out
          .println("  <meta content=\"Molecule display using jmol\" name=\"description\">");
      out.println("  <meta");
      out
          .println(" content=\"Page design and layout by J. Gutow 7-2006, page written by Orbitals to Web.jar\"");
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
          .println(" content=\"files needed are: 1) a gzipped files with the atom coordinates, in any of the formats Jmol\"");
      out.println(" name=\"instr5\">");
      out.println("  <meta");
      out.println(" content=\"can read.\"");
      out.println(" name=\"instr6\">");
      out.println("  <meta");
      out
          .println(" content=\"It is also recommended that the file names for the molecules correspond to\"");
      out.println(" name=\"instr8\">");
      out.println("  <meta");
      out
          .println(" content=\"reasonable names for the molecules as the file names are used to generate the menu items\"");
      out.println(" name=\"instr9\">");
      out
          .println("  <meta content=\"that appear in the popup menus or as links for selecting molecules.\"");
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
      out.println("molecules here. Don't forget to mention that there is a");
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
      
      out.println("jmolApplet(300);");
      out.println("jmolBr();");
      out
          .println("jmolHtml(\"This image may be rotated and zoomed.  See below for more instructions.\");");
      out.println("        </script>");
      out.println("      <br>");
      out.println("      </td>");
      out.println("      <td style=\"vertical-align: top;\">");
      out.println("      <div style=\"text-align: right;\"> </div>");
      out.println("      <form name=\"appletcontrol\">");
      out.println("        <div style=\"text-align: right;\"> </div>");
      out
          .println("        <table style=\"width: 100%; text-align: left;\" border=\"0\"");
      out.println(" cellpadding=\"2\" cellspacing=\"2\">");
      out.println("          <tbody>");
      switch (FormatChoice) {
      case FromLinks: 
      {
        for (int i = 0; i < MolecList.getModel().getSize(); i++) {
          out.println("            <tr>");
          out.println("              <td colspan=\"1\" rowspan=\"1\"");
          out
              .println(" style=\"vertical-align: top; white-space: nowrap; text-align: right;\"><span");
          out.println(" style=\"font-weight: bold;\">Molecule Name" + i
              + ":</span><br>");
          out.println("              </td> <td>");
          int dotIndex = MolecList.getModel().getElementAt(i).toString()
              .indexOf(".");
          String Itemname = MolecList.getModel().getElementAt(i).toString()
              .substring(0, dotIndex);
          out.println("                <script>");
          switch (Rendering) {
          case Wireframe: {
            
            out
                .println("       jmolLink('load "
                    + MolecList.getModel().getElementAt(i)
                    + "; spacefill 0%; wireframe; labels %e; set labeloffset 0 0; background black;',\""
                    + Itemname + "\");");
            break;
          }
          case BallandStick: {
            
            out.println("       jmolLink('load "
                + MolecList.getModel().getElementAt(i)
                + "; spacefill 20%; wireframe 0.15; background black; ',\""
                + Itemname + "\");");
            break;
          }
          case Spacefilling: {
            
            out.println("       jmolLink('load "
                + MolecList.getModel().getElementAt(i)
                + "; spacefill 100%; wireframe; background black; ',\""
                + Itemname + "\");");
            break;
          }

          }
          
          out.println("              </script></td><td> place holder text");
          out.println("             </td></tr>");
        }
        break;
      }
      case FromMenu: 
      {
        out.println("            <tr>");
        out.println("              <td colspan=\"1\" rowspan=\"1\"");
        out
            .println(" style=\"vertical-align: top; white-space: nowrap; text-align: right;\"><span");
        out
            .println(" style=\"font-weight: bold;\">Choose a Molecule:</span><br>");
        out.println("              </td>");
        out.println("              <td style=\"vertical-align: top;\">");
        out.println("              <script>");
        out.println("		jmolMenu([['load empty;','none'],");
        
        for (int i = 0; i < MolecList.getModel().getSize(); i++) {
          int dotIndex = MolecList.getModel().getElementAt(i).toString()
              .indexOf(".");
          String Itemname = MolecList.getModel().getElementAt(i).toString()
              .substring(0, dotIndex);
          switch (Rendering) {
          case Wireframe: {
            
            out
                .println("       ['load "
                    + MolecList.getModel().getElementAt(i)
                    + "; spacefill 0%; wireframe; labels %e; set labeloffset 0 0; background black;',\""
                    + Itemname + "\"],");
            break;
          }
          case BallandStick: {
            
            out.println("       ['load " + MolecList.getModel().getElementAt(i)
                + "; spacefill 20%; wireframe 0.15; background black; ',\""
                + Itemname + "\"],");
            break;
          }
          case Spacefilling: {
            
            out.println("       ['load " + MolecList.getModel().getElementAt(i)
                + "; spacefill 100%; wireframe; background black; ',\""
                + Itemname + "\"],");
            break;
          }

          }
          break;
        }
        out.println("		]);");
        out.println("		</script>");
        out.println("              <br>");
        out.println("              </td>");
        out.println("            </tr>");
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        

      }
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
      out.println("Gutow 7/2006</a>. </small> </td>");
      out.println("    </tr>");
      out.println("    <tr>");
      out
          .println("      <td colspan=\"2\" rowspan=\"1\" style=\"vertical-align: top;\">");
      out
          .println("      <div style=\"text-align: center;\"><span style=\"font-weight: bold;\">Instructions");
      out.println("for using Jmol to display");
      out.println("molecules</span>: <br>");
      out.println("      </div>");
      out.println("      <ol>");
      out
          .println("        <li>Choose which molecules to display by selecting them using the");
      out.println("popup menus or clicking on the appropriate link.");
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
