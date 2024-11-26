package com.ebs.business.service;

import java.util.List;
import com.ebs.business.domain.EleReport;

/**
 * 電子帳簿Service接口
 *
 * @author sxt
 * @date 2024-02-05
 */
public interface IEleReportService
{
    enum ResultOfCreateIpfs {SUCCESS, ERROR, ERROR_CREAT_PDF,ERROR_CREAT_HTML;}
    /**
     * 查询電子帳簿
     *
     * @param stampCode 電子帳簿主键
     * @return 電子帳簿
     */
    public EleReport selectEleReportByStampCode(String stampCode);

    /**
     * 查询電子帳簿列表
     *
     * @param eleReport 電子帳簿
     * @return 電子帳簿集合
     */
    public List<EleReport> selectEleReportList(EleReport eleReport);

    /**
     * 新增電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    public ResultOfCreateIpfs createIpfs(EleReport eleReport);

    /**
     * 新增電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    public EleReport insertEleReport(EleReport eleReport);

    /**
     * 修改電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    public EleReport updateEleReport(EleReport eleReport);

    /**
     * 批量删除電子帳簿
     *
     * @param cid1s 需要删除的電子帳簿主键集合
     * @return 结果
     */
    public int deleteEleReportByCid1s(String[] cid1s);

    /**
     * 删除電子帳簿信息
     *
     * @param cid1 電子帳簿主键
     * @return 结果
     */
    public int deleteEleReportByCid1(String cid1);
}
