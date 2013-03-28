using System;
using System.Collections.Generic;
using System.Text;
using System.Xml.Serialization;
using System.IO;
using System.Windows.Forms;
using FlickrNet;
using WorldWind;
using Microsoft.DirectX;
namespace YahooFlickr.Plugins
{
    class FlickrGUI : Form
    {
        private Button searchButton;
        private DateTimePicker dateTimePicker1;
        private TextBox keyBox;
        public KeySetter PassKeywords;
        public FlickrGUI()
        {
            InitializeComponent();
        }
        private void InitializeComponent()
        {
            this.keyBox = new System.Windows.Forms.TextBox();
            this.searchButton = new System.Windows.Forms.Button();
            this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
            this.SuspendLayout();
            this.keyBox.Location = new System.Drawing.Point(12, 10);
            this.keyBox.Name = "keyBox";
            this.keyBox.Size = new System.Drawing.Size(225, 20);
            this.keyBox.TabIndex = 0;
            this.keyBox.Text = "(Keywords)";
            this.searchButton.Location = new System.Drawing.Point(260, 8);
            this.searchButton.Name = "searchButton";
            this.searchButton.Size = new System.Drawing.Size(70, 28);
            this.searchButton.TabIndex = 1;
            this.searchButton.Text = "Search";
            this.searchButton.UseVisualStyleBackColor = true;
            this.searchButton.Click += new System.EventHandler(this.searchButton_Click);
            this.dateTimePicker1.Location = new System.Drawing.Point(12, 51);
            this.dateTimePicker1.Name = "dateTimePicker1";
            this.dateTimePicker1.Size = new System.Drawing.Size(224, 20);
            this.dateTimePicker1.TabIndex = 2;
            this.ClientSize = new System.Drawing.Size(340, 93);
            this.Controls.Add(this.dateTimePicker1);
            this.Controls.Add(this.searchButton);
            this.Controls.Add(this.keyBox);
            this.Name = "FlickrGUI";
            this.Text = "Flickr Preferences";
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private void searchButton_Click(object sender, EventArgs e)
        {
            if (!(keyBox.Text.Equals("(KeyWord)") || keyBox.Text.Trim().Equals("")))
            {
                this.PassKeywords(keyBox.Text);
            }
        }
    }
    public delegate void KeySetter(string text);
    public class FlickrPlugin: WorldWind.PluginEngine.Plugin
    {
        private FlickrIconsLayer layer;
        private System.Drawing.Bitmap Image;
        public override void Load()
        {
            if (ParentApplication.WorldWindow.CurrentWorld.IsEarth)
            {
                layer = new FlickrIconsLayer();
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
            }
            base.Load();
        }
        public override void Unload()
        {
            if (ParentApplication.WorldWindow.CurrentWorld.IsEarth && layer != null)
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Remove(layer);
            if (layer != null)
            {
                layer.Dispose();
                layer = null;
            }
            if(Image != null)
            {
                Image.Dispose();
                Image = null;
            }
            base.Unload();
        }
    }
    class FlickrIconsLayer : WorldWind.Renderable.Icons
    {
        private static string apikey = @"ce45f112b3cd6e5e08e6f1680a5bd73a";
        private Flickr flickr;
        private PhotoSearchOptions searchOptions;
        private System.Drawing.Bitmap Image;
        private WorldWind.Angle lastUpdatelon,lastUpdatelat;
        private double lastUpdatealt;
        private string m_cachedir;
        private bool m_needsupdate;
        private double m_maxdistance;
        public void KeyWords(string value)
        {
            if (searchOptions != null)
            {
                searchOptions.Tags = value;
                m_needsupdate = false;
                this.IsOn = false;
                try
                {
                    lock(this)
                        if(Directory.Exists(m_cachedir)) Directory.Delete(m_cachedir,true);
                }
                finally
                {
                    m_needsupdate = true;
                }
                this.IsOn = true;
            }
        }
        public FlickrIconsLayer():base("Flickr Photos")
        {
            this.flickr = new Flickr(apikey);
            searchOptions = new PhotoSearchOptions();
            searchOptions.Extras |= PhotoSearchExtras.Geo;
            searchOptions.PerPage = 10;
            searchOptions.Page = 1;
            searchOptions.Tags = "";
            m_cachedir =
                 Directory.GetParent(Application.ExecutablePath) +
                 "\\Cache\\Earth\\Flickr\\";
            m_maxdistance = 100000.0;
        }
        public override void Initialize(WorldWind.DrawArgs drawArgs)
        {
            base.Initialize(drawArgs);
            lastUpdatelat = lastUpdatelon = WorldWind.Angle.Zero;
            lastUpdatealt = 0.0;
            m_needsupdate = true;
            Image = new System.Drawing.Bitmap("Plugins\\Flickr\\flickr.ico");
        }
        public override void Dispose()
        {
            base.Dispose();
            if (Image != null)
                Image.Dispose();
        }
        public override void Update(WorldWind.DrawArgs drawArgs)
        {
            base.Update(drawArgs);
            double distance = WorldWind.World.ApproxAngularDistance(drawArgs.WorldCamera.Latitude,
            drawArgs.WorldCamera.Longitude, this.lastUpdatelat, this.lastUpdatelon).Degrees;
            double altchange = Math.Abs(this.lastUpdatealt - drawArgs.WorldCamera.Altitude);
            if (distance > 10 || altchange < 10000.0 || m_needsupdate)
            {
                this.lastUpdatelon = drawArgs.WorldCamera.Longitude;
                this.lastUpdatelat = drawArgs.WorldCamera.Latitude;
                this.lastUpdatealt = drawArgs.WorldCamera.Altitude;
                this.m_needsupdate = false;
                double viewNorth = drawArgs.WorldCamera.Latitude.Degrees + drawArgs.WorldCamera.TrueViewRange.Degrees * 0.5;
                double viewSouth = drawArgs.WorldCamera.Latitude.Degrees - drawArgs.WorldCamera.TrueViewRange.Degrees * 0.5;
                double viewWest = drawArgs.WorldCamera.Longitude.Degrees - drawArgs.WorldCamera.TrueViewRange.Degrees * 0.5;
                double viewEast = drawArgs.WorldCamera.Longitude.Degrees + drawArgs.WorldCamera.TrueViewRange.Degrees * 0.5;
                lock (this)
                {
                    PhotoCollection allPhotos = GetPhotoCollection(viewWest, viewSouth,
                        viewEast, viewNorth);
                    this.RemoveAll();
                    foreach (Photo photo in allPhotos)
                    {
                        double photolat = Convert.ToDouble(photo.Latitude);
                        double photolon = Convert.ToDouble(photo.Longitude);
                        WorldWind.Renderable.Icon ic = new WorldWind.Renderable.Icon(photo.Title,photolat
                            ,photolon);
                        ic.Image = Image;
                        ic.Width = 16;
                        ic.Height = 16;
                        WorldWind.Renderable.ScreenOverlay overlay
                        = new WorldWind.Renderable.ScreenOverlay(ic.Name,0.0f,0.0f,photo.ThumbnailUrl);
                        ic.AddOverlay(overlay);
                        double distanceToIcon = Vector3.Length(ic.Position - drawArgs.WorldCamera.Position);
                        this.Add(ic);
                    }
                }
            }
        }
        private PhotoCollection GetPhotoCollection(double west,double south
            ,double east,double north)
        {
            PhotoCollection collection = new PhotoCollection();
            double tileSize = 10.0;
            double currentSouth = -90;
            XmlSerializer serializer = new XmlSerializer(typeof(PhotoCollection));
            while (currentSouth < 90)
            {
                double currentNorth = currentSouth + tileSize;
                if (currentSouth > north || currentNorth < south)
                {
                    currentSouth += tileSize;
                    continue;
                }
                double currentWest = -180;
                while (currentWest < 180)
                {
                    double currentEast = currentWest + tileSize;
                    if (currentWest > east || currentEast < west)
                    {
                        currentWest += tileSize;
                        continue;
                    }
                    if (!Directory.Exists(m_cachedir))
                        Directory.CreateDirectory(m_cachedir);
                    string collectionFilename = m_cachedir
                        + currentEast + "_"
                        + currentWest + "_"
                        + currentNorth + "_"
                        + currentSouth +".xml" ;
                    PhotoCollection currentPhotoCollection;
                    if (File.Exists(collectionFilename))
                        currentPhotoCollection = (PhotoCollection)serializer.Deserialize(
                            new FileStream(collectionFilename,FileMode.Open));
                    else
                    {
                        searchOptions.BoundaryBox = new BoundaryBox(currentWest, currentSouth,
                            currentEast, currentNorth);
                        Photos photos = flickr.PhotosSearch(searchOptions);
                        currentPhotoCollection = photos.PhotoCollection;
                        serializer.Serialize(new FileStream(
                            collectionFilename, FileMode.Create),currentPhotoCollection);
                    }
                    collection.AddRange(currentPhotoCollection);
                    currentWest += tileSize;
                }
                currentSouth += tileSize;
            }
            return collection;
        }
        public override void BuildContextMenu(ContextMenu menu)
        {
            menu.MenuItems.Add("Properties", OnPropertiesClick);
        }
        protected override void OnPropertiesClick(object sender, EventArgs e)
        {
            if (m_propertyBrowser != null)
                m_propertyBrowser.Dispose();
            m_propertyBrowser = new FlickrGUI();
            ((FlickrGUI)m_propertyBrowser).PassKeywords = this.KeyWords;
            m_propertyBrowser.Show();
        }
    }
}
