import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadData
{
	public static List<double[]> RD(String fileName) throws Exception
	{
		List<double[]> output = new ArrayList<double[]>();
		Path filePath = Paths.get(fileName);
		BufferedReader fileReader = Files.newBufferedReader(filePath);
		List<String> inputStrings = new ArrayList<String>();

		double[] arimaInput = new double[96];
		int[] arimaOutput;
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
						dataPoints[j] = Double.parseDouble(scannedLine.next());
					}
				}

				output.add(dataPoints);
			}

			// These for-loops average the current days for ARIMA
			// This is done because ARIMA can only take in one dataset
			for (int x = 0; x < 96; x++)
			{
				arimaInput[x] = 0;

				for (int y = 0; y < days; y++)
				{
					arimaInput[x] += output.get(y)[x];
				}

				arimaInput[x] = arimaInput[x] / days;
			}

			// TODO: Fix Arima
			ARIMA prediction = new ARIMA(arimaInput);
			arimaOutput = prediction.getARIMAmodel(); 

			for (int c = 0; c < 96; c++)
			{
				predictionData[c] = (double)arimaOutput[c];
			}

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
