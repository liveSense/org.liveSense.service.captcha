package org.liveSense.service.captcha;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.ArrayList;
import java.util.List;

import com.octo.captcha.component.image.deformation.ImageDeformation;

/**
 * @author mag
 * @Date 5 mars 2008
 */
public class ImageDeformationByBufferedImageOp implements ImageDeformation{

    private List<BufferedImageOp> ImageOperations = new ArrayList<BufferedImageOp>();

    public void setImageOperations(List<BufferedImageOp> imageOperations) {
        ImageOperations = imageOperations;
    }

    public ImageDeformationByBufferedImageOp(List<BufferedImageOp> imageOperations) {
        ImageOperations = imageOperations;
    }

    public ImageDeformationByBufferedImageOp(BufferedImageOp imageOperation) {
        ImageOperations.add(imageOperation);
    }

    @Override
	public BufferedImage deformImage(BufferedImage image) {
        for(BufferedImageOp operation:ImageOperations){
            image = operation.filter(image, null);
        }
        return image;
    }
}
