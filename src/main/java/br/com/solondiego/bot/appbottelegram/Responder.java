package br.com.solondiego.bot.appbottelegram;

import org.springframework.stereotype.Component;

import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

import java.util.List;

@Component
public class Responder implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {


    private final TelegramClient telegramClient;

    public Responder() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

    @Override
    public String getBotToken() {
        return Bot.BOT_TOKEN;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {

        String response = "Sinto muito, mas não entendi sua mensagem.";

        String chat_id = "";

        SendMessage message = SendMessage
                .builder()
                .chatId(chat_id)
                .text(response)
                .build();

        if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty()) {

            String callBackData = update.getCallbackQuery().getData();

            chat_id = String.valueOf(update.getCallbackQuery().getMessage().getChatId());

            if (callBackData.equalsIgnoreCase(CallBackData.CD_YES.toString())) {
                LocalDateTime curreTime = LocalDateTime.now();
                message.setText(curreTime.toString());
            }

            if (callBackData.equalsIgnoreCase(CallBackData.CD_NO.toString())) {
                message.setText("OK, obrigado!");
            }

        }

        if (update.hasMessage() && update.getMessage().hasText()) {

            String userMessage = String.valueOf(update.getMessage().getText().trim());
            chat_id = String.valueOf(update.getMessage().getChatId());

            if (userMessage.equalsIgnoreCase("ola")) {
                message.setText("Como vai?");
            }

            if (userMessage.equalsIgnoreCase("como vai")) {
                message.setText("Estou bem, obrigado!");
            }

            if (userMessage.equalsIgnoreCase("hora")) {

                message.setText("Você gostaria de saber a hora atual?");

                InlineKeyboardMarkup keyboard = InlineKeyboardMarkup.builder().keyboard(List.of(
                        new InlineKeyboardRow(
                                InlineKeyboardButton.builder().text("Sim!").callbackData(CallBackData.CD_YES.toString()).build(),
                                InlineKeyboardButton.builder().text("Não, obrigado!").callbackData(CallBackData.CD_NO.toString()).build())
                )).build();

                message.setReplyMarkup(keyboard);
            }

            if (userMessage.equalsIgnoreCase("dia")){
                DayOfWeek toDayOfWeek = LocalDateTime.now().getDayOfWeek();
                message.setText(toDayOfWeek.toString());
            }

        }

        if (chat_id.isEmpty()){
            throw new IllegalStateException("Não foi possível identificar ou encontrar o ID do chat.");
        }

        message.setChatId(chat_id);

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


}