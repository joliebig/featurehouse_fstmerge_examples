import sys, os
import win32api
try:
    win32api.GetConsoleTitle()
except win32api.error:
    if hasattr(sys, "frozen"):
        temp_dir = win32api.GetTempPath()
        for i in range(3,0,-1):
            try: os.unlink(os.path.join(temp_dir, "SpamBayesService%d.log" % (i+1)))
            except os.error: pass
            try:
                os.rename(
                    os.path.join(temp_dir, "SpamBayesService%d.log" % i),
                    os.path.join(temp_dir, "SpamBayesService%d.log" % (i+1))
                    )
            except os.error: pass
        sys.stdout = open(os.path.join(temp_dir,"SpamBayesService1.log"), "wt", 0)
        sys.stderr = sys.stdout
    else:
        import win32traceutil
if not hasattr(sys, "frozen"):
    try:
        this_filename=__file__
    except NameError:
        this_filename = sys.argv[0]
    if not os.path.isabs(sys.argv[0]):
        sys.argv[0] = os.path.abspath(sys.argv[0])
        this_filename = sys.argv[0]
    sb_dir = os.path.dirname(os.path.dirname(this_filename))
    sb_scripts_dir = os.path.join(sb_dir,"scripts")
    sys.path.insert(0, sb_dir)
    sys.path.insert(-1, sb_scripts_dir)
    os.chdir(sb_dir)
import traceback
import threading
import cStringIO
import sb_server
import win32serviceutil, win32service
import pywintypes, win32con, winerror
from ntsecuritycon import *
class Service(win32serviceutil.ServiceFramework):
    _svc_name_ = "pop3proxy"
    _svc_display_name_ = "SpamBayes Service"
    _svc_deps_ =  ['tcpip'] # We depend on the tcpip service.
    def __init__(self, args):
        win32serviceutil.ServiceFramework.__init__(self, args)
        self.event_stopped = threading.Event()
        self.event_stopping = threading.Event()
        self.thread = None
    def SvcStop(self):
        self.ReportServiceStatus(win32service.SERVICE_STOP_PENDING)
        self.event_stopping.set()
        sb_server.stop()
    def SvcDoRun(self):
        import servicemanager
        try:
            sb_server.prepare(can_stop=False)
        except sb_server.AlreadyRunningException:
            msg = "The SpamBayes proxy service could not be started, as "\
                  "another SpamBayes server is already running on this machine"
            servicemanager.LogErrorMsg(msg)
            errCode = winerror.ERROR_SERVICE_SPECIFIC_ERROR
            self.ReportServiceStatus(win32service.SERVICE_STOPPED,
                                     win32ExitCode=errCode, svcExitCode = 1)
            return
        assert not sb_server.state.launchUI, "Service can't launch a UI"
        thread = threading.Thread(target=self.ServerThread)
        thread.start()
        from spambayes.Options import optionsPathname
        extra = " as user '%s', using config file '%s'" \
                % (win32api.GetUserName(),
                   optionsPathname)
        servicemanager.LogMsg(
            servicemanager.EVENTLOG_INFORMATION_TYPE,
            servicemanager.PYS_SERVICE_STARTED,
            (self._svc_name_, extra)
            )
        try:
            self.event_stopping.wait()
            for i in range(60):
                self.ReportServiceStatus(win32service.SERVICE_STOP_PENDING)
                self.event_stopped.wait(1)
                if self.event_stopped.isSet():
                    break
                print "The service is still shutting down..."
            else:
                print "The worker failed to stop - aborting it anyway"
        except KeyboardInterrupt:
            pass
        s = sb_server.state
        status = " after %d sessions (%d ham, %d spam, %d unsure)" % \
                (s.totalSessions, s.numHams, s.numSpams, s.numUnsure)
        servicemanager.LogMsg(
            servicemanager.EVENTLOG_INFORMATION_TYPE,
            servicemanager.PYS_SERVICE_STOPPED,
            (self._svc_name_, status)
            )
    def ServerThread(self):
        try:
            try:
                sb_server.start()
            except SystemExit:
                print "pop3proxy service shutting down due to user request"
            except:
                ob = cStringIO.StringIO()
                traceback.print_exc(file=ob)
                message = "The pop3proxy service failed with an " \
                          "unexpected error\r\n\r\n" + ob.getvalue()
                print message
                import servicemanager
                servicemanager.LogErrorMsg(message)
        finally:
            self.event_stopping.set()
            self.event_stopped.set()
if __name__=='__main__':
    win32serviceutil.HandleCommandLine(Service)
