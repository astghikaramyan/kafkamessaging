package org.example.model;

import org.apache.camel.spring.spi.BridgePropertyPlaceholderConfigurer;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class BridgeConfigPropertyPlaceholderConfigurer extends BridgePropertyPlaceholderConfigurer implements ResourceLoaderAware {
  private Resource[] locations;
  private ResourceLoader resourceLoader;
  private SCCPropertiesLoader sccPropertiesLoader;

  public void initProperties() {
    this.sccPropertiesLoader = new SCCPropertiesLoader(this.resourceLoader, this.locations);
    super.setLocations(this.sccPropertiesLoader.loadSCCProperties());
  }

  public void setLocations(Resource... locations) {
    this.locations = locations;
  }

  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }
}
