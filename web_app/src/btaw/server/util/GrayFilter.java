package btaw.server.util;
import java.awt.image.RGBImageFilter ;

public class GrayFilter extends RGBImageFilter {
    public double upper, lower ;

    final static int nlevels = 0xff ;
    int iupper, ilower ;
    double slope ;

    public GrayFilter( double upper, double lower ) {
	canFilterIndexColorModel = true ;
	this.upper = upper ;
	this.lower = lower ;

	iupper = (int)( 0.5 + upper * nlevels ) ;
	ilower = (int)( 0.5 + lower * nlevels ) ;

	iupper = (iupper >= nlevels) ? nlevels-1 : iupper ;
	ilower = (ilower < 0) ? 0 : ilower ;
	ilower = (ilower >= iupper) ? iupper-1 : ilower ;

	slope = (double)nlevels / (iupper - ilower) ;
	System.err.println( "GrayFilter: "+iupper+","+ilower+","+slope ) ;
    }

    int window( int val, int iu, int il, double slope ) {
	if( val > iu )		return nlevels ;
	else if( val <= il )	return 0 ;
	else			return (int)( 0.5 + slope * (val-il)) ;
    }

    public int filterRGB( int x, int y, int rgb ) {
	int a = (rgb & 0xff000000) ;
	int r = window( ((rgb >> 16) & 0xff), iupper, ilower, slope ) ;
	int g = window( ((rgb >> 8) & 0xff), iupper, ilower, slope ) ;
	int b = window( (rgb & 0xff), iupper, ilower, slope ) ;

	return (a | (r << 16) | (g << 8) | b) ;
    }
}