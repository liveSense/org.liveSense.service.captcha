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
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.ComponentContext;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;


class KaptchaBasedDefaultCaptchaEngineParameterProvider {
	public static final String PROP_ENGINE_NAME = "engine.name";
	public static final String DEFAULT_ENGINE_NAME = "KaptchaDefault";

	public static String getEngineName(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_ENGINE_NAME):DEFAULT_ENGINE_NAME, DEFAULT_ENGINE_NAME);
	}
}

@Component(label = "%kaptchadefault.service.name", description = "%kaptchadefault.service.description", immediate = true, metatype = true)

@Properties(value={
		@Property(label="%kaptchadefault.name", description="%kaptchadefault.name.description", name = KaptchaBasedDefaultCaptchaEngineParameterProvider.PROP_ENGINE_NAME,  value=KaptchaBasedDefaultCaptchaEngineParameterProvider.DEFAULT_ENGINE_NAME)
})
@Service

public class KaptchaBasedDefaultCaptchaEngine implements CaptchaEngine {

	private final java.util.Properties props = new java.util.Properties();

	private Producer kaptchaProducer = null;

	private String sessionKeyValue = null;

	private String sessionKeyDateValue = null;

	private String name = KaptchaBasedDefaultCaptchaEngineParameterProvider.DEFAULT_ENGINE_NAME;
	
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
	protected void activate(ComponentContext context) {
		this.name = KaptchaBasedDefaultCaptchaEngineParameterProvider.getEngineName(context);
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
