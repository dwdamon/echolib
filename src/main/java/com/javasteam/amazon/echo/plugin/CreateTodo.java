package com.javasteam.amazon.echo.plugin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.javasteam.amazon.echo.AmazonAPIAccessException;
import com.javasteam.amazon.echo.EchoTodoItem;
import com.javasteam.amazon.echo.EchoUserSession;
import com.javasteam.amazon.echo.plugin.util.TodoItemRetrievedListenerImpl;


/**
 * @author ddamon
 *
 */
public class CreateTodo extends TodoItemRetrievedListenerImpl {
  private final static Log          log = LogFactory.getLog( ExecuteExternal.class.getName() );
  
  public CreateTodo() {
  }

  /* (non-Javadoc)
   * @see com.javasteam.amazon.echo.plugin.util.TodoItemRetrievedListener#handleTodoItem(com.javasteam.amazon.echo.EchoTodoItem, com.javasteam.amazon.echo.EchoUserSession, java.lang.String)
   */
  public boolean handleTodoItem( EchoTodoItem todoItem, EchoUserSession echoUserSession, String remainder ) {
    boolean retval = false;
    
    log.info(  "Processing create todo: " + todoItem.getText() );
      
    retval = true;
      
    if( this.getCommands().length > 0 ) {
      for( String todo : this.getCommands() ) {
        try {
          todo = todo.replaceAll( "\"", "" );
          log.info( "Adding todo: " + todo );
          echoUserSession.getEchoBase().addTodoItem( todo, echoUserSession.getEchoUser() );
          retval = true;
        }
        catch( AmazonAPIAccessException e ) {
          log.error( "Failed addin todo item: " + todo );
          retval = false;
        }
      }        
    }

    return retval;
  }
}