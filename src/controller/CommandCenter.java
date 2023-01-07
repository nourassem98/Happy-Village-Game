package controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CannotTreatException;
import exceptions.CitizenAlreadyDeadException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import model.people.CitizenState;
import model.units.Ambulance;
import model.units.DiseaseControlUnit;
import model.units.Evacuator;
import model.units.FireTruck;
import model.units.GasControlUnit;
import model.units.Unit;
import model.units.UnitState;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulator;
import view.GameView;

public class CommandCenter implements SOSListener {
	private GameView gameView;
	private Simulator simulator;
	private ArrayList<JButton> btnsUnits;
	private ArrayList<Unit> units;
	private ArrayList<JButton> btnsBuildings;
	private ArrayList<ResidentialBuilding> buildings;
	private ArrayList<JButton> btnsCitizens;
	private ArrayList<Citizen> Citizens;
	private ArrayList<JButton> AllButtons;
	private JButton currentButton;
	private Unit lastUnit;
	private Rescuable lastRescuable;
	private ArrayList<ResidentialBuilding> destroyedBuildings;
	private ArrayList<Citizen> deadCitizens;

	public ArrayList<JButton> getBtnsCitizens() {
		return btnsCitizens;
	}

	public ArrayList<JButton> getBtnsUnits() {
		return btnsUnits;
	}

	public ArrayList<JButton> getBtnsBuildings() {
		return btnsBuildings;
	}

	public GameView getGameView() {
		return gameView;
	}

	public static boolean sameLocation(Address A, Address B) {
		if (A.getX() != B.getX())
			return false;
		if (A.getY() != B.getY())
			return false;
		return true;
	}

	public CommandCenter() throws Exception {
		gameView = new GameView();
		// gameView.setResizable(false);
		AllButtons = new ArrayList<JButton>();
		destroyedBuildings = new ArrayList<ResidentialBuilding>();
		deadCitizens = new ArrayList<Citizen>();
		btnsUnits = new ArrayList<JButton>();
		btnsBuildings = new ArrayList<JButton>();
		btnsCitizens = new ArrayList<JButton>();
		buildings = new ArrayList<ResidentialBuilding>();
		Citizens = new ArrayList<Citizen>();

		units = new ArrayList<Unit>();
		simulator = new Simulator(this);
		simulator.setEmergencyService(this);

		for (int i = 0; i < simulator.getEmergencyUnits().size(); i++) {
			JTextArea t = new JTextArea();
			JButton x = new JButton();
			Unit z = (Unit) (simulator.getEmergencyUnits().get(i));
			t.setEditable(false);
			x.setIcon(returnIcon(z));
			x.setBorderPainted( false );
			x.setBackground(Color.WHITE);
			x.addActionListener(new Action());
			gameView.addUnit(x);
			btnsUnits.add(x);
			units.add(z);
		}
		ImageIcon building = new ImageIcon("buildings.png");
		ImageIcon building2 = new ImageIcon("buildings2.png");

		for (int x = 0; x < 10; x++) {
			for (int y = 0; y < 10; y++) {
				JButton button = new JButton();
				button.setEnabled(false);
				if(x==0&&y==0){
					button.setBounds(20,20,35,30);
					button.setEnabled(true);
					button.setName("base");
					button.addActionListener(new Action());
					gameView.addBuilding(button);
					continue;
				}
				if(x==0&& y!=0)
					button.setBounds(20,20+42*y,35,30);
				if(x!=0&& y==0)
					button.setBounds(19+52*x,20,35,30);
				if(x!=0&& y!=0)
					button.setBounds(19+52*x,20+42*y,35,30);
				
				button.setBorderPainted( false );
				for (int i = 0; i < simulator.getBuildings().size(); i++) {
					ResidentialBuilding xaas = simulator.getBuildings().get(i);
					if (xaas.getLocation().getX() == x && xaas.getLocation().getY() == y) {
						button.addActionListener(new Action());
						button.setEnabled(true);
						if(Math.random()<0.5)
						button.setIcon(building2);
						else
							button.setIcon(building);
						button.setBackground(Color.white);
						btnsBuildings.add(button);
						buildings.add(xaas);
					}
				}
				if(x!=0 || y!=0){
					button.setName(x + "" + y);
					gameView.addBuilding(button);
					AllButtons.add(button);
					}
			}
		}

		boolean found;
		for (int i = 0; i < simulator.getCitizens().size(); i++) {
			found = false;
			for (int j = 0; j < simulator.getBuildings().size(); j++) {
				if (sameLocation(simulator.getCitizens().get(i).getLocation(),
						simulator.getBuildings().get(j).getLocation()))
					found = true;
			}
			if (!found) {
				Citizen citizen = simulator.getCitizens().get(i);
				Address location = citizen.getLocation();
				int x = location.getX();
				int y = location.getY();
				for (int k = 0; k < AllButtons.size(); k++) {
					int locationbutton = Integer.parseInt(AllButtons.get(k).getName());
					int xloc = locationbutton / 10;
					int yloc = locationbutton % 10;
					if (x == xloc && y == yloc) {
						JButton button = AllButtons.get(k);
						button.addActionListener(new Action());
						Citizens.add(citizen);
						button.setBorderPainted( false );
						btnsCitizens.add(button);
						button.setEnabled(true);
						button.setIcon(new ImageIcon("Person.png"));
						button.setBackground(Color.white);
					}

				}
			}
		}
		JLabel label = new JLabel("Current Cycle: " + simulator.getCurrentCycle());
		Dimension size = label.getPreferredSize();
		label.setBounds(0, 0, size.width, size.height);
		gameView.addTopPanel(label);

		JButton nextCycle1 = new JButton("Next Cycle");
		nextCycle1.setBounds(240, 0, 80, 50);
		nextCycle1.setFont(new Font("x", 0, 16));
		nextCycle1.addActionListener(new ActionNextCycle());
		gameView.addNextPanel(nextCycle1);

		JLabel label1 = new JLabel("Number of casualties: " + simulator.calculateCasualties());
		label1.setBounds(550, 0, 200,20);
		gameView.addTopPanel(label1);
		gameView.setVisible(true);
	}

	public String returnName(Unit z) {
		if (z instanceof Ambulance) {
			return "Ambulance";
		} else if (z instanceof DiseaseControlUnit) {
			return "Disease Control Unit";
		} else if (z instanceof Evacuator) {
			return "Evacuator";
		} else if (z instanceof FireTruck) {
			return "Fire Truck";
		} else if (z instanceof GasControlUnit) {
			return "Gas Control Unit";
		} else
			return z.toString();
	}
	public ImageIcon returnIcon(Unit z) {
		if (z instanceof Ambulance) {
			return new ImageIcon("Ambulancez.jpg");
		} else if (z instanceof DiseaseControlUnit) {
			return new ImageIcon("DiseaseControlUnitz.jpg");
		} else if (z instanceof Evacuator) {
			return new ImageIcon("Evacuatorz.jpg");
		} else if (z instanceof FireTruck) {
			return new ImageIcon("FireTruckz.png");
		} else if (z instanceof GasControlUnit) {
			return new ImageIcon("ghaaz.jpg");
		} else
			return null;
	}

	@Override
	public void receiveSOSCall(Rescuable r) {

		if (r instanceof ResidentialBuilding) {

			if (!buildings.contains(r))
				buildings.add((ResidentialBuilding) r);

		} else {

			if (!Citizens.contains(r))
				Citizens.add((Citizen) r);
		}

	}

	public class ActionRespond implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			try {
				lastUnit.respond(lastRescuable);

			} catch (IncompatibleTargetException | CannotTreatException e1) {
				JFrame error = new JFrame("Error");
				error.setResizable(false);
				error.setBounds(200, 200, 330, 100);
				JLabel actualerror = new JLabel("  " + e1.getMessage());
				error.add(actualerror);
				error.setVisible(true);
				error.setLocation(600 ,400);
			}
			gameView.getRespond().setVisible(false);
			lastUnit = null;
			lastRescuable = null;
		}
	}

	public class ActionNextCycle implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (simulator.checkGameOver()) {
				JFrame gameEnd = new JFrame("Game Ended");
				gameEnd.setBounds(200, 200, 280, 90);
				JLabel score = new JLabel(
						"    Goodgame man,you killed " + simulator.calculateCasualties() + " Human Being");
				JLabel betterluck = new JLabel("          I'm sure you could have done better");
				gameEnd.setLocation(600 ,400);
				gameEnd.add(score, BorderLayout.NORTH);
				gameEnd.add(betterluck, BorderLayout.SOUTH);
				gameEnd.setVisible(true);
			} else {
				getGameView().clearTopPanel();

				try {
					simulator.nextCycle();
				} 
				catch ( CitizenAlreadyDeadException e1) {
					JFrame error = new JFrame("Error");
					error.setResizable(false);
					error.setBounds(200, 200, 330, 100);
					JLabel actualerror = new JLabel(" This Citizen Has Died ");
					error.add(actualerror);
					error.setVisible(true);
					error.setLocation(600 ,400);
				}
				catch (BuildingAlreadyCollapsedException e1) {
					JFrame error = new JFrame("Error");
					error.setResizable(false);
					error.setBounds(200, 200, 330, 100);
					JLabel actualerror = new JLabel(" This building has Fallen ");
					error.add(actualerror);
					error.setVisible(true);
					error.setLocation(600 ,400);
				}
				JLabel label = new JLabel("Current Cycle: " + simulator.getCurrentCycle());
				Dimension size = label.getPreferredSize();
				label.setBounds(0, 0, size.width, size.height);
				gameView.addTopPanel(label);
				getGameView().addTopPanel(label);

				JLabel label1 = new JLabel("Number of casualties: " + simulator.calculateCasualties());
				label1.setBounds(550, 0, 200,20);
				getGameView().addTopPanel(label1);

				for (int i = 0; i < buildings.size(); i++) {
					if (buildings.get(i).getDisaster() != null && !buildings.get(i).getDisaster().isActive()) {
						btnsBuildings.get(i).setBackground(new JButton().getBackground());
					}
				}
				for (int i = 0; i < Citizens.size(); i++) {
					if (Citizens.get(i).getDisaster() != null && !Citizens.get(i).getDisaster().isActive()) {
						btnsCitizens.get(i).setBackground(new JButton().getBackground());
					}
				}
				for (int i = 0; i < buildings.size(); i++) {
					if (buildings.get(i).getDisaster() != null && buildings.get(i).getDisaster().isActive()) {
						btnsBuildings.get(i).setBackground(Color.pink);
					}
				}
				for (int i = 0; i < Citizens.size(); i++) {
					if (Citizens.get(i).getDisaster() != null && Citizens.get(i).getDisaster().isActive()) {
						btnsCitizens.get(i).setBackground(Color.pink);
					}
				}
				for (int i = 0; i < buildings.size(); i++) {
					if (buildings.get(i).getStructuralIntegrity() == 0) {
						btnsBuildings.get(i).setBackground(Color.red);
						if (!destroyedBuildings.contains(buildings.get(i))) {
							destroyedBuildings.add(buildings.get(i));
							JLabel y = new JLabel("building at " + buildings.get(i).getLocation() + " fell");
							gameView.addTemp(y);
							for (int j = 0; j < buildings.get(i).getOccupants().size(); j++) {
								JLabel lww = new JLabel(
										"Citizen at " + buildings.get(i).getOccupants().get(j).getLocation() + " died");
								gameView.addTemp(lww);
								deadCitizens.add(buildings.get(i).getOccupants().get(j));
							}
						}
					}
				}
				for (int i = 0; i < Citizens.size(); i++) {
					if (Citizens.get(i).getState() == CitizenState.DECEASED) {
						btnsCitizens.get(i).setBackground(Color.red);
						if (!deadCitizens.contains(Citizens.get(i))) {
							deadCitizens.add(Citizens.get(i));
							JLabel y = new JLabel("Citizen at " + Citizens.get(i).getLocation() + " died");
							gameView.addTemp(y);
						}
					}
				}

				for (int i = 0; i < simulator.getExecutedDisasters().size(); i++) {
					if (simulator.getExecutedDisasters().get(i).getStartCycle() == simulator.getCurrentCycle()) {
						String x;
						if (simulator.getExecutedDisasters().get(i) instanceof Collapse)
							x = "Collapse";
						else if (simulator.getExecutedDisasters().get(i) instanceof Fire)
							x = "Fire";
						else if (simulator.getExecutedDisasters().get(i) instanceof GasLeak)
							x = "Gas Leak";
						else if (simulator.getExecutedDisasters().get(i) instanceof Infection)
							x = "Infection";
						else
							x = "Injury";
						JLabel y = new JLabel("Disaster " + x + " happened");
						gameView.addTemp(y);
					}
				}
				if (currentButton != null) {
					currentButton.doClick();
				}

			}
			gameView.repaint();
		}
	}

	public class Action implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			getGameView().clearsidePanel();
			currentButton = (JButton) e.getSource();
			JButton clicked = (JButton) e.getSource();
			
			ImageIcon hamdy = new ImageIcon("hamdy.jpg");
			ImageIcon megan = new ImageIcon("meganfox.jpg");
			ImageIcon hamdyrip = new ImageIcon("hamdyrip.jpg");
			ImageIcon meganrip = new ImageIcon("meganfoxrip.jpg");

			if(clicked.getName()=="base"){
				gameView.getSidePanel().setVisible(false);
				gameView.getBaseInfo().setVisible(true);
				gameView.getResidentsScroll().setVisible(false);
				gameView.getResidents2().setVisible(true);
				getGameView().clearBaseInfo();
				JLabel LabelPanel = new JLabel("         		 BASE INFO");
				int added=0;
				getGameView().addBaseInfo(LabelPanel);
				for(int i=0; i<simulator.getEmergencyUnits().size();i++){
					if(sameLocation(simulator.getEmergencyUnits().get(i).getLocation(),new Address(0,0))&& units.get(i).getState()==UnitState.IDLE){
						Font f2 = new Font("TimeRoman", Font.PLAIN, 13);
						JLabel name=new JLabel("Name : "+ returnName(units.get(i)));
						name.setFont(f2);
						name.setBounds(5, 93+(added*110) , 170, 60);
						getGameView().addBaseInfo(name);

						JLabel UnitID = new JLabel("Unit ID: " + units.get(i).getUnitID());
						UnitID.setFont(f2);
						UnitID.setBounds(5, 5+(added*110), 150, 60);
						getGameView().addBaseInfo(UnitID);
						
					

						JLabel Location = new JLabel("Location: " + units.get(i).getLocation());
						Location.setFont(f2);
						Location.setBounds(5, 20+(added*110), 150, 60);
						getGameView().addBaseInfo(Location);

						JLabel Steps = new JLabel("Steps Per Cycle: " + units.get(i).getStepsPerCycle());
						Steps.setFont(f2);
						Steps.setBounds(5, 35+(added*110), 150, 60);
						getGameView().addBaseInfo(Steps);

						if (units.get(i).getTarget() != null) {
							String target;
							if (units.get(i).getTarget() instanceof ResidentialBuilding)
								target = "Residential Building";
							else
								target = "Citizen At ";
							JLabel Target = new JLabel("Target: " + target);
							Target.setFont(f2);
							Target.setBounds(5, 50+(added*110), 190, 60);
							getGameView().addBaseInfo(Target);

							JLabel TargetLocation = new JLabel("Target Location: " + units.get(i).getTarget().getLocation());
							TargetLocation.setFont(f2);
							TargetLocation.setBounds(5, 65+(added*110), 150, 60);
							getGameView().addBaseInfo(TargetLocation);
						} else {
							JLabel Target = new JLabel("Target: none");
							Target.setFont(f2);
							Target.setBounds(5, 50+(added*110), 150, 60);
							getGameView().addBaseInfo(Target);

							JLabel TargetLocation = new JLabel("Target Location: none");
							TargetLocation.setFont(f2);
							TargetLocation.setBounds(5, 65+(added*110), 150, 60);
							getGameView().addBaseInfo(TargetLocation);
						}
						JLabel State = new JLabel("State: " + units.get(i).getState());
						State.setFont(f2);
						State.setBounds(5, 80+(added*110), 150, 60);
						getGameView().addBaseInfo(State);

						if (Integer.parseInt(units.get(i).getUnitID()) == 3) {
							JLabel passengers2 = new JLabel(
									"Number of passengers: " + ((Evacuator) units.get(i)).getPassengers().size());
							passengers2.setBounds(5, 130+(added*110), 150, 15);
							getGameView().addBaseInfo(passengers2);

							
							for (int counter = 0; counter < ((Evacuator) units.get(i)).getPassengers().size(); counter++) {
								JButton button = new JButton();
								button.addActionListener(new Action());
								if ((((Evacuator) units.get(i)).getPassengers().get(counter).getAge()) % 2 == 0) {
									if (((Evacuator) units.get(i)).getPassengers().get(counter)
											.getState() != CitizenState.DECEASED)
										button.setIcon(hamdy);
									else
										button.setIcon(hamdyrip);
								} else {
									if (((Evacuator) units.get(i)).getPassengers().get(counter)
											.getState() != CitizenState.DECEASED)
										button.setIcon(megan);
									else
										button.setIcon(meganrip);
								}
								getGameView().addResidents(button);
								btnsCitizens.add(button);
								Citizens.add(((Evacuator) units.get(i)).getPassengers().get(counter));
							}

						}
						JLabel State1 = new JLabel("             --------------------");
						State.setBounds(5, 85+(added*110), 150, 60);
						getGameView().addBaseInfo(State1);
						added++;
					}
				}
				for(int i=0;i<simulator.getCitizens().size();i++){
					if(sameLocation(simulator.getCitizens().get(i).getLocation(),new Address(0,0))){
						getGameView().addResidents2(btnsCitizens.get(Citizens.indexOf(simulator.getCitizens().get(i))));
							
					}
				}
				
				gameView.setVisible(true);
				return;
			}
			
			
			
			
			gameView.getSidePanel().setVisible(true);
			gameView.getBaseInfo().setVisible(false);
			gameView.getResidentsScroll().setVisible(true);
			gameView.getResidents2().setVisible(false);
			if (getBtnsBuildings().contains(clicked)) {
				int i = getBtnsBuildings().indexOf(clicked);
				JLabel LabelPanel = new JLabel("    THIS BUILDING INFO");
				Font f = new Font("TimesRoman", Font.ITALIC, 16);
				LabelPanel.setFont(f);
				LabelPanel.setSize(200, 20);
				getGameView().addsidePanel(LabelPanel);

				Font f2 = new Font("TimeRoman", Font.PLAIN, 13);

				JLabel Location = new JLabel("Location: " + buildings.get(i).getLocation());
				Location.setFont(f2);
				Location.setBounds(5, 5, 150, 60);
				getGameView().addsidePanel(Location);
				String currentdisaster;
				if (buildings.get(i).getDisaster() != null && buildings.get(i).getDisaster().isActive()) {
					if (buildings.get(i).getDisaster() instanceof Collapse)
						currentdisaster = "Collapse";
					else if (buildings.get(i).getDisaster() instanceof Fire)
						currentdisaster = "Fire";
					else if (buildings.get(i).getDisaster() instanceof GasLeak)
						currentdisaster = "Gas Leak";
					else
						currentdisaster = "none";
				} else
					currentdisaster = "none";
				JLabel Disaster = new JLabel("Disaster: " + currentdisaster);
				Disaster.setFont(f2);
				Disaster.setBounds(5, 20, 150, 60);
				getGameView().addsidePanel(Disaster);

				JLabel StructuralIntegrity = new JLabel(
						"Structural Integrity: " + buildings.get(i).getStructuralIntegrity());
				StructuralIntegrity.setFont(f2);
				StructuralIntegrity.setBounds(5, 35, 150, 60);
				getGameView().addsidePanel(StructuralIntegrity);

				JLabel FireDamage = new JLabel("Fire Damage: " + buildings.get(i).getFireDamage());
				FireDamage.setFont(f2);
				FireDamage.setBounds(5, 50, 150, 60);
				getGameView().addsidePanel(FireDamage);

				JLabel GasLevel = new JLabel("Gas Level: " + buildings.get(i).getGasLevel());
				GasLevel.setFont(f2);
				GasLevel.setBounds(5, 65, 150, 60);
				getGameView().addsidePanel(GasLevel);

				JLabel FoundationDamage = new JLabel("Foundation Damage: " + buildings.get(i).getFoundationDamage());
				FoundationDamage.setFont(f2);
				FoundationDamage.setBounds(5, 80, 150, 60);
				getGameView().addsidePanel(FoundationDamage);

				JLabel NumberOfOccupants = new JLabel("Number Of Occupants: " + buildings.get(i).getOccupants().size());
				NumberOfOccupants.setFont(f2);
				NumberOfOccupants.setBounds(5, 95, 150, 60);
				getGameView().addsidePanel(NumberOfOccupants);

				
				for(int j=0; j<buildings.get(i).getOccupants().size();j++){
					JButton button = new JButton();
					if ((buildings.get(i).getOccupants().get(j).getAge()) % 2 == 0) {
						if (buildings.get(i).getOccupants().get(j).getState() != CitizenState.DECEASED)
							button.setIcon(hamdy);
						else
							button.setIcon(hamdyrip);
					} else {
						if (buildings.get(i).getOccupants().get(j).getState() != CitizenState.DECEASED)
							button.setIcon(megan);
						else
							button.setIcon(meganrip);
				}
					getGameView().addResidents(button);
					button.setBorderPainted(true);
					button.addActionListener(new Action());
					btnsCitizens.add(button);
					Citizens.add(buildings.get(i).getOccupants().get(j));
					
				}
				lastRescuable = buildings.get(i);

			} else if (getBtnsUnits().contains(clicked)) {
				int i = getBtnsUnits().indexOf(clicked);

				JLabel LabelPanel = new JLabel("       THIS UNIT INFO");
				Font f = new Font("TimesRoman", Font.ITALIC, 16);
				LabelPanel.setFont(f);
				LabelPanel.setSize(200, 20);
				getGameView().addsidePanel(LabelPanel);

				Font f2 = new Font("TimeRoman", Font.PLAIN, 13);
				JLabel name=new JLabel("Name : "+ returnName(units.get(i)));
				name.setFont(f2);
				name.setBounds(5, 93 , 170, 60);
				getGameView().addsidePanel(name);

				JLabel UnitID = new JLabel("Unit ID: " + units.get(i).getUnitID());
				UnitID.setFont(f2);
				UnitID.setBounds(5, 5, 150, 60);
				getGameView().addsidePanel(UnitID);
				
			

				JLabel Location = new JLabel("Location: " + units.get(i).getLocation());
				Location.setFont(f2);
				Location.setBounds(5, 20, 150, 60);
				getGameView().addsidePanel(Location);

				JLabel Steps = new JLabel("Steps Per Cycle: " + units.get(i).getStepsPerCycle());
				Steps.setFont(f2);
				Steps.setBounds(5, 35, 150, 60);
				getGameView().addsidePanel(Steps);

				if (units.get(i).getTarget() != null) {
					String target;
					if (units.get(i).getTarget() instanceof ResidentialBuilding)
						target = "Residential Building";
					else
						target = "Citizen At ";
					JLabel Target = new JLabel("Target: " + target);
					Target.setFont(f2);
					Target.setBounds(5, 50, 190, 60);
					getGameView().addsidePanel(Target);

					JLabel TargetLocation = new JLabel("Target Location: " + units.get(i).getTarget().getLocation());
					TargetLocation.setFont(f2);
					TargetLocation.setBounds(5, 65, 150, 60);
					getGameView().addsidePanel(TargetLocation);
				} else {
					JLabel Target = new JLabel("Target: none");
					Target.setFont(f2);
					Target.setBounds(5, 50, 150, 60);
					getGameView().addsidePanel(Target);

					JLabel TargetLocation = new JLabel("Target Location: none");
					TargetLocation.setFont(f2);
					TargetLocation.setBounds(5, 65, 150, 60);
					getGameView().addsidePanel(TargetLocation);
				}
				JLabel State = new JLabel("State: " + units.get(i).getState());
				State.setFont(f2);
				State.setBounds(5, 80, 150, 60);
				getGameView().addsidePanel(State);

				if (Integer.parseInt(units.get(i).getUnitID()) == 3) {
					JLabel passengers2 = new JLabel(
							"Number of passengers: " + ((Evacuator) units.get(i)).getPassengers().size());
					passengers2.setBounds(5, 110, 150, 60);
					getGameView().addsidePanel(passengers2);


					for (int counter = 0; counter < ((Evacuator) units.get(i)).getPassengers().size(); counter++) {
						JButton button = new JButton();
						button.addActionListener(new Action());
						if ((((Evacuator) units.get(i)).getPassengers().get(counter).getAge()) % 2 == 0) {
							if (((Evacuator) units.get(i)).getPassengers().get(counter)
									.getState() != CitizenState.DECEASED)
								button.setIcon(hamdy);
							else
								button.setIcon(hamdyrip);
						} else {
							if (((Evacuator) units.get(i)).getPassengers().get(counter)
									.getState() != CitizenState.DECEASED)
								button.setIcon(megan);
							else
								button.setIcon(meganrip);
						}
						getGameView().addResidents(button);
						btnsCitizens.add(button);
						Citizens.add(((Evacuator) units.get(i)).getPassengers().get(counter));
					}

				}
				lastUnit = units.get(i);

			} else {
				int i = getBtnsCitizens().indexOf(clicked);

				JLabel LabelPanel = new JLabel("     THIS CITIZEN INFO");
				Font f = new Font("TimesRoman", Font.ITALIC, 16);
				LabelPanel.setFont(f);
				LabelPanel.setSize(200, 20);
				getGameView().addsidePanel(LabelPanel);

				Font f2 = new Font("TimeRoman", Font.PLAIN, 13);

				JLabel Location = new JLabel("Location: " + Citizens.get(i).getLocation());
				Location.setFont(f2);
				Location.setBounds(5, 5, 150, 60);
				getGameView().addsidePanel(Location);

				JLabel Name = new JLabel("Name: " + Citizens.get(i).getName());
				Name.setFont(f2);
				Name.setBounds(5, 20, 150, 60);
				getGameView().addsidePanel(Name);

				JLabel Age = new JLabel("Age: " + Citizens.get(i).getAge());
				Age.setFont(f2);
				Age.setBounds(5, 35, 150, 60);
				getGameView().addsidePanel(Age);

				JLabel NationalID = new JLabel("National ID: " + Citizens.get(i).getNationalID());
				NationalID.setFont(f2);
				NationalID.setBounds(5, 50, 150, 60);
				getGameView().addsidePanel(NationalID);

				JLabel HP = new JLabel("HP: " + Citizens.get(i).getHp());
				HP.setFont(f2);
				HP.setBounds(5, 65, 150, 60);
				getGameView().addsidePanel(HP);

				JLabel BloodLoss = new JLabel("BloodLoss: " + Citizens.get(i).getBloodLoss());
				BloodLoss.setFont(f2);
				BloodLoss.setBounds(5, 80, 150, 60);
				getGameView().addsidePanel(BloodLoss);

				JLabel Toxicity = new JLabel("Toxicity: " + Citizens.get(i).getToxicity());
				Toxicity.setFont(f2);
				Toxicity.setBounds(5, 95, 150, 60);
				getGameView().addsidePanel(Toxicity);

				JLabel State = new JLabel("State: " + Citizens.get(i).getState());
				State.setFont(f2);
				State.setBounds(5, 110, 150, 60);
				getGameView().addsidePanel(State);

				String currentdisaster;
				if (Citizens.get(i).getDisaster() != null) {
					if (Citizens.get(i).getDisaster() instanceof Injury)
						currentdisaster = "Injury";
					else
						currentdisaster = "Infection";
				} else
					currentdisaster = "none";

				JLabel Disaster = new JLabel("Disaster: " + currentdisaster);
				Disaster.setFont(f2);
				Disaster.setBounds(5, 125, 150, 60);
				getGameView().addsidePanel(Disaster);

				lastRescuable = Citizens.get(i);

			}
			if (lastRescuable != null && lastUnit != null) {
				gameView.clearRespond();
				gameView.getRespond().setVisible(true);

				JButton respondto = new JButton("RESPOND TO");
				respondto.addActionListener(new ActionRespond());
				respondto.setBounds(210, 35, 130, 50);
				gameView.addRespond(respondto);

				JButton rescuabletorespond = new JButton("rescuable");
				rescuabletorespond.setBounds(365, 7, 160, 105);
				rescuabletorespond.setEnabled(false);

				if (lastRescuable instanceof ResidentialBuilding) {
					if (((ResidentialBuilding) lastRescuable).getStructuralIntegrity() == 0)
						rescuabletorespond.setIcon(new ImageIcon("wrecked.jpg"));
					else if (lastRescuable.getDisaster() != null && lastRescuable.getDisaster() instanceof Fire
							&& lastRescuable.getDisaster().isActive())
						rescuabletorespond.setIcon(new ImageIcon("buildingFire.jpg"));
					else if (lastRescuable.getDisaster() != null && lastRescuable.getDisaster() instanceof GasLeak
							&& lastRescuable.getDisaster().isActive())
						rescuabletorespond.setIcon(new ImageIcon("buildingGasLeak.jpg"));
					else if (lastRescuable.getDisaster() != null && lastRescuable.getDisaster() instanceof Collapse
							&& lastRescuable.getDisaster().isActive())
						rescuabletorespond.setIcon(new ImageIcon("buildingCollapse.jpg"));
					else
						rescuabletorespond.setIcon(new ImageIcon("building.jpg"));
				} else {
					if (((Citizen) lastRescuable).getState() == CitizenState.DECEASED)
						rescuabletorespond.setIcon(new ImageIcon("deceased.jpg"));
					else if (lastRescuable.getDisaster() != null && lastRescuable.getDisaster() instanceof Injury
							&& lastRescuable.getDisaster().isActive())
						rescuabletorespond.setIcon(new ImageIcon("citizenInjury.jpg"));
					else if (lastRescuable.getDisaster() != null && lastRescuable.getDisaster() instanceof Infection
							&& lastRescuable.getDisaster().isActive())
						rescuabletorespond.setIcon(new ImageIcon("citizenInfection.jpg"));
					else
						rescuabletorespond.setIcon(new ImageIcon("citizen.jpg"));
				}

				rescuabletorespond.setDisabledIcon(rescuabletorespond.getIcon());
				gameView.addRespond(rescuabletorespond);
			}
			gameView.setResizable(true);
			gameView.setResizable(false);
		}
	}
	
}
