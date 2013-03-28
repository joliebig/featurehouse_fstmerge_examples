using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading;
using System.Xml;
using System.Xml.Serialization;
using log4net;
using RssBandit.Common.Logging;
using NewsComponents.Collections;
namespace NewsComponents.Feed
{
    public enum NewsGatorOperation : byte
    {
        AddFeed = 51,
        AddFolder = 41,
        DeleteFeed = 50,
        DeleteFolder = 40,
        MarkAllItemsRead = 61,
        MarkSingleItemFlagged = 62,
        MarkSingleItemReadOrDeleted = 60,
        MarkSingleItemClipped = 59,
        MoveFeed = 45,
        MoveFolder = 46,
        RenameFeed = 21,
        RenameFolder = 20,
    }
    public class PendingNewsGatorOperation
    {
        public NewsGatorOperation Action;
        public string NewsGatorUserName;
        public object[] Parameters;
        private PendingNewsGatorOperation() { ;}
        public PendingNewsGatorOperation(NewsGatorOperation action, object[] parameters, string ngUserID)
        {
            this.Action = action;
            this.Parameters = parameters;
            this.NewsGatorUserName = ngUserID;
        }
        public override bool Equals(object obj)
        {
            PendingNewsGatorOperation pop = obj as PendingNewsGatorOperation;
            if (pop == null) return false;
            if (pop.Action != this.Action) return false;
            if (pop.Parameters.Length != this.Parameters.Length) return false;
            for (int i = 0; i < pop.Parameters.Length; i++)
            {
                if (!pop.Parameters[i].Equals(this.Parameters[i]))
                    return false;
            }
            return true;
        }
    }
    internal class NewsGatorModifier
    {
        private Dictionary<string, NewsGatorFeedSource> FeedSources = new Dictionary<string, NewsGatorFeedSource>();
        private Thread NewsGatorModifyingThread;
        private bool flushInprogress = false, threadRunning = false;
        private List<PendingNewsGatorOperation> pendingNewsGatorOperations = new List<PendingNewsGatorOperation>();
        private readonly string pendingNewsGatorOperationsFile = "pending-newsgator-operations.xml";
        private static readonly ILog _log = Log.GetLogger(typeof(NewsGatorModifier));
        private bool Offline
        {
            get
            {
                if (FeedSources.Count > 0)
                {
                    return FeedSources.Values.ElementAt(0).Offline;
                }
                return false;
            }
        }
        private NewsGatorModifier() { ;}
        public NewsGatorModifier(string applicationDataPath)
        {
            pendingNewsGatorOperationsFile = Path.Combine(applicationDataPath, pendingNewsGatorOperationsFile);
            this.LoadPendingOperations();
        }
        private void LoadPendingOperations()
        {
            if (File.Exists(this.pendingNewsGatorOperationsFile))
            {
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(List<PendingNewsGatorOperation>));
                pendingNewsGatorOperations = serializer.Deserialize(XmlReader.Create(this.pendingNewsGatorOperationsFile)) as List<PendingNewsGatorOperation>;
            }
        }
        private void CreateThread()
        {
            NewsGatorModifyingThread = new Thread(this.ThreadRun);
            NewsGatorModifyingThread.Name = "NewsGatorModifyingThread";
            NewsGatorModifyingThread.IsBackground = true;
            this.threadRunning = true;
            NewsGatorModifyingThread.Start();
        }
        private void ThreadRun()
        {
            while (threadRunning)
            {
                if (false == this.Offline && false == this.flushInprogress &&
                    this.pendingNewsGatorOperations.Count > 0)
                {
                    FlushPendingOperations(Math.Max(10, this.pendingNewsGatorOperations.Count / 10));
                    if (threadRunning)
                        Thread.Sleep(1000 * 5);
                }
                else
                {
                    Thread.Sleep(1000 * 30);
                }
            }
        }
        private void PerformOperation(PendingNewsGatorOperation current)
        {
            NewsGatorFeedSource source = null;
            this.FeedSources.TryGetValue(current.NewsGatorUserName, out source);
            if (source == null)
            {
                return;
            }
            try
            {
                switch (current.Action)
                {
                    case NewsGatorOperation.AddFeed:
                        source.AddFeedInNewsGatorOnline(current.Parameters[0] as string);
                        break;
                    case NewsGatorOperation.AddFolder:
                        source.AddFolderInNewsGatorOnline(current.Parameters[0] as string);
                        break;
                    case NewsGatorOperation.DeleteFeed:
                        source.DeleteFeedFromNewsGatorOnline(current.Parameters[0] as string);
                        break;
                    case NewsGatorOperation.MarkSingleItemFlagged:
                        source.ChangeItemStateInNewsGatorOnline(current.Parameters[0] as string, current.Parameters[1] as string, (NewsGatorFlagStatus)current.Parameters[2]);
                        break;
                    case NewsGatorOperation.MarkSingleItemClipped:
                        source.ChangeItemClippedStateInNewsGatorOnline(current.Parameters[0] as string, (bool)current.Parameters[1]);
                        break;
                    case NewsGatorOperation.MarkSingleItemReadOrDeleted:
                        source.ChangeItemStateInNewsGatorOnline(current.Parameters[0] as string, (NewsGatorItemState) current.Parameters[1]);
                        break;
                    case NewsGatorOperation.MarkAllItemsRead:
                        source.MarkAllItemsAsReadInNewsGatorOnline(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    case NewsGatorOperation.MoveFeed:
                        source.ChangeFolderInNewsGatorOnline(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    case NewsGatorOperation.RenameFeed:
                        source.RenameFeedInNewsGatorOnline(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    case NewsGatorOperation.RenameFolder:
                        source.RenameFolderInNewsGatorOnline(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    default:
                        Debug.Assert(false, "Unknown NewsGator Online operation: " + current.Action);
                        return;
                }
            }
            catch (Exception e)
            {
                _log.Error("Error in NewsGatorModifier.PerformOperation:", e);
            };
        }
        private void FlushPendingOperations(int batchedItemsAmount)
        {
            try
            {
                this.flushInprogress = true;
                do
                {
                    PendingNewsGatorOperation pendingOp = null;
                    lock (this.pendingNewsGatorOperations)
                    {
                        if (this.pendingNewsGatorOperations.Count > 0)
                        {
                            pendingOp = this.pendingNewsGatorOperations[0];
                        }
                    }
                    if (pendingOp != null)
                    {
                        this.PerformOperation(pendingOp);
                        this.pendingNewsGatorOperations.RemoveAt(0);
                    }
                    batchedItemsAmount--;
                } while (this.pendingNewsGatorOperations.Count > 0 && batchedItemsAmount >= 0);
            }
            finally
            {
                this.flushInprogress = false;
            }
        }
        public void StartBackgroundThread()
        {
            if (!this.threadRunning)
            {
                this.CreateThread();
            }
        }
        public void StopBackgroundThread()
        {
            this.threadRunning = false;
            while (this.flushInprogress)
                Thread.Sleep(50);
            this.SavePendingOperations();
        }
        public void SavePendingOperations()
        {
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(List<PendingNewsGatorOperation>));
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.OmitXmlDeclaration = true;
            serializer.Serialize(XmlWriter.Create(this.pendingNewsGatorOperationsFile, settings), this.pendingNewsGatorOperations);
        }
        public void RegisterFeedSource(NewsGatorFeedSource source)
        {
            this.FeedSources.Add(source.NewsGatorUserName, source);
        }
        public void UnregisterFeedSource(NewsGatorFeedSource source)
        {
            this.FeedSources.Remove(source.NewsGatorUserName);
        }
        public void ChangeItemStateInNewsGatorOnline(string newsgatorUserID, string itemId, NewsGatorItemState state)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.MarkSingleItemReadOrDeleted, new object[] { itemId, state }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void MarkAllItemsAsReadInNewsGatorOnline(string newsgatorUserID, string feedUrl, string syncToken)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.MarkAllItemsRead, new object[] { feedUrl, syncToken }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void ChangeItemClippedStateInNewsGatorOnline(string newsgatorUserID, string itemId, bool clipped)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.MarkSingleItemClipped, new object[] { itemId, clipped }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void ChangeItemStateInNewsGatorOnline(string newsgatorUserID, string itemId, string feedUrl, NewsGatorFlagStatus state)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.MarkSingleItemFlagged, new object[] { itemId, feedUrl, state }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void DeleteFeedFromNewsGatorOnline(string newsgatorUserID, string feedUrl)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.DeleteFeed, new object[] { feedUrl }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void AddFeedInNewsGatorOnline(string newsgatorUserID, string feedUrl)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.AddFeed, new object[] { feedUrl }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void AddFolderInNewsGatorOnline(string newsgatorUserID, string name)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.AddFolder, new object[] { name }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void ChangeFolderInNewsGatorOnline(string newsgatorUserID, string feedUrl, string cat)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.MoveFeed, new object[] { feedUrl, cat }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void RenameFeedInNewsGatorOnline(string newsgatorUserID, string url, string title)
        {
            PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.RenameFeed, new object[] { url, title }, newsgatorUserID);
            lock (this.pendingNewsGatorOperations)
            {
                this.pendingNewsGatorOperations.Add(op);
            }
        }
        public void RenameFolderInNewsGatorOnline(string newsgatorUserID, string oldName, string newName)
        {
            lock (this.pendingNewsGatorOperations)
            {
                PendingNewsGatorOperation addFolderOp = this.pendingNewsGatorOperations.Find(oldOp => oldOp.Action == NewsGatorOperation.AddFolder && oldName.Equals(oldOp.Parameters[0]));
                if (addFolderOp == null)
                {
                    PendingNewsGatorOperation renameOp = this.pendingNewsGatorOperations.Find(oldOp => oldOp.Action == NewsGatorOperation.RenameFolder && oldName.Equals(oldOp.Parameters[1]));
                    if (renameOp == null)
                    {
                        PendingNewsGatorOperation op = new PendingNewsGatorOperation(NewsGatorOperation.RenameFolder, new object[] { oldName, newName }, newsgatorUserID);
                        this.pendingNewsGatorOperations.Add(op);
                    }
                    else
                    {
                        this.pendingNewsGatorOperations.Remove(renameOp);
                        this.pendingNewsGatorOperations.Add(new PendingNewsGatorOperation(NewsGatorOperation.RenameFolder, new object[] { renameOp.Parameters[0], newName }, newsgatorUserID));
                    }
                }
                else
                {
                    this.pendingNewsGatorOperations.Remove(addFolderOp);
                    this.pendingNewsGatorOperations.Add(new PendingNewsGatorOperation(NewsGatorOperation.AddFolder, new object[] { newName }, newsgatorUserID));
                }
            }
        }
    }
}
