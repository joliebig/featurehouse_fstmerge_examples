using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Text.RegularExpressions;
using WikiFunctions;
using WikiFunctions.AWBSettings;
namespace AutoWikiBrowser.Plugins.TheTemplator
{
    public class TheTemplator : WikiFunctions.Plugin.IAWBPlugin
    {
        private readonly ToolStripMenuItem pluginMenuItem = new ToolStripMenuItem("TheTemplator plugin");
        private readonly ToolStripMenuItem pluginConfigMenuItem = new ToolStripMenuItem("&Configuration...");
        private readonly ToolStripMenuItem aboutMenuItem2 = new ToolStripMenuItem("About TheTemplator plugin...");
        public void Initialise(WikiFunctions.Plugin.IAutoWikiBrowser sender)
        {
            if (sender == null)
                throw new ArgumentNullException("sender");
            AWB = sender;
            pluginMenuItem.CheckedChanged += PluginEnabledCheckedChange;
            pluginConfigMenuItem.Click += ShowSettings;
            aboutMenuItem2.Click += AboutMenuItemClicked;
            pluginEnabledMenuItem.CheckOnClick = true;
            pluginConfigMenuItem.Click += ShowSettings;
            pluginEnabledMenuItem.CheckedChanged += PluginEnabledCheckedChange;
            aboutMenuItem1.Click += AboutMenuItemClicked;
            pluginMenuItem.DropDownItems.Add(pluginEnabledMenuItem);
            pluginMenuItem.DropDownItems.Add(pluginConfigMenuItem);
            pluginMenuItem.DropDownItems.Add("-");
            pluginMenuItem.DropDownItems.Add(aboutMenuItem1);
            sender.PluginsToolStripMenuItem.DropDownItems.Add(pluginMenuItem);
            sender.HelpToolStripMenuItem.DropDownItems.Add(aboutMenuItem2);
            TemplatorConfig dlg = new TemplatorConfig(defaultSettings.TemplateName,
                                                      defaultSettings.Parameters,
                                                      defaultSettings.Replacements,
                                                      defaultSettings.SkipIfNoTemplates,
                                                      defaultSettings.RemoveExcessPipes);
            defaultSettings.dlgWidth = dlg.Width;
            defaultSettings.dlgHeight = dlg.Height;
            defaultSettings.dlgCol0 = dlg.paramName.Width;
            defaultSettings.dlgCol1 = dlg.paramRegex.Width;
        }
        public void LoadSettings(object[] prefs)
        {
            if (prefs == null)
                return;
            Settings = defaultSettings;
            Settings.Parameters = new Dictionary<string, string>();
            Settings.Replacements = new Dictionary<string, string>();
            foreach (object o in prefs)
            {
                PrefsKeyPair p = o as PrefsKeyPair;
                if (p == null)
                    continue;
                switch (p.Name.ToLower())
                {
                case "enabled":
                    Settings.Enabled = (bool)p.Setting;
                    pluginEnabledMenuItem.Checked = Settings.Enabled;
                    break;
                case "xspipes":
                    Settings.RemoveExcessPipes = (bool)p.Setting;
                    break;
                case "skip":
                    Settings.SkipIfNoTemplates = (bool)p.Setting;
                    break;
                case "template":
                    Settings.TemplateName = (string)p.Setting;
                    break;
                case "dlgwidth":
                    Settings.dlgWidth = (int)p.Setting;
                    break;
                case "dlgheight":
                    Settings.dlgHeight = (int)p.Setting;
                    break;
                case "dlgcol0":
                    Settings.dlgCol0 = (int)p.Setting;
                    break;
                case "dlgcol1":
                    Settings.dlgCol1 = (int)p.Setting;
                    break;
                default:
                    if (p.Name.StartsWith(":"))
                        Settings.Parameters[p.Name.Substring(1)] = (string)p.Setting;
                    else if (p.Name.StartsWith(";"))
                        Settings.Replacements[p.Name.Substring(1)] = (string)p.Setting;
                    break;
                }
            }
            RegexString = "";
        }
        public object[] SaveSettings()
        {
            List<PrefsKeyPair> settings = new List<PrefsKeyPair>()
                                              {
                                                  new PrefsKeyPair("enabled", Settings.Enabled),
                                                  new PrefsKeyPair("xspipes", Settings.RemoveExcessPipes),
                                                  new PrefsKeyPair("skip", Settings.SkipIfNoTemplates),
                                                  new PrefsKeyPair("template", Settings.TemplateName),
                                                  new PrefsKeyPair("dlgwidth", Settings.dlgWidth),
                                                  new PrefsKeyPair("dlgheight", Settings.dlgHeight),
                                                  new PrefsKeyPair("dlgcol0", Settings.dlgCol0),
                                                  new PrefsKeyPair("dlgcol1", Settings.dlgCol1)
                                              };
            foreach (KeyValuePair<string, string> p in Settings.Parameters)
                settings.Add(new PrefsKeyPair(":" + p.Key, p.Value));
            foreach (KeyValuePair<string, string> p in Settings.Replacements)
                settings.Add(new PrefsKeyPair(";" + p.Key, p.Value));
            return settings.ToArray();
        }
        public string Name
        {
            get { return PluginName; }
        }
        public void Nudge(out bool cancel)
        {
            cancel = false;
        }
        public void Nudged(int nudges)
        {
        }
        public string ProcessArticle(WikiFunctions.Plugin.IAutoWikiBrowser sender, WikiFunctions.Plugin.IProcessArticleEventArgs eventargs)
        {
            string text = eventargs.ArticleText;
            if (!Settings.Enabled)
                return text;
            List<Match> matches = WikiFunctions.Parse.Parsers.GetTemplates(text, Settings.TemplateName);
            if (matches.Count == 0)
            {
                eventargs.Skip = Settings.SkipIfNoTemplates;
                return text;
            }
            if (RegexString == "")
                BuildRegexes();
            if (RegexString == "")
            {
                eventargs.Skip = Settings.SkipIfNoTemplates;
                return text;
            }
            int deltaLength = 0;
            foreach (Match match in matches)
            {
                string matchSegment = match.Value;
                if (matchSegment.Contains("{|"))
                {
                    MessageBox.Show("Infobox contains a wikitable:\r\n\r\n" + text, eventargs.ArticleTitle);
                    eventargs.Skip = Settings.SkipIfNoTemplates;
                    return text;
                }
                if (Settings.RemoveExcessPipes)
                    if (ExcessPipe.IsMatch(matchSegment))
                        matchSegment = ExcessPipe.Replace(matchSegment, RemovePipeReplacement);
                if (!findParametersRegex.IsMatch(matchSegment))
                {
                    MessageBox.Show(string.Format("Bad template matched:\r\n\r\n{0}", matchSegment), eventargs.ArticleTitle);
                    continue;
                }
                Match m = findParametersRegex.Match(matchSegment);
                System.Diagnostics.Debug.Assert(m.Success);
                string paramPipeStr = "|";
                string paramEqualsStr = "=";
                int paramDesiredLength = 0;
                string trailingWhitespace = "";
                foreach (KeyValuePair<string, string> param in Settings.Parameters)
                {
                    string paramSegment = m.Groups["__" + param.Key].ToString();
                    if (paramSegment != "")
                    {
                        paramPipeStr = paramPipeRegex.Match(paramSegment).Value;
                        string paramPatternMatch = paramEqualsRegex.Match(paramSegment).Value;
                        paramDesiredLength = paramPatternMatch.Length + param.Key.Length;
                        paramEqualsStr = paramPatternMatch.PadLeft(paramDesiredLength);
                        string valueSegment = m.Groups["_" + param.Key].ToString();
                        if (valueSegment == "")
                            valueSegment = paramSegment;
                        trailingWhitespace = trailingWhiteSpaceRegex.Match(valueSegment).Value;
                        break;
                    }
                }
                string paramSegments = "";
                foreach (KeyValuePair<string, string> param in Settings.Parameters)
                {
                    string paramSegment = m.Groups["__" + param.Key].ToString();
                    if (paramSegment == "")
                        paramSegments += "|" + param.Key + "=";
                    else
                        paramSegments += paramSegment + m.Groups["_" + param.Key];
                }
                paramSegments += "|";
                string paramReplacementStr = "";
                foreach (KeyValuePair<string, string> param in Settings.Replacements)
                {
                    string equalsStr;
                    if (trailingWhitespace.Contains("\n"))
                    {
                        int spaceLeft = paramDesiredLength - param.Key.Length;
                        if (spaceLeft <= 0)
                        {
                            equalsStr = "=";
                        }
                        else
                        {
                            equalsStr = paramEqualsStr;
                            int indexOfEquals = equalsStr.IndexOf('=');
                            equalsStr = equalsStr.Substring(Math.Min(indexOfEquals, paramDesiredLength - spaceLeft));
                            if (equalsStr.Length > spaceLeft)
                                equalsStr = equalsStr.Remove(spaceLeft);
                        }
                    }
                    else
                        equalsStr = paramEqualsStr;
                    paramReplacementStr += paramPipeStr + param.Key + equalsStr + param.Value + trailingWhitespace;
                }
                string paramReplacement = paramReplacementRegex.Replace(paramSegments, paramReplacementStr);
                paramReplacement = paramReplacement.Remove(paramReplacement.Length - 1);
                if (WikiRegexes.WhiteSpace.Replace(paramSegments, "") == WikiRegexes.WhiteSpace.Replace(paramReplacement, ""))
                    continue;
                int firstIndex = match.Length;
                foreach (KeyValuePair<string, string> param in Settings.Parameters)
                {
                    Regex segmentRegex = paramRemovalRegexes[param.Key];
                    Match segmentMatch = segmentRegex.Match(matchSegment);
                    if (segmentMatch.Success)
                    {
                        matchSegment = segmentRegex.Replace(matchSegment, "");
                        if (segmentMatch.Index < firstIndex)
                            firstIndex = segmentMatch.Index;
                    }
                }
                if (firstIndex == match.Length)
                {
                    continue;
                }
                matchSegment = matchSegment.Substring(0, firstIndex)
                             + paramReplacement
                             + matchSegment.Substring(firstIndex);
                text = text.Substring(0, match.Index + deltaLength)
                     + matchSegment
                     + text.Substring(match.Index + match.Length + deltaLength);
                deltaLength += matchSegment.Length - match.Length;
            }
            return text;
        }
        public void Reset()
        {
            Settings = defaultSettings;
            Settings.Parameters = new Dictionary<string, string>();
            Settings.Replacements = new Dictionary<string, string>();
        }
        public string WikiName
        {
            get { return Name + " Plugin version " + Version; }
        }
        private void BuildRegexes()
        {
            RegexString = "";
            if (Settings.Parameters.Count == 0)
                return;
            foreach (KeyValuePair<string, string> param in Settings.Parameters)
            {
                string name = nonIdentiferCharsRegex.Replace(param.Key, "_");
                string regexGroup
                    = @"(?<__" + name + @">\|\s*"
                    + param.Key + @"\s*=\s*)"
                    + @"(?=\S)"
                    + @"(?<_" + name + ">("
                    + WikiRegexes.UnformattedText
                    + @"|"
                    + WikiRegexes.NestedTemplates
                    + @"|"
                    + WikiRegexes.SimpleWikiLink
                    + @"|"
                    + BorkedExternalLinks
                    + @"|"
                    + @"[^\[\{\|\}]*"
                    + @")*"
                    + @")"
                    + @"(?=[\|\}])";
                RegexString += regexGroup + "|";
            }
            string rest
                = @"(\|\s*)"
                + @"(([^=\s]*)\s*=\s*(?=\S))?"
                + @"("
                + WikiRegexes.UnformattedText
                + @"|"
                + WikiRegexes.NestedTemplates
                + @"|"
                + WikiRegexes.SimpleWikiLink
                + @"|"
                + BorkedExternalLinks
                + @"|"
                + @"[^\[\{\|\}]*"
                + @")*(?=[\|\}])";
            RegexString = @"\{\{[^|]*" + ("(" + RegexString + rest + ")*") + @"\}\}";
            findParametersRegex = new Regex(RegexString, RegexOptions.Compiled | RegexOptions.ExplicitCapture | RegexOptions.Singleline);
            paramRemovalRegexes = new Dictionary<string, Regex>(Settings.Parameters.Count);
            foreach (KeyValuePair<string, string> param in Settings.Parameters)
            {
                string paramRegexString
                        = @"(\|\s*"
                        + param.Key + @"\s*=\s*(?=\S)"
                        + @"("
                        + WikiRegexes.UnformattedText
                        + @"|"
                        + WikiRegexes.NestedTemplates
                        + @"|"
                        + WikiRegexes.SimpleWikiLink
                        + @"|"
                        + BorkedExternalLinks
                        + @"|"
                        + @"[^\[\{\|\}]*"
                        + @")*)(?=[\|\}])";
                paramRemovalRegexes[param.Key] = new Regex(paramRegexString, RegexOptions.Compiled | RegexOptions.ExplicitCapture | RegexOptions.Singleline);
            }
            string paramMatcher = "";
            foreach (KeyValuePair<string, string> param in Settings.Parameters)
                paramMatcher += @"\|\s*" + param.Key + @"\s*=\s*(" + param.Value + @"\s*)?(?=\S)";
            paramReplacementRegex = new Regex(paramMatcher, RegexOptions.Compiled);
        }
        void ShowSettings(object o, EventArgs e)
        {
            TemplatorConfig dlg = new TemplatorConfig(Settings.TemplateName,
                                                      Settings.Parameters,
                                                      Settings.Replacements,
                                                      Settings.SkipIfNoTemplates,
                                                      Settings.RemoveExcessPipes)
                                      {
                                          Width = Settings.dlgWidth,
                                          Height = Settings.dlgHeight
                                      };
            dlg.paramName.Width = dlg.replacementParamName.Width = Settings.dlgCol0;
            dlg.paramRegex.Width = dlg.replacementExpression.Width = Settings.dlgCol1;
            if (dlg.ShowDialog() == DialogResult.OK)
            {
                Settings.TemplateName = dlg.TemplateName;
                Settings.SkipIfNoTemplates = dlg.SkipIfNone;
                Settings.RemoveExcessPipes = dlg.RemoveExcessPipes;
                Settings.Parameters = dlg.Parameters;
                Settings.Replacements = dlg.Replacements;
                Settings.dlgWidth = dlg.Width;
                Settings.dlgHeight = dlg.Height;
                Settings.dlgCol0 = dlg.paramName.Width;
                Settings.dlgCol1 = dlg.paramRegex.Width;
                RegexString = "";
            }
        }
        void PluginEnabledCheckedChange(object o, EventArgs e)
        {
            System.Diagnostics.Debug.Assert(o is ToolStripMenuItem);
            Settings.Enabled = (o as ToolStripMenuItem).Checked;
        }
        void AboutMenuItemClicked(object o, EventArgs e)
        {
            new AboutBox().ShowDialog();
        }
        public class TemplatorSettings
        {
            public bool Enabled = false;
            public bool SkipIfNoTemplates = false;
            public bool RemoveExcessPipes = false;
            public string TemplateName = "";
            public Dictionary<string, string> Parameters = new Dictionary<string, string>();
            public Dictionary<string, string> Replacements = new Dictionary<string, string>();
            public int dlgWidth;
            public int dlgHeight;
            public int dlgCol0;
            public int dlgCol1;
        }
        private string RegexString = "";
        private Regex findParametersRegex = null;
        private Regex paramReplacementRegex = null;
        internal static readonly Regex nonIdentiferCharsRegex = new Regex("[^a-z0-9_]", RegexOptions.Compiled);
        internal static readonly Regex ExcessPipe = new Regex(@"\|(?<losepipe>\s*[|}])", RegexOptions.Compiled);
        internal static readonly string RemovePipeReplacement = @" ${losepipe}";
        internal static readonly Regex BorkedExternalLinks = new Regex(@"\[.*?\]", RegexOptions.Compiled);
        private Dictionary<string, Regex> paramRemovalRegexes = null;
        internal static readonly Regex paramPipeRegex = new Regex(@"\s*\|\s*", RegexOptions.Compiled);
        internal static readonly Regex paramEqualsRegex = new Regex(@"(?<=\w)\s*=\s*", RegexOptions.Compiled);
        internal static readonly Regex leadingWhiteSpaceRegex = new Regex(@"^\s*", RegexOptions.Compiled);
        internal static readonly Regex trailingWhiteSpaceRegex = new Regex(@"(?<=\S)\s*$", RegexOptions.Compiled | RegexOptions.Singleline);
        private static WikiFunctions.Plugin.IAutoWikiBrowser AWB;
        internal static TemplatorSettings Settings = new TemplatorSettings();
        private static readonly TemplatorSettings defaultSettings = new TemplatorSettings();
        private static readonly string PluginName = System.Reflection.Assembly.GetExecutingAssembly().GetName().Name;
        private static readonly string Version = System.Reflection.Assembly.GetExecutingAssembly().GetName().Version.ToString();
    }
}
