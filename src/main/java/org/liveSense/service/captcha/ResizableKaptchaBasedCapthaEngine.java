package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;

@Component(label = "%captcha.service.name", description = "%captcha.service.description", immediate = false, metatype = true, configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, createPid = true)

@Properties(value={
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_NAME, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_NAME),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_WIDTH, longValue = 100),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_HEIGHT, longValue = 40),
		@Property(name = Constants.SERVICE_RANKING, intValue = 1) 
})
@Service(value=CaptchaEngine.class, serviceFactory=true)
public class ResizableKaptchaBasedCapthaEngine implements CaptchaEngine {

	public final static String PROP_NAME = "captcha.name";
	public final static String DEFAULT_NAME = "resizableDefault";

	public final static String PROP_WIDTH = "captcha.width";
	public final static Long DEFAULT_WIDTH = new Long(100);

	public final static String PROP_HEIGHT = "captcha.height";
	public final static Long DEFAULT_HEIGHT = new Long(40);


	private final java.util.Properties props = new java.util.Properties();

	private Producer kaptchaProducer = null;

	private String sessionKeyValue = null;

	private String sessionKeyDateValue = null;
	
	private Long width = DEFAULT_WIDTH;
	private Long height = DEFAULT_HEIGHT;
	private String name = DEFAULT_NAME;

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

		this.props.put("kaptcha.image.width", width.toString());
		this.props.put("kaptcha.image.height", height.toString());

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
		width = PropertiesUtil.toLong(context.getProperties().get(PROP_WIDTH), DEFAULT_WIDTH);
		height = PropertiesUtil.toLong(context.getProperties().get(PROP_HEIGHT), DEFAULT_HEIGHT);
		name = PropertiesUtil.toString(context.getProperties().get(PROP_NAME), DEFAULT_NAME);
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
