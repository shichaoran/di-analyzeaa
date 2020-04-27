package com.vd.canary.data.repository.es.threedemo;

/**
 * @Author shichaoran
 * @Date 2020/4/24 9:58
 * @Version
 */

import com.vd.canary.data.repository.es.model.Goddess;
import com.vd.canary.data.util.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GoddessDao {
    //增加
    public void addGoddess(Goddess g) throws SQLException {
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        //sql
        String sql = "INSERT INTO datajoin(title, datee, heads, contents, modifiier_id,"+
                "modifier_name, gmt_create_time, gmt_modify_time, deleted, gmt_deleted_time,version,client_id)"
                +"values("+"?,?,?,?,?,?,?,CURRENT_DATE(),?,CURRENT_DATE(),?)";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        //传参
        ptmt.setLong(1, g.getId());
        ptmt.setString(2, g.getBread());
        ptmt.setString(3, g.getSpec());
        ptmt.setString(4, g.getMaterial());
        ptmt.setString(5, g.getPlace());
        ptmt.setString(6,g.getPrice());
        ptmt.setString(7,g.getRaise());
        ptmt.setString(8,g.getNote());

        //执行
        ptmt.execute();
    }

    public void updateGoddess(Goddess gg) throws SQLException {
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        //sql, 每行加空格
        String sql = "UPDATE imooc_goddess" +
                " set title=?, datee=?, times=?,heads=?, contents=?, modifiier_id=?,"+
                " modifier_name=?, update_date=CURRENT_DATE(), isdel=? "+
                " where id=?";
        //预编译
        PreparedStatement ptmt = conn.prepareStatement(sql); //预编译SQL，减少sql执行

        //传参
        ptmt.setLong(1, gg.getId());
        ptmt.setString(2, gg.getBread());
        ptmt.setString(3, gg.getSpec());
        ptmt.setString(4, gg.getMaterial());
        ptmt.setString(5, gg.getPlace());
        ptmt.setString(6,gg.getPrice());
        ptmt.setString(7,gg.getRaise());
        ptmt.setString(8,gg.getNote());
        //执行
        ptmt.execute();
    }

//    public void delGoddess() throws SQLException {
//        //获取连接
//        Connection conn = JDBCUtils.getConnection();
//        //sql, 每行加空格
//        String sql = "delete from imooc_goddess where id=?";
//        //预编译SQL，减少sql执行
//        PreparedStatement ptmt = conn.prepareStatement(sql);
//
//        //传参
//        ptmt.setInt(1, id);
//
//        //执行
//        ptmt.execute();
//    }

    public List<Goddess> query() throws SQLException {
        Connection conn = JDBCUtils.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT ID, age FROM spec");

        List<Goddess> gs = new ArrayList<Goddess>();
        Goddess g = null;
        while(rs.next()){
            g = new Goddess();
            g.setId(rs.getLong("id"));
            g.setSpec(rs.getString("Spec"));

            gs.add(g);
        }
        return gs;
    }

    public Goddess get(Goddess ggg) throws SQLException {
        Goddess g = null;
        //获取连接
        Connection conn = JDBCUtils.getConnection();
        //sql, 每行加空格
        String sql = "select * from  imooc_goddess where id=?";
        //预编译SQL，减少sql执行
        PreparedStatement ptmt = conn.prepareStatement(sql);
        //传参
        ptmt.setLong(1, ggg.getId());
        //执行
        ResultSet rs = ptmt.executeQuery();
        while(rs.next()){
            g = new Goddess();
            g.setId(rs.getLong("id"));
            g.setBread(rs.getString("Bread"));
            g.setSpec(rs.getString("Spec"));
            g.setMaterial(rs.getString("Material"));
            g.setPlace(rs.getString("Place"));
            g.setPrice(rs.getString("Price"));
            g.setRaise(rs.getString("Raise"));
            g.setNote(rs.getString("Note"));
        }
        return g;
    }
}
