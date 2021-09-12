package edu.yu.cs.com1320.Final;

public class Main {

	public static void main(String[] args) {
		System.out.println("line 1, maps to 1:  "+new IPAddress("24.0.0.0/8"));
		System.out.println("line 2, maps to 2:  "+new IPAddress("24.0.0.0/9"));
		System.out.println("line 3, maps to 3:  "+new IPAddress("24.128.0.0/9"));
		System.out.println("line 4, maps to 4:  "+new IPAddress("24.64.0.0/10"));
		System.out.println("line 5, maps to 4:  "+new IPAddress("24.0.0.0/12"));
		System.out.println("line 6, maps to 4:  "+new IPAddress("24.16.0.0/13"));
		System.out.println("line 8, maps to 5:  "+new IPAddress("24.30.0.0/17"));
		System.out.println("line 9, maps to 6:  "+new IPAddress("24.34.0.0/16"));
		System.out.println("line 10, maps to 5: "+new IPAddress("24.60.0.0/14"));
		System.out.println("line 12, maps to 7: "+new IPAddress("24.91.0.0/16"));
		System.out.println("line 13, maps to 6: "+new IPAddress("24.98.0.0/15"));
		System.out.println("line 14, maps to 2: "+new IPAddress("85.0.0.0/8"));
		System.out.println("line 15, maps to 1: "+new IPAddress("85.85.0.0/15"));
	}

}
