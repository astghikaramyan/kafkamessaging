package com.xyz.enterprise.common.validation;

/**
 * Validator for POJO objects with generated getters.
 */
public class ModelValidatorFactory
{
  public ModelValidatorFactory()
  {
  }

  /**
   * Avoids validation on classes under passed packages
   * @param excludePackagesxyz
   *          - optional array of packages
   * @return returns new {@link ModelValidator} object
   */
  public ModelValidator exclude( String ... excludePackages )
  {
    return new ModelValidator( excludePackages );
  }

  public ModelValidator excludeDefault()
  {
    return exclude( "org.joda.time", "java.lang" );
  }

  public ModelValidator createValidator( Class clazz )
  {
    return new ModelValidator( clazz, new String[] {
      "org.joda.time", "java.lang"
    } );
  }

  public ModelValidator createValidator( Class clazz, String ... excludePackages )
  {
    return new ModelValidator( clazz, excludePackages );
  }
}
