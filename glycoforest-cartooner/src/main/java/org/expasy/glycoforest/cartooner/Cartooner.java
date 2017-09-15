package org.expasy.glycoforest.cartooner;

import org.eurocarbdb.application.glycanbuilder.*;
import parser.IupacParser;
import parser.IupacTree;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class Cartooner {

    private final MassOptions massOptions;

    public Cartooner() {

        massOptions = new MassOptions();
        massOptions.DERIVATIZATION = "Und";
        massOptions.REDUCING_END_TYPE = ResidueType.createFreeReducingEnd();
        massOptions.ION_CLOUD = new IonCloud();
        massOptions.NEUTRAL_EXCHANGES = new IonCloud();
    }



    public RenderedImage makeImage(String iupac, int width, int height){

        IupacParser iupacParser = new IupacParser(iupac);

        final String glycoct;
        try {

            final IupacTree tree = iupacParser.parse();
            iupacParser.getCtTree(tree);
            final String tmpGlycoct = iupacParser.getCtSequence();
            glycoct = tmpGlycoct.replace("RES\n1b:x-HEX-1:5", "RES\n1b:o-dgal-HEX-0:0|1:aldi");
        } catch (Exception e) {

            throw new IllegalStateException(e);
        }

        BuilderWorkspace workspace = new BuilderWorkspace(new GlycanRendererAWT());
        workspace.setNotation("cfg"); //cfg | cfgbw | uoxf | uoxfcol | text

        Glycan glycan;
        try {

            GlycoCTCondensedParser parser = new GlycoCTCondensedParser(false);
            glycan = parser.fromGlycoCTCondensed(glycoct, massOptions);
        } catch (Exception e) {

            return renderIupac(iupac, width, height);
        }

        GlycanRenderer renderer = workspace.getGlycanRenderer();

        final BufferedImage image = renderer.getImage(Collections.singletonList(glycan), false, false, true, 1, new PositionManager(), new BBoxManager());

        return fixSize(width, height, image);
    }

    private RenderedImage renderIupac(final String iupac, final int width, final int height) {

        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = bufferedImage.createGraphics();
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 0, width, height);

        g.setColor(Color.black);
        g.drawString(iupac, 10, height - 10);
        g.dispose();
        return bufferedImage;
    }

    private static RenderedImage fixSize(int targetWidth, int targetHeight, BufferedImage image) {

        BufferedImage fixed = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = fixed.createGraphics();
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0, 0, targetWidth, targetHeight);

        g.drawImage(image, targetWidth - image.getWidth(), (targetHeight - image.getHeight())/2, image.getWidth(), image.getHeight(), null);
        g.dispose();

        return fixed;
    }

    public static void main(String[] args) throws Exception {

        final RenderedImage image = new Cartooner().makeImage("Hex(b1-3)[S(6)Hex(b1-4)HexNAc(b1-6)]HexNAc", 342, 238);

        FileOutputStream stream = new FileOutputStream(new File("C:\\Users\\Oliver\\Documents\\tmp\\export\\test.png"));
        ImageIO.write(image, "png", stream);
        stream.close();
    }

}
