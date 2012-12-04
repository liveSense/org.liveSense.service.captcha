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
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_WIDTH, longValue = 200),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_HEIGHT, longValue = 50),

		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_CHARACTERS, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_CHARACTERS),
		
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_LENGTH, longValue = 5),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_FONTSIZE, longValue = 40),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_CHARSPACE, longValue = 5),
		
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_FONTNAMES, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_FONTNAMES),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_FONTCOLOR, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_FONTCOLOR),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_NOISECOLOR, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_NOISECOLOR),

		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_BGCLEARFROM, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_BGCLEARFROM),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_BGCLEARTO, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_BGCLEARTO),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_BORDER, boolValue = false),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_BORDERCOLOR, value = ResizableKaptchaBasedCapthaEngine.DEFAULT_BORDERCOLOR),
		@Property(name = ResizableKaptchaBasedCapthaEngine.PROP_BORDERTHICKNESS, longValue = 1),
		
		@Property(name = Constants.SERVICE_RANKING, intValue = 1) 
})
@Service(value=CaptchaEngine.class, serviceFactory=true)
public class ResizableKaptchaBasedCapthaEngine implements CaptchaEngine {

	public final static String PROP_NAME = "captcha.name";
	public final static String DEFAULT_NAME = "resizableDefault";

	public final static String PROP_WIDTH = "captcha.width";
	public final static Long DEFAULT_WIDTH = new Long(200);

	public final static String PROP_HEIGHT = "captcha.height";
	public final static Long DEFAULT_HEIGHT = new Long(50);

	public final static String PROP_CHARACTERS = "captcha.characters";
	public final static String DEFAULT_CHARACTERS = "abcde2345678gfynmnpwx";

	public final static String PROP_LENGTH = "captcha.length";
	public final static Long DEFAULT_LENGTH = new Long(5);
	
	public final static String PROP_FONTSIZE = "captcha.fontsize";
	public final static Long DEFAULT_FONTSIZE = new Long(40);

	public final static String PROP_CHARSPACE = "captcha.charspace";
	public final static Long DEFAULT_CHARSPACE = new Long(5);

	public final static String PROP_FONTNAMES = "captcha.fontnames";
	public final static String DEFAULT_FONTNAMES = "Arial";
	
	public final static String PROP_FONTCOLOR = "captcha.fontcolor";
	public final static String DEFAULT_FONTCOLOR = "black";
	
	public final static String PROP_NOISECOLOR = "captcha.noisecolor";
	public final static String DEFAULT_NOISECOLOR = "black";
	
	public final static String PROP_BGCLEARFROM = "captcha.bgclearfrom";
	public final static String DEFAULT_BGCLEARFROM = "lightGray";

	public final static String PROP_BGCLEARTO = "captcha.bgclearto";
	public final static String DEFAULT_BGCLEARTO = "white";

	public final static String PROP_BORDER = "captcha.border";
	public final static Boolean DEFAULT_BORDER = false;

	public final static String PROP_BORDERCOLOR = "captcha.bordercolor";
	public final static String DEFAULT_BORDERCOLOR = "black";
	
	public final static String PROP_BORDERTHICKNESS = "captcha.borderthickness";
	public final static Long DEFAULT_BORDERTHICKNESS = new Long(1);

	
	private final java.util.Properties props = new java.util.Properties();

	private Producer kaptchaProducer = null;

	private String sessionKeyValue = null;

	private String sessionKeyDateValue = null;
	
	private Long width = DEFAULT_WIDTH;
	private Long height = DEFAULT_HEIGHT;
	private String name = DEFAULT_NAME;
	private String characters = DEFAULT_CHARACTERS;
	private Long length = DEFAULT_LENGTH;
	private Long fontsize = DEFAULT_FONTSIZE;
	private Long charspace = DEFAULT_CHARSPACE;
	private String fontnames = DEFAULT_FONTNAMES;
	private String fontcolor = DEFAULT_FONTCOLOR;
	private String noisecolor = DEFAULT_NOISECOLOR;
	private String bgclearfrom = DEFAULT_BGCLEARFROM;
	private String bgclearto = DEFAULT_BGCLEARTO;
	private Boolean border = DEFAULT_BORDER;
	private String bordercolor = DEFAULT_BORDERCOLOR;
	private Long borderthickness = DEFAULT_BORDERTHICKNESS;

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

		this.props.put("kaptcha.textproducer.font.color", fontcolor.toString());
		this.props.put("kaptcha.textproducer.font.size", fontsize.toString());
		this.props.put("kaptcha.textproducer.font.names", fontnames.toString());

		this.props.put("kaptcha.textproducer.char.space", charspace.toString());
		this.props.put("kaptcha.textproducer.char.string", characters.toString());
		this.props.put("kaptcha.textproducer.char.length", length.toString());

		this.props.put("kaptcha.image.width", width.toString());
		this.props.put("kaptcha.image.height", height.toString());

		this.props.put("kaptcha.noise.color", noisecolor.toString());

		this.props.put("kaptcha.background.clear.from", bgclearfrom.toString());
		this.props.put("kaptcha.background.clear.to", bgclearto.toString());

		this.props.put("kaptcha.border", border ? "yes" : "no");
		this.props.put("kaptcha.border.color", bordercolor.toString());
		this.props.put("kaptcha.border.thickness", borderthickness.toString());
		
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
		characters = PropertiesUtil.toString(context.getProperties().get(PROP_CHARACTERS), DEFAULT_CHARACTERS);
		length = PropertiesUtil.toLong(context.getProperties().get(PROP_LENGTH), DEFAULT_LENGTH);
		fontsize = PropertiesUtil.toLong(context.getProperties().get(PROP_FONTSIZE), DEFAULT_FONTSIZE);
		charspace = PropertiesUtil.toLong(context.getProperties().get(PROP_CHARSPACE), DEFAULT_CHARSPACE);
		fontnames = PropertiesUtil.toString(context.getProperties().get(PROP_FONTNAMES), DEFAULT_FONTNAMES);
		fontcolor = PropertiesUtil.toString(context.getProperties().get(PROP_FONTCOLOR), DEFAULT_FONTCOLOR);
		noisecolor = PropertiesUtil.toString(context.getProperties().get(PROP_NOISECOLOR), DEFAULT_NOISECOLOR);
		bgclearfrom = PropertiesUtil.toString(context.getProperties().get(PROP_BGCLEARFROM), DEFAULT_BGCLEARFROM);
		bgclearto = PropertiesUtil.toString(context.getProperties().get(PROP_BGCLEARTO), DEFAULT_BGCLEARTO);
		border = PropertiesUtil.toBoolean(context.getProperties().get(PROP_BORDER), DEFAULT_BORDER);
		bordercolor = PropertiesUtil.toString(context.getProperties().get(PROP_BORDERCOLOR), DEFAULT_BORDERCOLOR);
		borderthickness = PropertiesUtil.toLong(context.getProperties().get(PROP_BORDERTHICKNESS), DEFAULT_BORDERTHICKNESS);
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
