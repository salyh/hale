/*
 * HUMBOLDT: A Framework for Data Harmonisation and Service Integration.
 * EU Integrated Project #030962                 01.10.2006 - 30.09.2010
 * 
 * For more information on the project, please refer to the this web site:
 * http://www.esdi-humboldt.eu
 * 
 * LICENSE: For information on the license under which this program is 
 * available, please refer to http:/www.esdi-humboldt.eu/license.html#core
 * (c) the HUMBOLDT Consortium, 2007 to 2011.
 */

package eu.esdihumboldt.cst.functions.core;

import java.util.Collections;
import java.util.Map;

import eu.esdihumboldt.hale.align.transformation.engine.TransformationEngine;
import eu.esdihumboldt.hale.align.transformation.function.TransformationFunction;
import eu.esdihumboldt.hale.align.transformation.function.impl.AbstractSingleTypeTransformation;
import eu.esdihumboldt.hale.align.transformation.report.TransformationLog;
import eu.esdihumboldt.hale.instance.model.MutableInstance;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;

/**
 * Simple 1:1 retype
 * @author Simon Templer
 */
public class Retype extends AbstractSingleTypeTransformation<TransformationEngine> {

	/**
	 * @see TransformationFunction#execute(String, TransformationEngine, Map, TransformationLog)
	 */
	@Override
	public void execute(String transformationIdentifier,
			TransformationEngine engine,
			Map<String, String> executionParameters, TransformationLog log) {
		// for each source instance create a target instance
		TypeDefinition targetType = getTarget().values().iterator().next().getDefinition().getDefinition();
		MutableInstance target = getInstanceFactory().createInstance(targetType);
		getPropertyTransformer().publish(Collections.singleton(getSourceType()), getSourceInstance(), target);
	}

}
