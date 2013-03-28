"""Implement specific prototypes class for Motorola phones"""
import prototypes
class CAL_DATE(prototypes.CSVSTRING):
    """Dates used for Calendar Events (mm-dd-yyyy)"""
    def __init__(self, *args, **kwargs):
        super(CAL_DATE, self).__init__(*args, **kwargs)
        self._valuedate=(0, 0, 0) # y,m,d
        if self._ismostderived(CAL_DATE):
            self._update(args, kwargs)
    def _converttostring(self, date):
        s=''
        if len(date)>=3:
            year,month,day=date[:3]
            if month>0 or day>0 or year>0:
                s='%2.2d-%2.2d-%4.4d'%(month, day, year)
        return s
    def _update(self, args, kwargs):
        for k in ('constant', 'default', 'value'):
            if kwargs.has_key(k):
                kwargs[k]=self._converttostring(kwargs[k])
        if len(args)==0:
            pass
        elif len(args)==1:
            args=(self._converttostring(args[0]),)
        else:
            raise TypeError("expected (year,month,day) as arg")
        super(CAL_DATE, self)._update(args, kwargs)
        self._complainaboutunusedargs(CAL_DATE, kwargs)
    def getvalue(self):
        s=super(CAL_DATE, self).getvalue()
        val=s.split('-')
        if len(val)<2:
            year=0
            month=0
            day=0
        else:
            year=int(val[2])
            month=int(val[0])
            day=int(val[1])
        return (year, month, day)
class CAL_TIME(prototypes.CSVSTRING):
    """Times used for Calendar Events (hh:mm)"""
    def __init__(self, *args, **kwargs):
        super(CAL_TIME, self).__init__(*args, **kwargs)
        self._valuetime=(0, 0) # h,m
        if self._ismostderived(CAL_TIME):
            self._update(args, kwargs)
    def _converttostring(self, date):
        s=''
        if len(date)>=2:
            s='%2.2d:%2.2d'%tuple(date[:2])
        return s
    def _update(self, args, kwargs):
        for k in ('constant', 'default', 'value'):
            if kwargs.has_key(k):
                kwargs[k]=self._converttostring(kwargs[k])
        if len(args)==0:
            pass
        elif len(args)==1:
            args=(self._converttostring(args[0]),)
        else:
            raise TypeError("expected (hour, min) as arg")
        super(CAL_TIME, self)._update(args, kwargs)
        self._complainaboutunusedargs(CAL_TIME, kwargs)
    def getvalue(self):
        s=super(CAL_TIME, self).getvalue()
        val=s.split(':')
        if len(val)==2:
            return (int(val[0]), int(val[1]))
        return (0, 0)
