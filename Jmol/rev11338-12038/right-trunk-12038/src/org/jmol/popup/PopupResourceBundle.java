
package org.jmol.popup;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Properties;

import org.jmol.i18n.GT;

class PopupResourceBundle {

  PopupResourceBundle(String menuStructure, Properties menuText) {
    buildStructure(menuStructure);
    localize(menuStructure != null, menuText);
  }

  String getMenu(String title) {
    return "# Jmol.mnu " + title + "\n\n" +
           "# Part I -- Menu Structure\n" +
           "# ------------------------\n\n" +
           dumpStructure(menuContents) + "\n\n" +
           "# Part II -- Key Definitions\n" +
           "# --------------------------\n\n" +
           dumpStructure(structureContents) + "\n\n" +
           "# Part III -- Word Translations\n" +
           "# -----------------------------\n\n" +
           dumpWords();
  }
  
  String getStructure(String key) {
    return structure.getProperty(key);
  }

  void addStructure(String key, String value) {
    structure.setProperty(key, value);
  }

  String getWord(String key) {
    String str = words.getProperty(key);
    return (str == null ? key : str);
  }

  
  private Properties structure = new Properties();
  private Properties words = new Properties();
  
  private static String Box(String cmd) {
    return "if (showBoundBox or showUnitcell) {"+cmd+"} else {boundbox on;"+cmd+";boundbox off}";
  }

  private static String[][] menuContents = {
    
      {   "@COLOR", "black white red orange yellow green cyan blue indigo violet"},      
      {   "@AXESCOLOR", "gray salmon maroon olive slateblue gold orchid"},
      
      {   "popupMenu",
          "FRAMESbyModelComputedMenu configurationComputedMenu - selectMenuText viewMenu renderMenu colorMenu - surfaceMenu SYMMETRYUNITCELLMenu - "
              + "zoomMenu spinMenu VIBRATIONMenu "
              + "FRAMESanimateMenu - "
              + "measureMenu pickingMenu - JVM12showConsole JVM12showMenu SIGNEDfileMenu computationMenu - "
              + "languageComputedMenu aboutComputedMenu" },
              
      {   "selectMenuText",
          "hideNotSelectedCheckbox showSelectionsCheckbox - selectAll selectNone invertSelection - elementsComputedMenu SYMMETRYSelectComputedMenu - "
              + "PDBproteinMenu PDBnucleicMenu PDBheteroMenu PDBcarboMenu PDBnoneOfTheAbove" },

      {   "PDBproteinMenu", 
          "PDBaaResiduesComputedMenu - "
              + "allProtein proteinBackbone proteinSideChains - "
              + "polar nonpolar - "
              + "positiveCharge negativeCharge noCharge" },
              
      {   "PDBcarboMenu",
          "PDBcarboResiduesComputedMenu - allCarbo" },

      {   "PDBnucleicMenu",
          "PDBnucleicResiduesComputedMenu - allNucleic nucleicBackbone nucleicBases - DNA RNA - "
              + "atPairs auPairs gcPairs" },
              
      {   "PDBheteroMenu",
          "PDBheteroComputedMenu - allHetero Solvent Water - "
              + "Ligand exceptWater nonWaterSolvent" },

      {   "viewMenu",
          "front left right top bottom back" },

      {   "renderMenu",
          "perspectiveDepthCheckbox showBoundBoxCheckbox showUNITCELLCheckbox showAxesCheckbox stereoMenu - renderSchemeMenu - atomMenu labelMenu bondMenu hbondMenu ssbondMenu - "
              + "PDBstructureMenu [set_axes]Menu [set_boundbox]Menu [set_UNITCELL]Menu" },

      {   "renderSchemeMenu",
          "renderCpkSpacefill renderBallAndStick "
              + "renderSticks renderWireframe PDBrenderCartoonsOnly PDBrenderTraceOnly" },
                            
      {   "atomMenu",
          "showHydrogensCheckbox - atomNone - "
              + "atom15 atom20 atom25 atom50 atom75 atom100" },

      {   "bondMenu",
          "bondNone bondWireframe - "
              + "bond100 bond150 bond200 bond250 bond300" },

      {   "hbondMenu",
          "PDBhbondCalc hbondNone hbondWireframe - "
              + "PDBhbondSidechain PDBhbondBackbone - "
              + "hbond100 hbond150 hbond200 hbond250 hbond300" },

      {   "ssbondMenu",
          "ssbondNone ssbondWireframe - "
              + "PDBssbondSidechain PDBssbondBackbone - "
              + "ssbond100 ssbond150 ssbond200 ssbond250 ssbond300" },

      {   "PDBstructureMenu",
          "structureNone - "
              + "backbone cartoon cartoonRockets ribbons rockets strands trace" },

      {   "VIBRATIONvectorMenu",
          "vectorOff vectorOn vector3 vector005 vector01 - "
              + "vectorScale02 vectorScale05 vectorScale1 vectorScale2 vectorScale5" },

      {   "stereoMenu",
          "stereoNone stereoRedCyan stereoRedBlue stereoRedGreen stereoCrossEyed stereoWallEyed" },

      {   "labelMenu",
          "labelNone - " + "labelSymbol labelName labelNumber - "
              + "labelPositionMenu" },

      {   "labelPositionMenu",
          "labelCentered labelUpperRight labelLowerRight labelUpperLeft labelLowerLeft" },

      {   "colorMenu",
          "colorrasmolCheckbox - [color_atoms]Menu [color_bonds]Menu [color_hbonds]Menu [color_ssbonds]Menu colorPDBStructuresMenu [color_isosurface]Menu"
              + " - [color_labels]Menu [color_vectors]Menu - [color_axes]Menu [color_boundbox]Menu [color_UNITCELL]Menu [color_background]Menu" },

      { "[color_atoms]Menu", "schemeMenu - @COLOR - opaque translucent" },
      { "[color_bonds]Menu", "none - @COLOR - opaque translucent" },
      { "[color_hbonds]Menu", null },
      { "[color_ssbonds]Menu", null },
      { "[color_labels]Menu", null },
      { "[color_vectors]Menu", null },
      { "[color_backbone]Menu", "none - schemeMenu - @COLOR - opaque translucent" },
      { "[color_cartoon]sMenu", null },
      { "[color_ribbon]sMenu", null },
      { "[color_rockets]Menu", null },
      { "[color_strands]Menu", null },
      { "[color_trace]Menu", null },
      { "[color_background]Menu", "@COLOR" },
      { "[color_isosurface]Menu", "@COLOR - opaque translucent" },
      { "[color_axes]Menu", "@AXESCOLOR" },
      { "[color_boundbox]Menu", null },
      { "[color_UNITCELL]Menu", null },


      {   "colorPDBStructuresMenu",
          "[color_backbone]Menu [color_cartoon]sMenu [color_ribbon]sMenu [color_rockets]Menu [color_strands]Menu [color_trace]Menu" },

      {   "schemeMenu",
          "cpk - formalcharge partialcharge#CHARGE - altloc#PDB amino#PDB chain#PDB group#PDB molecule monomer#PDB shapely#PDB structure#PDB relativeTemperature#BFACTORS fixedTemperature#BFACTORS" },

      {   "zoomMenu",
          "zoom50 zoom100 zoom150 zoom200 zoom400 zoom800 - "
              + "zoomIn zoomOut" },

      {   "spinMenu",
          "spinOn spinOff - " + "[set_spin_X]Menu [set_spin_Y]Menu [set_spin_Z]Menu - "
              + "[set_spin_FPS]Menu" },

      {   "VIBRATIONMenu", 
          "vibrationOff vibrationOn VIBRATIONvectorMenu" },

      {   "FRAMESanimateMenu",
          "animModeMenu - play pause resume stop - nextframe prevframe rewind - playrev restart - "
              + "FRAMESanimFpsMenu" },

      {   "FRAMESanimFpsMenu", 
          "animfps5 animfps10 animfps20 animfps30 animfps50" },

      {   "measureMenu",
          "showMeasurementsCheckbox - "
              + "measureOff measureDistance measureAngle measureTorsion - "
              + "measureDelete JVM12measureList - distanceNanometers distanceAngstroms distancePicometers" },

      {   "pickingMenu",
          "pickOff pickCenter pickIdent pickLabel pickAtom "
              + "pickMolecule pickElement PDBpickChain PDBpickGroup SYMMETRYpickSite pickSpin" },

      {   "computationMenu",
          "minimize"
               },

              
      {   "JVM12showMenu",
          "showHistory showFile showFileHeader - "
              + "showOrient showMeasure - "
              + "showSpacegroup showState SYMMETRYshowSymmetry UNITCELLshow - showIsosurface showMo - extractMOL" },

      {   "SIGNEDfileMenu",
          "loadFileOrUrl loadPdb loadScript - "
              + "reload loadFileUnitCell - "
              + "writeFileTextVARIABLE writeState writeHistory writeIsosurface - writeGif writeJpg writePng writePovray - "
              + "writeVrml writeX3d writeIdtf writeMaya" },

      { "[set_spin_X]Menu", "s0 s5 s10 s20 s30 s40 s50" },
      { "[set_spin_Y]Menu", null },
      { "[set_spin_Z]Menu", null },
      { "[set_spin_FPS]Menu", null },

      {   "animModeMenu", 
          "onceThrough palindrome loop" },


      {   "surfaceMenu",
          "surfDots surfVDW surfSolventAccessible14 surfSolvent14 surfMolecular CHARGEsurfMEP surfMoComputedMenu - surfOpaque surfTranslucent surfOff" },

      {   "SYMMETRYUNITCELLMenu",
          "SYMMETRYShowComputedMenu SYMMETRYhide UNITCELLone UNITCELLnine UNITCELLnineRestricted UNITCELLninePoly" },

      {   "[set_axes]Menu", 
          "off#axes dotted - byPixelMenu byAngstromMenu" },

      { "[set_boundbox]Menu", null },
      { "[set_UNITCELL]Menu", null },

      {   "byPixelMenu", 
          "1p 3p 5p 10p" },

      {   "byAngstromMenu", 
          "10a 20a 25a 50a 100a" },




      {   "aboutComputedMenu", 
          "- " },

  };
  
  
  
  private static String[][] structureContents = {

      { "colorrasmolCheckbox", ""},
      { "hideNotSelectedCheckbox", "set hideNotSelected true | set hideNotSelected false; hide(none)" },
      { "perspectiveDepthCheckbox", ""},
      { "showAxesCheckbox", "set showAxes true | set showAxes false;set axesMolecular" },
      { "showBoundBoxCheckbox", ""},
      { "showHydrogensCheckbox", ""},
      { "showMeasurementsCheckbox", ""},
      { "showSelectionsCheckbox", ""},
      { "showUNITCELLCheckbox", ""},

      { "selectAll", "SELECT all" },
      { "selectNone", "SELECT none" },
      { "invertSelection", "SELECT not selected" },
   
      { "allProtein", "SELECT protein" },
      { "proteinBackbone", "SELECT protein and backbone" },
      { "proteinSideChains", "SELECT protein and not backbone" },
      { "polar", "SELECT protein and polar" },
      { "nonpolar", "SELECT protein and not polar" },
      { "positiveCharge", "SELECT protein and basic" },
      { "negativeCharge", "SELECT protein and acidic" },
      { "noCharge", "SELECT protein and not (acidic,basic)" },
      { "allCarbo", "SELECT carbohydrate" },

      { "allNucleic", "SELECT nucleic" },
      { "DNA", "SELECT dna" },
      { "RNA", "SELECT rna" },
      { "nucleicBackbone", "SELECT nucleic and backbone" },
      { "nucleicBases", "SELECT nucleic and not backbone" },
      { "atPairs", "SELECT a,t" },
      { "gcPairs", "SELECT g,c" },
      { "auPairs", "SELECT a,u" },
      { "A", "SELECT a" },
      { "C", "SELECT c" },
      { "G", "SELECT g" },
      { "T", "SELECT t" },
      { "U", "SELECT u" },

      { "allHetero", "SELECT hetero" },
      { "Solvent", "SELECT solvent" },
      { "Water", "SELECT water" },
      
      { "nonWaterSolvent", "SELECT solvent and not water" },
      { "exceptWater", "SELECT hetero and not water" },
      { "Ligand", "SELECT ligand" },

      
      { "PDBnoneOfTheAbove", "SELECT not(hetero,protein,nucleic,carbohydrate)" },

      { "front", Box( "moveto 2.0 front;delay 1" ) },
      { "left", Box( "moveto 1.0 front;moveto 2.0 left;delay 1"  ) },
      { "right", Box( "moveto 1.0 front;moveto 2.0 right;delay 1"  ) },
      { "top", Box( "moveto 1.0 front;moveto 2.0 top;delay 1"  ) },
      { "bottom", Box( "moveto 1.0 front;moveto 2.0 bottom;delay 1"  ) },
      { "back", Box( "moveto 1.0 front;moveto 2.0 back;delay 1"  ) },

      { "renderCpkSpacefill", "restrict bonds not selected;select not selected;spacefill 100%;color cpk" },
      { "renderBallAndStick", "restrict bonds not selected;select not selected;spacefill 20%;wireframe 0.15;color cpk" },
      { "renderSticks", "restrict bonds not selected;select not selected;wireframe 0.3;color cpk" },
      { "renderWireframe", "restrict bonds not selected;select not selected;wireframe on;color cpk" },
      { "PDBrenderCartoonsOnly", "restrict bonds not selected;select not selected;cartoons on;color structure" },
      { "PDBrenderTraceOnly", "restrict bonds not selected;select not selected;trace on;color structure" },

      { "atomNone", "cpk off" },
      { "atom15", "cpk 15%" },
      { "atom20", "cpk 20%" },
      { "atom25", "cpk 25%" },
      { "atom50", "cpk 50%" },
      { "atom75", "cpk 75%" },
      { "atom100", "cpk on" },

      { "bondNone", "wireframe off" },
      { "bondWireframe", "wireframe on" },
      { "bond100", "wireframe .1" },
      { "bond150", "wireframe .15" },
      { "bond200", "wireframe .2" },
      { "bond250", "wireframe .25" },
      { "bond300", "wireframe .3" },

      { "PDBhbondCalc", "hbonds calculate" },
      { "hbondNone", "hbonds off" },
      { "hbondWireframe", "hbonds on" },
      { "PDBhbondSidechain", "set hbonds sidechain" },
      { "PDBhbondBackbone", "set hbonds backbone" },
      { "hbond100", "hbonds .1" },
      { "hbond150", "hbonds .15" },
      { "hbond200", "hbonds .2" },
      { "hbond250", "hbonds .25" },
      { "hbond300", "hbonds .3" },

      { "ssbondNone", "ssbonds off" },
      { "ssbondWireframe", "ssbonds on" },
      { "PDBssbondSidechain", "set ssbonds sidechain" },
      { "PDBssbondBackbone", "set ssbonds backbone" },
      { "ssbond100", "ssbonds .1" },
      { "ssbond150", "ssbonds .15" },
      { "ssbond200", "ssbonds .2" },
      { "ssbond250", "ssbonds .25" },
      { "ssbond300", "ssbonds .3" },

      { "structureNone",
          "backbone off;cartoons off;ribbons off;rockets off;strands off;trace off;" },
      { "backbone", "restrict not selected;select not selected;backbone 0.3" },
      { "cartoon", "restrict not selected;select not selected;set cartoonRockets false;cartoons on" },
      { "cartoonRockets", "restrict not selected;select not selected;set cartoonRockets;cartoons on" },
      { "ribbons", "restrict not selected;select not selected;ribbons on" },
      { "rockets", "restrict not selected;select not selected;rockets on" },
      { "strands", "restrict not selected;select not selected;strands on" },
      { "trace", "restrict not selected;select not selected;trace 0.3" },

      { "vibrationOff", "vibration off" },
      { "vibrationOn", "vibration on" },

      { "vectorOff", "vectors off" },
      { "vectorOn", "vectors on" },
      { "vector3", "vectors 3" },
      { "vector005", "vectors 0.05" },
      { "vector01", "vectors 0.1" },
      { "vectorScale02", "vector scale 0.2" },
      { "vectorScale05", "vector scale 0.5" },
      { "vectorScale1", "vector scale 1" },
      { "vectorScale2", "vector scale 2" },
      { "vectorScale5", "vector scale 5" },

      { "stereoNone", "stereo off" },
      { "stereoRedCyan", "stereo redcyan 3" },
      { "stereoRedBlue", "stereo redblue 3" },
      { "stereoRedGreen", "stereo redgreen 3" },
      { "stereoCrossEyed", "stereo -5" },
      { "stereoWallEyed", "stereo 5" },

      { "labelNone", "label off" },
      { "labelSymbol", "label %e" },
      { "labelName", "label %a" },
      { "labelNumber", "label %i" },

      { "labelCentered", "set labeloffset 0 0" },
      { "labelUpperRight", "set labeloffset 4 4" },
      { "labelLowerRight", "set labeloffset 4 -4" },
      { "labelUpperLeft", "set labeloffset -4 4" },
      { "labelLowerLeft", "set labeloffset -4 -4" },

      { "zoom50", "zoom 50" },
      { "zoom100", "zoom 100" },
      { "zoom150", "zoom 150" },
      { "zoom200", "zoom 200" },
      { "zoom400", "zoom 400" },
      { "zoom800", "zoom 800" },
      { "zoomIn", "move 0 0 0 40 0 0 0 0 1" },
      { "zoomOut", "move 0 0 0 -40 0 0 0 0 1" },

      { "spinOn", "spin on" },
      { "spinOff", "spin off" },

      { "s0", "0" },
      { "s5", "5" },
      { "s10", "10" },
      { "s20", "20" },
      { "s30", "30" },
      { "s40", "40" },
      { "s50", "50" },

      { "onceThrough", "anim mode once#" },
      { "palindrome", "anim mode palindrome#" },
      { "loop", "anim mode loop#" },
      { "play", "anim play#" },
      { "pause", "anim pause#" },
      { "resume", "anim resume#" },
      { "stop", "anim off#" },
      
      { "nextframe", "frame next#" },
      { "prevframe", "frame prev#" },
      { "playrev", "anim playrev#" },
      
      { "rewind", "anim rewind#" },
      { "restart", "anim on#" },
      
      { "animfps5", "anim fps 5#" },
      { "animfps10", "anim fps 10#" },
      { "animfps20", "anim fps 20#" },
      { "animfps30", "anim fps 30#" },
      { "animfps50", "anim fps 50#" },

      { "measureOff", "set pickingstyle MEASURE OFF; set picking OFF" },
      { "measureDistance",
          "set pickingstyle MEASURE; set picking MEASURE DISTANCE" },
      { "measureAngle", "set pickingstyle MEASURE; set picking MEASURE ANGLE" },
      { "measureTorsion",
          "set pickingstyle MEASURE; set picking MEASURE TORSION" },
      { "measureDelete", "measure delete" },
      { "JVM12measureList", "console on;show measurements" },
      { "distanceNanometers", "select *; set measure nanometers" },
      { "distanceAngstroms", "select *; set measure angstroms" },
      { "distancePicometers", "select *; set measure picometers" },

      { "pickOff", "set picking off" },
      { "pickCenter", "set picking center" },
      
      { "pickIdent", "set picking ident" },
      { "pickLabel", "set picking label" },
      { "pickAtom", "set picking atom" },
      { "PDBpickChain", "set picking chain" },
      { "pickElement", "set picking element" },
      { "PDBpickGroup", "set picking group" },
      { "pickMolecule", "set picking molecule" },
      { "SYMMETRYpickSite", "set picking site" },
      { "pickSpin", "set picking spin" },

      { "JVM12showConsole", "console" },
      { "showFile", "console on;show file" },
      { "showFileHeader", "console on;getProperty FileHeader" },
      { "showHistory", "console on;show history" },
      { "showIsosurface", "console on;show isosurface" },
      { "showMeasure", "console on;show measure" },
      { "showMo", "console on;show mo" },
      { "showModel", "console on;show model" },
      { "showOrient", "console on;show orientation" },
      { "showSpacegroup", "console on;show spacegroup" },
      { "showState", "console on;show state" },
      
      { "reload", "load \"\"" },
      { "loadPdb", "load ?PdbId?" },      
      { "loadFileOrUrl", "load ?" },      
      { "loadFileUnitCell", "load ? {1 1 1}" },      
      { "loadScript", "script ?.spt" },      

      { "writeFileTextVARIABLE", "write file \"?FILE?\"" },      
      { "writeState", "write state \"?FILEROOT?.spt\"" },      
      { "writeHistory", "write history \"?FILEROOT?.his\"" },     
      { "writeIsosurface", "write isosurface \"?FILEROOT?.jvxl\"" },      
      { "writeGif", "write image \"?FILEROOT?.gif\"" },      
      { "writeJpg", "write image \"?FILEROOT?.jpg\"" },      
      { "writePng", "write image \"?FILEROOT?.png\"" },      
      { "writePovray", "write POVRAY \"?FILEROOT?.pov\"" },      
      { "writeVrml", "write VRML \"?FILEROOT?.wrl\"" },      
      { "writeX3d", "write X3D \"?FILEROOT?.x3d\"" },      
      { "writeIdtf", "write IDTF \"?FILEROOT?.x3d\"" },      
      { "writeMaya", "write MAYA \"?FILEROOT?.ma\"" },       
      { "SYMMETRYshowSymmetry", "console on;show symmetry" },
      { "UNITCELLshow", "console on;show unitcell" },
      { "extractMOL", "console on;getproperty extractModel \"visible\" " },
      
       { "minimize", "minimize" },    
      
      
      { "surfDots", "dots on" },
      { "surfVDW", "isosurface delete resolution 0 solvent 0 translucent" },
      { "surfMolecular", "isosurface delete resolution 0 molecular translucent" },
      { "surfSolvent14",
          "isosurface delete resolution 0 solvent 1.4 translucent" },
      { "surfSolventAccessible14",
          "isosurface delete resolution 0 sasurface 1.4 translucent" },
      { "CHARGEsurfMEP",
          "isosurface delete resolution 0 molecular map MEP translucent" },
      { "surfOpaque", "mo opaque;isosurface opaque" },
      { "surfTranslucent", "mo translucent;isosurface translucent" },
      { "surfOff", "mo delete;isosurface delete;select *;dots off" },
      { "SYMMETRYhide", "draw sym_* delete" },
      { "UNITCELLone",
          "save orientation;load \"\" {1 1 1} ;restore orientation;center" },
      { "UNITCELLnine",
          "save orientation;load \"\" {444 666 1} ;restore orientation;center" },
      { "UNITCELLnineRestricted",
          "save orientation;load \"\" {444 666 1} ;restore orientation; unitcell on; display cell=555;center visible;zoom 200" },
      { "UNITCELLninePoly",
          "save orientation;load \"\" {444 666 1} ;restore orientation; unitcell on; display cell=555; polyhedra 4,6 (displayed);center (visible);zoom 200" },

      { "1p", "on" },
      { "3p", "3" },
      { "5p", "5" },
      { "10p", "10" },

      { "10a", "0.1" },
      { "20a", "0.20" },
      { "25a", "0.25" },
      { "50a", "0.50" },
      { "100a", "1.0" },



  

  
  };
  
  private String[] getWordContents() {
    
    boolean wasTranslating = GT.getDoTranslate();
    if (!wasTranslating)
      GT.setDoTranslate(true);
    String[] words = new String[] {
        "modelSetMenu", GT._("No atoms loaded"),
        
        "configurationComputedMenu", GT._("Configurations"),
        "elementsComputedMenu", GT._("Element"),
        "FRAMESbyModelComputedMenu", GT._("Model/Frame"),
        "languageComputedMenu", GT._("Language"),
        "PDBaaResiduesComputedMenu", GT._("By Residue Name"),
        "PDBnucleicResiduesComputedMenu", GT._("By Residue Name"),
        "PDBcarboResiduesComputedMenu", GT._("By Residue Name"),
        "PDBheteroComputedMenu", GT._("By HETATM"),
        "surfMoComputedMenu", GT._("Molecular Orbitals"),
        "SYMMETRYSelectComputedMenu", GT._("Symmetry"),
        "SYMMETRYShowComputedMenu", GT._("Space Group"),
        "SYMMETRYhide", GT._("Hide Symmetry"),
        "hiddenModelSetText", GT._("Model information"),
        "selectMenuText", GT._("Select ({0})"),
        "allModelsText", GT._("All {0} models"),
        "configurationMenuText", GT._("Configurations ({0})"),
        "modelSetCollectionText", GT._("Collection of {0} models"),
        "atomsText", GT._("atoms: {0}"),
        "bondsText", GT._("bonds: {0}"),
        "groupsText", GT._("groups: {0}"),
        "chainsText", GT._("chains: {0}"),
        "polymersText", GT._("polymers: {0}"),
        "modelMenuText", GT._("model {0}"),
        "viewMenuText", GT._("View {0}"),
        "mainMenuText", GT._("Main Menu"),
        "biomoleculesMenuText", GT._("Biomolecules"),
        "biomoleculeText", GT._("biomolecule {0} ({1} atoms)"),
        "loadBiomoleculeText", GT._("load biomolecule {0} ({1} atoms)"),
        
        

        "selectAll", GT._("All"),
        "selectNone", GT._("None"),
        "hideNotSelectedCheckbox", GT._("Display Selected Only"),
        "invertSelection", GT._("Invert Selection"),

        "viewMenu", GT._("View"),
        "front", GT._("Front"),
        "left", GT._("Left"),
        "right", GT._("Right"),
        "top", GT._("Top"),
        "bottom", GT._("Bottom"),
        "back", GT._("Back"),

        "PDBproteinMenu", GT._("Protein"),
        "allProtein", GT._("All"),
        "proteinBackbone", GT._("Backbone"),
        "proteinSideChains", GT._("Side Chains"),
        "polar", GT._("Polar Residues"),
        "nonpolar", GT._("Nonpolar Residues"),
        "positiveCharge", GT._("Basic Residues (+)"),
        "negativeCharge", GT._("Acidic Residues (-)"),
        "noCharge", GT._("Uncharged Residues"),
        "PDBnucleicMenu", GT._("Nucleic"),
        "allNucleic", GT._("All"),
        "DNA", GT._("DNA"),
        "RNA", GT._("RNA"),
        "nucleicBackbone", GT._("Backbone"),
        "nucleicBases", GT._("Bases"),
        "atPairs", GT._("AT pairs"),
        "gcPairs", GT._("GC pairs"),
        "auPairs", GT._("AU pairs"),
        "PDBheteroMenu", GT._("Hetero"),
        "allHetero", GT._("All PDB \"HETATM\""),
        "Solvent", GT._("All Solvent"),
        "Water", GT._("All Water"),
        "nonWaterSolvent",
            GT._("Nonaqueous Solvent") + " (solvent and not water)",
        "exceptWater", GT._("Nonaqueous HETATM") + " (hetero and not water)",
        "Ligand", GT._("Ligand") + " (hetero and not solvent)",

        "allCarbo", GT._("All"),
        "PDBcarboMenu", GT._("Carbohydrate"),
        "PDBnoneOfTheAbove", GT._("None of the above"),

        "renderMenu", GT._("Style"),
        "renderSchemeMenu", GT._("Scheme"),
        "renderCpkSpacefill", GT._("CPK Spacefill"),
        "renderBallAndStick", GT._("Ball and Stick"),
        "renderSticks", GT._("Sticks"),
        "renderWireframe", GT._("Wireframe"),
        "PDBrenderCartoonsOnly", GT._("Cartoon"),
        "PDBrenderTraceOnly", GT._("Trace"),

        "atomMenu", GT._("Atoms"),
        "atomNone", GT._("Off"),
        "atom15", GT._("{0}% van der Waals", "15"),
        "atom20", GT._("{0}% van der Waals", "20"),
        "atom25", GT._("{0}% van der Waals", "25"),
        "atom50", GT._("{0}% van der Waals", "50"),
        "atom75", GT._("{0}% van der Waals", "75"),
        "atom100", GT._("{0}% van der Waals", "100"),

        "bondMenu", GT._("Bonds"),
        "bondNone", GT._("Off"),
        "bondWireframe", GT._("On"),
        "bond100", GT._("{0} \u", "0.10"),
        "bond150", GT._("{0} \u", "0.15"),
        "bond200", GT._("{0} \u", "0.20"),
        "bond250", GT._("{0} \u", "0.25"),
        "bond300", GT._("{0} \u", "0.30"),

        "hbondMenu", GT._("Hydrogen Bonds"),
        "hbondNone", GT._("Off"),
        "PDBhbondCalc", GT._("Calculate"),
        "hbondWireframe", GT._("On"),
        "PDBhbondSidechain", GT._("Set H-Bonds Side Chain"),
        "PDBhbondBackbone", GT._("Set H-Bonds Backbone"),
        "hbond100", GT._("{0} \u", "0.10"),
        "hbond150", GT._("{0} \u", "0.15"),
        "hbond200", GT._("{0} \u", "0.20"),
        "hbond250", GT._("{0} \u", "0.25"),
        "hbond300", GT._("{0} \u", "0.30"),

        "ssbondMenu", GT._("Disulfide Bonds"),
        "ssbondNone", GT._("Off"),
        "ssbondWireframe", GT._("On"),
        "PDBssbondSidechain", GT._("Set SS-Bonds Side Chain"),
        "PDBssbondBackbone", GT._("Set SS-Bonds Backbone"),
        "ssbond100", GT._("{0} \u", "0.10"),
        "ssbond150", GT._("{0} \u", "0.15"),
        "ssbond200", GT._("{0} \u", "0.20"),
        "ssbond250", GT._("{0} \u", "0.25"),
        "ssbond300", GT._("{0} \u", "0.30"),

        "PDBstructureMenu", GT._("Structures"),
        "structureNone", GT._("Off"),
        "backbone", GT._("Backbone"),
        "cartoon", GT._("Cartoon"),
        "cartoonRockets", GT._("Cartoon Rockets"),
        "ribbons", GT._("Ribbons"),
        "rockets", GT._("Rockets"),
        "strands", GT._("Strands"),
        "trace", GT._("Trace"),

        "VIBRATIONMenu", GT._("Vibration"),
        "vibrationOff", GT._("Off"),
        "vibrationOn", GT._("On"),
        "VIBRATIONvectorMenu", GT._("Vectors"),
        "vectorOff", GT._("Off"),
        "vectorOn", GT._("On"),
        "vector3", GT._("{0} pixels", "3"),
        "vector005", GT._("{0} \u", "0.05"),
        "vector01", GT._("{0} \u", "0.10"),
        "vectorScale02", GT._("Scale {0}", "0.2"),
        "vectorScale05", GT._("Scale {0}", "0.5"),
        "vectorScale1", GT._("Scale {0}", "1"),
        "vectorScale2", GT._("Scale {0}", "2"),
        "vectorScale5", GT._("Scale {0}", "5"),

        "stereoMenu", GT._("Stereographic"),
        "stereoNone", GT._("None"),
        "stereoRedCyan", GT._("Red+Cyan glasses"),
        "stereoRedBlue", GT._("Red+Blue glasses"),
        "stereoRedGreen", GT._("Red+Green glasses"),
        "stereoCrossEyed", GT._("Cross-eyed viewing"),
        "stereoWallEyed", GT._("Wall-eyed viewing"),

        "labelMenu", GT._("Labels"),

        "labelNone", GT._("None"),
        "labelSymbol", GT._("With Element Symbol"),
        "labelName", GT._("With Atom Name"),
        "labelNumber", GT._("With Atom Number"),

        "labelPositionMenu", GT._("Position Label on Atom"),
        "labelCentered", GT._("Centered"),
        "labelUpperRight", GT._("Upper Right"),
        "labelLowerRight", GT._("Lower Right"),
        "labelUpperLeft", GT._("Upper Left"),
        "labelLowerLeft", GT._("Lower Left"),

        "colorMenu", GT._("Color"),
        "[color_atoms]Menu", GT._("Atoms"),

        "schemeMenu", GT._("By Scheme"),
        "cpk", GT._("Element (CPK)"),
        "altloc#PDB", GT._("Alternative Location"),
        "molecule", GT._("Molecule"),
        "formalcharge", GT._("Formal Charge"),
        "partialcharge#CHARGE", GT._("Partial Charge"),
        "relativeTemperature#BFACTORS", GT._("Temperature (Relative)"),
        "fixedTemperature#BFACTORS", GT._("Temperature (Fixed)"),

        "amino#PDB", GT._("Amino Acid"),
        "structure#PDB", GT._("Secondary Structure"),
        "chain#PDB", GT._("Chain"),
        "group#PDB", GT._("Group"),
        "monomer#PDB", GT._("Monomer"),
        "shapely#PDB", GT._("Shapely"),

        "none", GT._("Inherit"),
        "black", GT._("Black"),
        "white", GT._("White"),
        "cyan", GT._("Cyan"),

        "red", GT._("Red"),
        "orange", GT._("Orange"),
        "yellow", GT._("Yellow"),
        "green", GT._("Green"),
        "blue", GT._("Blue"),
        "indigo", GT._("Indigo"),
        "violet", GT._("Violet"),

        "salmon", GT._("Salmon"),
        "olive", GT._("Olive"),
        "maroon", GT._("Maroon"),
        "gray", GT._("Gray"),
        "slateblue", GT._("Slate Blue"),
        "gold", GT._("Gold"),
        "orchid", GT._("Orchid"),

        "opaque", GT._("Make Opaque"),
        "translucent", GT._("Make Translucent"),

        "[color_bonds]Menu", GT._("Bonds"),
        "[color_hbonds]Menu", GT._("Hydrogen Bonds"),
        "[color_ssbonds]Menu", GT._("Disulfide Bonds"),
        "colorPDBStructuresMenu", GT._("Structures"),
        "[color_backbone]Menu", GT._("Backbone"),
        "[color_trace]Menu", GT._("Trace"),
        "[color_cartoon]sMenu", GT._("Cartoon"),
        "[color_ribbon]sMenu", GT._("Ribbons"),
        "[color_rockets]Menu", GT._("Rockets"),
        "[color_strands]Menu", GT._("Strands"),
        "[color_labels]Menu", GT._("Labels"),
        "[color_background]Menu", GT._("Background"),
        "[color_isosurface]Menu", GT._("Surfaces"),
        "[color_vectors]Menu", GT._("Vectors"),
        "[color_axes]Menu", GT._("Axes"),
        "[color_boundbox]Menu", GT._("Boundbox"),
        "[color_UNITCELL]Menu", GT._("Unit cell"),

        "zoomMenu", GT._("Zoom"),
        "zoom50", "50%",
        "zoom100", "100%",
        "zoom150", "150%",
        "zoom200", "200%",
        "zoom400", "400%",
        "zoom800", "800%",
        "zoomIn", GT._("Zoom In"),
        "zoomOut", GT._("Zoom Out"),

        "spinMenu", GT._("Spin"),
        "spinOn", GT._("On"),
        "spinOff", GT._("Off"),

        "[set_spin_X]Menu", GT._("Set X Rate"),
        "[set_spin_Y]Menu", GT._("Set Y Rate"),
        "[set_spin_Z]Menu", GT._("Set Z Rate"),
        "[set_spin_FPS]Menu", GT._("Set FPS"),

        "s0", "0",
        "s5", "5",
        "s10", "10",
        "s20", "20",
        "s30", "30",
        "s40", "40",
        "s50", "50",

        "FRAMESanimateMenu", GT._("Animation"),
        "animModeMenu", GT._("Animation Mode"),
        "onceThrough", GT._("Play Once"),
        "palindrome", GT._("Palindrome"),
        "loop", GT._("Loop"),
        
        "play", GT._("Play"),
        "pause", GT._("Pause"),
        "resume", GT._("Resume"),
        "stop", GT._("Stop"),
        "nextframe", GT._("Next Frame"),
        "prevframe", GT._("Previous Frame"),
        "rewind", GT._("Rewind"),
        "playrev", GT._("Reverse"),
        "restart", GT._("Restart"),

        "FRAMESanimFpsMenu", GT._("Set FPS"),
        "animfps5", "5",
        "animfps10", "10",
        "animfps20", "20",
        "animfps30", "30",
        "animfps50", "50",

        "measureMenu", GT._("Measurements"),
        "measureOff", GT._("Double-Click begins and ends all measurements"),
        "measureDistance", GT._("Click for distance measurement"),
        "measureAngle", GT._("Click for angle measurement"),
        "measureTorsion", GT._("Click for torsion (dihedral) measurement"),
        "measureDelete", GT._("Delete measurements"),
        "JVM12measureList", GT._("List measurements"),
        "distanceNanometers", GT._("Distance units nanometers"),
        "distanceAngstroms", GT._("Distance units Angstroms"),
        "distancePicometers", GT._("Distance units picometers"),

        "pickingMenu", GT._("Set picking"),
        "pickOff", GT._("Off"),
        "pickCenter", GT._("Center"),
        
        "pickIdent", GT._("Identity"),
        "pickLabel", GT._("Label"),
        "pickAtom", GT._("Select atom"),
        "PDBpickChain", GT._("Select chain"),
        "pickElement", GT._("Select element"),
        "PDBpickGroup", GT._("Select group"),
        "pickMolecule", GT._("Select molecule"),
        "SYMMETRYpickSite", GT._("Select site"),
        "pickSpin", GT._("Spin"),

        "JVM12showMenu", GT._("Show"),
        "JVM12showConsole", GT._("Console"),
        "showFile", GT._("File Contents"),
        "showFileHeader", GT._("File Header"),
        "showHistory", GT._("History"),
        "showIsosurface", GT._("Isosurface JVXL data"),
        "showMeasure", GT._("Measurements"),
        "showMo", GT._("Molecular orbital JVXL data"),
        "showModel", GT._("Model"),
        "showOrient", GT._("Orientation"),
        "showSpacegroup", GT._("Space group"),
        "SYMMETRYshowSymmetry", GT._("Symmetry"),
        "showState", GT._("Current state"),
        
        "SIGNEDfileMenu", GT._("File"),
        "reload", GT._("Reload"),      
        "loadPdb", GT._("Open from PDB"),      
        "loadFileOrUrl", GT._("Open file or URL"),      
        "loadFileUnitCell", GT._("Load full unit cell"),      
        "loadScript", GT._("Open script"),      

        "writeFileTextVARIABLE", GT._("Save a copy of {0}"),
        "writeState", GT._("Save script with state"),      
        "writeHistory", GT._("Save script with history"),      
        "writeJpg", GT._("Export {0} image", "JPG"),      
        "writePng", GT._("Export {0} image", "PNG"),      
        "writeGif", GT._("Export {0} image", "GIF"),    
        "writePovray", GT._("Export {0} image", "POV-Ray"),      
        "writeIsosurface", GT._("Save JVXL isosurface"),      
        "writeVrml", GT._("Export {0} 3D model", "VRML"),      
        "writeX3d", GT._("Export {0} 3D model", "X3D"),      
        "writeIdtf", GT._("Export {0} 3D model", "IDTF"),      
        "writeMaya", GT._("Export {0} 3D model", "Maya"),      

        "computationMenu", GT._("Computation"),      
        "minimize", GT._("Optimize structure"),      
        
                
        "UNITCELLshow", GT._("Unit cell"),
        "extractMOL", GT._("Extract MOL data"),

        "surfaceMenu", GT._("Surfaces"),
        "surfDots", GT._("Dot Surface"),
        "surfVDW", GT._("van der Waals Surface"),
        "surfMolecular", GT._("Molecular Surface"),
        "surfSolvent14", GT._("Solvent Surface ({0}-Angstrom probe)", "1.4"),
        "surfSolventAccessible14",
            GT._("Solvent-Accessible Surface (VDW + {0} Angstrom)", "1.4"),
        "CHARGEsurfMEP", GT._("Molecular Electrostatic Potential"),
        "surfOpaque", GT._("Make Opaque"),
        "surfTranslucent", GT._("Make Translucent"),
        "surfOff", GT._("Off"),

        "SYMMETRYUNITCELLMenu", GT._("Symmetry"),
        "UNITCELLone", GT._("Reload {0}", "{1 1 1}"),
        "UNITCELLnine", GT._("Reload {0}", "{444 666 1}"),
        "UNITCELLnineRestricted", GT._("Reload {0} + Display {1}", new Object[] { "{444 666 1}", "555" } ),
        "UNITCELLninePoly", GT._("Reload + Polyhedra"),
        

        "[set_axes]Menu", GT._("Axes"), 
        "[set_boundbox]Menu", GT._("Boundbox"),
        "[set_UNITCELL]Menu", GT._("Unit cell"),

        "off#axes", GT._("Hide"), 
        "dotted", GT._("Dotted"),

        "byPixelMenu", GT._("Pixel Width"), 
        "1p", GT._("{0} px", "1"),
        "3p", GT._("{0} px", "3"), 
        "5p", GT._("{0} px", "5"),
        "10p", GT._("{0} px", "10"),

        "byAngstromMenu", GT._("Angstrom Width"),
        "10a", GT._("{0} \u", "0.10"),
        "20a", GT._("{0} \u", "0.20"),
        "25a", GT._("{0} \u", "0.25"),
        "50a", GT._("{0} \u", "0.50"),
        "100a", GT._("{0} \u", "1.0"),


        "showSelectionsCheckbox", GT._("Selection Halos"),
        "showHydrogensCheckbox", GT._("Show Hydrogens"),
        "showMeasurementsCheckbox", GT._("Show Measurements"),
        "perspectiveDepthCheckbox", GT._("Perspective Depth"),      
        "showBoundBoxCheckbox", GT._("Boundbox"),
        "showAxesCheckbox", GT._("Axes"),
        "showUNITCELLCheckbox", GT._("Unit cell"),      
        "colorrasmolCheckbox", GT._("RasMol Colors"),
        "aboutComputedMenu", GT._("About..."),
        
        

        "APPLETjmolUrl", "http://www.jmol.org",
        "APPLETmouseManualUrl", GT._("Mouse Manual"),
        "APPLETtranslationUrl", GT._("Translations")
    };
 
    if (!wasTranslating)
      GT.setDoTranslate(wasTranslating);

    return words;
  }
  
  private void buildStructure(String menuStructure) {
    addItems(menuContents);
    addItems(structureContents);
    setStructure(menuStructure);
  }
  
  
  private String dumpWords() {
    String[] wordContents = getWordContents();
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < wordContents.length; i++) {
      String key = wordContents[i++];
      if (structure.getProperty(key) == null)
        s.append(key).append(" | ").append(wordContents[i]).append('\n');
    }
    return s.toString();
  }
  
  private String dumpStructure(String[][] items) {
    String previous = "";
    StringBuffer s = new StringBuffer();
    for (int i = 0; i < items.length; i++) {
      String key = items[i][0];
      String label = words.getProperty(key);
      if (label != null)
        key += " | " + label;
      s.append(key).append(" = ")
       .append(items[i][1] == null ? previous : (previous = items[i][1]))
       .append('\n');
    }
    return s.toString();
  }
 
   
   
  public void setStructure(String slist) {
    if (slist == null)
      return;
    BufferedReader br = new BufferedReader(new StringReader(slist));
    String line;
    int pt;
    try {
      while ((line = br.readLine()) != null) {
        if (line.length() == 0 || line.charAt(0) == '#') 
          continue;
        pt = line.indexOf("=");
        if (pt < 0) {
          pt = line.length();
          line += "=";
        }
        String name = line.substring(0, pt).trim();
        String value = line.substring(pt + 1).trim();
        String label = null;
        if ((pt = name.indexOf("|")) >= 0) {
          label = name.substring(pt + 1).trim();
          name = name.substring(0, pt).trim();
        }
        if (name.length() == 0)
          continue;
        if (value.length() > 0)
          structure.setProperty(name, value);
        if (label != null && label.length() > 0)
          words.setProperty(name, GT._(label));
        
      }
    } catch (Exception e) {
      
    }
    try {
      br.close();
    } catch (Exception e) {
    }
  }
  
  private void addItems(String[][] itemPairs) {   
    String previous = "";
    for (int i = 0; i < itemPairs.length; i++) {
      String str = itemPairs[i][1];
      if (str == null)
        str = previous;
      previous = str;
      structure.setProperty(itemPairs[i][0], str);
    }
  }
  
  private void localize(boolean haveUserMenu, Properties menuText) {
    String[] wordContents = getWordContents();
    for (int i = 0; i < wordContents.length;)      
       {
        String item = wordContents[i++];
        String word = wordContents[i++];
        words.setProperty(item, word);
        
        if (menuText != null && item.indexOf("Text") >= 0)
          menuText.setProperty(item, word);
      }
  }
}
