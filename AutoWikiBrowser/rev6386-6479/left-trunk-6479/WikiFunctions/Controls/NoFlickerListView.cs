using System;
using System.ComponentModel;
using System.Windows.Forms;
using System.Collections;
namespace WikiFunctions.Controls
{
    public interface IListViewItemComparerFactory
    {
        IComparer CreateComparer(int column, SortOrder order);
    }
    public class NoFlickerExtendedListView : ListView, IListViewItemComparerFactory
    {
        public NoFlickerExtendedListView()
            : this(false, false)
        { }
        public NoFlickerExtendedListView(bool sortColumnOnClick, bool resizeColumnsOnControlResize)
        {
            SortColumnsOnClick = sortColumnOnClick;
            ResizeColumsOnControlResize = resizeColumnsOnControlResize;
            sortColumnsOnClick = sortColumnOnClick;
            DoubleBuffered = true;
            comparerFactory = this;
        }
        private void NoFlickerExtendedListView_Resize(object sender, EventArgs e)
        {
            ResizeColumns(true);
        }
        private bool ResizeColumnsOnControlResize;
        [DefaultValue(false)]
        public bool ResizeColumsOnControlResize
        {
            set
            {
                if (value && !ResizeColumnsOnControlResize)
                    Resize += NoFlickerExtendedListView_Resize;
                else if (!value && ResizeColumnsOnControlResize)
                    Resize -= NoFlickerExtendedListView_Resize;
                ResizeColumnsOnControlResize = value;
            }
            get { return ResizeColumnsOnControlResize; }
        }
        private bool sortColumnsOnClick;
        [DefaultValue(false)]
        public bool SortColumnsOnClick
        {
            set
            {
                if (value && !sortColumnsOnClick)
                    ColumnClick += ExtendedListView_ColumnClick;
                else if (!value && sortColumnsOnClick)
                    ColumnClick -= ExtendedListView_ColumnClick;
                sortColumnsOnClick = value;
            }
            get { return sortColumnsOnClick; }
        }
        private int sortColumn;
        private void ExtendedListView_ColumnClick(object sender, ColumnClickEventArgs e)
        {
            try
            {
                BeginUpdate();
                if (e.Column != sortColumn)
                {
                    sortColumn = e.Column;
                    Sorting = SortOrder.Ascending;
                }
                else
                {
                    Sorting = Sorting == SortOrder.Ascending ? SortOrder.Descending : SortOrder.Ascending;
                }
                Sort();
                ListViewItemSorter = comparerFactory.CreateComparer(e.Column, Sorting);
                EndUpdate();
            }
            catch { }
        }
        public void ResizeColumns()
        {
            foreach (ColumnHeader head in Columns)
            {
                head.AutoResize(ColumnHeaderAutoResizeStyle.HeaderSize);
                int width = head.Width;
                head.AutoResize(ColumnHeaderAutoResizeStyle.ColumnContent);
                int width2 = head.Width;
                if (width2 < width)
                    head.AutoResize(ColumnHeaderAutoResizeStyle.HeaderSize);
            }
        }
        private IListViewItemComparerFactory comparerFactory;
        [Browsable(false)]
        [Localizable(false)]
        public IListViewItemComparerFactory ComparerFactory
        {
            get { return comparerFactory; }
            set
            {
                comparerFactory = value;
                ListViewItemSorter = comparerFactory.CreateComparer(sortColumn, Sorting);
            }
        }
        public IComparer CreateComparer(int column, SortOrder order)
        {
            return new ListViewItemComparer(column, order);
        }
        public void ResizeColumns(bool useUpdateMethods)
        {
            if (useUpdateMethods)
                BeginUpdate();
            ResizeColumns();
            if (useUpdateMethods)
                EndUpdate();
        }
        sealed class ListViewItemComparer : IComparer
        {
            private readonly int Col;
            private readonly SortOrder Order;
            public ListViewItemComparer()
            {
                Order = SortOrder.Ascending;
            }
            public ListViewItemComparer(int column, SortOrder order)
            {
                Col = column;
                Order = order;
            }
            public int Compare(object x, object y)
            {
                int returnVal;
                string sx = ((ListViewItem) x).SubItems[Col].Text;
                string sy = ((ListViewItem) y).SubItems[Col].Text;
                DateTime firstDate, secondDate;
                double dblX, dblY;
                if (double.TryParse(sx, out dblX) && double.TryParse(sy, out dblY))
                {
                    returnVal = dblX.CompareTo(dblY);
                }
                else
                    if (DateTime.TryParse(sx, out firstDate) && DateTime.TryParse(sy, out secondDate))
                    {
                        returnVal = DateTime.Compare(firstDate, secondDate);
                    }
                    else
                    {
                        returnVal = String.Compare(sx, sy);
                    }
                if (Order == SortOrder.Descending)
                    returnVal *= -1;
                return returnVal;
            }
        }
    }
}
