
package genj.report;

import genj.common.ContextListWidget;
import genj.fo.Document;
import genj.gedcom.Context;
import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.Property;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public abstract class AnnotationsReport extends Report
{
	
	private List<ViewContext> annotations = new ArrayList<ViewContext>();

	
	private String message;

	
    public boolean startAsFo = false;

    
	@Override
	public Object start(Object context) throws Throwable
	{
		Gedcom gedcom = getGedcom(context);

		
		annotations.clear();
		message = getName();

		
		super.start(context);

		if (!startAsFo)
			return showAnnotationsToUser(gedcom, message, annotations);
		else
		{
			Document doc = new Document(getName());
			doc.startSection(message);
			for (ViewContext ctx : annotations)
			{
				doc.addText(ctx.getText());
				doc.nextParagraph();
			}
		    return doc;
		}
	}

	protected void setMessage(String message)
	{
		this.message = message;
	}

	protected void addAnnotation(ViewContext ctx)
	{
		annotations.add(ctx);
	}

	protected void addAnnotation(Entity entity)
	{
		annotations.add(new ViewContext(entity));
	}

	protected void addAnnotation(Entity entity, String text)
	{
		annotations.add(new ViewContext(entity).setText(text));
	}

	protected void addAnnotation(Property property, String text)
	{
		annotations.add(new ViewContext(property).setText(text));
	}

	protected void sortAnnotations()
	{
		Collections.sort(annotations);
	}

	
    @Override
	public boolean usesStandardOut()
	{
		return false;
	}

	
	private JPanel showAnnotationsToUser(Gedcom gedcom, String msg,
			List<ViewContext> annotations)
	{
		if (annotations.isEmpty())
		{
		      getOptionFromUser(message, Report.OPTION_OK);
		      return null;
		}
		else
		{
			
			JPanel content = new JPanel(new BorderLayout());
			content.add(BorderLayout.NORTH, new JLabel(msg));
			content.add(BorderLayout.CENTER, new JScrollPane(
					new ContextListWidget(gedcom, annotations)));

			return content;
		}
	}

	
	private static Gedcom getGedcom(Object context)
	{
		if (context instanceof Gedcom)
			return (Gedcom)context;
		if (context instanceof Entity)
			return ((Entity)context).getGedcom();
		if (context instanceof Property)
			return ((Property)context).getGedcom();
		if (context instanceof Entity[])
			return ((Entity[])context)[0].getGedcom();
		if (context instanceof Property[])
			return ((Property[])context)[0].getGedcom();
		return null;
	}
}
