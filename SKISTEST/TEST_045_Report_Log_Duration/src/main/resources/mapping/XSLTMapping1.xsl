<xsl:stylesheet version="3.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs">
    
    <xsl:output method="xml" indent="yes" />
    
    <xsl:template match="/r">
        <r>
            <xsl:apply-templates select="MessageProcessingLog" />
        </r>
    </xsl:template>
    
    <xsl:template match="MessageProcessingLog">
        <l>
            <xsl:copy-of select="Status" />
            <xsl:copy-of select="ApplicationMessageId" />
            <xsl:copy-of select="IntegrationArtifact/Id" />
            <xsl:copy-of select="MessageGuid" />
            
            <!-- UTC → KST 변환 -->
            <xsl:variable name="start" select="xs:dateTime(LogStart) + xs:dayTimeDuration('PT9H')" />
            <xsl:variable name="end" select="xs:dateTime(LogEnd) + xs:dayTimeDuration('PT9H')" />
            <xsl:variable name="duration" select="$end - $start" />
            <xsl:variable name="totalSeconds"
                select="
                    seconds-from-duration($duration) +
                    60 * minutes-from-duration($duration) +
                    3600 * hours-from-duration($duration) +
                    86400 * days-from-duration($duration) +
                    2592000 * months-from-duration($duration) +
                    31536000 * years-from-duration($duration)
            " />
            <LogStart>
                <xsl:value-of select="$start" />
            </LogStart>
            <LogEnd>
                <xsl:value-of select="$end" />
            </LogEnd>
            
            <!-- Runtime 계산 (초 단위) -->
            <Runtime>
                <xsl:value-of select="$totalSeconds" />
            </Runtime>
        </l>
    </xsl:template>
</xsl:stylesheet>