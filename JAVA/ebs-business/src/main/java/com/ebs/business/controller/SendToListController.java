package com.ebs.business.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ebs.common.annotation.Log;
import com.ebs.common.core.controller.BaseController;
import com.ebs.common.core.domain.AjaxResult;
import com.ebs.common.enums.BusinessType;
import com.ebs.business.domain.SendToList;
import com.ebs.business.service.ISendToListService;
import com.ebs.common.utils.poi.ExcelUtil;
import com.ebs.common.core.page.TableDataInfo;

/**
 * 配信先リストController
 *
 * @author sxt
 * @date 2023-11-02
 */
@RestController
@RequestMapping("/business/send_to")
public class SendToListController extends BaseController
{
    @Autowired
    private ISendToListService sendToListService;

    /**
     * 查询配信先リスト列表
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:list')")
    @GetMapping("/list")
    public TableDataInfo list(SendToList sendToList)
    {
//        startPage();
        List<SendToList> list = sendToListService.selectSendToListList(sendToList);
        return getDataTable(list);
    }

    /**
     * 查询配信先リスト列表
     */
//    @PreAuthorize("@ss.hasPermi('business:mail_format_info:list')")
    @GetMapping("/list2")
    public TableDataInfo list2(SendToList sendToList)
    {
        List<SendToList> list = sendToListService.selectSendToListList(sendToList);
        return getDataTable(list);
    }

    /**
     * 导出配信先リスト列表
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:export')")
    @Log(title = "配信先リスト", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SendToList sendToList)
    {
        List<SendToList> list = sendToListService.selectSendToListList(sendToList);
        ExcelUtil<SendToList> util = new ExcelUtil<SendToList>(SendToList.class);
        util.exportExcel(response, list, "配信先リストデータ");
    }

    /**
     * 获取配信先リスト详细信息
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:query')")
    @GetMapping(value = "/{sendToListId}")
    public AjaxResult getInfo(@PathVariable("sendToListId") Long sendToListId)
    {
        return success(sendToListService.selectSendToListBySendToListId(sendToListId));
    }

    /**
     * 新增配信先リスト
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:add')")
    @Log(title = "配信先リスト", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SendToList sendToList)
    {
        sendToList.setCreateBy(getUsername());
        return toAjax(sendToListService.insertSendToList(sendToList));
    }

    /**
     * 修改配信先リスト
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:edit')")
    @Log(title = "配信先リスト", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SendToList sendToList)
    {
        sendToList.setUpdateBy(getUsername());
        return toAjax(sendToListService.updateSendToList(sendToList));
    }

//    /**
//     * 删除配信先リスト
//     */
////    @PreAuthorize("@ss.hasPermi('business:send_to:remove')")
//    @Log(title = "配信先リスト", businessType = BusinessType.DELETE)
//	@DeleteMapping("/{sendToListIds}")
//    public AjaxResult remove(@PathVariable Long[] sendToListIds)
//    {
//        return toAjax(sendToListService.deleteSendToListBySendToListIds(sendToListIds));
//    }

    /**
     * 删除配信先リスト
     */
//    @PreAuthorize("@ss.hasPermi('business:send_to:remove')")
    @Log(title = "配信先リスト", businessType = BusinessType.DELETE)
    @DeleteMapping("/{sendToListId}")
    public AjaxResult remove(@PathVariable Long sendToListId)
    {
        return toAjax(sendToListService.deleteSendToListBySendToListId(sendToListId));
    }
}
