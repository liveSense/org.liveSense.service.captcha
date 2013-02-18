package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CaptchaService {
		
    public BufferedImage getCaptchaImage(String captchaID, Locale locale);
    
    public boolean validateCapthaResponse(String captchaID, String response);

    public boolean validateCaptchaResponse(HttpServletRequest request, String response);

    public String extractCaptchaIdFromRequest(HttpServletRequest request);

    public void setCaptchaId(HttpServletRequest request,
	    HttpServletResponse response, String captchaId);

    public BufferedImage getCaptchaImage(String captchaEngineName, String captchaID, Locale locale);
    
    public boolean validateCaptchaResponse(String captchaEngineName, String captchaID, String response);

    public boolean validateCaptchaResponse(String captchaEngineName, HttpServletRequest request, String response);
    
    public void setCaptchaId(String captchaEngineName, HttpServletRequest request,
	    HttpServletResponse response, String captchaId);

}
