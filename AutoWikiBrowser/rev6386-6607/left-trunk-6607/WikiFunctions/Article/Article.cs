using System;
using System.Text;
using System.Xml.Serialization;
using WikiFunctions.Logging;
using System.Text.RegularExpressions;
using WikiFunctions.Plugin;
using WikiFunctions.Options;
using WikiFunctions.Parse;
using WikiFunctions.Controls;
using System.Windows.Forms;
using WikiFunctions.API;
using System.Collections.Generic;
using WikiFunctions.TalkPages;
namespace WikiFunctions
{
    public delegate void ArticleRedirected(string oldTitle, string newTitle);
    public enum Exists { Yes, No, Unknown }
    public delegate void AddListenerDelegate(string key, IMyTraceListener listener);
    public class Article : IProcessArticleEventArgs, IComparable<Article>
    {
        protected AWBLogListener mAWBLogListener;
        protected string mArticleText = "";
        protected string mOriginalArticleText = "";
        protected string mPluginEditSummary = "";
        protected bool mPluginSkip;
        private readonly PageInfo mPage;
        private bool noChange;
        public virtual IAWBTraceListener Trace
        { get { return mAWBLogListener; } }
        public bool PreProcessed;
        public Article()
        {
            Exists = Exists.Unknown;
        }
        public Article(string name)
            : this(name, Namespace.Determine(name))
        {
        }
        public Article(string name, int nameSpaceKey)
            : this()
        {
            Name = name.Contains("#") ? name.Substring(0, name.IndexOf('#')) : name;
            InitialiseLogListener();
            NameSpaceKey = nameSpaceKey;
        }
        public Article(string name, string text)
            : this(name)
        {
            mOriginalArticleText = mArticleText = text;
        }
        public Article(PageInfo page)
            : this(page.Title, page.NamespaceID)
        {
            mPage = page;
            mArticleText = page.Text;
            Exists = page.Exists ? Exists.Yes : Exists.No;
        }
        private static event AddListenerDelegate addListener;
        private static TraceManager currentTraceManager;
        private static string whatName;
        public static void SetAddListener(AddListenerDelegate del, TraceManager trace, string what)
        {
            addListener += del;
            currentTraceManager = trace;
            whatName = what;
        }
        public void InitialiseLogListener()
        {
            if (mAWBLogListener != null)
                return;
            mAWBLogListener = new AWBLogListener(Name);
            if (currentTraceManager != null)
            {
                currentTraceManager.AddListener(whatName, mAWBLogListener);
            }
            if (addListener != null)
            {
                addListener(whatName, mAWBLogListener);
            }
        }
        public string Name { get; set; }
        [XmlIgnore]
        public string NamespacelessName
        {
            get
            {
                if (NameSpaceKey == Namespace.Article) return Name;
                int pos = Name.IndexOf(':');
                return pos < 0 ? Name : Name.Substring(pos + 1).Trim();
            }
        }
        [XmlAttribute]
        public int NameSpaceKey
        { get; set; }
        [XmlIgnore]
        public AWBLogListener LogListener
        { get { return mAWBLogListener; } }
        [XmlIgnore]
        public string URLEncodedName
        { get { return Tools.WikiEncode(Name); } }
        [XmlIgnore]
        public string ArticleText
        { get { return mArticleText.Trim(); } }
        [XmlIgnore]
        public string OriginalArticleText
        {
            get { return mPage == null ? mOriginalArticleText : mPage.Text; }
        }
        [XmlIgnore]
        public string EditSummary
        { get { return summary.ToString(); } }
        public void ResetEditSummary()
        {
            summary.Length = 0;
        }
        private readonly StringBuilder summary = new StringBuilder();
        private void AppendToSummary(string newText)
        {
            if (string.IsNullOrEmpty(newText.Trim()))
                return;
            if (summary.Length > 0)
                summary.Append(", " + newText);
            else
                summary.Append(newText);
        }
        [XmlIgnore]
        public bool IsStub { get { return Parsers.IsStub(mArticleText); } }
        [XmlIgnore]
        public bool HasDiacriticsInTitle
        { get { return (Tools.RemoveDiacritics(Name) != Name); } }
        [XmlIgnore]
        public bool ArticleIsAboutAPerson
        {
            get
            {
                return Variables.Project == ProjectEnum.wikipedia
                    && Variables.LangCode == "en"
                    && Parsers.IsArticleAboutAPerson(mArticleText, Name, true);
            }
        }
        [XmlIgnore]
        public bool HasStubTemplate
        { get { return Parsers.HasStubTemplate(mArticleText); } }
        [XmlIgnore]
        public bool HasInfoBox
        { get { return Parsers.HasInfobox(mArticleText); } }
        [XmlIgnore]
        public bool IsInUse
        { get { return Parsers.IsInUse(mArticleText); } }
        [XmlIgnore]
        public bool HasSicTag
        { get { return Parsers.HasSicTag(mArticleText); } }
        [XmlIgnore]
        public bool HasDeadLinks
        { get { return Parsers.HasDeadLinks(mArticleText); } }
        [XmlIgnore]
        public bool HasMorefootnotesAndManyReferences
        { get { return Parsers.HasMorefootnotesAndManyReferences(mArticleText); } }
        [XmlIgnore]
        public bool IsDisambiguationPage
        { get { return Variables.LangCode == "en" && NameSpaceKey == Namespace.Mainspace && WikiRegexes.Disambigs.IsMatch(mArticleText); } }
        [XmlIgnore]
        public bool IsDisambiguationPageWithRefs
        { get { return IsDisambiguationPage && WikiRegexes.Refs.IsMatch(mArticleText); } }
        [XmlIgnore]
        public bool HasRefAfterReflist
        { get { return Parsers.HasRefAfterReflist(mArticleText); } }
        [XmlIgnore]
        public bool HasNamedReferences
        { get { return Parsers.HasNamedReferences(mArticleText); } }
        [XmlIgnore]
        public bool HasBareReferences
        { get { return Parsers.HasBareReferences(mArticleText); } }
        [XmlIgnore]
        public bool HasAmbiguousCiteTemplateDates
        { get { return Parsers.AmbiguousCiteTemplateDates(mArticleText); } }
        [XmlIgnore]
        public bool SkipArticle
        { get { return mAWBLogListener.Skipped; } private set { mAWBLogListener.Skipped = value; } }
        [XmlIgnore]
        public bool CanDoGeneralFixes
        {
            get
            {
                return (NameSpaceKey == Namespace.Article
                        || NameSpaceKey == Namespace.Category
                        || Name.Contains("Sandbox"))
                    || Name.Contains("/doc");
            }
        }
        [XmlIgnore]
        public bool CanDoTalkGeneralFixes
        {
            get
            {
                return (NameSpaceKey == Namespace.Talk);
            }
        }
        [XmlIgnore]
        public bool IsMissingReferencesDisplay
        { get { return Parsers.IsMissingReferencesDisplay(mArticleText); } }
        [XmlIgnore]
        public Parsers.DateLocale DateLocale
        { get { return Parsers.DeterminePredominantDateLocale(mArticleText); } }
        public void AWBSkip(string reason)
        { Trace.AWBSkipped(reason); }
        [XmlIgnore]
        public bool OnlyWhiteSpaceChanged
        {
            get
            {
                return
                    (string.Compare(WikiRegexes.WhiteSpace.Replace(OriginalArticleText, ""),
                                    WikiRegexes.WhiteSpace.Replace(mArticleText, "")) == 0);
            }
        }
        [XmlIgnore]
        public bool OnlyCasingChanged
        {
            get { return Tools.CaseInsensitiveStringCompare(OriginalArticleText, mArticleText); }
        }
        [XmlIgnore]
        public bool OnlyWhiteSpaceAndCasingChanged
        {
            get
            {
                return Tools.CaseInsensitiveStringCompare(WikiRegexes.WhiteSpace.Replace(OriginalArticleText, ""),
                                                          WikiRegexes.WhiteSpace.Replace(mArticleText, ""));
            }
        }
        [XmlIgnore]
        public bool OnlyGeneralFixesChanged
        {
            get { return (_generalFixesCausedChange && (ArticleText == _afterGeneralFixesArticleText)); }
        }
        [XmlIgnore]
        public bool OnlyMinorGeneralFixesChanged
        {
            get { return (OnlyGeneralFixesChanged && !_generalFixesSignificantChange); }
        }
        [XmlIgnore]
        public bool NoArticleTextChanged
        {
            get { return (string.Compare(OriginalArticleText, mArticleText) == 0); }
        }
        public void SendPageToPlugin(IAWBPlugin plugin, IAutoWikiBrowser sender)
        {
            string strTemp = plugin.ProcessArticle(sender, this);
            if (mPluginSkip)
            {
                if (!SkipArticle)
                    Trace.SkippedArticle(plugin.Name, "Skipped by plugin");
            }
            else
            {
                mAWBLogListener.Skipped = false;
                PluginChangeArticleText(strTemp);
                AppendPluginEditSummary();
            }
        }
        public void Unicodify(bool skipIfNoChange, Parsers parsers)
        {
            string strTemp = parsers.Unicodify(mArticleText, out noChange);
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No Unicodification");
            else if (!noChange)
                AWBChangeArticleText("Page Unicodified", strTemp, false);
        }
        public int UnbalancedBrackets(ref int bracketLength)
        {
            return Parsers.UnbalancedBrackets(ArticleText, ref bracketLength);
        }
        public Dictionary<int, int> BadCiteParameters()
        {
            return Parsers.BadCiteParameters(ArticleText);
        }
        public Dictionary<int, int> UnclosedTags()
        {
            return Parsers.UnclosedTags(ArticleText);
        }
        public Dictionary<int, int> DeadLinks()
        {
            return Parsers.DeadLinks(ArticleText);
        }
        public Dictionary<int, int> AmbiguousCiteTemplateDates()
        {
            return Parsers.AmbigCiteTemplateDates(ArticleText);
        }
        public void UpdateImages(ImageReplaceOptions option,
                                 string imageReplaceText, string imageWithText, bool skipIfNoChange)
        {
            string strTemp = "";
            imageReplaceText = imageReplaceText.Trim();
            imageWithText = imageWithText.Trim();
            if (imageReplaceText.Length > 0)
                switch (option)
                {
                    case ImageReplaceOptions.NoAction:
                        return;
                    case ImageReplaceOptions.Replace:
                        if (imageWithText.Length > 0) strTemp = Parsers.ReplaceImage(imageReplaceText, imageWithText, mArticleText, out noChange);
                        break;
                    case ImageReplaceOptions.Remove:
                        strTemp = Parsers.RemoveImage(imageReplaceText, mArticleText, false, imageWithText, out noChange);
                        break;
                    case ImageReplaceOptions.Comment:
                        strTemp = Parsers.RemoveImage(imageReplaceText, mArticleText, true, imageWithText, out noChange);
                        break;
                    default:
                        throw new ArgumentOutOfRangeException("option");
                }
            if (noChange && skipIfNoChange)
                Trace.AWBSkipped("No File Changed");
            else if (!noChange)
                AWBChangeArticleText("File replacement applied", strTemp, false);
        }
        public void Categorisation(CategorisationOptions option, Parsers parsers,
                                   bool skipIfNoChange, string categoryText, string categoryText2, bool removeSortKey)
        {
            string strTemp, action = "";
            switch (option)
            {
                case CategorisationOptions.NoAction:
                    return;
                case CategorisationOptions.AddCat:
                    if (categoryText.Length < 1) return;
                    strTemp = parsers.AddCategory(categoryText, mArticleText, Name, out noChange);
                    action = "Added " + categoryText;
                    break;
                case CategorisationOptions.ReCat:
                    if (categoryText.Length < 1 || categoryText2.Length < 1) return;
                    strTemp = Parsers.ReCategoriser(categoryText, categoryText2, mArticleText, out noChange, removeSortKey);
                    break;
                case CategorisationOptions.RemoveCat:
                    if (categoryText.Length < 1) return;
                    strTemp = Parsers.RemoveCategory(categoryText, mArticleText, out noChange);
                    action = "Removed " + categoryText;
                    break;
                default:
                    throw new ArgumentOutOfRangeException("option");
            }
            if (noChange && skipIfNoChange)
                Trace.AWBSkipped("No Category Changed");
            else if (!noChange)
                AWBChangeArticleText(action, strTemp, false);
        }
        public void PerformFindAndReplace(FindandReplace findAndReplace, SubstTemplates substTemplates,
                                          ReplaceSpecial.ReplaceSpecial replaceSpecial, bool skipIfNoChange, bool skipIfOnlyMinorChange)
        {
            if (!findAndReplace.HasReplacements && !replaceSpecial.HasRules && !substTemplates.HasSubstitutions)
                return;
            string strTemp = Tools.ConvertFromLocalLineEndings(mArticleText),
            testText = strTemp,
            tmpEditSummary = "";
            bool majorChangesMade;
            strTemp = findAndReplace.MultipleFindAndReplace(strTemp, Name, ref tmpEditSummary, out majorChangesMade);
            bool farMadeMajorChanges = (testText != strTemp && majorChangesMade);
            string strTemp2 = replaceSpecial.ApplyRules(strTemp, Name);
            strTemp2 = substTemplates.SubstituteTemplates(strTemp2, Name);
            if (!farMadeMajorChanges && strTemp2 != strTemp)
                farMadeMajorChanges = true;
            if (testText == strTemp2)
            {
                if (skipIfNoChange)
                    Trace.AWBSkipped("No Find And Replace Changes");
                else
                    return;
            }
            else if (!farMadeMajorChanges && skipIfOnlyMinorChange)
            {
                Trace.AWBSkipped("Only minor Find And Replace Changes");
            }
            else
            {
                AWBChangeArticleText("Find and replace applied" + tmpEditSummary,
                                     Tools.ConvertToLocalLineEndings(strTemp2), true);
                AppendToSummary(tmpEditSummary);
            }
        }
        public void PerformTypoFixes(RegExTypoFix regexTypos, bool skipIfNoChange)
        {
            string tmpEditSummary;
            string strTemp = regexTypos.PerformTypoFixes(mArticleText, out noChange, out tmpEditSummary, Name);
            if (noChange && skipIfNoChange)
                Trace.AWBSkipped("No typo fixes");
            else if (!noChange)
            {
                AWBChangeArticleText(tmpEditSummary, strTemp, false);
                AppendToSummary(tmpEditSummary);
            }
        }
        public void AutoTag(Parsers parsers, bool skipIfNoChange, bool restrictOrphanTagging)
        {
            string tmpEditSummary = "";
            string strTemp = parsers.Tagger(mArticleText, Name, restrictOrphanTagging, out noChange, ref tmpEditSummary);
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No Tag changed");
            else if (!noChange)
            {
                AWBChangeArticleText("Auto tagger changes applied" + tmpEditSummary, strTemp, false);
                AppendToSummary(tmpEditSummary);
            }
        }
        protected void MinorFixes(string langCode, bool skipIfNoChange)
        {
            AWBChangeArticleText("Fixed interwikis", Parsers.InterwikiConversions(mArticleText, out noChange), true);
            if (langCode == "en")
            {
                string strTemp = mArticleText;
                if (!(Namespace.Determine(Name).Equals(Namespace.Template) && Name.EndsWith(@"/doc")))
                    strTemp = Parsers.Conversions(mArticleText);
                strTemp = Parsers.FixLivingThingsRelatedDates(strTemp);
                strTemp = Parsers.FixHeadings(strTemp, Name, out noChange);
                if (mArticleText == strTemp && skipIfNoChange)
                {
                    Trace.AWBSkipped("No header errors");
                }
                else if (!noChange)
                    AWBChangeArticleText("Fixed header errors", strTemp, true);
                else
                {
                    AWBChangeArticleText("Fixed minor formatting issues", strTemp, true);
                    if (skipIfNoChange) Trace.AWBSkipped("No header errors");
                }
            }
        }
        public void SetDefaultSort(string langCode, bool skipIfNoChange, bool restrictDefaultsortAddition)
        {
            if (langCode == "en" && Variables.IsWikimediaProject && !Variables.IsWikimediaMonolingualProject)
            {
                string strTemp = Parsers.ChangeToDefaultSort(mArticleText, Name, out noChange, restrictDefaultsortAddition);
                if (skipIfNoChange && noChange)
                    Trace.AWBSkipped("No DefaultSort Added");
                else if (!noChange)
                    AWBChangeArticleText("DefaultSort Added/Diacritics cleaned", strTemp, true);
            }
        }
        public void SetDefaultSort(string langCode, bool skipIfNoChange)
        {
            SetDefaultSort(langCode, skipIfNoChange, true);
        }
        public void CiteTemplateDates(Parsers parsers, bool skipIfNoChange)
        {
            string strTemp = parsers.CiteTemplateDates(mArticleText, out noChange);
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No Citation template dates fixed");
            else if (!noChange)
                AWBChangeArticleText("Citation template dates fixed", strTemp, true);
        }
        public void FixPeopleCategories(Parsers parsers, bool skipIfNoChange)
        {
            bool noChange2;
            string strTemp = parsers.FixPeopleCategories(mArticleText, Name, true, out noChange);
            strTemp = Parsers.LivingPeople(strTemp, out noChange2);
            if (!noChange2)
                noChange = false;
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No human category changes");
            else if (!noChange)
                AWBChangeArticleText("Human category changes", strTemp, true);
        }
        public void FixLinks(bool skipIfNoChange)
        {
            string strTemp = Parsers.FixLinks(mArticleText, Name, out noChange);
            if (noChange && skipIfNoChange)
                Trace.AWBSkipped("No bad links");
            else if (!noChange)
                AWBChangeArticleText("Fixed links", strTemp, false);
        }
        public void BulletExternalLinks(bool skipIfNoChange)
        {
            string strTemp = Parsers.BulletExternalLinks(mArticleText, out noChange);
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No missing bulleted links");
            else if (!noChange)
                AWBChangeArticleText("Bulleted external links", strTemp, false);
        }
        public void EmboldenTitles(Parsers parsers, bool skipIfNoChange)
        {
            string strTemp = parsers.BoldTitle(mArticleText, Name, out noChange);
            if (skipIfNoChange && noChange)
                Trace.AWBSkipped("No Titles to embolden");
            else if (!noChange)
                AWBChangeArticleText("Emboldened titles", strTemp, false);
        }
        public void SendPageToCustomModule(IModule module)
        {
            IProcessArticleEventArgs processArticleEventArgs = this;
            string strEditSummary;
            bool skipArticle;
            string strTemp = module.ProcessArticle(processArticleEventArgs.ArticleText,
                                                   processArticleEventArgs.ArticleTitle, NameSpaceKey, out strEditSummary, out skipArticle);
            if (!skipArticle)
            {
                processArticleEventArgs.EditSummary = strEditSummary;
                processArticleEventArgs.Skip = false;
                AWBChangeArticleText("Custom module", strTemp, true);
                AppendPluginEditSummary();
            }
            else
                Trace.AWBSkipped("Skipped by custom module");
        }
        public bool Disambiguate(Session session, string dabLinkText, string[] dabVariantsLines, bool botMode, int context,
                                 bool skipIfNoChange)
        {
            Disambiguation.DabForm df = new Disambiguation.DabForm(session);
            string strTemp = df.Disambiguate(mArticleText, Name, dabLinkText,
                                             dabVariantsLines, context, botMode, out noChange);
            if (df.Abort) return false;
            if (noChange && skipIfNoChange)
                Trace.AWBSkipped("No disambiguation");
            else if (!noChange)
                AWBChangeArticleText("Disambiguated " + dabLinkText, strTemp, false);
            return true;
        }
        public void ChangeArticleText(string changedBy, string reason, string newText, bool checkIfChanged)
        {
            if (checkIfChanged && newText == mArticleText) return;
            mArticleText = newText;
            mAWBLogListener.WriteLine(reason, changedBy);
        }
        public void AWBChangeArticleText(string reason, string newText, bool checkIfChanged)
        {
            ChangeArticleText("AWB", reason, newText, checkIfChanged);
        }
        public void AWBChangeArticleText(string reason, string newText, bool checkIfChanged, bool performsSignificantChanges)
        {
            if (performsSignificantChanges && (newText != mArticleText))
                _generalFixesSignificantChange = true;
            AWBChangeArticleText(reason, newText, checkIfChanged);
        }
        public void PluginChangeArticleText(string newText)
        {
            mArticleText = newText;
        }
        public void AppendPluginEditSummary()
        {
            if (mPluginEditSummary.Length > 0)
            {
                AppendToSummary(mPluginEditSummary.Trim());
                mPluginEditSummary = "";
            }
        }
        public void HideText(HideText removeText)
        { mArticleText = removeText.Hide(mArticleText); }
        public void UnHideText(HideText removeText)
        { mArticleText = removeText.AddBack(mArticleText); }
        public void HideMoreText(HideText removeText)
        { mArticleText = removeText.HideMore(mArticleText); }
        public void UnHideMoreText(HideText removeText)
        { mArticleText = removeText.AddBackMore(mArticleText); }
        public override string ToString()
        {
            return Name;
        }
        public override int GetHashCode()
        {
            return Name.GetHashCode();
        }
        public override bool Equals(object obj)
        {
            Article a = obj as Article;
            if (a == null)
            {
                if (obj is string)
                    return Name == obj as string;
                return false;
            }
            return (this == a);
        }
        public bool Equals(Article a)
        {
            return (this == a);
        }
        public int CompareTo(Article other)
        {
            return string.Compare(Name, other.Name, false, System.Globalization.CultureInfo.InvariantCulture);
        }
        public static bool operator ==(Article a, Article b)
        {
            if (ReferenceEquals(a, b))
                return true;
            if ((object)a == null || (object)b == null)
                return false;
            return (a.Name == b.Name);
        }
        public static bool operator !=(Article a, Article b)
        {
            return !(a == b);
        }
        string IProcessArticleEventArgs.ArticleTitle
        { get { return Name; } }
        string IProcessArticleEventArgs.EditSummary
        { get { return mPluginEditSummary; } set { mPluginEditSummary = value.Trim(); } }
        bool IProcessArticleEventArgs.Skip
        { get { return mPluginSkip; } set { mPluginSkip = value; } }
        [XmlIgnore]
        public Exists Exists { get; protected set; }
        private bool _generalFixesCausedChange, _textAlreadyChanged, _generalFixesSignificantChange;
        private string _afterGeneralFixesArticleText;
        public void PerformGeneralFixes(Parsers parsers, HideText removeText, ISkipOptions skip, bool replaceReferenceTags, bool restrictDefaultsortAddition, bool noMOSComplianceFixes)
        {
            BeforeGeneralFixesTextChanged();
            AWBChangeArticleText("Fix dates", parsers.FixDates(ArticleText), false);
            Variables.Profiler.Profile("FixDates");
            HideText(removeText);
            Variables.Profiler.Profile("HideText");
            AWBChangeArticleText("Fixes for {{article issues}}", parsers.ArticleIssues(ArticleText, Name), true);
            Variables.Profiler.Profile("ArticleIssues");
            MinorFixes(Variables.LangCode, skip.SkipNoHeaderError);
            Variables.Profiler.Profile("MinorFixes");
            FixPeopleCategories(parsers, skip.SkipNoPeopleCategoriesFixed);
            Variables.Profiler.Profile("FixPeopleCategories");
            SetDefaultSort(Variables.LangCode, skip.SkipNoDefaultSortAdded, restrictDefaultsortAddition);
            Variables.Profiler.Profile("SetDefaultSort");
            AWBChangeArticleText("Fix categories", Parsers.FixCategories(ArticleText), true);
            Variables.Profiler.Profile("FixCategories");
            AWBChangeArticleText("Fix images", Parsers.FixImages(ArticleText), true);
            Variables.Profiler.Profile("FixImages");
            AWBChangeArticleText("Fix whitespace in links", Parsers.FixLinkWhitespace(ArticleText, Name), true);
            Variables.Profiler.Profile("FixLinkWhitespace");
            AWBChangeArticleText("Fix syntax", Parsers.FixSyntax(ArticleText), true, true);
            Variables.Profiler.Profile("FixSyntax");
            AWBChangeArticleText("Fix citation templates", Parsers.FixCitationTemplates(ArticleText), true, true);
            Variables.Profiler.Profile("FixCitationTemplates");
            AWBChangeArticleText("Fix temperatures", Parsers.FixTemperatures(ArticleText), true);
            Variables.Profiler.Profile("FixTemperatures");
            if (!noMOSComplianceFixes)
            {
                AWBChangeArticleText("Fix non-breaking spaces", parsers.FixNonBreakingSpaces(ArticleText), true);
                Variables.Profiler.Profile("FixNonBreakingSpaces");
            }
            AWBChangeArticleText("Fix main article", Parsers.FixMainArticle(ArticleText), true);
            Variables.Profiler.Profile("FixMainArticle");
            if (replaceReferenceTags)
            {
                AWBChangeArticleText("Fix reference tags", Parsers.FixReferenceListTags(ArticleText), true);
                Variables.Profiler.Profile("FixReferenceListTags");
            }
            AWBChangeArticleText("Fix empty links and templates", Parsers.FixEmptyLinksAndTemplates(ArticleText), true);
            Variables.Profiler.Profile("FixEmptyLinksAndTemplates");
            AWBChangeArticleText("FixReferenceTags", Parsers.FixReferenceTags(ArticleText), true);
            Variables.Profiler.Profile("FixReferenceTags");
            AWBChangeArticleText("DuplicateUnnamedReferences", Parsers.DuplicateUnnamedReferences(ArticleText), true);
            Variables.Profiler.Profile("DuplicateUnnamedReferences");
            AWBChangeArticleText("DuplicateNamedReferences", Parsers.DuplicateNamedReferences(ArticleText), true);
            Variables.Profiler.Profile("DuplicateNamedReferences");
            AWBChangeArticleText("SameRefDifferentName", Parsers.SameRefDifferentName(ArticleText), true);
            Variables.Profiler.Profile("SameRefDifferentName");
            AWBChangeArticleText("ReorderReferences", Parsers.ReorderReferences(ArticleText), true);
            Variables.Profiler.Profile("ReorderReferences");
            AWBChangeArticleText("Fix empty references", Parsers.SimplifyReferenceTags(ArticleText), true);
            Variables.Profiler.Profile("FixEmptyReferences");
            AWBChangeArticleText("Add missing {{reflist}}", Parsers.AddMissingReflist(ArticleText), true, true);
            Variables.Profiler.Profile("AddMissingReflist");
            CiteTemplateDates(parsers, skip.SkipNoCiteTemplateDatesFixed);
            Variables.Profiler.Profile("CiteTemplateDates");
            AWBChangeArticleText("Redirect tagger", Parsers.RedirectTagger(ArticleText, Name), false);
            Variables.Profiler.Profile("RedirectTagger");
            BulletExternalLinks(skip.SkipNoBulletedLink);
            Variables.Profiler.Profile("BulletExternalLinks");
            AWBChangeArticleText("Remove empty comments", Parsers.RemoveEmptyComments(ArticleText), false);
            Variables.Profiler.Profile("RemoveEmptyComments");
            if (!noMOSComplianceFixes)
            {
                AWBChangeArticleText("Mdashes", parsers.Mdashes(ArticleText, Name), true);
                Variables.Profiler.Profile("Mdashes");
                AWBChangeArticleText("Fix Date Ordinals/Of", parsers.FixDateOrdinalsAndOf(ArticleText, Name), true, true);
                Variables.Profiler.Profile("FixDateOrdinalsAndOf");
            }
            Variables.Profiler.Profile("Links");
            if (!Globals.UnitTestMode)
            {
                UnHideText(removeText);
                AWBChangeArticleText("Sort meta data",
                                     parsers.SortMetaData(ArticleText, Name), true);
                HideText(removeText);
                Variables.Profiler.Profile("Metadata");
            }
            EmboldenTitles(parsers, skip.SkipNoBoldTitle);
            FixLinks(skip.SkipNoBadLink);
            Variables.Profiler.Profile("FixLinks");
            AWBChangeArticleText("Simplify links", Parsers.SimplifyLinks(ArticleText), true);
            Variables.Profiler.Profile("SimplifyLinks");
            UnHideText(removeText);
            AfterGeneralFixesTextChanged();
            Variables.Profiler.Profile("End of general fixes");
        }
        public void PerformMetaDataSort(Parsers parsers)
        {
            if (!Globals.UnitTestMode && NameSpaceKey == Namespace.Mainspace)
                mArticleText = parsers.SortMetaData(ArticleText, Name);
        }
        private void BeforeGeneralFixesTextChanged()
        {
            _textAlreadyChanged = (ArticleText != OriginalArticleText);
        }
        private void AfterGeneralFixesTextChanged()
        {
            if (!_textAlreadyChanged)
            {
                _generalFixesCausedChange = (ArticleText != OriginalArticleText);
                if (_generalFixesCausedChange)
                    _afterGeneralFixesArticleText = ArticleText;
            }
        }
        public void PerformUserTalkGeneralFixes(HideText removeText, Regex userTalkTemplatesRegex, bool skipIfNoChange)
        {
            string originalText = ArticleText;
            HideText(removeText);
            Variables.Profiler.Profile("HideText");
            AWBChangeArticleText("Subst user talk warnings",
                                 Parsers.SubstUserTemplates(ArticleText, Name, userTalkTemplatesRegex), true);
            Variables.Profiler.Profile("SubstUserTemplates");
            UnHideText(removeText);
            Variables.Profiler.Profile("UnHideText");
            if (skipIfNoChange && (originalText == ArticleText))
            {
                Trace.AWBSkipped("No user talk templates subst'd");
            }
        }
        public void PerformTalkGeneralFixes()
        {
            BeforeGeneralFixesTextChanged();
            string articleText = ArticleText, newSummary = "";
            TalkPageHeaders.ProcessTalkPage(ref articleText, ref newSummary, DEFAULTSORT.NoChange);
            if (articleText != ArticleText)
            {
                AWBChangeArticleText("Talk Page general fixes", articleText, false);
                AppendToSummary(newSummary);
            }
            AfterGeneralFixesTextChanged();
        }
        [XmlIgnore]
        public bool IsRedirect
        {
            get { return Tools.IsRedirect(ArticleText); }
        }
        private static string _lastMove = "", _lastDelete = "", _lastProtect = "";
        public bool Move(Session session, out string newTitle)
        {
            using (ArticleActionDialog dlgArticleAction = new ArticleActionDialog(ArticleAction.Move))
            {
                dlgArticleAction.NewTitle = Name;
                dlgArticleAction.Summary = _lastMove;
                if (dlgArticleAction.ShowDialog() == DialogResult.OK
                    && Name != dlgArticleAction.NewTitle)
                {
                    _lastMove = dlgArticleAction.Summary;
                    session.Editor.SynchronousEditor.Move(Name, dlgArticleAction.NewTitle,
                                                          ArticleActionSummary(dlgArticleAction), true ,
                                                          dlgArticleAction.NoRedirect, dlgArticleAction.Watch);
                    newTitle = dlgArticleAction.NewTitle;
                    return true;
                }
                newTitle = "";
                return false;
            }
        }
        public bool Delete(Session session)
        {
            using (ArticleActionDialog dlgArticleAction = new ArticleActionDialog(ArticleAction.Delete))
            {
                dlgArticleAction.Summary = _lastDelete;
                if (dlgArticleAction.ShowDialog() == DialogResult.OK)
                {
                    _lastDelete = dlgArticleAction.Summary;
                    session.Editor.SynchronousEditor.Delete(Name, ArticleActionSummary(dlgArticleAction), dlgArticleAction.Watch);
                    return true;
                }
                return false;
            }
        }
        public bool Protect(Session session)
        {
            using (ArticleActionDialog dlgArticleAction = new ArticleActionDialog(ArticleAction.Protect))
            {
                dlgArticleAction.Summary = _lastProtect;
                dlgArticleAction.EditProtectionLevel = session.Page.EditProtection;
                dlgArticleAction.MoveProtectionLevel = session.Page.MoveProtection;
                if (dlgArticleAction.ShowDialog() == DialogResult.OK)
                {
                    _lastProtect = dlgArticleAction.Summary;
                    session.Editor.SynchronousEditor.Protect(Name,
                                                             ArticleActionSummary(dlgArticleAction),
                                                             dlgArticleAction.ProtectExpiry,
                                                             dlgArticleAction.EditProtectionLevel,
                                                             dlgArticleAction.MoveProtectionLevel,
                                                             dlgArticleAction.CascadingProtection,
                                                             dlgArticleAction.Watch);
                    return true;
                }
                return false;
            }
        }
        public static bool AddUsingAWBOnArticleAction;
        private static string ArticleActionSummary(ArticleActionDialog dlgArticleAction)
        {
            return AddUsingAWBOnArticleAction
                ? dlgArticleAction.Summary + " (" + Variables.SummaryTag.Trim() + ")"
                : dlgArticleAction.Summary;
        }
    }
}
