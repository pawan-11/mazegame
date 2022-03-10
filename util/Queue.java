package util;

import java.util.Collection;

public class Queue<E> { //queue adt implemented using linked list, no search operation permitted

	private Node head, tail;	
	private int size; 
	
	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0; //or head == null
	}
	
	public void add(E e) {
		if (head == null) {
			head = new Node(e);
			tail = head;
		}
		else {
			tail.next = new Node(e);
			tail = tail.next;
		}
		size+=1;
	}
	
	public void addAll(Collection<E> c) {
		for (E e: c)
			add(e);
	}
	
	public void clear() { 
		head = null;
		tail = null; //hoping garbage collection takes care of rest :)
	}
	
	public E pop() {
		E old_head = head.element;
		head = head.next;
		return old_head;
	}
	
	public E peek() {
		return head.element;
	}
	
	public String toString() {
		String s = "Queue:";
		Node curr = head;
		while (curr != null) {
			s += curr.element+" ";
			curr = curr.next;
		}
		return s;
	}
	
	private class Node {
		
		private E element;
		private Node next;
		
		private Node(E element) {
			this.element = element;
			next = null;
		}
		
		//setnext, getnext
	}
	
}
