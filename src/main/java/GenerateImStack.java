import java.util.Random;

import cern.jet.random.Poisson;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.process.ShortProcessor;

/* Copyright 2017 Kristoffer Bernhem.
	 * This file is part of SMLMtestData.
	 *
	 *  SMLMtestData is free software: you can redistribute it and/or modify
	 *  it under the terms of the GNU General Public License as published by
	 *  the Free Software Foundation, either version 3 of the License, or
	 *  (at your option) any later version.
	 *
	 *  SMLMtestData is distributed in the hope that it will be useful,
	 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
	 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	 *  GNU General Public License for more details.
	 *
	 *  You should have received a copy of the GNU General Public License
	 *  along with SMLMtestData.  If not, see <http://www.gnu.org/licenses/>.
	 */
	/**
	 *
	 * @author kristoffer.bernhem@gmail.com
	 */
public class GenerateImStack {

	public static void main(String[] args) {
		// make default values, plugin will allow user to set these.
		int nFrame 				= 1000; // number of frames to generate.
		boolean staticObject 	= false; // if a static element should be included.
		int frameSize 			= 32; // pixel width.
		int pixelSize 			= 100; // pixelsize in nm.
		int[] objectCenter 		= {1580,1610}; // center position of object in nm.
		int gWidth				= 150;
		double SNR				= 5; // signal to noise ratio.
		int photons 			= 100;
		ShortProcessor cleanFrame 		= makeClean(frameSize, pixelSize, objectCenter, gWidth);	
		
		

		int[] staticXY = {700,700}; // xy center for static object
		ShortProcessor staticIP = makeClean(frameSize, pixelSize, staticXY, gWidth); // get a frame containing the static object.

		
		Random r = new Random();
		
		ImageStack imstack = new ImageStack(frameSize,frameSize);
		
		double maxI = cleanFrame.getMax();	
		for (int i = 0; i < frameSize*frameSize;i++)
		{
			if (maxI < cleanFrame.get(i))
				maxI = cleanFrame.get(i);
		}		
		maxI /= SNR;
		MersenneTwister Rand= new MersenneTwister();
		Poisson PO = new Poisson(photons,Rand);
		boolean addParticle = true;
		for (int frame = 0; frame < nFrame; frame++)
		{
			ShortProcessor currFrameIP = new ShortProcessor(frameSize,frameSize);
			int idx = 0; 
			double factor = PO.nextDouble()/photons; // poisson distribution, 0.9-1.1 or 0.99-1.01 or 0.7-1.3
			
			if (addParticle)
			{
				while (idx < frameSize*frameSize)
				{
					int value = (int)(r.nextDouble()*maxI);
					if (value < 0)
						value = 0;		
					currFrameIP.set(idx, (int)(factor*cleanFrame.get(idx))+value);
					idx++;
				}
			}else
			{
				while (idx < frameSize*frameSize)
				{
					int value = (int)(r.nextDouble()*maxI);
					if (value < 0)
						value = 0;		
					currFrameIP.set(idx, value);
					idx++;
				}
			}
			if (staticObject) // If a static object should be added.
			{				
				factor = PO.nextDouble()/photons; // poisson distribution, 0.9-1.1 or 0.99-1.01 or 0.7-1.3
				idx = 0; 
				while (idx < frameSize*frameSize)
				{
					currFrameIP.set(idx, (int)(currFrameIP.get(idx) + factor*staticIP.get(idx)));
					idx++;
					
					
				}
			}
			imstack.addSlice(currFrameIP);

		/*	if (Rand.nextDouble()> 0.98)
			{
				addParticle = true;
			}
			else
				addParticle = false;*/
		}
		
		/*
		 * create test frame.
		 */
		ImagePlus Image = ij.IJ.createHyperStack("Clean", 
				frameSize, 
				frameSize, 
				1, 
				1, 
				nFrame, 
				16);
		Image.setStack(imstack);		
		Image.setDisplayRange(0, cleanFrame.getMax());
		Image.updateAndDraw();
		Image.show();

	}
	
	public static ShortProcessor makeClean(int frameSize, int pixelSize, int[] objectCenter, int gWidth)
	{		
		ShortProcessor IP = new ShortProcessor(frameSize*pixelSize,frameSize*pixelSize);		
		IP.set(0);
		/*
		 * Assign values based on gWidth.
		 */
		
		double sigmaSquare = 2*gWidth*gWidth;

		for (int xi = 0; xi < frameSize*pixelSize; xi++)
		{
			for (int yi = 0; yi < frameSize*pixelSize; yi++)
			{
				int value =  (int) (1000 * Math.exp(-(
						(xi-objectCenter[0])*(xi-objectCenter[0])/sigmaSquare + 
						(yi-objectCenter[1])*(yi-objectCenter[1])/sigmaSquare
						)));
				
				IP.set(xi, yi, value);
			}
		}		
		ShortProcessor IP2 = (ShortProcessor) IP.bin(pixelSize);
		return IP2;
	}
}
