

package net.sf.freecol.common.networking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.FreeColException;

import org.w3c.dom.Element;
import org.xml.sax.SAXException;


final class ReceivingThread extends Thread {
    private static final Logger logger =
            Logger.getLogger(ReceivingThread.class.getName());

    
    private static final int MAXIMUM_RETRIES = 5;

    private final FreeColNetworkInputStream in;

    private XMLStreamReader xmlIn = null;

    private boolean shouldRun;

    private int nextNetworkReplyId = 1;

    private final Map<Integer, NetworkReplyObject> threadsWaitingForNetworkReply;

    private final Connection connection;

    private boolean locked = false;


    
    ReceivingThread(Connection connection, InputStream in, String threadName) {
        super(threadName + "ReceivingThread - " + connection.toString());

        this.connection = connection;
        this.in = new FreeColNetworkInputStream(in);

        shouldRun = true;

        threadsWaitingForNetworkReply = Collections.synchronizedMap(
                new HashMap<Integer, NetworkReplyObject>());
    }

    
    public synchronized int getNextNetworkReplyId() {
        return nextNetworkReplyId++;
    }

    
    public NetworkReplyObject waitForNetworkReply(int networkReplyId) {
        NetworkReplyObject nro = new NetworkReplyObject(networkReplyId, false);
        threadsWaitingForNetworkReply.put(networkReplyId, nro);
        return nro;
    }

    
    public NetworkReplyObject waitForStreamedNetworkReply(int networkReplyId) {
        NetworkReplyObject nro = new NetworkReplyObject(networkReplyId, true);
        threadsWaitingForNetworkReply.put(networkReplyId, nro);
        return nro;
    }

    
    public void run() {
        int timesFailed = 0;

        try {
            while (shouldRun()) {
                try {
                    listen();
                    timesFailed = 0;
                } catch (XMLStreamException e) {
                    timesFailed++;
                    
                    if (shouldRun && timesFailed > MAXIMUM_RETRIES) {
                        disconnect();
                    }
                } catch (SAXException e) {
                    timesFailed++;
                    
                    if (shouldRun && timesFailed > MAXIMUM_RETRIES) {
                        disconnect();
                    }
                } catch (IOException e) {
                    
                    if (shouldRun) {
                        disconnect();
                    }
                }
            }
        } finally {
            askToStop();
        }
    }

    public void unlock() {
        locked = false;
    }

    
    private void listen() throws IOException, SAXException, XMLStreamException {
        while (locked) {
            try {
                
                Thread.sleep(1);
            } catch (InterruptedException e) {
                
            }
        }

        BufferedInputStream bis = new BufferedInputStream(in);

        final int LOOK_AHEAD = 500;
        in.enable();
        bis.mark(LOOK_AHEAD);

        if (!shouldRun()) {
            return;
        }

        
        if (FreeCol.isInDebugMode()) {
            byte[] buf = new byte[LOOK_AHEAD];
            int r = bis.read(buf, 0, LOOK_AHEAD);
            if (r > 0) {
                System.out.print(new String(buf, 0, r));
                if (buf[LOOK_AHEAD - 1] != 0) {
                    System.out.println("...");
                } else {
                    System.out.println();
                }
                System.out.println();
            }
            bis.reset();
        }
        

        XMLInputFactory xif = XMLInputFactory.newInstance();
        xmlIn = xif.createXMLStreamReader(bis);
        xmlIn.nextTag();

        boolean disconnectMessage =
                (xmlIn.getLocalName().equals("disconnect")) ? true : false;
        if (xmlIn.getLocalName().equals("reply")) {

            String networkReplyID =
                    xmlIn.getAttributeValue(null, "networkReplyId");

            NetworkReplyObject nro = threadsWaitingForNetworkReply.remove(
                    Integer.valueOf(networkReplyID));

            if (nro != null) {
                if (nro.isStreamed()) {
                    locked = true;
                    nro.setResponse(xmlIn);
                } else {
                    xmlIn.close();
                    xmlIn = null;
                    bis.reset();

                    final Message msg = new Message(bis);
                    nro.setResponse(msg);
                }
            } else {
                while (xmlIn.hasNext()) {
                    xmlIn.next();
                }
                xmlIn.close();
                xmlIn = null;
                logger.warning("Could not find networkReplyId="
                        + networkReplyID);
            }
        } else {
            xmlIn.close();
            xmlIn = null;
            bis.reset();
            connection.handleAndSendReply(bis);
        }

        if (disconnectMessage) {
            askToStop();
        }
    }

    
    private synchronized boolean shouldRun() {
        return shouldRun;
    }

    
    synchronized void askToStop() {
        shouldRun = false;
        for (NetworkReplyObject o : threadsWaitingForNetworkReply.values()) {
            o.interrupt();
        }
    }

    private void disconnect() {
        if (connection.getMessageHandler() != null) {
            try {
                Element disconnectElement =
                        Message.createNewRootElement("disconnect");
                disconnectElement.setAttribute("reason", "reception exception");
                connection.getMessageHandler().handle(connection,
                        disconnectElement);
            } catch (FreeColException e) {
                e.printStackTrace();
            }
        }
    }

    
    class FreeColNetworkInputStream extends InputStream {
        private static final int BUFFER_SIZE = 8192;

        private static final char END_OF_STREAM = '\n';

        private final InputStream in;

        private byte[] buffer = new byte[BUFFER_SIZE];

        private int bStart = 0;

        private int bEnd = 0;

        private boolean empty = true;

        private boolean wait = false;


        
        FreeColNetworkInputStream(InputStream in) {
            this.in = in;
        }

        
        private boolean fill() throws IOException {
            int r;
            if (bStart < bEnd || empty && bStart == bEnd) {
                if (empty) {
                    bStart = 0;
                    bEnd = 0;
                }
                r = in.read(buffer, bEnd, BUFFER_SIZE - bEnd);
            } else if (bStart == bEnd) {
                throw new IllegalStateException();
            } else {
                r = in.read(buffer, bEnd, bStart - bEnd);
            }

            if (r <= 0) {
                logger.fine("Could not read data from stream.");
                return false;
            }

            empty = false;

            bEnd += r;
            if (bEnd == BUFFER_SIZE) {
                bEnd = 0;
            }
            return true;
        }

        
        void enable() {
            wait = false;
        }

        
        public int read() throws IOException {
            if (wait) {
                return -1;
            }

            if (empty) {
                if (!fill()) {
                    wait = true;
                    return -1;
                }
            }

            if (buffer[bStart] == END_OF_STREAM) {
                bStart++;
                if (bStart == BUFFER_SIZE) {
                    bStart = 0;
                }
                if (bStart == bEnd) {
                    empty = true;
                }
                wait = true;
                return -1;
            } else {
                bStart++;
                if (bStart == bEnd || bEnd == 0 && bStart == BUFFER_SIZE) {
                    empty = true;
                }
                if (bStart == BUFFER_SIZE) {
                    bStart = 0;
                    return buffer[BUFFER_SIZE - 1];
                } else {
                    return buffer[bStart - 1];
                }
            }
        }

        
        public int read(byte[] b, int off, int len) throws IOException {
            if (wait) {
                return -1;
            }

            if (empty) {
                if (!fill()) {
                    wait = true;
                    return -1;
                }
            }

            int r = 0;
            for (; r < len; r++) {
                if (buffer[bStart] == END_OF_STREAM) {
                    bStart++;
                    if (bStart == BUFFER_SIZE) {
                        bStart = 0;
                    }
                    if (bStart == bEnd) {
                        empty = true;
                    }
                    wait = true;
                    return r;
                }

                b[r + off] = buffer[bStart];

                bStart++;
                if (bStart == bEnd || bEnd == 0 && bStart == BUFFER_SIZE) {
                    empty = true;
                    if (!fill()) {
                        wait = true;
                        return -1;
                    }
                }
                if (bStart == BUFFER_SIZE) {
                    bStart = 0;
                }
            }

            return len;
        }
    }
}
