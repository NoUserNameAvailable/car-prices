package com.nousernameavailable.carprices.carprices.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_log")
public class JobLog {

    @Id
    @Column(name = "job_log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String jobLogId;

    private String model;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private String status;

    @Column(name = "added_lines")
    private int addedLines;

    @Column(name = "modified_lines")
    private int modifiedLines;

    @Column(name = "deleted_lines")
    private int deletedLines;

    private String site;
    private String brand;
    private String url;

    public JobLog(String model, LocalDateTime startTime, LocalDateTime endTime, String status, int addedLines,
                  int modifiedLines, int deletedLines, String site, String brand, String url) {
        this.model = model;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.addedLines = addedLines;
        this.modifiedLines = modifiedLines;
        this.deletedLines = deletedLines;
        this.site = site;
        this.brand = brand;
        this.url = url;
    }

    public String getJobLogId() {
        return jobLogId;
    }

    public JobLog setJobLogId(String jobLogId) {
        this.jobLogId = jobLogId;
        return this;
    }

    public String getModel() {
        return model;
    }

    public JobLog setModel(String model) {
        this.model = model;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public JobLog setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public JobLog setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public JobLog setStatus(String status) {
        this.status = status;
        return this;
    }

    public int getAddedLines() {
        return addedLines;
    }

    public JobLog setAddedLines(int addedLines) {
        this.addedLines = addedLines;
        return this;
    }

    public int getModifiedLines() {
        return modifiedLines;
    }

    public JobLog setModifiedLines(int modifiedLines) {
        this.modifiedLines = modifiedLines;
        return this;
    }

    public int getDeletedLines() {
        return deletedLines;
    }

    public JobLog setDeletedLines(int deletedLines) {
        this.deletedLines = deletedLines;
        return this;
    }

    public String getSite() {
        return site;
    }

    public JobLog setSite(String site) {
        this.site = site;
        return this;
    }

    public String getBrand() {
        return brand;
    }

    public JobLog setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public JobLog setUrl(String url) {
        this.url = url;
        return this;
    }
}

