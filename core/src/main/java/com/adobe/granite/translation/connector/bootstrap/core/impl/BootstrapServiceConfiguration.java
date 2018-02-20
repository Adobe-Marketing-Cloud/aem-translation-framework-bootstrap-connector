package com.adobe.granite.translation.connector.bootstrap.core.impl;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.Option;

@ObjectClassDefinition(name = "Bootstrap Translation Service", description = "Bootstrap Translation Service Configuration")
public @interface BootstrapServiceConfiguration {
	
	@AttributeDefinition(name = "Bootstrap Translation Factory Name", description = "The Unique ID associated with this Translation Factory Connector")
	String getTranslationFactory() default "Bootstrap Connector";
	
	@AttributeDefinition(name = "Enable Preview", description="Preview Enabled for Translation Objects")
	boolean isPreviewEnabled() default false;
	
	@AttributeDefinition(name = "Disable Psuedo L10n", description = "Disable Pseudo localization for Machine translations and use a simple Language prefix instead")
	boolean isPseudoLocalizationDisabled() default false;

	@AttributeDefinition(name = "Export Format", description = "Number values", options = { @Option(label = "XML", value = BootstrapConstants.EXPORT_FORMAT_XML),
			@Option(label = "XLIFF 1.2", value = BootstrapConstants.EXPORT_FORMAT_XLIFF_1_2),
			@Option(label = "XLIFF 2.0", value = BootstrapConstants.EXPORT_FORMAT_XLIFF_2_0)})
	String getExportFormat() default BootstrapConstants.EXPORT_FORMAT_XML;
	
}