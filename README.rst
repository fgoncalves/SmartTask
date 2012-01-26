Small description
=================

Imagine a student residence with several... well, students. These students need to keep themselves organized in order to do their every day tasks. Task such as clean the house or go to the grocery store.

SmartTask is an Android application which provides students with a common environment and enables them to manage such tasks.

A student creates a task, which can be for multiple users and assigns it one or more locations in a map. The system will take care of assigning those tasks to the correct students (Tasks are assigned to the students with fewer points, with no tasks currently assigned. Students are rewarded with points upon completing the assigned tasks). When a User approaches a location with an assigned task to him, an alert is displayed in the mobile device to notify such user.

SmartTask will automatically synchronize tasks between devices when users are near eachother. Also, there is a central server in the student residence to where data is automatically synchronized.

Content
========

1. System State
2. Needed Libraries
3. Database Creation
4. Database Configuration
5. Android Emulator Port Redirection

1. System State
===============

In order for our application to work, each mobile client need to be correctly connected to certain ports. What this means is that a mobile client with name "foo" and number 5554, must be connected to port 5554.

The emulator which initializes the monitoring application must be connected to port 5558 and the one which initializes the SMS gateway must be connected to port 5560.

2. Needed Libraries
===================

For the mobile client and the SMS gateway to work three jar files are needed::

	- MySQL JDBC driver.
	- CMDatabaseSystem project Jar.
	- SMSSerializer project Jar.

For the monitoring application only these jar files are needed::

	- MySQL JDBC driver.
	- CMDatabaseSystem project Jar.

3. Database Creation
====================

The script "cm.sql" must be executed to create the necessary tables.

For test purposes, this script also creates two users - "foo" and "bar", with phone numbers 5554 and 5556, respectively.ente.

4. Database Configuration
=========================

Before initializing any emulator, access to the remote database must be configured.

We opted to make these configurations in the file res/values/strings.xml of each application. As an example::

	<string name="mysql_database_url">jdbc:mysql://10.0.2.2/smartask</string>
	<string name="mysql_database_user">foo</string>
	<string name="mysql_database_password">pass</string>
	<string name="mysql_database_services_log_file"></string>

User 'foo' must have access to the database 'smartask'.

Since no log file was specified, no one will be created.

5. Android Emulator Port Redirection
====================================

For the IM protocol and the mobile user synchronization to work it's necessary to create four redirections in each emulator.

Suppose an emulator is connected to port 5554, then the redirections should be as follows::

	redir add tcp:6664:6664   ------> IM server
	redir add tcp:6665:6665   ------> IM client
	redir add tcp:7774:7774   ------> Synchronization server
	redir add tcp:7775:7775   ------> Synchronization client

This means that for any emulator connected to port X, the redirections should be done as follows::

	IM server:
	 	Redirect emulator port X + 1110 to host machine port X + 1110
	IM client:
		Redirect emulator port X + 1111 to host machine port X + 1111
	Synchronization server:
		Redirect emulator port X + 2220 to host machine port X + 2220
	Synchronization client:
		Redirect emulator port X + 2221 to host machine port X + 2221