public class Main{

	public static void main(String[] args) {
		Main main = new Main();
		synchronized(main){
		try{
			int x = 1/0;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return;
		
		//System.out.println("xxxxxxxxxxx");
		}
	
	}
}
