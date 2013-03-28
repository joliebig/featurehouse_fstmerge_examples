from __future__ import generators
import cPickle
import os
import sys
import errno
import shutil
import traceback
import operator
import win32api, win32con, win32gui
import win32com.client
import win32com.client.gencache
import pythoncom
import msgstore
import oastats
try:
    True, False
except NameError:
    True, False = 1, 0
filename_chars = ('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
                '0123456789'
                """$%'-_@~ `!()^#&+,;=[]""")
def _GetParent():
    try:
        return win32gui.GetActiveWindow()
    except win32gui.error:
        pass
    return 0
def _DoMessage(message, title, flags):
    return win32gui.MessageBox(_GetParent(), message, title, flags)
def ReportError(message, title = None):
    import traceback
    print "ERROR:", repr(message)
    if sys.exc_info()[0] is not None:
        traceback.print_exc()
    if title is None: title = "SpamBayes"
    _DoMessage(message, title, win32con.MB_ICONEXCLAMATION)
def ReportInformation(message, title = None):
    if title is None: title = "SpamBayes"
    _DoMessage(message, title, win32con.MB_ICONINFORMATION)
def AskQuestion(message, title = None):
    if title is None: title = "SpamBayes"
    return _DoMessage(message, title, win32con.MB_YESNO | \
                                      win32con.MB_ICONQUESTION) == win32con.IDYES
try:
    filesystem_encoding = sys.getfilesystemencoding()
except AttributeError:
    filesystem_encoding = "mbcs"
if hasattr(sys, "frozen"):
    assert sys.frozen == "dll", "outlook only supports inproc servers"
    this_filename = win32api.GetModuleFileName(sys.frozendllhandle)
else:
    try:
        this_filename = os.path.abspath(__file__)
    except NameError: # no __file__ - means Py2.2 and __name__=='__main__'
        this_filename = os.path.abspath(sys.argv[0])
try:
    import bsddb3 as bsddb
    use_db = True
except ImportError:
    try:
        import bsddb
        use_db = hasattr(bsddb, "db") # This name is not in the old one.
    except ImportError:
        assert not hasattr(sys, "frozen"), \
               "Don't build binary versions without bsddb!"
        use_db = False
def import_early_core_spambayes_stuff():
    try:
        from spambayes import OptionsClass
    except ImportError:
        parent = os.path.abspath(os.path.join(os.path.dirname(this_filename),
                                              ".."))
        sys.path.insert(0, parent)
def import_core_spambayes_stuff(ini_filenames):
    global bayes_classifier, bayes_tokenize, bayes_storage
    if "spambayes.Options" in sys.modules:
        assert hasattr(sys, "frozen")
        return
    use_names = []
    for name in ini_filenames:
        if isinstance(name, unicode):
            name = name.encode(filesystem_encoding)
        use_names.append(name)
    os.environ["BAYESCUSTOMIZE"] = os.pathsep.join(use_names)
    from spambayes import classifier
    from spambayes.tokenizer import tokenize
    from spambayes import storage
    bayes_classifier = classifier
    bayes_tokenize = tokenize
    bayes_storage = storage
    assert "spambayes.Options" in sys.modules, \
        "Expected 'spambayes.Options' to be loaded here"
def SavePickle(what, filename):
    temp_filename = filename + ".tmp"
    file = open(temp_filename,"wb")
    try:
        cPickle.dump(what, file, 1)
    finally:
        file.close()
    try:
        os.unlink(filename)
    except os.error:
        pass
    os.rename(temp_filename, filename)
class BasicStorageManager:
    db_extension = None # for pychecker - overwritten by subclass
    def __init__(self, bayes_base_name, mdb_base_name):
        self.bayes_filename = bayes_base_name + self.db_extension
        self.mdb_filename = mdb_base_name + self.db_extension
    def new_bayes(self):
        try:
            os.unlink(self.bayes_filename)
        except EnvironmentError, e:
            if e.errno != errno.ENOENT: raise
        return self.open_bayes()
    def store_bayes(self, bayes):
        bayes.store()
    def open_bayes(self):
        raise NotImplementedError
    def close_bayes(self, bayes):
        bayes.close()
class PickleStorageManager(BasicStorageManager):
    db_extension = ".pck"
    def open_bayes(self):
        return bayes_storage.PickledClassifier(self.bayes_filename)
    def open_mdb(self):
        return cPickle.load(open(self.mdb_filename, 'rb'))
    def new_mdb(self):
        return {}
    def store_mdb(self, mdb):
        SavePickle(mdb, self.mdb_filename)
    def close_mdb(self, mdb):
        pass
    def is_incremental(self):
        return False # False means we always save the entire DB
class DBStorageManager(BasicStorageManager):
    db_extension = ".db"
    def open_bayes(self):
        fname = self.bayes_filename.encode(filesystem_encoding)
        return bayes_storage.DBDictClassifier(fname)
    def open_mdb(self):
        fname = self.mdb_filename.encode(filesystem_encoding)
        return bsddb.hashopen(fname)
    def new_mdb(self):
        try:
            os.unlink(self.mdb_filename)
        except EnvironmentError, e:
            if e.errno != errno.ENOENT: raise
        return self.open_mdb()
    def store_mdb(self, mdb):
        mdb.sync()
    def close_mdb(self, mdb):
        mdb.close()
    def is_incremental(self):
        return True # True means only changed records get actually written
class ClassifierData:
    def __init__(self, db_manager, logger):
        self.db_manager = db_manager
        self.bayes = None
        self.message_db = None
        self.dirty = False
        self.logger = logger # currently the manager, but needed only for logging
    def Load(self):
        import time
        start = time.clock()
        bayes = message_db = None
        bayes = self.db_manager.open_bayes()
        fname = self.db_manager.bayes_filename.encode("mbcs", "replace")
        print "Loaded bayes database from '%s'" % (fname,)
        message_db = self.db_manager.open_mdb()
        fname = self.db_manager.mdb_filename.encode("mbcs", "replace")
        print "Loaded message database from '%s'" % (fname,)
        self.logger.LogDebug(0, "Bayes database initialized with "
                   "%d spam and %d good messages" % (bayes.nspam, bayes.nham))
        if len(message_db) != bayes.nham + bayes.nspam:
            print "*** - message database has %d messages - bayes has %d - something is screwey" % \
                    (len(message_db), bayes.nham + bayes.nspam)
        self.bayes = bayes
        self.message_db = message_db
        self.dirty = False
        self.logger.LogDebug(1, "Loaded databases in %gms" % ((time.clock()-start)*1000))
    def InitNew(self):
        if self.bayes is not None:
            self.db_manager.close_bayes(self.bayes)
        if self.message_db is not None:
            self.db_manager.close_mdb(self.message_db)
        self.bayes = self.db_manager.new_bayes()
        self.message_db = self.db_manager.new_mdb()
        self.dirty = True
    def SavePostIncrementalTrain(self):
        if self.db_manager.is_incremental():
            if self.dirty:
                self.Save()
            else:
                self.logger.LogDebug(1, "Bayes database is not dirty - not writing")
        else:
            print "Using a slow database - not saving after incremental train"
    def Save(self):
        import time
        start = time.clock()
        bayes = self.bayes
        if bayes.nspam + bayes.nham != len(self.message_db):
            print "WARNING: Bayes database has %d messages, " \
                  "but training database has %d" % \
                  (bayes.nspam + bayes.nham, len(self.message_db))
        if self.logger.verbose:
            print "Saving bayes database with %d spam and %d good messages" %\
                   (bayes.nspam, bayes.nham)
            print " ->", self.db_manager.bayes_filename
        self.db_manager.store_bayes(self.bayes)
        if self.logger.verbose:
            print " ->", self.db_manager.mdb_filename
        self.db_manager.store_mdb(self.message_db)
        self.dirty = False
        self.logger.LogDebug(1, "Saved databases in %gms" % ((time.clock()-start)*1000))
    def Close(self):
        if self.dirty and self.bayes:
            print "Warning: ClassifierData closed while Bayes database dirty"
        if self.db_manager:
            self.db_manager.close_bayes(self.bayes)
            self.db_manager.close_mdb(self.message_db)
            self.db_manager = None
        self.bayes = None
        self.logger = None
    def Adopt(self, other):
        assert not other.dirty, "Adopting dirty classifier data!"
        other.db_manager.close_bayes(other.bayes)
        other.db_manager.close_mdb(other.message_db)
        self.db_manager.close_bayes(self.bayes)
        self.db_manager.close_mdb(self.message_db)
        shutil.move(other.db_manager.bayes_filename, self.db_manager.bayes_filename)
        shutil.move(other.db_manager.mdb_filename, self.db_manager.mdb_filename)
        self.Load()
def GetStorageManagerClass():
    return [PickleStorageManager, DBStorageManager][use_db]
class BayesManager:
    def __init__(self, config_base="default", outlook=None, verbose=0):
        self.never_configured = True
        self.reported_error_map = {}
        self.reported_startup_error = False
        self.config = self.options = None
        self.addin = None
        self.verbose = verbose
        self.outlook = outlook
        self.dialog_parser = None
        self.test_suite_running = False
        import_early_core_spambayes_stuff()
        self.application_directory = os.path.dirname(this_filename)
        self.windows_data_directory = self.LocateDataDirectory()
        self.PrepareConfig()
        value = self.config.general.data_directory
        if value:
            try:
                value = value.decode(filesystem_encoding)
            except AttributeError: # May already be Unicode
                pass
            assert type(value) == type(u''), "%r should be a unicode" % value
            try:
                if not os.path.isdir(value):
                    os.makedirs(value)
                assert os.path.isdir(value), "just made the *ucker"
                value = os.path.abspath(value)
            except os.error:
                print "The configuration files have specified a data " \
                      "directory of", repr(value), "but it is not valid. " \
                      "Using default."
                value = None
        if value:
            self.data_directory = value
        else:
            self.data_directory = self.windows_data_directory
        self.MigrateDataDirectory()
        self.message_store = msgstore.MAPIMsgStore(outlook)
        self.LoadConfig()
        bayes_option_filenames = []
        for look_dir in [self.application_directory, self.data_directory]:
            look_file = os.path.join(look_dir, "default_bayes_customize.ini")
            if os.path.isfile(look_file):
                bayes_option_filenames.append(look_file)
        import_core_spambayes_stuff(bayes_option_filenames)
        bayes_base = os.path.join(self.data_directory, "default_bayes_database")
        mdb_base = os.path.join(self.data_directory, "default_message_database")
        ManagerClass = GetStorageManagerClass()
        db_manager = ManagerClass(bayes_base, mdb_base)
        self.classifier_data = ClassifierData(db_manager, self)
        self.LoadBayes()
        self.stats = oastats.Stats(self.config)
    def LoadBayes(self):
        try:
            self.classifier_data.Load()
        except:
            self.ReportFatalStartupError("Failed to load bayes database")
            self.classifier_data.InitNew()
    def InitNewBayes(self):
        self.classifier_data.InitNew()
    def SaveBayes(self):
        self.classifier_data.Save()
    def SaveBayesPostIncrementalTrain(self):
        self.classifier_data.SavePostIncrementalTrain()
    def LogDebug(self, level, *args):
        if self.verbose >= level:
            for arg in args[:-1]:
                print arg,
            print args[-1]
    def ReportError(self, message, title = None):
        if self.test_suite_running:
            print "ReportError:", repr(message)
            print "(but test suite running - not reported)"
            return
        ReportError(message, title)
    def ReportInformation(self, message, title=None):
        if self.test_suite_running:
            print "ReportInformation:", repr(message)
            print "(but test suite running - not reported)"
            return
        ReportInformation(message, title)
    def AskQuestion(self, message, title=None):
        return AskQuestion(message, title)
    def ReportFatalStartupError(self, message):
        if not self.reported_startup_error:
            self.reported_startup_error = True
            full_message = \
                "There was an error initializing the Spam plugin.\r\n\r\n" \
                "Spam filtering has been disabled.  Please re-configure\r\n" \
                "and re-enable this plugin\r\n\r\n" \
                "Error details:\r\n" + message
            if self.config is not None:
                self.config.filter.enabled = False
            self.ReportError(full_message)
        else:
            print "ERROR:", repr(message)
            traceback.print_exc()
    def ReportErrorOnce(self, msg, title = None, key = None):
        if key is None: key = msg
        if self.test_suite_running:
            print "ReportErrorOnce:", repr(msg)
            print "(but test suite running - not reported)"
            return
        print "ERROR:", repr(msg)
        if key in self.reported_error_map:
            print "(this error has already been reported - not displaying it again)"
        else:
            traceback.print_exc()
            self.reported_error_map[key] = True
            ReportError(msg, title)
    def WorkerThreadStarting(self):
        pythoncom.CoInitialize()
    def WorkerThreadEnding(self):
        pythoncom.CoUninitialize()
    def LocateDataDirectory(self):
        from win32com.shell import shell, shellcon
        try:
            appdata = shell.SHGetFolderPath(0,shellcon.CSIDL_APPDATA,0,0)
            path = os.path.join(appdata, "SpamBayes")
            if not os.path.isdir(path):
                os.makedirs(path)
            return path
        except pythoncom.com_error:
            return self.application_directory
        except EnvironmentError:
            return self.application_directory
    def MigrateDataDirectory(self):
        self._MigrateFile("default_bayes_database.pck")
        self._MigrateFile("default_bayes_database.db")
        self._MigrateFile("default_message_database.pck")
        self._MigrateFile("default_message_database.db")
        self._MigrateFile("default_configuration.pck")
    def _MigrateFile(self, filename):
        src = os.path.join(self.application_directory, filename)
        dest = os.path.join(self.data_directory, filename)
        if os.path.isfile(src) and not os.path.isfile(dest):
            shutil.copyfile(src, dest)
            os.remove(src)
    def FormatFolderNames(self, folder_ids, include_sub):
        names = []
        for eid in folder_ids:
            try:
                folder = self.message_store.GetFolder(eid)
                name = folder.name
            except self.message_store.MsgStoreException:
                name = "<unknown folder>"
            names.append(name)
        ret = '; '.join(names)
        if include_sub:
            ret += " (incl. Sub-folders)"
        return ret
    def EnsureOutlookFieldsForFolder(self, folder_id, include_sub=False):
        assert self.outlook is not None, "I need outlook :("
        field_name = self.config.general.field_score_name
        for msgstore_folder in self.message_store.GetFolderGenerator(
                                                    [folder_id], include_sub):
            folder_name = msgstore_folder.GetFQName()
            if msgstore_folder.DoesFolderHaveOutlookField(field_name):
                self.LogDebug(1, "Folder '%s' already has field '%s'" \
                                 % (folder_name, field_name))
                continue
            self.LogDebug(0, "Folder '%s' has no field named '%s' - creating" \
                      % (folder_name, field_name))
            message = msgstore_folder.CreateTemporaryMessage(msg_flags=1)
            outlook_message = message.GetOutlookItem()
            ups = outlook_message.UserProperties
            try:
                format = 1
                ups.Add(field_name,
                       win32com.client.constants.olPercent,
                       True, # Add to folder
                       format)
                outlook_message.Save()
            except pythoncom.com_error, details:
                if msgstore.IsReadOnlyCOMException(details):
                    self.LogDebug(1, "The folder '%s' is read-only - user "
                                     "property can't be added" % (folder_name,))
                else:
                    print "Warning: failed to create the Outlook " \
                          "user-property in folder '%s'" \
                          % (folder_name,)
                    print "", details
            msgstore_folder.DeleteMessages((message,))
            if not msgstore_folder.DoesFolderHaveOutlookField(field_name):
                self.LogDebug(0,
                        "WARNING: We just created the user field in folder "
                        "%s, but it appears to not exist.  Something is "
                        "probably wrong with DoesFolderHaveOutlookField()" % \
                        folder_name)
    def PrepareConfig(self):
        import config
        self.options = config.CreateConfig()
        self.config = config.OptionsContainer(self.options)
        filename = os.path.join(self.application_directory, "default_configuration.ini")
        self._MergeConfigFile(filename)
        filename = os.path.join(self.windows_data_directory, "default_configuration.ini")
        self._MergeConfigFile(filename)
    def _MergeConfigFile(self, filename):
        try:
            self.options.merge_file(filename)
        except:
            msg = "The configuration file named below is invalid.\r\n" \
                    "Please either correct or remove this file\r\n\r\n" \
                    "Filename: " + filename
            self.ReportError(msg)
    def LoadConfig(self):
        import locale; locale.setlocale(locale.LC_NUMERIC, "C")
        profile_name = self.message_store.GetProfileName()
        if profile_name is not None:
            profile_name = "".join([c for c in profile_name
                                    if ord(c)>127 or c in filename_chars])
        if profile_name is None:
            profile_name = "unknown_profile"
            print "*** NOTE: It appears you are running the source-code version of"
            print "* SpamBayes, and running a win32all version pre 154."
            print "* If you work with multiple Outlook profiles, it is recommended"
            print "* you upgrade - see http://starship.python.net/crew/mhammond"""
        else:
            try:
                os.rename(os.path.join(self.data_directory, "unknown_profile.ini"),
                          os.path.join(self.data_directory, profile_name + ".ini"))
            except os.error:
                pass
        self.config_filename = os.path.join(self.data_directory, profile_name + ".ini")
        self.never_configured = not os.path.exists(self.config_filename)
        self._MergeConfigFile(self.config_filename)
        self.verbose = self.config.general.verbose
        if self.verbose:
            self.LogDebug(self.verbose, "System verbosity set to", self.verbose)
        self.MigrateOldPickle()
        import config
        config.MigrateOptions(self.options)
        if self.verbose > 1:
            print "Dumping loaded configuration:"
            print self.options.display()
            print "-- end of configuration --"
    def MigrateOldPickle(self):
        assert self.config is not None, "Must have a config"
        pickle_filename = os.path.join(self.data_directory,
                                       "default_configuration.pck")
        try:
            f = open(pickle_filename, 'rb')
        except IOError:
            self.LogDebug(1, "No old pickle file to migrate")
            return
        print "Migrating old pickle '%s'" % pickle_filename
        try:
            try:
                old_config = cPickle.load(f)
            except:
                print "FAILED to load old pickle"
                traceback.print_exc()
                msg = "There was an error loading your old\r\n" \
                      "SpamBayes configuration file.\r\n\r\n" \
                      "It is likely that you will need to re-configure\r\n" \
                      "SpamBayes before it will function correctly."
                self.ReportError(msg)
                old_config = None
        finally:
            f.close()
        if old_config is not None:
            for section, items in old_config.__dict__.items():
                print " migrating section '%s'" % (section,)
                dict = getattr(items, "__dict__", None)
                if dict is None:
                    dict = {section: items}
                    section = "general"
                for name, value in dict.items():
                    sect = getattr(self.config, section)
                    setattr(sect, name, value)
        self.LogDebug(1, "pickle migration doing initial configuration save")
        try:
            self.LogDebug(1, "pickle migration removing '%s'" % pickle_filename)
            os.remove(pickle_filename)
        except os.error:
            msg = "There was an error migrating and removing your old\r\n" \
                  "SpamBayes configuration file.  Configuration changes\r\n" \
                  "you make are unlikely to be reflected next\r\n" \
                  "time you start Outlook.  Please try rebooting."
            self.ReportError(msg)
    def GetClassifier(self):
        """Return the classifier we're using."""
        return self.classifier_data.bayes
    def SaveConfig(self):
        import locale; locale.setlocale(locale.LC_NUMERIC, "C")
        self.verbose = self.config.general.verbose
        print "Saving configuration ->", self.config_filename.encode("mbcs", "replace")
        assert self.config and self.options, "Have no config to save!"
        if self.verbose > 1:
            print "Dumping configuration to save:"
            print self.options.display()
            print "-- end of configuration --"
        self.options.update_file(self.config_filename)
    def Save(self):
        if self.classifier_data.dirty:
            self.classifier_data.Save()
        else:
            self.LogDebug(1, "Bayes database is not dirty - not writing")
    def Close(self):
        global _mgr
        self.classifier_data.Close()
        self.config = self.options = None
        if self.message_store is not None:
            self.message_store.Close()
            self.message_store = None
        self.outlook = None
        self.addin = None
        if _mgr is self:
            _mgr = None
    def score(self, msg, evidence=False):
        """Score a msg.
        If optional arg evidence is specified and true, the result is a
        two-tuple
            score, clues
        where clues is a list of the (word, spamprob(word)) pairs that
        went into determining the score.  Else just the score is returned.
        """
        email = msg.GetEmailPackageObject()
        try:
            return self.classifier_data.bayes.spamprob(bayes_tokenize(email), evidence)
        except AssertionError:
            msg = "It appears your SpamBayes training database is corrupt.\r\n\r\n" \
                  "We are working on solving this, but unfortunately you\r\n" \
                  "must re-train the system via the SpamBayes manager."
            self.ReportErrorOnce(msg)
            self.config.filter.enabled = False
            raise
    def GetDisabledReason(self):
        config = self.config.filter
        ok_to_enable = operator.truth(config.watch_folder_ids)
        if not ok_to_enable:
            return "You must define folders to watch for new messages.  " \
                   "Select the 'Filtering' tab to define these folders."
        ok_to_enable = operator.truth(config.spam_folder_id)
        if not ok_to_enable:
            return "You must define the folder to receive your certain spam.  " \
                   "Select the 'Filtering' tab to define this folders."
        ms = self.message_store
        unsure_folder = None # unsure need not be specified.
        if config.unsure_folder_id:
            try:
                unsure_folder = ms.GetFolder(config.unsure_folder_id)
            except ms.MsgStoreException, details:
                return "The unsure folder is invalid: %s" % (details,)
        try:
            spam_folder = ms.GetFolder(config.spam_folder_id)
        except ms.MsgStoreException, details:
            return "The spam folder is invalid: %s" % (details,)
        if ok_to_enable:
            for folder in ms.GetFolderGenerator(config.watch_folder_ids,
                                                config.watch_include_sub):
                bad_folder_type = None
                if unsure_folder is not None and unsure_folder == folder:
                    bad_folder_type = "unsure"
                    bad_folder_name = unsure_folder.GetFQName()
                if spam_folder == folder:
                    bad_folder_type = "spam"
                    bad_folder_name = spam_folder.GetFQName()
                if bad_folder_type is not None:
                    return "You can not specify folder '%s' as both the " \
                           "%s folder, and as being watched." \
                           % (bad_folder_name, bad_folder_type)
        return None
    def ShowManager(self):
        import dialogs
        dialogs.ShowDialog(0, self, self.config, "IDD_MANAGER")
        self.SaveConfig()
        if self.addin is not None:
            self.addin.FiltersChanged()
    def ShowFilterNow(self):
        import dialogs
        dialogs.ShowDialog(0, self, self.config, "IDD_FILTER_NOW")
        self.SaveConfig()
    def ShowHtml(self,url):
        """Displays the main SpamBayes documentation in your Web browser"""
        import sys, os, urllib
        if urllib.splittype(url)[0] is None: # just a file spec
            if hasattr(sys, "frozen"):
                fname = os.path.join(os.path.dirname(sys.argv[0]),
                                     "../docs/outlook",
                                     url)
                if not os.path.isfile(fname):
                    fname = os.path.join(os.path.dirname(sys.argv[0]),
                                         url)
            else:
                fname = os.path.join(os.path.dirname(__file__),
                                        url)
            fname = os.path.abspath(fname)
            if not os.path.isfile(fname):
                self.ReportError("Can't find "+url)
                return
            url = fname
        from dialogs import SetWaitCursor
        SetWaitCursor(1)
        os.startfile(url)
        SetWaitCursor(0)
_mgr = None
def GetManager(outlook = None):
    global _mgr
    if _mgr is None:
        if outlook is None:
            outlook = win32com.client.Dispatch("Outlook.Application")
        _mgr = BayesManager(outlook=outlook)
    return _mgr
def ShowManager(mgr):
    mgr.ShowManager()
def main(verbose_level = 1):
    mgr = GetManager()
    mgr.verbose = max(mgr.verbose, verbose_level)
    ShowManager(mgr)
    mgr.Save()
    mgr.Close()
    return 0
def usage():
    print "Usage: manager [-v ...]"
    sys.exit(1)
if __name__=='__main__':
    verbose = 1
    import getopt
    opts, args = getopt.getopt(sys.argv[1:], "v")
    if args:
        usage()
    for opt, val in opts:
        if opt=="-v":
            verbose += 1
        else:
            usage()
    sys.exit(main(verbose))
