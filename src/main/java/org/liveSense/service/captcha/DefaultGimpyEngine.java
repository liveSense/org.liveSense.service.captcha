package org.liveSense.service.captcha;

import java.awt.Color;
import java.awt.Font;

import com.jhlabs.image.PinchFilter;
import com.jhlabs.math.ImageFunction2D;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.SingleColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.BaffleTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;

public class DefaultGimpyEngine extends ListImageCaptchaEngine {

	@Override
	protected void buildInitialFactories() {

		//build filters
		PinchFilter pinch = new PinchFilter();

		pinch.setAmount((float)(-.5*Math.random()));
		pinch.setRadius(70);
		pinch.setAngle((float) (Math.PI/32*Math.random()));
		pinch.setCentreX(0.5f);
		pinch.setCentreY(-0.01f);
		pinch.setEdgeAction(ImageFunction2D.CLAMP);       

		PinchFilter pinch2 = new PinchFilter();
		pinch2.setAmount(-.9f);
		pinch2.setRadius(70);
		pinch2.setAngle((float) (Math.PI/16*Math.random()));
		pinch2.setCentreX(0.3f);
		pinch2.setCentreY(1.01f);
		pinch2.setEdgeAction(ImageFunction2D.CLAMP);

		PinchFilter pinch3 = new PinchFilter();
		pinch3.setAmount(-.9f);
		pinch3.setRadius(70);
		pinch3.setAngle((float) (Math.PI/32*Math.random()));
		pinch3.setCentreX(0.8f);
		pinch3.setCentreY(-0.01f);
		pinch3.setEdgeAction(ImageFunction2D.CLAMP);



		ImageDeformation textDef[] =  new ImageDeformation[]{
				new ImageDeformationByBufferedImageOp(pinch),
				new ImageDeformationByBufferedImageOp(pinch2),
				new ImageDeformationByBufferedImageOp(pinch3),
		};

		//word generator
		WordGenerator dictionnaryWords = new com.octo.captcha.component.word.wordgenerator.RandomWordGenerator(
				"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		//wordtoimage components
		TextPaster randomPaster = new DecoratedRandomTextPaster(new Integer(6), new Integer(
				6), new SingleColorGenerator(Color.black)
		, new TextDecorator[]{new BaffleTextDecorator(1, Color.BLACK)}); //, new LineTextDecorator(1, Color.BLACK)});
		BackgroundGenerator back = new UniColorBackgroundGenerator(
				new Integer(200), new Integer(100), Color.white);

		FontGenerator shearedFont = new RandomFontGenerator(50,
				50,
				new Font[]{
				new Font("nyala",Font.BOLD, 50),
				new Font("Caslon",Font.BOLD, 50),
				new Font("Credit valley",  Font.BOLD, 50)
		});

		//FontGenerator shearedFont = new RandomFontGenerator(new Integer(30),
		//		new Integer(35));
		//word2image 1
		WordToImage word2image;
		word2image = new DeformedComposedWordToImage(shearedFont, back, randomPaster,
				new ImageDeformation[]{},
				new ImageDeformation[]{},
				textDef
				);


		this.addFactory(
				new com.octo.captcha.image.gimpy.GimpyFactory(dictionnaryWords,
						word2image));

	}
}
