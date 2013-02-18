package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.PropertyOption;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.ReferenceStrategy;
import org.apache.felix.scr.annotations.References;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.liveSense.core.Configurator;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Captcha service */

@Component(label = "%captcha.service.name", description = "%captcha.service.description", immediate = false, metatype = true)

@Properties(value={
		@Property(label="%captcha.service.defaultengine", description="%captcha.service.defaultengine.description", name = CaptchaServiceImpl.PAR_CAPTHA_ENGINE, value = CaptchaServiceImpl.DEFAULT_CAPTCHA_ENGINE),
		@Property(label="%captcha.service.storage",description="%captcha.service.storage.description", name = CaptchaServiceImpl.PAR_STORAGE, value = CaptchaServiceImpl.DEFAULT_STORAGE, options = {
				@PropertyOption(name = CaptchaServiceImpl.STORAGE_COOKIE, value = "Cookie"),
				@PropertyOption(name = CaptchaServiceImpl.STORAGE_SESSION_ATTRIBUTE, value = "Session Attribute") 
		})
})

@References(value={
		@Reference(cardinality=ReferenceCardinality.MANDATORY_MULTIPLE, policy=ReferencePolicy.DYNAMIC, strategy=ReferenceStrategy.EVENT, bind="bindEngine", unbind="unBindEngine", referenceInterface=CaptchaEngine.class)
})
@Service
public class CaptchaServiceImpl implements CaptchaService {

	Logger log = LoggerFactory.getLogger(CaptchaServiceImpl.class);

	/**
	 * The value of the parameter indicating the use of a Cookie to store the
	 * authentication data.
	 */
	public static final String STORAGE_COOKIE = "cookie";

	/**
	 * The value of the parameter indicating the use of a session attribute to
	 * store the authentication data.
	 */
	public static final String STORAGE_SESSION_ATTRIBUTE = "session";

	/**
	 * To be used to determine if the chalange ID comes from a cookie or from a
	 * session attribute.
	 */
	public static final String DEFAULT_STORAGE = STORAGE_COOKIE;
	public static final String PAR_STORAGE = "captcha.storage";

	public static final String DEFAULT_CAPTCHA_ENGINE = "JCaptchaDefault";
	public static final String PAR_CAPTHA_ENGINE = "captcha.engine.default";

	public static final String PAR_CAPTCHA_ATTRIBUTE_NAME = "captcha.uid";

	private String authStorageType = DEFAULT_STORAGE;
	private String defaultCapthaEngineName = DEFAULT_CAPTCHA_ENGINE;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	Configurator configurator;

	CaptchaEngine defaultEngine;

	Map<String, CaptchaEngine> captchaEngines = new ConcurrentHashMap<String, CaptchaEngine>();

	protected void bindEngine(CaptchaEngine engine) {
		log.info("Binding CaptcheEngine: "+engine.getName());
		if (defaultCapthaEngineName.equals(engine.getName())) {
			defaultEngine = engine;
		}
		captchaEngines.put(engine.getName(), engine);

	}

	protected void unBindEngine(CaptchaEngine engine) {
		log.info("UnBinding CaptcheEngine: "+engine.getName());
		captchaEngines.remove(engine.getName());
		if (defaultCapthaEngineName.equals(engine.getName())) {
			defaultEngine = null;
		}

	}

	@Activate
	protected void activate(ComponentContext componentContext) {
		authStorageType = PropertiesUtil.toString(componentContext.getProperties().get(PAR_STORAGE), DEFAULT_STORAGE);
		defaultCapthaEngineName = PropertiesUtil.toString(componentContext.getProperties().get(PAR_CAPTHA_ENGINE), DEFAULT_CAPTCHA_ENGINE);
	}

	@Deactivate
	protected void deactivate(ComponentContext componentContext) {
	}

	private CaptchaEngine getDefaultEngine() {
		if (defaultEngine != null) {
			return defaultEngine;
		} else {
			log.warn("No default engine is defined, we use the first service");
			for (Map.Entry<String, CaptchaEngine> entry : captchaEngines.entrySet()) {
				return entry.getValue();
			}
		}
		return null;
	}
	
	private CaptchaEngine getCapthaEngineByName(String engineName) {
		if (engineName == null) {
			return getDefaultEngine();
		}
		if (captchaEngines.containsKey(engineName)) {
			return captchaEngines.get(engineName);
		} else {
			log.warn("Could not found CaptchaEngine in the given name: "+engineName);
			return getDefaultEngine();
		}
	}

	private BufferedImage getCaptchaImage(CaptchaEngine engine, String captchaId, Locale locale) {
		return engine.getImage(captchaId, null, locale);
	}

	private boolean validateCaptchaResponse(CaptchaEngine engine, String captchaId, String response) {
		return engine.validateResponse(captchaId, response);
	}

	private boolean validateCaptchaResponse(CaptchaEngine engine, HttpServletRequest request, String response) {
		return validateCaptchaResponse(engine, extractCaptchaIdFromRequest(request), response);
	}

	public void setCaptchaId(CaptchaEngine engine, HttpServletRequest request,
			HttpServletResponse response, String captchaId) {
		if (authStorageType.equals(STORAGE_COOKIE)) {
			Cookie[] cookies = request.getCookies();
			boolean foundCookie = false;

			if (cookies != null) {
				for (int i = 0; i < cookies.length; i++) {
					Cookie cookie = cookies[i];
					if (cookie.getName().equals(PAR_CAPTCHA_ATTRIBUTE_NAME)) {
						foundCookie = true;
						cookie.setMaxAge(configurator.getSessionTimeout().intValue());
						cookie.setValue(captchaId);
						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
			}

			if (!foundCookie) {
				Cookie cookie = new Cookie(PAR_CAPTCHA_ATTRIBUTE_NAME,
						captchaId);
				cookie.setMaxAge(configurator.getSessionTimeout().intValue());
				cookie.setPath("/");
				response.addCookie(cookie);
			}

		} else {
			if (request.getSession() == null) {
				request.getSession(true).setMaxInactiveInterval(
						configurator.getSessionTimeout().intValue());
				request.getSession().setAttribute(PAR_STORAGE, captchaId);
				captchaId = request.getSession().getId();
			}

		}
	}

	@Override
	public BufferedImage getCaptchaImage(String captchaEngineName,
			String captchaID, Locale locale) {
		return getCaptchaImage(getCapthaEngineByName(captchaEngineName), captchaID, locale);
	}

	@Override
	public boolean validateCaptchaResponse(String captchaEngineName,
			String captchaID, String response) {
		return validateCaptchaResponse(getCapthaEngineByName(captchaEngineName), captchaID, response);
	}

	@Override
	public boolean validateCaptchaResponse(String captchaEngineName,
			HttpServletRequest request, String response) {
		return validateCaptchaResponse(getCapthaEngineByName(captchaEngineName), request, response);
	}

	@Override
	public void setCaptchaId(String captchaEngineName,
			HttpServletRequest request, HttpServletResponse response,
			String captchaId) {
		setCaptchaId(getCapthaEngineByName(captchaEngineName), request, response, captchaId);
	}

	@Override
	public BufferedImage getCaptchaImage(String captchaID, Locale locale) {
		return getCaptchaImage(getDefaultEngine(), captchaID, locale);
	}

	@Override
	public boolean validateCapthaResponse(String captchaID, String response) {
		return validateCaptchaResponse(getDefaultEngine(), captchaID, response);
	}

	@Override
	public boolean validateCaptchaResponse(HttpServletRequest request,
			String response) {
		return validateCaptchaResponse(getDefaultEngine(), request, response);
	}

	@Override
	public void setCaptchaId(HttpServletRequest request,
			HttpServletResponse response, String captchaId) {
		setCaptchaId(getDefaultEngine(), request, response, captchaId);
	}

	@Override
	public String extractCaptchaIdFromRequest(HttpServletRequest request) {
		String captchaId = null;
		if (authStorageType.equals(STORAGE_COOKIE)) {
			captchaId = UUID.randomUUID().toString();
			Cookie[] cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookie.getName().equals(PAR_CAPTCHA_ATTRIBUTE_NAME)) {
					captchaId = cookie.getValue();
				}
			}

		} else {
			if (request.getSession() != null) {
				captchaId = (String)request.getSession().getAttribute(PAR_CAPTCHA_ATTRIBUTE_NAME);
			}
		}
		return captchaId;
	}

}
