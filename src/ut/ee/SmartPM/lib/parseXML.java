package ut.ee.SmartPM.lib;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.os.AsyncTask;
import android.util.Log;

public class parseXML extends AsyncTask<String, Void, Void> {
	
	List<rulesObject<Double, Double, String>> volList = new ArrayList<rulesObject<Double, Double, String>>();
	String sensor_type = "";

	public parseXML(List<rulesObject<Double, Double, String>> volList, String sensor_type) {
		this.volList = volList;
		this.sensor_type = sensor_type;
	}
	
	@Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
	protected void onPostExecute(Void result) {
    }

	@Override
	protected Void doInBackground(String... params) {
		try {
            URL url = new URL(params[0]);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            
            NodeList lib = doc.getElementsByTagName("lib");
            Node libnode = lib.item(0);
            NamedNodeMap peetrus = libnode.getAttributes();
            sensor_type = peetrus.getNamedItem("keyword").getNodeValue();
            Log.d("SensorTypeParseXML", sensor_type);
            volList.add(new rulesObject<Double, Double, String>(0.0, 0.0, sensor_type));

            NodeList nodeList = doc.getElementsByTagName("data_value");

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);
                NamedNodeMap attr = node.getAttributes();
                Double low = Double.parseDouble(attr.getNamedItem("low").getNodeValue());
                Double high = Double.parseDouble(attr.getNamedItem("high").getNodeValue());
                String name = attr.getNamedItem("value").getNodeValue();
    	    	volList.add(new rulesObject<Double, Double, String>(low, high, name));


	        }
	        } catch (Exception e) {
	            Log.e("ParseXML", "XML Pasing Excpetion = " + e);
	        }
		return null;
	}

}
