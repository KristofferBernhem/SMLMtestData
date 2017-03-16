
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


	import ij.IJ;
	import ij.plugin.PlugIn;

public class SMLMtestData_ implements PlugIn {

		public static void main(final String... args) throws Exception {
			// create the ImageJ application context with all available services				
			final ij.ImageJ ij = new ij.ImageJ();
			ij.setVisible(true);
			Class<?> clazz = SMLMtestData_.class;
			String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
			String pluginsDir = url.substring("file:".length(), url.length() - clazz.getName().length() - ".class".length());
			System.setProperty("plugins.dir", pluginsDir);				
			IJ.runPlugIn(clazz.getName(), "");
			}


		@Override
		public void run(String arg0) {
			String args[] = null;
			GenerateImStack.main(args); //call GUI.
			
		}

	}


