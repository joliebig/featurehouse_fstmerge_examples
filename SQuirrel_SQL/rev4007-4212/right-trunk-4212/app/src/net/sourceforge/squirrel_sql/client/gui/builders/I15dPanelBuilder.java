
package net.sourceforge.squirrel_sql.client.gui.builders;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class I15dPanelBuilder extends PanelBuilder {
	
	private final ResourceBundle bundle;
	
	
	public I15dPanelBuilder(JPanel panel, FormLayout layout,
		ResourceBundle bundle) {
		super(panel, layout);
		this.bundle = bundle;
	}
	
	public I15dPanelBuilder(FormLayout layout, ResourceBundle bundle) {
		this(new JPanel(), layout, bundle);
	}
	
	
	public final JLabel addI15dLabel(String resourceKey,
		CellConstraints constraints) {
		return addLabel(getI15dString(resourceKey), constraints);
	}
	
	public final JLabel addI15dLabel(String resourceKey,
		String encodedConstraints) {
		return addI15dLabel(resourceKey,
			new CellConstraints(encodedConstraints));
	}
	
	public final JComponent addI15dSeparator(String resourceKey,
		CellConstraints constraints) {
		return addSeparator(getI15dString(resourceKey), constraints);
	}
	
	public final JComponent addI15dSeparator(String resourceKey,
		String encodedConstraints) {
		return addI15dSeparator(resourceKey, new CellConstraints(
			encodedConstraints));
	}
	
	public final JLabel addI15dTitle(String resourceKey,
		CellConstraints constraints) {
		return addTitle(getI15dString(resourceKey), constraints);
	}
	
	public final JLabel add15dTitle(String resourceKey,
			String encodedConstraints) {
		return addI15dTitle(resourceKey,
				new CellConstraints(encodedConstraints));
	}
	
	
	protected String getI15dString(String resourceKey) {
		if (bundle == null)
			throw new IllegalStateException("You must specify a ResourceBundle"
					+ " before using the internationalization support.");
		try {
			return bundle.getString(resourceKey);
		} catch (MissingResourceException mre) {
			return resourceKey;
		}
	}
}
