<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <!--  xmlns:mmow="http://mmowgli.nps.edu" xmlns="http://mmowgli.nps.edu" -->
    <!-- default parameter values can be overridden when invoking this stylesheet -->
    <xsl:param name="displaySingleActionPlanNumber"></xsl:param>
    <!-- displayHiddenPlans: true, false  -->
    <xsl:param name="displayHiddenPlans">true</xsl:param>

    <xsl:output method="html"/>

    <xsl:variable name="gameTitle">
        <!-- Piracy2012, Piracy2011.1, Energy2012, etc. -->
        <xsl:value-of select="//GameTitle"/>
    </xsl:variable>

    <xsl:variable name="gameSecurity">
        <!-- open, FOUO, etc. -->
        <xsl:value-of select="//GameSecurity"/>
    </xsl:variable>

    <xsl:variable name="exportDateTime">
        <xsl:value-of select="//ActionPlanList/@exported"/>
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
                <xsl:value-of disable-output-escaping="yes" select="//GameTitle"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="gameUrl">
        <!-- piracyMMOWGLI, energyMMOWGLI, etc. -->
        <xsl:choose>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.1')">
                <xsl:text>https://mmowgli.nps.edu/piracy2011.1</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.2')">
                <xsl:text>https://mmowgli.nps.edu/piracy2011.2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2011.3')">
                <xsl:text>https://mmowgli.nps.edu/piracy2011.3</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'iracy') and contains($gameTitle,'2012')">
                <xsl:text>https://mmowgli.nps.edu/piracy</xsl:text><!-- 2012 -->
            </xsl:when>
            <xsl:when test="contains($gameTitle,'nergy')">
                <xsl:text>https://mmowgli.nps.edu/energy</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'bii') or contains($gameTitle,'Bii')">
                <xsl:text>https://mmowgli.nps.edu/bii</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'Em2') or contains($gameTitle,'em2') or contains($gameTitle,'em')">
                <xsl:text>https://mmowgli.nps.edu/em2</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'vtp')"> <!-- evtp -->
                <xsl:text>https://mmowgli.nps.edu/evtp</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'am') or starts-with($gameTitle,'Am') or contains($gameTitle,'additive') or contains($gameTitle,'Additive')">
                <xsl:text>https://mmowgli.nps.edu/am</xsl:text>
            </xsl:when>
            <xsl:when test="starts-with($gameTitle,'cap2con') or starts-with($gameTitle,'Cap2con') or contains($gameTitle,'cap2con') or contains($gameTitle,'Cap2con')">
                <xsl:text>https://mmowgli.nps.edu/cap2con</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'uxvdm') or contains($gameTitle,'Uxvdm')">
                <xsl:text>https://mmowgli.nps.edu/uxvdm</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'darkportal') or contains($gameTitle,'dark')">
                <xsl:text>https://mmowgli.nps.edu/darkportal</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ig')">
                <xsl:text>https://mmowgli.nps.edu/ig</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ig')">
                <xsl:text>https://mmowgli.nps.edu/ig</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'training')">
                <xsl:text>https://mmowgli.nps.edu/training</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'navair') or contains($gameTitle,'nsc')">
                <xsl:text>https://mmowgli.nps.edu/nsc</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'blackswan') or contains(lower-case($gameTitle),'swan')">
                <xsl:text>https://mmowgli.nps.edu/blackswan</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'dd') or contains(lower-case($gameTitle),'DD')">
                <xsl:text>https://mmowgli.nps.edu/dd</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'pcc') or contains(lower-case($gameTitle),'PCC')">
                <xsl:text>https://mmowgli.nps.edu/pcc</xsl:text>
            </xsl:when>
            <xsl:when test="contains($gameTitle,'ndu') or contains($gameTitle,'NDU')">
                <xsl:text>https://mmowgli.nps.edu/ndu</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <!-- TODO add GameIdentifier in source XML, then insert here -->
                <xsl:text>https://mmowgli.nps.edu/TODOinsertGameIdentifier</xsl:text>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="XmlSourceFileName">
        <xsl:text>ActionPlanList_</xsl:text>
        <xsl:value-of select="$gameTitle"/>
        <xsl:text>.xml</xsl:text>
    </xsl:variable>

    <xsl:variable name="IdeaCardChainLocalLink">
        <!-- n.b. defined twice in this stylesheet, ensure consistent or links will break -->
        <xsl:text>IdeaCardChain_</xsl:text>
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

    <xsl:variable name="numberOfRounds">
        <xsl:value-of select="max(//CallToAction/@round)"/>
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
            <!-- GameSummary contained text, only -->
            <xsl:value-of select="Title"/>
        </h2>
    </xsl:template>

    <xsl:template match="ActionPlan">
        <xsl:variable name="ActionPlanLabel">
            <!-- n.b. defined twice in this stylesheet, ensure consistent or links will break -->
            <xsl:text>ActionPlan</xsl:text>
            <xsl:value-of select="ID"/>
        </xsl:variable>
        <xsl:variable name="authorCount" select="count(Author)"/>
        <xsl:variable name="imageCount" select="count(ImageList/Image)"/>
        <xsl:variable name="videoCount" select="count(VideoList/Video)"/>
        <xsl:variable name="chatLogMessageCount" select="count(ChatLog/Message)"/>
        <xsl:variable name="playerCommentsCount" select="count(CommentList/Comment)"/>
        <!-- report ranking of plan in this round in XML -->
        <xsl:variable name="ranking">
          <xsl:choose>
            <xsl:when test="string-length(@roundRanking) > 0">
              <xsl:value-of select="@roundRanking"/>
            </xsl:when>
            <xsl:otherwise>
              <xsl:text>TBD</xsl:text>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:variable name="roundNumber" select="@moveNumber"/>
        <xsl:variable name="numberPlansInRound" select="count(//ActionPlan[@moveNumber=$roundNumber])"/>
        <!-- debug 
        <xsl:comment>
            <xsl:text>$ActionPlanLabel=</xsl:text>
            <xsl:value-of select="$ActionPlanLabel"/>
            <xsl:text>, $ranking=</xsl:text>
            <xsl:value-of select="$ranking"/>
            <xsl:text>, $roundNumber=</xsl:text>
            <xsl:value-of select="$roundNumber"/>
            <xsl:text>, $numberPlansInRound=</xsl:text>
            <xsl:value-of select="$numberPlansInRound"/>
            <xsl:text>, @hidden=</xsl:text>
            <xsl:value-of select="@hidden"/>
            <xsl:text>, count(preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')])=</xsl:text>
  <xsl:value-of select="count(preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')])"/>
                  <xsl:text>, preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')][1]/@moveNumber=</xsl:text>
        <xsl:value-of select="preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')][1]/@moveNumber"/>
        </xsl:comment>
        -->
        
        <!-- print header if already-sorted Action Plans start a new round -->
        <xsl:if test="(count(preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')]) = 0)">
            <!--
            ($numberOfRounds != '1') and 
                      ((($roundNumber = '1') and (count(preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')]) = 0)) or 
                       ( $roundNumber != preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')][1]/@moveNumber))
                       -->
            <h1 style="background-color:lightgray;" align="center">
                <a name="ActionPlansRound{$roundNumber}"> 
                    <xsl:text> Action Plans </xsl:text>
                    <xsl:if test="($numberOfRounds != '1')">
                        <xsl:text>Round </xsl:text>
                        <xsl:value-of select="$roundNumber"/>
                    </xsl:if>
                </a>
                <xsl:text> (</xsl:text>
                <xsl:value-of select="count(//ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')])"/>
                <xsl:text> plans</xsl:text>
                <xsl:if test="($numberOfRounds != '1')">
                    <xsl:text> out of </xsl:text>
                    <xsl:value-of select="count(//ActionPlan[not(@hidden='true')])"/>
                </xsl:if>
                <xsl:text> total)</xsl:text>
            </h1>
            <hr />
        </xsl:if>

        <table border="0" width="100%" cellpadding="0">
            <tr>
                <td align="left" valign="middle">
                    <xsl:element name="h2">
                        <xsl:element name="a">
                            <xsl:attribute name="name">
                                <xsl:value-of select="$ActionPlanLabel"/>
                            </xsl:attribute>
                            <xsl:choose>
                                <xsl:when test="(@hidden='true')">
                                    <!-- strike or strikethrough style.  put grey color on text only so that tooltip title remains readable. -->
                                    <del title="[hidden: Action Plan {ID}]">
                                        <div style="color:grey">
                                            <xsl:text> Action Plan </xsl:text>
                                            <xsl:value-of select="ID"/>
                                            <xsl:text> (hidden)</xsl:text>
                                        </div>
                                    </del>
                                </xsl:when>
                                <xsl:otherwise>
                                            <xsl:text> Action Plan </xsl:text>
                                            <xsl:value-of select="ID"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:element>
                        <xsl:if test="@superInteresting='true'">
                            <xsl:text> </xsl:text>
                            <b title="Super Interesting">
                                <xsl:text>*</xsl:text>
                            </b>
                        </xsl:if>
                    </xsl:element>
                </td>
                <td align="left" valign="top">
                    <xsl:if test="(string-length($displaySingleActionPlanNumber) = 0)">
                        <!-- only show go-to-top shortcut image if showing all plans -->
                        <a href="#index" title="to top">
                            <!-- 1158 x 332, width="386" height="111"  -->
                            <img align="right" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="165" height="47" border="0"/>
                        </a>
                    </xsl:if>
                </td>
            </tr>
            <tr>
                <td align="left">
            <!-- =========================== ID =========================== -->
        <dl style="border:none; margin:0;">
            <dt> ID </dt>
            <dd>
                <!-- TODO  add game identifier information and link -->

                <!-- self-linked bookmark anchor for easy user reference to a given image in the page -->
                <xsl:element name="a">
                    <xsl:attribute name="name">
                        <xsl:value-of select="$ActionPlanLabel"/>
                    </xsl:attribute>
                    <xsl:element name="a">
                        <xsl:attribute name="href">
                            <xsl:text>#</xsl:text>
                            <xsl:value-of select="$ActionPlanLabel"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:text>bookmark for this plan: </xsl:text>
                            <xsl:value-of select="$ActionPlanLabel"/>
                        </xsl:attribute>
                        <xsl:text>Action Plan </xsl:text>
                        <xsl:value-of select="ID"/>
                    </xsl:element>
                </xsl:element>
                <xsl:text> for </xsl:text>
                <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                <xsl:if test="//ActionPlanList[@multipleMoves='true']">
                    <xsl:text>, </xsl:text>
                    <xsl:text>Round </xsl:text>
                    <xsl:value-of select="@moveNumber"/>
                </xsl:if>
            </dd>

            <!-- =========================== Title =========================== -->
            <dt>
                <a name="{$ActionPlanLabel}Title">Title</a>
            </dt>
            <dd>
                <b>
                    <xsl:call-template name="hyperlink">
                        <xsl:with-param name="string" select="normalize-space(Title/.)"/>
                    </xsl:call-template>
                </b>
            </dd>
            <dt> <a name="{$ActionPlanLabel}Rating">Rating</a> </dt>
            <dd>
                <b>
                    <xsl:value-of select="format-number(@thumbs,'#.#')"/>
                    <xsl:choose>
                        <xsl:when test="not(@thumbs) or (string-length(format-number(@thumbs,'#.#')) = 0)">
                            <xsl:text>0.0</xsl:text>
                        </xsl:when>
                        <xsl:when test="not(contains(format-number(@thumbs,'#.#'),'.'))">
                            <xsl:text>.0</xsl:text>
                        </xsl:when>
                    </xsl:choose>
                </b>
                <xsl:text disable-output-escaping="yes"> &amp;quot;thumbs up&amp;quot; score (range from 1 to 3) </xsl:text>
                <xsl:text> with </xsl:text>
                <b title="Number of players voting">
                    <xsl:value-of select="@numVoters"/>
                </b>
                <xsl:text> player votes received, ranking </xsl:text>
                <xsl:value-of select="$ranking"/>
                <xsl:text> out of </xsl:text>
                <xsl:value-of select="$numberPlansInRound"/>
                <xsl:text> plans</xsl:text>
                <xsl:if test="($numberOfRounds != '1')">
                    <xsl:text> in Round </xsl:text>
                    <xsl:value-of select="@moveNumber"/>
                </xsl:if>
                <xsl:text>.</xsl:text>
                <xsl:if test="@superInteresting='true'">
                    <br />
                    <b title="Super Interesting">
                        <xsl:text>* </xsl:text>
                    </b>
                    <xsl:text>This plan has been marked </xsl:text>
                    <b>
                        <xsl:text> Super Interesting </xsl:text>
                    </b>
                    <xsl:text> by a </xsl:text>
                    <xsl:text> Game Master </xsl:text>
                </xsl:if>
            </dd>
            <!-- =========================== Idea Card Chain =========================== -->
            <dt> <a name="{$ActionPlanLabel}IdeaCardChain">Idea Card Chain Providing Original Motivation</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="(string-length($IdeaCardChainLocalLink) > 0) and (string-length(CardChainRoot/@ID) > 0)">
                        <xsl:element name="a">
                            <xsl:attribute name="href">
                                <xsl:value-of select="$IdeaCardChainLocalLink"/>
                                <xsl:text>#IdeaCard</xsl:text>
                                <xsl:value-of select="CardChainRoot/@ID"/>
                            </xsl:attribute>
                            <xsl:attribute name="title">
                                <xsl:text>local link to Idea Card Chain that initiated this Action Plan </xsl:text>
                                <xsl:value-of select="CardChainRoot/@ID"/>
                            </xsl:attribute>
                            <xsl:text> Idea Card Chain </xsl:text>
                            <xsl:value-of select="CardChainRoot/@ID"/>
                        </xsl:element>
                    </xsl:when>
                    <xsl:when test="(string-length($IdeaCardChainLocalLink) > 0)">
                        <!-- missing CardChainRoot/@ID -->
                        <xsl:text> [missing] </xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- unlinked since no local idea card chain file is available -->
                        <xsl:text> Idea Card Chain </xsl:text>
                        <b>
                            <xsl:value-of select="CardChainRoot/@ID"/>
                        </b>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="CardChainRoot/@author">
                    <xsl:text> started by player </xsl:text>
                    <!-- TODO color code; title with date/time; also title with text -->
                    <a href="{concat($PlayerProfilesLocalLink,'#Player_',CardChainRoot/@author)}" title="player profile: {CardChainRoot/@author}">
                        <xsl:value-of select="CardChainRoot/@author"/>
                    </a>
                    <xsl:text>: </xsl:text>
                </xsl:if>
                <xsl:call-template name="hyperlink">
                    <xsl:with-param name="string" select="normalize-space(CardChainRoot/.)"/>
                </xsl:call-template>
            </dd>
            
            <!-- =========================== Authors =========================== -->
            <!-- trace -->
            <xsl:comment>
                <xsl:text>$authorCount=</xsl:text>
                <xsl:value-of select="$authorCount"/>
            </xsl:comment>
            <!-- TODO: linkify -->
            <xsl:choose>
                <!-- Must have at least one Author, otherwise erroneous database entry -->
                <xsl:when test="$authorCount = 0">
                    <dt> <a name="{$ActionPlanLabel}Authors">Author</a> </dt>
                    <dd>
                        <xsl:text> [no entry provided] </xsl:text>
                    </dd>
                </xsl:when>
                <xsl:when test="($authorCount = 1) and not(Author/GameName)">
                    <dt> <a name="{$ActionPlanLabel}Authors">Author</a> </dt>
                    <dd>
                        <xsl:text> [no entry provided] </xsl:text>
                    </dd>
                </xsl:when>
                <xsl:when test="$authorCount = 1">
                    <dt> <a name="{$ActionPlanLabel}Authors">Author</a> </dt>
                    <dd>
                        <!-- link authors via GameId, not game name -->
                        <a href="{concat($PlayerProfilesLocalLink,'#Player_',Author/GameId)}" title="player profile: {Author/GameName}">
                            <xsl:value-of select="Author/GameName"/>
                        </a>
                        <xsl:if test="(string-length(normalize-space(Author/Location)) > 0) and (normalize-space(Author/Location/.) != 'optional')">
                            <xsl:text> (</xsl:text>
                            <xsl:value-of select="Author/Location"/>
                            <xsl:text>) </xsl:text>
                        </xsl:if>
                    </dd>
                </xsl:when>
                <xsl:otherwise>
                    <dt> <a name="{$ActionPlanLabel}Authors">Co-Authors</a> </dt>
                    <dd>
                        <xsl:for-each select="Author">
                            <!-- link authors via GameId, not game name -->
                            <a href="{concat($PlayerProfilesLocalLink,'#Player_',GameId)}" title="player profile: {GameName}">
                                <xsl:value-of select="GameName"/>
                            </a>
                            <xsl:if test="string-length(Location) > 0">
                                <xsl:text> (</xsl:text>
                                <xsl:value-of select="Location"/>
                                <xsl:text>)</xsl:text>
                            </xsl:if>
                            <xsl:if test="$authorCount > position()">
                                <xsl:text>, </xsl:text>
                            </xsl:if>
                        </xsl:for-each>
                    </dd>
                </xsl:otherwise>
            </xsl:choose>
            
            <!-- =========================== Who Is Involved? =========================== -->
            <dt> <a name="{$ActionPlanLabel}WhoIsInvolved">Who Is Involved?</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="string-length(WhoIsInvolved) > 0">
                        <xsl:call-template name="hyperlink">
                            <xsl:with-param name="string" select="normalize-space(WhoIsInvolved/.)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> [no entry provided] </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </dd>
            <!-- =========================== What Is It? =========================== -->
            <dt> <a name="{$ActionPlanLabel}WhatIsIt">What Is It?</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="string-length(WhatIsIt) > 0">
                        <xsl:call-template name="hyperlink">
                            <xsl:with-param name="string" select="normalize-space(WhatIsIt/.)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> [no entry provided] </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </dd>
            <!-- =========================== What Will It Take? =========================== -->
            <dt> <a name="{$ActionPlanLabel}WhatWillItTake">What Will It Take?</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="string-length(WhatWillItTake) > 0">
                        <xsl:call-template name="hyperlink">
                            <xsl:with-param name="string" select="normalize-space(WhatWillItTake/.)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> [no entry provided] </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </dd>
            <!-- =========================== How Will It Work? =========================== -->
            <dt> <a name="{$ActionPlanLabel}HowWillItWork">How Will It Work?</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="string-length(HowWillItWork) > 0">
                        <xsl:call-template name="hyperlink">
                            <xsl:with-param name="string" select="normalize-space(HowWillItWork/.)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> [no entry provided] </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </dd>
            <!-- =========================== How Will It Change the Situation? =========================== -->
            <dt> <a name="{$ActionPlanLabel}HowWillItChangeThings">How Will It Change the Situation?</a> </dt>
            <dd>
                <xsl:choose>
                    <xsl:when test="string-length(HowWillItChangeThings) > 0">
                        <xsl:call-template name="hyperlink">
                            <xsl:with-param name="string" select="normalize-space(HowWillItChangeThings/.)"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text> [no entry provided] </xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </dd>
        </dl>
            <!-- =========================== Action Plan local Table of Contents (TOC) =========================== -->
                </td>
                <td valign="top">
                    <xsl:if test="(string-length($displaySingleActionPlanNumber) = 0)">
                        <!-- only show local TOC if showing all plans -->
                        <ul align="left" style="font-size:60%;">
                            <xsl:if test="(count(//ActionPlan) > 1)">
                                <li>
                                    <xsl:if test="(count(//ActionPlan) > 2)">
                                        <xsl:choose>
                                            <xsl:when          test="preceding-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')]">
                                                <a href="#ActionPlan{preceding-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')][1]/ID/.}">Previous</a>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <a href="#ActionPlan{following-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')][position()=last()]/ID/.}">Last</a>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                        <xsl:text> and </xsl:text>
                                    </xsl:if>
                                    <xsl:choose>
                                        <xsl:when          test="following-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')]">
                                            <a href="#ActionPlan{following-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')][1]/ID/.}">Next</a>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <a href="#ActionPlan{preceding-sibling::ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')][position()=last()]/ID/.}">First</a>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                    <xsl:text> plan </xsl:text>
                                </li>
                            </xsl:if>
                            <li> <a href="#{$ActionPlanLabel}Title">Title</a> </li>
                            <li> <a href="#{$ActionPlanLabel}Rating">Rating</a> </li>
                            <li> <a href="#{$ActionPlanLabel}IdeaCardChain">Idea Card Chain</a> </li>
                            <li> <a href="#{$ActionPlanLabel}WhoIsInvolved">Who Is Involved</a> </li>
                            <li> <a href="#{$ActionPlanLabel}WhatIsIt">What Is It</a> </li>
                            <li> <a href="#{$ActionPlanLabel}WhatWillItTake">What Will It Take</a> </li>
                            <!-- establish column width for TOC table -->
                            <li> <a href="#{$ActionPlanLabel}HowWillItChangeThings"><xsl:text disable-output-escaping="yes">How&amp;nbsp;Will&amp;nbsp;It&amp;nbsp;Change&amp;nbsp;Things</xsl:text></a> </li>
                            <xsl:choose>
                                <xsl:when test="$authorCount = 1">
                                    <li> <a href="#{$ActionPlanLabel}Authors">Author</a> </li>
                                </xsl:when>
                                <xsl:when test="$authorCount > 1">
                                    <li> <a href="#{$ActionPlanLabel}Authors">Authors</a> </li>
                                </xsl:when>
                                <!-- otherwise none -->
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$imageCount = 1">
                                    <li> <a href="#{$ActionPlanLabel}Images">Image</a> </li>
                                </xsl:when>
                                <xsl:when test="$imageCount > 1">
                                    <li> <a href="#{$ActionPlanLabel}Images">Images</a> </li>
                                </xsl:when>
                                <!-- otherwise none -->
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$videoCount = 1">
                                    <li> <a href="#{$ActionPlanLabel}Videos">Video</a> </li>
                                </xsl:when>
                                <xsl:when test="$videoCount > 1">
                                    <li> <a href="#{$ActionPlanLabel}Videos">Videos</a> </li>
                                </xsl:when>
                                <!-- otherwise none -->
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$chatLogMessageCount = 1">
                                <li> <a href="#{$ActionPlanLabel}ChatLog">Author Chat Message</a> </li>
                                </xsl:when>
                                <xsl:when test="$chatLogMessageCount > 1">
                                <li> <a href="#{$ActionPlanLabel}ChatLog">Author Chat Messages</a> </li>
                                </xsl:when>
                                <!-- otherwise none -->
                            </xsl:choose>
                            <xsl:choose>
                                <xsl:when test="$playerCommentsCount = 1">
                                <li> <a href="#{$ActionPlanLabel}PlayerComments">Player Comment</a> </li>
                                </xsl:when>
                                <xsl:when test="$playerCommentsCount > 1">
                                <li> <a href="#{$ActionPlanLabel}PlayerComments">Player Comments</a> </li>
                                </xsl:when>
                                <!-- otherwise none -->
                            </xsl:choose>
                            <li>
                                <!-- link to game -->
                                <a href="https://mmowgli.nps.edu/{$gameAcronym}#!92_{ID}" target="_z{$gameAcronym}Game" title="play the game! go online to Action Plan {ID}">
                                    <xsl:text>Go to game</xsl:text>
                                </a>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                <a href="https://mmowgli.nps.edu/{$gameAcronym}#!92_{ID}" target="{$gameAcronym}Game" title="play the game! go online to Action Plan {ID}">
                                    <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" width="16px" border="0"/>
                                </a>
                            </li>
                        </ul>
                    </xsl:if>
                </td>
            </tr>
            <tr>
                <td valign="top" colspan="2">
                    <dl style="border:none; margin:0;">
                        <!-- =========================== Images =========================== -->
                        <!-- May have zero or more Image definitions -->
                        <xsl:if test="$imageCount > 0">
                            <xsl:variable name="ActionPlanImageLabel">
                                <xsl:value-of select="$ActionPlanLabel"/>
                                <xsl:text>Images</xsl:text>
                            </xsl:variable>
                            <xsl:text>&#10;</xsl:text>
                            <a name="{$ActionPlanImageLabel}">
                                <xsl:choose>
                                    <xsl:when test="$imageCount = 1">
                                        <dt> Image </dt>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <dt> Images </dt>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </a>
                            <dd>
                                <table class="media">
                                    <xsl:for-each select="ImageList/Image">
                                        <xsl:variable name="imageUrl">
                                            <xsl:choose>
                                                <xsl:when test="starts-with(normalize-space(URL),'http')">
                                                    <xsl:value-of select="URL"/>
                                                </xsl:when>
                                                <xsl:when test="starts-with(normalize-space(URL),'../')">
                                                    <xsl:value-of select="$gameUrl"/>
                                                    <xsl:if test="not(ends-with($gameUrl,'/'))">
                                                        <xsl:text>/</xsl:text>
                                                    </xsl:if>
                                                    <!--<xsl:text>reports/</xsl:text>-->
                                                    <xsl:value-of select="substring-after(normalize-space(URL),'../')"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="$gameUrl"/>
                                                    <xsl:if test="not(ends-with($gameUrl,'/')) and not(starts-with(URL,'/'))">
                                                        <xsl:text>/</xsl:text>
                                                    </xsl:if>
                                                    <xsl:value-of select="normalize-space(URL)"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:variable>
                                        <!-- Debug
                                        <xsl:comment>
                                            <xsl:text> $gameUrl=</xsl:text>
                                            <xsl:value-of select="$gameUrl"/>
                                            <xsl:text>, $imageUrl=</xsl:text>
                                            <xsl:value-of select="$imageUrl"/>
                                        </xsl:comment>
                                        -->
                                        <tr>
                                            <td align="right" valign="top">
                                                <!-- self-linked bookmark anchor for easy user reference to a given image in the page -->
                                                <xsl:variable name="ActionPlanImageLabel">
                                                    <xsl:value-of select="$ActionPlanLabel"/>
                                                    <xsl:text>Image</xsl:text>
                                                    <xsl:value-of select="position()"/>
                                                </xsl:variable>
                                                <xsl:element name="a">
                                                    <xsl:attribute name="name">
                                                        <xsl:value-of select="$ActionPlanImageLabel"/>
                                                    </xsl:attribute>
                                                    <xsl:element name="a">
                                                        <xsl:attribute name="href">
                                                            <xsl:text>#</xsl:text>
                                                            <xsl:value-of select="$ActionPlanImageLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="title">
                                                            <xsl:text>bookmark for this image: </xsl:text>
                                                            <xsl:value-of select="$ActionPlanImageLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:value-of select="position()"/>
                                                    </xsl:element>
                                                </xsl:element>
                                            </td>
                                            <td align="right">
                                                <xsl:element name="a">
                                                    <xsl:attribute name="href">
                                                        <xsl:value-of select="$imageUrl"/>
                                                    </xsl:attribute>
                                                    <xsl:element name="img">
                                                        <xsl:attribute name="src">
                                                            <xsl:value-of select="$imageUrl"/>
                                                        </xsl:attribute>
                                                        <xsl:choose>
                                                            <xsl:when test="(@height > 600)">
                                                                <xsl:attribute name="height">
                                                                    <xsl:text>600</xsl:text>
                                                                </xsl:attribute>
                                                                <!-- width to match -->
                                                            </xsl:when>
                                                            <xsl:when test="(string-length(@height) > 0)">
                                                                <xsl:attribute name="height">
                                                                    <xsl:value-of select="@height"/>
                                                                </xsl:attribute>
                                                                <!-- width to match -->
                                                            </xsl:when>
                                                            <xsl:when test="(@width > 800)">
                                                                <xsl:attribute name="width">
                                                                    <xsl:text>800</xsl:text>
                                                                </xsl:attribute>
                                                                <!-- height to match -->
                                                            </xsl:when>
                                                            <xsl:when test="(string-length(@width) > 0)">
                                                                <xsl:attribute name="width">
                                                                    <xsl:value-of select="@width"/>
                                                                </xsl:attribute>
                                                                <!-- height to match -->
                                                            </xsl:when>
                                                        </xsl:choose>
                                                        <!--<img src="{$url}" width="{$width}" height="{$height}"/> -->
                                                    </xsl:element>
                                                </xsl:element>
                                                <br />
                                            </td>
                                            <td align="left" valign="top">
                                                <!-- TODO get correct name, also update ActionPlanList.xsd and ActionPlanExporter.java -->
                                                <xsl:if test="Title">
                                                    <p>
                                                        <b>
                                                            <xsl:call-template name="hyperlink">
                                                                <xsl:with-param name="string">
                                                                    <xsl:value-of disable-output-escaping="yes" select="normalize-space(Title/.)"/>
                                                                </xsl:with-param>
                                                            </xsl:call-template>
                                                        </b>
                                                    </p>
                                                </xsl:if>
                                                <xsl:if test="Title">
                                                    <p>
                                                        <xsl:call-template name="hyperlink">
                                                            <xsl:with-param name="string">
                                                                <xsl:value-of disable-output-escaping="yes" select="normalize-space(Description/.)"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                    </p>
                                                </xsl:if>
                                                <xsl:if test="Caption and not(contains(normalize-space(Description/.),
                                                                                       normalize-space(Caption/.)))">
                                                    <p>
                                                        <xsl:call-template name="hyperlink">
                                                            <xsl:with-param name="string">
                                                                <xsl:value-of disable-output-escaping="yes" select="normalize-space(Caption/.)"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                    </p>
                                                </xsl:if>
                                                <p>
                                                    <a href="{$imageUrl}"><xsl:value-of select="$imageUrl"/></a>
                                                </p>
                                            </td>
                                        </tr>
                                        <xsl:if test="not(position() = last())">
                                            <td align="center" valign="top" colspan="3">
                                                <hr />
                                            </td>
                                        </xsl:if>
                                    </xsl:for-each>
                                </table>
                            </dd>
                        </xsl:if>
                        <!-- =========================== Videos =========================== -->
                        <!-- May have zero or more Image definitions -->
                        <xsl:if test="$videoCount > 0">
                            <xsl:variable name="ActionPlanVideoLabel">
                                <xsl:value-of select="$ActionPlanLabel"/>
                                <xsl:text>Videos</xsl:text>
                            </xsl:variable>
                            <xsl:text>&#10;</xsl:text>
                            <a name="{$ActionPlanVideoLabel}">
                                <xsl:choose>
                                    <xsl:when test="$videoCount = 1">
                                        <dt> Video </dt>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <dt> Videos </dt>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </a>
                            <dd>
                                <table class="media">
                                    <xsl:for-each select="VideoList/Video">
                                        <xsl:variable name="videoUrl">
                                            <xsl:choose>
                                                <!-- fix software bug in URL creation -->
                                                <xsl:when test="contains(URL,'watch?v=watch?v=')">
                                                    <xsl:value-of select="normalize-space(substring-before(URL,'watch?v='))"/>
                                                    <xsl:value-of select="normalize-space(substring-after (URL,'watch?v='))"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="normalize-space(URL)"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:variable>
                                        <!-- build a variable to hold the munged url -->
                                        <xsl:variable name="youTubeEmbedUrl">
                                            <xsl:analyze-string select="$videoUrl" regex="(https://www.youtube.com/)watch\?v=(.*)">
                                                <xsl:matching-substring>
                                                    <xsl:value-of select="regex-group(1)"/>
                                                    <xsl:text>v/</xsl:text>
                                                    <xsl:value-of select="regex-group(2)"/>
                                                </xsl:matching-substring>
                                                <xsl:non-matching-substring>
                                                    <xsl:value-of select="$videoUrl"/>
                                                </xsl:non-matching-substring>
                                            </xsl:analyze-string>
                                            <!--
                                            <xsl:text disable-output-escaping="yes">?version=3&amp;feature-player_detailpage</xsl:text>
                                            -->
                                            <!-- prevent advertising other YouTube videos when complete
                                            <xsl:text disable-output-escaping="yes">&amp;rel=0</xsl:text> -->
                                        </xsl:variable>
                                        <tr>
                                            <td align="right" valign="top">
                                                <!-- self-linked bookmark anchor for easy user reference to a given image in the page -->
                                                <xsl:variable name="ActionPlanVideoLabel">
                                                    <xsl:value-of select="$ActionPlanLabel"/>
                                                    <xsl:text>Video</xsl:text>
                                                    <xsl:value-of select="position()"/>
                                                </xsl:variable>
                                                <xsl:element name="a">
                                                    <xsl:attribute name="name">
                                                        <xsl:value-of select="$ActionPlanVideoLabel"/>
                                                    </xsl:attribute>
                                                    <xsl:element name="a">
                                                        <xsl:attribute name="href">
                                                            <xsl:text>#</xsl:text>
                                                            <xsl:value-of select="$ActionPlanVideoLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="title">
                                                            <xsl:text>bookmark for this video: </xsl:text>
                                                            <xsl:value-of select="$ActionPlanVideoLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:value-of select="position()"/>
                                                    </xsl:element>
                                                </xsl:element>
                                            </td>
                                            <td align="right" valign="top">
                                                <!-- use the adapted videoUrl variable contents -->
                                                <object style="height: 390px; width: 640px">
                                                    <param name="movie" value="{$youTubeEmbedUrl}"/>
                                                    <param name="allowFullScreen" value="true"/>
                                                    <param name="allowScriptAccess" value="always"/>
                                                    <embed src="{$youTubeEmbedUrl}" type="application/x-shockwave-flash" allowfullscreen="true" allowScriptAccess="always" width="640" height="360"/>
                                                </object>
                                            </td>
                                            <td align="left" valign="top">
                                                <!-- TODO get correct name, also update ActionPlanList.xsd and ActionPlanExporter.java -->
                                                <xsl:if test="Title">
                                                    <p>
                                                        <b>
                                                            <xsl:call-template name="hyperlink">
                                                                <xsl:with-param name="string">
                                                                    <xsl:value-of disable-output-escaping="yes" select="normalize-space(Title/.)"/>
                                                                </xsl:with-param>
                                                            </xsl:call-template>
                                                        </b>
                                                    </p>
                                                </xsl:if>
                                                <xsl:if test="Description">
                                                    <p>
                                                        <xsl:call-template name="hyperlink">
                                                            <xsl:with-param name="string">
                                                                <xsl:value-of disable-output-escaping="yes" select="normalize-space(Description/.)"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                    </p>
                                                </xsl:if>
                                                <xsl:if test="Caption and not(contains(normalize-space(Description/.),
                                                                                       normalize-space(Caption/.)))
                                                                      and not(contains(normalize-space(Caption/.),'Describe this video here'))">
                                                    <p>
                                                        <xsl:call-template name="hyperlink">
                                                            <xsl:with-param name="string">
                                                                <xsl:value-of disable-output-escaping="yes" select="normalize-space(Caption/.)"/>
                                                            </xsl:with-param>
                                                        </xsl:call-template>
                                                    </p>
                                                </xsl:if>
                                                <p>
                                                    <a href="{$videoUrl}"><xsl:value-of select="$videoUrl"/></a>
                                                </p>
                                            </td>
                                        </tr>
                                        <xsl:if test="not(position() = last())">
                                            <td align="center" valign="top" colspan="3">
                                                <hr />
                                            </td>
                                        </xsl:if>
                                    </xsl:for-each>
                                </table>
                            </dd>
                        </xsl:if>
                        <!-- =========================== Author ChatLog Messages =========================== -->
                        <!-- May have zero or more Image definitions -->
                        <xsl:if test="$chatLogMessageCount > 0">
                            <xsl:variable name="ActionPlanChatLogLabel">
                                <xsl:value-of select="$ActionPlanLabel"/>
                                <xsl:text>ChatLog</xsl:text>
                            </xsl:variable>
                            <xsl:text>&#10;</xsl:text>
                            <a name="{$ActionPlanChatLogLabel}">
                                <dt>
                                    <xsl:text> Author-to-Author Chat Message</xsl:text>
                                    <xsl:if test="$chatLogMessageCount > 1">
                                        <xsl:text>s</xsl:text>
                                    </xsl:if>
                                </dt>
                            </a>
                            <dd>
                                <table cellpadding="5" class="chatLog">
                                    <xsl:for-each select="ChatLog/Message[not(@hidden='true')]">
                                        <xsl:sort select="position()" data-type="number" order="descending"/>
                                        <tr>
                                            <td align="right" valign="top">
                                                <xsl:if test="@superInteresting='true'">
                                                    <b title="Super Interesting">
                                                        <xsl:text>*</xsl:text>
                                                    </b>
                                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                                </xsl:if>
                                                <!-- self-linked bookmark anchor for easy user reference to a given image in the page -->
                                                <xsl:variable name="ActionPlanChatLabel">
                                                    <xsl:value-of select="$ActionPlanLabel"/>
                                                    <xsl:text>ChatLog</xsl:text>
                                                    <xsl:value-of select="position()"/>
                                                </xsl:variable>
                                                <xsl:element name="a">
                                                    <xsl:attribute name="name">
                                                        <xsl:value-of select="$ActionPlanChatLabel"/>
                                                    </xsl:attribute>
                                                    <xsl:element name="a">
                                                        <xsl:attribute name="href">
                                                            <xsl:text>#</xsl:text>
                                                            <xsl:value-of select="$ActionPlanChatLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="title">
                                                            <xsl:text>bookmark for this author-chat message: </xsl:text>
                                                            <xsl:value-of select="$ActionPlanChatLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:value-of select="position()"/>
                                                    </xsl:element>
                                                </xsl:element>
                                            </td>
                                            <td align="right" valign="top">
                                                <!-- fix column breaks -->
                                                <!-- translate(@title,' ','&#160;')" &amp;nbsp; -->
                                                <xsl:value-of select="translate(substring-before(@postTime,' 201'),' ','&#160;')"/><xsl:text disable-output-escaping="yes">&amp;nbsp;201</xsl:text><xsl:value-of select="substring-after(@postTime,' 201')"/>
                                                <xsl:if test="//ActionPlanList[@multipleMoves='true']">
                                                    <xsl:text>, </xsl:text>
                                                    <xsl:text>Round </xsl:text>
                                                    <xsl:value-of select="@moveNumber"/>
                                                </xsl:if>
                                           </td>
                                            <td align="left" valign="top">
                                                <i>
                                                    <a href="{concat($PlayerProfilesLocalLink,'#Player_',@from)}" title="player profile: {@from}">
                                                        <xsl:value-of select="@from"/>
                                                    </a>
                                                    <xsl:text>: </xsl:text>
                                                </i>
                                                <xsl:call-template name="hyperlink">
                                                    <xsl:with-param name="string" select="normalize-space(.)"/>
                                                </xsl:call-template>
                                                <br/>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </dd>
                        </xsl:if>
                        <!-- =========================== Player Comments =========================== -->
                        <!-- May have zero or more Image definitions -->
                        <xsl:if test="$playerCommentsCount > 0">
                            <xsl:variable name="ActionPlanPlayerCommentLabel">
                                <xsl:value-of select="$ActionPlanLabel"/>
                                <xsl:text>PlayerComments</xsl:text>
                            </xsl:variable>
                            <xsl:text>&#10;</xsl:text>
                            <a name="{$ActionPlanPlayerCommentLabel}">
                                <dt>
                                    <xsl:text>Player Comment</xsl:text>
                                    <xsl:if test="$playerCommentsCount > 1">
                                        <xsl:text>s</xsl:text>
                                    </xsl:if>
                                </dt>
                            </a>
                            <dd>
                                <table cellpadding="5" class="textLog">
                                    <xsl:for-each select="CommentList/Comment[not(@hidden='true')]">
                                        <xsl:sort select="position()" data-type="number" order="descending"/>
                                        <tr>
                                            <td align="right" valign="top">
                                                <xsl:if test="@superInteresting='true'">
                                                    <b title="Super Interesting">
                                                        <xsl:text>*</xsl:text>
                                                    </b>
                                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                                </xsl:if>
                                                <!-- self-linked bookmark anchor for easy user reference to a given image in the page -->
                                                <xsl:variable name="ActionPlanPlayerCommentsLabel">
                                                    <xsl:value-of select="$ActionPlanLabel"/>
                                                    <xsl:text>PlayerComment</xsl:text>
                                                    <xsl:value-of select="position()"/>
                                                </xsl:variable>
                                                <xsl:element name="a">
                                                    <xsl:attribute name="name">
                                                        <xsl:value-of select="$ActionPlanPlayerCommentsLabel"/>
                                                    </xsl:attribute>
                                                    <xsl:element name="a">
                                                        <xsl:attribute name="href">
                                                            <xsl:text>#</xsl:text>
                                                            <xsl:value-of select="$ActionPlanPlayerCommentsLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:attribute name="title">
                                                            <xsl:text>bookmark for this player comment: </xsl:text>
                                                            <xsl:value-of select="$ActionPlanPlayerCommentsLabel"/>
                                                        </xsl:attribute>
                                                        <xsl:value-of select="position()"/>
                                                    </xsl:element>
                                                </xsl:element>
                                            </td>
                                            <td align="right" valign="top">
                                                <!-- fix column breaks -->
                                                <!-- translate(@title,' ','&#160;')" &amp;nbsp; -->
                                                <xsl:value-of select="translate(substring-before(@postTime,' 201'),' ','&#160;')"/><xsl:text disable-output-escaping="yes">&amp;nbsp;201</xsl:text><xsl:value-of select="substring-after(@postTime,' 201')"/>
                                                <xsl:if test="//ActionPlanList[@multipleMoves='true']">
                                                    <xsl:text>, </xsl:text>
                                                    <xsl:text>Round </xsl:text>
                                                    <xsl:value-of select="@moveNumber"/>
                                                </xsl:if>
                                            </td>
                                            <td align="left" valign="top">
                                                <i>
                                                    <a href="{concat($PlayerProfilesLocalLink,'#Player_',@from)}" title="player profile: {@from}">
                                                        <xsl:value-of select="@from"/>
                                                    </a>
                                                    <xsl:text>: </xsl:text>
                                                </i>
                                                    <xsl:call-template name="hyperlink">
                                                        <xsl:with-param name="string" select="normalize-space(.)"/>
                                                    </xsl:call-template>
                                                <br/>
                                            </td>
                                        </tr>
                                    </xsl:for-each>
                                </table>
                            </dd>
                        </xsl:if>
                    </dl>
                </td>
            </tr>
        </table>
        <hr />
    </xsl:template>
    <!--
    <xsl:template match="Title">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="ID">
        <xsl:value-of select="."/>
    </xsl:template>
    <xsl:template match="WhoIsInvolved">
        <p>
            <b>Who is involved?</b>:
            <xsl:value-of select="."/>
        </p>
    </xsl:template>
    <xsl:template match="WhatIsIt">
        <p>
            <b>What is it?</b>:
            <xsl:value-of select="."/>
        </p>
    </xsl:template>
    <xsl:template match="WhatWillItTake">
        <p>
            <b>What will it take?</b>:
            <xsl:value-of select="."/>
        </p>
    </xsl:template>
    <xsl:template match="HowWillItWork">
        <p>
            <b>How will it work?</b>:
            <xsl:value-of select="."/>
        </p>
    </xsl:template>
    <xsl:template match="HowWillItChangeThings">
        <p>
            <b>How will it change things?</b>:
            <xsl:value-of select="."/>
        </p>
    </xsl:template>
    <xsl:template match="Author">
        <tr>
            <td>
                <xsl:value-of select="GameName"/>
            </td>
            <td>
                <xsl:value-of select="Location"/>
            </td>
        </tr>
    </xsl:template>
    <xsl:template match="CommentList">
        <p>
            <b>Comments</b>
            <br/>
            <xsl:for-each select="child::Comment">
                <xsl:variable name="cntnts" select="."/>
                <i>From: </i>
                <b>
                    <xsl:value-of select="@from"/>
                </b>
                <br/>
                <i>When: </i>
                <xsl:value-of select="@postTime"/>
                <br/>
                <xsl:value-of select="$cntnts"/>
                <br/>
                <br/>
            </xsl:for-each>
        </p>
    </xsl:template>
    <xsl:template match="ChatLog">
        <p>
            <b>Chat Log</b>
            <br/>
            <xsl:for-each select="child::Message">
                <xsl:variable name="cntnts" select="."/>
                <i>From: </i>
                <b>
                    <xsl:value-of select="@from"/>
                </b>
                <br/>
                <i>When: </i>
                <xsl:value-of select="@postTime"/>
                <br/>
                <xsl:value-of select="$cntnts"/>
                <br/>
                <br/>
            </xsl:for-each>
        </p>
    </xsl:template>
    <xsl:template match="ImageList">
        <p>
            <b>Images</b>
            <br/>
            <xsl:for-each select="child::Image">
                <xsl:variable name="urlcntnts" select="URL"/>
                <xsl:element name="img">
                    <xsl:attribute name="src">
                        <xsl:value-of select="URL"/>
                    </xsl:attribute>
                    <xsl:attribute name="width">
                        <xsl:value-of select="@width"/>
                    </xsl:attribute>
                    <xsl:attribute name="height">
                        <xsl:value-of select="@height"/>
                    </xsl:attribute>
					xxx img src="{$url}" width="{$width}" height="{$height}"/> xxx
                </xsl:element>
                <br/>
                <a href="{$urlcntnts}">
                    <xsl:value-of select="$urlcntnts"/>
                </a>
                <br/>
                <xsl:call-template name="hyperlink">
                    <xsl:with-param name="string" select="Caption/text()"/>
                </xsl:call-template>
                <br/>
                <br/>
            </xsl:for-each>
        </p>
    </xsl:template>
    <xsl:template match="VideoList">
        <p>
            <b>Videos</b>
        </p>
        <xsl:for-each select="child::Video">
            <xsl:variable name="origUrl" select="URL"/>
			xxx build a variable to hold the munged url xxx
            <xsl:variable name="youTubeEmbedUrl">
                <xsl:analyze-string select="$origUrl" regex="(https://www.youtube.com/)watch\?v=(.*)">
                    <xsl:matching-substring>
						<xsl:value-of select="regex-group(1)"/>v/<xsl:value-of select="regex-group(2)"/>
						<xsl:text disable-output-escaping="yes">?version=3&amp;feature-player_detailpage</xsl:text></xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <xsl:value-of select="$origUrl"/>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:variable>
			xxx use the variable contents xxx
            <object style="height: 390px; width: 640px">
                <param name="movie" value="{$youTubeEmbedUrl}"/>
                <param name="allowFullScreen" value="true"/>
                <param name="allowScriptAccess" value="always"/>
                <embed src="{$youTubeEmbedUrl}" type="application/x-shockwave-flash" allowfullscreen="true" allowScriptAccess="always" width="640" height="360"/>
            </object>
            <br/>
            <a href="{$origUrl}">c
            </a>
            <br/>
            <xsl:call-template name="hyperlink">
                <xsl:with-param name="string" select="Caption/text()"/>
            </xsl:call-template>
            <br/>
            <br/>
        </xsl:for-each>
    </xsl:template>
-->

    <xsl:template match="/">
        <html>
            <head>
            <!-- TODO
                <meta name="identifier" content="http:// TODO /ActionPlanList.html"/>
            -->
                <link rel="shortcut icon" href="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.ico" title="MMOWGLI game"/>
                <meta http-equiv="refresh" content="600"/> <!-- 600 seconds = 10 minutes -->
                <meta name="author"      content="Don Brutzman and Mike Bailey"/>
                <meta name="description" content="Action plan outputs from MMOWGLI game"/>
                <meta name="created"     content="{current-date()}"/>
                <meta name="exported"    content="{$exportDateTime}"/>
                <meta name="filename"    content="ActionPlanList_{$gameTitle}.html"/>
                <meta name="identifier"  content="https://mmowgli.nps.edu/{$gameAcronym}/IdeaCardChains_{$gameTitle}.html"/>
                <meta name="reference"   content="MMOWGLI Game Engine, https://portal.mmowgli.nps.edu"/>
                <meta name="generator"   content="Eclipse, https://www.eclipse.org"/>
                <meta name="generator"   content="Altova XML-Spy, https://www.altova.com"/>
                <meta name="generator"   content="Netbeans, https://www.netbeans.org"/>
                <meta name="generator"   content="X3D-Edit, https://savage.nps.edu/X3D-Edit"/>

                <xsl:element name="title">
                    <xsl:text>Action Plans Report, </xsl:text>
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
                <a name="index"> </a>
                <xsl:choose>
                    <xsl:when test="($gameSecurity='FOUO')">
                        <p align="center">
                            <a href="https://portal.mmowgli.nps.edu/fouo" target="_blank" title="UNCLASSIFIED / FOR OFFICIAL USE ONLY (FOUO)">
                                <img src="https://web.mmowgli.nps.edu/mmowMedia/images/fouo250w36h.png" width="250" height="36" border="0"/>
                            </a>
                        </p>
                    </xsl:when>
                </xsl:choose>

                <xsl:apply-templates/>

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

    <xsl:template match="ActionPlanList">

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
                    <xsl:text>https://portal.mmowgli.nps.edu/game-wiki</xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!-- remove any line-break elements -->
        <xsl:variable name="gameSummary">
            <xsl:choose>
                <xsl:when test="contains(//GameSummary,'&lt;br /&gt;')">
                    <xsl:value-of disable-output-escaping="yes" select="substring-before(//GameSummary,'&lt;br /&gt;')"/><xsl:value-of disable-output-escaping="yes" select="substring-after(//GameSummary,'&lt;br /&gt;')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of  select="//GameSummary"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!-- debug
        <xsl:comment> <xsl:value-of select="$gameLabel"/><xsl:text disable-output-escaping="yes">  '&lt;br /&gt;'</xsl:text></xsl:comment>
        -->

        <xsl:if test="(string-length($displaySingleActionPlanNumber) = 0)">

            <!-- page header -->
            <table align="center" class="banner">
                <tr>
                    <td>
                        <p>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </p>
                        <h1 align="center">
                            <xsl:text> Action Plans Report </xsl:text>
                        </h1>
                        <h2 align="center">
                            <xsl:value-of disable-output-escaping="yes" select="$gameLabel"/>
                        </h2>
                        <!--
                        <p align="center" class="font-size:smaller;">
                            <xsl:if test="string-length($IdeaCardChainLocalLink) > 0">
                                    <xsl:text> Corresponding </xsl:text>
                                    <a href="{$IdeaCardChainLocalLink}"><xsl:text>Idea Card Chains</xsl:text></a>
                                    <xsl:text>, </xsl:text>
                                    <a href="{$PlayerProfilesLocalLink}" title="Player Profiles report"><xsl:text>Player Profiles</xsl:text></a>
                                    <xsl:text> and </xsl:text>
                                    <a href="index.html" title="Reports Index for this game"><xsl:text>Reports Index</xsl:text></a>
                                    <xsl:text> for this game. </xsl:text>
                                    <br />
                            </xsl:if>
                            <xsl:text>Also available: </xsl:text>
                            <a href="https://portal.mmowgli.nps.edu/reports" target="_blank">all published MMOWGLI Game Reports</a>
                        </p>
                        -->
                    </td>
                    <td>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                    </td>
                    <td>
                        <a href="{$portalPage}" title="Game documentation for {$gameLabel}" target="_blank">
                            <!-- 1158 x 332 -->
                            <img align="center" src="https://web.mmowgli.nps.edu/piracy/MmowgliLogo.png" width="386" height="111" border="0"/>
                        </a>
                    </td>
                    <td>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                    </td>
                    <td>
                        <xsl:text> Corresponding report products: </xsl:text>
                        <ul>
                            <li>
                                <a href="index.html" title="Reports Index for this game"><xsl:text>Reports Index</xsl:text></a>
                            </li>
                            <xsl:if test="string-length($IdeaCardChainLocalLink) > 0">
                                <li>
                                    <a href="{$IdeaCardChainLocalLink}"><xsl:text>Idea Card Chains</xsl:text></a>
                                    <xsl:text> and </xsl:text>
                                    <!-- Sunburst Visualizer -->
                                    <a href="cardSunburstVisualizer.html" title="Idea Card Sunburst Visualizer">
                                        <xsl:text disable-output-escaping="yes">Sunburst&amp;nbsp;Visualizer</xsl:text>
                                    </a>
                                </li>
                            </xsl:if>
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
            <!-- Banner complete -->

            <!-- TODO
                 - online urls for each game and for corresponding portal documentation
                 - image
            -->
            <!-- Now provide Table of Contents (TOC) -->
            <table align="center" class="contents">
                <tr>
                    <th title="Action Plan ID number">
                        Plan
                    </th>
                    <th>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                    </th>
                    <th align="left" title="Action Plan summary">
                        Title
                    </th>
                    <th align="center" title="&quot;Thumbs up&quot; average score from 1 to 3">
                        Rating
                    </th>
                    <th align="right" title="Ranking among plans in the same round">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        <xsl:text>Rank</xsl:text>
                    </th>
                    <th>
                        <!-- column for icons linkiing to game -->
                    </th>
                </tr>
                <!-- First row is TOC entry for Call To Action, if present -->
                <xsl:if test="(//CallToAction/*)">
                    <tr>
                        <td align="right">
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                        <td>
                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                        </td>
                        <td>
                            <a href="#CallToAction" title="Motivation and purpose for this game">Call To Action</a>
                            <xsl:choose>
                                <xsl:when test="(count(//CallToAction/VideoYouTubeID) > 1) or (count(//CallToAction/VideoAlternateUrl) > 1)">
                                    <xsl:text> videos provide player motivation and describe </xsl:text>
                                </xsl:when>
                                <xsl:when test="(count(//CallToAction/VideoYouTubeID) > 0) or (count(//CallToAction/VideoAlternateUrl) > 0)">
                                    <xsl:text> video provides player motivation and describes </xsl:text>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:text> describes </xsl:text>
                                </xsl:otherwise>
                            </xsl:choose>
                            <xsl:text> the purpose of the </xsl:text>
                            <xsl:value-of select="$gameLabel"/>
                            <xsl:text> game.</xsl:text>
                        </td>
                    </tr>
                </xsl:if>

                <!-- index list for all plans, grouped by round and sorted by ID number -->
                <xsl:for-each select="//ActionPlan">
                    <xsl:sort select="number(@moveNumber)" />
                    <xsl:sort select="number(ID)" />
                    
                    <xsl:variable name="ActionPlanLabel">
                        <!-- n.b. defined twice in this stylesheet, ensure consistent or links will break -->
                        <xsl:text>ActionPlan</xsl:text>
                        <xsl:value-of select="ID"/>
                    </xsl:variable>
                    <xsl:comment>found action plan</xsl:comment>
                    <xsl:variable name="roundNumber" select="@moveNumber"/>
                    
                    <xsl:if test="($numberOfRounds != '1') and (count(preceding-sibling::ActionPlan[(@moveNumber = $roundNumber) and not(@hidden='true')]) = 0)">
                        <tr>
                            <td colspan="6">
                                <hr title="Round {@moveNumber}"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                            </td>
                            <td colspan="4">
                                <b>
                                    <a href="#Round{@moveNumber}"> 
                                        <xsl:text>Round </xsl:text>
                                        <xsl:value-of select="@moveNumber"/>
                                    </a>
                                </b>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                <xsl:text> (</xsl:text>
                                <xsl:value-of select="count(//ActionPlan[(@moveNumber = ($roundNumber)) and not(@hidden='true')])"/>
                                <xsl:text> plans)</xsl:text>
                            </td>
                        </tr>
                    </xsl:if>
                
                    <xsl:choose>
                        <!-- first list hidden plan, if allowed -->
                        <xsl:when test="((string-length(@hidden) > 0) and not(@hidden='false')) and ($displayHiddenPlans='true')">
                            <!-- hidden plan title -->
                            <tr>
                                <td align="right" valign="top">

                                    <!-- bookmark for hidden plan
                                    <xsl:element name="a">
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="$ActionPlanLabel"/>
                                        </xsl:attribute>
                                        ...
                                    </xsl:element>
                                    -->

                                        <xsl:if test="@superInteresting='true'">
                                            <b title="Super Interesting">
                                                <xsl:text>*</xsl:text>
                                            </b>
                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        </xsl:if>
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <!-- bookmark to plan -->
                                                <xsl:text>#</xsl:text>
                                                <xsl:value-of select="$ActionPlanLabel"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="Title"/>
                                            </xsl:attribute>
                                            <!-- strike or strikethrough style.  put grey color on text only so that tooltip title remains readable. -->
                                            <del title="[hidden: Action Plan {ID}]">
                                                <div style="color:grey">
                                                    <xsl:value-of select="ID"/>
                                                </div>
                                            </del>
                                        </xsl:element>
                                </td>
                                <td>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                </td>
                                <td valign="top">
                                        <xsl:element name="a">
                                            <xsl:attribute name="href">
                                                <!-- bookmark to plan -->
                                                <xsl:text>#</xsl:text>
                                                <xsl:value-of select="$ActionPlanLabel"/>
                                            </xsl:attribute>
                                            <xsl:attribute name="title">
                                                <xsl:value-of select="Title"/>
                                            </xsl:attribute>
                                    <!-- strike or strikethrough style.  put grey color on text only so that tooltip title remains readable. -->
                                    <del title="[hidden: Action Plan {ID}] {Title}">
                                        <div style="color:grey">
                                            <xsl:call-template name="hyperlink">
                                                <xsl:with-param name="string" select="normalize-space(Title/.)"/>
                                            </xsl:call-template>
                                        </div>
                                    </del>
                                        </xsl:element>
                                </td>
                                <td valign="top">
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                </td>
                                <td valign="top">
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                </td>
                            </tr>
                        </xsl:when>
                        <!-- second ignore hidden plan, if display not allowed -->
                        <xsl:when test="((string-length(@hidden) > 0) and not(@hidden='false')) and not($displayHiddenPlans='true')">
                            <!-- NOP -->
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- not-hidden plan title -->
                            <tr valign="top">
                                <td align="right">
                                    <xsl:if test="@superInteresting='true'">
                                        <!-- bold, anchor title -->
                                        <xsl:text disable-output-escaping="yes">&lt;a title="marked Super Interesting by a Game Master"&gt;&lt;b&gt;*</xsl:text>
                                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                    </xsl:if>
                                    <xsl:element name="a">
                                        <xsl:attribute name="href">
                                            <!-- bookmark to plan -->
                                            <xsl:text>#</xsl:text>
                                            <xsl:value-of select="$ActionPlanLabel"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:text>Action Plan </xsl:text>
                                            <xsl:value-of select="ID"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="ID"/>
                                    </xsl:element>
                                    <xsl:if test="(@superInteresting='true')">
                                            <!-- unbold, unanchor title -->
                                            <xsl:text disable-output-escaping="yes">&lt;/b&gt;&lt;/a&gt;</xsl:text>
                                    </xsl:if>
                                </td>
                                <td>
                                    <xsl:text disable-output-escaping="yes">&amp;nbsp;&amp;nbsp;&amp;nbsp;</xsl:text>
                                </td>
                                <td>
                                    <xsl:element name="a">
                                        <xsl:attribute name="href">
                                            <!-- bookmark to plan -->
                                            <xsl:text>#</xsl:text>
                                            <xsl:value-of select="$ActionPlanLabel"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="Title"/>
                                        </xsl:attribute>
                                        <xsl:call-template name="hyperlink">
                                            <xsl:with-param name="string" select="normalize-space(Title/.)"/>
                                        </xsl:call-template>
                                    </xsl:element>
                                </td>
                                <xsl:variable name="numberPlansInRound" select="count(//ActionPlan[@moveNumber=$roundNumber])"/>
                                <xsl:variable name="pointScore">
                                    <xsl:if test="starts-with(format-number(@thumbs,'#.#'),'.')">
                                        <xsl:text>0</xsl:text>
                                    </xsl:if>
                                    <xsl:value-of select="format-number(@thumbs,'#.#')"/>
                                    <xsl:choose>
                                        <xsl:when test="not(@thumbs) or (string-length(format-number(@thumbs,'#.#')) = 0)">
                                            <xsl:text>0.0</xsl:text>
                                        </xsl:when>
                                        <xsl:when test="not(contains(format-number(@thumbs,'#.#'),'.'))">
                                            <xsl:text>.0</xsl:text>
                                        </xsl:when>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="rankingTooltip">
                                    <xsl:text> ranking </xsl:text>
                                    <xsl:value-of select="@roundRanking"/>
                                    <xsl:text> out of </xsl:text>
                                    <xsl:value-of select="$numberPlansInRound"/>
                                    <xsl:text> plans </xsl:text>
                                    <xsl:if test="($numberOfRounds != '1')">
                                        <xsl:text>in Round </xsl:text>
                                        <xsl:value-of select="@moveNumber"/>
                                    </xsl:if>
                                </xsl:variable>
                                <td align="right" title="&quot;Thumbs up&quot; average score {$pointScore} from 1 to 3, {$rankingTooltip}">
                                    <xsl:if test="@superInteresting='true'">
                                        <b title="Super Interesting">
                                            <xsl:text>*</xsl:text>
                                            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                                        </b>
                                    </xsl:if>
                                    <xsl:value-of select="$pointScore"/>
                                </td>
                                <td align="right" title="&quot;Thumbs up&quot; average score {$pointScore} from 1 to 3, {$rankingTooltip}">
                                    <xsl:value-of select="@roundRanking"/>
                                </td>
                                <td align="right">
                                    <!-- link to game -->
                                    <a href="https://mmowgli.nps.edu/{$gameAcronym}#!92_{ID}" target="{$gameAcronym}Game" title="play the game! go online to Action Plan {ID}">
                                        <img src="https://portal.mmowgli.nps.edu/mmowgli-theme/images/favicon.png" width="16px" align="right" border="0"/>
                                    </a>
                                </td>
                            </tr>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>

                <xsl:if test="($numberOfRounds != '1')">
                    <tr>
                        <td colspan="6">
                            <hr />
                        </td>
                    </tr>
                </xsl:if>
                <tr>
                    <td align="right">
                        <xsl:text>plus</xsl:text>
                    </td>
                    <td>
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                    <td>
                        <a href="#License" title="License, Terms and Conditions for this data content"><xsl:text>License, Terms, Conditions</xsl:text></a>
                        and
                        <a href="#Contact" title="Contact links for further information"><xsl:text>Contact</xsl:text></a>
                    </td>
                    <td width="3">
                        <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
                    </td>
                </tr>
            </table>
            <!-- Table of Contents complete -->

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
                    <tr>
                        <td align="left">
                            <h2 title="Motivation and purpose for this game">
                                <a name="CallToAction">
                                    <xsl:text> Call to Action: Player Motivation and Game Purpose</xsl:text>
                                </a>
                            </h2>
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

                            <object height="315" width="560">
                                <param name="movie" value="https://www.youtube.com/v/{normalize-space($videoYouTubeID)}?version=3&amp;hl=en_US&amp;rel=0" />
                                <param name="allowFullScreen" value="true" />
                                <param name="allowscriptaccess" value="always" />
                                <embed allowfullscreen="true" allowscriptaccess="always" height="315" src="https://www.youtube.com/v/{normalize-space($videoYouTubeID)}?version=3&amp;hl=en_US&amp;rel=0" type="application/x-shockwave-flash" width="560"></embed>
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
                                <h2>
                                    <xsl:value-of select="$callToActionBriefingSummary" disable-output-escaping="yes"/>
                                </h2>
                            </xsl:if>

                            <xsl:if test="string-length(normalize-space($callToActionBriefingText)) > 0">
                                <xsl:value-of select="$callToActionBriefingText" disable-output-escaping="yes"/>
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
                                            <a href="https://portal.mmowgli.nps.edu/game-wiki">MMOWGLI Portal</a>
                                    </xsl:otherwise>
                                </xsl:choose>
                                contains further game information.
                            </p>
                        </td>
                    </tr>
                </table>
                
            </xsl:for-each>
            <!-- Call To Action complete -->

            <!-- Now process plans -->
            <br />
            <hr />
        </xsl:if> <!-- end if showing all plans -->

        <xsl:choose>
            <!-- show single plan -->
            <xsl:when test="(string-length($displaySingleActionPlanNumber) > 0)">
                <xsl:apply-templates select="ActionPlan[number($displaySingleActionPlanNumber)]"/>
            </xsl:when>
            <!-- show all plans, grouped by round and sorted by ID number -->
            <xsl:otherwise>
                <xsl:apply-templates select="ActionPlan[not(@hidden='true') or ($displayHiddenPlans='true')]">
                    <xsl:sort select="number(@moveNumber)" />
                    <xsl:sort select="number(ID)" />
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>

        <xsl:if test="(string-length($displaySingleActionPlanNumber) = 0)">

            <!-- showing all plans -->

            <!-- =================================================== License =================================================== -->

            <table border="0" width="100%" cellpadding="0">
                <tr>
                    <td align="left">
                        <h2><a name="License">License, Terms and Conditions </a></h2>
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
                <a href="mailto:mmowgli-trouble%20at%20nps.edu?subject=Action%20Plan%20Report%20feedback:%20{$gameLabel}"><i><xsl:text>mmowgli-trouble at nps.edu</xsl:text></i></a>.
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

        </xsl:if> <!-- endif all plans -->

    </xsl:template>

    <xsl:template name="hyperlink">
        <!-- Search and replace urls in text:  adapted (with thanks) from
            http://www.dpawson.co.uk/xsl/rev2/regex2.html#d15961e67 by Jeni Tennison using url regex (http://[^ ]+) -->
        <!-- Justin Saunders http://regexlib.com/REDetails.aspx?regexp_id=37 url regex ((mailto:|(news|(ht|f)tp(s?))://){1}\S+) -->
        <xsl:param name="string" select="string(.)"/>
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
                        <a href="{concat($IdeaCardChainLocalLink,'#IdeaCard',regex-group(6))}">
                            <xsl:value-of select="." disable-output-escaping="yes"/>
                        </a>
                        <!-- <xsl:text> </xsl:text> -->
                    </xsl:matching-substring>
                    <xsl:non-matching-substring>
                        <!-- Third: for non-url remainder(s), now find and link 'Action Plan 456' references -->
                        <xsl:analyze-string select="concat(' ',normalize-space(.),' ')" regex="(([Gg][Aa][Mm][Ee]\s*(\d|\.)+\s*)?([Aa][Cc][Tt][Ii][Oo][Nn]\s*#?\s*)?[Pp][Ll][Aa][Nn]\s*#?\s*(\d+))">
                            <xsl:matching-substring>
                                <xsl:variable name="ActionPlanLink">
                                    <xsl:choose>
                                        <xsl:when test="contains(regex-group(2),'2011.1')">
                                            <xsl:text>ActionPlanListPiracy2011.1.html</xsl:text>
                                        </xsl:when>
                                        <xsl:when test="contains(regex-group(2),'2011.2')">
                                            <xsl:text>ActionPlanListPiracy2011.2.html</xsl:text>
                                        </xsl:when>
                                        <xsl:when test="contains(regex-group(2),'2011.3')">
                                            <xsl:text>ActionPlanListPiracy2011.3.html</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <!-- same file -->
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <!-- bookmark #ActionPlan456 - see ActionPlanLabel above for consistency -->
                                <a href="{concat($ActionPlanLink,'#ActionPlan',regex-group(5))}">
                                    <xsl:value-of select="." disable-output-escaping="yes"/>
                                </a>
                            <!-- <xsl:text> </xsl:text> -->
                            </xsl:matching-substring>
                            <xsl:non-matching-substring>
                                <!-- avoid returning excess whitespace -->
                                <xsl:if test="string-length(normalize-space(.)) > 0">
                                    <xsl:value-of select="." disable-output-escaping="yes"/>
                                </xsl:if>
                            </xsl:non-matching-substring>
                        </xsl:analyze-string>
                    </xsl:non-matching-substring>
                </xsl:analyze-string>
            </xsl:non-matching-substring>
        </xsl:analyze-string>
    </xsl:template>

</xsl:stylesheet>
