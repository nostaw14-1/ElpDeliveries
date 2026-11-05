package com.elpdeliveries.model;
import lombok.Data;

@Data
public class SettingsDto {
    private Integer id;
    private String vroomIp;
    private Double depotLat;
    private Double depotLong;
    private String awsBucket;
    private String awsAccessKey;
    private String awsSecretKey;
    private String sqlServer;
    private String sqlUsername;
    private String sqlPassword;
}

