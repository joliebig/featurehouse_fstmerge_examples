"""message.py - Core Spambayes classes.
Classes:
    Message - an email.Message.Message, extended with spambayes methods
    SBHeaderMessage - A Message with spambayes header manipulations
    MessageInfoDB - persistent state storage for Message
Abstract:
    MessageInfoDB is a simple shelve persistency class for the persistent
    state of a Message obect.  Mark Hammond's idea is to have a master
    database, that simply keeps track of the names and instances
    of other databases, such as the wordinfo and msginfo databases.  The
    MessageInfoDB currently does not provide iterators, but should at some
    point.  This would allow us to, for example, see how many messages
    have been trained differently than their classification, for fp/fn
    assessment purposes.
    Message is an extension of the email package Message class, to
    include persistent message information. The persistent state
    currently consists of the message id, its current
    classification, and its current training.  The payload is not
    persisted.
    SBHeaderMessage extends Message to include spambayes header specific
    manipulations.
Usage:
    A typical classification usage pattern would be something like:
    >>> msg = spambayes.message.SBHeaderMessage()
    >>> msg.setPayload(substance) # substance comes from somewhere else
    >>> id = msg.setIdFromPayload()
    >>> if id is None:
    >>>     msg.setId(time())   # or some unique identifier
    >>> msg.delSBHeaders()      # never include sb headers in a classification
    >>> # bayes object is your responsibility
    >>> (prob, clues) = bayes.spamprob(msg.asTokens(), evidence=True)
    >>> msg.addSBHeaders(prob, clues)
    A typical usage pattern to train as spam would be something like:
    >>> msg = spambayes.message.SBHeaderMessage()
    >>> msg.setPayload(substance) # substance comes from somewhere else
    >>> id = msg.setId(msgid)     # id is a fname, outlook msg id, something...
    >>> msg.delSBHeaders()        # never include sb headers in a train
    >>> if msg.getTraining() == False:   # could be None, can't do boolean test
    >>>     bayes.unlearn(msg.asTokens(), False)  # untrain the ham
    >>> bayes.learn(msg.asTokens(), True) # train as spam
    >>> msg.rememberTraining(True)
To Do:
    o Master DB module, or at least make the msginfodb name an options parm
    o Figure out how to safely add message id to body (or if it can be done
      at all...)
    o Suggestions?
    """
__author__ = "Tim Stone <tim@fourstonesExpressions.com>"
__credits__ = "Mark Hammond, Tony Meyer, all the spambayes contributors."
from __future__ import generators
try:
    True, False
except NameError:
    True, False = 1, 0
    def bool(val):
        return not not val
import os
import types
import math
import re
import errno
import shelve
import pickle
import email
import email.Message
import email.Parser
import email.Header
from spambayes import dbmstorage
from spambayes.Options import options, get_pathname_option
from spambayes.tokenizer import tokenize
try:
    import cStringIO as StringIO
except ImportError:
    import StringIO
CRLF_RE = re.compile(r'\r\n|\r|\n')
class MessageInfoBase(object):
    def __init__(self, db_name):
        self.db_name = db_name
    def _getState(self, msg):
        if self.db is not None:
            try:
                attributes = self.db[msg.getId()]
            except KeyError:
                pass
            else:
                if not isinstance(attributes, types.ListType):
                    (msg.c, msg.t) = attributes
                    return
                for att, val in attributes:
                    setattr(msg, att, val)
    def _setState(self, msg):
        if self.db is not None:
            attributes = []
            for att in msg.stored_attributes:
                attributes.append((att, getattr(msg, att)))
            self.db[msg.getId()] = attributes
            self.store()
    def _delState(self, msg):
        if self.db is not None:
            del self.db[msg.getId()]
            self.store()
class MessageInfoPickle(MessageInfoBase):
    def __init__(self, db_name, pickle_type=1):
        MessageInfoBase.__init__(self, db_name)
        self.mode = pickle_type
        self.load()
    def load(self):
        try:
            fp = open(self.db_name, 'rb')
        except IOError, e:
            if e.errno == errno.ENOENT:
                self.db = {}
            else:
                raise
        else:
            self.db = pickle.load(fp)
            fp.close()
    def close(self):
        pass
    def store(self):
        fp = open(self.db_name, 'wb')
        pickle.dump(self.db, fp, self.mode)
        fp.close()
class MessageInfoDB(MessageInfoBase):
    def __init__(self, db_name, mode='c'):
        MessageInfoBase.__init__(self, db_name)
        self.mode = mode
        self.load()
    def load(self):
        try:
            self.dbm = dbmstorage.open(self.db_name, self.mode)
            self.db = shelve.Shelf(self.dbm)
        except dbmstorage.error:
            if options["globals", "verbose"]:
                print "Warning: no dbm modules available for MessageInfoDB"
            self.dbm = self.db = None
    def __del__(self):
        self.close()
    def close(self):
        def noop(): pass
        getattr(self.db, "close", noop)()
        getattr(self.dbm, "close", noop)()
    def store(self):
        if self.db is not None:
            self.db.sync()
message_info_db_name = get_pathname_option("Storage", "messageinfo_storage_file")
if options["Storage", "persistent_use_database"] is True or \
   options["Storage", "persistent_use_database"] == "dbm":
    msginfoDB = MessageInfoDB(message_info_db_name)
elif options["Storage", "persistent_use_database"] is False or \
     options["Storage", "persistent_use_database"] == "pickle":
    msginfoDB = MessageInfoPickle(message_info_db_name)
else:
    msginfoDB = MessageInfoPickle(message_info_db_name)
class Message(email.Message.Message):
    '''An email.Message.Message extended for Spambayes'''
    def __init__(self):
        email.Message.Message.__init__(self)
        self.stored_attributes = ['c', 't',]
        self.id = None
        self.c = None
        self.t = None
    def setPayload(self, payload):
        prs = email.Parser.Parser()
        fp = StringIO.StringIO(payload)
        prs._parseheaders(self, fp)
        prs._parsebody(self, fp)
    def setId(self, id):
        if self.id and self.id != id:
            raise ValueError, "MsgId has already been set, cannot be changed"
        if id is None:
            raise ValueError, "MsgId must not be None"
        if not type(id) in types.StringTypes:
            raise TypeError, "Id must be a string"
        self.id = id
        msginfoDB._getState(self)
    def getId(self):
        return self.id
    def asTokens(self):
        return tokenize(self.as_string())
    def tokenize(self):
        return self.asTokens()
    def _force_CRLF(self, data):
        """Make sure data uses CRLF for line termination."""
        return CRLF_RE.sub('\r\n', data)
    def as_string(self, unixfrom=False):
        try:
            return self._force_CRLF(\
                email.Message.Message.as_string(self, unixfrom))
        except TypeError:
            parts = []
            for part in self.get_payload():
                parts.append(email.Message.Message.as_string(part, unixfrom))
            return self._force_CRLF("\n".join(parts))
    def modified(self):
        if self.id:    # only persist if key is present
            msginfoDB._setState(self)
    def GetClassification(self):
        if self.c == 's':
            return options['Headers','header_spam_string']
        elif self.c == 'h':
            return options['Headers','header_ham_string']
        elif self.c == 'u':
            return options['Headers','header_unsure_string']
        return None
    def RememberClassification(self, cls):
        if cls == options['Headers','header_spam_string']:
            self.c = 's'
        elif cls == options['Headers','header_ham_string']:
            self.c = 'h'
        elif cls == options['Headers','header_unsure_string']:
            self.c = 'u'
        else:
            raise ValueError, \
                  "Classification must match header strings in options"
        self.modified()
    def GetTrained(self):
        return self.t
    def RememberTrained(self, isSpam):
        self.t = isSpam
        self.modified()
    def __repr__(self):
        return "spambayes.message.Message%r" % repr(self.__getstate__())
    def __getstate__(self):
        return (self.id, self.c, self.t)
    def __setstate__(self, t):
        (self.id, self.c, self.t) = t
class SBHeaderMessage(Message):
    '''Message class that is cognizant of Spambayes headers.
    Adds routines to add/remove headers for Spambayes'''
    def __init__(self):
        Message.__init__(self)
    def setIdFromPayload(self):
        try:
            self.setId(self[options['Headers','mailid_header_name']])
        except ValueError:
            return None
        return self.id
    def addSBHeaders(self, prob, clues):
        """Add hammie header, and remember message's classification.  Also,
        add optional headers if needed."""
        if prob < options['Categorization','ham_cutoff']:
            disposition = options['Headers','header_ham_string']
        elif prob > options['Categorization','spam_cutoff']:
            disposition = options['Headers','header_spam_string']
        else:
            disposition = options['Headers','header_unsure_string']
        self.RememberClassification(disposition)
        self[options['Headers','classification_header_name']] = disposition
        if options['Headers','include_score']:
            disp = "%.*f" % (options["Headers", "header_score_digits"], prob)
            if options["Headers", "header_score_logarithm"]:
                if prob<=0.005 and prob>0.0:
                    x=-math.log10(prob)
                    disp += " (%d)"%x
                if prob>=0.995 and prob<1.0:
                    x=-math.log10(1.0-prob)
                    disp += " (%d)"%x
            self[options['Headers','score_header_name']] = disp
        if options['Headers','include_thermostat']:
            thermostat = '**********'
            self[options['Headers','thermostat_header_name']] = \
                               thermostat[:int(prob*10)]
        if options['Headers','include_evidence']:
            hco = options['Headers','clue_mailheader_cutoff']
            sco = 1 - hco
            evd = []
            for word, score in clues:
                if (word[0] == '*' or score <= hco or score >= sco):
                    if isinstance(word, types.UnicodeType):
                        word = email.Header.Header(word,
                                                   charset='utf-8').encode()
                    evd.append("%r: %.2f" % (word, score))
            wrappedEvd = []
            headerName = options['Headers','evidence_header_name']
            lineLength = len(headerName) + len(': ')
            for component, index in zip(evd, range(len(evd))):
                wrappedEvd.append(component)
                lineLength += len(component)
                if index < len(evd)-1:
                    if lineLength + len('; ') + len(evd[index+1]) < 78:
                        wrappedEvd.append('; ')
                    else:
                        wrappedEvd.append(';\n\t')
                        lineLength = 8
            self[headerName] = "".join(wrappedEvd)
        if isinstance(options["Headers", "notate_to"], types.StringTypes):
            notate_to = (options["Headers", "notate_to"],)
        else:
            notate_to = options["Headers", "notate_to"]
        if disposition in notate_to:
            try:
                self.replace_header("To", "%s,%s" % (disposition,
                                                     self["To"]))
            except KeyError:
                self["To"] = disposition
        if isinstance(options["Headers", "notate_subject"], types.StringTypes):
            notate_subject = (options["Headers", "notate_subject"],)
        else:
            notate_subject = options["Headers", "notate_subject"]
        if disposition in notate_subject:
            try:
                self.replace_header("Subject", "%s,%s" % (disposition,
                                                          self["Subject"]))
            except KeyError:
                self["Subject"] = disposition
        if options['Headers','add_unique_id']:
            self[options['Headers','mailid_header_name']] = self.id
    def currentSBHeaders(self):
        """Return a dictionary containing the current values of the
        SpamBayes headers.  This can be used to restore the values
        after using the delSBHeaders() function."""
        headers = {}
        for header_name in [options['Headers','classification_header_name'],
                            options['Headers','mailid_header_name'],
                            options['Headers','classification_header_name'] + "-ID",
                            options['Headers','thermostat_header_name'],
                            options['Headers','evidence_header_name'],
                            options['Headers','score_header_name'],
                            options['Headers','trained_header_name'],
                            ]:
            value = self[header_name]
            if value is not None:
                headers[header_name] = value
        return headers
    def delSBHeaders(self):
        del self[options['Headers','classification_header_name']]
        del self[options['Headers','mailid_header_name']]
        del self[options['Headers','classification_header_name'] + "-ID"]  # test mode header
        del self[options['Headers','thermostat_header_name']]
        del self[options['Headers','evidence_header_name']]
        del self[options['Headers','score_header_name']]
        del self[options['Headers','trained_header_name']]
def message_from_string(s, _class=Message, strict=False):
    return email.message_from_string(s, _class, strict)
def sbheadermessage_from_string(s, _class=SBHeaderMessage, strict=False):
    return email.message_from_string(s, _class, strict)
