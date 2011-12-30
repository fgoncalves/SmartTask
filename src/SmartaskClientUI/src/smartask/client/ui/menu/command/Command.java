package smartask.client.ui.menu.command;

/**
 * This class can be view as a container. It contains a string representing the
 * action that will be fired when this command object is clicked in a menu. In
 * order to do this, these actions must be defined in the manifest file.
 * 
 * @author Grupo 1
 */
public class Command {
	private String title;
	private String action;

	public Command(String title, String action) {
		super();
		this.title = title;
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	@Override
	public String toString() {
		return title;
	}
}
