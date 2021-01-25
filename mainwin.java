import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Color;

public class camara {

	public JFrame frameCamara;
	JLabel path;
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
					camara window = new camara();
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
	public camara() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frameCamara = new JFrame();
		frameCamara.setBounds(100, 100, 636, 140);
		frameCamara.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameCamara.getContentPane().setLayout(null);
		
		JRadioButton brands = new JRadioButton("Brands ");
		brands.setBounds(10, 44, 81, 23);
		frameCamara.getContentPane().add(brands);
		
		JRadioButton categories = new JRadioButton("Categories ");
		categories.setBounds(10, 71, 90, 23);
		frameCamara.getContentPane().add(categories);
		
		JRadioButton description = new JRadioButton("Description");
		description.setBounds(101, 44, 90, 23);
		frameCamara.getContentPane().add(description);
		
		JRadioButton objects = new JRadioButton("Objects");
		objects.setBounds(102, 71, 81, 23);
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
		g.add(categories);
		g.add(description);
		g.add(objects);
		
		JButton open = new JButton("Seleccionar archivo");
		open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource()==open){
				    JFileChooser fc=new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				    fc.setDialogTitle("Selecciona una imagen...");
				    fc.setAcceptAllFileFilterUsed(false);
				    FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes PNG o JPG", "png", "jpg");
				    fc.addChoosableFileFilter(filter);
				    int i=fc.showOpenDialog(open);
				    if(i==JFileChooser.APPROVE_OPTION){    
				        File f=fc.getSelectedFile();    
				        filepath=f.getPath(); 
				        String filename=f.getName();
				        System.out.println(filepath);
				        path.setText(filename);
				        upload.setEnabled(true);
				    } else {
				    	JOptionPane.showMessageDialog(null, "Operacion cancelada por el usuario" ,"Error", 0);
				    }
				}   
			}
		});
		open.setBounds(292, 67, 154, 23);
		frameCamara.getContentPane().add(open);
		
		upload = new JButton("Subir archivo");
		upload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String modo = null;
				if(brands.isSelected()){ //ES EL MODO 1
					modo = "Brands";
					System.out.println("BRANDS (1)");
					post.POST(filepath, modo);
				}else if(categories.isSelected()) { //ES EL MODO 2
					modo = "Categories";
					System.out.println("CATEGORIES (2)");
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
		upload.setBounds(456, 67, 154, 23);
		frameCamara.getContentPane().add(upload);
		upload.setEnabled(false);
	}
}
