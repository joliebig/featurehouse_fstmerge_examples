namespace WikiFunctions.Profiles
{
    public class AWBProfile
    {
        public AWBProfile(int pID, string pUsername, string pPassword, string pDefaultSettings, string pNotes,
            bool pUseForUpload)
        {
            ID = pID;
            Username = pUsername;
            Password = pPassword;
            DefaultSettings = pDefaultSettings;
            Notes = pNotes;
            UseForUpload = pUseForUpload;
        }
        public AWBProfile() { }
        public int ID = -1;
        public string DefaultSettings, Notes;
        public string Username, Password;
        public bool UseForUpload;
    }
}
