using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;
using FlickrNet;
namespace YahooFlickr.Plugins
{
    public class FlickrPlugin: WorldWind.PluginEngine.Plugin
    {
        private static string apikey = @"ce45f112b3cd6e5e08e6f1680a5bd73a";
        private KMLPlugin.RIcons layer;
        private System.Drawing.Bitmap Image;
        public override void Load()
        {
            Flickr flickr = new Flickr(apikey);
            PhotoSearchOptions searchOptions = new PhotoSearchOptions();
            searchOptions.Tags = "trees";
            searchOptions.BoundaryBox = BoundaryBox.UKNewcastle;
            searchOptions.Extras |= PhotoSearchExtras.Geo;
            Photos treePhotos = flickr.PhotosSearch(searchOptions);
            PhotoCollection allPhotos = treePhotos.PhotoCollection;
            Image = new System.Drawing.Bitmap("Plugins\\Flickr\\flickr.ico");
            layer = new KMLPlugin.RIcons("Flickr Icons");
            foreach (Photo photo in allPhotos)
            {
                KMLPlugin.RIcon ic = new KMLPlugin.RIcon(photo.Title, Convert.ToSingle(photo.Latitude)
                    , Convert.ToSingle(photo.Longitude), null , 0);
                ic.Image = Image;
                ic.Width = 16;
                ic.Height = 16;
                ic.m_drawGroundStick = false;
                ic.isSelectable = true;
                layer.Add(ic);
                ic.Description = "<a href=\""+ photo.LargeUrl+
                    "\"><img src=\"" + photo.ThumbnailUrl + "\"/></a>";
            }
            if(ParentApplication.WorldWindow.CurrentWorld.IsEarth)
                ParentApplication.WorldWindow.CurrentWorld.RenderableObjects.Add(layer);
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
        private Flickr flickr;
        private PhotoSearchOptions searchOptions;
        private System.Drawing.Bitmap Image;
        public FlickrIconsLayer(Flickr flickr):base("Flickr Photos")
        {
            this.flickr = flickr;
        }
        public override void Initialize(WorldWind.DrawArgs drawArgs)
        {
            searchOptions = new PhotoSearchOptions();
            searchOptions.Tags = "trees";
            searchOptions.BoundaryBox = BoundaryBox.UKNewcastle;
            searchOptions.Extras |= PhotoSearchExtras.Geo;
            Photos treePhotos = flickr.PhotosSearch(searchOptions);
            PhotoCollection allPhotos = treePhotos.PhotoCollection;
            Image = new System.Drawing.Bitmap("Plugins\\Flickr\\flickr.ico");
            Photo photo = allPhotos[1];
                WorldWind.Renderable.Icon ic = new WorldWind.Renderable.Icon(photo.Title, Convert.ToDouble(photo.Latitude)
                    , Convert.ToDouble(photo.Longitude));
                ic.Image = Image;
                this.Add(ic);
            base.Initialize(drawArgs);
        }
        public override void Dispose()
        {
            base.Dispose();
            if (Image != null)
                Image.Dispose();
        }
    }
}
