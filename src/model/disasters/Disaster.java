package model.disasters;

import exceptions.BuildingAlreadyCollapsedException;
import exceptions.CitizenAlreadyDeadException;
import simulation.Rescuable;
import simulation.Simulatable;
import model.people.Citizen;
import model.people.CitizenState;
import model.infrastructure.ResidentialBuilding;

public abstract class Disaster implements Simulatable{
	private int startCycle;
	private Rescuable target;
	private boolean active;
	public Disaster(int startCycle, Rescuable target) {
		this.startCycle = startCycle;
		this.target = target;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getStartCycle() {
		return startCycle;
	}
	public Rescuable getTarget() {
		return target;
	}
	public void strike() throws BuildingAlreadyCollapsedException, CitizenAlreadyDeadException 
	{
		if((this instanceof Fire || this instanceof Collapse || this instanceof GasLeak)&&this.getTarget() instanceof ResidentialBuilding){
			if(((ResidentialBuilding)this.getTarget()).getStructuralIntegrity()==0)
				throw new BuildingAlreadyCollapsedException(this);
			else{
			target.struckBy(this);
			active=true;
			}
		}
		if((this instanceof Infection || this instanceof Injury) &&this.getTarget() instanceof Citizen){
			if(((Citizen)this.getTarget()).getState()==CitizenState.DECEASED)
				throw new CitizenAlreadyDeadException(this);
			else{
			target.struckBy(this);
			active=true;
			}
	}
	}
}
