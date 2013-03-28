

package net.sf.freecol.common.option;

import java.util.logging.Logger;

import net.sf.freecol.client.gui.i18n.Messages;
import net.sf.freecol.common.model.FreeColObject;


abstract public class AbstractOption extends FreeColObject implements Option {

    public static final String NO_ID = "NO_ID";

    private static Logger logger = Logger.getLogger(AbstractOption.class.getName());

    private String optionGroup = "";

    
    
    
    protected boolean isDefined = false;

    protected boolean previewEnabled = false;

    
    
    public AbstractOption(String id) {
        this(id, null);
    }

    
    
    
    public AbstractOption(String id, OptionGroup optionGroup) {
        setId(id);
        if (optionGroup != null) {
            setGroup(optionGroup.getId());
            optionGroup.add(this);
        }
    }

    
    public boolean isPreviewEnabled() {
        return previewEnabled;
    }
    
    
    public void setPreviewEnabled(boolean previewEnabled) {
        this.previewEnabled = previewEnabled;
    }

    
    public String toString() {
        return getName();
    }

    
    public String getGroup() {
        return optionGroup;
    }

    
    public void setGroup(String group) {
        if (group == null) {
            optionGroup = "";
        } else {
            optionGroup = group;
        }
    }

    
    public String getName() {
        return Messages.message(getGroup() + "." + getId().replaceFirst("model\\.option\\.", "") + ".name");
    }
    
    
    public String getShortDescription() {
        return Messages.message(getGroup() + "." + getId().replaceFirst("model\\.option\\.", "") + ".shortDescription");
    }
}
