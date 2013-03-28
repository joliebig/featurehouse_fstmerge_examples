package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.mimer.ws.validateSQL.SessionData;

public class WebServiceSession
{
	
	private final WebServicePreferences _prefs;

	
	private final WebServiceSessionProperties _sessionProps;

	private SessionData _sessionData;

	public WebServiceSession(WebServicePreferences prefs, WebServiceSessionProperties sessionProps)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		if (sessionProps == null)
		{
			throw new IllegalArgumentException("WebServiceSessionProperties == null");
		}
		_prefs = prefs;
		_sessionProps = sessionProps;
	}

	public boolean isOpen()
	{
		return _sessionData != null;
	}

	
	public void open() throws RemoteException, ServiceException
	{
		Service l_service = new Service();
		Call l_call = (Call)l_service.createCall();

		
		l_call.setTargetEndpointAddress(IWebServiceURL.WEB_SERVICE_URL);
		l_call.setOperationName(new QName("SQL99Validator", "openSession"));

		
		
		l_call.addParameter("a_userName", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_password", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_callingProgram", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_callingProgramVersion", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_targetDbms", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_targetDbmsVersion", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_connectionTechnology", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_connectionTechnologyVersion", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_interactive", XMLType.XSD_INT, ParameterMode.IN);

		QName l_qn = new QName(IWebServiceURL.REQUEST_URL, "SessionData");

		l_call.registerTypeMapping(SessionData.class, l_qn,
			new org.apache.axis.encoding.ser.BeanSerializerFactory(SessionData.class, l_qn),
			new org.apache.axis.encoding.ser.BeanDeserializerFactory(SessionData.class, l_qn));

		
		l_call.setReturnType(l_qn);

		
		final boolean anonLogon = _prefs.getUseAnonymousLogon();
		final boolean anonClient = _prefs.getUseAnonymousClient();
		final boolean anonDBMS = _sessionProps.getUseAnonymousDBMS();

		final Object[] parms = new Object[]
		{
			anonLogon ? "anonymous" : _prefs.getUserName(),
			anonLogon ? "N/A" : _prefs.retrievePassword(),
			anonClient ? "N/A" : _prefs.getClientName(),
			anonClient ? "N/A" : _prefs.getClientVersion(),
			anonDBMS ? "N/A" : _sessionProps.getTargetDBMSName(),
			anonDBMS ? "N/A" : _sessionProps.getTargetDBMSVersion(),
			anonDBMS ? "N/A" : _sessionProps.getConnectionTechnology(),
			anonDBMS ? "N/A" : _sessionProps.getConnectionTechnologyVersion(),
			Integer.valueOf(1)	
		};
		_sessionData = (SessionData)l_call.invoke(parms);
	}

	
	public void close()
	{
		_sessionData = null;
	}

	
	String getTargetURL()
	{
		validateState();
		return _sessionData.getTarget();
	}

	
	int getSessionID()
	{
		validateState();
		return _sessionData.getSessionId();
	}

	
	int getSessionKey()
	{
		validateState();
		return _sessionData.getSessionKey();
	}

	private void validateState()
	{
		if (_sessionData == null)
		{
			throw new IllegalStateException("Connection to web service has not been opened");
		}
	}
}

