package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CaptchaService {
	
	public void setCustomCaptchaEngine(CaptchaEngine engine);
	
    public BufferedImage getCaptchaImage(String captchaID, Locale locale);
    
    public boolean validateCapthaResponse(String captchaID, String response);

    public boolean validateCaptchaResponse(HttpServletRequest request, String response);
    
    public void setCaptchaId(HttpServletRequest request,
	    HttpServletResponse response, String captchaId);

}
