<?xml version="1.0" encoding="utf-8"?>
<!--
The MIT License

Copyright 2017 mkanis.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
-->

<xsl:stylesheet version="1.0"
      xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <xsl:output method="xml" indent="yes"/>
  <xsl:template match="/">
    <fo:root>
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4-portrait"
              page-height="29.7cm" page-width="21.0cm" margin="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      <fo:page-sequence master-reference="A4-portrait">
        <fo:flow flow-name="xsl-region-body">
          <fo:block font-size="22pt" font-weight="bold" space-after="5mm"><xsl:value-of select="document/data/Title"/>
          </fo:block>

          <fo:block font-size="18pt" font-weight="bold" space-after="3mm">
              Data
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            <xsl:for-each select="document/data/*">
                <xsl:value-of select="name()" />
                <xsl:text>: </xsl:text>
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>

          <fo:block font-size="18pt" font-weight="bold" space-before="5mm" space-after="1mm">
              Metadata
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            id: <xsl:value-of select="document/_id"/>
            rev: <xsl:value-of select="document/_rev"/>
            <xsl:text>&#xA;</xsl:text>
            <xsl:for-each select="document/metadata/*">
                <xsl:value-of select="name()" />
                <xsl:text>: </xsl:text>
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>

          <fo:block font-size="18pt" font-weight="bold" space-before="5mm" space-after="3mm">
              Workflow
          </fo:block>

          <fo:block font-weight="bold">
              State
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            <xsl:for-each select="document/workflow/state/*">
                <xsl:value-of select="name()" />
                <xsl:text>: </xsl:text>
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>

          <fo:block font-weight="bold" space-before="3mm">
              Assignment
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            <xsl:for-each select="document/workflow/assignment/*">
                <xsl:value-of select="name()" />
                <xsl:text>: </xsl:text>
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>

          <fo:block font-weight="bold" space-before="3mm">
              Labels
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            <xsl:for-each select="document/workflow/labels/*">
                <xsl:value-of select="name()" />
                <xsl:text>: </xsl:text>
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>

          <fo:block font-weight="bold" space-before="3mm">
              Permissions
          </fo:block>
          <fo:block linefeed-treatment="preserve">
            <xsl:for-each select="document/workflow/permissions">
                <xsl:value-of select="current()"/> 
                <xsl:text>&#xA;</xsl:text>
            </xsl:for-each>
          </fo:block>
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
</xsl:stylesheet>