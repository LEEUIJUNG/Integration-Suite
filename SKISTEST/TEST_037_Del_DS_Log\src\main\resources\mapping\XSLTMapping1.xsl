<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output method="xml" indent="yes"/>

  <!-- Root template -->
  <xsl:template match="/d">
    <d>
      <xsl:apply-templates select="results[position() &lt;= 100]"/>
    </d>
  </xsl:template>

  <!-- Copy only the first 3 <results> elements -->
  <xsl:template match="results">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Copy all attributes and child nodes -->
  <xsl:template match="@* | node()">
    <xsl:copy>
      <xsl:apply-templates select="@* | node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>