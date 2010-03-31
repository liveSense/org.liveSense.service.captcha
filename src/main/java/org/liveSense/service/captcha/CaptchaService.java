package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;

public interface CaptchaService {
	

    public BufferedImage getCaptchaImage(String captchaID, Locale locale);
    
    public boolean validateCapthaResponse(String captchaID, String response);

}
