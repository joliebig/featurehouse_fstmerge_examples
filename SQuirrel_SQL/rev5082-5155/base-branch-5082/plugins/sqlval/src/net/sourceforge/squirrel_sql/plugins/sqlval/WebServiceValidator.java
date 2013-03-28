package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.mimer.ws.validateSQL.ValidatorResult;

public class WebServiceValidator
{
	
	private final WebServiceSession _webServiceSession;

	
	private final WebServiceSessionProperties _prefs;

	
	public WebServiceValidator(WebServiceSession webServiceSession,
							WebServiceSessionProperties prefs)
	{
		super();
		if (webServiceSession == null)
		{
			throw new IllegalArgumentException("WebServiceSession == null");
		}
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_webServiceSession = webServiceSession;
		_prefs = prefs;
	}

	public ValidatorResult validate(String sql) throws ServiceException, RemoteException
	{
		Service l_service = new Service();
		Call l_call = (Call)l_service.createCall();

		
		l_call.setTargetEndpointAddress(_webServiceSession.getTargetURL());
		l_call.setOperationName(new QName("SQL99Validator", "validateSQL"));

		
		
		l_call.addParameter("a_sessionId", XMLType.XSD_INT, ParameterMode.IN);

		
		l_call.addParameter("a_sessionKey", XMLType.XSD_INT, ParameterMode.IN);

		
		l_call.addParameter("a_sqlStatement", XMLType.XSD_STRING, ParameterMode.IN);

		
		
		l_call.addParameter("a_resultType", XMLType.XSD_STRING, ParameterMode.IN);

		QName l_qn = new QName(IWebServiceURL.REQUEST_URL, "ValidatorResult");

		l_call.registerTypeMapping(ValidatorResult.class, l_qn,
			new org.apache.axis.encoding.ser.BeanSerializerFactory(
				ValidatorResult.class,
				l_qn),
			new org.apache.axis.encoding.ser.BeanDeserializerFactory(
				ValidatorResult.class,
				l_qn)
			);

		
		l_call.setReturnType(l_qn);

		
		final Object[] parms = new Object[]
		{
			Integer.valueOf(_webServiceSession.getSessionID()),
			Integer.valueOf(_webServiceSession.getSessionKey()),
			sql, "text"
		};

		
		return (ValidatorResult)l_call.invoke(parms);
	}
}

