import com_phone
import com_brew
import prototypes
import p_sk6100
class Phone(com_phone.Phone, com_brew.BrewProtocol):
    """Talk to SK Music Slider"""
	
    desc="SK Music Slider"
    protocolclass=p_sk6100
    serialsname='sk6100'
	
    phonetypes = [ 'cell', 'home', 'office', 'fax', 'pager' ]
	
    def __init__(self, logtarget, commport):
        com_phone.Phone.__init__(self, logtarget, commport)
        com_brew.BrewProtocol.__init__(self)
	
    def getfundamentals(self, results):
        grbuf = prototypes.buffer(self.getfilecontents('SKY/PBK/group.pbk'))
        groups = self.protocolclass.groups()
        groups.readfrombuffer(grbuf, logtitle="Groups read")
		
        grsort = [group for group in groups.pbgroups if group.name != '']
		
        results['groups'] = grsort
        return results
	
    def getphonebook(self, results):
		
        phonebuf = prototypes.buffer(self.getfilecontents('SKY/PBK/number.pbk'))
        phones = self.protocolclass.phones()
        phones.readfrombuffer(phonebuf, logtitle="Phonenumbers")
		
        phones = [phone for phone in phones.records if phone.owner_id]
		
        pbook={}
        bookbuf = prototypes.buffer(self.getfilecontents('SKY/PBK/book.pbk'))
        entries = self.protocolclass.wholebook()
        entries.readfrombuffer(bookbuf, logtitle="Names read")
		
        for entry in entries.pbentries:
            if not entry.record:
                continue
			
            group_name = "Group not recognised"
            for group in results['groups']:
                if group.group_id == entry.group_id:
                    group_name = group.name
                    break
			
            pbook[entry.slot] = {
                'names': [{'title': '', 'first': '', 
                           'last': '', 'full': entry.name, 'nickname': ''}],
                'categories': [{'category': group_name}],
                'numbers': [{'number': phone.number, 
                             'type': self.phonetypes[phone.type - 1]}
                            for phone in phones 
                            if phone.owner_id == entry.slot],
            'serials': [{'sourcetype': self.serialsname}]
            }
		
        results['phonebook'] = pbook
        results['categories'] = [group.name for group in results['groups']]
        return pbook 
	
    def getcalendar(self, results):
        pass
    def getwallpapers(self, results):
        pass
	
    def getringtones(self, results):
        pass
class Profile(com_phone.Profile):
    deviceclasses=("modem",)
	
    _supportedsyncs=(
        ('phonebook', 'read', None),  # all phonebook reading
        )
	
