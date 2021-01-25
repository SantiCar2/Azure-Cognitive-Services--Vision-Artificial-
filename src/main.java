
public class main {
	
	public static String key;

	public static void main(String[] args) {
		splash init = new splash();
		mainwin main = new mainwin();

		while(!splash.auth) { //Verifica si ya se autorizo la key
			init.authFrame.setVisible(true);
		}
		key = splash.key;
		init.authFrame.setVisible(false);
		System.out.println("TRUE");
		main.frameCamara.setVisible(true);
		
	}

}
