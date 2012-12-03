package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.osgi.framework.Constants;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

@Component(label = "%captcha.service.name", description = "%captcha.service.description", immediate = true, metatype = true)

@Properties(value={
		@Property(name = Constants.SERVICE_RANKING, intValue = 1) 
})
@Service

public class KaptchaBasedDefaultCaptchaEngine implements CaptchaEngine {

	private final java.util.Properties props = new java.util.Properties();

	private Producer kaptchaProducer = null;

	private String sessionKeyValue = null;

	private String sessionKeyDateValue = null;

	
	
	// TODO Clearing!
	private final Map<String, String> codeTexts = new ConcurrentHashMap<String, String>();

	@Override
	public BufferedImage getImage(String id, String text, Locale locale) {
		// create the text for the image
		String capText = this.kaptchaProducer.createText();
		codeTexts.put(id, capText);
		return this.kaptchaProducer.createImage(capText);
	}

	@Override
	public void init() {
		// Switch off disk based caching.
		ImageIO.setUseCache(false);

		this.props.put("kaptcha.border", "no");
		this.props.put("kaptcha.textproducer.font.color", "black");
		this.props.put("kaptcha.textproducer.char.space", "5");

		Config config = new Config(this.props);
		this.kaptchaProducer = config.getProducerImpl();
		this.sessionKeyValue = config.getSessionKey();
		this.sessionKeyDateValue = config.getSessionDate();	}

	@Override
	public void close() {
	}

	@Override
	public boolean validateResponse(String id, String response) {

		if (codeTexts.containsKey(id) && codeTexts.get(id).equalsIgnoreCase(response)) {
			codeTexts.remove(id);
			return true;
		} else {
			codeTexts.remove(id);
			return false;
		}
	}


	@Activate
	protected void activate() {
		init();
	}

	@Deactivate
	protected void deactivate() {
		close();
	}

	@Override
	public String getName() {
		return "KaptchaDefault";
	}
}
