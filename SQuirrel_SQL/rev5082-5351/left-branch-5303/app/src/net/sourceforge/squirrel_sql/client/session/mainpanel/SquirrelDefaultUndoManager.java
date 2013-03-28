package net.sourceforge.squirrel_sql.client.session.mainpanel;

import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;
import javax.swing.text.AbstractDocument;
import javax.swing.event.DocumentEvent;

public class SquirrelDefaultUndoManager extends UndoManager
{
   private static final long serialVersionUID = 1L;

    
   public SquirrelDefaultUndoManager()
	{
		super();
		
		setLimit(200000);
	}

	
	protected UndoableEdit editToBeUndone()
	{
		UndoableEdit ue = super.editToBeUndone();

		if (ue == null)
		{
			return null;
		}

		int i = edits.indexOf(ue);
		while (i >= 0)
		{
			UndoableEdit edit = edits.elementAt(i--);
			if (edit.isSignificant())
			{
				if (edit instanceof AbstractDocument.DefaultDocumentEvent)
				{
					if (DocumentEvent.EventType.CHANGE != ((AbstractDocument.DefaultDocumentEvent)edit).getType())
					{
						return edit;
					}
				}
				else
				{
					return edit;
				}
			}
		}
		return null;
	}

	
	protected UndoableEdit editToBeRedone()
	{
		int count = edits.size();
		UndoableEdit ue = super.editToBeRedone();

		if (null == ue)
		{
			return null;
		}

		int i = edits.indexOf(ue);

		while (i < count)
		{
			UndoableEdit edit = edits.elementAt(i++);
			if (edit.isSignificant())
			{
				if (edit instanceof AbstractDocument.DefaultDocumentEvent)
				{
					if (DocumentEvent.EventType.CHANGE != ((AbstractDocument.DefaultDocumentEvent)edit).getType())
					{
						return edit;
					}
				}
				else
				{
					return edit;
				}
			}
		}
		return null;
	}
}
