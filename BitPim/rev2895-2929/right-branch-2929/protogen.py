"Generate Python code from packet descriptions"
import tokenize
import sys
import token
import cStringIO
import os
class protoerror(Exception):
    def __init__(self, desc, token):
        Exception.__init__(self,desc)
        self.desc=desc
        self.token=token
    def __repr__(self):
        str=self.desc+"\nLine "+`self.token[2][0]`+":\n"
        str+=self.token[4]
        str+=" "*self.token[2][1]+"^\n"
        str+=`self.token[:4]`
        return str
    def __str__(self):
        return self.__repr__()
class protogentokenizer:
    LITERAL="LITERAL"
    PACKETSTART="PACKETSTART"
    PACKETEND="PACKETEND"
    CONDITIONALSTART="CONDITIONALSTART"
    CONDITIONALEND="CONDITIONALEND"
    FIELD="FIELD"
    ASSERTION="ASSERTION"
    STATE_TOPLEVEL="STATE_TOPLEVEL"
    STATE_PACKET="STATE_PACKET"
    STATE_CONDITIONAL="STATE_CONDITIONAL"
    def __init__(self, tokenizer, autogennamebase):
        self.tokenizer=tokenizer
        self.pb=[]  # pushback list from what we are tokenizing
        self.state=[self.STATE_TOPLEVEL] # state stack
        self.packetstack=[] # packets being described stack
        self.resultspb=[] # our results pushback stack
        self.lines=[None] # no zeroth line
        self.autogennamebase=autogennamebase
        self.deferredpackets=[] # used for nested packets
    def _getautogenname(self, line):
        return self.autogennamebase+`line`
    def _lookahead(self, howfar=1):
        "Returns a token howfar ahead"
        assert howfar>=1
        while len(self.pb)<howfar:
            self.pb.append(self._realnext())
        return self.pb[howfar-1]
    def _realnext(self):
        "Gets the next token from our input, ignoring the pushback list"
        while True:
            t=self.tokenizer.next()
            t=(token.tok_name[t[0]],)+t[1:]
            if len(self.lines)==t[2][0]:
                ll=t[4].split('\n')
                self.lines.extend(ll[:-1])
            elif t[3][0]>len(self.lines):
                ll=t[4].split('\n')
                ll=ll[:-1]
                for i,l in zip(range(t[2][0],t[3][0]+1), ll):
                    if len(self.lines)==i:
                        self.lines.append(l)
            if t[0]=='NL':
                t=('NEWLINE',)+t[1:]
            if t[0]!='COMMENT':
                break
        return t
    def _nextignorenl(self):
        "Gets next token ignoring newlines"
        while True:
            t=self._next()
            if t[0]!='NEWLINE':
                return t
    def _next(self):
        "Gets next token from our input, looking in pushback list"
        if len(self.pb):
            t=self.pb[0]
            self.pb=self.pb[1:]
            return t
        return self._realnext()
    def _consumenl(self):
        "consumes any newlines"
        while True:
            t=self._lookahead()
            if t[0]!='NEWLINE':
                break
            self._next()
    def _getuptoeol(self):
        """Returns everything up to newline as a string.  If end of line has backslash before it then
        next line is returned as well"""
        t=self._lookahead()
        res=self._getline(t[2][0])[t[2][1]:]
        while True:
            while t[0]!='NEWLINE':
                t=self._next()
            if res[-2]!='\\':
                break
            t=self._next()
            res+=self._getline(t[2][0])
        return res
    def _getline(self, line):
        return self.lines[line]
    def __iter__(self):
        return self
    def next(self):
        res=None
        if len(self.resultspb):
            res=self.resultspb.pop()
        if self.state[-1]==self.STATE_TOPLEVEL:
            if res is not None:
                return res
            if len(self.deferredpackets):
                res=self.deferredpackets[0]
                self.deferredpackets=self.deferredpackets[1:]
                return res
            t=self._lookahead()
            if t[0]=='NEWLINE':
                self._next() # consume
                return self.next()
            if t[0]=='OP' and t[1]=='%':
                return (self.LITERAL, self._getliteral())
            if t[0]=='NAME' and t[1]=='PACKET':
                return self._processpacketheader()
            if t[0]=='ENDMARKER':
                raise StopIteration()
            raise protoerror("Unexpected token", t)
        if self.state[-1]==self.STATE_PACKET or self.state[-1]==self.STATE_CONDITIONAL:
            if res is None:
                res=self._processpacketfield()
            if res[0]==self.PACKETSTART:
                q=[res]
                while True:
                    res=self.next()
                    q.append(res)
                    if res[0]==self.PACKETEND:
                        break
                self.deferredpackets.extend(q)
                return self.next()
            return res
        raise protoerror("Unexpected state", self._lookahead())
    def _getliteral(self):
        "Returns the section enclosed in %{ ... }%. The %{ and }% must be on lines by themselves."
        t=self._next()
        if t[0]!='OP' or t[1]!='%':
            raise protoerror("Expecting '%{'", t)
        t=self._next()
        if t[0]!='OP' or t[1]!='{':
            raise protoerror("Expecting '%{'", t)
        t=self._next()
        if t[0]!='NEWLINE':
            raise protoerror("Expecting newline", t)
        res=""
        lastline=-1
        while True:
            t=self._lookahead()
            t2=self._lookahead(2)
            if t[0]=='OP' and t[1]=='%' and \
               t2[0]=='OP' and t2[1]=='}':
                self._next() # consume %
                self._next() # consume }
                t=self._next()
                if t[0]!='NEWLINE':
                    raise protoerror("Expecting newline",t)
                break
            t=self._next()
            res+=t[4]
            lastline=t[2][0]
            while self._lookahead()[2][0]==lastline:
                self._next()
        return res
    def _getdict(self):
        """Returns a text string representing a dict.  If the next token is
        not a dict start then None is returned"""
        res=None
        t=self._lookahead()
        if t[0]!='OP' or t[1]!="{":
            return res
        res=""
        t=self._next()
        start=t[2]
        mostrecent=t # to aid in debugging
        nest=1
        while nest>0:
            t=self._next()
            if t[0]=='OP' and t[1]=='}':
                nest-=1
                continue
            if t[0]=='OP' and t[1]=='{':
                mostrecent=t
                nest+=1
                continue
            if t[0]=='DEDENT' or t[0]=='INDENT' or t[0]=='ENDMARKER':
                raise protoerror("Unterminated '{'", mostrecent)
        end=t[3]
        for line in range(start[0], end[0]+1):
            l=self._getline(line)
            if line==end[0]:
                l=l[:end[1]]
            if line==start[0]:
                l=l[start[1]:]
            res+=l
        return res
    def _processpacketheader(self):
        t=self._next()
        if t[0]!='NAME':
            raise protoerror("expecting 'PACKET'", t)
        thedict=self._getdict()
        t=self._next()
        if t[0]!='NAME':
            raise protoerror("expecting packet name", t)
        thename=t[1]
        t=self._next()
        if t[0]!='OP' and t[1]!=':':
            raise protoerror("expecting ':'", t)
        thecomment=None
        seenindent=False
        while True:
            t=self._lookahead()
            if t[0]=='NEWLINE':
                self._next()
                continue
            if t[0]=='STRING':
                if thecomment is not None:
                    raise protoerror("Duplicate string comment", t)
                thecomment=self._next()[1]
                continue
            if t[0]=='INDENT':
                if seenindent:
                    raise protoerror("Unexpected repeat indent", t)
                seenindent=True
                self._next()
                continue
            break
        if not seenindent:
            raise protoerror("Expecting an indent", t)
        self._consumenl()
        self.state.append(self.STATE_PACKET)
        self.packetstack.append( (thename, thedict, thecomment) )
        return self.PACKETSTART, thename, None, thedict, thecomment
    def _processpacketfield(self):
        """Read in one packet field"""
        self._consumenl()
        t=self._lookahead()
        if t[0]=='DEDENT':
            self._next() 
            x=self.state.pop()
            if x==self.STATE_CONDITIONAL:
                return (self.CONDITIONALEND,)
            return (self.PACKETEND,)
        if t[0]=='NUMBER':
            self._next()
            thesize=int(t[1])
        elif t[0]=='OP' and t[1]=='*':
            self._next()
            thesize=-1
        elif t[0]=='NAME' and t[1].upper()=='P':
            self._next()
            thesize='P'
        elif t[0]=='NAME' and t[1].upper()=='A':
            self._next()
            return self.ASSERTION, self._getuptoeol()
        elif t[0]=='NAME' and t[1]=='if':
            str=self._getuptoeol()
            self._consumenl()
            t=self._next()
            if t[0]!='INDENT':
                raise protoerror("Expecting an indent after if ...: statement", t)
            self.state.append(self.STATE_CONDITIONAL)
            return (self.CONDITIONALSTART, str)
        else:
            raise protoerror("Expecting field size as an integer, *, P, A or 'if' statement", t)
        t=self._next()
        if t[0]!='NAME':
            raise protoerror("Expecting field type", t)
        thetype=t[1]
        t=self._lookahead()
        if t[0]=='OP' and t[1]=='.':
            self._next()
            t=self._next()
            if t[0]!='NAME':
                raise protoerror("Expecting a name after . in field type", t)
            thetype+="."+t[1]
        thedict=self._getdict()
        themodifiers=""
        t=self._next()
        while t[0]=='OP':
            themodifiers+=t[1]
            t=self._next()
        if t[0]!='NAME':
            raise protoerror("Expecting field name", t)
        thename=t[1]
        thedesc=None
        t=self._lookahead()
        if t[0]=='OP' and t[1]==':':
            self._next()
            seenindent=False
            self._consumenl()
            t=self._lookahead()
            if t[0]=='STRING':
                thedesc=t[1]
                t=self._next()
            elif t[0]=='INDENT':
                seenindent=True
                self._next()
            self._consumenl()
            if not seenindent:
                t=self._next()
                if t[0]!='INDENT':
                    raise protoerror("Expected an indent after : based field", t)
            autoclass=self._getautogenname(t[2][0])
            self.resultspb.append( (self.PACKETSTART, autoclass, None, None, "'Anonymous inner class'") )
            self.state.append(self.STATE_PACKET)
            return self.FIELD, thename, thesize, thetype, "{'elementclass': "+autoclass+"}", \
                   thedict, thedesc, themodifiers
        if t[0]=='STRING':
            thedesc=t[1]
            self._next()
        self._consumenl()
        if thedesc is None:
            t=self._lookahead()
            if t[0]=='STRING':
                thedesc=t[1]
                self._next()
                self._consumenl()
        return self.FIELD, thename, thesize, thetype, None, thedict, thedesc, themodifiers
def indent(level=1):
    return "    "*level
class codegen:
    def __init__(self, tokenizer):
        self.tokenizer=tokenizer
    def gencode(self):
        tokens=self.tokenizer
        out=cStringIO.StringIO()
        print >>out, "# THIS FILE IS AUTOMATICALLY GENERATED.  EDIT THE SOURCE FILE NOT THIS ONE"
        for t in tokens:
            if t[0]==tokens.LITERAL:
                out.write(t[1])
                continue
            if t[0]==tokens.PACKETSTART:
                classdetails=t
                classfields=[]
                continue
            if t[0]==tokens.PACKETEND:
                self.genclasscode(out, classdetails, classfields)
                continue
            classfields.append(t)
        return out.getvalue()
    def genclasscode(self, out, namestuff, fields):
        classname=namestuff[1]
        tokens=self.tokenizer
        print >>out, "class %s(BaseProtogenClass):" % (classname,)
        if namestuff[4] is not None:
            print >>out, indent()+namestuff[4]
        fieldlist=[]
        for f in fields:
            if f[0]==tokens.FIELD:
                fieldlist.append(f[1])
        print >>out, indent(1)+"__fields="+`fieldlist`
        print >>out, ""
        print >>out, indent()+"def __init__(self, *args, **kwargs):"
        print >>out, indent(2)+"dict={}"
        if namestuff[2] is not None:
            print >>out, indent(2)+"# Default generator arguments"
            print >>out, indent(2)+"dict.update("+namestuff[2]+")"
        if namestuff[3] is not None:
            print >>out, indent(2)+"# User specified arguments in the packet description"
            print >>out, indent(2)+"dict.update("+namestuff[3]+")"
        print >>out, indent(2)+"# What was supplied to this function"
        print >>out, indent(2)+"dict.update(kwargs)"
        print >>out, indent(2)+"# Parent constructor"
        print >>out, indent(2)+"super(%s,self).__init__(**dict)"%(namestuff[1],)
        print >>out, indent(2)+"if self.__class__ is %s:" % (classname,)
        print >>out, indent(3)+"self._update(args,dict)"
        print >>out, "\n"
        print >>out, indent()+"def getfields(self):"
        print >>out, indent(2)+"return self.__fields"
        print >>out, "\n"
        print >>out, indent()+"def _update(self, args, kwargs):"
        print >>out, indent(2)+"super(%s,self)._update(args,kwargs)"%(namestuff[1],)
        print >>out, indent(2)+"keys=kwargs.keys()"
        print >>out, indent(2)+"for key in keys:"
        print >>out, indent(3)+"if key in self.__fields:"
        print >>out, indent(4)+"setattr(self, key, kwargs[key])"
        print >>out, indent(4)+"del kwargs[key]"
        print >>out, indent(2)+"# Were any unrecognized kwargs passed in?"
        print >>out, indent(2)+"if __debug__:"
        print >>out, indent(3)+"self._complainaboutunusedargs(%s,kwargs)" % (namestuff[1],)
        if len(fields)==1:
            print >>out, indent(2)+"if len(args):"
            d=[]
            if f[2]>=0:
                d.append("{'sizeinbytes': "+`f[2]`+"}")
            for xx in 4,5:
                if f[xx] is not None:
                    d.append(f[xx])
            for dd in d: assert dd[0]=="{" and dd[-1]=='}'
            d=[dd[1:-1] for dd in d]
            print >>out, indent(3)+"dict2={%s}" % (", ".join(d),)
            print >>out, indent(3)+"dict2.update(kwargs)"
            print >>out, indent(3)+"kwargs=dict2"
            print >>out, indent(3)+"self.__field_%s=%s(*args,**dict2)" % (f[1],f[3])
        else:
            print >>out, indent(2)+"if len(args): raise TypeError('Unexpected arguments supplied: '+`args`)"
        print >>out, indent(2)+"# Make all P fields that haven't already been constructed"
        for f in fields:
            if f[0]==tokens.FIELD and f[2]=='P':
                print >>out, indent(2)+"if getattr(self, '__field_"+f[1]+"', None) is None:"
                self.makefield(out, 3, f)
        print >>out, "\n"
        print >>out, indent()+"def writetobuffer(self,buf):"
        print >>out, indent(2)+"'Writes this packet to the supplied buffer'"
        print >>out, indent(2)+"self._bufferstartoffset=buf.getcurrentoffset()"
        i=2
        for f in fields:
            if f[0]==tokens.FIELD and f[2]!='P':
                if '+' in f[7]:
                    print >>out, indent(i)+"try: self.__field_%s" % (f[1],)
                    print >>out, indent(i)+"except:"
                    self.makefield(out, i+1, f, isreading=False)
                print >>out, indent(i)+"self.__field_"+f[1]+".writetobuffer(buf)"
            elif f[0]==tokens.CONDITIONALSTART:
                print >>out, indent(i)+f[1]
                i+=1
            elif f[0]==tokens.CONDITIONALEND:
                i-=1
        assert i==2
        print >>out, indent(2)+"self._bufferendoffset=buf.getcurrentoffset()"
        print >>out, "\n"
        print >>out, indent()+"def readfrombuffer(self,buf):"
        print >>out, indent(2)+"'Reads this packet from the supplied buffer'"
        i=2
        print >>out, indent(2)+"self._bufferstartoffset=buf.getcurrentoffset()"
        for f in fields:
            if f[0]==tokens.FIELD:
                if f[2]=='P':
                    continue
                self.makefield(out, i, f)
                print >>out, indent(i)+"self.__field_%s.readfrombuffer(buf)" % (f[1],)
            elif f[0]==tokens.CONDITIONALSTART:
                print >>out, indent(i)+f[1]
                i+=1
            elif f[0]==tokens.CONDITIONALEND:
                i-=1
        assert i==2
        print >>out, indent(2)+"self._bufferendoffset=buf.getcurrentoffset()"
        print >>out, "\n"
        for f in fields:
            if f[0]==tokens.FIELD:
                print >>out, indent()+"def __getfield_%s(self):" % (f[1],)
                if '+' in f[7]:
                    print >>out, indent(2)+"try: self.__field_%s" % (f[1],)
                    print >>out, indent(2)+"except:"
                    self.makefield(out, 3, f)
                print >>out, indent(2)+"return self.__field_%s.getvalue()\n" % (f[1],)
                print >>out, indent()+"def __setfield_%s(self, value):" % (f[1],)
                print >>out, indent(2)+"if isinstance(value,%s):" % (f[3],)
                print >>out, indent(3)+"self.__field_%s=value" % (f[1],)
                print >>out, indent(2)+"else:"
                self.makefield(out, 3, f, "value,", isreading=False)
                print >>out, ""
                print >>out, indent()+"def __delfield_%s(self): del self.__field_%s\n" % (f[1], f[1])
                print >>out, indent()+"%s=property(__getfield_%s, __setfield_%s, __delfield_%s, %s)\n" % (f[1], f[1], f[1], f[1], f[6])
                if '++' in f[7]:
                    print >>out, indent()+"def set_%s_attr(self, **kwargs):"%f[1]
                    print >>out, indent(2)+"self.%s"%f[1]
                    print >>out, indent(2)+"self.__field_%s.update(**kwargs)\n"%f[1]
        print >>out, indent()+"def iscontainer(self):"
        print >>out, indent(2)+"return True\n"
        print >>out, indent()+"def containerelements(self):"
        i=2
        for f in fields:
            if f[0]==tokens.FIELD:
                print >>out, indent(i)+"yield ('%s', self.__field_%s, %s)" % (f[1], f[1], f[6])
            elif f[0]==tokens.CONDITIONALSTART:
                print >>out, indent(i)+f[1]
                i+=1
            elif f[0]==tokens.CONDITIONALEND:
                i-=1
        assert i==2
        print >>out, "\n\n"
    def makefield(self, out, indentamount, field, args="", isreading=True):
        d=[]
        if field[2]!='P' and field[2]>=0:
            d.append("{'sizeinbytes': "+`field[2]`+"}")
        if not (isreading and '*' in field[7]):
            for xx in 4,5:
                if field[xx] is not None:
                    d.append(field[xx])
        for dd in d:
            assert dd[0]=='{' and dd[-1]=='}'
        if len(d)==0:
            print >>out, indent(indentamount)+"self.__field_%s=%s(%s)" % (field[1], field[3], args)
            return
        d=[dd[1:-1] for dd in d]
        dd="{"+", ".join(d)+"}"
        print >>out, indent(indentamount)+"self.__field_%s=%s(%s**%s)" % (field[1], field[3], args, dd)
def processfile(inputfilename, outputfilename):
    print "Processing",inputfilename,"to",outputfilename
    fn=os.path.basename(outputfilename)
    fn=os.path.splitext(fn)[0]
    f=open(inputfilename, "rtU")
    tokens=tokenize.generate_tokens(f.readline)
    tt=protogentokenizer(tokens, "_gen_"+fn+"_")
    f2=open(outputfilename, "wt")
    cg=codegen(tt)
    f2.write(cg.gencode())
    f2.close()
if __name__=='__main__':
    if len(sys.argv)>3 or (len(sys.argv)==2 and sys.argv[1]=="--help"):
        print "protogen                compiles all .p files in this directory to .py"
        print "protogen foo.p          compiles foo.p to foo.py"
        print "protogen foo.p bar.py   compiles foo.p to bar.py"
        sys.exit(1)
    elif len(sys.argv)==3:
        processfile(sys.argv[1], sys.argv[2])
    elif len(sys.argv)==2:
        processfile(sys.argv[1], sys.argv[1]+"y")
    elif len(sys.argv)==1:
        import glob
        for f in glob.glob("*.p"):
            processfile(f, f+"y")
        for f in glob.glob("phones/*.p"):
            processfile(f, f+"y")
