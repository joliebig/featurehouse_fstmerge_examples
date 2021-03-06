""" Configure the scripts for a particular network.
Script for configuring the scripts for a network passed in as a
command line argument.  This needs to run a separate process because
editing scripts requires root access, and the GUI/Tray are typically
run as the current user.
"""
import sys
import os
import gtk
import ConfigParser
import dbus
import gtk.glade
import wicd.wpath as wpath
import wicd.misc as misc
_ = misc.get_gettext()
language = {}
language['configure_scripts'] = _("Configure Scripts")
language['before_script'] = _("Pre-connection Script")
language['after_script'] = _("Post-connection Script")
language['disconnect_script'] = _("Disconnection Script")
bus = dbus.SystemBus()
try:
    print 'Attempting to connect tray to daemon...'
    proxy_obj = bus.get_object('org.wicd.daemon', '/org/wicd/daemon')
    print 'Success.'
except Exception:
    print 'Daemon not running...'
    misc.PromptToStartDaemon()
    sys.exit(1)
wireless = dbus.Interface(proxy_obj, 'org.wicd.daemon.wireless')
wired = dbus.Interface(proxy_obj, 'org.wicd.daemon.wired')
config = dbus.Interface(proxy_obj, 'org.wicd.daemon.config')
wireless_conf = wpath.etc + 'wireless-settings.conf'
wired_conf = wpath.etc + 'wired-settings.conf'
def none_to_blank(text):
    """ Converts special string cases to a blank string.
    If text is None, 'None', or '' then this method will
    return '', otherwise it will just return str(text).
    """
    if text in (None, "None", ""):
        return ""
    else:
        return str(text)
def blank_to_none(text):
    """ Convert an empty or null string to 'None'. """
    if text in ("", None):
        return "None"
    else:
        return str(text)
def get_val(con, network, option, default="None"):
    """ Returns the specified option for the given network.
    Returns the value stored in the config file for the given option,
    unless the option isn't stored yet, in which case the default value
    provided is stored and then returned.
    Keyword arguments:
    network -- The section to search.
    option -- The option to search for.
    deafult -- The default value to store/return if the option isn't found.
    """
    if not con.has_option(network, option):
        con.set(network, option, default)
    return con.get(network, option)
def get_script_info(network, network_type):
    """ Read script info from disk and load it into the configuration dialog """
    info = {}
    con = ConfigParser.ConfigParser()
    if network_type == "wired":
        con.read(wired_conf)
        if con.has_section(network):
            info["pre_entry"] = get_val(con, network, "beforescript")
            info["post_entry"] = get_val(con, network, "afterscript")
            info["disconnect_entry"] = get_val(con, network, "disconnectscript")
    else:
        bssid = wireless.GetWirelessProperty(int(network), "bssid")
        con.read(wireless_conf)
        if con.has_section(bssid):
            info["pre_entry"] = get_val(con, bssid, "beforescript")
            info["post_entry"] = get_val(con, bssid, "afterscript")
            info["disconnect_entry"] = get_val(con, bssid, "disconnectscript")
    return info
def write_scripts(network, network_type, script_info):
    """ Writes script info to disk and loads it into the daemon. """
    con = ConfigParser.ConfigParser()
    if network_type == "wired":
        con.read(wired_conf)
        if not con.has_section(network):
            con.add_section(network)
        con.set(network, "beforescript", script_info["pre_entry"])
        con.set(network, "afterscript", script_info["post_entry"])
        con.set(network, "disconnectscript", script_info["disconnect_entry"])
        con.write(open(wired_conf, "w"))
        config.ReadWiredNetworkProfile(network)
        config.SaveWiredNetworkProfile(network)
    else:
        bssid = wireless.GetWirelessProperty(int(network), "bssid")
        con.read(wireless_conf)
        if not con.has_section(bssid):
            con.add_section(bssid)
        con.set(bssid, "beforescript", script_info["pre_entry"])
        con.set(bssid, "afterscript", script_info["post_entry"])
        con.set(bssid, "disconnectscript", script_info["disconnect_entry"])
        con.write(open(wireless_conf, "w"))
        config.ReadWirelessNetworkProfile(int(network))
        config.SaveWirelessNetworkProfile(int(network))
def main (argv):
    """ Runs the script configuration dialog. """
    if len(argv) < 2:
        print 'Network id to configure is missing, aborting.'
        sys.exit(1)
    network = argv[1]
    network_type = argv[2]
    script_info = get_script_info(network, network_type)
    gladefile = wpath.share + "wicd.glade"
    wTree = gtk.glade.XML(gladefile)
    dialog = wTree.get_widget("configure_script_dialog")
    wTree.get_widget("pre_label").set_label(language['before_script'] + ":")
    wTree.get_widget("post_label").set_label(language['after_script'] + ":")
    wTree.get_widget("disconnect_label").set_label(language['disconnect_script']
                                                   + ":")
    wTree.get_widget("window1").hide()
    pre_entry = wTree.get_widget("pre_entry")
    post_entry = wTree.get_widget("post_entry")
    disconnect_entry = wTree.get_widget("disconnect_entry")
    pre_entry.set_text(none_to_blank(script_info.get("pre_entry")))
    post_entry.set_text(none_to_blank(script_info.get("post_entry")))
    disconnect_entry.set_text(none_to_blank(script_info.get("disconnect_entry")))
    dialog.show_all()
    result = dialog.run()
    if result == 1:
        script_info["pre_entry"] = blank_to_none(pre_entry.get_text())
        script_info["post_entry"] = blank_to_none(post_entry.get_text())
        script_info["disconnect_entry"] = blank_to_none(disconnect_entry.get_text())
        write_scripts(network, network_type, script_info)
    dialog.destroy()
if __name__ == '__main__':
    if os.getuid() != 0:
        print "Root privileges are required to configure scripts.  Exiting."
        sys.exit(0)
    main(sys.argv)
