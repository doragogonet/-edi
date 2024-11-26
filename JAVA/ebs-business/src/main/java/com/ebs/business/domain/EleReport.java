package com.ebs.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ebs.common.annotation.Excel;
import com.ebs.common.core.domain.BaseEntity;

/**
 * 電子帳簿对象 ele_report
 *
 * @author sxt
 * @date 2024-02-05
 */
public class EleReport extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** タイムスタンプコード付きの電子文書のCID */
    private String cid1;

    /** 原簿のCID */
    @Excel(name = "原簿のCID")
    private String cid2;

    /** htmlのCID */
    private String cid3;

    /** タイムスタンプコード付きの電子文書のURL */
    @Excel(name = "タイムスタンプコード付きの電子文書のURL")
    private String reportUrl1;

    /** 原簿のURL */
    @Excel(name = "原簿のURL")
    private String reportUrl2;

    /** 送信日付 */
    @Excel(name = "送信日付")
    private String sendDate;

    /** 返信日付 */
    @Excel(name = "返信日付")
    private String replyDate;

    /** 復号コード */
    @Excel(name = "復号コード")
    private String decryptionCode;

    /** スタンプコード */
    @Excel(name = "スタンプコード")
    private String stampCode;

    /** 送信先ID */
    private Long sendToListId;

    /** 受信者 */
    @Excel(name = "受信者")
    private String sendToName;

    /** 状態 */
    @Excel(name = "状態")
    private String status;

    /** 種別 */
    @Excel(name = "種別")
    private String reportTypeName;

    /** 原簿 */
    private String originalFilename;

    private String reportTitle;
    private String reportRemark;
    private String fileName;

    public void setCid1(String cid1)
    {
        this.cid1 = cid1;
    }

    public String getCid1()
    {
        return cid1;
    }
    public void setCid2(String cid2)
    {
        this.cid2 = cid2;
    }

    public String getCid2()
    {
        return cid2;
    }

    public void setCid3(String cid3)
    {
        this.cid3 = cid3;
    }

    public String getCid3()
    {
        return cid3;
    }
    public void setReportUrl1(String reportUrl1)
    {
        this.reportUrl1 = reportUrl1;
    }

    public String getReportUrl1()
    {
        return reportUrl1;
    }
    public void setReportUrl2(String reportUrl2)
    {
        this.reportUrl2 = reportUrl2;
    }

    public String getReportUrl2()
    {
        return reportUrl2;
    }
    public void setSendDate(String sendDate)
    {
        this.sendDate = sendDate;
    }

    public String getSendDate()
    {
        return sendDate;
    }
    public void setReplyDate(String replyDate)
    {
        this.replyDate = replyDate;
    }

    public String getReplyDate()
    {
        return replyDate;
    }
    public void setDecryptionCode(String decryptionCode)
    {
        this.decryptionCode = decryptionCode;
    }

    public String getDecryptionCode()
    {
        return decryptionCode;
    }
    public void setStampCode(String stampCode)
    {
        this.stampCode = stampCode;
    }

    public String getStampCode()
    {
        return stampCode;
    }

    public void setSendToListId(Long sendToListId)
    {
        this.sendToListId = sendToListId;
    }

    public Long getSendToListId()
    {
        return sendToListId;
    }
    public void setSendToName(String sendToName)
    {
        this.sendToName = sendToName;
    }

    public String getSendToName()
    {
        return sendToName;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }
    public void setReportTypeName(String reportTypeName)
    {
        this.reportTypeName = reportTypeName;
    }

    public String getReportTypeName()
    {
        return reportTypeName;
    }
    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getReportRemark() {
        return reportRemark;
    }

    public void setReportRemark(String reportRemark) {
        this.reportRemark = reportRemark;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("cid1", getCid1())
            .append("cid2", getCid2())
            .append("cid3", getCid3())
            .append("reportUrl1", getReportUrl1())
            .append("reportUrl2", getReportUrl2())
            .append("sendDate", getSendDate())
            .append("replyDate", getReplyDate())
            .append("decryptionCode", getDecryptionCode())
            .append("stampCode", getStampCode())
            .append("sendToListId", getSendToListId())
            .append("sendToName", getSendToName())
            .append("status", getStatus())
            .append("reportTypeName", getReportTypeName())
            .append("originalFilename", getOriginalFilename())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("reportTitle", getReportTitle())
            .append("reportRemark", getReportRemark())
            .append("fileName", getFileName())
            .toString();
    }
}
