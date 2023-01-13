package Server.Models;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Serializable {
    public String toJSON()  throws JsonProcessingException;
    public String toString();
    public String toFancyString();
}
