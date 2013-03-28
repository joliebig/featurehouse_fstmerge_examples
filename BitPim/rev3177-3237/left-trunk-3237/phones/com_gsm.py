"""Communicate with a GSM phones using AT commands"""
import com_phone
import commport
import prototypes
import p_gsm
class Phone(com_phone.Phone):
    """ Talk to generic GSM phones
    """
    desc='GSM'
    protocolclass=p_gsm
    def __init__(self, logtarget, commport):
        com_phone.Phone.__init__(self, logtarget, commport)
        self.mode=self.MODENONE
    def sendATcommand(self, request, responseclass, ignoreerror=False):
        """Similar to the sendpbcommand in com_sanyo and com_lg, except that
        a list of responses is returned, one per line of information returned
        from the phone"""
        buffer=prototypes.buffer()
        request.writetobuffer(buffer, logtitle="GSM sendATcommand")
        data=buffer.getvalue()
        try:
            response_lines=self.comm.sendatcommand(data, ignoreerror=ignoreerror)
        except commport.ATError:
            raise
        except:
            self.comm.success=False
            self.mode=self.MODENONE
            self.raisecommsdnaexception("sending AT command")
        self.comm.success=True
        if responseclass is None:
            return response_lines
        reslist=[]
        for line in response_lines:
            res=responseclass()
            buffer=prototypes.buffer(line)
            res.readfrombuffer(buffer, logtitle="GSM receive AT response")
            reslist.append(res)
        return reslist
    def _setmodemodem(self):
        self.log("_setmodemodem")
        try:
            self.comm.sendatcommand("Z")
            self.comm.sendatcommand('E0V1')
            return True
        except:
            pass
        for baud in (0, 115200, 19200, 230400):
            self.log("Baud="+`baud`)
            if baud:
                if not self.comm.setbaudrate(baud):
                    continue
            try:
                self.comm.sendatcommand("Z")
                self.comm.sendatcommand('E0V1')
                return True
            except:
                pass
        return False
    def get_esn(self):
        req=self.protocolclass.esnrequest()
        res=self.sendATcommand(req, self.protocolclass.esnresponse)
        try:
            return res[0].esn
        except:
            return ''
    def get_sim_id(self):
        req=self.protocolclass.SIM_ID_Req()
        res=self.sendATcommand(req, self.protocolclass.single_value_resp)
        try:
            return res[0].value
        except:
            return None
    def get_manufacturer_id(self):
        return self.sendATcommand(self.protocolclass.manufacturer_id_req(),
                                  self.protocolclass.single_value_resp)[0].value
    def get_model_id(self):
        return self.sendATcommand(self.protocolclass.model_id_req(),
                                  self.protocolclass.single_value_resp)[0].value
    def get_firmware_version(self):
        return self.sendATcommand(self.protocolclass.firmware_version_req(),
                                  self.protocolclass.single_value_resp)[0].value
class Profile(com_phone.Profile):
    BP_Calendar_Version=3
