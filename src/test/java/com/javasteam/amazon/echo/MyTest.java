package com.javasteam.amazon.echo;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.javasteam.amazon.echo.plugin.util.EchoCommandHandler;
import com.javasteam.amazon.echo.plugin.util.EchoCommandHandlerBuilder;
import com.javasteam.amazon.echo.plugin.util.EchoCommandHandlerDefinitionPropertyParser;
import com.javasteam.http.Form;
import com.javasteam.http.FormFieldMap;
import com.javasteam.http.User;
import com.javasteam.http.UserImpl;

public class MyTest {
  private final static Log          log = LogFactory.getLog( MyTest.class.getName() );
  
  
  public MyTest() {
    // TODO Auto-generated constructor stub
  }
  
  public boolean handleCommand( EchoTodoItemImpl todoItem, EchoUserSession echoUserSession, String remainder, String[] commands ) {
    System.out.println( "Called with: " + todoItem.getText() );
    
    if( commands != null ) {
      for( String command : commands ) {
        System.out.println( "command: " + command );
      }
    }
    
    return true;
  }
  
  public static void parseIt( String theString ) {
    String classnameAndMethodString = theString.split( "[\\s;]" )[0];
    
    String[] classnameAndMethodArray = classnameAndMethodString.split( ":" );
        
    String theClassname  = classnameAndMethodArray[ 0 ];
    String theMethodname = (classnameAndMethodArray.length > 1) ? classnameAndMethodArray[ 1 ] : "handleEchoItem"; 
    
    System.out.println( "Parsed: " + theClassname + "->" + theMethodname );
  }
  
  public static void dumpForms( String url ) {
    com.javasteam.http.DumpForms dumpForms = new com.javasteam.http.DumpForms();
    
    dumpForms.dump( url );
  }

  public static void dumpElementById( String url, String id) {
    com.javasteam.http.DumpForms dumpForms = new com.javasteam.http.DumpForms();
    
    dumpForms.dumpElementById( url, id );
  }
  
  public static void printFormFields( Form form ) {
    Map<String,String> fields = form.getFields();
    
    for( String fieldName : fields.keySet() ) {
      System.out.println( "Field '" + fieldName + "' == '" + fields.get( fieldName ) + "'" );
    }    
  }
  
  public static void printFormFieldsForFormByName( String url, String id ) {
    Form form = FormFieldMap.getHtmlFormFieldsByFormName( url, id, new UserImpl() );
    printFormFields( form );
  }

  public static void printFormFieldsForFormByAction( String url, String action ) {
    Form form = FormFieldMap.getHtmlFormFieldsByAction( url, action, new UserImpl() );
    printFormFields( form );
  }

  
  
  public static synchronized String twitterLogin( User user ) throws ClientProtocolException, IOException {
    Form form = FormFieldMap.getHtmlFormFieldsByAction( "https://twitter.com/login", "https://twitter.com/sessions", user );
    
    Document doc     = FormFieldMap.getDocumentByUrl( "https://twitter.com/login", user );
    Elements elements = doc.getElementsByAttributeValue( "name", "authenticity_token" );
    
    String authenticityToken = elements.get(0).attr( "value" );
    System.out.println( "authenticityToken: " + authenticityToken );
    
    if( !form.getAction().isEmpty() ) {
      form.getFields().put( "session[username_or_email]",    user.getUsername() );
      form.getFields().put( "session[password]",             user.getPassword() );
      form.getFields().put( "authenticity_token", authenticityToken );
      EchoBase base = new EchoBase();
      
       
      try {
        base.postForm( form, user );
      }
      catch( IOException e ) {
        log.error( "Failed!", e );
      }
      catch( AmazonLoginException e ) {
        log.error( "Failed!", e );
      }

      log.info( "Login successful" );
    }
    
    return authenticityToken;
  }
  
  public static void sendTwit( EchoBase base, String authenticityToken, String twitText, User user ) {
    Form               form = new Form( "https://twitter.com/i/tweet/create" );
    Map<String,String> fields = new HashMap<String,String>();
    List<Cookie>       cookies = user.getCookieStore().getCookies();
    String             authToken = "";
    
    
    form.setFields( fields );
    
    /*
    for( Cookie cookie : cookies ) {
      if( cookie.getName().equalsIgnoreCase( "auth_token" )) {
        authToken = cookie.getValue();
      }
    }
    */
    
    fields.put( "status",             twitText );
    fields.put( "place_id",           "" );
    fields.put( "tagged_users",       "" );
    fields.put( "page_context",       "profile" );
    fields.put( "authenticity_token", authenticityToken );
    
    //fields.put( "authenticity_token", "13cc65a35f3c87ceec9059f8838fb4e8cd32b773" );
    
    printFormFields( form );
    
    try {
      base.postForm( form, user );
    }
    catch( AmazonLoginException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch( IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public static void main( String[] args ) {
    BasicConfigurator.configure();
    EchoBase.setHttpClientPool( 1, 1 );
    
    LogManager.getRootLogger().setLevel( Level.DEBUG );
    EchoBase           base = new EchoBase();
    
    //dumpForms( "https://pitangui.amazon.com/" );
    //dumpForms( "https://twitter.com/login" );
    //dumpElementById( "ap_signin_form" );
    //printFormFieldsForFormByName( "https://pitangui.amazon.com/", "ap_signin_form" );
    //printFormFieldsForFormByAction( "https://twitter.com/login", "https://twitter.com/sessions" );
    User user = new UserImpl( "username", "password" );
    
    try {
      String authenticityToken = twitterLogin( user );
      base.httpGet( "https://twitter.com/i/jot", user );
      sendTwit( base, authenticityToken, "This is a test twit_001", user );
    }
    catch( ClientProtocolException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch( IOException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
  }

  public static void main_old( String[] args ) {
    parseIt( "com.junk.test.Blah:handleSomething:handleSomethingElse some other stuff" );
    parseIt( "com.junk.test.Blah:handleSomething some other stuff" );
    parseIt( "com.junk.test.Blah some other stuff" );
    parseIt( "" );
    //parseIt( null );

    EchoCommandHandlerBuilder builder = EchoCommandHandlerDefinitionPropertyParser.getCommandHandlerBuilder( "com.javasteam.amazon.echo.MyTest:handleCommand key=theKey,command=\"one two\" two" );
    
    EchoCommandHandler handler = builder.generate();

    EchoTodoItemImpl todoItem = new EchoTodoItemImpl();
    todoItem.setText( "This is a test todo item" );
    EchoUserSession echoUserSession = new EchoUserSession();
    String remainder = "extra stuff";
    
    boolean output = handler.handle( todoItem, echoUserSession, remainder );
    System.out.println( "returned: " + output );
  }
}
