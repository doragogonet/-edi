package com.ebs.web.controller.system;

import com.ebs.business.domain.EleReport;
import com.ebs.common.core.domain.AjaxResult;
import com.ebs.common.core.page.TableDataInfo;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ebs.common.config.RuoYiConfig;
import com.ebs.common.utils.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页
 *
 * @author ruoyi
 */
@RestController
public class SysIndexController
{
    /** 系统基础配置 */
    @Autowired
    private RuoYiConfig ruoyiConfig;

    @Value("${server.ipfs}")
    private String ipfsAddress;

    /**
     * 访问首页，提示语
     */
//    @RequestMapping("/")
//    public String index()
//    {
//        return StringUtils.format("欢迎使用{}后台管理框架，当前版本：v{}，请通过前端地址访问。", ruoyiConfig.getName(), ruoyiConfig.getVersion());
//    }

    @RequestMapping("/")
    public AjaxResult index() {

        try {
            // IPFSインスタンスを作成（デフォルトのローカルノードに接続）
            IPFS ipfs = new IPFS(ipfsAddress);
            // レポの統計情報を取得（引数としてfalseを渡す）
            Map<String, Object> repoStat = (Map<String, Object>) ipfs.repo.stat(false);

//            System.out.println("Repo Size: " + repoStat.get("RepoSize"));
//            System.out.println("Storage Max: " + repoStat.get("StorageMax"));
//            System.out.println("Num Objects: " + repoStat.get("NumObjects"));
            // 他の利用可能な統計情報も同様に取得できます
            return success(repoStat);
        } catch (IOException e) {
            e.printStackTrace();
            return AjaxResult.error("error");
        }
    }
    /**
     * 返回成功消息
     */
    public AjaxResult success(Object data)
    {
        return AjaxResult.success(data);
    }
}
