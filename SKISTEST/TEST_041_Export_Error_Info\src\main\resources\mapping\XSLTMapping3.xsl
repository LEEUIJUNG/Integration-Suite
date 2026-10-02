<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs">
    
    <xsl:param name="ErrorMessage"/>

    <xsl:output method="xml" indent="yes" omit-xml-declaration="yes"/>

    <xsl:template match="/r">
        <xsl:apply-templates select="l"/>
    </xsl:template>

    <xsl:template match="l">
        <l>
            <xsl:copy-of select="Id"/>
            <xsl:copy-of select="MessageGuid"/>
            <xsl:element name="callTime">
                <xsl:value-of select="replace(LogEnd, 'T', ' ')"/>
            </xsl:element>
            <xsl:copy-of select="Status"/>
            <xsl:copy-of select="ApplicationMessageId"/>
            <xsl:copy-of select="LogStart"/>
            <xsl:copy-of select="LogEnd"/>
            <xsl:copy-of select="Runtime"/>
            <xsl:element name="ErrorMessage">
                <!--<xsl:value-of select="$ErrorMessage"/>-->
                <xsl:value-of select="translate($ErrorMessage, '&#10;&#13;', '  ')"/>
            </xsl:element>
        </l>
    </xsl:template>
</xsl:stylesheet>