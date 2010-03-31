/*
 * JCaptcha, the open source java framework for captcha definition and integration
 * Copyright (c)  2007 jcaptcha.net. All Rights Reserved.
 * See the LICENSE.txt file distributed with this package.
 */

package org.liveSense.service.captcha;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.jcr.api.SlingRepository;

import com.octo.captcha.Captcha;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaAndLocale;
import com.octo.captcha.service.captchastore.CaptchaStore;

public class SlingCaptchaStore implements CaptchaStore {

/**
* The usermanager to administrate users and groups
*
* @scr.reference
*/
private SlingRepository repo;
private Session session;

    public SlingCaptchaStore() throws RepositoryException {
		session = repo.loginAdministrative(null);
    }
    
    public boolean hasCaptcha(String s) {

    	try {
    		// Get JCR node
    		//session.getWorkspace().
            Object result = null; 
            if (result != null) {
                return true;
            }
            else
                return false;

        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }        
    }

    public void storeCaptcha(String s, Captcha captcha) throws CaptchaServiceException {

        try {
        	// Put captcha to JCR 
            //cache.put(cacheQualifiedName, s, new CaptchaAndLocale(captcha));
        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }
    }

    public void storeCaptcha(String s, Captcha captcha, Locale locale) throws CaptchaServiceException {

        try {
//            cache.put(cacheQualifiedName, s, new CaptchaAndLocale(captcha, locale));
        	// Put captcha to JCR 

        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }
    }

    public boolean removeCaptcha(String s) {
        try {
        	// Remove JCR node
        	Object captcha = null;
        
        	if (captcha != null)
                return true;
            else
                return false;
        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }
    }

    public Captcha getCaptcha(String s) throws CaptchaServiceException {

        try {
        	// Retrive captcha from JCR node
            Object result = null;
            if (result != null) {
                CaptchaAndLocale captchaAndLocale = (CaptchaAndLocale) result;
                return captchaAndLocale.getCaptcha();
            }
            else
                return null;

        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }
    }

    public Locale getLocale(String s) throws CaptchaServiceException {

        try {
        	// Retrive captcha from JCR node
            Object result = null;
            if (result != null) {
                CaptchaAndLocale captchaAndLocale = (CaptchaAndLocale) result;
                return captchaAndLocale.getLocale();
            }
            else
                return null;

        } catch (Throwable e) {
            throw new CaptchaServiceException(e);
        }
    }

    public int getSize() {
/*
    	try {
    		Node root = cache.getRoot();
    		if (root != null) {
    			Node captchas = root.getChild(cacheQualifiedName);
    			if (captchas != null)
    				return captchas.dataSize();
    		}
    		return 0;
    	} catch (CacheException e) {
            throw new CaptchaServiceException(e);
        }	
        */
    	return 0;
    }

    public Collection getKeys() {
/*
    	try {
    		Node root = cache.getRoot();
    		if (root != null) {
    			Node captchas = root.getChild(cacheQualifiedName);
    			if (captchas != null) {
    				Collection keys = captchas.getKeys(); 
    		        if (keys != null)
    		        	return keys;
    			}
    		}
    		return Collections.EMPTY_SET;
    	} catch (CacheException e) {
            throw new CaptchaServiceException(e);
        }
        */
    	return null;
    }

    public void empty() {
        /*
    	try {
        	Node root = cache.getRoot();
    		if (root != null) {
    			Node captchas = root.getChild(cacheQualifiedName);
    			if (captchas != null) {
    				captchas.clearData();
    			}
    		}        	
            cache.removeNode(cacheQualifiedName);
        } catch (CacheException e) {
            throw new CaptchaServiceException(e);
        }
        */
    }
    
    /* (non-Javadoc)
	 * @see com.octo.captcha.service.captchastore.CaptchaStore#initAndStart()
	 */
	public void initAndStart() {
		/*
		String configFileName = System.getProperty(JCAPTCHA_JBOSSCACHE_CONFIG);
        if (configFileName == null)
            throw new RuntimeException("The system property " + JCAPTCHA_JBOSSCACHE_CONFIG + " have to be set");
		
		CacheFactory factory = DefaultCacheFactory.getInstance();
	    cache = factory.createCache(configFileName);
	    */								
	}

	/* (non-Javadoc)
	 * @see com.octo.captcha.service.captchastore.CaptchaStore#shutdownAndClean()
	 */
	public void cleanAndShutdown() {
		/*
		cache.stop();
		cache.destroy();
		*/
	}
}
