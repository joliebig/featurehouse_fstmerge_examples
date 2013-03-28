

package net.sf.freecol.common.model;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import net.sf.freecol.common.model.Operand.OperandType;

import org.w3c.dom.Element;



public final class Limit extends FreeColGameObjectType {

    public static enum Operator {
        EQ, LT, GT, LE, GE
    }

    
    private Operator operator;

    
    private Operand leftHandSide;

    
    private Operand rightHandSide;

    public Limit() {
        
    }

    public Limit(String id, Operand lhs, Operator op, Operand rhs) {
        setId(id);
        leftHandSide = lhs;
        rightHandSide = rhs;
        operator = op;
    }
    
    
    public Operator getOperator() {
        return operator;
    }

    
    public void setOperator(final Operator newOperator) {
        this.operator = newOperator;
    }

    
    public Operand getLeftHandSide() {
        return leftHandSide;
    }

    
    public void setLeftHandSide(final Operand newLeftHandSide) {
        this.leftHandSide = newLeftHandSide;
    }

    
    public Operand getRightHandSide() {
        return rightHandSide;
    }

    
    public void setRightHandSide(final Operand newRightHandSide) {
        this.rightHandSide = newRightHandSide;
    }

    public boolean appliesTo(FreeColObject object) {
        return leftHandSide.appliesTo(object);
    }

    
    public boolean evaluate(Game game) {
        Integer lhs = null;
        switch(leftHandSide.getScopeLevel()) {
        case GAME:
            lhs = leftHandSide.getValue(game);
            break;
        default:
            lhs = leftHandSide.getValue();
        }

        Integer rhs = null;
        switch(rightHandSide.getScopeLevel()) {
        case GAME:
            rhs = rightHandSide.getValue(game);
            break;
        default:
            rhs = rightHandSide.getValue();
        }

        return evaluate(lhs, rhs);
    }

    
    public boolean evaluate(Player player) {
        Integer lhs = null;
        switch(leftHandSide.getScopeLevel()) {
        case PLAYER:
            lhs = leftHandSide.getValue(player);
            break;
        case GAME:
            lhs = leftHandSide.getValue(player.getGame());
            break;
        default:
            lhs = leftHandSide.getValue();
        }

        Integer rhs = null;
        switch(rightHandSide.getScopeLevel()) {
        case PLAYER:
            rhs = rightHandSide.getValue(player);
            break;
        case GAME:
            rhs = rightHandSide.getValue(player.getGame());
            break;
        default:
            rhs = rightHandSide.getValue();
        }

        return evaluate(lhs, rhs);
    }

    
    public boolean evaluate(Settlement settlement) {
        Integer lhs = null;
        switch(leftHandSide.getScopeLevel()) {
        case SETTLEMENT:
            lhs = leftHandSide.getValue(settlement);
            break;
        case PLAYER:
            lhs = leftHandSide.getValue(settlement.getOwner());
            break;
        case GAME:
            lhs = leftHandSide.getValue(settlement.getGame());
            break;
        default:
            lhs = leftHandSide.getValue();
        }

        Integer rhs = null;
        switch(rightHandSide.getScopeLevel()) {
        case SETTLEMENT:
            rhs = rightHandSide.getValue(settlement);
            break;
        case PLAYER:
            rhs = rightHandSide.getValue(settlement.getOwner());
            break;
        case GAME:
            rhs = rightHandSide.getValue(settlement.getGame());
            break;
        default:
            rhs = rightHandSide.getValue();
        }

        return evaluate(lhs, rhs);
    }

    
    public boolean hasOperandType(OperandType type) {
        return leftHandSide.getOperandType() == type
            || rightHandSide.getOperandType() == type;
    }

    private boolean evaluate(Integer lhs, Integer rhs) {
        if (lhs == null || rhs == null) {
            return true;
        }
        switch(operator) {
        case EQ: return lhs == rhs;
        case LT: return lhs < rhs;
        case GT: return lhs > rhs;
        case LE: return lhs <= rhs;
        case GE: return lhs >= rhs;
        default: return false;
        }
    }


        
    public void toXMLImpl(XMLStreamWriter out) throws XMLStreamException {
        out.writeStartElement(getXMLElementTagName());
        writeAttributes(out);
        writeChildren(out);
        out.writeEndElement();
    }

    @Override
    public void readAttributes(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        operator = Enum.valueOf(Operator.class, in.getAttributeValue(null, "operator"));
    }

    @Override
    public void readChildren(XMLStreamReader in, Specification specification)
        throws XMLStreamException {
        while (in.nextTag() != XMLStreamConstants.END_ELEMENT) {
            if ("leftHandSide".equals(in.getLocalName())) {
                leftHandSide = new Operand();
                leftHandSide.readFromXMLImpl(in);
            } else if ("rightHandSide".equals(in.getLocalName())) {
                rightHandSide = new Operand();
                rightHandSide.readFromXMLImpl(in);
            } else {
                logger.warning("Unsupported child element: " + in.getLocalName());
            }
        }
    }
    
    public void writeAttributes(XMLStreamWriter out) throws XMLStreamException {
        out.writeAttribute(ID_ATTRIBUTE_TAG, getId());
        out.writeAttribute("operator", operator.toString());
    }

    public void writeChildren(XMLStreamWriter out) throws XMLStreamException {
        leftHandSide.toXMLImpl(out, "leftHandSide");
        rightHandSide.toXMLImpl(out, "rightHandSide");
    }

    
    public static String getXMLElementTagName() {
        return "limit";
    }

    public String toString() {
        return leftHandSide.toString() + " " + operator.toString() + " "
            + rightHandSide.toString();
    }


}
