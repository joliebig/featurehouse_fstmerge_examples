using System;
using System.Collections.Generic;
using System.Collections;
using System.Text;
using System.Windows.Forms;
namespace WikiFunctions.Controls.Lists
{
    public class ListBox2<T> : ListBox, IEnumerable<T>
    {
        private readonly static SaveFileDialog SaveListDialog;
        static ListBox2()
        {
            SaveListDialog = new SaveFileDialog()
                                 {
                                     DefaultExt = "txt",
                                     Filter =
                                         "Text file with wiki markup|*.txt|Plaintext list|*.txt|CSV (Comma Separated Values)|*.txt|CSV with Wikitext|*.txt",
                                     Title = "Save article list"
                                 };
        }
        public IEnumerator<T> GetEnumerator()
        {
            int i = 0;
            while (i < Items.Count)
            {
                yield return (T)Items[i];
                i++;
            }
        }
        IEnumerator IEnumerable.GetEnumerator()
        {
            int i = 0;
            while (i < Items.Count)
            {
                yield return Items[i];
                i++;
            }
        }
        public new void Sort()
        {
            BeginUpdate();
            Sorted = true;
            Sorted = false;
            EndUpdate();
        }
        private class ReverseComparer: IComparer
        {
            public int Compare (Object object1, Object object2)
            {
                return -((IComparable) object1).CompareTo (object2);
            }
        }
        public void ReverseSort()
        {
            BeginUpdate();
            Sorted = false;
            Article[] currentArticles = new Article[Items.Count];
            for (int i = 0; i < Items.Count; i++)
                currentArticles[i] = (Article)Items[i];
            Array.Sort(currentArticles, new ReverseComparer());
            Items.Clear();
            foreach (Article a in currentArticles)
                Items.Add(a);
            EndUpdate();
        }
        public void RemoveSelected()
        {
            BeginUpdate();
            int i = SelectedIndex;
            while (SelectedItems.Count > 0)
                Items.RemoveAt(SelectedIndex);
            if (Items.Count > i)
                SelectedIndex = i;
            else
                SelectedIndex = Math.Min(i, Items.Count) - 1;
            EndUpdate();
        }
        static string _list = "";
        public enum OutputFormat
        {
            WikiText = 1,
            PlainText = 2,
            Csv = 3,
            CsvWikiText = 4
        }
        public void SaveList()
        {
            if (_list.Length > 0) SaveListDialog.FileName = _list;
            if (SaveListDialog.ShowDialog() == DialogResult.OK)
            {
                _list = SaveListDialog.FileName;
                SaveList(_list, (OutputFormat)SaveListDialog.FilterIndex);
            }
        }
        public void SaveList(string filename, OutputFormat format)
        {
            try
            {
                StringBuilder list = new StringBuilder();
                switch (format)
                {
                    case OutputFormat.WikiText:
                        foreach (var a in this)
                            list.AppendLine("# [[:" + a + "]]");
                        break;
                    case OutputFormat.PlainText:
                        foreach (var a in this)
                            list.AppendLine(a.ToString());
                        break;
                    case OutputFormat.Csv:
                        foreach (var a in this)
                            list.Append(a + ", ");
                        list = list.Remove(list.Length - 2, 2);
                        break;
                    case OutputFormat.CsvWikiText:
                        foreach (var a in this)
                            list.Append("[[:" + a + "]], ");
                        list = list.Remove(list.Length - 2, 2);
                        break;
                }
                Tools.WriteTextFileAbsolutePath(list, filename, false);
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
        }
    }
    public class ListBoxString : ListBox2<string>
    {
    }
    public class ListBoxArticle : ListBox2<Article>
    {
    }
}
