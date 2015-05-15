package net.moonlight.skinconverter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class Skinconverter extends JFrame {
	private static final long serialVersionUID = 1L;
	
	BufferedImage skinLayer1, skinLayer2, convertedImage;
	
	private JPanel header = new JPanel();
	private JPanel preview = new JPanel();
	private JPanel footer = new JPanel();
	
	private JLabel layer1Label = new JLabel("Main skin File");
	private JTextField loadPrimaryDirectory= new JTextField("", 20);
	private JButton browseButton = new JButton("Browse");
	private JButton loadButton = new JButton("Load skin");
	
	private JLabel layer2Label = new JLabel("Second layer skin File (*optional*)");
	private JTextField loadSecondaryDirectory= new JTextField("", 20);
	private JButton browseLayer2Button = new JButton("Browse");
	private JButton loadlayer2Button = new JButton("Load skin");
	
	
	private JButton saveButton = new JButton("      Save      ");
	private JButton convertButton = new JButton("ConvertImage skin");
	
	public Skinconverter() {

		//SETUP WINDOW
		setTitle("Simple Skin Converter by Moonlight");
		setSize(650, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {e.printStackTrace();     }
		
		
		//SETUP GROUP
		GroupLayout layout = new GroupLayout(header);
		header.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(
				layout.createParallelGroup().addGroup(
						layout.createSequentialGroup().addComponent(layer1Label).addComponent(loadPrimaryDirectory).addComponent(browseButton).addComponent(loadButton)
						)
				.addGroup(layout.createSequentialGroup().addComponent(layer2Label).addComponent(loadSecondaryDirectory).addComponent(browseLayer2Button).addComponent(loadlayer2Button)
						)
				);
		layout.setVerticalGroup(
				layout.createSequentialGroup().addGroup(
						layout.createParallelGroup().addComponent(layer1Label).addComponent(loadPrimaryDirectory).addComponent(browseButton).addComponent(loadButton)
				).addGroup(
						layout.createParallelGroup().addComponent(layer2Label).addComponent(loadSecondaryDirectory).addComponent(browseLayer2Button).addComponent(loadlayer2Button)
						)
				);
		
		
		saveButton.setEnabled(false);
		footer.add(convertButton);
		footer.add(saveButton);
		
		add(header, BorderLayout.NORTH);
		add(footer, BorderLayout.SOUTH);
		
	
		//SETUP LAYER 1 BUTTONS
		browseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				final JFileChooser fc = new JFileChooser();
				int response = fc.showOpenDialog(null);
				if(response == JFileChooser.APPROVE_OPTION){
					loadPrimaryDirectory.setText(fc.getSelectedFile().toString());
					revalidate();
					repaint();
					}
				}
			}
		);
		
		loadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String dir = loadPrimaryDirectory.getText();
				if(dir.indexOf("\\")!=-1){
					skinLayer1 = LoadImageFromFile(dir);
					}
				else{
					skinLayer1 = LoadImgFromServer(dir);
					}
				}
			}
		);
		
		
		//SETUP LAYER 2 BUTTONS
		browseLayer2Button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				final JFileChooser fc = new JFileChooser();
				int response = fc.showOpenDialog(null);
				if(response == JFileChooser.APPROVE_OPTION){
					loadSecondaryDirectory.setText(fc.getSelectedFile().toString());
					revalidate();
					repaint();
					}
				}
			}
		);
		
		loadlayer2Button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				String dir = loadSecondaryDirectory.getText();
				if(dir.indexOf("\\")!=-1){
					skinLayer2 = LoadImageFromFile(dir);
					}
				else{
					skinLayer2 = LoadImgFromServer(dir);
					}
				}
			}
		);
		
		
		//SETUP FOOTER BUTTONS
		saveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SaveFile();
				}
			}
		);
		
		convertButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				
				ConvertImage();
				
				//Resize for Preview
				BufferedImage bi = new BufferedImage(preview.getHeight(), preview.getHeight(), BufferedImage.TYPE_INT_ARGB);
				bi.getGraphics().drawImage(convertedImage, 0, 0, preview.getHeight(), preview.getHeight(), 0, 0, convertedImage.getWidth(), convertedImage.getHeight(), null);
				
				JLabel image = new JLabel(new ImageIcon(bi));
				
				//Draw Preview
				preview.removeAll();
				add(preview, BorderLayout.CENTER);
				preview.add(image);
				
				saveButton.setEnabled(true);
				
				revalidate();
				repaint();
				
				}
			}
		);
		
	}

	
	//LOAD AN IMAGE FROM DIRECTORY OR ONLINE
	public BufferedImage LoadImageFromFile(String dir){
		
		BufferedImage tempimage = null;
		
		try {
			tempimage = ImageIO.read(new File(dir));
		} 
		catch (IOException e) {
			FailedToLoadFile(true, dir);
		}
		
		//Resize for Preview
		BufferedImage bi = new BufferedImage(this.getWidth()-50, (this.getWidth()-50)/2, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(tempimage, 0, 0, this.getWidth()-50, (this.getWidth()-50)/2, 0, 0, tempimage.getWidth(), tempimage.getHeight(), null);
		
		JLabel image = new JLabel(new ImageIcon(bi));
	
		//Draw Preview
		preview.removeAll();
		add(preview, BorderLayout.CENTER);
		preview.add(image);
		revalidate();
		repaint();
		
		return tempimage;
		
	}
	
	public BufferedImage LoadImgFromServer(String dir){
		
		BufferedImage tempimage = null;
		String tempstring = "http://s3.amazonaws.com/MinecraftSkins/"+dir+".png";
		try {
			URL url = new URL(tempstring);
			tempimage = ImageIO.read(url);
		}
		catch (IOException e) {
			FailedToLoadFile(false, dir);
		}
	
		//Resize for Preview
		BufferedImage bi = new BufferedImage(this.getWidth()-50, (this.getWidth()-50)/2, BufferedImage.TYPE_INT_ARGB);
		bi.getGraphics().drawImage(tempimage, 0, 0, this.getWidth()-50, (this.getWidth()-50)/2, 0, 0, tempimage.getWidth(), tempimage.getHeight(), null);
		
		JLabel image = new JLabel(new ImageIcon(bi));
	
		//Draw Preview
		preview.removeAll();
		add(preview, BorderLayout.CENTER);
		preview.add(image);
		revalidate();
		repaint();
		
		return tempimage;
		
	}
	
	
	//CUT THE IMAGE UP FOR REARANGEMENT
	public BufferedImage[] CalcSkin (BufferedImage skinIn){
		//Scale Modifier
		int origsize = skinIn.getWidth()/64;
		
		//Arm Front
		BufferedImage armF = new BufferedImage(12*origsize, 12*origsize, BufferedImage.TYPE_INT_ARGB);
		armF.getGraphics().drawImage(skinIn, 0, 0, 12*origsize, 12*origsize, 40*origsize, 20*origsize, (40*origsize) + (12*origsize), (20*origsize) + (12*origsize), null);
		AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
		tx.translate(-armF.getWidth(null), 0);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		armF = op.filter(armF, null);

		//Arm Back
		BufferedImage armB = new BufferedImage(4*origsize, 12*origsize, BufferedImage.TYPE_INT_ARGB);
		armB.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 12*origsize, 52*origsize, 20*origsize, (52*origsize) + (4*origsize), (20*origsize) + (12*origsize), null);
		AffineTransform txab = AffineTransform.getScaleInstance(-1, 1);
		txab.translate(-armB.getWidth(null), 0);
		AffineTransformOp opab = new AffineTransformOp(txab, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		armB = opab.filter(armB, null);

		//Arm Top
		BufferedImage armT = new BufferedImage(4*origsize, 4*origsize, BufferedImage.TYPE_INT_ARGB);
		armT.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 4*origsize, 44*origsize, 16*origsize, (44*origsize) + (4*origsize), (16*origsize) + (4*origsize), null);
		AffineTransform txat = AffineTransform.getScaleInstance(-1, 1);
		txat.translate(-armT.getWidth(null), 0);
		AffineTransformOp opat = new AffineTransformOp(txat, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		armT = opat.filter(armT, null);

		//Arm Bottom
		BufferedImage armBo = new BufferedImage(4*origsize, 4*origsize, BufferedImage.TYPE_INT_ARGB);
		armBo.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 4*origsize, 48*origsize, 16*origsize, (48*origsize) + (4*origsize), (16*origsize) + (4*origsize), null);
		armBo = opat.filter(armBo, null);

		//Leg Front
		BufferedImage legF = new BufferedImage(12*origsize, 12*origsize, BufferedImage.TYPE_INT_ARGB);
		legF.getGraphics().drawImage(skinIn, 0, 0, 12*origsize, 12*origsize, 0, 20*origsize, 0+12*origsize, (20*origsize) + (12*origsize), null);
		legF = op.filter(legF, null);

		//Leg Back
		BufferedImage legB = new BufferedImage(4*origsize, 12*origsize, BufferedImage.TYPE_INT_ARGB);
		legB.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 12*origsize, 12*origsize, 20*origsize, (12*origsize) + (4*origsize), (20*origsize) + (12*origsize), null);
		legB = opab.filter(legB, null);

		//Leg Top
		BufferedImage legT = new BufferedImage(4*origsize, 4*origsize, BufferedImage.TYPE_INT_ARGB);
		legT.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 4*origsize, 4*origsize, 16*origsize, (4*origsize) + (4*origsize), (16*origsize) + (4*origsize), null);
		legT = opat.filter(legT, null);

		//Leg Bottom
		BufferedImage legBo = new BufferedImage(4*origsize, 4*origsize, BufferedImage.TYPE_INT_ARGB);
		legBo.getGraphics().drawImage(skinIn, 0, 0, 4*origsize, 4*origsize, 8*origsize, 16*origsize, (8*origsize) + (4*origsize), (16*origsize) + (4*origsize), null);
		legBo = opab.filter(legBo, null);
		
		
		BufferedImage[] returnimages = new BufferedImage[8];
		
		returnimages[0] = armF;
		returnimages[1] = armB;
		returnimages[2] = armT;
		returnimages[3] = armBo;
		
		returnimages[4] = legF;
		returnimages[5] = legB;
		returnimages[6] = legT;
		returnimages[7] = legBo;
		
		return returnimages;
		
	}
		
	
	//PLACE PEICES OF THE IMAGE WHERE THEY NEED TO GO
	public void ConvertImage(){
		
		//Scale Modifier
		int origsize = skinLayer1.getWidth()/64;

		//Upper
		BufferedImage upper = new BufferedImage(64*origsize, 32*origsize, BufferedImage.TYPE_INT_ARGB);
		upper.getGraphics().drawImage(skinLayer1, 0, 0, 64*origsize, 32*origsize, 0, 0, 64*origsize, 32*origsize, null);
		

		BufferedImage bi = new BufferedImage(64*origsize, 64*origsize, BufferedImage.TYPE_INT_ARGB);
		
		bi.getGraphics().drawImage(upper, 0, 0, 64*origsize, 64*origsize, 0, 0, 64*origsize, 64*origsize, null);
		
		//Arm
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[0], 0, 0, 64*origsize, 64*origsize, -32*origsize, -52*origsize, (-32*origsize) + (64*origsize), (-52*origsize) + (64*origsize), null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[1], 0, 0, 64*origsize, 64*origsize, -44*origsize, -52*origsize, (-44*origsize) + (64*origsize), (-52*origsize) + (64*origsize), null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[2], 0, 0, 64*origsize, 64*origsize, -36*origsize, -48*origsize, (-36*origsize) + (64*origsize), (-48*origsize) + (64*origsize), null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[3], 0, 0, 64*origsize, 64*origsize, -40*origsize, -48*origsize, -40*origsize + 64*origsize, -48*origsize + 64*origsize, null);
		//Leg
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[4], 0, 0, 64*origsize, 64*origsize, -16*origsize, -52*origsize, -16*origsize + 64*origsize, -52*origsize + 64*origsize, null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[5], 0, 0, 64*origsize, 64*origsize, -28*origsize, -52*origsize, -28*origsize + 64*origsize, -52*origsize + 64*origsize, null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[6], 0, 0, 64*origsize, 64*origsize, -20*origsize, -48*origsize, -20*origsize + 64*origsize, -48*origsize+ 64*origsize, null);
		bi.getGraphics().drawImage(CalcSkin(skinLayer1)[7], 0, 0, 64*origsize, 64*origsize, -24*origsize, -48*origsize, -24*origsize + 64*origsize, -48*origsize + 64*origsize, null);
		
		
		//LAYER 2
		if(skinLayer2 != null){
			
			//Body
			BufferedImage body = new BufferedImage(24*origsize, 16*origsize, BufferedImage.TYPE_INT_ARGB);
			body.getGraphics().drawImage(skinLayer2, 0, 0, 24*origsize, 16*origsize, 16*origsize, 16*origsize, 40*origsize, 32*origsize, null);
			
			//Arm
			BufferedImage arm = new BufferedImage(16*origsize, 16*origsize, BufferedImage.TYPE_INT_ARGB);
			arm.getGraphics().drawImage(skinLayer2, 0, 0, 16*origsize, 16*origsize, 40*origsize, 16*origsize, 56*origsize, 32*origsize, null);
			
			//Leg
			BufferedImage leg = new BufferedImage(16*origsize, 16*origsize, BufferedImage.TYPE_INT_ARGB);
			leg.getGraphics().drawImage(skinLayer2, 0, 0, 16*origsize, 16*origsize, 0*origsize, 16*origsize, 16*origsize, 32*origsize, null);
			
			bi.getGraphics().drawImage(body, 0, 0, 64*origsize, 64*origsize, -16*origsize, -32*origsize, (-16*origsize) + (64*origsize), (-32*origsize) + (64*origsize), null);
			bi.getGraphics().drawImage(arm, 0, 0, 64*origsize, 64*origsize, -40*origsize, -32*origsize, (-40*origsize) + (64*origsize), (-32*origsize) + (64*origsize), null);
			bi.getGraphics().drawImage(leg, 0, 0, 64*origsize, 64*origsize, 0*origsize, -32*origsize, (0*origsize) + (64*origsize), (-32*origsize) + (64*origsize), null);
			
			//Arm
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[0], 0, 0, 64*origsize, 64*origsize, -48*origsize, -52*origsize, (-48*origsize) + (64*origsize), (-52*origsize) + (64*origsize), null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[1], 0, 0, 64*origsize, 64*origsize, -60*origsize, -52*origsize, (-60*origsize) + (64*origsize), (-52*origsize) + (64*origsize), null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[2], 0, 0, 64*origsize, 64*origsize, -52*origsize, -48*origsize, (-52*origsize) + (64*origsize), (-48*origsize) + (64*origsize), null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[3], 0, 0, 64*origsize, 64*origsize, -56*origsize, -48*origsize, -56*origsize + 64*origsize, -48*origsize + 64*origsize, null);
			
			//Leg
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[4], 0, 0, 64*origsize, 64*origsize, -0*origsize, -52*origsize, -0*origsize + 64*origsize, -52*origsize + 64*origsize, null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[5], 0, 0, 64*origsize, 64*origsize, -12*origsize, -52*origsize, -12*origsize + 64*origsize, -52*origsize + 64*origsize, null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[6], 0, 0, 64*origsize, 64*origsize, -4*origsize, -48*origsize, -4*origsize + 64*origsize, -48*origsize+ 64*origsize, null);
			bi.getGraphics().drawImage(CalcSkin(skinLayer2)[7], 0, 0, 64*origsize, 64*origsize, -8*origsize, -48*origsize, -8*origsize + 64*origsize, -48*origsize + 64*origsize, null);
		
		}
		
		convertedImage = bi;	
		
	}
	
	
	//ATTEMPT TO SAVE THE FILE
	private String tempsaveloc = null;
	public void SaveFile(){
		
		//Open File Dialog
		final JFileChooser fc = new JFileChooser();
		int response = fc.showSaveDialog(null);
		if(response == JFileChooser.APPROVE_OPTION){
			if(!fc.getSelectedFile().toString().substring(fc.getSelectedFile().toString().length()-4).contentEquals(".png") || fc.getSelectedFile().toString().length() < 4)
				tempsaveloc = fc.getSelectedFile().toString() + ".png";
			else
				tempsaveloc = fc.getSelectedFile().toString();
		}	
		
		//Does the file already exist?
		try{
			ImageIO.read(new File(tempsaveloc));

			final JFrame overwrite = new JFrame();
			overwrite.setTitle("Overwrite?");
			overwrite.setLocationRelativeTo(null);
			overwrite.setSize(400,100);
			overwrite.setResizable(false);
			overwrite.setVisible(true);
			JPanel text = new JPanel();
			JPanel buttons = new JPanel();
		
			JLabel tex = new JLabel("File Already Exists! \n Do you wish to overwrite it?");
			JButton overwr = new JButton("Overwrite");
			JButton cancel = new JButton("Cancel");
		
			buttons.add(cancel);
			buttons.add(overwr);
			text.add(tex);
			overwrite.add(text, BorderLayout.CENTER);
			overwrite.add(buttons, BorderLayout.SOUTH);
		
			//Cancel Saving
			cancel.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					overwrite.dispose();
					SaveFile();
					return;
					}
				}
			);
			
			//Overwrite
			overwr.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					try {
						ImageIO.write(convertedImage, "png", new File(tempsaveloc));
						FileSaved();
					} catch (IOException e2) {}
					overwrite.dispose();
					}
				}
			);
			
			
		}
		
		//File Doesn't Exist. Save
		catch(IOException e){
			try {
				ImageIO.write(convertedImage, "png", new File(tempsaveloc));
				FileSaved();
			} catch (IOException e2) {}
		}
		
	}
	

	//COULDN'T LOAD THE FILE
	public void FailedToLoadFile(boolean local, String dir){
		JFrame loaderror = new JFrame();
		loaderror.setTitle("Error: Failed to load skin");
		loaderror.setLocationRelativeTo(null);
		loaderror.setSize(400,90);
		loaderror.setVisible(true);
		loaderror.setResizable(false);
		JPanel errorp = new JPanel();
		JPanel errorfooter = new JPanel();
		loaderror.add(errorfooter,BorderLayout.SOUTH);
		loaderror.add(errorp);
		
		if(local == true){
			JLabel error = new JLabel("Error: The selected destination does not exist!");
			errorp.add(error, BorderLayout.CENTER);
			JLabel localf = new JLabel(dir);
			errorfooter.add(localf);
		}
		else{
			JLabel error = new JLabel("Error: The skin failed to load!");
			errorp.add(error, BorderLayout.CENTER);
			JLabel usercheck = new JLabel("Make sure the username is spelled correctly: "+dir);
			errorfooter.add(usercheck);
		}
	}
		
	//FILE SAVED
	public void FileSaved(){
		final JFrame sucs = new JFrame();
		sucs.setTitle("Skin Saved");
		sucs.setLocationRelativeTo(null);
		sucs.setResizable(false);
		sucs.setSize(400,100);
		sucs.setVisible(true);
		JPanel txt = new JPanel();
		JPanel button = new JPanel();

		JLabel text = new JLabel("The skin has been converted successfully!");
		JButton ok = new JButton("OK");

		txt.add(text);
		button.add(ok);

		sucs.add(txt, BorderLayout.NORTH);
		sucs.add(button, BorderLayout.SOUTH);

		ok.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				sucs.dispose();
				}
			}
		);
	}
		
}