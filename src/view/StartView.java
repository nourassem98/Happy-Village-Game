package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import controller.CommandCenter;

	@SuppressWarnings("serial")
	public class StartView extends JFrame {
		public StartView() {
			Dimension dim= Toolkit.getDefaultToolkit().getScreenSize();
			this.setTitle("Rescue Simulation");
			this.setDefaultCloseOperation(EXIT_ON_CLOSE);
			this.setSize(785, 720);
			this.setLocation((dim.width-785)/2, (dim.height-720)/2);
			this.setLayout(null);
			
			ImageIcon StartImage = new ImageIcon("Start.jpg");
			JLabel x= new JLabel("",StartImage,JLabel.HORIZONTAL);
			//x.setIcon(Start);
			x.setBounds(0, 0,785,720);
			x.setBounds(0, 0, 785, 720);
			x.setVisible(true);
			JButton z=new CircleButton("Start a new game");
			z.setBackground(Color.GRAY);
			z.setBounds(490, 500, 150, 150);
			z.setBackground(Color.BLACK);
			z.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					exitStart();
					JButton clicked=(JButton) e.getSource();
					if(clicked==z) {
						try {
							
							new CommandCenter();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} 	
				}
			});
			add(x);
			z.setVisible(true);
			x.add(z);
			this.setVisible(true);
			this.setResizable(false);
			this.setLocation((dim.width-785)/2, (dim.height-720)/2);
		}
		public void exitStart() {
			this.setVisible(false);
		}
		public static void main (String[]args) {
			new StartView();
		}

}
