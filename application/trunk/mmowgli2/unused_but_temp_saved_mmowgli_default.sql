# ************************************************************
# Sequel Pro SQL dump
# Version 4096
#
# http://www.sequelpro.com/
# http://code.google.com/p/sequel-pro/
#
# Host: web4 (MySQL 5.5.35-log)
# Database: chds
# Generation Time: 2014-03-04 00:00:43 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table ActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan`;

CREATE TABLE `ActionPlan` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `idForSorting` bigint(20) NOT NULL,
  `averageThumb` double NOT NULL,
  `chatLog_id` bigint(20) DEFAULT NULL,
  `chainRoot_id` bigint(20) DEFAULT NULL,
  `currentAuthorInnovationPoints` float NOT NULL,
  `currentInnoBrokerInnovationPoints` float NOT NULL,
  `discussion` longtext,
  `headline` varchar(255) DEFAULT NULL,
  `helpWanted` longtext,
  `howWillItChangeText` longtext,
  `howWillItWorkText` longtext,
  `imagesInstructions` longtext,
  `lockedBy_id` bigint(20) DEFAULT NULL,
  `mapInstructions` longtext,
  `map_id` bigint(20) DEFAULT NULL,
  `planInstructions` longtext,
  `powerPlay` bit(1) NOT NULL,
  `priceToInvest` double NOT NULL,
  `subTitle` longtext,
  `sumThumbs` double NOT NULL,
  `talkItOverInstructions` longtext,
  `title` varchar(255) DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  `videosInstructions` longtext,
  `whatIsItText` longtext,
  `whatWillItTakeText` longtext,
  `hidden` bit(1) NOT NULL DEFAULT b'0',
  `quickAuthorList` varchar(255) DEFAULT '',
  `creationDate` datetime DEFAULT NULL,
  `createdInMove_id` bigint(20) DEFAULT NULL,
  `superInteresting` bit(1) NOT NULL DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FKE5314E9FE2111CC4` (`map_id`),
  KEY `FKE5314E9F6E826C5D` (`chatLog_id`),
  KEY `FKE5314E9F9C8D35A1` (`lockedBy_id`),
  KEY `FKE5314E9F5B0A1B64` (`chainRoot_id`),
  KEY `FKE5314E9FAF29DE0A` (`createdInMove_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `ActionPlan` DISABLE KEYS */;

INSERT INTO `ActionPlan` (`id`, `idForSorting`, `averageThumb`, `chatLog_id`, `chainRoot_id`, `currentAuthorInnovationPoints`, `currentInnoBrokerInnovationPoints`, `discussion`, `headline`, `helpWanted`, `howWillItChangeText`, `howWillItWorkText`, `imagesInstructions`, `lockedBy_id`, `mapInstructions`, `map_id`, `planInstructions`, `powerPlay`, `priceToInvest`, `subTitle`, `sumThumbs`, `talkItOverInstructions`, `title`, `version`, `videosInstructions`, `whatIsItText`, `whatWillItTakeText`, `hidden`, `quickAuthorList`, `creationDate`, `createdInMove_id`, `superInteresting`)
VALUES
	(1,1,0,1,1,0,0,NULL,NULL,NULL,'Assume success -- then what happens? Play the game, change the game!\n\nHow do we change the playing field for this problem? Will these solutions make a difference, for better or for worse? What new opportunities and what new risks are presented?','A plan for action describes how to make things work.\n\nHow can we execute to achieve these goals? Which individuals and groups need to work together? Are any prerequisite steps needed? Are there any additional steps needed after the plan works? Is there a \"Plan B\" if something goes wrong?','Game.defaultActionPlanImagesText: Photographs, pictures, and charts bring your action plan to life and convey important details that might not fit easily into the text of your plan. \n<p>\nYou can search online for images, or upload images from your own desktop. Be sure to add a caption that explains the significance of each image.\n</p>\n<i>Hint: You may want to give one of your team members the responsibility for tracking down the images that support your plan.</i>\n',NULL,'You can annotate a map to show how your plan would work. Here are some ways to use the map:\n<ul>\n<li>Annotate it with numbered steps to show how your plan will unfold.</li>\n\n<li>Mark the locations of resources your plan will draw on--or put in place.</li>\n\n<li>Add data points that explain the what\'s happening on the ground.</li>\n</ul>\nMaps are powerful tools for planning. Use yours in the way that best supports your plan!\n</p>\n<p>\nAs an author, you may make changes to this map by re-centering, zooming and adding markers.\nSave your changes using one or both of the save buttons above the map before leaving this page. (Non authors do not see the buttons.)\n</p>\n<b>New markers (authors only)</b>:  drag one of the following icons onto the map.  To delete a marker, click it on the map and follow directions in the popup window.\n</p>\n',1,'Describe your action plan here. Talk it over with your fellow authors in real-time or asynchronous chat. Add images, videos, or map annotations. Remember this is a team effort! So work with your teammates to come up with the best possible plan.\n<p>\nNeed more information? Check our <a href=\"https://portal.mmowgli.nps.edu/instructions/-/asset_publisher/e02P/content/how-to-build-action-plans\" target=\"_portal\">help</a> page.\n</p>\n<p>\n<b>The 5 Basic Steps:</b>\n<ol>\n<li>Start by entering a headline that captures the big idea.</li>\n\n<li>Describe the basic plan in the What Is It? box.</li>\n\n<li>Make a list of the resources you need in the What Will It Take? box.</li>\n\n<li>Outline the steps to succeed in the How Will It Work box. <i>Hint: Use your card chain as a starting place</i>.</li>\n\n<li>Sum up the impact in the last box, How Will Change the Situation?</li>\n</ol>\nClick Save Changes often to make sure your text is saved. Click History to review previous versions.\n</p><p>\nWork fast. Work smart. Work together.\n<b><i>Good luck!</i></b></p>\n',00000000,200,'First declare who your target community might be, and what their relevant motivations are.\r\n\r\nWho is affected by this plan? Why should someone care about it? How does this plan improve their situation?',0,'Game.defaultActionPlanTalkText: Coming up with an action plan -- with people you may not know across multiple time zones -- can be a challenge. But you can use this private* chat room to trade ideas in real time or leave messages for your teammates.\n<p>\nYou might want to start by discussing the basic ideas in your card chain. How are you going to make those ideas work?  What\'s the core idea?  And what are the actions you need to take? \n</p><p>\nTalk it over here. But don\'t be shy about just starting to fill in the plan. Switch back and forth between The Plan and this team discussion space as you build your winning action plan.\n</p>\n*<i>Your chats cannot be seen by anyone else in the game other than gamemasters. However, they will be available to analysts for post-game analysis</i>.','PLAYER FAMILIARIZATION.  Action plans describe how to solve game challenges and achieve our motivating goals. This action plan provides example guidance for new players.',118,'A video is worth a thousand words.  Consider:<OL><LI> Making a 1-2 minute video to tell us about your action plan.</LI><LI>Share a video you\'ve found that helps support your action plan.</LI></OL>Add a caption to highlight the point you\'re making.','State your challenge, proposed solution, and goal outcomes next. \n\nWhat problem is being addressed? How will you solve it? What does success look like?','Describe what decisions, capabilities and resources are needed to accomplish this plan.\n\nWhat is needed to succeed? Is it possible to build a system that works? Can we combine and re-use existing resources? Do we need new assets?',00000000,'gm_donb','2013-10-01 16:00:00',1,00000000),
	(2,2,0,2,2,0,0,NULL,NULL,NULL,'Game masters can help people to be more engaged by connecting relevant card chains and similar ideas. This approach supports the creation of action plans to bring larger groups together, and also helps reduce the risk of conversation on critical ideas getting split and lost in the avalanche of cards. Constructive moderation also helps flesh out ideas so that, after the game ends and further analysis occurs, so that ideas deemed to have the best potential can be acted upon and taken to the next step toward implementation. ','Game masters can use the tools at their disposal to communicate with the players, mark cards, invite players to participate in action plans, expand on ideas in the card chains, add glossary items, and generally add value. Players are able to ask questions of trusted individuals. Game masters do not appear on the Leader Board, but they are allowed to play with separate accounts at the same time. Playing in two separate browsers makes it easy to \"keep your hats straight\" and participate via both roles. ','Game.defaultActionPlanImagesText: Photographs, pictures, and charts bring your action plan to life and convey important details that might not fit easily into the text of your plan. \n<p>\nYou can search online for images, or upload images from your own desktop. Be sure to add a caption that explains the significance of each image.\n</p>\n<i>Hint: You may want to give one of your team members the responsibility for tracking down the images that support your plan.</i>\n',NULL,'You can annotate a map to show how your plan would work. Here are some ways to use the map:\n<ul>\n<li>Annotate it with numbered steps to show how your plan will unfold.</li>\n\n<li>Mark the locations of resources your plan will draw on--or put in place.</li>\n\n<li>Add data points that explain the what\'s happening on the ground.</li>\n</ul>\nMaps are powerful tools for planning. Use yours in the way that best supports your plan!\n</p>\n<p>\nAs an author, you may make changes to this map by re-centering, zooming and adding markers.\nSave your changes using one or both of the save buttons above the map before leaving this page. (Non authors do not see the buttons.)\n</p>\n<b>New markers (authors only)</b>:  drag one of the following icons onto the map.  To delete a marker, click it on the map and follow directions in the popup window.\n</p>\n',2,'Describe your action plan here. Talk it over with your fellow authors in real-time or asynchronous chat. Add images, videos, or map annotations. Remember this is a team effort! So work with your teammates to come up with the best possible plan.\n<p>\nNeed more information? Check our <a href=\"https://portal.mmowgli.nps.edu/instructions/-/asset_publisher/e02P/content/how-to-build-action-plans\" target=\"_portal\">help</a> page.\n</p>\n<p>\n<b>The 5 Basic Steps:</b>\n<ol>\n<li>Start by entering a headline that captures the big idea.</li>\n\n<li>Describe the basic plan in the What Is It? box.</li>\n\n<li>Make a list of the resources you need in the What Will It Take? box.</li>\n\n<li>Outline the steps to succeed in the How Will It Work box. <i>Hint: Use your card chain as a starting place</i>.</li>\n\n<li>Sum up the impact in the last box, How Will Change the Situation?</li>\n</ol>\nClick Save Changes often to make sure your text is saved. Click History to review previous versions.\n</p><p>\nWork fast. Work smart. Work together.\n<b><i>Good luck!</i></b></p>\n',00000000,200,'This plan involves all game masters. It also provides helpful information for players, letting them know how it all WORKS.',0,'Game.defaultActionPlanTalkText: Coming up with an action plan -- with people you may not know across multiple time zones -- can be a challenge. But you can use this private* chat room to trade ideas in real time or leave messages for your teammates.\n<p>\nYou might want to start by discussing the basic ideas in your card chain. How are you going to make those ideas work?  What\'s the core idea?  And what are the actions you need to take? \n</p><p>\nTalk it over here. But don\'t be shy about just starting to fill in the plan. Switch back and forth between The Plan and this team discussion space as you build your winning action plan.\n</p>\n*<i>Your chats cannot be seen by anyone else in the game other than gamemasters. However, they will be available to analysts for post-game analysis</i>.','PLAYER FAMILIARIZATION.  How can game masters best moderate to help players engage, learn and contribute in the game?',59,'A video is worth a thousand words.  Consider:<OL><LI> Making a 1-2 minute video to tell us about your action plan.</LI><LI>Share a video you\'ve found that helps support your action plan.</LI></OL>Add a caption to highlight the point you\'re making.','The Game Master Guidance document is now available on the (controlled access) Game Masters Portal. It involves game masters coaching, encouraging, and supporting player efforts. Hopefully the game quickly becomes clear enough so that players can simply play without lots of explanation. Available to game masters at https://portal.mmowgli.nps.edu/web/portal/gamemaster-guidance','It takes collaboration from multiple disciplines and multiple players. It takes game masters reading cards, marking very interesting ideas, and inviting players to do action plans. It involves asking question about the details of the action plans. A \"light touch\" let\'s player voices be heard without filtering or topspin. Game masters can also play as players to be a catalyst for discussion/thought without the potential to intimidate other players by using the gm_name title with its implied authority.',00000000,'gm_becca,gm_donb','2013-10-01 17:00:00',1,00000000);

/*!40000 ALTER TABLE `ActionPlan` ENABLE KEYS */;


# Dump of table ActionPlan_AuthorMessages
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_AuthorMessages`;

CREATE TABLE `ActionPlan_AuthorMessages` (
  `actionplan_id` bigint(20) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`message_id`),
  UNIQUE KEY `message_id` (`message_id`),
  KEY `FK588C5B74F28CA9D` (`message_id`),
  KEY `FK588C5B7CA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Authors
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Authors`;

CREATE TABLE `ActionPlan_Authors` (
  `actionPlan_id` bigint(20) NOT NULL,
  `author_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionPlan_id`,`author_user_id`),
  KEY `FKDDC38E8895FC9BC3` (`author_user_id`),
  KEY `FKDDC38E88CA276057` (`actionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `ActionPlan_Authors` DISABLE KEYS */;

INSERT INTO `ActionPlan_Authors` (`actionPlan_id`, `author_user_id`)
VALUES
	(1,5),
	(2,5),
	(2,8);

/*!40000 ALTER TABLE `ActionPlan_Authors` ENABLE KEYS */;


# Dump of table ActionPlan_Award
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Award`;

CREATE TABLE `ActionPlan_Award` (
  `ActionPlan_id` bigint(20) NOT NULL,
  `awards_id` bigint(20) NOT NULL,
  PRIMARY KEY (`ActionPlan_id`,`awards_id`),
  UNIQUE KEY `awards_id` (`awards_id`),
  KEY `FK2365889DAC2415A4` (`awards_id`),
  KEY `FK2365889DCA276057` (`ActionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Comments
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Comments`;

CREATE TABLE `ActionPlan_Comments` (
  `actionplan_id` bigint(20) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`message_id`),
  UNIQUE KEY `message_id` (`message_id`),
  KEY `FK615B27B44F28CA9D` (`message_id`),
  KEY `FK615B27B4CA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Declinees
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Declinees`;

CREATE TABLE `ActionPlan_Declinees` (
  `actionPlan_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionPlan_id`,`user_id`),
  KEY `FK3D07786486C88E77` (`user_id`),
  KEY `FK3D077864CA276057` (`actionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_HowChange_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_HowChange_History`;

CREATE TABLE `ActionPlan_HowChange_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKD22C995CA276057` (`actionplan_id`),
  KEY `FKD22C995C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_HowWork_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_HowWork_History`;

CREATE TABLE `ActionPlan_HowWork_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK1C1F77F6CA276057` (`actionplan_id`),
  KEY `FK1C1F77F6C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_InnovationBrokers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_InnovationBrokers`;

CREATE TABLE `ActionPlan_InnovationBrokers` (
  `actionPlan_id` bigint(20) NOT NULL,
  `broker_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionPlan_id`,`broker_user_id`),
  KEY `FK4953B8D5B9A8C411` (`broker_user_id`),
  KEY `FK4953B8D5CA276057` (`actionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Invitees
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Invitees`;

CREATE TABLE `ActionPlan_Invitees` (
  `actionPlan_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionPlan_id`,`user_id`),
  KEY `FKAAE5E73786C88E77` (`user_id`),
  KEY `FKAAE5E737CA276057` (`actionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Media
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Media`;

CREATE TABLE `ActionPlan_Media` (
  `ActionPlan_id` bigint(20) NOT NULL,
  `media_id` bigint(20) NOT NULL,
  KEY `FK24067E242BFE963D` (`media_id`),
  KEY `FK24067E24CA276057` (`ActionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `ActionPlan_Media` DISABLE KEYS */;

INSERT INTO `ActionPlan_Media` (`ActionPlan_id`, `media_id`)
VALUES
	(1,210),
	(1,212),
	(2,411),
	(2,412);

/*!40000 ALTER TABLE `ActionPlan_Media` ENABLE KEYS */;


# Dump of table ActionPlan_PlanFields
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_PlanFields`;

CREATE TABLE `ActionPlan_PlanFields` (
  `ActionPlan_id` bigint(20) NOT NULL,
  `planFields` varchar(255) DEFAULT NULL,
  KEY `FK43420D02CA276057` (`ActionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_SubTitles_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_SubTitles_History`;

CREATE TABLE `ActionPlan_SubTitles_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKF8A949F0CA276057` (`actionplan_id`),
  KEY `FKF8A949F0C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_ThumbsByUser
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_ThumbsByUser`;

CREATE TABLE `ActionPlan_ThumbsByUser` (
  `ActionPlan_id` bigint(20) NOT NULL,
  `userThumbs` int(11) DEFAULT NULL,
  `userThumbs_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`ActionPlan_id`,`userThumbs_KEY`),
  KEY `FKFA350E3FE075EAF0` (`userThumbs_KEY`),
  KEY `FKFA350E3FCA276057` (`ActionPlan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_Titles_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_Titles_History`;

CREATE TABLE `ActionPlan_Titles_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK368BFB10CA276057` (`actionplan_id`),
  KEY `FK368BFB10C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_WhatIs_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_WhatIs_History`;

CREATE TABLE `ActionPlan_WhatIs_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK4A904203CA276057` (`actionplan_id`),
  KEY `FK4A904203C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table ActionPlan_WhatTake_History
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ActionPlan_WhatTake_History`;

CREATE TABLE `ActionPlan_WhatTake_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKFF436360CA276057` (`actionplan_id`),
  KEY `FKFF436360C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Affiliation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Affiliation`;

CREATE TABLE `Affiliation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `affiliation` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Affiliation` DISABLE KEYS */;

INSERT INTO `Affiliation` (`id`, `affiliation`)
VALUES
	(1,'optional'),
	(2,'U.S. Navy'),
	(3,'U.S. Air Force'),
	(4,'U.S. Army'),
	(5,'U.S. Coast Guard'),
	(6,'U.S. Marines'),
	(9,'U.S. Government'),
	(10,'Merchant Marine'),
	(16,'International Military'),
	(18,'International Government'),
	(21,'Academia'),
	(22,'Student'),
	(24,'Non-profit/Non-Governmental Organization'),
	(26,'Industry'),
	(28,'Public'),
	(30,'Media');

/*!40000 ALTER TABLE `Affiliation` ENABLE KEYS */;


# Dump of table Avatar
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Avatar`;

CREATE TABLE `Avatar` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `media_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK7597AD792BFE963D` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Avatar` DISABLE KEYS */;

INSERT INTO `Avatar` (`id`, `description`, `media_id`)
VALUES
	(1,'Bulb',60),
	(2,'Buttons',61),
	(3,'Fire',62),
	(4,'Fox',63),
	(5,'Gears',64),
	(6,'Juggle',65),
	(7,'Leaf',66),
	(8,'Music',67),
	(9,'Paint',68),
	(10,'Raven',69),
	(11,'Red',70),
	(12,'Rocks',71),
	(13,'Skyscraper',72),
	(14,'Tea',73),
	(15,'Trex',74),
	(16,'Trojan',75),
	(17,'Zags',76);

/*!40000 ALTER TABLE `Avatar` ENABLE KEYS */;


# Dump of table Award
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Award`;

CREATE TABLE `Award` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timeAwarded` datetime DEFAULT NULL,
  `awardType_id` bigint(20) DEFAULT NULL,
  `awardedBy_id` bigint(20) DEFAULT NULL,
  `awardedTo_id` bigint(20) DEFAULT NULL,
  `move_id` bigint(20) DEFAULT NULL,
  `storyUrl` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `FK3CB8A3DD9A948B7` (`move_id`),
  KEY `FK3CB8A3D6986A42F` (`awardedBy_id`),
  KEY `FK3CB8A3D2DB31FFD` (`awardType_id`),
  KEY `FK3CB8A3D6A7FBF8B` (`awardedTo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table AwardType
# ------------------------------------------------------------

DROP TABLE IF EXISTS `AwardType`;

CREATE TABLE `AwardType` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `basicValue` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `powerValue` int(11) NOT NULL,
  `icon300x300_id` bigint(20) DEFAULT NULL,
  `icon55x55_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKC27A271744F82A2` (`icon55x55_id`),
  KEY `FKC27A2717A9FB652C` (`icon300x300_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `AwardType` DISABLE KEYS */;

INSERT INTO `AwardType` (`id`, `basicValue`, `description`, `name`, `powerValue`, `icon300x300_id`, `icon55x55_id`)
VALUES
	(1,0,'MMOWGLI Achievement Medal (MAM)','MMOWGLI Achievement Medal (MAM)',0,134,131),
	(2,0,'MMOWGLI Commendation Medal (MCM)','MMOWGLI Commendation Medal (MCM)',0,135,132),
	(3,0,'The Legion de MMOWGLI','The Legion de MMOWGLI',0,136,133),
	(4,0,'First-place winning player overall','First-Place Player',0,136,133),
	(5,0,'Second-place winning player overall','Second-Place Player',0,136,133),
	(6,0,'Third-place winning player overall','Third-Place Player',0,136,133),
	(7,0,'Co-author of best Action Plan overall','Best Action',0,136,133),
	(8,0,'Certificate of Appreciation, Best Game Play','Certificate of Appreciation',0,138,137),
	(9,0,'Certificate of Commendation, Best Game Play','Certificate of Commendation',0,138,137),
	(10,0,'Certificate of Appreciation, Best Action Plan','Certificate of Appreciation',0,138,137),
	(11,0,'Certificate of Commendation, Best Action Plan','Certificate of Commendation',0,138,137);

/*!40000 ALTER TABLE `AwardType` ENABLE KEYS */;


# Dump of table Badge
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Badge`;

CREATE TABLE `Badge` (
  `badge_pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `badgeName` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `media_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`badge_pk`),
  KEY `FK3CFAB832BFE963D` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Badge` DISABLE KEYS */;

INSERT INTO `Badge` (`badge_pk`, `badgeName`, `description`, `media_id`)
VALUES
	(1,'Root cards','Played at least one of each root card type (Innovate & Defend)',147),
	(2,'All cards','Played at least one of every type of card (all 6)',148),
	(3,'Super-active root','Played the root card of a super-active chain',149),
	(4,'Super-interesting card','Played a card marked super-interesting by a game master',150),
	(5,'Favorite','Played a card marked as a favorite by another player',151),
	(6,'Action plan author','Accepted at least one Action Plan authorship invitation',152),
	(7,'Top 50','Ranked in top 50 of Leaderboard at any point during gameplay',153),
	(8,'Active player','Logged in each day of gameplay (each day of each session, ex: Tues, Wed, and Thurs of one week)',154);

/*!40000 ALTER TABLE `Badge` ENABLE KEYS */;


# Dump of table Card
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Card`;

CREATE TABLE `Card` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `creationDate` datetime DEFAULT NULL,
  `factCard` bit(1) NOT NULL,
  `hidden` bit(1) NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `author_id` bigint(20) DEFAULT NULL,
  `cardType_id` bigint(20) DEFAULT NULL,
  `createdInMove_id` bigint(20) DEFAULT NULL,
  `parentCard_id` bigint(20) DEFAULT NULL,
  `authorName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1FEF306A6D506D` (`parentCard_id`),
  KEY `FK1FEF3070DE67B7` (`cardType_id`),
  KEY `FK1FEF30E78180B7` (`author_id`),
  KEY `FK1FEF30AF29DE0A` (`createdInMove_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Card` DISABLE KEYS */;

INSERT INTO `Card` (`id`, `creationDate`, `factCard`, `hidden`, `text`, `author_id`, `cardType_id`, `createdInMove_id`, `parentCard_id`, `authorName`)
VALUES
	(1,'2013-10-01 01:01:01',00000000,00000000,'Card 1 text',2,1,1,NULL,'SeedCard'),
	(2,'2013-10-01 01:01:02',00000000,00000000,'Card 2 text',2,1,1,NULL,'SeedCard'),
	(3,'2013-10-01 01:01:03',00000000,00000000,'Card 3 text',2,1,1,NULL,'SeedCard'),
	(4,'2013-10-01 01:01:04',00000000,00000000,'Card 4 text',2,1,1,NULL,'SeedCard'),
	(5,'2013-10-01 01:01:05',00000000,00000000,'Card 5 text',2,2,1,NULL,'SeedCard'),
	(6,'2013-10-01 01:01:06',00000000,00000000,'Card 6 text',2,2,1,NULL,'SeedCard'),
	(7,'2013-10-01 01:01:07',00000000,00000000,'Card 7 text',2,2,1,NULL,'SeedCard'),
	(8,'2013-10-01 01:01:08',00000000,00000000,'Card 8 text',2,2,1,NULL,'SeedCard');

/*!40000 ALTER TABLE `Card` ENABLE KEYS */;


# Dump of table Card_CardMarking
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Card_CardMarking`;

CREATE TABLE `Card_CardMarking` (
  `Card_id` bigint(20) NOT NULL,
  `marking_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Card_id`,`marking_id`),
  KEY `FK678B3A1649A10D2D` (`marking_id`),
  KEY `FK678B3A16B0898C57` (`Card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Card_FollowOnCards
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Card_FollowOnCards`;

CREATE TABLE `Card_FollowOnCards` (
  `card_id` bigint(20) NOT NULL,
  `follow_on_card_id` bigint(20) NOT NULL,
  PRIMARY KEY (`card_id`,`follow_on_card_id`),
  UNIQUE KEY `follow_on_card_id` (`follow_on_card_id`),
  KEY `FK73CC5CE441AA8345` (`follow_on_card_id`),
  KEY `FK73CC5CE4B0898C57` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table CardMarking
# ------------------------------------------------------------

DROP TABLE IF EXISTS `CardMarking`;

CREATE TABLE `CardMarking` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `label` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `CardMarking` DISABLE KEYS */;

INSERT INTO `CardMarking` (`id`, `description`, `label`)
VALUES
	(1,'Use sparingly','Super-Interesting'),
	(2,'Doesn\'t lead anwhere useful','Scenario Fail'),
	(3,'Self-evident','Common Knowledge'),
	(4,'Bad behavior or sensitive information','Hidden');

/*!40000 ALTER TABLE `CardMarking` ENABLE KEYS */;


# Dump of table CardType
# ------------------------------------------------------------

DROP TABLE IF EXISTS `CardType`;

CREATE TABLE `CardType` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cardClass` int(11) NOT NULL,
  `ideaCard` bit(1) NOT NULL,
  `descendantOrdinal` int(11) DEFAULT NULL,
  `prompt` varchar(255) DEFAULT NULL,
  `summaryHeader` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `titleAlternate` varchar(255) DEFAULT NULL,
  `cssColorStyle` varchar(255) DEFAULT '',
  `cssLightColorStyle` varchar(255) DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `CardType` DISABLE KEYS */;

INSERT INTO `CardType` (`id`, `cardClass`, `ideaCard`, `descendantOrdinal`, `prompt`, `summaryHeader`, `title`, `titleAlternate`, `cssColorStyle`, `cssLightColorStyle`)
VALUES
	(1,0,00000001,NULL,'How can Navy acquisition fix problem areas that are found in past practices and current processes?','CHALLENGES','Challenges',NULL,'m-purple','m-purple-light'),
	(2,1,00000001,NULL,'How can Navy and industry together design, build, procure and sustain both payloads and platforms?','FUTURE GOALS','Future Goals',NULL,'m-green','m-green-light'),
	(3,2,00000000,1,'Build on this idea to amplify its impact','EXPAND','Expand',NULL,'m-orange','m-orange-light'),
	(4,2,00000000,2,'Challenge this idea','COUNTER','Counter',NULL,'m-red','m-red-light'),
	(5,2,00000000,3,'Take this idea in a different direction','ADAPT','Adapt',NULL,'m-blue','m-blue-light'),
	(6,2,00000000,4,'Something missing? Ask a question','EXPLORE','Explore',NULL,'m-lime','m-lime-light');

/*!40000 ALTER TABLE `CardType` ENABLE KEYS */;


# Dump of table ChatLog
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ChatLog`;

CREATE TABLE `ChatLog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `ChatLog` DISABLE KEYS */;

INSERT INTO `ChatLog` (`id`)
VALUES
	(1),
	(2);

/*!40000 ALTER TABLE `ChatLog` ENABLE KEYS */;


# Dump of table ChatLog_Messagess
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ChatLog_Messagess`;

CREATE TABLE `ChatLog_Messagess` (
  `chatlog_id` bigint(20) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`chatlog_id`,`message_id`),
  UNIQUE KEY `message_id` (`message_id`),
  KEY `FKFA28ACB46E826C5D` (`chatlog_id`),
  KEY `FKFA28ACB44F28CA9D` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Edits
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Edits`;

CREATE TABLE `Edits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime DEFAULT NULL,
  `value` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Email
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Email`;

CREATE TABLE `Email` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `isPrimary` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table EmailConfirmation
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EmailConfirmation`;

CREATE TABLE `EmailConfirmation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `confirmationCode` varchar(255) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9F2FE1D186C88E77` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table EmailPii
# ------------------------------------------------------------

DROP TABLE IF EXISTS `EmailPii`;

CREATE TABLE `EmailPii` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `isPrimary` bit(1) NOT NULL DEFAULT b'1',
  `digest` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `digestKey` (`digest`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `EmailPii` DISABLE KEYS */;

INSERT INTO `EmailPii` (`id`, `address`, `isPrimary`, `digest`)
VALUES
	(1,'SB4BwhPPl+Uhf/5G6iCyXQ8ovHKyg2T0jRShJdCKEQYxrzl/pfU+GyW3Ba1KimKA',00000001,'xMR/DIMHzYEjInz9xbQEVYSMwpU='),
	(2,'tmp8e2kBHIbKoZsLsIhJOIClQsz9Af4Oi73COGBcp4N03bxiDwyeWcAKSHOpYz8N',00000001,'xMR/DIMHzYEjInz9xbQEVYSMwpU='),
	(3,'vb5FjGzc2J06tbVrRZWLlynA1+qQlMnaCOmdi7x/JnYWeIlnR0u3Wkr9PatXVU0A',00000001,'xMR/DIMHzYEjInz9xbQEVYSMwpU='),
	(4,'uJLKvNJnxc0dMx07rV+YZwEFJpD3wBlUh2+9Jr1U6/8BIKe971WOvkK2WWm67Rgp',00000001,'xMR/DIMHzYEjInz9xbQEVYSMwpU='),
	(5,'8C0ovoIBHHtXYjEsLaS3C0IrygpI6g4TSlnyBwpsFPU=',00000001,'H5grjdqcXkx3bqR0Z5IlhuVpqkE='),
	(6,'9k3QNFZ2mL60oW4sRA9rWWH6lpzveGkv',00000001,'HoiMcR5T9/AXDmRCEq+Ii/gWd2w='),
	(7,'yJh24Ap/A3SmEE5ocyWE4Pewbz6eKGiV1lbvtDNiKks=',00000001,'VoPj7JVdfI6JEG0AOryAXRc8Nrw='),
	(8,'0CcOXi/rSt6fyUY/ujYPx9COR3W90Omj',00000001,'BMEzjQKYKUeJ+l1b0L6BKE7qU1M=');

/*!40000 ALTER TABLE `EmailPii` ENABLE KEYS */;


# Dump of table Expertise
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Expertise`;

CREATE TABLE `Expertise` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `expertise` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table FactPopup
# ------------------------------------------------------------

DROP TABLE IF EXISTS `FactPopup`;

CREATE TABLE `FactPopup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Game
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Game`;

CREATE TABLE `Game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL DEFAULT '20140509',
  `backgroundImageLink` varchar(255) DEFAULT NULL,
  `mapTitle` varchar(255) DEFAULT 'Mmowgli Map',
  `mapLatitude` DOUBLE  NOT NULL  DEFAULT '36.610902',
  `mapLongitude` DOUBLE  NOT NULL  DEFAULT '-121.8674989',
  `mapZoom`  INT(11)  NOT NULL  DEFAULT '13',
  `cardsReadonly` bit(1) NOT NULL,
  `clusterMaster` varchar(255) DEFAULT NULL,
  `currentMove_id` bigint(20) DEFAULT NULL,
  `lastMove_id` bigint(20) DEFAULT NULL,
  `defaultActionPlanImagesText` longtext,
  `defaultActionPlanMapText` longtext,
  `defaultActionPlanTalkText` longtext,
  `defaultActionPlanThePlanText` longtext,
  `defaultActionPlanVideosText` longtext,
  `defaultActionPlanMapLon` double DEFAULT '-99.476',
  `defaultActionPlanMapLat` double DEFAULT '38.597',
  `description` varchar(255) DEFAULT NULL,
  `displayedMoveNumberOverride` varchar(255) DEFAULT NULL,
  `endDate` datetime DEFAULT NULL,
  `maxUsersOnline` int(11) NOT NULL,
  `maxUsersRegistered` int(11) NOT NULL,
  `preregistrationDate` datetime DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  `readonly` bit(1) NOT NULL,
  `startDate` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `acronym` varchar(255) DEFAULT NULL,
  `reportIntervalMinutes` bigint(20) NOT NULL DEFAULT '0',
  `restrictByQueryListInterval` bit(1) NOT NULL DEFAULT b'0',
  `headerBranding_id` bigint(20) DEFAULT NULL,
  `linkRegexs` longblob,
  `topCardsReadonly` bit(1) NOT NULL DEFAULT b'0',
  `emailConfirmation` bit(1) NOT NULL DEFAULT b'0',
  `secondLoginPermissionPage` bit(1) NOT NULL DEFAULT b'0',
  `secondLoginPermissionPageTitle` varchar(255) NOT NULL DEFAULT '',
  `secondLoginPermissionPageText` longtext NOT NULL,
  `showFouo` bit(1) NOT NULL DEFAULT b'0',
  `fouoDescription` varchar(255) NOT NULL DEFAULT '',
  `showPriorMovesCards` bit(1) NOT NULL DEFAULT b'0',
  `showPriorMovesActionPlans` bit(1) NOT NULL DEFAULT b'0',
  `playOnPriorMovesCards` bit(1) NOT NULL DEFAULT b'0',
  `editPriorMovesActionPlans` bit(1) NOT NULL DEFAULT b'0',
  `fouoButtonImage` varchar(255) DEFAULT NULL,
  `pdfAvailable` bit(1) NOT NULL DEFAULT b'0',
  `inGameMailEnabled` bit(1) NOT NULL DEFAULT b'1',
  `userSignupAnswerPoints` float NOT NULL DEFAULT '10',
  `userActionPlanCommentPoints` float NOT NULL DEFAULT '1',
  `cardSuperInterestingPoints` float NOT NULL DEFAULT '5',
  `actionPlanCommentPoints` float NOT NULL DEFAULT '3',
  `actionPlanThumbFactor` float NOT NULL DEFAULT '1',
  `actionPlanAuthorPoints` float NOT NULL DEFAULT '100',
  `actionPlanSuperInterestingPoints` float NOT NULL DEFAULT '12',
  `actionPlanRaterPoints` float NOT NULL DEFAULT '5',
  `cardAncestorPoints` float NOT NULL DEFAULT '1',
  `cardAuthorPoints` float NOT NULL DEFAULT '7',
  `cardAncestorPointsGenerationFactors` varchar(255) NOT NULL DEFAULT '2.0 1.8 1.6 1.4 1.2 1.0 0.8',
  `externalMailEnabled` bit(1) NOT NULL DEFAULT b'1',
  `adminLoginMessage` longtext,
  `bootStrapping` bit(1) NOT NULL DEFAULT b'1',
  `headerBannerImage` varchar(255) DEFAULT 'mmowgliBanner350w130h.png',
  `actionPlansEnabled` bit(1) NOT NULL DEFAULT b'1',
  `showHeaderBranding` bit(1) NOT NULL DEFAULT b'1',
  `playIdeaButtonImage` varchar(255) DEFAULT 'playIdeaButt124w18h.png',
  `gameHandle` varchar(255) NOT NULL DEFAULT 'mmowgli',
  PRIMARY KEY (`id`),
  KEY `FK21C01217AD211E` (`currentMove_id`),
  KEY `FK21C012E29785A9` (`question_id`),
  KEY `FK21C012EF1BC319` (`headerBranding_id`),
  KEY `FK21C012E6BEC161` (`lastMove_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Game` DISABLE KEYS */;

INSERT INTO `Game` (`id`, `version`, `backgroundImageLink`, `mapTitle`, `cardsReadonly`, `clusterMaster`, `currentMove_id`, `lastMove_id`, `defaultActionPlanImagesText`, `defaultActionPlanMapText`, `defaultActionPlanTalkText`, `defaultActionPlanThePlanText`, `defaultActionPlanVideosText`, `defaultActionPlanMapLon`, `defaultActionPlanMapLat`, `description`, `displayedMoveNumberOverride`, `endDate`, `maxUsersOnline`, `maxUsersRegistered`, `preregistrationDate`, `question_id`, `readonly`, `startDate`, `title`, `acronym`, `reportIntervalMinutes`, `restrictByQueryListInterval`, `headerBranding_id`, `linkRegexs`, `topCardsReadonly`, `emailConfirmation`, `secondLoginPermissionPage`, `secondLoginPermissionPageTitle`, `secondLoginPermissionPageText`, `showFouo`, `fouoDescription`, `showPriorMovesCards`, `showPriorMovesActionPlans`, `playOnPriorMovesCards`, `editPriorMovesActionPlans`, `fouoButtonImage`, `pdfAvailable`, `inGameMailEnabled`, `userSignupAnswerPoints`, `userActionPlanCommentPoints`, `cardSuperInterestingPoints`, `actionPlanCommentPoints`, `actionPlanThumbFactor`, `actionPlanAuthorPoints`, `actionPlanSuperInterestingPoints`, `actionPlanRaterPoints`, `cardAncestorPoints`, `cardAuthorPoints`, `cardAncestorPointsGenerationFactors`, `externalMailEnabled`, `adminLoginMessage`, `bootStrapping`, `headerBannerImage`, `actionPlansEnabled`, `showHeaderBranding`, `playIdeaButtonImage`, `gameHandle`)
VALUES
	(1,20140129,'https://web.mmowgli.nps.edu/mmowMedia/images/nauticalBackground.jpg','NAVAIR Patuxent River Maryland',00000000,'web7',1,1,'Game.defaultActionPlanImagesText: Photographs, pictures, and charts bring your action plan to life and convey important details that might not fit easily into the text of your plan. \n<p>\nYou can search online for images, or upload images from your own desktop. Be sure to add a caption that explains the significance of each image.\n</p>\n<i>Hint: You may want to give one of your team members the responsibility for tracking down the images that support your plan.</i>\n','You can annotate a map to show how your plan would work. Here are some ways to use the map:\n<ul>\n<li>Annotate it with numbered steps to show how your plan will unfold.</li>\n\n<li>Mark the locations of resources your plan will draw on--or put in place.</li>\n\n<li>Add data points that explain the what\'s happening on the ground.</li>\n</ul>\nMaps are powerful tools for planning. Use yours in the way that best supports your plan!\n</p>\n<p>\nAs an author, you may make changes to this map by re-centering, zooming and adding markers.\nSave your changes using one or both of the save buttons above the map before leaving this page. (Non authors do not see the buttons.)\n</p>\n<b>New markers (authors only)</b>:  drag one of the following icons onto the map.  To delete a marker, click it on the map and follow directions in the popup window.\n</p>\n','Game.defaultActionPlanTalkText: Coming up with an action plan -- with people you may not know across multiple time zones -- can be a challenge. But you can use this private* chat room to trade ideas in real time or leave messages for your teammates.\n<p>\nYou might want to start by discussing the basic ideas in your card chain. How are you going to make those ideas work?  What\'s the core idea?  And what are the actions you need to take? \n</p><p>\nTalk it over here. But don\'t be shy about just starting to fill in the plan. Switch back and forth between The Plan and this team discussion space as you build your winning action plan.\n</p>\n*<i>Your chats cannot be seen by anyone else in the game other than gamemasters. However, they will be available to analysts for post-game analysis</i>.','Describe your action plan here. Talk it over with your fellow authors in real-time or asynchronous chat. Add images, videos, or map annotations. Remember this is a team effort! So work with your teammates to come up with the best possible plan.\n<p>\nNeed more information? Check our <a href=\"https://portal.mmowgli.nps.edu/instructions/-/asset_publisher/e02P/content/how-to-build-action-plans\" target=\"_portal\">help</a> page.\n</p>\n<p>\n<b>The 5 Basic Steps:</b>\n<ol>\n<li>Start by entering a headline that captures the big idea.</li>\n\n<li>Describe the basic plan in the What Is It? box.</li>\n\n<li>Make a list of the resources you need in the What Will It Take? box.</li>\n\n<li>Outline the steps to succeed in the How Will It Work box. <i>Hint: Use your card chain as a starting place</i>.</li>\n\n<li>Sum up the impact in the last box, How Will Change the Situation?</li>\n</ol>\nClick Save Changes often to make sure your text is saved. Click History to review previous versions.\n</p><p>\nWork fast. Work smart. Work together.\n<b><i>Good luck!</i></b></p>\n','A video is worth a thousand words.  Consider:<OL><LI> Making a 1-2 minute video to tell us about your action plan.</LI><LI>Share a video you\'ve found that helps support your action plan.</LI></OL>Add a caption to highlight the point you\'re making.',-99.4759979248047,38.5970001220703,NULL,NULL,NULL,100,10000,NULL,6,00000000,NULL,'NewMmowgliGame','new',60,00000001,NULL,X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A6088220300007870770400000001737200276564752E6E70732E6D6F7665732E6D6D6F77676C692E64622E47616D652452656765785061697200000000000000010200024C000572656765787400124C6A6176612F6C616E672F537472696E673B4C000B7265706C6163656D656E7471007E0003787074002047616D6520323031312E285C642920416374696F6E20506C616E20285C642B2974006D3C6120687265663D2268747470733A2F2F7765622E6D6D6F77676C692E6E70732E6564752F7069726163792F416374696F6E506C616E4C697374323031312E24312E68746D6C23416374696F6E506C616E243222207461726765743D22617077696E646F77223E24303C2F613E78',00000001,00000000,00000001,'Important!','<p>Please do not contribute any material specific to ongoing contracts or solicitations that are in any way competition sensitive.</p>',00000000,'Unclassified -- For Official Use Only',00000000,00000000,00000000,00000000,'fouo250w36h.png',00000000,00000001,10,1,10,3,1,100,12,5,1,5,'5.0 4.0 3.0 2.0 1.0',00000001,NULL,00000000,'mmowgliBanner350w130h.png',00000001,00000001,'playIdeaButt124w18h.png','mmowgli');

/*!40000 ALTER TABLE `Game` ENABLE KEYS */;


# Dump of table Game_ActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Game_ActionPlan`;

CREATE TABLE `Game_ActionPlan` (
  `Game_id` bigint(20) NOT NULL,
  `top5ActionPlans_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Game_id`,`top5ActionPlans_id`),
  UNIQUE KEY `top5ActionPlans_id` (`top5ActionPlans_id`),
  KEY `FK3DD384ECBEC0B622` (`top5ActionPlans_id`),
  KEY `FK3DD384EC83DE4917` (`Game_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Game_Move
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Game_Move`;

CREATE TABLE `Game_Move` (
  `Game_id` bigint(20) NOT NULL,
  `moves_id` bigint(20) NOT NULL,
  KEY `FKB30CE3FE83DE4917` (`Game_id`),
  KEY `FKB30CE3FE895E9426` (`moves_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Game_Move` DISABLE KEYS */;

INSERT INTO `Game_Move` (`Game_id`, `moves_id`)
VALUES
	(1,1);

/*!40000 ALTER TABLE `Game_Move` ENABLE KEYS */;


# Dump of table Game_Turn
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Game_Turn`;

CREATE TABLE `Game_Turn` (
  `Game_id` bigint(20) NOT NULL,
  `turns_id` bigint(20) NOT NULL,
  UNIQUE KEY `turns_id` (`turns_id`),
  KEY `FKB31028AA83DE4917` (`Game_id`),
  KEY `FKB31028AA979AE77E` (`turns_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GameEvent
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GameEvent`;

CREATE TABLE `GameEvent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime DEFAULT NULL,
  `description` longtext DEFAULT NULL,
  `eventtype` int(11) DEFAULT NULL,
  `parameter` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GameLinks
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GameLinks`;

CREATE TABLE `GameLinks` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `aboutLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/About%20MMOWGLI',
  `actionPlanRequestLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/action-plan-request',
  `blogLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-blogs',
  `creditsLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Credits%20and%20Contact',
  `faqLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/faq',
  `fixesLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/fixes',
  `fouoLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/fouo',
  `gameFromEmail` varchar(255) NOT NULL DEFAULT 'mmowgli@nps.navy.mil',
  `gameFullLink` varchar(255) NOT NULL DEFAULT 'https://mmowgli.nps.edu/pleaseWaitGameFull.html',
  `gameHomeUrl` varchar(255) NOT NULL DEFAULT 'mmowgli.nps.edu',
  `glossaryLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary',
  `howToPlayLink` varchar(255) DEFAULT NULL,
  `improveScoreLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/instructions',
  `informedConsentLink` varchar(255) NOT NULL DEFAULT 'https://web.mmowgli.nps.edu/mmowMedia/MmowgliGameParticipantInformedConsent.html',
  `learnMoreLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/instructions',
  `mmowgliMapLink` varchar(255) NOT NULL DEFAULT 'http://maps.google.com/maps/ms?hl=en&amp;ie=UTF8&amp;t=h&amp;msa=0&amp;msid=&amp;ll=38.895111,-77.036667&amp;spn=15.060443,18.676758&amp;z=6&amp;output=embed',
  `surveyConsentLink` varchar(255) NOT NULL DEFAULT 'https://movesinstitute.org/mmowMedia/MMOWGLI-AnonymousSurveyConsentUnsigned2013January25.pdf',
  `termsLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms%20and%20Conditions',
  `thanksForInterestLink` varchar(255) NOT NULL DEFAULT 'https://mmowgli.nps.edu/thanksForInterest.html',
  `thanksForPlayingLink` varchar(255) NOT NULL DEFAULT 'https://mmowgli.nps.edu/thanksForPlaying.html',
  `troubleLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/trouble',
  `troubleMailto` varchar(255) NOT NULL DEFAULT 'mmowgli-trouble@movesinstitute.org?subject=Problem%20creating%20new%20account',
  `userAgreementLink` varchar(255) NOT NULL DEFAULT 'http://www.defense.gov/socialmedia/user-agreement.aspx',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `GameLinks` DISABLE KEYS */;

INSERT INTO `GameLinks` (`id`, `aboutLink`, `actionPlanRequestLink`, `blogLink`, `creditsLink`, `faqLink`, `fixesLink`, `fouoLink`, `gameFromEmail`, `gameFullLink`, `gameHomeUrl`, `glossaryLink`, `howToPlayLink`, `improveScoreLink`, `informedConsentLink`, `learnMoreLink`, `mmowgliMapLink`, `surveyConsentLink`, `termsLink`, `thanksForInterestLink`, `thanksForPlayingLink`, `troubleLink`, `troubleMailto`, `userAgreementLink`)
VALUES
	(1,'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/About%20MMOWGLI','https://portal.mmowgli.nps.edu/action-plan-request','https://portal.mmowgli.nps.edu/game-blogs','https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Credits%20and%20Contact','https://portal.mmowgli.nps.edu/faq','https://portal.mmowgli.nps.edu/fixes','https://portal.mmowgli.nps.edu/fouo','mmowgli@nps.navy.mil','https://mmowgli.nps.edu/pleaseWaitGameFull.html','mmowgli.nps.edu','https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary',NULL,'https://portal.mmowgli.nps.edu/instructions','https://web.mmowgli.nps.edu/mmowMedia/MmowgliGameParticipantInformedConsent.html','https://portal.mmowgli.nps.edu/instructions','http://maps.google.com/maps/ms?hl=en&amp;ie=UTF8&amp;t=h&amp;msa=0&amp;msid=&amp;ll=38.895111,-77.036667&amp;spn=15.060443,18.676758&amp;z=6&amp;output=embed','https://movesinstitute.org/mmowMedia/MMOWGLI-AnonymousSurveyConsentUnsigned2013January25.pdf','https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms%20and%20Conditions','https://mmowgli.nps.edu/thanksForInterest.html','https://mmowgli.nps.edu/thanksForPlaying.html','https://portal.mmowgli.nps.edu/trouble','mmowgli-trouble@movesinstitute.org?subject=Problem%20creating%20new%20account','http://www.defense.gov/socialmedia/user-agreement.aspx');

/*!40000 ALTER TABLE `GameLinks` ENABLE KEYS */;


# Dump of table GameQuestion
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GameQuestion`;

CREATE TABLE `GameQuestion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `question` varchar(255) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `GameQuestion` DISABLE KEYS */;

INSERT INTO `GameQuestion` (`id`, `question`, `summary`)
VALUES
	(1,'What is your birthplace?','POB'),
	(2,'When did the first Somali pirate incident take place?','First Somali piracy'),
	(3,'What nations are militarily active in the Gulf of Aden?','Active military in Aden'),
	(4,'What\'s been the most important learning of your career?','Most important learning experience'),
	(5,'What do you hope to learn about Somali piracy?','To learn about piracy'),
	(6,'What\'s your hope for the world in 2021?','HOPE FOR 2021');

/*!40000 ALTER TABLE `GameQuestion` ENABLE KEYS */;


# Dump of table GoogleMap
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMap`;

CREATE TABLE `GoogleMap` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `latCenter` double DEFAULT NULL,
  `lonCenter` double DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `zoom` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `GoogleMap` DISABLE KEYS */;

INSERT INTO `GoogleMap` (`id`, `description`, `latCenter`, `lonCenter`, `title`, `zoom`)
VALUES
	(1,'',38.901720914998,-77.0299530029297,'',11),
	(2,'',38.5970001220703,-99.4759979248047,'',6);

/*!40000 ALTER TABLE `GoogleMap` ENABLE KEYS */;


# Dump of table GoogleMap_GoogleMapMarker
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMap_GoogleMapMarker`;

CREATE TABLE `GoogleMap_GoogleMapMarker` (
  `GoogleMap_id` bigint(20) NOT NULL,
  `markers_id` bigint(20) NOT NULL,
  UNIQUE KEY `markers_id` (`markers_id`),
  KEY `FK7611A261EF703961` (`markers_id`),
  KEY `FK7611A261CEE92C3D` (`GoogleMap_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GoogleMap_GoogleMapPolyOverlay
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMap_GoogleMapPolyOverlay`;

CREATE TABLE `GoogleMap_GoogleMapPolyOverlay` (
  `GoogleMap_id` bigint(20) NOT NULL,
  `overlays_id` bigint(20) NOT NULL,
  UNIQUE KEY `overlays_id` (`overlays_id`),
  KEY `FK9360D91DCEE92C3D` (`GoogleMap_id`),
  KEY `FK9360D91D87F9F9D5` (`overlays_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GoogleMapMarker
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMapMarker`;

CREATE TABLE `GoogleMapMarker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `draggable` bit(1) NOT NULL,
  `iconAnchorX` double DEFAULT NULL,
  `iconAnchorY` double DEFAULT NULL,
  `iconUrl` varchar(255) DEFAULT NULL,
  `lat` double DEFAULT NULL,
  `lon` double DEFAULT NULL,
  `popupContent` longtext,
  `title` varchar(255) DEFAULT NULL,
  `visible` bit(1) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GoogleMapPolyOverlay
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMapPolyOverlay`;

CREATE TABLE `GoogleMapPolyOverlay` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `clickable` bit(1) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `opacity` double NOT NULL,
  `weight` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table GoogleMapPolyOverlay_points
# ------------------------------------------------------------

DROP TABLE IF EXISTS `GoogleMapPolyOverlay_points`;

CREATE TABLE `GoogleMapPolyOverlay_points` (
  `GoogleMapPolyOverlay_id` bigint(20) NOT NULL,
  `points` tinyblob,
  KEY `FK124BD4414F0EC897` (`GoogleMapPolyOverlay_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table KeepAlive
# ------------------------------------------------------------

DROP TABLE IF EXISTS `KeepAlive`;

CREATE TABLE `KeepAlive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `receiveDate` datetime DEFAULT NULL,
  `receiver` varchar(255) DEFAULT NULL,
  `response` bit(1) NOT NULL,
  `sendDate` datetime DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Level
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Level`;

CREATE TABLE `Level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `ordinal` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `ordinal` (`ordinal`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Level` DISABLE KEYS */;

INSERT INTO `Level` (`id`, `description`, `ordinal`)
VALUES
	(1,'Player',1),
	(2,'Keen',2),
	(3,'Inspired',3),
	(4,'Brilliant',4),
	(5,'Luminous',5),
	(6,'Genius',6),
	(7,'Extreme Genious',7),
	(8,'Beyond Extreme Genius',8),
	(9,'Legend',9),
	(100,'Game Master',-1);

/*!40000 ALTER TABLE `Level` ENABLE KEYS */;


# Dump of table Login
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Login`;

CREATE TABLE `Login` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK462FF4986C88E77` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Media
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Media`;

CREATE TABLE `Media` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `caption` longtext,
  `description` longtext,
  `handle` varchar(255) DEFAULT NULL,
  `inAppropriate` bit(1) NOT NULL,
  `source` int(11) DEFAULT NULL,
  `title` longtext,
  `type` int(11) DEFAULT NULL,
  `url` longtext,
  `alternateUrl` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Media` DISABLE KEYS */;

INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`, `alternateUrl`)
VALUES
	(10,'MovePhase.callToActionBriefing placeholder','MovePhase.callToActionBriefing placeholder','YouTubeVideo',00000000,3,'MovePhase.callToActionBriefing placeholder',3,'zproG0jlmJw',NULL),
	(11,'MovePhase.orientationVideo placeholder','MovePhase.orientationVideo placeholder','Youtube',00000000,3,'MovePhase.orientationVideo placeholder',3,'boMsnx4lL30',NULL),
	(60,NULL,'Bulb','Bulb',00000000,0,NULL,2,'bulb.jpg',NULL),
	(61,NULL,'Buttons','Buttons',00000000,0,NULL,2,'buttons.jpg',NULL),
	(62,NULL,'Fire','Fire',00000000,0,NULL,2,'fire.jpg',NULL),
	(63,NULL,'Fox','Fox',00000000,0,NULL,2,'fox.jpg',NULL),
	(64,NULL,'Gears','Gears',00000000,0,NULL,2,'gears.jpg',NULL),
	(65,NULL,'Juggle','Juggle',00000000,0,NULL,2,'juggle.jpg',NULL),
	(66,NULL,'Leaf','Leaf',00000000,0,NULL,2,'leaf.jpg',NULL),
	(67,NULL,'Music','Music',00000000,0,NULL,2,'music.jpg',NULL),
	(68,NULL,'Paint','Paint',00000000,0,NULL,2,'paint.jpg',NULL),
	(69,NULL,'Raven','Raven',00000000,0,NULL,2,'raven.jpg',NULL),
	(70,NULL,'Red','Red',00000000,0,NULL,2,'red.jpg',NULL),
	(71,NULL,'Rocks','Rocks',00000000,0,NULL,2,'rocks.jpg',NULL),
	(72,NULL,'Skyscraper','Skyscraper',00000000,0,NULL,2,'skyscraper.jpg',NULL),
	(73,NULL,'Tea','Tea',00000000,0,NULL,2,'tea.jpg',NULL),
	(74,NULL,'Trex','Trex',00000000,0,NULL,2,'trex.jpg',NULL),
	(75,NULL,'Trojan','Trojan',00000000,0,NULL,2,'trojan.jpg',NULL),
	(76,NULL,'Zags','Zags',00000000,0,NULL,2,'zags.jpg',NULL),
	(78,'Orientation','MOVES 1,2 Orientation','Orientation video',00000000,1,NULL,3,'BtTGOxCHcD0',NULL),
	(80,'Piracy Brand image for header overlay','piracy in italics','piracy',00000000,0,'piracy',0,'https://web.mmowgli.nps.edu/mmowMedia/images/piracyBrand190w48h.png',NULL),
	(131,'award1','green achievement award','award1',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/achievement55w55h.png',NULL),
	(132,'award2','blue commendation award','award2',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/commendation55w55h.png',NULL),
	(133,'award3','red legion award','award3',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/legion55w55h.png',NULL),
	(134,'award1_300','green achievement award','award1_300',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/achievement300w300h.png',NULL),
	(135,'award2_300','blue commendation award','award2_300',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/commendation300w300h.png',NULL),
	(136,'award3_300','red legion award','award3_300',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/legion300w300h.png',NULL),
	(137,'cert_55','mmowgli certificate','cert_55',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png',NULL),
	(138,'cert_300','mmowgli certificate','cert_300',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked300x300.png',NULL),
	(147,'badge1','purple swoosh green','badge1',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge1_55w55h.png',NULL),
	(148,'badge2','vertical stripes','badge2',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge2_55w55h.png',NULL),
	(149,'badge3','mmowgli spider','badge3',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge3_55w55h.png',NULL),
	(150,'badge4','winged s','badge4',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge4_55w55h.png',NULL),
	(151,'badge5','orange star on black','badge5',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge5_55w55h.png',NULL),
	(152,'badge6','green checked folder','badge6',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge6_55w55h.png',NULL),
	(153,'badge7','winded star','badge7',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge7_55w55h.png',NULL),
	(154,'badge8','eye','badge8',00000000,3,NULL,0,'https://web.mmowgli.nps.edu/mmowMedia/images/badge8_55w55h.png',NULL),
	(159,'U.S. Secretary of the Navy Ray Mabus says the Navy and Marine Corps use enough energy spark alternative fuel infrastructure development.\n\nThis Carnegie Council event took place on November 9, 2010. For complete video, audio, and transcript, go to: http://www.carnegiecouncil.org','U.S. Secretary of the Navy Ray Mabus says the Navy and Marine Corps use enough energy spark alternative fuel infrastructure development.\n\nThis Carnegie Council event took place on November 9, 2010. For complete video, audio, and transcript, go to: http://www.carnegiecouncil.org','YouTubeVideo',00000000,3,'Ray Mabus: Naval Energy Market',3,'nB35qd7erjg',NULL),
	(163,'How to play cards','Youtube: MMOWGLI HowToPlayCards','HowToPlayCardsVideo',00000000,1,NULL,3,'D8_TAVyW6t4',NULL),
	(164,'How to win action','Youtube: MMOWGLI HowToWinAction','HowToWinActionVideo',00000000,1,NULL,3,'AXyD8t28ZVY',NULL),
	(165,'MMOWGLI Call to Action','Default call-to-action video','default call-to-action video handle',00000000,3,NULL,3,'nW4LnDuh8Go',NULL),
	(169,'The new Secretary of the Navy, Ray Mabus says his top priority is supporting Sailors, Marines and their families. He also wants to make the Navy energy independent and cut costs. See more DoD videos at http://dodvclips.mil','The new Secretary of the Navy, Ray Mabus says his top priority is supporting Sailors, Marines and their families. He also wants to make the Navy energy independent and cut costs. See more DoD videos at http://dodvclips.mil','YouTubeVideo',00000000,3,'Interview with SecNav Ray Mabus, 3 July 2009',3,'24zNBWPDJLM',NULL),
	(172,NULL,NULL,NULL,00000000,1,NULL,0,'1/FirefoxScreenSnapz1000.png',NULL),
	(174,'The new Secretary of the Navy, Ray Mabus says his top priority is supporting Sailors, Marines and their families. He also wants to make the Navy energy independent and cut costs. See more DoD videos at http://dodvclips.mil','The new Secretary of the Navy, Ray Mabus says his top priority is supporting Sailors, Marines and their families. He also wants to make the Navy energy independent and cut costs. See more DoD videos at http://dodvclips.mil','YouTubeVideo',00000000,3,'SecNav Ray Mabus',3,'24zNBWPDJLM',NULL),
	(175,'Describe this video here','Action Plan video','YouTubeVideo',00000000,3,'Title here',3,'watch?v=nB35qd7erjg',NULL),
	(176,'U.S. Secretary of the Navy Ray Mabus says the Navy and Marine Corps use enough energy spark alternative fuel infrastructure development.\n\nThis Carnegie Council event took place on November 9, 2010. For complete video, audio, and transcript, go to: http://www.carnegiecouncil.org','U.S. Secretary of the Navy Ray Mabus says the Navy and Marine Corps use enough energy spark alternative fuel infrastructure development.\n\nThis Carnegie Council event took place on November 9, 2010. For complete video, audio, and transcript, go to: http://www.carnegiecouncil.org','YouTubeVideo',00000000,3,'Ray Mabus: Naval Energy Market',3,'nB35qd7erjg',NULL),
	(200,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'8qEmLaom6ko',NULL),
	(201,'Somalia Piracy - created at http://animoto.com','Somalia Piracy - created at http://animoto.com','YouTubeVideo',00000000,3,'Somalia Piracy',3,'BQoXBaawblg',NULL),
	(202,'U.S. NAVY SEALS rescue Captain Richard Phillips from Somali Pirates.','Somali pirates hijack MV Maersk Alabama, U.S. Navy Seals conduct rescue.','YouTubeVideo',00000000,3,'NAVY SEALS RESCUE CAPTAIN RICHARD PHILLIPS',3,'lVSCZEjoBKw',NULL),
	(207,'','Somali pirates use small, fast boats to board ships','handle',00000000,3,'Somali pirate skiff',0,'http://1.bp.blogspot.com/_kC5MT2r5U8s/S7U7-1eazKI/AAAAAAAAOLs/Lzml27hT4ok/s1600/somali+pirate+skff.jpg',NULL),
	(208,NULL,'TODO fix game software bug, ensure that titles and captions are included in the published Action Plans report.',NULL,00000000,1,'Local upload image failure!',0,'1/0402-us-navy-hits-back-at-pirates_jpg_full_6002.jpg',NULL),
	(210,NULL,'The Naval Open Systems Architecture\n(OSA) strategy will decompose monolithic business and technical designs into manageable\nproduct lines composed of competition-driven modular Enterprise components. This will yield\ninnovation, reduced cycle time, and lower total ownership costs.',NULL,00000000,1,'Charting a Course to the Future BII',0,'1/ChartingCourseToFutureBII.jpg',NULL),
	(211,'Describe this video here','Action Plan video','YouTubeVideo',00000000,3,'Title here',3,'A24HxJvsd2g',NULL),
	(212,'bii MMOWGLI Call to Action describes the motivation behind the Business Innovation Initiative (bii) Massive Multiplayer Online Wargame Leveraging the Internet (MMOWGLI) game.  Navy and industry professionals are exploring how best to achieve the business goals of the Navy\'s new Open Systems Architecture (OSA) strategy..  Play the game, change the game!\nhttps://mmowgli.nps.edu/bii','bii MMOWGLI Call to Action describes the motivation behind the Business Innovation Initiative (bii) Massive Multiplayer Online Wargame Leveraging the Internet (MMOWGLI) game.  Navy and industry professionals are exploring how best to achieve the business goals of the Navy\'s new Open Systems Architecture (OSA) strategy..  Play the game, change the game!\nhttps://mmowgli.nps.edu/bii','YouTubeVideo',00000000,3,'bii MMOWGLI Call to Action Video',3,'A24HxJvsd2g',NULL),
	(400,'MMOWGLI Orientation','Default orientation video','default orientation video handle',00000000,3,'MMOWGLI Orientation ',3,'BtTGOxCHcD0',NULL),
	(401,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'CzblsGQfun0',NULL),
	(402,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'',NULL),
	(403,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'T1yVWGRxxSc',NULL),
	(404,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'DBSu-7KFYtc',NULL),
	(405,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'k36fduaDK6c',NULL),
	(406,NULL,'',NULL,00000000,1,'Discovery Girl',0,'5/8686-MA-1-lrg.jpg',NULL),
	(407,NULL,'',NULL,00000000,1,'Happy Birthday',0,'5/Happy_Birthday_Samantha.jpg',NULL),
	(409,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'HOm9SVTKym4',NULL),
	(410,'Becca\'s video','Becca\'s video','YouTubeVideo',00000000,3,'Becca\'s video',3,'HOm9SVTKym4',NULL),
	(411,'','','handle',00000000,3,NULL,0,'http://upload.wikimedia.org/wikipedia/en/3/39/Fab%40Home_Model_1_3D_printer.jpg',NULL),
	(412,NULL,NULL,NULL,00000000,1,NULL,0,'2/3dgun.jpg',NULL);

/*!40000 ALTER TABLE `Media` ENABLE KEYS */;


# Dump of table Message
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Message`;

CREATE TABLE `Message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime DEFAULT NULL,
  `text` longtext,
  `fromUser_id` bigint(20) DEFAULT NULL,
  `toUser_id` bigint(20) DEFAULT NULL,
  `hidden` bit(1) NOT NULL,
  `superInteresting` bit(1) NOT NULL DEFAULT b'0',
  `createdInMove_id` bigint(20) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK9C2397E7C06D4F9C` (`toUser_id`),
  KEY `FK9C2397E72728638D` (`fromUser_id`),
  KEY `FK9C2397E7AF29DE0A` (`createdInMove_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table MessageUrl
# ------------------------------------------------------------

DROP TABLE IF EXISTS `MessageUrl`;

CREATE TABLE `MessageUrl` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL,
  `url` varchar(511) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `tooltip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Move
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Move`;

CREATE TABLE `Move` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `endDate` datetime DEFAULT NULL,
  `number` int(11) NOT NULL,
  `startDate` datetime DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `showMoveBranding` bit(1) NOT NULL DEFAULT b'0',
  `name` varchar(255) DEFAULT NULL,
  `currentMovePhase_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK24AFF1E69C1AD6` (`currentMovePhase_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Move` DISABLE KEYS */;

INSERT INTO `Move` (`id`, `endDate`, `number`, `startDate`, `title`, `showMoveBranding`, `name`, `currentMovePhase_id`)
VALUES
	(1,NULL,1,NULL,'new',00000000,'Round 1',1);

/*!40000 ALTER TABLE `Move` ENABLE KEYS */;


# Dump of table Move_MovePhase
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Move_MovePhase`;

CREATE TABLE `Move_MovePhase` (
  `Move_id` bigint(20) NOT NULL,
  `movePhases_id` bigint(20) NOT NULL,
  UNIQUE KEY `movePhases_id` (`movePhases_id`),
  KEY `FK22BE93CD9A948B7` (`Move_id`),
  KEY `FK22BE93CD657495E` (`movePhases_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Move_MovePhase` DISABLE KEYS */;

INSERT INTO `Move_MovePhase` (`Move_id`, `movePhases_id`)
VALUES
	(1,1),
	(1,2),
	(1,3),
	(1,4);

/*!40000 ALTER TABLE `Move_MovePhase` ENABLE KEYS */;


# Dump of table MovePhase
# ------------------------------------------------------------

DROP TABLE IF EXISTS `MovePhase`;

CREATE TABLE `MovePhase` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `authorInviteCallToActionText` varchar(255) DEFAULT NULL,
  `authorInviteHeadline` varchar(255) DEFAULT NULL,
  `authorInviteSummary` varchar(255) DEFAULT NULL,
  `callToActionBriefingHeadline` varchar(255) DEFAULT NULL,
  `callToActionBriefingPrompt` varchar(255) DEFAULT NULL,
  `callToActionBriefingSummary` varchar(255) DEFAULT NULL,
  `callToActionBriefingText` longtext,
  `description` varchar(255) DEFAULT NULL,
  `investorInviteCallToActionText` varchar(255) DEFAULT NULL,
  `investorInviteHeadline` varchar(255) DEFAULT NULL,
  `investorInviteSummary` varchar(255) DEFAULT NULL,
  `orientationCallToActionText` varchar(255) DEFAULT NULL,
  `orientationHeadline` longtext,
  `orientationSummary` varchar(255) DEFAULT NULL,
  `playACardSubtitle` varchar(255) DEFAULT NULL,
  `playACardTitle` varchar(255) DEFAULT NULL,
  `authorInviteVideo_id` bigint(20) DEFAULT NULL,
  `callToActionBriefingVideo_id` bigint(20) DEFAULT NULL,
  `investorInviteVideo_id` bigint(20) DEFAULT NULL,
  `orientationVideo_id` bigint(20) DEFAULT NULL,
  `windowTitle` varchar(255) DEFAULT NULL,
  `signupText` longtext,
  `signupPageEnabled` bit(1) NOT NULL DEFAULT b'0',
  `signupButtonEnabled` bit(1) NOT NULL DEFAULT b'0',
  `signupButtonShow` bit(1) NOT NULL DEFAULT b'0',
  `signupButtonIcon` varchar(255) NOT NULL DEFAULT ' ',
  `signupButtonSubText` varchar(255) NOT NULL DEFAULT '',
  `signupButtonToolTip` varchar(255) NOT NULL DEFAULT '',
  `newButtonEnabled` bit(1) NOT NULL DEFAULT b'1',
  `newButtonShow` bit(1) NOT NULL DEFAULT b'1',
  `newButtonIcon` varchar(255) NOT NULL DEFAULT '',
  `newButtonSubText` varchar(255) NOT NULL DEFAULT '',
  `newButtonToolTip` varchar(255) NOT NULL DEFAULT '',
  `loginButtonEnabled` bit(1) NOT NULL DEFAULT b'1',
  `loginButtonShow` bit(1) NOT NULL DEFAULT b'1',
  `loginButtonIcon` varchar(255) NOT NULL DEFAULT '',
  `loginButtonSubText` varchar(255) NOT NULL DEFAULT '',
  `loginButtonToolTip` varchar(255) NOT NULL DEFAULT '',
  `guestButtonEnabled` bit(1) NOT NULL DEFAULT b'0',
  `guestButtonShow` bit(1) NOT NULL DEFAULT b'0',
  `guestButtonIcon` varchar(255) NOT NULL DEFAULT '',
  `guestButtonSubText` varchar(255) NOT NULL DEFAULT '',
  `guestButtonToolTip` varchar(255) NOT NULL DEFAULT '',
  `loginPermissions` smallint(6) NOT NULL DEFAULT '-1',
  `restrictByQueryList` bit(1) NOT NULL DEFAULT b'0',
  `signupHeaderImage` varchar(255) DEFAULT 'mmowgli_logo_final.png',
  PRIMARY KEY (`id`),
  KEY `FKB648B80AC06546E7` (`investorInviteVideo_id`),
  KEY `FKB648B80AF4583C16` (`orientationVideo_id`),
  KEY `FKB648B80A504D563D` (`callToActionBriefingVideo_id`),
  KEY `FKB648B80ACD3E9A` (`authorInviteVideo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `MovePhase` DISABLE KEYS */;

INSERT INTO `MovePhase` (`id`, `authorInviteCallToActionText`, `authorInviteHeadline`, `authorInviteSummary`, `callToActionBriefingHeadline`, `callToActionBriefingPrompt`, `callToActionBriefingSummary`, `callToActionBriefingText`, `description`, `investorInviteCallToActionText`, `investorInviteHeadline`, `investorInviteSummary`, `orientationCallToActionText`, `orientationHeadline`, `orientationSummary`, `playACardSubtitle`, `playACardTitle`, `authorInviteVideo_id`, `callToActionBriefingVideo_id`, `investorInviteVideo_id`, `orientationVideo_id`, `windowTitle`, `signupText`, `signupPageEnabled`, `signupButtonEnabled`, `signupButtonShow`, `signupButtonIcon`, `signupButtonSubText`, `signupButtonToolTip`, `newButtonEnabled`, `newButtonShow`, `newButtonIcon`, `newButtonSubText`, `newButtonToolTip`, `loginButtonEnabled`, `loginButtonShow`, `loginButtonIcon`, `loginButtonSubText`, `loginButtonToolTip`, `guestButtonEnabled`, `guestButtonShow`, `guestButtonIcon`, `guestButtonSubText`, `guestButtonToolTip`, `loginPermissions`, `restrictByQueryList`, `signupHeaderImage`)
VALUES
	(1,NULL,NULL,NULL,NULL,NULL,'Welcome to the (new) MMOWGLI game','<p>The <b>additive manufacturing (am) MMOWGLI game</b> is for Navy, DoD, Industry and academic professionals exploring how best to use emerging am capabilities. We’re collaborating to build Idea Card chains and Action Plans that investigate exciting new opportunities.</p>\n<p> \nParticipants consider solutions for <i>today\'s Navy</i> and transformation opportunities for the <i>Navy of tomorrow</i>. We are currently in the design phase of the am game, considering how top-level Seed Cards can initiate effective dialog on major topics of interest.\n</p>\n<p>  Player contributions are essential.  Please join in to contribute!\n<br />\nThe <a href=\"https://portal.mmowgli.nps.edu/am\" target=\"_blank\">additive manufacturing (am) portal</a> is a great information resource for game play.  Check out the <a href=\"https://portal.mmowgli.nps.edu/am-blog\" target=\"_blank\">am blog</a> for latest game news.</p>\n<p>\nThanks for your ideas.\nPlay&nbsp;the&nbsp;game, change the game!</p>','PREPARE',NULL,NULL,NULL,'additive manufacturing (am) MMOWGLI: welcome players!','<!-- p style=\"color:green\">Please come back Monday to register and play! </p-->\n\n<p> The additive manufacturing (am) game will explore <b>today\'s opportunities</b> and tomorrow\'s <b>transformative challenges</b> for the Navy.</p>\n<p> <!--span style=\"color:green\"> Once the game is open on Monday,</span--> If you work for the Navy/DoD, or for a company that supports the Navy, or at a university, please\n<a href=\"https://mmowgli.nps.edu/am/signup\">signup to be notified</a>\nwhen am mmowgli is ready to begin.  <b>U.S. citizens only, please</b>.\n<!-- begin by following <i>I\'m&nbsp;new to MMOWGLI</i> below. Registration is fast.-->\n</p>\n<p>\nGame masters: to&nbsp;login again, follow the<br /> <i>I\'m registered</i> link below.\n<!--You can also <a href=\"https://mmowgli.nps.edu/am/signup\">sign up to be notified</a> when the game starts.-->\n</p>\n<p>The <a href=\"https://portal.mmowgli.nps.edu/am\" target=\"portal\">am mmowgli portal</a> has project news and game information for Navy and industry players. Let\'s innovate together - thanks for all contributions.\nPlay the game, change the game!</p>','','Start now, play fast, work together.','Play an Idea Card now!',NULL,409,NULL,78,'new mmowgli: Massive Multiplayer Online Wargame Leveraging the Internet','<table>\n<tr>\n<td>\n<!--\n<img src=\"https://portal.mmowgli.nps.edu/image/image_gallery?uuid=c7847796-f33e-40d7-a287-fce3efdbe504&groupId=10156&t=1377741241726\" hspace=\"4\" width=\"250\"/>\n-->\n<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/HOm9SVTKym4\" frameborder=\"0\" allowfullscreen></iframe>\n<p align=\"center\">\n<a href=\"https://portal.mmowgli.nps.edu/am-videos\">Can\'t see the video?</a></p>\n</td>\n<td> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\n</td>\n<td>\n<p>\nThe initial round of the <i><b>additive manufacturing (am) mmowgli</b></i>&nbsp; game is being designed to explore Navy opportunities and challenges associated with additive manufacturing, 3D printing, improved maintenance, forward-deployed logistics, and related capabilities.\n</p>\n\n<p>\nWe’re collaborating to build Idea Card chains and Action Plans that investigate exciting new opportunities.\nParticipants consider <i>Solutions</i> for immediate use and <i>Transformation</i> opportunities for the Navy over the long term. \n</p>\n<p>\n<i>If you want to receive an email message</i> when the Additive Manufacturing (am) MMOWGLI game begins, <b><font color=\"#008000\">16-27&nbsp;September&nbsp;2013</font></b>, please fill out the following information and click \"Signup\" below. You will create your game-name account later, when the game actually runs next week. </p>\n<p>\n<b>The game is primarily for individuals in Navy, DoD, industry or academia.</b>  Players with .mil email addresses are automatically granted access.  If you work in industry and might want to participate, please also let us know your Navy/DoD business relationship that justifies your access to this game. <b>U.S. citizens only, 18 or older.</b></p>\n<p> More information is available on the \n<a href=\"https://portal.mmowgli.nps.edu/am\" target=\"_blank\">additive manufacturing (am) portal</a>\nand\n<a href=\"https://portal.mmowgli.nps.edu/am-blog\" target=\"_blank\">additive manufacturing (am) blog</a>. </p>\n<p> <img src=\'https://web.mmowgli.nps.edu/mmowMedia/images/blackThumb21w29h.png\' height=\'29\' width=\'21\'/>  Thanks for your interest.\nPlay the game, change the game! </p>\n</td>\n</tr>\n</table>\n',00000001,00000001,00000001,'tellMeMore130w15h.png','Signup for email notification','Navy/DoD, industry or academia, please',00000000,00000001,'imNewButton202w22h.png','Game masters only please...','You can get started in 2 minutes...',00000001,00000001,'imRegisteredButton133w24h.png','Rejoin the action now!','Login using your game name',00000000,00000001,'guestLogin97w24h.png','Look around a bit','Read only',7,00000000,'mmowgli_logo_final.png'),
	(2,NULL,NULL,NULL,NULL,NULL,'Welcome to the (new) MMOWGLI game!','<p>The <b>additive manufacturing (am) MMOWGLI game</b> is for a diverse group of subject matter experts from the DoD, Industry and Academia. Together, we’re proposing and publishing new Idea Card Chains and Action Plans to work together more effectively.</p>\n<p> \nBy actively contributing, participants will explore how AM can help solve the problems of today\'s Navy and how AM will transform the Navy of tomorrow.  Each day we will have a new theme with specially marked Seed Cards! Play on these cards and receive a special award at the end of the game and/or contribute your own top-level idea cards!\n<p>\nThe <a href=\"https://portal.mmowgli.nps.edu/am\" target=\"_blank\">AM game portal</a> is a great information resource for game play.  You can also check out the <a href=\"https://portal.mmowgli.nps.edu/am-blog\" target=\"_blank\">am game blog</a> for the latest game news and our special Seed Cards of the day! Your contributions are essential.</p>\n<p>\nThanks for your ideas. Play&nbsp;the&nbsp;game, change the game!</p>','PLAY',NULL,NULL,NULL,'am MMOWGLI: welcome players!','<!-- p style=\"color:green\">Please come back Monday to register and play! </p-->\n\n<p> The <b>am MMOWGLI</b> wargame examines both new&nbsp;opportunities and future challenges of Additive&nbsp;Manufacturing for the Navy.\n</p>\n<p> <span style=\"color:green\"> Game play is now open, 16-27 SEPT 2013.</span>\n<br />\n If you work for the Navy, in academia, or for a company supporting the Navy, then great. \n You must be a U.S. citizen and 18+ years old to join.\n</p>\n<p>\n If you are interested in additive manufacturing, we&nbsp;need your ideas!\nTo begin, select the <br /><i>\"I\'m new to MMOWGLI\"</i> button below\nto start.\n<!-- begin by following <i>I\'m&nbsp;new to MMOWGLI</i> below. Registration is quick and easy!--> \n\n<!--You can also <a href=\"https://mmowgli.nps.edu/am/signup\">sign up to be notified</a> when the game starts.-->\n</p>\n<p>The <a href=\"https://portal.mmowgli.nps.edu/am\" target=\"portal\">am portal</a> has project news and game information for Navy and industry players.  The&nbsp;<a href=\"https://portal.mmowgli.nps.edu/am-blog\" target=\"portal\">am&nbsp;blog</a> has the latest game news. \n<br />\nPlay the game, change the game!\n</p>\n<!--\n<p>Let&apos;s innovate together - thanks for all contributions.\nPlay the game, change the game!\n</p>\n-->','','Start now, play fast, work together.','Today\'s theme is Qualification and Certification',NULL,410,NULL,78,'new mmowgli: Massive Multiplayer Online Wargame Leveraging the Internet','\n<h2> Requesting Permission to Play in the <a href=\"https://mmowgli.nps.edu/am\" target=\"amGame\">am MMOWGLI game</a></h2>\n<table>\n<tr>\n<td>\n<p align=\"center\">\n<a href=\"http://youtube.com/v/HOm9SVTKym4\"><img alt=\"Call to Action: additive manufacturing (am) MMOWGLI game\" src=\"https://portal.mmowgli.nps.edu/image/image_gallery?uuid=91d96f40-6ea1-4927-b6c6-1e06f8b9c3d6&groupId=10156&t=1378510781585\" style=\"width: 222px; height: 133px; margin: 6px;\" /></a>\n<br />\n<a href=\"https://portal.mmowgli.nps.edu/am-videos\">Can\'t see the video?</a></p>\n</td>\n<td valign=\"top\">\n<p>\nThe <i><b>am mmowgli</b></i> game is designed to explore Navy opportunities and challenges associated with Additive Manufacturing, 3D printing, improved maintenance, forward&#8209;deployed logistics, and related capabilities.\n</p>\n<p>\nAdditive Manufacturing (AM), including 3D printing, is a recognized \"game changing\" manufacturing technology. The Navy operates in a unique and extremely harsh environment far from the shores of the United States. How will our ability to manufacture parts and systems on demand, where and when they are needed, impact tomorrow&apos;s Navy? What are the challenges associated with the implementation of AM? What might be the unexpected, innovative effects of AM?\n</p>\n</td>\n</tr>\n</table>\n\n\n<p>\nMost people are granted permission based on their confirmation email address (.mil .edu etc.)  Some email addresses (such as .com) have to first request permission for access using this form.\n</p>\n<p>\n <b>You must be a U.S. citizen and 18 years or older to join.</b>\nIf you want permission to play when the Additive Manufacturing game is running <b>16-27 September 2013</b>, please fill out the following information and click \"Signup\" below. </p>\n<p>\nThe game is primarily for NAVAIR and Navy personnel, but game sponsors will allow industry, academic and other professionals will be granted access.  If you work in industry and might want to participate, please also let us know your Navy/DoD business relationship that justifies your access to this game. </p>\n\n<p> More information is available on the \n<a href=\"https://portal.mmowgli.nps.edu/am\" target=\"blank\">am mmowgli portal</a>\nand\n<a href=\"https://portal.mmowgli.nps.edu/am-blog\" target=\"_blank\">am blog</a>. </p>\n<p> <img src=\'https://web.mmowgli.nps.edu/mmowMedia/images/blackThumb21w29h.png\' height=\'29\' width=\'21\'/>  Thanks for your interest.\nPlay the game, change the game! </p>',00000001,00000001,00000001,'tellMeMore130w15h.png','Request access if needed','Permission signup for email notification',00000001,00000001,'imNewButton202w22h.png','Create a new game name to play','You can get started in 2 minutes...',00000001,00000001,'imRegisteredButton133w24h.png','Rejoin the action now!','Login using your game name',00000000,00000000,'guestLogin97w24h.png','Look around a bit','Read only',55,00000000,'mmowgli_logo_final.png'),
	(3,NULL,NULL,NULL,NULL,NULL,'Welcome to the (new) MMOWGLI game3','<p>The <b>additive MMOWGLI game</b> is for NAVAIR professionals exploring how best to achieve shared command goals together. Together, we’re proposing and publishing new Idea Card Chains and Action Plans to work together more effectively.</p>\n<p> \nParticipants will explore  Group Goals and Group Challenges in the design phase of this game. By actively contributing, top level cards and seed cards for the live game will begin to emerge!\n</p>\n<p>  Player contributions are essential.  Please join in to contribute!\n<br />\nThe <a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"_blank\">additive game portal</a> is a great information resource for game play.  You can also check out the <a href=\"https://portal.mmowgli.nps.edu/additive-blog\" target=\"_blank\">additive game blog</a> for game news.</p>\n<p>\nThanks for your ideas.\nPlay&nbsp;the&nbsp;game, change the game!</p>','REVIEW',NULL,NULL,NULL,'additive MMOWGLI: welcome NAVAIR players and partners!','<!-- p style=\"color:green\">Please come back Monday to register and play! </p-->\n\n<p> The <b>additive MMOWGLI</b> wargame will examine new opportunities and challenges of Additive Manufacturing for Navy personnel.</p>\n<p> <!--span style=\"color:green\"> Once the game is open on Monday,</span--> If you work for the Navy, or for a company that supports the Navy, please\n<a href=\"https://mmowgli.nps.edu/additive/signup\">signup to be notified</a>\nwhen&nbsp;the additive mmowgli game is ready to begin.\n<!-- begin by following <i>I\'m&nbsp;new to MMOWGLI</i> below. Registration is fast.--> \n\nGame masters: to&nbsp;login again, follow the<br /> <i>I\'m registered</i> link below.\n<!--You can also <a href=\"https://mmowgli.nps.edu/adwd/signup\">sign up to be notified</a> when the game starts.-->\n</p>\n<p>The <a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"portal\">additive mmowgli portal</a> has project news and game information for Navy and industry players. Let&apos;s innovate together - thanks for all contributions.\nPlay the game, change the game!</p>','(Preparation-phase game design in progress)','Start now, play fast, work together.','Play an Idea Card about our topic Now!',NULL,404,NULL,78,'new mmowgli: Massive Multiplayer Online Wargame Leveraging the Internet','<p>\r\nThe initial round of the <i><b>additive mmowgli</b></i> game is being designed to explore Navy opportunities and challenges associated with additive manufacturing, 3D printing, improved maintenance, forward-deployed logistics, and related capabilities.\r\n</p>\r\n<p>\r\nIf you want to receive an email message when the Additive Manufacturing game is running, later this summer, please fill out the following information and click \"Signup\" below. </p>\r\n<p>\r\nThe game is primarily for NAVAIR and Navy personnel.  Game sponsors are considering whether industry professionals will also be granted access.  If you work in industry and might want to participate, please also let us know your Navy/DoD business relationship that justifies your access to this game. </p>\r\n\r\n<p> More information is available on the \r\n<a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"_blank\">additive mmowgli portal</a>\r\nand\r\n<a href=\"https://portal.mmowgli.nps.edu/additive-blog\" target=\"_blank\">additive game blog</a>. </p>\r\n<p> <img src=\'https://web.mmowgli.nps.edu/mmowMedia/images/blackThumb21w29h.png\' height=\'29\' width=\'21\'/>  Thanks for your interest.\r\nPlay the game, change the game! </p>',00000001,00000001,00000001,'tellMeMore130w15h.png','Signup for email notification','',00000000,00000001,'imNewButton202w22h.png','Game masters only please...','You can get started in 2 minutes...',00000001,00000001,'imRegisteredButton133w24h.png','Rejoin the action now!','Login using your game name',00000000,00000001,'guestLogin97w24h.png','Look around a bit','Read only',-9,00000000,'mmowgli_logo_final.png'),
	(4,NULL,NULL,NULL,NULL,NULL,'Welcome to the (new) MMOWGLI game4','<p>The <b>additive MMOWGLI game</b> is for NAVAIR professionals exploring how best to achieve shared command goals together. Together, we’re proposing and publishing new Idea Card Chains and Action Plans to work together more effectively.</p>\n<p> \nParticipants will explore  Group Goals and Group Challenges in the design phase of this game. By actively contributing, top level cards and seed cards for the live game will begin to emerge!\n</p>\n<p>  Player contributions are essential.  Please join in to contribute!\n<br />\nThe <a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"_blank\">additive game portal</a> is a great information resource for game play.  You can also check out the <a href=\"https://portal.mmowgli.nps.edu/additive-blog\" target=\"_blank\">additive game blog</a> for game news.</p>\n<p>\nThanks for your ideas.\nPlay&nbsp;the&nbsp;game, change the game!</p>','PUBLISH',NULL,NULL,NULL,'additive MMOWGLI: welcome NAVAIR players and partners!','<!-- p style=\"color:green\">Please come back Monday to register and play! </p-->\n\n<p> The <b>additive MMOWGLI</b> wargame will examine new opportunities and challenges of Additive Manufacturing for Navy personnel.</p>\n<p> <!--span style=\"color:green\"> Once the game is open on Monday,</span--> If you work for the Navy, or for a company that supports the Navy, please\n<a href=\"https://mmowgli.nps.edu/additive/signup\">signup to be notified</a>\nwhen&nbsp;the additive mmowgli game is ready to begin.\n<!-- begin by following <i>I\'m&nbsp;new to MMOWGLI</i> below. Registration is fast.--> \n\nGame masters: to&nbsp;login again, follow the<br /> <i>I\'m registered</i> link below.\n<!--You can also <a href=\"https://mmowgli.nps.edu/adwd/signup\">sign up to be notified</a> when the game starts.-->\n</p>\n<p>The <a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"portal\">additive mmowgli portal</a> has project news and game information for Navy and industry players. Let&apos;s innovate together - thanks for all contributions.\nPlay the game, change the game!</p>','(Preparation-phase game design in progress)','Start now, play fast, work together.','Play an Idea Card about our topic Now!',NULL,404,NULL,78,'new mmowgli: Massive Multiplayer Online Wargame Leveraging the Internet','<p>\r\nThe initial round of the <i><b>additive mmowgli</b></i> game is being designed to explore Navy opportunities and challenges associated with additive manufacturing, 3D printing, improved maintenance, forward-deployed logistics, and related capabilities.\r\n</p>\r\n<p>\r\nIf you want to receive an email message when the Additive Manufacturing game is running, later this summer, please fill out the following information and click \"Signup\" below. </p>\r\n<p>\r\nThe game is primarily for NAVAIR and Navy personnel.  Game sponsors are considering whether industry professionals will also be granted access.  If you work in industry and might want to participate, please also let us know your Navy/DoD business relationship that justifies your access to this game. </p>\r\n\r\n<p> More information is available on the \r\n<a href=\"https://portal.mmowgli.nps.edu/additive\" target=\"_blank\">additive mmowgli portal</a>\r\nand\r\n<a href=\"https://portal.mmowgli.nps.edu/additive-blog\" target=\"_blank\">additive game blog</a>. </p>\r\n<p> <img src=\'https://web.mmowgli.nps.edu/mmowMedia/images/blackThumb21w29h.png\' height=\'29\' width=\'21\'/>  Thanks for your interest.\r\nPlay the game, change the game! </p>',00000001,00000001,00000001,'tellMeMore130w15h.png','Signup for email notification','',00000000,00000001,'imNewButton202w22h.png','Game masters only please...','You can get started in 2 minutes...',00000001,00000001,'imRegisteredButton133w24h.png','Rejoin the action now!','Login using your game name',00000000,00000001,'guestLogin97w24h.png','Look around a bit','Read only',-9,00000000,'mmowgli_logo_final.png');

/*!40000 ALTER TABLE `MovePhase` ENABLE KEYS */;


# Dump of table MovePhase_CardType
# ------------------------------------------------------------

DROP TABLE IF EXISTS `MovePhase_CardType`;

CREATE TABLE `MovePhase_CardType` (
  `MovePhase_id` bigint(20) NOT NULL,
  `allowedCards_id` bigint(20) NOT NULL,
  PRIMARY KEY (`MovePhase_id`,`allowedCards_id`),
  KEY `FK37A6BF1F9435CADD` (`MovePhase_id`),
  KEY `FK37A6BF1FB19999A6` (`allowedCards_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `MovePhase_CardType` DISABLE KEYS */;

INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`)
VALUES
	(1,1),
	(1,2),
	(1,3),
	(1,4),
	(1,5),
	(1,6),
	(2,1),
	(2,2),
	(2,3),
	(2,4),
	(2,5),
	(2,6),
	(3,1),
	(3,2),
	(3,3),
	(3,4),
	(3,5),
	(3,6),
	(4,1),
	(4,2),
	(4,3),
	(4,4),
	(4,5),
	(4,6);

/*!40000 ALTER TABLE `MovePhase_CardType` ENABLE KEYS */;


# Dump of table Pages
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Pages`;

CREATE TABLE `Pages` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `confirmationEmail` longtext,
  `actionPlanInviteEmail` longtext,
  `confirmedReminderEmail` longtext,
  `welcomeEmail` longtext,
  `confirmationEmailSubject` varchar(255) DEFAULT NULL,
  `confirmedReminderEmailSubject` varchar(255) DEFAULT NULL,
  `welcomeEmailSubject` varchar(255) DEFAULT NULL,
  `actionPlanInviteEmailSubject` varchar(255) DEFAULT NULL,
  `passwordResetEmail` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `Pages` DISABLE KEYS */;

INSERT INTO `Pages` (`id`, `confirmationEmail`, `actionPlanInviteEmail`, `confirmedReminderEmail`, `welcomeEmail`, `confirmationEmailSubject`, `confirmedReminderEmailSubject`, `welcomeEmailSubject`, `actionPlanInviteEmailSubject`, `passwordResetEmail`)
VALUES
	(1,'<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], someone (presumably you) signed up to play <i>[$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>Please follow <a href=\'[$CONFIRMLINK$]\'>this link to confirm your registration</a>  ([$CONFIRMLINK$]).</p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] trouble report</a> page at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>, or by email to <br /><a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>','<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], you were invited to become a (co-) author of the following ActionPlan:<br/><center>[$ACTIONPLANTITLE$]</center></p><p>The creation of successful action plans from card chains is the ultimate goal of the grand <i>mmowgli</i> experiment.  You receive points for participating in action plan, so you are encouraged to participate.  Click the <i>Take Action</i> button in the <i>mmowgli</i> page header to view the list of plans on the <i>Action Dashboard</i>.  Find the correct plan and follow the directions.</p><p><i>mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer to receive no email messages during <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the appropriate box.</p><p>Your User Profile page is available to you by clicking your game name in the <i>mmowgli</i> page header.</p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>MMOWGLI Trouble Report</a> page at [$TROUBLELINK$].</p><p>Thanks for playing [$GAMEACRONYM$] mmowgli.</p>','<p>Your [$GAMEACRONYM$] [$GAMEHANDLE$] account has been confirmed.  You are welcome to play the game.<br/>[$GAMEURL$]</p><p>If you receive this message in error, indicating, perhaps, that someone has gained entry by pretending to use your account, please notify us immediately via a Trouble Report.</br>[$TROUBLELINK$]</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>','<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], you enrolled a new player name [$UNAME$] in <i>[$GAMENAME$]</i> at <a href=\'[$GAMEURL$]\'>[$GAMEURL$]</a>, and we&apos;re glad to have you.</p><p>If this enrollment was in error, or your email address was somehow used by someone else without your permission, please notify us at <a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a> and we will take corrective action.  You can also submit a Trouble Report at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>.</p><p><i>[$GAMEACRONYM$] mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer to receive no email messages from <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the appropriate box.</p><p>When logged in, your User Profile page is available to you by clicking your game name in the <i>[$GAMEACRONYM$] mmowgli</i> page header.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>MMOWGLI Portal</a>.</p><p>\"How to Play\" tips can be found on the <a href=\'[$HOWTOPLAYURL$]\'>Game Instructions</a> page.</p><p>Problems may always be reported with the <a href=\'[$TROUBLELINK$]\'>MMOWGLI Trouble Report</a> at [$TROUBLELINK$].</p><p>Thanks for your interest in playing [$GAMEACRONYM$] mmowgli.  Play the game, change the game!</p>','[$GAMEHANDLE$]: Signup Confirmation','[$GAMEHANDLE$]: Account Creation Confirmation','Your [$GAMEHANDLE$] registration','[$GAMEHANDLE$]: Invitation to author Action Plan','<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], someone (presumably you) requested a password reset for<i> [$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>Please follow <a href=\'[$CONFIRMLINK$]\'>this link to complete your password reset request</a>.</p><br /><p>Please note that this process will expire within three hours after this email was generated.</p><p>If you have not changed your password within this timeframe, you will have to re-initiate this process from the <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i> login in screen.<br /></p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] trouble report</a> page at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>, or by email to <br /><a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>');

/*!40000 ALTER TABLE `Pages` ENABLE KEYS */;


# Dump of table PasswordReset
# ------------------------------------------------------------

DROP TABLE IF EXISTS `PasswordReset`;

CREATE TABLE `PasswordReset` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `resetCode` varchar(255) DEFAULT NULL,
  `creationDate` timestamp NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expireDate` timestamp NOT NULL DEFAULT '1970-01-01 00:00:00',
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9F2FE1D186C88E77` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Query2
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Query2`;

CREATE TABLE `Query2` (
  `email` varchar(255) DEFAULT '',
  `background` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `digest` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `digestKey` (`digest`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Query2Pii
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Query2Pii`;

CREATE TABLE `Query2Pii` (
  `email` varchar(255) DEFAULT '',
  `background` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `digest` varchar(32) NOT NULL DEFAULT '',
  `invited` bit(1) NOT NULL DEFAULT b'0',
  `confirmed` bit(1) NOT NULL DEFAULT b'0',
  `ingame` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `digestKey` (`digest`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;



# Dump of table Resume
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Resume`;

CREATE TABLE `Resume` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `level` tinyblob,
  `move` tinyblob,
  `points` bigint(20) NOT NULL,
  `power` bigint(20) NOT NULL,
  `user` tinyblob,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Resume_Award
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Resume_Award`;

CREATE TABLE `Resume_Award` (
  `Resume_id` bigint(20) NOT NULL,
  `awards_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Resume_id`,`awards_id`),
  UNIQUE KEY `awards_id` (`awards_id`),
  KEY `FK750373CB705CE7F7` (`Resume_id`),
  KEY `FK750373CBAC2415A4` (`awards_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Role
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Role`;

CREATE TABLE `Role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table Turn
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Turn`;

CREATE TABLE `Turn` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `turnNumber` int(11) NOT NULL,
  `turnStartTime` datetime DEFAULT NULL,
  `turnStopTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User`;

CREATE TABLE `User` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `administrator` bit(1) NOT NULL,
  `affiliation` varchar(255) DEFAULT NULL,
  `answer` varchar(255) DEFAULT NULL,
  `basicScore` float NOT NULL,
  `designer` bit(1) NOT NULL DEFAULT b'0',
  `registeredInMove_id` bigint(20) DEFAULT NULL,
  `expertise` varchar(255) DEFAULT NULL,
  `facebookId` varchar(255) DEFAULT NULL,
  `firstChildEmailSent` bit(1) NOT NULL,
  `gameMaster` bit(1) NOT NULL,
  `innovationScore` float NOT NULL,
  `linkedInId` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `okEmail` bit(1) NOT NULL,
  `okGameMessages` bit(1) NOT NULL,
  `okSurvey` bit(1) NOT NULL,
  `online` bit(1) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `realFirstName` varchar(255) DEFAULT NULL,
  `realLastName` varchar(255) DEFAULT NULL,
  `registerDate` datetime DEFAULT NULL,
  `tweeter` bit(1) NOT NULL,
  `twitterId` varchar(255) DEFAULT NULL,
  `userName` varchar(255) DEFAULT NULL,
  `avatar_id` bigint(20) DEFAULT NULL,
  `level_id` bigint(20) DEFAULT NULL,
  `question_id` bigint(20) DEFAULT NULL,
  `role_id` bigint(20) DEFAULT NULL,
  `accountDisabled` bit(1) NOT NULL,
  `emailConfirmed` bit(1) NOT NULL DEFAULT b'0',
  `welcomeEmailSent` bit(1) NOT NULL DEFAULT b'0',
  `viewOnly` bit(1) NOT NULL DEFAULT b'0',
  `basicScoreMove1` float NOT NULL DEFAULT '0',
  `basicScoreMove2` float NOT NULL DEFAULT '0',
  `basicScoreMove3` float NOT NULL DEFAULT '0',
  `basicScoreMove4` float NOT NULL DEFAULT '0',
  `basicScoreMove5` float NOT NULL DEFAULT '0',
  `innovScoreMove1` float NOT NULL DEFAULT '0',
  `innovScoreMove2` float NOT NULL DEFAULT '0',
  `innovScoreMove3` float NOT NULL DEFAULT '0',
  `innovScoreMove4` float NOT NULL DEFAULT '0',
  `innovScoreMove5` float NOT NULL DEFAULT '0',
  `basicScoreMove10` float NOT NULL DEFAULT '0',
  `basicScoreMove11` float NOT NULL DEFAULT '0',
  `basicScoreMove12` float NOT NULL DEFAULT '0',
  `basicScoreMove13` float NOT NULL DEFAULT '0',
  `basicScoreMove14` float NOT NULL DEFAULT '0',
  `basicScoreMove15` float NOT NULL DEFAULT '0',
  `basicScoreMove16` float NOT NULL DEFAULT '0',
  `basicScoreMove6` float NOT NULL DEFAULT '0',
  `basicScoreMove7` float NOT NULL DEFAULT '0',
  `basicScoreMove8` float NOT NULL DEFAULT '0',
  `basicScoreMove9` float NOT NULL DEFAULT '0',
  `innovScoreMove10` float NOT NULL DEFAULT '0',
  `innovScoreMove11` float NOT NULL DEFAULT '0',
  `innovScoreMove12` float NOT NULL DEFAULT '0',
  `innovScoreMove13` float NOT NULL DEFAULT '0',
  `innovScoreMove14` float NOT NULL DEFAULT '0',
  `innovScoreMove15` float NOT NULL DEFAULT '0',
  `innovScoreMove16` float NOT NULL DEFAULT '0',
  `innovScoreMove6` float NOT NULL DEFAULT '0',
  `innovScoreMove7` float NOT NULL DEFAULT '0',
  `innovScoreMove8` float NOT NULL DEFAULT '0',
  `innovScoreMove9` float NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `userName` (`userName`),
  KEY `FK285FEBE19DCA97` (`role_id`),
  KEY `FK285FEBE293723D` (`level_id`),
  KEY `FK285FEBA7224877` (`avatar_id`),
  KEY `FK285FEBE29785A9` (`question_id`),
  KEY `FK285FEBD0DE2BB0` (`registeredInMove_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User` DISABLE KEYS */;

INSERT INTO `User` (`id`, `administrator`, `affiliation`, `answer`, `basicScore`, `designer`, `registeredInMove_id`, `expertise`, `facebookId`, `firstChildEmailSent`, `gameMaster`, `innovationScore`, `linkedInId`, `location`, `okEmail`, `okGameMessages`, `okSurvey`, `online`, `password`, `realFirstName`, `realLastName`, `registerDate`, `tweeter`, `twitterId`, `userName`, `avatar_id`, `level_id`, `question_id`, `role_id`, `accountDisabled`, `emailConfirmed`, `welcomeEmailSent`, `viewOnly`, `basicScoreMove1`, `basicScoreMove2`, `basicScoreMove3`, `basicScoreMove4`, `basicScoreMove5`, `innovScoreMove1`, `innovScoreMove2`, `innovScoreMove3`, `innovScoreMove4`, `innovScoreMove5`, `basicScoreMove10`, `basicScoreMove11`, `basicScoreMove12`, `basicScoreMove13`, `basicScoreMove14`, `basicScoreMove15`, `basicScoreMove16`, `basicScoreMove6`, `basicScoreMove7`, `basicScoreMove8`, `basicScoreMove9`, `innovScoreMove10`, `innovScoreMove11`, `innovScoreMove12`, `innovScoreMove13`, `innovScoreMove14`, `innovScoreMove15`, `innovScoreMove16`, `innovScoreMove6`, `innovScoreMove7`, `innovScoreMove8`, `innovScoreMove9`)
VALUES
	(1,00000001,NULL,NULL,0,00000001,1,'expertise',NULL,00000001,00000001,0,NULL,NULL,00000001,00000001,00000000,00000000,NULL,NULL,NULL,NULL,00000000,NULL,'Administrator',1,6,6,NULL,00000001,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(2,00000000,NULL,NULL,0,00000001,1,'expertise',NULL,00000001,00000001,0,NULL,NULL,00000001,00000001,00000000,00000000,NULL,NULL,NULL,NULL,00000000,NULL,'SeedCard',1,6,6,NULL,00000000,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(3,00000000,NULL,NULL,0,00000000,1,'expertise',NULL,00000000,00000000,0,NULL,NULL,00000001,00000001,00000000,00000000,NULL,NULL,NULL,NULL,00000000,NULL,'Guest',1,6,6,NULL,00000000,00000001,00000001,00000001,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(4,00000000,NULL,NULL,0,00000001,1,'expertise',NULL,00000000,00000001,0,NULL,NULL,00000001,00000001,00000000,00000000,NULL,NULL,NULL,NULL,00000000,NULL,'GameMaster',1,6,6,NULL,00000001,00000001,00000000,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(5,00000001,'U.S. Navy','semper mmowgli',0,00000001,1,'Game design, 3D modeling, web standards',NULL,00000001,00000001,0,NULL,'Monterey California',00000001,00000001,00000001,00000000,NULL,NULL,NULL,'2013-04-26 15:08:29',00000001,NULL,'gm_donb',8,1,6,NULL,00000000,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(6,00000001,'','',0,00000001,1,'stuff',NULL,00000000,00000001,0,NULL,'',00000001,00000001,00000001,00000000,NULL,NULL,NULL,'2013-04-26 15:48:21',00000000,NULL,'gm_donm',1,1,6,NULL,00000000,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(7,00000001,'Academia','That Mmowgli runs without crashing',0,00000001,1,'Mmowgli developer',NULL,00000000,00000001,0,NULL,'Naval Postgraduate School, Monterey CA, USA',00000001,00000001,00000001,00000000,NULL,NULL,NULL,'2013-04-26 15:50:55',00000000,NULL,'gm_mike',1,1,6,NULL,00000000,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0),
	(8,00000001,'Academia','My hope for 2021 is that everyone has experienced a MMOWGLI game ;)',0,00000001,1,'MMOWGLI expert',NULL,00000001,00000001,0,NULL,'Norfolk, VA',00000001,00000001,00000001,00000000,NULL,NULL,NULL,'2013-04-29 11:26:54',00000001,NULL,'gm_becca',4,1,6,NULL,00000000,00000001,00000001,00000000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);

/*!40000 ALTER TABLE `User` ENABLE KEYS */;


# Dump of table User_ActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_ActionPlan`;

CREATE TABLE `User_ActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlansInvited_id` bigint(20) NOT NULL,
  `actionPlansInvestedIn_id` bigint(20) NOT NULL,
  `actionPlansFollowing_id` bigint(20) NOT NULL,
  `actionPlansAuthored_id` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlansAuthored_id`),
  KEY `FKF958B3737AB1D3AF` (`actionPlansInvited_id`),
  KEY `FKF958B373BDD926D9` (`actionPlansFollowing_id`),
  KEY `FKF958B3734DAE78F8` (`actionPlansAuthored_id`),
  KEY `FKF958B37386C88E77` (`User_id`),
  KEY `FKF958B3736EB0389` (`actionPlansInvestedIn_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_AuthoredPlans
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_AuthoredPlans`;

CREATE TABLE `User_AuthoredPlans` (
  `user_id` bigint(20) NOT NULL,
  `actionplan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`actionplan_id`),
  KEY `FK3740AFAC86C88E77` (`user_id`),
  KEY `FK3740AFACCA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_AuthoredPlans` DISABLE KEYS */;

INSERT INTO `User_AuthoredPlans` (`user_id`, `actionplan_id`)
VALUES
	(5,1),
	(5,2),
	(6,2),
	(8,2);

/*!40000 ALTER TABLE `User_AuthoredPlans` ENABLE KEYS */;


# Dump of table User_Award
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_Award`;

CREATE TABLE `User_Award` (
  `User_id` bigint(20) NOT NULL,
  `awards_id` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`awards_id`),
  UNIQUE KEY `awards_id` (`awards_id`),
  KEY `FKE76570E9AC2415A4` (`awards_id`),
  KEY `FKE76570E986C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_Badges
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_Badges`;

CREATE TABLE `User_Badges` (
  `user_id` bigint(20) NOT NULL,
  `badge_pk` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`badge_pk`),
  KEY `FK5C8B42486C88E77` (`user_id`),
  KEY `FK5C8B424E1018ABD` (`badge_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_Badges` DISABLE KEYS */;

INSERT INTO `User_Badges` (`user_id`, `badge_pk`)
VALUES
	(1,1),
	(1,6),
	(2,6),
	(5,6),
	(8,6);

/*!40000 ALTER TABLE `User_Badges` ENABLE KEYS */;


# Dump of table User_Email
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_Email`;

CREATE TABLE `User_Email` (
  `User_id` bigint(20) NOT NULL,
  `emailAddresses_id` bigint(20) NOT NULL,
  UNIQUE KEY `emailAddresses_id` (`emailAddresses_id`),
  KEY `FKE79942289ED8A713` (`emailAddresses_id`),
  KEY `FKE799422886C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_FavoriteCards
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_FavoriteCards`;

CREATE TABLE `User_FavoriteCards` (
  `user_id` bigint(20) NOT NULL,
  `card_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`card_id`),
  KEY `FK4E1379B386C88E77` (`user_id`),
  KEY `FK4E1379B3B0898C57` (`card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_FollowedPlans
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_FollowedPlans`;

CREATE TABLE `User_FollowedPlans` (
  `user_id` bigint(20) NOT NULL,
  `actionplan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`actionplan_id`),
  KEY `FKE30BFEC686C88E77` (`user_id`),
  KEY `FKE30BFEC6CA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_gameMessages
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_gameMessages`;

CREATE TABLE `User_gameMessages` (
  `user_id` bigint(20) NOT NULL,
  `message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`message_id`),
  UNIQUE KEY `message_id` (`message_id`),
  KEY `FK95971F3286C88E77` (`user_id`),
  KEY `FK95971F324F28CA9D` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_ImplAuthorScoreByActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_ImplAuthorScoreByActionPlan`;

CREATE TABLE `User_ImplAuthorScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanAuthorScores` double DEFAULT NULL,
  `actionPlanAuthorScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanAuthorScores_KEY`),
  KEY `FKA12BEE9306A36C7` (`actionPlanAuthorScores_KEY`),
  KEY `FKA12BEE986C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_ImplAuthorScoreByActionPlan` DISABLE KEYS */;

INSERT INTO `User_ImplAuthorScoreByActionPlan` (`User_id`, `actionPlanAuthorScores`, `actionPlanAuthorScores_KEY`)
VALUES
	(1,0,1),
	(2,0,1),
	(3,0,1),
	(5,0,1),
	(5,0,2),
	(6,0,2),
	(8,0,2);

/*!40000 ALTER TABLE `User_ImplAuthorScoreByActionPlan` ENABLE KEYS */;


# Dump of table User_ImplCommentScoreByActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_ImplCommentScoreByActionPlan`;

CREATE TABLE `User_ImplCommentScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanCommentScores` double DEFAULT NULL,
  `actionPlanCommentScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanCommentScores_KEY`),
  KEY `FK5E92EF7D86C88E77` (`User_id`),
  KEY `FK5E92EF7DEEAD55BD` (`actionPlanCommentScores_KEY`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_ImplRatedScoreByActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_ImplRatedScoreByActionPlan`;

CREATE TABLE `User_ImplRatedScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanRatedScores` double DEFAULT NULL,
  `actionPlanRatedScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanRatedScores_KEY`),
  KEY `FK722F59F8EF4B38E2` (`actionPlanRatedScores_KEY`),
  KEY `FK722F59F886C88E77` (`User_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table User_ImplThumbScoreByActionPlan
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_ImplThumbScoreByActionPlan`;

CREATE TABLE `User_ImplThumbScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanThumbScores` double DEFAULT NULL,
  `actionPlanThumbScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanThumbScores_KEY`),
  KEY `FKA33AA5E6D2595CB4` (`actionPlanThumbScores_KEY`),
  KEY `FKA33AA5E686C88E77` (`User_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;



# Dump of table User_InnovationByMove
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_InnovationByMove`;

CREATE TABLE `User_InnovationByMove` (
  `User_id` bigint(20) NOT NULL,
  `innovationByMove` float DEFAULT NULL,
  `innovationByMove_KEY` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`User_id`,`innovationByMove_KEY`),
  KEY `FKF492248186C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_InnovationByMove` DISABLE KEYS */;

INSERT INTO `User_InnovationByMove` (`User_id`, `innovationByMove`, `innovationByMove_KEY`)
VALUES
	(1,0,1),
	(2,0,1),
	(3,0,1),
	(4,0,1),
	(5,0,1),
	(6,0,1),
	(7,0,1),
	(8,0,1);

/*!40000 ALTER TABLE `User_InnovationByMove` ENABLE KEYS */;


# Dump of table User_InvestedPlans
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_InvestedPlans`;

CREATE TABLE `User_InvestedPlans` (
  `user_id` bigint(20) NOT NULL,
  `actionplan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`actionplan_id`),
  KEY `FKE33E860286C88E77` (`user_id`),
  KEY `FKE33E8602CA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_InvitedPlans
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_InvitedPlans`;

CREATE TABLE `User_InvitedPlans` (
  `user_id` bigint(20) NOT NULL,
  `actionplan_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`actionplan_id`),
  KEY `FKDDD660A386C88E77` (`user_id`),
  KEY `FKDDD660A3CA276057` (`actionplan_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_IsFollowing
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_IsFollowing`;

CREATE TABLE `User_IsFollowing` (
  `user_id` bigint(20) NOT NULL,
  `is_following_user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`is_following_user_id`),
  KEY `FK481312133DBDC6F4` (`is_following_user_id`),
  KEY `FK4813121386C88E77` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table User_LevelByMove
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_LevelByMove`;

CREATE TABLE `User_LevelByMove` (
  `User_id` bigint(20) NOT NULL,
  `levelByMove` bigint(20) DEFAULT NULL,
  `levelByMove_KEY` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`User_id`,`levelByMove_KEY`),
  KEY `FK21946AF886C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_LevelByMove` DISABLE KEYS */;

INSERT INTO `User_LevelByMove` (`User_id`, `levelByMove`, `levelByMove_KEY`)
VALUES
	(1,1,1),
	(2,1,1),
	(3,1,1),
	(4,1,1),
	(5,1,1),
	(6,1,1),
	(7,1,1),
	(8,1,1);

/*!40000 ALTER TABLE `User_LevelByMove` ENABLE KEYS */;


# Dump of table User_PointsByMove
# ------------------------------------------------------------

DROP TABLE IF EXISTS `User_PointsByMove`;

CREATE TABLE `User_PointsByMove` (
  `User_id` bigint(20) NOT NULL,
  `pointsByMove` float DEFAULT NULL,
  `pointsByMove_KEY` bigint(20) NOT NULL DEFAULT '0',
  PRIMARY KEY (`User_id`,`pointsByMove_KEY`),
  KEY `FKD2F85F7F86C88E77` (`User_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40000 ALTER TABLE `User_PointsByMove` DISABLE KEYS */;

INSERT INTO `User_PointsByMove` (`User_id`, `pointsByMove`, `pointsByMove_KEY`)
VALUES
	(1,0,1),
	(2,0,1),
	(3,0,1),
	(4,0,1),
	(5,0,1),
	(6,0,1),
	(7,0,1),
	(8,0,1);

/*!40000 ALTER TABLE `User_PointsByMove` ENABLE KEYS */;


# Dump of table UserPii
# ------------------------------------------------------------

DROP TABLE IF EXISTS `UserPii`;

CREATE TABLE `UserPii` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `userObjectId` bigint(20) DEFAULT NULL,
  `realFirstName` varchar(255) DEFAULT NULL,
  `realLastName` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `facebookId` varchar(255) DEFAULT NULL,
  `linkedInId` varchar(255) DEFAULT NULL,
  `twitterId` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `UserPii` DISABLE KEYS */;

INSERT INTO `UserPii` (`id`, `userObjectId`, `realFirstName`, `realLastName`, `password`, `facebookId`, `linkedInId`, `twitterId`)
VALUES
	(1,1,'acFjF0lpEb8Wg5gDQJo5do9LIKamWpTL','WE7y1SeczFx8+CArRqYJOz2+FuN65cSQ','cE4IEl4XXfojra1FyUOOve4slw9XWRU8yxDYZ+BtVsw82M09+1aRsp4pE8DR8FFi4fX+H4r63rkhGSxYcVj+DFVJLvHmdYNAHccbXL8SoDU=',NULL,NULL,NULL),
	(2,2,'zo4rOwEVqnKWbEryLynYiPy4edK0Z3r1','tlzIXNysi3tN2Ol07+WKHo9g0l7y82Z/','LYzzItZimd7UCbr5r1/xFV0p7PyCrfIPPA/gW6f0D3vaPWCDx5p10doJ4w4IVYCdPMwp2RkBM667utwrjsGxbLstFmiONapv4rEk+39yeTY=',NULL,NULL,NULL),
	(3,3,'NYfftsa1uoWZ+dOHLjNV6w==','okw2wBom53MJp5C4UorHxQ==','uxZTEaOyxcjEk/v2vGc/euD5yhKSGd7cuRxf2bkQ5IHRkMGCMBfGG+4EOgkO4AGIMI9vCpJIeuD27JfQPvH1p5ZVbr0SWPMUff6is/iP4Io=',NULL,NULL,NULL),
	(4,4,'dFd36X2YF+T7h5UXGFJAI53s/bjpMXq4','zwF3aQwkrWyBAK8CEL/3421mfQF9BooX','UnJd21sG8VAtz0LWbdj5x+O795XzIDJ4ZVfEnd46z1/E+LXBckbWTNZQZNPaWafNkoMdREjHAtUd/BSRGHvN+CYmMwQBYQ/g3G2EyqRKgfc=',NULL,NULL,NULL),
	(5,5,'kPijYn+zQcAubGRbJHHsCQ==','47wxH/fqpgwF97rLortgDP5JFkk79/GR','ZNRAncA26948/K5bxmGURkrdRoYvwNyHDoZ1UZc/OKfqlaJJRR/D5HOZ9N2dlp/hdhT4SQ5q9qO5BibNQdf1AnVkh8edvGZv2V9U8Fcwrnc=',NULL,NULL,NULL),
	(6,6,'1BYQMKMeRxPr40jYZnCTgg==','uyURvbofHVKQPu6Iz5xKwCjk2kwREtAv','bL9tqDq+rsSOjB0AGD9fiXSWdZs0Vi4/38BRuDqitdlAy0l+Fcv3FDA7vTtfIpDqQquILa5o3vstgrwl4cPicuzccIO+l/SI06D5K1at7GE=',NULL,NULL,NULL),
	(7,7,'HXoUr4apMMD6UoezjGZZHw==','gkff+xNNJSzuc5DYCaFt0A==','7obN3PCzG16ZQ33clHuyWVYdRaGxU2vEByY0WYnMFsjW6+SKx7PKCL8wqQgwNguYdaGUpfrnf26gtaQmunRgNkDgx2HZ1FCZq1Mp5YNBftU=',NULL,NULL,NULL),
	(8,8,'rgy4h2EBQJxbkeM7YSceYg==','x92TbFSeaqkVueh0AvhHSQ==','xvYH6KEOfLpZMhGk0FFh4AFz5XVNkuBwnHvlQQlJ1Zauro4LDivV4qc5yZ6LXRsPnBTzZNpozwScSlBUgijW1lmIuzae784Yp/s/nM920EI=',NULL,NULL,NULL);

/*!40000 ALTER TABLE `UserPii` ENABLE KEYS */;


# Dump of table UserPii_EmailPii
# ------------------------------------------------------------

DROP TABLE IF EXISTS `UserPii_EmailPii`;

CREATE TABLE `UserPii_EmailPii` (
  `UserPii_id` bigint(20) NOT NULL,
  `emailAddresses_id` bigint(20) NOT NULL,
  UNIQUE KEY `emailAddresses_id` (`emailAddresses_id`),
  KEY `FKE79942289ED8A713` (`emailAddresses_id`),
  KEY `FKE799422886C88E77` (`UserPii_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

/*!40000 ALTER TABLE `UserPii_EmailPii` DISABLE KEYS */;

INSERT INTO `UserPii_EmailPii` (`UserPii_id`, `emailAddresses_id`)
VALUES
	(4,4),
	(3,3),
	(2,2),
	(1,1),
	(5,5),
	(6,6),
	(7,7),
	(8,8);

/*!40000 ALTER TABLE `UserPii_EmailPii` ENABLE KEYS */;


# Dump of table Vip
# ------------------------------------------------------------

DROP TABLE IF EXISTS `Vip`;

CREATE TABLE `Vip` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `entry` varchar(255) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table VipPii
# ------------------------------------------------------------

DROP TABLE IF EXISTS `VipPii`;

CREATE TABLE `VipPii` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `entry` varchar(255) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


UPDATE `Game` SET `version` = '20150116' WHERE `id` = '1';
ALTER TABLE `Card` ADD `version` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `version` BIGINT(20)  NOT NULL  DEFAULT '0';

UPDATE `Game` SET `version` = '20150206' WHERE `id` = '1';
ALTER TABLE `ActionPlan` DROP `version`;
ALTER TABLE `ActionPlan` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `Card` CHANGE `version` `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `Game` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0'  AFTER `id`;
ALTER TABLE `Move` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `MovePhase` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `User` CHANGE `version` `revision` BIGINT(20)  NOT NULL  DEFAULT '0';


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
