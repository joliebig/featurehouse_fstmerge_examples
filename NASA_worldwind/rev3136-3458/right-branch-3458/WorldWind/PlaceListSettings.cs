using System;
using System.Xml.Serialization;
using WorldWind.Configuration;
namespace WorldWind
{
 public class PlaceListSettings : SettingsBase
 {
      public class MetaDataEntry
      {
         [XmlAttribute]
         public string name;
         [XmlAttribute]
         public string value;
      }
      public class PlaceData
      {
         [XmlAttribute]
         public string Name;
         [XmlAttribute]
         public float Lat;
         [XmlAttribute]
         public float Lon;
         public MetaDataEntry [] metadata;
      }
      public PlaceData [] places;
      public PlaceListSettings()
  {
  }
 }
   public class FavoritesSettings : PlaceListSettings
   {
      public override string ToString() { return "Favorites"; }
   }
   public class HistorySettings : PlaceListSettings
   {
      public override string ToString() { return "History"; }
   }
}
