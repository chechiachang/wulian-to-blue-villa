/*
 * 連線及控制設備主程式
 * 主要作接收及傳送
 */
package td.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;

import cc.wulian.ihome.wan.json.*;
//import td.callback.HandleCallBack;
import td.tools.HeartTask;
import td.tools.MacAddressUtil;
//import td.ui.JListModel;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import cc.wulian.ihome.wan.MessageCallback;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.entity.MonitorInfo;
import cc.wulian.ihome.wan.entity.RegisterInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.json.JSONArray;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

/**
 *
 * @author OniYY
 */
public class Transervlet extends HttpServlet {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/bluevilla?connectTimeout=3000";
    //  Database credentials
    static final String USER = "wulian";
    static final String PASS = "wulian";
    private static final long serialVersionUID = 1L;
    ScheduledExecutorService scheduleService = new ScheduledThreadPoolExecutor(2);
    Runnable heartRunnable = null;
    ScheduledFuture<?> future;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws SQLException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            String strCmd = request.getParameter("cmd");//欲控制的功能

            /*
             * 此段是以servlet做訊息交換，相關接值的命令與之後的動作，可以自訂
             */
            //↓↓↓↓連線到GateWay,cmd=connect↓↓↓↓
            if (strCmd.equals("connect")) {
                JSONObject opts = new JSONObject();//使用 json 物件
                this.init();// API 設備初始化
                //↓↓↓↓註冊 GateWay ↓↓↓↓
                RegisterInfo info = new RegisterInfo("2234567890456799");
                info.setSimId("aaa");
                info.setSimSerialNo("ddddddd");

                //↓↓↓↓取得由前端接到的設備 ID 與欲連接之設備密碼(MD5加密)↓↓↓↓
                NetSDK.connect(request.getParameter("strGwID"), MD5Util.encrypt(request.getParameter("strPwd")), info);

                //↓↓↓↓開始執行資料庫相關動作↓↓↓↓
                stmt = conn.createStatement();
                String strSql = "truncate table devices";
                stmt.executeUpdate(strSql);

                if (NetSDK.isConnected(request.getParameter("strGwID"))) {
                    System.out.println("========設備連線========");
                    opts.put("result", "1");
                    opts.toString();
                    out.print(opts.toString());
                } else {
                    opts.put("result", "0");
                    out.print(opts.toString());
                }
                //↓↓↓↓控制設備命令,cmd=control↓↓↓↓
                //} else if (strCmd.equals("control") && NetSDK.isConnected(request.getParameter("strGwID"))) {
            } else if (strCmd.equals("control")) {
                System.out.println("========控制設備========");
                NetSDK.sendControlDevMsg(request.getParameter("strGwID"), request.getParameter("strDevID"), "14", request.getParameter("strDevType"), request.getParameter("strCtrlData"));
                //↓↓↓↓離線命令,cmd=disconnect，並將結果以JSON格式傳到前端{result,0}↓↓↓↓
            } else if (strCmd.equals("disconnect")) {
                JSONObject opts = new JSONObject();
                System.out.println("========與設備斷線========" + request.getParameter("strGwID"));
                NetSDK.disconnect(request.getParameter("strGwID"));
                if (!NetSDK.isConnected(request.getParameter("strGwID"))) {
                    System.out.println("========設備離線========");
                    opts.put("result", "0");
                    opts.toString();
                    out.print(opts.toString());
                }
                //↓↓↓↓傳送所有上線中設備到前端,cmd=gatalldevices↓↓↓↓
            } else if (strCmd.equals("getalldevices")) {
                JSONObject opts = new JSONObject();
                stmt = conn.createStatement();
                String strSql = "select * from devices";
                rs = stmt.executeQuery(strSql);
                //↓↓↓↓將設備資料撈出，組成 Json 格式後送到前端↓↓↓↓
                while (rs.next()) {
                    opts.put("id,devID,devInfo,devDataText,epType,devStatus,openCtrlData,closeCtrlData,stopCtrlData,uTime", rs.getString(1) + rs.getString(2) + rs.getString(3) + rs.getString(4) + rs.getString(5) + rs.getString(6) + rs.getString(7) + rs.getString(8) + rs.getString(9) + rs.getString(10));
                    opts.toString();
                    out.print(opts.toString());
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException se2) {
                se2.printStackTrace();
            }// nothing we can do
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }//end finally try
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws SQLException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws SQLException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    public void init() {
        NetSDK.init(new MessageCallback() {

            @Override
            public void ConnectServer(int result) {
                if (result != ResultUtil.RESULT_SUCCESS) {
                    System.out.println("與伺服器連線失敗");
                    TaskExecutor.getInstance().removeScheduled(heartRunnable);
                }

            }

            @Override
            public void ConnectGateway(int result, final String gwID, GatewayInfo gwInfo) {
                if (result == 0) {
                    if (heartRunnable == null) {
                        heartRunnable = new Runnable() {

                            @Override
                            public void run() {
                                NetSDK.sendHeartMsg(gwID);

                            }
                        };
                    }
                    TaskExecutor.getInstance().addScheduled(heartRunnable, 0, 60 * 1000, TimeUnit.MILLISECONDS);
                    NetSDK.sendRefreshDevListMsg(gwID, null);
                    System.out.println("連線GateWay成功" + ",gwID=" + gwID);
                } else {
                    System.out.println("連線GateWay失敗");

                }

            }

            @Override
            public void SetTimerSceneInfo(String gwID, String mode, String groupID,
                    String groupName, String status, JSONArray data) {
            }

            @Override
            public void SetTaskInfo(String gwID, String version, String sceneID,
                    String devID, String type, String ep, String epType,
                    Set<TaskInfo> taskInfoSet) {
                System.out.println("setTaskInfo");
            }

            @Override
            public void SetSceneInfo(String mode, SceneInfo sceneInfo) {
                System.out.println("SetSceneInfo");
            }

            @Override
            public void SetRoomInfo(String mode, RoomInfo roomInfo) {
                System.out.println("SetRoomInfo");
            }

            @Override
            public void SetMonitorInfo(MonitorInfo monitorInfo) {
                System.out.println("MonitorInfo");
            }

            @Override
            public void SetDeviceInfo(String mode, DeviceInfo devInfo, DeviceEPInfo devEPInfo) {
                System.out.println("SetDeviceInfo: " + mode + " " + devInfo + " " + devEPInfo);
            }

            @Override
            public void SetDeviceIRInfo(String gwID, String mode, String devID,
                    String ep, String irType, Set<DeviceIRInfo> devIRInfoSet) {
                System.out.println("SetDeviceIRInfo");
            }

            @Override
            public void ReportTimerSceneInfo(String gwID, JSONArray data) {
                System.out.println("ReportTimeSceneInfo");
            }

            @Override
            public void GetTimerSceneInfo(String gwID, JSONArray data) {
                System.out.println("GetTimerSceneInfo");
            }

            @Override
            public void GetTaskInfo(String gwID, String version, String sceneID,
                    Set<TaskInfo> taskInfoSet) {
                System.out.println("GetTaskInfo");
            }

            @Override
            public void GetSceneInfo(String gwID, Set<SceneInfo> sceneInfoSet) {
                System.out.println("GetSceneInfo");
            }

            @Override
            public void GetRoomInfo(String gwID, Set<RoomInfo> roomInfoSet) {
                System.out.println("GetRoomInfo");
            }

            @Override
            public void GetMonitorInfo(String gwID, Set<MonitorInfo> monitorInfoSet) {
                System.out.println("GetMonitorInfo");
            }

            @Override
            public void GetDeviceIRInfo(String gwID, String devID, String ep,
                    String mode, Set<DeviceIRInfo> devIRInfoSet) {
                System.out.println("GetDeviceIRInfo");
            }

            @Override
            public void SetBindSceneInfo(String gwID, String mode, String devID,
                    JSONArray data) {
                System.out.println("SetBindSceneInfo");
            }

            @Override
            public void SetBindDevInfo(String gwID, String mode, String devID,
                    JSONArray data) {
                System.out.println("SetBindDevInfo");
            }

            @Override
            public void QueryDevRssiInfo(String gwID, String devID, String data) {
                System.out.println("QueryDevRssiInfo");
            }

            @Override
            public void QueryDevRelaInfo(String gwID, String devID, String data) {
                System.out.println("QueryDevRelaInfo");
            }

            @Override
            public void PermitDevJoin(String gwID, String devID, String data) {
                System.out.println("PermitDevJoin");
            }

            @Override
            public void GetBindSceneInfo(String gwID, String devID, JSONArray data) {
                System.out.println("GetBindSceneInfo");
            }

            @Override
            public void GetBindDevInfo(String gwID, String devID, JSONArray data) {
                System.out.println("GetBindDevInfo");
            }

            @Override
            public void DeviceHardData(String gwID, String devID, String devType,
                    String data) {
                System.out.println("DeviceHardData");
            }

            @Override
            public void GetDevRecordInfo(String gwID, String mode, String count,
                    JSONArray data) {
                System.out.println("GetDevRecordInfo");
            }

            @Override
            public void GetDevAlarmNum(String gwID, String userID, String devID,
                    String data) {
                System.out.println("GetDevAlarmNum");
            }

            @Override
            public void PushUserChatSome(String gwID, String userType, String userID,
                    String from, String alias, String to, String time, String data) {
                System.out.println("PushUserChatSome");
            }

            @Override
            public void PushUserChatMsg(String gwID, String userType, String userID,
                    String from, String alias, String time, String data) {
                System.out.println("PushUserChatMsg");
            }

            @Override
            public void PushUserChatAll(String gwID, String userType, String userID,
                    String from, String alias, String time, String data) {
                System.out.println("PushUserChatAll");
            }

            @Override
            public void GatewayDown(String gwID) {
                System.out.println("GatewayDown");
            }

            @Override
            public void GatewayData(int result, String gwID) {
                System.out.println("GatewayData");
            }

            @Override
            public void DisConnectGateway(int result, String gwID) {
                System.out.println("DisConnectGateway");
            }

            @Override
            public void DeviceUp(DeviceInfo devInfo, Set<DeviceEPInfo> devEPInfoSet, boolean isFirst) {
                String devID = devInfo.getDevID();
                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    java.util.Date now = new java.util.Date();
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    String strDelSql = "delete from devices where devID = '" + devID + "'";

                    //origin code from JiaHuang
                    //String strInsSql = "insert into devices(devID,devInfo,uTime) values('" + devID + "','" + devInfo.getData().toString() + "','" + (int) now.getTime() / 1000 + "')";
                    //parse devInfo().getData into detail data
                    String json = devInfo.getData().toString();
                    String jsonElement = json.substring(1, json.length() - 1);
                    JSONObject jsonObject = new JSONObject(jsonElement);

                    String strInsSql = "INSERT INTO `devices` SET "
                            + "devID = '" + devID + "', "
                            + "devDataText = '" + jsonObject.get("epData").toString() + "', "
                            //0330 test need epType 
                            + "epType = '" + jsonObject.get("epType").toString() + "', "
                            //+ devInfo.getData().toString() + "', "
                            + "devInfo = '" + devInfo.getData().toString() + "', "
                            //+ devInfo.getDevEPInfo().getEpData() + "', "
                            + "uTime = '" + (int) now.getTime() / 1000 + "'";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(strDelSql);
                    stmt.executeUpdate(strInsSql);
                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } catch (JSONException ex) {
                    java.util.logging.Logger.getLogger(Transervlet.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (SQLException se2) {
                        se2.printStackTrace();
                    }// nothing we can do
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }//end finally try
                }
                devInfo.setDevEPInfo((DeviceEPInfo) devEPInfoSet.toArray()[0]);
                System.out.println("dev up devID:" + devInfo.getDevID());
            }

            @Override
            public void DeviceDown(String gwID, String devID) {
                // TODO Auto-generated method stub
            }

            @Override
            public void DeviceData(String gwID, String devID, String devType,
                    DeviceEPInfo devEPInfo) {
                DeviceInfo devInfo = new DeviceInfo();
                JSONObject opts = new JSONObject();
                java.util.Date date = new java.util.Date();

                Connection conn = null;
                Statement stmt = null;
                ResultSet rs = null;

                try {
                    opts.put("date", date.toString());
                    opts.put("devID:", devID);
                    opts.put("gwID:", gwID);
                    opts.put("devType:", devType);
                    //devEPInfo is a jsonobject without toString() function
                    opts.put("DeviceInfo:", devEPInfo.getEpData());
                    opts.toString();
                    System.out.println("DeviceData_Json = " + opts.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                devInfo.setGwID(gwID);
                devInfo.setDevID(devID);
                devInfo.setDevEPInfo(devEPInfo);

                String boodevStatus = "1";

                try {
                    conn = DriverManager.getConnection(DB_URL, USER, PASS);
                    String strSql = "update devices set "
                            //
                            + "devDataText = '" + td.tools.DeviceTool.getDevDataText(devEPInfo.getEpType(), devEPInfo.getEpData(), devEPInfo.getEpStatus()) + "',"
                            //+ "epType = '" + devInfo.getType() + "',"
                            + "epType = '" + devType + "',"
                            + "devStatus = '" + boodevStatus + "' "
                            //+ "openCtrlData = '" + td.tools.DeviceTool.getOpenCtrlData(devType) + "',"
                            //+ "closeCtrlData = '" + td.tools.DeviceTool.getCloseCtrlData(devType) + "',"
                            //+ "stopCtrlData  = '" + td.tools.DeviceTool.getStopOrDelayCtrlData(devType) + "' "
                            + "where devID ='" + devID + "'";
                    stmt = conn.createStatement();
                    stmt.executeUpdate(strSql);
                    /*
                     //insert into  devices_data
                     String strDataSql = "INSERT INTO `devices_data` SET "
                     + "devID = '" + devID + "', "
                     + "devType = '" + devType + "', "
                     + "devDataText = '" + devEPInfo.getEpData() + "', "
                     + "devStatus = '" + boodevStatus + "', "
                     + "openCtrlData = '" + td.tools.DeviceTool.getOpenCtrlData(devType) + "', "
                     + "closeCtrlData = '" + td.tools.DeviceTool.getCloseCtrlData(devType) + "', "
                     + "stopCtrlData = '" + td.tools.DeviceTool.getStopOrDelayCtrlData(devType) + "'";
                     System.out.println("insert into devices_data : " + devID);
                     stmt.executeUpdate(strDataSql);
                     */
                    /*cause NullPointException by unknown reason
                     //insert into devices_info
                     String strDevInfoSql = "INSERT INTO `devices_info` SET "
                     + "gwID = '" + gwID + "', "
                     + "devID = '" + devID + "', "
                     + "type = '" + devType + "', "
                     //+ "category = '" + devInfo.getCategory() + "', "
                     //+ "name = '" + devInfo.getName() + "', "
                     //+ "roomID = '" + devInfo.getRoomID() + "', "
                     //
                     //+ "data = '" + devInfo.toString() + "', " //cause NullPointException by unknown reason
                     + "data = '" + devInfo.getData().toString() + "', "
                     //the following line WONT WORK
                     //+ "data = '" + td.tools.DeviceTool.getDevDataText(devEPInfo.getEpType(), devEPInfo.getEpData(), devEPInfo.getEpStatus()) + "', "
                     //+ "devEpInfo = '" + devEPInfo.toString() + "', "
                     + "devEpInfo = '" + devEPInfo.getEpData() + "', "
                     //Map<String, DeviceEpInfo> DeviceEPInfoMap
                     //+ "deviceEpInfoMap = '" + devInfo.getDeviceEPInfoMap().toString() + "', "
                     + "ep = '" + devEPInfo.getEp() + "', "
                     + "epType = '" + devEPInfo.getEpType() + "', "
                     + "epName = '" + devEPInfo.getEpName() + "', "
                     //what we need
                     + "epData = '" + devEPInfo.getEpData() + "', "
                     + "epStatus = '" + devEPInfo.getEpStatus() + "', "
                     + "epClst = '" + devEPInfo.getEpClst() + "', "
                     + "epAttr = '" + devEPInfo.getEpAttr() + "', "
                     + "time = '" + devEPInfo.getTime() + "'";
                     System.out.println("insert into devices_info : " + devID);
                     stmt.executeUpdate(strDevInfoSql);
                     */
                    stmt.close();
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                    } catch (SQLException se2) {
                        se2.printStackTrace();
                    }// nothing we can do
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }//end finally try
                }
                System.out.println("dev Data  epData:" + devEPInfo.getEpData());
            }

            @Override
            public void HandleException(Exception e) {
                Logger.debug(e.getMessage());
            }

            @Override
            public void SetCombindDevInfo(String gwID, String mode,
                    String bindID, String name, String roomID,
                    String devIDLeft, String devIDRight) {
                System.out.println("SetCombindDevInfo");
            }

            @Override
            public void GetCombindDevInfo(String gwID,
                    JSONArray data) {
                System.out.println("GetCombindDevInfo");
            }

            @Override
            public void readOfflineDevices(String gwID, String status) {
                System.out.println("readOfflineDevices");
            }

            @Override
            public void setGatewayInfo(String gwID, String mode, String gwVer,
                    String gwName, String gwLocation, String gwPath) {
                System.out.println("setGatewayInfo");
            }

            @Override
            public void reqeustOrSetTwoStateConfigration(String mode,
                    String gwID, String devID, String ep, JSONArray data) {
                System.out.println("requestOrSetTwoStateConfiguration");
            }

            @Override
            public void sendControlGroupDevices(String gwID, String group,
                    String mode, String data) {
                System.out.println("sendControlGroupDevices");
            }

            @Override
            public void offlineDevicesBack(DeviceInfo devcieInfo,
                    Set<DeviceEPInfo> deviceEpInfoSet) {
                System.out.println("offlineDevicesBack");
            }
        });
    }
}
