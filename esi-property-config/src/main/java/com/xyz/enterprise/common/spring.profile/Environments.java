package org.example;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Environments {
  DEV_ADC("dev", "adc"),
  PROD_ADC("prod", "adc"),
  DEV_LOCAL("dev", "local"),
  SJCD1_SC9("sjcd1", "sc9");

  private String environmentName;
  private String dataCenterName;
  private static final String DELIMITER = "_";

  private Environments(String environmentName, String dataCenterName) {
    this.environmentName = environmentName;
    this.dataCenterName = dataCenterName;
  }

  public String getEnvironmentName() {
    return this.environmentName;
  }

  public String getDataCenterName() {
    return this.dataCenterName;
  }

  public String getFullEnvironmentName() {
    return this.environmentName + "_" + this.dataCenterName;
  }

  public static Environments getEnvironment(String fullName) {
    Optional<Environments> environment = EnumSet.allOf(Environments.class).stream().filter((env) -> env.getFullEnvironmentName().equals(fullName)).findFirst();
    return (Environments)environment.orElseThrow(() -> new IllegalArgumentException(String.format("Unknown '%s' environment", fullName)));
  }

  public static List<String> getFullEnvironmentNames() {
    return (List)EnumSet.allOf(Environments.class).stream().map(Environments::getFullEnvironmentName).collect(Collectors.toList());
  }
}
