<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"
                xmlns:fn="w3.org/2005/xpath-functions">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu"
                xmlns:date="http://exslt.org/dates-and-times" -->
    <!-- default parameter values can be overridden when invoking this stylesheet -->
    <xsl:param name="singleIdeaCardChainRootNumber"></xsl:param>
    <!-- displayHiddenCards: summaryOnly, true, false  -->
    <xsl:param name="displayHiddenCards">false</xsl:param>
    <!-- displayRoundNumber: all, 1, 2, etc.  -->
    <xsl:param name="displayRoundNumber">all</xsl:param>

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

    <!-- must also change CSS td.cardCell below! ensure px included. example: 50px -->
    <xsl:variable name="cardCellWidth">
        <xsl:text>55px</xsl:text>
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
        <xsl:value-of select="//CardTree/@exported"/>
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
                <xsl:value-of disable-output-escaping="yes" select="//GameAcronym"/>
            </xsl:otherwise>
        </xsl:choose>
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
    
    <!-- supported values:  true, false, summaryOnly -->
    <!-- it is sometimes useful to show hidden cards while quality/correctness review is in progress
    <xsl:variable name="displayHiddenCards">
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

    <!-- Titles may vary for top-level InnovateCards and DefendCards.  Ignore hidden cards because they might have been created during setup period. -->
    <xsl:variable name="innovateCardName"  select="(//InnovateCards/Card[not(@hidden='true')][1]/@type)"/>
    <xsl:variable name="defendCardName"    select="(//DefendCards/Card[not(@hidden='true')][1]/@type)"/>

    <!-- these are initial colors which may vary in subsequent rounds, if multipleRounds=true -->
    <xsl:variable name="innovateCardColor" select="//InnovateCards/Card[not(@hidden='true')][1]/@color"/>
    <xsl:variable name= "defendCardColor" select="//DefendCards/Card[not(@hidden='true')][1]/@color"/>
    <xsl:variable name= "expandCardColor" select="//Card[@type= 'Expand'][1]/@color"/>
    <xsl:variable name=  "adaptCardColor" select="//Card[@type=  'Adapt'][1]/@color"/>
    <xsl:variable name="counterCardColor" select="//Card[@type='Counter'][1]/@color"/>
    <xsl:variable name="exploreCardColor" select="//Card[@type='Explore'][1]/@color"/>

    <xsl:variable name="XmlSourceFileName">
        <xsl:text>IdeaCardChain_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>.xml</xsl:text>
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

    <!-- template to put out empty <td/> elements count minus i times -->
    <xsl:template name="indent.for.loop">
        <xsl:param name="i"/>
        <xsl:param name="count"/>
        <xsl:param name="cardBackgroundClass"/>

        <xsl:variable name="padCell">
            <xsl:choose>
                <xsl:when test="i > 1">
                    <xsl:text disable-output-escaping="yes">&#160;&#160;</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text disable-output-escaping="yes">&#160;&#160;&#160;</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <xsl:choose>
            <xsl:when test="$i &lt; $count">
                <td width="{$cardCellWidth}" class="cardCell {$cardBackgroundClass}" align="left">
                    <xsl:text disable-output-escaping="yes">&#160;&#160;&#160;</xsl:text>
                    <!-- <xsl:value-of select="$padCell"/> -->
                </td>
                <!-- recurse -->
                <xsl:call-template name="indent.for.loop">
                    <xsl:with-param name="i">
                        <xsl:value-of select="$i + 1"/>
                    </xsl:with-param>
                    <xsl:with-param name="count">
                        <xsl:value-of select="$count"/>
                    </xsl:with-param>
                    <xsl:with-param name="cardBackgroundClass">
                        <xsl:value-of select="$cardBackgroundClass"/>
                    </xsl:with-param>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="GameSummary">
        <h2>
            <xsl:value-of select="." disable-output-escaping="yes"/>
        </h2>
    </xsl:template>

    <xsl:template name="cardType">
        <xsl:param name="cType"/>
        <xsl:param name="color"/>
        <xsl:param name="cardLabel">
            <!-- optional, set blank if not provided -->
            <xsl:text disable-output-escaping="yes"> </xsl:text> <!-- &#160;&#160;&#160; -->
        </xsl:param>
        <xsl:param name="size"/>

          <xsl:variable name="style">
            <xsl:choose>
              <xsl:when test="string-length($color) > 0">
                <xsl:text>background-color:</xsl:text>
                <!-- sidestep bug where multiple color values returned -->
                <xsl:value-of select="substring-before(concat($color,' '), ' ')"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:message>
                  <xsl:text>warning: no color value found for cType=</xsl:text>
                  <xsl:value-of select="$cType"/>
                  <xsl:text>, label=</xsl:text>
                  <xsl:value-of select="$cardLabel"/>
                </xsl:message>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:variable>

        <xsl:choose>
            <!-- ================================== -->
            <!-- top-level categories for piracy 2011.x -->
            <xsl:when test="$cType = 'Best Strategy'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Best Strategy (Innovate) card: What approaches can best meet this challenge?"                    align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Worst Strategy'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Worst Strategy (Defend old approaches) card: What mistakes or pitfalls need to be avoided?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for piracy 2011.x -->
            <xsl:when test="$cType = 'Innovate'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Innovate (New or Best Strategy) card: What approaches can best meet this challenge?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Defend'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Defend (Status Quo Strategy) card: What mistakes or pitfalls need to be avoided?"  align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for Energy game -->
            <xsl:when test="$cType = 'Efficiency'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Efficiency (New or Best Strategy) card: How can energy efficiency be improved?"                  align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Consumption'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Consumption (Defend Status Quo Strategy) card: How can energy consumption status quo be reduced?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for cap2con game, round 1 -->
            <xsl:when test="$cType = 'Understanding EM'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Understanding EM card: How can the DoD instill a deep understanding of EM Energy by 2022?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Negate the Threat'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Negate the Threat card: Can EM Energy be made irrelevant as a threat to the Maritime Force by 2022?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for em2 game, round 1 -->
            <xsl:when test="$cType = 'Understanding EM'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Understanding EM card: How can the DoD instill a deep understanding of EM Energy by 2022?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Negate the Threat'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Negate the Threat card: Can EM Energy be made irrelevant as a threat to the Maritime Force by 2022?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for em2 game, round 2 -->
            <xsl:when test="$cType = 'Be Agile'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Be Agile card: How do we adapt current DoD C2 approaches for EM Maneuver Warfare?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Negate the Threat'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Commanding EM card: How are Command and Authorities affected by the EM Environment?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- TODO top-level categories for em2 game, round 3 -->
            <xsl:when test="$cType = 'Be Agile'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Be Agile card: How do we adapt current DoD C2 approaches for EM Maneuver Warfare?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Negate the Threat'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Commanding EM card: How are Command and Authorities affected by the EM Environment?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for ig game -->
            <xsl:when test="$cType = 'Recommendations'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Recommendations card: What are specific findings, recommendations and implications of IG report?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Opportunities'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Opportunities card: What opportunities for improvement can best help NPS execute its mission?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- top-level categories for ongoing Piracy game -->
            <xsl:when test="$cType = 'Challenges'">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="Challenges (Improve Current Capabilities) card: What approaches can best help in near term?"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Future Goals'">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="Future Goals (New Efforts Needed) card: What approaches can best help in long term?"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- other top-level categories.  TODO may need to track move number? -->
            <xsl:when test="$cType = //InnovateCards/Card[not(@hidden='true')][1]/@type">
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}" title="{//InnovateCards/Card[1]/@type} (Innovate) card"     align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = //DefendCards/Card[not(@hidden='true')][1]/@type">
                <td width="{$cardCellWidth}" style="{$style}" class="defendStrategy cardCell{$size}"   title="{//DefendCards/Card[1]/@type} (Innovate) card"             align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <!-- reaction cards -->
            <xsl:when test="$cType = 'Expand'">
              <!--  -->
                <td width="{$cardCellWidth}" style="{$style}" class="expand cardCell{$size}"            title="Expand card: Build on a parent idea to amplify its impact"                                       align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Counter'">
              <!-- class="expand cardCell{$size}" -->
                <td width="{$cardCellWidth}" style="{$style}" class="counter cardCell{$size}"            title="Counter card: Challenge a parent idea"                                                           align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Adapt'">
              <!-- class="expand cardCell{$size}" -->
                <td width="{$cardCellWidth}" style="{$style}" class="adapt cardCell{$size}"            title="Adapt card: Take an idea in a different direction"                                               align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <xsl:when test="$cType = 'Explore'">
              <!-- class="expand cardCell{$size}" -->
                <!-- TODO update database with prose: -->
                <td width="{$cardCellWidth}" style="{$style}" class="explore cardCell{$size}"            title="Explore card: Is something missing that needs consideration?"                                    align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:when>
            <!-- ================================== -->
            <xsl:otherwise>
                <td width="{$cardCellWidth}" style="{$style}" class="innovateStrategy cardCell{$size}"                                                                                                         align="center"><xsl:value-of select="$cardLabel"/></td>
            </xsl:otherwise>
            <!-- ================================== -->
        </xsl:choose>
    </xsl:template>

    <xsl:template match="Card">
        <xsl:param name="recurse">
            <xsl:text>true</xsl:text>
        </xsl:param>
        <!-- Insert divider row before new top-level cards, but only when listing entire card chains -->
        <xsl:variable name="currentCardId"  select="@id"/>
        <xsl:variable name="currentCardType"  select="@type"/>
        <xsl:variable name="currentCardMoveNumber"  select="@moveNumber"/>
        <!-- debug 
        <xsl:comment>
            <xsl:text>$singleIdeaCardChainRootNumber=</xsl:text>
            <xsl:value-of select="$singleIdeaCardChainRootNumber"/>
            <xsl:text>, $currentCardId=</xsl:text>
            <xsl:value-of select="$currentCardId"/>
            <xsl:text>, $currentCardMoveNumber=</xsl:text>
            <xsl:value-of select="$currentCardMoveNumber"/>
            <xsl:text>, number(@level)=</xsl:text>
            <xsl:value-of select="number(@level)"/>
            <xsl:text>, $recurse=</xsl:text>
            <xsl:value-of select="$recurse"/>
            <xsl:text>, preceding-sibling::*[not(@hidden = 'true')][1]/@moveNumber=</xsl:text>
            <xsl:value-of select="preceding-sibling::*[not(@hidden = 'true')][1]/@moveNumber"/>
        </xsl:comment>
        -->
        <xsl:variable name="IdeaCardLabel">
            <xsl:text disable-output-escaping="yes">IdeaCard</xsl:text>
            <xsl:value-of select="@id"/>
        </xsl:variable>
        <xsl:variable name="followOnTopLevelCard">
            <xsl:value-of select="(number(@level) = 1) and ($recurse = 'true') and not(@hidden = 'true') and 
                                  not(position() = 1)"/>
        </xsl:variable>
        <xsl:variable name="cardBackgroundClass">
            <!-- top-level card -->
            <xsl:choose>
                <xsl:when test="(number(@level) = 1) and ($recurse = 'true') and not(@hidden = 'true')">
                <xsl:text>toplevelcard</xsl:text>
                </xsl:when>
                <xsl:when test="(@commonKnowledge='true')">
                <xsl:text>commonknowledgecard</xsl:text>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="dateText">
            <xsl:choose>
                <xsl:when test="@date or (string-length(@date) > 0)">
                    <xsl:text> (</xsl:text><xsl:value-of select="@date"/><xsl:text>)</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <!-- null string -->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="cardText" select="normalize-space(text()[1])"/>
        <xsl:variable name="IdeaCardNumberTitle">
            <xsl:text>bookmark: </xsl:text>
            <xsl:value-of select="$IdeaCardLabel"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$dateText"/>
            <xsl:if test="//CardTree[@multipleMoves='true']">
                <xsl:text> Round </xsl:text>
                <xsl:value-of select="@moveNumber"/>
            </xsl:if>
        </xsl:variable>
        <xsl:variable name="cardType" select="@type"/>
                
        <xsl:choose>
            <xsl:when test="(number(@level) = 1) and ($recurse = 'true') and not(@hidden = 'true') and 
                            ((position() = 1) or not(preceding-sibling::*[@type = $currentCardType]) or not(preceding-sibling::*[not(@hidden = 'true')][1][@moveNumber = $currentCardMoveNumber])) and
                            (string-length($singleIdeaCardChainRootNumber) = 0)">
                <tr>
                    <td align="left" valign="bottom" colspan="{number($maxColumnCount) - 1}" class="{$cardBackgroundClass}">
                        <!-- embedded table to clean up, simplify column spacing -->
                        <table border="0" width="100%" cellpadding="0">
                            <tr>
                                <td colspan="2" valign="middle">
                                    <h1 style="background-color:lightgray;" align="center">
                                        <!-- bookmark -->
                                        <xsl:element name="a">
                                            <xsl:attribute name="name">
                                                <xsl:value-of select="translate(normalize-space(@type),' ','')"/>
                                            </xsl:attribute>
                                            <xsl:text>Idea Card Chains</xsl:text>
                                            <xsl:if test="//CardTree[@multipleMoves='true']">
                                                <xsl:text>, Round </xsl:text>
                                                <xsl:value-of select="@moveNumber"/>
                                            </xsl:if>
                                        </xsl:element>
                                    </h1>
                                </td>
                            </tr>
                            <tr>
				<td align="left" valign="bottom">
                                    <h2 title="Idea card chains for innovation">
                                        <xsl:if test="//CardTree[@multipleMoves='true']">
                                            <xsl:text> Round </xsl:text>
                                            <xsl:value-of select="@moveNumber"/>
                                            <xsl:choose>
                                                <xsl:when test="(//TopLevelCardTypes/InnovateType/Type/@title = $cardType)">
                                                    <xsl:text> Innovate</xsl:text>
                                                </xsl:when>
                                                <xsl:when test="(//TopLevelCardTypes/DefendType/Type/@title = $cardType)">
                                                    <xsl:text> Defend</xsl:text>
                                                </xsl:when>
                                            </xsl:choose>
                                            <xsl:text>: </xsl:text>
                                        </xsl:if>
                                        <i>
                                            <xsl:value-of select="@type"/>
                                        </i>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    </h2>
                                    <h3>
                                        <!-- top-level cardType prompt -->
                                        <xsl:value-of select="//TopLevelCardTypes/*/Type[@title = $currentCardType][@round = $currentCardMoveNumber]/@prompt"/>
                                    </h3>
				</td>
				<td align="right" valign="top">
                                    <a href="#index" title="to top">
                                        <!-- 1158 x 332, width="386" height="111"  -->
                                        <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                                    </a>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="2">
                                    <!-- Seed Card index table -->
                                    <table border="1" align="left" width="100%">
                                        <tr cellpadding="4" cellspacing="4" align="center">
                                            <xsl:variable name="currentSeedCardCount">
                                                <xsl:choose>
                                                    <xsl:when test="(//TopLevelCardTypes/InnovateType/Type/@title = $cardType)">
                                                        <xsl:value-of select="count(//InnovateCards/Card[@level='1'][@moveNumber = $currentCardMoveNumber][not(@hidden='true')])"/>
                                                    </xsl:when>
                                                    <xsl:when test="(//TopLevelCardTypes/DefendType/Type/@title = $cardType)">
                                                        <xsl:value-of select="count(//DefendCards/Card[@level='1'][@moveNumber = $currentCardMoveNumber][not(@hidden='true')])"/>
                                                    </xsl:when>
                                                </xsl:choose>
                                            </xsl:variable>
                                            <th style="background-color:{@color}; color:white;">
                                                <xsl:text> Table of Contents: </xsl:text>
                                                <xsl:value-of select="@type"/>
                                                <xsl:text> Top-Level Seed Cards (</xsl:text>
                                                <xsl:value-of select="$currentSeedCardCount"/>
                                                <xsl:text> total) </xsl:text>
                                            </th>
                                        </tr>
                                        <tr cellpadding="4" cellspacing="4">
                                            <xsl:choose>
                                                <xsl:when test="(//TopLevelCardTypes/InnovateType/Type/@title = $cardType)">
                                                    <td>
                                                        <ul>
                                                            <xsl:for-each select="(//InnovateCards/Card[@level='1'][@moveNumber = $currentCardMoveNumber][not(@hidden='true')])">
        <xsl:variable name="IdeaCardLabel">
            <xsl:text disable-output-escaping="yes">IdeaCard</xsl:text>
            <xsl:value-of select="@id"/>
        </xsl:variable>
        <xsl:variable name="dateText">
            <xsl:choose>
                <xsl:when test="@date or (string-length(@date) > 0)">
                    <xsl:text> (</xsl:text><xsl:value-of select="@date"/><xsl:text>)</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <!-- null string -->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="cardText" select="normalize-space(text()[1])"/>
        <xsl:variable name="IdeaCardNumberTitle">
            <xsl:text>bookmark: </xsl:text>
            <xsl:value-of select="$IdeaCardLabel"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$dateText"/>
            <xsl:if test="//CardTree[@multipleMoves='true']">
                <xsl:text> Round </xsl:text>
                <xsl:value-of select="@moveNumber"/>
            </xsl:if>
        </xsl:variable>
                                                                <li>
                    <xsl:if test="@superInteresting='true'">
                        <b title="Super Interesting">
                            <xsl:text>*</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@commonKnowledge='true'">
                        <b title="+ Common Knowledge">
                            <xsl:text>+</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@noMoreChildren='true'">
                        <b title="No more children?">
                            <xsl:text>^</xsl:text>
                        </b>
                    </xsl:if>
                                                                    <xsl:if test="@author and (string-length(@author) > 0)">
                                                                        <a href="{concat($PlayerProfilesLocalLink,'#Player_',@author)}" title="player profile: {@author}">
                                                                            <xsl:value-of select="@author"/>
                                                                        </a>
                                                                        <xsl:text>: </xsl:text>
                                                                    </xsl:if>
                                                                    <xsl:text>card </xsl:text>
                                                                    <xsl:element name="a">
                                                                        <xsl:attribute name="href">
                                                                            <xsl:text>#</xsl:text>
                                                                            <xsl:value-of select="$IdeaCardLabel"/>
                                                                        </xsl:attribute>
                                                                        <xsl:attribute name="title">
                                                                            <xsl:value-of select="$IdeaCardNumberTitle"/>
                                                                        </xsl:attribute>
                                                                        <xsl:value-of select="@id"/> <!-- display card number -->
                                                                    </xsl:element>
                                                                    <xsl:text>, </xsl:text>
                                                                    <xsl:element name="a">
                                                                        <xsl:attribute name="href">
                                                                            <xsl:text>#</xsl:text>
                                                                            <xsl:value-of select="$IdeaCardLabel"/>
                                                                        </xsl:attribute>
                                                                        <xsl:attribute name="title">
                                                                            <xsl:value-of select="$IdeaCardNumberTitle"/>
                                                                        </xsl:attribute>
                                                                        <xsl:value-of select="$cardText"/>
                                                                    </xsl:element>
                                                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                                                    <xsl:text> (</xsl:text>
                                                                    <xsl:value-of select="count(descendant::*[not(@hidden='true')])"/>
                                                                    <xsl:text> children) </xsl:text>
                                                                </li>
                                                            </xsl:for-each>
                                                        </ul>
                                                    </td>
                                                </xsl:when>
                                                <xsl:when test="(//TopLevelCardTypes/DefendType/Type/@title = $cardType)">
                                                    <td>
                                                        <ul>
                                                            <xsl:for-each select="(//DefendCards/Card[@level='1'][@moveNumber = $currentCardMoveNumber][not(@hidden='true')])">
        <xsl:variable name="IdeaCardLabel">
            <xsl:text disable-output-escaping="yes">IdeaCard</xsl:text>
            <xsl:value-of select="@id"/>
        </xsl:variable>
        <xsl:variable name="dateText">
            <xsl:choose>
                <xsl:when test="@date or (string-length(@date) > 0)">
                    <xsl:text> (</xsl:text><xsl:value-of select="@date"/><xsl:text>)</xsl:text>
                </xsl:when>
                <xsl:otherwise>
                    <!-- null string -->
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="cardText" select="normalize-space(text()[1])"/>
        <xsl:variable name="IdeaCardNumberTitle">
            <xsl:text>bookmark: </xsl:text>
            <xsl:value-of select="$IdeaCardLabel"/>
            <xsl:text> </xsl:text>
            <xsl:value-of select="$dateText"/>
            <xsl:if test="//CardTree[@multipleMoves='true']">
                <xsl:text> Round </xsl:text>
                <xsl:value-of select="@moveNumber"/>
            </xsl:if>
        </xsl:variable>
                                                                <li>
                    <xsl:if test="@superInteresting='true'">
                        <b title="Super Interesting">
                            <xsl:text>*</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@commonKnowledge='true'">
                        <b title="+ Common Knowledge">
                            <xsl:text>+</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@noMoreChildren='true'">
                        <b title="No more children?">
                            <xsl:text>^</xsl:text>
                        </b>
                    </xsl:if>
                                                                    <xsl:if test="@author and (string-length(@author) > 0)">
                                                                        <a href="{concat($PlayerProfilesLocalLink,'#Player_',@author)}" title="player profile: {@author}">
                                                                            <xsl:value-of select="@author"/>
                                                                        </a>
                                                                        <xsl:text>: </xsl:text>
                                                                    </xsl:if>
                                                                    <xsl:text>card </xsl:text>
                                                                    <xsl:element name="a">
                                                                        <xsl:attribute name="href">
                                                                            <xsl:text>#</xsl:text>
                                                                            <xsl:value-of select="$IdeaCardLabel"/>
                                                                        </xsl:attribute>
                                                                        <xsl:attribute name="title">
                                                                            <xsl:value-of select="$IdeaCardNumberTitle"/>
                                                                        </xsl:attribute>
                                                                        <xsl:value-of select="@id"/> <!-- display card number -->
                                                                    </xsl:element>
                                                                    <xsl:text>, </xsl:text>
                                                                    <xsl:element name="a">
                                                                        <xsl:attribute name="href">
                                                                            <xsl:text>#</xsl:text>
                                                                            <xsl:value-of select="$IdeaCardLabel"/>
                                                                        </xsl:attribute>
                                                                        <xsl:attribute name="title">
                                                                            <xsl:value-of select="$IdeaCardNumberTitle"/>
                                                                        </xsl:attribute>
                                                                        <xsl:value-of select="$cardText"/>
                                                                    </xsl:element>
                                                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                                                    <xsl:text> (</xsl:text>
                                                                    <xsl:value-of select="count(descendant::*[not(@hidden='true')])"/>
                                                                    <xsl:text> children) </xsl:text>
                                                                </li>
                                                            </xsl:for-each>
                                                        </ul>
                                                    </td>
                                                </xsl:when>
                                            </xsl:choose>
                                        </tr>
                                    </table>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </xsl:when>
            <xsl:when test="($followOnTopLevelCard='true')">
                <!-- azure dividing bar before top-level seed cards -->
                <tr>
                    <td align="left" valign="top" colspan="{number($maxColumnCount) - 2}" class="toplevelcard">
                        <xsl:element name="a">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$IdeaCardLabel"/>
                                <!-- avoid duplicate creation of master reference; OK to create regular hyperlink if only appearing in summary table -->
                                <xsl:if test="not($recurse = 'true') and not($displayHiddenCards = 'summaryOnly')">
                                    <xsl:text>Reference</xsl:text>
                                </xsl:if>
                            </xsl:attribute>
                            <!-- <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>space before link -->
                        </xsl:element>
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </td>
                    <td align="right" class="toplevelcard">
                        <xsl:variable name="previousCardId">
                            <xsl:choose>
                                <xsl:when test="not(preceding-sibling::*[not(@hidden='true')][1])">
                                    <xsl:value-of select="following-sibling::*[not(@hidden='true')][$currentCardMoveNumber = @moveNumber][position() = last()]/@id"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="preceding-sibling::*[$currentCardMoveNumber = @moveNumber][1]/@id"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="nextCardId">
                            <xsl:choose>
                                <xsl:when test="not(following-sibling::*[not(@hidden='true')])">
                                    <xsl:value-of select="preceding-sibling::*[not(@hidden='true')][$currentCardMoveNumber = @moveNumber][position() = last()]/@id"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="following-sibling::*[not(@hidden='true')][$currentCardMoveNumber = @moveNumber][1]/@id"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:if test="(string-length($previousCardId) > 0)">
                            <a href="#index" title="previous Seed Card">
                                <xsl:attribute name="href">
                                    <xsl:text>#</xsl:text>
                                    <xsl:text>IdeaCard</xsl:text>
                                    <xsl:value-of select="$previousCardId"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:text>to previous top-level seed card </xsl:text>
                                    <xsl:value-of select="$previousCardId"/>
                                </xsl:attribute>
                                <img align="right" src="https://web.mmowgli.nps.edu/mmowMedia/images/triangleUp15w15h.png" width="15" height="15" border="0"/>
                            </a>
                            <br />
                        </xsl:if>
                        <xsl:if test="(string-length($nextCardId) > 0)">
                            <a href="#index" title="next Seed Card">
                                <xsl:attribute name="href">
                                    <xsl:text>#</xsl:text>
                                    <xsl:text>IdeaCard</xsl:text>
                                    <xsl:value-of select="$nextCardId"/>
                                </xsl:attribute>
                                <xsl:attribute name="title">
                                    <xsl:text>to next top-level seed card </xsl:text>
                                    <xsl:value-of select="$nextCardId"/>
                                </xsl:attribute>
                                <img align="right" src="https://web.mmowgli.nps.edu/mmowMedia/images/triangleDown15w15h.png" width="15" height="15" border="0"/>
                            </a>
                        </xsl:if>
                    </td>
                </tr>
            </xsl:when>
        </xsl:choose>

        <xsl:if test="(string-length($singleIdeaCardChainRootNumber) = 0) or ($singleIdeaCardChainRootNumber = $currentCardId) or (ancestor-or-self::*[@id = $currentCardId])">
            <!-- show card only if showing all cards or showing current card chain -->
            <tr>
                <xsl:call-template name="indent.for.loop">
                    <xsl:with-param name="i">1</xsl:with-param>
                    <xsl:with-param name="count">
                        <xsl:value-of select="@level"/>
                    </xsl:with-param>
                    <xsl:with-param name="cardBackgroundClass">
                        <xsl:value-of select="$cardBackgroundClass"/>
                    </xsl:with-param>
                </xsl:call-template>

                <xsl:variable name="indent" select="@level"/>
                <xsl:variable name="span" select="number($maxColumnCount - $indent - 2)"/>

                <xsl:call-template name="cardType">
                    <xsl:with-param name="cType">
                        <xsl:value-of select="@type"/>
                    </xsl:with-param>
                    <xsl:with-param name="color">
                        <xsl:value-of select="@color"/>
                    </xsl:with-param>
                    <xsl:with-param name="cardLabel">
                        <xsl:value-of select="@type"/>
                    </xsl:with-param>
                    <xsl:with-param name="size">
                        <xsl:text>Small</xsl:text>
                    </xsl:with-param>
                </xsl:call-template>

                <td align="center" title="{$IdeaCardNumberTitle}" width="{$cardCellWidth}" class="cardCell {$cardBackgroundClass}">
                    <xsl:if test="not($followOnTopLevelCard='true')">
                        <xsl:element name="a">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$IdeaCardLabel"/>
                                <!-- avoid duplicate creation of master reference; OK to create regular hyperlink if only appearing in summary table -->
                                <xsl:if test="not($recurse = 'true') and not($displayHiddenCards = 'summaryOnly')">
                                    <xsl:text>Reference</xsl:text>
                                </xsl:if>
                            </xsl:attribute>
                            <!-- <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>space before link -->
                        </xsl:element>
                    </xsl:if>
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:text>#</xsl:text>
                            <xsl:value-of select="$IdeaCardLabel"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:value-of select="$IdeaCardNumberTitle"/>
                        </xsl:attribute>
                        <xsl:value-of select="@id"/> <!-- display card number -->
                    </xsl:element>
                    <!-- <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>space after link -->
                </td>
                <td class="longtext {$cardBackgroundClass}" colspan="{$span}" title="{$cardText}">
                    <xsl:if test="@superInteresting='true'">
                        <b title="Super Interesting">
                            <xsl:text>*</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@commonKnowledge='true'">
                        <b title="+ Common Knowledge">
                            <xsl:text>+</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@noMoreChildren='true'">
                        <b title="No more children?">
                            <xsl:text>^</xsl:text>
                        </b>
                    </xsl:if>
                    <xsl:if test="@author and (string-length(@author) > 0)">
                        <!-- comma looks too busy if prepended here: -->
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <a href="{concat($PlayerProfilesLocalLink,'#Player_',@author)}" title="player profile: {@author}">
                            <xsl:value-of select="@author"/>
                        </a>
                        <xsl:text>:</xsl:text>
                    </xsl:if>
                    <xsl:text> </xsl:text>
                    <xsl:variable name="roundColor">
                        <xsl:choose>
                            <xsl:when test="(@moveNumber='2')">
                                <xsl:text>MidnightBlue</xsl:text>
                            </xsl:when>
                            <xsl:when test="(@moveNumber='3')">
                                <xsl:text>navy</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- default is black -->
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    
                    <!-- show card content -->
                    <xsl:choose>
                        <xsl:when test="(@hidden = 'true') and (($displayHiddenCards = 'true') or (($displayHiddenCards = 'summaryOnly') and not($recurse = 'true')))">
                            <!-- strike or strikethrough style, do not link urls since hidden and may be undesirable -->
                            <del title="hidden: {.}" style="color:$roundColor">
                                <xsl:value-of select="." disable-output-escaping="yes"/>
                            </del>
                            <!-- link to game -->
                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!86_{@id}" target="{$gameAcronym}Game" title="play the game! go online to Card {@id}" >
                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" align="right" width="16px" border="0"/>
                            </a>
                        </xsl:when>
                        <xsl:when test="(@hidden = 'true') and not($displayHiddenCards = 'true') and ($singleIdeaCardChainRootNumber = $currentCardId)">
                            <!-- showing only single card but it is hidden -->
                            <del title="a Game Master has marked this card Hidden" style="color:$roundColor">
                                <xsl:text>card content is hidden</xsl:text>
                            </del>
                        </xsl:when>
                        <xsl:when test="(@hidden = 'true') and not($displayHiddenCards = 'true')">
                            <!-- completely hidden -->
                        </xsl:when>
                        <!-- Titles may vary for top-level InnovateCards and DefendCards -->
                        <xsl:when test="(@type = (//InnovateCards/Card[1]/@type)) or (@type = (//DefendCards/Card[1]/@type))">
                            <!-- top-level parent cards are bold -->
                            <b style="color:{$roundColor};">
                                <xsl:call-template name="hyperlink">
                                    <xsl:with-param name="string" select="$cardText"/> <!-- normalize-space(text() doesn't work -->
                                </xsl:call-template>
                            </b>
                            <!-- link to game -->
                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!86_{@id}" target="{$gameAcronym}Game" title="play the game! go online to Card {@id}">
                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" align="right" width="16px" border="0"/>
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- child cards are not bold -->
                            <span style="color:{$roundColor};">
                                <xsl:call-template name="hyperlink">
                                    <xsl:with-param name="string" select="$cardText"/> <!-- normalize-space(text() doesn't work -->
                                </xsl:call-template>
                            </span>
                            <!-- link to game -->
                            <a href="https://mmowgli.nps.edu/{$gameAcronym}#!86_{@id}" target="{$gameAcronym}Game" title="play the game! go online to Card {@id}">
                                <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" align="right" width="16px" border="0"/>
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </td>
                <!-- perhaps extra space column for spanning avoids some problems??
                <td>
                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                </td>
                 -->
                <!--
                    <div style="max-width:500px;">
                    </div>

                       <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@type = 'Best Strategy'">
                        <td colspan="{$span}" title="{$ctext}">
                            <b>
                                <div style="max-width:500px;">
                                    <xsl:value-of select="text()"/>
                                </div>
                            </b>
                        </td>
                    </xsl:when>
                    <xsl:when test="@type = 'Worst Strategy'">
                        <td colspan="{$span}" title="{$ctext}">
                            <b>
                                <div style="max-width:500px;">
                                    <xsl:value-of select="@id"/>&#160;
                                    <xsl:value-of select="text()"/>
                                </div>
                            </b>
                        </td>
                    </xsl:when>
                    <xsl:otherwise>
                        <td colspan="{$span}" title="{$ctext}">
                            <div style="max-width:500px;">
                                <xsl:value-of select="@id"/>&#160;
                                <xsl:value-of select="."/>
                            </div>
                        </td>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:variable name="cauthor" select="@author"/>
                <td title="{$cauthor}">
                    <div style="max-width:150px;">&#160;
                        <xsl:value-of select="@author"/>
                    </div>
                </td>
                <xsl:variable name="cdate" select="@date"/>
                -->
            </tr>
        </xsl:if>
        
        <!-- Recurse and display children cards -->
        <xsl:choose>
            <xsl:when test="($recurse = 'true') and (($displayHiddenCards = 'true')) or (string-length($singleIdeaCardChainRootNumber) > 0)">
                <xsl:apply-templates select="*">
                    <xsl:sort select="position()" data-type="number" order="descending"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:when test="($recurse = 'true') and not($displayHiddenCards = 'true')">
                <xsl:apply-templates select="*[not(@hidden='true')]">
                    <xsl:sort select="position()" data-type="number" order="descending"/>
                </xsl:apply-templates>
            </xsl:when>
        </xsl:choose>

    </xsl:template>

    <!-- default match for text() is to ignore -->
    <xsl:template match="text()"/>

    <xsl:template match="InnovateCards">
        <xsl:param name="roundNumber">
            <xsl:text></xsl:text>
        </xsl:param>
        <!-- debug
        <p>
            <xsl:text>InnovateCards looping over Round </xsl:text>
            <xsl:value-of select="$roundNumber"/>
            <xsl:text>, $displayHiddenCard=</xsl:text>
            <xsl:value-of select="$displayHiddenCards"/>
        </p> -->
        <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
            <xsl:choose>
                <xsl:when test="(string-length($singleIdeaCardChainRootNumber) > 0)">
                    <xsl:apply-templates select="descendant-or-self::Card[@id = $singleIdeaCardChainRootNumber]"/>
                </xsl:when>
                <xsl:when test="(string-length($roundNumber) > 0) and ($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*[@moveNumber = $roundNumber]"/>
                </xsl:when>
                <xsl:when test="(string-length($roundNumber) > 0) and not($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*[@moveNumber = $roundNumber][not(@hidden='true')]"/>
                </xsl:when>
                <xsl:when test="($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="*[not(@hidden='true')]"/>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>

    <xsl:template match="DefendCards">
        <xsl:param name="roundNumber">
            <xsl:text></xsl:text>
        </xsl:param>
        <!-- debug
        <p>
            <xsl:text>DefendCards looping over Round </xsl:text>
            <xsl:value-of select="$roundNumber"/>
            <xsl:text>, $displayHiddenCard=</xsl:text>
            <xsl:value-of select="$displayHiddenCards"/>
        </p> -->
        <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
            <xsl:choose>
                <xsl:when test="(string-length($singleIdeaCardChainRootNumber) > 0)">
                    <xsl:apply-templates select="descendant-or-self::Card[@id = $singleIdeaCardChainRootNumber]"/>
                </xsl:when>
                <xsl:when test="(string-length($roundNumber) > 0) and ($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*[@moveNumber = $roundNumber]"/>
                </xsl:when>
                <xsl:when test="(string-length($roundNumber) > 0) and not($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*[@moveNumber = $roundNumber][not(@hidden='true')]"/>
                </xsl:when>
                <xsl:when test="($displayHiddenCards = 'true')">
                    <xsl:apply-templates select="*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="*[not(@hidden='true')]"/>
                </xsl:otherwise>
            </xsl:choose>
        </table>
    </xsl:template>

    <xsl:template match="CardTree">
        <!-- Refactor
        <h1>
            <xsl:text>Card Tree</xsl:text>
            <xsl:if test="GameSummary">
                <xsl:text>, MMOWGLI </xsl:text>
                <xsl:value-of select="GameSummary"/>
            </xsl:if>
        </h1>
        <xsl:apply-templates select="GameSummary"/>
        -->

        <xsl:if test="(string-length($singleIdeaCardChainRootNumber) = 0)">
            <hr />
        </xsl:if>
        <xsl:apply-templates select="InnovateCards"/>
        <br />
        <xsl:if test="(string-length($singleIdeaCardChainRootNumber) = 0)">
            <hr />
        </xsl:if>
        <xsl:apply-templates select="DefendCards"/>
    </xsl:template>

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

        <!--
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
                <meta name="identifier" content="https:// TODO /IdeaCardChains.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta http-equiv="refresh" content="600"/> <!-- 600 seconds = 10 minutes -->
                <meta name="author"      content="Don Brutzman and Mike Bailey"/>
                <meta name="description" content="Idea card chain outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="IdeaCardChains_{$gameTitle}.html"/>
                <meta name="identifier"  content="https://mmowgli.nps.edu/{$gameAcronym}/reports/IdeaCardChain_{$gameTitle}.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, https://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, https://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>

                <xsl:element name="title">
                    <xsl:text disable-output-escaping="yes">Idea Card Chains Report, </xsl:text>
                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                    <xsl:text> MMOWGLI game</xsl:text>
                </xsl:element>
<!--  when mangling the code to display properly in a browser window, the linefeed her screw us up.
Jam the code altogether (see below), but keep this commented section for readablity.
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
.toplevelcard {
    background-color:azure;
}
.commonknowledgecard {
    background-color:#F2F2FF;
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
-->
		<style type="text/css">
table {    border-collapse:collapse;}table.banner{    padding:5px 20px;}td.cardCell {    align:center;    width:60px;}td.cardCellSmall {    align:center;    width:60px;    font:35%;    color:white;}td.longtext {    /* white-space: nowrap; */    overflow: hidden;}.toplevelcard {    background-color:azure;}.commonknowledgecard {    background-color:#F2F2FF;}.innovateStrategy {    background-color:#00ab4f;}.defendStrategy {    background-color:#FFD700; /* #6d3695; */}.expand {    background-color:#f39025; /* #f37025; */}.counter {    background-color:#ee1111; /* #bf1961 */}.adapt {    background-color:#047cc2;}.explore {    background-color:#9933cc; /* #97c93c */}.lightgreylink {a:link    {color:lightgrey;}  /* unvisited link */a:visited {color:lightgrey;}  /* visited link */a:hover   {color:lightgrey;}  /* mouse over link */a:active  {color:lightgrey;}  /* selected link */text-shadow:; /* off */}
		</style>
            </head>
            <body>
                <a name="index"></a>
                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
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
                            <xsl:text>https://portal.mmowgli.nps.edu</xsl:text>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <!-- page header -->
                
                <!-- test if full report or single card chain -->
                <xsl:if test="(string-length($singleIdeaCardChainRootNumber) = 0)">
                    <table align="center" border="0" class="banner">
                        <tr border="0">
                            <td align="center">
                                <p>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                </p>
                                <h1 align="center">
                                        <xsl:text disable-output-escaping="yes"> Idea Card Chains Report </xsl:text>
                                </h1>
                                <h2 align="center">
                                    <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/> <!-- want escaped <br /> intact for line break -->
                                    <xsl:text disable-output-escaping="yes"> Game </xsl:text>
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
                                <br />
                            </td>
                        </tr>
                    </table>
                    <table align="center" border="0" class="banner" cellpadding="4">
                        <tr border="1" valign="top" style="background-color:lightgrey">
                            <td align="left">
                                <!-- Table of Contents -->
                                <xsl:if test="(//CallToAction/*)">
                                    <b>
                                        <xsl:text>Player Motivation and Game Purpose</xsl:text>
                                    </b>
                                    <ul>
                                            <li>
                                                <a href="#CallToAction" title="Motivation and purpose for this game">
                                                    <xsl:text>Call To Action</xsl:text>
                                                </a>
                                                <xsl:text> video</xsl:text>
                                                <xsl:if test="(count(//CallToAction) > 1)">
                                                    <xsl:text>s </xsl:text>
                                                </xsl:if>
                                                <xsl:if test="(count(//CallToAction) > 1)">
                                                    <xsl:text> (</xsl:text>
                                                    <xsl:for-each select="//CallToAction">
                                                        <a href="#CallToActionRound{@round}">
                                                            <xsl:text>Round </xsl:text>
                                                            <xsl:value-of select="@round"/>
                                                        </a>
                                                        <xsl:if test="(position() != last())">
                                                            <xsl:text>, </xsl:text>
                                                        </xsl:if>
                                                    </xsl:for-each>
                                                    <xsl:text>)</xsl:text>
                                                </xsl:if>
                                            </li>
                                    </ul>
                                </xsl:if>
                                
                                <!-- TOC links for top-level seed cards -->
                                <b>
                                    <xsl:text> Idea Card Chains</xsl:text>
                                </b>
                                <xsl:if test="//CardTree[@multipleMoves='true']">
                                    <xsl:text>: Innovate and Defend</xsl:text>
                                </xsl:if>
                                
                                <ul>
                                    <!-- link to Innovate and Defend categories -->
                                    <xsl:choose>
                                        <!-- multiple rounds, present index links by round then by listing both categories of innovate/defend -->
                                        <xsl:when test="//CardTree[@multipleMoves='true']">
                                            <xsl:for-each select="//CardTree/TopLevelCardTypes/InnovateType/Type">
                                                <!-- loop over each @round -->
                                                <xsl:variable name="roundNumber"  select="@round"/>
                                                <xsl:variable name="innovateType" select="//TopLevelCardTypes/InnovateType/Type[@round=$roundNumber]/@title"/>
                                                <xsl:variable name=  "defendType" select="//TopLevelCardTypes/DefendType/Type[  @round=$roundNumber]/@title"/>
                                                <li>
                                                    <xsl:text> Round </xsl:text>
                                                    <xsl:value-of select="$roundNumber"/>
                                                    <xsl:text>. </xsl:text>
                                                    <a href="#{translate(normalize-space($innovateType),' ','')}" title="{$innovateType} Idea Card Chains">
                                                        <xsl:value-of select="$innovateType"/>
                                                    </a>
                                                    <xsl:text> (</xsl:text>
                                                      <xsl:value-of select="count(//InnovateCards/Card[not(@hidden='true')][@moveNumber=$roundNumber][@type = $innovateType])"/>
                                                    <xsl:text> total)</xsl:text>
                                                    
                                                    <xsl:text> and </xsl:text>
                                                    <a href="#{translate(normalize-space($defendType),' ','')}" title="{$defendType} Idea Card Chains">
                                                        <xsl:value-of select="$defendType"/>
                                                    </a>
                                                    <xsl:text> (</xsl:text>
                                                      <xsl:value-of select="count(//DefendCards/Card[not(@hidden='true')][@moveNumber=$roundNumber][@type = $defendType])"/>
                                                    <xsl:text> total)</xsl:text>
                                                </li>
                                            </xsl:for-each>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <!-- single round, present index links by listing both categories of innovate/defend -->
                                            <li>
                                                <a href="#{translate(normalize-space($innovateCardName),' ','')}" title="{$innovateCardName} Idea Card Chains">
                                                  <xsl:value-of select="$innovateCardName"/>
                                                </a>
                                                <xsl:text> and </xsl:text>
                                                <a href="#{translate(normalize-space($defendCardName),' ','')}" title="{$defendCardName} Idea Card Chains">
                                                    <xsl:value-of select="$defendCardName"/>
                                                </a>
                                            </li>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </ul>

                                <a href="#License" title="License, Terms and Conditions for this content"><xsl:text>License, Terms, Conditions</xsl:text></a>
                                and
                                <a href="#Contact" title="Contact links for further information"><xsl:text>Contact</xsl:text></a>
                            </td>
                            <td>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            </td>
                            <td align="center">
                                <!-- key to Card Categories -->
                                <p align="center" width="80%" sytle="font-size:small;">
                                    <xsl:variable name="innovateCardCount" select="count(//InnovateCards/*[not(@hidden='true')])"/>
                                    <xsl:variable name=  "defendCardCount" select="count(//DefendCards/*[not(@hidden='true')])"/>
                                    <xsl:variable name=  "expandCardCount" select="count(//Card[@type='Expand'][not(@hidden='true')])"/>
                                    <xsl:variable name=   "adaptCardCount" select="count(//Card[@type='Adapt'][not(@hidden='true')])"/>
                                    <xsl:variable name= "counterCardCount" select="count(//Card[@type='Counter'][not(@hidden='true')])"/>
                                    <xsl:variable name= "exploreCardCount" select="count(//Card[@type='Explore'][not(@hidden='true')])"/>

                                    <xsl:text> Idea Card Categories and Counts </xsl:text>
                                    <span title="total does not include Hidden cards">
                                        <xsl:text> (</xsl:text>
                                        <b>
                                            <xsl:value-of select="$innovateCardCount + $defendCardCount + $exploreCardCount + $expandCardCount + $adaptCardCount + $counterCardCount"/>
                                            <xsl:text> total</xsl:text>
                                        </b>
                                        <xsl:text>)</xsl:text>
                                    </span>
                                    <table align="center" border="0" cellpadding="5" style="color: lightgrey; text-shadow: 1px 1px #000000;">
                                        <tr align="center">
                                            <!-- Innovate and Defend cards; use generic title if card titles have changed due to more than one round -->
                                            <xsl:choose>
                                                <xsl:when test="//CardTree[@multipleMoves='true']">
                                                    <xsl:call-template name="cardType">
                                                        <xsl:with-param name="cType">
                                                            <xsl:text>Innovate</xsl:text>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="color">
                                                            <xsl:value-of select="$innovateCardColor"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="cardLabel">
                                                            <xsl:text> Innovate </xsl:text>
                                                            <br />
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="$innovateCardCount"/>
                                                        </xsl:with-param>
                                                    </xsl:call-template>
                                                    <xsl:call-template name="cardType">
                                                        <xsl:with-param name="cType">
                                                            <xsl:text>Defend</xsl:text>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="color">
                                                            <xsl:value-of select="$defendCardColor"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="cardLabel">
                                                            <xsl:text> Defend </xsl:text>
                                                            <br />
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="$defendCardCount"/>
                                                        </xsl:with-param>
                                                    </xsl:call-template>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:call-template name="cardType">
                                                        <xsl:with-param name="cType">
                                                            <xsl:value-of select="$innovateCardName"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="color">
                                                            <xsl:value-of select="$innovateCardColor"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="cardLabel">
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="translate($innovateCardName,' ','&#160;')"/>
                                                            <xsl:text> </xsl:text>
                                                            <br />
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="$innovateCardCount"/>
                                                        </xsl:with-param>
                                                    </xsl:call-template>
                                                    <xsl:call-template name="cardType">
                                                        <xsl:with-param name="cType">
                                                            <xsl:value-of select="$defendCardName"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="color">
                                                            <xsl:value-of select="$defendCardColor"/>
                                                        </xsl:with-param>
                                                        <xsl:with-param name="cardLabel">
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="translate($defendCardName,' ','&#160;')"/>
                                                            <xsl:text> </xsl:text>
                                                            <br />
                                                            <xsl:text> </xsl:text>
                                                            <xsl:value-of select="$defendCardCount"/>
                                                        </xsl:with-param>
                                                    </xsl:call-template>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                            <!-- Cards with names that don't change -->
                                            <xsl:call-template name="cardType">
                                                <xsl:with-param name="cType">
                                                    <xsl:text>Expand</xsl:text>
                                                </xsl:with-param>
                                                <xsl:with-param name="color">
                                                  <xsl:value-of select="$expandCardColor"/>
                                                </xsl:with-param>
                                                <xsl:with-param name="cardLabel">
                                                    <xsl:text> Expand </xsl:text>
                                                    <br />
                                                    <xsl:value-of select="$expandCardCount"/>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                            <xsl:call-template name="cardType">
                                                <xsl:with-param name="cType">
                                                    <xsl:text>Counter</xsl:text>
                                                </xsl:with-param>
                                                <xsl:with-param name="color">
                                                  <xsl:value-of select="$counterCardColor"/>
                                                </xsl:with-param>
                                                <xsl:with-param name="cardLabel">
                                                    <xsl:text> Counter </xsl:text>
                                                    <br />
                                                    <xsl:value-of select="$counterCardCount"/>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                            <xsl:call-template name="cardType">
                                                <xsl:with-param name="cType">
                                                    <xsl:text>Adapt</xsl:text>
                                                </xsl:with-param>
                                                <xsl:with-param name="color">
                                                  <xsl:value-of select="$adaptCardColor"/>
                                                </xsl:with-param>
                                                <xsl:with-param name="cardLabel">
                                                    <xsl:text> Adapt </xsl:text>
                                                    <br />
                                                    <xsl:value-of select="$adaptCardCount"/>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                            <xsl:call-template name="cardType">
                                                <xsl:with-param name="cType">
                                                    <xsl:text>Explore</xsl:text>
                                                </xsl:with-param>
                                                <xsl:with-param name="color">
                                                  <xsl:value-of select="$exploreCardColor"/>
                                                </xsl:with-param>
                                                <xsl:with-param name="cardLabel">
                                                    <xsl:text> Explore </xsl:text>
                                                    <br />
                                                    <xsl:value-of select="$exploreCardCount"/>
                                                </xsl:with-param>
                                            </xsl:call-template>
                                        </tr>
                                    </table>
                                </p>

                                <xsl:variable name= "superInterestingCardCount" select="count(//Card[(@superInteresting='true')])"/>
                                <xsl:variable name=  "commonKnowledgeCardCount" select="count(//Card[(@commonKnowledge='true')])"/>
                                <xsl:variable name=       "noMoreChildrenCardCount" select="count(//Card[(@noMoreChildren='true')])"/>
                                <xsl:variable name=           "hiddenCardCount" select="count(//Card[(@hidden='true')])"/>

                                <!-- key to Game Master flags -->
                                <p align="center" width="80%">
                                    <xsl:text> Game Master flagged cards </xsl:text>
                                    <table align="center" border="1" cellpadding="5" style="background-color:#eeeeee">
                                        <tr style="font-size:small;align:center">
                                            <td align="center" title="as selected by a Game Master">
                                                <b title="Super Interesting"><xsl:text> * </xsl:text></b>
                                                <a href="#SuperInterestingCards"><i><xsl:text>Super Interesting</xsl:text></i></a>
                                                <br />
                                                <xsl:text> </xsl:text>
                                                <xsl:value-of select="$superInterestingCardCount"/>
                                            </td>
                                            <td align="center" title="considered obvious or a known requirement">
                                                <b title="Common Knowledge"><xsl:text> + </xsl:text></b>
                                                <a href="#CommonKnowledgeCards"><i><xsl:text>Common Knowledge</xsl:text></i></a>
                                                <br />
                                                <xsl:text> </xsl:text>
                                                <xsl:value-of select="$commonKnowledgeCardCount"/>
                                            </td>
                                            <td align="center" title="considered as having no relevant value to the game topics">
                                                <xsl:text> </xsl:text>
                                                <b title="No More Children">
                                                    <xsl:text>^</xsl:text>
                                                </b>
                                                <xsl:text> </xsl:text>
                                                <a href="#NoChildrenCards"><i><xsl:text>No More Children</xsl:text></i></a>
                                                <br />
                                                <xsl:text> </xsl:text>
                                                <xsl:value-of select="$noMoreChildrenCardCount"/>
                                            </td>
                                            <td align="center" title="only viewable by game masters">
                                                <!--
                                                    <xsl:text>strikethrough</xsl:text>
                                                    <xsl:text>: </xsl:text>
                                                -->
                                                <del title="Hidden">
                                                    <a href="#HiddenCards"><i><xsl:text>Hidden</xsl:text></i></a>
                                                </del>
                                                <br />
                                                <xsl:text> </xsl:text>
                                                <xsl:value-of select="$hiddenCardCount"/>
                                            </td>
                                        </tr>
                                    </table>
                                </p>
                            </td>
                            <td>
                                <xsl:text disable-output-escaping="yes"> &amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp; </xsl:text>
                            </td>
                            <td valign="middle">
                                <xsl:text> Corresponding report products: </xsl:text>
                                <ul>
                                    <li>
                                        <a href="index.html" title="Reports Index for this game"><xsl:text>Reports Index</xsl:text></a>
                                    </li>
                                    <xsl:if test="string-length($ActionPlanLocalLink) > 0">
                                        <li>
                                            <a href="{$ActionPlanLocalLink}">Action Plans</a>
                                        </li>
                                    </xsl:if>
                                    <li>
                                        <!-- Sunburst Visualizer -->
                                        <a href="cardSunburstVisualizer.html" title="Idea Card Sunburst Visualizer">
                                            <xsl:text disable-output-escaping="yes">Idea&amp;nbsp;Card Sunburst&amp;nbsp;Visualizer</xsl:text>
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
                
                <table align="center" width="100%">
                    <tr align="top">
                        <td align="left">
                            <h2 title="Motivation and purpose for this game">
                                <a name="CallToAction">
                                    <xsl:text> Call to Action: Player Motivation and Game Purpose</xsl:text>
                                </a>
                            </h2>
                            <!-- Debug diagnostic -->
                            <xsl:comment><xsl:text>$gameLabel=</xsl:text><xsl:value-of select="$gameLabel"/></xsl:comment>
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
                                <a href="{normalize-space($videoAlternateUrl)}" target="blank">this</a>)
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
            <!-- Call To Action complete -->

        </xsl:if>
                
        <hr />
            
        <!-- Top-Level Seed Cards

        <h1 style="background-color:lightgray;" align="center">
            <a name="SeedCardsRound{@round}"> 
                <xsl:text>Top-Level Seed Cards</xsl:text>
                <xsl:if test="//CallToAction[@round = '2']">
                    <xsl:text>, Round </xsl:text>
                    <xsl:value-of select="@round"/>
                </xsl:if>
            </a>
        </h1>
        -->
            
        <!-- Top-Level Seed Cards Complete -->

        <!-- Process CardTree/*/Card(s) -->
        <!-- process CardTree by Round, for InnovateCards and DefendCards -->
        <xsl:choose>
            <!-- multiple rounds, sort cards first by round then by categories of innovate/defend -->
            <xsl:when test="//CardTree[@multipleMoves='true']">
                <xsl:for-each select="//CardTree/TopLevelCardTypes/InnovateType/Type">
                    <!-- loop over each @round -->
                    <xsl:variable name="roundNumber"  select="@round"/>
                    <!-- debug
                    <p>
                        <xsl:text>Top-level looping over Round </xsl:text>
                        <xsl:value-of select="$roundNumber"/>
                    </p> -->
                    <!-- InnovateCards then DefendCards for this round -->
                    <xsl:apply-templates select="//InnovateCards">
                        <xsl:with-param name="roundNumber">
                            <xsl:value-of select="$roundNumber"/>
                        </xsl:with-param>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="//DefendCards">
                        <xsl:with-param name="roundNumber">
                            <xsl:value-of select="$roundNumber"/>
                        </xsl:with-param>
                    </xsl:apply-templates>
                    <!--
                    <xsl:choose>
                        <xsl:when test="(($displayHiddenCards = 'true')) or (string-length($singleIdeaCardChainRootNumber) > 0)">
                            <xsl:apply-templates select="//InnovateCards/Card[@moveNumber = $roundNumber]">
                                <xsl:sort select="position()" data-type="number" order="descending"/>
                            </xsl:apply-templates>
                            <xsl:apply-templates select="//DefendCards/Card[@moveNumber = $roundNumber]">
                                <xsl:sort select="position()" data-type="number" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:when test="not($displayHiddenCards = 'true')">
                            <xsl:apply-templates select="//InnovateCards/Card[@moveNumber = $roundNumber][not(@hidden='true')]">
                                <xsl:sort select="position()" data-type="number" order="descending"/>
                            </xsl:apply-templates>
                            <xsl:apply-templates select="//DefendCards/Card[@moveNumber = $roundNumber][not(@hidden='true')]">
                                <xsl:sort select="position()" data-type="number" order="descending"/>
                            </xsl:apply-templates>
                        </xsl:when>
                    </xsl:choose>
                    -->
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>     
                <!-- single round -->              
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
        

        <xsl:if test="(string-length($singleIdeaCardChainRootNumber) = 0)">

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
                <xsl:choose>
                    <xsl:when test="starts-with($gameTitle,'pcc') or starts-with($gameTitle,'PCC') or contains($gameTitle,'pcc') or contains($gameTitle,'PCC')">
                        <xsl:text> Cards marked "Common Knowledge" are found in the approved requirements references.</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> Cards marked "Common Knowledge" by a game master are considered to be obvious.</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
                
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
                                    <xsl:text>^</xsl:text>
                                </b>
                                <xsl:text> No More Children</xsl:text>
                            </a>
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="count(//Card[@noMoreChildren='true'])"/>
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
                <xsl:text> Cards marked "No More Children" by a game master are considered as having being extensions of top level cards.</xsl:text>
            </p>
            <table border="1" style="table-layout:fixed;width:100%;overflow:hidden;">
                <!-- do not recurse, only show cards of interest -->
                <xsl:apply-templates select="//Card[@noMoreChildren='true']">
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
                    <xsl:when test="($displayHiddenCards = 'true') or ($displayHiddenCards = 'summaryOnly')">
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
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
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
                <a href="https://creativecommons.org/licenses/by-sa/3.0" target="blank">Creative Commons 3.0 "By Attribution - Share Alike"</a>
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
                <a href="https://portal.mmowgli.nps.edu/game-wiki/-/wiki/PlayerResources/Terms+and+Conditions" target="blank">Terms and Conditions</a>
                of the game, which include the
                <a href="https://movesinstitute.org/mmowMedia/MmowgliGameParticipantInformedConsent.html" target="blank">Informed Consent to Participate in Research</a>
                and the
                <a href="https://www.defense.gov/socialmedia/user-agreement.aspx" target="blank">Department of Defense Social Media User Agreement</a>.
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
                Additional information is available online for the
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
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="concat-text-nodes">
        <xsl:param name="string" select="string(.)" />
        <xsl:value-of select="$string[1]"/>
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
                        <xsl:variable name="IdeaCardChainLink">
                            <xsl:choose>
                                <xsl:when test="contains(regex-group(2),'2011.1')">
                                    <xsl:text>IdeaCardChainPiracy2011.1.html</xsl:text>
                                </xsl:when>
                                <xsl:when test="contains(regex-group(2),'2011.2')">
                                    <xsl:text>IdeaCardChainPiracy2011.2.html</xsl:text>
                                </xsl:when>
                                <xsl:when test="contains(regex-group(2),'2011.3')">
                                    <xsl:text>IdeaCardChainPiracy2011.3.html</xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!-- same file -->
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <!-- bookmark #IdeaCard1234 - see IdeaCardLabel above for consistency -->
                        <a href="{concat($IdeaCardChainLink,'#IdeaCard',regex-group(6))}">
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </a>
                        <!-- <xsl:text> </xsl:text> -->
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <!-- Third: for non-url remainder(s), now find and link 'Action Plan 456' references -->
                        <xsl:analyze-string select="concat(' ',normalize-space(.),' ')" regex="(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Aa][Cc][Tt][Ii][Oo][Nn]\s*)?[Pp][Ll][Aa][Nn]\s*#?\s*(\d+))">
                            <xsl:matching-substring>
                                <!-- bookmark #ActionPlan456 - see ActionPlanLabel above for consistency -->
                                <a href="{concat($ActionPlanLocalLink,'#ActionPlan',regex-group(5))}">
                                    <xsl:value-of select="." disable-output-escaping="yes"/>
                                </a>
                                <!-- <xsl:text> </xsl:text> -->
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <!-- avoid returning excess whitespace -->
                                <xsl:if test="string-length(normalize-space(.)) > 0">
                                    <xsl:value-of select="." disable-output-escaping="yes" />
                                </xsl:if>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>
