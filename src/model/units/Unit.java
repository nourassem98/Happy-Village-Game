package model.units;

import exceptions.CannotTreatException;
import exceptions.IncompatibleTargetException;
import model.disasters.Collapse;
import model.disasters.Disaster;
import model.disasters.Fire;
import model.disasters.GasLeak;
import model.disasters.Infection;
import model.disasters.Injury;
import model.events.SOSResponder;
import model.events.WorldListener;
import model.infrastructure.ResidentialBuilding;
import model.people.Citizen;
import simulation.Address;
import simulation.Rescuable;
import simulation.Simulatable;

public abstract class Unit implements Simulatable, SOSResponder {
	private String unitID;
	private UnitState state;
	private Address location;
	private Rescuable target;
	private int distanceToTarget;
	private int stepsPerCycle;
	private WorldListener worldListener;

	public Unit(String unitID, Address location, int stepsPerCycle,
			WorldListener worldListener) {
		this.unitID = unitID;
		this.location = location;
		this.stepsPerCycle = stepsPerCycle;
		this.state = UnitState.IDLE;
		this.worldListener = worldListener;
	}


	public void setWorldListener(WorldListener listener) {
		this.worldListener = listener;
	}

	public WorldListener getWorldListener() {
		return worldListener;
	}

	public UnitState getState() {
		return state;
	}

	public void setState(UnitState state) {
		this.state = state;
	}

	public Address getLocation() {
		return location;
	}

	public void setLocation(Address location) {
		this.location = location;
	}

	public String getUnitID() {
		return unitID;
	}

	public Rescuable getTarget() {
		return target;
	}

	public int getStepsPerCycle() {
		return stepsPerCycle;
	}

	public void setDistanceToTarget(int distanceToTarget) {
		this.distanceToTarget = distanceToTarget;
	}

	@Override
	public void respond(Rescuable r) throws IncompatibleTargetException, CannotTreatException{
		String unittype;
		if(this instanceof GasControlUnit)
			unittype="GasControlUnit";
		else if(this instanceof FireTruck)
			unittype="FireTruck";
		else
			unittype="Evacuator";
					
		if(this.isComplatible(r)){
			if(!this.canTreat(r)){
				throw new CannotTreatException(this,r,"Unit "+unittype+" Cannot Treat Building at "+r.getLocation());
			}
			else{
				if (target != null && state == UnitState.TREATING){
					reactivateDisaster();}
					finishRespond(r);
			}
		}
		else{
			throw new IncompatibleTargetException(this,r,"Unit "+unittype+" is Incompatible with Citizens");
		}
	}

	public void reactivateDisaster() {
		Disaster curr = target.getDisaster();
		curr.setActive(true);
	}

	public void finishRespond(Rescuable r) {
		target = r;
		state = UnitState.RESPONDING;
		Address t = r.getLocation();
		distanceToTarget = Math.abs(t.getX() - location.getX())
				+ Math.abs(t.getY() - location.getY());

	}

	public abstract void treat();

	public void cycleStep() {
		if (state == UnitState.IDLE)
			return;
		if (distanceToTarget > 0) {
			distanceToTarget = distanceToTarget - stepsPerCycle;
			if (distanceToTarget <= 0) {
				distanceToTarget = 0;
				Address t = target.getLocation();
				worldListener.assignAddress(this, t.getX(), t.getY());
			}
		} else {
			state = UnitState.TREATING;
			treat();
		}
	}

	public void jobsDone() {
		target = null;
		state = UnitState.IDLE;

	}
	public boolean canTreat(Rescuable r){
		if(this instanceof FireTruck && r instanceof ResidentialBuilding && ((ResidentialBuilding)r).getDisaster() instanceof Fire)
			return true;
		else if(this instanceof GasControlUnit && r instanceof ResidentialBuilding && ((ResidentialBuilding)r).getDisaster() instanceof GasLeak)
			return true;
		else if(this instanceof Evacuator && r instanceof ResidentialBuilding &&  ((ResidentialBuilding)r).getDisaster() instanceof Collapse&& ((ResidentialBuilding)r).getOccupants().size()!=0  && sameLocation(this.getLocation(),((ResidentialBuilding)r).getLocation()))
			return true;
		else if(this instanceof DiseaseControlUnit && r instanceof Citizen && ((Citizen)r).getDisaster() instanceof Infection )
			return true;
		else if(this instanceof Ambulance && r instanceof Citizen && ((Citizen)r).getDisaster() instanceof Injury)
			return true;
		else if(this instanceof Evacuator && r instanceof ResidentialBuilding &&  ((ResidentialBuilding)r).getDisaster() instanceof Collapse && !sameLocation(this.getLocation(),((ResidentialBuilding)r).getLocation()) )
			return true;
		return false;
	}
	public boolean isComplatible(Rescuable r){
		if(this instanceof Ambulance &&r instanceof Citizen)
			return true;
		if(this instanceof DiseaseControlUnit &&r instanceof Citizen)
			return true;
		if(this instanceof Evacuator &&r instanceof ResidentialBuilding)
			return true;
		if(this instanceof FireTruck &&r instanceof ResidentialBuilding)
			return true;
		if(this instanceof GasControlUnit &&r instanceof ResidentialBuilding)
			return true;
		return false;
	}
	public static boolean sameLocation(Address A, Address B){
		if(A.getX()!=B.getX())
			return false;
		if(A.getY()!=B.getY())
			return false;
		return true;
	}
}
