package exceptions;

import model.disasters.Disaster;

@SuppressWarnings("serial")
public abstract class DisasterException extends SimulationException{
	Disaster disaster;
public Disaster getDisaster() {
		return disaster;
	}
public DisasterException(Disaster disaster) {
	super();
}
public DisasterException(Disaster disaster,String message) {
	super(message);
}
}
