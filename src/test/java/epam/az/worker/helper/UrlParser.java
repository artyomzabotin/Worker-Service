package epam.az.worker.helper;

public class UrlParser {

    public static Long getIdFromLocationUrl(String locationUrl) {

        String[] splittedUrl = locationUrl.split("/");
        String id = splittedUrl[splittedUrl.length - 1];

        return Long.valueOf(id);
    }
}
