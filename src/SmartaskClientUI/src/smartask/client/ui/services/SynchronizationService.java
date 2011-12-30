package smartask.client.ui.services;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import smartask.client.core.ApplicationConfiguration;
import smartask.client.core.Session;
import smartask.client.db.DatabaseSQLiteQueryEngine;
import smartask.client.observer.IEventListener;
import smartask.client.observer.IEventPublisher;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import engine.services.CreateTasksService;
import engine.services.ListAllUsersServices;
import engine.services.MarkConcludedTasksService;
import engine.services.ObtainFriendsPositionsService;
import engine.services.SelectAllUncompletedTasksService;
import engine.services.UpdateUserService;
import engine.views.CoordinateView;
import engine.views.TaskView;
import engine.views.UserView;

/**
 * This service synchronizes the local database with the main database.
 * 
 * @author Grupo 1
 */
public class SynchronizationService extends Service implements IEventListener {

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private Timer timer;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onCreate();
		ApplicationConfiguration.getInstance().registerEventListener(this);
		TaskAlertNotification.getInstance().setup(getApplicationContext());
		initializeTimer();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		if (timer != null) {
			timer.cancel();
		}
		TaskAlertNotification.getInstance().tearDown(getApplicationContext());
		stopSelf();
	}

	@Override
	public void handleEvent(IEventPublisher source) {
		if (source instanceof ApplicationConfiguration) {
			timer.cancel();
			if (!((Boolean) Session.get("synchronization.disabled"))) {
				initializeTimer();
			}
		}
	}

	private void initializeTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				System.out.println(SynchronizationService.class
						+ " - synchronizing...");

				// Update my profile
				if (Session.contains("username")) {
					UserView newProfile = new UpdateUserService(
							DatabaseSQLiteQueryEngine
									.selectUser((String) Session
											.get("username"))).execute();
					if (newProfile == null)
						Log.e("SYNCHRONIZATION", "Could not get new profile");
					else
						DatabaseSQLiteQueryEngine.alterUser(newProfile);

					// Update users positions
					for (Entry<String, CoordinateView> entry : new ObtainFriendsPositionsService(
							(String) Session.get("username")).execute()
							.entrySet()) {
						DatabaseSQLiteQueryEngine.addUserPosition(entry
								.getKey(), entry.getValue());
					}
				}

				// Add new users
				Collection<UserView> cl = new ListAllUsersServices().execute();
				DatabaseSQLiteQueryEngine.updateUsers(cl);

				// Send all completed tasks
				Collection<TaskView> clTv = DatabaseSQLiteQueryEngine
						.selectAllCompletedTasks();
				Boolean res = new MarkConcludedTasksService(clTv).execute();
				if (res == null) {
					return;
				}
				DatabaseSQLiteQueryEngine.cleanCompletedTasks();

				// Send new tasks
				Collection<TaskView> clTvNew = DatabaseSQLiteQueryEngine
						.selectAllPseudoTasks();
				res = new CreateTasksService(clTvNew).execute();
				if (res == null) {
					return;
				}
				DatabaseSQLiteQueryEngine.cleanPseudoEntities();
				// Get new tasks
				if (Session.contains("username")) {
					int ntasks = DatabaseSQLiteQueryEngine.countTasks();
					int maxtasks = (Integer) Session.get("number.tasks");
					if (maxtasks > ntasks) {
						Collection<TaskView> clTvNewTasks = new SelectAllUncompletedTasksService(
								(String) Session.get("username"), maxtasks
										- ntasks).execute();
						if (clTvNewTasks == null) {
							return;
						}
						DatabaseSQLiteQueryEngine.updateTasks(clTvNewTasks);
						TaskAlertNotification.getInstance().setupTaskAlerts(
								getApplicationContext());
					}
				}
				System.out.println(SynchronizationService.class + " - done!");
			}
		}, 0, (Integer) Session.get("synchronization.rate"));
	}
}
