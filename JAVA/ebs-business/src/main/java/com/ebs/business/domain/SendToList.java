package com.ebs.business.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ebs.common.annotation.Excel;
import com.ebs.common.core.domain.BaseEntity;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

/**
 * 送信先リスト对象 send_to_list
 *
 * @author sxt
 * @date 2023-11-02
 */
public class SendToList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long sendToListId;

    /** 送信先名 */
    private String sendToListName;

    public void setSendToListId(Long sendToListId)
    {
        this.sendToListId = sendToListId;
    }

    public Long getSendToListId()
    {
        return sendToListId;
    }
    public void setSendToListName(String sendToListName)
    {
        this.sendToListName = sendToListName;
    }

    public String getSendToListName()
    {
        return sendToListName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("sendToListId", getSendToListId())
            .append("sendToListName", getSendToListName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
