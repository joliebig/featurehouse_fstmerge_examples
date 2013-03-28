using System; 
using System.ComponentModel; 
using System.Collections; 
using System.Drawing; 
using System.Windows.Forms; namespace  RssBandit.AppServices {
	
 public interface  IDocumentWindowManager {
		
  event CancelEventHandler Closing; 
  event EventHandler Closed; 
  event EventHandler Load; 
  void Activate(); 
  void Close(); 
  void ShowMessage(string message); 
  ICommandBarManager CommandBarManager { get; set; } 
  Control Content { get; set; } 
  bool Visible { get; set; } 
  IDocumentWindowCollection Windows { get; }
	}
	
 public interface  IDocumentWindowCollection :  ICollection, IEnumerable {
		
  IDocumentWindow Add(string identifier, Control content, string caption); 
  IDocumentWindow Add(string identifier, Control content, string caption, Image image); 
  void Remove(string identifier); 
  IDocumentWindow this[string identifier] { get; }
	}
	
 public interface  IDocumentWindow {
		
  string Caption { get; set; } 
  Image Image { get; set; } 
  Control Content { get; } 
  bool Visible { get; set; }
	}

}
