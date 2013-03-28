

package net.sf.freecol.common.networking;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;


public class Connection {
    private static final Logger logger = Logger.getLogger(Connection.class.getName());




    private static final int TIMEOUT = 5000;
    
    private final OutputStream out;

    private final InputStream in;

    private final Socket socket;

    private final Transformer xmlTransformer;

    private final ReceivingThread thread;

    private final XMLOutputFactory xof = XMLOutputFactory.newInstance();

    private MessageHandler messageHandler;

    private XMLStreamWriter xmlOut = null;

    private int currentQuestionID = -1;
    
    private String threadName;


    
    protected Connection() {
        out = null;
        in = null;
        socket = null;
        thread = null;
        xmlTransformer = null;
    }

    
    public Connection(String host, int port, MessageHandler messageHandler, String threadName) throws IOException {
        this(createSocket(host, port), messageHandler, threadName);
    }

    
    public Connection(Socket socket, MessageHandler messageHandler, String threadName) throws IOException {
        this.messageHandler = messageHandler;
        this.socket = socket;
        this.threadName = threadName;
        
        out = socket.getOutputStream();
        in = socket.getInputStream();

        Transformer myTransformer;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            myTransformer = factory.newTransformer();
            myTransformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        } catch (TransformerException e) {
            logger.log(Level.WARNING, "Failed to install transformer!", e);
            myTransformer = null;
        }
        xmlTransformer = myTransformer;

        thread = new ReceivingThread(this, in, threadName);
        thread.start();
    }

    private static Socket createSocket(String host, int port) throws IOException {
        Socket socket = new Socket();
        SocketAddress addr = new InetSocketAddress(host, port);
        socket.connect(addr, TIMEOUT);
        
        return socket;
    }
    
    
    public void close() throws IOException {
        Element disconnectElement = Message.createNewRootElement("disconnect");
        send(disconnectElement);

        reallyClose();
    }

    
    public void reallyClose() throws IOException {
        if (thread != null) {
            thread.askToStop();
        }

        if (out != null) {
            out.close();
        }

        if (socket != null) {
            socket.close();
        }

        if (in != null) {
            in.close();
        }

        logger.info("Connection closed.");
    }

    
    public void send(Element element) throws IOException {
        
        
        synchronized (out) {
            while (currentQuestionID != -1) {
                try {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Waiting to send element " + element.getTagName() + "...");
                    }
                    out.wait();
                } catch (InterruptedException e) {
                }
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Sending element " + element.getTagName() + "...");
            }
            try {
                xmlTransformer.transform(new DOMSource(element), new StreamResult(out));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to transform and send element!", e);
            }

            out.write('\n');
            out.flush();

            
            out.notifyAll();
        }
    }

    
    public Element ask(Element element) throws IOException {
        int networkReplyId = thread.getNextNetworkReplyId();

        Element questionElement = element.getOwnerDocument().createElement("question");
        questionElement.setAttribute("networkReplyId", Integer.toString(networkReplyId));
        questionElement.appendChild(element);

        if (Thread.currentThread() == thread) {
            logger.warning("Attempt to 'wait()' the ReceivingThread for sending " + element.getTagName());
            throw new IOException("Attempt to 'wait()' the ReceivingThread.");
        } else {
            NetworkReplyObject nro = thread.waitForNetworkReply(networkReplyId);
            send(questionElement);
            Message response = (Message) nro.getResponse();
            if (response == null) return null;
            Element rootElement = response.getDocument().getDocumentElement();
            return (Element) rootElement.getFirstChild();
        }
    }

    
    public XMLStreamWriter ask() throws IOException {
        waitForAndSetNewQuestionId();
        try {
            xmlOut = xof.createXMLStreamWriter(out);
            xmlOut.writeStartElement("question");
            xmlOut.writeAttribute("networkReplyId", Integer.toString(currentQuestionID));
            return xmlOut;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to ask question (" + currentQuestionID + ")", e);
            releaseQuestionId();
            throw new IOException(e.toString());
        }
    }

    
    private void releaseQuestionId() {
        synchronized (out) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(toString() + " released question id " + currentQuestionID);
            }
            currentQuestionID = -1;
            out.notifyAll();
        }
    }

    
    private void waitForAndSetNewQuestionId() {
        synchronized (out) {
            while (currentQuestionID != -1) {
                try {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(toString() + " waiting for question id...");
                    }
                    out.wait();
                } catch (InterruptedException e) {
                    logger.log(Level.WARNING, "Interrupted waiting for question id!", e);
                }
            }
            currentQuestionID = thread.getNextNetworkReplyId();
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(toString() + " installed new question id " + currentQuestionID);
            }
        }
    }

    
    public XMLStreamWriter send() throws IOException {
        waitForAndSetNewQuestionId();
        try {
            xmlOut = xof.createXMLStreamWriter(out);
            return xmlOut;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to send message", e);
            releaseQuestionId();
            throw new IOException(e.toString());
        }
    }

    
    public XMLStreamReader getReply() throws IOException {
        try {
            NetworkReplyObject nro = thread.waitForStreamedNetworkReply(currentQuestionID);
            xmlOut.writeEndElement();
            xmlOut.writeCharacters("\n");
            xmlOut.flush();
            xmlOut.close();
            xmlOut = null;

            XMLStreamReader in = (XMLStreamReader) nro.getResponse();
            in.nextTag();

            return in;
        } catch (Exception e) {
            logger.log(Level.WARNING, toString() + " failed to get reply (" + currentQuestionID + ")", e);
            throw new IOException(e.toString());
        }
    }

    
    public void endTransmission(XMLStreamReader in) throws IOException {
        try {
            if (in != null) {
                while (in.hasNext()) {
                    in.next();
                }
                thread.unlock();
                in.close();
            } else {
                xmlOut.writeCharacters("\n");
                xmlOut.flush();
                xmlOut.close();
                xmlOut = null;
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, toString() + " failed to end transmission", e);
            throw new IOException(e.toString());
        } finally {
            
            releaseQuestionId();
        }
    }

    
    public void sendAndWait(Element element) throws IOException {
        ask(element);
    }

    
    public void setMessageHandler(MessageHandler mh) {
        messageHandler = mh;
    }

    
    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    
    public void handleAndSendReply(final BufferedInputStream in) {
        try {
            in.mark(200);
            final XMLInputFactory xif = XMLInputFactory.newInstance();
            final XMLStreamReader xmlIn = xif.createXMLStreamReader(in);
            xmlIn.nextTag();

            final String networkReplyId = xmlIn.getAttributeValue(null, "networkReplyId");

            final boolean question = xmlIn.getLocalName().equals("question");
            boolean messagedConsumed = false;
            if (messageHandler instanceof StreamedMessageHandler) {
                StreamedMessageHandler smh = (StreamedMessageHandler) messageHandler;
                if (question) {
                    xmlIn.nextTag();
                }
                if (smh.accepts(xmlIn.getLocalName())) {
                    XMLStreamWriter xmlOut = null;
                    if (question) {
                        xmlOut = send();
                        xmlOut.writeStartElement("reply");
                        xmlOut.writeAttribute("networkReplyId", networkReplyId);
                    }
                    smh.handle(this, xmlIn, xmlOut);
                    if (question) {
                        xmlOut.writeEndElement();
                        endTransmission(null);
                    }
                    thread.unlock();
                    messagedConsumed = true;
                }
            }
            if (!messagedConsumed) {
                xmlIn.close();
                in.reset();
                final Message msg = new Message(in);

                final Connection connection = this;
                Thread t = new Thread(msg.getType()) {
                    @Override
                    public void run() {
                        try {
                            Element element = msg.getDocument().getDocumentElement();

                            if (question) {
                                Element reply = messageHandler.handle(connection, (Element) element.getFirstChild());

                                if (reply == null) {
                                    reply = Message.createNewRootElement("reply");
                                    reply.setAttribute("networkReplyId", networkReplyId);
                                    logger.finest("reply == null");
                                } else {
                                    Element replyHeader = reply.getOwnerDocument().createElement("reply");
                                    replyHeader.setAttribute("networkReplyId", networkReplyId);
                                    replyHeader.appendChild(reply);
                                    reply = replyHeader;
                                }

                                connection.send(reply);
                            } else {
                                Element reply = messageHandler.handle(connection, element);

                                if (reply != null) {
                                    connection.send(reply);
                                }
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Message handler failed!", e);
                            logger.warning(msg.getDocument().getDocumentElement().toString());
                        }
                    }
                };
                t.setName(threadName+"MessageHandler:" + t.getName());
                t.start();
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to handle and send reply", e);
        }
    }

    
    

    
    public Socket getSocket() {
        return socket;
    }

    
    @Override
    public String toString() {
        return "Connection[" + getSocket() + "]";
    }
}
