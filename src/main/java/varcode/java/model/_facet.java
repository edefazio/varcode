package varcode.java.model;

/**
 * Marker interface for organizing "facets" of 
 * _class, _enum, _interfaces)
 * 
 * (any field, method, modifier, annotation,...)
 * this allows the interface for an entity (_class,_enum, _interface)
 * 
 * to have "generic" methods:
 * public _class add( facet facet )
 * {
 *    //...adds a _method, _annotation, _field, _constructor, _import, 
 * }
 * 
 * 
 * @author Eric DeFazio eric@varcode.io
 */
public interface _facet
{
    
}
