

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.plt.collect.WeakHashSet;
import java.util.Set;


class ModelList<T> {
  private Node<T> _head;
  private Node<T> _tail;
  
  private int _length;
  
  private Set<ModelIterator> _listeners;

  
  ModelList() {
    
    
    _head = new Node<T>();
    _tail = new Node<T>();
    _head._prev = null;
    _head._next = _tail;
    _tail._prev = _head;
    _tail._next = null;
    _length = 0;
    
    
    _listeners = new WeakHashSet<ModelIterator>();
  }

  public void insertFront(T item) { insert(_head._next, item); }
  
  
  private Node<T> insert(Node<T> point, T item) {
    assert point != _head;
    Node<T> newNode = point.insert(item);
    _length++;
    return newNode;
  }

  
  private void remove(Node<T> point) {
    assert point != _head && point != _tail;
    point.remove();
    _length--;
  } 

  private void addListener(ModelIterator that) { _listeners.add(that); }

  private void removeListener(ModelIterator that) { _listeners.remove(that); }

  public int listenerCount() { return _listeners.size(); }
  
  
  public boolean isEmpty() { return _head._next == _tail; }

  public int length() { return _length; }

  
  public ModelIterator getIterator() { return new ModelIterator(); }

  
  private static class Node<T> {
    Node<T> _prev;
    Node<T> _next;
    T _item;

    
    Node() { }

    
    Node(T item, Node<T> pred, Node<T> succ) {
      _item = item;
      _prev = pred;
      _next = succ;
    }
    
    
    Node<T> insert(T item) {
      assert _prev != null;
      Node<T> newNode = new Node<T>(item, _prev, this);
      _prev._next = newNode;
      _prev = newNode;
      return newNode;
    }
    
    
    void remove() {
      assert _prev != null && _next != null;
      _prev._next = _next;
      _next._prev = _prev;
    }
  }

  
  class ModelIterator {
    private Node<T> _point;  
    private int _pos;        

    
    public ModelIterator() {
      _point = _head;
      _pos = 0;
      addListener(this);
    }

    
    public ModelIterator(ModelIterator iter) {
      _point = iter._point;
      _pos = iter._pos;
      addListener(this);
    }

    public ModelIterator copy() { return new ModelIterator(this); }

    
    public boolean eq(ModelIterator that) { return _point == that._point; }

    
    public void setTo(ModelIterator that) {
      _point = that._point;
      _pos = that._pos;
    }

    
    public void dispose() { removeListener(this); }

    
    public boolean atStart() { return _point == _head; }

    
    public boolean atEnd() { return _point == _tail; }

    
    public boolean atFirstItem() { return _point._prev == _head; }

    
    public boolean atLastItem() { return _point._next == _tail; }

    
    public T current() {

      return _point._item;
    }

    
    public T prevItem() {
      assert ! atStart() && ! isEmpty() && ! atFirstItem();
      return _point._prev._item;
    }

    
    public T nextItem() {
      assert ! atStart() && ! isEmpty() && ! atLastItem();
      return _point._next._item;
    }
    
    public int pos() { return _pos; }

    
    public void insert(T item) {
      
      if (atStart()) next();
      _point = ModelList.this.insert(_point, item);
      int savPos = _pos;
      notifyOfInsert(_pos);

      _pos = savPos;  
    }

    
    public void remove() {
      Node<T> succ = _point._next;
      ModelList.this.remove(_point);
      _point = succ;
      notifyOfRemove(_pos, succ);
    }

    
    public void prev() {
      assert ! atStart();
      _point = _point._prev;
      _pos--;
    }

    
    public void next() {
      assert ! atEnd();
      _point = _point._next;
      _pos++;
    }

    
    public void collapse(ModelIterator iter) {
      int itPos = iter._pos;
      int diff = Math.abs(_pos - itPos);
      if (diff <= 1) return; 
      
      int leftPos, rightPos;
      Node<T> leftPoint, rightPoint;
      
      if (_pos > itPos) {
        leftPos = itPos;
        leftPoint = iter._point;
        rightPos = _pos;
        rightPoint = _point;
      }
      else  {
        leftPos = _pos;
        leftPoint = _point;
        rightPos = itPos;
        rightPoint = iter._point;
      }
      
      rightPoint._prev = leftPoint;
      leftPoint._next = rightPoint;
      _length -= rightPos - leftPos - 1;  
      notifyOfCollapse(leftPos, rightPos, rightPoint);
    }

    
    private void notifyOfInsert(int pos) {
      for (ModelIterator listener : _listeners) {
        int lisPos = listener._pos;
        if (lisPos >= pos) listener._pos = lisPos + 1;
      } 
    }

    
    private void notifyOfRemove(int pos, Node<T> point) {
      for (ModelIterator listener : _listeners) {
        int lisPos = listener._pos;
        if (lisPos == pos) listener._point = point;
        else if (lisPos > pos) listener._pos = lisPos - 1;
      }
    }

    
    private void notifyOfCollapse(int leftPos, int rightPos, Node<T> rightPoint) {
      for (ModelIterator listener : _listeners) {
        int lisPos = listener._pos;
        if (lisPos <= leftPos) continue;
        if (lisPos < rightPos) {
          listener._pos = leftPos + 1;
          listener._point = rightPoint;
        }
        else { 
          listener._pos = lisPos - (rightPos - leftPos - 1);
        }
      }
    }
  }
}
