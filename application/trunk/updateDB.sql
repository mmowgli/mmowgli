# This is a list of SQL commands which may be useful to update old game databases. This file is not intended to be run in its entirety.
# Remember to also update the MmowgliConstants field DATABASE_VERSION

UPDATE `Game` SET `version` = '20150529' WHERE `id` = '1';
ALTER TABLE `Game` ADD `requireCACregistration` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `reportsShowHiddenCards`;
ALTER TABLE `Game` ADD `enforceCACdataRegistration` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `requireCACregistration`;
ALTER TABLE `Game` ADD `requireCAClogin` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `enforceCACdataRegistration`;
ALTER TABLE `Game` ADD `useCAClogin` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `requireCAClogin`;
ALTER TABLE `User` ADD `cacId` VARCHAR(255)  NULL  DEFAULT NULL  AFTER `mapZoom`;

UPDATE `Game` SET `version` = '20150504' WHERE `id` = '1';
ALTER TABLE `Media` ADD `width` BIGINT(20)  NULL  AFTER `alternateUrl`;
ALTER TABLE `Media` ADD `height` BIGINT(20)  NULL  DEFAULT NULL  AFTER `width`;

UPDATE `Game` SET `version` = '20150429' WHERE `id` = '1';
ALTER TABLE `Image` ADD `width` INT(11)  NOT NULL  DEFAULT '0'  AFTER `description`;
ALTER TABLE `Image` ADD `height` INT(11)  NOT NULL  DEFAULT '0'  AFTER `width`;

UPDATE `Game` SET `version` = '20150420' WHERE `id` = '1';
ALTER TABLE `Game` ADD `reportsShowHiddenCards` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `gameHandle`;

UPDATE `Game` SET `version` = '20150417' WHERE `id` = '1';
UPDATE `CardMarking` SET `description` = 'No more children', `label` = 'No More Children' WHERE `id` = '2';

UPDATE `Game` SET `version` = '20150403' WHERE `id` = '1';
ALTER TABLE `Game` ADD `mapLayersCSV` VARCHAR(511)  NOT NULL  DEFAULT 'ESRI_WORLD_IMAGERY'  AFTER `mapZoom`;

UPDATE `Game` SET `version` = '20150325' WHERE `id` = '1';
ALTER TABLE `GameLinks` ADD `videosLink` VARCHAR(255)  NOT NULL  DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Video+Resources'  AFTER `userAgreementLink`;
ALTER TABLE `User` ADD `mapCenterLatitude` FLOAT  NULL  DEFAULT NULL  AFTER `revision`;
ALTER TABLE `User` ADD `mapCenterLongitude` FLOAT  NULL  DEFAULT NULL  AFTER `mapCenterLatitude`;
ALTER TABLE `User` ADD `mapZoom` INT  NULL  DEFAULT NULL  AFTER `mapCenterLongitude`;
CREATE TABLE `User_ActiveMapLayers` (
  `User_id` bigint(20) NOT NULL,
  `activeMapLayers` varchar(255) NOT NULL DEFAULT ''
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE `Game` SET `version` = '20150225' WHERE `id` = '1';
CREATE TABLE `Image` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL DEFAULT '',
  `mimeType` varchar(255) NOT NULL DEFAULT '',
  `bytes` longblob,
  `description` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE `Game` SET `version` = '20150206' WHERE `id` = '1';
ALTER TABLE `ActionPlan` DROP `version`;
ALTER TABLE `ActionPlan` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `Card` CHANGE `version` `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `Game` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0'  AFTER `id`;
ALTER TABLE `Move` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `MovePhase` ADD `revision` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `User` CHANGE `version` `revision` BIGINT(20)  NOT NULL  DEFAULT '0';

UPDATE `Game` SET `version` = '20150116' WHERE `id` = '1';
ALTER TABLE `Card` ADD `version` BIGINT(20)  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `version` BIGINT(20)  NOT NULL  DEFAULT '0';

UPDATE `Game` SET `version` = '20141114' WHERE `id` = '1';
ALTER TABLE `MovePhase` ADD `actionPlanWhoIsInvolvedHeader` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT 'Who is involved?';
ALTER TABLE `MovePhase` ADD `actionPlanWhatIsItHeader` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT 'What is it?'  AFTER `actionPlanWhoIsInvolvedHeader`;
ALTER TABLE `MovePhase` ADD `actionPlanWhatWillItTakeHeader` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT 'What will it take?'  AFTER `actionPlanWhatIsItHeader`;
ALTER TABLE `MovePhase` ADD `actionPlanHowWillItWorkHeader` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT 'How will it work?'  AFTER `actionPlanWhatWillItTakeHeader`;
ALTER TABLE `MovePhase` ADD `actionPlanHowWillItChangeHeader` VARCHAR(255)  CHARACTER SET utf8  COLLATE utf8_general_ci  NOT NULL  DEFAULT 'How will it change the situation?'  AFTER `actionPlanHowWillItWorkHeader`;
UPDATE `MovePhase` SET `actionPlanWhoIsInvolvedHeader` = 'Who is involved?';
UPDATE `MovePhase` SET `actionPlanWhatIsItHeader` = 'What is it?';
UPDATE `MovePhase` SET `actionPlanWhatWillItTakeHeader` = 'What will it take?';
UPDATE `MovePhase` SET `actionPlanHowWillItWorkHeader` = 'How will it work?';
UPDATE `MovePhase` SET `actionPlanHowWillItChangeHeader` = 'How will it change the situation?';

UPDATE `Game` SET `version` = '20140509' WHERE `id` = '1';
ALTER TABLE `Game` ADD `mapLatitude` DOUBLE  NOT NULL  DEFAULT '36.610902'  AFTER `mapTitle`;
ALTER TABLE `Game` ADD `mapLongitude` DOUBLE  NOT NULL  DEFAULT '-121.8674989' AFTER `mapLatitude`;
ALTER TABLE `Game` ADD `mapZoom`  INT(11)  NOT NULL  DEFAULT '13'  AFTER `mapLongitude`;

UPDATE `Game` SET `version` = '20140324' WHERE `id` = '1';
ALTER TABLE `GameEvent` CHANGE `description` `description` LONGTEXT  CHARACTER SET utf8  NULL;

UPDATE `Game` SET `version` = '20140317' WHERE `id` = '1';
UPDATE `Pages` SET `passwordResetEmail` = '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet.\"</p><p>At [$DATETIME$], someone (presumably you) requested a password reset for <i>[$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>In order to ensure the security of your account, please <a href=\'[$CONFIRMLINK$]\'>confirm your password reset request</a>.</p><br /><p>This process only remains active for three hours. If you have not changed your password within this time frame, then you will have to re-initiate this process from the <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i> login in screen.<br /><!-- TODO add link --></p><p>You can report any problems by sending a <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] Trouble Report</a> at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>.</p><p>You can also send trouble-report email directly to <br /><a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>' WHERE `id` = '1';
ALTER TABLE `Pages` ADD `passwordResetEmailSubject` varchar(255);
UPDATE `Pages` SET `passwordResetEmailSubject` = 'Your [$GAMEHANDLE$] password reset confirmation' WHERE `id` = '1';
ALTER TABLE `Pages` ADD `gameMasterRegistrationEmail` LONGTEXT  NULL;
UPDATE `Pages` SET `gameMasterRegistrationEmail` = '<p>Greetings all <i>mmowgli / [$GAMENAME$]</i> Administrators>/p><p>At [$DATETIME$], a new Game Master, [$UNAME$], completed registration for [$GAMENAME$].</p><p>Please log on to <a href=\'[$GAMEURL$]\'>[$GAMEURL$]</a> at your convenience and enable all pertinent permissions for GM [$UNAME$] so that they may commence building [$GAMENAME$] for play.</p>' WHERE `id` = '1';
ALTER TABLE `Pages` ADD `gameMasterRegistrationEmailSubject` varchar(255);
UPDATE `Pages` SET `gameMasterRegistrationEmailSubject` = 'A new Game Master has registered for [$GAMEHANDLE$]' WHERE `id` = '1';

UPDATE `Game` SET `version` = '20140129' WHERE `id` = '1';
ALTER TABLE `Pages` ADD `passwordResetEmail` LONGTEXT  NULL;
UPDATE `Pages` SET `passwordResetEmail` = '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], someone (presumably you) requested a password reset for<i> [$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>Please follow <a href=\'[$CONFIRMLINK$]\'>this link to complete your password reset request</a>.</p><br /><p>Please note that this process will expire within three hours after this email was generated.</p><p>If you have not changed your password within this timeframe, you will have to re-initiate this process from the <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i> login in screen.<br /></p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] trouble report</a> page at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>, or by email to <br /><a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>' WHERE `id` = '1';
UPDATE `Pages` SET `confirmationEmail` = '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], someone (presumably you) signed up to play <i>[$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>Please follow <a href=\'[$CONFIRMLINK$]\'>this link to confirm your registration</a>.</p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] trouble report</a> page at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>, or by email to <br /><a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>' WHERE `id` = '1';
UPDATE `Pages` SET `actionPlanInviteEmail` = '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], you were invited to become a (co-) author of the following ActionPlan:<br/><center>[$ACTIONPLANTITLE$]</center></p><p>The creation of successful action plans from card chains is the ultimate goal of the grand <i>mmowgli</i> experiment.  You receive points for participating in action plan, so you are encouraged to participate.  Click the <i>Take Action</i> button in the <i>mmowgli</i> page header to view the list of plans on the <i>Action Dashboard</i>.  Find the correct plan and follow the directions.</p><p><i>mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer to receive no email messages during <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the appropriate box.</p><p>Your User Profile page is available to you by clicking your game name in the <i>mmowgli</i> page header.</p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>MMOWGLI Trouble Report</a> page at [$TROUBLELINK$].</p><p>Thanks for playing [$GAMEACRONYM$] mmowgli.</p>' WHERE `id` = '1';
UPDATE `Pages` SET `welcomeEmail` = '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] mmowgli</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], you enrolled a new player name [$UNAME$] in <i>[$GAMENAME$]</i> at <a href=\'[$GAMEURL$]\'>[$GAMEURL$]</a>, and we&apos;re glad to have you.</p><p>If this enrollment was in error, or your email address was somehow used by someone else without your permission, please notify us at <a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a> and we will take corrective action.  You can also submit a Trouble Report at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>.</p><p><i>[$GAMEACRONYM$] mmowgli</i> periodically sends mail to users notifying them of important game events.  Other players may also send you direct messages, but this is through the game interface and your email address is always kept private.  If you prefer to receive no email messages from <i>mmowgli</i> game play, you may opt out by visiting your User Profile page and checking the appropriate box.</p><p>When logged in, your User Profile page is available to you by clicking your game name in the <i>[$GAMEACRONYM$] mmowgli</i> page header.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>MMOWGLI Portal</a>.</p><p>"How to Play" tips can be found on the <a href=\'[$HOWTOPLAYURL$]\'>Game Instructions</a> page.</p><p>Problems may always be reported with the <a href=\'[$TROUBLELINK$]\'>MMOWGLI Trouble Report</a> at [$TROUBLELINK$].</p><p>Thanks for your interest in playing [$GAMEACRONYM$] mmowgli.  Play the game, change the game!</p>' WHERE `id` = '1';
UPDATE `Pages` SET `actionPlanInviteEmailSubject` = '[$GAMEHANDLE$]: Invitation to author Action Plan' WHERE `id` = '1';
UPDATE `Pages` SET `confirmationEmailSubject` = '[$GAMEHANDLE$]: Signup Confirmation' WHERE `id` = '1';
UPDATE `Pages` SET `confirmedReminderEmailSubject` = '[$GAMEHANDLE$]: Account Creation Confirmation' WHERE `id` = '1';
UPDATE `Pages` SET `welcomeEmailSubject` = 'Your [$GAMEHANDLE$] registration' WHERE `id` = '1';

CREATE TABLE `PasswordReset` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `resetCode` varchar(255) DEFAULT NULL,
  `creationDate` timestamp NOT NULL DEFAULT '1970-01-01 00:00:00',
  `expireDate` timestamp NOT NULL DEFAULT '1970-01-01 00:00:00',
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9F2FE1D186C88E77` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

UPDATE `Game` SET `version` = '20140116' WHERE `id` = '1';
ALTER TABLE `Pages` ADD `welcomeEmail` LONGTEXT;
ALTER TABLE `Pages` ADD `confirmationEmailSubject` varchar(255);
ALTER TABLE `Pages` ADD `confirmedReminderEmailSubject` varchar(255);
ALTER TABLE `Pages` ADD `welcomeEmailSubject` varchar(255);
ALTER TABLE `Pages` ADD `actionPlanInviteEmailSubject` varchar(255);

UPDATE `Game` SET `version` = '20131206' WHERE `id` = '1';
ALTER TABLE `GameEvent` CHANGE `description` `description` VARCHAR(511)  CHARACTER SET utf8  NULL  DEFAULT NULL;
ALTER TABLE `MessageUrl` CHANGE `url` `url` VARCHAR(511)  CHARACTER SET utf8  COLLATE utf8_general_ci  NULL  DEFAULT NULL;

UPDATE `Game` SET `version` = '20131119' WHERE `id` = '1';
CREATE TABLE `GameLinks` (
  `id` bigint(11) unsigned NOT NULL AUTO_INCREMENT,
  `aboutLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/About%20MMOWGLI',
  `actionPlanRequestLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/action-plan-request',
  `blogLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-blogs',
  `creditsLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Credits%20and%20Contact',
  `faqLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/faq',
  `fixesLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/fixes',
  `fouoLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/fouo',
  `gameFromEmail` varchar(255) NOT NULL DEFAULT 'mmowgli@nps.navy.mil',
  `gameFullLink` varchar(255) NOT NULL DEFAULT 'http://mmowgli.nps.edu/pleaseWaitGameFull.html',
  `gameHomeUrl` varchar(255) NOT NULL DEFAULT 'mmowgli.nps.edu',
  `glossaryLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary',
  `howToPlayLink` varchar(255) DEFAULT NULL,
  `improveScoreLink` varchar(255) NOT NULL DEFAULT 'https://portal.mmowgli.nps.edu/instructions',
  `informedConsentLink` varchar(255) NOT NULL DEFAULT 'http://web.mmowgli.nps.edu/mmowMedia/MmowgliGameParticipantInformedConsent.html',
  `learnMoreLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/instructions',
  `mmowgliMapLink` varchar(255) NOT NULL DEFAULT 'http://maps.google.com/maps/ms?hl=en&amp;ie=UTF8&amp;t=h&amp;msa=0&amp;msid=&amp;ll=38.895111,-77.036667&amp;spn=15.060443,18.676758&amp;z=6&amp;output=embed',
  `surveyConsentLink` varchar(255) NOT NULL DEFAULT 'https://movesinstitute.org/mmowMedia/MMOWGLI-AnonymousSurveyConsentUnsigned2013January25.pdf',
  `termsLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms%20and%20Conditions',
  `thanksForInterestLink` varchar(255) NOT NULL DEFAULT 'http://mmowgli.nps.edu/thanksForInterest.html',
  `thanksForPlayingLink` varchar(255) NOT NULL DEFAULT 'http://mmowgli.nps.edu/thanksForPlaying.html',
  `troubleLink` varchar(255) NOT NULL DEFAULT 'http://portal.mmowgli.nps.edu/trouble',
  `troubleMailto` varchar(255) NOT NULL DEFAULT 'mmowgli-trouble@movesinstitute.org?subject=Problem%20creating%20new%20account',
  `userAgreementLink` varchar(255) NOT NULL DEFAULT 'http://www.defense.gov/socialmedia/user-agreement.aspx',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
INSERT INTO `GameLinks` (`id`, `aboutLink`, `actionPlanRequestLink`, `blogLink`, `creditsLink`, `faqLink`, `fixesLink`, `fouoLink`, `gameFromEmail`, `gameFullLink`, `gameHomeUrl`, `glossaryLink`, `howToPlayLink`, `improveScoreLink`, `informedConsentLink`, `learnMoreLink`, `mmowgliMapLink`, `surveyConsentLink`, `termsLink`, `thanksForInterestLink`, `thanksForPlayingLink`, `troubleLink`, `troubleMailto`, `userAgreementLink`)
VALUES
	(1,'http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/About%20MMOWGLI','http://portal.mmowgli.nps.edu/action-plan-request','https://portal.mmowgli.nps.edu/game-blogs','http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Credits%20and%20Contact','http://portal.mmowgli.nps.edu/faq','https://portal.mmowgli.nps.edu/fixes','https://portal.mmowgli.nps.edu/fouo','mmowgli@nps.navy.mil','http://mmowgli.nps.edu/pleaseWaitGameFull.html','mmowgli.nps.edu','https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Glossary',NULL,'https://portal.mmowgli.nps.edu/instructions','http://web.mmowgli.nps.edu/mmowMedia/MmowgliGameParticipantInformedConsent.html','http://portal.mmowgli.nps.edu/instructions','http://maps.google.com/maps/ms?hl=en&amp;ie=UTF8&amp;t=h&amp;msa=0&amp;msid=&amp;ll=38.895111,-77.036667&amp;spn=15.060443,18.676758&amp;z=6&amp;output=embed','https://movesinstitute.org/mmowMedia/MMOWGLI-AnonymousSurveyConsentUnsigned2013January25.pdf','http://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms%20and%20Conditions','http://mmowgli.nps.edu/thanksForInterest.html','http://mmowgli.nps.edu/thanksForPlaying.html','http://portal.mmowgli.nps.edu/trouble','mmowgli-trouble@movesinstitute.org?subject=Problem%20creating%20new%20account','http://www.defense.gov/socialmedia/user-agreement.aspx');

ALTER TABLE `Game` DROP `aboutLink`;
ALTER TABLE `Game` DROP `actionPlanRequestLink`;
ALTER TABLE `Game` DROP `blogLink`;
ALTER TABLE `Game` DROP `creditsLink`;
ALTER TABLE `Game` DROP `faqLink`;
ALTER TABLE `Game` DROP `fouoLink`;
ALTER TABLE `Game` DROP `gameFullLink`;
ALTER TABLE `Game` DROP `gameHomeUrl`;
ALTER TABLE `Game` DROP `improveScoreLink`;
ALTER TABLE `Game` DROP `informedConsentLink`;
ALTER TABLE `Game` DROP `learnMoreLink`;
ALTER TABLE `Game` DROP `mmowgliMapLink`;
ALTER TABLE `Game` DROP `surveyConsentLink`;
ALTER TABLE `Game` DROP `termsLink`;
ALTER TABLE `Game` DROP `thanksForInterestLink`;
ALTER TABLE `Game` DROP `thanksForPlayingLink`;
ALTER TABLE `Game` DROP `troubleLink`;
ALTER TABLE `Game` DROP `userAgreementLink`;

ALTER TABLE `Game` ADD `headerBannerImage` VARCHAR(255)  NULL  DEFAULT 'mmowgliBanner350w130h.png';
ALTER TABLE `Game` ADD `actionPlansEnabled` BIT(1)  NOT NULL  DEFAULT 1;
ALTER TABLE `Game` ADD `showHeaderBranding` BIT(1)  NOT NULL  DEFAULT 1;
ALTER TABLE `Game` ADD `playIdeaButtonImage` VARCHAR(255)  NULL  DEFAULT 'playIdeaButt124w18h.png';
ALTER TABLE `Game` ADD `gameHandle` VARCHAR(255)  NOT NULL  DEFAULT 'mmowgli';

CREATE TABLE `Pages` (id BIGINT(20) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT)  ENGINE = `InnoDB`;
ALTER TABLE `Pages` ADD `confirmationEmail` LONGTEXT;
ALTER TABLE `Pages` ADD `actionPlanInviteEmail` LONGTEXT;
ALTER TABLE `Pages` ADD `confirmedReminderEmail` LONGTEXT;
INSERT INTO `Pages` (`id`, `confirmationEmail`, `actionPlanInviteEmail`, `confirmedReminderEmail`) VALUES ('1', '<p>Greetings, <b>[$UNAME$]</b>, from <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>, the \"Massively Multiplayer Online War Game Leveraging the Internet\".</p><p>At [$DATETIME$], someone (presumably you) signed up to play <i>[$GAMEHANDLE$] / [$GAMENAME$]</i>.</p><p>Please follow <a href=\'[$CONFIRMLINK$]\'>this link to confirm your registration</a>.</p><p>Problems may always be reported on the <a href=\'[$TROUBLELINK$]\'>[$GAMEHANDLE$] trouble report</a> page at <a href=\'[$TROUBLELINK$]\'>[$TROUBLELINK$]</a>, or by email to <a href=\'mailto:[$TROUBLEMAILTO$]\'>[$TROUBLEMAILTO$]</a>.</p><p>More information is also available on the <a href=\'[$PORTALLINK$]\'>[$GAMEHANDLE$] portal</a>.</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>', 'tbd', '<p>Your [$GAMEACRONYM$] [$GAMEHANDLE$] account has been confirmed.  You are welcome to play the game.<br/>[$GAMEURL$]</p><p>If you receive this message in error, indicating, perhaps, that someone has gained entry by pretending to use your account, please notify us immediately via a Trouble Report.</br>[$TROUBLELINK$]</p><p>Thanks for your interest in playing <i>[$GAMEACRONYM$] [$GAMEHANDLE$]</i>.  Play the game, change the game!</p>');

UPDATE `Game` SET `version` = '20131112' WHERE `id` = '1';
ALTER TABLE `MovePhase` ADD `signupHeaderImage` VARCHAR(255)  NULL  DEFAULT 'mmowgli_logo_final.png';

UPDATE `Game` SET `version` = '20130828' WHERE `id` = '1';
# may have to manually adjust the MovePhase_CardType table
CREATE TABLE `User_ImplRatedScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanRatedScores` double DEFAULT NULL,
  `actionPlanRatedScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanRatedScores_KEY`),
  KEY `FK722F59F8EF4B38E2` (`actionPlanRatedScores_KEY`),
  KEY `FK722F59F886C88E77` (`User_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `User_ImplThumbScoreByActionPlan` (
  `User_id` bigint(20) NOT NULL,
  `actionPlanThumbScores` double DEFAULT NULL,
  `actionPlanThumbScores_KEY` bigint(20) NOT NULL,
  PRIMARY KEY (`User_id`,`actionPlanThumbScores_KEY`),
  KEY `FKA33AA5E6D2595CB4` (`actionPlanThumbScores_KEY`),
  KEY `FKA33AA5E686C88E77` (`User_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

ALTER TABLE `User` DROP `gamemasterPoints`;
ALTER TABLE `User` DROP `innovationTarget`;

ALTER TABLE `Game` ADD `userSignupAnswerPoints` FLOAT(1)  NOT NULL  DEFAULT '10.0'  AFTER `inGameMailEnabled`;
ALTER TABLE `Game` ADD `userActionPlanCommentPoints` FLOAT(1)  NOT NULL  DEFAULT '1.0'  AFTER `userSignupAnswerPoints`;
ALTER TABLE `Game` ADD `cardSuperInterestingPoints` FLOAT(1)  NOT NULL  DEFAULT '5.0'  AFTER `userActionPlanCommentPoints`;
ALTER TABLE `Game` ADD `actionPlanCommentPoints` FLOAT(1)  NOT NULL  DEFAULT '3.0'  AFTER `cardSuperInterestingPoints`;
ALTER TABLE `Game` ADD `actionPlanThumbFactor` FLOAT(1)  NOT NULL  DEFAULT '1.0'  AFTER `actionPlanCommentPoints`;
ALTER TABLE `Game` ADD `actionPlanAuthorPoints` FLOAT(1)  NOT NULL  DEFAULT '100.0'  AFTER `actionPlanThumbFactor`;
ALTER TABLE `Game` ADD `actionPlanSuperInterestingPoints` FLOAT(1)  NOT NULL  DEFAULT '12.0'  AFTER `actionPlanAuthorPoints`;
ALTER TABLE `Game` ADD `actionPlanRaterPoints` FLOAT(1)  NOT NULL  DEFAULT '5.0'  AFTER `actionPlanSuperInterestingPoints`;
ALTER TABLE `Game` ADD `cardAncestorPoints` FLOAT(1)  NOT NULL  DEFAULT '1.0'  AFTER `actionPlanRaterPoints`;
ALTER TABLE `Game` ADD `cardAuthorPoints` FLOAT(1)  NOT NULL  DEFAULT '7.0'  AFTER `cardAncestorPoints`;
ALTER TABLE `Game` ADD `cardAncestorPointsGenerationFactors` VARCHAR(255)  NOT NULL  DEFAULT '2.0 1.8 1.6 1.4 1.2 1.0 0.8'  AFTER `cardAuthorPoints`;


UPDATE `Game` SET `version` = '20130723' WHERE `id` = '1';
ALTER TABLE `MovePhase` DROP `registeredLogonsOnly`;
ALTER TABLE `MovePhase` DROP `newUserPermissions`;

UPDATE `Game` SET `version` = '20130711' WHERE `id` = '1';
ALTER TABLE `Media` ADD `alternateUrl` LONGTEXT  NULL;

# After updating for version 20130626
# set programatically ... UPDATE `Game` SET `version` = '20130627' WHERE `id` = '1';

UPDATE `Game` SET `version` = '20130626' WHERE `id` = '1';
ALTER TABLE `Query2Pii` ADD `invited` BIT(1)  NOT NULL  DEFAULT 0;
ALTER TABLE `Query2Pii` ADD `confirmed` BIT(1)  NOT NULL  DEFAULT 0;
ALTER TABLE `Query2Pii` ADD `ingame` BIT(1)  NULL  DEFAULT NULL;

ALTER TABLE `EmailPii` ADD `digest` varchar(32) NOT NULL DEFAULT '';
ALTER TABLE `EmailPii` ADD KEY `digestKey` (`digest`);

UPDATE `Game` SET `version` = '20130415' WHERE `id` = '1';
ALTER TABLE `Game` ADD `adminLoginMessage` LONGTEXT  NULL;

UPDATE `Game` SET `version` = '20130410' WHERE `id` = '1';
ALTER TABLE `Game` ADD `bootStrapping` BIT(1)  NOT NULL  DEFAULT b'0';
ALTER TABLE `Game` ADD `externalMailEnabled` BIT(1) NOT NULL DEFAULT b'1';
ALTER TABLE `Game` ADD `inGameMailEnabled` BIT(1) NOT NULL DEFAULT b'1';

UPDATE `Game` SET `version` = '20130329' WHERE `id` = '1';
ALTER TABLE `Move` DROP `preMove_id`;
ALTER TABLE `Move` DROP `inMove_id`;
ALTER TABLE `Move` DROP `postMove_id`;
ALTER TABLE `Move` DROP `callToActionBackgroundLinks`;
ALTER TABLE `Move` DROP `currentPhase`;
ALTER TABLE `Move` ADD `currentMovePhase_id` BIGINT(20) DEFAULT NULL;
ALTER TABLE `Move` ADD INDEX `FK24AFF1E69C1AD6` (`currentMovePhase_id`);

ALTER TABLE `Game` DROP `loginPermissions`;
ALTER TABLE `Game` DROP `newUserPermissions`;
ALTER TABLE `Game` DROP `registeredLogonsOnly`;
ALTER TABLE `Game` DROP `restrictByQueryList`;
ALTER TABLE `MovePhase` ADD `loginPermissions` smallint(6) NOT NULL DEFAULT '-1';
ALTER TABLE `MovePhase` ADD `newUserPermissions` smallint(6) NOT NULL DEFAULT '0';
ALTER TABLE `MovePhase` ADD `registeredLogonsOnly` bit(1) NOT NULL DEFAULT b'0';
ALTER TABLE `MovePhase` ADD `restrictByQueryList` bit(1) NOT NULL DEFAULT b'0';

CREATE TABLE `Move_MovePhase` (
  `Move_id` bigint(20) NOT NULL,
  `movePhases_id` bigint(20) NOT NULL,
  UNIQUE KEY `movePhases_id` (`movePhases_id`),
  KEY `FK22BE93CD9A948B7` (`Move_id`),
  KEY `FK22BE93CD657495E` (`movePhases_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Check the existing db for the applicability of the following
INSERT INTO `Move_MovePhase` (`Move_id`, `movePhases_id`) VALUES ('1', '1');
INSERT INTO `Move_MovePhase` (`Move_id`, `movePhases_id`) VALUES ('1', '2');
INSERT INTO `Move_MovePhase` (`Move_id`, `movePhases_id`) VALUES ('1', '3');

# These might be useful
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '1');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '2');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '3');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '4');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '5');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('3', '6');

INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '1');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '2');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '3');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '4');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '5');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('4', '6');

INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '1');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '2');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '3');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '4');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '5');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('5', '6');

INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '1');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '2');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '3');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '4');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '5');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('6', '6');
# thats it for the iffy stuff

UPDATE `MovePhase` SET `description` = 'PREPARE' WHERE `id` = '1';
UPDATE `MovePhase` SET `description` = 'PLAY' WHERE `id` = '2';
UPDATE `MovePhase` SET `description` = 'PUBLISH' WHERE `id` = '3';

ALTER Table `User` ADD `basicScoreMove10` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove11` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove12` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove13` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove14` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove15` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove16` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove6` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove7` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove8` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `basicScoreMove9` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove10` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove11` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove12` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove13` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove14` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove15` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove16` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove6` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove7` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove8` float NOT NULL DEFAULT '0';
ALTER Table `User` ADD `innovScoreMove9` float NOT NULL DEFAULT '0';


UPDATE `Game` SET `version` = '20130314' WHERE `id` = '1';
ALTER TABLE `Game` ADD `pdfAvailable` BIT(1)  NOT NULL  DEFAULT 0;
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`)
VALUES (400, 'MMOWGLI Orientation', 'Default orientation video', 'default orientation video handle', 0, 3, 'MMOWGLI Orientation ', 3, 'BtTGOxCHcD0');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`)
VALUES (165, 'MMOWGLI Call to Action', 'Default call-to-action video', 'default call-to-action video handle', 0, 3, NULL, 3, 'nW4LnDuh8Go');

# commands to update to multi-move scoring
UPDATE User, User_PointsByMove SET User.`basicScoreMove1` =
User_PointsByMove.`pointsByMove` WHERE
User.`id` = User_PointsByMove.`User_id` AND
User_PointsByMove.`pointsByMove_KEY` = '1';

UPDATE User, User_PointsByMove SET User.`basicScoreMove2` =
User_PointsByMove.`pointsByMove` WHERE
User.`id` = User_PointsByMove.`User_id` AND
User_PointsByMove.`pointsByMove_KEY` = '2';

UPDATE User, User_PointsByMove SET User.`basicScoreMove3` =
User_PointsByMove.`pointsByMove` WHERE
User.`id` = User_PointsByMove.`User_id` AND
User_PointsByMove.`pointsByMove_KEY` = '3';

UPDATE User, User_InnovationByMove SET User.`innovScoreMove1` =
User_InnovationByMove.`innovationByMove` WHERE
User.`id` = User_InnovationByMove.`User_id` AND
User_InnovationByMove.`innovationByMove_KEY` = '1';

UPDATE User, User_InnovationByMove SET User.`innovScoreMove2` =
User_InnovationByMove.`innovationByMove` WHERE
User.`id` = User_InnovationByMove.`User_id` AND
User_InnovationByMove.`innovationByMove_KEY` = '2';

UPDATE User, User_InnovationByMove SET User.`innovScoreMove3` =
User_InnovationByMove.`innovationByMove` WHERE
User.`id` = User_InnovationByMove.`User_id` AND
User_InnovationByMove.`innovationByMove_KEY` = '3';

UPDATE `Game` SET `version` = '20130227' WHERE `id` = '1';

UPDATE `Game` SET `surveyConsentLink` = 'https://movesinstitute.org/mmowMedia/MMOWGLI-AnonymousSurveyConsentUnsigned2013January25.pdf' WHERE `id` = '1';
UPDATE `User` SET `okSurvey` = b'1';

UPDATE `CardMarking` SET `description` = 'Use sparingly' WHERE `id` = '1';
UPDATE `CardMarking` SET `description` = 'Doesn\'t lead anwhere useful' WHERE `id` = '4';
UPDATE `CardMarking` SET `description` = 'Self-evident' WHERE `id` = '5';
UPDATE `CardMarking` SET `description` = 'Bad behavior or sensitive information' WHERE `id` = '6';

INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('131', 'award1', 'green achievement award', 'award1', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/achievement55w55h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('132', 'award2', 'blue commendation award', 'award2', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/commendation55w55h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('133', 'award3', 'red legion award', 'award3', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/legion55w55h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('134', 'award1_300', 'green achievement award', 'award1_300', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/achievement300w300h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('135', 'award2_300', 'blue commendation award', 'award2_300', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/commendation300w300h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('136', 'award3_300', 'red legion award', 'award3_300', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/legion300w300h.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('137', 'cert_55', 'mmowgli certificate', 'cert_55', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png');
INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES ('138', 'cert_300', 'mmowgli certificate', 'cert_300', b'0', '3', NULL, '0', 'https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked300x300.png');

ALTER TABLE `Award` ADD `storyUrl` VARCHAR(255)  NOT NULL  DEFAULT '';
ALTER TABLE `AwardType` ADD `icon300x300_id` BIGINT(20)  DEFAULT NULL;
ALTER TABLE `AwardType` ADD `icon55x55_id` BIGINT(20)  DEFAULT NULL;
ALTER TABLE `AwardType` ADD KEY `FKC27A271744F82A2` (`icon55x55_id`);
ALTER TABLE `AwardType` ADD KEY `FKC27A2717A9FB652C` (`icon300x300_id`);
INSERT INTO `AwardType` (`id`, `basicValue`, `description`, `name`, `powerValue`, `icon300x300_id`, `icon55x55_id`) VALUES ('1', '0', 'MMOWGLI Achievement Medal (MAM)', 'MMOWGLI Achievement Medal (MAM)', '0', '134', '131');
INSERT INTO `AwardType` (`id`, `basicValue`, `description`, `name`, `powerValue`, `icon300x300_id`, `icon55x55_id`) VALUES ('2', '0', 'MMOWGLI Commendation Medal (MCM)', 'MMOWGLI Commendation Medal (MCM)', '0', '135', '132');
INSERT INTO `AwardType` (`id`, `basicValue`, `description`, `name`, `powerValue`, `icon300x300_id`, `icon55x55_id`) VALUES ('3', '0', 'The Legion de MMOWGLI', 'The Legion de MMOWGLI', '0', '136', '133');

# INSERT INTO `Award` (`id`, `timeAwarded`, `awardType_id`, `awardedBy_id`, `awardedTo_id`, `move_id`, `storyUrl`)
# VALUES
#(1,'2013-02-05 10:00:00',8,  5, 35, 1,'https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1'),
#(2,'2013-02-05 10:00:00',10, 5, 35, 1,'https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1'),
#(3,'2013-02-05 10:00:00',10, 5, 70, 1,'https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1'),
#(4,`2013-03-26 17:00:00`,10, 5, 86, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(5,`2013-03-26 17:00:00`,10, 5, 31, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(6,`2013-03-26 17:00:00`,10, 5, 29, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(7,`2013-03-26 17:00:00`,10, 5, 30, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(8,`2013-03-26 17:00:00`,10, 5, 52, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(9,`2013-03-26 17:00:00`,10, 5, 18, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`),
#(10,`2013-03-26 17:00:00`,10,5, 61, 1,`https://portal.mmowgli.nps.edu/bii-blog/-/blogs/player-awards-for-bii-game-round-1`);


# INSERT INTO `User_Award` (`User_id`, `awards_id`) VALUES ('23', '1');
# INSERT INTO `User_Award` (`User_id`, `awards_id`) VALUES ('23', '2');
# INSERT INTO `User_Award` (`User_id`, `awards_id`) VALUES ('23', '3');

ALTER TABLE `User` ADD `basicScoreMove1` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `basicScoreMove2` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `basicScoreMove3` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `basicScoreMove4` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `basicScoreMove5` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `innovScoreMove1` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `innovScoreMove2` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `innovScoreMove3` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `innovScoreMove4` FLOAT  NOT NULL  DEFAULT '0';
ALTER TABLE `User` ADD `innovScoreMove5` FLOAT  NOT NULL  DEFAULT '0';

UPDATE `Game` SET `version` = '20130225' WHERE `id` = '1';
ALTER TABLE `Message` ADD `createdInMove_id` BIGINT(20) NULL DEFAULT '1';
ALTER TABLE `Message` ADD INDEX `FK9C2397E7AF29DE0A` (`createdInMove_id`);

UPDATE `Game` SET `version` = '20130222' WHERE `id` = '1';
ALTER TABLE `Game` ADD `loginPermissions` SMALLINT NOT NULL DEFAULT '-1';
ALTER TABLE `Game` ADD `newUserPermissions` SMALLINT NOT NULL DEFAULT '0';
ALTER TABLE `ActionPlan` ADD `superInteresting` BIT(1)  NOT NULL  DEFAULT b'0';
ALTER TABLE `Message` ADD `superInteresting` BIT(1)  NOT NULL  DEFAULT b'0';

UPDATE `Game` SET `version` = '20130220' WHERE `id` = '1';
ALTER TABLE `ActionPlan` ADD `idForSorting` BIGINT(20)  NOT NULL  AFTER `id`;
UPDATE `ActionPlan` SET `idForSorting` = `id`;

# Game gets updated to 20130215 after run
# 14 Feb 2013 (multimoves)
# use tool to copy movephase 1 to movephase 2
# use tool to copy move 1 to move 2, number = 2

UPDATE `Game` SET `version` = '20130214' WHERE `id` = '1';
UPDATE `Card` SET `createdInMove_id` = '1' WHERE `createdInMove_id` IS NULL;
ALTER TABLE `Move` ADD `showMoveBranding` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `postMove_id`;
ALTER TABLE `Move` ADD `name` VARCHAR(255)  NULL  DEFAULT NULL  AFTER `showMoveBranding`;
ALTER TABLE `ActionPlan` ADD `creationDate` DATETIME NOT NULL DEFAULT '1970-01-01 00:00:00' AFTER `quickAuthorList`;
# UPDATE `ActionPlan` SET `creationDate` = '1970-01-01 00:00:00';
ALTER TABLE `ActionPlan` ADD `createdInMove_id` BIGINT(20)  NULL  DEFAULT NULL  AFTER `creationDate`;
ALTER TABLE `ActionPlan` ADD INDEX `FKE5314E9FAF29DE0A` (`createdInMove_id`);
ALTER TABLE `User` ADD `registeredInMove_id` BIGINT(20)  NULL  DEFAULT NULL  AFTER `designer`;
ALTER TABLE `User` ADD INDEX `FK285FEBD0DE2BB0` (`registeredInMove_id`);

UPDATE `User` SET `registeredInMove_id` = '1';
UPDATE `ActionPlan` set `createdInMove_id` = '1';

ALTER TABLE `Game` ADD `lastMove_id` BIGINT(20) NULL DEFAULT NULL AFTER `currentMove_id`;
ALTER TABLE `Game` ADD INDEX `FK21C012E6BEC161` (`lastMove_id`);
UPDATE `Game` SET `lastMove_id` = '1';

ALTER TABLE `Game` ADD `showPriorMovesCards` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `fouoDescription`;
ALTER TABLE `Game` ADD `showPriorMovesActionPlans` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `showPriorMovesCards`;
ALTER TABLE `Game` ADD `playOnPriorMovesCards` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `showPriorMovesActionPlans`;
ALTER TABLE `Game` ADD `editPriorMovesActionPlans` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `playOnPriorMovesCards`;
UPDATE `Move` SET `preMove_id` = '2', `inMove_id` = '2', `postMove_id` = '2', `name` = 'Round 2' WHERE `id` = '2';
INSERT INTO `CardType` (`id`, `cardClass`, `ideaCard`, `descendantOrdinal`, `prompt`, `summaryHeader`, `title`, `titleAlternate`, `cssColorStyle`, `cssLightColorStyle`) VALUES ('7', '0', b'1', NULL, 'Round 2 positive idea type', 'RND2-POS', 'RND-2POS', NULL, 'm-purple', 'm-purple-light');
INSERT INTO `CardType` (`id`, `cardClass`, `ideaCard`, `descendantOrdinal`, `prompt`, `summaryHeader`, `title`, `titleAlternate`, `cssColorStyle`, `cssLightColorStyle`) VALUES ('8', '1', b'1', NULL, 'Round 2 neg Idea', 'RND2-NEG', 'r2-neg', NULL, 'm-green', 'm-green-light');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '7');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '8');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '3');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '4');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '5');
INSERT INTO `MovePhase_CardType` (`MovePhase_id`, `allowedCards_id`) VALUES ('2', '6');

# 30 Jan 2013
UPDATE `Game` SET `version` = '20130130' WHERE `id` = '1';
ALTER TABLE `Game` ADD `showFouo` BIT(1)  NOT NULL  DEFAULT b'0'  AFTER `secondLoginPermissionPageText`;
ALTER TABLE `Game` ADD `fouoLink` VARCHAR(255)  NULL  DEFAULT NULL  AFTER `showFouo`;
ALTER TABLE `Game` ADD `fouoDescription` VARCHAR(255)  NOT NULL  DEFAULT ''  AFTER `fouoLink`;
ALTER TABLE `Game` ADD `fouoButtonImage` VARCHAR(255)  NULL  DEFAULT NULL  AFTER `fouoDescription`;
UPDATE `Game` SET `fouoDescription` = 'Unclassified -- For Official Use Only' WHERE `id` = '1';
UPDATE `Game` SET `fouoLink` = 'https://portal.mmowgli.nps.edu/fouo' WHERE `id` = '1';
UPDATE `Game` SET `fouoButtonImage` = 'fouo250w36h.png' WHERE `id` = '1';

# 24 Jan 2013
UPDATE `Game` SET `version` = '20130124' WHERE `id` = '1';
ALTER TABLE `MovePhase` ADD `signupPageEnabled` BIT(1)  NOT NULL  DEFAULT 0;
ALTER TABLE `MovePhase` ADD `signupButtonEnabled` BIT(1)  NOT NULL  DEFAULT 0;
ALTER TABLE `MovePhase` ADD `signupButtonShow` bit(1) NOT NULL DEFAULT b'0';
ALTER TABLE `MovePhase` ADD `signupButtonIcon` varchar(255) NOT NULL DEFAULT ' ';
ALTER TABLE `MovePhase` ADD `signupButtonSubText` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `signupButtonToolTip` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `newButtonEnabled` bit(1) NOT NULL DEFAULT b'1';
ALTER TABLE `MovePhase` ADD `newButtonShow` bit(1) NOT NULL DEFAULT b'1';
ALTER TABLE `MovePhase` ADD `newButtonIcon` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `newButtonSubText` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `newButtonToolTip` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `loginButtonEnabled` bit(1) NOT NULL DEFAULT b'1';
ALTER TABLE `MovePhase` ADD `loginButtonShow` bit(1) NOT NULL DEFAULT b'1';
ALTER TABLE `MovePhase` ADD `loginButtonIcon` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `loginButtonSubText` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `loginButtonToolTip` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `guestButtonEnabled` bit(1) NOT NULL DEFAULT b'0';
ALTER TABLE `MovePhase` ADD `guestButtonShow` bit(1) NOT NULL DEFAULT b'0';
ALTER TABLE `MovePhase` ADD `guestButtonIcon` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `guestButtonSubText` varchar(255) NOT NULL DEFAULT '';
ALTER TABLE `MovePhase` ADD `guestButtonToolTip` varchar(255) NOT NULL DEFAULT '';
UPDATE `MovePhase` SET `signupPageEnabled` = b'0' WHERE `id` = '1';
UPDATE `MovePhase` SET `signupButtonEnabled` = b'0' WHERE `id` = '1';
UPDATE `MovePhase` SET `signupButtonShow` = b'0'  WHERE `id` = '1';
UPDATE `MovePhase` SET `signupButtonIcon` = 'tellMeMore130w15h.png' WHERE `id` = '1';
UPDATE `MovePhase` SET `signupButtonSubText` = 'Signup for email notification' WHERE `id` = '1';
UPDATE `MovePhase` SET `signupButtonToolTip` = '' WHERE `id` = '1';
UPDATE `MovePhase` SET `newButtonEnabled` = b'1' WHERE `id` = '1';
UPDATE `MovePhase` SET `newButtonShow` = b'1'  WHERE `id` = '1';
UPDATE `MovePhase` SET `newButtonIcon` = 'imNewButton202w22h.png' WHERE `id` = '1';
UPDATE `MovePhase` SET `newButtonSubText` = 'You can get started in 2 minutes...' WHERE `id` = '1';
UPDATE `MovePhase` SET `newButtonToolTip` = ''  WHERE `id` = '1';
UPDATE `MovePhase` SET `loginButtonEnabled` = b'1' WHERE `id` = '1';
UPDATE `MovePhase` SET `loginButtonShow` = b'1' WHERE `id` = '1';
UPDATE `MovePhase` SET `loginButtonIcon` = 'imRegisteredButton133w24h.png' WHERE `id` = '1';
UPDATE `MovePhase` SET `loginButtonSubText` = 'Rejoin the action now!' WHERE `id` = '1';
UPDATE `MovePhase` SET `loginButtonToolTip` = '' WHERE `id` = '1';
UPDATE `MovePhase` SET `guestButtonEnabled` = b'0' WHERE `id` = '1';
UPDATE `MovePhase` SET `guestButtonShow` = b'0'  WHERE `id` = '1';
UPDATE `MovePhase` SET `guestButtonIcon` = 'guestLogin97w24h.png'  WHERE `id` = '1';
UPDATE `MovePhase` SET `guestButtonSubText` = 'Look around a bit before you join' WHERE `id` = '1';
UPDATE `MovePhase` SET `guestButtonToolTip` = '' WHERE `id` = '1';

ALTER TABLE `Game` ADD `secondLoginPermissionPage` BIT(1)  NOT NULL  DEFAULT 0;
ALTER TABLE `Game` ADD `secondLoginPermissionPageTitle` VARCHAR(255)  NOT NULL  DEFAULT '';
ALTER TABLE `Game` ADD `secondLoginPermissionPageText` LONGTEXT  NOT NULL  DEFAULT '';
UPDATE `Game` SET `secondLoginPermissionPageTitle` = 'User-configurable title' where `id` = 1;
UPDATE `Game` SET `secondLoginPermissionPageText` = 'User-configurable text' where `id` = 1;

# 3 Jan, 2013
ALTER TABLE `MovePhase` ADD `signupText` LONGTEXT  NULL AFTER `windowTitle`;
UPDATE `MovePhase` SET `signupText` = '<img src=\'http://www.defense.gov/multimedia/web_graphics/navy/USNc.jpg\' height=\'25\' width=\'25\'/><u>(Game designers edit this field.)</u> This <i><b>mmowgli</b></i> game is being readied for play.  To receive an email message when the game is open, please fill out the following information and click \"register\".' WHERE `id` = '1';
UPDATE `Game` SET `version` = '20130103' WHERE `id` = '1';

# 13 Nov, 2012
ALTER TABLE `Game` ADD `acronym` VARCHAR(255)  NULL  DEFAULT NULL  AFTER `title`;
UPDATE `Game` SET `version` = '20121113' WHERE `id` = '1';
# 8 Nov, 2012
ALTER TABLE `Game` ADD `reportIntervalMinutes` BIGINT(20)  NOT NULL  DEFAULT '0'  AFTER `emailConfirmation`;
UPDATE `Game` SET `version` = '20121108' WHERE `id` = '1';

# 6 Nov, 2012
ALTER TABLE `User` ADD `designer` BIT(1)  NOT NULL DEFAULT b'0';
UPDATE `Game` SET `version` = '20121106' WHERE `id` = '1';

# 11 Sep, 2012
ALTER TABLE `Game` ADD `emailConfirmation` BIT(1)  NOT NULL  DEFAULT b'0';
ALTER TABLE `User` ADD `emailConfirmed` BIT(1)  NOT NULL  DEFAULT b'0';
ALTER TABLE `User` ADD `welcomeEmailSent` BIT(1)  NOT NULL  DEFAULT b'0';
UPDATE `User` SET `emailConfirmed` = b'1';
UPDATE `User` SET `welcomeEmailSent` = b'1';
UPDATE `Game` SET `version` = '20120911' WHERE `id` = '1';
CREATE TABLE `EmailConfirmation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `confirmationCode` varchar(255) DEFAULT NULL,
  `creationDate` datetime DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9F2FE1D186C88E77` (`user_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
# 24 July, 2012
ALTER TABLE User ADD viewOnly bit(1) NOT NULL DEFAULT b'0';

# 18 May, 2012
ALTER TABLE `GoogleMapMarker` CHANGE `popupContent` `popupContent` LONGTEXT  NULL;

# May 8, 2012
CREATE TABLE `Vip` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `entry` varchar(255) NOT NULL DEFAULT '',
  `type` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `Vip` (`id`, `entry`, `type`)
VALUES
	(1,'.mil',0),
	(2,'nps.edu',0),
	(3,'.gov',0),
	(4,'iftf.org',0),
	(5,'oneearthfuture.org',0),
	(6,'nato.int',0),
	(7,'cimicweb.org',0),
	(8,'gatech.edu',0),
	(9,'fred@flintstones.org',1);

# May 8, 2012
ALTER TABLE MessageUrl ADD tooltip varchar(255) DEFAULT NULL;

# May 4, 2012
ALTER TABLE Game ADD linkRegexs longblob DEFAULT NULL;
# Put in the piracy 2011* regex strings
UPDATE Game set linkRegexs = X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A6088220300007870770400000001737200276564752E6E70732E6D6F7665732E6D6D6F77676C692E64622E47616D652452656765785061697200000000000000010200024C000572656765787400124C6A6176612F6C616E672F537472696E673B4C000B7265706C6163656D656E7471007E0003787074002047616D6520323031312E285C642920416374696F6E20506C616E20285C642B2974006D3C6120687265663D2268747470733A2F2F7765622E6D6D6F77676C692E6E70732E6564752F7069726163792F416374696F6E506C616E4C697374323031312E24312E68746D6C23416374696F6E506C616E243222207461726765743D22617077696E646F77223E24303C2F613E78';

# May 3, 2012
ALTER TABLE Game ADD topCardsReadonly bit(1) NOT NULL DEFAULT 0;
# previous:

ALTER TABLE ActionPlan ADD helpWanted longtext;
ALTER TABLE ActionPlan ADD howChangeHistory longblob;
ALTER TABLE ActionPlan ADD howWorkHistory longblob;
ALTER TABLE ActionPlan ADD imagesInstructions longtext;
ALTER TABLE ActionPlan ADD subTitleHistory longblob;
ALTER TABLE ActionPlan ADD titles longblob;
ALTER TABLE ActionPlan ADD whatIsItHistory longblob;
ALTER TABLE ActionPlan ADD whatTakeHistory longblob;

UPDATE ActionPlan set howChangeHistory = X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A608822030000787077040000000174000C506C616E206D656D6265727378';
UPDATE ActionPlan set howWorkHistory = 	 X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A608822030000787077040000000174000C506C616E206D656D6265727378';
UPDATE ActionPlan set subTitleHistory =  X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A608822030000787077040000000174000C506C616E206D656D6265727378';
UPDATE ActionPlan set whatIsItHistory =  X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A608822030000787077040000000174000C506C616E206D656D6265727378';
UPDATE ActionPlan set whatTakeHistory =  X'ACED0005737200146A6176612E7574696C2E4C696E6B65644C6973740C29535D4A608822030000787077040000000174000C506C616E206D656D6265727378';

DROP TABLE IF EXISTS `Badge`;

CREATE TABLE `Badge` (
  `badge_pk` bigint(20) NOT NULL AUTO_INCREMENT,
  `badgeName` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `description` varchar(255) CHARACTER SET latin1 DEFAULT NULL,
  `media_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`badge_pk`),
  KEY `FK3CFAB832BFE963D` (`media_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `Badge` (`badge_pk`, `badgeName`, `description`, `media_id`)
VALUES
	(1,'Root cards','Played at least one of each root card type (Innovate & Defend)',200),
	(2,'All cards','Played at least one of every type of card (all 6);',201),
	(3,'Super-active root','Played the root card of a super-active chain',202),
	(4,'Super-interesting card','Played a card marked super-interesting by a game master',203),
	(5,'Favorite','Played a card marked as a favorite by another player',204),
	(6,'Action plan author','Accepted at least one Action Plan authorship invitation',205),
	(7,'Top 50','Ranked in top 50 of Leaderboard at any point during gameplay',206),
	(8,'Active player','Logged in each day of gameplay (each day of each session, ex: Tues, Wed, and Thurs of one week)',207);

INSERT INTO `Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`)
VALUES
	(200, 'badge1', 'purple swoosh green', 'badge1', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge1_55w55h.png'),
	(201, 'badge2', 'vertical stripes', 'badge2', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge2_55w55h.png'),
	(202, 'badge3', 'mmowgli spider', 'badge3', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge3_55w55h.png'),
	(203, 'badge4', 'winged s', 'badge4', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge4_55w55h.png'),
	(204, 'badge5', 'orange star on black', 'badge5', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge5_55w55h.png'),
	(205, 'badge6', 'green checked folder', 'badge6', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge6_55w55h.png'),
	(206, 'badge7', 'winded star', 'badge7', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge7_55w55h.png'),
	(207, 'badge8', 'eye', 'badge8', 0, 3, NULL, 0, 'https://web.mmowgli.nps.edu/mmowMedia/images/badge8_55w55h.png');

ALTER TABLE Card ADD authorName varchar(255) DEFAULT '';

# ALTER TABLE CardType ADD cardClass int(11) NOT NULL;
# ALTER TABLE CardType ADD descendantOrdinal int(11) DEFAULT NULL;
# ALTER TABLE CardType ADD cssColorStyle varchar(255) DEFAULT '';
# ALTER TABLE CardType ADD cssLightColorStyle varchar(255) DEFAULT '';

# UPDATE CardType SET cardClass = 0 WHERE id = 1;
# UPDATE CardType SET cardClass = 1 WHERE id = 2;
# UPDATE CardType SET cardClass = 2 WHERE id = 3;
# UPDATE CardType SET cardClass = 2 WHERE id = 4;
# UPDATE CardType SET cardClass = 2 WHERE id = 5;
# UPDATE CardType SET cardClass = 2 WHERE id = 6;

DROP TABLE IF EXISTS `CardType`;

CREATE TABLE `CardType` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cardClass` int(11) NOT NULL,
  `descendantOrdinal` int(11) DEFAULT NULL,
  `ideaCard` bit(1) NOT NULL,
  `prompt` varchar(255) DEFAULT NULL,
  `summaryHeader` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `titleAlternate` varchar(255) DEFAULT NULL,
  `cssColorStyle` varchar(255) DEFAULT NULL,
  `cssLightColorStyle` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

INSERT INTO `CardType` (`id`, `cardClass`, `descendantOrdinal`, `ideaCard`, `prompt`, `summaryHeader`, `title`, `titleAlternate`, `cssColorStyle`, `cssLightColorStyle`)
VALUES
	(1,0,NULL,b'1','Design the best strategy for EPIC anti-piracy efforts to pursue','INNOVATE','Innovate','Resource','m-purple','m-purple-light'),
	(2,1,NULL,b'1','Imagine the worst strategy that EPIC anti-piracy efforts might take','DEFEND','Defend','Risk','m-green','m-green-light'),
	(3,2,1,b'0','Build on this idea to amplify its impact','EXPAND','Expand',NULL,'m-orange','m-orange-light'),
	(4,2,2,b'0','Challenge this idea','COUNTER','Counter',NULL,'m-red','m-red-light'),
	(5,2,3,b'0','Take this idea in a different direction','ADAPT','Adapt',NULL,'m-blue','m-blue-light'),
	(6,2,4,b'0','Something missing? Ask a question','EXPLORE','Explore',NULL,'m-lime','m-lime-light');


ALTER TABLE Game Drop backgroundArt_id;
ALTER TABLE Game Drop footerIftfLogo_id;
ALTER TABLE Game Drop footerNpsLogo_id;
ALTER TABLE Game Drop footerOnrLogo_id;
ALTER TABLE Game Drop headerGameLogo_id;
ALTER TABLE Game Drop logoArt_id;
ALTER TABLE Game Drop orientationVideo_id;

ALTER TABLE Game ADD `actionPlanRequestLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `backgroundImageLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `blogLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `clusterMaster` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `gameFullLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `headerBranding_id` bigint(20) DEFAULT NULL;
ALTER TABLE Game ADD `improveScoreLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `informedConsentLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `mmowgliMapLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `restrictByQueryList` bit(1) NOT NULL;
ALTER TABLE Game ADD `restrictByQueryListInterval` bit(1) NOT NULL;
ALTER TABLE Game ADD `surveyConsentLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `thanksForInterestLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `thanksForPlayingLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `troubleLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD `userAgreementLink` varchar(255) DEFAULT NULL;
ALTER TABLE Game ADD KEY `headerBranding_id` (`headerBranding_id`);


UPDATE Game SET
  actionPlanRequestLink='http://portal.mmowgli.nps.edu/action-plan-request',
  backgroundImageLink='https://web.mmowgli.nps.edu/mmowMedia/images/nauticalBackground.jpg',
  blogLink='http://portal.mmowgli.nps.edu/game-blog',
  clusterMaster='web1',
  gameFullLink='http://mmowgli.nps.edu/pleaseWaitGameFull.html',
  headerBranding_id=NULL,
  improveScoreLink='https://portal.mmowgli.nps.edu/instructions',
  informedConsentLink='http://web.mmowgli.nps.edu/mmowMedia/MmowgliGameParticipantInformedConsent.html',
  mmowgliMapLink='http://maps.google.com/maps/ms?ll=27.059126,10.898438&amp;spn=140.072175,337.851563&amp;t=h&amp;z=2&amp;msa=0&amp;msid=207084999472810915099.0004bee8a05816b530cff&amp;output=embed',
  restrictByQueryList=0,
  restrictByQueryListInterval=0,
  surveyConsentLink=NULL,
  thanksForInterestLink='http://mmowgli.nps.edu/thanksForInterest.html',
  thanksForPlayingLink='http://mmowgli.nps.edu/thanksForPlaying.html',
  troubleLink='http://portal.mmowgli.nps.edu/trouble',
  userAgreementLink='http://www.defense.gov/socialmedia/user-agreement.aspx'
  WHERE id=1;

CREATE TABLE `KeepAlive` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `receiveDate` datetime DEFAULT NULL,
  `receiver` varchar(255) DEFAULT NULL,
  `response` bit(1) NOT NULL,
  `sendDate` datetime DEFAULT NULL,
  `sender` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `MessageUrl` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `text` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE MovePhase ADD windowTitle varchar(255) DEFAULT NULL;
UPDATE MovePhase SET windowTitle='MMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet';

ALTER TABLE Query ADD date datetime DEFAULT NULL;

CREATE TABLE `Query2` (
  `email` varchar(255) DEFAULT '',
  `background` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `date` datetime DEFAULT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `digest` varchar(32) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`),
  KEY `digestKey` (`digest`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `User_Badges` (
  `user_id` bigint(20) NOT NULL,
  `badge_pk` bigint(20) NOT NULL,
  PRIMARY KEY (`user_id`,`badge_pk`),
  KEY `FK5C8B42486C88E77` (`user_id`),
  KEY `FK5C8B424E1018ABD` (`badge_pk`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

ALTER TABLE Game ADD `registeredLogonsOnly` bit(1) NOT NULL DEFAULT 0;
ALTER TABLE ActionPlan ADD `hidden` bit(1) NOT NULL DEFAULT 0;
ALTER TABLE ActionPlan ADD quickAuthorList varchar(255) DEFAULT '';

CREATE TABLE `ActionPlan_HowChange_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKD22C995CA276057` (`actionplan_id`),
  KEY `FKD22C995C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ActionPlan_HowWork_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK1C1F77F6CA276057` (`actionplan_id`),
  KEY `FK1C1F77F6C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ActionPlan_SubTitles_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKF8A949F0CA276057` (`actionplan_id`),
  KEY `FKF8A949F0C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ActionPlan_Titles_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK368BFB10CA276057` (`actionplan_id`),
  KEY `FK368BFB10C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ActionPlan_WhatIs_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FK4A904203CA276057` (`actionplan_id`),
  KEY `FK4A904203C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `ActionPlan_WhatTake_History` (
  `actionplan_id` bigint(20) NOT NULL,
  `edits_id` bigint(20) NOT NULL,
  PRIMARY KEY (`actionplan_id`,`edits_id`),
  UNIQUE KEY `edits_id` (`edits_id`),
  KEY `FKFF436360CA276057` (`actionplan_id`),
  KEY `FKFF436360C0C9441D` (`edits_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Edits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `dateTime` datetime DEFAULT NULL,
  `value` longtext,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=41 DEFAULT CHARSET=latin1;

INSERT INTO `Level` (`id`, `description`, `ordinal`) VALUES (100, 'Game Master', -1);
UPDATE Level SET description='Player' where id='1';

ALTER TABLE Game ADD mapTitle varchar(255) DEFAULT 'Mmowgli Map';
ALTER TABLE MovePhase MODIFY orientationHeadline LONGTEXT;

# version 20120716
ALTER TABLE Game ADD version bigint(20) NOT NULL DEFAULT '0' AFTER `id`;
UPDATE `Game` SET `version` = '20120716' where id='1';

# version 20120718
ALTER TABLE Game ADD `defaultActionPlanMapLon` double DEFAULT '-99.476';
ALTER TABLE Game ADD `defaultActionPlanMapLat` double DEFAULT '38.597';
