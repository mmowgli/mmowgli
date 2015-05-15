<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:fn="w3.org/2005/xpath-functions">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu"
                xmlns:date="http://exslt.org/dates-and-times" -->
    <!-- default parameter values can be overridden when invoking this stylesheet -->
    <xsl:param name="singlePlayerNumber"></xsl:param>
    <!-- TODO displayHiddenPlayers: summaryOnly, true, false  -->
    <xsl:param name="displayHiddenPlayers">false</xsl:param>
    <!-- displayRoundNumber: all, 1, 2, etc.  -->
    <xsl:param name="displayRoundNumber">all</xsl:param>
    <xsl:param name="reportsDirectoryUrl"><xsl:text></xsl:text></xsl:param>

    <xsl:output method="html"/>
    <!-- <xsl:output method="xhtml" encoding="UTF-8" indent="yes"/> -->

    <!-- Global variables -->

    <!--
    <xsl:variable name="todaysDate">
        <xsl:value-of select="date:day-in-month()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="date:month-name()"/>
        <xsl:text> </xsl:text>
        <xsl:value-of select="date:year()"/>
    </xsl:variable>
    -->

    <xsl:variable name="gameTitle">
        <!-- Piracy2012, Piracy2011.1, Energy2012, etc. -->
        <xsl:value-of select="//GameTitle"/>
    </xsl:variable>

    <xsl:variable name="gameSecurity">
        <!-- open, FOUO, etc. -->
        <xsl:value-of select="//GameSecurity"/>
    </xsl:variable>

    <xsl:variable name="exportDateTime">
        <xsl:value-of select="//UserList/@exported"/>
    </xsl:variable>

    <xsl:variable name="numberOfRounds">
        <xsl:value-of select="max(//User[string-length(@registeredInMove) > 0]/@registeredInMove)"/>
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
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em') or contains($gameTitle,'Em2') or contains($gameTitle,'Em')">
                <xsl:text>em2 ElectroMagnetic Maneuver</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>evtp Edge Virtual Training Program</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>am Additive Manufacturing</xsl:text>
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
            <xsl:when test="contains($gameTitle,'blackswan') or contains(lower-case($gameTitle),'swan')">
                <xsl:text>blackswan</xsl:text>
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
    <xsl:variable name="gameDirectory">
        <!-- piracyMMOWGLI, energyMMOWGLI, etc. -->
        <xsl:choose>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.1')">
                <xsl:text>piracy2011.1</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.2')">
                <xsl:text>piracy2011.2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.3')">
                <xsl:text>piracy2011.3</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2012')">
                <xsl:text>piracy</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'nergy')">
                <xsl:text>energy</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                <xsl:text>bii</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'Em2')">
                <xsl:text>em2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>evtp</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>am</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>cap2con</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'dark')">
                <xsl:text>darkportal</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ig')">
                <xsl:text>ig</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'training')">
                <xsl:text>training</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'navair') or contains($gameTitle,'nsc')">
                <xsl:text>nsc</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'blackswan') or contains(lower-case($gameTitle),'swan')">
                <xsl:text>blackswan</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'dd') or contains(lower-case($gameTitle),'DD')">
                <xsl:text>dd</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'pcc') or contains(lower-case($gameTitle),'PCC')">
                <xsl:text>pcc</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ndu') or contains(lower-case($gameTitle),'NDU')">
                <xsl:text>ndu</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>uxvdm</xsl:text>
            </xsl:when>
            <xsl:when test="contains(lower-case($gameTitle),'mmowgli')">
                <xsl:value-of disable-output-escaping="yes" select="lower-case(substring-before(lower-case($gameTitle),'mmowgli'))"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of disable-output-escaping="yes" select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

        <!-- supported values:  true, false, summaryOnly -->
    <!-- it is sometimes useful to show hidden cards while quality/correctness review is in progress
    <xsl:variable name="displayHiddenPlayers">
        <xsl:choose>
            <xsl:when test="($gameTitle = 'whatever')">
                <xsl:text>true</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:text>summaryOnly</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
   -->

    <xsl:variable name="XmlSourceFileName">
        <xsl:text>PlayerProfiles_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>.xml</xsl:text>
    </xsl:variable>

    <xsl:variable name="IdeaCardChainLocalLink">
        <xsl:text>IdeaCardChain_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <!-- .html or .xml -->
    </xsl:variable>

    <xsl:variable name="ActionPlanLocalLink">
        <xsl:text>ActionPlanList_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <!-- .html or .xml -->
    </xsl:variable>

    <xsl:variable name="ReportsIndexLocalLink">
        <!-- supports game titles with spaces
        <xsl:text>index</xsl:text> -->
        <xsl:text>ReportsIndex_</xsl:text>
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

    <xsl:template name="UserList">
        <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
            <xsl:choose>
                <xsl:when test="(string-length($singlePlayerNumber) > 0)">
                    <xsl:apply-templates select="//User[@id = $singlePlayerNumber]"/>
                    <!--
                    <xsl:apply-templates select="descendant-or-self::User[@id = $singlePlayerNumber]"/>
                    -->
                </xsl:when>
                <xsl:when test="($displayHiddenPlayers = 'true')">
                    <xsl:apply-templates select="//User"/>
                    <!--
                    <xsl:apply-templates select="*"/>
                    -->
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="//User[not(@hidden='true')]"/>
                    <!--
                    <xsl:apply-templates select="*[not(@hidden='true')]"/>
                    -->
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>
    <xsl:template match="/">

        <!-- debug
        <xsl:message>
          <xsl:text>reportsDirectoryUrl=</xsl:text>
          <xsl:value-of select="$reportsDirectoryUrl"/>
          <xsl:text>, gameDirectory=</xsl:text>
          <xsl:value-of select="$gameDirectory"/>
        </xsl:message>
        -->

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

        <!-- debug
        <xsl:message>
          <xsl:text>innovateCardColor=</xsl:text>
          <xsl:value-of select="$innovateCardColor"/>
          <xsl:text>, defendCardColor=</xsl:text>
          <xsl:value-of select="$defendCardColor"/>
        </xsl:message>
        -->

        <html>
            <head>
            <!-- TODO
                <meta name="identifier" content="http:// TODO /IdeaCardChains.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta http-equiv="refresh" content="600"/> <!-- 600 seconds = 10 minutes -->
                <meta name="author"      content="Don Brutzman and Mike Bailey"/>
                <meta name="description" content="Idea card chain outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="PlayerProfiles_{$gameTitle}.html"/>
                <meta name="identifier"  content="https://mmowgli.nps.edu/{$gameAcronym}/IdeaCardChains_{$gameTitle}.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, https://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, https://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>

                <xsl:element name="title">
                    <xsl:text disable-output-escaping="yes">Player Profiles Report, </xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                    <xsl:text> MMOWGLI game</xsl:text>
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
    width:60px;
}
td.cardCellSmall {
    align:center;
    width:60px;
    font:35%;
    color:white;
}
td.longtext {
    /* white-space: nowrap; */
    overflow: hidden;
}
.innovateStrategy {
    background-color:#00ab4f;
}
.defendStrategy {
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
.lightgreylink {
a:link    {color:lightgrey;}  /* unvisited link */
a:visited {color:lightgrey;}  /* visited link */
a:hover   {color:lightgrey;}  /* mouse over link */
a:active  {color:lightgrey;}  /* selected link */
text-shadow:; /* off */
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
                        <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'EM2')">
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
                            <xsl:value-of select="$gameLabel"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

        <!-- page header -->
        <xsl:if test="(string-length($singlePlayerNumber) = 0)">

                    <table align="center" border="0" class="banner">
                        <tr border="0">
                            <td>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            </td>
                            <td align="center">
                                <h1 align="center">
                                        <xsl:text> Player Profiles Report </xsl:text>
                                </h1>
                                <h2 align="center">
                                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/> <!-- want escaped <br /> intact for line break -->
                                    <xsl:text> Game </xsl:text>
                                </h2>
                            </td>
                            <td>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp; </xsl:text>
                            </td>
                            <td align="left">
                                <blockquote>
                                    <a href="{$portalPage}" title="Game documentation for {$gameLabel}" target="_blank">
                                        <!-- 1158 x 332 -->
                                        <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="386" height="111" border="0"/>
                                    </a>
                                    <br />
                                </blockquote>
                            </td>
                            <td>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            </td>
                            <td rowspan="2" valign="middle">
                                <xsl:text> Corresponding report products: </xsl:text>
                                <ul>
                                    <li>
                                        <a href="index.html" title="Reports Index for this game"><xsl:text>Reports Index</xsl:text></a>
                                    </li>
                                    <xsl:if test="string-length($ActionPlanLocalLink) > 0">
                                        <li>
                                            <a href="{$ActionPlanLocalLink}.html">Action Plans</a>
                                        </li>
                                    </xsl:if>
                                    <li>
                                        <a href="{$IdeaCardChainLocalLink}.html">Idea Card Chains</a>
                                        <xsl:text> and </xsl:text>
                                        <!-- Sunburst Visualizer -->
                                        <a href="cardSunburstVisualizer.html" title="Idea Card Sunburst Visualizer">
                                            <xsl:text disable-output-escaping="yes">Sunburst&amp;nbsp;Visualizer</xsl:text>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="https://portal.mmowgli.nps.edu/reports" target="blank">All MMOWGLI Game Reports</a>
                                    </li>
                                </ul>
                            </td>
                        </tr>
                        <tr valign="top">
                            <td colspan="2">
                                <blockquote>
                                    <ul>
                                        <li>
                                            <a href="#AffiliationTable" title="Affiliation categories offered to players">
                                                <xsl:text>Affiliation Table</xsl:text>
                                            </a>
                                            <xsl:text> describing player demographics</xsl:text>
                                        </li>
                                        <li>
                                            <a href="#AwardsTable" title="Motivation and purpose for this game">
                                                <xsl:text>Awards Table</xsl:text>
                                            </a>
                                            <xsl:text disable-output-escaping="yes"> describing Player Recognition by Game&amp;nbsp;Masters </xsl:text>
                                        </li>
                                        <li>
                                            <a href="#BadgesTable" title="Badges for automatic recognition of player contributions in the game">
                                                <xsl:text>Badges Table</xsl:text>
                                            </a>
                                            <xsl:text> describing Player Recognition for Game Activity</xsl:text>
                                        </li>
                                    </ul>
                                </blockquote>
                            </td>
                            <td colspan="2">
                                <blockquote>
                                    <ul>
                                        <li>
                                            <!-- TOC links for players, blockquote appears below -->
                                            <a href="#PlayersList" title="List of all registered players, in alphabetic order">
                                                <xsl:text>Players List</xsl:text>
                                            </a>
                                            <xsl:text> (</xsl:text>
                                            <b>
                                                <xsl:value-of select="count(//User)"/>
                                                <xsl:text> total players </xsl:text>
                                            </b>
                                                <xsl:text> with </xsl:text>
                                            <b>
                                                <xsl:value-of select="count(//User[starts-with(@gameName,'gm_')])"/>
                                                <xsl:text> game masters</xsl:text>
                                            </b>
                                            <xsl:text>)</xsl:text>
                                        </li>
                                        <li>
                                            <!-- Table of Contents -->
                                            <xsl:if test="(//CallToAction/*)">
                                                  <a href="#CallToAction" title="Motivation and purpose for this game">
                                                      <xsl:text>Call To Action</xsl:text>
                                                  </a>
                                                  <xsl:text>: Player Motivation</xsl:text>
                                                  <br />
                                            </xsl:if>
                                        </li>
                                        <li>
                                            <a href="#License" title="License, Terms and Conditions for this content"><xsl:text>License, Terms, Conditions</xsl:text></a>
                                            and
                                            <a href="#Contact" title="Contact links for further information"><xsl:text>Contact</xsl:text></a>
                                        </li>
                                    </ul>
                                </blockquote>
                            </td>
                        </tr>
                    </table>
                    <table align="center" border="0" class="banner" width="95%">
                        <tr cellspacing="4" cellpadding="4">
                            <td cellspacing="8" cellpadding="8" style="background-color:lightgrey;">
                                    <xsl:for-each select="//User">
                                        <xsl:sort select="lower-case(@gameName)" data-type="text" order="ascending"/>
                                        <a href="#Player{@id}" title="Player{@id} {@gameName}">
                                          <xsl:value-of select="@gameName"/>
                                        </a>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
                                        <xsl:text> </xsl:text>
                                    </xsl:for-each>
                            </td>
                        </tr>
                    </table>
        <hr />

        <h2 title="Affiliation categories offered to players">
            <a name="AffiliationTable">
                <xsl:text> Affiliation Table: Player Demographics </xsl:text>
            </a>
            <a href="#index" title="to top">
                <!-- 1158 x 332, width="386" height="111"  -->
                <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
            </a>
        </h2>
        <!-- TODO Affiliation code change: omit duplicate entries, omit 'Please Select' -->
        <p>
            <table border="1" cellpadding="2">
                <tr>
                    <th>Affiliation Category</th>
                    <!-- debug
                    <xsl:message>
                        <xsl:text>$numberOfRounds=</xsl:text>
                        <xsl:value-of select="$numberOfRounds"/>
                        <xsl:text>. </xsl:text>
                        <xsl:text>Missing @registeredInMove attribute </xsl:text>
                        <xsl:value-of select="count(//User[string-length(@registeredInMove) = 0])"/>
                        <xsl:text> nodes </xsl:text>
                    </xsl:message>
                    -->
                    <!-- crude but effective approach since node-list filtering difficult when some values missing -->
                    <xsl:choose>
                        <xsl:when test="$numberOfRounds = 1">
                            <!-- omit -->
                        </xsl:when>
                        <xsl:otherwise>
                           <th>Round 1</th>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="$numberOfRounds >= 2">
                        <th>Round 2</th>
                    </xsl:if>
                    <xsl:if test="$numberOfRounds >= 3">
                        <th>Round 3</th>
                    </xsl:if>
                    <xsl:if test="$numberOfRounds >= 4">
                        <th>Round 4</th>
                    </xsl:if>
                    <xsl:if test="$numberOfRounds >= 5">
                        <th>Round 5</th>
                    </xsl:if>
                    <xsl:if test="$numberOfRounds >= 6">
                        <th>Round 6</th>
                    </xsl:if>
                    <th>Overall</th>
                </tr>
                <xsl:for-each select="//AffiliationDefaults/Affiliation">
                    <xsl:variable name="affiliation">
                        <xsl:choose>
                            <xsl:when test="(string-length(normalize-space(.)) = 0) or (normalize-space(.) = 'Please Select')">
                                <xsl:text>(not provided)</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="." disable-output-escaping="yes"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <tr>
                        <td>
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </td>
                        <!-- crude but effective approach since node-list filtering difficult when some values missing -->
                        <xsl:choose>
                            <xsl:when test="$numberOfRounds = 1">
                                <!-- omit -->
                            </xsl:when>
                            <xsl:otherwise>
                               <td align="right">
                                   <xsl:value-of select="count(//User[(@registeredInMove='1') or (string-length(@registeredInMove)=0) or not(@registeredInMove)]/Affiliation[contains(.,$affiliation)])"/>
                               </td>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="$numberOfRounds >= 2">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='2']/Affiliation[contains(.,$affiliation)])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 3">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='3']/Affiliation[contains(.,$affiliation)])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 4">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='4']/Affiliation[contains(.,$affiliation)])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 5">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='5']/Affiliation[contains(.,$affiliation)])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 6">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='6']/Affiliation[contains(.,$affiliation)])"/>
                            </td>
                        </xsl:if>
                        <!-- overall -->
                        <td align="right">
                            <xsl:value-of select="count(//User/Affiliation[contains(.,$affiliation)])"/>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                    </tr>
                </xsl:for-each>
                <tr>

                        <td>
                            <xsl:text>(Different affiliation)</xsl:text>
                        </td>
                        <td align="right">
                            <!-- TODO
                            <xsl:value-of select="count(//User/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            -->
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                </tr>
                <tr>

                        <td>
                            <xsl:text>(No affiliation given)</xsl:text>
                        </td>
                        <!-- crude but effective approach since node-list filtering difficult when some values missing -->
                        <xsl:choose>
                            <xsl:when test="$numberOfRounds = 1">
                                <!-- omit -->
                            </xsl:when>
                            <xsl:otherwise>
                               <td align="right">
                                   <xsl:value-of select="count(//User[(@registeredInMove='1') or (string-length(@registeredInMove)=0) or not(@registeredInMove)]/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                               </td>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="$numberOfRounds >= 2">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='2']/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 3">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='3']/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 4">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='4']/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 5">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='5']/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            </td>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 6">
                            <td align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='6']/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            </td>
                        </xsl:if>
                        <!-- overall -->
                        <td align="right">
                            <xsl:value-of select="count(//User/Affiliation[(string-length(normalize-space(.))=0) or (normalize-space(.)=' ')])"/>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                </tr>
                <tr>
                        <th>
                            <xsl:text>Total Players</xsl:text>
                        </th>
                        <!-- crude but effective approach since node-list filtering difficult when some values missing -->
                        <xsl:choose>
                            <xsl:when test="$numberOfRounds = 1">
                                <!-- omit -->
                            </xsl:when>
                            <xsl:otherwise>
                               <th align="right">
                                   <xsl:value-of select="count(//User[(@registeredInMove='1') or (string-length(@registeredInMove)=0) or not(@registeredInMove)])"/>
                               </th>
                            </xsl:otherwise>
                        </xsl:choose>
                        <xsl:if test="$numberOfRounds >= 2">
                            <th align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='2'])"/>
                            </th>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 3">
                            <th align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='3'])"/>
                            </th>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 4">
                            <th align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='4'])"/>
                            </th>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 5">
                            <th align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='5'])"/>
                            </th>
                        </xsl:if>
                        <xsl:if test="$numberOfRounds >= 6">
                            <th align="right">
                                <xsl:value-of select="count(//User[@registeredInMove='6'])"/>
                            </th>
                        </xsl:if>
                        <!-- overall -->
                        <th align="right">
                            <xsl:value-of select="count(//User)"/>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </th>
                </tr>
            </table>
        </p>
        <br />
        <hr />

        <h2 title="Awards by game masters, recognizing major player achievements in the game">
            <a name="AwardsTable">
                <xsl:text disable-output-escaping="yes"> Awards Table: Player Recognition by Game&amp;nbsp;Masters </xsl:text>
            </a>
            <a href="#index" title="to top">
                <!-- 1158 x 332, width="386" height="111"  -->
                <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
            </a>
        </h2>
        <p>
            <table border="1" cellpadding="2">
                <xsl:for-each select="//AwardTypes/AwardType">
                    <!-- TODO omit listing awards not actually given to players -->
                    <xsl:sort select="@type" data-type="number" order="ascending"/>

                    <xsl:variable name="awardIconUrl" select="@iconUrl"/>
                    <xsl:variable name="awardType"  select="@type"/>
                    <xsl:variable name="awardCount" select="count(//User/Awards/Award[@type = $awardType])"/>

                    <xsl:if test="($awardCount > 0)">
                        <tr>
                            <!--
                            <td>
                                <xsl:value-of select="@type"/>
                                <xsl:text>.</xsl:text>
                            </td>
                            -->
                            <td>
                                <a name="Award{@type}">
                                    <img src="{$awardIconUrl}" title="{@description}" width="55" height="55"/>
                                </a>
                            </td>
                            <td>
                                <i>
                                    <xsl:value-of select="@name"/>
                                    <xsl:text>.</xsl:text>
                                </i>
                            </td>
                            <td>
                                <xsl:value-of select="@description"/>
                            </td>
                            <td>
                                <i>
                                    <xsl:value-of select="$awardCount"/>
                                    <xsl:text> awards presented: </xsl:text>
                                </i>
                                <xsl:for-each select="//User[Awards/Award/@type = $awardType]">
                                    <xsl:sort select="lower-case(@gameName)" data-type="text" order="ascending"/>

                                    <a href="#Player{@id}" title="bookmark: Player{@id} {@gameName}">
                                        <xsl:value-of select="@gameName"/>
                                    </a>
                                    <xsl:if test="not(position() = last())">
                                        <xsl:text>, </xsl:text>
                                    </xsl:if>
                                </xsl:for-each>
                            </td>
                        </tr>
                    </xsl:if>
                </xsl:for-each>

                <!-- otherwise -->
                <xsl:if test="(count(//User/Awards/Award) = 0)">
                    <tr>
                        <td>
                            <xsl:text>No awards have been presented in this game.</xsl:text>
                        </td>
                    </tr>
                </xsl:if>
            </table>
        </p>
        <br />
        <hr />

        <h2 title="Badges for automatic recognition of player activity in the game">
            <a name="BadgesTable">
                <xsl:text> Badges Table: Player Recognition for Game Activity </xsl:text>
            </a>
            <a href="#index" title="to top">
                <!-- 1158 x 332, width="386" height="111"  -->
                <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
            </a>
        </h2>
        <p>
            <table border="1" cellpadding="2">
                <xsl:for-each select="//BadgeTypes/BadgeType">
                    <xsl:sort select="@type" data-type="number" order="ascending"/>

                    <xsl:variable name="badgeType"  select="@type"/>
                    <xsl:variable name="badgeCount" select="count(//User/Badges/Badge[@type = $badgeType])"/>

                    <xsl:variable name="badgeIconUrl">
                        <!-- used by BadgesTable and Player Badges -->
                        <xsl:choose>
                            <xsl:when test="@iconUrl">
                                <xsl:value-of select="@iconUrl"/>
                            </xsl:when>
                            <xsl:when test="@type">
                                <!-- example: https://web.mmowgli.nps.edu/mmowMedia/images/badge1_55w55h.png -->
                                <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/badge</xsl:text>
                                <xsl:value-of select="@type"/>
                                <xsl:text>_55w55h.png</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/badge_empty_55w55h.png</xsl:text>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <tr>
                        <td>
                                <xsl:value-of select="@type"/>
                                <xsl:text>.</xsl:text>
                        </td>
                        <td>
                            <a name="Badge{@type}">
                                <img src="{$badgeIconUrl}" title="{@description}" width="55" height="55"/>
                            </a>
                        </td>
                        <td>
                            <i>
                                <xsl:value-of select="@name"/>
                                <xsl:text>.</xsl:text>
                            </i>
                        </td>
                        <td>
                            <xsl:value-of select="@description"/>
                        </td>
                        <td width="50%">
                            <i>
                                <xsl:value-of select="$badgeCount"/>
                                <xsl:text> players earned this badge</xsl:text>
                            </i>
                            <xsl:choose>
                                <xsl:when test="($badgeCount > 0)">
                                    <xsl:text>: </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text>.</xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:for-each select="//User[Badges/Badge/@type = $badgeType]">
                                <xsl:sort select="lower-case(@gameName)" data-type="text" order="ascending"/>

                                <a href="#Player{@id}" title="bookmark: Player{@id} {@gameName}">
                                    <xsl:value-of select="@gameName"/>
                                </a>
                                <xsl:if test="not(position() = last())">
                                    <xsl:text>, </xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </td>
                </tr>
                </xsl:for-each>

                <!-- otherwise -->
                <xsl:if test="(count(//User/Badges/Badge) = 0)">
                    <tr>
                        <td>
                            <xsl:text>No badges have been earned in this game.</xsl:text>
                        </td>
                    </tr>
                </xsl:if>
            </table>
        </p>
        <br />

            <!-- Now provide Call To Action, if available -->
            
            <xsl:text>&#10;</xsl:text>
            <a name="CallToAction"><xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text></a>
            <xsl:text>&#10;</xsl:text>
            
            <xsl:for-each select="//CallToAction"> <!-- [not(@round = following-sibling::CallToAction/@round)] -->
                
                <xsl:variable name="videoYouTubeID">
                    <xsl:choose>
                        <xsl:when           test="VideoYouTubeID">
                            <xsl:value-of select="VideoYouTubeID"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text></xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="videoAlternateUrl">
                    <xsl:choose>
                        <xsl:when           test="VideoAlternateUrl">
                            <xsl:value-of select="VideoAlternateUrl"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text></xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="callToActionBriefingSummary">
                    <xsl:choose>
                        <xsl:when           test="BriefingSummary">
                            <xsl:value-of select="BriefingSummary"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text></xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="callToActionBriefingText">
                    <xsl:choose>
                        <xsl:when           test="BriefingText">
                            <xsl:value-of select="BriefingText"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text></xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="orientationSummary">
                    <xsl:choose>
                        <xsl:when           test="OrientationSummary">
                            <xsl:value-of select="OrientationSummary"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text></xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <hr />
                
                <h1 style="background-color:lightgray;" align="center">
                    <a name="CallToActionRound{@round}"> 
                        <xsl:text>Call To Action</xsl:text>
                        <xsl:if test="//CallToAction[@round = '2']">
                            <xsl:text>, Round </xsl:text>
                            <xsl:value-of select="@round"/>
                        </xsl:if>
                    </a>
                </h1>
                <hr />
                
        <table align="center" width="100%">
            <tr align="top">
                <td align="left">
                    <h2 title="Motivation and purpose for this game">
                        <a name="CallToAction">
                            <xsl:text> Call to Action: Player Motivation </xsl:text>
                        </a>
                    </h2>
                    <!-- Debug diagnostic
                    <xsl:comment><xsl:text>$gameLabel=</xsl:text><xsl:value-of select="$gameLabel"/></xsl:comment> -->
                </td>
                <td>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                </td>
                <td align="right" valign="top">
                    <a href="#index" title="to top">
                        <!-- 1158 x 332, width="386" height="111"  -->
                        <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                    </a>
                </td>
            </tr>
        </table>
        <table align="center" width="100%">
            <tr>
                <td>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                </td>
                <td align="left" valign="top">
                    <!-- 80% of height="315" width="560" -->

                    <object height="252" width="448">
                        <param name="movie" value="https://www.youtube.com/v/{normalize-space($videoYouTubeID)}?version=3&amp;hl=en_US&amp;rel=0" />
                        <param name="allowFullScreen" value="true" />
                        <param name="allowscriptaccess" value="always" />
                        <embed height="252" width="448" allowfullscreen="true" allowscriptaccess="always" src="https://www.youtube.com/v/{normalize-space($videoYouTubeID)}?version=3&amp;hl=en_US&amp;rel=0" type="application/x-shockwave-flash" ></embed>
                    </object>
                    <xsl:if test="string-length(normalize-space($videoAlternateUrl)) > 0">
                        <br />
                        (No video? Try
                        <a href="{normalize-space($videoAlternateUrl)}" target="_blank">this</a>)
                    </xsl:if>
                </td>
                <td>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                </td>
                <td valign="top">

                    <xsl:if test="string-length(normalize-space($callToActionBriefingSummary)) > 0">
                        <h3>
                            <xsl:value-of select="$callToActionBriefingSummary" disable-output-escaping="yes"/>
                        </h3>
                    </xsl:if>

                    <xsl:if test="string-length(normalize-space($callToActionBriefingText)) > 0">
                        <xsl:value-of select="$callToActionBriefingText" disable-output-escaping="yes"/>
                    </xsl:if>

                    <xsl:if test="string-length(normalize-space($orientationSummary)) > 0">
                        <xsl:value-of select="$orientationSummary" disable-output-escaping="yes"/>
                    </xsl:if>

                    <p>
                        The
                        <xsl:choose>
                            <xsl:when test="contains($gameTitle,'nergy')">
                                    <a href="https://portal.mmowgli.nps.edu/energy-welcome">energyMMOWGLI Portal</a>
                            </xsl:when>
                            <xsl:when test="contains($gameTitle,'iracy')">
                                    <a href="https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Piracy+MMOWGLI+Games">piracyMMOWGLI Portal</a>
                            </xsl:when>
                            <xsl:otherwise>
                                    <a href="https://portal.mmowgli.nps.edu">MMOWGLI Portal</a>
                            </xsl:otherwise>
                        </xsl:choose>
                        contains further game information.
                    </p>
                </td>
            </tr>
        </table>
        </xsl:for-each>
        <br />
        <hr />
        <!-- Call To Action complete -->

        </xsl:if>
        <!-- single-player table -->

        <!-- Process User elements -->

        <h1 style="background-color:lightgray;" align="center" title="List of all registered players, in alphabetic order">
            <a name="PlayersList">
                <xsl:text> Players List </xsl:text>
            </a>
        </h1>
        <table border="1" width="100%" cellpadding="1">
            <xsl:for-each select="//User">
                <xsl:sort select="lower-case(@gameName)" data-type="text" order="ascending"/>

                <xsl:variable name="avatarIconUrl">
                    <xsl:choose>
                        <xsl:when test="@iconUrl">
                            <xsl:value-of select="@iconUrl"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/avatars/bulb.jpg</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <tr valign="top">
                    <!-- ================================================================================== -->
                    <!-- Player avatar icon -->
                    <td align="left">
                        <a name="Player{@id}">
                            <a href="#Player{@id}" title="bookmark: Player{@id} {@gameName}">
                                <img align="center" src="{$avatarIconUrl}" width="85" height="85" border="0"/>
                            </a>
                        </a>
                    </td>
                    <!-- ================================================================================== -->
                    <td align="left" width="33%">
                            <a href="#Player{@id}" title="bookmark: Player{@id} {@gameName}">
                                <xsl:text> Player </xsl:text>
                                <xsl:value-of select="@id"/>
                            </a>
                            <xsl:text>: </xsl:text>

                            <b>
                                <!-- Alternate #Player_gameName bookmark link for use by Idea Card and Action Plan reports -->
                                <a name="Player_{@gameName}">
                                    <xsl:value-of select="@gameName"/>
                                </a>
                            </b>
                            
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!91_{@id}" target="{$gameAcronym}Game" title="play the game! go online to Player {@id}">
                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" width="16px" border="0"/>
                            </a>

                            <ul>
                                <!-- ================================================================================== -->
                                <xsl:if test="(@isGameAdministrator='true') or (@isGameDesigner='true') or (@isGameMaster='true')">
                                    <li>
                                        <i>Roles: </i>
                                        <xsl:if test="(@isGameAdministrator='true')">
                                            <xsl:text> Administrator</xsl:text>
                                        </xsl:if>
                                        <xsl:if test="(@isGameDesigner='true')">
                                            <xsl:if test="(@isGameAdministrator='true')">
                                                <xsl:text>,</xsl:text>
                                            </xsl:if>
                                            <xsl:text> GameDesigner</xsl:text>
                                        </xsl:if>
                                        <xsl:if test="(@isGameMaster='true')">
                                            <xsl:if test="(@isGameAdministrator='true') or (@isGameDesigner='true')">
                                                <xsl:text>,</xsl:text>
                                            </xsl:if>
                                            <xsl:text> GameMaster</xsl:text>
                                        </xsl:if>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="string-length(Affiliation) > 0">
                                    <li>
                                        <xsl:variable name="affiliation">
                                            <xsl:choose>
                                                <xsl:when test="contains(normalize-space(Affiliation/.),'Please select:') and (string-length(normalize-space(Affiliation/.)) gt 14)">
                                                    <xsl:value-of select="normalize-space(concat(substring-before((Affiliation/.),'Please select:'),' ',substring-after((Affiliation/.),'Please select:')))"/>
                                                </xsl:when>
                                                <xsl:when test="(string-length(normalize-space(Affiliation/.)) = 0) or contains(normalize-space(Affiliation/.),'Please select:')">
                                                    <xsl:text>(not provided)</xsl:text>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="Affiliation"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:variable>
                                        <i>Affiliation: </i>
                                        <!-- TODO check for duplicated value, cleanup in game software -->
                                        <xsl:value-of select="$affiliation"/>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="string-length(Expertise) > 0">
                                    <li>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        <i>Expertise: </i>
                                        <xsl:value-of select="Expertise"/>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="string-length(Location) > 0">
                                    <li>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
                                        <i>Location: </i>
                                        <xsl:value-of select="Location"/>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="string-length(@registeredInMove) > 0">
                                    <li>
                                        <i>Registered: </i>
                                        <xsl:value-of select="@registrationDate"/>
                                        <xsl:text disable-output-escaping="yes"> during&amp;nbsp;Round&amp;nbsp;</xsl:text>
                                        <xsl:value-of select="@registeredInMove"/>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="count(Scores/MoveScores) > 0">
                                    <li>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        <i>Scores: </i>
                                        <!-- TODO tighter formatting if only one round -->
                                        <table border="1" cellpadding="2">
                                            <tr>
                                                <th valign="bottom" align="center">Round</th>
                                                <th><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text></th>
                                                <th valign="bottom" align="right">Rank</th>
                                                <th>Idea Card Exploration </th>
                                                <th><xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text></th>
                                                <th valign="bottom" align="right">Rank</th>
                                                <th>Action Plan Implementation</th>
                                            </tr>
                                            <xsl:for-each select="Scores/MoveScores">
                                                <tr align="center">
                                                    <td>
                                                        <xsl:value-of select="@move"/>
                                                    </td>
                                                    <td>
                                                        <!-- empty -->
                                                    </td>
                                                    <!-- Idea Card ranking, points -->
                                                    <td>
                                                        <xsl:if test="@basic > 0">
                                                            <!-- fix column width for readability -->
                                                            <xsl:call-template name="threeDigitSpacing">
                                                                <xsl:with-param name="value">
                                                                    <xsl:value-of select="@basicRank"/>
                                                                </xsl:with-param>
                                                            </xsl:call-template>
                                                            <b>
                                                                <xsl:text disable-output-escaping="yes">#&amp;nbsp;</xsl:text>
                                                                <xsl:value-of select="format-number(@basicRank,'##0')"/>
                                                            </b>
                                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                                        </xsl:if>
                                                    </td>
                                                    <td align="center">
                                                        <xsl:call-template name="fourDigitSpacing">
                                                            <xsl:with-param name="value">
                                                                <xsl:value-of select="@basic"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                        <b>
                                                            <xsl:value-of select="format-number(@basic,'##0')"/>
                                                        </b>
                                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;points</xsl:text>
                                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                                    </td>
                                                    <td>
                                                        <!-- empty -->
                                                    </td>
                                                    <!-- Action Plan ranking, points -->
                                                    <td>
                                                        <xsl:if test="@implementation > 0">
                                                            <!-- fix column width for readability -->
                                                            <xsl:call-template name="threeDigitSpacing">
                                                                <xsl:with-param name="value">
                                                                    <xsl:value-of select="@implementationRank"/>
                                                                </xsl:with-param>
                                                            </xsl:call-template>
                                                            <b>
                                                                <xsl:text disable-output-escaping="yes">#&amp;nbsp;</xsl:text>
                                                                <xsl:value-of select="format-number(@implementationRank,'##0')"/>
                                                            </b>
                                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                                        </xsl:if>
                                                    </td>
                                                    <td align="center">
                                                        <xsl:call-template name="fourDigitSpacing">
                                                            <xsl:with-param name="value">
                                                                <xsl:value-of select="@implementation"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                        <b>
                                                            <xsl:value-of select="format-number(@implementation,'##0')"/>
                                                        </b>
                                                        <xsl:text> points</xsl:text>
                                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                                    </td>
                                                </tr>
                                            </xsl:for-each>
                                        </table>
                                    </li>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:variable name="numberAwards" select="count(Awards/Award)"/>
                                <xsl:if test="Awards and ($numberAwards > 0)">
                                    <li>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        <i>
                                            <xsl:text>Award</xsl:text>
                                            <xsl:if test="$numberAwards ne 1">
                                                <xsl:text>s</xsl:text>
                                            </xsl:if>
                                            <xsl:if test="$numberAwards ne 0">
                                                <xsl:text> Recognition by Game Masters</xsl:text>
                                            </xsl:if>
                                            <xsl:text>: </xsl:text>
                                        </i>
                                        <xsl:choose>
                                            <xsl:when test="$numberAwards eq 0">
                                                <!-- not reachable -->
                                                <xsl:text> none </xsl:text>
                                            </xsl:when>
                                            <xsl:when test="$numberAwards eq 1">
                                                <!-- omit -->
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="$numberAwards"/>
                                                <xsl:text> total </xsl:text>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </li>
                                    <table border="1" cellpadding="2">
                                        <xsl:for-each select="Awards/Award">
                                            <xsl:sort select="@round" data-type="number" order="ascending"/>
                                            <xsl:variable name="awardType" select="@type"/>
                                            <xsl:variable name="awardIconUrl">
                                                <!-- used by AwardsTable and Player Awards -->
                                                <xsl:choose>
                                                    <xsl:when test="//AwardTypes/AwardType[@type = $awardType]/@iconUrl">
                                                        <xsl:value-of select="//AwardTypes/AwardType[@type = $awardType]/@iconUrl"/>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '1')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/achievement55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '2')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/commendation55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '3')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/legion55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <!-- TODO
                                                    <xsl:when test="(@type = '4')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '5')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '6')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '7')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/</xsl:text>
                                                    </xsl:when>
                                                    -->
                                                    <xsl:when test="(@type = '8')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '9')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '10')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '11')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/BiiAwardCertificateWatermarked55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '12')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/cap2con1stPlaceRnd1_55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '13')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/cap2con2ndPlaceRnd1_55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '14')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/cap2con3rdPlaceRnd1_55x55.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '15')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/cap2con_badge_wk_2_1st_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '16')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/cap2con_badge_wk_2_2nd_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '17')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/Stealth_Badge_a_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '18')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/FrontlineBadge_a_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:when test="(@type = '19')">
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/CourageBadge_a_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/badge_empty_55w55h.png</xsl:text>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:variable>
                                            <xsl:variable name="awardDescription">
                                                <!--
                                                <xsl:value-of select="//AwardTypes/AwardType[@type = $awardType]/@name"/>
                                                <xsl:text>. </xsl:text>
                                                -->
                                                <xsl:value-of select="//AwardTypes/AwardType[@type = $awardType]/@description"/>
                                            </xsl:variable>
                                            <tr>
                                                <td>
                                                    <xsl:value-of select="$awardType"/>
                                                </td>
                                                <td>
                                                    <!-- link to Awards table -->
                                                    <a href="#Award{@type}" border="0">
                                                        <img src="{$awardIconUrl}" title="{$awardDescription}" width="55" height="55"/>
                                                    </a>
                                                </td>
                                                <td>
                                                    <xsl:value-of select="normalize-space($awardDescription)"/>
                                                    <xsl:if test="not(ends-with(normalize-space($awardDescription),'.'))">
                                                        <xsl:text>. </xsl:text>
                                                    </xsl:if>
                                                    <xsl:variable name="awardUrl" select="normalize-space(url)"/>
                                                    <xsl:if test="string-length($awardUrl) > 0">
                                                        <br />
                                                        <xsl:text> See the </xsl:text>
                                                        <a href="{$awardUrl}" target="_blank">
                                                            <xsl:text>award announcement</xsl:text>
                                                        </a>
                                                        <xsl:text> to learn more. </xsl:text>
                                                    </xsl:if>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </table>
                                </xsl:if>
                                <!-- ================================================================================== -->
                                <xsl:if test="Badges">
                                    <li>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        <xsl:variable name="numberBadges" select="count(Badges/Badge)"/>
                                        <i>
                                            <xsl:text>Badge</xsl:text>
                                            <xsl:if test="$numberBadges ne 1">
                                                <xsl:text>s</xsl:text>
                                            </xsl:if>
                                            <xsl:if test="$numberBadges ne 0">
                                                <xsl:text> Recognition for Game Activity</xsl:text>
                                            </xsl:if>
                                            <xsl:text>: </xsl:text>
                                        </i>
                                        <xsl:choose>
                                            <xsl:when test="$numberBadges eq 0">
                                                <xsl:text> none </xsl:text>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="$numberBadges"/>
                                                <xsl:text> total </xsl:text>
                                                <br />
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:for-each select="Badges/Badge">
                                            <xsl:variable name="badgeType" select="@type"/>
                                            <xsl:variable name="badgeIconUrl">
                                                <!-- used by BadgesTable and Player Badges -->
                                                <xsl:choose>
                                                    <xsl:when test="//BadgeTypes/BadgeType[@type = $badgeType]/@iconUrl">
                                                        <xsl:value-of select="//BadgeTypes/BadgeType[@type = $badgeType]/@iconUrl"/>
                                                    </xsl:when>
                                                    <xsl:when test="@type">
                                                        <!-- example: https://web.mmowgli.nps.edu/mmowMedia/images/badge1_55w55h.png -->
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/badge</xsl:text>
                                                        <xsl:value-of select="@type"/>
                                                        <xsl:text>_55w55h.png</xsl:text>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:text>https://web.mmowgli.nps.edu/mmowMedia/images/badge_empty_55w55h.png</xsl:text>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:variable>
                                            <xsl:variable name="badgeDescription">
                                                <xsl:value-of select="//BadgeTypes/BadgeType[position() = $badgeType]/@name"/>
                                                <xsl:text>. </xsl:text>
                                                <xsl:value-of select="//BadgeTypes/BadgeType[position() = $badgeType]/@description"/>
                                            </xsl:variable>
                                            <!-- TODO link to table -->
                                            <a href="#Badge{@type}" border="0">
                                                <img src="{$badgeIconUrl}" title="{$badgeDescription}" width="55" height="55"/>
                                            </a>
                                        </xsl:for-each>
                                    </li>
                                </xsl:if>
                            </ul>
                    </td>
                    <!-- ================================================================================== -->
                    <td align="left" width="33%">
                            <i>
                                <xsl:text> Idea cards: </xsl:text>
                            </i>
                            <xsl:choose>
                                <xsl:when test="number(CardsPlayed[not(@hidden='true')]/@count) eq 0">
                                    <xsl:text> none </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="CardsPlayed[not(@hidden='true')]/@count"/>
                                    <xsl:text> total </xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <!-- todo sort, list, label, link CardID array -->
                            <xsl:if test="CardsPlayed[not(@hidden='true')]/@count > 0">
                                <ul>
                                    <xsl:for-each select="CardsPlayed[not(@hidden='true')]/CardID">
                                        <xsl:sort select="." data-type="number" order="ascending"/>
                                        <xsl:variable name="cardID" select="."/>

                                        <xsl:variable name="superInteresting">
                                            <xsl:choose>
                                                <xsl:when test="string-length($reportsDirectoryUrl) > 0">
                                                    <xsl:value-of select="document(concat($reportsDirectoryUrl,'/',$IdeaCardChainLocalLink,'.xml'))//Card[@id=$cardID]/@superInteresting"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <!-- local directory if reportsDirectoryUrl not provided -->
                                                    <xsl:value-of select="document(concat('reports/',$gameDirectory,'/',$IdeaCardChainLocalLink,'.xml'))//Card[@id=$cardID]/@superInteresting"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:variable>
                                        <li>
                                            <!-- link to game -->
                                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!86_{$cardID}" target="{$gameAcronym}Game" title="play the game! go online to Card {$cardID}">
                                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" align="right" width="16px" border="0"/>
                                            </a>
                                            <a href="{concat($IdeaCardChainLocalLink,'.html#IdeaCard',.)}" title="to Idea Card {.}">
                                                 <xsl:value-of select="." disable-output-escaping="yes"/>
                                            </a>
                                            <xsl:text>. </xsl:text>
                                            <xsl:if test="($superInteresting='true')">
                                                <!-- bold, anchor title -->
                                                <xsl:text disable-output-escaping="yes">&lt;a title="marked Super Interesting by a Game Master"&gt;&lt;b&gt;* </xsl:text>
                                            </xsl:if>
                                            <!-- retrieve and hyperlink text -->
                                            <xsl:call-template name="hyperlink">
                                                <xsl:with-param name="string">
                                                <xsl:choose>
                                                    <xsl:when test="string-length($reportsDirectoryUrl) > 0">
                                                        <xsl:value-of select="document(concat($reportsDirectoryUrl,'/',$IdeaCardChainLocalLink,'.xml'))//Card[@id=$cardID]/text()"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <!-- local directory if reportsDirectoryUrl not provided -->
                                                        <xsl:value-of select="document(concat('reports/',$gameDirectory,'/',$IdeaCardChainLocalLink,'.xml'))//Card[@id=$cardID]/text()"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                            <xsl:if test="($superInteresting='true')">
                                                    <!-- unbold, unanchor title -->
                                                    <xsl:text disable-output-escaping="yes">&lt;/b&gt;&lt;/a&gt;</xsl:text>
                                            </xsl:if>
                                        </li>
                                    </xsl:for-each>
                                </ul>
                            </xsl:if>
                    </td>
                    <!-- ================================================================================== -->
                    <td align="left" width="33%">
                            <i>
                                <xsl:text> Action Plans: </xsl:text>
                            </i>
                            <!-- quicklink to top -->
                            <a href="#index" title="to top">
                                <!-- 1158 x 332, width="386" height="111",  width="165" height="47"  -->
                                <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="110" height="32" border="0"/>
                            </a>
                            <xsl:choose>
                                <xsl:when test="number(AuthoredPlans/@count) eq 0">
                                    <xsl:text> none </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="AuthoredPlans/@count"/>
                                    <xsl:text> total </xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <!-- TODO: hide hidden -->
                            <!-- todo sort, list, label, link ActionPlanID array -->
                            <xsl:if test="AuthoredPlans/@count > 0">
                                <ul>
                                    <xsl:for-each select="AuthoredPlans/ActionPlanID">
                                        <xsl:sort select="." data-type="number" order="ascending"/>
                                        <xsl:variable name="actionPlanID" select="."/>

                                        <xsl:variable name="superInteresting">
                                            <xsl:choose>
                                                <xsl:when test="string-length($reportsDirectoryUrl) > 0">
                                                    <xsl:value-of select="document(concat($reportsDirectoryUrl,'/',$ActionPlanLocalLink,'.xml'))//ActionPlan[position()=$actionPlanID]/@superInteresting"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <!-- local directory if reportsDirectoryUrl not provided -->
                                                    <xsl:value-of select="document(concat('reports/',$gameDirectory,'/',$ActionPlanLocalLink,'.xml'))//ActionPlan[position()=$actionPlanID]/@superInteresting"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:variable>
                                        <li>
                                            <!-- link to game -->
                                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!92_{$actionPlanID}" target="{$gameAcronym}Game" title="play the game! go online to Action Plan {$actionPlanID}">
                                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" width="16px" align="right" border="0"/>
                                            </a>
                                            <a href="{concat($ActionPlanLocalLink,'.html#ActionPlan',$actionPlanID)}" title="to Action Plan {.}">
                                                <xsl:value-of select="." disable-output-escaping="yes"/>
                                            </a>
                                            <xsl:text>. </xsl:text>
                                            <xsl:if test="($superInteresting='true')">
                                                    <!-- bold, anchor title -->
                                                    <xsl:text disable-output-escaping="yes">&lt;a title="marked Super Interesting by a Game Master"&gt;&lt;b&gt;* </xsl:text>
                                            </xsl:if>
                                            <!-- retrieve and hyperlink text -->
                                            <xsl:call-template name="hyperlink">
                                                <xsl:with-param name="string">
                                                <xsl:choose>
                                                    <xsl:when test="string-length($reportsDirectoryUrl) > 0">
                                                        <xsl:value-of select="document(concat($reportsDirectoryUrl,'/',$ActionPlanLocalLink,'.xml'))//ActionPlan[position()=$actionPlanID]/Title"/>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <!-- local directory if reportsDirectoryUrl not provided -->
                                                        <xsl:value-of select="document(concat('reports/',$gameDirectory,'/',$ActionPlanLocalLink,'.xml'))//ActionPlan[position()=$actionPlanID]/Title"/>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                        </li>
                                        <xsl:if test="($superInteresting='true')">
                                                <!-- unbold, unanchor title -->
                                                <xsl:text disable-output-escaping="yes">&lt;/b&gt;&lt;/a&gt;</xsl:text>
                                        </xsl:if>                                        
                                    </xsl:for-each>
                                </ul>
                            </xsl:if>
                    </td>
                </tr>

            </xsl:for-each>
        </table>

        <xsl:if test="(string-length($singlePlayerNumber) = 0)">

            <br />
            <hr />

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h3>
                            <a name="SuperInterestingCards">
                                <b>
                                    <xsl:text>*</xsl:text>
                                </b>
                                <xsl:text> Super Interesting</xsl:text>
                            </a>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="count(//Card[@superInteresting='true'])"/>
                            <xsl:text> cards total)</xsl:text>
                        </h3>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>
            <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
                <!-- do not recurse, only show cards of interest -->
                <xsl:apply-templates select="//Card[@superInteresting='true']">
                    <xsl:with-param name="recurse">
                        <xsl:text>false</xsl:text>
                    </xsl:with-param>
                </xsl:apply-templates>
            </table>

            <br />
            <hr />

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h3>
                            <a name="CommonKnowledgeCards">
                                <b>
                                    <xsl:text>+</xsl:text>
                                </b>
                                <xsl:text> Common Knowledge</xsl:text>
                            </a>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="count(//Card[@commonKnowledge='true'])"/>
                            <xsl:text> cards total)</xsl:text>
                        </h3>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>
            <p>
                <xsl:text> Cards marked "Common Knowledge" by a game master are considered to be so obvious that their inclusion does not add value to the game.</xsl:text>
            </p>
            <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
                <!-- do not recurse, only show cards of interest -->
                <xsl:apply-templates select="//Card[@commonKnowledge='true']">
                    <xsl:with-param name="recurse">
                        <xsl:text>false</xsl:text>
                    </xsl:with-param>
                </xsl:apply-templates>
            </table>

            <br />
            <hr />

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h3>
                            <a name="NoChildrenCards">
                                <b>
                                    <xsl:text>?</xsl:text>
                                </b>
                                <xsl:text> No More Children</xsl:text>
                            </a>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="count(//Card[@noChildren='true'])"/>
                            <xsl:text> cards total)</xsl:text>
                        </h3>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>
            <p>
                <xsl:text> Cards marked "No More Children" by a game master are considered to be extensions of the top level card set.</xsl:text>
            </p>
            <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
                <!-- do not recurse, only show cards of interest -->
                <xsl:apply-templates select="//Card[@noChildren='true']">
                    <xsl:with-param name="recurse">
                        <xsl:text>false</xsl:text>
                    </xsl:with-param>
                </xsl:apply-templates>
            </table>

            <br />
            <hr />

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h3>
                            <a name="HiddenCards">
                                <del>
                                <xsl:text>Hidden</xsl:text>
                                </del>
                                <xsl:text> </xsl:text>
                            </a>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="count(//Card[@hidden='true'])"/>
                            <xsl:text> cards total)</xsl:text>
                        </h3>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>
            <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
                <xsl:choose>
                    <xsl:when test="($displayHiddenPlayers = 'true') or ($displayHiddenPlayers = 'summaryOnly')">
                        <!-- do not recurse, only show cards of interest -->
                        <xsl:apply-templates select="//Card[@hidden='true']">
                            <xsl:with-param name="recurse">
                                <xsl:text>false</xsl:text>
                            </xsl:with-param>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <p>
                            <xsl:text> Cards marked "Hidden" by a game master are not displayed individually in this report.  They are only viewable by game masters in the game, or via database query by a system administrator.</xsl:text>
                        </p>
                    </xsl:otherwise>
                </xsl:choose>
            </table>

            <br />
            <hr />

    <!-- =================================================== License =================================================== -->

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h2><a name="License">License, Terms and Conditions</a></h2>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>

            <h3> License for Contributed Information </h3>

            <blockquote>
                All Idea Cards, Action Plans and non-personal player information contributed by MMOWGLI players
                in this report are published under an open-source license.
                    <xsl:choose>
                        <xsl:when test="($gameSecurity='FOUO')">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)
                            </a>
                            access restrictions apply.
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:text>
                                Additional access restrictions may apply.
                            </xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
            </blockquote>

            <blockquote>
                This data corpus is published under the
                <a href="https://creativecommons.org/licenses/by-sa/3.0" target="_blank">Creative Commons 3.0 "By Attribution - Share Alike"</a>
                license for open-source content.
            </blockquote>

            <blockquote>
                You are free to
                   <ul>
                       <li>
                           <b>Share</b>:  to copy, distribute and transmit the work
                       </li>
                       <li>
                           <b>Remix</b>:  to adapt the work
                       </li>
                       <li>
                           make commercial use of the work
                       </li>
                   </ul>
               Under the following conditions
                   <ul>
                       <li>
                           <b>Attribution</b>:  You must attribute the work in the manner specified by the author or licensor
                           (but not in any way that suggests that they endorse you or your use of the work).
                       </li>
                       <li>
                           <b>Share Alike</b>:  If you alter, transform, or build upon this work, you may distribute
                           the resulting work only under the same or similar license to this one.
                       </li>
                   </ul>
               With further understandings listed in the license.
            </blockquote>

            <blockquote>
                <b>Notice</b>:  For any reuse or distribution, you must make clear to others the license terms of this work.
                The best way to do this is by providing a link to the license page above.
            </blockquote>

            <h3> Terms and Conditions </h3>

            <blockquote>
                Prior to contributing, MMOWGLI players agree to follow the
                <a href="https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms+and+Conditions" target="_blank">Terms and Conditions</a>
                of the game, which include the
                <a href="https://movesinstitute.org/mmowMedia/MmowgliGameParticipantInformedConsent.html" target="_blank">Informed Consent to Participate in Research</a>
                and the
                <a href="https://www.defense.gov/socialmedia/user-agreement.aspx" target="_blank">Department of Defense Social Media User Agreement</a>.
            </blockquote>

            <blockquote>
                The official language of the MMOWGLI game is English.
                We do not support other languages during this version of the game in order to ensure that player postings are appropriate.
            </blockquote>

            <blockquote>
                No classified or sensitive information can be posted to the game.
                Violation of this policy may lead to serious consequences.
            </blockquote>

            <blockquote>
                All players must acknowledge and accept these requirements prior to user registration and game play.
                No exceptions are permitted.
            </blockquote>

            <br />
            <hr />

<!-- =================================================== Contact =================================================== -->

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h2><a name="Contact">Contact</a></h2>
                    </td>
                    <td align="right" valign="top">
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                </tr>
            </table>

            <blockquote>
                Game information in this report was exported
                <b><xsl:value-of select="$exportDateTime"/></b>.
            </blockquote>

            <blockquote>
                The
                <a href="{$XmlSourceFileName}"><xsl:value-of select="$XmlSourceFileName"/></a>
                source file contains the MMOWGLI game data used to produce this page.
                It is provided subject to the same
                <a href="#License">License, Terms and Conditions</a>.
            </blockquote>

            <blockquote>
                Questions, suggestions and comments about these game products are welcome.
                Please provide a
                <a href="https://portal.mmowgli.nps.edu/trouble">Trouble Report</a>
                or send mail to
                <a href="mailto:mmowgli-trouble%20at%20nps.edu?subject=Idea%20Card%20Report%20feedback:%20{$gameLabel}"><i><xsl:text disable-output-escaping="yes">mmowgli-trouble at nps.edu</xsl:text></i></a>.
            </blockquote>

            <blockquote>
                To learn more, additional information is available online for the
                <a href="{$portalPage}"><xsl:value-of select="$gameLabel"/><xsl:text> game</xsl:text></a>
                and the
                <a href="https://portal.mmowgli.nps.edu">MMOWGLI project</a>.
            </blockquote>

            <blockquote>
    <a href="https://www.nps.navy.mil/disclaimer" target="disclaimer">Official disclaimer</a>:
    "Material contained herein is made available for the purpose of
    peer review and discussion and does not necessarily reflect the
    views of the Department of the Navy or the Department of Defense."
            </blockquote>
        </xsl:if>

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

    <xsl:template name="threeDigitSpacing">
        <xsl:param name="value" select="string(.)" />

        <xsl:choose>
            <xsl:when test="(string-length($value) = 1)">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:when test="(string-length($value) = 2)">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="fourDigitSpacing">
        <xsl:param name="value" select="string(.)" />

        <xsl:choose>
            <xsl:when test="(string-length($value) = 1)">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:when test="(string-length($value) = 2)">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
            <xsl:when test="(string-length($value) = 3)">
                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;</xsl:text>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="hyperlink">
        <!-- Search and replace urls in text:  adapted (with thanks) from
            http://www.dpawson.co.uk/xsl/rev2/regex2.html#d15961e67 by Jeni Tennison using url regex (http://[^ ]+) -->
        <!-- Justin Saunders http://regexlib.com/REDetails.aspx?regexp_id=37 url regex ((mailto:|(news|(ht|f)tp(s?))://){1}\S+) -->
        <xsl:param name="string" select="string(.)" />
        <!-- wrap html text string with spaces to ensure no mismatches occur -->
        <xsl:variable name="spacedString">
            <xsl:text> </xsl:text>
            <xsl:value-of select="$string"/>
            <xsl:text> </xsl:text>
        </xsl:variable>
        <!-- First: find and link url values -->
        <xsl:analyze-string select="$spacedString" regex="((mailto:|(news|http|https|sftp)://)[\S]+)">
            <xsl:matching-substring>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                        <xsl:if test="(contains(.,'youtube.com') or contains(.,'youtu.be')) and not(contains(.,'rel='))">
                            <!-- prevent advertising other YouTube videos when complete -->
                            <xsl:text disable-output-escaping="yes">&amp;rel=0</xsl:text>
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:attribute name="target">
                        <xsl:text>_blank</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="." disable-output-escaping="yes"/>
                </xsl:element>
                <!-- <xsl:text> </xsl:text> -->
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- Second:  for non-url remainder(s), now find and link 'Idea Card Chain 123' references -->
                <xsl:analyze-string select="concat(' ',normalize-space(.),' ')" regex="(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Ii][Dd][Ee][Aa]\s*)?[Cc][Aa][Rr][Dd]\s*#?\s*([Cc][Hh][Aa][Ii][Nn]\s*#?\s*)?(\d+))">
                    <xsl:matching-substring>
                        <!-- bookmark #IdeaCard1234 - see IdeaCardLabel above for consistency -->
                        <a href="{concat($IdeaCardChainLocalLink,'.html#IdeaCard',regex-group(6))}">
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </a>
                        <!-- <xsl:text> </xsl:text> -->
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <!-- Third: for non-url remainder(s), now find and link 'Action Plan 456' references -->
                        <xsl:analyze-string select="concat(' ',normalize-space(.),' ')" regex="(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Aa][Cc][Tt][Ii][Oo][Nn]\s*)?[Pp][Ll][Aa][Nn]\s*#?\s*(\d+))">
                            <xsl:matching-substring>
                                <!-- bookmark #ActionPlan456 - see ActionPlanLabel above for consistency -->
                                <a href="{concat($ActionPlanLocalLink,'.html#ActionPlan',regex-group(5))}">
                                    <xsl:value-of select="." disable-output-escaping="yes"/>
                                </a>
                                <!-- <xsl:text> </xsl:text> -->
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <!-- avoid returning excess whitespace -->
                                <xsl:if test="string-length(normalize-space(.)) > 0">
                                    <xsl:value-of select="." />
                                </xsl:if>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>
