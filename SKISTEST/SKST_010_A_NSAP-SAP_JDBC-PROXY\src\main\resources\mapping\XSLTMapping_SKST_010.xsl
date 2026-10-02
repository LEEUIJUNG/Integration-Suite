<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="http://skshieldus.com/SKST_010" version="1.0">
	<xsl:template match="/">
		<ns0:SKST_010_OA1_MT>
			<xsl:for-each select="//row">
				<xsl:copy-of select='.'/>
			</xsl:for-each>
		</ns0:SKST_010_OA1_MT>
	</xsl:template>
</xsl:stylesheet>