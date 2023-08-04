package com.xmltools;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class ImageTypeIdentifier {
		public static void main(String[] args) {
				if (args.length != 1) {
						System.err.println("Usage: java ImageTypeIdentifier <folder_path>");
						return;
				}

				String folderPath = args[0];
				File folder = new File(folderPath);
				processFolder(folder);
		}

		public static void processFolder(File folder) {
				File[] files = folder.listFiles();
				if (files == null) {
						System.err.println("Invalid folder path.");
						return;
				}

				for (File file : files) {
						if (file.isDirectory()) {
								processFolder(file);
						} else if (file.isFile() && file.getName().endsWith(".xxx")) {
								String newExtension = getImageType(file);
								if (newExtension != null) {
										String newFileName = file.getName().replace(".xxx", "." + newExtension);
										File newFile = new File(file.getParent(), newFileName);

										if (file.renameTo(newFile)) {
												System.out.println("Renamed: " + file.getName() + " -> " + newFileName);
										} else {
												System.err.println("Failed to rename: " + file.getName());
										}
								}
						}
				}
		}

		public static String getImageType(File file) {
				try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
						Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
						if (readers.hasNext()) {
								ImageReader reader = readers.next();
								String formatName = reader.getFormatName().toLowerCase();

								reader.setInput(iis);
								int numImages = reader.getNumImages(true);

								if (formatName.equals("jpeg") || formatName.equals("jpg")) {
										return "jpg";
								} else if (formatName.equals("gif")) {
										return "gif";
								} else if (formatName.equals("bmp")) {
										return "bmp";
								} else if (formatName.equals("png")) {
										return "png";
								} else {
										System.err.println("Unknown image type: [" + file.getName() + "] Type name :" + formatName);
										return null;
								}
						} else if (getAIImageType(file)!=null) {
								return "ai";
						} else {
								System.err.println("No suitable ImageReader found for: " + file.getName());
								return null;
						}
				} catch (IOException e) {
						System.err.println("Error reading the file: " + file.getName());
						e.printStackTrace();
						return null;
				}
		}


		public static String getAIImageType(File file) {
				try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
						String firstLine = reader.readLine();
						if (firstLine != null && firstLine.matches("%!PS-Adobe-\\d\\.\\d EPSF-\\d\\.\\d")) {
								return "ai";
						}
				} catch (IOException e) {
						System.err.println("No ai header found for file: " + file.getName());
				}
				return null;
		}
}
