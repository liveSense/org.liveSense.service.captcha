package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.UUID;

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
import org.apache.felix.scr.annotations.Service;
import org.liveSense.core.Configurator;
import org.osgi.service.component.ComponentContext;

/** Captcha service */

@Component(label = "%captcha.service.name", description = "%captcha.service.description", immediate = false, metatype = true)

@Properties(value={
	@Property(name = CaptchaServiceImpl.PAR_STORAGE, value = CaptchaServiceImpl.DEFAULT_STORAGE, options = {
			@PropertyOption(name = CaptchaServiceImpl.STORAGE_COOKIE, value = "Cookie"),
			@PropertyOption(name = CaptchaServiceImpl.STORAGE_SESSION_ATTRIBUTE, value = "Session Attribute") 
	})
})
@Service
public class CaptchaServiceImpl implements CaptchaService {

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

	public static final String PAR_CAPTCHA_ATTRIBUTE_NAME = "captcha.uid";

	private final String authStorageType = DEFAULT_STORAGE;

	@Reference
	Configurator configurator;

	@Reference
	CaptchaEngine engine;
	
	@Activate
	protected void activate(ComponentContext componentContext) {
//		engine = new JCaptchaBasedDefaultCaptchaEngine();
//		engine.init();
	}

	@Deactivate
	protected void deactivate(ComponentContext componentContext) {
//		engine.close();
	}

	@Override
	public BufferedImage getCaptchaImage(String captchaId, Locale locale) {
		return engine.getImage(captchaId, null, locale);
	}

	@Override
	public boolean validateCapthaResponse(String captchaId, String response) {
		return engine.validateResponse(captchaId, response);
	}

	@Override
	public boolean validateCaptchaResponse(HttpServletRequest request, String response) {
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
		return validateCapthaResponse(captchaId, response);
	}

	@Override
	public void setCaptchaId(HttpServletRequest request,
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
	public void setCustomCaptchaEngine(CaptchaEngine engine) {
		if (engine != null)
			engine.close();
		this.engine = engine;
	}


}
