namespace RssBandit.DasBlog {
    using System.Diagnostics;
    using System.Web.Services;
    using System.ComponentModel;
    using System.Web.Services.Protocols;
    using System;
    using System.Xml.Serialization;
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Web.Services.WebServiceBindingAttribute(Name="ConfigEditingServiceSoap", Namespace="urn:schemas-newtelligence-com:dasblog:config-services")]
    public partial class ConfigEditingService : System.Web.Services.Protocols.SoapHttpClientProtocol {
        private authenticationHeader authenticationHeaderValueField;
        private System.Threading.SendOrPostCallback GetSiteConfigOperationCompleted;
        private System.Threading.SendOrPostCallback UpdateSiteConfigOperationCompleted;
        private System.Threading.SendOrPostCallback EnumBlogrollsOperationCompleted;
        private System.Threading.SendOrPostCallback GetBlogrollOperationCompleted;
        private System.Threading.SendOrPostCallback PostBlogrollOperationCompleted;
        private bool useDefaultCredentialsSetExplicitly;
        public ConfigEditingService() {
            this.Url = "http://localhost/dasBlog/ConfigEditingService.asmx";
            if ((this.IsLocalFileSystemWebService(this.Url) == true)) {
                this.UseDefaultCredentials = true;
                this.useDefaultCredentialsSetExplicitly = false;
            }
            else {
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
        public authenticationHeader authenticationHeaderValue {
            get {
                return this.authenticationHeaderValueField;
            }
            set {
                this.authenticationHeaderValueField = value;
            }
        }
        public new string Url {
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
        public new bool UseDefaultCredentials {
            get {
                return base.UseDefaultCredentials;
            }
            set {
                base.UseDefaultCredentials = value;
                this.useDefaultCredentialsSetExplicitly = true;
            }
        }
        public event GetSiteConfigCompletedEventHandler GetSiteConfigCompleted;
        public event UpdateSiteConfigCompletedEventHandler UpdateSiteConfigCompleted;
        public event EnumBlogrollsCompletedEventHandler EnumBlogrollsCompleted;
        public event GetBlogrollCompletedEventHandler GetBlogrollCompleted;
        public event PostBlogrollCompletedEventHandler PostBlogrollCompleted;
        [System.Web.Services.Protocols.SoapHeaderAttribute("authenticationHeaderValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("urn:schemas-newtelligence-com:dasblog:config-services/GetSiteConfig", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Bare)]
        [return: System.Xml.Serialization.XmlElementAttribute("siteConfig", Namespace="urn:schemas-newtelligence-com:dasblog:config-services")]
        public SiteConfig GetSiteConfig() {
            object[] results = this.Invoke("GetSiteConfig", new object[0]);
            return ((SiteConfig)(results[0]));
        }
        public System.IAsyncResult BeginGetSiteConfig(System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetSiteConfig", new object[0], callback, asyncState);
        }
        public SiteConfig EndGetSiteConfig(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((SiteConfig)(results[0]));
        }
        public void GetSiteConfigAsync() {
            this.GetSiteConfigAsync(null);
        }
        public void GetSiteConfigAsync(object userState) {
            if ((this.GetSiteConfigOperationCompleted == null)) {
                this.GetSiteConfigOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetSiteConfigOperationCompleted);
            }
            this.InvokeAsync("GetSiteConfig", new object[0], this.GetSiteConfigOperationCompleted, userState);
        }
        private void OnGetSiteConfigOperationCompleted(object arg) {
            if ((this.GetSiteConfigCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetSiteConfigCompleted(this, new GetSiteConfigCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        [System.Web.Services.Protocols.SoapHeaderAttribute("authenticationHeaderValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("urn:schemas-newtelligence-com:dasblog:config-services/UpdateSiteConfig", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Bare)]
        public void UpdateSiteConfig([System.Xml.Serialization.XmlElementAttribute(Namespace="urn:newtelligence-com:dasblog:config", IsNullable=true)] SiteConfig siteConfig) {
            this.Invoke("UpdateSiteConfig", new object[] {
                        siteConfig});
        }
        public System.IAsyncResult BeginUpdateSiteConfig(SiteConfig siteConfig, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("UpdateSiteConfig", new object[] {
                        siteConfig}, callback, asyncState);
        }
        public void EndUpdateSiteConfig(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
        public void UpdateSiteConfigAsync(SiteConfig siteConfig) {
            this.UpdateSiteConfigAsync(siteConfig, null);
        }
        public void UpdateSiteConfigAsync(SiteConfig siteConfig, object userState) {
            if ((this.UpdateSiteConfigOperationCompleted == null)) {
                this.UpdateSiteConfigOperationCompleted = new System.Threading.SendOrPostCallback(this.OnUpdateSiteConfigOperationCompleted);
            }
            this.InvokeAsync("UpdateSiteConfig", new object[] {
                        siteConfig}, this.UpdateSiteConfigOperationCompleted, userState);
        }
        private void OnUpdateSiteConfigOperationCompleted(object arg) {
            if ((this.UpdateSiteConfigCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.UpdateSiteConfigCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        [System.Web.Services.Protocols.SoapHeaderAttribute("authenticationHeaderValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("urn:schemas-newtelligence-com:dasblog:config-services/EnumBlogrolls", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Bare)]
        [return: System.Xml.Serialization.XmlArrayAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services")]
        public string[] EnumBlogrolls() {
            object[] results = this.Invoke("EnumBlogrolls", new object[0]);
            return ((string[])(results[0]));
        }
        public System.IAsyncResult BeginEnumBlogrolls(System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("EnumBlogrolls", new object[0], callback, asyncState);
        }
        public string[] EndEnumBlogrolls(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((string[])(results[0]));
        }
        public void EnumBlogrollsAsync() {
            this.EnumBlogrollsAsync(null);
        }
        public void EnumBlogrollsAsync(object userState) {
            if ((this.EnumBlogrollsOperationCompleted == null)) {
                this.EnumBlogrollsOperationCompleted = new System.Threading.SendOrPostCallback(this.OnEnumBlogrollsOperationCompleted);
            }
            this.InvokeAsync("EnumBlogrolls", new object[0], this.EnumBlogrollsOperationCompleted, userState);
        }
        private void OnEnumBlogrollsOperationCompleted(object arg) {
            if ((this.EnumBlogrollsCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.EnumBlogrollsCompleted(this, new EnumBlogrollsCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        [System.Web.Services.Protocols.SoapHeaderAttribute("authenticationHeaderValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("urn:schemas-newtelligence-com:dasblog:config-services/GetBlogroll", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Bare)]
        [return: System.Xml.Serialization.XmlElementAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services")]
        public System.Xml.XmlElement GetBlogroll([System.Xml.Serialization.XmlElementAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services")] string blogRollName) {
            object[] results = this.Invoke("GetBlogroll", new object[] {
                        blogRollName});
            return ((System.Xml.XmlElement)(results[0]));
        }
        public System.IAsyncResult BeginGetBlogroll(string blogRollName, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("GetBlogroll", new object[] {
                        blogRollName}, callback, asyncState);
        }
        public System.Xml.XmlElement EndGetBlogroll(System.IAsyncResult asyncResult) {
            object[] results = this.EndInvoke(asyncResult);
            return ((System.Xml.XmlElement)(results[0]));
        }
        public void GetBlogrollAsync(string blogRollName) {
            this.GetBlogrollAsync(blogRollName, null);
        }
        public void GetBlogrollAsync(string blogRollName, object userState) {
            if ((this.GetBlogrollOperationCompleted == null)) {
                this.GetBlogrollOperationCompleted = new System.Threading.SendOrPostCallback(this.OnGetBlogrollOperationCompleted);
            }
            this.InvokeAsync("GetBlogroll", new object[] {
                        blogRollName}, this.GetBlogrollOperationCompleted, userState);
        }
        private void OnGetBlogrollOperationCompleted(object arg) {
            if ((this.GetBlogrollCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.GetBlogrollCompleted(this, new GetBlogrollCompletedEventArgs(invokeArgs.Results, invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        [System.Web.Services.Protocols.SoapHeaderAttribute("authenticationHeaderValue")]
        [System.Web.Services.Protocols.SoapDocumentMethodAttribute("urn:schemas-newtelligence-com:dasblog:config-services/PostBlogroll", Use=System.Web.Services.Description.SoapBindingUse.Literal, ParameterStyle=System.Web.Services.Protocols.SoapParameterStyle.Bare)]
        public void PostBlogroll([System.Xml.Serialization.XmlElementAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services")] string blogRollName, [System.Xml.Serialization.XmlElementAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services")] System.Xml.XmlElement blogRoll) {
            this.Invoke("PostBlogroll", new object[] {
                        blogRollName,
                        blogRoll});
        }
        public System.IAsyncResult BeginPostBlogroll(string blogRollName, System.Xml.XmlElement blogRoll, System.AsyncCallback callback, object asyncState) {
            return this.BeginInvoke("PostBlogroll", new object[] {
                        blogRollName,
                        blogRoll}, callback, asyncState);
        }
        public void EndPostBlogroll(System.IAsyncResult asyncResult) {
            this.EndInvoke(asyncResult);
        }
        public void PostBlogrollAsync(string blogRollName, System.Xml.XmlElement blogRoll) {
            this.PostBlogrollAsync(blogRollName, blogRoll, null);
        }
        public void PostBlogrollAsync(string blogRollName, System.Xml.XmlElement blogRoll, object userState) {
            if ((this.PostBlogrollOperationCompleted == null)) {
                this.PostBlogrollOperationCompleted = new System.Threading.SendOrPostCallback(this.OnPostBlogrollOperationCompleted);
            }
            this.InvokeAsync("PostBlogroll", new object[] {
                        blogRollName,
                        blogRoll}, this.PostBlogrollOperationCompleted, userState);
        }
        private void OnPostBlogrollOperationCompleted(object arg) {
            if ((this.PostBlogrollCompleted != null)) {
                System.Web.Services.Protocols.InvokeCompletedEventArgs invokeArgs = ((System.Web.Services.Protocols.InvokeCompletedEventArgs)(arg));
                this.PostBlogrollCompleted(this, new System.ComponentModel.AsyncCompletedEventArgs(invokeArgs.Error, invokeArgs.Cancelled, invokeArgs.UserState));
            }
        }
        public new void CancelAsync(object userState) {
            base.CancelAsync(userState);
        }
        private bool IsLocalFileSystemWebService(string url) {
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
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services:auth-temp")]
    [System.Xml.Serialization.XmlRootAttribute(Namespace="urn:schemas-newtelligence-com:dasblog:config-services:auth-temp", IsNullable=false)]
    public partial class authenticationHeader : System.Web.Services.Protocols.SoapHeader {
        private string userNameField;
        private string passwordField;
        public string userName {
            get {
                return this.userNameField;
            }
            set {
                this.userNameField = value;
            }
        }
        public string password {
            get {
                return this.passwordField;
            }
            set {
                this.passwordField = value;
            }
        }
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Xml", "2.0.50727.832")]
    [System.SerializableAttribute()]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:newtelligence-com:dasblog:config")]
    public partial class CrosspostSite {
        private System.Xml.XmlElement[] anyField;
        private string profileNameField;
        private string hostNameField;
        private int portField;
        private string endpointField;
        private string usernameField;
        private string passwordField;
        private string blogidField;
        private string blognameField;
        private string apitypeField;
        private System.Xml.XmlAttribute[] anyAttrField;
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any {
            get {
                return this.anyField;
            }
            set {
                this.anyField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string profileName {
            get {
                return this.profileNameField;
            }
            set {
                this.profileNameField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string hostName {
            get {
                return this.hostNameField;
            }
            set {
                this.hostNameField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public int port {
            get {
                return this.portField;
            }
            set {
                this.portField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string endpoint {
            get {
                return this.endpointField;
            }
            set {
                this.endpointField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string username {
            get {
                return this.usernameField;
            }
            set {
                this.usernameField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string password {
            get {
                return this.passwordField;
            }
            set {
                this.passwordField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string blogid {
            get {
                return this.blogidField;
            }
            set {
                this.blogidField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string blogname {
            get {
                return this.blognameField;
            }
            set {
                this.blognameField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string apitype {
            get {
                return this.apitypeField;
            }
            set {
                this.apitypeField = value;
            }
        }
        [System.Xml.Serialization.XmlAnyAttributeAttribute()]
        public System.Xml.XmlAttribute[] AnyAttr {
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
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:newtelligence-com:dasblog:config")]
    public partial class ContentFilter {
        private System.Xml.XmlElement[] anyField;
        private string findField;
        private string replaceField;
        private bool isregexField;
        private System.Xml.XmlAttribute[] anyAttrField;
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any {
            get {
                return this.anyField;
            }
            set {
                this.anyField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string find {
            get {
                return this.findField;
            }
            set {
                this.findField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public string replace {
            get {
                return this.replaceField;
            }
            set {
                this.replaceField = value;
            }
        }
        [System.Xml.Serialization.XmlAttributeAttribute()]
        public bool isregex {
            get {
                return this.isregexField;
            }
            set {
                this.isregexField = value;
            }
        }
        [System.Xml.Serialization.XmlAnyAttributeAttribute()]
        public System.Xml.XmlAttribute[] AnyAttr {
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
    [System.Xml.Serialization.XmlTypeAttribute(Namespace="urn:newtelligence-com:dasblog:config")]
    public partial class SiteConfig {
        private System.Xml.XmlElement[] anyField;
        private string titleField;
        private string subtitleField;
        private string themeField;
        private string descriptionField;
        private string contactField;
        private string rootField;
        private string copyrightField;
        private int rssDayCountField;
        private int rssMainEntryCountField;
        private int rssEntryCountField;
        private bool enableRssItemFootersField;
        private string rssItemFooterField;
        private int frontPageDayCountField;
        private int frontPageEntryCountField;
        private bool categoryAllEntriesField;
        private string frontPageCategoryField;
        private bool alwaysIncludeContentInRSSField;
        private bool entryTitleAsLinkField;
        private bool notifyWebLogsDotComField;
        private bool notifyBloGsField;
        private bool obfuscateEmailField;
        private string notificationEMailAddressField;
        private bool sendCommentsByEmailField;
        private bool sendReferralsByEmailField;
        private bool sendTrackbacksByEmailField;
        private bool sendPingbacksByEmailField;
        private bool enableBloggerApiField;
        private bool enableCommentsField;
        private bool enableCommentApiField;
        private bool enableConfigEditServiceField;
        private bool enableEditServiceField;
        private bool enableAutoPingbackField;
        private bool showCommentCountField;
        private bool enableTrackbackServiceField;
        private bool enablePingbackServiceField;
        private bool enableStartPageCachingField;
        private bool enableBlogrollDescriptionField;
        private bool enableUrlRewritingField;
        private bool enableFtbField;
        private bool enableCrosspostsField;
        private bool useUserCultureField;
        private bool showItemDescriptionInAggregatedViewsField;
        private bool enableClickThroughField;
        private bool enableAggregatorBuggingField;
        private int displayTimeZoneIndexField;
        private bool adjustDisplayTimeZoneField;
        private string editPasswordField;
        private string contentDirField;
        private string logDirField;
        private string binariesDirField;
        private string smtpServerField;
        private bool enablePop3Field;
        private string pop3ServerField;
        private string pop3UsernameField;
        private string pop3PasswordField;
        private string pop3SubjectPrefixField;
        private int pop3IntervalField;
        private bool pop3InlineAttachedPicturesField;
        private int pop3InlinedAttachedPicturesThumbHeightField;
        private bool applyContentFiltersToWebField;
        private bool applyContentFiltersToRSSField;
        private bool enableXSSUpstreamField;
        private string xSSUpstreamEndpointField;
        private string xSSUpstreamUsernameField;
        private string xSSUpstreamPasswordField;
        private string xSSRSSFilenameField;
        private int xSSUpstreamIntervalField;
        private ContentFilter[] contentFiltersField;
        private CrosspostSite[] crosspostSitesField;
        private System.Xml.XmlAttribute[] anyAttrField;
        [System.Xml.Serialization.XmlAnyElementAttribute()]
        public System.Xml.XmlElement[] Any {
            get {
                return this.anyField;
            }
            set {
                this.anyField = value;
            }
        }
        public string Title {
            get {
                return this.titleField;
            }
            set {
                this.titleField = value;
            }
        }
        public string Subtitle {
            get {
                return this.subtitleField;
            }
            set {
                this.subtitleField = value;
            }
        }
        public string Theme {
            get {
                return this.themeField;
            }
            set {
                this.themeField = value;
            }
        }
        public string Description {
            get {
                return this.descriptionField;
            }
            set {
                this.descriptionField = value;
            }
        }
        public string Contact {
            get {
                return this.contactField;
            }
            set {
                this.contactField = value;
            }
        }
        public string Root {
            get {
                return this.rootField;
            }
            set {
                this.rootField = value;
            }
        }
        public string Copyright {
            get {
                return this.copyrightField;
            }
            set {
                this.copyrightField = value;
            }
        }
        public int RssDayCount {
            get {
                return this.rssDayCountField;
            }
            set {
                this.rssDayCountField = value;
            }
        }
        public int RssMainEntryCount {
            get {
                return this.rssMainEntryCountField;
            }
            set {
                this.rssMainEntryCountField = value;
            }
        }
        public int RssEntryCount {
            get {
                return this.rssEntryCountField;
            }
            set {
                this.rssEntryCountField = value;
            }
        }
        public bool EnableRssItemFooters {
            get {
                return this.enableRssItemFootersField;
            }
            set {
                this.enableRssItemFootersField = value;
            }
        }
        public string RssItemFooter {
            get {
                return this.rssItemFooterField;
            }
            set {
                this.rssItemFooterField = value;
            }
        }
        public int FrontPageDayCount {
            get {
                return this.frontPageDayCountField;
            }
            set {
                this.frontPageDayCountField = value;
            }
        }
        public int FrontPageEntryCount {
            get {
                return this.frontPageEntryCountField;
            }
            set {
                this.frontPageEntryCountField = value;
            }
        }
        public bool CategoryAllEntries {
            get {
                return this.categoryAllEntriesField;
            }
            set {
                this.categoryAllEntriesField = value;
            }
        }
        public string FrontPageCategory {
            get {
                return this.frontPageCategoryField;
            }
            set {
                this.frontPageCategoryField = value;
            }
        }
        public bool AlwaysIncludeContentInRSS {
            get {
                return this.alwaysIncludeContentInRSSField;
            }
            set {
                this.alwaysIncludeContentInRSSField = value;
            }
        }
        public bool EntryTitleAsLink {
            get {
                return this.entryTitleAsLinkField;
            }
            set {
                this.entryTitleAsLinkField = value;
            }
        }
        public bool NotifyWebLogsDotCom {
            get {
                return this.notifyWebLogsDotComField;
            }
            set {
                this.notifyWebLogsDotComField = value;
            }
        }
        public bool NotifyBloGs {
            get {
                return this.notifyBloGsField;
            }
            set {
                this.notifyBloGsField = value;
            }
        }
        public bool ObfuscateEmail {
            get {
                return this.obfuscateEmailField;
            }
            set {
                this.obfuscateEmailField = value;
            }
        }
        public string NotificationEMailAddress {
            get {
                return this.notificationEMailAddressField;
            }
            set {
                this.notificationEMailAddressField = value;
            }
        }
        public bool SendCommentsByEmail {
            get {
                return this.sendCommentsByEmailField;
            }
            set {
                this.sendCommentsByEmailField = value;
            }
        }
        public bool SendReferralsByEmail {
            get {
                return this.sendReferralsByEmailField;
            }
            set {
                this.sendReferralsByEmailField = value;
            }
        }
        public bool SendTrackbacksByEmail {
            get {
                return this.sendTrackbacksByEmailField;
            }
            set {
                this.sendTrackbacksByEmailField = value;
            }
        }
        public bool SendPingbacksByEmail {
            get {
                return this.sendPingbacksByEmailField;
            }
            set {
                this.sendPingbacksByEmailField = value;
            }
        }
        public bool EnableBloggerApi {
            get {
                return this.enableBloggerApiField;
            }
            set {
                this.enableBloggerApiField = value;
            }
        }
        public bool EnableComments {
            get {
                return this.enableCommentsField;
            }
            set {
                this.enableCommentsField = value;
            }
        }
        public bool EnableCommentApi {
            get {
                return this.enableCommentApiField;
            }
            set {
                this.enableCommentApiField = value;
            }
        }
        public bool EnableConfigEditService {
            get {
                return this.enableConfigEditServiceField;
            }
            set {
                this.enableConfigEditServiceField = value;
            }
        }
        public bool EnableEditService {
            get {
                return this.enableEditServiceField;
            }
            set {
                this.enableEditServiceField = value;
            }
        }
        public bool EnableAutoPingback {
            get {
                return this.enableAutoPingbackField;
            }
            set {
                this.enableAutoPingbackField = value;
            }
        }
        public bool ShowCommentCount {
            get {
                return this.showCommentCountField;
            }
            set {
                this.showCommentCountField = value;
            }
        }
        public bool EnableTrackbackService {
            get {
                return this.enableTrackbackServiceField;
            }
            set {
                this.enableTrackbackServiceField = value;
            }
        }
        public bool EnablePingbackService {
            get {
                return this.enablePingbackServiceField;
            }
            set {
                this.enablePingbackServiceField = value;
            }
        }
        public bool EnableStartPageCaching {
            get {
                return this.enableStartPageCachingField;
            }
            set {
                this.enableStartPageCachingField = value;
            }
        }
        public bool EnableBlogrollDescription {
            get {
                return this.enableBlogrollDescriptionField;
            }
            set {
                this.enableBlogrollDescriptionField = value;
            }
        }
        public bool EnableUrlRewriting {
            get {
                return this.enableUrlRewritingField;
            }
            set {
                this.enableUrlRewritingField = value;
            }
        }
        public bool EnableFtb {
            get {
                return this.enableFtbField;
            }
            set {
                this.enableFtbField = value;
            }
        }
        public bool EnableCrossposts {
            get {
                return this.enableCrosspostsField;
            }
            set {
                this.enableCrosspostsField = value;
            }
        }
        public bool UseUserCulture {
            get {
                return this.useUserCultureField;
            }
            set {
                this.useUserCultureField = value;
            }
        }
        public bool ShowItemDescriptionInAggregatedViews {
            get {
                return this.showItemDescriptionInAggregatedViewsField;
            }
            set {
                this.showItemDescriptionInAggregatedViewsField = value;
            }
        }
        public bool EnableClickThrough {
            get {
                return this.enableClickThroughField;
            }
            set {
                this.enableClickThroughField = value;
            }
        }
        public bool EnableAggregatorBugging {
            get {
                return this.enableAggregatorBuggingField;
            }
            set {
                this.enableAggregatorBuggingField = value;
            }
        }
        public int DisplayTimeZoneIndex {
            get {
                return this.displayTimeZoneIndexField;
            }
            set {
                this.displayTimeZoneIndexField = value;
            }
        }
        public bool AdjustDisplayTimeZone {
            get {
                return this.adjustDisplayTimeZoneField;
            }
            set {
                this.adjustDisplayTimeZoneField = value;
            }
        }
        public string EditPassword {
            get {
                return this.editPasswordField;
            }
            set {
                this.editPasswordField = value;
            }
        }
        public string ContentDir {
            get {
                return this.contentDirField;
            }
            set {
                this.contentDirField = value;
            }
        }
        public string LogDir {
            get {
                return this.logDirField;
            }
            set {
                this.logDirField = value;
            }
        }
        public string BinariesDir {
            get {
                return this.binariesDirField;
            }
            set {
                this.binariesDirField = value;
            }
        }
        public string SmtpServer {
            get {
                return this.smtpServerField;
            }
            set {
                this.smtpServerField = value;
            }
        }
        public bool EnablePop3 {
            get {
                return this.enablePop3Field;
            }
            set {
                this.enablePop3Field = value;
            }
        }
        public string Pop3Server {
            get {
                return this.pop3ServerField;
            }
            set {
                this.pop3ServerField = value;
            }
        }
        public string Pop3Username {
            get {
                return this.pop3UsernameField;
            }
            set {
                this.pop3UsernameField = value;
            }
        }
        public string Pop3Password {
            get {
                return this.pop3PasswordField;
            }
            set {
                this.pop3PasswordField = value;
            }
        }
        public string Pop3SubjectPrefix {
            get {
                return this.pop3SubjectPrefixField;
            }
            set {
                this.pop3SubjectPrefixField = value;
            }
        }
        public int Pop3Interval {
            get {
                return this.pop3IntervalField;
            }
            set {
                this.pop3IntervalField = value;
            }
        }
        public bool Pop3InlineAttachedPictures {
            get {
                return this.pop3InlineAttachedPicturesField;
            }
            set {
                this.pop3InlineAttachedPicturesField = value;
            }
        }
        public int Pop3InlinedAttachedPicturesThumbHeight {
            get {
                return this.pop3InlinedAttachedPicturesThumbHeightField;
            }
            set {
                this.pop3InlinedAttachedPicturesThumbHeightField = value;
            }
        }
        public bool ApplyContentFiltersToWeb {
            get {
                return this.applyContentFiltersToWebField;
            }
            set {
                this.applyContentFiltersToWebField = value;
            }
        }
        public bool ApplyContentFiltersToRSS {
            get {
                return this.applyContentFiltersToRSSField;
            }
            set {
                this.applyContentFiltersToRSSField = value;
            }
        }
        public bool EnableXSSUpstream {
            get {
                return this.enableXSSUpstreamField;
            }
            set {
                this.enableXSSUpstreamField = value;
            }
        }
        public string XSSUpstreamEndpoint {
            get {
                return this.xSSUpstreamEndpointField;
            }
            set {
                this.xSSUpstreamEndpointField = value;
            }
        }
        public string XSSUpstreamUsername {
            get {
                return this.xSSUpstreamUsernameField;
            }
            set {
                this.xSSUpstreamUsernameField = value;
            }
        }
        public string XSSUpstreamPassword {
            get {
                return this.xSSUpstreamPasswordField;
            }
            set {
                this.xSSUpstreamPasswordField = value;
            }
        }
        public string XSSRSSFilename {
            get {
                return this.xSSRSSFilenameField;
            }
            set {
                this.xSSRSSFilenameField = value;
            }
        }
        public int XSSUpstreamInterval {
            get {
                return this.xSSUpstreamIntervalField;
            }
            set {
                this.xSSUpstreamIntervalField = value;
            }
        }
        public ContentFilter[] ContentFilters {
            get {
                return this.contentFiltersField;
            }
            set {
                this.contentFiltersField = value;
            }
        }
        public CrosspostSite[] CrosspostSites {
            get {
                return this.crosspostSitesField;
            }
            set {
                this.crosspostSitesField = value;
            }
        }
        [System.Xml.Serialization.XmlAnyAttributeAttribute()]
        public System.Xml.XmlAttribute[] AnyAttr {
            get {
                return this.anyAttrField;
            }
            set {
                this.anyAttrField = value;
            }
        }
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    public delegate void GetSiteConfigCompletedEventHandler(object sender, GetSiteConfigCompletedEventArgs e);
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class GetSiteConfigCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        private object[] results;
        internal GetSiteConfigCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
        public SiteConfig Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((SiteConfig)(this.results[0]));
            }
        }
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    public delegate void UpdateSiteConfigCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    public delegate void EnumBlogrollsCompletedEventHandler(object sender, EnumBlogrollsCompletedEventArgs e);
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class EnumBlogrollsCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        private object[] results;
        internal EnumBlogrollsCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
        public string[] Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((string[])(this.results[0]));
            }
        }
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    public delegate void GetBlogrollCompletedEventHandler(object sender, GetBlogrollCompletedEventArgs e);
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    [System.Diagnostics.DebuggerStepThroughAttribute()]
    [System.ComponentModel.DesignerCategoryAttribute("code")]
    public partial class GetBlogrollCompletedEventArgs : System.ComponentModel.AsyncCompletedEventArgs {
        private object[] results;
        internal GetBlogrollCompletedEventArgs(object[] results, System.Exception exception, bool cancelled, object userState) :
                base(exception, cancelled, userState) {
            this.results = results;
        }
        public System.Xml.XmlElement Result {
            get {
                this.RaiseExceptionIfNecessary();
                return ((System.Xml.XmlElement)(this.results[0]));
            }
        }
    }
    [System.CodeDom.Compiler.GeneratedCodeAttribute("System.Web.Services", "2.0.50727.42")]
    public delegate void PostBlogrollCompletedEventHandler(object sender, System.ComponentModel.AsyncCompletedEventArgs e);
}
