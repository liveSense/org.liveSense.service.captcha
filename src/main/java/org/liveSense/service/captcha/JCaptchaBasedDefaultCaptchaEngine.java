package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

@Component(label = "%captcha.service.name", description = "%captcha.service.description", immediate = false, metatype = true)

@Properties(value={
		@Property(name = Constants.SERVICE_RANKING, value = "0") 
})
@Service
public class JCaptchaBasedDefaultCaptchaEngine implements CaptchaEngine {
	private DefaultManageableImageCaptchaService instance;
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
	protected void activate() {
		init();
	}
	
	@Deactivate
	protected void deactivate() {
		close();
	}
}
