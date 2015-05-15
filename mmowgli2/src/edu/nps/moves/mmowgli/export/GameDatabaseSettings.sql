/*
  Copyright (C) 2010-2014 Modeling Virtual Environments and Simulation
  (MOVES) Institute at the Naval Postgraduate School (NPS)
  http://www.MovesInstitute.org and http://www.nps.edu
 
  This file is part of Mmowgli.
  
  Mmowgli is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  any later version.

  Mmowgli is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with Mmowgli in the form of a file named COPYING.  If not,
  see <http://www.gnu.org/licenses/>
*/


/* Piracy and Energy MMOWGLI Game Settings */

UPDATE `piracy2011_1`.`MovePhase` SET `windowTitle`='2011.1 piracyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'    WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `windowTitle`='2011.2 piracyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'    WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `windowTitle`='2011.3 piracyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'    WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `windowTitle`='2012 piracyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'      WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `windowTitle`='test.Piracy 2012 MMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet' WHERE `id`='1';
UPDATE       `energy`.`MovePhase` SET `windowTitle`='energyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'            WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `windowTitle`='test.energyMMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'       WHERE `id`='1';

UPDATE `piracy2011_1`.`MovePhase` SET `orientationCallToActionText`='Welcome piracyMMOWGLI 2011.1'     WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `orientationCallToActionText`='Welcome piracyMMOWGLI 2011.2'     WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `orientationCallToActionText`='Welcome piracyMMOWGLI 2011.3'     WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `orientationCallToActionText`='Welcome piracyMMOWGLI 2012!'      WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `orientationCallToActionText`='Welcome piracyMMOWGLI 2012'       WHERE `id`='1';
UPDATE       `energy`.`MovePhase` SET `orientationCallToActionText`='Welcome to energyMMOWGLI!' WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `orientationCallToActionText`='Welcome to energyMMOWGLI!' WHERE `id`='1';

/* TODO orientationHeadline database field size needs to be longer:  longText */
UPDATE `piracy2011_1`.`MovePhase` SET `orientationHeadline`='<p>Play the game, change the game!</p><p>Move 1: TURN THE TIDE       </p><p>Please login as <i>guest</i>&nbsp; (password <i>guest</i>) to view this completed game.</p><p>The <a href=\"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games\" target=\"portal\">Piracy MMOWGLI Games</a> provides detailed game information. Current game activity is reported on the <a href=\"http://portal.mmowgli.nps.edu/piracy-blog\" target=\"portal\">MMOWGLI Piracy Blog</a>.</p>' WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `orientationHeadline`='<p>Play the game, change the game!</p><p>Move 2: TAKE ACTION TOGETHER</p><p>Please login as <i>guest</i>&nbsp; (password <i>guest</i>) to view this completed game.</p><p>The <a href=\"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games\" target=\"portal\">Piracy MMOWGLI Games</a> provides detailed game information. Current game activity is reported on the <a href=\"http://portal.mmowgli.nps.edu/piracy-blog\" target=\"portal\">MMOWGLI Piracy Blog</a>.</p>' WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `orientationHeadline`='<p>Play the game, change the game!</p><p>Move 3: PUSHING BACK        </p><p>Please login as <i>guest</i>&nbsp; (password <i>guest</i>) to view this completed game.</p><p>The <a href=\"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games\" target=\"portal\">Piracy MMOWGLI Games</a> provides detailed game information. Current game activity is reported on the <a href=\"http://portal.mmowgli.nps.edu/piracy-blog\" target=\"portal\">MMOWGLI Piracy Blog</a>.</p>' WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `orientationHeadline`='<p>This long-term game is for maritime professionals exploring how to best combat piracy.</p><p>This week\'s theme: <b>Naval Operations</b> from the <a href="http://www.oceansBeyondPiracy.org" target="_blank">Oceans Beyond Piracy</a> Independent Assessment Report.</p><p>If you received an email invitation, follow <i>I\'m new to MMOWGLI</i> below. Registration is fast. To login again, follow <i>I\'m registered</i> below.</p><p>The <a href=\"http://portal.mmowgli.nps.edu/piracy-blog\" target=\"portal\">MMOWGLI Piracy Blog</a> has game news.</p>' WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `orientationHeadline`='<p>Welcome. This long-term game is for maritime professionals exploring how to best combat piracy.</p><p>Community experts are first reviewing ideas and action plans from past Piracy MMOWGLI games.</p><p>If you received an email invitation, follow <i>I\'m new to MMOWGLI</i> below. Registration is fast. To login again, follow <i>I\'m registered</i> below.</p><p>The <a href=\"http://portal.mmowgli.nps.edu/piracy-blog\" target=\"portal\">MMOWGLI Piracy Blog</a> has game news.</p>' WHERE `id`='1';

UPDATE       `energy`.`MovePhase` SET `orientationHeadline`='<p>This game is about the future of energy.</p><p>We\'re working on new ideas and plans to reduce energy usage by the Navy and Marine Corps. You can help improve our nation\'s energy security.</p><p>Welcome back players! Thanks for your patience and many efforts. As thanks, this long weekend is an "extra inning" for already-registered players.</p><p>The <a href=\"http://portal.mmowgli.nps.edu/energy-blog\" target=\"portal\">energyMMOWGLI Blog</a> has game news.</p>' WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `orientationHeadline`='<p>This game is about the future of energy.</p><p>We\'re working on new ideas and plans to reduce energy usage by the Navy and Marine Corps. You can help improve the energy security of our nation.</p><p>If you received an email invitation and this is your first time here, follow <i>I\'m new to MMOWGLI</i> below.  Registration is fast. To login again, follow <i>I\'m registered</i> below.</p><p>The <a href=\"http://portal.mmowgli.nps.edu/energy-blog\" target=\"portal\">energyMMOWGLI Blog</a> has game news.</p>' WHERE `id`='1';


UPDATE `piracy2011_1`.`MovePhase` SET `orientationSummary`='<p>Thanks for your interest.</p>'       WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `orientationSummary`='<p>Thanks for your interest.</p>'       WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `orientationSummary`='<p>Thanks for your interest.</p>'       WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `orientationSummary`='<p>Thanks for all contributions.<br />Play the game, change the game!</p>'      WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `orientationSummary`='<p>Thanks for all contributions.<br />Play the game, change the game!</p>'      WHERE `id`='1';
UPDATE       `energy`.`MovePhase` SET `orientationSummary`='Thanks for your continuing contributions.<br />Play the game, change the game!' WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `orientationSummary`='Thanks for your continuing contributions.<br />Play the game, change the game!' WHERE `id`='1';

UPDATE `piracy2011_1`.`MovePhase` SET `playACardTitle`='Completed Idea Card Play' WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `playACardTitle`='Completed Idea Card Play' WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `playACardTitle`='Completed Idea Card Play' WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `playACardTitle`='PLAY AN IDEA CARD NOW!'   WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `playACardTitle`='PLAY AN IDEA CARD'        WHERE `id`='1';
UPDATE       `energy`.`MovePhase` SET `playACardTitle`='PLAY AN IDEA CARD'        WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `playACardTitle`='PLAY AN IDEA CARD'        WHERE `id`='1';

UPDATE `piracy2011_1`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.'                                               WHERE `id`='1';
UPDATE `piracy2011_2`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.'                                               WHERE `id`='1';
UPDATE `piracy2011_3`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.'                                               WHERE `id`='1';
UPDATE       `piracy`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.' WHERE `id`='1';
UPDATE   `piracytest`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.' WHERE `id`='1';
UPDATE       `energy`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.'                                               WHERE `id`='1';
UPDATE   `energytest`.`MovePhase` SET `playACardSubtitle`='Start now, play fast, work together.'                                               WHERE `id`='1';

/* ================================================================= */
/* Developmental MMOWGLI Game Settings */

/* TODO rename database piracy to piracy2012 */

/* prior to update of card art
UPDATE     `energy`.`CardType` SET `summaryHeader`='BEST STRATEGY',  `prompt`='How can we improve Navy and Marine Corps energy efficiency, either afloat or ashore?' WHERE `id`='1';
UPDATE `energytest`.`CardType` SET `summaryHeader`='BEST STRATEGY',  `prompt`='How can we improve Navy and Marine Corps energy efficiency, either afloat or ashore?' WHERE `id`='1';
UPDATE     `energy`.`CardType` SET `summaryHeader`='WORST STRATEGY', `prompt`='What big mistakes or hidden pitfalls need to be avoided?'             WHERE `id`='2';
UPDATE `energytest`.`CardType` SET `summaryHeader`='WORST STRATEGY', `prompt`='What big mistakes or hidden pitfalls need to be avoided?'             WHERE `id`='2';
*/
UPDATE     `energy`.`CardType` SET `summaryHeader`='EFFICIENCY',     `prompt`='How can we improve energy efficiency, afloat or ashore, for Navy and Marine Corps?' WHERE `id`='1';
UPDATE     `energy`.`CardType` SET `summaryHeader`='CONSUMPTION',    `prompt`='How can we reduce overall energy consumption by the Navy and Marine Corps?'         WHERE `id`='2';

/* TODO Youtube links */


/*
INSERT INTO     `energy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (171, 'energyMMOWGLI Call To Action!', 'energyMMOWGLI Call To Action! created by IFTF', 'YouTubeVideo', 0, 3, 'energyMMOWGLI', 3, 'zBPLVAKpvS8');
INSERT INTO `energytest`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (171, 'energyMMOWGLI Call To Action!', 'energyMMOWGLI Call To Action! created by IFTF', 'YouTubeVideo', 0, 3, 'energyMMOWGLI', 3, 'zBPLVAKpvS8');


long-form
INSERT INTO `energy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (182, 'energyMMOWGLI Welcome', 'The energyMMOWGLI game is exploring better use of energy resources', 'YouTubeVideo', 0, 3, 'energyMMOWGLI Orientation ', 3, 'wKXrIWsuxnE');
200
Record 171 then needs to be updated as the pointer in MovePhase....
UPDATE `energy`.`MovePhase` SET `callToActionBriefingVideo_id`=171 WHERE `id`='1';
*/

UPDATE     `energy`.`MovePhase` SET `callToActionBriefingVideo_id`=182 WHERE `id`='1';
UPDATE `energytest`.`MovePhase` SET `callToActionBriefingVideo_id`=182 WHERE `id`='1';

UPDATE     `energy`.`Media` SET `caption`='energyMMOWGLI Call To Action!', `description`='energyMMOWGLI Call To Action! created by Institute for the Future (IFTF.org)' WHERE `id`='171';
UPDATE `energytest`.`Media` SET `caption`='energyMMOWGLI Call To Action!', `description`='energyMMOWGLI Call To Action! created by Institute for the Future (IFTF.org)' WHERE `id`='171';

/* Now integrated into MMOWGLI code:  suffix to turn off YouTube panarama (of other supposedly related videos) at end of play:  &rel=0  */

/* Initial draft zBPLVAKpvS8, second draft ZeKyO397Azg, third version/trailer jIeFLxZA_T8 */
UPDATE     `energy`.`Media` SET `url`='jIeFLxZA_T8' WHERE `id`='171';
UPDATE `energytest`.`Media` SET `url`='jIeFLxZA_T8' WHERE `id`='171';

/*
https://web.mmowgli.nps.edu/mmowMedia/mov/emmowgli_draft6 trailer-720p.mov
https://web.mmowgli.nps.edu/mmowMedia/mov/emmowgli_longdraft_v07a-720.mov
UPDATE `energy`.`MovePhase` SET `callToActionBriefingText`='<h4 style=\"margin: 0px\"> Welcome to the energyMMOWGLI game !!</h4>\r \r <p style=\"margin: 0px\">\r \r<em>Your <b>ideas</b> and <b>action plans</b> are needed.</em> The U.S. Navy and Marine Corps depend far too much on petroleum, which degrades the strategic position and tactical performance of our forces.\r The global supply of oil is finite, increasingly difficult to find, and costs continue to rise.\r We need to improve our energy security, increase energy independence,\r and help lead the nation towards a clean energy economy. Please help us execute the\r <a href=\"http://www.navy.mil/features/Navy_EnergySecurity.pdf\" target=\"blank\">Navy Energy Security Strategy</a>:</p>\r <ul style=\"white-space:normal\">\r <li>Energy-Efficient Acquisition for better buildings and systems,</li>\r <li>Sail the <a href=\"http://greenfleet.dodlive.mil/home\" target=\"_blank\">Great Green Fleet</a> Strike Group,</li>\r <li><a href=\"http://www.navy.mil/search/display.asp?story_id=63220\" target=\"_blank\">Reduce Non-Tactical Petroleum Use</a>,</li>\r <li>Increase Alternative Energy Ashore, and</li>\r <li>Increase Alternative Energy Use Navy-Wide.</li>\r </ul>\r' WHERE `id`='1';
*/

/* Short trailer emmowgli_draft6 trailer-720p.mov   long trailer emmowgli_longdraft_v07a-720.mov */
UPDATE `energy`.`MovePhase` SET `callToActionBriefingText`='<h4 style=\"margin: 0px\"> Welcome to the energyMMOWGLI game !!</h4>\r <br />\r <p style=\"margin: 0px\">\r \r<em>Your <b>ideas</b> and <b>action plans</b> are needed.</em> The U.S. Navy and Marine Corps depend far too much on petroleum, hampering our forces.\r The global supply of oil is finite and increasingly hard to find.  Costs continue to rise.\r <br /> We need to improve our energy security, increase energy independence,\r and lead the nation towards a clean energy economy. Please help us execute the\r <a href=\"http://www.navy.mil/features/Navy_EnergySecurity.pdf\" target=\"blank\">Navy Energy Security Strategy</a>:</p>\r <ul style=\"white-space:normal\">\r <li>Energy-Efficient Acquisition for better buildings and systems,</li>\r <li>Sail the <a href=\"http://greenfleet.dodlive.mil/home\" target=\"_blank\">Great Green Fleet</a> Strike Group,</li>\r <li><a href=\"http://www.navy.mil/search/display.asp?story_id=63220\" target=\"_blank\">Reduce Non-Tactical Petroleum Use</a>,</li>\r <li>Increase Alternative Energy Ashore, and</li>\r <li>Increase Alternative Energy Use Navy-Wide.</li>\r </ul>\r <p align="right"> (No video? Try <a href="http://web.mmowgli.nps.edu/mmowMedia/mov/emmowgli_longdraft_v07a-720.mov" target="_blank">this</a>)</p>' WHERE `id`='1';

UPDATE `energy`.`Game` SET `maxUsersOnline`=600 WHERE `id`='1';

UPDATE `piracy`.`MovePhase` SET `callToActionBriefingText`='<p>Somali piracy is a serious international problem that affects many nations, industries and individuals. During 2011, many people from the general public played the <a href=\"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy%20MMOWGLI%20Games\" target=\"piracy\">Piracy MMOWGLI games</a> to explore how to best combat piracy.  Latest game news is found on our <a href=\"https://portal.mmowgli.nps.edu/piracy-blog\" target=\"piracy\">Piracy Blog</a>.</p> <p><em>Now it\'s time for counter-piracy professionals.</em>  Current agreements for naval operations, regional capacity building, and strategic messaging expire at the end of 2014.  What are our priorities? Our transition options? International experts are joining together to build a community, brainstorm good ideas, and collaborate on action plans.  We will examine and challenge the work produced by <a href="http://www.oceansbeyondpiracy.org">Oceans Beyond Piracy</a> in their <a href=\"http://oceansbeyondpiracy.org/independent_assessment\" target=\"piracy\">Independent Assessment Report</a>. </p> <p> Our first Line of Effort is <i><b>Naval Operations</b></i>. Please spend ~30 minutes each week and contribute. Play the game, change the game! </p> ' WHERE `id`='1';

UPDATE     `piracy`.`CardType` SET `summaryHeader`='CHALLENGES',       `prompt`='How can we improve our partner efforts and best practices today?' WHERE `id`='1';
UPDATE     `piracy`.`CardType` SET `summaryHeader`='FUTURE GOALS',     `prompt`='What counter-piracy cooperation is needed after 2014?'            WHERE `id`='2';

UPDATE `piracy`.`CardType` SET `title`='Challenges'     WHERE `id`='1';
UPDATE `piracy`.`CardType` SET `title`='Future Goals'   WHERE `id`='2';

/* How can our counter-piracy efforts be successful by 2015? */

UPDATE `piracy`.`MovePhase` SET `callToActionBriefingSummary`='Welcome Maritime Experts' WHERE `id`='1';

/* piracyMMOWGLI 2102 */

INSERT INTO     `piracy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (100, 'piracyMMOWGLI Orientation Video', 'piracyMMOWGLI Orientation Video created by NPS', 'YouTubeVideo', 0, 3, 'energyMMOWGLI', 3, '0WLrMFwMOog');
UPDATE `piracy`.`MovePhase` SET `orientationVideo_id`=100 WHERE `id`='1';

INSERT INTO `piracy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (400, 'MMOWGLI Welcome', 'The MMOWGLI game explores online collaboration to build shared idea chains and action plans. Play the game, change the game!', 'Orientation (bumper) Video', 0, 3, 'MMOWGLI Orientation ', 3, 'BtTGOxCHcD0');
UPDATE `piracy`.`MovePhase` SET `orientationVideo_id`=400 WHERE `id`='1';

INSERT INTO `piracy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (203, 'Piracy 2012 Call To Action', 'Piracy 2012 Call To Action by NPS', 'YouTubeVideo', 0, 3, 'Piracy 2012 Call To Action by NPS', 3, 'LlhRKAxyiU0');
UPDATE `piracy`.`MovePhase` SET `callToActionBriefingVideo_id`=203 WHERE `id`='1';

INSERT INTO `piracy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (204, 'Piracy 2012 Call To Action', 'Piracy 2012 Call To Action by NPS', 'YouTubeVideo', 0, 3, 'Piracy 2012 Call To Action by NPS', 3, '75e7PaUf7E4');
UPDATE `piracy`.`MovePhase` SET `callToActionBriefingVideo_id`=204 WHERE `id`='1';

INSERT INTO `piracy`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (205, 'Piracy 2012 Call To Action', 'Piracy 2012 Call To Action by NPS', 'YouTubeVideo', 0, 3, 'Piracy 2012 Call To Action: Regional Capacity Building', 3, 'iYIESGXENAY');
UPDATE `piracy`.`MovePhase` SET `callToActionBriefingVideo_id`=205 WHERE `id`='1';



/* alternate way to change: */
UPDATE          `piracy`.`Media` SET `url`='0WLrMFwMOog' WHERE `id`='100';
UPDATE          `piracy`.`Media` SET `url`='LlhRKAxyiU0' WHERE `id`='203';

UPDATE `piracy`.`GameQuestion` SET `question`='What do you hope to change about Somali piracy?', `summary`='HOPE TO CHANGE ABOUT PIRACY' WHERE `id`='5';

/* clean MMOWGLI orientation (bumper video) with music, without piracty references:  BtTGOxCHcD0 
    https://www.youtube.com/watch?v=BtTGOxCHcD0
  TODO: get movesMMOWGLI account password, list it publicly
*/

INSERT INTO `em2test`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (400, 'MMOWGLI Welcome', 'The MMOWGLI game explores online collaboration to build shared idea chains and action plans. Play the game, change the game!', 'Orientation (bumper) Video', 0, 3, 'MMOWGLI Orientation ', 3, 'BtTGOxCHcD0');
UPDATE `em2test`.`MovePhase` SET `orientationVideo_id`=400 WHERE `id`='1';


/* EM2 MMOWGLI https://test.mmowgli.nps.edu/em2 */

UPDATE `em2test`.`MovePhase` SET `windowTitle`='em2 MMOWGLI: Massive Multiplayer Online Wargame Leveraging the Internet'    WHERE `id`='1';

UPDATE `em2test`.`MovePhase` SET `orientationCallToActionText`='EM Maneuver MMOWGLI game!' WHERE `id`='1';

UPDATE `em2test`.`MovePhase` SET `orientationHeadline`='<p>This online wargame is for Navy professionals exploring how to best meet the challenges and achieve the objectivess of EM Maneuver Warfare.  This wargame is also called <b>em<sup>2</sup> MMOWGLI</b>.</p><p>Join motivated warfighters who are examining and extending critical ideas about the future.</p><p>If you received an invitation, follow <i>I\'m new to MMOWGLI</i> below. Registration is fast - confirm your identity using  your navy.mil email account. To login again, follow <i>I\'m registered</i> below.</p>' WHERE `id`='1';
/* findings in the latest CNO Strategic Study Group (SSG) Report */

UPDATE `em2test`.`MovePhase` SET `orientationSummary`='Thanks for your reactions and contributions.<br />Play the game, change the game!'      WHERE `id`='1';

/* TODO:  "piracy" text overlaying central MMOWGLI logo */

UPDATE `em2test`.`MovePhase` SET `callToActionBriefingSummary`='Welcome to the EM Maneuver MMOWGLI game!' WHERE `id`='1';

UPDATE `em2test`.`MovePhase` SET `callToActionBriefingText`='<p>How can we act on the EM Maneuver Warfare challenges?  How can we best achieve future success?  Here is where groups of people describe their best ideas and recommendations for moving forward effectively.</p><p>(This Call To Action video is from CNO SSG XXX, <i>The Navy\'s Future Computing and Information Environment of 2020</i>.  We might use <i>Red Victory</i> for Navy players, otherwise will need different video clips.)</p><p>(More motivation for each phase of the game goes here.)</p><p>(More background...) During 2011-2012, many people from the general public played the <a href=\"https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy%20MMOWGLI%20Games\" target=\"piracy\">Piracy MMOWGLI games</a> to explore how to best combat piracy.  Latest game news is found on our <a href=\"https://portal.mmowgli.nps.edu/piracy-blog\" target=\"piracy\">Piracy Blog</a>.</p> <p><em>Now it\'s your turn.</em>  Please spend time each week and contribute. Play the game, change the game! </p> ' WHERE `id`='1';

INSERT INTO     `em2test`.`Media` (`id`, `caption`, `description`, `handle`, `inAppropriate`, `source`, `title`, `type`, `url`) VALUES (210, 'CNO SSG XXX Call To Action!', 'Navy\'s Future Computing and Information Environment of 2020', 'YouTubeVideo', 0, 3, 'em2MMOWGLI', 3, 'EVoKOWwfR7E');
UPDATE `em2test`.`MovePhase` SET `callToActionBriefingVideo_id`=210 WHERE `id`='1';

/* USNWC Newport RI location 41.508192, -71.329429 */
/* TODO map location is not updating, restart needed?  bug to fix. */
UPDATE `em2test`.`Game` SET `defaultActionPlanMapLon`=-71.329429, `defaultActionPlanMapLat`=41.508192 WHERE `id`='1';

UPDATE `em2test`.`CardType` SET `summaryHeader`='NEW CHALLENGES',   `prompt`='What are the real-world challenges facing EM Maneuver Warfare?' WHERE `id`='1';
UPDATE `em2test`.`CardType` SET `summaryHeader`='FUTURE ACTION',    `prompt`='How do we prepare to effectively conduct EM Maneuver Warfare?'  WHERE `id`='2';


