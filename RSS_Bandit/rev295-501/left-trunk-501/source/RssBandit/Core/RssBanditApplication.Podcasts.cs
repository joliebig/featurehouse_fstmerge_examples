using System;
using System.IO;
using iTunesLib;
using NewsComponents.Net;
using WMPLib;
namespace RssBandit
{
    internal partial class RssBanditApplication
    {
        public string EnclosureFolder
        {
            get
            {
    return Preferences.EnclosureFolder;
            }
        }
        public string PodcastFolder
        {
            get
            {
                return feedHandler.PodcastFolder;
            }
        }
        public int NumEnclosuresToDownloadOnNewFeed
        {
            get
            {
                return Preferences.NumEnclosuresToDownloadOnNewFeed;
            }
        }
        public int EnclosureCacheSize
        {
            get
            {
                return Preferences.EnclosureCacheSize;
            }
        }
        public string PodcastFileExtensions
        {
            get
            {
                return feedHandler.PodcastFileExtensionsAsString;
            }
        }
        public bool DownloadCreateFolderPerFeed
        {
            get
            {
                return Preferences.CreateSubfoldersForEnclosures;
            }
        }
        public bool EnableEnclosureAlerts
        {
            get
            {
                return Preferences.EnclosureAlert;
            }
        }
        public bool DownloadEnclosures
        {
            get
            {
                return Preferences.DownloadEnclosures;
            }
        }
        private static bool IsWMPFile(IEquatable<string> fileExt)
        {
            if (fileExt.Equals(".asf") || fileExt.Equals(".wma") || fileExt.Equals(".avi")
                || fileExt.Equals(".mpg") || fileExt.Equals(".mpeg") || fileExt.Equals(".m1v")
                || fileExt.Equals(".wmv") || fileExt.Equals(".wm") || fileExt.Equals(".asx")
                || fileExt.Equals(".wax") || fileExt.Equals(".wpl") || fileExt.Equals(".wvx")
                || fileExt.Equals(".wmd") || fileExt.Equals(".dvr-ms") || fileExt.Equals(".m3u")
                || fileExt.Equals(".mp3") || fileExt.Equals(".mp2") || fileExt.Equals(".mpa")
                || fileExt.Equals(".mpe") || fileExt.Equals(".mpv2") || fileExt.Equals(".wms")
                || fileExt.Equals(".mid") || fileExt.Equals(".midi") || fileExt.Equals(".rmi")
                || fileExt.Equals(".aif") || fileExt.Equals(".aifc") || fileExt.Equals(".aiff")
                || fileExt.Equals(".wav") || fileExt.Equals(".au") || fileExt.Equals(".snd")
                || fileExt.Equals(".ivf") || fileExt.Equals(".wmz"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private void AddPodcastToWMP(DownloadItem podcast)
        {
            try
            {
                if (!IsWMPFile(Path.GetExtension(podcast.File.LocalName)))
                {
                    return;
                }
                string playlistName = Preferences.SinglePlaylistName;
                if (!Preferences.SinglePodcastPlaylist && feedHandler.IsSubscribed(podcast.OwnerFeedId))
                {
                    playlistName = feedHandler.GetFeeds()[podcast.OwnerFeedId].title;
                }
                WindowsMediaPlayer wmp = new WindowsMediaPlayer();
                IWMPPlaylist podcastPlaylist = null;
                IWMPPlaylistArray playlists = wmp.playlistCollection.getAll();
                for (int i = 0; i < playlists.count; i++)
                {
                    IWMPPlaylist pl = playlists.Item(i);
                    if (pl.name.Equals(playlistName))
                    {
                        podcastPlaylist = pl;
                    }
                }
                if (podcastPlaylist == null)
                {
                    podcastPlaylist = wmp.playlistCollection.newPlaylist(playlistName);
                }
                IWMPMedia wm = wmp.newMedia(Path.Combine(podcast.TargetFolder, podcast.File.LocalName));
                podcastPlaylist.appendItem(wm);
            }
            catch (Exception e)
            {
                _log.Error("The following error occured in AddPodcastToWMP(): ", e);
            }
        }
        private static bool IsITunesFile(IEquatable<string> fileExt)
        {
            if (fileExt.Equals(".mov") || fileExt.Equals(".mp4") || fileExt.Equals(".mp3")
                || fileExt.Equals(".m4v") || fileExt.Equals(".m4a") || fileExt.Equals(".m4b")
                || fileExt.Equals(".m4p") || fileExt.Equals(".wav") || fileExt.Equals(".aiff")
                || fileExt.Equals(".aif") || fileExt.Equals(".aifc") || fileExt.Equals(".aa"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        private void AddPodcastToITunes(DownloadItem podcast)
        {
            try
            {
                if (!IsITunesFile(Path.GetExtension(podcast.File.LocalName)))
                {
                    return;
                }
                string playlistName = Preferences.SinglePlaylistName;
                if (!Preferences.SinglePodcastPlaylist && feedHandler.IsSubscribed(podcast.OwnerFeedId))
                {
                    playlistName = feedHandler.GetFeeds()[podcast.OwnerFeedId].title;
                }
                iTunesApp itunes = new iTunesApp();
                IITUserPlaylist podcastPlaylist = null;
                foreach (IITPlaylist pl in itunes.LibrarySource.Playlists)
                {
                    if (pl.Name.Equals(playlistName))
                    {
                        podcastPlaylist = (IITUserPlaylist) pl;
                    }
                }
                if (podcastPlaylist == null)
                {
                    podcastPlaylist = (IITUserPlaylist) itunes.CreatePlaylist(playlistName);
                }
                podcastPlaylist.AddFile(Path.Combine(podcast.TargetFolder, podcast.File.LocalName));
            }
            catch (Exception e)
            {
                _log.Error("The following error occured in AddPodcastToITunes(): ", e);
            }
        }
    }
}
