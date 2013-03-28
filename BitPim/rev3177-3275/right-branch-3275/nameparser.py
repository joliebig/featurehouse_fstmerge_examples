"""Various routines that deal with names"""
def formatfullname(name):
    """Returns a string of the name, including all fields that are present"""
    res=""
    full=name.get("full", "")
    fml=""
    f=name.get("first", "")
    m=name.get("middle", "")
    l=name.get("last", "")
    if len(f) or len(m) or len(l):
        fml+=f
        if len(m) and len(fml) and fml[-1]!=' ':
            fml+=" "
        fml+=m
        if len(l) and len(fml) and fml[-1]!=' ':
            fml+=" "
        fml+=l
    if len(fml) or len(full):
        if fml==full:
            res+=full
        else:
            if len(full):
                res+=full
            if len(fml):
                if len(res):
                    res+=" | "
                res+=fml
    if name.has_key("nickname"):
        res+=" ("+name["nickname"]+")"
    return res
def formatsimplename(name):
    "like L{formatname}, except we use the first matching component"
    if len(name.get("full", "")):
        return name.get("full")
    f=name.get("first", "")
    m=name.get("middle", "")
    l=name.get("last", "")
    if len(f) or len(m) or len(l):
        return " ".join([p for p in (f,m,l) if len(p)])
    return name.get('nickname', "")
def formatsimplelastfirst(name):
    "Returns the name formatted as Last, First Middle"
    f,m,l=getparts(name)
    if len(l):
        if len(f+m):
            return l+", "+" ".join([f,m])
        return l
    return " ".join([f,m])
def getfullname(name):
    """Gets the full name, joining the first/middle/last if necessary"""
    if name.has_key("full"):
        return name["full"]
    parts=[name.get(part, "") for part in ("first", "middle", "last")]
    return " ".join([part for part in parts if len(part)])
lastparts= [ "van", "von", "de", "di" ]
def getparts(name):
    """Returns (first, middle, last) for name.  If the part doesn't exist
    then a blank string is returned"""
    for i in ("first", "middle", "last"):
        if name.has_key(i):
            return (name.get("first", ""), name.get("middle", ""), name.get("last", ""))
    if not name.has_key("full"):
        return (name.get("nickname", ""), "", "")
    n=name.get("full")
    parts=n.split()
    if len(parts)<=1:
        return (n, "", "")
    if len(parts)==2:
        return (parts[0], "", parts[1])
    f=[parts[0]]
    m=[]
    l=[parts[-1]]
    del parts[0]
    del parts[-1]
    while len(parts) and (parts[-1][0].lower()==parts[-1][0] or parts[-1].lower() in lastparts):
        l=[parts[-1]]+l
        del parts[-1]
    m=parts
    return (" ".join(f), " ".join(m), " ".join(l))
def getfirst(name):
    return getparts(name)[0]
def getmiddle(name):
    return getparts(name)[1]
def getlast(name):
    return getparts(name)[2]
