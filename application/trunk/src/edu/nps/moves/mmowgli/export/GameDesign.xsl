<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu" -->
    <xsl:output method="html"/>

    <xsl:variable name="mmowgliGameDescription">
        <xsl:value-of select="//MmowgliGame/@description"/>
    </xsl:variable>

    <xsl:variable name="exportDateTime">
        <xsl:value-of select="//MmowgliGame/@exported"/>
    </xsl:variable>

    <xsl:variable name="gameTitle">
        <!-- Piracy2012, Piracy2011.1, Energy2012, etc. -->
        <!-- TODO make consistent -->
        <xsl:value-of select="//GameTitle"/>
    </xsl:variable>

    <xsl:variable name="gameSecurity">
        <!-- open, FOUO, etc. -->
        <xsl:value-of select="//GameSecurity"/>
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
                <xsl:text>Business Innovation Initiative (bii)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em') or contains($gameTitle,'Em2') or contains($gameTitle,'Em')">
                <xsl:text>EM Maneuver (em2)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>Edge Virtual Training Program (evtp)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>Additive Manufacturing (am)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'blackswan') or starts-with($gameTitle,'Blackswan') or contains($gameTitle,'blackswan') or contains($gameTitle,'Blackswan')">
                <xsl:text>blackswan</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'dd') or starts-with($gameTitle,'DD') or contains($gameTitle,'dd') or contains($gameTitle,'DD')">
                <xsl:text>Data Dilemma (dd)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'pcc') or starts-with($gameTitle,'PCC') or contains($gameTitle,'pcc') or contains($gameTitle,'PCC')">
                <xsl:text>Professional Core Competencies (pcc)</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>Capacity, Capabilities and Constraints (cap2con)</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>Unmanned Vehicle Digital Manufacturing (uxvdm)</xsl:text>
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
                <xsl:text>NAWCAD Strategic Cell</xsl:text>
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
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>Unmanned Vehicle Digital Manufacturing (uxvdm)</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of disable-output-escaping="yes" select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

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
            <xsl:when test="contains($gameTitle,'em2') or contains($gameTitle,'em')">
                <xsl:text>https://portal.mmowgli.nps.edu/em2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>https://portal.mmowgli.nps.edu/evtp</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>https://portal.mmowgli.nps.edu/am</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>https://portal.mmowgli.nps.edu/cap2con</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'dark')">
                <xsl:text>https://portal.mmowgli.nps.edu/NDU</xsl:text>
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
                <xsl:value-of disable-output-escaping="yes" select="//BrandingText"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="XmlSourceFileName">
        <xsl:text>GameDesign_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>MmowgliGame.xml</xsl:text>
    </xsl:variable>

    <xsl:variable name="IdeaCardChainLocalLink">
        <xsl:text>IdeaCardChain_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <!-- .html or .xml -->
    </xsl:variable>

    <xsl:variable name="ActionPlanLocalLink">
        <xsl:text>ActionPlanList_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>.html</xsl:text>
    </xsl:variable>

    <xsl:variable name="PlayerProfilesLocalLink">
        <xsl:text>PlayerProfiles_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>.html</xsl:text>
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

    <xsl:template match="/">
        <html>
            <head>
            <!-- TODO
                <meta name="identifier" content="http:// TODO /ActionPlanList.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta http-equiv="refresh" content="600"/> <!-- 600 seconds = 10 minutes -->
                <meta name="description" content="Action plan outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="GameDesign_{$gameTitle}.html"/>
                <meta name="identifier"  content="https://mmowgli.nps.edu/{$gameAcronym}/IdeaCardChains_{$gameTitle}.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, https://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, https://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>

                <xsl:element name="title">
                    <xsl:text>Game Design Report, </xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                    <xsl:text> MMOWGLI game</xsl:text>
                </xsl:element>

     <style type="text/css">

dt { font-weight:bold; } <!-- font-style:italic; -->
table.banner   { border:0; background-color:#ffffff; padding-left:15px; padding-right:15px; padding-top:1px; padding-bottom:1px; }
table.contents { border:0; background-color:#eeffff; padding-left:15px; padding-right:15px; padding-top:1px; padding-bottom:1px; }
table.media    { border:0; background-color:#eeeeee; padding-left:15px; padding-right:15px; padding-top:1px; padding-bottom:1px; }
table.chatLog  { border:0; background-color:#eeeeee; padding-left: 5px; padding-right: 5px; padding-top:1px; padding-bottom:1px; }
table.textLog  { border:0; background-color:#eeeeee; padding-left: 5px; padding-right: 5px; padding-top:1px; padding-bottom:1px; }
<!--
span.element {color: navy}
span.attribute {color: green}
span.value {color: teal}
span.plain {color: black}
span.gray  {color: gray}
span.idName {color: maroon}
a.idName {color: maroon}
div.center {text-align: center}
div.indent {margin-left: 25px}

span.prototype {color: purple}
a.prototype {color: purple}
a.prototype:visited {color: black}
span.route {color: red}
b.warning {color: #CC5500}
b.error {color: #CC0000}
-->
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

                <!-- page header -->
                <table align="center" border="0" class="banner">
                    <tr>
                        <td align="center">
                            <p>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            </p>
                            <h1 align="center">
                                <xsl:text> Game Design Report </xsl:text>
                            </h1>
                            <h2 align="center">
                                <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/> <!-- want escaped <br /> intact for line break -->
                            </h2>
                        </td>
                        <td>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                        </td>
                        <td align="center">
                            <a href="{$portalPage}" title="Game documentation for {$gameLabel}" target="_blank">
                                <!-- 1158 x 332 -->
                                <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="386" height="111" border="0"/>
                            </a>
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
                                        <a href="{$PlayerProfilesLocalLink}" title="Player Profiles report">
                                            <xsl:text>Player Profiles</xsl:text>
                                        </a>
                                    </li>
                                    <li>
                                        <a href="https://portal.mmowgli.nps.edu/reports" target="blank">All MMOWGLI Game Reports</a>
                                    </li>
                                </ul>
                            </td>
                    </tr>
                </table>

                <!-- Table of Contents -->
                <p align="center" style="font-size:-2">
                    <xsl:for-each select="//MmowgliGame/*">
                        <a href="#{local-name()}"><xsl:value-of select="local-name()"/></a>
                        <xsl:if test="position() != last()">
                            <xsl:text> | </xsl:text>
                        </xsl:if>
                    </xsl:for-each>
                    <xsl:text> | </xsl:text>
                    <a href="{concat($ReportsIndexLocalLink,'.html')}" title="Reports Index for this game"><xsl:text>Reports Index</xsl:text></a>
                    <xsl:text> | </xsl:text>
                    <a href="#Contact"><xsl:text>Contact</xsl:text></a>
                </p>

                <xsl:apply-templates select="//MmowgliGame/*"/>

<!-- =================================================== Contact =================================================== -->

            <br />
            <hr />

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

            <xsl:variable name="LicenseLink">
                <xsl:text>IdeaCardChain_</xsl:text>
                <xsl:value-of select="$gameTitle"/>
                <xsl:text>MmowgliGame.html#License</xsl:text>
            </xsl:variable>

            <blockquote>
                The
                <a href="{$XmlSourceFileName}"><xsl:value-of select="$XmlSourceFileName"/></a>
                source file contains the MMOWGLI game data used to produce this page.
                It is provided subject to the same
                <a href="{$LicenseLink}">License, Terms and Conditions</a>.
            </blockquote>

            <blockquote>
                Questions, suggestions and comments about these game products are welcome.
                Please provide a
                <a href="https://portal.mmowgli.nps.edu/trouble">Trouble Report</a>
                or send mail to
                <a href="mailto:mmowgli-trouble%20at%20nps.edu?subject=Game%20Design%20Report%20feedback:%20{$gameLabel}"><i><xsl:text disable-output-escaping="yes">mmowgli-trouble at nps.edu</xsl:text></i></a>.
            </blockquote>

            <blockquote>
                Additional information is available online for the
                <a href="{$portalPage}"><xsl:value-of select="$gameLabel"/><xsl:text> game</xsl:text></a>
                and the
                <a href="https://portal.mmowgli.nps.edu/game-wiki">MMOWGLI project</a>.
            </blockquote>

            <blockquote>
    <a href="https://www.nps.navy.mil/disclaimer" target="disclaimer">Official disclaimer</a>:
    "Material contained herein is made available for the purpose of
    peer review and discussion and does not necessarily reflect the
    views of the Department of the Navy or the Department of Defense."
            </blockquote>

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

    <xsl:template match="*">
        <h3>
            <a name="{local-name()}">
                <xsl:value-of select="local-name()"/>
            </a>
        </h3>
        <xsl:if test="(local-name()='GameSecurity')">
            <indent>
                <xsl:value-of select="." disable-output-escaping="yes"/>
                <br />
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
            </indent>
        </xsl:if>
        <table border="1">
            <xsl:for-each select="*">
                <tr valign="top">
                    <td>
                        <b>
                            <xsl:value-of select="local-name()"/>
                        </b>
                    </td>
                    <td valign="top">
                        <xsl:choose>
                            <!-- plain text -->
                            <xsl:when test="(local-name()='BrandingText') or
                                            (local-name()='WindowTitle')">
                                <xsl:value-of select="." disable-output-escaping="yes"/>
                            </xsl:when>
                            <!-- html source -->
                            <xsl:when test="(local-name()='OrientationHeadline') or
                                            (local-name()='OrientationSummary') or
                                            (local-name()='BriefingText') or
                                            (contains(local-name(),'Instructions'))">
                                <!-- first, avoid escaping html codes in order to see actual rendering here -->
                                <xsl:value-of select="." disable-output-escaping="yes"/>
                                <!--<xsl:text>(html source)</xsl:text>-->
                                <hr />
                                <span style="background-color:lightgrey;" title="this is html source saved in game database">
                                    <xsl:text disable-output-escaping="yes">&#10;</xsl:text>
                                    <code>
                                        <xsl:call-template name="hyperlink">
                                            <xsl:with-param name="string">
                                                <xsl:call-template name="treatParagraphBreaks">
                                                    <xsl:with-param name="string">
                                                        <xsl:call-template name="treatHtml">
                                                            <xsl:with-param name="string" select="."/>
                                                        </xsl:call-template>
                                                    </xsl:with-param>
                                                </xsl:call-template>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                    </code>
                                </span>
                                <hr />
                            </xsl:when>
                            <!-- image -->
                            <xsl:when test="(local-name()='ScreenShot')">
                                <!-- TODO prefix online url -->
                                <a href="{.}" target="_blank"><img src="{.}" width="20" align="top"/></a>
                                <xsl:text> </xsl:text>
                                <a href="{.}" target="_blank"><xsl:value-of select="." disable-output-escaping="yes"/></a>
                            </xsl:when>
                            <!-- video -->
                            <xsl:when test="(local-name()='Video')">
                                <!-- TODO other sources besides YouTube -->
                                <xsl:text>YouTube </xsl:text>
                                <a href="https://youtube.com/watch?v={.}" target="_blank"><xsl:value-of select="." disable-output-escaping="yes"/></a>
                                <br/>
                                <iframe width="560" height="315" src="https://www.youtube.com/embed/{.}" frameborder="0" allowfullscreen="true"></iframe>
                            </xsl:when>
                            <!-- url -->
                            <xsl:when test="starts-with(normalize-space(string(.)),'http')">
                                <a href="{.}" target="_blank">
                                    <xsl:call-template name="treatUrl">
                                        <xsl:with-param name="string" select="."/>
                                    </xsl:call-template>
                                </a>
                            </xsl:when>
                            <!-- empty -->
                            <xsl:when test="not(.) or (string-length(.)=0)">
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            </xsl:when>
                            <!-- other plain text -->
                            <xsl:otherwise>
                                <xsl:value-of select="string(.)"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <xsl:template name="treatHtml">
        <xsl:param name="string" select="string(.)"/>
        <!-- First: find and fix < values -->
        <xsl:analyze-string select="$string" regex="&lt;">
            <xsl:matching-substring>
                <xsl:text disable-output-escaping="yes">&amp;lt;</xsl:text>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- Second:  find and fix > values -->
                <xsl:analyze-string select="." regex="&gt;">
                    <xsl:matching-substring>
                        <xsl:text disable-output-escaping="yes">&amp;gt;</xsl:text>
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <!-- Third: remainder is text -->
                        <xsl:value-of select="." disable-output-escaping="yes"/>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="treatUrl">
        <xsl:param name="string" select="string(.)"/>
        <!-- Find and fix %20 values -->
        <xsl:analyze-string select="$string" regex="%20">
            <xsl:matching-substring>
                <xsl:text disable-output-escaping="yes"> </xsl:text>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- Second: remainder is text -->
                <xsl:value-of select="." disable-output-escaping="yes"/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

    <xsl:template name="treatParagraphBreaks">
        <xsl:param name="string" select="string(.)"/>
        <!-- limitation:  only works on lower-case html tags -->
        <!-- First: check for paragraph break, i.e. find and fix </p><p> combinations between paragraphs -->
        <xsl:variable name="result1">
            <xsl:value-of select="replace($string, '&amp;lt;/p&amp;gt;', '&amp;lt;/p&amp;gt;&#10;&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Second: check for bullet (i.e. line item li) break -->
        <xsl:variable name="result2">
            <xsl:value-of select="replace($result1, '&amp;lt;/li&amp;gt;', '&amp;lt;/li&amp;gt;&#10;&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Third: check for unnumbered list (i.e. ol) break -->
        <xsl:variable name="result3">
            <xsl:value-of select="replace($result2, '&amp;lt;/ul&amp;gt;', '&amp;lt;/ul&amp;gt;&#10;&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Fourth: check for ordered list (i.e. ol) break -->
        <xsl:variable name="result4">
            <xsl:value-of select="replace($result3, '&amp;lt;/ol&amp;gt;', '&amp;lt;/ol&amp;gt;&#10;&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Fifth: check for unnumbered list (i.e. ul) start -->
        <xsl:variable name="result5">
            <xsl:value-of select="replace($result4, '&amp;lt;ul&amp;gt;', '&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;&amp;lt;ul&amp;gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Sixth: check for ordered list (i.e. ol) start -->
        <xsl:variable name="result6">
            <xsl:value-of select="replace($result5, '&amp;lt;ol&amp;gt;', '&lt;/code&gt;&#10;&lt;br /&gt;&#10;&lt;code&gt;&#10;&amp;lt;ol&amp;gt;&#10;')" disable-output-escaping="yes"/>
        </xsl:variable>
        <!-- Third: remainder is unmodified -->
        <xsl:value-of select="$result6" disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template name="hyperlink">
        <!-- Search and replace urls in text:  adapted (with thanks) from
            http://www.dpawson.co.uk/xsl/rev2/regex2.html#d15961e67 by Jeni Tennison using url regex (http://[^ ]+) -->
        <!-- Justin Saunders http://regexlib.com/REDetails.aspx?regexp_id=37 url regex ((mailto:|(news|(ht|f)tp(s?))://){1}\S+) -->
        <xsl:param name="string" select="string(.)" />
        <!-- wrap html text string with spaces to ensure no mismatches occur -->
        <xsl:variable name="spacedString">
            <xsl:text> </xsl:text>
            <xsl:value-of select="$string" disable-output-escaping="yes"/>
            <xsl:text> </xsl:text>
        </xsl:variable>
        <!-- First: find and link url values -->
        <!-- negative lookahead for " needed, see http://www.regular-expressions.info/lookaround.html -->
        <xsl:analyze-string select="$spacedString" regex="((mailto:|(news|http|https|sftp)://)[\S]+)">
            <xsl:matching-substring>
                <xsl:variable name="urlQuoteStripped">
                    <xsl:choose>
                        <xsl:when test="contains(.,'&quot;')">
                            <xsl:value-of select="substring-before(.,'&quot;')" disable-output-escaping="yes"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:element name="a">
                    <xsl:attribute name="href">
                        <xsl:value-of select="$urlQuoteStripped" disable-output-escaping="yes"/>
                        <xsl:if test="(contains(.,'youtube.com') or contains(.,'youtu.be')) and not(contains(.,'rel='))">
                            <!-- prevent advertising other YouTube videos when complete -->
                            <xsl:text disable-output-escaping="yes">&amp;rel=0</xsl:text>
                        </xsl:if>
                    </xsl:attribute>
                    <xsl:attribute name="target">
                        <xsl:text>_blank</xsl:text>
                    </xsl:attribute>
                    <xsl:value-of select="$urlQuoteStripped" disable-output-escaping="yes"/>
                </xsl:element>
                <!-- restore trailing quote if present -->
                <xsl:if test="contains(.,'&quot;')">
                    <xsl:text>&quot;</xsl:text>
                    <xsl:value-of select="substring-after(.,'&quot;')" disable-output-escaping="yes"/>
                </xsl:if>
            </xsl:matching-substring>
            <xsl:non-matching-substring>
                <!-- Second:  no further action -->
                <xsl:value-of select="." disable-output-escaping="yes"/>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>
