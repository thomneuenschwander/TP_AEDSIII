package application.resource;

import java.util.List;

public class Response<T> {
    private final int status;
    private final long timestamp;
    private final String message;
    private final T body;

    public Response(int status, String message, T body, long timestamp) {
        this.status = status;
        this.message = message;
        this.body = body;
        this.timestamp = timestamp;
    }
    public Response(int status, String message, T body) {
        this.status = status;
        this.message = message;
        this.body = body;
        this.timestamp = 0;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Response {");
        sb.append(System.lineSeparator());
        sb.append("\tstatus: ").append(status);
        sb.append(System.lineSeparator());
        sb.append("\ttimestamp: ").append(timestamp).append(" ms");
        sb.append(System.lineSeparator());
        sb.append("\tmessage: '").append(message).append("'");
        sb.append(System.lineSeparator());

        if (body == null) {
            sb.append("\tbody: null");
        } else {
            if (body instanceof List) {
                List<?> listBody = (List<?>) body;
                sb.append("\tbody: [");
                for (int i = 0; i < listBody.size(); i++) {
                    sb.append(listBody.get(i));
                    if (i < listBody.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            } else {
                sb.append("\tbody: ").append(body);
            }
        }

        sb.append(System.lineSeparator());
        sb.append("}");
        return sb.toString();
    }
}
