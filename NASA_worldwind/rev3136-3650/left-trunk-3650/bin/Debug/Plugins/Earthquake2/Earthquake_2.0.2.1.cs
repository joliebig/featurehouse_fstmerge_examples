using System;
using System.Globalization;
using System.IO;
using WorldWind;
using WorldWind.Renderable;
using WorldWind.Net;
using System.Windows.Forms;
using System.Xml;
using System.Xml.XPath;
namespace XMLEarthquake.Plugin
{
 public class Earthquake : WorldWind.PluginEngine.Plugin
 {
  System.Windows.Forms.MenuItem menuItem;
  static string EarthquakeXmlUri = "http://earthquake.usgs.gov/eqcenter/recenteqsww/catalogs/eqs7day-M2.5.xml";
  Icons EQIcons = new Icons("EarthQuake Icons");
  public override void Load()
  {
   if (ParentApplication.WorldWindow.CurrentWorld != null && ParentApplication.WorldWindow.CurrentWorld.Name.IndexOf("Earth") >= 0)
            {
                menuItem = new System.Windows.Forms.MenuItem();
                menuItem.Text = "Refresh Earthquake 7-Day List";
                menuItem.Click += new System.EventHandler(menuItem_Click);
                ParentApplication.PluginsMenu.MenuItems.Add(menuItem);
    EQIcons.IsOn = false;
                m_Application.WorldWindow.CurrentWorld.RenderableObjects.Add(EQIcons);
                base.Load();
            }
  }
  public override void Unload()
  {
   ParentApplication.PluginsMenu.MenuItems.Remove(menuItem);
   m_Application.WorldWindow.CurrentWorld.RenderableObjects.Remove(EQIcons);
   base.Unload ();
  }
  void menuItem_Click(object sender, EventArgs e)
  {
   EQIcons.IsOn = true;
   CultureInfo icy = CultureInfo.InvariantCulture;
   EarthquakeEntry[] earthquakeList = GetEarthquakeList(EarthquakeXmlUri);
   foreach(EarthquakeEntry entry in earthquakeList)
   {
    float magnitude = float.Parse(entry.subjects[0], icy);
    string quakedepth = entry.subjects[2];
    DateTime QuakeDate = System.DateTime.Parse(entry.description, icy);
    DateTime CurrentDate = DateTime.Now.AddDays(-1);
    DateTime PastDay = DateTime.Now.AddDays(-1);
    DateTime PastWeek = DateTime.Now.AddDays(-3);
    string bitmapSize = "small";
    string bitmapRange = "CD";
    if(QuakeDate >= CurrentDate)
     bitmapRange = "CD";
    else if((QuakeDate == PastDay) || (QuakeDate >= PastWeek))
     bitmapRange = "PD";
    else
     bitmapRange = "PW";
    if(magnitude <= 3)
     bitmapSize = "small";
    else if((magnitude == 4) || (magnitude <= 5))
     bitmapSize = "medium";
    else if((magnitude == 6) || (magnitude <= 7))
     bitmapSize = "big";
    else
     bitmapSize = "large";
    string bitmapFileName = String.Format("{0}_{1}.png", bitmapRange, bitmapSize);
    string bitmapPath = Path.Combine(PluginDirectory, bitmapFileName);
    CreateIcon(entry.title +"\nDepth: "+ quakedepth +"\n"+ entry.description, "Click the icon to visit related page", (float)entry.latitude, (float)entry.longitude, 0, entry.link, bitmapPath);
   }
  }
  public static EarthquakeEntry[] GetEarthquakeList(string uri)
  {
   XPathDocument docNav = new XPathDocument(uri);
   XPathNavigator nav = docNav.CreateNavigator();
   XPathNodeIterator itemIter = nav.Select("/rss/channel/item");
   XmlNamespaceManager context = new XmlNamespaceManager(nav.NameTable);
   context.AddNamespace("geo", "http://www.w3.org/2003/01/geo/wgs84_pos#");
   context.AddNamespace("dc", "http://purl.org/dc/elements/1.1/");
   context.AddNamespace("feedburner", "http://rssnamespace.org/feedburner/ext/1.0");
   System.Collections.ArrayList itemList = new System.Collections.ArrayList();
   CultureInfo icy = CultureInfo.InvariantCulture;
   while(itemIter.MoveNext())
   {
    try
    {
     EarthquakeEntry curEntry = new EarthquakeEntry();
     System.Collections.ArrayList subjectList = new System.Collections.ArrayList();
     XPathNodeIterator iter = itemIter.Current.Select("*");
     while(iter.MoveNext())
     {
      switch(iter.Current.Name)
      {
       case "title":
        curEntry.title = iter.Current.Value;
        break;
       case "description":
        curEntry.description = iter.Current.Value;
        break;
       case "link":
        curEntry.link = iter.Current.Value;
        break;
       case "geo:lat":
        curEntry.latitude = double.Parse(iter.Current.Value, icy);
        break;
       case "geo:long":
        curEntry.longitude = double.Parse(iter.Current.Value, icy);
        break;
       case "dc:subject":
        subjectList.Add(iter.Current.Value);
        break;
       case "feedburner:origLink":
        curEntry.origLink = iter.Current.Value;
        break;
      }
     }
     curEntry.subjects = (string[])subjectList.ToArray(typeof(string));
     itemList.Add(curEntry);
    }
    catch(Exception)
    {
    }
   }
   return (EarthquakeEntry[])itemList.ToArray(typeof(EarthquakeEntry));
  }
  static string getInnerTextFromFirstChild(XPathNodeIterator iter)
  {
   if(iter.Count == 0)
   {
    return null;
   }
   else
   {
    iter.MoveNext();
    return iter.Current.Value;
   }
  }
  private void CreateIcon(string Name, string Desc, float Lat, float Lon, int Alt, string URL, string bitmapPath)
  {
   int bitmapSize = 64;
   Icon ic = new Icon(
    Name,
    Lat,
    Lon,
    (float)Alt);
   ic.Description = Desc;
   ic.ClickableActionURL = URL;
   ic.TextureFileName = bitmapPath;
   ic.Height = ic.Width = bitmapSize;
   ic.RenderPriority = RenderPriority.Icons;
   EQIcons.AddIcon(ic);
  }
  public struct EarthquakeEntry
  {
   public string title;
   public string description;
   public string link;
   public double latitude;
   public double longitude;
   public string[] subjects;
   public string origLink;
  }
 }
}
