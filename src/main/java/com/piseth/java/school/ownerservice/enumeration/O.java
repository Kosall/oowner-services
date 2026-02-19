package com.piseth.java.school.ownerservice.enumeration;

public class O {

	public static void main(String[] args) {
		System.out.println("Hello world");
		String name="Kosal Nim Kalar";
		String reverse = reverse(name);
		System.out.println(reverse);

	}
	private static String reverse(String name) {
		String[] split = name.split(" ");
		String n="";
		for(int i=split.length-1;i>=0;i--) {
			n+=split[i];
			
		}
		return n;
	}

}
