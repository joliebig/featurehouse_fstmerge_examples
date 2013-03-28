using System;
using System.Collections;
using System.Collections.Generic;
using ThoughtWorks.CruiseControl.Core.Config;
using ThoughtWorks.CruiseControl.Core.Util;
using ThoughtWorks.CruiseControl.Remote;
using System.Threading;
namespace ThoughtWorks.CruiseControl.Core.Queues
{
 public class IntegrationQueue : ArrayList, IIntegrationQueue
 {
        private static object blockingLockObject = new object();
        private readonly string name;
        private readonly IQueueConfiguration configuration;
        private readonly List<string> blockingQueueNames;
        private readonly IntegrationQueueSet parentQueueSet;
        private bool inUse = false;
        private static readonly object queueLockSync = new object();
  public IntegrationQueue(string name, IQueueConfiguration configuration, IntegrationQueueSet parentQueueSet)
  {
   this.name = name;
            this.configuration = configuration;
            this.parentQueueSet = parentQueueSet;
            this.blockingQueueNames = new List<string>();
  }
  public string Name
  {
   get { return name; }
  }
        public virtual bool IsBlocked
        {
            get
            {
                lock (queueLockSync)
                {
                    return blockingQueueNames.Count != 0;
                }
            }
        }
        public virtual IQueueConfiguration Configuration
        {
            get { return configuration; }
        }
  public void Enqueue(IIntegrationQueueItem integrationQueueItem)
  {
   lock (this)
   {
    if (Count == 0)
    {
     AddToQueue(integrationQueueItem);
    }
    else
    {
     int? foundIndex = null;
                    bool addItem = true;
                    IIntegrationQueueItem foundItem = null;
     for (int index = 1; index < Count; index++)
     {
      IIntegrationQueueItem queuedItem = GetIntegrationQueueItem(index);
      if (queuedItem.Project == integrationQueueItem.Project)
      {
                            foundItem = queuedItem;
                            foundIndex = index;
                            break;
      }
     }
     if (foundIndex != null)
      {
                        switch (configuration.HandlingMode)
                        {
                            case QueueDuplicateHandlingMode.UseFirst:
                                Log.Info(String.Format("Project: {0} already on queue: {1} - cancelling new request", integrationQueueItem.Project.Name, Name));
                                addItem = false;
                                break;
                            case QueueDuplicateHandlingMode.ApplyForceBuildsReAdd:
                                if (foundItem.IntegrationRequest.BuildCondition >= integrationQueueItem.IntegrationRequest.BuildCondition)
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} - cancelling new request", integrationQueueItem.Project.Name, Name));
                                    addItem = false;
                                }
                                else
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} with lower prority - cancelling existing request", integrationQueueItem.Project.Name, Name));
                                    lock (this)
                                    {
                                        NotifyExitingQueueAndRemoveItem(foundIndex.Value, foundItem, true);
                                    }
                                }
                                break;
                            case QueueDuplicateHandlingMode.ApplyForceBuildsReAddTop:
                                addItem = false;
                                if (foundItem.IntegrationRequest.BuildCondition >= integrationQueueItem.IntegrationRequest.BuildCondition)
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} - cancelling new request", integrationQueueItem.Project.Name, Name));
                                }
                                else
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} with lower prority - cancelling existing request", integrationQueueItem.Project.Name, Name));
                                    lock (this)
                                    {
                                        NotifyExitingQueueAndRemoveItem(foundIndex.Value, foundItem, true);
                                        AddToQueue(integrationQueueItem, 1);
                                    }
                                }
                                break;
                            case QueueDuplicateHandlingMode.ApplyForceBuildsReplace:
                                addItem = false;
                                if (foundItem.IntegrationRequest.BuildCondition >= integrationQueueItem.IntegrationRequest.BuildCondition)
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} - cancelling new request", integrationQueueItem.Project.Name, Name));
                                }
                                else
                                {
                                    Log.Info(String.Format("Project: {0} already on queue: {1} with lower prority - replacing existing request at position {2}", integrationQueueItem.Project.Name, Name, foundIndex));
                                    lock (this)
                                    {
                                        NotifyExitingQueueAndRemoveItem(foundIndex.Value, foundItem, true);
                                        AddToQueue(integrationQueueItem, foundIndex);
                                    }
                                }
                                break;
                            default:
                                throw new ConfigurationException("Unknown handling mode for duplicates: " + configuration.HandlingMode);
                        }
      }
                    if (addItem)
                    {
                        lock (this)
                        {
                            AddToQueue(integrationQueueItem);
                        }
                    }
    }
   }
  }
  private IIntegrationQueueItem GetIntegrationQueueItem(int index)
  {
   return this[index] as IIntegrationQueueItem;
  }
  public void Dequeue()
  {
   lock (this)
   {
    if (Count > 0)
    {
     IIntegrationQueueItem integrationQueueItem = (IIntegrationQueueItem) this[0];
     NotifyExitingQueueAndRemoveItem(0, integrationQueueItem, false);
    }
   }
  }
  public void RemovePendingRequest(IProject project)
  {
   lock (this)
   {
    bool considerFirstQueueItem = false;
    RemoveProjectItems(project, considerFirstQueueItem);
   }
  }
  public void RemoveProject(IProject project)
  {
   lock (this)
   {
    bool considerFirstQueueItem = true;
    RemoveProjectItems(project, considerFirstQueueItem);
   }
  }
  public IIntegrationQueueItem[] GetQueuedIntegrations()
  {
   return (IIntegrationQueueItem[]) ToArray(typeof (IIntegrationQueueItem));
  }
  public IntegrationRequest GetNextRequest(IProject project)
  {
            lock (this)
            {
                if (Count == 0) return null;
                if (IsBlocked) return null;
                IIntegrationQueueItem item = GetIntegrationQueueItem(0);
                if (item != null && item.Project == project)
                    return item.IntegrationRequest;
                return null;
            }
  }
  public bool HasItemOnQueue(IProject project)
  {
   return HasItemOnQueue(project, false);
  }
  public bool HasItemPendingOnQueue(IProject project)
  {
   return HasItemOnQueue(project, true);
  }
  private bool HasItemOnQueue(IProject project, bool pendingItemsOnly)
  {
   lock (this)
   {
    int startIndex = pendingItemsOnly ? 1 : 0;
    if (Count > startIndex)
    {
     for (int index = startIndex; index < Count; index++)
     {
      IIntegrationQueueItem queuedIntegrationQueueItem = this[index] as IIntegrationQueueItem;
      if ((queuedIntegrationQueueItem != null) && (queuedIntegrationQueueItem.Project == project))
       return true;
     }
    }
    return false;
   }
  }
        private void AddToQueue(IIntegrationQueueItem integrationQueueItem)
        {
            AddToQueue(integrationQueueItem, null);
        }
  private void AddToQueue(IIntegrationQueueItem integrationQueueItem, int? queuePosition)
  {
            if (!queuePosition.HasValue)
            {
                queuePosition = GetPrioritisedQueuePosition(integrationQueueItem.Project.QueuePriority);
                Log.Info(string.Format("Project: '{0}' is added to queue: '{1}' in position {2}. Requestsource : {3} ({4})",
                                       integrationQueueItem.Project.Name, Name, queuePosition, integrationQueueItem.IntegrationRequest.Source,integrationQueueItem.IntegrationRequest.UserName));
            }
   integrationQueueItem.IntegrationQueueNotifier.NotifyEnteringIntegrationQueue();
   Insert(queuePosition.Value, integrationQueueItem);
  }
  private int GetPrioritisedQueuePosition(int insertingItemPriority)
  {
   int targetQueuePosition = Count;
   if (insertingItemPriority != 0 && Count > 1)
   {
    for (int index = 1; index < Count; index++)
    {
     IIntegrationQueueItem queuedIntegrationQueueItem = this[index] as IIntegrationQueueItem;
     if (queuedIntegrationQueueItem != null)
     {
      int compareQueuePosition = queuedIntegrationQueueItem.Project.QueuePriority;
      if (compareQueuePosition == 0 || compareQueuePosition > insertingItemPriority)
      {
       targetQueuePosition = index;
       break;
      }
     }
    }
   }
   return targetQueuePosition;
  }
  private void RemoveProjectItems(IProject project, bool considerFirstQueueItem)
  {
   int startQueueIndex = considerFirstQueueItem ? 0 : 1;
   for (int index = Count - 1; index >= startQueueIndex; index--)
   {
    IIntegrationQueueItem integrationQueueItem = (IIntegrationQueueItem) this[index];
    if (integrationQueueItem.Project.Equals(project))
    {
     Log.Info("Project: " + integrationQueueItem.Project.Name + " removed from_ queue: " + Name);
     bool isPendingItemCancelled = index > 0;
     NotifyExitingQueueAndRemoveItem(index, integrationQueueItem, isPendingItemCancelled);
    }
   }
  }
  private void NotifyExitingQueueAndRemoveItem(int index, IIntegrationQueueItem integrationQueueItem, bool isPendingItemCancelled)
  {
   integrationQueueItem.IntegrationQueueNotifier.NotifyExitingIntegrationQueue(isPendingItemCancelled);
            RemoveAt(index);
  }
        private IEnumerable<IIntegrationQueue> LockQueues
        {
            get
            {
                if (!string.IsNullOrEmpty(configuration.LockQueueNames) && parentQueueSet != null)
                {
                    string[] queues = configuration.LockQueueNames.Split(new char[] { ',' }, StringSplitOptions.RemoveEmptyEntries);
                    List<string> actualQueues = new List<string>(parentQueueSet.GetQueueNames());
                    for (int i = 0; i < queues.Length; i++)
                    {
                        string queueToLock = queues[i].Trim();
                        if (actualQueues.Contains(queueToLock))
                            yield return parentQueueSet[queueToLock];
                        else
                            Log.Warning(string.Format("Unknown queue found: '{0}'", queueToLock));
                    }
                }
            }
        }
        public bool TryLock(out IDisposable lockObject)
        {
            Log.Info(string.Format("Queue: '{0}' is attempting to be in-use, trying to lock related queues", Name));
            lockObject = null;
            lock (blockingLockObject)
            {
                if (IsBlocked)
                {
                    Log.Info(string.Format("Queue: '{0}' is locked and cannot be in-use", Name));
                    return false;
                }
                IList<IIntegrationQueue> lockedQueues = new List<IIntegrationQueue>();
                bool failed = false;
                foreach (IIntegrationQueue queue in LockQueues)
                {
                    if (queue.BlockQueue(this))
                    {
                        Log.Info(string.Format("Queue: '{0}' has acquired a lock against queue '{1}'", Name, queue.Name));
                        lockedQueues.Add(queue);
                    }
                    else
                    {
                        Log.Info(string.Format("Queue: '{0}' has FAILED to acquire a lock against queue '{1}'", Name, queue.Name));
                        failed = true;
                        break;
                    }
                }
                if (failed)
                {
                    foreach (IIntegrationQueue queue in lockedQueues)
                    {
                        Log.Info(string.Format("Queue: '{0}' has released a lock against queue '{1}'", Name, queue.Name));
                        queue.UnblockQueue(this);
                        return false;
                    }
                }
                lockObject = new LockHolder(this, lockedQueues);
                inUse = true;
                return true;
            }
        }
        private sealed class LockHolder : IDisposable
        {
            private IntegrationQueue lockingQueue;
            private IList<IIntegrationQueue> lockedQueues;
            public LockHolder(IntegrationQueue lockingQueue, IList<IIntegrationQueue> lockedQueues)
            {
                this.lockingQueue = lockingQueue;
                this.lockedQueues = lockedQueues;
            }
            public void Dispose()
            {
                foreach (IIntegrationQueue queue in lockedQueues)
                {
                    Log.Info(string.Format("Queue: '{0}' has released a lock against queue '{1}'", lockingQueue.Name, queue.Name));
                    queue.UnblockQueue(lockingQueue);
                }
                lockingQueue.inUse = false;
            }
        }
        public bool BlockQueue(IIntegrationQueue requestingQueue)
        {
            if (inUse)
                return false;
            lock (queueLockSync)
            {
                if (!blockingQueueNames.Contains(requestingQueue.Name))
                {
                    blockingQueueNames.Add(requestingQueue.Name);
                }
            }
            return true;
        }
        public void UnblockQueue(IIntegrationQueue requestingQueue)
        {
            lock (queueLockSync)
            {
                blockingQueueNames.Remove(requestingQueue.Name);
            }
        }
    }
}
