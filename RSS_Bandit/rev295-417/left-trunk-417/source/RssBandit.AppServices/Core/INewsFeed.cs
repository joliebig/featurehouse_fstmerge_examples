using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Xml;
using RssBandit.AppServices.Core;
namespace NewsComponents.Feed
{
    public interface INewsFeed : INotifyPropertyChanged, ISharedProperty
    {
        string title { get; set; }
        string link { get; set; }
        string id { get; set; }
        bool lastretrievedSpecified { get; set; }
        DateTime lastretrieved { get; set; }
        string etag { get; set; }
        string cacheurl { get; set; }
        List<string> storiesrecentlyviewed { get; set; }
        List<string> deletedstories { get; set; }
        DateTime lastmodified { get; set; }
        bool lastmodifiedSpecified { get; set; }
        string authUser { get; set; }
        Byte[] authPassword { get; set; }
        string favicon { get; set; }
        int causedExceptionCount { get; set; }
        bool causedException { get; set; }
        bool replaceitemsonrefresh { get; set; }
        bool replaceitemsonrefreshSpecified { get; set; }
        string newsaccount { get; set; }
        XmlElement[] Any { get; set; }
        XmlAttribute[] AnyAttr { get; set; }
        bool alertEnabled { get; set; }
        bool alertEnabledSpecified { get; set; }
        object Tag { get; set; }
        object owner { get; set; }
        bool containsNewComments { get; set; }
        bool containsNewMessages { get; set; }
        string category { get; set; }
        void AddViewedStory(string storyid);
        void RemoveViewedStory(string storyid);
        void AddDeletedStory(string storyid);
        void RemoveDeletedStory(string storyid);
    }
}
