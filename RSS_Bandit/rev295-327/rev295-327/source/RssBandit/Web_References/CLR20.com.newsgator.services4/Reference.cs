namespace  RssBandit.CLR20.com.newsgator.services4 {
	
    using System.Diagnostics; 
    using System.Web.Services; 
    using System.ComponentModel; 
    using System.Web.Services.Protocols; 
    using System; 
    using System.Xml.Serialization; 
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Web.Services.WebServiceBindingAttribute(Name="PostItemSoap", Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  PostItem  : System.Web.Services.Protocols.SoapHttpClientProtocol {
		
        private  NGImpersonate nGImpersonateValueField;
 
        private  NGAPIToken nGAPITokenValueField;
 
        private  System.Threading.SendOrPostCallback SetStateOperationCompleted;
 
        private  System.Threading.SendOrPostCallback UpdatePostMetadataOperationCompleted;
 
        private  System.Threading.SendOrPostCallback UpdatePostMetadatav2OperationCompleted;
 
        private  System.Threading.SendOrPostCallback RatePostOperationCompleted;
 
        private  System.Threading.SendOrPostCallback GetLinkCountOperationCompleted;
 
        private  System.Threading.SendOrPostCallback GetLinksOperationCompleted;
 
        private  System.Threading.SendOrPostCallback GetPostIdOperationCompleted;
 
        private  System.Threading.SendOrPostCallback ClipPostsOperationCompleted;
 
        private  System.Threading.SendOrPostCallback ClipItemsOperationCompleted;
 
        private  System.Threading.SendOrPostCallback UnClipPostsOperationCompleted;
 
        private  System.Threading.SendOrPostCallback GetItemsOperationCompleted;
 
        private  System.Threading.SendOrPostCallback GetHighlyViewedPostsOperationCompleted;
 
        private  System.Threading.SendOrPostCallback SendPostOperationCompleted;
 
        private  bool useDefaultCredentialsSetExplicitly;
 
        public  PostItem() {
            this.Url = "http://services.newsgator.com/ngws/svc/PostItem.asmx";
            if ((this.IsLocalFileSystemWebService(this.Url) == true)) {
                this.UseDefaultCredentials = true;
                this.useDefaultCredentialsSetExplicitly = false;
            }
            else {
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
 
        public  NGImpersonate NGImpersonateValue {
            get {
                return this.nGImpersonateValueField;
            }
            set {
                this.nGImpersonateValueField = value;
            }
        }
 
        public  NGAPIToken NGAPITokenValue {
            get {
                return this.nGAPITokenValueField;
            }
            set {
                this.nGAPITokenValueField = value;
            }
        }
 
        public new  string Url {
            get {
                return base.Url;
            }
            set {
                if ((((this.IsLocalFileSystemWebService(base.Url) == true)
                            && (this.useDefaultCredentialsSetExplicitly == false))
                            && (this.IsLocalFileSystemWebService(value) == false))) {
                    base.UseDefaultCredentials = false;
                }
                base.Url = value;
            }
        }
 
        public new  bool UseDefaultCredentials {
            get {
                return base.UseDefaultCredentials;
            }
            set {
                base.UseDefaultCredentials = value;
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
 
        public  event SetStateCompletedEventHandler SetStateCompleted; 
        public  event UpdatePostMetadataCompletedEventHandler UpdatePostMetadataCompleted; 
        public  event UpdatePostMetadatav2CompletedEventHandler UpdatePostMetadatav2Completed; 
        public  event RatePostCompletedEventHandler RatePostCompleted; 
        public  event GetLinkCountCompletedEventHandler GetLinkCountCompleted; 
        public  event GetLinksCompletedEventHandler GetLinksCompleted; 
        public  event GetPostIdCompletedEventHandler GetPostIdCompleted; 
        public  event ClipPostsCompletedEventHandler ClipPostsCompleted; 
        public  event ClipItemsCompletedEventHandler ClipItemsCompleted; 
        public  event UnClipPostsCompletedEventHandler UnClipPostsCompleted; 
        public  event GetItemsCompletedEventHandler GetItemsCompleted; 
        public  event GetHighlyViewedPostsCompletedEventHandler GetHighlyViewedPostsCompleted; 
        public  event SendPostCompletedEventHandler SendPostCompleted; 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/SetState", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  PostState[] SetState(string locationName, string[] deletedPosts, string[] readPosts, string[] unreadPosts) {
            object[] results = this.Invoke("SetState", new object[] {
                        locationName,
                        deletedPosts,
                        readPosts,
                        unreadPosts});
            return ((PostState[])(results[0]));
        }
 
        public  System.IAsyncResult BeginSetState(string locationName, string[] deletedPosts, string[] readPosts, string[] unreadPosts, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("SetState", new object[] {
                        locationName,
                        deletedPosts,
                        readPosts,
                        unreadPosts}, callback, asyncState);
        }
 
        public  PostState[] EndSetState(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((PostState[])(results[0]));
        }
 
        public  void SetStateAsync(string locationName, string[] deletedPosts, string[] readPosts, string[] unreadPosts) {
            this.SetStateAsync(locationName, deletedPosts, readPosts, unreadPosts, null);
        }
 
        public  void SetStateAsync(string locationName, string[] deletedPosts, string[] readPosts, string[] unreadPosts, object userState) {
            if ((this.SetStateOperationCompleted == null)) {
                this.SetStateOperationCompleted = new System.Threading.SendOrPostCallback(this.OnSetStateOperationCompleted);
            }
            this.InvokeAsync("SetState", new object[] {
                        locationName,
                        deletedPosts,
                        readPosts,
                        unreadPosts}, this.SetStateOperationCompleted, userState);
        }
 
        private  void OnSetStateOperationCompleted(object arg) {
            if ((this.SetStateCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.SetStateCompleted(this, new SetStateCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/UpdatePostMetadata", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  FeedMetadata[] UpdatePostMetadata(string locationName, string syncToken, bool unreadOnly, FeedMetadata[] newStates, bool updateRelevanceScores) {
            object[] results = this.Invoke("UpdatePostMetadata", new object[] {
                        locationName,
                        syncToken,
                        unreadOnly,
                        newStates,
                        updateRelevanceScores});
            return ((FeedMetadata[])(results[0]));
        }
 
        public  System.IAsyncResult BeginUpdatePostMetadata(string locationName, string syncToken, bool unreadOnly, FeedMetadata[] newStates, bool updateRelevanceScores, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("UpdatePostMetadata", new object[] {
                        locationName,
                        syncToken,
                        unreadOnly,
                        newStates,
                        updateRelevanceScores}, callback, asyncState);
        }
 
        public  FeedMetadata[] EndUpdatePostMetadata(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((FeedMetadata[])(results[0]));
        }
 
        public  void UpdatePostMetadataAsync(string locationName, string syncToken, bool unreadOnly, FeedMetadata[] newStates, bool updateRelevanceScores) {
            this.UpdatePostMetadataAsync(locationName, syncToken, unreadOnly, newStates, updateRelevanceScores, null);
        }
 
        public  void UpdatePostMetadataAsync(string locationName, string syncToken, bool unreadOnly, FeedMetadata[] newStates, bool updateRelevanceScores, object userState) {
            if ((this.UpdatePostMetadataOperationCompleted == null)) {
                this.UpdatePostMetadataOperationCompleted = new System.Threading.SendOrPostCallback(this.OnUpdatePostMetadataOperationCompleted);
            }
            this.InvokeAsync("UpdatePostMetadata", new object[] {
                        locationName,
                        syncToken,
                        unreadOnly,
                        newStates,
                        updateRelevanceScores}, this.UpdatePostMetadataOperationCompleted, userState);
        }
 
        private  void OnUpdatePostMetadataOperationCompleted(object arg) {
            if ((this.UpdatePostMetadataCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.UpdatePostMetadataCompleted(this, new UpdatePostMetadataCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/UpdatePostMetadatav2", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  FeedMetadata[] UpdatePostMetadatav2(string locationName, string syncToken, FeedMetadata[] newStates, bool updateRelevanceScores) {
            object[] results = this.Invoke("UpdatePostMetadatav2", new object[] {
                        locationName,
                        syncToken,
                        newStates,
                        updateRelevanceScores});
            return ((FeedMetadata[])(results[0]));
        }
 
        public  System.IAsyncResult BeginUpdatePostMetadatav2(string locationName, string syncToken, FeedMetadata[] newStates, bool updateRelevanceScores, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("UpdatePostMetadatav2", new object[] {
                        locationName,
                        syncToken,
                        newStates,
                        updateRelevanceScores}, callback, asyncState);
        }
 
        public  FeedMetadata[] EndUpdatePostMetadatav2(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((FeedMetadata[])(results[0]));
        }
 
        public  void UpdatePostMetadatav2Async(string locationName, string syncToken, FeedMetadata[] newStates, bool updateRelevanceScores) {
            this.UpdatePostMetadatav2Async(locationName, syncToken, newStates, updateRelevanceScores, null);
        }
 
        public  void UpdatePostMetadatav2Async(string locationName, string syncToken, FeedMetadata[] newStates, bool updateRelevanceScores, object userState) {
            if ((this.UpdatePostMetadatav2OperationCompleted == null)) {
                this.UpdatePostMetadatav2OperationCompleted = new System.Threading.SendOrPostCallback(this.OnUpdatePostMetadatav2OperationCompleted);
            }
            this.InvokeAsync("UpdatePostMetadatav2", new object[] {
                        locationName,
                        syncToken,
                        newStates,
                        updateRelevanceScores}, this.UpdatePostMetadatav2OperationCompleted, userState);
        }
 
        private  void OnUpdatePostMetadatav2OperationCompleted(object arg) {
            if ((this.UpdatePostMetadatav2Completed != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.UpdatePostMetadatav2Completed(this, new UpdatePostMetadatav2CompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/RatePost", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  void RatePost(string url, int rating) {
            this.Invoke("RatePost", new object[] {
                        url,
                        rating});
        }
 
        public  System.IAsyncResult BeginRatePost(string url, int rating, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("RatePost", new object[] {
                        url,
                        rating}, callback, asyncState);
        }
 
        public  void EndRatePost(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
 
        public  void RatePostAsync(string url, int rating) {
            this.RatePostAsync(url, rating, null);
        }
 
        public  void RatePostAsync(string url, int rating, object userState) {
            if ((this.RatePostOperationCompleted == null)) {
                this.RatePostOperationCompleted = new System.Threading.SendOrPostCallback(this.OnRatePostOperationCompleted);
            }
            this.InvokeAsync("RatePost", new object[] {
                        url,
                        rating}, this.RatePostOperationCompleted, userState);
        }
 
        private  void OnRatePostOperationCompleted(object arg) {
            if ((this.RatePostCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.RatePostCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/GetLinkCount", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  int GetLinkCount(string postId) {
            object[] results = this.Invoke("GetLinkCount", new object[] {
                        postId});
            return ((int)(results[0]));
        }
 
        public  System.IAsyncResult BeginGetLinkCount(string postId, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetLinkCount", new object[] {
                        postId}, callback, asyncState);
        }
 
        public  int EndGetLinkCount(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((int)(results[0]));
        }
 
        public  void GetLinkCountAsync(string postId) {
            this.GetLinkCountAsync(postId, null);
        }
 
        public  void GetLinkCountAsync(string postId, object userState) {
            if ((this.GetLinkCountOperationCompleted == null)) {
                this.GetLinkCountOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetLinkCountOperationCompleted);
            }
            this.InvokeAsync("GetLinkCount", new object[] {
                        postId}, this.GetLinkCountOperationCompleted, userState);
        }
 
        private  void OnGetLinkCountOperationCompleted(object arg) {
            if ((this.GetLinkCountCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetLinkCountCompleted(this, new GetLinkCountCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/GetLinks", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  string[] GetLinks(string postId) {
            object[] results = this.Invoke("GetLinks", new object[] {
                        postId});
            return ((string[])(results[0]));
        }
 
        public  System.IAsyncResult BeginGetLinks(string postId, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetLinks", new object[] {
                        postId}, callback, asyncState);
        }
 
        public  string[] EndGetLinks(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((string[])(results[0]));
        }
 
        public  void GetLinksAsync(string postId) {
            this.GetLinksAsync(postId, null);
        }
 
        public  void GetLinksAsync(string postId, object userState) {
            if ((this.GetLinksOperationCompleted == null)) {
                this.GetLinksOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetLinksOperationCompleted);
            }
            this.InvokeAsync("GetLinks", new object[] {
                        postId}, this.GetLinksOperationCompleted, userState);
        }
 
        private  void OnGetLinksOperationCompleted(object arg) {
            if ((this.GetLinksCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetLinksCompleted(this, new GetLinksCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/GetPostId", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  string GetPostId(string url) {
            object[] results = this.Invoke("GetPostId", new object[] {
                        url});
            return ((string)(results[0]));
        }
 
        public  System.IAsyncResult BeginGetPostId(string url, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetPostId", new object[] {
                        url}, callback, asyncState);
        }
 
        public  string EndGetPostId(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((string)(results[0]));
        }
 
        public  void GetPostIdAsync(string url) {
            this.GetPostIdAsync(url, null);
        }
 
        public  void GetPostIdAsync(string url, object userState) {
            if ((this.GetPostIdOperationCompleted == null)) {
                this.GetPostIdOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetPostIdOperationCompleted);
            }
            this.InvokeAsync("GetPostId", new object[] {
                        url}, this.GetPostIdOperationCompleted, userState);
        }
 
        private  void OnGetPostIdOperationCompleted(object arg) {
            if ((this.GetPostIdCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetPostIdCompleted(this, new GetPostIdCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/ClipPosts", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  void ClipPosts(ClippedPost[] clipList) {
            this.Invoke("ClipPosts", new object[] {
                        clipList});
        }
 
        public  System.IAsyncResult BeginClipPosts(ClippedPost[] clipList, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("ClipPosts", new object[] {
                        clipList}, callback, asyncState);
        }
 
        public  void EndClipPosts(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
 
        public  void ClipPostsAsync(ClippedPost[] clipList) {
            this.ClipPostsAsync(clipList, null);
        }
 
        public  void ClipPostsAsync(ClippedPost[] clipList, object userState) {
            if ((this.ClipPostsOperationCompleted == null)) {
                this.ClipPostsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnClipPostsOperationCompleted);
            }
            this.InvokeAsync("ClipPosts", new object[] {
                        clipList}, this.ClipPostsOperationCompleted, userState);
        }
 
        private  void OnClipPostsOperationCompleted(object arg) {
            if ((this.ClipPostsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.ClipPostsCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/ClipItems", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  ClippedItem[] ClipItems(ClippedItem[] clipList) {
            object[] results = this.Invoke("ClipItems", new object[] {
                        clipList});
            return ((ClippedItem[])(results[0]));
        }
 
        public  System.IAsyncResult BeginClipItems(ClippedItem[] clipList, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("ClipItems", new object[] {
                        clipList}, callback, asyncState);
        }
 
        public  ClippedItem[] EndClipItems(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((ClippedItem[])(results[0]));
        }
 
        public  void ClipItemsAsync(ClippedItem[] clipList) {
            this.ClipItemsAsync(clipList, null);
        }
 
        public  void ClipItemsAsync(ClippedItem[] clipList, object userState) {
            if ((this.ClipItemsOperationCompleted == null)) {
                this.ClipItemsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnClipItemsOperationCompleted);
            }
            this.InvokeAsync("ClipItems", new object[] {
                        clipList}, this.ClipItemsOperationCompleted, userState);
        }
 
        private  void OnClipItemsOperationCompleted(object arg) {
            if ((this.ClipItemsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.ClipItemsCompleted(this, new ClipItemsCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/UnClipPosts", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  void UnClipPosts(string[] postIds) {
            this.Invoke("UnClipPosts", new object[] {
                        postIds});
        }
 
        public  System.IAsyncResult BeginUnClipPosts(string[] postIds, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("UnClipPosts", new object[] {
                        postIds}, callback, asyncState);
        }
 
        public  void EndUnClipPosts(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
 
        public  void UnClipPostsAsync(string[] postIds) {
            this.UnClipPostsAsync(postIds, null);
        }
 
        public  void UnClipPostsAsync(string[] postIds, object userState) {
            if ((this.UnClipPostsOperationCompleted == null)) {
                this.UnClipPostsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnUnClipPostsOperationCompleted);
            }
            this.InvokeAsync("UnClipPosts", new object[] {
                        postIds}, this.UnClipPostsOperationCompleted, userState);
        }
 
        private  void OnUnClipPostsOperationCompleted(object arg) {
            if ((this.UnClipPostsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.UnClipPostsCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/GetItems", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)]
        [return: System.Xml.Serialization.XmlElementAttribute(Namespace="")] 
        public  rss GetItems(string[] postIds) {
            object[] results = this.Invoke("GetItems", new object[] {
                        postIds});
            return ((rss)(results[0]));
        }
 
        public  System.IAsyncResult BeginGetItems(string[] postIds, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetItems", new object[] {
                        postIds}, callback, asyncState);
        }
 
        public  rss EndGetItems(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((rss)(results[0]));
        }
 
        public  void GetItemsAsync(string[] postIds) {
            this.GetItemsAsync(postIds, null);
        }
 
        public  void GetItemsAsync(string[] postIds, object userState) {
            if ((this.GetItemsOperationCompleted == null)) {
                this.GetItemsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetItemsOperationCompleted);
            }
            this.InvokeAsync("GetItems", new object[] {
                        postIds}, this.GetItemsOperationCompleted, userState);
        }
 
        private  void OnGetItemsOperationCompleted(object arg) {
            if ((this.GetItemsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetItemsCompleted(this, new GetItemsCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/GetHighlyViewedPosts", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  System.Xml.XmlElement GetHighlyViewedPosts(int maxPosts, System.DateTime fromDate, System.DateTime toDate, bool includeDescription) {
            object[] results = this.Invoke("GetHighlyViewedPosts", new object[] {
                        maxPosts,
                        fromDate,
                        toDate,
                        includeDescription});
            return ((System.Xml.XmlElement)(results[0]));
        }
 
        public  System.IAsyncResult BeginGetHighlyViewedPosts(int maxPosts, System.DateTime fromDate, System.DateTime toDate, bool includeDescription, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetHighlyViewedPosts", new object[] {
                        maxPosts,
                        fromDate,
                        toDate,
                        includeDescription}, callback, asyncState);
        }
 
        public  System.Xml.XmlElement EndGetHighlyViewedPosts(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((System.Xml.XmlElement)(results[0]));
        }
 
        public  void GetHighlyViewedPostsAsync(int maxPosts, System.DateTime fromDate, System.DateTime toDate, bool includeDescription) {
            this.GetHighlyViewedPostsAsync(maxPosts, fromDate, toDate, includeDescription, null);
        }
 
        public  void GetHighlyViewedPostsAsync(int maxPosts, System.DateTime fromDate, System.DateTime toDate, bool includeDescription, object userState) {
            if ((this.GetHighlyViewedPostsOperationCompleted == null)) {
                this.GetHighlyViewedPostsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetHighlyViewedPostsOperationCompleted);
            }
            this.InvokeAsync("GetHighlyViewedPosts", new object[] {
                        maxPosts,
                        fromDate,
                        toDate,
                        includeDescription}, this.GetHighlyViewedPostsOperationCompleted, userState);
        }
 
        private  void OnGetHighlyViewedPostsOperationCompleted(object arg) {
            if ((this.GetHighlyViewedPostsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetHighlyViewedPostsCompleted(this, new GetHighlyViewedPostsCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGAPITokenValue")]
        [System.Web.Services.Protocols.SoapHeaderAttribute("NGImpersonateValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("http://services.newsgator.com/ngws/svc/PostItem.asmx/SendPost", RequestNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", ResponseNamespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Wrapped)] 
        public  void SendPost(long postId, string subject, string comments, string address, string protocol) {
            this.Invoke("SendPost", new object[] {
                        postId,
                        subject,
                        comments,
                        address,
                        protocol});
        }
 
        public  System.IAsyncResult BeginSendPost(long postId, string subject, string comments, string address, string protocol, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("SendPost", new object[] {
                        postId,
                        subject,
                        comments,
                        address,
                        protocol}, callback, asyncState);
        }
 
        public  void EndSendPost(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
 
        public  void SendPostAsync(long postId, string subject, string comments, string address, string protocol) {
            this.SendPostAsync(postId, subject, comments, address, protocol, null);
        }
 
        public  void SendPostAsync(long postId, string subject, string comments, string address, string protocol, object userState) {
            if ((this.SendPostOperationCompleted == null)) {
                this.SendPostOperationCompleted = new System.Threading.SendOrPostCallback(this.OnSendPostOperationCompleted);
            }
            this.InvokeAsync("SendPost", new object[] {
                        postId,
                        subject,
                        comments,
                        address,
                        protocol}, this.SendPostOperationCompleted, userState);
        }
 
        private  void OnSendPostOperationCompleted(object arg) {
            if ((this.SendPostCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.SendPostCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
 
        public new  void CancelAsync(object userState) {
            base.CancelAsync(userState);
        }
 
        private  bool IsLocalFileSystemWebService(string url) {
            if (((url == null)
                        || (url == string.Empty))) {
                return false;
            }
            System.Uri wsUri = new System.Uri(url);
            if (((wsUri.Port >= 1024)
                        && (string.Compare(wsUri.Host, "localHost", System.StringComparison.OrdinalIgnoreCase) == 0))) {
                return true;
            }
            return false;
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx", IsNullable=false)] 
    public partial class  NGAPIToken  : System.Web.Services.Protocols.SoapHeader {
		
        private  string tokenField;
 
        private  bool enableCompressionField;
 
        private  System.Xml.XmlAttribute[] anyAttrField;
 
        public  string Token {
            get {
                return this.tokenField;
            }
            set {
                this.tokenField = value;
            }
        }
 
        public  bool EnableCompression {
            get {
                return this.enableCompressionField;
            }
            set {
                this.enableCompressionField = value;
            }
        }
 
        [System.Xml.Serialization.XmlAnyAttributeAttribute()] 
        public  System.Xml.XmlAttribute[] AnyAttr {
            get {
                return this.anyAttrField;
            }
            set {
                this.anyAttrField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  rssChannelItemEnclosure {
		
        private  string urlField;
 
        private  string typeField;
 
        [System.Xml.Serialization.XmlAttributeAttribute()] 
        public  string url {
            get {
                return this.urlField;
            }
            set {
                this.urlField = value;
            }
        }
 
        [System.Xml.Serialization.XmlAttributeAttribute()] 
        public  string type {
            get {
                return this.typeField;
            }
            set {
                this.typeField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  rssChannelItemGuid {
		
        private  bool isPermaLinkField;
 
        private  string valueField;
 
        public  rssChannelItemGuid() {
            this.isPermaLinkField = true;
        }
 
        [System.Xml.Serialization.XmlAttributeAttribute()]
        [System.ComponentModel.DefaultValueAttribute(true)] 
        public  bool isPermaLink {
            get {
                return this.isPermaLinkField;
            }
            set {
                this.isPermaLinkField = value;
            }
        }
 
        [System.Xml.Serialization.XmlTextAttribute()] 
        public  string Value {
            get {
                return this.valueField;
            }
            set {
                this.valueField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  rssChannelItem {
		
        private  object titleField;
 
        private  object linkField;
 
        private  object descriptionField;
 
        private  object pubDateField;
 
        private  rssChannelItemGuid guidField;
 
        private  string postIdField;
 
        private  bool readField;
 
        private  bool readFieldSpecified;
 
        private  bool deletedField;
 
        private  bool deletedFieldSpecified;
 
        private  string commentRssField;
 
        private  string commentField;
 
        private  string commentsField;
 
        private  string authorField;
 
        private  rssChannelItemEnclosure enclosureField;
 
        private  string userRatingField;
 
        private  decimal avgRatingField;
 
        private  string folderIdField;
 
        private  string feedIdField;
 
        private  string extensionDataField;
 
        private  string sourceField;
 
        public  rssChannelItem() {
            this.readField = false;
            this.deletedField = false;
        }
 
        public  object title {
            get {
                return this.titleField;
            }
            set {
                this.titleField = value;
            }
        }
 
        public  object link {
            get {
                return this.linkField;
            }
            set {
                this.linkField = value;
            }
        }
 
        public  object description {
            get {
                return this.descriptionField;
            }
            set {
                this.descriptionField = value;
            }
        }
 
        public  object pubDate {
            get {
                return this.pubDateField;
            }
            set {
                this.pubDateField = value;
            }
        }
 
        public  rssChannelItemGuid guid {
            get {
                return this.guidField;
            }
            set {
                this.guidField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  string postId {
            get {
                return this.postIdField;
            }
            set {
                this.postIdField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  bool read {
            get {
                return this.readField;
            }
            set {
                this.readField = value;
            }
        }
 
        [System.Xml.Serialization.XmlIgnoreAttribute()] 
        public  bool readSpecified {
            get {
                return this.readFieldSpecified;
            }
            set {
                this.readFieldSpecified = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  bool deleted {
            get {
                return this.deletedField;
            }
            set {
                this.deletedField = value;
            }
        }
 
        [System.Xml.Serialization.XmlIgnoreAttribute()] 
        public  bool deletedSpecified {
            get {
                return this.deletedFieldSpecified;
            }
            set {
                this.deletedFieldSpecified = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://wellformedweb.org/CommentAPI/")] 
        public  string commentRss {
            get {
                return this.commentRssField;
            }
            set {
                this.commentRssField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://wellformedweb.org/CommentAPI/")] 
        public  string comment {
            get {
                return this.commentField;
            }
            set {
                this.commentField = value;
            }
        }
 
        public  string comments {
            get {
                return this.commentsField;
            }
            set {
                this.commentsField = value;
            }
        }
 
        public  string author {
            get {
                return this.authorField;
            }
            set {
                this.authorField = value;
            }
        }
 
        public  rssChannelItemEnclosure enclosure {
            get {
                return this.enclosureField;
            }
            set {
                this.enclosureField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions", DataType="integer")] 
        public  string userRating {
            get {
                return this.userRatingField;
            }
            set {
                this.userRatingField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  decimal avgRating {
            get {
                return this.avgRatingField;
            }
            set {
                this.avgRatingField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions", DataType="integer")] 
        public  string folderId {
            get {
                return this.folderIdField;
            }
            set {
                this.folderIdField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions", DataType="integer")] 
        public  string feedId {
            get {
                return this.feedIdField;
            }
            set {
                this.feedIdField = value;
            }
        }
 
        public  string extensionData {
            get {
                return this.extensionDataField;
            }
            set {
                this.extensionDataField = value;
            }
        }
 
        public  string source {
            get {
                return this.sourceField;
            }
            set {
                this.sourceField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  rssChannel {
		
        private  string titleField;
 
        private  string linkField;
 
        private  string copyrightField;
 
        private  string descriptionField;
 
        private  object webMasterField;
 
        private  object lastBuildDateField;
 
        private  string tokenField;
 
        private  string idField;
 
        private  string ttlField;
 
        private  rssChannelItem[] itemField;
 
        public  string title {
            get {
                return this.titleField;
            }
            set {
                this.titleField = value;
            }
        }
 
        public  string link {
            get {
                return this.linkField;
            }
            set {
                this.linkField = value;
            }
        }
 
        public  string copyright {
            get {
                return this.copyrightField;
            }
            set {
                this.copyrightField = value;
            }
        }
 
        public  string description {
            get {
                return this.descriptionField;
            }
            set {
                this.descriptionField = value;
            }
        }
 
        public  object webMaster {
            get {
                return this.webMasterField;
            }
            set {
                this.webMasterField = value;
            }
        }
 
        public  object lastBuildDate {
            get {
                return this.lastBuildDateField;
            }
            set {
                this.lastBuildDateField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  string token {
            get {
                return this.tokenField;
            }
            set {
                this.tokenField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(Namespace="http://newsgator.com/schema/extensions")] 
        public  string id {
            get {
                return this.idField;
            }
            set {
                this.idField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute(DataType="nonNegativeInteger")] 
        public  string ttl {
            get {
                return this.ttlField;
            }
            set {
                this.ttlField = value;
            }
        }
 
        [System.Xml.Serialization.XmlElementAttribute("item")] 
        public  rssChannelItem[] item {
            get {
                return this.itemField;
            }
            set {
                this.itemField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  rss {
		
        private  rssChannel channelField;
 
        private  string versionField;
 
        private  string tokenField;
 
        public  rssChannel channel {
            get {
                return this.channelField;
            }
            set {
                this.channelField = value;
            }
        }
 
        [System.Xml.Serialization.XmlAttributeAttribute()] 
        public  string version {
            get {
                return this.versionField;
            }
            set {
                this.versionField = value;
            }
        }
 
        [System.Xml.Serialization.XmlAttributeAttribute(Form=System.Xml.Schema.XmlSchemaForm.Qualified, Namespace="http://newsgator.com/schema/extensions")] 
        public  string token {
            get {
                return this.tokenField;
            }
            set {
                this.tokenField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  ClippedItem {
		
        private  string postIdField;
 
        private  string urlField;
 
        private  string titleField;
 
        private  int folderIdField;
 
        public  string PostId {
            get {
                return this.postIdField;
            }
            set {
                this.postIdField = value;
            }
        }
 
        public  string Url {
            get {
                return this.urlField;
            }
            set {
                this.urlField = value;
            }
        }
 
        public  string Title {
            get {
                return this.titleField;
            }
            set {
                this.titleField = value;
            }
        }
 
        public  int FolderId {
            get {
                return this.folderIdField;
            }
            set {
                this.folderIdField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  ClippedPost {
		
        private  string postIdField;
 
        private  int folderIdField;
 
        private  bool deletedField;
 
        private  System.DateTime lastModifiedField;
 
        public  string PostId {
            get {
                return this.postIdField;
            }
            set {
                this.postIdField = value;
            }
        }
 
        public  int FolderId {
            get {
                return this.folderIdField;
            }
            set {
                this.folderIdField = value;
            }
        }
 
        public  bool Deleted {
            get {
                return this.deletedField;
            }
            set {
                this.deletedField = value;
            }
        }
 
        public  System.DateTime LastModified {
            get {
                return this.lastModifiedField;
            }
            set {
                this.lastModifiedField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  PostMetadata {
		
        private  string postIDField;
 
        private  int stateField;
 
        private  bool stateFieldSpecified;
 
        private  int flagStateField;
 
        private  bool flagStateFieldSpecified;
 
        private  string userActionsField;
 
        public  string PostID {
            get {
                return this.postIDField;
            }
            set {
                this.postIDField = value;
            }
        }
 
        public  int State {
            get {
                return this.stateField;
            }
            set {
                this.stateField = value;
            }
        }
 
        [System.Xml.Serialization.XmlIgnoreAttribute()] 
        public  bool StateSpecified {
            get {
                return this.stateFieldSpecified;
            }
            set {
                this.stateFieldSpecified = value;
            }
        }
 
        public  int FlagState {
            get {
                return this.flagStateField;
            }
            set {
                this.flagStateField = value;
            }
        }
 
        [System.Xml.Serialization.XmlIgnoreAttribute()] 
        public  bool FlagStateSpecified {
            get {
                return this.flagStateFieldSpecified;
            }
            set {
                this.flagStateFieldSpecified = value;
            }
        }
 
        public  string UserActions {
            get {
                return this.userActionsField;
            }
            set {
                this.userActionsField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  FeedMetadata {
		
        private  string feedIDField;
 
        private  string userActionsField;
 
        private  PostMetadata[] postMetadataField;
 
        public  string FeedID {
            get {
                return this.feedIDField;
            }
            set {
                this.feedIDField = value;
            }
        }
 
        public  string UserActions {
            get {
                return this.userActionsField;
            }
            set {
                this.userActionsField = value;
            }
        }
 
        public  PostMetadata[] PostMetadata {
            get {
                return this.postMetadataField;
            }
            set {
                this.postMetadataField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/svc/PostItem.asmx")] 
    public partial class  PostState {
		
        private  string postIDField;
 
        private  int stateField;
 
        private  string feedIDField;
 
        private  int flagStateField;
 
        private  bool flagStateFieldSpecified;
 
        private  string userActionsField;
 
        public  string PostID {
            get {
                return this.postIDField;
            }
            set {
                this.postIDField = value;
            }
        }
 
        public  int State {
            get {
                return this.stateField;
            }
            set {
                this.stateField = value;
            }
        }
 
        public  string FeedID {
            get {
                return this.feedIDField;
            }
            set {
                this.feedIDField = value;
            }
        }
 
        public  int FlagState {
            get {
                return this.flagStateField;
            }
            set {
                this.flagStateField = value;
            }
        }
 
        [System.Xml.Serialization.XmlIgnoreAttribute()] 
        public  bool FlagStateSpecified {
            get {
                return this.flagStateFieldSpecified;
            }
            set {
                this.flagStateFieldSpecified = value;
            }
        }
 
        public  string UserActions {
            get {
                return this.userActionsField;
            }
            set {
                this.userActionsField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="http://services.newsgator.com/ngws/Impersonate")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="http://services.newsgator.com/ngws/Impersonate", IsNullable=false)] 
    public partial class  NGImpersonate  : System.Web.Services.Protocols.SoapHeader {
		
        private  string impersonateUserField;
 
        private  System.Xml.XmlAttribute[] anyAttrField;
 
        public  string ImpersonateUser {
            get {
                return this.impersonateUserField;
            }
            set {
                this.impersonateUserField = value;
            }
        }
 
        [System.Xml.Serialization.XmlAnyAttributeAttribute()] 
        public  System.Xml.XmlAttribute[] AnyAttr {
            get {
                return this.anyAttrField;
            }
            set {
                this.anyAttrField = value;
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  SetStateCompletedEventHandler (object sender, SetStateCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  SetStateCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  SetStateCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  PostState[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((PostState[])(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  UpdatePostMetadataCompletedEventHandler (object sender, UpdatePostMetadataCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  UpdatePostMetadataCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  UpdatePostMetadataCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  FeedMetadata[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((FeedMetadata[])(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  UpdatePostMetadatav2CompletedEventHandler (object sender, UpdatePostMetadatav2CompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  UpdatePostMetadatav2CompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  UpdatePostMetadatav2CompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  FeedMetadata[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((FeedMetadata[])(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  RatePostCompletedEventHandler (object sender, System.ComponentModel.AsyncCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  GetLinkCountCompletedEventHandler (object sender, GetLinkCountCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  GetLinkCountCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  GetLinkCountCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  int Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((int)(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  GetLinksCompletedEventHandler (object sender, GetLinksCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  GetLinksCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  GetLinksCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  string[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((string[])(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  GetPostIdCompletedEventHandler (object sender, GetPostIdCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  GetPostIdCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  GetPostIdCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  string Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((string)(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  ClipPostsCompletedEventHandler (object sender, System.ComponentModel.AsyncCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  ClipItemsCompletedEventHandler (object sender, ClipItemsCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  ClipItemsCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  ClipItemsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  ClippedItem[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((ClippedItem[])(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  UnClipPostsCompletedEventHandler (object sender, System.ComponentModel.AsyncCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  GetItemsCompletedEventHandler (object sender, GetItemsCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  GetItemsCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  GetItemsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  rss Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((rss)(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  GetHighlyViewedPostsCompletedEventHandler (object sender, GetHighlyViewedPostsCompletedEventArgs e);
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")] 
    public partial class  GetHighlyViewedPostsCompletedEventArgs  : System.ComponentModel.AsyncCompletedEventArgs {
		
        private  object[] results;
 
        internal  GetHighlyViewedPostsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
 
        public  System.Xml.XmlElement Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((System.Xml.XmlElement)(this.results[0]));
            }
        }

	}
	
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")] 
    public delegate  void  SendPostCompletedEventHandler (object sender, System.ComponentModel.AsyncCompletedEventArgs e);

}
