package exceptions;

import model.units.Unit;
import simulation.Rescuable;

@SuppressWarnings("serial")
public abstract class UnitException extends SimulationException{
	Unit unit;
	public Unit getUnit() {
		return unit;
	}
	public Rescuable getTarget() {
		return target;
	}
	Rescuable target;
	public UnitException (Unit unit,Rescuable target) {
		super();
	}
	public UnitException(Unit unit, Rescuable target, String message) {
		super(message);
	}

}
