package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.util.Locale;


import org.osgi.service.component.ComponentContext;

import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/** Captcha service
*
* @scr.service 

* @scr.component immediate="false" label="%captcha.name"
* 					description="%captcha.description"
* @scr.property name="service.description" value="Captcha service"
* @scr.property name="service.vendor" value="eSayfasi.com"
*/
public class CaptchaServiceImpl implements CaptchaService {
	

	private static DefaultManageableImageCaptchaService instance;

    protected void activate(ComponentContext componentContext) {
    	instance = new DefaultManageableImageCaptchaService(new FastHashMapCaptchaStore(), new  DefaultGimpyEngine(), 180,
            100000, 75000);
    }
    
    public BufferedImage getCaptchaImage(String captchaID, Locale locale) {
    	return instance.getImageChallengeForID(captchaID, locale);
    }
    
    public boolean validateCapthaResponse(String captchaID, String response) {
        return instance.validateResponseForID(captchaID, response);
    }


}
