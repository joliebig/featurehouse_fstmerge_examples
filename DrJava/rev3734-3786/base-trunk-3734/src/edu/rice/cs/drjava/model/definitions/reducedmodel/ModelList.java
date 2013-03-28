

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import java.util.HashSet;
import java.util.Set;




class ModelList<T> {
  private Node<T> _head;
  private Node<T> _tail;
  
  private int _length;
  
  private Set<Iterator> _listeners;

  
  ModelList() {
    
    
    
    _head = new Node<T>();
    _tail = new Node<T>();
    _head.pred = null;
    _head.succ = _tail;
    _tail.pred = _head;
    _tail.succ = null;
    _length = 0;
    _listeners = new HashSet<Iterator>();
  }

  
  private void insert(Node<T> point, T item) {
    Node<T> ins = new Node<T>(item, point.pred, point);
    point.pred.succ = ins;
    point.pred = ins;
    _length++;
  }

  public void insertFront(T item) {
    Iterator it = new Iterator();
    it.insert(item);
    it.dispose();
  }

  
  private void remove(Node<T> point) {
    if ((point == _head) || (point == _tail))
      throw new RuntimeException("Can't remove head.");
    else
    {
      point.succ.pred = point.pred;
      point.pred.succ = point.succ;
      _length--;
    }
  }

  private void addListener(Iterator thing) {
    this._listeners.add(thing);
  }

  private void removeListener(Iterator thing) {
    this._listeners.remove(thing);
  }

  public int listenerCount() {
    return _listeners.size();
  }
  
  public boolean isEmpty() {
    return (_head.succ == _tail);
  }

  public int length() {
    return _length;
  }

  
  public Iterator getIterator() {
    return new Iterator();
  }


  
  private static class Node<T> {
    Node<T> pred;
    Node<T> succ;
    private T _item;

    Node() {
      _item = null;
      pred = this;
      succ = this;
    }

    Node(T item, Node<T> previous, Node<T> successor) {
      _item = item;
      pred = previous;
      succ = successor;
    }

    T getItem() {
      return _item;
    }
  }

  
  class Iterator {
    private Node<T> _point;
    private int _pos;

    
    public Iterator() {
      _point = ModelList.this._head;
      _pos = 0;
      ModelList.this.addListener(this);
    }

    
    public Iterator(Iterator iter) {
      _point = iter._point;
      _pos = iter._pos;
      ModelList.this.addListener(this);
    }

    public Iterator copy() {
      return new Iterator(this);
    }

    
    public boolean eq(Object thing) {
      return this._point == ((Iterator)(thing))._point;
    }

    
    public void setTo(Iterator it) {
      this._point = it._point;
      this._pos = it._pos;
    }

    
    public void dispose() { ModelList.this.removeListener(this); }

    
    public boolean atStart() { return (_point == ModelList.this._head); }

    
    public boolean atEnd() { return (_point == ModelList.this._tail); }

    
    public boolean atFirstItem() { return (_point.pred == ModelList.this._head); }

    
    public boolean atLastItem() { return (_point.succ == ModelList.this._tail); }

    
    public T current() {
      if (atStart())
        throw new RuntimeException("Attempt to call current on an " +
                                   "iterator in the initial position");
      if (atEnd())
        throw new RuntimeException("Attempt to call current on an " +
                                   "iterator in the final position");
      return _point.getItem();
    }

    
    public T prevItem() {
      if (atFirstItem() || atStart() || ModelList.this.isEmpty())
        throw new RuntimeException("No more previous items.");
      return _point.pred.getItem();
    }

    
    public T nextItem() {
      if (atLastItem() || atEnd() || ModelList.this.isEmpty())
        throw new RuntimeException("No more following items.");
      return _point.succ.getItem();
    }

    
    public void insert(T item) {
      
      if (this.atStart()) next();
      ModelList.this.insert(_point, item);
      _point = _point.pred; 
      notifyOfInsert(_pos);

      
      
      _pos -= 1;
    }

    
    public void remove() {
      Node<T> tempNode = _point.succ;
      ModelList.this.remove(_point);
      _point = tempNode;
      notifyOfRemove(_pos, _point);
    }

    
    public void prev() {
      if (atStart()) {
        throw new RuntimeException("Can't cross list boundary.");
      }
      _point = _point.pred;
      _pos--;
    }

    
    public void next() {
      if (atEnd()) throw new RuntimeException("Can't cross list boundary.");
      _point = _point.succ;
      _pos++;
    }

    
    public void collapse(Iterator iter) {
      int leftPos;
      int rightPos;
      Node<T> rightPoint;

      if (this._pos > iter._pos) {
        leftPos = iter._pos;
        rightPos = this._pos;
        rightPoint = this._point;

        this._point.pred = iter._point;
        iter._point.succ = this._point;
        
        ModelList.this._length -= this._pos - iter._pos - 1;
        notifyOfCollapse(leftPos, rightPos, rightPoint);
      }
      else if (this._pos < iter._pos) {
        leftPos = this._pos;
        rightPos = iter._pos;
        rightPoint = iter._point;

        iter._point.pred = this._point;
        this._point.succ = iter._point;

        ModelList.this._length -= iter._pos - this._pos - 1;
        notifyOfCollapse(leftPos, rightPos, rightPoint);
      }
      else { 
      }
    }

    
    private void notifyOfInsert(int pos) {
      java.util.Iterator<Iterator> iter =
        ModelList.this._listeners.iterator();
      while (iter.hasNext()) {
        Iterator next = iter.next();
        if ( next._pos < pos ) {
          
        }
        else { 
          next._pos += 1;
        }
      }
    }

    
    private void notifyOfRemove(int pos, Node<T> point) {
      java.util.Iterator<Iterator> iter =
        ModelList.this._listeners.iterator();
      while (iter.hasNext()) {
        Iterator next = iter.next();
        if ( next._pos < pos ) {
          
        }
        else if ( next._pos == pos ) {
          next._point = point;
        }
        else { 
          next._pos -= 1;
        }
      }
    }

    
    private void notifyOfCollapse(int leftPos, int rightPos, Node<T> rightPoint) {
      java.util.Iterator<Iterator> iter = ModelList.this._listeners.iterator();
      while (iter.hasNext()) {
        Iterator next = iter.next();
        if ( next._pos <= leftPos ) {
          
        }
        else if (( next._pos > leftPos ) && ( next._pos <= rightPos )) {
          next._pos = leftPos + 1;
          next._point = rightPoint;
        }
        else { 
          next._pos -= (rightPos - leftPos - 1);
        }
      }
    }
  }
}
