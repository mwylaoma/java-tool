package org.osgl.util;

import org.osgl.$;
import org.osgl.exception.UnexpectedException;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides utilities for generic relevant operations
 */
public class Generics {

    public static <T> Class<T> classOf(Type type) {
        if (type instanceof Class) {
            return $.cast(type);
        } else if (type instanceof ParameterizedType) {
            return $.cast(((ParameterizedType) type).getRawType());
        } else {
            throw new IllegalArgumentException("Cannot find class from type: " + type);
        }
    }

    /**
     * Returns implementation of type parameters declared in the root class/interface by the given sub class
     * <p>
     * For example, suppose a super class has generic type params: `MyBase&lt;MODEL, QUERY&gt;`, and a
     * sub class is declared as `MyModel&lt;MyModel, MyQuery&gt;`, then passing `MyModel.class` to
     * this method shall return a list of `{MyModel.class, MyQuery.class}`
     * <p>
     * <p>
     * If the specified class doesn't have generic type declared then it shall return
     * an empty list
     *
     * @param theClass  the end class
     * @param rootClass the root class or interface
     * @return a list of type variable implementation on root class
     */
    public static List<Type> typeParamImplementations(Class theClass, Class rootClass) {
        if (rootClass.getTypeParameters().length == 0) {
            return C.list();
        }
        try {
            return typeParamImplementations(theClass, rootClass, new ArrayList<Type>());
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(S.fmt("Cannot infer type parameter implementation on %s against %s", theClass.getName(), rootClass.getName()), e);
        }
    }

    /**
     * Build class type variable name and type variable implementation lookup
     *
     * @param theClass the class to build the lookup
     * @return the lookup
     */
    public static Map<String, Class> buildTypeParamImplLookup(Class theClass) {
        Map<String, Class> lookup = new HashMap<>();
        buildTypeParamImplLookup(theClass, lookup);
        return lookup;
    }

    public static void buildTypeParamImplLookup(Class theClass, Map<String, Class> lookup) {
        if (null == theClass || Object.class == theClass) {
            return;
        }

        // what type variable of the super class implemented by this class
        Type superType = theClass.getGenericSuperclass();
        if (superType instanceof ParameterizedType) {
            ParameterizedType ptype = $.cast(superType);
            Type[] typeParams = ptype.getActualTypeArguments();
            TypeVariable[] typeArgs = theClass.getSuperclass().getTypeParameters();
            int len = typeParams.length;
            for (int i = 0; i < len; ++i) {
                Type typeParam = typeParams[i];
                if (typeParam instanceof Class) {
                    TypeVariable typeVar = typeArgs[i];
                    lookup.put(typeVar.getName(), (Class) typeParam);
                } else if (typeParam instanceof TypeVariable) {
                    TypeVariable var = $.cast(typeParam);
                    String name = var.getName();
                    Class impl = lookup.get(name);
                    TypeVariable typeVar = typeArgs[i];
                    if (null != impl) {
                        lookup.put(typeVar.getName(), impl);
                    } else {
                        Type[] ta = var.getBounds();
                        if (null != ta && ta.length == 1) {
                            Type bound = ta[0];
                            if (bound instanceof Class) {
                                lookup.put(typeVar.getName(), (Class) bound);
                            }
                        }
                    }
                }
            }
        }

        buildTypeParamImplLookup(theClass.getSuperclass(), lookup);
    }

    private static List<Type> typeParamImplementations(Class theClass, Class rootClass, List<Type> subClassTypeParams) {
        Type superType = null;
        Type[] interfaces = theClass.getGenericInterfaces();
        for (Type intf : interfaces) {
            if (intf instanceof ParameterizedType) {
                Type rawType = ((ParameterizedType) intf).getRawType();
                if ($.eq(rawType, rootClass)) {
                    superType = intf;
                }
            }
        }
        if (null == superType) {
            superType = theClass.getGenericSuperclass();
        }
        Class superClass = null;
        boolean theClassIsInterface = theClass.isInterface();
        while (!(superType instanceof ParameterizedType) && Object.class != superType) {
            if (theClassIsInterface) {
                try {
                    if (null == superClass) {
                        superClass = theClass;
                    }
                    Type[] types = superClass.getGenericInterfaces();
                    superType = types[0];
                    Class[] intfs = theClass.getInterfaces();
                    superClass = intfs[0];
                } catch (RuntimeException e) {
                    throw new UnexpectedException("Cannot find type implementation for %s", theClass);
                }
            } else {
                if (null == superClass) {
                    superClass = theClass.getSuperclass();
                }
                superType = superClass.getGenericSuperclass();
                superClass = superClass.getSuperclass();
            }
        }
        if (superType instanceof ParameterizedType) {
            TypeVariable<Class>[] declaredTypeVariables = theClass.getTypeParameters();
            ParameterizedType pSuperType = $.cast(superType);
            Type[] superTypeParams = pSuperType.getActualTypeArguments();
            List<Type> nextList = new ArrayList<>();
            for (Type stp : superTypeParams) {
                if (stp instanceof Class || stp instanceof ParameterizedType) {
                    nextList.add(stp);
                } else if (stp instanceof TypeVariable) {
                    boolean found = false;
                    for (int i = 0; i < declaredTypeVariables.length; ++i) {
                        TypeVariable declared = declaredTypeVariables[i];
                        if ($.eq(declared, stp)) {
                            nextList.add(subClassTypeParams.get(i));
                            found = true;
                        }
                    }
                    E.illegalStateIf(!found, "Cannot find type implementation for %s", theClass);
                }
            }
            superClass = (Class) pSuperType.getRawType();
            // We don't compare class directly to workaround some rare case when same class loaded by
            // different class loaders
            if ($.eq(superClass.getName(), rootClass.getName())) {
                return nextList;
            }
            return typeParamImplementations(superClass, rootClass, nextList);
        }
        throw E.unexpected("Cannot find type param implementation: super type %s of %s is not a parameterized type", superType, theClass);
    }

    public static void main(String[] args) {
        Class c = Serializable.class;
        System.out.println(c.getGenericSuperclass());
        System.out.println($.toString2(c.getGenericInterfaces()));
    }


}
