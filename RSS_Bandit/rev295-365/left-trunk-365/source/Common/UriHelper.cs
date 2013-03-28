using System;
using System.Collections.Generic;
using System.Diagnostics;
namespace RssBandit.Common
{
    internal class UriComparer: IComparer<string>{
        public int Compare(string x, string y){
            if( x == null || y == null) return -1;
            Uri a = null, b = null;
            Uri.TryCreate(x,UriKind.Absolute, out a);
            Uri.TryCreate(y, UriKind.Absolute, out b);
            if( a != null && b != null)
                return a.CanonicalizedUri().CompareTo(b.CanonicalizedUri());
            else
                return x.CompareTo(y);
        }
    }
    public static class UriHelper
    {
        public static IComparer<string> Comparer = new UriComparer();
        public static string CanonicalizedUri(this Uri uri)
        {
            return CanonicalizedUri(uri, false);
        }
        public static string CanonicalizedUri(this Uri uri, bool replaceWWW)
        {
            if (uri.IsFile || uri.IsUnc)
                return uri.LocalPath;
            UriBuilder builder = new UriBuilder(uri);
            if (replaceWWW)
            {
                builder.Host = (builder.Host.ToLower().StartsWith("www.") ? builder.Host.Substring(4) : builder.Host);
            }
            builder.Path = (builder.Path.EndsWith("/") ? builder.Path.Substring(0, builder.Path.Length - 1) : builder.Path);
            string strUri = builder.ToString();
            if (builder.Scheme == "http" && builder.Port == 80)
                strUri = strUri.Replace(":" + builder.Port + "/", "/");
            if (builder.Scheme == "https" && builder.Port == 443)
                strUri = strUri.ToString().Replace(":" + builder.Port + "/", "/");
            return strUri;
        }
    }
}
