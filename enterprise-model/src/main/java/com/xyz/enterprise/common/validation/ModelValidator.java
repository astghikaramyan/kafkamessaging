package com.xyz.enterprise.common.validation;

import com.xyz.enterprise.common.annotation.Required;
import com.xyz.enterprise.common.annotation.Size;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModelValidator
{
  // default message from required annotation
  public final static String DEFAULT_MESSAGE = "Field is required";

  private String[] excludePackages;
  private Class rootClass;

  public ModelValidator( String[] excludePackages )
  {
    this.excludePackages = excludePackages;
  }

  public ModelValidator( Class rootClass, String[] excludePackages )
  {
    this.rootClass = rootClass;
    this.excludePackages = excludePackages;
  }

  /**
   * Performs validation on passed POJO
   *
   * @param obj   - object to be validated
   * @param clazz - Class of object to be validated
   * @return - Set of validation messages
   * @throws IllegalAccessException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   */
  public Set<String> validate( Object obj, Class clazz )
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    Set<String> messages = new LinkedHashSet<String>();
    Field[] fields = clazz.getDeclaredFields();
    for( Field field : fields ) {
      if (!field.isSynthetic()) {
	Required required = field.getAnnotation( Required.class );
	Size size = field.getAnnotation( Size.class );
	Object value = getPojoFieldValue( obj, clazz, field );
	if ( size != null && value instanceof List ) {
	  List listValue = (List)value;
	  if ( size.min() < 0 || size.max() < 0 ) {
	    messages.add( generateNegativeMinMaxMessage( size, field, clazz ) );
	  } else if ( size.min() > size.max() ) {
	    messages.add( generateMaxLessThenMinMessage( size, field, clazz ) );
	  } else if ( listValue.size() < size.min() ||
	      listValue.size() > size.max() ) {
	    messages.add(
		generateSizeNotValidMessage( size, field, clazz, listValue ) );
	  }
	}
	if ( explicitlyRequired( required ) && value == null ) {
	  messages.add( generateMessage( required, field, clazz ) );
	} else if ( value != null && canBeValidated( field ) ) {
	  if ( value instanceof Collection )
	    messages.addAll( collectMessagesOfEachItemOfCollection( field, value ) );
	  else
	    messages.addAll( validate( value, field.getType() ) );
	}
      }
    }
    return messages;
  }

  public Set<String> validate( Object obj )
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    return validate( obj, rootClass );
  }

  Set<String> collectMessagesOfEachItemOfCollection( Field field, Object value )
    throws IllegalAccessException, NoSuchMethodException, InvocationTargetException
  {
    Class collectionType = (Class)( (ParameterizedType)field.getGenericType() ).getActualTypeArguments()[0];
    Set<String> messagesOfCollection = new LinkedHashSet<String>();
    if ( canBeValidated( collectionType ) ) {
      for( Object item : (Collection)value ) {
        messagesOfCollection.addAll( validate( item, collectionType ) );
      }
    }
    return messagesOfCollection;
  }

  private boolean explicitlyRequired( Required required )
  {
    if ( required != null && required.value() ) {
      Class[] operations = required.operations();
      // by default checking should be done for all operations
      if ( operations == null || operations.length == 0 ) {
        return true;
      }

      // if the operations were specified then check only specific
      for( Class operation : operations ) {
        if ( operation.equals( rootClass ) ) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean canBeValidated( Field field )
  {
    Class<?> type = field.getType();
    return canBeValidated( type );
  }

  private boolean canBeValidated( Class<?> type )
  {
    if ( !type.isEnum() ) {
      boolean toBeValidated = true;
      String fullName = type.getName();
      for( String excludePackage : excludePackages ) {
        if ( fullName.startsWith( excludePackage ) ) {
          toBeValidated = false;
          break;
        }
      }
      return toBeValidated;
    }
    return false;
  }

  private <T> Object getPojoFieldValue( Object obj, Class<T> clazz, Field field )
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    return clazz.getMethod( getterFor( field ) ).invoke( obj );
  }

  private String getterFor( Field field )
  {
    String name = field.getName();
    String methodName = name;
    if ( !name.startsWith( "is" ) ) {
      String methodStart;
      if ( field.getType() == boolean.class )
        methodStart = "is";
      else
        methodStart = "get";
      methodName = methodStart + name.substring( 0, 1 ).toUpperCase()
        + name.substring( 1 );
    } else if ( field.getType() == Boolean.class ) {
      if ( field.getType() == Boolean.class )
        methodName = "get" + name.substring( 0, 1 ).toUpperCase()
          + name.substring( 1 );
      else
        methodName = "get" + methodName.substring( 2 );
    }
    return methodName;
  }

  private String generateMessage( Required required, Field field, Class clazz )
  {
    if ( required.messageOnAbsence().equals( DEFAULT_MESSAGE ) ) {
      // fullpath will contain modifiers, field type and fully-qualified name
      String shortPath = getShortPath( clazz, field );
      return DEFAULT_MESSAGE + ": " + shortPath;
    } else {
      return required.messageOnAbsence();
    }
  }

  private String generateMaxLessThenMinMessage( Size size, Field field, Class clazz )
  {
    String shortPath = getShortPath( clazz, field );
    return String.format( "Max less then min: %s min: %d max: %d", shortPath,
      size.min(),
      size.max() );
  }

  private String generateSizeNotValidMessage( Size size, Field field, Class clazz,
    List listValue )
  {
    String shortPath = getShortPath( clazz, field );
    return String.format( "%s: %s expected min: %d max: %d actual: %d",
      size.errorMessage(), shortPath, size.min(),
      size.max(), listValue.size() );
  }

  private String generateNegativeMinMaxMessage( Size size, Field field, Class clazz )
  {
    String shortPath = getShortPath( clazz, field );
    return String.format(
      "Negative range value error: %s min: %d max: %d",
      shortPath,
      size.min(),
      size.max() );
  }

  private String getShortPath( Class clazz, Field field )
  {
    String fullPath = field.toString();
    String[] parts = fullPath.split( " " );
    String fullName = parts[parts.length - 1];
    return fullName.substring(
      fullName.indexOf( clazz.getSimpleName() ) );
  }
}
