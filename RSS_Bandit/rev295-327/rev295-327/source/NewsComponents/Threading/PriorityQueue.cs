using System; 
using System.Collections; namespace  NewsComponents.Collections {
	
 public class  PriorityQueue  : ICollection {
		
  private  BinaryHeap _heap;
 
  public  PriorityQueue() { _heap = new BinaryHeap(); }
 
  public  PriorityQueue(PriorityQueue queue)
  {
   _heap = queue._heap.Clone();
  }
 
  public virtual  void Enqueue(int priority, object value)
  {
   _heap.Insert(priority, value);
  }
 
  public virtual  object Dequeue()
  {
   return _heap.Remove();
  }
 
  public virtual  void Clear()
  {
   _heap.Clear();
  }
 
  public virtual  void CopyTo(System.Array array, int index) { _heap.CopyTo(array, index); }
 
  public virtual  bool IsSynchronized { get { return _heap.IsSynchronized; } }
 
  public virtual  int Count { get { return _heap.Count; } }
 
  public  object SyncRoot { get { return _heap.SyncRoot; } }
 
  public  IEnumerator GetEnumerator() { return _heap.GetEnumerator(); }
 
  public static  PriorityQueue Synchronize(PriorityQueue queue)
  {
   if (queue is SyncPriorityQueue) return queue;
   return new SyncPriorityQueue(queue);
  }
 
  public class  SyncPriorityQueue  : PriorityQueue {
			
   internal  SyncPriorityQueue(PriorityQueue queue)
   {
    if (!(_heap is BinaryHeap.SyncBinaryHeap))
    {
     _heap = BinaryHeap.Synchronize(_heap);
    }
   }

		}

	}

}
