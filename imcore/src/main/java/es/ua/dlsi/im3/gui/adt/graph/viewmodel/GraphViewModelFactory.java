package es.ua.dlsi.im3.gui.adt.graph.viewmodel;

import es.ua.dlsi.im3.core.IM3Exception;
import es.ua.dlsi.im3.gui.utils.ParallelModelFactory;

/**
 * @author drizo
 *
 */
public class GraphViewModelFactory {
	private static GraphViewModelFactory instance = null;
	ParallelModelFactory<IViewModel> factory; 
	
	private GraphViewModelFactory() {
		factory = new ParallelModelFactory<>("viewmodel", "ViewModel");
	}
	
	public static final GraphViewModelFactory getInstance() {
		synchronized (GraphViewModelFactory.class) {
			if (instance == null) {
				instance = new GraphViewModelFactory();
			}
			return instance;
		}
	}
	
	/**
	 * It looks for the View of the model class. If the modelClassInstance belongs to class es.ua.X it looks for
	 * the class es.ua.views.XView 
	 * If not found it looks in the parents 
	 * @param modelClassInstance
	 * @return
	 * @throws IM3Exception
	 * @throws InstantiationException 
	 */
	public IViewModel createViewModelFor(Object modelClassInstance) throws IM3Exception, InstantiationException {
		return factory.createParallelClassFor(modelClassInstance);
	}
}
