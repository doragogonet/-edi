package com.ebs.business.service;

import java.util.List;
import com.ebs.business.domain.SendToList;

/**
 * 配信先リストService接口
 * 
 * @author sxt
 * @date 2023-11-02
 */
public interface ISendToListService 
{
    /**
     * 查询配信先リスト
     * 
     * @param sendToListId 配信先リスト主键
     * @return 配信先リスト
     */
    public SendToList selectSendToListBySendToListId(Long sendToListId);

    /**
     * 查询配信先リスト列表
     * 
     * @param sendToList 配信先リスト
     * @return 配信先リスト集合
     */
    public List<SendToList> selectSendToListList(SendToList sendToList);

    /**
     * 新增配信先リスト
     * 
     * @param sendToList 配信先リスト
     * @return 结果
     */
    public int insertSendToList(SendToList sendToList);

    /**
     * 修改配信先リスト
     * 
     * @param sendToList 配信先リスト
     * @return 结果
     */
    public int updateSendToList(SendToList sendToList);

    /**
     * 批量删除配信先リスト
     * 
     * @param sendToListIds 需要删除的配信先リスト主键集合
     * @return 结果
     */
    public int deleteSendToListBySendToListIds(Long[] sendToListIds);

    /**
     * 删除配信先リスト信息
     * 
     * @param sendToListId 配信先リスト主键
     * @return 结果
     */
    public int deleteSendToListBySendToListId(Long sendToListId);
}