import os, sys
import logging
import gtk
from dbusmanager import daemon, interface
from dbusmanager import set, get, do
from proxyinterface import ProxyInterface
class UiManager(object):
    def __init__(self, vbox):
        self.vbox = vbox
        self._load_interface_ui_entries()
    def _load_interface_ui_entries(self):
        self._import_gtk_modules()
        self._place_uis_in_window()
    def _place_uis_in_window(self):
        first = True
        for ui in self.interface_uis:
            logging.debug('packing %s' % ui)
            if not first:
                hsep = gtk.HSeparator()
                self.vbox.pack_start(hsep, padding=10, fill=False,
                                     expand=False)
                hsep.show()
            else:
                first = False
            self.vbox.pack_start(ui.box, fill=False, expand=False)
    def _import_gtk_modules(self):
        interface_names = interface.ListInterfaces()
        interfaces = [ ProxyInterface(name) for name in interface_names ]
        module_dirs = []
        for the_interface in interfaces:
            path = the_interface.get_module_path()
            directory = os.path.dirname(path)
            module_dirs.append((the_interface, directory))
            logging.debug('%s: module directory is %s' % (the_interface,
                                                          directory))
        modules = []
        for the_interface, xdirectory in module_dirs:
            modules.append((the_interface, self._load_gtk_interface(xdirectory)))
        self.interface_uis = self._create_ui_from_module(modules)
    def _create_ui_from_module(self, modules):
        uis = []
        for the_interface, module in modules:
            logging.debug('%s: %s' % (the_interface.interface_name, module))
            for item in dir(module):
                if item.endswith('Ui'):
                    logging.debug('found ui %s in module %s' % (item, module) )
                    uis.append(getattr(module, item)(the_interface))
                    logging.debug('created ui %s' % uis[-1])
        return uis
    def _load_gtk_interface(self, module_directory):
        ui = None
        try:
            sys.path.insert(0, os.path.abspath(os.path.join(module_directory, '..')))
            module_name = os.path.split(module_directory)[1]
            module = __import__('%s.ui.gtkui' % module_name)
            sys.path.pop(0)
        except ImportError, e:
            logging.debug('%s: ui import error: %s' % (os.path.split(module_directory)[1], e))
            return None
        else:
            logging.debug('%s: ui module loaded' % os.path.split(module_directory)[1])
            return module.ui.gtkui
