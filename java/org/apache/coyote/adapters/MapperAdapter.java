package org.apache.coyote.adapters;

import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.coyote.Adapter;
import org.apache.coyote.Request;
import org.apache.coyote.Response;
import org.apache.coyote.http11.Http11BaseProtocol;
import org.apache.coyote.standalone.MessageWriter;
import org.apache.tomcat.util.http.mapper.Mapper;
import org.apache.tomcat.util.loader.Loader;
import org.apache.tomcat.util.loader.Repository;

/**
 * 
 */
public class MapperAdapter implements Adapter {

    public Mapper mapper=new Mapper();
    
    // TODO: add extension mappings 
    // Key = prefix, one level only, value= class name of Adapter
    // key starts with a / and has no other / ( /foo - but not /foo/bar )
    Hashtable prefixMap=new Hashtable();

    String fileAdapterCN="org.apache.coyote.adapters.FileAdapter";
    Adapter defaultAdapter=new FileAdapter();    

    public MapperAdapter() {
    }

    public void service(Request req, final Response res)
    throws Exception {
        try {           
            String uri=req.requestURI().toString();
            if( uri.equals("/") ) uri="index.html";
            String ctx="";
            String local=uri;
            if( uri.length() > 1 ) {
                int idx=uri.indexOf('/', 1);
                if( idx > 0 ) {
                    ctx=uri.substring(0, idx);
                    local=uri.substring( idx );
                }
            }
            Adapter h=(Adapter)prefixMap.get( ctx );
            if( h != null ) {
                h.service( req, res );
            } else {
                defaultAdapter.service( req, res );
            }
        } catch( Throwable t ) {
            t.printStackTrace();
        } 

        //out.flushBuffer();
        //out.getByteChunk().flushBuffer(); - part of res.finish()
        // final processing
        MessageWriter.getWriter(req, res, 0).flush();
        res.finish();

        req.recycle();
        res.recycle();

    }


    public void addAdapter( String prefix, Adapter adapter ) {
        prefixMap.put(prefix, adapter);
    }
    
    public void setDefaultAdapter(Adapter adapter) {
        defaultAdapter=adapter;
    }

    public Adapter getDefaultAdapter() {
        return defaultAdapter;
    }

}