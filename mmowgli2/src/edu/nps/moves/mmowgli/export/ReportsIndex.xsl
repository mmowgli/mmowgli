<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu" -->
    <xsl:output method="html"/>

    <!-- Global variables -->

    <!-- must also change CSS td.cardCell below! ensure px included. example: 50px -->
    <xsl:variable name="cardCellWidth">
        <xsl:text>50px</xsl:text>
    </xsl:variable>
    <xsl:variable name="maxColumnCount">
        <xsl:text>30</xsl:text>
    </xsl:variable>

    <xsl:variable name="gameTitle">
        <!-- Piracy2012, Piracy2011.1, Energy2012, etc. -->
        <xsl:value-of select="//GameTitle"/>
    </xsl:variable>

    <xsl:variable name="gameSecurity">
        <!-- open, FOUO, etc. -->
        <xsl:value-of select="//GameSecurity"/>
    </xsl:variable>

    <xsl:variable name="exportDateTime">
        <xsl:value-of select="//MmowgliGame/@exported"/>
    </xsl:variable>

    <!-- Common variable for each stylesheet -->
    <xsl:variable name="gameLabel">
        <!-- piracyMMOWGLI, energyMMOWGLI, etc. -->
        <xsl:choose>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.1')">
                <xsl:text>piracyMMOWGLI 2011.1</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.2')">
                <xsl:text>piracyMMOWGLI 2011.2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.3')">
                <xsl:text>piracyMMOWGLI 2011.3</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2012')">
                <xsl:text>piracyMMOWGLI 2012</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'nergy')">
                <xsl:text>energyMMOWGLI 2012</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                <xsl:text>bii Business Innovation Initiative</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'Em2')">
                <xsl:text>em2 ElectroMagnetic Maneuver</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>evtp Edge Virtual Training Program</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>am Additive Manufacturing</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>cap2con Capacity, Capabilities and Constraints</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'dark')">
                <xsl:text>dark Portal (NDU)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ig')">
                <xsl:text>NPS Inspector General (ig) Review</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'training')">
                <xsl:text>MMOWGLI Training</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'navair') or contains($gameTitle,'nsc')">
                <xsl:text>nsc NAWCAD Strategic Cell</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'blackswan') or starts-with($gameTitle,'Blackswan') or contains($gameTitle,'blackswan') or contains($gameTitle,'Blackswan')">
                <xsl:text>blackswan</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'dd') or starts-with($gameTitle,'DD') or contains($gameTitle,'dd') or contains($gameTitle,'DD') or contains($gameTitle,'Dilemma')">
                <xsl:text>dd Data Dilemma</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'pcc') or starts-with($gameTitle,'PCC') or contains($gameTitle,'pcc') or contains($gameTitle,'PCC')">
                <xsl:text>pcc Professional Core Competencies</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>uxvdm Unmanned Vehicle Digital Manufacturing</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ndu')">
                <xsl:text>ndu National Defense University</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of disable-output-escaping="yes" select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="IdeaCardLocalLink">
        <xsl:text>IdeaCardChain_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>

    <xsl:variable name="ActionPlanLocalLink">
        <xsl:text>ActionPlanList_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>

    <xsl:variable name="PlayerProfilesLocalLink">
        <xsl:text>PlayerProfiles_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>

    <xsl:variable name="GameDesignLocalLink">
        <xsl:text>GameDesign_</xsl:text>
        <!-- supports game titles with spaces -->
        <xsl:value-of select="replace($gameTitle,' ','_')"/>
    </xsl:variable>
    
    <!-- TODO get game acronym, url in XML file -->
    <xsl:variable name="gameAcronym">
        <xsl:choose>
            <xsl:when           test="(string-length(//GameAcronym) > 0)">
                <xsl:value-of select="//GameAcronym"/>
            </xsl:when>
            <xsl:when           test="contains(//GameTitle,'Mmowgli')">
                <xsl:value-of select="substring-before(//GameTitle,'Mmowgli')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:template match="GameSummary">
        <h2>
            <xsl:value-of select="." disable-output-escaping="yes"/>
        </h2>
    </xsl:template>

    <!-- default match for text() is to ignore -->
    <xsl:template match="text()"/>

    <xsl:template match="/">

        <!-- remove any line-break elements -->
        <xsl:variable name="gameSummary">
            <xsl:choose>
                <xsl:when test="contains(//GameSummary,'&lt;br /&gt;')">
                    <xsl:value-of disable-output-escaping="yes" select="substring-before(//GameSummary,'&lt;br /&gt;')"        /><xsl:value-of disable-output-escaping="yes" select="substring-after(//GameSummary,'&lt;br /&gt;')"/>
                </xsl:when>
                <xsl:when test="contains(//GameSummary,'&amp;lt;br /&amp;gt;')">
                    <xsl:value-of disable-output-escaping="yes" select="substring-before(//GameSummary,'&amp;lt;br /&amp;gt;')"/><xsl:value-of disable-output-escaping="yes" select="substring-after(//GameSummary,'&amp;lt;br /&amp;gt;')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="//GameSummary"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- debug
        <xsl:comment> <xsl:value-of select="$gameLabel"/><xsl:text disable-output-escaping="yes">  '&lt;br /&gt;'</xsl:text></xsl:comment>
        -->

        <html>
            <head>
            <!-- TODO
                <meta name="identifier" content="https:// TODO /IdeaCardChains.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta http-equiv="refresh" content="600"/> <!-- 600 seconds = 10 minutes -->
                <meta name="author"      content="Don Brutzman and Mike Bailey"/>
                <meta name="description" content="Idea card chain outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="ReportsIndex_{$gameTitle}.html"/>
                <meta name="identifier"  content="https://mmowgli.nps.edu/{$gameAcronym}/IdeaCardChains_{$gameTitle}.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, https://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="ReportsIndex.xsl"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, https://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>

                <xsl:element name="title">
                    <xsl:text disable-output-escaping="yes">Reports Index, </xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                    <xsl:if test="not(contains(lower-case($gameLabel),'mmowgli'))">
                        <xsl:text> MMOWGLI game</xsl:text>
                    </xsl:if>
                </xsl:element>

                <style type="text/css">
table {
    border-collapse:collapse;
}
table.banner
{
    padding:5px 20px;
}
td.cardCell {
    align:center;
    width:50px;
}
td.longtext {
    white-space: nowrap;
    overflow: hidden;
}
.beststrategy {
    background-color:#00ab4f;
}
.worststrategy {
    background-color:#FFD700; /* #6d3695; */
}
.expand {
    background-color:#f39025; /* #f37025; */
}
.counter {
    background-color:#ee1111; /* #bf1961 */
}
.adapt {
    background-color:#047cc2;
}
.explore {
    background-color:#9933cc; /* #97c93c */
}
                </style>
            </head>
            <body>
                <a name="index"></a>
                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>

                <!-- This list of url links appears in both ActionPlanList.xsl and CardTree.xsl -->
                <xsl:variable name="portalPage">
                    <xsl:choose>
                        <xsl:when test="contains($gameTitle,'2011.1')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.1</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'2011.2')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.2</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'2011.3')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2011.3</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'Piracy')">
                            <xsl:text>https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games#section-Piracy+MMOWGLI+Games-PiracyMMOWGLIGame2012</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'nergy')">
                            <xsl:text>https://portal.mmowgli.nps.edu/energy-welcome</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                            <xsl:text>https://portal.mmowgli.nps.edu/bii</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'cap2con') or contains($gameTitle,'cap2con')">
                            <xsl:text>https://portal.mmowgli.nps.edu/cap2con</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'darkportal')">
                            <xsl:text>https://portal.mmowgli.nps.edu/darkportal</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em')">
                            <xsl:text>https://portal.mmowgli.nps.edu/em2</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                            <xsl:text>https://portal.mmowgli.nps.edu/evtp</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'ig')">
                            <xsl:text>https://portal.mmowgli.nps.edu/ig</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'training')">
                            <xsl:text>https://portal.mmowgli.nps.edu/training</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'navair') or contains($gameTitle,'nsc')">
                            <xsl:text>https://portal.mmowgli.nps.edu/nsc</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'blackswan') or contains(lower-case($gameTitle),'swan')">
                            <xsl:text>https://portal.mmowgli.nps.edu/blackswan</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'dd') or contains(lower-case($gameTitle),'DD')">
                            <xsl:text>https://portal.mmowgli.nps.edu/dd</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'pcc') or contains(lower-case($gameTitle),'PCC')">
                            <xsl:text>https://portal.mmowgli.nps.edu/pcc</xsl:text>
                        </xsl:when>
                        <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                            <xsl:text>https://portal.mmowgli.nps.edu/uxvdm</xsl:text>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>https://portal.mmowgli.nps.edu/</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <table align="left" border="0" class="banner">
                    <tr>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <td align="center" valign="middle">
                            <h1 align="center">
                            <br />
                                <xsl:text> Game Reports </xsl:text>
                            </h1>
                            <h2 align="center" valign="middle">
                                <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                            </h2>
                        </td>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <td align="left" valign="middle">
                            <a href="{$portalPage}" title="Game documentation for {$gameLabel}" target="_blank">
                                <!-- 1158 x 332 -->
                                <img align="center" valign="middle" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="386" height="111" border="0"/>
                            </a>
                        </td>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <!-- Table of Contents -->
                        <td align="left" valign="middle">
                <p style="font-size:-2">
                    <!--
                    <a href="#index">Game Reports</a><xsl:text> </xsl:text>
                    <br />
                    -->
                    <a href="#DatabaseExports">Database Exports</a><xsl:text> </xsl:text>
                    <br />
                    <a href="#ProductionNotes">Production Notes</a><xsl:text> </xsl:text>
                    <br />
                    <a href="#Contact"><xsl:text>Contact</xsl:text></a>
                </p>
                        </td>
                    </tr>
                    <tr align="center" rowspan="3">
                        <td colspan="6">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                    <tr align="center" rowspan="3">
                        <td colspan="6">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                </table>
                
                <!-- Table of Contents
                <p align="center" style="font-size:-2">
                    <a href="#GameReports">Game Reports</a><xsl:text> | </xsl:text>
                    <a href="#DatabaseExports">Database Exports</a><xsl:text> | </xsl:text>
                    <a href="#ProductionNotes">Production Notes</a><xsl:text> | </xsl:text>
                    <a href="#Contact"><xsl:text>Contact</xsl:text></a>
                </p>

                <h2> 
                    <a name="GameReports">Game Reports</a>
                </h2> -->
                
                <!-- vertical line spacing -->
                <p>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </p>
                
                <!-- TODO add totals?  requires reaching into other files -->

                <table cellspacing="2" cellpadding="4" border="0">
                    <tr align="center">
                        <td>
                            <a href="{concat($IdeaCardLocalLink,'.html')}" title="Idea Card chains report .html">
                                <img src="images/PlayAnIdea.png"/>
                            </a>
                            <br />
                            <a href="{concat($IdeaCardLocalLink,'.html')}" title="Idea Card chains report .html">
                                <xsl:text>Idea Card Chains</xsl:text>
                            </a>
                            <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                                <xsl:text> (</xsl:text>
                                <a href="{concat($IdeaCardLocalLink,'.pdf')}" title="Idea Card chains report .pdf">
                                    <xsl:text>.pdf</xsl:text>
                                </a>
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                        </td>
                        <td>
                            <a href="cardSunburstVisualizer.html" title="Idea Card Sunburst Visualizer">
                                <img src="images/IdeaCardSunburstVisualizer.png" height="100"/>
                            </a>
                            <br />
                            <a href="cardSunburstVisualizer.html" title="Idea Card Sunburst Visualizer">
                                <xsl:text disable-output-escaping="yes">Idea&amp;nbsp;Card Sunburst&amp;nbsp;Visualizer</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="{concat($ActionPlanLocalLink,'.html')}" title="Action Plans report .html">
                                <img src="images/TakeAction100.png"/>
                            </a>
                            <br />
                            <a href="{concat($ActionPlanLocalLink,'.html')}" title="Action Plans report .html">
                                <xsl:text>Action Plans</xsl:text>
                            </a>
                            <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                                <xsl:text>(</xsl:text>
                                <a href="{concat($ActionPlanLocalLink,'.pdf')}" title="Action Plans report .pdf">
                                    <xsl:text>.pdf</xsl:text>
                                </a>
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                        </td>
                        <td width="175">
                            <a href="{concat($PlayerProfilesLocalLink,'.html')}" title="Player Profiles report .html">
                                <img src="images/MMOWGLIteam258w_q75.jpg" height="100"/>
                            </a>
                            <br />
                            <a href="{concat($PlayerProfilesLocalLink,'.html')}" title="Player Profiles report .html">
                                <xsl:text>Player Profiles</xsl:text>
                            </a>

                            <!-- TODO
                            <xsl:if test="(//ApplicationURLs/PdfAvailable = 'true')">
                                <xsl:text>(</xsl:text>
                                <a href="{concat($PlayerProfilesLocalLink,'.pdf')}" title="Player Profiles report .pdf">
                                    <xsl:text>.pdf</xsl:text>
                                </a>
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                            -->
                        </td>
                        <td>
                            <a href="{concat($GameDesignLocalLink,'.html')}" title="Game Design report">
                                <img src="images/stoplights.png" height="100"/>
                            </a>
                            <br />
                            <a href="{concat($GameDesignLocalLink,'.html')}" title="Game Design report">
                                <xsl:text>Game Design</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="{//ApplicationURLs/Game/.}" target="{//Other/GameAcronym/.}Game" title="Play the game! go online">
                                <img src="images/MmowgliWelcomeSplash.png" height="100"/>
                            </a>
                            <br />
                            <a href="{//ApplicationURLs/Game/.}" target="{//Other/GameAcronym/.}Game" title="Play the game! go online">
                                <xsl:text>Play the </xsl:text>
                                <xsl:value-of select="//HeaderFooter/BrandingText/."/>
                                <xsl:text> game</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="{//ApplicationURLs/Game/.}/mobile" target="{//Other/GameAcronym/.}Game" title="Play the game! go mobile online">
                                <img src="images/mmowgli.nps.edu.{//Other/GameAcronym/.}.mobile.qr.png" height="100"/>
                            </a>
                            <br />
                            <a href="{//ApplicationURLs/Game/.}/mobile" target="{//Other/GameAcronym/.}Game" title="Play the game! go mobile online">
                                <xsl:text>Mobile</xsl:text>
                            </a>
                        </td>
                    </tr>
                    
                    <tr align="center" rowspan="3">
                        <td colspan="7">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                    <tr align="center" rowspan="3">
                        <td colspan="7">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                    
                    <tr align="center">
                        <td>
                            <a href="{substring-before(//HeaderFooter/BlogURL,'-blog')}">
                                <img src="images/PlayersPortal.png" height="100"/>
                            </a>
                            <br />
                            <a href="{substring-before(//HeaderFooter/BlogURL,'-blog')}">
                                <xsl:text>Players Portal</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="{//HeaderFooter/BlogURL}">
                                <img src="images/GameBlog.png" height="100"/>
                            </a>
                            <br />
                            <a href="{//HeaderFooter/BlogURL}">
                                <xsl:text>Game Blog</xsl:text>
                            </a>
                        </td>
                        <td valign="bottom">
                            <a href="{//ApplicationURLs/AlternateVideo/.}" title="Videos" target="_blank">
                                <img src="images/1424078129_device_camera_recorder_video_-128.png"/>
                            </a>
                            <br />
                            <a href="{//ApplicationURLs/AlternateVideo/.}">Videos</a>
                        </td>
                        <td>
                            <a href="https://twitter.com/mmowgli" title="Follow @MMOWGLI" target="_blank">
                                <img src="images/twitterLogoSquare.png" height="100"/>
                            </a>
                            <br />
                            <a href="https://twitter.com/mmowgli" title="Follow @MMOWGLI" target="_blank">
                                <xsl:text>@MMOWGLI</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="https://portal.mmowgli.nps.edu/reports" title="All published MMOWGLI Game Reports" target="_blank">
                                <img src="images/DirectoryProducts.png" height="100"/>
                            </a>
                            <br />
                            <a href="https://portal.mmowgli.nps.edu/reports" title="All published MMOWGLI Game Reports" target="_blank">
                                <xsl:text>All Reports</xsl:text>
                            </a>
                        </td>
                        <td valign="bottom">
                            <a href="https://portal.mmowgli.nps.edu/trouble" title="Trouble Report" target="_blank">
                                <img src="images/Oops.png"/>
                            </a>
                            <br />
                            <a href="https://portal.mmowgli.nps.edu/trouble" title="Trouble Report" target="_blank">
                                <xsl:text>Trouble Report</xsl:text>
                            </a>
                        </td>
                        <td>
                            <a href="{//ApplicationURLs/Game/.}/mobile" target="{//Other/GameAcronym/.}Game" title="Play the game! go mobile online">
                                <img src="images/AndroidHandheldTabletAlphaTestPhoto.jpg" height="100"/>
                            </a>
                            <br />
                            <a href="{//ApplicationURLs/Game/.}/mobile" target="{//Other/GameAcronym/.}Game" title="Play the game! go mobile online">
                                <xsl:text>Mobile</xsl:text>
                            </a>
                        </td>
                    </tr>
                    <tr align="center" rowspan="3">
                        <td colspan="7">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                </table>

                <h2>
                    <a name="DatabaseExports">Database Exports</a>
                </h2>
                
                <p>
                    <!-- no directory link -->
                    <!-- open-source license, https://www.iconfinder.com/icons/315116/file_xml_icon#size=128 -->
                    <img src="images/1428177355_699265-icon-37-file-xml-94.png" width="24" height="24" align="left"/>
                    <xsl:text> XML data files for this game are converted into web pages using XSLT stylesheets. </xsl:text>
                </p>

                <ul>
                    <li>
                        <a href="{concat($IdeaCardLocalLink,'.xml')}" title="Idea card chains: XML source">
                            <xsl:value-of select="concat($IdeaCardLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <a href="{concat($ActionPlanLocalLink,'.xml')}" title="Action Plans: XML source">
                            <xsl:value-of select="concat($ActionPlanLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <a href="{concat($PlayerProfilesLocalLink,'.xml')}" title="User List: XML source">
                            <xsl:value-of select="concat($PlayerProfilesLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                    <li>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                        <a href="{concat($GameDesignLocalLink,'.xml')}" title="Game Design: XML source">
                            <xsl:value-of select="concat($GameDesignLocalLink,'.xml')"/>
                        </a>
                        <xsl:text> XML source </xsl:text>
                    </li>
                </ul>
                
                <p>
                    <a href="data/" title="Database tables as text data" target="_blank">
                        <img src="images/Mimetypes-inode-directory-icon.png" width="24" height="24" align="left"/>
                    </a>
                    <xsl:text> The </xsl:text>
                    <a href="data/" title="database table text export">reports/data/</a>
                    <xsl:text> subdirectory contains database tables exported as tab-delimited text files. </xsl:text>
                </p>

                <ul>
                <li>
                    <xsl:text> To import a data table as an Excel spreadsheet: </xsl:text>
                    <ul>
                        <li>
                            <xsl:text> Open Excel and create a new worksheet. </xsl:text>
                        </li>
                        <li>
                            <xsl:text> Select the </xsl:text>
                            <i>Data</i>
                            <xsl:text> tab and then the </xsl:text>
                            <i>Import from Web</i>
                            <xsl:text> button. </xsl:text>
                        </li>
                        <li>
                            <xsl:text> Copy the link address for the table file of interest. </xsl:text>
                        </li>
                        <li>
                            <xsl:text> Tables of frequent interest: </xsl:text>
                            <a href="data/ActionPlan.txt" title="database table text export">ActionPlan.txt</a>
                            <xsl:text>, </xsl:text>
                            <a href="data/CardType.txt" title="database table text export">CardType.txt</a>
                            <xsl:text>, and </xsl:text>
                            <a href="data/User.txt" title="database table text export">User.txt</a>
                        </li>
                        <li>
                            <xsl:text>Follow the Excel import wizard instructions. </xsl:text>
                        </li>
                    </ul>
                </li>
                <li>
                    <xsl:text> TODO: automatically generate report spreadsheets and presentations. </xsl:text>
                </li>
            </ul>
            <p>
                <xsl:text> Contributions to the open-source MMOWGLI game platform are always welcome. </xsl:text>
            </p>

<!-- =================================================== Production Notes =================================================== -->

        <h2>
            <a name="ProductionNotes">Production Notes</a>
        </h2>

        <p>
            <xsl:text>These reports were produced from a game database export dated </xsl:text>
            <b>
                <xsl:value-of select="$exportDateTime"/>
            </b>
            <xsl:text>.</xsl:text>
        </p>

                <ul>
                    <li>
                        Player-provided information collected by the MMOWGLI game is automatically processed to produce these reports.
                    </li>
                    <li>
                        All data entries are saved immediately to an open-source MySql database.
                        Personal identifying information (PII) is encrypted and not shared in any reports.
                    </li>
                    <li>
                        Game-related information is exported to an XML data file, typically on an hourly basis while a game is active.
                        The XML file is simpler and easier for use by analytic tools than a database interface might be.
                    </li>
                    <li>
                        The XML data files are rapidly converted to HTML web pages via XSLT stylesheet transforms.
                    </li>
                    <li>
                        Each of the report pages include details regarding
                        <a href="https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms+and+Conditions">Terms and Conditions</a>
                        and the MMOWGLI
                        <a href="https://portal.mmowgli.nps.edu/license">open-source license</a>.
                    </li>
                </ul>

        <p>
            <xsl:text>Online at </xsl:text>
            
            <a href="{//ApplicationURLs/Game/.}/reports">
                <xsl:value-of select="//ApplicationURLs/Game/."/>
                <xsl:text>/reports</xsl:text>
            </a>
        </p>

<!-- =================================================== Contact =================================================== -->

        <h2>
            <a name="Contact">Contact</a>
        </h2>

        <ul>
            <li>
                Questions, suggestions and comments about these game products are welcome.
                Please provide a
                <a href="https://portal.mmowgli.nps.edu/trouble">Trouble Report</a>
                or send mail to
                <a href="mailto:mmowgli-trouble%20at%20nps.edu?subject=Reports%20Index%20feedback:%20{$gameLabel}"><i><xsl:text disable-output-escaping="yes">mmowgli-trouble at nps.edu</xsl:text></i></a>.
            </li>

            <li>
                Additional information is available online for the
                <a href="{$portalPage}"><xsl:value-of select="$gameLabel"/><xsl:text> game</xsl:text></a>
                and the
                <a href="https://portal.mmowgli.nps.edu">MMOWGLI project</a>.
            </li>
        </ul>

        <p>
<a href="https://www.nps.navy.mil/disclaimer" target="disclaimer">Official disclaimer</a>:
"Material contained herein is made available for the purpose of
peer review and discussion and does not necessarily reflect the
views of the Department of the Navy or the Department of Defense."
        </p>

                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
