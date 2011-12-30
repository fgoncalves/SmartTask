package smartask.client.ui.menu;

import android.os.Bundle;
import smartask.client.ui.menu.command.Command;

/**
 * This is the main menu.
 * 
 * @author Grupo 1
 */
public class RootMenu extends Menu {
	public RootMenu() {
		super(new Command[] { new Command("Profile", "smartask.ui.PROFILE"),
				new Command("Contacts", "smartask.ui.LISTCONTACTS"),
				new Command("Tasks", "smartask.ui.menu.TASKS"),
				new Command("Map", "smartask.client.ui.MAPVIEW"),
				new Command("Configuration", "smartask.ui.menu.CONFIG") });
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
}