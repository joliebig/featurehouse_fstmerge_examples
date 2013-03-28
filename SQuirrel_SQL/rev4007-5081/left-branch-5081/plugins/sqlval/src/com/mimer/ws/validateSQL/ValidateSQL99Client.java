
package com.mimer.ws.validateSQL;


import org.apache.axis.AxisFault;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;
import java.rmi.RemoteException;
import javax.xml.namespace.QName;
import java.net.URL;


public class ValidateSQL99Client {
    
    
    
    
    public ValidatorResult callSQL99Validator (URL a_url, int a_sessionId, int a_sessionKey, String a_sqlStatement, String a_resultType) throws RemoteException, ServiceException {

        Service  l_service = new Service();
        Call     l_call    = (Call) l_service.createCall();

        
        l_call.setTargetEndpointAddress( a_url );
        l_call.setOperationName( new QName("SQL99Validator", "validateSQL") );

        
        
        l_call.addParameter( "a_sessionId", XMLType.XSD_INT, ParameterMode.IN );
        
        l_call.addParameter( "a_sessionKey", XMLType.XSD_INT, ParameterMode.IN );
        
        l_call.addParameter( "a_sqlStatement", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_resultType", XMLType.XSD_STRING, ParameterMode.IN );
        
        QName l_qn = new QName( "http://sqlvalidator.mimer.com/v1.0", "ValidatorResult" );


        l_call.registerTypeMapping(ValidatorResult.class, l_qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(ValidatorResult.class, l_qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(ValidatorResult.class, l_qn));        

        
        l_call.setReturnType(l_qn);

        Object l_ret = 
            l_call.invoke( new Object[] { Integer.valueOf(a_sessionId), 
                                          Integer.valueOf(a_sessionKey), 
                                          a_sqlStatement, 
                                          a_resultType} );

        return (ValidatorResult)l_ret;
    }
    
    public SessionData openSession(URL a_url,
                                   String a_userName, String a_password,
                                   String a_callingProgram, String a_callingProgramVersion,
                                   String a_targetDbms, String a_targetDbmsVersion,
                                   String a_connectionTechnology, String a_connectionTechnologyVersion,
                                   int a_interactive) throws RemoteException, ServiceException {
                                       
        Service  l_service = new Service();
        Call     l_call    = (Call) l_service.createCall();

        
        l_call.setTargetEndpointAddress( a_url );
        l_call.setOperationName( new QName("SQL99Validator", "openSession") );

        
        l_call.addParameter( "a_userName", XMLType.XSD_STRING, ParameterMode.IN );
        
        l_call.addParameter( "a_password", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_callingProgram", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_callingProgramVersion", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_targetDbms", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_targetDbmsVersion", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_connectionTechnology", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_connectionTechnologyVersion", XMLType.XSD_STRING, ParameterMode.IN );
        
        
        l_call.addParameter( "a_interactive", XMLType.XSD_INT, ParameterMode.IN );

        QName l_qn = new QName( "http://sqlvalidator.mimer.com/v1.0", "SessionData" );


        l_call.registerTypeMapping(SessionData.class, l_qn,
                      new org.apache.axis.encoding.ser.BeanSerializerFactory(SessionData.class, l_qn),        
                      new org.apache.axis.encoding.ser.BeanDeserializerFactory(SessionData.class, l_qn));        

        
        l_call.setReturnType(l_qn);

        Object l_ret = 
            l_call.invoke( new Object[] { a_userName, 
                                          a_password,
                                          a_callingProgram, 
                                          a_callingProgramVersion,
                                          a_targetDbms, 
                                          a_targetDbmsVersion,
                                          a_connectionTechnology, 
                                          a_connectionTechnologyVersion,
                                          Integer.valueOf(a_interactive)} );

        return (SessionData)l_ret;
    }
    
    public static void main(String args[]) {
        try {
            
            
            
            

			URL l_url = new URL("http://sqlvalidator.mimer.com/v1/services");


            
            ValidateSQL99Client l_valSql = new ValidateSQL99Client ();

            SessionData l_sd = l_valSql.openSession(l_url, "anonymous", "doesn't matter", "OlleClient", "8534", "Mimer SQL Engine", "8.2.4g", "JDBC", "2.0", 2);

            int l_session = l_sd.getSessionId();
            int l_key = l_sd.getSessionKey();
            
            
            
            l_url = new URL(l_sd.getTarget());

            
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "select * from tab1", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "select", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "select * from t1 where a like \"a\"", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert into t1 values (1,2,3)", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert t1 values (1,2,3)", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "delete from t1 where a > 1", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "delete t1 where a < 1", "html"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert into t2 (a,b,c) values (1, 2, 3)", "text"));
            System.out.println(l_valSql.callSQL99Validator(l_url, l_session, l_key, "insert ito t2 (a,b,c) values (1, 2, 3)", "html"));
        }
        catch( Exception e ) {
            if ( e instanceof AxisFault ) {
                System.err.println( ((AxisFault)e).dumpToString() );
            } else
                e.printStackTrace();
        }
    }
};


