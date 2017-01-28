/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template toFile, choose Tools | Templates
 * and open the template in the editor.
 */
package usecase.model;

import varcode.java.adhoc.Export;
import varcode.java.model._class;

/**
 *
 * @author Eric
 */
public class AuthorExportSimple
{
    public static void main( String[] args )
    {
        _class _c = _class.of( "package com.example.helloworld;",
            "public final class HelloWorld" )
            .mainMethod( "System.out.println(\"Hello JavaPoet!\");" );
            
        Export.TEMP_DIR.toFile( _c );
        
        // we can specify the Base Directory this way:
        // Export.dir( "C:\\temp" ).toFile( _c );
    }
}
