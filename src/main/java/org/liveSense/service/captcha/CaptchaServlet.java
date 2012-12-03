/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.liveSense.service.captcha;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * The <code>CaptchaServlet</code> returns a captcha image and set sessionID for captcha 
 * servlet.
 */
@Component(label="%captcha.servlet.name", description="%captcha.servlet.descpription", immediate=true, metatype=true)
@Service
@Properties(value={
		@Property(name="sling.servlet.paths", value={"/session/captcha.jpg", "/session/captcha.png"}),
		@Property(name="sling.servlet.methods", value={"GET"})
})
public class CaptchaServlet extends SlingAllMethodsServlet {



	private static final long serialVersionUID = -2160335731233369891L;

	@Reference(cardinality=ReferenceCardinality.MANDATORY_UNARY, policy=ReferencePolicy.DYNAMIC)
	CaptchaService captcha;


	/** default log */
	private final Logger log = LoggerFactory.getLogger(CaptchaServlet.class);

	@Override
	protected void doGet(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {

		byte[] captchaChallengeAsJpeg = null;
		String captchaId = null;

		// the output stream to render the captcha image as jpeg into
		ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
		try {
			// get the session id that will identify the generated captcha.
			//the same id must be used to validate the response, the session id is a good candidate!

			// Generate new captcha ID
			captchaId = UUID.randomUUID().toString();

			// call the ImageCaptchaService getChallenge method
			BufferedImage challenge = null; 

			// If engineName is defined in request parameter, we use it
			if (request.getParameter("engineName") != null) {
				challenge = captcha.getCaptchaImage(request.getParameter("engineName"), captchaId, request.getLocale());
			} else {
				challenge = captcha.getCaptchaImage(captchaId, request.getLocale());
			}

			if (request.getRequestURI().toLowerCase().endsWith(".png")) {

				// Transparency filter The white will be totally transparent
				ImageFilter filter = new RGBImageFilter() {
					@Override
					public final int filterRGB(int x, int y, int rgb) {

						int r = (rgb >> 16) & 0xff;
						int g = (rgb >>  8) & 0xff;
						int b = (rgb      ) & 0xff;

						int a = ((int) (255-(.299 * r + .587 * g + .114 * b))) & 0xff;
						return (r << 16) + (g << 8) + (b) + (a << 24); 
					}
				};

				Image transparentImage = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(challenge.getSource(), filter));
				BufferedImage pngImageBuffer = new BufferedImage(transparentImage.getWidth(null), transparentImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2 = pngImageBuffer.createGraphics();
				g2.drawImage(transparentImage, 0, 0, null);
				g2.dispose();

				ImageIO.write(pngImageBuffer, "PNG", imageOutputStream);
				response.setContentType("image/png");
			} else {
				ImageIO.write(challenge, "JPEG", imageOutputStream);
				response.setContentType("image/jpeg");
			}
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		} catch (CaptchaServiceException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		captchaChallengeAsJpeg = imageOutputStream.toByteArray();

		// flush it in the response
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		captcha.setCaptchaId(request, response, captchaId);
		response.getOutputStream().write(captchaChallengeAsJpeg);
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}

	@Override
	protected void doPost(SlingHttpServletRequest request,
			SlingHttpServletResponse response) throws IOException {
		//handle the same as GET
		doGet(request, response);
	}
}
