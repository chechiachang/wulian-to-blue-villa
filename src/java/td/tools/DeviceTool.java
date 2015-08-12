package td.tools;

import java.io.IOException;
import java.util.Properties;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

public class DeviceTool
{
	static Properties mProperties;
	static{
		mProperties = new Properties();
		try{
			mProperties.load(DeviceTool.class.getClassLoader().getResourceAsStream("devices.properties"));
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}

	public static boolean isAlarmDevByType( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_WARNING.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_MOTION.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_CONTACT.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_EMERGENCY.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_MOTION_F.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_FIRE.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_NH3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_GAS.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE.equals(devType)){
			return true;
		}
		else{
			return false;
		}
	}

	public static boolean isMutiCtrlDevByType( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_BUTTON_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_BUTTON_3.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_BUTTON_4.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_CONTROL_3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_LIGHT_2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_LIGHT_4.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_DOCK_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_DOCK_3.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
			return true;
		}
		else{
			return false;
		}
	}

	public static boolean isBindSceneByType( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_TOUCH_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_TOUCH_3.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_TOUCH_4.equals(devType)){
			return true;
		}
		else{
			return false;
		}
	}

	public static boolean isSensorDevByType( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT_S.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_TEMHUM.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_FLOW.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CTHV.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CO2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_VOC.equals(devType)){
			return true;
		}
		else{
			return false;
		}
	}

	public static boolean isNoQuickCtrlDevByType( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_TOUCH_2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_TOUCH_3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_TOUCH_4.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_SCALE.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CARPARK.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_BUTTON_2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_BUTTON_3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_EXTENDER.equals(devType) || isSensorDevByType(devType)){
			return true;
		}
		else{
			return false;
		}
	}

	public static String getDevDefaultNameByType( String devType ){
		if (StringUtil.isNullOrEmpty(devType)) {
			return "unknow";
		} else {
			return mProperties.getProperty(devType, "unknow");
		}
	}

	static String getResString( String key ){
		if (StringUtil.isNullOrEmpty(key)) {
			return null;
		} else {
			return mProperties.getProperty(key);
		}
	}

	public static boolean getSendCtrlStatusByByTypeAndData( String epData, String devType, String epStatus, boolean isCtrl ){
		if (isAlarmDevByType(devType)){
			if (ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE.equals(devType)){
				if (isCtrl){
					if (epData.endsWith("1")){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					if (epData.endsWith("1")){
						return true;
					}
					else{
						if (epData.startsWith("1")){
							return true;
						}
						else{
							return false;
						}
					}
				}
			}
			else{
				if (isCtrl){
					if (!StringUtil.isNullOrEmpty(epStatus) && epStatus.startsWith("1")){
						return true;
					}
					else{
						return false;
					}
				}
				else{
					if (epData.endsWith("1")){
						return true;
					}
					else{
						if (!StringUtil.isNullOrEmpty(epStatus) && epStatus.startsWith("0")){
							return true;
						}
						else{
							return false;
						}
					}
				}
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE.equals(devType)){
			if (epData.endsWith("1")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT.equals(devType)){
			if (epData.startsWith("1")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT.equals(devType)){
			if ("0".equals(epData)){
				return false;
			}
			else{
				return true;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
			if ("000".equals(epData)){
				return false;
			}
			else{
				return true;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_EMS.equals(devType)){
			if (epData.startsWith("0101")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK.equals(devType)){
			if (epData.startsWith("1")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL.equals(devType)){
			if ("2".equals(epData) || "4".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BARRIER.equals(devType)){
			if ("2".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE.equals(devType)){
			boolean normal = "11".equals(epData);
			boolean timer = epData.startsWith("2");
			if (normal || timer){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK_1.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK_2.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK_3.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_1.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BUTTON_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_2.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_2.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BUTTON_3.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_3.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_3.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BUTTON_4.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_LIGHT_4.equals(devType)){
			if ("1".equals(epData)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SHADE.equals(devType)){
			if (epData.startsWith("2")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BLIND.equals(devType)){
			if (epData.startsWith("2")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK.equals(devType)){
			if (epData.length() < 5){ return false; }
			if (epData.startsWith("3") || epData.startsWith("1")){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2.equals(devType)){
			if (epData.length() < 5){ return false; }
			String first = epData.substring(0, 1);
			if ("1".equals(first)){
				return true;
			}
			else{
				return false;
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3.equals(devType)){
			if (epData.startsWith("1")){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	public static String getDevDataText( String epType, String epData, String epStatus ){
		String first;
		String second;
		String third;
		StringBuffer sb = new StringBuffer();
		if (StringUtil.isNullOrEmpty(epData)){ return ""; }
		if (isAlarmDevByType(epType)){
			if (ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE.equals(epType)){
				// alarm
				sb.append(epData.startsWith("1") ? getResString("device_baojing") : getResString("device_daijing"));
				sb.append("\t");
				sb.append(epData.endsWith("0") ? getResString("device_guan") : getResString("device_ka"));
			}
			else{
				sb.append(epData.startsWith("0") ? getResString("device_daijing") : getResString("device_baojing"));
				sb.append("\t");
				sb.append("0".equals(epStatus) ? getResString("device_cafang") : getResString("device_bufang"));
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT.equals(epType)){
			sb.append("0".equals(epData) ? getResString("device_guan") : getResString("device_kai"));
		}
		// add d_light_2
		else if (ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT.equals(epType)){
			int epDataInt = StringUtil.toInteger(epData);
			sb.append(0 == epDataInt ? getResString("device_guan") : 100 == epDataInt ? getResString("device_kai") : epDataInt + "%");
		}
		// add ems
		else if (ConstUtil.DEV_TYPE_FROM_GW_EMS.equals(epType)){
			sb.append(epData.startsWith("0101") ? getResString("device_kai") : epData.startsWith("0100")
					? getResString("device_guan")
					: getResString("device_exception"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK.equals(epType)){
			sb.append(epData.startsWith("0") ? getResString("device_guan") : getResString("device_kai"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_TEMHUM.equals(epType)){
			if (epData.contains(",")){
				String[] temp = epData.split(",");
				sb.append(temp[0]).append("℃");
				sb.append(" ");
				sb.append(temp[1]).append("%RH");
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_CO2.equals(epType) || ConstUtil.DEV_TYPE_FROM_GW_VOC.equals(epType)){
			sb.append(epData).append("PPM");
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT_S.equals(epType)){
			sb.append(epData).append("LUX");
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL.equals(epType)){
			sb.append("2".equals(epData) ? getResString("device_kai") : "3".equals(epData) ? getResString("device_guan") : "4"
					.equals(epData) ? getResString("device_stop") : getResString("device_exception"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BARRIER.equals(epType)){
			sb.append("2".equals(epData) ? getResString("device_kai") : "3".equals(epData) ? getResString("device_guan") : "4"
					.equals(epData) ? getResString("device_stop") : getResString("device_exception"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE.equals(epType)){
			boolean normal = "11".equals(epData);
			boolean timer = epData.startsWith("2");
			sb.append(normal || timer ? getResString("device_kai") : getResString("device_guan"));
		}
		else if (isBindSceneByType(epType)){
			sb.append(" ");
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_FLOW.equals(epType)){
			sb.append(epData).append("M3");
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SCALE.equals(epType)){
			sb.append(epData).append("KG");
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_CARPARK.equals(epType)){
			if (epData.contains(",")){
				String[] temp = epData.split(",");
				first = temp[0];
				second = temp[1];

				sb.append(first)
						.append("MM")
						.append("\t")
						.append(
								"0".equals(second) ? getResString("device_park_no_obstacle") : "1".equals(second) ? getResString("device_park_has_obstacle") : getResString("device_exception"));
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOCK_1.equals(epType) || ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1.equals(epType)
				|| ConstUtil.DEV_TYPE_FROM_GW_CONTROL_1.equals(epType) || ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1.equals(epType) || isMutiCtrlDevByType(epType)){
			sb.append(epData.endsWith("0") ? getResString("device_guan") : epData.endsWith("1")
					? getResString("device_kai")
					: getResString("device_exception"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SHADE.equals(epType) || ConstUtil.DEV_TYPE_FROM_GW_BLIND.equals(epType)){
			sb.append("3".equals(epData) ? getResString("device_guan") : "2".equals(epData)
					? getResString("device_kai")
					: getResString("device_stop"));
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK.equals(epType) || ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3.equals(epType)){
			if (epData.length() >= 5){
				first = epData.substring(0, 1);
				second = epData.substring(1, 3);
				third = epData.substring(3, 5);

				sb.append("1".equals(first) ? getResString("device_jiesuo") : "2".equals(first) ? getResString("device_shangsuo") : "3"
						.equals(first) ? getResString("device_jiesuoyanshi") : getResString("device_exception"));
				sb.append(" ");

				sb.append("10".equals(second) ? getResString("device_yibaoxian") : "11".equals(second)
						? getResString("device_weibaoxian")
						: getResString("device_exception"));
				sb.append(" ");

				sb.append("21".equals(third) ? getResString("device_suoyikai") : "22".equals(third)
						? getResString("device_suoyiguan")
						: "23".equals(third) ? getResString("device_baojing") : getResString("device_exception"));
			}
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2.equals(epType)){
			if (epData.length() >= 5){
				first = epData.substring(0, 1);
				second = epData.substring(1, 3);
				third = epData.substring(3, 5);

				sb.append("1".equals(first) ? getResString("device_jiesuo") : "2".equals(first)
						? getResString("device_shangsuo")
						: getResString("device_exception"));
				sb.append(" ");

				sb.append("10".equals(second) ? getResString("device_yibaoxian") : "11".equals(second) ? getResString("device_weibaoxian") : "25".equals(second) ? getResString("device_qiangzhishangsuo") : "26".equals(second) ? getResString(
						"device_zidongshangsuo") : "30".equals(second) ? getResString("device_mimajiesuo") : "31".equals(second)
						? getResString("device_niukou") + "1" + getResString("device_jiesuo")
						: "32".equals(second) ? getResString("device_niukou") + "2" + getResString("device_jiesuo") : "33".equals(second)
								? getResString("device_niukou") + "3" + getResString("device_jiesuo")
								: "34".equals(second) ? getResString("device_niukou") + "4" + getResString("device_jiesuo") : getResString("device_exception"));
				sb.append(" ");

				sb.append("23".equals(third) ? getResString("device_ruqinbaojing") : "24".equals(third) ? getResString("device_jiechubaojing") : "29".equals(third)
						? getResString("device_pohuaibaojing")
						: getResString("device_exception"));
			}
		}
		return sb.toString();
	}

	public static String getDevCtrlDataByType( String devType, int ctrlType ){
		if (ctrlType == 0){
			return getOpenCtrlData(devType);
		}
		else if (ctrlType == 1){
			return getCloseCtrlData(devType);
		}
		else if (ctrlType == 2){
			return getStopOrDelayCtrlData(devType);
		}
		else{
			return null;
		}
	}

	public static String getOpenCtrlData( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_DOCK.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_DOCK_1.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_1.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1.equals(devType) || isMutiCtrlDevByType(devType)){
			if (ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
				return "100";
			}
			else{
				return "1";
			}
		}
		else if (isAlarmDevByType(devType)){
			return "1";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT.equals(devType)){
			return "100";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
			return "100";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_EMS.equals(devType)){
			return "11";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL.equals(devType)){
			return "2";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BARRIER.equals(devType)){
			return "2";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE.equals(devType)){
			return "11";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SHADE.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_BLIND.equals(devType)){
			return "2";
		}
		// DOORLOCK one use 3s for close door
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK.equals(devType)){
			return "3";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3.equals(devType)){
			return "1";
		}
		else{
			return null;
		}
	}

	public static String getCloseCtrlData( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_DOCK.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_DOCK_1.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_CONTROL_1.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1.equals(devType) || isMutiCtrlDevByType(devType)){
			if (ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
				return "000";
			}
			else{
				return "0";
			}
		}
		else if (isAlarmDevByType(devType)){
			return "0";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_LIGHT.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT.equals(devType)){
			return "0";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT.equals(devType)){
			return "000";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_EMS.equals(devType)){
			return "10";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL.equals(devType)){
			return "3";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_BARRIER.equals(devType)){
			return "3";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE.equals(devType)){
			return "10";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SHADE.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_BLIND.equals(devType)){
			return "3";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK.equals(devType)){
			// always send open 3s
			return "3";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3.equals(devType)){
			return "2";
		}
		else{
			return null;
		}
	}

	public static String getStopOrDelayCtrlData( String devType ){
		if (ConstUtil.DEV_TYPE_FROM_GW_BARRIER.equals(devType)){
			return "4";
		}
		else if (ConstUtil.DEV_TYPE_FROM_GW_SHADE.equals(devType) || ConstUtil.DEV_TYPE_FROM_GW_BLIND.equals(devType)){
			return "1";
		}
		else{
			return null;
		}
	}
	
	// called by MainFrame JList rigth click
	public static void controlDevice(DeviceInfo deviceInfo){
		String gwId = deviceInfo.getGwID();
		String devID = deviceInfo.getDevID();
		String devType = deviceInfo.getType();
		String data = deviceInfo.getDevEPInfo().getEpData();
		String epStatus = deviceInfo.getDevEPInfo().getEpStatus();

		if (DeviceTool.isNoQuickCtrlDevByType(devType)) return;
		boolean isOpened = DeviceTool.getSendCtrlStatusByByTypeAndData(data, devType, epStatus, true);
		int ctrlType = isOpened ? 1 : 0;
		if (ConstUtil.DEV_TYPE_FROM_GW_TOUCH_4.equals(devType)
				|| ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL.equals(devType)){
		}
		else if (DeviceTool.isAlarmDevByType(devType)){
			if (ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE.equals(devType)){
				System.out.println("NetSDK.sendControlDevMsg");// LOG
				NetSDK.sendControlDevMsg(gwId, devID, "14", devType, DeviceTool.getDevCtrlDataByType(devType, ctrlType));
			}
		}
		else{
			System.out.println("NetSDK.sendControlDevMsg");// LOG
			NetSDK.sendControlDevMsg(gwId, devID, "14", devType, DeviceTool.getDevCtrlDataByType(devType, ctrlType));
		}
	}
}
