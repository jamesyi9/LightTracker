import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.rosuda.JRI.*;

public class ReadData
{
	public static List<double[]> RD(String fileName) throws Exception
	{
		List<double[]> output = new ArrayList<double[]>();
		Path filePath = Paths.get(fileName);
		BufferedReader fileReader = Files.newBufferedReader(filePath);
		List<String> inputStrings = new ArrayList<String>();
		double[] predictionData = new double[96];

		try
		{
			String fileLine = null;
			int days = 0;
			Scanner scannedLine;

			while ((fileLine = fileReader.readLine()) != null)
			{
				inputStrings.add(fileLine);
			}

			days = CheckNumOfDays(inputStrings.size());

			for (int i = 0; i < days; i++)
			{
				double[] dataPoints = new double[96];

				for (int j = 0; j < 97; j++)
				{
					if (j == 96)
					{
						continue;
					}
					else if (j != 96)
					{
						scannedLine = new Scanner(inputStrings.get(i * 97 + j));
						dataPoints[j] = Double.parseDouble(scannedLine.next()) / 1000;
					}
				}

				output.add(dataPoints);
			}

			String directory = System.getProperty("user.dir") + "/data/data.txt";
			directory = directory.replace("\\", "/");
			System.out.println(directory);
			Rengine re = new Rengine(null, false, null);
			System.out.println("Regine created, waiting for R");
			
			if (!re.waitForR())
			{
				System.out.println("Cannot load R");
			}

			re.eval("library(forecast);");
			re.eval("data<-scan("+"\""+directory+"\""+",skip=1);");
			re.eval("datats<-data");
			re.eval("arima<-auto.arima(datats);");
			re.eval("fcast<-forecast(arima,h=96);");

			REXP fs = re.eval("fcast$x");
			double[] forecast = fs.asDoubleArray();
			
			for (int i = 0; i < forecast.length; i++)
			{
				// System.out.println(forecast[i]);
			}

			re.end();
			predictionData = forecast;
			output.add(predictionData);
		}
		catch (IOException x)
		{
			throw new Exception("Could not open the input file.");
		}
		catch (Exception x)
		{
			throw new Exception(x.getMessage());
		}
		finally
		{
			try
			{
				if (fileReader != null)
				{
					fileReader.close();
				}
			}
			catch (IOException x)
			{
				throw new Exception("Could not close the input file.");
			}
		}

		return output;
	}

	private static int CheckNumOfDays(int size) throws Exception
	{
		int days = 0;

		if ((size + 1) % 97 != 0)
		{
			throw new Exception("Input file can not be used.");
		}
		else if ((size + 1) % 97 == 0)
		{
			days = (size + 1) / 97;
		}

		return days;
	}
}
