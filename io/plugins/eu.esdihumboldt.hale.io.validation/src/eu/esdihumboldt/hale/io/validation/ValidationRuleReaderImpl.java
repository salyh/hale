/*
 * Copyright (c) 2016 wetransform GmbH
 * 
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contributors:
 *     wetransform GmbH <http://www.wetransform.to>
 */

package eu.esdihumboldt.hale.io.validation;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.apache.commons.io.IOUtils;

import eu.esdihumboldt.hale.common.core.io.IOProviderConfigurationException;
import eu.esdihumboldt.hale.common.core.io.ProgressIndicator;
import eu.esdihumboldt.hale.common.core.io.impl.AbstractImportProvider;
import eu.esdihumboldt.hale.common.core.io.report.IOReport;
import eu.esdihumboldt.hale.common.core.io.report.IOReporter;
import eu.esdihumboldt.hale.common.core.io.supplier.DefaultInputSupplier;

/**
 * Validation rule reader
 * 
 * @author Florian Esser
 */
public class ValidationRuleReaderImpl extends AbstractImportProvider
		implements ValidationRuleReader {

	/**
	 * The provider ID.
	 */
	public static final String PROVIDER_ID = "eu.esdihumboldt.hale.io.validation.reader";

	private ValidationRule rule;

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.IOProvider#isCancelable()
	 */
	@Override
	public boolean isCancelable() {
		return false;
	}

	/**
	 * @see eu.esdihumboldt.hale.io.schematron.ValidationRuleReader#getRule()
	 */
	@Override
	public ValidationRule getRule() {
		return rule;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#execute(eu.esdihumboldt.hale.common.core.io.ProgressIndicator,
	 *      eu.esdihumboldt.hale.common.core.io.report.IOReporter)
	 */
	@Override
	protected IOReport execute(ProgressIndicator progress, IOReporter reporter)
			throws IOProviderConfigurationException, IOException {

		progress.begin("Loading validation rule.", ProgressIndicator.UNKNOWN);

		final URI sourceLocation = getSource().getLocation();
		if (sourceLocation == null) {
			throw new IOProviderConfigurationException(
					"No source location provided when trying to read validation rule.");
		}
		final DefaultInputSupplier validationRuleInputSupplier = new DefaultInputSupplier(
				sourceLocation);
		final InputStream validaionRuleInput = validationRuleInputSupplier.getInput();
		if (validaionRuleInput == null) {
			throw new IOProviderConfigurationException("No validation rule input.");
		}
		try {
			rule = new ValidationRule(IOUtils.toString(validaionRuleInput, StandardCharsets.UTF_8),
					sourceLocation);
			reporter.setSuccess(true);
		} catch (Exception e) {
			throw new IOProviderConfigurationException(
					MessageFormat.format("Could not read validation rule from '{0}': {1}",
							sourceLocation.toString(), e.getMessage()),
					e);
		} finally {
			IOUtils.closeQuietly(validaionRuleInput);
		}

		progress.setCurrentTask("Validation rule loaded.");
		return reporter;
	}

	/**
	 * @see eu.esdihumboldt.hale.common.core.io.impl.AbstractIOProvider#getDefaultTypeName()
	 */
	@Override
	protected String getDefaultTypeName() {
		return "Validation rule";
	}
}
