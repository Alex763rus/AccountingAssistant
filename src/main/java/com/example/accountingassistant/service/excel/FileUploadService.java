package com.example.accountingassistant.service.excel;

import com.example.accountingassistant.config.BotConfig;
import com.example.accountingassistant.enums.FileType;
import jakarta.annotation.PostConstruct;
import lombok.val;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.text.ParseException;
import java.util.Date;

import static com.example.accountingassistant.utils.DateConverter.TEMPLATE_DATE_FILE_NAME;
import static com.example.accountingassistant.utils.DateConverter.convertDateFormat;

@Service
public class FileUploadService {

    @Autowired
    private BotConfig botConfig;

    private String SPEC_FILE_SEARCH;
    private String SPEC_FILE_DOWNLOAD;

    @PostConstruct
    public void init() {
        SPEC_FILE_SEARCH = "https://api.telegram.org/bot" + botConfig.getBotToken() + "/getFile?file_id=";
        SPEC_FILE_DOWNLOAD = "https://api.telegram.org/file/bot" + botConfig.getBotToken() + "/";
    }

    public String getFileName(FileType fileType, String file_name) throws ParseException {
        val curDate = convertDateFormat(new Date(), TEMPLATE_DATE_FILE_NAME);
        return botConfig.getInputFilePath() + fileType.getFolderName() + curDate + file_name;
    }

    public File uploadFileFromServer(String path) throws ParseException {
        return new File(path);
    }

    public File uploadFileFromTg(String fullFilePath, String file_id) throws IOException, ParseException {
        val url = new URL(SPEC_FILE_SEARCH + file_id);
        val in = new BufferedReader(new InputStreamReader(url.openStream()));
        val res = in.readLine();
        val file_path = new JSONObject(res).getJSONObject("result").getString("file_path");
        val download = new URL(SPEC_FILE_DOWNLOAD + file_path);
        val fos = new FileOutputStream(fullFilePath);
        val rbc = Channels.newChannel(download.openStream());
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        in.close();
        fos.close();
        rbc.close();
        return new File(fullFilePath);
    }

    public XSSFWorkbook uploadXlsx(String fullFilePath, String file_id) throws Exception {
        return (XSSFWorkbook) (WorkbookFactory.create(uploadFileFromTg(fullFilePath, file_id)));
    }
}
