using System;
namespace WikiFunctions.API
{
    public interface IApiEdit
    {
        string URL
        { get; }
        bool PHP5
        { get; }
        UserInfo User
        { get; }
        int Maxlag
        { get; set; }
        bool NewMessageThrows
        { get; set; }
        void RefreshUserInfo();
        string Action
        { get; }
        PageInfo Page
        { get; }
        string HtmlHeaders
        { get; }
        IApiEdit Clone();
        void Reset();
        string HttpGet(string url);
        void Login(string username, string password);
        void Logout();
        string Open(string title);
        SaveInfo Save(string pageText, string summary, bool minor, WatchOptions watch);
        void Watch(string title);
        void Unwatch(string title);
        void Delete(string title, string reason);
        void Delete(string title, string reason, bool watch);
        void Protect(string title, string reason, string expiry, string edit, string move);
        void Protect(string title, string reason, TimeSpan expiry, string edit, string move);
        void Protect(string title, string reason, string expiry, string edit, string move, bool cascade, bool watch);
        void Protect(string title, string reason, TimeSpan expiry, string edit, string move, bool cascade, bool watch);
        void Move(string title, string newTitle, string reason);
        void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect);
        void Move(string title, string newTitle, string reason, bool moveTalk, bool noRedirect, bool watch);
        string Preview(string title, string text);
        string ExpandTemplates(string title, string text);
        void Abort();
        string QueryApi(string queryParameters);
    }
}
