package com.sparshui.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

import com.sparshui.common.ClientProtocol;
import com.sparshui.common.Event;
import com.sparshui.common.Location;
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
    sendType(MessageType.GET_ALLOWED_GESTURES);

    _bufferOut.writeInt(groupID);
    sendBuffer();

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

  
  public int getGroupID(Location location) throws IOException {
    byte[] tempFloat = new byte[4];
    
    sendType(MessageType.GET_GROUP_ID);
    Converter.floatToByteArray(tempFloat, 0, location.getX());
    _bufferOut.write(tempFloat);
    Converter.floatToByteArray(tempFloat, 0, location.getY());
    _bufferOut.write(tempFloat);

    
    sendBuffer();

    
    int ret = _in.readInt();
    return ret;
  }

  

  
  public void processEvents(int groupID, Vector events) throws IOException {
    for (int i = 0; i < events.size(); i++) {
      Event event = (Event) events.get(i);
      sendType(MessageType.EVENT);
      
      
      _bufferOut.writeInt(groupID);
      _bufferOut.write(event.serialize());
      sendBuffer();
    }
  }

  
  

  

  private void sendBuffer() throws IOException {
    _out.writeInt(_buffer.size()); 
    _out.write(_buffer.toByteArray()); 
    _buffer.reset();
  }

  

  private void sendType(int type) throws IOException {
    _out.writeByte((byte) type);
  }

}
