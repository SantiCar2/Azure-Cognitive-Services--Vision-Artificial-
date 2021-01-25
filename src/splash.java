import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;

public class splash {
	public static String key;
	public JFrame authFrame;
	private JTextField keyTextField;
	public static boolean auth;
	POST post = new POST();
	static splash window;

	public static void main(String[] args) {
		//System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
				window = new splash();
				try {
					window.authFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

	}

	/**
	 * Create the application.
	 */
	public splash() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		
		authFrame = new JFrame();
		authFrame.setResizable(false);
		authFrame.setTitle("Autenticacion de usuario.");
		authFrame.setBounds(100, 100, 712, 126);
		authFrame.setVisible(true);
		authFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		authFrame.getContentPane().setLayout(null);
		
		JLabel labelAuth1 = new JLabel("Introduce la Key1 que puedes generar desde Azure (recuerda usar los servidores 'westus2'):");
		labelAuth1.setBounds(10, 11, 676, 25);
		authFrame.getContentPane().add(labelAuth1);
		
		keyTextField = new JTextField();
		keyTextField.setText("bit.ly/3aoMRsI");
		keyTextField.setBounds(10, 47, 577, 20);
		authFrame.getContentPane().add(keyTextField);
		keyTextField.setColumns(10);
		
		
		
		JButton validateButton = new JButton("Validar");
		validateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				key = keyTextField.getText();
				if (key.length() == 32) { //Verifica la longitud de la key, debe ser de 32 caracteres
						try {
							boolean keytest = post.keyTest(key); //Realiza la prueva de la key, en la clase POST
							if(!keytest) {
								JOptionPane.showMessageDialog(null, "La key no es valida" ,"ERROR", 0); //En el caso de que no sea valida, retorna error
								auth = false;
								
							}else {
								JOptionPane.showMessageDialog(null, "La Key fue validada exitosamente" ,"", 1); //Si es validada exitosamente cierra la ventana para que la clase MAIN abra la otra.
								auth = true;
								authFrame.dispose();
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
				}else {
					JOptionPane.showMessageDialog(null, "La key debe ser de 32 caracteres" ,"ERROR", 0);
				}
			}
		});
		validateButton.setBounds(597, 47, 89, 20);
		authFrame.getContentPane().add(validateButton);
	}
}
