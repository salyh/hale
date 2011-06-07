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

package eu.esdihumboldt.hale.ui.service.instance.internal.orient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.iterator.ORecordIteratorClass;
import com.orientechnologies.orient.core.record.impl.ODocument;

import eu.esdihumboldt.hale.instance.model.Instance;
import eu.esdihumboldt.hale.instance.model.InstanceCollection;
import eu.esdihumboldt.hale.instance.model.ResourceIterator;
import eu.esdihumboldt.hale.instance.model.impl.OInstance;
import eu.esdihumboldt.hale.instance.model.impl.ONameUtil;
import eu.esdihumboldt.hale.schema.model.TypeDefinition;
import eu.esdihumboldt.hale.schema.model.TypeIndex;

/**
 * Instance collection based on a {@link LocalOrientDB}
 * @author Simon Templer
 */
public class OrientInstanceCollection implements InstanceCollection {
	
	private class OrientInstanceIterator implements ResourceIterator<Instance> {

		private DatabaseReference<ODatabaseDocumentTx> ref;
		
		private Map<String, TypeDefinition> classTypes;
		
		private Queue<String> classQueue;
		
		private String currentClass;
		
		private ORecordIteratorClass<ODocument> currentIterator; 
		
		/**
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			update();
			
			if (currentClass == null) {
				return false;
			}
			
			return currentIterator.hasNext();
		}

		private void update() {
			if (ref == null) {
				// initialize the connection and the state
				classQueue = new LinkedList<String>();
				classTypes = new HashMap<String, TypeDefinition>();
				for (TypeDefinition type : types.getMappableTypes()) {
					String className = ONameUtil.encodeName(type.getIdentifier());
					classTypes.put(className, type);
					classQueue.add(className);
				}
				
				ref = database.openRead();
				if (!classQueue.isEmpty()) {
					currentClass = classQueue.poll();
					currentIterator = ref.getDatabase().browseClass(currentClass);
				}
			}
			
			// update class if needed
			while (currentClass != null && !currentIterator.hasNext()) {
				currentClass = classQueue.poll();
				currentIterator = ref.getDatabase().browseClass(currentClass);
			}
		}

		/**
		 * @see Iterator#next()
		 */
		@Override
		public Instance next() {
			if (hasNext()) {
				ODocument doc = currentIterator.next();
				return new OInstance(doc, getCurrentType());
			}
			else {
				throw new IllegalStateException("No more instances available, you should have checked hasNext().");
			}
		}
		
		private TypeDefinition getCurrentType() {
			return classTypes.get(currentClass);
		}

		/**
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @see ResourceIterator#dispose()
		 */
		@Override
		public void dispose() {
			if (ref != null) {
				ref.dispose();
			}
	
		}
	}

	private final LocalOrientDB database;
	
	private final TypeIndex types;
	
	/**
	 * Create an instance collection based on the given database
	 * @param database the database
	 * @param types the type index
	 */
	public OrientInstanceCollection(LocalOrientDB database,
			TypeIndex types) {
		super();
		this.database = database;
		this.types = types;
	}

	/**
	 * @see InstanceCollection#iterator()
	 */
	@Override
	public ResourceIterator<Instance> iterator() {
		return new OrientInstanceIterator();
	}

	/**
	 * @see InstanceCollection#hasSize()
	 */
	@Override
	public boolean hasSize() {
		return true;
	}

	/**
	 * @see InstanceCollection#size()
	 */
	@Override
	public int size() {
		int size = 0;
		DatabaseReference<ODatabaseDocumentTx> ref = database.openRead();
		ODatabaseDocumentTx db = ref.getDatabase();
		try {
			Collection<String> classes = getMainClassNames();
			for (String clazz : classes) {
				size += db.countClass(clazz);
			}
		} finally {
			ref.dispose();
		}
		
		return size;
	}

	/**
	 * Get the main class names
	 * @return the main class names
	 */
	private Collection<String> getMainClassNames() {
		Collection<String> classes = new ArrayList<String>();
		
		for (TypeDefinition type : types.getMappableTypes()) {
			classes.add(ONameUtil.encodeName(type.getIdentifier()));
		}
		
		return classes;
	}

	/**
	 * @see InstanceCollection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		DatabaseReference<ODatabaseDocumentTx> ref = database.openRead();
		ODatabaseDocumentTx db = ref.getDatabase();
		try {
			Collection<String> classes = getMainClassNames();
			for (String clazz : classes) {
				if (db.countClass(clazz) > 0) {
					return false;
				}
			}
			return true;
		} finally {
			ref.dispose();
		}
	}

}
