package com.sparshui.client;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import com.sparshui.common.ClientProtocol;
import com.sparshui.common.Event;
import com.sparshui.common.Location;
import com.sparshui.common.messages.events.*;
import com.sparshui.common.utils.Converter;


public class ClientToServerProtocol extends ClientProtocol {

	
	public ClientToServerProtocol(Socket socket) throws IOException {
		super(socket);
	}

	
	public boolean processRequest(Client client) {
		try {
			
			int type = recvType();
			
			int length = _in.readInt();
			
			byte[] data = new byte[length];
			
			_in.readFully(data);

			
			switch (type) {
			case MessageType.EVENT:
				handleEvents(client, data);
				break;
			case MessageType.GET_GROUP_ID:
				handleGetGroupID(client, data);
				break;
			case MessageType.GET_ALLOWED_GESTURES:
				handleGetAllowedGestures(client, data);
				break;
			}
		} catch (IOException e) {
			System.err
					.println("[Client Protocol] GestureServer Connection Lost");
			handleEvents(client, null);
			return false;
		}
		return true;
	}

	
	private void handleEvents(Client client, byte[] data) {
	  
	  if (data == null) {	    
      client.processEvent(Integer.MAX_VALUE, null);
      return;
	  }
		
		if (data.length < 1) {
			return;
		}
		
		int groupID = Converter.byteArrayToInt(data);

		
		int eventType = Converter.byteArrayToInt(data, 4);
		
		
		
		byte[] newData = new byte[data.length - 8];
		System.arraycopy(data, 8, newData, 0, data.length - 8);

		Event event = null;

		switch (eventType) {
		case EventType.DRAG_EVENT:
			event = new DragEvent(newData);
			break;
		case EventType.ROTATE_EVENT:
			event = new RotateEvent(newData);
			break;
		case EventType.SPIN_EVENT:
			
			event = new SpinEvent();
			break;
		case EventType.TOUCH_EVENT:
			event = new TouchEvent(newData);
			break;
		case EventType.ZOOM_EVENT:
			event = new ZoomEvent(newData);
			break;
		case EventType.DBLCLK_EVENT:
			event = new DblClkEvent(newData);
			break;
		case EventType.FLICK_EVENT:
			event = new FlickEvent(newData);
			break;
		case EventType.RELATIVE_DRAG_EVENT:
			event = new RelativeDragEvent(newData);
			break;
		}

		if (event != null)
			client.processEvent(groupID, event);

	}

	
	private void handleGetGroupID(Client client, byte[] data)
			throws IOException {
    _out.writeInt(client.getGroupID(new Location(Converter.byteArrayToFloat(data, 0),
        Converter.byteArrayToFloat(data, 4))));
	}

	
  private void handleGetAllowedGestures(Client client, byte[] data)
      throws IOException {
    List gestureIDs = client.getAllowedGestures(Converter.byteArrayToInt(data));
    int length = (gestureIDs == null ? 0 : gestureIDs.size());
    int blen = length * 4;
    for (int i = 0; i < length; i++) {
      Object gid = gestureIDs.get(i);
      if (gid instanceof String)
        blen += ((String) gid).length();
    }
    _out.writeInt(blen);

    
    for (int i = 0; i < length; i++) {
      Object gid = gestureIDs.get(i);
      if (gid instanceof Integer) {
        _out.writeInt(((Integer) gid).intValue());
      } else if (gid instanceof String) {
        int len = ((String) gid).length();
        if (len > 0) {
          _out.writeInt(-len);
          _out.write(Converter.stringToByteArray((String) gid));
        }
      }
    }
  }

	
	private int recvType() throws IOException {
		int data = (int) _in.readByte();
		
		return data;
	}

}
