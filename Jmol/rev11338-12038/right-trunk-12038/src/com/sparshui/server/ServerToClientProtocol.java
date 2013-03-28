package com.sparshui.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import com.sparshui.common.ClientProtocol;
import com.sparshui.common.Event;
import com.sparshui.common.utils.Converter;


public class ServerToClientProtocol extends ClientProtocol {

  private DataOutputStream _bufferOut;
  private ByteArrayOutputStream _buffer;

  
  public ServerToClientProtocol(Socket socket) throws IOException {
    super(socket);
    _buffer = new ByteArrayOutputStream();
    _bufferOut = new DataOutputStream(_buffer);
  }

  
  public Vector getGestures(int groupID) throws IOException {
    Vector gestures = new Vector();
    _bufferOut.writeInt(groupID);
    sendBuffer(MessageType.GET_ALLOWED_GESTURES);

    for (int length = _in.readInt(); length > 0; length -= 4) {
      int gestureID = _in.readInt();
      if (gestureID < 0) {
        
        byte[] bytes = new byte[-gestureID];
        _in.read(bytes);
        gestures.add(Converter.byteArrayToString(bytes));
        length -= bytes.length;
      } else {
        gestures.add(new Integer(gestureID));
      }
    }
    return gestures;
  }

  
  public int getGroupID(TouchPoint touchPoint) throws IOException {
    byte[] tempFloat = new byte[4];
    
    Converter.floatToByteArray(tempFloat, 0, touchPoint.getLocation().getX());
    _bufferOut.write(tempFloat);
    Converter.floatToByteArray(tempFloat, 0, touchPoint.getLocation().getY());
    _bufferOut.write(tempFloat);
    sendBuffer(MessageType.GET_GROUP_ID);

    
    int ret = _in.readInt();
    return ret;
  }

  
  public void processEvents(int groupID, Vector events) throws IOException {
    for (int i = 0; i < events.size(); i++) {
      _bufferOut.writeInt(groupID);
      _bufferOut.write(((Event) events.get(i)).serialize());
      sendBuffer(MessageType.EVENT);
    }
  }

  
  public void processError(int errCode) throws IOException {
    _bufferOut.writeInt(-1);
    _bufferOut.writeInt(errCode);
    sendBuffer(MessageType.EVENT);
  }
 
  

  private void sendBuffer(int type) throws IOException {
    _out.writeByte((byte) type);
    _out.writeInt(_buffer.size()); 
    _out.write(_buffer.toByteArray()); 
    _buffer.reset();
  }

}
