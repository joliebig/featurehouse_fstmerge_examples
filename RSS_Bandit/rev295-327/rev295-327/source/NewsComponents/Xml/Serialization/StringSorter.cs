using System.Collections; namespace  NewsComponents.Xml.Serialization {
	
 internal class  StringSorter {
		
  ArrayList list = new ArrayList();
 
  public  StringSorter()
  {
  }
 
  public  void AddString( string s )
  {
   list.Add(s);
  }
 
  public  string[] GetOrderedArray()
  {
   list.Sort();
   return list.ToArray(typeof(string)) as string[];
  }

	}

}
