package http.handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    //если сервер корректно выполнил запрос и вернул данные
    protected void sendText200(HttpExchange h, String responseString, int responseCode) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
 /*       byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();*/
    }

    //если запрос выполнен успешно, но возвращать данные нет необходимости
    protected static void sendSuccessButNoNeedToReturn201(HttpExchange h, String responseString, int responseCode)
            throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
     /*   byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(201, resp.length);
        h.getResponseBody().write(resp);
        h.close();*/
    }

    //для отправки ответа в случае, если объект не был найден
    protected void sendNotFound404(HttpExchange h, String responseString,int responseCode) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
     /*   byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(404, resp.length);
        h.getResponseBody().write(resp);
        h.close();*/
    }

     //для отправки ответа, если при создании или обновлении задача пересекается с уже существующими
     protected void sendHasInteractions406(HttpExchange h, String responseString, int responseCode) throws IOException {
         try (OutputStream os = h.getResponseBody()) {
             h.sendResponseHeaders(responseCode, 0);
             os.write(responseString.getBytes(DEFAULT_CHARSET));
         }
         h.close();
      /*   byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
         h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
         h.sendResponseHeaders(406, resp.length);
         h.getResponseBody().write(resp);
         h.close();*/
     }

     //если произошла ошибка при обработке запроса
    protected void sendInternalServerError500(HttpExchange h, String responseString, int responseCode)
            throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
   /*     byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(500, resp.length);
        h.getResponseBody().write(resp);
        h.close();*/
    }

    protected void sendBadRequest400(HttpExchange h, String responseString,int responseCode) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
    }

    protected void sendNotAllowed405(HttpExchange h, String responseString,int responseCode) throws IOException {
        try (OutputStream os = h.getResponseBody()) {
            h.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        h.close();
    }


/*        byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(400, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }*/

}
/*
private void writeResponse (HttpExchange exchange,
                            String responseString,
                            int responseCode) throws IOException {
    try (OutputStream os = exchange.getResponseBody()) {
        exchange.sendResponseHeaders(responseCode, 0);
        os.write(responseString.getBytes(DEFAULT_CHARSET));
    }
    exchange.close();
}*/
