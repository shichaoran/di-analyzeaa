package com.vd.canary.data.service.es.threeService;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vd.canary.data.repository.es.threedemo.RemoteDataUtil;


import java.sql.*;
import java.util.List;

/**
 * @Author shichaoran
 * @Date 2020/4/24 11:02
 * @Version
 */
public class JDBCService {
    public static JSONArray getJSONArray(String url) throws Exception {

//        Goddess goddess = new Goddess();
        String aa = RemoteDataUtil.getData();
        if (aa != null) {
            JSONArray jsonArray = JSON.parseArray(aa);
            return jsonArray;
        }
        return null;
    }

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://192.168.5.20:3306/di_dataanalyze?characterEncoding=utf-8";
        String user = "root";
        String password = "123456@wj";
        PreparedStatement psql;
        int i = 0;
        try {
            String sql = "insert into di_dataanalyze.datejoin111"
                    + "(id,bread,spec,material,place,price,raise,avgpri10,avgpri30,note,rowId,unit,avgpri7,category,avgmonthpri,date,time )"
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            String[] ids = {"id"};

            Class.forName(driver);
            con = (Connection) DriverManager.getConnection(url, user, password);
            psql = con.prepareStatement(sql, ids);
            if (!con.isClosed())
                System.out.println("连接成功");

            // 得到json数据

            JSONObject jsonObject = new JSONObject();
            List<JSONObject> jsonObjects = JSONArray.parseArray(RemoteDataUtil.getData(), JSONObject.class);
            for (JSONObject object : jsonObjects) {
                String contents = object.get("contents").toString();
                String date = object.get("date").toString();
                String time = object.get("time").toString();
                List<JSONObject> jsonObjectss = JSONArray.parseArray(contents, JSONObject.class);
                for (JSONObject objectss : jsonObjectss) {
//                    ResultSet rs = psql.getGeneratedKeys();
//
//                        int anInt = rs.getInt(1);

                    String breads = objectss.get("breed").toString();
                    String spec = objectss.get("spec").toString();
                    String material = objectss.get("material").toString();
                    String place = objectss.get("place").toString();
                    String price = objectss.get("price").toString();
                    String raise = objectss.get("raise").toString();
                    String avgpri10 = objectss.get("avgpri10").toString();
                    String note = objectss.get("note").toString();
                    String avgpri30 = objectss.get("avgpri30").toString();
                    String rowId = objectss.get("rowId").toString();
                    String unit = objectss.get("unit").toString();
                    String avgpri7 = objectss.get("avgpri7").toString();
                    String category = objectss.get("category").toString();
                    String avgmonthpri = objectss.get("avgmonthpri").toString();
                    psql.setInt(1, 1111111);
                    psql.setString(2, breads);
                    psql.setString(3, spec);
                    psql.setString(4, material);
                    psql.setString(5, place);
                    psql.setString(6, price);
                    psql.setString(7, raise);
                    psql.setString(8, avgpri10);
                    psql.setString(9, avgpri30);
                    psql.setString(10, note);
                    psql.setString(11, rowId);
                    psql.setString(12, unit);
                    psql.setString(13, avgpri7);
                    psql.setString(14, category);
                    psql.setString(15, avgmonthpri);
                    psql.setString(16,date);
                    psql.setString(17,time);
                    psql.executeUpdate();
                    System.out.println(breads.toString());
                }
                System.out.println(contents.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            con.close();
            System.out.println("执行成功");
        }
    }
}