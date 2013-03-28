package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.net.MalformedURLException;
import java.net.URL;

public class URLWrapper
{
	public interface IURLWrapperPropertyNames
	{
		String URL = "url";
	}

	private String _externalForm;

	public URLWrapper()
	{
		this(null);
	}

	public URLWrapper(URL url) {
		super();
		setFromURL(url);
	}

	public String getExternalForm()
	{
		return _externalForm;
	}

	public void setExternalForm(String value)
	{
		_externalForm = value;
	}

	public URL createURL() throws MalformedURLException {
		return new URL(_externalForm);
	}

	public void setFromURL(URL value) {
		_externalForm = value != null ? value.toExternalForm() : null;
	}
}
