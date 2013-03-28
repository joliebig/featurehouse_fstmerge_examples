"""Interface to the database"""

import os

import copy

import time

import sha

import random

import apsw

import common

class  basedataobject (dict) :
	"""A base object derived from dict that is used for various
    records.  Existing code can just continue to treat it as a dict.
    New code can treat it as dict, as well as access via attribute
    names (ie object["foo"] or object.foo).  attribute name access
    will always give a result includes None if the name is not in
    the dict.
    As a bonus this class includes checking of attribute names and
    types in non-production runs.  That will help catch typos etc.
    For production runs we may be receiving data that was written out
    by a newer version of BitPim so we don't check or error."""
	    _knownproperties=[]
	    _knownlistproperties={'serials': ['sourcetype', '*']}
	    _knowndictproperties={}
	    if __debug__:

        def _check_property(self,name,value=None):

            assert isinstance(name, (str, unicode)), "keys must be a string type"

            assert name in self._knownproperties or name in self._knownlistproperties or name in self._knowndictproperties, "unknown property named '"+name+"'"

            if value is None: return

            if name in getattr(self, "_knownlistproperties"):

                assert isinstance(value, list), "list properties ("+name+") must be given a list as value"

                for v in value:

                    self._check_property_dictvalue(name,v)

                return

            if name in getattr(self, "_knowndictproperties"):

                assert isinstance(value, dict), "dict properties ("+name+") must be given a dict as value"

                self._check_property_dictvalue(name,value)

                return

            assert isinstance(value, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

        def _check_property_dictvalue(self, name, value):

            assert isinstance(value, dict), "item(s) in "+name+" (a list) must be dicts"

            assert name in self._knownlistproperties or name in self._knowndictproperties

            if name in self._knownlistproperties:

                for key in value:

                    assert key in self._knownlistproperties[name] or '*' in self._knownlistproperties[name], "dict key "+key+" as member of item in list "+name+" is not known"

                    v=value[key]

                    assert isinstance(v, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

            elif name in self._knowndictproperties:

                for key in value:

                    assert key in self._knowndictproperties[name] or '*' in self._knowndictproperties[name], "dict key "+key+" as member of dict in item "+name+" is not known"

                    v=value[key]

                    assert isinstance(v, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

        def update(self, items):

            assert isinstance(items, dict), "update only supports dicts" 

            for k in items:

                self._check_property(k, items[k])

            super(basedataobject, self).update(items)

        def __getitem__(self, name):

            self._check_property(name)

            v=super(basedataobject, self).__getitem__(name)

            self._check_property(name, v)

            return v

        def __setitem__(self, name, value):

            self._check_property(name, value)

            super(basedataobject,self).__setitem__(name, value)

        def __setattr__(self, name, value):

            self._check_property(name, value)

            self.__setitem__(name, value)

        def __getattr__(self, name):

            if name not in self._knownproperties and name not in self._knownlistproperties and  name not in self._knowndictproperties:

                raise AttributeError(name)

            self._check_property(name)

            if name in self.keys():

                return self[name]

            return None

        def __delattr__(self, name):

            self._check_property(name)

            if name in self.keys():

                del self[name]

    else:

        def __setattr__(self, name, value):

            super(basedataobject,self).__setitem__(name, value)

        def __getattr__(self, name):

            if name not in self._knownproperties and name not in self._knownlistproperties and  name not in self._knowndictproperties:

                raise AttributeError(name)

            if name in self.keys():

                return self[name]

            return None

        def __delattr__(self, name):

            if name in self.keys():

                del self[name]

    
	def GetBitPimSerial(self):

        "Returns the BitPim serial for this item"

        if "serials" not in self:

            raise KeyError("no bitpim serial present")

        for v in self.serials:

            if v["sourcetype"]=="bitpim":

                return v["id"]

        raise KeyError("no bitpim serial present")

	_persistrandom=random.Random()
	    _shathingy=None
	    def _getnextrandomid(self, item):

        """Returns random ids used to give unique serial numbers to items
        @param item: any object - its memory location is used to help randomness
        @returns: a 20 character hexdigit string
        """

        if basedataobject._shathingy is None:

            basedataobject._shathingy=sha.new()

            basedataobject._shathingy.update(`basedataobject._persistrandom.random()`)

        basedataobject._shathingy.update(`id(self)`)

        basedataobject._shathingy.update(`basedataobject._persistrandom.random()`)

        basedataobject._shathingy.update(`id(item)`)

        return basedataobject._shathingy.hexdigest()

	def EnsureBitPimSerial(self):

        "Ensures this entry has a serial"

        if self.serials is None:

            self.serials=[]

        for v in self.serials:

            if v["sourcetype"]=="bitpim":

                return

        self.serials.append({'sourcetype': "bitpim", "id": self._getnextrandomid(self.serials)})

	"""A base object derived from dict that is used for various
    records.  Existing code can just continue to treat it as a dict.
    New code can treat it as dict, as well as access via attribute
    names (ie object["foo"] or object.foo).  attribute name access
    will always give a result includes None if the name is not in
    the dict.
    As a bonus this class includes checking of attribute names and
    types in non-production runs.  That will help catch typos etc.
    For production runs we may be receiving data that was written out
    by a newer version of BitPim so we don't check or error."""
	    if __debug__:

        def _check_property(self,name,value=None):

            assert isinstance(name, (str, unicode)), "keys must be a string type"

            assert name in self._knownproperties or name in self._knownlistproperties or name in self._knowndictproperties, "unknown property named '"+name+"'"

            if value is None: return

            if name in getattr(self, "_knownlistproperties"):

                assert isinstance(value, list), "list properties ("+name+") must be given a list as value"

                for v in value:

                    self._check_property_dictvalue(name,v)

                return

            if name in getattr(self, "_knowndictproperties"):

                assert isinstance(value, dict), "dict properties ("+name+") must be given a dict as value"

                self._check_property_dictvalue(name,value)

                return

            assert isinstance(value, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

        def _check_property_dictvalue(self, name, value):

            assert isinstance(value, dict), "item(s) in "+name+" (a list) must be dicts"

            assert name in self._knownlistproperties or name in self._knowndictproperties

            if name in self._knownlistproperties:

                for key in value:

                    assert key in self._knownlistproperties[name] or '*' in self._knownlistproperties[name], "dict key "+key+" as member of item in list "+name+" is not known"

                    v=value[key]

                    assert isinstance(v, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

            elif name in self._knowndictproperties:

                for key in value:

                    assert key in self._knowndictproperties[name] or '*' in self._knowndictproperties[name], "dict key "+key+" as member of dict in item "+name+" is not known"

                    v=value[key]

                    assert isinstance(v, (str, unicode, buffer, int, long, float)), "only serializable types supported for values"

        def update(self, items):

            assert isinstance(items, dict), "update only supports dicts" 

            for k in items:

                self._check_property(k, items[k])

            super(basedataobject, self).update(items)

        def __getitem__(self, name):

            self._check_property(name)

            v=super(basedataobject, self).__getitem__(name)

            self._check_property(name, v)

            return v

        def __setitem__(self, name, value):

            self._check_property(name, value)

            super(basedataobject,self).__setitem__(name, value)

        def __setattr__(self, name, value):

            self._check_property(name, value)

            self.__setitem__(name, value)

        def __getattr__(self, name):

            if name not in self._knownproperties and name not in self._knownlistproperties and  name not in self._knowndictproperties:

                raise AttributeError(name)

            self._check_property(name)

            if name in self.keys():

                return self[name]

            return None

        def __delattr__(self, name):

            self._check_property(name)

            if name in self.keys():

                del self[name]

    else:

        def __setattr__(self, name, value):

            super(basedataobject,self).__setitem__(name, value)

        def __getattr__(self, name):

            if name not in self._knownproperties and name not in self._knownlistproperties and  name not in self._knowndictproperties:

                raise AttributeError(name)

            if name in self.keys():

                return self[name]

            return None

        def __delattr__(self, name):

            if name in self.keys():

                del self[name]

    

class  dataobjectfactory :
	"Called by the code to read in objects when it needs a new object container"
	    def __init__(self, dataobjectclass=basedataobject):

        self.dataobjectclass=dataobjectclass

	if __debug__:

        def newdataobject(self, values={}):

            v=self.dataobjectclass()

            if len(values):

                v.update(values)

            return v

    else:

        def newdataobject(self, values={}):

            return self.dataobjectclass(values)


	"Called by the code to read in objects when it needs a new object container"
	if __debug__:

        def newdataobject(self, values={}):

            v=self.dataobjectclass()

            if len(values):

                v.update(values)

            return v

    else:

        def newdataobject(self, values={}):

            return self.dataobjectclass(values)



def extractbitpimserials(dict):

    """Returns a new dict with keys being the bitpim serial for each
    row.  Each item must be derived from basedataobject"""

    res={}

    for record in dict.itervalues():

        res[record.GetBitPimSerial()]=record

    return res

def ensurebitpimserials(dict):

    """Ensures that all records have a BitPim serial.  Each item must
    be derived from basedataobject"""

    for record in dict.itervalues():

        record.EnsureBitPimSerial()

def findentrywithbitpimserial(dict, serial):

    """Returns the entry from dict whose bitpim serial matches serial"""

    for record in dict.itervalues():

        if record.GetBitPimSerial()==serial:

            return record

    raise KeyError("not item with serial "+serial+" found")

def ensurerecordtype(dict, factory):

    for key,record in dict.iteritems():

        if not isinstance(record, basedataobject):

            dict[key]=factory.newdataobject(record)

dictdataobjectfactory=dataobjectfactory(dict)

if __debug__:

    TRACE=False

else:

    TRACE=False


def ExclusiveWrapper(method):

    """Wrap a method so that it has an exclusive lock on the database
    (noone else can read or write) until it has finished"""

    def _transactionwrapper(*args, **kwargs):

        self=args[0]

        self.excounter+=1

        self.transactionwrite=False

        if self.excounter==1:

            print "BEGIN EXCLUSIVE TRANSACTION"

            self.cursor.execute("BEGIN EXCLUSIVE TRANSACTION")

            self._schemacache={}

        try:

            try:

                success=True

                return method(*args, **kwargs)

            except:

                success=False

                raise

        finally:

            self.excounter-=1

            if self.excounter==0:

                w=self.transactionwrite

                if success:

                    if w:

                        cmd="COMMIT TRANSACTION"

                    else:

                        cmd="END TRANSACTION"

                else:

                    if w:

                        cmd="ROLLBACK TRANSACTION"

                    else:

                        cmd="END TRANSACTION"

                print cmd

                self.cursor.execute(cmd)

    setattr(_transactionwrapper, "__doc__", getattr(method, "__doc__"))

    return _transactionwrapper

def sqlquote(s):

    "returns an sqlite quoted string (the return value will begin and end with single quotes)"

    return "'"+s.replace("'", "''")+"'"

def idquote(s):

    """returns an sqlite quoted identifier (eg for when a column name is also an SQL keyword
    The value returned is quoted in square brackets"""

    return '['+s+']'

class  IntegrityCheckFailed (Exception) :
 pass
class  Database :
	def __init__(self, filename):

        self.connection=apsw.Connection(filename)

        self.cursor=self.connection.cursor()

        icheck=[]

        print "database integrity check"

        for row in self.cursor.execute("pragma integrity_check"):

            icheck.extend(row)

        print "database integrity check complete"

        icheck="\n".join(icheck)

        if icheck!="ok":

            raise IntegrityCheckFailed(icheck)

        self.excounter=0

        self.transactionwrite=False

        self._schemacache={}

        self.sql=self.cursor.execute

        self.sqlmany=self.cursor.executemany

        if TRACE:

            self.cursor.setexectrace(self._sqltrace)

            self.cursor.setrowtrace(self._rowtrace)

	def _sqltrace(self, cmd, bindings):

        print "SQL:",cmd

        if bindings:

            print " bindings:",bindings

        return True

	def _rowtrace(self, *row):

        print "ROW:",row

        return row

	def sql(self, statement, params=()):

        "Executes statement and return a generator of the results"

        assert False

	def sqlmany(self, statement, params):

        "execute statements repeatedly with params"

        assert False

	def doestableexist(self, tablename):

        if tablename in self._schemacache:

            return True

        return bool(self.sql("select count(*) from sqlite_master where type='table' and name=%s" % (sqlquote(tablename),)).next()[0])

	def getcolumns(self, tablename, onlynames=False):

        res=self._schemacache.get(tablename,None)

        if res is None:

            res=[]

            for colnum,name,type, _, default, primarykey in self.sql("pragma table_info("+idquote(tablename)+")"):

                if primarykey:

                    type+=" primary key"

                res.append([colnum,name,type])

            self._schemacache[tablename]=res

        if onlynames:

            return [name for colnum,name,type in res]

        return res

	def savemajordict(self, tablename, dict, timestamp=None):

        """This is the entrypoint for saving a first level dictionary
        such as the phonebook or calendar.
        @param tablename: name of the table to use
        @param dict: The dictionary of record.  The key must be the uniqueid for each record.
                   The @L{extractbitpimserials} function can do the conversion for you for
                   phonebook and similar formatted records.
        @param timestamp: the UTC time in seconds since the epoch.  This is 
        """

        if timestamp is None:

            timestamp=time.time()

        dict=dict.copy()

        if not self.doestableexist(tablename):

            self.transactionwrite=True

            self.sql("create table %s (__rowid__ integer primary key, __timestamp__, __deleted__ integer, __uid__ varchar)" % (idquote(tablename),))

        current=self.getmajordictvalues(tablename)

        deleted=[k for k in current if k not in dict]

        new=[k for k in dict if k not in current]

        modified=[k for k in dict if k in current] 

        dl=[]

        for i,k in enumerate(modified):

            if dict[k]==current[k]:

                del dict[k]

                dl.append(i)

        dl.reverse()

        for i in dl:

            del modified[i]

        for d in deleted:

            assert d not in dict

            dict[d]=current[d]

            dict[d]["__deleted__"]=1

        dk=[]

        for k in dict.keys():

            if k not in deleted:

                dict[k]=dict[k].copy()

            for kk in dict[k]:

                if kk not in dk:

                    dk.append(kk)

        assert len([k for k in dk if k.startswith("__") and not k=="__deleted__"])==0

        dbkeys=self.getcolumns(tablename, onlynames=True)

        missing=[k for k in dk if k not in dbkeys]

        if len(missing):

            creates=[]

            for m in missing:

                islist=None

                isdict=None

                isnotindirect=None

                for r in dict.keys():

                    record=dict[r]

                    v=record.get(m,None)

                    if v is None:

                        continue

                    if isinstance(v, list):

                        islist=record

                    elif isinstance(v,type({})):

                        isdict=record

                    else:

                        isnotindirect=record

                    if not __debug__:

                        break

                if islist is None and isdict is None and isnotindirect is None:

                    del dk[dk.index(m)]

                    continue

                if int(islist is not None)+int(isdict is not None)+int(isnotindirect is not None)!=int(True):

                    raise ValueError("key %s for table %s has values with inconsistent types. eg LIST: %s, DICT: %s, NOTINDIRECT: %s" % (m,tablename,`islist`,`isdict`,`isnotindirect`))

                if islist is not None:

                    creates.append( (m, "indirectBLOB") )

                    continue

                if isdict:

                    creates.append( (m, "indirectdictBLOB"))

                    continue

                if isnotindirect is not None:

                    creates.append( (m, "valueBLOB") )

                    continue

                assert False, "You can't possibly get here!"

            if len(creates):

                self._altertable(tablename, creates, createindex=1)

        dbtkeys=self.getcolumns(tablename)

        for _,n,t in dbtkeys:

            if t in ("indirectBLOB", "indirectdictBLOB"):

                indirects={}

                for r in dict.keys():

                    record=dict[r]

                    v=record.get(n,None)

                    if v is not None:

                        if not len(v): 

                            record[n]=None

                        else:

                            if t=="indirectdictBLOB":

                                indirects[r]=[v] 

                            else:

                                indirects[r]=v

                if len(indirects):

                    self.updateindirecttable(tablename+"__"+n, indirects)

                    for r in indirects.keys():

                        dict[r][n]=indirects[r]

        for k in dict.keys():

            record=dict[k]

            record["__uid__"]=k

            rk=record.keys()

            rk.sort()

            cmd=["insert into", idquote(tablename), "( [__timestamp__],"]

            cmd.append(",".join([idquote(r) for r in rk]))

            cmd.extend([")", "values", "(?,"])

            cmd.append(",".join(["?" for r in rk]))

            cmd.append(")")

            self.sql(" ".join(cmd), [timestamp]+[record[r] for r in rk])

            self.transactionwrite=True

	def updateindirecttable(self, tablename, indirects):

        if not self.doestableexist(tablename):

            self.sql("create table %s (__rowid__ integer primary key)" % (idquote(tablename),))

            self.transactionwrite=True

        datakeys=[]

        for i in indirects.keys():

            assert isinstance(indirects[i], list)

            for v in indirects[i]:

                assert isinstance(v, dict)

                for k in v.keys():

                    if k not in datakeys:

                        assert not k.startswith("__")

                        datakeys.append(k)

        dbkeys=self.getcolumns(tablename, onlynames=True)

        missing=[k for k in datakeys if k not in dbkeys]

        if len(missing):

            self._altertable(tablename, [(m,"valueBLOB") for m in missing], createindex=2)

        for r in indirects:

            res=tablename+","

            for record in indirects[r]:

                cmd=["select __rowid__ from", idquote(tablename), "where"]

                params=[]

                coals=[]

                for d in datakeys:

                    v=record.get(d,None)

                    if v is None:

                        coals.append(idquote(d))

                    else:

                        if cmd[-1]!="where":

                            cmd.append("and")

                        cmd.extend([idquote(d), "= ?"])

                        params.append(v)

                assert cmd[-1]!="where" 

                if len(coals)==1:

                    cmd.extend(["and",coals[0],"isnull"])

                elif len(coals)>1:

                    cmd.extend(["and coalesce(",",".join(coals),") isnull"])

                found=None

                for found in self.sql(" ".join(cmd), params):

                    found=found[0]

                    break

                if found is None:

                    cmd=["insert into", idquote(tablename), "("]

                    params=[]

                    for k in record:

                        if cmd[-1]!="(":

                            cmd.append(",")

                        cmd.append(k)

                        params.append(record[k])

                    cmd.extend([")", "values", "("])

                    cmd.append(",".join(["?" for p in params]))

                    cmd.append("); select last_insert_rowid()")

                    found=self.sql(" ".join(cmd), params).next()[0]

                    self.transactionwrite=True

                res+=`found`+","

            indirects[r]=res

	def getmajordictvalues(self, tablename, factory=dictdataobjectfactory,
                           at_time=None):

        if not self.doestableexist(tablename):

            return {}

        res={}

        uids=[u[0] for u in self.sql("select distinct __uid__ from %s" % (idquote(tablename),))]

        schema=self.getcolumns(tablename)

        for colnum,name,type in schema:

            if name=='__deleted__':

                deleted=colnum

            elif name=='__uid__':

                uid=colnum

        if isinstance(at_time, (int, float)):

            sql_string="select * from %s where __uid__=? and __timestamp__<=%f order by __rowid__ desc limit 1" % (idquote(tablename), float(at_time))

        else:

            sql_string="select * from %s where __uid__=? order by __rowid__ desc limit 1" % (idquote(tablename),)

        indirects={}

        for row in self.sqlmany(sql_string, [(u,) for u in uids]):

            if row[deleted]:

                continue

            record=factory.newdataobject()

            for colnum,name,type in schema:

                if name.startswith("__") or type not in ("valueBLOB", "indirectBLOB", "indirectdictBLOB") or row[colnum] is None:

                    continue

                if type=="valueBLOB":

                    record[name]=row[colnum]

                    continue

                assert type=="indirectBLOB" or type=="indirectdictBLOB"

                if name not in indirects:

                    indirects[name]=[]

                indirects[name].append( (row[uid], row[colnum], type) )

            res[row[uid]]=record

        for name,values in indirects.iteritems():

            for uid,v,type in values:

                if type=="indirectBLOB":

                    res[uid][name]=self._getindirect(v)

                else:

                    res[uid][name]=self._getindirect(v)[0]

        return res

	def _getindirect(self, what):

        """Gets a list of values (indirect) as described by what
        @param what: what to get - eg phonebook_serials,1,3,5,
                      (note there is always a trailing comma)
        """

        tablename,rows=what.split(',', 1)

        schema=self.getcolumns(tablename)

        res=[]

        for row in self.sqlmany("select * from %s where __rowid__=?" % (idquote(tablename),), [(int(r),) for r in rows.split(',') if len(r)]):

            record={}

            for colnum,name,type in schema:

                if name.startswith("__") or type not in ("valueBLOB", "indirectBLOB", "indirectdictBLOB") or row[colnum] is None:

                    continue

                if type=="valueBLOB":

                    record[name]=row[colnum]

                    continue

                assert type=="indirectBLOB" or type=="indirectdictBLOB"

                assert False, "indirect in indirect not handled"

            assert len(record)

            res.append(record)

        assert len(res)

        return res

	def _altertable(self, tablename, columnstoadd, createindex=0):

        """Alters the named table by adding the listed columns
        @param tablename: name of the table to alter
        @param columnstoadd: a list of (name,type) of the columns to add
        @param createindex: what sort of index to create.  0 means none, 1 means on just __uid__ and 2 is on all data columns
        """

        dbtkeys=self.getcolumns(tablename)

        del self._schemacache[tablename]

        self.transactionwrite=True

        cmd=["create", "temporary", "table", idquote("backup_"+tablename), "("]

        for _,n,t in dbtkeys:

            if cmd[-1]!="(":

                cmd.append(",")

            cmd.append(idquote(n))

            cmd.append(t)

        cmd.append(")")

        self.sql(" ".join(cmd))

        self.sql("insert into %s select * from %s" % (idquote("backup_"+tablename), idquote(tablename)))

        self.sql("drop table %s" % (idquote(tablename),))

        del cmd[1] 

        cmd[2]=idquote(tablename) 

        del cmd[-1] 

        for n,t in columnstoadd:

            cmd.extend((',', idquote(n), t))

        cmd.append(')')

        self.sql(" ".join(cmd))

        if createindex:

            if createindex==1:

                cmd=["create index", idquote("__index__"+tablename), "on", idquote(tablename), "(__uid__)"]

            elif createindex==2:

                cmd=["create index", idquote("__index__"+tablename), "on", idquote(tablename), "("]

                cols=[]

                for _,n,t in dbtkeys:

                    if not n.startswith("__"):

                        cols.append(idquote(n))

                for n,t in columnstoadd:

                    cols.append(idquote(n))

                cmd.extend([",".join(cols), ")"])

            else:

                raise ValueError("bad createindex "+`createindex`)

            self.sql(" ".join(cmd))

        cmd=["insert into", idquote(tablename), '(']

        for _,n,_ in dbtkeys:

            if cmd[-1]!="(":

                cmd.append(",")

            cmd.append(idquote(n))

        cmd.extend([")", "select * from", idquote("backup_"+tablename)])

        self.sql(" ".join(cmd))

        self.sql("drop table "+idquote("backup_"+tablename))

	def deleteold(self, tablename, uids=None, minvalues=3, maxvalues=5, keepoldest=93):

        """Deletes old entries from the database.  The deletion is based
        on either criterion of maximum values or age of values matching.
        @param uids: You can limit the items deleted to this list of uids,
             or None for all entries.
        @param minvalues: always keep at least this number of values
        @param maxvalues: maximum values to keep for any entry (you
             can supply None in which case no old entries will be removed
             based on how many there are).
        @param keepoldest: values older than this number of days before
             now are removed.  You can also supply None in which case no
             entries will be removed based on age.
        @returns: number of rows removed,number of rows remaining
             """

        if not self.doestableexist(tablename):

            return (0,0)

        timecutoff=0

        if keepoldest is not None:

            timecutoff=time.time()-(keepoldest*24*60*60)

        if maxvalues is None:

            maxvalues=sys.maxint-1

        if uids is None:

            uids=[u[0] for u in self.sql("select distinct __uid__ from %s" % (idquote(tablename),))]

        deleterows=[]

        for uid in uids:

            deleting=False

            for count, (rowid, deleted, timestamp) in enumerate(
                self.sql("select __rowid__,__deleted__, __timestamp__ from %s where __uid__=? order by __rowid__ desc" % (idquote(tablename),), [uid])):

                if count<minvalues:

                    continue

                if deleting:

                    deleterows.append(rowid)

                    continue

                if count>=maxvalues or timestamp<timecutoff:

                    deleting=True

                    if deleted:

                        deleterows.append(rowid)

                        continue

                    if count>0:

                        deleterows.append(rowid)

                    continue

        self.sqlmany("delete from %s where __rowid__=?" % (idquote(tablename),), [(r,) for r in deleterows])

        return len(deleterows), self.sql("select count(*) from "+idquote(tablename)).next()[0]

	def savelist(self, tablename, values):

        """Just save a list of items (eg categories).  There is no versioning or transaction history.
        Internally the table has two fields.  One is the actual value and the other indicates if
        the item is deleted.
        """

        tn=(idquote(tablename),)

        if not self.doestableexist(tablename):

            self.sql("create table %s (__rowid__ integer primary key, item, __deleted__ integer)" % tn)

        delete=[]

        known=[]

        revive=[]

        for row, item, dead in self.sql("select __rowid__,item,__deleted__ from %s" % tn):

            known.append(item)

            if item in values:

                if dead:

                    revive.append((row,))

                continue

            if dead:

                continue

            delete.append((row,))

        create=[(v,) for v in values if v not in known]

        self.sqlmany("update %s set __deleted__=0 where __rowid__=?" % tn, revive)

        self.sqlmany("update %s set __deleted__=1 where __rowid__=?" % tn, delete)

        self.sqlmany("insert into %s (item, __deleted__) values (?,0)" % tn, create)

        if __debug__:

            vdup=values[:]

            vdup.sort()

            vv=self.loadlist(tablename)

            vv.sort()

            assert vdup==vv

	def loadlist(self, tablename):

        """Loads a list of items (eg categories)"""

        if not self.doestableexist(tablename):

            return []

        return [v[0] for v in self.sql("select item from %s where __deleted__=0" % (idquote(tablename),))]

	savemajordict=ExclusiveWrapper(savemajordict)
	    getmajordictvalues=ExclusiveWrapper(getmajordictvalues)
	    deleteold=ExclusiveWrapper(deleteold)
	    savelist=ExclusiveWrapper(savelist)
	    loadlist=ExclusiveWrapper(loadlist)
	    def getchangescount(self, tablename):

        """Return the number of additions, deletions, and modifications
        made to this table over time.
        Expected fields containted in this table: __timestamp__,__deleted__,
        __uid__
        Assuming that both __rowid__ and __timestamp__ values are both ascending
        """

        if not self.doestableexist(tablename):

            return {}

        tn=idquote(tablename)

        sql_cmd='select distinct __timestamp__ from %s' % tn

        res={}

        for t in self.sql(sql_cmd):

            res[t[0]]={ 'add': 0, 'del': 0, 'mod': 0 }

        existing_uid={}

        sql_cmd='select __timestamp__,__uid__,__deleted__ from %s order by __timestamp__ asc' % tn

        for e in self.sql(sql_cmd):

            tt=e[0]

            uid=e[1]

            del_flg=e[2]

            if existing_uid.has_key(uid):

                if del_flg:

                    res[tt]['del']+=1

                    del existing_uid[uid]

                else:

                    res[tt]['mod']+=1

            else:

                existing_uid[uid]=None

                res[tt]['add']+=1

        return res


if __name__=='__main__':

    import common

    import sys

    import time

    import os

    sys.excepthook=common.formatexceptioneh

    class phonebookdataobject(basedataobject):

        _knownlistproperties=basedataobject._knownlistproperties.copy()

        _knownlistproperties.update( {'names': ['title', 'first', 'middle', 'last', 'full', 'nickname'],
                                      'categories': ['category'],
                                      'emails': ['email', 'type'],
                                      'urls': ['url', 'type'],
                                      'ringtones': ['ringtone', 'use'],
                                      'addresses': ['type', 'company', 'street', 'street2', 'city', 'state', 'postalcode', 'country'],
                                      'wallpapers': ['wallpaper', 'use'],
                                      'flags': ['secret'],
                                      'memos': ['memo'],
                                      'numbers': ['number', 'type', 'speeddial'],
                                      })

        _knowndictproperties=basedataobject._knowndictproperties.copy()

        _knowndictproperties.update( {'repeat': ['daily', 'orange']} )

    phonebookobjectfactory=dataobjectfactory(phonebookdataobject)

    try:

        execfile(os.getenv("DBTESTFILE", "examples/phonebook-index.idx"))

    except UnicodeError:

        common.unicode_execfile(os.getenv("DBTESTFILE", "examples/phonebook-index.idx"))

    ensurerecordtype(phonebook, phonebookobjectfactory)

    phonebookmaster=phonebook

    def testfunc():

        global phonebook, TRACE, db

        if len(sys.argv)>=2:

            iterations=int(sys.argv[1])

        else:

            iterations=1

        if iterations >1:

            TRACE=False

        db=Database("testdb")

        b4=time.time()

        for i in xrange(iterations):

            phonebook=phonebookmaster.copy()

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

            del phonebook[17] 

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

            phonebook[15]['addresses'][0]['city']="Bananarama"

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

        after=time.time()

        print "time per iteration is",(after-b4)/iterations,"seconds"

        print "total time was",after-b4,"seconds for",iterations,"iterations"

        if iterations>1:

            print "testing repeated reads"

            b4=time.time()

            for i in xrange(iterations*10):

                db.getmajordictvalues("phonebook")

            after=time.time()

            print "\ttime per iteration is",(after-b4)/(iterations*10),"seconds"

            print "\ttotal time was",after-b4,"seconds for",iterations*10,"iterations"

            print

            print "testing repeated writes"

            x=extractbitpimserials(phonebook)

            k=x.keys()

            b4=time.time()

            for i in xrange(iterations*10):

                xcopy=x.copy()

                for l in range(i,i+len(k)/3):

                    del xcopy[k[l%len(x)]]

                db.savemajordict("phonebook",xcopy)

            after=time.time()

            print "\ttime per iteration is",(after-b4)/(iterations*10),"seconds"

            print "\ttotal time was",after-b4,"seconds for",iterations*10,"iterations"

    if len(sys.argv)==3:

        def profile(filename, command):

            import hotshot, hotshot.stats, os

            file=os.path.abspath(filename)

            profile=hotshot.Profile(file)

            profile.run(command)

            profile.close()

            del profile

            howmany=100

            stats=hotshot.stats.load(file)

            stats.strip_dirs()

            stats.sort_stats('time', 'calls')

            stats.print_stats(100)

            stats.sort_stats('cum', 'calls')

            stats.print_stats(100)

            stats.sort_stats('calls', 'time')

            stats.print_stats(100)

            sys.exit(0)

        profile("dbprof", "testfunc()")

    else:

        testfunc()


if __debug__:

    TRACE=False

else:

    TRACE=False


if __name__=='__main__':

    import common

    import sys

    import time

    import os

    sys.excepthook=common.formatexceptioneh

    class phonebookdataobject(basedataobject):

        _knownlistproperties=basedataobject._knownlistproperties.copy()

        _knownlistproperties.update( {'names': ['title', 'first', 'middle', 'last', 'full', 'nickname'],
                                      'categories': ['category'],
                                      'emails': ['email', 'type'],
                                      'urls': ['url', 'type'],
                                      'ringtones': ['ringtone', 'use'],
                                      'addresses': ['type', 'company', 'street', 'street2', 'city', 'state', 'postalcode', 'country'],
                                      'wallpapers': ['wallpaper', 'use'],
                                      'flags': ['secret'],
                                      'memos': ['memo'],
                                      'numbers': ['number', 'type', 'speeddial'],
                                      })

        _knowndictproperties=basedataobject._knowndictproperties.copy()

        _knowndictproperties.update( {'repeat': ['daily', 'orange']} )

    phonebookobjectfactory=dataobjectfactory(phonebookdataobject)

    try:

        execfile(os.getenv("DBTESTFILE", "examples/phonebook-index.idx"))

    except UnicodeError:

        common.unicode_execfile(os.getenv("DBTESTFILE", "examples/phonebook-index.idx"))

    ensurerecordtype(phonebook, phonebookobjectfactory)

    phonebookmaster=phonebook

    def testfunc():

        global phonebook, TRACE, db

        if len(sys.argv)>=2:

            iterations=int(sys.argv[1])

        else:

            iterations=1

        if iterations >1:

            TRACE=False

        db=Database("testdb")

        b4=time.time()

        for i in xrange(iterations):

            phonebook=phonebookmaster.copy()

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

            del phonebook[17] 

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

            phonebook[15]['addresses'][0]['city']="Bananarama"

            db.savemajordict("phonebook", extractbitpimserials(phonebook))

            v=db.getmajordictvalues("phonebook")

            assert v==extractbitpimserials(phonebook)

        after=time.time()

        print "time per iteration is",(after-b4)/iterations,"seconds"

        print "total time was",after-b4,"seconds for",iterations,"iterations"

        if iterations>1:

            print "testing repeated reads"

            b4=time.time()

            for i in xrange(iterations*10):

                db.getmajordictvalues("phonebook")

            after=time.time()

            print "\ttime per iteration is",(after-b4)/(iterations*10),"seconds"

            print "\ttotal time was",after-b4,"seconds for",iterations*10,"iterations"

            print

            print "testing repeated writes"

            x=extractbitpimserials(phonebook)

            k=x.keys()

            b4=time.time()

            for i in xrange(iterations*10):

                xcopy=x.copy()

                for l in range(i,i+len(k)/3):

                    del xcopy[k[l%len(x)]]

                db.savemajordict("phonebook",xcopy)

            after=time.time()

            print "\ttime per iteration is",(after-b4)/(iterations*10),"seconds"

            print "\ttotal time was",after-b4,"seconds for",iterations*10,"iterations"

    if len(sys.argv)==3:

        def profile(filename, command):

            import hotshot, hotshot.stats, os

            file=os.path.abspath(filename)

            profile=hotshot.Profile(file)

            profile.run(command)

            profile.close()

            del profile

            howmany=100

            stats=hotshot.stats.load(file)

            stats.strip_dirs()

            stats.sort_stats('time', 'calls')

            stats.print_stats(100)

            stats.sort_stats('cum', 'calls')

            stats.print_stats(100)

            stats.sort_stats('calls', 'time')

            stats.print_stats(100)

            sys.exit(0)

        profile("dbprof", "testfunc()")

    else:

        testfunc()


