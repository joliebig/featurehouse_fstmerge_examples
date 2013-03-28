


package net.sf.freecol.common.model;



public interface FreeColGameObjectListener {



    public void setFreeColGameObject(String id, FreeColGameObject freeColGameObject);

    public void removeFreeColGameObject(String id);

    public void ownerChanged(FreeColGameObject source, Player oldOwner, Player newOwner);
}
