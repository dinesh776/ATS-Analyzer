package com.resume.analyzer.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "app_settings")
@Data
public class Settings {
    @Id
    private int id=1;

    @Column(name = "baseUrl")
    private String baseUrl;

    @Column(name = "apiKey")
    private String apiKey;

    @Column(name = "model")
    private String model;

    @Column(name = "samplingRate")
    private Double samplingRate;
}
