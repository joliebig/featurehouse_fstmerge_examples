using System.Drawing;
using System.Windows.Forms;
namespace WikiFunctions.Controls
{
    public enum Colour { Red, Green, Blue };
    public partial class LED : UserControl
    {
        private Brush br = Brushes.Red;
        private Colour col = Colour.Red;
        public LED()
        {
            InitializeComponent();
            DoubleBuffered = true;
        }
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            e.Graphics.FillEllipse(br, 2, 2, Size.Width - 2, Size.Height - 2);
        }
        public Colour Colour
        {
            get { return col; }
            set
            {
                switch (value)
                {
                    case Colour.Blue:
                        br = Brushes.Blue;
                        break;
                    case Colour.Green:
                        br = Brushes.Green;
                        break;
                    case Colour.Red:
                        br = Brushes.Red;
                        break;
                }
                col = value;
                base.Refresh();
            }
        }
    }
}
