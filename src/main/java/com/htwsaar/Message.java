package com.htwsaar;


import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Christopher Jung
 */
public class Message
{
    public int id;
    public int origin;
    public int destination;
    public String message;

    public Timestamp timestamp;

}
