/*
 * Copyright (c) 2015 Data Harmonisation Panel
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
 *     Data Harmonisation Panel <http://www.dhpanel.eu>
 */

package eu.esdihumboldt.cst.functions.geometric.interiorpoint;

import java.text.MessageFormat;
import java.util.Locale;

import eu.esdihumboldt.hale.common.align.model.Cell;
import eu.esdihumboldt.hale.common.align.model.CellUtil;
import eu.esdihumboldt.hale.common.align.model.Entity;
import eu.esdihumboldt.hale.common.align.model.impl.AbstractCellExplanation;

/**
 * Explanation for Interior Point cells.
 * 
 * @author Simon Templer
 */
public class InteriorPointExplanation extends AbstractCellExplanation {

	@Override
	protected String getExplanation(Cell cell, boolean html, Locale locale) {

		Entity source = CellUtil.getFirstEntity(cell.getSource());
		Entity target = CellUtil.getFirstEntity(cell.getTarget());

		if (target != null && source != null) {
			String message = "Determines a point that lies within the geometry or geometries contained in the {1} property and assigns the result to the {0} property.";

			return MessageFormat.format(message, formatEntity(target, html, true),
					formatEntity(source, html, true));
		}
		return null;
	}

}
