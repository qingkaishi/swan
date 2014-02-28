package example;

public class URLParse {
	private String url;

	URLParse(String url) {
		this.url = url;
	}

	public void parse(String key) {
		String val = getVal(key);
		if (val.equals("Alice"))
			replaceVal(key, "A");
		if (val.equals("Bob"))
			replaceVal(key, "B");
	}

	private void replaceVal(String key, String newVal) {
		synchronized (this) {
			int keyPos = url.indexOf(key);
			int valPos = url.indexOf("=", keyPos) + 1;
			int ampPos = url.indexOf("&", keyPos);
			if (ampPos < 0)
				ampPos = url.length();
			url = url.substring(0, valPos) + newVal + url.substring(ampPos);
		}
	}

	private String getVal(String key) {
		//synchronized (this) {
			
			int keyPos = url.indexOf(key);
			int valPos = url.indexOf("=", keyPos) + 1;
			int ampPos = url.indexOf("&", keyPos);
			if (ampPos < 0)
				ampPos = url.length();

			/*
			 * The follow code makes the program easy to crash
			 */
			try {
				if (key.equals("key2"))
					Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			

			return url.substring(valPos, ampPos);
		//}
	}

	public String getURL() {
		return url;
	}
}