using System;
using System.Windows.Forms;
using System.Collections;
using System.IO;
using System.Xml.Serialization;
using WorldWind;
using WorldWind.Renderable;
namespace WorldWind
{
   public class WWListView : ListView
   {
      int m_nSortCol = 0;
      Hashtable m_htColumnIndices = new Hashtable();
      ColumnDescriptor [] m_colDesc;
      public class ColumnDescriptor
      {
         public string m_columnName;
         public string m_attribName;
         public int m_width;
         public bool m_isNumeric;
         public ColumnDescriptor(string colName, string attribName, int width, bool isNumeric)
         {
            m_columnName = colName;
            m_attribName = attribName;
            m_width = width;
            m_isNumeric = isNumeric;
         }
      }
      class WWListViewItemComparer: IComparer
      {
         WWListView m_wwList;
         public WWListViewItemComparer(WWListView wwlv)
         {
            m_wwList = wwlv;
         }
         public int Compare(object x, object y)
         {
            int nCol = m_wwList.m_nSortCol;
            ListViewItem lvix = (ListViewItem)x;
            ListViewItem lviy = (ListViewItem)y;
            int nCompareResult;
            if(m_wwList.m_colDesc[nCol].m_isNumeric)
            {
               double xval = lvix.SubItems[nCol].Text == string.Empty ? 0 : Double.Parse(lvix.SubItems[nCol].Text);
               double yval = lviy.SubItems[nCol].Text == string.Empty ? 0 : Double.Parse(lviy.SubItems[nCol].Text);
               nCompareResult = Math.Sign(xval - yval);
            }
            else
            {
               nCompareResult = String.Compare(lvix.SubItems[nCol].Text, lviy.SubItems[nCol].Text);
            }
            return this.m_wwList.Sorting == SortOrder.Descending ? -nCompareResult : nCompareResult;
         }
      }
      protected override void OnColumnClick(ColumnClickEventArgs e)
      {
         base.OnColumnClick(e);
         if(this.ListViewItemSorter == null)
         {
            this.ListViewItemSorter = new WWListViewItemComparer(this);
         }
         if(this.m_nSortCol == e.Column)
         {
            this.Sorting = (this.Sorting == SortOrder.Descending) ? SortOrder.Ascending : SortOrder.Descending;
         }
         else
         {
            this.m_nSortCol = e.Column;
            this.Sorting = SortOrder.Ascending;
         }
         this.Sort();
      }
      public WWListView() : base()
      {
         this.View = System.Windows.Forms.View.Details;
      }
      public WWListView(ColumnDescriptor [] columnDescriptors) : this()
      {
         this.m_colDesc = columnDescriptors;
         for(int i=0; i < columnDescriptors.Length; i++)
         {
            ColumnHeader ch = new ColumnHeader();
            ch.Text = columnDescriptors[i].m_columnName;
            ch.Width = columnDescriptors[i].m_width;
            this.Columns.Add(ch);
            this.m_htColumnIndices.Add(columnDescriptors[i].m_attribName, i);
         }
      }
      public ListViewItem AddKeysAndValues(Hashtable knv)
      {
         string [] strSubItems = new string [this.m_htColumnIndices.Count];
         foreach(DictionaryEntry AttribAndValue in knv)
         {
            object oCol = this.m_htColumnIndices[AttribAndValue.Key];
            if(oCol != null)
            {
               strSubItems[(int)oCol] = (string)AttribAndValue.Value;
            }
         }
         ListViewItem lvi = new ListViewItem(strSubItems);
         this.Items.Add(lvi);
         return lvi;
      }
   }
   public class WWPlaceListView : WWListView
   {
      public delegate void AddPlaceDelegate(PlaceItem pi);
      public AddPlaceDelegate addPlaceDelegate;
      public class WWPlaceMenuItem : MenuItem
      {
         public enum RequiredElement
         {
            None,
            Items,
            SingleSelection,
            Selection,
         }
         private RequiredElement m_requiredElement = RequiredElement.None;
         public RequiredElement Requires
         {
            get
            {
               return m_requiredElement;
            }
            set
            {
               m_requiredElement = value;
            }
         }
         public WWPlaceMenuItem(string name, EventHandler handler, RequiredElement requires)
            : base(name, handler)
         {
            Requires = requires;
         }
      }
      public PlaceItem [] SelectedPlaces
      {
         get
         {
            ArrayList al = new ArrayList();
            foreach(ListViewItem lvi in this.SelectedItems)
            {
               al.Add(lvi.Tag);
            }
            return (PlaceItem [])al.ToArray(typeof(PlaceItem));
         }
      }
      protected override void OnDoubleClick(EventArgs e)
      {
         base.OnDoubleClick (e);
         ListViewItem lvi = this.FocusedItem;
         if(lvi == null) return;
         PlaceItem pi = (PlaceItem)lvi.Tag;
         if(this.WorldWindow != null) pi.Goto(this.m_worldWindow);
         if(this.RecentFinds != null) this.RecentFinds.AddPlace(pi);
      }
      private void placeListMenu_Goto(object sender, System.EventArgs e)
      {
         PlaceItem [] selplaces = this.SelectedPlaces;
         if(selplaces.Length == 0) return;
         if(this.m_worldWindow != null) selplaces[0].Goto(this.m_worldWindow);
      }
      private void placeListMenu_CopyURL(object sender, System.EventArgs e)
      {
         PlaceItem [] selplaces = this.SelectedPlaces;
         if(selplaces.Length == 0) return;
         Clipboard.SetDataObject(selplaces[0].GotoURL(this.m_worldWindow), true);
      }
      private void placeListMenu_Del(object sender, System.EventArgs e)
      {
         foreach(ListViewItem li in this.SelectedItems)
         {
            this.Items.Remove(li);
         }
      }
      private void placeListMenu_Clear(object sender, System.EventArgs e)
      {
         this.Items.Clear();
      }
      private void placeListMenu_AddToFavs(object sender, System.EventArgs e)
      {
         if(this.Favorites == null) return;
         PlaceItem [] selplaces = this.SelectedPlaces;
         foreach(PlaceItem pi in selplaces)
         {
            this.Favorites.AddPlace(pi);
         }
      }
      private void placeListMenu_GpxLoad(object sender, System.EventArgs e)
      {
         OpenFileDialog ofd = new OpenFileDialog();
         ofd.Filter = "gpx files (*.gpx)|*.gpx|All files (*.*)|*.*" ;
         ofd.FilterIndex = 1;
         ofd.RestoreDirectory = true;
         ofd.Title = "Import from GPX file";
         ofd.CheckFileExists = true;
         if(ofd.ShowDialog() == DialogResult.OK)
         {
            this.LoadFromGpx(ofd.FileName);
         }
      }
      private void placeListMenu_GpxSave(object sender, System.EventArgs e)
      {
         SaveFileDialog sfd = new SaveFileDialog();
         sfd.Filter = "gpx files (*.gpx)|*.gpx|All files (*.*)|*.*" ;
         sfd.FilterIndex = 1;
         sfd.RestoreDirectory = true;
         sfd.Title = "Export to GPX file";
         sfd.OverwritePrompt = true;
         if(sfd.ShowDialog() == DialogResult.OK)
         {
            this.SaveToGpx(sfd.FileName);
         }
      }
      public WWPlaceListView() : base (
         new WWListView.ColumnDescriptor [] {
                                               new WWListView.ColumnDescriptor("Name", "Name", 70, false),
                                               new WWListView.ColumnDescriptor("State", "State", 100, false),
                                               new WWListView.ColumnDescriptor("County", "County", 100, false),
                                               new WWListView.ColumnDescriptor("Country", "Country", 130, false),
                                               new WWListView.ColumnDescriptor("Layer", "Layer", 170, false),
                                               new WWListView.ColumnDescriptor("Type", "Feature Type", 50, false),
                                               new WWListView.ColumnDescriptor("Elevation", "Elevation", 40, true),
                                               new WWListView.ColumnDescriptor("Population", "Population", 50, true),
                                               new WWListView.ColumnDescriptor("Latitude", "Latitude", 60, true),
                                               new WWListView.ColumnDescriptor("Longitude", "Longitude", 60, true),
      }
         )
      {
         ContextMenu placeListMenu = new ContextMenu(
            new MenuItem [] {
                               new WWPlaceMenuItem("&Goto", new EventHandler(placeListMenu_Goto), WWPlaceMenuItem.RequiredElement.SingleSelection),
                               new WWPlaceMenuItem("&Copy URL", new EventHandler(placeListMenu_CopyURL), WWPlaceMenuItem.RequiredElement.SingleSelection),
                               new WWPlaceMenuItem("&Remove selected", new EventHandler(placeListMenu_Del), WWPlaceMenuItem.RequiredElement.Selection),
                               new WWPlaceMenuItem("Remove &all", new EventHandler(placeListMenu_Clear), WWPlaceMenuItem.RequiredElement.Items),
                               new MenuItem("-"),
                               new WWPlaceMenuItem("&Import from GPX", new EventHandler(placeListMenu_GpxLoad), WWPlaceMenuItem.RequiredElement.None),
                               new WWPlaceMenuItem("&Export to GPX", new EventHandler(placeListMenu_GpxSave), WWPlaceMenuItem.RequiredElement.Items),
                               new MenuItem("-"),
         });
         placeListMenu.Popup += new EventHandler(ContextMenu_Popup);
         this.ContextMenu = placeListMenu;
         this.addPlaceDelegate = new AddPlaceDelegate(AddPlace);
      }
      private void ContextMenu_Popup(object sender, EventArgs e)
      {
         ContextMenu mnu = (ContextMenu)sender;
         foreach(MenuItem mi in mnu.MenuItems)
         {
            WWPlaceMenuItem wwpmi = mi as WWPlaceMenuItem;
            if(wwpmi == null) continue;
            switch(wwpmi.Requires)
            {
               case WWPlaceMenuItem.RequiredElement.None:
                  wwpmi.Enabled = true;
                  break;
               case WWPlaceMenuItem.RequiredElement.Items:
                  wwpmi.Enabled = this.Items.Count > 0;
                  break;
               case WWPlaceMenuItem.RequiredElement.Selection:
                  wwpmi.Enabled = this.SelectedItems.Count > 0;
                  break;
               case WWPlaceMenuItem.RequiredElement.SingleSelection:
                  wwpmi.Enabled = this.SelectedItems.Count == 1;
                  break;
            }
         }
      }
      private WWPlaceListView m_lvwFavorites = null;
      public WWPlaceListView Favorites
      {
         get
         {
            return m_lvwFavorites;
         }
         set
         {
            if(m_lvwFavorites == null && value != null)
            {
               this.ContextMenu.MenuItems.Add(new WWPlaceMenuItem("&Add to Favorites", new EventHandler(placeListMenu_AddToFavs), WWPlaceMenuItem.RequiredElement.Selection));
            }
            m_lvwFavorites = value;
         }
      }
      private WWPlaceListView m_lvwRecentFinds = null;
      public WWPlaceListView RecentFinds
      {
         get
         {
            return m_lvwRecentFinds;
         }
         set
         {
            m_lvwRecentFinds = value;
         }
      }
      private WorldWindow m_worldWindow = null;
      public WorldWindow WorldWindow
      {
         get
         {
            return m_worldWindow;
         }
         set
         {
            m_worldWindow = value;
         }
      }
      public void AddPlace(PlaceItem pi)
      {
         Hashtable knv = new Hashtable();
         knv.Add("Name", pi.pn.Name);
         knv.Add("Layer", pi.placeDescriptor == null ? "" : pi.placeDescriptor.Name.Value);
         knv.Add("Latitude", pi.pn.Lat.ToString());
         knv.Add("Longitude", pi.pn.Lon.ToString());
         if(pi.pn.metaData != null)
         {
            foreach(DictionaryEntry de in pi.pn.metaData)
            {
               knv.Add(de.Key, de.Value);
            }
         }
         ListViewItem lvi = this.AddKeysAndValues(knv);
         lvi.Tag = pi;
      }
      public void LoadFromGpx(string strGpxPath)
      {
         if(!File.Exists(strGpxPath)) return;
         XmlSerializer ser = new XmlSerializer(typeof(gpxType));
         TextReader tr = new StreamReader(strGpxPath);
         gpxType gpx = null;
         try
         {
            gpx = (gpxType)ser.Deserialize(tr);
         }
         catch(Exception caught)
         {
            System.Diagnostics.Debug.WriteLine(caught.InnerException.ToString());
         }
         tr.Close();
         if(gpx == null || gpx.wpt == null) return;
         foreach(wptType wpt in gpx.wpt)
         {
            PlaceItem pi = new PlaceItem();
            pi.pn = new WorldWindPlacename();
            pi.pn.Name = wpt.name;
            pi.pn.Lat = (float)wpt.lat;
            pi.pn.Lon = (float)wpt.lon;
            AddPlace(pi);
         }
      }
      public void SaveToGpx(string strGpxPath)
      {
         gpxType gpx = new gpxType();
         gpx.creator = "NASA World Wind";
         gpx.version = "1.1";
         gpx.wpt = new wptType [this.Items.Count];
         int i = 0;
         foreach(ListViewItem lvi in this.Items)
         {
            PlaceItem pi = (PlaceItem)lvi.Tag;
            wptType wp = new wptType();
            wp.name = pi.pn.Name;
            wp.lat = (decimal)pi.pn.Lat;
            wp.lon = (decimal)pi.pn.Lon;
            wp.sym = "Waypoint";
            gpx.wpt[i++] = wp;
         }
         XmlSerializer ser = new XmlSerializer(typeof(gpxType));
         TextWriter tw = new StreamWriter(strGpxPath);
         ser.Serialize(tw, gpx);
         tw.Close();
      }
      public void FillSettingsFromList(PlaceListSettings pls)
      {
         pls.places = new PlaceListSettings.PlaceData [this.Items.Count];
         for(int i = 0; i < this.Items.Count; i++)
         {
            PlaceItem pi = (PlaceItem)this.Items[i].Tag;
            PlaceListSettings.PlaceData pd = new PlaceListSettings.PlaceData();
            pd.Name = pi.pn.Name;
            pd.Lat = pi.pn.Lat;
            pd.Lon = pi.pn.Lon;
            int mdCount = pi.pn.metaData == null ? 0 : pi.pn.metaData.Count;
            pd.metadata = new PlaceListSettings.MetaDataEntry [mdCount];
            int j = 0;
            if(pi.pn.metaData != null)
            {
               foreach(DictionaryEntry de in pi.pn.metaData)
               {
                  pd.metadata[j] = new PlaceListSettings.MetaDataEntry();
                  pd.metadata[j].name = de.Key.ToString();
                  pd.metadata[j].value = de.Value.ToString();
                  j++;
               }
            }
            pls.places[i] = pd;
         }
      }
      public void FillListFromSettings(PlaceListSettings pls)
      {
         this.Items.Clear();
         if(pls.places == null) return;
         foreach(PlaceListSettings.PlaceData pd in pls.places)
         {
            PlaceItem pi = new PlaceItem();
            pi.pn = new WorldWindPlacename();
            pi.pn.Name = pd.Name;
            pi.pn.Lat = (float)pd.Lat;
            pi.pn.Lon = (float)pd.Lon;
            pi.pn.metaData = new Hashtable();
            for(int i=0; i < pd.metadata.Length; i++)
            {
               pi.pn.metaData.Add(pd.metadata[i].name, pd.metadata[i].value);
            }
            AddPlace(pi);
         }
      }
   }
}
