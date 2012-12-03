package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

public interface CaptchaEngine {
	
	public boolean validateResponse(String id, String response);
	
	public BufferedImage getImage(String id, String text, Locale locale);
	
	public void init();
	
	public void close();

	public String getName();
}
