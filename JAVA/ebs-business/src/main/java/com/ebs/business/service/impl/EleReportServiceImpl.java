package com.ebs.business.service.impl;

import java.util.List;


import com.ebs.common.config.RuoYiConfig;
import com.ebs.common.exception.ServiceException;
import com.ebs.common.utils.DateUtils;
import org.apache.pdfbox.pdmodel.encryption.*;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ebs.business.mapper.EleReportMapper;
import com.ebs.business.domain.EleReport;
import com.ebs.business.service.IEleReportService;
import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 電子帳簿Service业务层处理
 *
 * @author sxt
 * @date 2024-02-05
 */
@Service
public class EleReportServiceImpl implements IEleReportService {
    enum FileType {PDF, HTML;}
    @Autowired
    private EleReportMapper eleReportMapper;

    @Value("${server.port}")
    private String port;

    @Value("${server.ipfs}")
    private String ipfsAddress;

    @Value("${server.ajaxUrl}")
    private String ajaxUrl;

    @Value("${server.doragogoUrl}")
    private String doragogoUrl;

    /**
     * 查询電子帳簿
     *
     * @param stampCode 電子帳簿主键
     * @return 電子帳簿
     */
    @Override
    public EleReport selectEleReportByStampCode(String stampCode) {
        return eleReportMapper.selectEleReportByStampCode(stampCode);
    }

    /**
     * 查询電子帳簿列表
     *
     * @param eleReport 電子帳簿
     * @return 電子帳簿
     */
    @Override
    public List<EleReport> selectEleReportList(EleReport eleReport) {
        return eleReportMapper.selectEleReportList(eleReport);
    }

    /**
     * 修改電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    @Override
    public EleReport updateEleReport(EleReport eleReport) {

        //スタンプコード
        String stampCode = eleReport.getStampCode();
        EleReport eleReportTemp = eleReportMapper.selectEleReportByStampCode(stampCode);
        eleReport.setCid1(eleReportTemp.getCid1());
        eleReport.setDecryptionCode(eleReportTemp.getDecryptionCode());

        //返信日付
        eleReport.setReplyDate(this.getNowDate());
        eleReportMapper.updateEleReport(eleReport);
        return eleReport;
    }

    /**
     * 新增電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    @Override
    public ResultOfCreateIpfs createIpfs(EleReport eleReport) {
        try {
            String password = this.generateRandomPassword(10);
            // IPFSクライアントを初期化
            IPFS ipfs = new IPFS(ipfsAddress);

            // 原簿ファイル名
            String originalFullFileName = getOriginalFileName(eleReport);

            //スタンプコード作成
            String stampCode = this.createStampCode(originalFullFileName);

            // 新PDFファイル名
            String newFullFileName = this.getNewFileName(originalFullFileName,FileType.PDF);

            // 新PDFファイルを作成する  タイムスタンプコード
            if (!createNewPdf(originalFullFileName,newFullFileName, "TIMESTAMPCODE: " + stampCode)) {
                //新PDFファイルを作成失敗する
                return ResultOfCreateIpfs.ERROR_CREAT_PDF;
            }

            // 新PDFファイルを暗号化する
            encrypt(newFullFileName,newFullFileName,password);

            // 転送する原簿ファイル
            File foriginalFile = new File(originalFullFileName);

            // 原簿ファイルをIPFSに転送
            NamedStreamable.FileWrapper foriginalFileWrapper = new NamedStreamable.FileWrapper(foriginalFile);
            MerkleNode response1 = ipfs.add(foriginalFileWrapper).get(0);
            ipfs.pin.add(response1.hash);       //sxt 20240228 add

            // 転送する新PDFファイル
            File newFile = new File(newFullFileName);

            // 新PDFファイルをIPFSに転送
            NamedStreamable.FileWrapper newFileWrapper = new NamedStreamable.FileWrapper(newFile);
            MerkleNode response2 = ipfs.add(newFileWrapper).get(0);
            ipfs.pin.add(response2.hash);       //sxt 20240228 add

            //タイムスタンプコード付きの電子文書のCID
            eleReport.setCid1(response2.hash.toString());
            //原簿のCID
            eleReport.setCid2(response1.hash.toString());
            //タイムスタンプコード付きの電子文書のURL
            String newFileName = "/profile/upload" + newFullFileName.replace(RuoYiConfig.getUploadPath(),"");
            eleReport.setReportUrl1(newFileName);
            //原簿のURL
            String originalFileName = "/profile/upload" + originalFullFileName.replace(RuoYiConfig.getUploadPath(),"");
            eleReport.setReportUrl2(originalFileName);
            //送信日付
            eleReport.setSendDate(this.getNowDate());
            //復号コード
            eleReport.setDecryptionCode(password);
            //スタンプコード
            eleReport.setStampCode(stampCode);
            //受信者はVUE側に設定しました
            //状態
            eleReport.setStatus("0");       //0:初期値   1:合意   2:却下
            //種別名はVUE側に設定しました

            eleReport.setCreateTime(DateUtils.getNowDate());

            // 新HTMLファイル名
//            String newFullFileNameOfHtml = this.getNewFileName(originalFullFileName,FileType.HTML);
            String newFullFileNameOfHtml = newFullFileName.replace(".pdf",".html");
            // 新HTMLファイルを作成する
            if (!createNewHtml(eleReport,newFullFileNameOfHtml)) {
                // 新HTMLファイルを作成失敗する
                return ResultOfCreateIpfs.ERROR_CREAT_HTML;
            }

            // 転送する新HTMLファイル
            File newFileOfHtml = new File(newFullFileNameOfHtml);

            // 新HTMLファイルをIPFSに転送
            NamedStreamable.FileWrapper newFileWrapperOfHtml = new NamedStreamable.FileWrapper(newFileOfHtml);
//            MerkleNode response3 = ipfs.add(newFileWrapperOfHtml).get(0);
            MerkleNode response3 = ipfs.add(newFileWrapperOfHtml).get(0);
            ipfs.pin.add(response3.hash);       //sxt 20240228 add

            //HtmlのCID
            eleReport.setCid3(response3.hash.toString());

            // 転送したファイルのハッシュを表示
//            System.out.println("https://ipfs.io/ipfs/" + response.hash.toString());
//            System.out.println("http://rpc.doragogo.net:8080/ipfs/" + response.hash.toString());
        } catch (Exception e) {
//            e.printStackTrace();
            return ResultOfCreateIpfs.ERROR;
        }

        return ResultOfCreateIpfs.SUCCESS;
    }

    /**
     * 新增電子帳簿
     *
     * @param eleReport 電子帳簿
     * @return 结果
     */
    @Override
    public EleReport insertEleReport(EleReport eleReport)
    {
        //初期，送信先为空
        eleReport.setSendToName("");
        eleReportMapper.insertEleReport(eleReport);

        String filePath = RuoYiConfig.getUploadPath();
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

        return eleReport;
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

    /**
     * PDF文件加密
     * @param srcpath 原始文件全路径
     * @param despath 加密文件全路径
     * @param password 密码
     * @throws InvalidPasswordException
     * @throws IOException
     */
    private static void encrypt(String srcpath,String despath,String password) throws InvalidPasswordException, IOException {

        //所有者密码
        String ownerPassWord = password;
        //用户密码
        String userPassWord = password;
        File file = new File(srcpath);
        PDDocument load = PDDocument.load(file);

        AccessPermission permissions = new AccessPermission();
        // 是否可以插入/删除/旋转页面
        permissions.setCanAssembleDocument(false);
        // 是否可以复制和提取内容
        permissions.setCanExtractContent(false);

        permissions.setCanExtractForAccessibility(false);
        // 设置用户是否可以填写交互式表单字段（包括签名字段）
        permissions.setCanFillInForm(false);
        // 设置用户是否可以修改文档
        permissions.setCanModify(false);
        // 设置用户是否可以添加或修改文本注释并填写交互式表单字段，如果canModify()返回true，则创建或修改交互式表单字段（包括签名字段）。
        permissions.setCanModifyAnnotations(false);
        // 设置用户是否可以打印。
        permissions.setCanPrint(false);
        // 设置用户是否可以降级格式打印文档
        permissions.setCanPrintDegraded(false);

        StandardProtectionPolicy p = new StandardProtectionPolicy(ownerPassWord, userPassWord, permissions);
        SecurityHandler sh = new StandardSecurityHandler(p);
        sh.prepareDocumentForEncryption(load);
        PDEncryption encryptionOptions = new PDEncryption();
        encryptionOptions.setSecurityHandler(sh);

        load.setEncryptionDictionary(encryptionOptions);
        load.save(despath);
        load.close();

    }

    //创建随机密码
    private static String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder();
        char[] characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length);
            sb.append(characters[index]);
        }

        return sb.toString();
    }
    private String getNowDate() {
        java.util.Date dateTo = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(dateTo);
    }

    private String getNowDate2() {
        java.util.Date dateTo = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(dateTo);
    }

    private boolean createNewHtml(EleReport eleReport,String newFullFileName) throws UnknownHostException {
        String cid2 = eleReport.getCid2();
        String companyName = eleReport.getSendToName();
        String stampCode = eleReport.getStampCode();
        String serverUrl = ajaxUrl;
        String reportTypeName = eleReport.getReportTypeName();
        String fileName = eleReport.getFileName();
        String reportTitle = eleReport.getReportTitle();
        try{
            File fileRead = new File(RuoYiConfig.getUploadPath() + "/index.html"); //指定要读取的文本文件路径
            File fileWrite = new File(newFullFileName); //指定要写入的文本文件路径

            BufferedReader reader = new BufferedReader(new FileReader(fileRead));
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileWrite));
            String line;
            while ((line = reader.readLine()) != null){
                //向文件中写入内容
                if (line.indexOf("{{cid2}}")>-1) {
                    String temp = line.replace("{{cid2}}",cid2);
                    if (line.indexOf("{{doragogoUrl}}")>-1) {
                        temp = temp.replace("{{doragogoUrl}}",doragogoUrl);
                    }
                    writer.write(temp);
                } else if (line.indexOf("{{companyName}}")>-1) {
                    writer.write(line.replace("{{companyName}}",companyName));
                } else if (line.indexOf("{{stampCode}}")>-1) {
                    writer.write(line.replace("{{stampCode}}",stampCode));
                } else if (line.indexOf("{{serverUrl}}")>-1) {
                    writer.write(line.replace("{{serverUrl}}",serverUrl));
                } else if (line.indexOf("{{doragogoUrl}}")>-1) {
                    writer.write(line.replace("{{doragogoUrl}}",doragogoUrl));
                } else if (line.indexOf("{{reportTypeName}}")>-1) {
                    writer.write(line.replace("{{reportTypeName}}",reportTypeName));
                } else if (line.indexOf("{{fileName}}")>-1) {
                    writer.write(line.replace("{{fileName}}",fileName));
                } else if (line.indexOf("{{reportTitle}}")>-1) {
                    if (reportTitle!=null) {    //sxt 20240401 add start
                        writer.write(line.replace("{{reportTitle}}",reportTitle));
                    }   //sxt 20240401 add end
                } else {
                    writer.write(line);
                }
                writer.newLine(); //换行
            }
            reader.close();
            writer.flush(); //清空缓存区并将内容写入文件
            writer.close();
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

    //生成新的pdf文件（加スタンプコード）
    private boolean createNewPdf(String originalFullFileName, String newFullFileName, String stampCode)  throws Exception {
        try {
            int pageSize = getPdfPageNum(originalFullFileName);		//pdf页数
            InsertPageContent(originalFullFileName, newFullFileName, pageSize, stampCode);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private String getOriginalFileName(EleReport eleReport) {
        // 上传文件路径
        String filePath = RuoYiConfig.getUploadPath();
        return filePath + eleReport.getOriginalFilename().replace("/profile/upload","");
    }

    //设置文件名
    private String getNewFileName(String originalFilename,FileType fileType) {

        String result="";

        if (originalFilename == null)
        {
            return "";
        }
        int lastUnixPos = originalFilename.lastIndexOf('/');
        int lastWindowsPos = originalFilename.lastIndexOf('\\');
        int index = Math.max(lastUnixPos, lastWindowsPos);

        switch (fileType) {
            case PDF:
                result = originalFilename.substring(0,index + 1) + dateFormat() + "IPFS.pdf";
                break;
            case HTML:
                result = originalFilename.substring(0,index + 1) + dateFormat() + "IPFS.html";
                break;
        }

        return result;
    }

    private String dateFormat() {
        java.util.Date dateTo = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
        String dateToFormat = sdf.format(dateTo);
        return dateToFormat;
    }

    //スタンプコード作成
    private String createStampCode(String fullFileName) throws IOException {
        // バイト列を16進数文字列に変換
        StringBuilder hashHex = new StringBuilder();

        FileInputStream inputStream = null;
        BufferedInputStream bis = null;
        try {
            inputStream = new FileInputStream(new File(fullFileName));
            bis = new BufferedInputStream(inputStream);

            byte[] buff = new byte[bis.available()];
            bis.read(buff);

            ByteBuffer byteBuffer = ByteBuffer.allocate(10240000); // 创建一个大小为10的ByteBuffer对象
            //将缓冲区恢复初始值  恢复limit值
            byteBuffer.clear();
            byteBuffer.put(buff); 	// 向buffer写入数据
            byteBuffer.flip(); 		// 切换到读取模式
            StringBuilder stringBuilder = new StringBuilder();
            while (byteBuffer.hasRemaining()) {
                char c = (char) byteBuffer.get(); 	// 从buffer获取下一个字节并转换成字符
                stringBuilder.append(c); 			// 添加到StringBuilder中
            }
            String data = stringBuilder.toString(); // 将StringBuilder转换为字符串
            data += dateFormat();   //pdf的内容 + 日期 ，然后进行hash计算。

            try {
                // ハッシュ関数を選択（ここではSHA-256を使用）
                MessageDigest md = MessageDigest.getInstance("SHA-256");

                // データをハッシュに追加
                md.update(data.getBytes());

                // ハッシュを計算
                byte[] hashBytes = md.digest();

                for (byte b : hashBytes) {
                    hashHex.append(String.format("%02x", b));
                }

//                System.out.println("固定長ハッシュ: " + hashHex.toString());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            inputStream.close();
            bis.close();
        }
        return hashHex.toString() + getNowDate2();

    }

    private int getPdfPageNum(String fullFileName) throws IOException {
        PDDocument pdDocument = null;
        int pages = 0;
        try {
            pdDocument = PDDocument.load(new File(fullFileName));
            pages= pdDocument.getNumberOfPages();
        } finally {
            if (pdDocument!=null) {
                pdDocument.close();
            }
        }
        return pages;
    }

    /**
     * 指定页插入一段文字
     * @param inputFilePath
     * @param outputFilePath
     * @param pageNum
     * @param message
     * @throws Exception
     */
    private static void InsertPageContent(String inputFilePath, String outputFilePath, Integer pageNum, String message) throws Exception {
        File inputPDFFile = new File(inputFilePath);
        File outputPDFFile = new File(outputFilePath);
        // the document
        PDDocument doc = null;
        try {
            doc = PDDocument.load(inputPDFFile);
            PDPageTree allPages = doc.getDocumentCatalog().getPages();
//            PDFont font = PDType1Font.HELVETICA_BOLD;
            PDFont font = PDType1Font.COURIER;
            //字体大小
//            float fontSize = 12.0f;
            float fontSize = 8.0f;
            for (PDPage page : allPages) {
//            PDPage page = (PDPage) allPages.get(pageNum - 1);
//            // calculate to center of the page
//            int rotation = page.getRotation();
//            boolean rotate = rotation == 90 || rotation == 270;
//            // append the content to the existing stream
//            PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true, true);
//            contentStream.beginText();
//            // set font and font size
//            contentStream.setFont(font, fontSize);
//            if (rotate) {
//                // rotate the text according to the page rotation
//                contentStream.setTextRotation(Math.PI / 2, getDpi(10), getDpi(10));
//            } else {
//                contentStream.setTextTranslation(getDpi(1), getDpi(1));
//            }

                // 获取页面的大小
//            PDRectangle pageSize = page.getMediaBox();
                PDRectangle pageSize = page.getTrimBox();
                float stringWidth = font.getStringWidth(message)*fontSize/1000f;
                float pageWidth = pageSize.getWidth();
                float pageHeight =  pageSize.getHeight();
                float centeredXPosition = (pageWidth - stringWidth)/2f;
                float centeredYPosition = pageSize.getLowerLeftY() + 10;

                PDPageContentStream contentStream = new PDPageContentStream(doc, page, true, true,true);
                contentStream.beginText();
                // set font and font size
                contentStream.setFont(font, fontSize);

                contentStream.setTextTranslation(centeredXPosition, centeredYPosition);
                contentStream.drawString(message);
                contentStream.endText();
                contentStream.close();
            }
            // 移除加密
            doc.setAllSecurityToBeRemoved(true);
            doc.save(outputPDFFile);
        } catch (Exception e) {
            String a = e.toString();
        } finally {
            if( doc != null ) {
                doc.close();
            }
        }
    }

    // 获取X，y坐标
    // value X，y标尺 毫米数
    private static float getDpi(int value){
        double v = value / 25.4;
        float v1 = (float)(72 * v);
        return (float) (Math.round(v1 * 10)) /10;
    }

    /**
     * 批量删除電子帳簿
     *
     * @param cid1s 需要删除的電子帳簿主键
     * @return 结果
     */
    @Override
    public int deleteEleReportByCid1s(String[] cid1s)
    {
        return eleReportMapper.deleteEleReportByCid1s(cid1s);
    }

    /**
     * 删除電子帳簿信息
     *
     * @param cid1 電子帳簿主键
     * @return 结果
     */
    @Override
    public int deleteEleReportByCid1(String cid1)
    {
        return eleReportMapper.deleteEleReportByCid1(cid1);
    }
}
