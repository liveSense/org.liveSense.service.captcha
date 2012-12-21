package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

class JCaptchaBasedDefaultCaptchaEngineParameterProvider {
	public static final String PROP_ENGINE_NAME = "engine.name";
	public static final String DEFAULT_ENGINE_NAME = "JCaptchaDefault";

	public static String getEngineName(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_ENGINE_NAME):DEFAULT_ENGINE_NAME, DEFAULT_ENGINE_NAME);
	}
}

@Component(label = "%jcaptchadefault.service.name", description = "%jcaptchadefault.service.description", immediate = true, metatype = true)

@Properties(value={
		@Property(label="%jcaptchadefault.name", description="%jcaptchadefault.name.description", name = JCaptchaBasedDefaultCaptchaEngineParameterProvider.PROP_ENGINE_NAME,  value=JCaptchaBasedDefaultCaptchaEngineParameterProvider.DEFAULT_ENGINE_NAME)
})
@Service
public class JCaptchaBasedDefaultCaptchaEngine implements CaptchaEngine {

	
	private DefaultManageableImageCaptchaService instance;
	private String name = JCaptchaBasedDefaultCaptchaEngineParameterProvider.DEFAULT_ENGINE_NAME;
			
	@Override
	public BufferedImage getImage(String id, String text, Locale locale) {
		BufferedImage ret = null;
		for (int i=0; i<3; i++) {
			try {
				ret = instance.getImageChallengeForID(id, locale);
				return ret;
			} catch (CaptchaServiceException e) {
			}
		}
		return instance.getImageChallengeForID(id, locale);
	}

	@Override
	public void init() {
		instance = new DefaultManageableImageCaptchaService(
				new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), 180,
				100000, 75000);
	}

	@Override
	public void close() {
		if (instance != null)
			instance.emptyCaptchaStore();
	}

	@Override
	public boolean validateResponse(String id, String response) {
		return instance.validateResponseForID(id, response);
	}
	

	@Activate
	protected void activate(ComponentContext context) {
		name = JCaptchaBasedDefaultCaptchaEngineParameterProvider.getEngineName(context);
		init();
	}
	
	@Deactivate
	protected void deactivate() {
		close();
	}

	@Override
	public String getName() {
		return name;
	}
}
