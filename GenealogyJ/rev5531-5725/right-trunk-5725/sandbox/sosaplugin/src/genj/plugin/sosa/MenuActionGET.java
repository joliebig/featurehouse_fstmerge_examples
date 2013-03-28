

package genj.plugin.sosa;



import genj.util.swing.Action2;
import genj.util.Resources;
import genj.window.WindowManager;

import genj.util.swing.ChoiceWidget;

import java.util.logging.Logger;



public class MenuActionGET extends Action2 {

	private boolean status;

	private SosaIndexation sosaIndexation;

	private Logger LOG = Logger.getLogger("genj.plugin.sosa");

	private final Resources RESOURCES = Resources.get(this);

	
	public MenuActionGET(String menuItem, boolean status, SosaIndexation sosaIndexation) {
		this.status = status;
		this.sosaIndexation = sosaIndexation;
		LOG.fine("Set menu item GET========= = " + menuItem);
		setText(RESOURCES.getString(menuItem));
	}

	
	public void setSosaIndexationValue(SosaIndexation indexation) {
		this.sosaIndexation=indexation;
	}

	
	public boolean getVisibilityStatus() {
		return status;
	}

	
	public void	setVisibilityStatus(boolean status) {
		this.status=status;
	}


	
	protected void execute() {
		LOG.fine("Passe SOSA_GET");
		
		ChoiceWidget choice = new ChoiceWidget(sosaIndexation
				.getSosaIndexArray(),
				sosaIndexation.getSosaIndexArray().length > 0 ? sosaIndexation
						.getSosaIndexArray()[0] : "");
		int rc = WindowManager.getInstance(getTarget()).openDialog(null,
				"Choisir un index", WindowManager.QUESTION_MESSAGE, choice,
				Action2.okCancel(), getTarget());
		String result = rc == 0 ? choice.getText() : null;
		if (result != null) {
			LOG.fine("individual is : "
					+ sosaIndexation.getSosaMap().get(Integer.parseInt(result))
							.toString());
		}
	}
}
