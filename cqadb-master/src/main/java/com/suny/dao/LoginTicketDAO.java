package com.suny.dao;

import com.suny.model.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * Created by admin on 23-2-1.10:43 am
 */
@Mapper
public interface LoginTicketDAO {

    String TABLE_NAME = "login_ticket";
    String INSERT_FIELDS = " user_id, expired, status, ticket";
    String SELECT_FIELDS = "id " + INSERT_FIELDS;


    @Insert({"insert into " + TABLE_NAME + "(" + INSERT_FIELDS + ") values(#{userId},#{expired},#{status},#{ticket})"})
    int addTicket(LoginTicket loginTicket);

    @Insert({"delete from " + TABLE_NAME + " where id=#{id}"})
    int deleteTicket(int id);


//    @Select({"select", SELECT_FIELDS + " from " + TABLE_NAME + " where ticket=#{ticket}"})
    LoginTicket selectByTicket(String ticket);

//    @Select({"select", SELECT_FIELDS + " from " + TABLE_NAME + " where user_id=#{userId}"})
    LoginTicket selectByUserId(int userId);


    @Update({"update", TABLE_NAME, "set status=#{status} where ticket=#{ticket}"})
    void updateStatus(@Param("ticket") String ticket, @Param("status") int status);

}


















