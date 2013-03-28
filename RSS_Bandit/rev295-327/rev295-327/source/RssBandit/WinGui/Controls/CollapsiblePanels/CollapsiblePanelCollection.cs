using System; 
using System.Runtime.InteropServices; namespace  RssBandit.WinGui.Controls.CollapsiblePanels {
	
 public class  CollapsiblePanelCollection  : System.Collections.CollectionBase {
		
  public  void Add(CollapsiblePanel panel)
  {
   this.List.Add(panel);
  }
 
  public  void Remove(int index)
  {
   if((index >= this.Count) || (index < 0))
   {
    throw new IndexOutOfRangeException("The supplied index is out of range");
   }
   this.List.RemoveAt(index);
  }
 
  public  CollapsiblePanel Item(int index)
  {
   if((index >= this.Count) || (index < 0))
   {
    throw new IndexOutOfRangeException("The supplied index is out of range");
   }
   return (CollapsiblePanel)this.List[index];
  }
 
  public  void Insert(int index, CollapsiblePanel panel)
  {
   this.List.Insert(index, panel);
  }
 
  public  void CopyTo(System.Array array, System.Int32 index)
  {
   this.List.CopyTo(array, index);
  }
 
  public  int IndexOf(CollapsiblePanel panel)
  {
   return this.List.IndexOf(panel);
  }

	}

}
