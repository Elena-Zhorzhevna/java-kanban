package service.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import service.in_memory.InMemoryHistoryManager;
import service.in_memory.InMemoryTaskManager;

public class Managers { //подбирает нужную реализацию TaskManager и возвращает объект правильного типа
    public static TaskManager getDefault() { //возвращает объект - менеджер

        return new InMemoryTaskManager(getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - менеджер

        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
       // gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }

}
