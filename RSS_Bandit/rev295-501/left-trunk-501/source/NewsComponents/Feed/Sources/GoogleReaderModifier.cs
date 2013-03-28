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
    public enum GoogleReaderOperation : byte
    {
        AddFeed = 51,
        AddLabel = 41,
        DeleteFeed = 50,
        DeleteLabel = 40,
        MarkAllItemsRead = 61,
        MarkSingleItemRead = 60,
        MarkSingleItemStarred = 59,
        MoveFeed = 45,
        RenameFeed = 21,
        RenameLabel = 20,
    }
    public class PendingGoogleReaderOperation
    {
        public GoogleReaderOperation Action;
        public string GoogleUserName;
        public object[] Parameters;
        private PendingGoogleReaderOperation() { ;}
        public PendingGoogleReaderOperation(GoogleReaderOperation action, object[] parameters, string googleUserID)
        {
            this.Action = action;
            this.Parameters = parameters;
            this.GoogleUserName = googleUserID;
        }
        public override bool Equals(object obj)
        {
            PendingGoogleReaderOperation pop = obj as PendingGoogleReaderOperation;
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
    internal class GoogleReaderModifier
    {
        private Dictionary<string, GoogleReaderFeedSource> FeedSources = new Dictionary<string, GoogleReaderFeedSource>();
        private Thread GoogleReaderModifyingThread;
        private bool flushInprogress = false, threadRunning = false;
        private List<PendingGoogleReaderOperation> pendingGoogleReaderOperations = new List<PendingGoogleReaderOperation>();
        private readonly string pendingGoogleOperationsFile = "pending-googlereader-operations.xml";
        private static readonly ILog _log = Log.GetLogger(typeof(GoogleReaderModifier));
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
        private GoogleReaderModifier() { ;}
        public GoogleReaderModifier(string applicationDataPath)
        {
            pendingGoogleOperationsFile = Path.Combine(applicationDataPath, pendingGoogleOperationsFile);
            this.LoadPendingOperations();
        }
        private void LoadPendingOperations()
        {
            if (File.Exists(this.pendingGoogleOperationsFile))
            {
                XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(List<PendingGoogleReaderOperation>));
                pendingGoogleReaderOperations = serializer.Deserialize(XmlReader.Create(this.pendingGoogleOperationsFile)) as List<PendingGoogleReaderOperation>;
            }
        }
        private void CreateThread()
        {
            GoogleReaderModifyingThread = new Thread(this.ThreadRun);
            GoogleReaderModifyingThread.Name = "GoogleReaderModifyingThread";
            GoogleReaderModifyingThread.IsBackground = true;
            this.threadRunning = true;
            GoogleReaderModifyingThread.Start();
        }
        private void ThreadRun()
        {
            while (threadRunning)
            {
                if (false == this.Offline && false == this.flushInprogress &&
                    this.pendingGoogleReaderOperations.Count > 0)
                {
                    FlushPendingOperations(Math.Max(10, this.pendingGoogleReaderOperations.Count / 10));
                    if (threadRunning)
                        Thread.Sleep(1000 * 5);
                }
                else
                {
                    Thread.Sleep(1000 * 30);
                }
            }
        }
        private void PerformOperation(PendingGoogleReaderOperation current)
        {
            GoogleReaderFeedSource source = null;
            this.FeedSources.TryGetValue(current.GoogleUserName, out source);
            if (source == null)
            {
                return;
            }
            try
            {
                switch (current.Action)
                {
                    case GoogleReaderOperation.AddFeed:
                        source.AddFeedInGoogleReader(current.Parameters[0] as string);
                        break;
                    case GoogleReaderOperation.AddLabel:
                        source.AddCategoryInGoogleReader(current.Parameters[0] as string);
                        break;
                    case GoogleReaderOperation.DeleteFeed:
                        source.DeleteFeedFromGoogleReader(current.Parameters[0] as string);
                        break;
                    case GoogleReaderOperation.DeleteLabel:
                        source.DeleteCategoryInGoogleReader(current.Parameters[0] as string);
                        break;
                    case GoogleReaderOperation.MarkAllItemsRead:
                        source.MarkAllItemsAsReadInGoogleReader(current.Parameters[0] as string, (DateTime)current.Parameters[1]);
                        break;
                    case GoogleReaderOperation.MarkSingleItemRead:
                        source.ChangeItemReadStateInGoogleReader(current.Parameters[0] as string, current.Parameters[1] as string, (bool) current.Parameters[2]);
                        break;
                    case GoogleReaderOperation.MarkSingleItemStarred:
                        source.ChangeItemStarredStateInGoogleReader(current.Parameters[0] as string, current.Parameters[1] as string, (bool)current.Parameters[2]);
                        break;
                    case GoogleReaderOperation.MoveFeed:
                        source.ChangeCategoryInGoogleReader(current.Parameters[0] as string, current.Parameters[1] as string, current.Parameters[2] as string);
                        break;
                    case GoogleReaderOperation.RenameFeed:
                        source.RenameFeedInGoogleReader(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    case GoogleReaderOperation.RenameLabel:
                        source.RenameCategoryInGoogleReader(current.Parameters[0] as string, current.Parameters[1] as string);
                        break;
                    default:
                        Debug.Assert(false, "Unknown Google Reader operation: " + current.Action);
                        return;
                }
            }
            catch (Exception e) {
                _log.Error("Error in GoogleReaderModifier.PerformOperation:", e);
            };
        }
        private void FlushPendingOperations(int batchedItemsAmount)
        {
            try
            {
                this.flushInprogress = true;
                do
                {
                   PendingGoogleReaderOperation pendingOp = null;
                    lock (this.pendingGoogleReaderOperations)
                    {
                        if (this.pendingGoogleReaderOperations.Count > 0)
                        {
                            pendingOp = this.pendingGoogleReaderOperations[0];
                        }
                    }
                    if (pendingOp != null)
                    {
                        this.PerformOperation(pendingOp);
                        this.pendingGoogleReaderOperations.RemoveAt(0);
                    }
                    batchedItemsAmount--;
                } while (this.pendingGoogleReaderOperations.Count > 0 && batchedItemsAmount >= 0);
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
            XmlSerializer serializer = XmlHelper.SerializerCache.GetSerializer(typeof(List<PendingGoogleReaderOperation>));
            XmlWriterSettings settings = new XmlWriterSettings();
            settings.Indent = true;
            settings.OmitXmlDeclaration = true;
            serializer.Serialize(XmlWriter.Create(this.pendingGoogleOperationsFile, settings), this.pendingGoogleReaderOperations);
        }
        public void RegisterFeedSource(GoogleReaderFeedSource source)
        {
            this.FeedSources.Add(source.GoogleUserName, source);
        }
        public void UnregisterFeedSource(GoogleReaderFeedSource source)
        {
            this.FeedSources.Remove(source.GoogleUserName);
        }
        public void DeleteCategoryInGoogleReader(string googleUserID, string name)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.DeleteLabel, new object[] { name }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void RenameCategoryInGoogleReader(string googleUserID, string oldName, string newName)
        {
            lock (this.pendingGoogleReaderOperations)
            {
                PendingGoogleReaderOperation addFolderOp = this.pendingGoogleReaderOperations.Find(oldOp => oldOp.Action == GoogleReaderOperation.AddLabel && oldName.Equals(oldOp.Parameters[0]));
                if (addFolderOp == null)
                {
                    PendingGoogleReaderOperation renameOp = this.pendingGoogleReaderOperations.Find(oldOp => oldOp.Action == GoogleReaderOperation.RenameLabel && oldName.Equals(oldOp.Parameters[1]));
                    if (renameOp == null)
                    {
                        PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.RenameLabel, new object[] { oldName, newName }, googleUserID);
                        this.pendingGoogleReaderOperations.Add(op);
                    }
                    else
                    {
                        this.pendingGoogleReaderOperations.Remove(renameOp);
                        this.pendingGoogleReaderOperations.Add(new PendingGoogleReaderOperation(GoogleReaderOperation.RenameLabel, new object[] { renameOp.Parameters[0], newName }, googleUserID));
                    }
                }
                else
                {
                    this.pendingGoogleReaderOperations.Remove(addFolderOp);
                    this.pendingGoogleReaderOperations.Add(new PendingGoogleReaderOperation(GoogleReaderOperation.AddLabel, new object[] { newName }, googleUserID));
                }
            }
        }
        public void RenameFeedInGoogleReader(string googleUserID, string url, string title)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.RenameFeed, new object[] { url, title }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void MarkAllItemsAsReadInGoogleReader(string googleUserID, string feedUrl, DateTime olderThan)
        {
             PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.MarkAllItemsRead, new object[] { feedUrl, olderThan }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void ChangeItemReadStateInGoogleReader(string googleUserID, string feedId, string itemId, bool beenRead)
        {
           PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.MarkSingleItemRead, new object[] { feedId, itemId, beenRead }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void ChangeItemStarredStateInGoogleReader(string googleUserID, string feedId, string itemId, bool starred)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.MarkSingleItemStarred, new object[] { feedId, itemId, starred }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void ChangeCategoryInGoogleReader(string googleUserID, string feedUrl, string newCategory, string oldCategory)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.MoveFeed, new object[] { feedUrl, newCategory, oldCategory }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void AddFeedInGoogleReader(string googleUserID, string feedUrl)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.AddFeed, new object[] { feedUrl }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        internal void AddCategoryInGoogleReader(string googleUserID, string name)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.AddLabel, new object[] { name }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
        public void DeleteFeedFromGoogleReader(string googleUserID, string feedUrl)
        {
            PendingGoogleReaderOperation op = new PendingGoogleReaderOperation(GoogleReaderOperation.DeleteFeed, new object[] { feedUrl }, googleUserID);
            lock (this.pendingGoogleReaderOperations)
            {
                this.pendingGoogleReaderOperations.Add(op);
            }
        }
    }
}
