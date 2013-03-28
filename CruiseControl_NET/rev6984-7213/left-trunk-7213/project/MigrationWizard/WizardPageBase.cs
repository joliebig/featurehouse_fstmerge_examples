using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    public partial class WizardPageBase
        : UserControl
    {
        private WizardPageBase previousPage;
        private WizardPageBase nextPage;
        private bool isValid;
        private bool headerVisible;
        private int headerHeight;
        private Color headerBackgroundColour;
        private Color headerBorderColour;
        private Color headerTitleColour;
        private string headerTitle;
        private Padding headerTitlePadding;
        private Font headerTitleFont;
        private string headerText;
        private Color headerTextColour;
        private Padding headerTextPadding;
        private Font headerTextFont;
        private Image headerImage;
        private Padding headerImagePadding;
        public WizardPageBase()
        {
            InitializeComponent();
            CanCancel = true;
            ConfirmCancel = true;
            IsValid = true;
            HeaderVisible = true;
            UseFormIcon = true;
            HeaderHeight = 48;
            ResetHeaderBackgroundColour();
            ResetHeaderBorderColour();
            ResetHeaderTitleColour();
            ResetHeaderTitlePadding();
            ResetHeaderTitleFont();
            ResetHeaderTextColour();
            ResetHeaderTextPadding();
            ResetHeaderTextFont();
            ResetHeaderImagePadding();
        }
        [Browsable(false)]
        public WizardPageBase PreviousPage
        {
            get { return previousPage; }
            set
            {
                if (!object.ReferenceEquals(previousPage, value))
                {
                    previousPage = value;
                    if (PreviousPageChanged != null) PreviousPageChanged(this, EventArgs.Empty);
                }
            }
        }
        [Browsable(false)]
        public WizardPageBase NextPage
        {
            get { return nextPage; }
            set
            {
                if (!object.ReferenceEquals(previousPage, value))
                {
                    nextPage = value;
                    if (NextPageChanged != null) NextPageChanged(this, EventArgs.Empty);
                }
            }
        }
        [DisplayName("Can Cancel Wizard")]
        [Description("Can the wizard be cancelled from_ this page. This will control whether the cancel button is enabled or not.")]
        [Category("Wizard Control")]
        [DefaultValue(true)]
        public bool CanCancel { get; set; }
        [DisplayName("Comfirm Cancel")]
        [Description("Should the user be prompted if they want to cancel. This allows for the user accidently clicking on the cancel button.")]
        [Category("Wizard Control")]
        [DefaultValue(true)]
        public bool ConfirmCancel { get; set; }
        [DisplayName("Can Finish Wizard")]
        [Description("Can the wizard be finished from_ this page. This will control whether the finish button is enabled or not.")]
        [Category("Wizard Control")]
        [DefaultValue(false)]
        public bool CanFinish { get; set; }
        [Browsable(false)]
        public bool IsValid
        {
            get { return isValid; }
            set
            {
                isValid = value;
                if (NextPageChanged != null) NextPageChanged(this, EventArgs.Empty);
            }
        }
        [DisplayName("Header Visible")]
        [Description("Is the wizard header visible. This will be displayed as a band across the top of the page.")]
        [Category("Wizard Display")]
        [DefaultValue(true)]
        public bool HeaderVisible
        {
            get { return headerVisible; }
            set
            {
                headerVisible = value;
                Invalidate();
            }
        }
        [DisplayName("Header Height")]
        [Description("The height of the header band.")]
        [Category("Wizard Display")]
        [DefaultValue(48)]
        public int HeaderHeight
        {
            get { return headerHeight; }
            set
            {
                headerHeight = value;
                Invalidate();
            }
        }
        [DisplayName("Header Background Colour")]
        [Description("The background colour of the header band.")]
        [Category("Wizard Display")]
        public Color HeaderBackgroundColour
        {
            get { return headerBackgroundColour; }
            set
            {
                headerBackgroundColour = value;
                Invalidate();
            }
        }
        public void ResetHeaderBackgroundColour()
        {
            HeaderBackgroundColour = SystemColors.Window;
        }
        public bool ShouldSerializeHeaderBackgroundColour()
        {
            return (HeaderBackgroundColour != SystemColors.Window);
        }
        [DisplayName("Header Border Colour")]
        [Description("The border colour of the header band.")]
        [Category("Wizard Display")]
        public Color HeaderBorderColour
        {
            get { return headerBorderColour; }
            set
            {
                headerBorderColour = value;
                Invalidate();
            }
        }
        public void ResetHeaderBorderColour()
        {
            HeaderBorderColour = SystemColors.WindowFrame;
        }
        public bool ShouldSerializeHeaderBorderColour()
        {
            return (HeaderBorderColour != SystemColors.WindowFrame);
        }
        [DisplayName("Header Title")]
        [Description("The title for the header band.")]
        [Category("Wizard Display")]
        [DefaultValue(null)]
        public string HeaderTitle
        {
            get { return headerTitle; }
            set
            {
                headerTitle = value;
                Invalidate();
            }
        }
        [DisplayName("Header Title Colour")]
        [Description("The colour of the header title.")]
        [Category("Wizard Display")]
        public Color HeaderTitleColour
        {
            get { return headerTitleColour; }
            set
            {
                headerTitleColour = value;
                Invalidate();
            }
        }
        public void ResetHeaderTitleColour()
        {
            HeaderTitleColour = SystemColors.WindowText;
        }
        public bool ShouldSerializeHeaderTitleColour()
        {
            return (HeaderTitleColour != SystemColors.WindowText);
        }
        [DisplayName("Header Title Padding")]
        [Description("The padding around the header title.")]
        [Category("Wizard Display")]
        public Padding HeaderTitlePadding
        {
            get { return headerTitlePadding; }
            set
            {
                headerTitlePadding = value;
                Invalidate();
            }
        }
        public void ResetHeaderTitlePadding()
        {
            HeaderTitlePadding = new Padding(5);
        }
        public bool ShouldSerializeHeaderTitlePadding()
        {
            return (HeaderTitlePadding.Left != 5) ||
                (HeaderTitlePadding.Top != 5) ||
                (HeaderTitlePadding.Right != 5) ||
                (HeaderTitlePadding.Bottom != 5);
        }
        [DisplayName("Header Title Font")]
        [Description("The font for the header title.")]
        [Category("Wizard Display")]
        public Font HeaderTitleFont
        {
            get { return headerTitleFont; }
            set
            {
                headerTitleFont = value;
                Invalidate();
            }
        }
        public void ResetHeaderTitleFont()
        {
            HeaderTitleFont = new Font(Font, FontStyle.Bold);
        }
        public bool ShouldSerializeHeaderTitleFont()
        {
            return (HeaderTitleFont.Style != FontStyle.Bold) ||
                (HeaderTitleFont.Size != Font.Size) ||
                (HeaderTitleFont.Name != Font.Name) ||
                (HeaderTitleFont.FontFamily != Font.FontFamily);
        }
        [DisplayName("Header Text")]
        [Description("The text for the header band.")]
        [Category("Wizard Display")]
        [DefaultValue(null)]
        public string HeaderText
        {
            get { return headerText; }
            set
            {
                headerText = value;
                Invalidate();
            }
        }
        [DisplayName("Header Text Colour")]
        [Description("The colour of the header text.")]
        [Category("Wizard Display")]
        public Color HeaderTextColour
        {
            get { return headerTextColour; }
            set
            {
                headerTextColour = value;
                Invalidate();
            }
        }
        public void ResetHeaderTextColour()
        {
            HeaderTextColour = SystemColors.WindowText;
        }
        public bool ShouldSerializeHeaderTextColour()
        {
            return (HeaderTextColour != SystemColors.WindowText);
        }
        [DisplayName("Header Text Padding")]
        [Description("The padding around the header text.")]
        [Category("Wizard Display")]
        public Padding HeaderTextPadding
        {
            get { return headerTextPadding; }
            set
            {
                headerTextPadding = value;
                Invalidate();
            }
        }
        public void ResetHeaderTextPadding()
        {
            HeaderTextPadding = new Padding(5, 0, 5, 5);
        }
        public bool ShouldSerializeHeaderTextPadding()
        {
            return (HeaderTextPadding.Left != 5) ||
                (HeaderTextPadding.Top != 0) ||
                (HeaderTextPadding.Right != 5) ||
                (HeaderTextPadding.Bottom != 5);
        }
        [DisplayName("Header Text Font")]
        [Description("The font for the header text.")]
        [Category("Wizard Display")]
        public Font HeaderTextFont
        {
            get { return headerTextFont; }
            set
            {
                headerTextFont = value;
                Invalidate();
            }
        }
        public void ResetHeaderTextFont()
        {
            HeaderTextFont = new Font(Font, FontStyle.Regular);
        }
        public bool ShouldSerializeHeaderTextFont()
        {
            return (HeaderTextFont.Style != FontStyle.Regular) ||
                (HeaderTextFont.Size != Font.Size) ||
                (HeaderTextFont.Name != Font.Name) ||
                (HeaderTextFont.FontFamily != Font.FontFamily);
        }
        [DisplayName("Use Form Icon")]
        [Description("Should the form icon be used if no image is set. If this is off and no images is selected, then no image will be displayed.")]
        [Category("Wizard UI")]
        [DefaultValue(true)]
        public bool UseFormIcon { get; set; }
        [DisplayName("Header Image")]
        [Description("The image to display in the header band.")]
        [Category("Wizard Display")]
        [DefaultValue(null)]
        public Image HeaderImage
        {
            get { return headerImage; }
            set
            {
                headerImage = value;
                Invalidate();
            }
        }
        [DisplayName("Header Text Padding")]
        [Description("The padding around the header image.")]
        [Category("Wizard Display")]
        public Padding HeaderImagePadding
        {
            get { return headerImagePadding; }
            set
            {
                headerImagePadding = value;
                Invalidate();
            }
        }
        public void ResetHeaderImagePadding()
        {
            HeaderImagePadding = new Padding(5);
        }
        public bool ShouldSerializeHeaderImagePadding()
        {
            return (HeaderImagePadding.Left != 5) ||
                (HeaderImagePadding.Top != 5) ||
                (HeaderImagePadding.Right != 5) ||
                (HeaderImagePadding.Bottom != 5);
        }
        [Browsable(false)]
        public MigrationOptions MigrationOptions { get; set; }
        [Browsable(false)]
        public MainFormController Controller { get; set; }
        public void LinkNextPage(WizardPageBase nextPage)
        {
            NextPage = nextPage;
            nextPage.previousPage = this;
        }
        public virtual void RunPage()
        {
        }
        public virtual void CompletePage()
        {
        }
        public event EventHandler PreviousPageChanged;
        public event EventHandler NextPageChanged;
        public event EventHandler PageCompeleted;
        protected override void OnPaint(PaintEventArgs e)
        {
            base.OnPaint(e);
            if (HeaderVisible)
            {
                using (var brush = new SolidBrush(HeaderBackgroundColour))
                {
                    e.Graphics.FillRectangle(brush, new Rectangle(0, 0, ClientSize.Width, HeaderHeight));
                }
                using (var pen = new Pen(HeaderBorderColour))
                {
                    e.Graphics.DrawLine(pen, new Point(0, HeaderHeight), new Point(ClientSize.Width, HeaderHeight));
                }
                var imageWidth = 0;
                if ((HeaderImage != null) || (UseFormIcon && (FindForm() != null)))
                {
                    var imageToDisplay = HeaderImage;
                    if (imageToDisplay == null)
                    {
                        var form = FindForm();
                        imageToDisplay = form.Icon.ToBitmap();
                    }
                    var srcRect = new Rectangle(0, 0, imageToDisplay.Width, imageToDisplay.Height);
                    var maxHeight = HeaderHeight - HeaderImagePadding.Top - HeaderImagePadding.Bottom;
                    var destRect = new Rectangle(ClientSize.Width - HeaderImagePadding.Right - imageToDisplay.Width,
                        HeaderImagePadding.Top,
                        imageToDisplay.Width,
                        imageToDisplay.Height > maxHeight ? maxHeight : imageToDisplay.Height);
                    e.Graphics.DrawImage(imageToDisplay, destRect, srcRect, GraphicsUnit.Pixel);
                    imageWidth = HeaderImagePadding.Right + imageToDisplay.Width + HeaderImagePadding.Left;
                }
                var titleHeight = 0;
                if (!string.IsNullOrEmpty(HeaderTitle))
                {
                    using (var brush = new SolidBrush(HeaderTitleColour))
                    {
                        var rect = new RectangleF(HeaderTitlePadding.Left,
                            HeaderTitlePadding.Top,
                            ClientSize.Width - HeaderTitlePadding.Right - imageWidth,
                            HeaderHeight - HeaderTitlePadding.Bottom);
                        var format = new StringFormat(StringFormatFlags.FitBlackBox | StringFormatFlags.NoWrap);
                        format.Trimming = StringTrimming.EllipsisWord;
                        e.Graphics.DrawString(HeaderTitle, HeaderTitleFont, brush, rect, format);
                        titleHeight = Convert.ToInt32(
                            e.Graphics.MeasureString(HeaderTitle,
                                HeaderTitleFont,
                                new SizeF(rect.Width, rect.Height),
                                format).Height) +
                            HeaderTitlePadding.Top +
                            HeaderTitlePadding.Bottom;
                    }
                }
                if (!string.IsNullOrEmpty(HeaderText))
                {
                    using (var brush = new SolidBrush(HeaderTextColour))
                    {
                        var rect = new RectangleF(HeaderTextPadding.Left,
                            titleHeight + HeaderTextPadding.Top,
                            ClientSize.Width - HeaderTextPadding.Right - imageWidth,
                            HeaderHeight - titleHeight - HeaderTextPadding.Bottom);
                        var format = new StringFormat(StringFormatFlags.FitBlackBox);
                        format.Trimming = StringTrimming.EllipsisWord;
                        e.Graphics.DrawString(HeaderText, HeaderTextFont, brush, rect, format);
                    }
                }
            }
        }
        protected void FirePageCompeleted()
        {
            if (PageCompeleted != null) PageCompeleted(this, EventArgs.Empty);
        }
    }
}
