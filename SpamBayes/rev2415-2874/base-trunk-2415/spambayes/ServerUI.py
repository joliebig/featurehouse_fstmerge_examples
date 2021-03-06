"""IMAP Server Web Interface
Classes:
    ServerInterface - Interface class for imapserver
Abstract:
This module implements a browser based Spambayes user interface for the
IMAP server.  Users may use it to interface with the server.
The following functions are currently included:
[From the base class UserInterface]
  onClassify - classify a given message
  onWordquery - query a word from the database
  onTrain - train a message or mbox
  onSave - save the database and possibly shutdown
[Here]
  onHome - a home page with various options
To do:
 o Suggestions?
"""
__author__ = "Tony Meyer <ta-meyer@ihug.co.nz>"
__credits__ = "All the Spambayes folk."
try:
    True, False
except NameError:
    True, False = 1, 0
from spambayes import UserInterface
from spambayes.Options import options
parm_ini_map = (
    ('POP3 Proxy Options',  None),
    ('pop3proxy',           'remote_servers'),
    ('pop3proxy',           'listen_ports'),
    ('IMAP Server Options', None),
    ('imapserver',          'username'),
    ('imapserver',          'password'),
    ('imapserver',          'port'),
    ('Storage Options',  None),
    ('Storage',             'persistent_storage_file'),
    ('Storage',             'messageinfo_storage_file'),
    ('Statistics Options',  None),
    ('Categorization',      'ham_cutoff'),
    ('Categorization',      'spam_cutoff'),
)
class ServerUserInterface(UserInterface.UserInterface):
    """Serves the HTML user interface for the server."""
    def __init__(self, state, state_recreator):
        UserInterface.UserInterface.__init__(self, state.bayes,
                                             parm_ini_map)
        self.state = state
        self.state_recreator = state_recreator
    def onHome(self):
        """Serve up the homepage."""
        stateDict = self.state.__dict__.copy()
        stateDict.update(self.state.bayes.__dict__)
        statusTable = self.html.statusTable.clone()
        if not self.state.servers:
            statusTable.proxyDetails = "No POP3 proxies running."
        content = (self._buildBox('Status and Configuration',
                                  'status.gif', statusTable % stateDict)+
                   self._buildTrainBox() +
                   self._buildClassifyBox() +
                   self._buildBox('Word query', 'query.gif',
                                  self.html.wordQuery)
                   )
        self._writePreamble("Home")
        self.write(content)
        self._writePostamble()
    def reReadOptions(self):
        """Called by the config page when the user saves some new options,
        or restores the defaults."""
        self.state.bayes.store()
        import Options
        Options.load_options()
        global options
        from Options import options
        self.state = self.state_recreator()
    def verifyInput(self, parms, pmap):
        '''Check that the given input is valid.'''
        errmsg = UserInterface.UserInterface.verifyInput(self, parms)
        slist = list(parms['pop3proxy_remote_servers'])
        plist = list(parms['pop3proxy_listen_ports'])
        if len(slist) != len(plist):
            errmsg += '<li>The number of POP3 proxy ports specified ' + \
                      'must match the number of servers specified</li>\n'
        plist.sort()
        for p in range(len(plist)-1):
            try:
                if plist[p] == plist[p+1]:
                    errmsg += '<li>All POP3 port numbers must be unique</li>'
                    break
            except IndexError:
                pass
        return errmsg
