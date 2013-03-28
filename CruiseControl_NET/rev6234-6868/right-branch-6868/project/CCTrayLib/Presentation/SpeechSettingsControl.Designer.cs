namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
 partial class SpeechSettingsControl
 {
  private System.ComponentModel.IContainer components = null;
  protected override void Dispose(bool disposing)
  {
   if (disposing) {
    if (components != null) {
     components.Dispose();
    }
   }
   base.Dispose(disposing);
  }
  private void InitializeComponent()
  {
   this.checkBoxSpeechEnabled = new System.Windows.Forms.CheckBox();
   this.checkBoxSpeakBuildStartedEvents = new System.Windows.Forms.CheckBox();
   this.checkBoxSpeakBuildSuccededEvents = new System.Windows.Forms.CheckBox();
   this.checkBoxSpeakBuildFailedEvents = new System.Windows.Forms.CheckBox();
   this.SuspendLayout();
   this.checkBoxSpeechEnabled.Location = new System.Drawing.Point(8, 8);
   this.checkBoxSpeechEnabled.Name = "checkBoxSpeechEnabled";
   this.checkBoxSpeechEnabled.Size = new System.Drawing.Size(190, 24);
   this.checkBoxSpeechEnabled.TabIndex = 0;
   this.checkBoxSpeechEnabled.Text = "&Speech Enabled";
   this.checkBoxSpeechEnabled.UseVisualStyleBackColor = true;
   this.checkBoxSpeechEnabled.CheckedChanged += new System.EventHandler(this.CheckBoxSpeechEnabledCheckedChanged);
   this.checkBoxSpeakBuildStartedEvents.Location = new System.Drawing.Point(27, 39);
   this.checkBoxSpeakBuildStartedEvents.Name = "checkBoxSpeakBuildStartedEvents";
   this.checkBoxSpeakBuildStartedEvents.Size = new System.Drawing.Size(210, 24);
   this.checkBoxSpeakBuildStartedEvents.TabIndex = 1;
   this.checkBoxSpeakBuildStartedEvents.Text = "Speak &Build Started Events";
   this.checkBoxSpeakBuildStartedEvents.UseVisualStyleBackColor = true;
   this.checkBoxSpeakBuildSuccededEvents.Location = new System.Drawing.Point(27, 70);
   this.checkBoxSpeakBuildSuccededEvents.Name = "checkBoxSpeakBuildSuccededEvents";
   this.checkBoxSpeakBuildSuccededEvents.Size = new System.Drawing.Size(210, 24);
   this.checkBoxSpeakBuildSuccededEvents.TabIndex = 2;
   this.checkBoxSpeakBuildSuccededEvents.Text = "Speak Build &Succeded Events";
   this.checkBoxSpeakBuildSuccededEvents.UseVisualStyleBackColor = true;
   this.checkBoxSpeakBuildFailedEvents.Location = new System.Drawing.Point(27, 100);
   this.checkBoxSpeakBuildFailedEvents.Name = "checkBoxSpeakBuildFailedEvents";
   this.checkBoxSpeakBuildFailedEvents.Size = new System.Drawing.Size(210, 24);
   this.checkBoxSpeakBuildFailedEvents.TabIndex = 3;
   this.checkBoxSpeakBuildFailedEvents.Text = "Speak Build &Failed Events";
   this.checkBoxSpeakBuildFailedEvents.UseVisualStyleBackColor = true;
   this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Inherit;
   this.BackColor = System.Drawing.SystemColors.Control;
   this.Controls.Add(this.checkBoxSpeakBuildFailedEvents);
   this.Controls.Add(this.checkBoxSpeakBuildSuccededEvents);
   this.Controls.Add(this.checkBoxSpeakBuildStartedEvents);
   this.Controls.Add(this.checkBoxSpeechEnabled);
   this.Name = "SpeechSettingsControl";
   this.Size = new System.Drawing.Size(667, 289);
   this.ResumeLayout(false);
  }
  private System.Windows.Forms.CheckBox checkBoxSpeakBuildSuccededEvents;
  private System.Windows.Forms.CheckBox checkBoxSpeakBuildFailedEvents;
  private System.Windows.Forms.CheckBox checkBoxSpeakBuildStartedEvents;
  private System.Windows.Forms.CheckBox checkBoxSpeechEnabled;
 }
}
