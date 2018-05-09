package es.ua.dlsi.im3.gui.utils;

import es.ua.dlsi.im3.core.IM3Exception;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to create views and view model counterparts of models
 * @author drizo
 *
 */
public class ParallelModelFactory<ResultClassType> {
	private String classNameSuffix;
	private String packageName;
	
	public ParallelModelFactory(String subpackageName, String classNameSuffix) {
		this.packageName = subpackageName;
		this.classNameSuffix = classNameSuffix;
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
	public ResultClassType createParallelClassFor(Object modelClassInstance) throws IM3Exception, InstantiationException {
		Class<?> modelClass = modelClassInstance.getClass();
		return createParallelClassFor(modelClass, modelClassInstance);
	}
	/**
	 * It looks for the View of the model class. If the modelClassInstance belongs to class es.ua.X it looks for
	 * the class es.ua.views.XView 
	 * If not found it looks in the parents 
	 * @return
	 * @throws InstantiationException 
	 */
	private ResultClassType createParallelClassFor(Class<?> modelClass, Object modelClassInstance) throws IM3Exception, InstantiationException {
		ResultClassType result = null;
		String viewPackageName = modelClass.getPackage().getName() + "." + packageName;
		Class<?> viewModelClass = null;
		String vc = viewPackageName + "." + modelClass.getSimpleName() + classNameSuffix;			
		try {
			viewModelClass = Class.forName(vc);
		} catch (Exception e) {
			Class<?> modelParentClass = modelClass.getSuperclass();
			if (modelParentClass == null) {
				return null;
			}
			result = createParallelClassFor(modelParentClass, modelClassInstance);
			if (result == null) {
				Logger.getLogger(ParallelModelFactory.class.getName()).log(Level.INFO, "Class " + vc + " not found", e);
				throw new IM3Exception("Cannot find class for " + modelClass.getName() + " or any of its ancestors in package "+ viewPackageName);
			}
			return result;
		}
		
		Constructor<?> constructor = null;
		try {
			constructor = viewModelClass.getConstructor(modelClass);
		} catch (NoSuchMethodException | SecurityException e1) {
			throw new IM3Exception("Cannot find public constructor of " + viewModelClass.getName() + " with parameter class " + modelClass.getName());
		}
		try {
			return (ResultClassType) constructor.newInstance(modelClassInstance);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			throw new InstantiationException("Cannot instantiate "+ viewModelClass.getName()); 
		}
	}
	
}
