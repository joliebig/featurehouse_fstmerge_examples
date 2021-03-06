import os
import gtk.glade
from dbusmanager import dbus_manager
import logging
class ShortInterfaceUiBase(object): #gtk.HBox):
    def __init__(self, interface):
        cwd = os.getcwd()
        os.chdir(os.path.split(__file__)[0])
        widgets = gtk.glade.XML("uibase.glade", root='network_hbox')
        box = widgets.get_widget('network_hbox')
        box.widgets = widgets
        settings_widgets = gtk.glade.XML(
            "uibase.glade", root='settings_dialog'
            )
        self.settings_dialog = settings_widgets.get_widget('settings_dialog')
        self.settings_dialog.widgets = settings_widgets
        self._setup_treeview(settings_widgets.get_widget('dns_treeview'))
        dic = {
            "on_add_clicked" : self.add_clicked,
            'on_remove_clicked' : self.remove_clicked
            }
        settings_widgets.signal_autoconnect(dic)
        os.chdir(cwd)
        logging.debug(type(box))
        logging.debug(id(self))
        self.box = box
        logging.debug(id(self))
        logging.debug('in gtkuibase init')
        self.interface = interface
        self.image = widgets.get_widget('image')
        self.name_label = widgets.get_widget('name_label')
        self.status_label = widgets.get_widget('status_label')
        self.status_image = widgets.get_widget('status_image')
        self.disconnect_button = widgets.get_widget('disconnect_button')
        self.connect_button = widgets.get_widget('connect_button')
        self.cancel_button = widgets.get_widget('cancel_button')
        self.name_label.set_markup('<b>%s</b>' % self.interface.get_name())
        dic = { "on_connect_button_clicked" : self.connect_clicked,
                "on_disconnect_button_clicked" : self.disconnect_clicked,
                "on_settings_button_clicked" : self.settings_clicked,
                'on_cancel_button_clicked' : self.cancel_clicked}
        widgets.signal_autoconnect(dic)
        dbus_manager.connect_to_signal('StatusChange', self.status_change)
        self._update_status()
        logging.debug('done setting up gtkuibase %s', self.name_label)
    def _setup_treeview(self, treeview):
        self.liststore = gtk.ListStore(str)
        treeview.set_model(self.liststore)
        cell = gtk.CellRendererText()
        cell.set_property('editable', True)
        column = gtk.TreeViewColumn('IP address', cell, text=0)
        treeview.append_column(column)
        self.treeview = treeview
        cell.connect('edited', self.edited_cell, self.liststore)
    def edited_cell(self, widget, position, new_text, model):
        logging.debug('cell edited: %s', new_text)
        model[position][0] = new_text
    def add_clicked(self, widget):
        logging.debug('add clicked')
        self.liststore.append(('IP Address', ))
    def remove_clicked(self, widget):
        selection = self.treeview.get_selection()
        model, iter_ = selection.get_selected()
        if iter_:
            model.remove(iter_)
    def connect_clicked(self, widget):
        logging.debug('connect clicked')
        self.interface.do_connect()
    def disconnect_clicked(self, widget):
        logging.debug('disconnect clicked')
        self.interface.do_disconnect()
    def settings_clicked(self, widget):
        self.settings_dialog.show()
    def cancel_clicked(self, widget):
        logging.debug('cancelclicked')
        self.interface.do_cancel_connect()
    def status_change(self, interface_name, previous_status, status):
        if not interface_name == self.interface.interface_name:
            return
        self._update_status()
    def state_change(self, interface_name, state=None):
        if not interface_name == self.interface.interface_name:
            return
        self._update_status()
    def _update_status(self):
        status = self.interface.get_internal_status()
        status_string = self.interface.get_status()
        connected = self.interface.get_connected_to_something()
        self.buttons_disabled(status)
        if status == 'idle':
            self.status_label.set_text(status_string)
        else:
            self.status_label.set_text(status.title())
        self.change_connected(connected, status)
    def buttons_disabled(self, status):
        enabled = None
        if status == 'idle':
            enabled = True
        else:
            enabled = False
        self.connect_button.set_sensitive(enabled)
        self.disconnect_button.set_sensitive(enabled)
    def change_connected(self, connected, status):
        if connected and status == 'idle':
            self.status_image.set_from_stock(gtk.STOCK_YES, 1)
            self.connect_button.hide()
            self.disconnect_button.show()
            self.cancel_button.hide()
        elif not connected and status == 'idle':
            self.status_image.set_from_stock(gtk.STOCK_NO, 1)
            self.connect_button.show()
            self.disconnect_button.hide()
            self.cancel_button.hide()
        elif not connected and status == 'connecting':
            self.status_image.set_from_stock(gtk.STOCK_NO, 1)
            self.cancel_button.show()
            self.connect_button.hide()
            self.disconnect_button.hide()
        elif connected:
            self.status_image.set_from_stock(gtk.STOCK_YES, 1)
            self.disconnect_button.show()
            self.connect_button.hide()
            self.cancel_button.hide()
        elif not connected:
            self.status_image.set_from_stock(gtk.STOCK_NO, 1)
            self.disconnect_button.hide()
            self.connect_button.show()
            self.cancel_button.hide()
