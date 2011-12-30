SET FOREIGN_KEY_CHECKS = 1;

DROP DATABASE IF EXISTS smartask;
CREATE DATABASE smartask;

USE smartask;

DROP TABLE IF EXISTS `User`;
CREATE TABLE `User`(
	name VARCHAR(50),
	email VARCHAR(100),
	credits REAL NOT NULL,
	telephone VARCHAR(15) UNIQUE NOT NULL,
	PRIMARY KEY(name)
) ENGINE=INNODB;

DROP TABLE IF EXISTS `Task`;
CREATE TABLE `Task`(
	id INT AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	description VARCHAR(255),
	priority INT NOT NULL,
	done BOOLEAN NOT NULL,
	credits REAL NOT NULL,
	numberOfUsersNeeded INT NOT NULL,
	PRIMARY KEY(id)
) ENGINE=INNODB;

DROP TABLE IF EXISTS `Task_Local`;
CREATE TABLE `Task_Local`(
	latitude BIGINT,
    longitude BIGINT,
	task_id INT,
	PRIMARY KEY(latitude,longitude,task_id),
	FOREIGN KEY (task_id) REFERENCES `Task`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

DROP TABLE IF EXISTS `User_Task`;
CREATE TABLE `User_Task`(
	username VARCHAR(50),
	task_id INT,
	completionDate TIMESTAMP,
	completed BOOLEAN DEFAULT FALSE,
	PRIMARY KEY(username,task_id),
	FOREIGN KEY (username) REFERENCES `User`(name) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (task_id) REFERENCES `Task`(id) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

DROP TABLE IF EXISTS `User_Local`;
CREATE TABLE `User_Local`(
	username VARCHAR(50),
	latitude BIGINT,
    longitude BIGINT,
    PRIMARY KEY(username),
	FOREIGN KEY (username) REFERENCES `User`(name) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

DROP TABLE IF EXISTS `Conflicts`;
CREATE TABLE `Conflicts`(
	number INT AUTO_INCREMENT PRIMARY KEY,
	usernameToRemoveCredits VARCHAR(50) NOT NULL,
	usernameToAddCredits VARCHAR(50) NOT NULL,
	creditsBeforeRemove REAL NOT NULL,
	creditsAfterRemove REAL NOT NULL,
	creditsBeforeAddition REAL NOT NULL,
	creditsAfterAddition REAL NOT NULL,
	firstCompletionOn TIMESTAMP NOT NULL,
	secondCompletionOn TIMESTAMP NOT NULL
) ENGINE=INNODB;

DROP TABLE IF EXISTS `Allowed_Notifications`;
CREATE TABLE `Allowed_Notifications`(
	usernameFrom VARCHAR(50),
	usernameTo VARCHAR(50),
	approved BOOLEAN NOT NULL,
	PRIMARY KEY(usernameFrom,usernameTo),
	FOREIGN KEY (usernameFrom) REFERENCES `User`(name) ON DELETE CASCADE ON UPDATE CASCADE,
	FOREIGN KEY (usernameTo) REFERENCES `User`(name) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=INNODB;

DROP PROCEDURE IF EXISTS assign_tasks;
DELIMITER |
CREATE PROCEDURE assign_tasks(task_id INT)
BEGIN
	DECLARE i INT;
	DECLARE lastCreditsValue INT;
	DECLARE numberOfUsersNeeded INT;
	DECLARE username VARCHAR(50);
	DECLARE creds INT;
	DECLARE no_more_rows BOOLEAN;  
	DECLARE cur CURSOR FOR SELECT name,credits 
						   FROM `User` 
						   ORDER BY credits;
	
    DECLARE CONTINUE HANDLER FOR NOT FOUND
   								 SET no_more_rows = TRUE;
    
	SET i = 0;
	SET no_more_rows = FALSE;
	SET lastCreditsValue = 0;
	
	SELECT `Task`.numberOfUsersNeeded INTO numberOfUsersNeeded
	FROM `Task`
	WHERE id = task_id;
	
	OPEN cur;
	
	label1: LOOP		
		FETCH cur INTO username,creds;
		IF no_more_rows THEN
			LEAVE label1;
		END IF;
		IF lastCreditsValue = creds THEN
			INSERT INTO `User_Task`(task_id,username) VALUES(task_id,username);
			SET i = i + 1;
			ITERATE label1;
		END IF;
		IF i >= numberOfUsersNeeded THEN
			LEAVE label1;
		END IF;
		SET lastCreditsValue = creds;
	    INSERT INTO `User_Task`(task_id,username) VALUES(task_id,username);
		SET i = i + 1;
	END LOOP label1;
	CLOSE cur;
END|

DROP PROCEDURE IF EXISTS complete_task|
CREATE PROCEDURE complete_task(task_id INT, userN VARCHAR(50), compDate TIMESTAMP)
BEGIN
	DECLARE itIsDone INT;
	DECLARE endDate TIMESTAMP;
	DECLARE updateUserName VARCHAR(50);
	DECLARE numberOfCredits REAL;
	DECLARE creditsBeforeRemoval REAL;
	DECLARE creditsBeforeAddition REAL;
	DECLARE no_more_rows BOOLEAN;
	DECLARE cur CURSOR FOR SELECT username
						   FROM `User_Task` 
						   WHERE `User_Task`.task_id = task_id AND `User_Task`.completed;
	
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET no_more_rows = TRUE;
    		
	SELECT credits / numberOfUsersNeeded INTO numberOfCredits
	FROM `Task` 
	WHERE `Task`.id = task_id;
			
	IF (SELECT done 
		FROM `Task` 
		WHERE `Task`.id = task_id)
	THEN
		SELECT completionDate, username INTO endDate,updateUserName
		FROM `User_Task`		
		WHERE `User_Task`.task_id = task_id AND `User_Task`.completed
		ORDER BY `User_Task`.completionDate DESC
		LIMIT 1;
				
		IF compDate < endDate THEN
			SELECT credits INTO creditsBeforeRemoval
			FROM `User`
			WHERE name = updateUserName;
			SELECT credits INTO creditsBeforeAddition
			FROM `User`
			WHERE name = userN;
			INSERT INTO `Conflicts`(usernameToRemoveCredits,usernameToAddCredits,creditsBeforeRemove,creditsAfterRemove,
									creditsBeforeAddition,creditsAfterAddition,firstCompletionOn,secondCompletionOn)
			VALUES(updateUserName,userN,creditsBeforeRemoval,creditsBeforeRemoval - numberOfCredits,creditsBeforeAddition,
				   creditsBeforeAddition + numberOfCredits,endDate,compDate);
			UPDATE `User`
			SET credits = credits - numberOfCredits
			WHERE name = updateUserName;
			UPDATE `User`
			SET credits = credits + numberOfCredits
			WHERE name = userN;
			UPDATE `User_Task`
			SET `User_Task`.completed = FALSE
			WHERE `User_Task`.username = updateUserName AND `User_Task`.task_id = task_id;
			UPDATE `User_Task`
			SET `User_Task`.completed = TRUE,
				`User_Task`.completionDate = compDate
			WHERE `User_Task`.username = userN AND `User_Task`.task_id = task_id;
		END IF;
	ELSE
		UPDATE `User_Task`
		SET `User_Task`.completed = TRUE,
			`User_Task`.completionDate = compDate
		WHERE `User_Task`.username = userN AND `User_Task`.task_id = task_id; 
		SELECT COUNT(*) INTO itIsDone
		FROM `User_Task`
		WHERE `User_Task`.task_id = task_id AND `User_Task`.completed;
		IF itIsDone = (SELECT numberOfUsersNeeded 
						FROM `Task` 
						WHERE `Task`.id = task_id)
		THEN
			UPDATE `Task`
			SET done = TRUE
			WHERE `Task`.id = task_id;
	
			OPEN cur;
			FETCH cur INTO updateUserName;
			REPEAT
				UPDATE `User`
				SET credits = credits + numberOfCredits
				WHERE `User`.name = updateUserName;
				FETCH cur INTO updateUserName;
		    UNTIL no_more_rows END REPEAT;
		    CLOSE cur;		    
		END IF;
	END IF;
END|
DELIMITER ;



insert into `User` values('foo','foo@gmail.com',0,'5554');
insert into `User` values('bar','bar@gmail.com',0,'5556');