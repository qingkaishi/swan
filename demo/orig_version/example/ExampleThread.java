package example;
public class ExampleThread extends Thread                                  
{                                                                            
    URLParse urlparse;                                             
    String key;                                                         
    ExampleThread(URLParse url,String key){                                                                        
        this.urlparse = url;                                  
        this.key = key;                                               
    }                                                                       
    public void run()                                               
    {       
    	try{
    		urlparse.parse(key);     
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    		System.exit(1);
    	}
    }                                                                      
}
