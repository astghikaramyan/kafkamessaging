package org.example;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfileManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(ProfileManager.class);
  private static final String SPRING_PROFILES_ACTIVE_IS_ABSENT_WARNING = "spring.profiles.active value is not set. Please specify it in the app run configuration for correctly setting up the environment";
  @Value("${spring.profiles.active:}")
  private String activeProfiles;
  private static final String LOCATION_PROPERTY = "LOCATION";
  private static final String ENV_PROPERTY = "ENV";
  private static final String PROFILE_DELIMITER = ",";

  @PostConstruct
  public void initActiveProfileProperties() {
    if (StringUtils.isNotEmpty(this.activeProfiles)) {
      List<String> profiles = Arrays.asList(this.activeProfiles.split(","));
      List<String> environmentNames = Environments.getFullEnvironmentNames();
      Stream var10000 = profiles.stream();
      environmentNames.getClass();
      Optional<String> activeProfile = var10000.filter(environmentNames::contains).findFirst();
      if (activeProfile.isPresent()) {
        Environments env = (Environments)activeProfile.map(Environments::getEnvironment).get();
        this.setEnvironmentProperties(env.getEnvironmentName(), env.getDataCenterName());
      } else {
        LOGGER.warn("spring.profiles.active value is not set. Please specify it in the app run configuration for correctly setting up the environment");
      }
    } else {
      LOGGER.warn("spring.profiles.active value is not set. Please specify it in the app run configuration for correctly setting up the environment");
    }

  }

  private void setEnvironmentProperties(String env, String location) {
    System.setProperty("ENV", env);
    System.setProperty("LOCATION", location);
  }
}
