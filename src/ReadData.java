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
