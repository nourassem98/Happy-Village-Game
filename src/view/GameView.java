package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import model.units.Unit;

@SuppressWarnings("serial")
public class GameView extends JFrame {
	private JPanel nextPanel;
	private JPanel topPanel;
	private JPanel rescuePanel;
	private JTextArea infoPanel;
	private JPanel unitPanel;
	private JPanel sidePanel;
	
	public JPanel getSidePanel() {
		return sidePanel;
	}
	private JScrollPane ResidentsScroll;
	private JScrollPane ResidentsScroll2;
	private JPanel Residents;
	private JPanel Residents2;
	private JLabel buildings;
	private JPanel respond;
	private JScrollPane baseInfo;
	
	public JScrollPane getBaseInfo() {
		return baseInfo;
	}
	
	private JScrollPane scroll;
	private JPanel temp;
	private JPanel temp2;
	private JLabel UnitInf;
	public JPanel getRespond() {
		return respond;
	}
	public GameView() {
		Dimension dim= Toolkit.getDefaultToolkit().getScreenSize();
		this.setTitle("Rescue Simulation");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds((dim.width-780)/2,(dim.height-680)/2, 780, 680);
		this.setLayout(null);
	
		
		temp= new JPanel();
		temp.setLayout(new GridLayout(0, 1));
		temp.setBackground(Color.LIGHT_GRAY);
		scroll= new JScrollPane(temp);
		scroll.setBounds(570,400,190,100);
		scroll.setBackground(Color.LIGHT_GRAY);
		add(scroll);
		
		
		
		UnitInf = new JLabel();
		UnitInf.setBounds(20, 507, 100, 50);
		Font f = new Font("TimesRoman", Font.BOLD, 16);
		UnitInf.setFont(f);
		UnitInf.setText("Units : ");
		add(UnitInf);
		
		temp2= new JPanel();
		temp2.setLayout(new GridLayout(0, 1));
		temp2.setBackground(Color.LIGHT_GRAY);
		baseInfo= new JScrollPane(temp2);
		baseInfo.setBounds(570,60,190,170);
		baseInfo.setBackground(Color.LIGHT_GRAY);
		add(baseInfo);
		baseInfo.setVisible(false);
		
		unitPanel=new JPanel();
		unitPanel.setBounds(0,560, 430, 100);
		//unitPanel.setBackground(Color.LIGHT_GRAY);
		add(unitPanel);
	
		
		respond= new JPanel();
		respond.setBounds(230,530,590,120);
		respond.setLayout(null);
		//respond.setBackground(Color.pink);
		add(respond);
		//hereeee ^
		
		nextPanel=new JPanel();
		nextPanel.setBounds(320,0,150,60);
		add(nextPanel);
	
		
		topPanel=new JPanel();
		topPanel.setBounds(40,0,780,20);
		topPanel.setLayout(null);
		topPanel.setVisible(true);
		add(topPanel);
		
		ImageIcon icon1 = new ImageIcon("buildingsmap.png"); 
		buildings=new JLabel();
		buildings.setBounds(20,60,543,448);
		buildings.setIcon(icon1);
		buildings.setLayout(null);
		add(buildings);
		
		sidePanel= new JPanel();
		sidePanel.setVisible(true);
		sidePanel.setLayout(null);
		sidePanel.setBounds(570,60,190,170);
		ImageIcon icon = new ImageIcon("wood.jpg"); 
		JLabel thumb = new JLabel();
		thumb.setBounds(0,0,190,200);
		thumb.setIcon(icon);
		//addsidePanel(thumb);
		sidePanel.setBackground(Color.LIGHT_GRAY);
		add(sidePanel);

		Residents= new JPanel();             
		Residents.setVisible(true); 
		Residents.setBackground(Color.LIGHT_GRAY);
		Residents.setLayout(new GridLayout(3,6));
		ResidentsScroll=new JScrollPane(Residents);
		ResidentsScroll.setBounds(570,230,190,170); 
		                                
		add(ResidentsScroll);           
		
		Residents2= new JPanel();
		Residents2.setVisible(true);
		Residents2.setLayout(new GridLayout(0, 1));
		Residents2.setBackground(Color.LIGHT_GRAY);
		ResidentsScroll2 = new JScrollPane(Residents2);
		ResidentsScroll2.setBounds(570,230,190,170);
		ResidentsScroll2.setBackground(Color.LIGHT_GRAY);
		
		
		add(ResidentsScroll2);
	
		this.setResizable(false);
		setVisible(true);
	}
	public JScrollPane getResidentsScroll() {
		return ResidentsScroll;
	}
	public JScrollPane getResidentsScroll2() {
		return ResidentsScroll2;
	}
	public JPanel getResidents() {
		return Residents;
	}
	public JPanel getResidents2() {
		return Residents2;
	}
	public void viewRescuable(JButton rescuable) {
		rescuePanel.add(rescuable);
	}
	public void addResidents(JButton button){
		Residents.setVisible(false);
		Residents.add(button);
		Residents.setVisible(true);
	}
	public void addResidents(JLabel button){
		Residents.add(button);
	}
	public void addResidents2(JButton button){
		Residents2.add(button);
	}
	public void addTemp(JLabel label){
		Font f2=new Font("X",0,10);
		label.setFont(f2);
		temp.add(label);
		scroll.setVisible(false);
		scroll.setVisible(true);
		
	}
	public void addBuilding(JButton button) {
		buildings.add(button);
	}
	public void clearTopPanel(){
		topPanel.removeAll();
	}
	public void clearsidePanel() {
		Residents.removeAll();
		sidePanel.removeAll();
	}
	public void clearRespond(){
		respond.removeAll();
	}
	
	public void addsidePanel(JLabel label) {
		sidePanel.add(label);
	}
	public void addRespond(JButton button) {
		respond.add(button);
	}
	public void addUnit(JButton unit) {
		unitPanel.add(unit);
	}
	public void addInfo(JTextArea info) {
		infoPanel.add(info);
	}
	public void addTopPanel(JLabel label){
		topPanel.add(label);
	}
	public void addBaseInfo(JLabel label){
		temp2.add(label);
	}
	public void addBaseInfo(JButton button){
		temp2.add(button);
	}
	public void clearBaseInfo(){
		temp2.removeAll();
	}
	public void addNextPanel(JButton button){
		nextPanel.add(button);
	}
	public void addTopPanel(JButton button){
		topPanel.add(button);
	}
	public void updateUnit(Unit Unit) {
		String s="";
			s+=Unit.toString();
		
		s+="Steps per Cycle"+Unit.getStepsPerCycle();
		infoPanel.setText(s);
		
	}
}

