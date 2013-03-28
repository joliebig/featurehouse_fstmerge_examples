using System;
using System.Drawing;
using System.Text.RegularExpressions;
using System.Collections.Generic;
using System.Windows.Forms;
namespace WikiFunctions.Controls
{
    public class ArticleTextBox : RichTextBox
    {
        public ArticleTextBox()
        {
            LanguageOption = RichTextBoxLanguageOptions.DualFont;
            InitializeComponent();
        }
        bool Locked;
        public override string Text
        {
            get { return base.Text.Replace("\n", "\r\n"); }
            set
            {
                Locked = true;
                base.Text = value;
                Locked = false;
            }
        }
        public override string SelectedText
        {
            get { return base.SelectedText.Replace("\n", "\r\n"); }
            set
            {
                Locked = true;
                base.SelectedText = value;
                Locked = false;
            }
        }
        public string RawText { get { return base.Text; } }
        protected override void OnTextChanged(EventArgs e)
        {
            if (!Locked) base.OnTextChanged(e);
        }
        protected override void OnHandleCreated(EventArgs e)
        {
            base.OnHandleCreated(e);
            if (!AutoWordSelection)
            {
                AutoWordSelection = true;
                AutoWordSelection = false;
            }
        }
        bool AutoKeyboardDisabled;
        protected override void OnEnter(EventArgs e)
        {
            if (!AutoKeyboardDisabled)
            {
                LanguageOption &= ~RichTextBoxLanguageOptions.AutoKeyboard;
                AutoKeyboardDisabled = true;
            }
            base.OnEnter(e);
        }
        private Regex RegexObj;
        private Match MatchObj;
        public void ResetFind()
        {
            RegexObj = null;
            MatchObj = null;
        }
        public void Find(string strRegex, bool isRegex, bool caseSensitive, string articleName)
        {
            string articleText = RawText;
            RegexOptions regOptions = caseSensitive ? RegexOptions.None : RegexOptions.IgnoreCase;
            strRegex = Tools.ApplyKeyWords(articleName, strRegex);
            if (!isRegex)
                strRegex = Regex.Escape(strRegex);
            if (MatchObj == null || RegexObj == null)
            {
                int findStart = SelectionStart;
                RegexObj = new Regex(strRegex, regOptions);
                MatchObj = RegexObj.Match(articleText, findStart);
                SelectionStart = MatchObj.Index;
                SelectionLength = MatchObj.Length;
            }
            else
            {
                if (MatchObj.NextMatch().Success)
                {
                    MatchObj = MatchObj.NextMatch();
                    SelectionStart = MatchObj.Index;
                    SelectionLength = MatchObj.Length;
                }
                else
                {
                    SelectionStart = 0;
                    SelectionLength = 0;
                    ResetFind();
                }
            }
            Focus();
            ScrollToCaret();
        }
        public Dictionary<int, int> FindAll(string strRegex, bool isRegex, bool caseSensitive, string articleName)
        {
            Dictionary<int, int> found = new Dictionary<int, int>();
            if (string.IsNullOrEmpty(strRegex))
                return found;
            string articleText = RawText;
            strRegex = Tools.ApplyKeyWords(articleName, strRegex);
            if (!isRegex)
                strRegex = Regex.Escape(strRegex);
            RegexObj = new Regex(strRegex, caseSensitive ? RegexOptions.None : RegexOptions.IgnoreCase);
            foreach (Match m in RegexObj.Matches(articleText))
            {
                found.Add(m.Index, m.Length);
            }
            return found;
        }
        public void SetEditBoxSelection(int inputIndex, int inputLength)
        {
            if (inputIndex >= 0 && inputLength > 0 && (inputIndex + inputLength) <= TextLength)
            {
                SelectionStart = inputIndex;
                SelectionLength = inputLength;
            }
            ScrollToCaret();
        }
        private void InitializeComponent()
        {
            SuspendLayout();
            DetectUrls = false;
            ResumeLayout(false);
        }
        public void HighlightSyntax()
        {
            Font currentFont = SelectionFont;
            Font boldFont = new Font(currentFont.FontFamily, currentFont.Size, FontStyle.Bold);
            Font italicFont = new Font(currentFont.FontFamily, currentFont.Size, FontStyle.Italic);
            Font boldItalicFont = new Font(currentFont.FontFamily, currentFont.Size, FontStyle.Bold | FontStyle.Italic);
            foreach (Match m in WikiRegexes.Heading.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[2].Index, m.Groups[2].Length);
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.NestedTemplates.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.LightGray;
            }
            foreach (Match m in WikiRegexes.StarRows.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.LightGray;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.TemplateName.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.DarkBlue;
            }
            foreach (Match m in WikiRegexes.Refs.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.LightGray;
            }
            foreach (Match m in WikiRegexes.ExternalLinks.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionColor = Color.Blue;
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.FileNamespaceLink.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.LightGreen;
            }
            foreach (Match m in WikiRegexes.Italics.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionFont = italicFont;
            }
            foreach (Match m in WikiRegexes.Bold.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionFont = currentFont;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.BoldItalics.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionFont = currentFont;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionFont = boldItalicFont;
            }
            foreach (Match m in WikiRegexes.PipedWikiLink.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[2].Index, m.Groups[2].Length);
                SelectionColor = Color.Blue;
                SelectionFont = boldFont;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.Blue;
            }
            foreach (Match m in WikiRegexes.UnPipedWikiLink.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.Blue;
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.WikiLinksOnlyPlusWord.Matches(RawText))
            {
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.Blue;
                SelectionFont = boldFont;
            }
            foreach (Match m in WikiRegexes.Category.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.LightGray;
                SelectionFont = currentFont;
                SelectionColor = Color.Black;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.Blue;
            }
            foreach (Match m in WikiRegexes.PossibleInterwikis.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.Gray;
                SelectionFont = currentFont;
                SetEditBoxSelection(m.Groups[2].Index, m.Groups[2].Length);
                SelectionColor = Color.Blue;
                SetEditBoxSelection(m.Groups[1].Index, m.Groups[1].Length);
                SelectionColor = Color.Black;
            }
            foreach (Match m in WikiRegexes.Comments.Matches(RawText))
            {
                SetEditBoxSelection(m.Index, m.Length);
                SelectionBackColor = Color.PaleGoldenrod;
            }
        }
    }
}
