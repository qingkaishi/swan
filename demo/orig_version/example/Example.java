package example;
public class Example {
    public static void main (String [] args) {
    	
    	String url="key1=Alice&key2=Bob"; 

        URLParse urlparse = new URLParse (url);          
        
        Thread t1 = new ExampleThread(urlparse ,"key1");    
        Thread t2 = new ExampleThread(urlparse ,"key2");
		
        System.out.println("before parsing: "+urlparse.getURL());	
        
        t2.start ();
		t1.start();
		
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("after parsing: "+urlparse.getURL());
	}
}