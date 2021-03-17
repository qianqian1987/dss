<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:dss="http://dss.esig.europa.eu/validation/simple-report">
                
	<xsl:output method="html" encoding="utf-8" indent="yes" omit-xml-declaration="yes" />

    <xsl:template match="/dss:SimpleReport">
		<xsl:comment>Generated by DSS v.${project.version}</xsl:comment>
	    
		<xsl:apply-templates select="dss:ValidationPolicy"/>
		<xsl:apply-templates select="dss:Signature"/>
		<xsl:apply-templates select="dss:Timestamp"/>
	    
	    <xsl:call-template name="documentInformation"/>
    </xsl:template>

    <xsl:template match="dss:DocumentName"/>
    <xsl:template match="dss:SignatureFormat"/>
    <xsl:template match="dss:SignaturesCount"/>
    <xsl:template match="dss:ValidSignaturesCount"/>
    <xsl:template match="dss:ValidationTime"/>
    <xsl:template match="dss:ContainerType"/>

    <xsl:template match="dss:ValidationPolicy">
		<div>
    		<xsl:attribute name="class">card mb-3</xsl:attribute>
    		<div>
    			<xsl:attribute name="class">card-header bg-primary</xsl:attribute>
	    		<xsl:attribute name="data-target">#collapsePolicy</xsl:attribute>
		       	<xsl:attribute name="data-toggle">collapse</xsl:attribute>
    			Validation Policy : <xsl:value-of select="dss:PolicyName"/>
	        </div>
    		<div>
    			<xsl:attribute name="class">card-body collapse show</xsl:attribute>
	        	<xsl:attribute name="id">collapsePolicy</xsl:attribute>
	        	<xsl:value-of select="dss:PolicyDescription"/>
    		</div>
    	</div>
    </xsl:template>

    <xsl:template match="dss:Signature|dss:Timestamp">
		<xsl:param name="cardStyle" select="'primary'" />
        <xsl:variable name="indicationText" select="dss:Indication/text()"/>
        <xsl:variable name="idToken" select="@Id" />
        <xsl:variable name="nodeName" select="name()" />
        <xsl:variable name="indicationCssClass">
        	<xsl:choose>
				<xsl:when test="$indicationText='TOTAL_PASSED'">success</xsl:when>
				<xsl:when test="$indicationText='PASSED'">success</xsl:when>
				<xsl:when test="$indicationText='INDETERMINATE'">warning</xsl:when>
				<xsl:when test="$indicationText='FAILED'">danger</xsl:when>
				<xsl:when test="$indicationText='TOTAL_FAILED'">danger</xsl:when>
			</xsl:choose>
        </xsl:variable>
		<xsl:variable name="copyIdBtnColor">
			<xsl:choose>
				<xsl:when test="$cardStyle='primary'">light</xsl:when>
				<xsl:otherwise>dark</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
        
        <div>
    		<xsl:attribute name="class">card mb-3</xsl:attribute>
    		<div>
    			<xsl:attribute name="class">card-header bg-<xsl:value-of select="$cardStyle" /></xsl:attribute>
	    		<xsl:attribute name="data-target">#collapseSig<xsl:value-of select="$idToken" /></xsl:attribute>
		       	<xsl:attribute name="data-toggle">collapse</xsl:attribute>
		       	
		       	<xsl:if test="@CounterSignature = 'true'">
					<span>
			        	<xsl:attribute name="class">badge badge-info pull-right</xsl:attribute>
						Counter-signature
		        	</span>
				</xsl:if>

				<span>
					<xsl:if test="$nodeName = 'Signature'">
						Signature
					</xsl:if>
					<xsl:if test="$nodeName = 'Timestamp'">
						Timestamp
					</xsl:if>
					<xsl:value-of select="$idToken" />
				</span>
				<i>
					<xsl:attribute name="class">id-copy fa fa-clipboard btn btn-outline-light cursor-pointer text-<xsl:value-of select="$copyIdBtnColor"/> border-0 p-2 ml-1 mr-1</xsl:attribute>
					<xsl:attribute name="data-id"><xsl:value-of select="$idToken"/></xsl:attribute>
					<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
					<xsl:attribute name="data-placement">right</xsl:attribute>
					<xsl:attribute name="data-success-text">Id copied successfully!</xsl:attribute>
					<xsl:attribute name="title">Copy Id to clipboard</xsl:attribute>
				</i>
	        </div>
    		<div>
    			<xsl:attribute name="class">card-body collapse show</xsl:attribute>
				<xsl:attribute name="id">collapseSig<xsl:value-of select="$idToken" /></xsl:attribute>
				
				<xsl:if test="dss:Filename">
					<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			    		
						<xsl:if test="$nodeName = 'Signature'">
			            	<dt>
			            		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            	
			            		Signature filename:
			            	</dt>
						</xsl:if>
						<xsl:if test="$nodeName = 'Timestamp'">
			            	<dt>
			            		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            	
			            		Timestamp filename:
			            	</dt>
						</xsl:if>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            
							<xsl:value-of select="dss:Filename" />
			        	</dd>
			        </dl>
				</xsl:if>
				
				<xsl:if test="dss:SignatureLevel | dss:TimestampLevel">
					<dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			            	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            	
			            	Qualification:
			            </dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            
							<xsl:if test="dss:SignatureLevel">
								<xsl:value-of select="dss:SignatureLevel" />
							</xsl:if>
							<xsl:if test="dss:TimestampLevel">
								<xsl:value-of select="dss:TimestampLevel" />
							</xsl:if>
							<i>
				    			<xsl:attribute name="class">fa fa-info-circle text-info ml-2</xsl:attribute>
								<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
								<xsl:attribute name="data-placement">right</xsl:attribute>
								
								<xsl:if test="dss:SignatureLevel">
									<xsl:attribute name="title"><xsl:value-of select="dss:SignatureLevel/@description" /></xsl:attribute>
								</xsl:if>
								<xsl:if test="dss:TimestampLevel">
									<xsl:attribute name="title"><xsl:value-of select="dss:TimestampLevel/@description" /></xsl:attribute>
								</xsl:if>
				    		</i>					
			        	</dd>
			        </dl>
				</xsl:if>

				<xsl:apply-templates select="dss:QualificationDetails" />

				<xsl:if test="@SignatureFormat">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			            	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            	
			            	Signature format:
			            </dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            
			            	<xsl:value-of select="@SignatureFormat"/>
			            </dd>
			        </dl>
		        </xsl:if>
			
				<dl>
					<xsl:attribute name="class">row mb-0</xsl:attribute>
					<dt>
			        	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            Indication:
					</dt>
					<dd>
			           	<xsl:attribute name="class">col-sm-9 text-<xsl:value-of select="$indicationCssClass" /></xsl:attribute>
			
						<div>
			           		<xsl:attribute name="class">badge mr-2 badge-<xsl:value-of select="$indicationCssClass" /></xsl:attribute>

							<xsl:variable name="dssIndication" select="dss:Indication" />
							<xsl:variable name="semanticText" select="//dss:Semantic[contains(@Key,$dssIndication)]"/>
			           		
			           		<xsl:if test="string-length($semanticText) &gt; 0">
								<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
								<xsl:attribute name="data-placement">right</xsl:attribute>
								<xsl:attribute name="title"><xsl:value-of select="$semanticText" /></xsl:attribute>
			     			</xsl:if>
			           		
							<xsl:value-of select="$indicationText" />
						</div>
					
						<xsl:choose>
							<xsl:when test="$indicationText='TOTAL_PASSED'">
								<i>
									<xsl:attribute name="class">fa fa-check-circle align-middle</xsl:attribute>
								</i>
							</xsl:when>
							<xsl:when test="$indicationText='PASSED'">
								<i>
									<xsl:attribute name="class">fa fa-check-circle align-middle</xsl:attribute>
								</i>
							</xsl:when>
							<xsl:when test="$indicationText='INDETERMINATE'">
								<i>
									<xsl:attribute name="class">fa fa-exclamation-circle align-middle</xsl:attribute>
								</i>
							</xsl:when>
							<xsl:when test="$indicationText='FAILED'">
								<i>
									<xsl:attribute name="class">fa fa-times-circle align-middle</xsl:attribute>
								</i>
							</xsl:when>
							<xsl:when test="$indicationText='TOTAL_FAILED'">
								<i>
									<xsl:attribute name="class">fa fa-times-circle align-middle</xsl:attribute>
								</i>
							</xsl:when>
						</xsl:choose>
					</dd>
				</dl>   
		        
		        <xsl:apply-templates select="dss:SubIndication">
		            <xsl:with-param name="indicationClass" select="$indicationCssClass"/>
		        </xsl:apply-templates>

				<xsl:apply-templates select="dss:AdESValidationDetails" />

		        <dl>
	        		<xsl:attribute name="class">row mb-0</xsl:attribute>
		            <dt>
			        	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        	
			        	Certificate Chain:
			        </dt>
		            <xsl:choose>
			            <xsl:when test="dss:CertificateChain">
			        		<dd>
		            			<xsl:attribute name="class">col-sm-9</xsl:attribute>
		            			
		            			<ul>
		            				<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
		            			
						            <xsl:for-each select="dss:CertificateChain/dss:Certificate">
						            	<xsl:variable name="index" select="position()"/>
					            			
				        				<li>
				        					<i><xsl:attribute name="class">fa fa-link mr-2</xsl:attribute></i>
						        			<xsl:choose>
						        				<xsl:when test="$index = 1">
						        					<b><xsl:value-of select="dss:qualifiedName" /></b>
						        				</xsl:when>
						        				<xsl:otherwise>
													<xsl:value-of select="dss:qualifiedName" />				        				
						        				</xsl:otherwise>
						        			</xsl:choose>
					        			</li>
						        	</xsl:for-each>
					        	</ul>
		        			</dd>
			        	</xsl:when>
			        	<xsl:otherwise>
			        		<dd>/</dd>
			        	</xsl:otherwise>
		        	</xsl:choose>
	        	</dl>
		        
				<xsl:if test="dss:SigningTime">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
			        		On claimed time:
			        	</dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            	
			            	<xsl:value-of select="dss:SigningTime"/>
			            </dd>
			        </dl>
		        </xsl:if>
		        
				<xsl:if test="dss:ProductionTime">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
			        		Production time:
			        	</dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            	
			            	<xsl:value-of select="dss:ProductionTime"/>
			            </dd>
			        </dl>
		        </xsl:if>
		        
				<xsl:if test="dss:BestSignatureTime">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			            	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			            	
			            	Best signature time:
			            </dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            	
			            	<xsl:value-of select="dss:BestSignatureTime"/>
			            	<i>
				    			<xsl:attribute name="class">fa fa-info-circle text-info ml-2</xsl:attribute>
								<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
								<xsl:attribute name="data-placement">right</xsl:attribute>
								<xsl:attribute name="title">
									Lowest time at which there exists a proof of existence for the signature 
								</xsl:attribute>
				    		</i>		
			            </dd>
			        </dl>
		        </xsl:if>
		        
				<xsl:if test="$nodeName = 'Signature'">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
			        		Signature position:
			        	</dt>
			            <dd>
			            	<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            	
			            	<xsl:value-of select="count(preceding-sibling::dss:Signature) + 1"/> out of <xsl:value-of select="count(ancestor::*/dss:Signature)"/>
			            </dd>
			        </dl>
				</xsl:if>
		        
				<xsl:if test="dss:SignatureScope">
			        <xsl:for-each select="dss:SignatureScope">
				        <dl>
				    		<xsl:attribute name="class">row mb-0</xsl:attribute>
				            <dt>
			        			<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        			
			        			Signature scope:
			        		</dt>
				            <dd>
			            		<xsl:attribute name="class">col-sm-9</xsl:attribute>
			            	
				            	<xsl:value-of select="@name"/> (<xsl:value-of select="@scope"/>)<br />
				            	<xsl:value-of select="."/>
				            </dd>
				        </dl>
			        </xsl:for-each>
		        </xsl:if>

				<xsl:if test="dss:Timestamps">
					<div>
						<xsl:attribute name="class">card mt-3</xsl:attribute>
						<div>
							<xsl:attribute name="class">card-header bg-primary collapsed</xsl:attribute>
							<xsl:attribute name="data-target">#collapseSigDetails<xsl:value-of select="$idToken" /></xsl:attribute>
							<xsl:attribute name="data-toggle">collapse</xsl:attribute>
							<xsl:attribute name="aria-expanded">false</xsl:attribute>

							Timestamps <span class="badge badge-light"><xsl:value-of select="count(dss:Timestamps/dss:Timestamp)" /></span>
						</div>
						<div>
							<xsl:attribute name="class">card-body collapse pb-1</xsl:attribute>
							<xsl:attribute name="id">collapseSigDetails<xsl:value-of select="$idToken" /></xsl:attribute>
							<xsl:apply-templates select="dss:Timestamps" />
						</div>
					</div>
				</xsl:if>

    		</div>
    	</div>
    </xsl:template>

	<xsl:template match="dss:AdESValidationDetails|dss:QualificationDetails">
		<xsl:variable name="header">
			<xsl:choose>
				<xsl:when test="name() = 'AdESValidationDetails'">AdES Validation Details</xsl:when>
				<xsl:when test="name() = 'QualificationDetails'">Qualification Details</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<dl>
			<xsl:attribute name="class">row mb-0</xsl:attribute>
			<dt>
				<xsl:attribute name="class">col-sm-3</xsl:attribute>

				<xsl:value-of select="$header" /> :
			</dt>
			<dd>
				<xsl:attribute name="class">col-sm-9</xsl:attribute>
				<ul>
					<xsl:attribute name="class">list-unstyled mb-0</xsl:attribute>
					<xsl:apply-templates select="dss:Error" />
					<xsl:apply-templates select="dss:Warning" />
					<xsl:apply-templates select="dss:Info" />
				</ul>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template match="dss:Error|dss:Warning|dss:Info">
		<xsl:variable name="style">
			<xsl:choose>
				<xsl:when test="name() = 'Error'">danger</xsl:when>
				<xsl:when test="name() = 'Warning'">warning</xsl:when>
				<xsl:otherwise>auto</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<li>
			<xsl:attribute name="class">text-<xsl:value-of select="$style" /></xsl:attribute>
			<xsl:value-of select="." />
		</li>
	</xsl:template>

	<xsl:template match="dss:Timestamps">
		<div>
			<xsl:apply-templates select="dss:Timestamp">
				<xsl:with-param name="cardStyle" select="'light'"/>
			</xsl:apply-templates>
		</div>
	</xsl:template>

	<xsl:template match="dss:SubIndication">
		<xsl:param name="indicationClass" />
		
		<xsl:variable name="subIndicationText" select="." />
		<xsl:variable name="semanticText" select="//dss:Semantic[contains(@Key,$subIndicationText)]"/>
				
		<dl>
    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			<dt>
				<xsl:attribute name="class">col-sm-3</xsl:attribute>
				
				Sub indication:
			</dt>
			<dd>
				<xsl:attribute name="class">col-sm-9</xsl:attribute>
				<div>
					<xsl:attribute name="class">badge badge-<xsl:value-of select="$indicationClass" /></xsl:attribute>

					<xsl:if test="string-length($semanticText) &gt; 0">
						<xsl:attribute name="data-toggle">tooltip</xsl:attribute>
						<xsl:attribute name="data-placement">right</xsl:attribute>
						<xsl:attribute name="title"><xsl:value-of select="$semanticText" /></xsl:attribute>
	     			</xsl:if>
	     			
					<xsl:value-of select="$subIndicationText" />
				</div>
			</dd>
		</dl>
	</xsl:template>

    <xsl:template name="documentInformation">
		<div>
    		<xsl:attribute name="class">card</xsl:attribute>
    		<div>
    			<xsl:attribute name="class">card-header bg-primary</xsl:attribute>
	    		<xsl:attribute name="data-target">#collapseInfo</xsl:attribute>
		       	<xsl:attribute name="data-toggle">collapse</xsl:attribute>
    			Document Information
	        </div>
    		<div>
    			<xsl:attribute name="class">card-body collapse show</xsl:attribute>
	        	<xsl:attribute name="id">collapseInfo</xsl:attribute>
	        	
				<xsl:if test="dss:ContainerType">
			        <dl>
			    		<xsl:attribute name="class">row mb-0</xsl:attribute>
			            <dt>
			        		<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        		
			        		Container type:
			        	</dt>
			            <dd>
							<xsl:attribute name="class">col-sm-9</xsl:attribute>
							
							<xsl:value-of select="dss:ContainerType"/>
						</dd>
			        </dl>
		        </xsl:if>
	        	<dl>
		    		<xsl:attribute name="class">row mb-0</xsl:attribute>
		            <dt>
			        	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        	
			        	Signatures status:
			        </dt>
		            <dd>
		                <xsl:choose>
		                    <xsl:when test="dss:ValidSignaturesCount = dss:SignaturesCount">
		                        <xsl:attribute name="class">col-sm-9 text-success</xsl:attribute>
		                    </xsl:when>
		                    <xsl:otherwise>
		                        <xsl:attribute name="class">col-sm-9 text-warning</xsl:attribute>
		                    </xsl:otherwise>
		                </xsl:choose>
		                <xsl:value-of select="dss:ValidSignaturesCount"/> valid signatures, out of <xsl:value-of select="dss:SignaturesCount"/>
		            </dd>
		        </dl>
		        <dl>
		    		<xsl:attribute name="class">row mb-0</xsl:attribute>
		            <dt>
			        	<xsl:attribute name="class">col-sm-3</xsl:attribute>
			        	
			        	Document name:
			        </dt>
		            <dd>
						<xsl:attribute name="class">col-sm-9</xsl:attribute>
						
						<xsl:value-of select="dss:DocumentName"/>
					</dd>
		        </dl>
		        
    		</div>
    	</div>
    </xsl:template>
</xsl:stylesheet>
