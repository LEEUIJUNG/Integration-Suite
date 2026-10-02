<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:key name="groupId" match="MessageProcessingLog/Id" use="."/>
    <xsl:template match="root">
        <root>
            <xsl:for-each select="MessageProcessingLogs/MessageProcessingLog/Id[generate-id() = generate-id(key('groupId',.)[1])]">
                <MessageProcessingLog>
                    <xsl:copy-of select="."/>
                    <count>
                        <xsl:value-of select="count(key('groupId',.))"/>
                    </count>
                </MessageProcessingLog>
            </xsl:for-each>
        </root>
    </xsl:template>
</xsl:stylesheet>