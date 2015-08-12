package td.tools;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Vector;

public class MacAddressUtil
{
	private Vector<String> mMacAddress = new Vector<String>();

	public Vector<String> getMacAddress(){
		return mMacAddress;
	}

	public boolean isVali(){
		return !mMacAddress.isEmpty();
	}

	public void getSystemAllMacAddress(){
		try{
			Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
			while (enumeration.hasMoreElements()){
				NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();
				byte[] mac = networkInterface.getHardwareAddress();
				if (mac == null || mac.length == 0) continue;
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mac.length; i++){
					String hex = Integer.toHexString(mac[i] < 0 ? (mac[i] + 256) : mac[i]).toUpperCase();
					if (hex.equals("0")) hex = hex.replace("0", "00");
					sb.append(hex);
				}
				mMacAddress.add(sb.toString());
			}
		}
		catch (SocketException e){
			e.printStackTrace();
		}
	}
}