package com.ebs.business.controller;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.ebs.common.constant.HttpStatus;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ebs.common.annotation.Log;
import com.ebs.common.core.controller.BaseController;
import com.ebs.common.core.domain.AjaxResult;
import com.ebs.common.enums.BusinessType;
import com.ebs.business.domain.EleReport;
import com.ebs.business.service.IEleReportService;
import com.ebs.common.utils.poi.ExcelUtil;
import com.ebs.common.core.page.TableDataInfo;

/**
 * 電子帳簿Controller
 *
 * @author sxt
 * @date 2024-02-05
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/business/report")
public class EleReportController extends BaseController
{
    @Autowired
    private IEleReportService eleReportService;

    @Value("${server.doragogoUrl}")
    private String doragogoUrl;

    /**
     * 查询電子帳簿列表
     */
//    @PreAuthorize("@ss.hasPermi('business:report:list')")
    @GetMapping("/list")
    public TableDataInfo list(EleReport eleReport) {
        List<EleReport> list = eleReportService.selectEleReportList(eleReport);
        return getDataTable(list);
    }

    //sxt 20240301 add start
    @GetMapping("/getDoragogoUrl")
    public AjaxResult getDoragogoUrl() {
        return success(doragogoUrl);
    }
    //sxt 20240301 add end

    /**
     * 导出電子帳簿列表
     */
//    @PreAuthorize("@ss.hasPermi('business:report:export')")
    @Log(title = "電子帳簿", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, EleReport eleReport)
    {
        List<EleReport> list = eleReportService.selectEleReportList(eleReport);
        ExcelUtil<EleReport> util = new ExcelUtil<EleReport>(EleReport.class);
        util.exportExcel(response, list, "電子帳簿数据");
    }

//    //sxt 20240228 add start
//    /**
//     * 查询電子帳簿
//     */
//    @CrossOrigin(origins = "*")
//    @PostMapping("/getInfoByCid3")
//    public AjaxResult getInfoByCid3(@RequestBody EleReport eleReport)
//    {
//        String cid3 = eleReport.getCid3();
//        eleReport = eleReportService.selectEleReportByCid3(cid3);
//        HashMap<String,String> result = new HashMap<>();
//        result.put("status",eleReport.getStatus());
//
//        return success(result);
//    }
//    //sxt 20240228 add end

//    /**
//     * 获取電子帳簿详细信息
//     */
//    @CrossOrigin(origins = "*")
//    @GetMapping(value = "/{stampCode}")
//    public AjaxResult getInfo(@PathVariable("stampCode") String stampCode)
//    {
//        EleReport eleReport = eleReportService.selectEleReportByStampCode(stampCode);
//        HashMap<String,String> result = new HashMap<>();
//        result.put("status",eleReport.getStatus());
//        return success(result);
//    }

    /**
     * 获取電子帳簿详细信息
     */
    @CrossOrigin(origins = "*")
    @PostMapping("/getInfo")
    public AjaxResult getInfo(@RequestBody EleReport eleReport)
    {
        String stampCode = eleReport.getStampCode();
        eleReport = eleReportService.selectEleReportByStampCode(stampCode);
        HashMap<String,String> result = new HashMap<>();
        result.put("status",eleReport.getStatus());
        result.put("cid1",eleReport.getCid1());
        result.put("decryptionCode",eleReport.getDecryptionCode());
        return success(result);
    }

    /**
     * 新增電子帳簿
     */
    @Log(title = "電子帳簿", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody EleReport eleReport)
    {
//        eleReport.setCreateBy(getUsername());
        IEleReportService.ResultOfCreateIpfs result = eleReportService.createIpfs(eleReport);
        switch (result) {
            case ERROR:
                return error("IPFS作成失敗しました");
            case ERROR_CREAT_PDF:
                return error("タイムスタンプコード付きの電子文書を作成失敗しました");
            case ERROR_CREAT_HTML:
                return error("取引先使うHTMLを作成失敗しました");
        }
        eleReport.setCreateBy(getNickName());
        return success(eleReportService.insertEleReport(eleReport));
    }

    /**
     * 修改電子帳簿
     */
//    @PreAuthorize("@ss.hasPermi('business:report:edit')")
//    @Log(title = "電子帳簿", businessType = BusinessType.UPDATE)
    @PutMapping("/edit")
    @CrossOrigin(origins = "*")
//    @RequestMapping(value = "edit", method = RequestMethod.PUT)
    public AjaxResult edit(@RequestBody EleReport eleReport)
    {
        return success(eleReportService.updateEleReport(eleReport));
    }

    /**
     * 删除電子帳簿
     */
//    @PreAuthorize("@ss.hasPermi('business:report:remove')")
    @Log(title = "電子帳簿", businessType = BusinessType.DELETE)
	@DeleteMapping("/{cid1s}")
    public AjaxResult remove(@PathVariable String[] cid1s)
    {
        return toAjax(eleReportService.deleteEleReportByCid1s(cid1s));
    }
}
