package com.example.accountingassistant.model.wpapper;

import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.val;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.example.accountingassistant.constant.Constant.PARSE_MODE;

@Getter
@SuperBuilder(setterPrefix = "set", builderMethodName = "init", toBuilder = true)
public class SendPhotoWrapper {

    private String chatIdString;
    private String caption;
    private Long chatIdLong;
    private InputFile photo;
    private InlineKeyboardMarkup inlineKeyboardMarkup;

    public SendPhoto createMessage() {
        val sendPhoto = new SendPhoto();
        val chatId = chatIdString == null ? String.valueOf(chatIdLong) : chatIdString;
        sendPhoto.setChatId(chatId);
        sendPhoto.setReplyMarkup(inlineKeyboardMarkup);
        sendPhoto.setParseMode(PARSE_MODE);
        sendPhoto.setPhoto(photo);
        sendPhoto.setCaption(caption);
        return sendPhoto;
    }
}
