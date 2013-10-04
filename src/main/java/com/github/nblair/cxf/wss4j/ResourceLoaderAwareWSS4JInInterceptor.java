/**
 * 
 */
package com.github.nblair.cxf.wss4j;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.handler.RequestData;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * Custom extension of {@link WSS4JInInterceptor} to allow for reading {@link Crypto} properties using the Spring
 * {@link ResourceLoader} interface.
 * 
 * {@link #setResourceLoader(ResourceLoader)} is required (injected automatically by Spring due to {@link ResourceLoaderAware}).
 * 
 * @link http://docs.spring.io/spring/docs/3.1.x/spring-framework-reference/html/resources.html
 * @see ResourceLoader
 * @author nblair
 */
public class ResourceLoaderAwareWSS4JInInterceptor extends WSS4JInInterceptor implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;
	protected final Log log = LogFactory.getLog(this.getClass());
	/**
	 * @param resourceLoader
	 */
	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}
	/**
	 * 
	 * @param properties
	 */
	public ResourceLoaderAwareWSS4JInInterceptor(Map<String, Object> properties) {
		super(properties);
	}
	/**
	 * Attempts to resolve the resourcePath using the Spring {@link ResourceLoader}. 
	 * If the resource was not resolved by the {@link ResourceLoader}, falls back to the super implementation (reads
	 * the resourcePath location as on the classpath).
	 * 
	 * @see CryptoFactory#getInstance(Properties)
	 * @see org.apache.cxf.ws.security.wss4j.AbstractWSS4JInterceptor#loadCryptoFromPropertiesFile(java.lang.String, org.apache.ws.security.handler.RequestData)
	 * @param resourcePath
	 * @param requestData
	 * @return the initialized {@link Crypto}
	 */
	@Override
	protected Crypto loadCryptoFromPropertiesFile(String resourcePath, RequestData requestData)
			throws WSSecurityException {
		Resource resource = resourceLoader.getResource(resourcePath);
		if(resource.exists()) {
			log.debug("resourceLoader successfully located resource " + resourcePath);
			Properties properties = new Properties();
			try {
				properties.load(resource.getInputStream());
				log.debug(resourcePath + " loaded");
				return CryptoFactory.getInstance(properties);
			} catch (IOException e) {
				throw new WSSecurityException("caught IOException while loading resource at " + resourcePath, e);
			}
		} else {
			log.debug("resourceLoader unable to find resource " + resourcePath);
			return super.loadCryptoFromPropertiesFile(resourcePath, requestData);
		}
	}

}
