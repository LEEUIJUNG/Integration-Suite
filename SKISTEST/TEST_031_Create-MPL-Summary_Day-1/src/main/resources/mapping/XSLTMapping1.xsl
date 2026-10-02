<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:template match="/root">
    <root>
      <xsl:for-each-group select="MessageProcessingLogs/MessageProcessingLog" group-by="Id">
        <MessageProcessingLog>
          <Id><xsl:value-of select="current-grouping-key()"/></Id>

          <!-- 상태별 카운트 출력 -->
          <xsl:variable name="statuses" select="('FAILED','RETRY','COMPLETED','PROCESSING','ESCALATED','CANCELLED','DISCARDED','ABANDONED')"/>
          <xsl:for-each select="$statuses">
            <xsl:variable name="status" select="."/>
            <xsl:variable name="count" select="count(current-group()[Status = $status])"/>
            <xsl:element name="{$status}">
              <xsl:value-of select="$count"/>
            </xsl:element>
          </xsl:for-each>

          <!-- 총합 출력 -->
          <Total>
            <xsl:value-of select="
              sum(for $s in $statuses return count(current-group()[Status = $s]))
            "/>
          </Total>
        </MessageProcessingLog>
      </xsl:for-each-group>
    </root>
  </xsl:template>
</xsl:stylesheet>