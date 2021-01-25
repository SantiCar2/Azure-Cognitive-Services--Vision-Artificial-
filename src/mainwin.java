import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class mainwin {

	public JFrame frameCamara;
	JLabel path;
	JLabel image = new JLabel("");
	JButton upload;
	POST post = new POST();
	String filepath = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainwin window = new mainwin();
					window.frameCamara.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public mainwin() {
		System.loadLibrary( Core.NATIVE_LIBRARY_NAME ); 
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameCamara = new JFrame();
		frameCamara.setBounds(100, 100, 636, 342);
		frameCamara.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Crear frame
		frameCamara.getContentPane().setLayout(null);
		
		JRadioButton brands = new JRadioButton("Brands "); //Crea todo los radiobutton que utilizaremos
		brands.setBounds(10, 44, 81, 23);
		frameCamara.getContentPane().add(brands);
		
		JRadioButton description = new JRadioButton("Description");
		description.setBounds(101, 44, 90, 23);
		frameCamara.getContentPane().add(description);
		
		JRadioButton objects = new JRadioButton("Objects");
		objects.setBounds(10, 71, 81, 23);
		frameCamara.getContentPane().add(objects);
		
		JLabel lblNewLabel = new JLabel("Selecciona que se va a analizar:");
		lblNewLabel.setBounds(10, 11, 181, 23);
		frameCamara.getContentPane().add(lblNewLabel);
		
		path = new JLabel("Selecciona un archivo para continuar...");
		path.setForeground(Color.GRAY);
		path.setBounds(293, 14, 317, 16);
		frameCamara.getContentPane().add(path);
		
		ButtonGroup g = new ButtonGroup();
		g.add(brands);
		g.add(description);
		g.add(objects);
		
		JButton open = new JButton("Seleccionar archivo");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //Cuando se selecciona elegir archivo, se abre el selector de archivos
				if(e.getSource()==open){
				    JFileChooser fc=new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				    fc.setDialogTitle("Selecciona una imagen...");
				    fc.setAcceptAllFileFilterUsed(false);
				    FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes PNG o JPEG", "png", "jpeg", "jpg"); //Se filtran los formatos de imagenes que soporta Azure
				    fc.addChoosableFileFilter(filter);
				    int i=fc.showOpenDialog(open);
				    if(i==JFileChooser.APPROVE_OPTION){    
				        File f=fc.getSelectedFile();    
				        filepath=f.getPath(); 
				        String filename=f.getName();
				        System.out.println(filepath);
				        path.setText(filename);
				        upload.setEnabled(true);
				        
				        try {
					        Size sz = new Size(300,200); //Cambia las dimensiones de la imagen para que no se corte en la vista previe
					        Mat matrizColor = Imgcodecs.imread(filepath, Imgcodecs.IMREAD_COLOR);
					        Mat matrizResized = new Mat();
					        Imgproc.resize(matrizColor, matrizResized, sz , 0, 0, Imgproc.INTER_CUBIC);
					        MatOfByte matrizByteColor = new MatOfByte();
					        if(filepath.contains("png")) {//Inicia el proceso para mostrar la imegn
					        	Imgcodecs.imencode(".png", matrizResized, matrizByteColor); 
					        }else{
					        	Imgcodecs.imencode(".jpg", matrizResized, matrizByteColor); 
					        }
					        byte[] byteArrayColor = matrizByteColor.toArray();
					        InputStream inColor = new ByteArrayInputStream(byteArrayColor);
					        BufferedImage bufImageColor = null;
							  try {
								  bufImageColor = ImageIO.read(inColor);
							  } catch (IOException e1) {
								e1.printStackTrace();
							  } 
							  image.setIcon(new ImageIcon(bufImageColor)); //Muestra la imagen
				        }catch (Exception error) { // En el caso de que OpenCV no pueda ajustar las dimensiones de la imagen, mostrar el error
				        	JOptionPane.showMessageDialog(null, "No se pudo cargar la vista previa de la imagen \n Es imposible ajustar el tamaño de la imagen sea demasiado grande \n Error de OpenCV" ,"Error", 0);
				        }
				        
				        
				    } else {
				    	JOptionPane.showMessageDialog(null, "Operacion cancelada por el usuario" ,"Error", 0);
				    }
				}   
			}
		});
		open.setBounds(10, 269, 282, 23);
		frameCamara.getContentPane().add(open);
		
		upload = new JButton("Subir archivo");
		upload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { //Al presionar subir imagen, identificar el modo que esta seleccionado,  y enviar el path de la imagen con el modod a la clase POST
				String modo = null;
				if(brands.isSelected()){ //ES EL MODO 1
					modo = "Brands";
					System.out.println("BRANDS (1)");
					post.POST(filepath, modo);
				}else if(description.isSelected()) {
					modo = "Description";
					System.out.println("DESCRIPTION (3)");
					post.POST(filepath, modo);
				}else if(objects.isSelected()) {
					modo = "Objects";
					System.out.println("OBJECTS (4)");
					post.POST(filepath, modo);
				} else {
					JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna opcion." ,"Error", 0);
				}
				
				
			}
		});
		upload.setBounds(328, 269, 282, 23);
		frameCamara.getContentPane().add(upload);
		upload.setEnabled(false);
		
		
		image.setBounds(293, 53, 300, 200);
		frameCamara.getContentPane().add(image);
	}
}
