using System;
using System.IO;
using System.Collections;
using System.Globalization;
using WorldWind;
using WorldWind.Net;
using WorldWind.Renderable;
namespace WorldWind
{
   public struct WplIndexEntry
   {
      public System.Int16 fileNumber;
      public System.Int32 seekOffset;
   }
   public class IndexedTiledPlaceNameSet
   {
      public LayerSet.Type_TiledPlacenameSet2 placenameSet;
      public WplIndex wplIndex;
      public IndexedTiledPlaceNameSet(LayerSet.Type_TiledPlacenameSet2 pns, WplIndex idx)
      {
         placenameSet = pns;
         wplIndex = idx;
      }
   }
   public class PlaceItem
   {
      public WorldWindPlacename pn;
      public LayerSet.Type_TiledPlacenameSet2 placeDescriptor;
      public double Altitude
      {
         get
         {
            double altitude = 22500;
            if(placeDescriptor != null)
            {
               altitude = placeDescriptor.MaximumDisplayAltitude.DoubleValue() * 0.9;
            }
            return altitude;
         }
      }
      public void Goto(WorldWindow ww)
      {
         ww.GotoLatLonAltitude(pn.Lat, pn.Lon, this.Altitude);
      }
      public String GotoURL(WorldWindow ww)
      {
         WorldWindUri uri = new WorldWindUri();
         uri.Latitude = Angle.FromDegrees(this.pn.Lat);
         uri.Longitude = Angle.FromDegrees(this.pn.Lon);
         return uri.ToString();
      }
   }
   public class IndexedPlace : PlaceItem
   {
      public WplIndexEntry indexEntry;
   }
   public class PlaceItemComparer : IComparer
   {
      public int Compare(object x, object y)
      {
         return String.Compare(((PlaceItem)x).pn.Name, ((PlaceItem)y).pn.Name, true);
      }
   }
   public class WplIndex
   {
      public class IndexEntryToStringComparer: IComparer
      {
         WplIndex myWplIndex = null;
         bool partialAllowed = false;
         public IndexEntryToStringComparer(WplIndex theIndex, bool bPartialAllowed)
         {
            myWplIndex = theIndex;
            partialAllowed = bPartialAllowed;
         }
         public int Compare(object x, object y)
         {
            WplIndexEntry ie;
            if(x.GetType() == typeof(int))
            {
               ie = myWplIndex.m_indexEntries[(int)x];
            }
            else
            {
               ie = (WplIndexEntry)x;
            }
            PlaceItem pi = myWplIndex.GetPlaceItemFromIndexEntry(ie, MetaDataAction.Omit);
            if(!partialAllowed)
            {
               return String.Compare(pi.pn.Name, (string)y, true);
            }
            string strSearch = (string)y;
            return String.Compare(pi.pn.Name, 0, strSearch, 0, strSearch.Length, true);
         }
      }
      LayerSet.Type_TiledPlacenameSet2 m_placeNameSet;
      string m_strWplPath;
      string m_strBasedir;
      string [] m_strWwpNames;
      int m_nTotalPlaceNames = -1;
      IndexedPlace [] m_indexedPlaces = null;
      WplIndexEntry [] m_indexEntries = null;
      WeakReference weakReferenceIndexEntries = null;
      static double ProgressPercent(int nTaskNbr, int nTaskTotal, double dblFrom, double dblTo, double dblCur)
      {
         double dblPercentStart = ((nTaskNbr-1)/(double)nTaskTotal)*100.0;
         double dblPercentEnd = ((nTaskNbr)/(double)nTaskTotal)*100.0;
         double dblCompletionRatio = (dblCur-dblFrom)/dblTo;
         return dblPercentStart+(dblPercentEnd-dblPercentStart)*dblCompletionRatio;
      }
      static BinaryReader OpenBinReader(string strFilePath)
      {
         FileStream fsWwp = File.Open(strFilePath, FileMode.Open, FileAccess.Read, FileShare.Read);
         return new BinaryReader(fsWwp, System.Text.Encoding.Default);
      }
      BinaryReader OpenWwpReader(int n)
      {
         return OpenBinReader(Path.Combine(m_strBasedir, this.m_strWwpNames[n]));
      }
      void ReadWwpNamesFromWpl(string strWplFile)
      {
   using( BinaryReader brWpl = OpenBinReader(strWplFile) )
   {
    int count = brWpl.ReadInt32();
    m_strWwpNames = new string [count];
    for(int i = 0; i < count; i++)
    {
     m_strWwpNames[i] = brWpl.ReadString();
     brWpl.ReadSingle();
     brWpl.ReadSingle();
     brWpl.ReadSingle();
     brWpl.ReadSingle();
    }
   }
      }
      int ComputePlaceNameCount()
      {
         int nTotalPlaceNames = 0;
         for(int i=0; i < m_strWwpNames.Length; i++)
         {
            using( BinaryReader brWwp = OpenWwpReader(i) )
             nTotalPlaceNames += brWwp.ReadInt32();
         }
         return nTotalPlaceNames;
      }
      void AddSingleWwpPlaces(int nFileNbr, ref int nNextEntry)
      {
   using( BinaryReader brWwp = OpenWwpReader(nFileNbr) )
   {
    int nEntryCount = brWwp.ReadInt32();
    for(int i=0; i < nEntryCount; i++)
    {
     IndexedPlace ip = new IndexedPlace();
     ip.pn = new WorldWindPlacename();
     ip.placeDescriptor = this.m_placeNameSet;
     ip.indexEntry.fileNumber = (System.Int16)nFileNbr;
     ip.indexEntry.seekOffset = (int)brWwp.BaseStream.Position;
     PlaceItem pi = ip;
     ReadPlaceName(brWwp, ref pi.pn, MetaDataAction.Skip);
     this.m_indexedPlaces[nNextEntry++] = ip;
    }
   }
      }
      void LoadPlaceInfos(ProgressReportDelegate pr, int nTask, int nTotal)
      {
         int nSize = this.PlaceCount;
         m_indexedPlaces = new IndexedPlace [nSize];
         int nEntryCount = 0;
         int nWwpCount = this.m_strWwpNames.Length;
         for(int i=0; i < nWwpCount; i++)
         {
            AddSingleWwpPlaces(i, ref nEntryCount);
            if(pr != null) pr(ProgressPercent(nTask, nTotal, 0, nWwpCount, i), "Loading place infos");
         }
      }
      void SortPlaceInfos(ProgressReportDelegate pr, int nTask, int nTotal)
      {
         if(pr != null) pr(ProgressPercent(nTask, nTotal, 0, 1, 0), "Sorting");
         Array.Sort(m_indexedPlaces, new PlaceItemComparer());
         if(pr != null) pr(ProgressPercent(nTask, nTotal, 0, 1, 1), "Sorting");
      }
      string IndexFileName()
      {
         return Path.ChangeExtension(this.m_strWplPath, "idx");
      }
      void CreateIndexFromPlaceInfos(ProgressReportDelegate pr, int nTask, int nTotal)
      {
         using( FileStream fsIdx = File.Open(IndexFileName(), FileMode.Create, FileAccess.Write, FileShare.None) )
   using( BinaryWriter bwIdx = new BinaryWriter(fsIdx, System.Text.Encoding.Default) )
   {
    bwIdx.Write(this.m_indexedPlaces.Length);
    int nPlaceCount = m_indexedPlaces.Length;
    for(int i = 0; i < nPlaceCount; i++)
    {
     IndexedPlace ip = m_indexedPlaces[i];
     bwIdx.Write(ip.indexEntry.fileNumber);
     bwIdx.Write(ip.indexEntry.seekOffset);
     if(i % 100 == 0)
     {
      if(pr != null) pr(ProgressPercent(nTask, nTotal, 0, nPlaceCount, i), "Writing index");
     }
    }
   }
      }
      void DiscardPlaceInfos()
      {
         this.m_indexedPlaces = null;
      }
      PlaceItem GetPlaceItemFromIndexEntry(WplIndexEntry ie, MetaDataAction metaDataAction)
      {
   using( BinaryReader brWwp = OpenWwpReader(ie.fileNumber) )
   {
    brWwp.BaseStream.Seek(ie.seekOffset, SeekOrigin.Begin);
    PlaceItem pi = new PlaceItem();
    pi.pn = new WorldWindPlacename();
    pi.placeDescriptor = this.m_placeNameSet;
    ReadPlaceName(brWwp, ref pi.pn, metaDataAction);
    return pi;
   }
      }
      public delegate void ProgressReportDelegate(double percentComplete, string currentAction);
   public WplIndex(LayerSet.Type_TiledPlacenameSet2 tps, string strWplFilePath)
      {
         this.m_placeNameSet = tps;
         this.m_strWplPath = strWplFilePath;
         m_strBasedir = Path.GetDirectoryName(strWplFilePath);
         ReadWwpNamesFromWpl(strWplFilePath);
      }
      public enum MetaDataAction { Store, Skip, Omit };
      static public void ReadPlaceName(BinaryReader br, ref WorldWindPlacename pn, MetaDataAction metaDataAction)
      {
         pn.Name = br.ReadString();
         pn.Lat = br.ReadSingle();
         pn.Lon = br.ReadSingle();
         int metaCount = br.ReadInt32();
         if(metaDataAction == MetaDataAction.Store)
         {
            pn.metaData = new Hashtable();
         }
         else
         {
            pn.metaData = null;
         }
         if(metaDataAction == MetaDataAction.Omit)
         {
            return;
         }
         for(int j = 0; j < metaCount; j++)
         {
            string strKey = br.ReadString();
            string strValue = br.ReadString();
            if(metaDataAction == MetaDataAction.Store) pn.metaData.Add(strKey, strValue);
         }
      }
      public int PlaceCount
      {
         get
         {
            if(this.m_nTotalPlaceNames == -1)
            {
               this.m_nTotalPlaceNames = ComputePlaceNameCount();
            }
            return this.m_nTotalPlaceNames;
         }
      }
      public bool IsValidIndex(int n) {
         return n >= 0 && n < this.PlaceCount;
      }
      public bool IsAvailable
      {
         get
         {
            return File.Exists(IndexFileName());
         }
      }
      public bool IsLoaded
      {
         get
         {
            return this.m_indexEntries != null;
         }
      }
      public void CreateIndex(ProgressReportDelegate pr)
      {
         LoadPlaceInfos(pr, 1, 3);
         SortPlaceInfos(pr, 2, 3);
         CreateIndexFromPlaceInfos(pr, 3, 3);
         DiscardPlaceInfos();
      }
      public void Load(ProgressReportDelegate pr)
      {
         using(BinaryReader brIdx = OpenBinReader(IndexFileName()))
         {
            int nCount = brIdx.ReadInt32();
            this.m_indexEntries = new WplIndexEntry [nCount];
            for(int i = 0; i < nCount; i++)
            {
               WplIndexEntry ie = new WplIndexEntry();
               ie.fileNumber = brIdx.ReadInt16();
               ie.seekOffset = brIdx.ReadInt32();
               m_indexEntries[i] = ie;
            }
         }
      }
      public void Lock(ProgressReportDelegate pr)
      {
         if(this.IsLoaded) return;
         if(this.weakReferenceIndexEntries == null)
         {
            this.weakReferenceIndexEntries = new WeakReference(null);
         }
         if(this.weakReferenceIndexEntries.IsAlive)
         {
            m_indexEntries = (WplIndexEntry [])this.weakReferenceIndexEntries.Target;
         }
         else
         {
            Load(pr);
            this.weakReferenceIndexEntries = new WeakReference(m_indexEntries);
         }
      }
      public void Release()
      {
         if(!this.IsLoaded) return;
         this.m_indexEntries = null;
      }
      public PlaceItem GetPlaceItem(int nIndex)
      {
         return GetPlaceItemFromIndexEntry(this.m_indexEntries[nIndex], MetaDataAction.Store);
      }
      public int FindPlaceByName(string strPlaceName, bool bPartial)
      {
         return Array.BinarySearch(this.m_indexEntries, strPlaceName,
            new WplIndex.IndexEntryToStringComparer(this, bPartial));
      }
   }
}
