package org.example.model;


import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class SCCPropertiesLoader {
  private static final Logger logger = LoggerFactory.getLogger(SCCPropertiesLoader.class);
  private static final String SPRING_CLOUD_CONFIG_LABEL_PROPERTY = "spring.cloud.config.label";
  private static final String SCC_SERVER_URI_PROPERTY = "spring.cloud.config.server.uri";
  private static final String APPLICATION_NAME_PROPERTY = "application.name";
  private static final String LOCATION_PROPERTY = "LOCATION";
  private static final String ENV_PROPERTY = "ENV";
  private static final String LOCAL_PROPERTIES_FILE_PATH = "classpath:/conf/environments/{0}/{1}/*.properties";
  private static final String SCC_CONFIG_FILE_PATH = "classpath:/conf/environments/{0}/{1}/env-scc.properties";
  private static final String SCC_PROPERTY_URL_PATH = "{0}/{1}/{2}-{3}_{4}.properties";
  private static final String LOCAL_ENVIRONMENT = "local";
  private static final String DEFAULT_LABEL = "master";
  private static final String PROPERTY_DELIMITER = ",";
  private String location;
  private String environment;
  private String sccServerUri;
  private ResourceLoader resourceLoader;
  private RestTemplate restTemplate;
  private Resource[] locations;

  public SCCPropertiesLoader(ResourceLoader resourceLoader, Resource[] locations) {
    this.resourceLoader = resourceLoader;
    this.locations = locations;
    this.restTemplate = new RestTemplate();
  }

  public SCCPropertiesLoader() {
  }

  public Resource[] loadSCCProperties() {
    this.initSystemProperties();
    return this.isLocalEnvironment() ? this.getLocalResources(this.constructLocalEnvPropertiesFilePath()) : this.initEnvironmentProperties();
  }

  private void initSystemProperties() {
    this.location = this.getSystemProperty("LOCATION");
    this.environment = this.getSystemProperty("ENV");
    if (StringUtils.isEmpty(this.location) || StringUtils.isEmpty(this.environment)) {
      throw new IllegalStateException("ENV or LOCATION system properties are not set");
    }
  }

  private String getSystemProperty(String key) {
    String property = System.getProperty(key);
    if (StringUtils.isEmpty(property)) {
      property = System.getenv(key);
    }

    return StringUtils.trim(property);
  }

  private boolean isLocalEnvironment() {
    return "local".equals(this.location);
  }

  private Resource[] getLocalResources(String pattern) {
    Resource[] resources = null;

    try {
      resources = ResourcePatternUtils.getResourcePatternResolver(this.resourceLoader).getResources(pattern);
    } catch (Exception var4) {
      logger.error("There was en error while reading properties file: " + pattern);
    }

    if (ArrayUtils.isNotEmpty(this.locations)) {
      resources = (Resource[])ArrayUtils.addAll(this.locations, resources);
    }

    return resources;
  }

  private Resource[] initEnvironmentProperties() {
    Properties sccProperties = this.loadProperties(this.constructSCCConfigurationFilePath());
    if (sccProperties == null) {
      return new Resource[0];
    } else {
      this.sccServerUri = sccProperties.getProperty("spring.cloud.config.server.uri");
      if (this.sccServerUri == null) {
        logger.warn("{} is not defined in properties", "spring.cloud.config.server.uri");
        return new Resource[0];
      } else {
        String applicationNames = sccProperties.getProperty("application.name");
        if (applicationNames == null) {
          logger.warn("{} is not defined in properties", "application.name");
          return new Resource[0];
        } else {
          String[] applicationArray = applicationNames.split(",");
          String labelsString = this.getSpringLabelsAsString(sccProperties);
          String[] cloudConfigLabels = this.getLabels(labelsString);
          List<Resource> confServerResources = this.getPropertiesFromConfigServer(cloudConfigLabels, applicationArray);
          if (ArrayUtils.isNotEmpty(this.locations)) {
            confServerResources.addAll(Arrays.asList(this.locations));
          }

          return (Resource[])confServerResources.toArray(new Resource[confServerResources.size()]);
        }
      }
    }
  }

  private String[] getLabels(String labelsString) {
    String[] cloudConfigLabels = labelsString.split(",");
    boolean hasMasterBranch = Arrays.stream(cloudConfigLabels).anyMatch("master"::equals);
    if (!hasMasterBranch) {
      cloudConfigLabels = (String[])Arrays.copyOf(cloudConfigLabels, cloudConfigLabels.length + 1);
      cloudConfigLabels[cloudConfigLabels.length - 1] = "master";
    }

    return cloudConfigLabels;
  }

  private String getSpringLabelsAsString(Properties sccProperties) {
    String labelsString = sccProperties.getProperty("spring.cloud.config.label");
    if (StringUtils.isBlank(labelsString)) {
      labelsString = "master";
    }

    return labelsString;
  }

  private Properties loadProperties(String fileName) {
    logger.info("Retrieving properties file: " + fileName);
    Properties properties = new Properties();

    try {
      InputStream resourceAsStream = this.resourceLoader.getResource(fileName).getInputStream();
      properties.load(resourceAsStream);
      return properties;
    } catch (Exception e) {
      logger.error("There was en error while reading properties file: " + fileName, e);
      return null;
    }
  }

  private String constructSCCConfigurationFilePath() {
    return MessageFormat.format("classpath:/conf/environments/{0}/{1}/env-scc.properties", this.location, this.environment);
  }

  private String constructLocalEnvPropertiesFilePath() {
    return MessageFormat.format("classpath:/conf/environments/{0}/{1}/*.properties", this.location, this.environment);
  }

  private String constructSCCPropertyURLPath(String cloudConfigLabel, String applicationName) {
    return MessageFormat.format("{0}/{1}/{2}-{3}_{4}.properties", this.sccServerUri, cloudConfigLabel, applicationName, this.environment, this.location);
  }

  private List<String> constructEnvironmentPropertiesPath(String[] cloudConfigLabels, String applicationName) {
    return (List)Arrays.stream(cloudConfigLabels).map((cloudConfigLabel) -> this.constructSCCPropertyURLPath(cloudConfigLabel, applicationName)).collect(Collectors.toList());
  }

  private List<Resource> getPropertiesFromConfigServer(String[] cloudConfigLabels, String[] applicationArray) {
    List<Resource> confServerResources = new ArrayList();
    Arrays.stream(applicationArray).forEach((applicationName) -> {
      confServerResources.add(this.getProperties(this.constructEnvironmentPropertiesPath(cloudConfigLabels, applicationName)));
      confServerResources.removeIf(Objects::isNull);
    });
    return confServerResources;
  }

  private Resource getProperties(List<String> urls) {
    for(String url : urls) {
      ResponseEntity<Resource> properties = this.getPropertiesByUrl(url);
      if (properties != null && properties.hasBody()) {
        return (Resource)properties.getBody();
      }
    }

    return null;
  }

  private ResponseEntity<Resource> getPropertiesByUrl(String url) {
    logger.debug("Retrieving properties file from SCC: {}", url);

    try {
      return this.restTemplate.exchange(url, HttpMethod.GET, (HttpEntity)null, Resource.class, new Object[0]);
    } catch (RestClientException restClientException) {
      logger.warn("An exception took place when tried to retrieve properties, {}", restClientException.getMessage());
      return null;
    }
  }
}
