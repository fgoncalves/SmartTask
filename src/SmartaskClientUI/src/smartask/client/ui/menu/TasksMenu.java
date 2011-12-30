package smartask.client.ui.menu;

import smartask.client.ui.menu.command.Command;

/**
 * This class displays a menu that has two options: create and list tasks.
 * 
 * @author Grupo 1
 */
public class TasksMenu extends Menu {

	public TasksMenu() {
		super(new Command[] {
				new Command("View Tasks", "smartask.ui.LISTTASKS"),
				new Command("Create Task", "smartask.ui.CREATETASK") });
	}

}
