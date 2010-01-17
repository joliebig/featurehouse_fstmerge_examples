package net.sf.jabref.collab;

import javax.swing.JComponent;
import javax.swing.JLabel;

import net.sf.jabref.*;
import net.sf.jabref.undo.NamedCompound;
import net.sf.jabref.undo.UndoableInsertString;
import net.sf.jabref.undo.UndoableStringChange;

public class StringNameChange extends Change {

  BibtexString string;
  String mem, tmp, disk, content;
    private BibtexString tmpString;

    public StringNameChange(BibtexString string, BibtexString tmpString,
                          String mem, String tmp, String disk, String content) {
        this.tmpString = tmpString;
        name = Globals.lang("Renamed string")+": '"+tmp+"'";
    this.string = string;
    this.content = content;
    this.mem = mem;
    this.tmp = tmp;
    this.disk = disk;

  }

  public void makeChange(BasePanel panel, BibtexDatabase secondary, NamedCompound undoEdit) {

    if (panel.database().hasStringLabel(disk)) {
      
      Globals.logger("Cannot rename string '"+mem+"' to '"+disk+"' because the name "
                     +"is already in use.");
    }

    if (string != null) {
      string.setName(disk);
      undoEdit.addEdit(new UndoableStringChange(panel, string, true, mem,
                                                disk));
    } else {
      
	String newId = Util.createNeutralId();
	BibtexString bs = new BibtexString(newId, disk, content);
      try {
        panel.database().addString(bs);
        undoEdit.addEdit(new UndoableInsertString(panel, panel.database(), bs));
      } catch (KeyCollisionException ex) {
        Globals.logger("Error: could not add string '"+bs.getName()+"': "+ex.getMessage());
      }
    }

      
      if (tmpString != null) {
          tmpString.setName(disk);
      }
      else {
          String newId = Util.createNeutralId();
	      BibtexString bs = new BibtexString(newId, disk, content);
          secondary.addString(bs);
      }
  }


  JComponent description() {
    return new JLabel(disk+" : "+content);
  }


}
