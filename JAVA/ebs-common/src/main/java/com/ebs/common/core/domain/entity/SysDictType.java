package com.ebs.common.core.domain.entity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ebs.common.annotation.Excel;
import com.ebs.common.annotation.Excel.ColumnType;
import com.ebs.common.core.domain.BaseEntity;

/**
 * 字典类型表 sys_dict_type
 *
 * @author ruoyi
 */
public class SysDictType extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 字典主键 */
    @Excel(name = "辞書キー", cellType = ColumnType.NUMERIC)
    private Long dictId;

    /** 字典名称 */
    @Excel(name = "辞書名称")
    private String dictName;

    /** 字典类型 */
    @Excel(name = "辞書タイプ")
    private String dictType;

    /** 状态（0正常 1停用） */
    @Excel(name = "状態", readConverterExp = "0=正常,1=非アクティブ化")
    private String status;

    public Long getDictId()
    {
        return dictId;
    }

    public void setDictId(Long dictId)
    {
        this.dictId = dictId;
    }

    @NotBlank(message = "辞書名称は必須入力項目です")
    @Size(min = 0, max = 100, message = "辞書名称の長さは100文字を超えることはできません")
    public String getDictName()
    {
        return dictName;
    }

    public void setDictName(String dictName)
    {
        this.dictName = dictName;
    }

    @NotBlank(message = "辞書タイプは必須入力項目です")
    @Size(min = 0, max = 100, message = "辞書タイプの長さは100文字を超えることはできません")
    @Pattern(regexp = "^[a-z][a-z0-9_]*$", message = "辞書タイプは文字で始まる必要があり、使用できるのは (小文字英字、数字、アンダースコア) のみです。")
    public String getDictType()
    {
        return dictType;
    }

    public void setDictType(String dictType)
    {
        this.dictType = dictType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("dictId", getDictId())
            .append("dictName", getDictName())
            .append("dictType", getDictType())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
