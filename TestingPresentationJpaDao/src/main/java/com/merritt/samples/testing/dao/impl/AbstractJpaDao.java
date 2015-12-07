package com.merritt.samples.testing.dao.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class AbstractJpaDao<T, I extends Serializable>
{
    @PersistenceContext
    private transient EntityManager entityManager;
    
    private transient final Class<T> persistentClass;
    
    //currently there is no better way of doing this...
    @SuppressWarnings( "unchecked" )
    public AbstractJpaDao()
    {
        this.persistentClass = (Class<T>)( (ParameterizedType)getClass().getGenericSuperclass() ).getActualTypeArguments()[0];
    }
    
    public T findById( final I primaryId )
    {
        return this.entityManager.find( this.persistentClass, primaryId );
    }
    
    //they don't provide a type safe method for this yet...
    @SuppressWarnings( "unchecked" )
    public List<T> findAll()
    {
        return this.entityManager.createQuery( "from " + this.persistentClass.getName() ).getResultList();
    }
    
    public void save( final T entity )
    {
        this.entityManager.persist( entity );
    }
    
    public void update( final T entity )
    {
        this.entityManager.merge( entity );
    }
    
    public void delete( final T entity )
    {
        this.entityManager.remove( entity );
    }
    
    public void deleteById( final I primaryId )
    {
        final T entity = this.findById( primaryId );
        
        this.delete( entity );
    }
}
