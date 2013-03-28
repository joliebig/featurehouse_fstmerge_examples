using System;
using System.Diagnostics;
using System.Net;
using System.Windows.Forms;
using Microsoft.ApplicationBlocks.ExceptionManagement;
using NewsComponents;
using NewsComponents.Feed;
using NewsComponents.Net;
using RssBandit.Resources;
using RssBandit.WinGui;
using RssBandit.WinGui.Controls;
using RssBandit.WinGui.Forms;
using RssBandit.WinGui.Interfaces;
namespace RssBandit
{
    internal partial class RssBanditApplication
    {
        private void OnRequestCertificateIssue(object sender, CertificateIssueCancelEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                guiMain.OnRequestCertificateIssue(sender, e);
                            });
        }
        private void OnMovedFeed(object sender, FeedSource.FeedMovedEventArgs e){
            InvokeOnGui(delegate
            {
                TreeFeedsNodeBase tn = TreeHelper.FindNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.FeedUrl);
                TreeFeedsNodeBase parent = TreeHelper.FindCategoryNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.NewCategory);
                if (tn != null && parent!= null)
                {
                    guiMain.MoveNode(tn, parent);
                    SubscriptionModified(NewsFeedProperty.FeedAdded);
                }
            });
        }
        private void OnRenamedFeed(object sender, FeedSource.FeedRenamedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                TreeFeedsNodeBase tn = TreeHelper.FindNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.FeedUrl);
                if (tn != null)
                {
                    guiMain.RenameTreeNode(tn, e.NewName);
                    SubscriptionModified(NewsFeedProperty.FeedTitle);
                }
            });
        }
        private void OnDeletedFeed(object sender, FeedSource.FeedDeletedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                RaiseFeedDeleted(e.FeedUrl, e.Title);
                SubscriptionModified(NewsFeedProperty.FeedRemoved);
            });
        }
        private void OnAddedFeed(object sender, FeedSource.FeedChangedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                INewsFeed f = null;
                this.FeedHandler.GetFeeds().TryGetValue(e.FeedUrl, out f);
                if (f != null)
                {
                    guiMain.AddNewFeedNode(f.category, f);
                    SubscriptionModified(NewsFeedProperty.FeedAdded);
                }
            });
        }
        private void OnMovedCategory(object sender, FeedSource.CategoryChangedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                TreeFeedsNodeBase parent = null, tn = TreeHelper.FindCategoryNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.CategoryName);
                int index = e.NewCategoryName.LastIndexOf(FeedSource.CategorySeparator);
                if(index == -1){
                    parent = guiMain.GetRoot(RootFolderType.MyFeeds);
                }else{
                    parent = TreeHelper.FindCategoryNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.NewCategoryName.Substring(0, index));
                }
                if (tn != null && parent != null)
                {
                    guiMain.MoveNode(tn, parent);
                    SubscriptionModified(NewsFeedProperty.FeedCategoryAdded);
                }
            });
        }
        private void OnRenamedCategory(object sender, FeedSource.CategoryChangedEventArgs e)
        {
            InvokeOnGui(delegate
            {
                TreeFeedsNodeBase tn = TreeHelper.FindChildNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.CategoryName, FeedNodeType.Category);
                if (tn != null)
                {
                    guiMain.RenameTreeNode(tn, e.NewCategoryName);
                    SubscriptionModified(NewsFeedProperty.FeedCategoryAdded);
                }
            });
        }
        private void OnAddedCategory(object sender, FeedSource.CategoryEventArgs e)
        {
                InvokeOnGui(delegate
                {
                    this.AddCategory(e.CategoryName);
                    SubscriptionModified(NewsFeedProperty.FeedCategoryAdded);
                });
        }
        private void OnDeletedCategory(object sender, FeedSource.CategoryEventArgs e)
        {
            InvokeOnGui(delegate
            {
                TreeFeedsNodeBase tn = TreeHelper.FindChildNode(guiMain.GetRoot(RootFolderType.MyFeeds), e.CategoryName, FeedNodeType.Category);
                if (tn != null)
                {
                    guiMain.DeleteCategory(tn);
                    SubscriptionModified(NewsFeedProperty.FeedCategoryRemoved);
                }
            });
        }
        private void OnUpdateFeedsStarted(object sender, FeedSource.UpdateFeedsEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                if (e.ForcedRefresh)
                                    stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshAllForced);
                                else
                                    stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshAllAuto);
                            });
        }
        private void BeforeDownloadFeedStarted(object sender, FeedSource.DownloadFeedCancelEventArgs e)
        {
            InvokeOnGui(
                delegate
                    {
                        bool cancel = e.Cancel;
                        guiMain.OnFeedUpdateStart(e.FeedUri, ref cancel);
                        e.Cancel = cancel;
                    });
        }
        internal void OnUpdateFeedStarted(object sender, FeedSource.UpdateFeedEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOne);
                            });
        }
        internal void OnUpdatedFeed(object sender, FeedSource.UpdatedFeedEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                guiMain.UpdateFeed(e.UpdatedFeedUri, e.NewFeedUri, e.UpdateState == RequestResult.OK);
                                if (e.FirstSuccessfulDownload)
                                {
                                    SubscriptionModified(NewsFeedProperty.FeedCacheUrl);
                                }
                                stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOneDone);
                            });
        }
        internal void OnUpdatedCommentFeed(object sender, FeedSource.UpdatedFeedEventArgs e)
        {
            if (e.UpdateState == RequestResult.OK)
            {
                InvokeOnGui(delegate
                                {
                                    guiMain.UpdateCommentFeed(e.UpdatedFeedUri, e.NewFeedUri);
                                });
            }
        }
        internal void OnUpdatedFavicon(object sender, FeedSource.UpdatedFaviconEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                guiMain.UpdateFavicon(e.Favicon, e.FeedUrls);
                            });
        }
        internal void OnDownloadedEnclosure(object sender, DownloadItemEventArgs e)
        {
            if (Preferences.AddPodcasts2WMP)
            {
                AddPodcastToWMP(e.DownloadItem);
            }
            if (Preferences.AddPodcasts2ITunes)
            {
                AddPodcastToITunes(e.DownloadItem);
            }
            InvokeOnGui(delegate
                            {
                                guiMain.OnEnclosureReceived(sender, e);
                            });
        }
        internal void OnUpdateFeedException(object sender, FeedSource.UpdateFeedExceptionEventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                WebException webex = e.ExceptionThrown as WebException;
                                if (webex != null)
                                {
                                    if (webex.Status == WebExceptionStatus.NameResolutionFailure ||
                                        webex.Status == WebExceptionStatus.ProxyNameResolutionFailure)
                                    {
                                        UpdateInternetConnectionState(true);
                                        if (!InternetAccessAllowed)
                                        {
                                            guiMain.UpdateFeed(e.FeedUri, null, false);
                                            stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOneDone);
                                            return;
                                        }
                                    }
                                }
                                Trace.WriteLine(e.ExceptionThrown.StackTrace);
                                UpdateXmlFeedErrorFeed(e.ExceptionThrown, e.FeedUri, true);
                                stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshOneDone);
                            });
        }
        private void OnAllCommentFeedRequestsCompleted(object sender, EventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                guiMain.OnAllAsyncUpdateCommentFeedsFinished();
                            });
        }
        private void OnAllRequestsCompleted(object sender, EventArgs e)
        {
            InvokeOnGui(delegate
                            {
                                stateManager.MoveNewsHandlerStateTo(NewsHandlerState.RefreshAllDone);
                                guiMain.TriggerGUIStateOnNewFeeds(true);
                                guiMain.OnAllAsyncUpdateFeedsFinished();
                            });
        }
        private void OnLoadingFeedlistProgress(object sender, ThreadWorkerProgressArgs args)
        {
            if (args.Exception != null)
            {
                args.Cancel = true;
                BanditApplicationException ex = args.Exception as BanditApplicationException;
                if (ex != null)
                {
                    if (ex.Number == ApplicationExceptions.FeedlistOldFormat)
                    {
                        Application.Exit();
                    }
                    else if (ex.Number == ApplicationExceptions.FeedlistOnRead)
                    {
                        ExceptionManager.Publish(ex.InnerException);
                        this.MessageError(SR.ExceptionReadingFeedlistFile(ex.InnerException.Message, GetLogFileName()));
                        this.SetGuiStateFeedbackText(SR.GUIStatusErrorReadingFeedlistFile);
                    }
                    else if (ex.Number == ApplicationExceptions.FeedlistOnProcessContent)
                    {
                        this.MessageError(SR.InvalidFeedlistFileMessage(GetLogFileName()));
                        this.SetGuiStateFeedbackText(SR.GUIStatusValidationErrorReadingFeedlistFile);
                    }
                    else if (ex.Number == ApplicationExceptions.FeedlistNA)
                    {
                        this.refreshRate = feedHandler.RefreshRate;
                        this.SetGuiStateFeedbackText(SR.GUIStatusNoFeedlistFile);
                    }
                    else
                    {
                        PublishException(args.Exception);
                        this.SetGuiStateFeedbackText(SR.GUIStatusErrorReadingFeedlistFile);
                    }
                }
                else
                {
                    PublishException(args.Exception);
                    this.SetGuiStateFeedbackText(SR.GUIStatusErrorReadingFeedlistFile);
                }
            }
            else if (!args.Done)
            {
                if (!IsFormAvailable(guiMain))
                {
                    args.Cancel = true;
                    return;
                }
                this.SetGuiStateFeedbackText(SR.GUIStatusLoadingFeedlist);
            }
            else if (args.Done)
            {
                this.refreshRate = feedHandler.RefreshRate;
                this.CheckAndMigrateSettingsAndPreferences();
                this.CheckAndMigrateListViewLayouts();
                this.feedHandler.ResumePendingDownloads();
                if (!IsFormAvailable(guiMain))
                {
                    args.Cancel = true;
                    return;
                }
                try
                {
                    this.guiMain.PopulateFeedSubscriptions(feedHandler.GetCategories().Values, feedHandler.GetFeeds(),
                                                           DefaultCategory);
                }
                catch (Exception ex)
                {
                    PublishException(ex);
                }
                if (FeedlistLoaded != null)
                    FeedlistLoaded(this, EventArgs.Empty);
                this.SetGuiStateFeedbackText(SR.GUIStatusDone);
                foreach (string newFeedUrl in this.commandLineOptions.SubscribeTo)
                {
                    if (IsFormAvailable(guiMain))
                        this.guiMain.AddFeedUrlSynchronized(newFeedUrl);
                }
                guiMain.UpdateAllFeeds(this.Preferences.FeedRefreshOnStartup);
            }
        }
    }
}
