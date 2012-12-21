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
import org.osgi.service.component.ComponentContext;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.util.Config;


class ResizableKaptchaBasedCapthaEngineParameterProvider {
	public static final String PROP_ENGINE_NAME = "engine.name";
	public static final String DEFAULT_ENGINE_NAME = "resizableKaptchaDefault";

	public final static String PROP_WIDTH = "resizablekaptcha.width";
	public final static long DEFAULT_WIDTH = 200;

	public final static String PROP_HEIGHT = "resizablekaptcha.height";
	public final static long DEFAULT_HEIGHT = 50;

	public final static String PROP_CHARACTERS = "resizablekaptcha.characters";
	public final static String DEFAULT_CHARACTERS = "abcde2345678gfynmnpwx";

	public final static String PROP_LENGTH = "resizablekaptcha.length";
	public final static long DEFAULT_LENGTH = 5;
	
	public final static String PROP_FONTSIZE = "caresizablekaptchaptcha.fontsize";
	public final static long DEFAULT_FONTSIZE = 40;

	public final static String PROP_CHARSPACE = "resizablekaptcha.charspace";
	public final static long DEFAULT_CHARSPACE = 5;

	public final static String PROP_FONTNAMES = "resizablekaptcha.fontnames";
	public final static String DEFAULT_FONTNAMES = "Arial";
	
	public final static String PROP_FONTCOLOR = "resizablekaptcha.fontcolor";
	public final static String DEFAULT_FONTCOLOR = "black";
	
	public final static String PROP_NOISECOLOR = "resizablekaptcha.noisecolor";
	public final static String DEFAULT_NOISECOLOR = "black";
	
	public final static String PROP_BGCLEARFROM = "resizablekaptcha.bgclearfrom";
	public final static String DEFAULT_BGCLEARFROM = "lightGray";

	public final static String PROP_BGCLEARTO = "resizablekaptcha.bgclearto";
	public final static String DEFAULT_BGCLEARTO = "white";

	public final static String PROP_BORDER = "resizablekaptcha.border";
	public final static boolean DEFAULT_BORDER = false;

	public final static String PROP_BORDERCOLOR = "resizablekaptcha.bordercolor";
	public final static String DEFAULT_BORDERCOLOR = "black";
	
	public final static String PROP_BORDERTHICKNESS = "resizablekaptcha.borderthickness";
	public final static long DEFAULT_BORDERTHICKNESS = 1;

	public static String getEngineName(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_ENGINE_NAME):DEFAULT_ENGINE_NAME, DEFAULT_ENGINE_NAME);
	}

	public static long getWidth(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_WIDTH):DEFAULT_WIDTH, DEFAULT_WIDTH);
	}
	
	public static long getHeight(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_HEIGHT):DEFAULT_HEIGHT, DEFAULT_HEIGHT);
	}
	
	public static String getCharacters(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_CHARACTERS):DEFAULT_CHARACTERS, DEFAULT_CHARACTERS);
	}
	
	public static long getLength(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_LENGTH):DEFAULT_LENGTH, DEFAULT_LENGTH);
	}
	
	public static long getFontsize(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_FONTSIZE):DEFAULT_FONTSIZE, DEFAULT_FONTSIZE);
	}
	
	public static long getCharspace(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_CHARSPACE):DEFAULT_CHARSPACE, DEFAULT_CHARSPACE);
	}
	
	public static String getFontnames(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_FONTNAMES):DEFAULT_FONTNAMES, DEFAULT_FONTNAMES);
	}
	
	public static String getFontcolor(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_FONTCOLOR):DEFAULT_FONTCOLOR, DEFAULT_FONTCOLOR);
	}
	
	public static String getNoisecolor(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_NOISECOLOR):DEFAULT_NOISECOLOR, DEFAULT_NOISECOLOR);
	}
	
	public static String getBgclearfrom(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_BGCLEARFROM):DEFAULT_BGCLEARFROM, DEFAULT_BGCLEARFROM);
	}
	
	public static String getBgclearto(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_BGCLEARTO):DEFAULT_BGCLEARTO, DEFAULT_BGCLEARTO);
	}
	
	public static boolean getBorder(ComponentContext context) {
		return PropertiesUtil.toBoolean(context!=null?context.getProperties().get(PROP_BORDER):DEFAULT_BORDER, DEFAULT_BORDER);
	}
	
	public static String getBordercolor(ComponentContext context) {
		return PropertiesUtil.toString(context!=null?context.getProperties().get(PROP_BORDERCOLOR):DEFAULT_BORDERCOLOR, DEFAULT_BORDERCOLOR);
	}
	
	public static long getBorderthickness(ComponentContext context) {
		return PropertiesUtil.toLong(context!=null?context.getProperties().get(PROP_BORDERTHICKNESS):DEFAULT_BORDERTHICKNESS, DEFAULT_BORDERTHICKNESS);
	}
}

@Component(label = "%resizablekaptcha.service.name", description = "%resizablekaptcha.service.description", immediate = false, metatype = true, configurationFactory = true, policy = ConfigurationPolicy.REQUIRE, createPid = true)

@Properties(value={
		@Property(label = "%resizablekaptcha.name", description="%resizablekaptcha.name.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_ENGINE_NAME, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_ENGINE_NAME),
		@Property(label = "%resizablekaptcha.width", description="%resizablekaptcha.width.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_WIDTH, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_WIDTH),
		@Property(label = "%resizablekaptcha.height", description="%resizablekaptcha.height.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_HEIGHT, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_HEIGHT),

		@Property(label = "%resizablekaptcha.characters", description="%resizablekaptcha.characters.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_CHARACTERS, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_CHARACTERS),
		
		@Property(label = "%resizablekaptcha.length", description="%resizablekaptcha.height.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_LENGTH, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_LENGTH),
		@Property(label = "%resizablekaptcha.fontsize", description="%resizablekaptcha.fontsize.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_FONTSIZE, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTSIZE),
		@Property(label = "%resizablekaptcha.charspace", description="%resizablekaptcha.charspace.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_CHARSPACE, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_CHARSPACE),
		
		@Property(label = "%resizablekaptcha.fontnames", description="%resizablekaptcha.fontnames.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_FONTNAMES, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTNAMES),
		@Property(label = "%resizablekaptcha.fontcolor", description="%resizablekaptcha.fontcolor.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_FONTCOLOR, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTCOLOR),
		@Property(label = "%resizablekaptcha.noisecolor", description="%resizablekaptcha.noisecolor.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_NOISECOLOR, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_NOISECOLOR),

		@Property(label = "%resizablekaptcha.bgclearfrom", description="%resizablekaptcha.bgclearfrom.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_BGCLEARFROM, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BGCLEARFROM),
		@Property(label = "%resizablekaptcha.bgclearto", description="%resizablekaptcha.bgclearto.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_BGCLEARTO, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BGCLEARTO),
		@Property(label = "%resizablekaptcha.border", description="%resizablekaptcha.border.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_BORDER, boolValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDER),
		@Property(label = "%resizablekaptcha.bordercolor", description="%resizablekaptcha.bordercolor.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_BORDERCOLOR, value = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDERCOLOR),
		@Property(label = "%resizablekaptcha.borderthickness", description="%resizablekaptcha.borderthickness.description", name = ResizableKaptchaBasedCapthaEngineParameterProvider.PROP_BORDERTHICKNESS, longValue = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDERTHICKNESS)
		
})
@Service(value=CaptchaEngine.class, serviceFactory=true)
public class ResizableKaptchaBasedCapthaEngine implements CaptchaEngine {


	
	private final java.util.Properties props = new java.util.Properties();

	private Producer kaptchaProducer = null;

	private String sessionKeyValue = null;

	private String sessionKeyDateValue = null;
	
	private String name = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_ENGINE_NAME;	
	private Long width = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_WIDTH;
	private Long height = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_HEIGHT;
	private String characters = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_CHARACTERS;
	private Long length = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_LENGTH;
	private Long fontsize = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTSIZE;
	private Long charspace = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_CHARSPACE;
	private String fontnames = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTNAMES;
	private String fontcolor = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_FONTCOLOR;
	private String noisecolor = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_NOISECOLOR;
	private String bgclearfrom = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BGCLEARFROM;
	private String bgclearto = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BGCLEARTO;
	private Boolean border = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDER;
	private String bordercolor = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDERCOLOR;
	private Long borderthickness = ResizableKaptchaBasedCapthaEngineParameterProvider.DEFAULT_BORDERTHICKNESS;

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
		name = ResizableKaptchaBasedCapthaEngineParameterProvider.getEngineName(context);
		width = ResizableKaptchaBasedCapthaEngineParameterProvider.getWidth(context);
		height = ResizableKaptchaBasedCapthaEngineParameterProvider.getHeight(context);
		characters = ResizableKaptchaBasedCapthaEngineParameterProvider.getCharacters(context);
		length = ResizableKaptchaBasedCapthaEngineParameterProvider.getLength(context);
		fontsize = ResizableKaptchaBasedCapthaEngineParameterProvider.getFontsize(context);
		charspace = ResizableKaptchaBasedCapthaEngineParameterProvider.getCharspace(context);
		fontnames = ResizableKaptchaBasedCapthaEngineParameterProvider.getFontnames(context);
		fontcolor = ResizableKaptchaBasedCapthaEngineParameterProvider.getFontcolor(context);
		noisecolor = ResizableKaptchaBasedCapthaEngineParameterProvider.getNoisecolor(context);
		bgclearfrom = ResizableKaptchaBasedCapthaEngineParameterProvider.getBgclearfrom(context);
		bgclearto = ResizableKaptchaBasedCapthaEngineParameterProvider.getBgclearto(context);
		border = ResizableKaptchaBasedCapthaEngineParameterProvider.getBorder(context);
		bordercolor = ResizableKaptchaBasedCapthaEngineParameterProvider.getBordercolor(context);
		borderthickness = ResizableKaptchaBasedCapthaEngineParameterProvider.getBorderthickness(context);
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
