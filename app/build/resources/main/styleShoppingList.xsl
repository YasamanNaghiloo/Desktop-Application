<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">
    <xsl:output encoding="UTF-8" indent="yes" method="xml" standalone="no" omit-xml-declaration="no"/>
    <xsl:template match="shopping-list-data">
        <fo:root language="EN">
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-portrail" page-height="297mm" page-width="210mm" margin-top="5mm" margin-bottom="5mm" margin-left="5mm" margin-right="5mm">
                    <fo:region-body margin-top="25mm" margin-bottom="20mm"/>
                    <fo:region-before region-name="xsl-region-before" extent="50mm" display-align="before" precedence="true"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="A4-portrail">
                <fo:static-content flow-name="xsl-region-before">
                  <fo:block text-align="center" font-size="10pt" font-family="Lucida Bright, Times Roman" font-weight="light" color="white">-</fo:block>
                  <fo:block text-align="center" font-size="25pt" font-family="Lucida Bright, Times Roman" font-weight="light">Shopping List</fo:block>
                  <fo:block text-align="center" font-size="6pt" font-family="Lucida Bright, Times Roman" font-weight="light" color="white">-</fo:block>
                  <fo:block text-align="center" font-size="20pt" font-family="Lucida Bright, Times Roman" font-weight="light" font-style="italic"> Week 
                    <xsl:value-of select="week"/>
                  </fo:block>
                </fo:static-content>
                <fo:flow flow-name="xsl-region-body" border-collapse="collapse" reference-orientation="0">
                  <fo:table table-layout="fixed" width="100%" font-size="16pt" border-color="white" border-width="0.0mm" border-style="solid" text-align="center" display-align="center" space-after="5mm">
                        <fo:table-column column-width="proportional-column-width(50)"/>
                        <fo:table-column column-width="proportional-column-width(50)"/>
                        <fo:table-body font-size="95%">
                          <fo:table-row height="8mm">
                                <fo:table-cell>
                                    <fo:block text-align="center" font-size="18pt" font-family="Lucida Bright, Times Roman" font-weight="light" color = "white">-----</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                  <fo:block text-align="center" font-size="18pt" font-family="Lucida Bright, Times Roman" font-weight="light" color = "white">----</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                            <fo:table-row height="8mm">
                                <fo:table-cell border-bottom="1pt solid rgb(180,180,180)">
                                    <fo:block text-align="center" font-size="18pt" font-family="Lucida Bright, Times Roman" font-weight="light">&#160;&#160;&#160;Ingredient</fo:block>
                                </fo:table-cell>
                                <fo:table-cell border-bottom="1pt solid rgb(180,180,180)">
                                  <fo:block text-align="center" font-size="18pt" font-family="Lucida Bright, Times Roman" font-weight="light">Amount</fo:block>
                                </fo:table-cell>
                              </fo:table-row>
                          <xsl:for-each select="ingredient-data">
                              <fo:table-row>
                                  <fo:table-cell border-bottom="1pt solid rgb(180,180,180)">
                                    <fo:table table-layout="fixed" width="100%" font-size="16pt" border-color="white" border-width="0.0mm" border-style="solid" text-align="center" display-align="center" space-after="5mm">
                                      <fo:table-column column-width="proportional-column-width(6)"/>
                                      <fo:table-column column-width="proportional-column-width(94)"/>
                                      <fo:table-body font-size="95%">
                                        <fo:table-row height="8mm">
                                          <fo:table-cell >
                                            <fo:block text-align="center" font-size="16pt" font-family="Lucida Bright, Times Roman" font-weight="light">     
                                        <fo:inline border-style="solid" border-width="1pt" border-color= "gray">&#160;&#160;&#160;</fo:inline>
                                        </fo:block>
                                          </fo:table-cell>
                                          <fo:table-cell >
                                            <fo:block text-align="center" font-size="16pt" font-family="Lucida Bright, Times Roman" font-weight="light">     
                                            <xsl:value-of select="ingredient"/>
                                        </fo:block>
                                          </fo:table-cell>
                                        </fo:table-row>
                                      </fo:table-body>
                                      </fo:table>
                                  </fo:table-cell>
                                  <fo:table-cell border-bottom="1pt solid rgb(180,180,180)">
                                      <fo:block text-align="center" font-size="16pt" font-family="Lucida Bright, Times Roman" font-weight="light">
                                          <xsl:value-of select="amount"/>
                                      </fo:block>
                                  </fo:table-cell>
                              </fo:table-row>
                          </xsl:for-each>
                          </fo:table-body>
                  </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
