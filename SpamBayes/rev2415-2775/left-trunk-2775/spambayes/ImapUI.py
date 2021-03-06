"""IMAPFilter Web Interface
Classes:
    IMAPUserInterface - Interface class for the IMAP filter
Abstract:
This module implements a browser based Spambayes user interface for the
IMAP filter.  Users may use it to interface with the filter - it is
expected that this will primarily be for configuration, although users
may also wish to look up words in the database, or classify a message.
The following functions are currently included:
[From the base class UserInterface]
  onClassify - classify a given message
  onWordquery - query a word from the database
  onTrain - train a message or mbox
  onSave - save the database and possibly shutdown
[Here]
  onHome - a home page with various options
To do:
 o This could have a neat review page, like pop3proxy, built up by
   asking the IMAP server appropriate questions.  I don't know whether
   this is needed, however.  This would then allow viewing a message,
   showing the clues for it, and so on.  Finding a message (via the
   spambayes id) could also be done.
 o Suggestions?
"""
__author__ = "Tony Meyer <ta-meyer@ihug.co.nz>, Tim Stone"
__credits__ = "All the Spambayes folk."
try:
    True, False
except NameError:
    True, False = 1, 0
import re
import cgi
import types
import UserInterface
from spambayes.Options import options, optionsPathname, _
parm_map = (
    (_('IMAP Options'),       None),
    ('imap',                  'server'),
    ('imap',                  'username'),
    ('imap',                  'password'),
    ('imap',                  'use_ssl'),
    (_('Header Options'),     None),
    ('Headers',               'notate_to'),
    ('Headers',               'notate_subject'),
    (_('Storage Options'),    None),
    ('Storage',               'persistent_storage_file'),
    ('Storage',               'messageinfo_storage_file'),
    (_('Statistics Options'), None),
    ('Categorization',        'ham_cutoff'),
    ('Categorization',        'spam_cutoff'),
)
adv_map = (
    (_('Statistics Options'), None),
    ('Classifier',            'max_discriminators'),
    ('Classifier',            'minimum_prob_strength'),
    ('Classifier',            'unknown_word_prob'),
    ('Classifier',            'unknown_word_strength'),
    ('Classifier',            'use_bigrams'),
    (_('Header Options'),     None),
    ('Headers',               'include_score'),
    ('Headers',               'header_score_digits'),
    ('Headers',               'header_score_logarithm'),
    ('Headers',               'include_thermostat'),
    ('Headers',               'include_evidence'),
    ('Headers',               'clue_mailheader_cutoff'),
    (_('Storage Options'),    None),
    ('Storage',               'persistent_use_database'),
    (_('Tokenising Options'), None),
    ('Tokenizer',             'mine_received_headers'),
    ('Tokenizer',             'replace_nonascii_chars'),
    ('Tokenizer',             'summarize_email_prefixes'),
    ('Tokenizer',             'summarize_email_suffixes'),
    ('Tokenizer',             'x-pick_apart_urls'),
    (_('Interface Options'),  None),
    ('html_ui',               'display_adv_find'),
    ('html_ui',               'allow_remote_connections'),
    ('html_ui',               'http_authentication'),
    ('html_ui',               'http_user_name'),
    ('html_ui',               'http_password'),
)
class IMAPUserInterface(UserInterface.UserInterface):
    """Serves the HTML user interface for the proxies."""
    def __init__(self, cls, imap, pwd, imap_session_class,
                 lang_manager=None, stats=None):
        global parm_map
        try:
            from imaplib import IMAP4_SSL
        except ImportError:
            parm_list = list(parm_map)
            parm_list.remove(("imap", "use_ssl"))
            parm_map = tuple(parm_list)
        else:
            del IMAP4_SSL
        UserInterface.UserInterface.__init__(self, cls, parm_map, adv_map,
                                             lang_manager, stats)
        self.classifier = cls
        self.imap = imap
        self.imap_pwd = pwd
        self.imap_logged_in = False
        self.app_for_version = "SpamBayes IMAP Filter"
        self.imap_session_class = imap_session_class
    def onHome(self):
        """Serve up the homepage."""
        stateDict = self.classifier.__dict__.copy()
        stateDict["warning"] = ""
        stateDict.update(self.classifier.__dict__)
        statusTable = self.html.statusTable.clone()
        del statusTable.proxyDetails
        statusTable.configurationLink += "<br />&nbsp;&nbsp;&nbsp;&nbsp;" \
            "&nbsp;" + _("You can also <a href='filterfolders'>configure" \
                         " folders to filter</a><br />and " \
                         "<a href='trainingfolders'>Configure folders to" \
                         " train</a>")
        findBox = self._buildBox(_('Word query'), 'query.gif',
                                 self.html.wordQuery)
        if not options["html_ui", "display_adv_find"]:
            del findBox.advanced
        content = (self._buildBox(_('Status and Configuration'),
                                  'status.gif', statusTable % stateDict)+
                   self._buildTrainBox() +
                   self._buildClassifyBox() +
                   findBox
                   )
        self._writePreamble(_("Home"))
        self.write(content)
        self._writePostamble()
    def reReadOptions(self):
        """Called by the config page when the user saves some new options, or
        restores the defaults."""
        self.classifier.store()
        import Options
        Options.load_options()
        global options
        from Options import options
    def onSave(self, how):
        if self.imap is not None:
            self.imap.logout()
        UserInterface.UserInterface.onSave(self, how)
    def onFilterfolders(self):
        self._writePreamble(_("Select Filter Folders"))
        self._login_to_imap()
        if self.imap_logged_in:
            available_folders = self.imap.folder_list()
            content = self.html.configForm.clone()
            content.configFormContent = ""
            content.introduction = _("This page allows you to change " \
                                     "which folders are filtered, and " \
                                     "where filtered mail ends up.")
            content.config_submit.value = _("Save Filter Folders")
            content.optionsPathname = optionsPathname
            for opt in ("unsure_folder", "spam_folder",
                        "filter_folders"):
                folderBox = self._buildFolderBox("imap", opt, available_folders)
                content.configFormContent += folderBox
            self.write(content)
            self._writePostamble()
    def _login_to_imap(self):
        if self.imap_logged_in:
            return
        if self.imap is None and len(options["imap", "server"]) > 0:
            server = options["imap", "server"][0]
            if server.find(':') > -1:
                server, port = server.split(':', 1)
                port = int(port)
            else:
                if options["imap", "use_ssl"]:
                    port = 993
                else:
                    port = 143
            self.imap = self.imap_session_class(server, port)
            if not self.imap.connected:
              content = self._buildBox(_("Error"), None,
                                       _("Please check server/port details."))
              self.write(content)
              self._writePostamble()
              return
        if self.imap is None:
            content = self._buildBox(_("Error"), None,
                                     _("Must specify server details first."))
            self.write(content)
            self._writePostamble()
            return
        username = options["imap", "username"]
        if isinstance(username, types.TupleType):
            username = username[0]
        if not username:
            content = self._buildBox(_("Error"), None,
                                     _("Must specify username first."))
            self.write(content)
            self._writePostamble()
            return
        if not self.imap_pwd:
            self.imap_pwd = options["imap", "password"]
            if isinstance(self.imap_pwd, types.TupleType):
                self.imap_pwd = self.imap_pwd[0]
        if not self.imap_pwd:
            content = self._buildBox(_("Error"), None,
                                     _("Must specify password first."))
            self.write(content)
            self._writePostamble()
            return
        self.imap.login(username, self.imap_pwd)
        self.imap_logged_in = True
    def onTrainingfolders(self):
        self._writePreamble(_("Select Training Folders"))
        self._login_to_imap()
        if self.imap_logged_in:
            available_folders = self.imap.folder_list()
            content = self.html.configForm.clone()
            content.configFormContent = ""
            content.introduction = _("This page allows you to change " \
                                     "which folders contain mail to " \
                                     "train Spambayes.")
            content.config_submit.value = _("Save Training Folders")
            content.optionsPathname = optionsPathname
            for opt in ("ham_train_folders",
                        "spam_train_folders"):
                folderBox = self._buildFolderBox("imap", opt, available_folders)
                content.configFormContent += folderBox
            self.write(content)
            self._writePostamble()
    def onChangeopts(self, **parms):
        backup = self.parm_ini_map
        if parms["how"] == _("Save Training Folders") or \
           parms["how"] == _("Save Filter Folders"):
            del parms["how"]
            self.parm_ini_map = ()
            for opt, value in parms.items():
                del parms[opt]
                if opt[-len(value):] == value:
                    opt = opt[:-len(value)]
                self.parm_ini_map += ("imap", opt),
                key = "imap_" + opt
                if parms.has_key(key):
                    parms[key] += ',' + value
                else:
                    parms[key] = value
        UserInterface.UserInterface.onChangeopts(self, **parms)
        self.parm_ini_map = backup
    def _buildFolderBox(self, section, option, available_folders):
        folderTable = self.html.configTable.clone()
        del folderTable.configTextRow1
        del folderTable.configTextRow2
        del folderTable.configCbRow1
        del folderTable.configRow2
        del folderTable.blankRow
        del folderTable.folderRow
        firstRow = True
        for folder in available_folders:
            folder = cgi.escape(folder)
            folderRow = self.html.configTable.folderRow.clone()
            if firstRow:
                folderRow.helpCell = options.doc(section, option)
                firstRow = False
            else:
                del folderRow.helpCell
            folderRow.folderBox.name = option
            folderRow.folderBox.value = folder
            folderRow.folderName = folder
            if options.multiple_values_allowed(section, option):
                if folder in options[section, option]:
                    folderRow.folderBox.checked = "checked"
                folderRow.folderBox.name += folder
            else:
                if folder == options[section, option]:
                    folderRow.folderBox.checked = "checked"
                folderRow.folderBox.type = "radio"
            folderTable += folderRow
        return self._buildBox(options.display_name(section, option),
                              None, folderTable)
