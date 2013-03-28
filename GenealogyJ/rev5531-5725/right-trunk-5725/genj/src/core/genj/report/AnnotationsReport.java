
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
	
	private List<Context> annotations = new ArrayList<Context>();

	
	private String message;

	
    public boolean startAsFo = false;

    
	@Override
	public void start(Object context) throws Throwable
	{
		Gedcom gedcom = getGedcom(context);

		
		annotations.clear();
		message = getName();

		
		super.start(context);

		if (!startAsFo)
			showAnnotationsToUser(gedcom, message, annotations);
		else
		{
			Document doc = new Document(getName());
			doc.startSection(message);
			for (Context ctx : annotations)
			{
				doc.addText(ctx.getText());
				doc.nextParagraph();
			}
		    showDocumentToUser(doc);
		}
	}

	protected void setMessage(String message)
	{
		this.message = message;
	}

	protected void addAnnotation(Context ctx)
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

	
	private void showAnnotationsToUser(Gedcom gedcom, String msg,
			List<Context> annotations)
	{
		if (annotations.isEmpty())
		{
		      getOptionFromUser(message, Report.OPTION_OK);
		}
		else
		{
			
			JPanel content = new JPanel(new BorderLayout());
			content.add(BorderLayout.NORTH, new JLabel(msg));
			content.add(BorderLayout.CENTER, new JScrollPane(
					new ContextListWidget(gedcom, annotations)));

			showComponentToUser(content);
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
