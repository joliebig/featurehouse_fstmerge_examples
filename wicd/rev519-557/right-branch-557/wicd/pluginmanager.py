from logfile import log
from misc import WicdError
import os
class PluginManager(object):
    def __init__(self,  daemon):
        self.daemon = daemon
        self.loaded_plugins = []
        self.load_all_plugins()
    def find_available_plugins(self):
        available_plugins = []
        for item in os.listdir(os.path.join(
            os.path.dirname(os.path.realpath(__file__)),
            'plugins')):
            if item.endswith('.py') and \
               not item == 'baseplugin.py' and \
               not item == '__init__.py':
                available_plugins.append(item)
        return available_plugins
    def load_all_plugins(self):
        classes = []
        for plugin in self.find_available_plugins():
            module = self.load_plugin(plugin)
            for item in dir(module):
                if item.endswith("Plugin") \
                   and not item == 'BasePlugin':
                    the_class = getattr(module, item)
                    classes.append(the_class)
        import operator
        classes.sort(key=operator.attrgetter('PRIORITY')) 
        for the_class in classes:
            log( 'loading plugin %s' % the_class)
            self.loaded_plugins.append(the_class(self.daemon))
    def load_plugin(self, name):
        if name in self.find_available_plugins():
            if name.endswith('.py'):
                name = name[:-3]
            module = __import__('plugins.' + str(name))
            module = getattr(module, name)
            return module
        else:
            raise WicdError('Plugin %s was not found' % name)
    def action(self, action_name, values=()):
        for plugin in self.loaded_plugins:
            if hasattr(plugin, "do_%s" % action_name):
                getattr(plugin, "do_%s" % action_name)(*values)
