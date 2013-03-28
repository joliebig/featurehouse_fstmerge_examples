using System;
using System.Windows.Forms;
namespace Genghis.Windows.Forms
{
    public class CursorChanger : IDisposable
    {
        public CursorChanger(Cursor newCursor)
        {
            _originalCursor = Cursor.Current;
            Cursor.Current = newCursor;
        }
        ~CursorChanger()
        {
            Dispose();
        }
        public void Dispose()
        {
            Cursor.Current = _originalCursor;
        }
        Cursor _originalCursor;
    }
}
