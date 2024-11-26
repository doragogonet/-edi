package com.ebs.business.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ebs.business.domain.EleReport;
import com.ebs.business.mapper.EleReportMapper;
import com.ebs.business.service.IEleReportService;
import com.ebs.common.config.RuoYiConfig;
import com.ebs.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ebs.business.mapper.SendToListMapper;
import com.ebs.business.domain.SendToList;
import com.ebs.business.service.ISendToListService;

/**
 * 配信先リストService业务层处理
 *
 * @author sxt
 * @date 2023-11-02
 */
@Service
public class SendToListServiceImpl implements ISendToListService
{
    @Autowired
    private SendToListMapper sendToListMapper;

    @Autowired
    private EleReportMapper eleReportMapper;

    /**
     * 查询配信先リスト
     *
     * @param sendToListId 配信先リスト主键
     * @return 配信先リスト
     */
    @Override
    public SendToList selectSendToListBySendToListId(Long sendToListId)
    {
        return sendToListMapper.selectSendToListBySendToListId(sendToListId);
    }

    /**
     * 查询配信先リスト列表
     *
     * @param sendToList 配信先リスト
     * @return 配信先リスト
     */
    @Override
    public List<SendToList> selectSendToListList(SendToList sendToList)
    {
        return sendToListMapper.selectSendToListList(sendToList);
    }

    /**
     * 新增配信先リスト
     *
     * @param sendToList 配信先リスト
     * @return 结果
     */
    @Override
    public int insertSendToList(SendToList sendToList)
    {
        sendToList.setCreateTime(DateUtils.getNowDate());
        return sendToListMapper.insertSendToList(sendToList);
    }

    /**
     * 修改配信先リスト
     *
     * @param sendToList 配信先リスト
     * @return 结果
     */
    @Override
    public int updateSendToList(SendToList sendToList)
    {
        sendToList.setUpdateTime(DateUtils.getNowDate());
        return sendToListMapper.updateSendToList(sendToList);
    }

    /**
     * 批量删除配信先リスト
     *
     * @param sendToListIds 需要删除的配信先リスト主键
     * @return 结果
     */
    @Override
    public int deleteSendToListBySendToListIds(Long[] sendToListIds)
    {
        return sendToListMapper.deleteSendToListBySendToListIds(sendToListIds);
    }

    /**
     * 删除配信先リスト信息
     *
     * @param sendToListId 配信先リスト主键
     * @return 结果
     */
    @Override
    public int deleteSendToListBySendToListId(Long sendToListId)
    {
        List<EleReport> eleReportlist = eleReportMapper.selectEleReportListBySendToListId(sendToListId);
        String filePath = RuoYiConfig.getUploadPath();
        List<String> ids = new ArrayList<String>();
        for (EleReport eleReport: eleReportlist) {
            //加密的pdf全路径
            String fullFileName1 = filePath + eleReport.getReportUrl1().replace("/profile/upload","");
            //原始的pdf全路径
            String fullFileName2 = filePath + eleReport.getReportUrl2().replace("/profile/upload","");
            //html全路径
            String fullFileName3 = fullFileName1.replace(".pdf",".html");

            //删除文件
            deleteFile(fullFileName1);
            deleteFile(fullFileName2);
            deleteFile(fullFileName3);

            //cid1
            ids.add(eleReport.getStampCode());
        }

        if (ids.size()>0) {
            //删除電子帳簿表中的数据
            eleReportMapper.deleteEleReportByCid1s(ids.toArray(new String[0]));
        }

        return sendToListMapper.deleteSendToListBySendToListId(sendToListId);
    }

    private void deleteFile(String fullFileName) {
        Path path = Paths.get(fullFileName);
        try {
            Files.delete(path);
            System.out.println("文件删除成功");
        } catch (IOException e) {
            System.out.println("文件删除失败: " + e.getMessage());
        }
    }
}
