package com.Broadcom;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class Broadcom {

	public static void main(String[] args) {
		
		// Get Arguments
		float latitude = Float.parseFloat(args[0]);
		float longitude = Float.parseFloat(args[1]);
		
		// Make "point" request
		try
		{
			String pointResponse = null;
			
			pointResponse = getPoint(latitude, longitude);
			
			if(pointResponse != null)
			{
				JSONObject json = new JSONObject(pointResponse);
				
				getForecast(json.getJSONObject("properties").getString("gridId"),
						String.valueOf(json.getJSONObject("properties").getInt("gridX")),
						String.valueOf(json.getJSONObject("properties").getInt("gridY")));
			}
			else
			{
				System.out.println("ERROR :: NO RESPONSE DATA");
			}
			
		}
		catch(IOException e)
		{
			System.out.println("ERROR :: ISSUE WITH GET REQUEST");
		}
	}
	
	private static String getPoint(float latitude, float longitude) throws IOException
	{
		String line = null;
		StringBuffer response = new StringBuffer();
		BufferedReader buffer = null;
		InputStreamReader reader = null;
		URL url = new URL("https://api.weather.gov/points/" + latitude + "," + longitude);
		HttpURLConnection service = (HttpURLConnection) url.openConnection();
		
		service.setRequestMethod("GET");
		reader = new InputStreamReader(service.getInputStream());
		
		if(service.getResponseCode() == 200)
		{
			buffer = new BufferedReader(reader);
			while ((line = buffer.readLine()) != null) {
	               response.append(line);
	        }
			
			buffer.close();
			return response.toString();
		}
		
		return "null";
	}

	private static void getForecast(String gridId, String gridX, String gridY) throws IOException
	{
		InputStreamReader reader = null;
		BufferedReader buffer = null;
		String line = null;
		StringBuffer response = new StringBuffer();
		
		URL url = new URL("https://api.weather.gov/gridpoints/" + gridId + "/" + gridX + "," + gridY + "/forecast");
		HttpURLConnection service = (HttpURLConnection) url.openConnection();
		
		service.setRequestMethod("GET");
		reader = new InputStreamReader(service.getInputStream());
		if(service.getResponseCode() == 200)
		{
			buffer = new BufferedReader(reader);
			while ((line = buffer.readLine()) != null) {
				response.append(line);
	        }
			
			JSONObject json = new JSONObject(response.toString());
			
			for(Object forecast : json.getJSONObject("properties").getJSONArray("periods"))
			{
				JSONObject obj = (JSONObject)forecast;
				
				if(obj.getInt("number") == 1)
				{
					System.out.println("Todays forecast is " + obj.getString("detailedForecast"));
					break;
				}
			}
			
			buffer.close();
		}
	}
}
