package net.termat.dem;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

public class TileUtil {
	public static String[] urls=new String[]{
			"https://cyberjapandata.gsi.go.jp/xyz/demgm_png",
			"https://cyberjapandata.gsi.go.jp/xyz/dem_png/",
			"https://cyberjapandata.gsi.go.jp/xyz/dem5a_png/",
			"https://cyberjapandata.gsi.go.jp/xyz/dem5b_png/",
			"https://cyberjapandata.gsi.go.jp/xyz/dem5c_png/"
	};
	private static final int P8=256;
	private static final int P16=65536;
	private static final int P23=8388608;
	private static final int P24=16777216;
	private static final double U=0.01;

	public static Tile getTile(int z,int x,int y) throws MalformedURLException, IOException{
		BufferedImage img=getGSIDem(z,x,y);
		img=g2mDem(img);
		Tile ret=new Tile();
		ret.z=z;
		ret.x=x;
		ret.y=y;
		ret.imageBytes=bi2Bytes(img);
		return ret;
	}



	public static BufferedImage getGSIDem(int z,int x,int y) throws MalformedURLException, IOException{
		if(z<=8){
			String url=urls[0]+Integer.toString(z)+"/"+Integer.toString(x)+"/"+Integer.toString(y)+".png";
			BufferedImage img=ImageIO.read(new URL(url));
			return img;
		}else if(z<=14){
			String url=urls[1]+Integer.toString(z)+"/"+Integer.toString(x)+"/"+Integer.toString(y)+".png";
			BufferedImage img=ImageIO.read(new URL(url));
			return img;
		}else{
			String url=urls[2]+Integer.toString(z)+"/"+Integer.toString(x)+"/"+Integer.toString(y)+".png";
			BufferedImage img=ImageIO.read(new URL(url));
			return img;
		}
	}

	public static BufferedImage g2mDem(BufferedImage img){
		int w=img.getWidth();
		int h=img.getHeight();
		BufferedImage ret=new BufferedImage(w,h,img.getType());
		for(int i=0;i<w;i++){
			for(int j=0;j<h;j++){
				double zz=getZ(img.getRGB(i, j));
				int rgb=transZ(zz);
				ret.setRGB(i, j, rgb);
			}
		}
		return ret;
	}

	public static double getZ(int rgb){
		Color c=new Color(rgb);
		int x=c.getRed()*P16+c.getGreen()*P8+c.getBlue();
		if(x<P23){
			return U*(double)x;
		}else if(x>P23){
			return U*(double)(x-P24);
		}else{
			return 0.0;
		}
	}

	public static int transZ(double h){
	    int rgb = (int)Math.floor((h + 10000)/0.1);
	    int r= (rgb & 0xff0000) >> 16;
		int g=(rgb & 0x00ff00) >> 8;
		int b=(rgb & 0x0000ff);
		return new Color(r,g,b).getRGB();
	}

	/*
	 * byte[] -> BufferedImage
	 */
	private static byte[] bi2Bytes(BufferedImage img)throws IOException{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write( img, "png", baos );
		baos.flush();
		return baos.toByteArray();
	}

}
