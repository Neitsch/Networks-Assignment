package util;

//This is the text editor interface. 
//Anything you type or change here will be seen by the other person in real time.

/*
 A least recently used cache is a data structure which has a fixed capacity, and when adding a new item when at capacity, discards the least
 recently used items first. Instead of this, consider a least frequently used cache, whose caching rule instead removes the item that is least _frequently used_.

 Implement the get(key) and put(key, value) methods. Assume keys and values are both Integers for this.
 a put on a key thts already in the cache just adds one to the frequency.
*/
import java.util.*;

class Solution {
 private class Node {
     Node next;
     Node prev;
     Integer key;
     Integer value;
     int requestCount = 0;
 }
 
 private class CustomLinkedList {
     Node lowest;
     int count;
     
     CustomLinkedList() {
         lowest = null;
         count = 0;
     }
     
     Node removeLast() {
         Node old = lowest;
         lowest = lowest.next;
         lowest.prev = null;
         count--;
         return old;
     }
     
     void insert(Node n) {
         if(lowest == null) {
             lowest = n;
         } else {
             Node help = lowest;
             lowest.prev = n;
             lowest = n;
             n.next = help;
         }
         count++;
     }
     
     void print() {
         System.out.println("List:");
         Node n = lowest;
         while(n.next != null) {
             System.out.println("Key: " + n.key + ", Value: "+ n.value + " Count " + n.requestCount);
             n = n.next;
         }
         System.out.println("Key: " + n.key + ", Value: "+ n.value + " Count " + n.requestCount);
     }
 }
 
 private Map<Integer, Node> table;
 private CustomLinkedList list;
 private int capacity = 3;
 public Solution() {
     table = new HashMap();
     list = new CustomLinkedList();
 }
 
 public Integer get(Integer key) {
     if(!table.containsKey(key)) {
         return null;
     }
     Node n = table.get(key);
     this.keyIncrement(n);
     list.print();
     return n.value;
 }
 
 public void put(Integer key, Integer value) {
     if(table.containsKey(key)) {
         Node n = table.get(key);
         n.value = value;
         this.keyIncrement(n);
     } else {
         Node n = new Node();
         n.key = key;
         n.value = value;
         this.insertNewNode(n);
     }
     list.print();
 }
 
 private void keyIncrement(Node n) {
     n.requestCount += 1;
     while(n.next != null && n.next.requestCount < n.requestCount) {
         this.switchNodes(n, n.next);
     }
 }
 
 private void insertNewNode(Node n) {
     while(list.count >= capacity) {
    	 list.print();
         Node old = list.removeLast();
         System.out.println("Old: "+ old.key);
         list.print();
         table.remove(old.key);
     }
     list.insert(n);
     table.put(n.key, n);
 }
 
 private void switchNodes(Node prev, Node next) {
     Node previousNode = prev.prev;
     Node nextNode = next.next;
     next.prev = previousNode;
     prev.next = nextNode;
     next.next = prev;
     prev.prev = next;
 }


 public static void main(String[] args) {
     Solution s = new Solution();
     s.put(1,2);
     s.put(2,4);
     s.put(3,6);
     s.get(2);
     s.get(3);
     s.put(4,8);
     System.out.println(s.get(1));
 }
}